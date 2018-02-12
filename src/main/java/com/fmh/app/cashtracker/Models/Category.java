package com.fmh.app.cashtracker.Models;

import java.io.Serializable;

/**
 * Created by ralf on 01.02.18.
 */

public class Category implements Serializable {

    // Empty constructor
    public Category() {

    }

    // constructor
    public Category(String Title, String User, int Rating) {
        this._Title = Title;
        this._User = User;
        this._Rating = Rating;
    }

    // constructor
    public Category(String Title, long CreateDate, String User, int Rating, double Total) {
        this._Title = Title;
        this._CreateDate = CreateDate;
        this._User = User;
        this._Rating = Rating;
        this._Total = Total;
    }

    // constructor
    public Category(long Id, String Title, long CreateDate, String User, int Rating, double Total, long Count) {
        this._Id = Id;
        this._Title = Title;
        this._CreateDate = CreateDate;
        this._User = User;
        this._Rating = Rating;
        this._Total = Total;
        this._Count = Count;
    }

    public long getCategoryID() {
        return this._Id;
    }

    public void setCategoryID(long id) {
        this._Id = id;
    }

    public String getTitle() {
        return this._Title;
    }

    public void setTitle(String Title) {
        this._Title = Title;
    }

    public long getCreateDate() {
        return this._CreateDate;
    }

    public void setCreateDate(long CreateDate) {
        this._CreateDate = CreateDate;
    }

    public String getUser() {
        return this._User;
    }

    public void setUser(String User) {
        this._User = User;
    }

    public int getRating() {
        return this._Rating;
    }

    public void setRating(int Rating) {
        this._Rating = Rating;
    }

    public double getTotal() {
        return this._Total;
    }

    public void setTotal(double Total) {
        this._Total = Total;
    }

    public long getCount() {
        return _Count;
    }

    public void setCount(long _Count) {
        this._Count = _Count;
    }

    // private variables
    private long _Id = -1;
    private long _Count = 0;
    private String _Title;
    private long _CreateDate;
    private String _User;
    private int _Rating;
    private double _Total;

}
