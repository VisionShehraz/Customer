package com.gotaxiride.passenger.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;

/**
 * Created by Androgo on 12/18/2018.
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }
    public static RealmController with(Fragment fragment) {
        if (instance == null) instance = new RealmController(fragment.getActivity().getApplication());
        return instance;
    }
    public static RealmController with(Activity activity) {
        if (instance == null) instance = new RealmController(activity.getApplication());
        return instance;
    }
    public static RealmController with(Application application) {
        if (instance == null) instance = new RealmController(application);
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }


}
