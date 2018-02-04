package com.fmh.app.cashtracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ralf on 04.02.2018.
 */

class CategoryAsyncList extends AsyncTask<Object, Void, List<Category>> {

    private List<Category> _list = new ArrayList<>();
    private CategoryAdapter _adapter;
    private DataBase _DataBase;
    private String _user;
    private Activity _activity;

    public CategoryAsyncList(CategoryAdapter adapter, List<Category> list, Activity activity, String user) {
        this._adapter = adapter;
        this._list = list;
        this._DataBase = new DataBase(activity);
        this._user = user;
    }

    @Override
    protected List<Category> doInBackground(Object... objects) {
        _list = _DataBase.getCategorys(_list, _user);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Category> o) {
        _adapter.notifyDataSetChanged();

    }
}
