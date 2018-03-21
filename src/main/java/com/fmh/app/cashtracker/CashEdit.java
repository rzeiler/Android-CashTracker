package com.fmh.app.cashtracker;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ralf on 06.02.18.
 */

public class CashEdit extends BaseEdit {

    private Cash _cash;
    private Category _category;
    private Context context;
    private EditText etDescription, etSum;
    private Spinner sRepeat;
    private SharedPreferences preference;
    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        Intent intent = getIntent();
        _cash = (Cash) intent.getSerializableExtra(CashListActivity.CASH_ITEM);
        _category = (Category) intent.getSerializableExtra(CategoryListActivity.CATEGORY_ITEM);
        preference = PreferenceManager.getDefaultSharedPreferences(context);

        getSupportActionBar().setSubtitle(_category.getTitle());

        bDate = (Button) findViewById(R.id.bDate);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etSum = (EditText) findViewById(R.id.etSum);
        sRepeat = (Spinner) findViewById(R.id.sRepeat);

        if (_cash != null) {
            getSupportActionBar().setTitle(getString(R.string.label_edit_item));

            etDescription.setText(_cash.getContent());
            etSum.setText(String.format("%.2f", _cash.getTotal()));

        } else {
            getSupportActionBar().setTitle(getString(R.string.label_new_item));

            _cash = new Cash();
            _cash.setTotal(0.00);
            _cash.setCategory(_category.getCategoryID());
            _cash.setCreateDate(calendar.getTimeInMillis());

        }

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(_cash.getCreateDate());

        bDate.setText(DateFormat.format(_cash.getCreateDate()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cash_edit, menu);

        menu.findItem(R.id.action_send).setVisible(true);
        menu.findItem(R.id.action_delete).setVisible((_cash.getCashID() > 0));
        menu.findItem(R.id.action_cut).setVisible((_cash.getCashID() > 0));

        _menu = menu;

        if (preference.getLong(CashListActivity.CUT_CASH_ITEM, -1) > 0)
            menu.findItem(R.id.action_cut).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        Intent returnIntent;
        int id = item.getItemId();

        if (id == android.R.id.home) {
            returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        if (id == R.id.action_cut) {

            SharedPreferences.Editor editor = preference.edit();
            editor.putLong(CashListActivity.CUT_CASH_ITEM, _cash.getCashID());
            editor.commit();

            returnIntent = new Intent();
            returnIntent.putExtra("result", "Gespeichert");
            returnIntent.putExtra(CashListActivity.CASH_ITEM, (Serializable) _cash);
            returnIntent.putExtra(CategoryListActivity.CATEGORY_ITEM, (Serializable) _category);
            setResult(4, returnIntent);

            finish();

        }


        if (id == R.id.action_send) {

            DataBase _db = new DataBase(context);

            _cash.setContent(String.valueOf(etDescription.getText()));
            _cash.setCategory(_category.getCategoryID());
            _cash.setTotal(Double.parseDouble(String.valueOf(etSum.getText())));
            _cash.setRepeat((int) sRepeat.getSelectedItemId());
            Date _date = _db.getDateFromString(String.valueOf(bDate.getText()));
            _cash.setCreateDate(_date.getTime());

            long dbResult;

            if (_cash.getCashID() > 0) {
                dbResult = _db.updateCash(_cash);
            } else {
                dbResult = _db.addCash(_cash);
            }

            if (dbResult > 0) {

                returnIntent = new Intent();
                returnIntent.putExtra("result", "Gespeichert");
                returnIntent.putExtra(CashListActivity.CASH_ITEM, (Serializable) _cash);
                returnIntent.putExtra(CategoryListActivity.CATEGORY_ITEM, (Serializable) _category);
                setResult(RESULT_OK, returnIntent);

                finish();
            } else {
                Snackbar.make(bDate, String.format("Fehler beim speichern von %s.", _cash.getCategory()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return true;
        }

        if (id == R.id.action_delete) {

            DataBase _db = new DataBase(context);
            _db.deleteCash(_cash);

            AlertDialog dialog = ConfirmDelete(context);

            dialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent  returnIntent = new Intent(CashEdit.this, CashListActivity.class);
                    returnIntent.putExtra("result", "Gelöscht");
                    returnIntent.putExtra(CashListActivity.CASH_ITEM, (Serializable) _cash);
                    returnIntent.putExtra(CategoryListActivity.CATEGORY_ITEM, (Serializable) _category);
                    returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    setResult(3, returnIntent);

                    finish();
                }
            });

            dialog.show();

            return true;

        }


        return super.onOptionsItemSelected(item);
    }

}
