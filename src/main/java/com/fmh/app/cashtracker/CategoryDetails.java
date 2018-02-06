package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.fmh.app.cashtracker.CategoryList.CATEGORY_ITEM;

public class CategoryDetails extends AppCompatActivity {

    private Context context;
    private Category _category;
    private Boolean _hasChange = false;
    private TextView _detailsMonat, _detailsJahr;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private List<CategorySum> categorySumList = new ArrayList<>();
    public CategoryDetailsAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.category_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        _category = (Category) intent.getSerializableExtra(CATEGORY_ITEM);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setTitle(_category.getTitle());

        SharedPreferences _sp = PreferenceManager.getDefaultSharedPreferences(context);

        DataBase db = new DataBase(this);
        categorySumList = db.getCategorySum(categorySumList, _sp.getString("active_user", "default"), _category);

        double maxTotal = 0.0;

        for (CategorySum item : categorySumList) {
            if (maxTotal < item.getTotal())
                maxTotal = item.getTotal();
        }

        _adapter = new CategoryDetailsAdapter(categorySumList, context, maxTotal);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(_adapter);
        /* performance */
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        _adapter.notifyDataSetChanged();

        _detailsMonat = (TextView) findViewById(R.id.detailsMonat);
        _detailsJahr = (TextView) findViewById(R.id.detailsJahr);

        Double sumYear = db.getSum(Calendar.YEAR, _sp.getString("_active_user", "default"), _category);
        Double sumMonth = db.getSum(Calendar.MONTH, _sp.getString("_active_user", "default"), _category);

        _detailsJahr.setText(String.format("Jahr   %.2f €", sumYear));
        _detailsMonat.setText(String.format("Monat %.2f €", sumMonth));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), CategoryEdit.class);
                intent.putExtra(CATEGORY_ITEM, (Serializable) _category);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            if (_hasChange) {
                returnIntent.putExtra("result", "Gespeichert");
                returnIntent.putExtra(CATEGORY_ITEM, (Serializable) _category);
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                _hasChange = true;
                _category = (Category) data.getSerializableExtra(CATEGORY_ITEM);
                collapsingToolbarLayout.setTitle(_category.getTitle());
            }
            if (resultCode == RESULT_CANCELED) {
                _hasChange = false;
                Toast.makeText(context, "RESULT_CANCELED ", Toast.LENGTH_SHORT).show();
            }
        }


    }


}
