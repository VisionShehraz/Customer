package com.gotaxiride.driver.network;

import org.json.JSONObject;


public interface NetworkActionResult {
    public void onSuccess(JSONObject obj);

    public void onFailure(String message);

    public void onError(String message);

}