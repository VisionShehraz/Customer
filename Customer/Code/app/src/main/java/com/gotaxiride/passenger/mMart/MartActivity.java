package com.gotaxiride.passenger.mMart;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.MapDirectionAPI;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.gmap.directions.Directions;
import com.gotaxiride.passenger.gmap.directions.Route;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.Pesanan;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.GetNearRideCarRequestJson;
import com.gotaxiride.passenger.model.json.book.GetNearRideCarResponseJson;
import com.gotaxiride.passenger.model.json.book.RequestMartRequestJson;
import com.gotaxiride.passenger.splash.SplashActivity;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 12/3/2018.
 */

public class MartActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    public static final int MART_LOCATION = 1;
    public static final int DESTINATION_LOCATION = 2;
    public static final String FITUR_KEY = "FiturKey";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.mart_martName)
    EditText martName;
    @BindView(R.id.mart_martLocation)
    CardView martGetLocationButton;
    @BindView(R.id.mart_martLocationText)
    TextView martGetLocationText;

    @BindView(R.id.mart_plusList)
    TextView productAdd;
    @BindView(R.id.mart_minusList)
    TextView productRemove;
    @BindView(R.id.mart_menuListRecycler)
    RecyclerView productListRecycler;

    @BindView(R.id.mart_destinationButton)
    CardView destinationGetLocationButton;
    @BindView(R.id.mart_destinationText)
    TextView destinationGetLocationText;
    @BindView(R.id.mart_detailsName)
    EditText destinationDetails;

    @BindView(R.id.mart_estimatedCost)
    EditText estimatedCostEdit;

    @BindView(R.id.mart_order)
    Button orderButton;

    @BindView(R.id.mart_distance)
    TextView distanceText;

    @BindView(R.id.mart_price)
    TextView priceText;

    @BindView(R.id.mart_topUp)
    Button topUpButton;

    @BindView(R.id.mart_detailOrder)
    LinearLayout detailOrder;

    @BindView(R.id.mart_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.mart_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.mart_cashPayment)
    RadioButton cashButton;
    @BindView(R.id.mart_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.discountText)
    TextView discountText;


    private List<MartItem> martItemList;
    private FastItemAdapter<MartItem> martAdapter;

    private LatLng martLatLng;
    private LatLng destinationLatLng;

    private Fitur fitur;

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Polyline directionLine;
    private Marker martMarker;
    private Marker destinationMarker;
   // private Realm realm;

    private Location lastKnownLocation;

    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;

    private double jarak;
    private long harga;
    private long saldoMpay;
    private double timeDistance = 0;
//    DiskonMpay diskonMpay;
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(MartActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(MartActivity.this, json);
                if (distance >= 0) {
                    MartActivity.this.runOnUiThread(new Runnable() {
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
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
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

            fetchNearDriver();
        }
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
                updateLastLocation();
            } else {
                // TODO: 10/15/2018 Tell user to use GPS
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mart);
        ButterKnife.bind(this);

        martItemList = new ArrayList<>();
        initializeRecyclerView();
//        diskonMpay = GoTaxiApplication.getInstance(this).getDiskonMpay();

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        martGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMartLocation();
            }
        });

        destinationGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDestinationLocation();
            }
        });
        User userLogin = new User();
        if (GoTaxiApplication.getInstance(this).getLoginUser() != null) {
            userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        } else {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        saldoMpay = userLogin.getmPaySaldo();
        setupGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderClick();
            }
        });

        Intent intent = getIntent();
        int selectedFitur = intent.getIntExtra(FITUR_KEY, -1);
        Realm realm = Realm.getDefaultInstance();

        if (selectedFitur != -1)
            fitur = realm.where(Fitur.class).equalTo("idFitur", selectedFitur).findFirst();

        discountText.setText("Diskon " + fitur.getDiskon() + " if using a wallet");
        cashButton.setChecked(true);

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.mart_mPayPayment:
                        long biayaTotal = (long) (harga * fitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case R.id.mart_cashPayment:
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;

                }
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });
    }

    private void fetchNearDriver() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
        param.setLatitude(lastKnownLocation.getLatitude());
        param.setLongitude(lastKnownLocation.getLongitude());

        service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
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

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);


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
                driverMarkers.add(
                        gMap.addMarker(new MarkerOptions()
                                .position(currentDriverPos)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ride_position)))
                );

            }
        }
    }

    private void orderClick() {
        if (martName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill mart name", Toast.LENGTH_SHORT).show();
        }

        if (martLatLng == null && martGetLocationText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select mart location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (destinationLatLng == null && destinationGetLocationText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select destination location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (estimatedCostEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill the estimated cost", Toast.LENGTH_SHORT).show();
            return;
        }

        List<MartItem> items = martAdapter.getAdapterItems();
        for (MartItem item : items) {
            if (item.getNamaProduk().trim().isEmpty()) {
                Toast.makeText(this, "Please fill the product name", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<Pesanan> pesananList = new ArrayList<>();
        for (MartItem item : items) {
            Pesanan pesanan = new Pesanan();
            pesanan.setNamaBarang(item.getNamaProduk());
            pesanan.setQty(item.getQuantity());
            pesananList.add(pesanan);
        }

        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

        RequestMartRequestJson param = new RequestMartRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur(String.valueOf(fitur.getIdFitur()));
        param.setStartLatitude(destinationLatLng.latitude);
        param.setStartLongitude(destinationLatLng.longitude);
        param.setTokoLatitude(martLatLng.latitude);
        param.setTokoLongitude(martLatLng.longitude);
        param.setAlamatAsal(destinationGetLocationText.getText().toString());
        param.setAlamatToko(martGetLocationText.getText().toString());
        param.setNamaToko(martName.getText().toString());
        param.setJarak(jarak);

        param.setHarga(harga);
        param.setCatatan(destinationDetails.getText().toString());
        param.setEstimasiBiaya(Long.valueOf(estimatedCostEdit.getText().toString()));
        param.setPesanan(pesananList);

        if (mPayButton.isChecked()) {
            param.setHarga((long) (harga * fitur.getBiayaAkhir()));
        }
//        switch (paymentGroup.getCheckedRadioButtonId()) {
//            case R.id.mart_mPayPayment:
//                param.setHarga(harga/2);
//
//                break;
//            case R.id.mart_cashPayment:
//
//                break;
//        }

        if (driverAvailable.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(MartActivity.this)
                    .setMessage("Sorry, driver not available!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            dialog.show();
        } else {
            Intent intent = new Intent(MartActivity.this, MartWaitingActivity.class);
            intent.putExtra(MartWaitingActivity.REQUEST_PARAM, param);
            intent.putExtra(MartWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
            intent.putExtra("time_distance", timeDistance);
            startActivity(intent);
        }

    }

    private void getMartLocation() {
        Intent intent = new Intent(MartActivity.this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, MART_LOCATION);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    private void getDestinationLocation() {
        Intent intent = new Intent(MartActivity.this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, DESTINATION_LOCATION);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationPickerActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                int fillData = data.getIntExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, -1);
                String address = data.getStringExtra(LocationPickerActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(LocationPickerActivity.LOCATION_LATLNG);

                switch (fillData) {
                    case MART_LOCATION:
                        martGetLocationText.setText(address);
                        martLatLng = latLng;

                        if (martMarker != null) martMarker.remove();
                        martMarker = gMap.addMarker(new MarkerOptions()
                                .position(martLatLng)
                                .title("Pick Up")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));
                        fetchNearDriver(martLatLng.latitude, martLatLng.longitude);

                        break;
                    case DESTINATION_LOCATION:
                        destinationGetLocationText.setText(address);
                        destinationLatLng = latLng;

                        if (destinationMarker != null) destinationMarker.remove();
                        destinationMarker = gMap.addMarker(new MarkerOptions()
                                .position(destinationLatLng)
                                .title("Destination")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));

                        break;
                }

                requestRoute();
            }
        }
    }

    private void initializeRecyclerView() {
        if (martItemList.isEmpty()) martItemList.add(new MartItem());

        martAdapter = new FastItemAdapter<>();

        productListRecycler.setLayoutManager(new LinearLayoutManager(this));
        productListRecycler.setAdapter(martAdapter);
        productListRecycler.setNestedScrollingEnabled(false);

        martAdapter.setNewList(martItemList);
        productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        productRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem();
            }
        });
    }

    private void addItem() {
        if (martItemList.size() + 1 <= 20) martItemList.add(new MartItem());
        martAdapter.setNewList(martItemList);
        martAdapter.notifyDataSetChanged();
    }

    private void removeItem() {
        if (martItemList.size() - 1 > 0) martItemList.remove(martItemList.size() - 1);
        martAdapter.setNewList(martItemList);
        martAdapter.notifyDataSetChanged();
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

    private void requestRoute() {
        if (martLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(martLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateDistance(long distance) {
        detailOrder.setVisibility(View.VISIBLE);

        float km = ((float) distance) / General.RANGE_VALUE;

        this.jarak = km;

        String format = String.format(Locale.US, "Distance %.2f " + General.UNIT_OF_DISTANCE, km);
        distanceText.setText(format);

        long biayaTotal = fitur.getBiaya();

        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;

        this.harga = biayaTotal;

        if (mPayButton.isChecked()) {
            biayaTotal = (long) (biayaTotal * fitur.getBiayaAkhir());
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        priceText.setText(formattedText);

        if (saldoMpay < (harga * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(MartActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(MartActivity.this, R.color.black))
                        .width(7));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }   */


}
