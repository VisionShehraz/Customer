package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.R;

import java.io.Serializable;

/**
 * Created by haris on 12/1/16.
 */

public class ItemHistory implements Serializable {
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
    public double start_latitude;

    @Expose
    @SerializedName("start_longitude")
    public double start_longitude;

    @Expose
    @SerializedName("end_latitude")
    public double end_latitude;

    @Expose
    @SerializedName("end_longitude")
    public double end_longitude;

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

    @Expose
    @SerializedName("harga")
    public long harga;

    @Expose
    @SerializedName("jarak")
    public double jarak;

    @Expose
    @SerializedName("reg_id")
    public String reg_id;

    @Expose
    @SerializedName("merek")
    public String merek;

    @Expose
    @SerializedName("tipe")
    public String tipe;

    @Expose
    @SerializedName("jenis")
    public String jenis;

    @Expose
    @SerializedName("nomor_kendaraan")
    public String nomor_kendaraan;

    @Expose
    @SerializedName("warna")
    public String warna;


    @Expose
    @SerializedName("image_id")
    public int image_id = R.drawable.ic_mride;


}
