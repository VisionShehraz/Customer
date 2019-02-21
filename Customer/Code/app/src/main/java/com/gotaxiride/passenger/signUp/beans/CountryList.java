package com.gotaxiride.passenger.signUp.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Androgo on 06 Sep 2018
 */

public class CountryList {

    @SerializedName("employee")
    @Expose
    private ArrayList<Country> employee = null;

    public ArrayList<Country> getEmployee() {
        return employee;
    }

    public void setEmployee(ArrayList<Country> employee) {
        this.employee = employee;
    }

}
