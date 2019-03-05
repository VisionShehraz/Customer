package com.letsride.passenger.mFood;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.config.General;
import com.letsride.passenger.mMart.PlaceAutocompleteAdapter;
import com.letsride.passenger.model.Driver;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.GetNearRideCarRequestJson;
import com.letsride.passenger.model.json.book.GetNearRideCarResponseJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.letsride.passenger.config.General.BOUNDS;

/**
 * Created by Androgo on 12/4/2018.
 */

public class LocationPickerActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int LOCATION_PICKER_ID = 78;
    public static final String FORM_VIEW_INDICATOR = "FormToFill";
    public static final String LOCATION_NAME = "LocationName";
    public static final String LOCATION_LATLNG = "LocationLatLng";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.locationPicker_autoCompleteText)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.locationPicker_currentAddress)
    TextView currentAddress;
    @BindView(R.id.locationPicker_destinationButton)
    Button selectLocation;


    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;

    private PlaceAutocompleteAdapter mAdapter;
    private BookService service;
    private List<Marker> driverMarkers;


    //new
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.relative_load)
    RelativeLayout relative_load;


//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private int formToFill;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        setupGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationPicker_maps);
        mapFragment.getMapAsync(this);

        setupAutocompleteTextView();

        Intent intent = getIntent();
        formToFill = intent.getIntExtra(FORM_VIEW_INDICATOR, -1);

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });

        driverMarkers = new ArrayList<>();

//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("TAG", "Place: " + place.getName());
//                CameraPosition cameraPosition = new CameraPosition.Builder().
//                        target(place.getLatLng()).
//                        tilt(45).
//                        zoom(15).
//                        bearing(0).
//                        build();
//                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });
    }

    private void selectLocation() {
        LatLng selectedLocation = gMap.getCameraPosition().target;
        String selectedAddress = currentAddress.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(FORM_VIEW_INDICATOR, formToFill);
        intent.putExtra(LOCATION_NAME, selectedAddress);
        intent.putExtra(LOCATION_LATLNG, selectedLocation);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
    }

    private void setupAutocompleteTextView() {
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        autoCompleteTextView.setAdapter(mAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) LocationPickerActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(places.get(0).getLatLng()));
                        }
                    }
                });

            }
        });
    }

    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }


    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
            );

            gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            fetchNearDriver(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            show();

        }
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (service == null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
            service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        }

        GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
        param.setLatitude(latitude);
        param.setLongitude(longitude);

        service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    List<Driver> driverList = response.body().getData();
                    if (!driverList.isEmpty()) {
                        for (Marker m : driverMarkers) {
                            m.remove();
                        }
                        driverMarkers.clear();
                        for (Driver driver : driverList) {
                            LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());

                            driverMarkers.add(
                                    gMap.addMarker(new MarkerOptions()
                                            .position(currentDriverPos)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ride_position)))
                            );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
        setupMapOnCameraChange();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2018 Tell user to use GPS
            }
        }
    }

    private void setupMapOnCameraChange() {
        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = gMap.getCameraPosition().target;
                fillAddress(currentAddress, center);
                show();
            }
        });
    }

    private void fillAddress(final TextView textView, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(LocationPickerActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    LocationPickerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    textView.setText(address);
                                    hidden();
                                }
                            } else {
                                textView.setText(R.string.text_addressNotAvailable);
                                show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private void show (){
        loading.setVisibility(View.VISIBLE);
        relative_load.setVisibility(View.GONE);
        selectLocation.setVisibility(View.GONE);
    }

    private void hidden (){
        loading.setVisibility(View.GONE);
        relative_load.setVisibility(View.VISIBLE);
        selectLocation.setVisibility(View.VISIBLE);
    }


}
