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
import android.os.ParcelFileDescriptor;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
                SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd'T'hh_mm_ss");
                String filename = String.format("cashtracker_backup_%s.json", ft.format(new Date()));
                createFile("*/*", filename);
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

            boolean rightFile = false;

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

        if (requestCode == WRITE_REQUEST_CODE && data != null) {

            Uri uri = data.getData();

            if (progressDialog != null)
                progressDialog.dismiss();

            showProgressDialog();
            progressDialog.setTitle("Sicherung");

            DataBase _db = new DataBase(getActivity());
            new writeBackup(_db, uri).execute();

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


    public class readBackup extends AsyncTask<Object, Integer, Integer> {

        private DataBase _db;
        private Uri _uri;

        public readBackup(DataBase db, Uri uri) {
            this._db = db;
            this._uri = uri;
        }

        @Override
        protected Integer doInBackground(Object... params) {

            try {
                String content = readTextFromUri(_uri);
                Gson gson = new Gson();
                Category[] mcArray = gson.fromJson(content, Category[].class);
                List<Category> mcList = Arrays.asList(mcArray);
                int max = mcList.size(), index = 0;
                for (Category _category : mcList) {
                    long iId = _db.addCategory(_category);
                    /* update satus */
                    double progressstate = (index + 1) * 100 / (max + 1);
                    publishProgress((int) progressstate);
                    index++;
                    for (Cash _cash : _category.getCashes()) {
                        _cash.setCategory(iId);
                        _db.addCash(_cash);
                    }
                }

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

    private class writeBackup extends AsyncTask<Object, Integer, Integer> {

        private DataBase _db;
        private Uri _uri;

        public writeBackup(DataBase db, Uri uri) {
            this._db = db;
            this._uri = uri;
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                List<Category> _list = _db.getBackupData();
                int max = _list.size(), currentIndex = 0;

                Gson gson = new Gson();
                String jsonString = gson.toJson(_list);

                try {
                    ParcelFileDescriptor pfd = getActivity().getContentResolver().
                            openFileDescriptor(_uri, "w");
                    FileOutputStream fileOutputStream =
                            new FileOutputStream(pfd.getFileDescriptor());
                    fileOutputStream.write(jsonString.getBytes());
                    // Let the document provider know you're done by closing the stream.
                    fileOutputStream.close();
                    pfd.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
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

}
