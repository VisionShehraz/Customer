package com.gotaxiride.passenger.mFood;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.model.Restoran;
import com.gotaxiride.passenger.model.RestoranFoodSearchResult;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.GetFoodRestoRequestJson;
import com.gotaxiride.passenger.model.json.book.GetFoodRestoResponseJson;
import com.gotaxiride.passenger.model.json.book.SearchRestoranFoodRequest;
import com.gotaxiride.passenger.model.json.book.SearchRestoranFoodResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fathony on 23/01/2017.
 */

public class SearchRestoranActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.btn_home)
    ImageView backButton;
    @BindView(R.id.searchRestoran_searchQuery)
    EditText searchQuery;
    @BindView(R.id.searchRestoran_searchResult)
    RecyclerView searchResult;
    @BindView(R.id.searchRestoran_requirement)
    TextView requirement;
    @BindView(R.id.searchRestoran_noResult)
    CardView noResult;
    @BindView(R.id.searchRestoran_progress)
    ProgressBar progress;
    private Realm realm;
    private User loginUser;
    private BookService bookService;
    private Call<SearchRestoranFoodResponse> searchCall;
    private Callback<SearchRestoranFoodResponse> callbackRequest;
    private FastItemAdapter<RestoranFoodSearchResult> searchResultAdapter;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;

    private Dialog progressDialog;

    private Dialog LoadingSpinner(Context mContext) {
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);
        return pd;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restoran);
        ButterKnife.bind(this);


        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        searchResultAdapter = new FastItemAdapter<>();
        progressDialog = LoadingSpinner(this);
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchResult.setAdapter(searchResultAdapter);

        callbackRequest = new Callback<SearchRestoranFoodResponse>() {
            @Override
            public void onResponse(Call<SearchRestoranFoodResponse> call, Response<SearchRestoranFoodResponse> response) {
                if (response.isSuccessful()) {
                    List<RestoranFoodSearchResult> searchResult = response.body().getData();
                    if (searchResult.size() > 0) {
                        searchResultAdapter.clear();
                        searchResultAdapter.set(searchResult);
                        searchResultAdapter.notifyDataSetChanged();
                        showRecycler();
                    } else {
                        showNoResultFoundMessage();
                    }
                } else {
                    onFailure(call, new RuntimeException("Connection failed."));
                }
            }

            @Override
            public void onFailure(Call<SearchRestoranFoodResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    showNoResultFoundMessage();
                    Toast.makeText(SearchRestoranActivity.this, "Check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();
        loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        showInsertTextMessage();

        searchResultAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranFoodSearchResult>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranFoodSearchResult> adapter, final RestoranFoodSearchResult item, int position) {
                progressDialog.show();
                GetFoodRestoRequestJson param = new GetFoodRestoRequestJson();
                param.setIdResto(item.getIdRestoran());
                bookService.getFoodResto(param).enqueue(new Callback<GetFoodRestoResponseJson>() {
                    @Override
                    public void onResponse(Call<GetFoodRestoResponseJson> call, Response<GetFoodRestoResponseJson> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            Restoran restoran = response.body().getFoodResto().getDetailRestoran().get(0);

                            Intent intent = new Intent(SearchRestoranActivity.this, FoodMenuActivity.class);
                            intent.putExtra(FoodMenuActivity.ID_RESTO, restoran.getId());
                            intent.putExtra(FoodMenuActivity.NAMA_RESTO, restoran.getNamaResto());
                            intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, restoran.getAlamat());
                            intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, item.getDistance());
                            intent.putExtra(FoodMenuActivity.JAM_BUKA, restoran.getJamBuka());
                            intent.putExtra(FoodMenuActivity.JAM_TUTUP, restoran.getJamTutup());
                            intent.putExtra(FoodMenuActivity.IS_OPEN, restoran.isOpen());
                            intent.putExtra(FoodMenuActivity.PICTURE_URL, restoran.getFotoResto());
                            intent.putExtra(FoodMenuActivity.IS_MITRA, restoran.isPartner());
                            startActivity(intent);
                        } else {
                            onFailure(call, new RuntimeException("Check internet connection."));
                        }
                    }

                    @Override
                    public void onFailure(Call<GetFoodRestoResponseJson> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(SearchRestoranActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });

        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchCall != null) searchCall.cancel();
                String strings = s.toString();
                if (strings.length() >= 3) {
                    showProgress();
                    SearchRestoranFoodRequest param = new SearchRestoranFoodRequest();
                    param.setLatitude(lastKnownLocation.getLatitude());
                    param.setLongitude(lastKnownLocation.getLongitude());
                    param.setCari(strings);

                    searchCall = bookService.searchRestoranOrFood(param);
                    searchCall.enqueue(callbackRequest);
                } else {
                    showInsertTextMessage();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showRecycler() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showInsertTextMessage() {
        requirement.setVisibility(View.VISIBLE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showNoResultFoundMessage() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
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
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();
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
}
