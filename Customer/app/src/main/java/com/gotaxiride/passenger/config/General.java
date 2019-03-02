package com.gotaxiride.passenger.config;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.gotaxiride.passenger.R;


public class General {
    //please insert Server_KEY CLOUD_MESSAGING
    public static final String FCM_KEY = "Server_KEY CLOUD_MESSAGING";
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-33.8688197, 0),
            new LatLng(0, 151.20929550000005));


    // Currency settings
    public static final String MONEY = "$";

    //number sos
    public static final String NUMBER_SOS = "999";

    //if you use RTL Language e.g : Arabic Language or other, set true
    public static final boolean ENABLE_RTL_MODE = false;

    // if you use distance in KM then
    public static final String UNIT_OF_DISTANCE = "Km"; //if you use km or miles
    public static final Float RANGE_VALUE = 1000f; //if using km (1000f) or Miles using 1609f

    //Setting menu names on Home
    public static final String Name_GOCAB = "GO-CAB";
    public static final String Name_GOMOTO = "GO-MOTO";
    public static final String Name_GOSEND = "GO-SEND";
    public static final String Name_GOFOOD = "GO-FOOD";
    public static final String Name_GOMART = "GO-MART";
    public static final String Name_GOMASSAGE = "GO-MASSAGE";
    public static final String Name_GOBOX = "GO-BOX";
    public static final String Name_GOSERVICE = "GO-SERVICE";


}
