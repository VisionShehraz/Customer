package com.letsride.passenger.gmap.directions;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    private Bound bounds;
    private String copyrights;
    private List<Leg> legs;
    private List<LatLng> overviewPolyLine;
    private String summary;

    public Route(Context context) {
        legs = new ArrayList<Leg>();
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Bound getBounds() {
        return bounds;
    }

    public void setBounds(Bound bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public void addLeg(Leg leg) {
        this.legs.add(leg);
    }

    public List<LatLng> getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public void setOverviewPolyLine(List<LatLng> overviewPolyLine) {
        this.overviewPolyLine = overviewPolyLine;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
