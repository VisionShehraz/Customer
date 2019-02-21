package com.gotaxiride.passenger.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.gotaxiride.passenger.model.DataPulsa;


/**
 * Created by Androgo on 08/18/2018.
 */

public class GetDataPulsaResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataPulsa dataPulsa;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataPulsa getDataPulsa() {
        return dataPulsa;
    }

    public void setData(DataPulsa dataPulsa) {
        this.dataPulsa = dataPulsa;
    }

}
