package com.gotaxiride.driver.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Content;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.preference.SettingPreference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationService";
    public String loc;
    LocationService service;


    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }
        service = LocationService.this;

        return START_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    protected void sendLocationToWebService(final Location location) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Queries que = new Queries(new DBHandler(service));
                Driver driver = que.getDriver();
                que.closeDatabase();
                String idT;
                if (driver.id == null)
                    idT = "D0";
                else
                    idT = driver.id;
                try {
                    String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(Double.toString(location.getLatitude()), "UTF-8");
                    data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(Double.toString(location.getLongitude()), "UTF-8");
                    data += "&" + URLEncoder.encode("id_driver", "UTF-8") + "=" + URLEncoder.encode(idT, "UTF-8");

                    String statLoc = HTTPHelper.sendBasicAuthenticationPOSTRequest(MyConfig.UPDATE_LOCATION_URL, data, driver.email, driver.password);
//                    Log.d("Status_from_server ", statLoc+" to "+driver.id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
//                    stopSelf();
                }
                return null;
            }
        }.execute();
    }

    private void announceToUser(String userRegID, Location loc) {

//        Driver driver = new UserPreference(this).getDriver();
//        Content content = new Content();
//        content.addRegId(userRegID);
//        content.createDataLocation(driver.id, loc);
//        sendLocationToPelanggan(content);
    }

    protected void sendLocationToPelanggan(final Content content) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                int status = HTTPHelper.sendToGCMServer(content);
                Log.d("Sending_loc_to", status + "");
                return null;
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
//            if (location.getAccuracy() < 500.0f) {
//                stopLocationUpdates();
//            }

//            String [] sp  = new SettingPreference(this).getSetting();
//            if(sp[2].equals("OFF")){
//                stopLocationUpdates();
//            }else{
//            if(location.getAccuracy() < 200){
            if (new SettingPreference(this).getSetting()[2].equals("")) {
                stopLocationUpdates();
                stopSelf();
            } else {
                sendLocationToWebService(location);
                Queries que = new Queries(new DBHandler(this));
                que.updateLokasi(new String[]{String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())});
                que.closeDatabase();
//                new UserPreference(this).updateLokasi(location.getLatitude(), location.getLongitude());
            }
//            }
//                if(new UserPreference(this).getDriver().status != 1){
//                    Queries que = new Queries(new DBHandler(this));
//                    Transaksi trans = que.getInProgressTransaksi();
//                    announceToUser(trans.id_transaksi, location);
//                    que.closeDatabase();
//                }
//            }
        }
    }

    public void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        stopSelf();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(20000); // milliseconds
        locationRequest.setFastestInterval(20000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

}