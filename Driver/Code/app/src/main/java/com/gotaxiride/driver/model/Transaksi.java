package com.gotaxiride.driver.model;

import java.io.Serializable;

/**
 * Created by GagahIB on 27/11/2016.
 */
public class Transaksi implements Serializable {

    public String id_transaksi;
    public String id_pelanggan;
    public String reg_id_pelanggan;
    public String order_fitur;
    public String start_latitude;
    public String start_longitude;
    public String end_latitude;
    public String end_longitude;
    public String jarak;
    public String harga;
    public String waktu_order;
    public String waktu_selesai;
    public String alamat_asal;
    public String alamat_tujuan;
    public String rate;
    public String kode_promo;
    public String kredit_promo;
    public String pakai_mpay;
    public String biaya_akhir;
    public String nama_pelanggan;
    public String telepon_pelanggan;

    public String nama_barang;
    public String tanggal_pelayanan;
    public String jam_pelayanan;
    public String lama_pelayanan;
    public String time_accept;


    public int estimasi_biaya;
    public String harga_akhir;
    public String nama_toko;


    public String nama_penerima;
    public String nama_pengirim;
    public String telepon_pengirim;
    public String telepon_penerima;

    public String waktu_pelayanan;
    public String kendaraan_angkut;

    public String quantity;
    public String residential_type;
    public String problem;
    public String jenis_service;
    public String ac_type;

    public int asuransi;
    public int shipper;

    public String kota;
    public String massage_menu;
    public String pelanggan_gender;
    public String prefer_gender;
    public String catatan_tambahan;

    public int total_biaya;
    public String nama_resto;
    public String telepon_resto;


}
