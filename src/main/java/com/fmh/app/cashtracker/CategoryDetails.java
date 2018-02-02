package com.fmh.app.cashtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import static com.fmh.app.cashtracker.CategoryList.CATEGORY_ITEM;

public class CategoryDetails extends AppCompatActivity {

    private Context context;
    private Category _category;
    private Boolean _hasChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        Intent intent = getIntent();
        _category = (Category) intent.getSerializableExtra(CATEGORY_ITEM);

        TextView _textView = (TextView) findViewById(R.id.detail_text);
        _textView.setText(_category.getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CategoryEdit.class);
                intent.putExtra(CATEGORY_ITEM, (Serializable) _category);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(_category.getTitle());
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
                setResult(RESULT_OK, returnIntent);
            } else {
                setResult(RESULT_CANCELED, returnIntent);
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                _hasChange = true;
                Toast.makeText(context, "RESULT_OK " + String.valueOf(data.getStringExtra("result")), Toast.LENGTH_SHORT).show();

            }
            if (resultCode == RESULT_CANCELED) {
                _hasChange = false;
                Toast.makeText(context, "RESULT_CANCELED ", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
