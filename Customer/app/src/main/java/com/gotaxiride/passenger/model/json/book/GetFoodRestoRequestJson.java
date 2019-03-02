package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Androgo on 09/31/2018.
 */

public class GetFoodRestoRequestJson {

    @Expose
    @SerializedName("id_resto")
    private int idResto;

    public int getIdResto() {
        return idResto;
    }

    public void setIdResto(int idResto) {
        this.idResto = idResto;
    }
}
