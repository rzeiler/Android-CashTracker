package com.fmh.app.cashtracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ralf on 05.02.18.
 */


public class BaseEdit extends AppCompatActivity {

    public int year, month, day;
    public SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public Calendar calendar = Calendar.getInstance();
    public Button bDate;

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    calendar.set(Calendar.YEAR, arg1);
                    calendar.set(Calendar.MONTH, arg2);
                    calendar.set(Calendar.DAY_OF_MONTH, arg3);

                    bDate.setText(DateFormat.format(calendar.getTimeInMillis()));
                }
            };

    public AlertDialog ConfirmDelete(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.message_delete);
        builder.setTitle(R.string.action_delete);
        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        return builder.create();

    }


}
