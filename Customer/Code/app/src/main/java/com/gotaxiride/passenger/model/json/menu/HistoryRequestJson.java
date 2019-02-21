package com.gotaxiride.passenger.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by haris on 11/28/16.
 */

public class HistoryRequestJson {
    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("id_transaksi")
    public String id_transaksi;

    @Expose
    @SerializedName("id_driver")
    public String id_driver;

    @Expose
    @SerializedName("order_fitur")
    public String order_fitur;

    @Expose
    @SerializedName("start_latitude")
    public String start_latitude;

    @Expose
    @SerializedName("start_longitude")
    public String start_longitude;

    @Expose
    @SerializedName("end_latitude")
    public String end_latitude;

    @Expose
    @SerializedName("end_longitude")
    public String end_longitude;

    @Expose
    @SerializedName("waktu_order")
    public String waktu_order;

    @Expose
    @SerializedName("waktu_selesai")
    public String waktu_selesai;

    @Expose
    @SerializedName("alamat_asal")
    public String alamat_asal;

    @Expose
    @SerializedName("alamat_tujuan")
    public String alamat_tujuan;

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("nama_depan_driver")
    public String nama_depan_driver;

    @Expose
    @SerializedName("nama_belakang_driver")
    public String nama_belakang_driver;

    @Expose
    @SerializedName("no_telepon")
    public String no_telepon;

    @Expose
    @SerializedName("foto")
    public String foto;

    @Expose
    @SerializedName("rating")
    public String rating;

}
