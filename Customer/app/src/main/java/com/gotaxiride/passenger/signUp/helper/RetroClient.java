package com.gotaxiride.passenger.signUp.helper;
import com.gotaxiride.passenger.api.ApiCountryCode;
import com.gotaxiride.passenger.api.ServiceGenerator;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Androgo on 06 Sep 2018
 */

public class RetroClient {

    /********
     * URLS
     *******/
    private static final String ROOT_URL = ServiceGenerator.API_BASE_URL;

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiCountryCode getApiService() {
        return getRetrofitInstance().create(ApiCountryCode.class);
    }
}