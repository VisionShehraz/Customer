package com.gotaxiride.passenger.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haris on 12/1/16.
 */

public class VersionRequestJson {
    @Expose
    @SerializedName("version")
    public String version;

    @Expose
    @SerializedName("application")
    public String application;

}
