package com.gotaxiride.passenger.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.gotaxiride.passenger.model.FirebaseToken;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Androgo on 10/13/2018.
 */

public class GoTaxiInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        saveToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void saveToken(String tokenId) {
        FirebaseToken token = new FirebaseToken(tokenId);
        EventBus.getDefault().postSticky(token);
    }

}
