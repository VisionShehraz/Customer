package com.gotaxiride.passenger.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.mMassage.MenuMassageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/22/2018.
 */

public class GetLayananMassageResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<MenuMassageItem> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MenuMassageItem> getData() {
        return data;
    }

    public void setData(List<MenuMassageItem> data) {
        this.data = data;
    }
}
