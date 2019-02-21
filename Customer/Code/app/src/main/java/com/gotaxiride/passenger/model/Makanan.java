package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Androgo on 12/31/2018.
 */

public class Makanan extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama_menu")
    private String namaMenu;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("kategori_menu_makanan")
    private int kategoriMenuMakanan;

    @Expose
    @SerializedName("deskripsi_menu")
    private String deskripsiMenu;

    @Expose
    @SerializedName("foto")
    private String foto;

    @Expose
    @SerializedName("id_menu_makanan")
    private int idMenuMakanan;

    @Expose
    @SerializedName("menu_makanan")
    private String menuMakanan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public int getKategoriMenuMakanan() {
        return kategoriMenuMakanan;
    }

    public void setKategoriMenuMakanan(int kategoriMenuMakanan) {
        this.kategoriMenuMakanan = kategoriMenuMakanan;
    }

    public String getDeskripsiMenu() {
        return deskripsiMenu;
    }

    public void setDeskripsiMenu(String deskripsiMenu) {
        this.deskripsiMenu = deskripsiMenu;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getIdMenuMakanan() {
        return idMenuMakanan;
    }

    public void setIdMenuMakanan(int idMenuMakanan) {
        this.idMenuMakanan = idMenuMakanan;
    }

    public String getMenuMakanan() {
        return menuMakanan;
    }

    public void setMenuMakanan(String menuMakanan) {
        this.menuMakanan = menuMakanan;
    }
}
