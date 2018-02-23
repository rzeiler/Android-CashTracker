package com.fmh.app.cashtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preference;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);


        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preference.registerOnSharedPreferenceChangeListener(this);

        Preference btnRestore = findPreference("btnRestore");
        btnRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.notification_restore_message);
                builder.setTitle(R.string.notification_alert_title);
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* close dialog */
                        dialogInterface.dismiss();

                        Intent intent = new Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_activity_setting)), 200);
                    }

                });

                builder.create();
                builder.show();

                return false;
            }
        });

        Preference btnBackup = findPreference("btnBackup");
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd'T'hh_mm_ss");
                String filename = String.format("cashtracker_backup_%s.json", ft.format(new Date()));
                createFile("*/*", filename);
                return false;
            }
        });


        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), RepeatReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast( getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean isService = preference.getBoolean("cbxService", true);
        if (isService) {

            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);

        } else {
            try {
                alarmMgr.cancel(alarmIntent);
            } catch (Exception e) {
                // nothing
            }
        }

        Preference btnService = findPreference("cbxService");
        btnService.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                return false;
            }

        });







    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && data != null) {
            Uri uri = data.getData();
            DataBase _db = new DataBase(getActivity());
            new readBackup(_db, uri, showProgressDialog(getString(R.string.label_restore))).execute();
        }

        if (requestCode == WRITE_REQUEST_CODE && data != null) {
            Uri uri = data.getData();
            DataBase _db = new DataBase(getActivity());
            new writeBackup(_db, uri, showProgressDialog(getString(R.string.label_save))).execute();
        }
    }

    private static final int WRITE_REQUEST_CODE = 43;

    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private ProgressDialog showProgressDialog(String title) {
        ProgressDialog _progressDialog = new ProgressDialog(getActivity());
        _progressDialog.setMax(100);
        _progressDialog.setMessage("");
        _progressDialog.setTitle(title);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _progressDialog.setCancelable(false);
        _progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Schließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        _progressDialog.show();
        /* hide button */
        _progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
        return _progressDialog;
    }

    public class readBackup extends AsyncTask<Object, Integer, Integer> {

        private DataBase _db;
        private Uri _uri;
        private ProgressDialog progressDialog;

        public readBackup(DataBase db, Uri uri, ProgressDialog progressDialog) {
            this._db = db;
            this._uri = uri;
            this.progressDialog = progressDialog;
            this.progressDialog.setProgress(0);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            String JSON = null;
            try {
                JSON = readTextFromUri(_uri);
                JSONArray ja;
                ja = new JSONArray(JSON);
                JSONObject jo, sjo;
                /* delete old data */
                _db.ClearData();

                Log.w("doInBackground", "run");

                int max = ja.length();
                for (int index = 0; index < max; index++) {
                    /* state display */
                    double progressstate = (index + 1) * 100 / (max + 1);
                    publishProgress((int) progressstate);
                    jo = ja.getJSONObject(index);

                    /* add category */
                    Date _date = new Date();
                    try {

                        Object createdate = jo.get("createdate");
                        if (createdate instanceof Long) {
                            _date.setTime(jo.getLong("createdate"));
                        }
                        if (createdate instanceof String) {
                            _date = _db.getDateFromString(jo.getString("createdate"));
                        }
                        /* overwrite */
                        jo.put("createdate", _date.getTime());

                        Category _category = new Category();
                        _category.setTitle(jo.getString("title"));
                        _category.setCreateDate(jo.getLong("createdate"));
                        _category.setUser(jo.getString("user"));
                        _category.setRating(jo.getInt("rating"));
                        long iId = _db.addCategory(_category);
                        _category.setCategoryID(Long.valueOf(iId).intValue());


                        JSONArray cashArray = jo.getJSONArray("cash");
                        for (int cashIndex = 0; cashIndex < cashArray.length(); cashIndex++) {
                            sjo = cashArray.getJSONObject(cashIndex);
                            /* add cash */
                            try {
                                int ISCLONED = 1;
                                if (sjo.has("iscloned")) {
                                    ISCLONED = sjo.getInt("iscloned");
                                }
                                _date = new Date();

                                createdate = sjo.get("createdate");
                                if (createdate instanceof Long)
                                    _date.setTime(sjo.getLong("createdate"));

                                if (createdate instanceof String)
                                    _date = _db.getDateFromString(sjo.getString("createdate"));

                                sjo.put("createdate", _date.getTime());
                                sjo.put("iscloned", ISCLONED);

                                Cash _cash = new Cash();
                                _cash.setCategory(_category.getCategoryID());
                                _cash.setContent(sjo.getString("content"));
                                _cash.setCreateDate(sjo.getLong("createdate"));
                                _cash.setRepeat(sjo.getInt("repeat"));
                                _cash.setTotal(sjo.getInt("total"));
                                _cash.setIsCloned(sjo.getInt("iscloned"));
                                _db.addCash(_cash);

                            } catch (SQLiteException ex) {
                                //ex.printStackTrace();
                            }
                        }
                    } catch (SQLiteException ex) {
                        //ex.printStackTrace();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Integer i) {
            progressDialog.setProgress(100);
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        }
    }

    private class writeBackup extends AsyncTask<Object, Integer, String> {

        private DataBase _db;
        private Uri _uri;
        private ProgressDialog progressDialog;

        public writeBackup(DataBase db, Uri uri, ProgressDialog progressDialog) {
            this._db = db;
            this._uri = uri;
            this.progressDialog = progressDialog;
            this.progressDialog.setProgress(0);
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                /* get data */
                JSONArray _listCategory = _db.getBackupCategoryData();
                int categoryMax = _listCategory.length(), index = 0;
                for (int a = 0; a < categoryMax; a++) {
                    JSONObject jo = _listCategory.getJSONObject(a);
                    JSONArray _listCash = _db.getBackupCashData(jo.getLong("id"));
                    jo.put("cash", _listCash);
                    jo.remove("id");
                    /* ui status */
                    double progressstate = (index + 1) * 100 / (categoryMax + 1);
                    progressDialog.setProgress((int) progressstate);
                    index++;
                }
                /* to file */
                ParcelFileDescriptor pfd = getActivity().getContentResolver().
                        openFileDescriptor(_uri, "w");
                FileOutputStream fileOutputStream =
                        new FileOutputStream(pfd.getFileDescriptor());
                fileOutputStream.write(_listCategory.toString().getBytes());
                fileOutputStream.close();
                pfd.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Fertig";
        }


        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String i) {
            progressDialog.setProgress(100);
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);

        }
    }
}
