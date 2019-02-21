package com.gotaxiride.driver.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gotaxiride.driver.slidinguppanel.SlidingUpPanelLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.ItemShopping;
import com.gotaxiride.driver.model.DestinationGoBox;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.model.FoodShopping;
import com.gotaxiride.driver.model.Transaksi;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DashboardFragment extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    MainActivity activity;
    ImageView imageBekerja;
    Switch switchBekerja;
    //FrameLayout switchWrapper;
    LinearLayout switchWrapper;
    boolean isOn = false;
    Driver driver;
    int maxRetry = 4;
    int maxRetry1 = 4;
    int maxRetry2 = 4;
    ProgressDialog pd, pd1;
    private View rootView;
    TextView Statuskerja;
    TextView active;
    public boolean statusFragment, ordering = false;
    DrawerLayout drawer;
    private GoogleMap mMap;
    private static final int REQUEST_PERMISSION_LOCATION = 991;

    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;
    private SlidingUpPanelLayout mLayout;
    CircularImageView imageDriver;


    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_main, container, false);
        setHasOptionsMenu(true);

        imageBekerja = (ImageView) rootView.findViewById(R.id.iconBekerja);
        switchBekerja = (Switch) rootView.findViewById(R.id.switch_bekerja);
        switchWrapper = (LinearLayout) rootView.findViewById(R.id.switch_wrapper);
        Statuskerja = (TextView) rootView.findViewById(R.id.Statuskerja);
        active = (TextView) rootView.findViewById(R.id.active);

        // imageDriver = (CircularImageView) rootView.findViewById(R.id.imageDriver);
        // loadImageFromStorage(imageDriver);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.app_name);
        Queries quem = new Queries(new DBHandler(activity));
        driver = quem.getDriver();
        quem.closeDatabase();
        activate();
        pd = showLoading();
        syncronizingAccount();

        // tambahan slide up panel
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //        Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //      Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelHeight(0);
            }
        });


        switchWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Queries que = new Queries(new DBHandler(activity));
                Driver driver = que.getDriver();
                que.closeDatabase();
                if ((driver.deposit < 15)) {
                    showOnline();
                } else if (driver.status == 4) {
                    pd1 = showLoading();
                    turningTheJob(true);
                } else {
                    showWarning();
                }
            }
        });
        return rootView;






    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.mapOrder);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Memulai Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_drivers_available_selected));
        mCurrLocationMarker = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //menghentikan pembaruan lokasi
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 991;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Izin diberikan.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Izin ditolak.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        que.closeDatabase();
    }




    private MaterialDialog showWarning() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Warning")
                .content(R.string.warning_close)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Yes")
                .negativeText("No")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd1 = showLoading();
                turningTheJob(false);
                md.dismiss();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }

    private MaterialDialog showMessage(String title, String message) {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(title)
                .content(message)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.GREEN)
                        .sizeDp(24))
                .positiveText("Oke")
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }



    private MaterialDialog showOnline() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title(R.string.off_work)
                .content(R.string.low_money)
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_money)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText(R.string.Fill_balance)
                .negativeText("Later")
                .cancelable(false)
                .positiveColor(Color.BLUE)
                .negativeColor(Color.RED)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new DepositFragment(), false);
                statusFragment = true;


                md.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });



        return md;
    }

    public void activate() {
        Queries que = new Queries(new DBHandler(activity));
        Driver driver = que.getDriver();
        que.closeDatabase();
        if (driver.deposit < 10){
            turningOff();
        } else{
            turningOn();
        }
        if (driver.status == 4) {
            switchBekerja.setChecked(false);
            switchBekerja.setBackgroundColor(getResources().getColor(R.color.textGrey));
            switchWrapper.setBackgroundColor(getResources().getColor(R.color.textGrey));
            imageBekerja.setImageResource(R.drawable.fitur_dashboard_off);
            Statuskerja.setText(R.string.Statuskerja);
            active.setText("Status : Not active");

        } else {
            switchBekerja.setChecked(true);
            switchBekerja.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            switchWrapper.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            imageBekerja.setImageResource(R.drawable.fitur_dashboard);
            Statuskerja.setText(R.string.Status_aktif);
            active.setText("Status : Active");
        }
    }

    private void turningOff() {
        Queries que = new Queries(new DBHandler(activity));

//        SettingPreference sp = new SettingPreference(activity);
        switchBekerja.setChecked(false);
        switchBekerja.setBackgroundColor(getResources().getColor(R.color.textGrey));
        switchWrapper.setBackgroundColor(getResources().getColor(R.color.textGrey));
        imageBekerja.setImageResource(R.drawable.fitur_dashboard_off);
        Statuskerja.setText(R.string.Statuskerja);
        active.setText("Status : Not active");
//        sp.updateKerja("OFF");
        que.updateStatus(4);
        que.closeDatabase();
    }

    private void turningOn() {
        Queries que = new Queries(new DBHandler(activity));

//        SettingPreference sp = new SettingPreference(activity);
        switchBekerja.setChecked(true);
        switchBekerja.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        switchWrapper.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        imageBekerja.setImageResource(R.drawable.fitur_dashboard);
        Statuskerja.setText(R.string.Status_aktif);
        active.setText("Status : Active");
        que.updateStatus(1);
        que.closeDatabase();

//        sp.updateKerja("ON");
//        Intent service = new Intent(activity, LocationService.class);
//        activity.startService(service);
    }

    private void turningTheJob(final boolean action) {
        JSONObject jTurn = new JSONObject();
        try {
            jTurn.put("is_turn", action);
            jTurn.put("id_driver", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON_turning_on", jTurn.toString());
        HTTPHelper.getInstance(activity).turningOn(jTurn, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                android.util.Log.d("DashboardFragment", "onSuccess: " + obj);
                pd1.dismiss();
                maxRetry1 = 4;
                try {
                    if (obj.getString("message").equals("banned")) {
                        showMessage("Sorry", "Your account is currently suspended, please contact our office immediately!");
                        turningOff();
                    } else if (obj.getString("message").equals("success")) {
                        if (action) {
                            turningOn();
                            showMessage("Your account is active", "Thank you for online, we really miss you for work. Good luck again.");
                        } else {
                            turningOff();
                        }
                    } else {
                        activate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry1 == 0) {
                    showMessage("Sorry", "A network error has occurred, please try again!");
                    pd1.dismiss();
                    maxRetry1 = 4;
                } else {
//                    pd = showLoading();
                    turningTheJob(action);
                    Log.d("try_ke_turn_off", String.valueOf(maxRetry1));
                    maxRetry1--;
                }
            }
        });
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            pd = showLoading();
            syncronizingAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncronizingAccount() {
        //get saldo
        //get status account
        //get get order

        JSONObject jSync = new JSONObject();
        try {
            jSync.put("id", driver.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPHelper.getInstance(activity).syncAccount(jSync, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    if (obj.getString("message").equals("success")) {
                        driver = HTTPHelper.getInstance(activity).parseUserSync(activity, obj.toString());
                        activity.saldo.setText(amountAdapter(driver.deposit));
                        activity.textRating.setText(convertJarak(Double.parseDouble(driver.rating)) + " / 5");
                        Queries que = new Queries(new DBHandler(activity));
                        que.updateDeposit(driver.deposit);
                        que.updateRating(driver.rating);
                        que.updateStatus(driver.status);
                        Transaksi runTrans = HTTPHelper.getInstance(activity).parseTransaksi(activity, obj.toString());
                        if (!runTrans.id_transaksi.equals("0")) {
                            selectRunTrans(runTrans);
                        }
                        que.closeDatabase();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
//                pd.dismiss();
                if (maxRetry == 0) {
                    Toast.makeText(activity, "Problem connection..", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    maxRetry = 4;
                } else {
//                    pd = showLoading();
                    syncronizingAccount();
                    Log.d("try_ke_sync ", String.valueOf(maxRetry));
                    maxRetry--;
                }

            }
        });
    }

    private String convertJarak(Double jarak) {
        int range = (int) (jarak * 10);
        jarak = (double) range / 10;
        return String.valueOf(jarak);
    }

    private String amountAdapter(int amo) {
        return "Wallet : $ " + NumberFormat.getNumberInstance(Locale.US).format(amo)+ ".00";
    }

    private void selectRunTrans(Transaksi runTruns) {
        JSONObject jTrans = new JSONObject();
        try {
            jTrans.put("id_transaksi", runTruns.id_transaksi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (runTruns.order_fitur) {
            case "3": {
                get_data_transaksi_mfood(jTrans, runTruns);
                break;
            }
            case "4": {
                get_data_transaksi_mmart(jTrans, runTruns);
                break;
            }
            case "5": {
                get_data_transaksi_msend(jTrans, runTruns);
                break;
            }
            case "6": {
                get_data_transaksi_mmassage(jTrans, runTruns);
                break;
            }
            case "7": {
                get_data_transaksi_mbox(jTrans, runTruns);
                break;
            }
            case "8": {
                get_data_transaksi_mservice(jTrans, runTruns);
                break;
            }




            default: {
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(runTruns);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                break;
            }
        }
    }

    private void get_data_transaksi_mmart(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMmart(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmart(currTrans, obj);
                ArrayList<ItemShopping> arrBarang = HTTPHelper.parseBarangBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                que.insertBarangBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Problem connection", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mmart", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mmart(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mmart", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mfood(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMfood(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmart(currTrans, obj);
                ArrayList<FoodShopping> arrBarang = HTTPHelper.parseMakananBelanja(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                que.insertMakananBelanja(arrBarang);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Connection problem..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mfood(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mfood", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mbox(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMbox(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMbox(currTrans, obj);
                ArrayList<DestinationGoBox> arrDestinasi = HTTPHelper.parseDestinasiMbox(obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                que.insertDestinasiMbox(arrDestinasi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Connection problem..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mbox", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mbox(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mbox", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_msend(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMsend(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMsend(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "connection is problem..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_msend", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_msend(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_msend", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }

    private void get_data_transaksi_mservice(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMservice(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMservice(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Connection problem..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mservice", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mservice(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mservice", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }


    private void get_data_transaksi_mmassage(final JSONObject jTrans, final Transaksi currTrans) {
        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).getTransaksiMmassage(jTrans, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                Transaksi transaksi = HTTPHelper.parseDataMmassage(currTrans, obj);
                Queries que = new Queries(new DBHandler(activity));
                que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                que.insertInProgressTransaksi(transaksi);
                que.closeDatabase();
                changeFragment(new OrderFragment(), false);
                pd.dismiss();
                maxRetry2 = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry2 == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Problem connection", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mmassage", "Retrieving Data Null");
                    maxRetry2 = 4;
                } else {
                    get_data_transaksi_mmassage(jTrans, currTrans);
                    maxRetry2--;
                    Log.d("Try_ke_data_mmassage", String.valueOf(maxRetry2));
                    pd.dismiss();
                }
            }
        });
    }








    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container_body, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


    private void loadImageFromStorage(CircularImageView civ) {
        if (!driver.image.equals("")) {
            ContextWrapper cw = new ContextWrapper(activity);
            File directory = cw.getDir("fotoDriver", Context.MODE_PRIVATE);
            File f = new File(directory, "profile.jpg");
            Bitmap circleBitmap = decodeFile(f);
            civ.setImageBitmap(circleBitmap);
        }else {
            imageDriver.setImageResource(R.drawable.ic_account);

        }
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
