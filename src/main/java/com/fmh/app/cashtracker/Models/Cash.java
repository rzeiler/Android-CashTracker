package com.fmh.app.cashtracker.Models;

import java.io.Serializable;

/**
 * Created by ralf on 01.02.18.
 */

public class Cash implements Serializable {

    long _Id = -1;
    String _Content;
    long _CreateDate;
    long _Category;
    int _Repeat;
    int _IsCloned = 0;
    double _Total;

    // Empty constructor
    public Cash() {

    }

    // constructor
    public Cash(String Title, long CreateDate, int Category, int Repeat, double Total, int IsCloned) {
        this._Content = Title;
        this._CreateDate = CreateDate;
        this._Category = Category;
        this._Repeat = Repeat;
        this._Total = Total;
        this._IsCloned = IsCloned;
    }

    // constructor
    public Cash(long Id, String Title, long CreateDate, int Category, int Rating, double Total, int IsCloned) {
        this._Id = Id;
        this._Content = Title;
        this._CreateDate = CreateDate;
        this._Category = Category;
        this._Repeat = Rating;
        this._Total = Total;
        this._IsCloned = IsCloned;
    }


    public long getCashID() {
        return this._Id;
    }

    public void setCashID(int id) {
        this._Id = id;
    }

    public String getContent() {
        return this._Content;
    }

    public void setContent(String Title) {
        this._Content = Title;
    }

    public long getCreateDate() {
        return this._CreateDate;
    }

    public void setCreateDate(long CreateDate) {
        this._CreateDate = CreateDate;
    }

    public long getCategory() {
        return this._Category;
    }

    public void setCategory(long Category) {
        this._Category = Category;
    }

    public int getRepeat() {
        return this._Repeat;
    }

    public void setRepeat(int Rating) {
        this._Repeat = Rating;
    }

    public double getTotal() {
        return this._Total;
    }

    public void setTotal(double Total) {
        this._Total = Total;
    }

    public int getIsCloned() {
        return this._IsCloned;
    }

    public void setIsCloned(int IsCloned) {
        this._IsCloned = IsCloned;
    }

}
