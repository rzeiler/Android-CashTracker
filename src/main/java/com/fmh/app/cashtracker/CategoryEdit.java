package com.fmh.app.cashtracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;

import static com.fmh.app.cashtracker.CategoryList.CATEGORY_ITEM;

public class CategoryEdit extends BaseEdit {

    private Category _category;
    private Context context;
    private EditText etTitle;
    private RatingBar rbFirma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        Intent intent = getIntent();
        _category = (Category) intent.getSerializableExtra(CategoryList.CATEGORY_ITEM);

        bDate = (Button) findViewById(R.id.bDate);
        etTitle = (EditText) findViewById(R.id.etCategory);
        rbFirma = (RatingBar) findViewById(R.id.rbPosition);

        if (_category != null) {
            getSupportActionBar().setTitle(_category.getTitle());
            etTitle.setText(_category.getTitle());
            rbFirma.setRating(_category.getRating());
        } else {
            getSupportActionBar().setTitle(getString(R.string.label_new_item));
            _category = new Category();
            _category.setUser("test");
            _category.setCreateDate(calendar.getTimeInMillis());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(_category.getCreateDate());

        bDate.setText(DateFormat.format(_category.getCreateDate()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_category, menu);

        menu.findItem(R.id.action_send).setVisible(true);
        menu.findItem(R.id.action_delete).setVisible((_category.getCategoryID() > 0));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        if (id == R.id.action_send) {

            DataBase _db = new DataBase(context);
            _category.setTitle(String.valueOf(etTitle.getText()));
            _category.setRating((int) rbFirma.getRating());
            _category.setCreateDate(calendar.getTimeInMillis());

            int i = _db.updateCategory(_category);
            if (i > 0) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Gespeichert");
                returnIntent.putExtra(CATEGORY_ITEM, (Serializable) _category);
                setResult(RESULT_OK, returnIntent);

                Toast.makeText(context, String.format("%s gespeichert.", _category.getTitle()), Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Snackbar.make(bDate, String.format("Fehler beim speichern von %s.", _category.getTitle()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return true;
        }

        if (id == R.id.action_delete) {

            AlertDialog dialog = ConfirmDelete(context);

            dialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent returnIntent = new Intent(CategoryEdit.this, CategoryList.class);
                    returnIntent.putExtra(CATEGORY_ITEM, (Serializable) _category);
                    returnIntent.putExtra("result", "Gelöscht");
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
