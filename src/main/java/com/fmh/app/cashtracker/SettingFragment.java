package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preference;
    private Context context;

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

        Preference btnBackup = findPreference("btnBackup");
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, getString(R.string.title_activity_setting)), 200);

                return true;
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

            String content = null;
            try {
                content = readTextFromUri(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataBase _db = new DataBase(getActivity());
            new RestoreDataBase(_db).execute(content);

            Toast.makeText(getActivity(), String.format("DATEI '%s' (%d) = (%d) = %s", uri, requestCode, requestCode, content), Toast.LENGTH_SHORT).show();
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

                                Log.w("Cash Add", String.format("%s, %s", cashid, iId));

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

        }

        protected void onPostExecute(Integer i) {
            Toast.makeText(getActivity(), "Wiederherstellung abgeschloßen.", Toast.LENGTH_SHORT).show();
        }
    }

}
