package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by haris on 11/30/16.
 */

public class History extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("date")
    @Expose
    public String date;


}
