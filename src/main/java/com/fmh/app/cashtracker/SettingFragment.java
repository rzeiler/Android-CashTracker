package com.fmh.app.cashtracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;
import com.fmh.app.cashtracker.Models.ListMonthYear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preference;
    private Context context;
    private ProgressDialog progressDialog;

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

                        Intent intent = new Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(intent, getString(R.string.title_activity_setting)), 200);
                    }

                });
                // Create the AlertDialog
                builder.create();
                builder.show();

                return false;
            }
        });

        Preference btnBackup = findPreference("btnBackup");
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(i, getString(R.string.title_activity_setting)), 100);

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
            boolean rightFile = false;
            Uri uri = data.getData();
            Pattern pNumber = Pattern.compile("fmh_backup_..........T.........json");
            Matcher mNumber = pNumber.matcher(uri.getPath());
            while (mNumber.find()) {
                rightFile = true;
            }
            if (rightFile) {
                String content = null;
                try {
                    content = readTextFromUri(uri);
                    showProgressDialog();
                    progressDialog.setTitle("Wiederherstellung");
                    DataBase _db = new DataBase(getActivity());
                    new RestoreDataBase(_db).execute(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.notification_wrong_backup_file), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 100 && data != null) {

            showProgressDialog();
            progressDialog.setTitle("Sicherung");
            Uri uri = data.getData();
            progressDialog.setMessage(uri.getPath());
            DataBase _db = new DataBase(getActivity());
            new CreateBackup(_db).execute();
        }
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("");
        progressDialog.setTitle("Wiederherstellung");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Schließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
    }

    public class RestoreDataBase extends AsyncTask<Object, Integer, Integer> {

        private DataBase _db;

        public RestoreDataBase(DataBase db) {
            this._db = db;
        }

        @Override
        protected Integer doInBackground(Object... params) {

            String JSON = (String) params[0];
            JSONArray ja;
            try {
                ja = new JSONArray(JSON);
                JSONObject jo, sjo;
                _db = new DataBase(getActivity());
                _db.ClearData();
                int max = ja.length();
                for (int index = 0; index < max; index++) {
                    /* state display */
                    double progressstate = (index + 1) * 100 / (max + 1);
                    publishProgress((int) progressstate);
                    jo = ja.getJSONObject(index);
                    /* add category */
                    Date _date = new Date();
                    try {
                        _date.setTime(0);
                        Object createdate = jo.get("createdate");
                        if (createdate instanceof Long) {
                            _date.setTime(jo.getLong("createdate"));
                        }
                        if (createdate instanceof String) {
                            _date = _db.getDateFromString(jo.getString("createdate"));
                        }
                        /* overwrite */
                        jo.put("createdate", _date.getTime());
                        jo.put("id", -1);

                        Category _category = new Category();
                        _category.setTitle(jo.getString("title"));
                        _category.setCreateDate(jo.getLong("createdate"));
                        _category.setUser(jo.getString("user"));
                        _category.setRating(jo.getInt("rating"));
                        long iId = _db.addCategory(_category);
                        _category.setCategoryID(Long.valueOf(iId).intValue());
                        Log.w("Category Add", String.format("%s", _category.getCategoryID()));

                        JSONArray cashArray = jo.getJSONArray("cash");
                        for (int cashIndex = 0; cashIndex < cashArray.length(); cashIndex++) {
                            sjo = cashArray.getJSONObject(cashIndex);
                            /* add cash */
                            try {
                                int ISCLONED = 1;
                                if (sjo.has("iscloned")) {
                                    ISCLONED = sjo.getInt("iscloned");
                                }
                                _date.setTime(0);

                                createdate = sjo.get("createdate");
                                if (createdate instanceof Long)
                                    _date.setTime(sjo.getLong("createdate"));

                                if (createdate instanceof String)
                                    _date = _db.getDateFromString(sjo.getString("createdate"));

                                sjo.put("createdate", _date.getTime());
                                sjo.put("id", -1);
                                sjo.put("iscloned", ISCLONED);

                                Cash _cash = new Cash();
                                _cash.setCategory(_category.getCategoryID());
                                _cash.setContent(sjo.getString("content"));
                                _cash.setCreateDate(sjo.getLong("createdate"));
                                _cash.setRepeat(sjo.getInt("repeat"));
                                _cash.setTotal(sjo.getInt("total"));
                                _cash.setIsCloned(sjo.getInt("iscloned"));
                                long cashid = _db.addCash(_cash);
                            } catch (SQLiteException ex) {
                                //ex.printStackTrace();
                            }
                        }
                    } catch (SQLiteException ex) {
                        //ex.printStackTrace();

                    }
                }
            } catch (JSONException e) {
                //e.printStackTrace();

            } catch (SQLiteException ex) {
                //ex.printStackTrace();

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

    public class CreateBackup extends AsyncTask<Object, Integer, Integer> {

        private DataBase _db;

        public CreateBackup(DataBase db) {
            this._db = db;
        }

        @Override
        protected Integer doInBackground(Object... params) {

            ListMonthYear model = new ListMonthYear();
            model = _db.getCategorys(model, preference.getString("active_user", "default"));
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

}
