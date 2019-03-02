package com.gotaxiride.passenger.api;

import com.gotaxiride.passenger.signUp.beans.CountryList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Androgo on 06 Sep 2018
 */

public interface ApiCountryCode {


    @GET("apicountry/list_country.json")
    Call<CountryList> getMyJSON();
}