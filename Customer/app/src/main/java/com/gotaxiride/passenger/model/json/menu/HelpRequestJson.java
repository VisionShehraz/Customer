package com.gotaxiride.passenger.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haris on 11/28/16.
 */

public class HelpRequestJson {
    @Expose
    @SerializedName("subjek")
    public String subject;

    @Expose
    @SerializedName("isi_bantuan")
    public String description;

    @Expose
    @SerializedName("id_fitur")
    public String type;

    @Expose
    @SerializedName("id_pelanggan")
    public String id_pelanggan;


}
