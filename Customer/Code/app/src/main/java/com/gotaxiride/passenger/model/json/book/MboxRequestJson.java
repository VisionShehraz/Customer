package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.MboxLocation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Androgo on 12/15/2018.
 */

public class MboxRequestJson implements Serializable {

    @Expose
    @SerializedName("id_pelanggan")
    public String idPelanggan;

    @Expose
    @SerializedName("order_fitur")
    public int orderFitur;

    @Expose
    @SerializedName("start_latitude")
    public double startLatitude;

    @Expose
    @SerializedName("start_longitude")
    public double startLongitude;

    @Expose
    @SerializedName("end_latitude")
    public double endLatitude = 0;

    @Expose
    @SerializedName("end_longitude")
    public double endLongitude = 0;

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
    @SerializedName("kendaraan_angkut")
    public int kendaraanAngkut;

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

    @Expose
    @SerializedName("asuransi")
    public int asuransi;

    @Expose
    @SerializedName("shipper")
    public int shipper;

    @Expose
    @SerializedName("tanggal_pelayanan")
    public String tanggal_pelayanan = "2015-04-02";

    @Expose
    @SerializedName("jam_pelayanan")
    public String jam_pelayanan = "00:00:00";


    @Expose
    @SerializedName("destinasi")
    public List<MboxLocation> destinasi;


//    @Expose
//    @SerializedName("tangal_pelayanan")
//    public String tanggalPelayanan;
//
//    @Expose
//    @SerializedName("jam_pelayanan")
//    public String jamPelayanan;
//
//    @Expose
//    @SerializedName("catatan")
//    public String catatan;

}
