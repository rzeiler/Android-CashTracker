package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;
import com.fmh.app.cashtracker.Models.ListMonthYear;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CashListActivity extends BaseListActivity {

    public static final String CASH_ITEM = "com.fmh.app.cashtracker.CATSH_ITEM";
    public static final String CUT_CASH_ITEM = "com.fmh.app.cashtracker.CUT_CASH_ITEM";

    private Category _category;
    private ListMonthYear _model = new ListMonthYear();
    private RecyclerView recyclerView;
    public CashAdapter mAdapter;
    private Context context;
    private SharedPreferences preference;
    private DataBase db;
    private Menu _menu;
    private TextView tvMonthLimit, tvYearLimit;
    private ProgressBar pbYearLimit, pbMonthLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        Intent intent = getIntent();
        _category = (Category) intent.getSerializableExtra(CategoryListActivity.CATEGORY_ITEM);
        toolbar.setTitle(_category.getTitle());

        getSupportActionBar().setTitle(String.format(getString(R.string.label_cashs), _category.getTitle()));
        getSupportActionBar().setSubtitle(getString(R.string.app_name));

        tvMonthLimit = findViewById(R.id.tvMonthLimit);
        tvYearLimit = findViewById(R.id.tvYearLimit);
        pbYearLimit = findViewById(R.id.pbYearLimit);
        pbMonthLimit = findViewById(R.id.pbMonthLimit);


        preference = PreferenceManager.getDefaultSharedPreferences(context);
        long CashId = preference.getLong(CashListActivity.CUT_CASH_ITEM, -1);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        /* create with interface */
        db = new DataBase(this);
              /* set */
        mAdapter = new CashAdapter(CashId, _model.data, new CashAdapter.Listener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent;
                intent = new Intent(context, CashEdit.class);
                intent.putExtra(CASH_ITEM, (Serializable) _model.data.get(position));
                intent.putExtra(CategoryListActivity.CATEGORY_ITEM, (Serializable) _category);
                startActivityForResult(intent, 1);

            }
        }, context);

        /* ui info */
        final RelativeLayout info = findViewById(R.id.cash_list_info);
        ImageButton closeButton = findViewById(R.id.closeButton);
        info.setVisibility(preference.getBoolean("cbxInfo", true) ? View.VISIBLE : View.GONE);

        if (preference.getBoolean("cbxInfo", true)) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    info.setVisibility(View.GONE);
                    Toast.makeText(context, getString(R.string.message_hide_info), Toast.LENGTH_LONG).show();
                    preference.edit().putBoolean("cbxInfo", false).apply();
                }
            });
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        /* performance */
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CashEdit.class);
                intent.putExtra(CASH_ITEM, (Serializable) null);
                intent.putExtra(CategoryListActivity.CATEGORY_ITEM, (Serializable) _category);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
            }
        });

        new CashAsyncTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cash_list, menu);

        _menu = menu;

        if (preference.getLong(CashListActivity.CUT_CASH_ITEM, -1) > 0)
            menu.findItem(R.id.action_paste).setVisible(true);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(queryTextListener);

        return true;
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // categoryList rebuild
            _model.data.clear();
            db.getCashs(_model, _category, newText.toLowerCase());
            mAdapter.notifyDataSetChanged();
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            Intent returnIntent = new Intent();
            setResult(CategoryListActivity.RESULT_UPDATE, returnIntent);

            finish();
            return true;
        }

        if (id == R.id.action_paste) {
            long CashId = preference.getLong(CashListActivity.CUT_CASH_ITEM, -1);
            Cash _cash = db.getCashById(CashId);
            _cash.setCategory(_category.getCategoryID());
            _model.data.add(_cash);
            /* update db */
            db.updateCash(_cash);
            /* update list */
            List<Cash> tempList = new ArrayList<Cash>(_model.data);
            _model.data.clear();
            /* update list */
            _model.data = new ArrayList<Cash>(tempList);
            mAdapter.notifyDataSetChanged();
            /* update pref */
            SharedPreferences.Editor editor = preference.edit();
            editor.putLong(CashListActivity.CUT_CASH_ITEM, -1);
            editor.commit();
            /* update menu */
            _menu.findItem(R.id.action_paste).setVisible(false);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Cash _cash = (Cash) data.getSerializableExtra(CASH_ITEM);
            if (resultCode == RESULT_OK) {
                /* search item by id */
                for (Cash item : (List<Cash>) _model.data) {
                    if (item.getCashID() == _cash.getCashID()) {
                        item.setContent(_cash.getContent());
                        item.setCreateDate(_cash.getCreateDate());
                        item.setTotal(_cash.getTotal());
                        item.setRepeat(_cash.getRepeat());
                    }
                }
                /* track change */
                mAdapter.notifyDataSetChanged();
            }

            if (resultCode == 3) {
                /* delete */
                /* search item by id */
                for (int i = 0; i < _model.data.size(); i++) {
                    Cash item = (Cash) _model.data.get(i);
                    if (item.getCashID() == _cash.getCashID()) {
                        _model.data.remove(i);
                    }
                }
                /* track change */
                mAdapter.notifyDataSetChanged();
            }

            if (resultCode == 4) {
                /* cut */
                _menu.findItem(R.id.action_paste).setVisible(true);
                List<Cash> tempList = new ArrayList<Cash>(_model.data);
                _model.data.clear();
                /* update list */
                _model.data = new ArrayList<Cash>(tempList);
                /* track change */
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public class CashAsyncTask extends AsyncTask<Object, Void, ListMonthYear> {

        @Override
        protected ListMonthYear doInBackground(Object... objects) {
            return db.getCashs(_model, _category);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ListMonthYear o) {
            mAdapter.notifyDataSetChanged();
            /* limit  */
            double iLm = Double.parseDouble(preference.getString("limitMonth", "1000"));
            double iLy = Double.parseDouble(preference.getString("limitYear", "12000"));
            double progressstate = 0;
            if (iLy < o.getYearSum()) {
                progressstate = 100;
            } else {
                progressstate = o.getYearSum() * 100 / iLy;
            }
            AnimateProgressBar(pbYearLimit, (int) progressstate);
            progressstate = 0;
            if (iLm < o.getMonthSum()) {
                progressstate = 100;
            } else {
                progressstate = o.getMonthSum() * 100 / iLm;
            }
            AnimateProgressBar(pbMonthLimit, (int) progressstate);
            tvMonthLimit.setText(String.format("%.2f", o.getMonthSum()));
            tvYearLimit.setText(String.format("%.2f", o.getYearSum()));
        }
    }

}
