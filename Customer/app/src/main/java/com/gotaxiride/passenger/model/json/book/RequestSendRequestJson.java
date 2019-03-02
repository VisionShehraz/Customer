package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Androgo on 10/19/2018.
 */

public class RequestSendRequestJson implements Serializable {

    @Expose
    @SerializedName("id_pelanggan")
    public String idPelanggan;

    @Expose
    @SerializedName("order_fitur")
    public String orderFitur;

    @Expose
    @SerializedName("start_latitude")
    public double startLatitude;

    @Expose
    @SerializedName("start_longitude")
    public double startLongitude;

    @Expose
    @SerializedName("end_latitude")
    public double endLatitude;

    @Expose
    @SerializedName("end_longitude")
    public double endLongitude;

    @Expose
    @SerializedName("jarak")
    public double jarak;

    @Expose
    @SerializedName("harga")
    public long harga;

    @Expose
    @SerializedName("alamat_asal")
    public String alamatAsal;

    @Expose
    @SerializedName("alamat_tujuan")
    public String alamatTujuan;

    @Expose
    @SerializedName("pakai_mpay")
    public int pakaiMpay;

    @Expose
    @SerializedName("nama_pengirim")
    public String namaPengirim;

    @Expose
    @SerializedName("telepon_pengirim")
    public String teleponPengirim;

    @Expose
    @SerializedName("nama_penerima")
    public String namaPenerima;

    @Expose
    @SerializedName("telepon_penerima")
    public String teleponPenerima;

    @Expose
    @SerializedName("nama_barang")
    public String namaBarang;


}
