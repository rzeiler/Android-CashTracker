package com.fmh.app.cashtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fmh.app.cashtracker.Models.Category;
import com.fmh.app.cashtracker.Models.ListMonthYear;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class CategoryListActivity extends BaseListActivity {

    public static final String CATEGORY_ITEM = "com.fmh.app.cashtracker.CATEGORY_ITEM";

    public static final Integer RESULT_UPDATE = 3;

    private ListMonthYear _categoryList = new ListMonthYear();
    private RecyclerView recyclerView;
    public CategoryAdapter mAdapter;
    private Context context;
    private SharedPreferences preference;
    private DataBase db;
    private TextView tvMonthLimit, tvYearLimit;
    private ProgressBar pbYearLimit, pbMonthLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        getSupportActionBar().setTitle(getString(R.string.label_categorys));
        getSupportActionBar().setSubtitle(getString(R.string.app_name));

        tvMonthLimit = findViewById(R.id.tvMonthLimit);
        tvYearLimit = findViewById(R.id.tvYearLimit);
        pbYearLimit = findViewById(R.id.pbYearLimit);
        pbMonthLimit = findViewById(R.id.pbMonthLimit);

        preference = PreferenceManager.getDefaultSharedPreferences(context);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /* create with interface */
        mAdapter = new CategoryAdapter(_categoryList.data, new CategoryAdapter.Listener() {
            @Override
            public void onItemClick(View v, int position) {


                Intent intent;

                switch (v.getId()) {
                    case R.id.tvBigletter:

                        intent = new Intent(context, CategoryDetailsActivity.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) _categoryList.data.get(position));
                        startActivityForResult(intent, 1);

                        break;
                    default:
                        intent = new Intent(context, CashListActivity.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) _categoryList.data.get(position));
                        startActivityForResult(intent, 1);
                        break;
                }


            }
        }, context);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        /* performance */
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        db = new DataBase(this);

        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryEdit.class);
                intent.putExtra(CATEGORY_ITEM, (Serializable) null);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
            }
        });

        new CategoryAsyncTask().execute();




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);

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
            _categoryList.data.clear();
            db.getCategorys(_categoryList, preference.getString("active_user", "default"), newText.toLowerCase());
            mAdapter.notifyDataSetChanged();
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, Setting.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Category _category = (Category) data.getSerializableExtra(CATEGORY_ITEM);
                /* search item by id */
                for (Category item : (List<Category>) _categoryList.data) {
                    if (item.getCategoryID() == _category.getCategoryID()) {
                        item.setTitle(_category.getTitle());
                        item.setRating(_category.getRating());
                    }
                }

                /* track change */
                mAdapter.notifyDataSetChanged();
            }

            if (resultCode == RESULT_UPDATE) {
                _categoryList.data.clear();
                new CategoryAsyncTask().execute();
            }
        }
    }

    public class CategoryAsyncTask extends AsyncTask<Object, Void, ListMonthYear> {

        @Override
        protected ListMonthYear doInBackground(Object... objects) {
            return db.getCategorys(_categoryList, preference.getString("active_user", "default"));
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
