package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.FoodResto;

/**
 * Created by Androgo on 12/31/2018.
 */

public class GetFoodRestoResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private FoodResto foodResto;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FoodResto getFoodResto() {
        return foodResto;
    }

    public void setFoodResto(FoodResto foodResto) {
        this.foodResto = foodResto;
    }
}
