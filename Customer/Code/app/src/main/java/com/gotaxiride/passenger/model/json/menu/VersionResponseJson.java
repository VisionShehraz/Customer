package com.gotaxiride.passenger.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haris on 12/1/16.
 */

public class VersionResponseJson {
    @Expose
    @SerializedName("new_version")
    public String new_version;

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("data")
    public String[] data;


}
