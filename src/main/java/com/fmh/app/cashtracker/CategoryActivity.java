package com.fmh.app.cashtracker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoryAdapter mAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /* create with interface */
        mAdapter = new CategoryAdapter(categoryList, new CategoryAdapter.Listener() {
            @Override
            public void onItemClick(View view, int position) {



                Toast.makeText(context, "click ok button at " + position + " Viewtag: " + String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
            }
        }, context);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void prepareMovieData() {
        Category movie = new Category("Mad Max: Fury Road", "Action & Adventure", 2015);
        categoryList.add(movie);

        movie = new Category("Inside Out", "Animation, Kids & Family", 2015);
        categoryList.add(movie);

        movie = new Category("Star Wars: Episode VII - The Force Awakens", "Action", 2015);
        categoryList.add(movie);

        movie = new Category("Shaun the Sheep", "Animation", 2015);
        categoryList.add(movie);

        movie = new Category("The Martian", "Science Fiction & Fantasy", 2015);
        categoryList.add(movie);

        movie = new Category("Mission: Impossible Rogue Nation", "Action", 2015);
        categoryList.add(movie);

        movie = new Category("Up", "Animation", 2015);
        categoryList.add(movie);

        movie = new Category("Star Trek", "Science Fiction", 2015);
        categoryList.add(movie);

        movie = new Category("The LEGO Movie", "Animation", 2015);
        categoryList.add(movie);

        movie = new Category("Iron Man", "Action & Adventure", 2015);
        categoryList.add(movie);

        movie = new Category("Aliens", "Science Fiction",2015);
        categoryList.add(movie);

        movie = new Category("Chicken Run", "Animation", 2015);
        categoryList.add(movie);

        movie = new Category("Back to the Future", "Science Fiction", 2015);
        categoryList.add(movie);

        movie = new Category("Raiders of the Lost Ark", "Action & Adventure", 2015);
        categoryList.add(movie);

        movie = new Category("Goldfinger", "Action & Adventure", 2015);
        categoryList.add(movie);

        movie = new Category("Guardians of the Galaxy", "Science Fiction & Fantasy", 2015);
        categoryList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
