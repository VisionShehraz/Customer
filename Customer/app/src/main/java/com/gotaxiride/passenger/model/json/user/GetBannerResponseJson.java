package com.gotaxiride.passenger.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.Banner;

import java.util.ArrayList;

/**
 * Created by Androgo on 10/17/2018.
 */

public class GetBannerResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public ArrayList<Banner> data;

}
