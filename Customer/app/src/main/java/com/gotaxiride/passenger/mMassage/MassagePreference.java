package com.gotaxiride.passenger.mMassage;

import java.io.Serializable;

/**
 * Created by Androgo on 12/22/2018.
 */

public class MassagePreference implements Serializable {

    private int idGender;
    private String gender;

    private double durasi;
    private String durasiText;

    private int idTherapist;
    private String therapist;

    public int getIdGender() {
        return idGender;
    }

    public void setIdGender(int idGender) {
        this.idGender = idGender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getDurasi() {
        return durasi;
    }

    public void setDurasi(double durasi) {
        this.durasi = durasi;
    }

    public String getDurasiText() {
        return durasiText;
    }

    public void setDurasiText(String durasiText) {
        this.durasiText = durasiText;
    }

    public int getIdTherapist() {
        return idTherapist;
    }

    public void setIdTherapist(int idTherapist) {
        this.idTherapist = idTherapist;
    }

    public String getTherapist() {
        return therapist;
    }

    public void setTherapist(String therapist) {
        this.therapist = therapist;
    }
}
