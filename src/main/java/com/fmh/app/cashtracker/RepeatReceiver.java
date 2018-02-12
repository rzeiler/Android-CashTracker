package com.fmh.app.cashtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.fmh.app.cashtracker.Models.Cash;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RepeatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        new CheckRepeats(context, new DataBase(context)).execute();

    }

    private class CheckRepeats extends AsyncTask<Object, Void, List<Cash>> {

        private DataBase dataBase;
        private Context context;

        public CheckRepeats(Context c, DataBase d) {
            dataBase = d;
            context = c;
        }

        @Override
        protected List<Cash> doInBackground(Object... params) {
            List<Cash> _cashs = new ArrayList<>();
            Calendar c, b;
            Integer r = 0;
            for (int i = 1; i < 4; i++) {
                c = Calendar.getInstance();
                if (i == 1) {
                    c.set(Calendar.WEEK_OF_YEAR, c.get(Calendar.WEEK_OF_YEAR) - 1);
                }
                if (i == 2) {
                    c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
                }
                if (i == 3) {
                    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
                }

                List<Cash> lc = dataBase.getCashs(i, c.getTimeInMillis()); // db.getCash(" iscloned=0 AND repeat=" + i + " AND int_create_date <= " + c.getTimeInMillis() + " ");
                for (Cash cash : lc) {
                    b = Calendar.getInstance();
                    cash.setIsCloned(1);

                    if (dataBase.updateCash(cash) > 0) {
                        b.setTime(new Date(cash.getCreateDate()));

                        if (i == 1) {
                            b.set(Calendar.WEEK_OF_YEAR, b.get(Calendar.WEEK_OF_YEAR) + 1);
                        }
                        if (i == 2) {
                            b.set(Calendar.MONTH, b.get(Calendar.MONTH) + 1);
                        }
                        if (i == 3) {
                            b.set(Calendar.YEAR, b.get(Calendar.YEAR) + 1);
                        }
                        Cash nc = new Cash(cash.getContent(), b.getTimeInMillis(), cash.getCategory(), cash.getRepeat(), cash.getTotal(), 0);
                        if (dataBase.addCash(nc) > 0)
                            _cashs.add(nc);
                    }
                }
            }
            return _cashs;
        }

        protected void onPostExecute(List<Cash> _cashs) {
            new RepeatNotification().notify(context, _cashs);
        }

    }

}
