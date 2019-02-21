package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by haris on 12/11/16.
 */

public class Banner implements Serializable {
    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("fitur_promosi")
    public String fiturPromosi;

    @Expose
    @SerializedName("foto")
    public String foto;


}
