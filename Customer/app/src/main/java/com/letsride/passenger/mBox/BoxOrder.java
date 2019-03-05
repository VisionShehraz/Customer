package com.letsride.passenger.mBox;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.letsride.passenger.config.General;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.MapDirectionAPI;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.gmap.directions.Directions;
import com.letsride.passenger.gmap.directions.Route;
import com.letsride.passenger.model.AdditionalMbox;
import com.letsride.passenger.model.Driver;
import com.letsride.passenger.model.Fitur;
import com.letsride.passenger.model.KendaraanAngkut;
import com.letsride.passenger.model.MboxInsurance;
import com.letsride.passenger.model.MboxLocation;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.GetAdditionalMboxResponseJson;
import com.letsride.passenger.model.json.book.GetNearBoxRequestJson;
import com.letsride.passenger.model.json.book.GetNearBoxResponseJson;
import com.letsride.passenger.utils.Log;

import java.io.IOException;
import java.text.DecimalFormat;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.letsride.passenger.mBox.BoxDetailOrder.ASALNAMA_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.ASALPHONE_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.ASAL_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.BARANG_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.CATATAN_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.DESTINATION_LOCATION;
import static com.letsride.passenger.mBox.BoxDetailOrder.ENDLATLONG_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.HARGA_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.JAM_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.JARAK_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.ORIGIN_LOCATION;
import static com.letsride.passenger.mBox.BoxDetailOrder.STRLATLONG_KEY;
import static com.letsride.passenger.mBox.BoxDetailOrder.TANGGAL_KEY;
import static com.letsride.passenger.mBox.MboxWaiting.DRIVER_LIST;

public class BoxOrder extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Validator.ValidationListener {

    public static final String FITUR_KEY = "order_fitur";
    public static final String KENDARAAN_KEY = "id_kendaraan";
    public static final String ADDRESS_KEY = "address";
    public static final String LAT_KEY = "lat";

//    @NotEmpty
//    @BindView(R.id.org_locdetail)
//    EditText originLocDetail;
    public static final String LONG_KEY = "long";
    public static final String POSITION = "position";
    public static final String SHIPPER = "shipper";
    public static final String SHIPPER_PRICE = "shipper_price";
    public static final String INSURANCE_PRICE = "insurance_price";
    public static final String INSURANCE = "insurance";
    public static final String DRIVER = "driver";
    public static final int DESTINATION_LOCATION_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_LOCATION = 991;

//    @BindView(R.id.mbox_picknow)
//    RadioButton PickupNow;
//
//    @BindView(R.id.mbox_picklater)
//    RadioButton PickupLater;
    private static final int ORIGIN_LOCATION_REQUEST_CODE = 0;
    @BindView(R.id.mbox_price)
    LinearLayout priceContainer;
    @BindView(R.id.mbox_pricetag)
    TextView priceTag;
    @BindView(R.id.price_space)
    View priceSpace;
    @NotEmpty
    @BindView(R.id.org_loc)
    EditText originLocation;
    @BindView(R.id.org_contact)
    TextView originContact;
    @BindView(R.id.add_orgcontact)
    LinearLayout addOrgContact;
    @BindView(R.id.org_contactname)
    EditText originName;
    @BindView(R.id.org_contactphone)
    EditText originPhone;
    @BindView(R.id.org_instruction)
    EditText originInstruction;
    @BindView(R.id.mbox_destinasiitem)
    RecyclerView DestinationRecycle;
    @BindView(R.id.mbox_nextdestination)
    Button nextDestination;
    @BindView(R.id.mbox_removeDestination)
    Button removeDestination;
    @NotEmpty
    @BindView(R.id.mbox_items)
    EditText mboxItem;
    @BindView(R.id.pickuptime_container)
    LinearLayout PickuptimeContainer;
    @BindView(R.id.mbox_pickuptime)
    EditText mBoxPickupTime;
    @BindView(R.id.mbox_agreement)
    CheckBox mBoxAgreement;
    @BindView(R.id.mbox_next)
    Button mBoxNext;
    @BindView(R.id.btn_tambah)
    ImageView addShipper;
    @BindView(R.id.btn_kurang)
    ImageView reduceShipper;
    @BindView(R.id.shipper_price)
    TextView shipperPrice;
    @BindView(R.id.note_insurance)
    TextView noteInsurance;
    @BindView(R.id.additional_shipper)
    TextView additionalShipperTextView;
    @BindView(R.id.note_item)
    TextView noteItem;
    @BindView(R.id.mbox_loadservice)
    CheckBox loadingServiceCheckBox;
    @BindView(R.id.insurance_spinner)
    Spinner insuranceSpinner;
    long additionalShipper; // per shipper price
    List<MboxInsurance> mboxInsurances;
    int shipperCount = 0;
    int maxShipper = 2;
    int insurancePrice = 0;
    KendaraanAngkut cargo;
    List<String> insuranceList;
    ArrayAdapter<String> insuranceAdapter;
    MboxLocation mboxLocationOrigin = new MboxLocation();
    ArrayList<MboxLocation> mboxLocationListDest = new ArrayList<>();
    private int fiturId;
    private int idKendaraan;
    private String tglPelayanan;
    private String jamPelayanan;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng originLatLng;
    private LatLng destinationLatLng;
    private Fitur selectedFitur;
    private KendaraanAngkut selectedCargo;
    private Realm realm;
    private Validator validator;
    private ArrayList<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private List<DestinasiItem> nextDestinasiList;
    private FastItemAdapter<DestinasiItem> mboxAdapter;
    private double jarak;
    private long harga;
    private String stringPickupTime;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private ArrayList<Marker> destinationMarker = new ArrayList<Marker>();
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(BoxOrder.this, json);
                if (distance >= 0) {
                    BoxOrder.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDistance(distance);
                            updateLineDestination(json);
                        }
                    });
                }
            }
        }
    };
    private PopupWindow popwindow;
    private String currentDate;
    private String currentMonth;
    private String currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbox_order);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        idKendaraan = intent.getIntExtra(KENDARAAN_KEY, 0);
        fiturId = intent.getIntExtra(FITUR_KEY, -1);
        cargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", idKendaraan).findFirst();

        if (fiturId != -1) {
            selectedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();
        }

        if (idKendaraan != 0) {
            selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", idKendaraan).findFirst();
        }

        originLocation.setFocusable(false);
        mBoxNext.setEnabled(false);

        priceContainer.setVisibility(GONE);

        originLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getLocation = new Intent(BoxOrder.this, PickLocation.class);
                startActivityForResult(getLocation, ORIGIN_LOCATION_REQUEST_CODE);
            }
        });

        DestinationRecycle.setNestedScrollingEnabled(false);

        nextDestinasiList = new ArrayList<>();
        initiateDestination();
        getAdditionalData();


//        nextDestination.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

//        PickupNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    mBoxPickupTime.getText().clear();
//                    getCurrentDateTime();
//                    PickuptimeContainer.setVisibility(GONE);
//                    PickupLater.setChecked(false);
//                    PickupLater.setEnabled(true);
//                }
//            }
//        });
//
//        PickupLater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    TimePickerWindow();
//                    PickupNow.setChecked(false);
//                    PickupLater.setEnabled(true);
//                }
//            }
//        });

        validator = new Validator(this);
        validator.setValidationListener(this);

        mBoxAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBoxNext.setEnabled(true);
                    mBoxNext.setBackgroundResource(R.color.colorPrimary);
                } else {
                    mBoxNext.setEnabled(false);
                    mBoxNext.setBackgroundResource(R.color.colorPrimarySoft);
                }
            }
        });

        mBoxNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        additionalShipperTextView.setText(shipperCount + " Additional Shipper");
        addShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SHIPPER", "ADD");
                if (shipperCount < maxShipper) {
                    shipperCount++;
                    additionalShipperTextView.setText(shipperCount + " Additional Shipper");
                }

            }
        });

        reduceShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shipperCount > 0) {
                    shipperCount--;
                    additionalShipperTextView.setText(shipperCount + " Additional Shipper");
                }
                Log.e("SHIPPER", "REDUCE");
            }
        });

        originContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addOrgContact.getVisibility() == VISIBLE) {
                    addOrgContact.setVisibility(GONE);
                } else {
                    addOrgContact.setVisibility(VISIBLE);
                }
            }
        });


        noteItem.setText("Max size " + cargo.getDimensiKendaraan() + "\n" +
                "Max weight " + cargo.getMaxweightKendaraan());


        addShipper.setVisibility(GONE);
        reduceShipper.setVisibility(GONE);
        additionalShipperTextView.setVisibility(GONE);
//        shipperPrice.setVisibility(GONE);
        loadingServiceCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.e("CHECKBOX", isChecked + "");
                if (isChecked) {
                    addShipper.setVisibility(VISIBLE);
                    reduceShipper.setVisibility(VISIBLE);
                    additionalShipperTextView.setVisibility(VISIBLE);
//                    shipperPrice.setVisibility(VISIBLE);
                    shipperCount = 1;
                } else {
                    addShipper.setVisibility(GONE);
                    reduceShipper.setVisibility(GONE);
                    additionalShipperTextView.setVisibility(GONE);
//                    shipperPrice.setVisibility(GONE);
                    shipperCount = 0;
                }
                additionalShipperTextView.setText(shipperCount + " Additional Shipper");
            }
        });


        insuranceList = new ArrayList<String>();
//        insuranceList.add("Rp 0, Insurance up to Rp 0");

        insuranceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, insuranceList);
        insuranceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        insuranceSpinner.setAdapter(insuranceAdapter);
        insuranceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("INDEX", i + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updatePrice() {

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        gMap.getUiSettings().setZoomControlsEnabled(true);

        updateLastLocation(true);
    }

    private void getAdditionalData() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.getAdditionalMbox().enqueue(new Callback<GetAdditionalMboxResponseJson>() {
            @Override
            public void onResponse(Call<GetAdditionalMboxResponseJson> call, Response<GetAdditionalMboxResponseJson> response) {
                if (response.isSuccessful()) {
                    AdditionalMbox data = response.body().data;
                    additionalShipper = data.additional_shipper;
                    mboxInsurances = data.asuransi;
                    shipperPrice.setText("+"+ General.MONEY +" " + additionalShipper + " per shipper");

                    for (MboxInsurance ins : mboxInsurances) {
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String formattedString = formatter.format(ins.estimasi_biaya);
                        formattedString = formattedString.replaceAll("[$,]", ".");
                        insuranceList.add(General.MONEY +" " + ins.premi + ", Insurance up to "+ General.MONEY  + formattedString + " ");
                    }
                    insuranceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetAdditionalMboxResponseJson> call, Throwable t) {

            }
        });
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
        }
        fetchNearDriver();
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearBoxRequestJson param = new GetNearBoxRequestJson();

            param.latitude = lastKnownLocation.getLatitude();
            param.longitude = lastKnownLocation.getLongitude();
            param.kendaraan_angkut = idKendaraan;

            if (originLatLng != null) {
                param.latitude = originLatLng.latitude;
                param.longitude = originLatLng.longitude;
            }

            service.getNearBox(param).enqueue(new Callback<GetNearBoxResponseJson>() {
                @Override
                public void onResponse(Call<GetNearBoxResponseJson> call, Response<GetNearBoxResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(Call<GetNearBoxResponseJson> call, Throwable t) {

                }
            });
        }
    }

    private void fetchNearDriver(LatLng latLng) {
        if (lastKnownLocation != null) {
            User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearBoxRequestJson param = new GetNearBoxRequestJson();

            param.latitude = latLng.latitude;
            param.longitude = latLng.longitude;
            param.kendaraan_angkut = idKendaraan;

            service.getNearBox(param).enqueue(new Callback<GetNearBoxResponseJson>() {
                @Override
                public void onResponse(Call<GetNearBoxResponseJson> call, Response<GetNearBoxResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(Call<GetNearBoxResponseJson> call, Throwable t) {

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
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mbox_pin)))
                );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("RESULTCODE", requestCode + "");
        if (data == null) {
            return;
        }
        if (data.hasExtra(POSITION)) {
            Log.e("POSITION", data.getIntExtra(POSITION, 0) + "");
        }

        if (requestCode == ORIGIN_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                originLatLng = new LatLng(data.getDoubleExtra(LAT_KEY, 0), data.getDoubleExtra(LONG_KEY, 0));
                mboxLocationOrigin.latitude = data.getDoubleExtra(LAT_KEY, 0);
                mboxLocationOrigin.longitude = data.getDoubleExtra(LONG_KEY, 0);
                mboxLocationOrigin.location = data.getStringExtra(ADDRESS_KEY);

                originLocation.setText(data.getStringExtra(ADDRESS_KEY));

                requestRoute();
            }
        } else if (requestCode == DESTINATION_LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(POSITION, 0);
                destinationLatLng = new LatLng(data.getDoubleExtra(LAT_KEY, 0), data.getDoubleExtra(LONG_KEY, 0));
                MboxLocation mboxLocation = new MboxLocation();
                mboxLocation.latitude = data.getDoubleExtra(LAT_KEY, 0);
                mboxLocation.longitude = data.getDoubleExtra(LONG_KEY, 0);
                mboxLocation.location = data.getStringExtra(ADDRESS_KEY);

                if (mboxLocationListDest.size() <= position) {
                    mboxLocationListDest.add(mboxLocation);
                } else {
//                    mboxLocationListDest.remove(position);
//                    mboxLocationListDest.add(position, mboxLocation);
                    mboxLocationListDest.get(position).latitude = mboxLocation.latitude;
                    mboxLocationListDest.get(position).longitude = mboxLocation.longitude;
                    mboxLocationListDest.get(position).location = mboxLocation.location;
                }

                mboxAdapter.getAdapterItem(position).setLocationData(data);

                requestRoute();
                Log.e("DESTINATION_COUNT", mboxLocationListDest.size() + "");
            }

            /* CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 13);
            gMap.animateCamera(camera); */
        }
    }

    private void onDestinationClick() {
//        if (destinationMarker != null) destinationMarker.remove();
        Marker marker;
        MarkerOptions userIndicator = new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .snippet("Snippet")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_destination));
        marker = gMap.addMarker(userIndicator);
        destinationMarker.add(marker);

        requestRoute();
    }

    private void onPickUpClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(originLatLng)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pickup)));
        fetchNearDriver();
        requestRoute();
    }

    private void requestRoute() {
//        if (originLatLng != null && destinationLatLng != null) {
//            MapDirectionAPI.getDirection(originLatLng, destinationLatLng).enqueue(updateRouteCallback);
//        }
//        destinationMarker.clear();
        gMap.clear();

        if (mboxLocationOrigin.location != null) {
            if (pickUpMarker != null) pickUpMarker.remove();
            pickUpMarker = gMap.addMarker(new MarkerOptions()
                    .position(originLatLng)
                    .title("Pick Up")
                    .snippet(mboxLocationOrigin.location)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_pickup)));
            fetchNearDriver(originLatLng);
        }

        if (!mboxLocationListDest.isEmpty()) {
            for (MboxLocation loc : mboxLocationListDest) {
                Marker marker;
                MarkerOptions userIndicator = new MarkerOptions()
                        .position(new LatLng(loc.latitude, loc.longitude))
                        .title("Destination")
                        .snippet(loc.location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_destination));
                gMap.addMarker(userIndicator);
//                marker = gMap.addMarker(userIndicator);
//                destinationMarker.add(marker);
            }
        }

        if (mboxLocationOrigin.latitude != 0 && mboxLocationOrigin.latitude != 0 && mboxLocationListDest.size() > 0) {
            MapDirectionAPI.getViaDirection(mboxLocationOrigin, mboxLocationListDest).enqueue(updateRouteCallback);

          /*  CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(originLatLng, 12);
            gMap.animateCamera(camera); */
        }

    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(BoxOrder.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(BoxOrder.this, R.color.black))
                        .width(7));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDistance(long distance) {
        priceContainer.setVisibility(VISIBLE);
        priceSpace.setVisibility(VISIBLE);
        float km = ((float) distance) / 1000f;
        this.jarak = km;
        String format = String.format(Locale.US, "(%.1f Km)", km);

        long biayaTotal = (long) ((selectedCargo.getHarga()) + selectedFitur.getBiaya() * km);
        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;
        this.harga = biayaTotal;

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        priceTag.setText(format + " " + formattedText);
    }

    private void getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        tglPelayanan = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        jamPelayanan = timeFormat.format(calendar.getTime());
    }

    @Override
    public void onValidationSucceeded() {

        onNextButtonClick();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initiateDestination() {
        if (nextDestinasiList.isEmpty()) nextDestinasiList.add(new DestinasiItem(this));
        mboxAdapter = new FastItemAdapter<>();

        DestinationRecycle.setLayoutManager(new LinearLayoutManager(this));
//        DestinationRecycle.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        DestinationRecycle.setAdapter(mboxAdapter);
        DestinationRecycle.setNestedScrollingEnabled(false);

        mboxAdapter.setNewList(nextDestinasiList);
//        mboxAdapter.add(new DestinasiItem(this));
        nextDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        removeDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem();
            }
        });
    }

    private void addItem() {
        Log.e("LIST SIZE", nextDestinasiList.size() + "");
        if (nextDestinasiList.size() <= 10) nextDestinasiList.add(new DestinasiItem(this));
        mboxAdapter.setNewList(nextDestinasiList);
//        mboxAdapter.add(nextDestinasiList);
        mboxAdapter.notifyDataSetChanged();
    }

    private void removeItem() {
        if (nextDestinasiList.size() > 1) nextDestinasiList.remove(nextDestinasiList.size() - 1);
        mboxAdapter.setNewList(nextDestinasiList);
        mboxAdapter.notifyDataSetChanged();

        if (mboxLocationListDest.size() > 1)
            mboxLocationListDest.remove(mboxLocationListDest.size() - 1);
        if (destinationMarker.size() > 1) destinationMarker.remove(destinationMarker.size() - 1);
        requestRoute();

    }

    private void onNextButtonClick() {
        mboxLocationOrigin.name = originName.getText().toString();
        mboxLocationOrigin.phone = originPhone.getText().toString();
        mboxLocationOrigin.note = originInstruction.getText().toString();
//        mboxLocationOrigin.locationDetail = originLocDetail.getText().toString();
        mboxLocationOrigin.location = originLocation.getText().toString();

        for (int i = 0; i < mboxLocationListDest.size(); i++) {
            mboxLocationListDest.get(i).name = mboxAdapter.getAdapterItem(i).getNamaReceiver();
            mboxLocationListDest.get(i).phone = mboxAdapter.getAdapterItem(i).getTeleponReceiver();
            mboxLocationListDest.get(i).locationDetail = mboxAdapter.getAdapterItem(i).getTeleponReceiver();
            mboxLocationListDest.get(i).note = mboxAdapter.getAdapterItem(i).getInstruction();

        }

        Intent intent = new Intent(BoxOrder.this, BoxDetailOrder.class);
        intent.putExtra(FITUR_KEY, fiturId);
        intent.putExtra(STRLATLONG_KEY, originLatLng);
        intent.putExtra(ENDLATLONG_KEY, destinationLatLng);
        intent.putExtra(JARAK_KEY, jarak);
        intent.putExtra(HARGA_KEY, harga);
        intent.putExtra(SHIPPER, shipperCount);
        intent.putExtra(SHIPPER_PRICE, additionalShipper);
        intent.putExtra(INSURANCE, mboxInsurances.get(insuranceSpinner.getSelectedItemPosition()).id);
        intent.putExtra(INSURANCE_PRICE, mboxInsurances.get(insuranceSpinner.getSelectedItemPosition()).premi);
        intent.putExtra(ASAL_KEY, originLocation.getText().toString());
//        intent.putExtra(ASALDETAIL_KEY, originLocDetail.getText().toString());
        intent.putExtra(ASALNAMA_KEY, originName.getText().toString());
        intent.putExtra(ASALPHONE_KEY, originPhone.getText().toString());
        intent.putExtra(KENDARAAN_KEY, idKendaraan);
        intent.putExtra(BARANG_KEY, mboxItem.getText().toString());
        intent.putExtra(TANGGAL_KEY, tglPelayanan);
        intent.putExtra(JAM_KEY, jamPelayanan);
        intent.putExtra(CATATAN_KEY, "");
        intent.putExtra(ORIGIN_LOCATION, (Parcelable) mboxLocationOrigin);
        intent.putParcelableArrayListExtra(DESTINATION_LOCATION, mboxLocationListDest);
        intent.putExtra(DRIVER_LIST, (ArrayList) driverAvailable);

        startActivity(intent);
    }

    private void TimePickerWindow() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) BoxOrder.this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.datetime_picker, (ViewGroup) findViewById(R.id.popup_element));

            popwindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popwindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

            final TextView textdate = (TextView) layout.findViewById(R.id.date_text);
            final TextView textmonth = (TextView) layout.findViewById(R.id.month_text);

            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("d");
            currentDate = dateFormat.format(calendar.getTime());
            final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
            currentMonth = monthFormat.format(calendar.getTime());
            final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            currentYear = yearFormat.format(calendar.getTime());

            textdate.setText(currentDate);
            textmonth.setText(currentMonth);

            final SimpleDateFormat bulanJsonFormat = new SimpleDateFormat("MM");
            currentMonth = bulanJsonFormat.format(calendar.getTime());

            final Button btnToday = (Button) layout.findViewById(R.id.date_today);
            final Button btnTomorrow = (Button) layout.findViewById(R.id.date_tomorrow);
            final Button btnCancel = (Button) layout.findViewById(R.id.picker_cancel);
            final Button btnSet = (Button) layout.findViewById(R.id.picker_set);
            final TimePicker tp = (TimePicker) layout.findViewById(R.id.time_picker);
            tp.setIs24HourView(true);

            btnToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentDate = dateFormat.format(calendar.getTime());
                    currentMonth = monthFormat.format(calendar.getTime());
                    currentYear = yearFormat.format(calendar.getTime());
                    textdate.setText(currentDate);
                    textmonth.setText(currentMonth);
                    currentMonth = bulanJsonFormat.format(calendar.getTime());
                    btnTomorrow.setEnabled(true);
                    btnToday.setBackgroundResource(R.color.colorPrimary);
                    btnToday.setTextColor(Color.parseColor("#FFFFFF"));
                    btnTomorrow.setBackgroundResource(R.color.material_light_white);
                    btnTomorrow.setTextColor(Color.parseColor("#FF127399"));
                }
            });

            btnTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    currentDate = dateFormat.format(calendar.getTime());
                    currentMonth = monthFormat.format(calendar.getTime());
                    currentYear = yearFormat.format(calendar.getTime());
                    textdate.setText(currentDate);
                    textmonth.setText(currentMonth);
                    currentMonth = bulanJsonFormat.format(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    btnTomorrow.setEnabled(false);
                    btnTomorrow.setBackgroundResource(R.color.colorPrimary);
                    btnTomorrow.setTextColor(Color.parseColor("#FFFFFF"));
                    btnToday.setBackgroundResource(R.color.material_light_white);
                    btnToday.setTextColor(Color.parseColor("#FF127399"));
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popwindow.dismiss();
//                    PickupLater.setChecked(false);
//                    PickupNow.setChecked(true);
                }
            });

            btnSet.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(23)
                public void onClick(View v) {
                    int hour;
                    int minute;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        hour = tp.getHour();
                        minute = tp.getMinute();
                    } else {
                        hour = tp.getCurrentHour();
                        minute = tp.getCurrentMinute();
                    }
                    stringPickupTime = currentYear + "-" + currentMonth + "-" + currentDate + " " + hour + ":" + minute;
                    Log.d("tp", currentYear + "-" + currentMonth + "-" + currentDate + " " + hour + ":" + minute);

                    tglPelayanan = currentYear + "-" + currentMonth + "-" + currentDate;
                    jamPelayanan = "" + hour + ":" + minute;
                    int selectedHour = tp.getHour() + tp.getMinute();
                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + Calendar.getInstance().get(Calendar.MINUTE);
                    if (selectedHour < currentHour) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BoxOrder.this);
                        builder.setTitle("Warning");
                        builder.setMessage("Pick-up time must be later than the current time");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    } else {
                        mBoxPickupTime.setText(stringPickupTime);
                    }
                    popwindow.dismiss();
//                    PickupLater.setChecked(true);
//                    PickupNow.setEnabled(true);
                    PickuptimeContainer.setVisibility(VISIBLE);
                    mBoxPickupTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TimePickerWindow();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
