package com.fmh.app.cashtracker;

/**
 * Created by ralf on 05.02.18.
 */

public class CategorySum {

    // private variables
    String _Month;
    double _Total;

    // Empty constructor
    public CategorySum() {

    }

    // constructor
    public CategorySum(String Month, double Total) {
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
