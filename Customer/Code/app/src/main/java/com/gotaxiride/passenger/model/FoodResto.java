package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 12/31/2018.
 */

public class FoodResto implements Serializable {

    @Expose
    @SerializedName("detail_restoran")
    private List<Restoran> detailRestoran = new ArrayList<>();

    @Expose
    @SerializedName("list_menu_makanan")
    private List<MenuMakanan> menuMakananList = new ArrayList<>();

    @Expose
    @SerializedName("list_makanan")
    private List<Makanan> makananList = new ArrayList<>();

    public List<MenuMakanan> getMenuMakananList() {
        return menuMakananList;
    }

    public void setMenuMakananList(List<MenuMakanan> menuMakananList) {
        this.menuMakananList = menuMakananList;
    }

    public List<Makanan> getMakananList() {
        return makananList;
    }

    public void setMakananList(List<Makanan> makananList) {
        this.makananList = makananList;
    }

    public List<Restoran> getDetailRestoran() {
        return detailRestoran;
    }

    public void setDetailRestoran(List<Restoran> detailRestoran) {
        this.detailRestoran = detailRestoran;
    }
}
