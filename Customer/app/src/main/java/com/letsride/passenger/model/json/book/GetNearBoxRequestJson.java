package com.letsride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**** Go - TAXI - On Demand All in One App Services Android
 * Created by Androgo on 10/17/2018.
 */

public class GetNearBoxRequestJson {

    @Expose
    @SerializedName("latitude")
    public double latitude;

    @Expose
    @SerializedName("longitude")
    public double longitude;
    @Expose
    @SerializedName("kendaraan_angkut")
    public int kendaraan_angkut;


}
