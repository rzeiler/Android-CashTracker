package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import java.util.Calendar;

public class CategoryEdit extends AppCompatActivity {

    private Category _category;
    private Context context;

    private EditText etTitle;
    private RatingBar rbFirma;
    private Button bDate;

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        Intent intent = getIntent();
        _category = (Category) intent.getSerializableExtra(CategoryList.CATEGORY_ITEM);

        //toolbar.setTitle(_category.getTitle());

        //bDate = (Button) findViewById(R.id.bDate);
        etTitle = (EditText) findViewById(R.id.etCategory);
        rbFirma = (RatingBar) findViewById(R.id.rbPosition);

        if (_category != null) {
            etTitle.setText(_category.getTitle());
            rbFirma.setRating(_category.getRating());
        } else {
            _category = new Category();
            _category.setUser("test");
            _category.setCreateDate(calendar.getTimeInMillis());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_category, menu);

        menu.findItem(R.id.action_send).setVisible(true);
        menu.findItem(R.id.action_delete).setVisible((_category != null));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED,returnIntent);
            finish();
            return true;
        }

        if (id ==  R.id.action_send) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result","Gespeichert");
            setResult(RESULT_OK,returnIntent);
            finish();
            return true;
        }

        if (id ==  R.id.action_delete) {
            Intent returnIntent = new Intent( CategoryEdit.this,CategoryList.class  );
            returnIntent.putExtra("result","Gelöscht");
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            setResult(RESULT_OK,returnIntent);

            finish();
            return true;
        }




        return super.onOptionsItemSelected(item);
    }

}
