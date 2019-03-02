package com.gotaxiride.passenger.mRideCar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.makeramen.roundedimageview.RoundedImageView;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.FCMHelper;
import com.gotaxiride.passenger.api.MapDirectionAPI;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.gmap.directions.Directions;
import com.gotaxiride.passenger.gmap.directions.Route;
import com.gotaxiride.passenger.home.ChatActivity;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Transaksi;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.fcm.CancelBookRequestJson;
import com.gotaxiride.passenger.model.json.fcm.CancelBookResponseJson;
import com.gotaxiride.passenger.model.json.fcm.DriverRequest;
import com.gotaxiride.passenger.model.json.fcm.DriverResponse;
import com.gotaxiride.passenger.model.json.fcm.FCMMessage;
import com.gotaxiride.passenger.utils.Log;
import com.gotaxiride.passenger.utils.db.DBHandler;
import com.gotaxiride.passenger.utils.db.Queries;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gotaxiride.passenger.config.General.FCM_KEY;
import static com.gotaxiride.passenger.model.FCMType.ORDER;
import static com.gotaxiride.passenger.model.ResponseCode.ACCEPT;
import static com.gotaxiride.passenger.model.ResponseCode.CANCEL;
import static com.gotaxiride.passenger.model.ResponseCode.FINISH;
import static com.gotaxiride.passenger.model.ResponseCode.REJECT;
import static com.gotaxiride.passenger.model.ResponseCode.START;
import static com.gotaxiride.passenger.service.GoTaxiMessagingService.BROADCAST_ORDER;

//import com.gumcode.mangjek.utils.Log;

public class InProgressActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private static final int REQUEST_PERMISSION_CALL = 992;
    @BindView(R.id.rideCar_pickUpText)
    EditText pickUpText;
    @BindView(R.id.rideCar_destinationText)
    EditText destinationText;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.driver_image)
    RoundedImageView driverImage;
    @BindView(R.id.driver_name)
    TextView driverName;
    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.driver_police_number)
    TextView driverPoliceNumber;
    @BindView(R.id.driver_car)
    TextView driverCar;
    @BindView(R.id.driver_arriving_time)
    TextView driverArrivingTime;
    @BindView(R.id.chat_driver)
    ImageView chatDriver;
    @BindView(R.id.call_driver)
    ImageView callDriver;
    @BindView(R.id.cancelBook)
    CardView cancelBook;
    Bundle orderBundle;
    Driver driver;
    DriverRequest request;
    User loginUser;
    Realm realm;
    private GoogleMap gMap;
    private boolean isMapReady = false;
    private Location lastKnownLocation;
    private GoogleApiClient googleApiClient;
    private LatLng pickUpLatLng;
    private LatLng destinationLatLng;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private Context context;
    private boolean isCancelable = true;
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(InProgressActivity.this, json);
                if (distance >= 0) {
                    InProgressActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
//                            updateDistance(distance);
                        }
                    });
                }
            }
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            orderBundle = intent.getExtras();
            orderHandler(orderBundle.getInt("code"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }


        context = getApplicationContext();
        realm = Realm.getDefaultInstance();

//        readTransaction();

        loginUser = GoTaxiApplication.getInstance(InProgressActivity.this).getLoginUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        driver = (Driver) getIntent().getSerializableExtra("driver");
        request = (DriverRequest) getIntent().getSerializableExtra("request");
        Log.e("DATA DRIVER", driver.getNamaDepan() + " " + driver.getNamaBelakang());
        Log.e("DATA REQUEST", request.getAlamatAsal() + " to " + request.getAlamatTujuan());


        pickUpText.setText(request.getAlamatAsal());
        destinationText.setText(request.getAlamatTujuan());
        String format = String.format(Locale.US, "Distance %.2f " + General.UNIT_OF_DISTANCE, request.getJarak());
        distanceText.setText(format);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(request.getHarga());
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        priceText.setText(formattedText);

        Glide.with(getApplicationContext()).load(driver.getFoto()).into(driverImage);
        driverName.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        orderNumber.setText("Order no. " + request.getIdTransaksi());
        driverPoliceNumber.setText(driver.getNomor_kendaraan());
        driverCar.setText(driver.getMerek() + " " + driver.getTipe() + " (" + driver.getWarna() + ")");
        driverArrivingTime.setText("Estimated trip time " + getIntent().getDoubleExtra("time_distance", 0) + " menit");

        pickUpLatLng = new LatLng(request.getStartLatitude(), request.getStartLongitude());
        destinationLatLng = new LatLng(request.getEndLatitude(), request.getEndLongitude());

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Chat with driver", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", driver.getRegId());
                startActivity(intent);
            }
        });

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                alertDialogBuilder.setTitle("Contact Driver");
                alertDialogBuilder.setMessage("Are you sure want to contact the driver? " + driver.getNoTelepon() + "?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(InProgressActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(InProgressActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + driver.getNoTelepon()));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelable) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                    alertDialogBuilder.setTitle("Cancel Order");
                    alertDialogBuilder.setMessage("Are you sure you want to cancel this order?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cancelOrder();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Can not be undone, trip has started!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cancelOrder() {
        User loginUser = GoTaxiApplication.getInstance(InProgressActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = driver.getId();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(InProgressActivity.this, "order canceled!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        finish();
                    } else {
                        Toast.makeText(InProgressActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InProgressActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driver.getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
        requestRoute();

        if (pickUpMarker != null) pickUpMarker.remove();
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(pickUpLatLng)
                .title("Jemput")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));


        if (destinationMarker != null) destinationMarker.remove();
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Tujuan")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_blue)));

    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (pickUpLatLng != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        pickUpLatLng, 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
//            fetchNearDriver();
        }
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

        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestRoute() {
        if (pickUpLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(pickUpLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(InProgressActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(InProgressActivity.this, R.color.directionLine))
                        .width(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ORDER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void orderHandler(int code) {
        switch (code) {
            case REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                isCancelable = false;
                break;
            case ACCEPT:
                Log.e("DRIVER RESPONSE", "accept");
                break;
            case CANCEL:
                Log.e("DRIVER RESPONSE", "cancel");
                finish();
                break;

            case START:
                Log.e("DRIVER RESPONSE", "start");
                isCancelable = false;
                Toast.makeText(getApplicationContext(), "Perjalanan Anda sudah Dimulai!", Toast.LENGTH_SHORT).show();
                break;
            case FINISH:
                Log.e("DRIVER RESPONSE", "finish");
                isCancelable = false;
//                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                Toast.makeText(getApplicationContext(), "Anda sudah sampai di tempat tujuan.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RateDriverActivity.class);
                intent.putExtra("id_transaksi", request.getIdTransaksi());
                intent.putExtra("id_pelanggan", loginUser.getId());
                intent.putExtra("driver_photo", driver.getFoto());
                intent.putExtra("id_driver", driver.getId());
                startActivity(intent);
                finish();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());

    }

    private void readTransaction() {
        RealmResults<Transaksi> results = realm.where(Transaksi.class).findAll();

        Log.e("ALL TRANSACTION", results.toString());
        Log.e("TRANSACTION SIZE", results.size() + "");
        for (int i = 0; i < results.size(); i++) {
            Log.e("TRANSACTION ID", results.get(i).getId() + "");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
