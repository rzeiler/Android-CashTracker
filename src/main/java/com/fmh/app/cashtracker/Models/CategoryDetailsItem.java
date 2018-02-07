package com.fmh.app.cashtracker.Models;

/**
 * Created by ralf on 05.02.18.
 */

public class CategoryDetailsItem {

    // private variables
    String _Month;
    double _Total;

    // Empty constructor
    public CategoryDetailsItem() {

    }

    // constructor
    public CategoryDetailsItem(String Month, double Total) {
        this._Month = Month;
        this._Total = Total;
    }


    public double getTotal() {
        return this._Total;
    }

    public void setTotal(double Total) {
        this._Total = Total;
    }

    public String getMonth() {
        return this._Month;
    }

    public void setMonth(String Month) {
        this._Month = Month;
    }

}
