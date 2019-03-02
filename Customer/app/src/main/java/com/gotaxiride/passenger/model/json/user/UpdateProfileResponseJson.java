package com.gotaxiride.passenger.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 10/13/2018.
 */

public class UpdateProfileResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<String> data = new ArrayList<>();
}
