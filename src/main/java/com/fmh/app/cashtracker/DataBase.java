package com.fmh.app.cashtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    private static final String TABLE_CASH_MOVE = "cash_move";

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

        String[] tableColumns = new String[] {
                KEY_sID,
                KEY_sCONTENT,
                KEY_sCREATEDATE,
                KEY_sCATEGORY,
                KEY_sREPEAT,
                KEY_sTOTAL,
                KEY_sISCLONED
        };
        String whereClause = String.format("%s = ?",KEY_sID) ;

        String[] whereArgs = new String[] {
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

    public List<Category> getCategorys(List<Category> _list, String user) {
        return getCategorys(_list, user, "");
    }

    public List<Category> getCategorys(List<Category> _list, String user, String filterTitel) {

        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 0);

        String query = "SELECT a.id, a.title, SUM(IFNull(b.total,0.00)),  a." + KEY_cCREATEDATE + ", a.rating, a.user FROM category AS a LEFT OUTER JOIN cash AS b ON b.category=a.id AND b." + KEY_sCREATEDATE + " >= ? WHERE a.user='" + user + "' GROUP BY a." + KEY_cID + " ORDER BY a.rating DESC, a.title ASC;";

        if (filterTitel != "") {
            query = "SELECT a.id, a.title, SUM(IFNull(b.total,0.00)),  a." + KEY_cCREATEDATE + ", a.rating, a.user FROM category AS a LEFT OUTER JOIN cash AS b ON b.category=a.id AND b." + KEY_sCREATEDATE + " >= ? WHERE a.title LIKE '" + filterTitel + "%' AND a.user='" + user + "' GROUP BY a." + KEY_cID + " ORDER BY a.rating DESC, a.title ASC;";
        }

        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            do {

                Category _category = new Category();
                _category.setCategoryID(cursor.getInt(0));
                _category.setTitle(cursor.getString(1));
                _category.setTotal(cursor.getDouble(2));
                _category.setCreateDate(cursor.getLong(3));
                _category.setRating(cursor.getInt(4));
                _category.setUser(cursor.getString(5));
                _list.add(_category);
                _category = null;

            } while (cursor.moveToNext());
        }
        cursor.close();

        return _list;
    }

    public Double getSum(int Range, String user, Category category) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_MONTH, 0);
        switch (Range) {
            case Calendar.YEAR:
                c.set(Calendar.MONTH, 0);
                break;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        Double result = 0.0;
        /* year and month */
        Cursor cursor = null;
        if (category == null) {
            query = "SELECT IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.int_create_date >= ? AND b.user = ?;";
            cursor = db.rawQuery(query, new String[]{String.valueOf(c.getTimeInMillis()), user});
        } else {
            query = "SELECT IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.int_create_date >= ? AND b.user = ? AND b.id = ?;";
            cursor = db.rawQuery(query, new String[]{String.valueOf(c.getTimeInMillis()), user, String.valueOf(category.getCategoryID())});
        }
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public List<CategorySum> getCategorySum(List<CategorySum> _list, String user, Category category) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 3);

        SQLiteDatabase db = this.getReadableDatabase();
        /* year and month */
        Cursor cursor = null;
        String striftime = "strftime('%Y',datetime(a.int_create_date/1000, 'unixepoch')), strftime('%m',datetime(a.int_create_date/1000, 'unixepoch')) ";
        String query = "SELECT " + striftime + ", IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.category = ? AND a.int_create_date >= ? AND b.user = ? GROUP BY " + striftime + " ORDER BY strftime('%Y',datetime(a.int_create_date/1000, 'unixepoch')) DESC;";
        cursor = db.rawQuery(query, new String[]{String.valueOf(category.getCategoryID()), String.valueOf(c.getTimeInMillis()), user});

        if (cursor.moveToFirst()) {
            do {
                /* get name */
                c.set(Calendar.MONTH, cursor.getInt(1));
                c.set(Calendar.YEAR, cursor.getInt(0));
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                /* set item */
                CategorySum _categorySum = new CategorySum(String.format("%s %s", cursor.getString(0), month), cursor.getDouble(2));
                _list.add(_categorySum);
                _categorySum = null;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return _list;
    }

    public Double getSum(int Range, String user) {
        return getSum(Range, user, null);
    }

    public List<Cash> getCashs(List<Cash> cashList, String user, Category category) {
        return getCashs(cashList, user, category, "");
    }

    public List<Cash> getCashs(List<Cash> cashList, String user, Category category, String filterTitel) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] tableColumns = new String[] {
                KEY_sID,
                KEY_sCONTENT,
                KEY_sCREATEDATE,
                KEY_sCATEGORY,
                KEY_sREPEAT,
                KEY_sTOTAL,
                KEY_sISCLONED
        };
        String whereClause = String.format("%s = ?",KEY_sCATEGORY) ;

        String[] whereArgs = new String[] {
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
                cashList.add(_cash);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return cashList;
    }
}
