package com.gotaxiride.passenger.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 10/13/2018.
 */

public class UpdateProfileRequestJson {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("nama_depan")
    @Expose
    public String nama_depan;

    @SerializedName("nama_belakang")
    @Expose
    public String nama_belakang;

    @SerializedName("no_telepon")
    @Expose
    public String no_telepon;

    @SerializedName("alamat")
    @Expose
    public String alamat;

    @SerializedName("tgl_lahir")
    @Expose
    public String tgl_lahir;

    @SerializedName("tempat_lahir")
    @Expose
    public String tempat_lahir;

}
