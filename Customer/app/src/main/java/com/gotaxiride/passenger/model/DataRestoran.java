package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/28/2018.
 */

public class DataRestoran implements Serializable {

    @Expose
    @SerializedName("kategori_restoran")
    private List<KategoriRestoran> kategoriRestoranList = new ArrayList<>();

    @Expose
    @SerializedName("restoran_by_location")
    private List<Restoran> restoranList = new ArrayList<>();

    @Expose
    @SerializedName("promosi_mfood")
    private List<PromosiMFood> promosiMFood = new ArrayList<>();

    public List<KategoriRestoran> getKategoriRestoranList() {
        return kategoriRestoranList;
    }

    public void setKategoriRestoranList(List<KategoriRestoran> kategoriRestoranList) {
        this.kategoriRestoranList = kategoriRestoranList;
    }

    public List<Restoran> getRestoranList() {
        return restoranList;
    }

    public void setRestoranList(List<Restoran> restoranList) {
        this.restoranList = restoranList;
    }

    public List<PromosiMFood> getPromosiMFood() {
        return promosiMFood;
    }

    public void setPromosiMFood(List<PromosiMFood> promosiMFood) {
        this.promosiMFood = promosiMFood;
    }
}
