package com.fmh.app.cashtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fmh.app.cashtracker.Models.Cash;
import com.fmh.app.cashtracker.Models.Category;
import com.fmh.app.cashtracker.Models.CategoryDetailsItem;
import com.fmh.app.cashtracker.Models.ListMonthYear;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ralf on 01.02.18.
 */

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CashTracker.db";
    /* filds */
    private static final String KEY_cID = "id";
    private static final String KEY_cTITLE = "title";
    private static final String KEY_cCREATEDATE = "int_create_date";
    private static final String KEY_cUSER = "user";
    private static final String KEY_cRATING = "rating";
    private static final String KEY_sID = "id";
    private static final String KEY_sCONTENT = "content";
    private static final String KEY_sCREATEDATE = "int_create_date";
    private static final String KEY_sCATEGORY = "category";
    private static final String KEY_sREPEAT = "repeat";
    private static final String KEY_sTOTAL = "total";
    private static final String KEY_sISCLONED = "iscloned";
    /* table */
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_CASH = "cash";

    /* create */
    private static final String SQL_CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "(" + KEY_cID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + KEY_cTITLE + " TEXT," + KEY_cCREATEDATE + " INTEGER," + KEY_cUSER
            + " TEXT," + KEY_cRATING + " INTEGER" + ")";

    private static final String SQL_CREATE_CASH = "CREATE TABLE " + TABLE_CASH + "(" + KEY_sID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_sCONTENT
            + " TEXT," + KEY_sCREATEDATE + " INTEGER," + KEY_sCATEGORY + " INTEGER,"
            + KEY_sREPEAT + " INTEGER," + KEY_sTOTAL + " DECIMAL(10,2)," + KEY_sISCLONED
            + " INTEGER DEFAULT 0, FOREIGN KEY(" + KEY_sCATEGORY + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_cID + "))";

    private SimpleDateFormat DeDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat EnDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat Test = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORY);
        db.execSQL(SQL_CREATE_CASH);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        long i = 0;
        /* build values */
        ContentValues values = new ContentValues();
        values.put(KEY_cTITLE, _category.getTitle());
        values.put(KEY_cCREATEDATE, _category.getCreateDate());
        values.put(KEY_cUSER, _category.getUser());
        values.put(KEY_cRATING, _category.getRating());
        /* call db */
        i = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return i;
    }

    public int updateCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        // build values
        ContentValues values = new ContentValues();
        values.put(KEY_cTITLE, _category.getTitle());
        values.put(KEY_cCREATEDATE, _category.getCreateDate());
        values.put(KEY_cUSER, _category.getUser());
        values.put(KEY_cRATING, _category.getRating());
        // call db
        return db.update(TABLE_CATEGORY, values, KEY_cID + "=?",
                new String[]{String.valueOf(_category.getCategoryID())});
    }

    public int deleteCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        // call db
        return db.delete(TABLE_CATEGORY, KEY_cID + " = ?", new String[]{String.valueOf(_category.getCategoryID())});
    }

    public Cash getCashById(long Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cash _cash = new Cash();

        String[] tableColumns = new String[]{
                KEY_sID,
                KEY_sCONTENT,
                KEY_sCREATEDATE,
                KEY_sCATEGORY,
                KEY_sREPEAT,
                KEY_sTOTAL,
                KEY_sISCLONED
        };
        String whereClause = String.format("%s = ?", KEY_sID);

        String[] whereArgs = new String[]{
                String.valueOf(Id)
        };

        Cursor cursor = db.query(TABLE_CASH, tableColumns, whereClause, whereArgs,
                null, null, KEY_sCREATEDATE + " DESC");

        if (cursor.moveToFirst()) {
            do {

                _cash.setCashID(cursor.getInt(0));
                _cash.setContent(cursor.getString(1));
                _cash.setCreateDate(cursor.getLong(2));
                _cash.setCategory(cursor.getInt(3));
                _cash.setRepeat(cursor.getInt(4));
                _cash.setTotal(cursor.getDouble(5));
                _cash.setIsCloned(cursor.getInt(6));

                Log.w("Cash Date", Test.format(cursor.getLong(2)) + " = " + String.format("%s", cursor.getLong(2)));


            } while (cursor.moveToNext());
        }
        cursor.close();

        return _cash;
    }

    public long addCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        // build values
        ContentValues values = new ContentValues();
        values.put(KEY_sCONTENT, _cash.getContent());
        values.put(KEY_sCREATEDATE, _cash.getCreateDate());
        values.put(KEY_sCATEGORY, _cash.getCategory());
        values.put(KEY_sREPEAT, _cash.getRepeat());
        values.put(KEY_sTOTAL, _cash.getTotal());
        values.put(KEY_sISCLONED, _cash.getIsCloned());
        // call db
        return db.insert(TABLE_CASH, null, values);
    }

    public int updateCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        // build values
        ContentValues values = new ContentValues();
        values.put(KEY_sCONTENT, _cash.getContent());
        values.put(KEY_sCREATEDATE, _cash.getCreateDate());
        values.put(KEY_sCATEGORY, _cash.getCategory());
        values.put(KEY_sREPEAT, _cash.getRepeat());
        values.put(KEY_sTOTAL, _cash.getTotal());
        values.put(KEY_sISCLONED, _cash.getIsCloned());
        // call db
        return db.update(TABLE_CASH, values, KEY_sID + " = ?", new String[]{String.valueOf(_cash.getCashID())});
    }

    public int deleteCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        // call db
        return db.delete(TABLE_CASH, KEY_sID + " = ?", new String[]{String.valueOf(_cash.getCashID())});
    }

    public void ClearData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CATEGORY);
        db.execSQL("DELETE FROM " + TABLE_CASH);
    }

    public Date getDateFromString(String s) {
        Date _date = null;
        try {
            _date = DeDateFormat.parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (_date == null) {
            try {
                _date = EnDateFormat.parse(s);
            } catch (java.text.ParseException e) {

            }
        }
        return _date;
    }

    public ListMonthYear getCategorys(ListMonthYear _categoryList, String user) {
        return getCategorys(_categoryList, user, "");
    }

    public ListMonthYear getCategorys(ListMonthYear _categoryList, String user, String filterTitel) {

        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        if (filterTitel != "")
            filterTitel = "a." + KEY_cTITLE + " LIKE '" + filterTitel + "%' AND ";

        String query = "SELECT a." + KEY_cID + ", a." + KEY_cTITLE + ", SUM(IFNull(b." + KEY_sTOTAL + ",0.00)),  a." + KEY_cCREATEDATE +
                ", a." + KEY_cRATING + ", a." + KEY_cUSER + ", COUNT(a." + KEY_cID + ") FROM " + TABLE_CATEGORY + " AS a LEFT OUTER JOIN " + TABLE_CASH + " AS b ON b." + KEY_sCATEGORY + "=a.id AND b." +
                KEY_sCREATEDATE + " >= ? WHERE " + filterTitel + " a." + KEY_cUSER + " = ? GROUP BY a." + KEY_cID + " ORDER BY a." + KEY_cRATING + " DESC, a." + KEY_cTITLE + " ASC;";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(c.getTimeInMillis()),
                user
        });

        if (cursor.moveToFirst()) {
            do {

                Category _category = new Category();
                _category.setCategoryID(cursor.getInt(0));
                _category.setTitle(cursor.getString(1));
                _category.setTotal(cursor.getDouble(2));
                _category.setCreateDate(cursor.getLong(3));
                _category.setRating(cursor.getInt(4));
                _category.setUser(cursor.getString(5));
                _categoryList.data.add(_category);
                _category = null;

            } while (cursor.moveToNext());
        }
        cursor.close();
        /* get month sum */
        String[] tableColumns = new String[]{
                "IFNULL(SUM(a." + KEY_sTOTAL + "),0)"
        };
        String whereClause = "a." + KEY_sCATEGORY + "=b." + KEY_cID + " AND a." + KEY_sCREATEDATE + " >= ? AND b." + KEY_cUSER + " = ?";

        String[] whereArgs = new String[]{
                String.valueOf(c.getTimeInMillis()),
                user
        };

        cursor = db.query(TABLE_CASH + " AS a," + TABLE_CATEGORY + " AS b", tableColumns, whereClause, whereArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                _categoryList.setMonthSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        /* get year sum */
        c.set(Calendar.MONTH, 0);
        whereArgs = new String[]{
                String.valueOf(c.getTimeInMillis()),
                user
        };

        cursor = db.query(TABLE_CASH + " AS a," + TABLE_CATEGORY + " AS b", tableColumns, whereClause, whereArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                _categoryList.setYearSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return _categoryList;
    }

    public ListMonthYear getCategoryDetails(ListMonthYear _model, Category category) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, -1);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        /* get month sum */
        String[] tableColumns = new String[]{
                "IFNULL(SUM(" + KEY_sTOTAL + "),0)"
        };
        String whereClause = KEY_sCATEGORY + "=? AND " + KEY_sCREATEDATE + " >= ?";

        String[] whereArgs = new String[]{
                String.valueOf(category.getCategoryID()),
                String.valueOf(c.getTimeInMillis())
        };

        Log.w("getCategoryDetails", Test.format(c.getTimeInMillis()));


        cursor = db.query(TABLE_CASH, tableColumns, whereClause, whereArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                _model.setMonthSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        /* get year sum */
        c.set(Calendar.MONTH, 0);

        Log.w("und", Test.format(c.getTimeInMillis()));

        whereArgs = new String[]{
                String.valueOf(category.getCategoryID()),
                String.valueOf(c.getTimeInMillis())
        };

        cursor = db.query(TABLE_CASH, tableColumns, whereClause, whereArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                _model.setYearSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        /* year and month */

        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 3);

        String striftime = "strftime('%Y',datetime(a.int_create_date/1000, 'unixepoch')), strftime('%m',datetime(a.int_create_date/1000, 'unixepoch')) ";
        String query = "SELECT " + striftime + ", IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.category = ? AND a.int_create_date >= ? AND b.user = ? GROUP BY " + striftime + " ORDER BY a.int_create_date DESC;";
        cursor = db.rawQuery(query, new String[]{String.valueOf(category.getCategoryID()), String.valueOf(c.getTimeInMillis()), category.getUser()});

        if (cursor.moveToFirst()) {
            do {
                /* get name */
                c = Calendar.getInstance();
                c.set(Calendar.MONTH, (cursor.getInt(1) - 1));
                c.set(Calendar.YEAR, cursor.getInt(0));
                c.set(Calendar.DAY_OF_MONTH, 1);
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                /* set item */
                CategoryDetailsItem _categorySum = new CategoryDetailsItem(String.format("%s %s", cursor.getString(0), month), cursor.getDouble(2));
                _model.data.add(_categorySum);
                _categorySum = null;
            } while (cursor.moveToNext());
        }
        cursor.close();


        return _model;
    }

    public ListMonthYear getCashs(ListMonthYear _model, Category category) {
        return getCashs(_model, category, "");
    }

    public ListMonthYear getCashs(ListMonthYear _model, Category category, String filterTitel) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] tableColumns = new String[]{
                KEY_sID,
                KEY_sCONTENT,
                KEY_sCREATEDATE,
                KEY_sCATEGORY,
                KEY_sREPEAT,
                KEY_sTOTAL,
                KEY_sISCLONED
        };
        String whereClause = String.format("%s = ?", KEY_sCATEGORY);

        String[] whereArgs = new String[]{
                String.valueOf(category.getCategoryID())
        };

        Cursor cursor = db.query(TABLE_CASH, tableColumns, whereClause, whereArgs,
                null, null, KEY_sCREATEDATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Cash _cash = new Cash();
                _cash.setCashID(cursor.getInt(0));
                _cash.setContent(cursor.getString(1));
                _cash.setCreateDate(cursor.getLong(2));
                _cash.setCategory(cursor.getInt(3));
                _cash.setRepeat(cursor.getInt(4));
                _cash.setTotal(cursor.getDouble(5));
                _cash.setIsCloned(cursor.getInt(6));
                _model.data.add(_cash);

                Log.w("Cash Date", cursor.getString(1) + " = " + Test.format(cursor.getLong(2)) + " = " + String.format("%s", cursor.getLong(2)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        /* get month sum */
        tableColumns = new String[]{
                "IFNULL(SUM(" + KEY_sTOTAL + "),0)"
        };
        whereClause = "" + KEY_sCATEGORY + "=? AND " + KEY_sCREATEDATE + " >= ?";

        whereArgs = new String[]{
                String.valueOf(category.getCategoryID()),
                String.valueOf(c.getTimeInMillis()),
        };

        cursor = db.query(TABLE_CASH , tableColumns, whereClause, whereArgs,
                null, null, KEY_sCREATEDATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                _model.setMonthSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        /* get year sum */
        c.set(Calendar.MONTH, 0);
        whereArgs = new String[]{
                String.valueOf(category.getCategoryID()),
                String.valueOf(c.getTimeInMillis()),
        };

        cursor = db.query(TABLE_CASH, tableColumns, whereClause, whereArgs,
                null, null, KEY_sCREATEDATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                _model.setYearSum(cursor.getDouble(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return _model;
    }
}
