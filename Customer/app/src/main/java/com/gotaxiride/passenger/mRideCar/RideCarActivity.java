package com.gotaxiride.passenger.mRideCar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.MapDirectionAPI;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.gmap.directions.Directions;
import com.gotaxiride.passenger.gmap.directions.Route;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.mMart.PlaceAutocompleteAdapter;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.GetNearRideCarRequestJson;
import com.gotaxiride.passenger.model.json.book.GetNearRideCarResponseJson;
import com.gotaxiride.passenger.model.json.book.RequestRideCarRequestJson;
import com.gotaxiride.passenger.utils.Log;
import com.gotaxiride.passenger.utils.Tools;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gotaxiride.passenger.config.General.BOUNDS;

/**
 * Created by Androgo on 10/26/2018.
 */

public class RideCarActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "FiturKey";
    private static final String TAG = "RideCarActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.rideCar_title)
    TextView title;
    @BindView(R.id.btn_logo)
    ImageView logo;
    @BindView(R.id.rideCar_pickUpContainer)
    LinearLayout setPickUpContainer;
    @BindView(R.id.rideCar_destinationContainer)
    LinearLayout setDestinationContainer;
    @BindView(R.id.rideCar_pickUpButton)
    ImageView setPickUpButton;
    @BindView(R.id.rideCar_destinationButton)
    ImageView setDestinationButton;
    @BindView(R.id.rideCar_pickUpText)
    AutoCompleteTextView pickUpText;
    @BindView(R.id.rideCar_destinationText)
    AutoCompleteTextView destinationText;
    @BindView(R.id.rideCar_detail)
    LinearLayout detail;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.rideCar_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.rideCar_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.rideCar_cashPayment)
    RadioButton cashButton;
    @BindView(R.id.rideCar_topUp)
    Button topUpButton;
    @BindView(R.id.rideCar_order)
    Button orderButton;
    @BindView(R.id.rideCar_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.rideCar_select_car_container)
    RelativeLayout carSelectContainer;
    @BindView(R.id.rideCar_select_car)
    ImageView carSelect;
    @BindView(R.id.rideCar_select_car_text)
    TextView carSelectText;
    @BindView(R.id.rideCar_select_ride_container)
    RelativeLayout rideSelectContainer;
    @BindView(R.id.rideCar_select_ride)
    ImageView rideSelect;
    @BindView(R.id.ride_car_select_ride_text)
    TextView rideSelectText;
    @BindView(R.id.discountText)
    TextView discountText;

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private Realm realm;
    private Fitur designedFitur;
    private double jarak;
    private double timeDistance = 0;
   // private long harga;
   private double harga;
    private long saldoMpay;
    private boolean isMapReady = false;
    //    private long minPrice = 0 ;
    private PlaceAutocompleteAdapter mAdapter;
    private Toolbar toolbar;
    private ActionBar actionBar;




    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(RideCarActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(RideCarActivity.this, json);
                if (distance >= 0) {
                    RideCarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time / 60;
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_car);
        ButterKnife.bind(this);


        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        initToolbar();
//        diskonMpay = GoTaxiApplication.getInstance(this).getDiskonMpay();
        setPickUpContainer.setVisibility(View.GONE);
        setDestinationContainer.setVisibility(View.GONE);
        detail.setVisibility(View.GONE);

        User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        saldoMpay = userLogin.getmPaySaldo();

        pickUpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setPickUpContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        destinationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setDestinationContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        setPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpClick();


            }
        });

        setDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestinationClick();

            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        int fiturId = intent.getIntExtra(FITUR_KEY, -1);

        Log.e("FITUR_ID", fiturId + "");
        if (fiturId != -1)
            designedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();

        RealmResults<Fitur> fiturs = realm.where(Fitur.class).findAll();

        for (Fitur fitur : fiturs) {
            Log.e("ID_FITUR", fitur.getIdFitur() + " " + fitur.getFitur() + " " + fitur.getBiayaAkhir());
        }

        setupFitur();

        discountText.setText("Discount " + designedFitur.getDiskon() + " if using a wallet");
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderButtonClick();
            }
        });

        carSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCar();
            }
        });

        rideSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRide();
            }
        });

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.rideCar_mPayPayment:
                        double biayaTotal = (long)(harga*designedFitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case R.id.rideCar_cashPayment:
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;

                }
            }
        });

        setupAutocompleteTextView();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //ToolsTaxi.setCompleteSystemBarLight(this);
        Tools.setSystemBarColor(this, R.color.black);
    }

    private void setupAutocompleteTextView() {
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        pickUpText.setAdapter(mAdapter);
        pickUpText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(pickUpText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);

//                LatLng latLng = getLocationFromAddress(item.getFullText(null).toString());
//                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                onPickUpClick();

                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onPickUpClick();
                        }
                    }
                });

            }
        });
        destinationText.setAdapter(mAdapter);
        destinationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(destinationText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
//                LatLng latLng = getLocationFromAddress(item.getFullText(null).toString());
//                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                onDestinationClick();
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        final Place place = places.get(0);
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onDestinationClick();
                        }
                    }
                });
            }
        });

    }

    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }


    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalanceText.setText(formattedText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {
                // TODO: 10/15/2018 Tell user to use GPS
            }
        }
    }

    private void cancelOrder() {

    }


    private void selectCar() {
        carSelect.setSelected(true);
        carSelectText.setSelected(true);
        rideSelect.setSelected(false);
        rideSelectText.setSelected(false);

        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();

//        minPrice = designedFitur.getBiaya_minimum();
        updateFitur();
    }

    private void selectRide() {
        rideSelect.setSelected(true);
        rideSelectText.setSelected(true);
        carSelect.setSelected(false);
        carSelectText.setSelected(false);

        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
//        minPrice = designedFitur.getBiaya_minimum();

        updateFitur();
    }

    private void updateFitur() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (designedFitur.getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
            logo.setImageResource(R.drawable.ride);
        } else if (designedFitur.getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
            logo.setImageResource(R.drawable.car);
        }

        if (isMapReady) updateLastLocation(false);
    }

    private void setupFitur() {
        if (designedFitur.getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
            logo.setImageResource(R.drawable.ride);
            selectRide();
        } else if (designedFitur.getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
            logo.setImageResource(R.drawable.car);
            selectCar();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        isMapReady = true;

        updateLastLocation(true);
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
            fetchNearDriver();
        }
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);

            if (designedFitur.getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (designedFitur.getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            if (designedFitur.getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (designedFitur.getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void createMarker() {
        if (!driverAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driverAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                if (designedFitur.getIdFitur() == 1) {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ride_position)))
                    );
                } else {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_position)))
                    );
                }
            }
        }
    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.rideCar_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Sorry, driver not available")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga((long) (this.harga * designedFitur.getBiayaAkhir()));
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());

                    Log.e("M-PAY", "used");
                    param.setPakaiMpay(1);


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }


                break;
            case R.id.rideCar_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Sorry, driver not available!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(designedFitur.getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga(this.harga);
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());


                    Log.e("M-PAY", "not using m pay");


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
        }
    }

    private void onDestinationClick() {
        if (destinationMarker != null) destinationMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_destination)));
        destinationLatLang = centerPos;

        fillAddress(destinationText, destinationLatLang);

        requestRoute();
    }

    private void onPickUpClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pickup)));
        pickUpLatLang = centerPos;

        fillAddress(pickUpText, pickUpLatLang);

        destinationText.requestFocus();
        fetchNearDriver(pickUpLatLang.latitude, pickUpLatLang.longitude);
        requestRoute();
    }

    private void requestRoute() {
        if (pickUpLatLang != null && destinationLatLang != null) {
            MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang).enqueue(updateRouteCallback);
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(destinationLatLang, 13);
            gMap.animateCamera(camera);
        }
    }

    private void fillAddress(final EditText editText, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(RideCarActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    RideCarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    editText.setText(address);
                                }
                            } else {
                                editText.setText(R.string.text_addressNotAvailable);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(RideCarActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(RideCarActivity.this, R.color.black))
                        .width(7));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDistance(long distance) {
        detail.setVisibility(View.VISIBLE);

        float km = ((float) distance) / General.RANGE_VALUE;
        this.jarak = km;


        String format = String.format(Locale.US, "Distance %.2f " + General.UNIT_OF_DISTANCE, km);
        distanceText.setText(format);

        double biayaTotal = (double) (designedFitur.getBiaya() * km);


        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;

        this.harga = biayaTotal;
        if(biayaTotal < designedFitur.getBiaya_minimum()){
            this.harga = designedFitur.getBiaya_minimum();
            biayaTotal = designedFitur.getBiaya_minimum();
        }

        if(mPayButton.isChecked()){
            Log.e("MPAY","total :"+biayaTotal+" kali "+designedFitur.getBiayaAkhir());
            biayaTotal *= designedFitur.getBiayaAkhir();
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String noCent = String.format(Locale.US, General.MONEY + " %s.00", formattedTotal);
        priceText.setText(noCent);

       /* String priceCent = String.format(Locale.US, General.MONEY +" %.2f", biayaTotal);
       priceText.setText(noCent);

        if (General.Cent) {
            priceText.setText(priceCent);
        } else {
            priceText.setText(noCent);
        } */


        if(saldoMpay < (harga*designedFitur.getBiayaAkhir())){
            mPayButton.setEnabled(false);
            cashButton.toggle();
        }else {
            mPayButton.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
