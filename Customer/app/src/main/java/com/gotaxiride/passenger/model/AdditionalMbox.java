package com.gotaxiride.passenger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haris on 12/23/16.
 */

public class AdditionalMbox implements Serializable {
    @Expose
    @SerializedName("additional_shipper")
    public long additional_shipper;

    @Expose
    @SerializedName("asuransi")
    public List<MboxInsurance> asuransi;


}
