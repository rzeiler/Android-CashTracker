package com.fmh.app.cashtracker.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ralf on 07.02.18.
 */

public class ListMonthYear {

    public List data = new ArrayList();
    double monthSum = 0.0;
    double yearSum = 0.0;

    public double getMonthSum() {
        return monthSum;
    }

    public void setMonthSum(double monthSum) {
        this.monthSum = monthSum;
    }

    public double getYearSum() {
        return yearSum;
    }

    public void setYearSum(double yearSum) {
        this.yearSum = yearSum;
    }

}
