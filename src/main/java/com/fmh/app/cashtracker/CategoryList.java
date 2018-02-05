package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryList extends AppCompatActivity {

    public static final String CATEGORY_ITEM = "com.fmh.app.cashtracker.CATEGORY_ITEM";

    private List<Category> categoryListFiltered = new ArrayList<>();
    public List<Category> categoryListRoot = new ArrayList<>();
    private RecyclerView recyclerView;
    public CategoryAdapter mAdapter;
    private Context context;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        preference = PreferenceManager.getDefaultSharedPreferences(context);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /* create with interface */
        mAdapter = new CategoryAdapter(categoryListFiltered, new CategoryAdapter.Listener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent;

                switch (v.getId()) {
                    case R.id.tvBigletter:

                        intent = new Intent(context, CategoryDetails.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) categoryListFiltered.get(position));
                        startActivityForResult(intent, 1);

                        break;
                    default:
                        intent = new Intent(context, CategoryEdit.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) categoryListFiltered.get(position));
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

        DataBase db = new DataBase(this);
        db.getCategorys(categoryListFiltered, preference.getString("active_user", "default"));
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

            // categoryList
            categoryListFiltered.clear();
            for (Category cat : categoryListRoot) {
                if (cat.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                    categoryListFiltered.add(cat);
                }
            }
            mAdapter.notifyDataSetChanged();
            if (newText != "")
                Toast.makeText(context, String.format("Suche nach '%s' (%d) ", newText, categoryListFiltered.size()), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(context, "RESULT_OK " + String.valueOf(data.getStringExtra("result")), Toast.LENGTH_SHORT).show();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "RESULT_CANCELED ", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
