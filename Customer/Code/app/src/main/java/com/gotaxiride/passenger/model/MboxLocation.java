package com.gotaxiride.passenger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by haris on 12/24/16.
 */

public class MboxLocation implements Parcelable, Serializable {

    public static final Creator<MboxLocation> CREATOR = new Creator<MboxLocation>() {
        @Override
        public MboxLocation createFromParcel(Parcel in) {
            return new MboxLocation(in);
        }

        @Override
        public MboxLocation[] newArray(int size) {
            return new MboxLocation[size];
        }
    };
    @Expose
    @SerializedName("lokasi")
    public String location;
    @Expose
    @SerializedName("detail_lokasi")
    public String locationDetail;
    @Expose
    @SerializedName("latitude")
    public double latitude;
    @Expose
    @SerializedName("longitude")
    public double longitude;
    @Expose
    @SerializedName("nama_penerima")
    public String name;
    @Expose
    @SerializedName("telepon_penerima")
    public String phone;

//    @Expose
//    @SerializedName("distance")
//    public long distance;
    @Expose
    @SerializedName("instruksi")
    public String note;

    public MboxLocation() {

    }

    protected MboxLocation(Parcel in) {
        location = in.readString();
        locationDetail = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        phone = in.readString();
        note = in.readString();
//        distance = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location);
        parcel.writeString(locationDetail);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(note);
//        parcel.writeLong(distance);
    }
}
