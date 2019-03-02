package com.gotaxiride.passenger.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.ItemHistory;

import java.util.ArrayList;

/**
 * Created by haris on 11/28/16.
 */

public class HistoryResponseJson {
    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public ArrayList<ItemHistory> data = new ArrayList<>();

}
