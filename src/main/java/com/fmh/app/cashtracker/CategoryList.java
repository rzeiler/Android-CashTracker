package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /* create with interface */
        mAdapter = new CategoryAdapter(categoryListFiltered, new CategoryAdapter.Listener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent;

                switch (v.getId()) {
                    case R.id.title:
                        intent = new Intent(context, CategoryDetails.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) categoryListFiltered.get(position));
                        startActivityForResult(intent,1);
                        break;
                    default:
                        intent = new Intent(context, CategoryEdit.class);
                        intent.putExtra(CATEGORY_ITEM, (Serializable) categoryListFiltered.get(position));
                        startActivityForResult(intent,1);
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


        for (int i = 0; i < 10; i++) {
            prepareMovieData();
        }
        for (Category cat : categoryListRoot) {
            categoryListFiltered.add(cat);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Message", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Intent intent = new Intent(context, CategoryEdit.class);
                intent.putExtra(CATEGORY_ITEM, (Serializable) null);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,1);
            }
        });
    }

    private void prepareMovieData() {
        Category movie = new Category("Mad Max: Fury Road", "Action & Adventure", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Inside Out", "Animation, Kids & Family", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Star Wars: Episode VII - The Force Awakens", "Action", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Shaun the Sheep", "Animation", 2015);
        categoryListRoot.add(movie);

        movie = new Category("The Martian", "Science Fiction & Fantasy", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Mission: Impossible Rogue Nation", "Action", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Up", "Animation", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Star Trek", "Science Fiction", 2015);
        categoryListRoot.add(movie);

        movie = new Category("The LEGO Movie", "Animation", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Iron Man", "Action & Adventure", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Aliens", "Science Fiction", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Chicken Run", "Animation", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Back to the Future", "Science Fiction", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Raiders of the Lost Ark", "Action & Adventure", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Goldfinger", "Action & Adventure", 2015);
        categoryListRoot.add(movie);

        movie = new Category("Guardians of the Galaxy", "Science Fiction & Fantasy", 2015);
        categoryListRoot.add(movie);

        mAdapter.notifyDataSetChanged();
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
            Intent intent = new Intent(context, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                Toast.makeText(context, "RESULT_OK " + String.valueOf(data.getStringExtra("result")), Toast.LENGTH_SHORT).show();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "RESULT_CANCELED " , Toast.LENGTH_SHORT).show();

            }
        }
    }

}
