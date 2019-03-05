package com.letsride.passenger.mFood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.letsride.passenger.config.General;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.model.PesananFood;
import com.letsride.passenger.model.Restoran;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.GetDataRestoByKategoriRequestJson;
import com.letsride.passenger.model.json.book.GetDataRestoByKategoriResponseJson;
import com.letsride.passenger.utils.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KategoriSelectActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String KATEGORI_ID = "KategoriId";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.btn_home)
    ImageView btnHome;
    @BindView(R.id.food_search)
    LinearLayout foodSearch;
    @BindView(R.id.nearme_recycler)
    RecyclerView nearmeRecycler;
    @BindView(R.id.nearme_noRecord)
    TextView noRecord;
    @BindView(R.id.nearme_progress)
    ProgressBar progress;
    private Realm realm;
    private User loginUser;
    private BookService service;
    private String kategoriId;
    private Location lastKnownLocation;
    private GoogleApiClient googleApiClient;
    private FastItemAdapter<RestoranItem> restoranAdapter;
    private boolean requestUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearme);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        Intent intent = getIntent();
        kategoriId = intent.getStringExtra(KATEGORI_ID);

        showProgress();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();
        //loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());

        loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KategoriSelectActivity.this, SearchRestoranActivity.class);
                KategoriSelectActivity.this.startActivity(intent);
            }
        });

        restoranAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearmeRecycler.setAdapter(restoranAdapter);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItem> adapter, RestoranItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(KategoriSelectActivity.this, FoodMenuActivity.class);
                intent.putExtra(FoodMenuActivity.ID_RESTO, item.id);
                intent.putExtra(FoodMenuActivity.NAMA_RESTO, item.namaResto);
                intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, item.alamat);
                intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, item.distance);
                intent.putExtra(FoodMenuActivity.JAM_BUKA, item.jamBuka);
                intent.putExtra(FoodMenuActivity.JAM_TUTUP, item.jamTutup);
                intent.putExtra(FoodMenuActivity.IS_OPEN, item.isOpen);
                intent.putExtra(FoodMenuActivity.PICTURE_URL, item.pictureUrl);
                intent.putExtra(FoodMenuActivity.IS_MITRA, item.isMitra);
                startActivity(intent);
                return true;
            }
        });

    }

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


    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (requestUpdate) {
            fetchDataAfterLocation();
        }

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

    private void fetchDataAfterLocation() {
        GetDataRestoByKategoriRequestJson param = new GetDataRestoByKategoriRequestJson();
        param.setLatitude(lastKnownLocation.getLatitude());
        param.setLongitude(lastKnownLocation.getLongitude());
        param.setIdKategori(kategoriId);

        service.getDataRestoranByKategori(param).enqueue(new Callback<GetDataRestoByKategoriResponseJson>() {
            @Override
            public void onResponse(Call<GetDataRestoByKategoriResponseJson> call, Response<GetDataRestoByKategoriResponseJson> response) {
                if (response.isSuccessful()) {
                    List<Restoran> restoranList = response.body().getRestoranList();
                    if (restoranList.size() > 0) {
                        updateData(restoranList);
                        showRecycler();
                    } else {
                        showNoRecord();
                    }
                } else {
                    onFailure(call, new RuntimeException("No Internet connection."));
                }
            }

            @Override
            public void onFailure(Call<GetDataRestoByKategoriResponseJson> call, Throwable t) {
                showNoRecord();
            }
        });

    }

    private void updateData(List<Restoran> restoranList) {
        //TODO: Status restoran kerja sama dengan m-food, jam buka - tutup
        List<Restoran> restoranResults = restoranList;
        restoranAdapter.clear();
        RestoranItem restoranItem;
        for (int i = 0; i < restoranResults.size(); i++) {
            restoranItem = new RestoranItem(KategoriSelectActivity.this);
            restoranItem.id = restoranResults.get(i).getId();
            restoranItem.namaResto = restoranResults.get(i).getNamaResto();
            restoranItem.alamat = restoranResults.get(i).getAlamat();
            restoranItem.distance = restoranResults.get(i).getDistance();
            restoranItem.jamBuka = restoranResults.get(i).getJamBuka();
            restoranItem.jamTutup = restoranResults.get(i).getJamTutup();
            restoranItem.fotoResto = restoranResults.get(i).getFotoResto();
            restoranItem.isOpen = restoranResults.get(i).isOpen();
            restoranItem.pictureUrl = restoranResults.get(i).getFotoResto();
            restoranItem.isMitra = restoranResults.get(i).isPartner();
            restoranAdapter.add(restoranItem);
            Log.e("RESTO", restoranItem.namaResto + "");
            Log.e("RESTO", restoranItem.alamat + "");
            Log.e("RESTO", restoranItem.jamBuka + "");
            Log.e("RESTO", restoranItem.jamTutup + "");
            Log.e("RESTO", restoranItem.fotoResto + "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();
    }

    private void showRecycler() {
        nearmeRecycler.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showNoRecord() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestUpdate = false;
    }
}
