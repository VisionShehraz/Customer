package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.RestoranFoodSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 23/01/2017.
 */

public class SearchRestoranFoodResponse {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<RestoranFoodSearchResult> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RestoranFoodSearchResult> getData() {
        return data;
    }

    public void setData(List<RestoranFoodSearchResult> data) {
        this.data = data;
    }
}
