package com.gotaxiride.passenger.mService;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
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

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.mMart.PlaceAutocompleteAdapter;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.GetNearServiceRequestJson;
import com.gotaxiride.passenger.model.json.book.GetNearServiceResponseJson;
import com.gotaxiride.passenger.model.json.book.MserviceRequestJson;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gotaxiride.passenger.config.General.BOUNDS;

public class mServiceBook extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "order_fitur";
    public static final String JENIS_KEY = "id_jenis";
    public static final String JENIS_STRING = "string_jenis";
    public static final String HARGA_KEY = "harga";
    public static final String ACTYPE_KEY = "ac_type";
    public static final String ACTYPE_STRING = "ac_string";
    public static final String FARE_KEY = "fare";
    public static final String QUANTITY_KEY = "quantity";
    public static final String RESIDENTIAL_KEY = "residential";
    public static final String PROBLEM_KEY = "problem";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.btn_home)
    ImageView btnHome;
    @BindView(R.id.mService_location)
    AutoCompleteTextView setLocation;
    @BindView(R.id.mService_locbtn)
    Button setLocButton;
    @BindView(R.id.service_paymentgroup)
    RadioGroup paymentGroup;
    @BindView(R.id.service_cash)
    RadioButton serviceCash;
    @BindView(R.id.service_mpay)
    RadioButton serviceMpay;
    @BindView(R.id.mpay_balance)
    TextView textMpay;
    @BindView(R.id.service_pricetag)
    TextView textPrice;
    @BindView(R.id.service_topup)
    Button topupButton;
    @BindView(R.id.order_btn)
    Button orderButton;
    @BindView(R.id.discountText)
    TextView discountText;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private Marker pickUpMarker;
    private boolean isMapReady = false;
    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private Realm realm;
    private Fitur selectedFitur;
    private User userLogin;
    private String tglPelayanan;
    private String jamPelayanan;
    private PlaceAutocompleteAdapter mAdapter;
    private int fiturId;
    private int jenisId;
    private String jenisString;
    private long harga;
    private int acId;
    private String acString;
    private double fare;
    private int quantity;
    private String residential;
    private String problem;
    private int pakaiMpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mservice_book);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        userLogin = GoTaxiApplication.getInstance(this).getLoginUser();

        Intent getDataOrder = getIntent();
        fiturId = getDataOrder.getIntExtra(FITUR_KEY, -1);
        jenisId = getDataOrder.getIntExtra(JENIS_KEY, -2);
        jenisString = getDataOrder.getStringExtra(JENIS_STRING);
        harga = getDataOrder.getLongExtra(HARGA_KEY, 0);
        acId = getDataOrder.getIntExtra(ACTYPE_KEY, -3);
        acString = getDataOrder.getStringExtra(ACTYPE_STRING);
        fare = getDataOrder.getDoubleExtra(FARE_KEY, 5);
        quantity = getDataOrder.getIntExtra(QUANTITY_KEY, 99);
        residential = getDataOrder.getStringExtra(RESIDENTIAL_KEY);
        problem = getDataOrder.getStringExtra(PROBLEM_KEY);

        if (fiturId != -1) {
            selectedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();
        }

        discountText.setText("Diskon " + selectedFitur.getDiskon() + " if using a wallet");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mService_map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        CalculateBiaya();
        updateMap();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(mServiceBook.this, MainActivity.class);
                startActivity(home);
            }
        });

        orderButton.setEnabled(false);

        setLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetLocationClick();
            }
        });

        setupAutocompleteTextView();

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                long biayaTotal = 0;
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.service_mpay:
                        pakaiMpay = 1;
                        biayaTotal = (long) (harga * selectedFitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        textPrice.setText(formattedText);
                        break;
                    case R.id.service_cash:
                        pakaiMpay = 0;
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, General.MONEY +". %s .00", formattedTotal);
                        textPrice.setText(formattedText);
                        break;
                }
            }
        });

        topupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderButtonClick();
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        textMpay.setText(formattedText);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
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

    private void updateMap() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (isMapReady) updateLastLocation(false);
    }

    private void setupAutocompleteTextView() {
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        setLocation.setAdapter(mAdapter);
        setLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) mServiceBook.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(setLocation.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                AutocompletePrediction item = mAdapter.getItem(position);
                getLocationFromPlaceId(item.getPlaceId(), new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place place = places.get(0);
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                            onSetLocationClick();
                        }
                    }
                });

            }
        });
    }

    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
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

    private void onSetLocationClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));
        pickUpLatLang = centerPos;

        fillAddress(setLocation, pickUpLatLang);
        fetchNearDriver(pickUpLatLang.latitude, pickUpLatLang.longitude, jenisId);

        orderButton.setEnabled(true);
        orderButton.setBackgroundResource(R.color.colorPrimary);
    }

    private void fillAddress(final EditText editText, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(mServiceBook.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    mServiceBook.this.runOnUiThread(new Runnable() {
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

    private void fetchNearDriver(double latitude, double longitude, int idService) {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearServiceRequestJson param = new GetNearServiceRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);
            param.setIdService(idService);

            service.getNearService(param).enqueue(new Callback<GetNearServiceResponseJson>() {
                @Override
                public void onResponse(Call<GetNearServiceResponseJson> call, Response<GetNearServiceResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(Call<GetNearServiceResponseJson> call, Throwable t) {

                }
            });
        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearServiceRequestJson param = new GetNearServiceRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());
            param.setIdService(jenisId);

            service.getNearService(param).enqueue(new Callback<GetNearServiceResponseJson>() {
                @Override
                public void onResponse(Call<GetNearServiceResponseJson> call, Response<GetNearServiceResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(Call<GetNearServiceResponseJson> call, Throwable t) {

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

    private void getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        tglPelayanan = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        jamPelayanan = timeFormat.format(calendar.getTime());
    }

    private void CalculateBiaya() {
        long biayaTotal = (long) (harga * fare * quantity);

        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;

        this.harga = biayaTotal;

        if (serviceMpay.isChecked()) {
            biayaTotal = (long) (biayaTotal * selectedFitur.getBiayaAkhir());
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        textPrice.setText(formattedText);

        if (userLogin.getmPaySaldo() < (harga * selectedFitur.getBiayaAkhir())) {
            serviceMpay.setEnabled(false);
            serviceCash.toggle();
        } else {
            serviceMpay.setEnabled(true);
        }
    }

    private void onOrderButtonClick() {
        if (driverAvailable.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(mServiceBook.this)
                    .setMessage("Sorry, no technicians are available")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            dialog.show();
        } else {
            getCurrentDateTime();
            MserviceRequestJson param = new MserviceRequestJson();
            User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
            param.setIdPelanggan(userLogin.getId());
            param.setOrderFitur(String.valueOf(selectedFitur.getIdFitur()));
            param.setAlamatAsal(setLocation.getText().toString());
            param.setHarga(harga);
            param.setStartLatitude(pickUpLatLang.latitude);
            param.setStartLongitude(pickUpLatLang.longitude);
            param.setTglService(tglPelayanan);
            param.setJamService(jamPelayanan);
            param.setJenis(jenisId);
            param.setAcType(acId);
            param.setQuantity(quantity);
            param.setResidentialType(residential);
            param.setProblem(problem);
            param.setPakaiMpay(pakaiMpay);
//            if(serviceMpay.isChecked()){
//                param.setHarga((long)(harga * selectedFitur.getBiayaAkhir()));
//            }

            Intent intent = new Intent(mServiceBook.this, mServiceWaiting.class);
            intent.putExtra(mServiceWaiting.REQUEST_PARAM, param);
            intent.putExtra(mServiceWaiting.DRIVER_LIST, (ArrayList) driverAvailable);
            intent.putExtra(mServiceWaiting.JENIS_STRING, jenisString);
            intent.putExtra(mServiceWaiting.ACTYPE_STRING, acString);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
