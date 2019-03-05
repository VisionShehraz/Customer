package com.letsride.passenger.mBox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.model.KendaraanAngkut;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.GetKendaraanAngkutResponseJson;
import com.letsride.passenger.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoxActivity extends AppCompatActivity {

//    @BindView(R.cargoId.button_pickup)
//    LinearLayout buttonPickup;
//
//    @BindView(R.cargoId.button_truck)
//    LinearLayout buttonTruck;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";
    @BindView(R.id.cargo_type_recyclerView)
    RecyclerView cargoTypeRecyclerView;
    int featureId;
    FastItemAdapter<CargoItem> itemAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbox);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, -1);

        realm = Realm.getDefaultInstance();
        LoadKendaraan();
        itemAdapter = new FastItemAdapter<>();
        cargoTypeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cargoTypeRecyclerView.setAdapter(itemAdapter);

        itemAdapter.withItemEvent(new ClickEventHook<CargoItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItem.ViewHolder) {
                    return ((CargoItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItem> fastAdapter, CargoItem item) {
                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", itemAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON", "CLICKED, ID : " + selectedCargo.getIdKendaraan());
                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });

//        buttonPickup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 4).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
//
//        buttonTruck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 5).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
    }

    private void LoadKendaraan() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if (response.isSuccessful()) {

                    Realm realm = GoTaxiApplication.getInstance(BoxActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();


                    CargoItem cargoItem;
                    for (KendaraanAngkut cargo : response.body().getData()) {
                        cargoItem = new CargoItem(BoxActivity.this);
                        cargoItem.featureId = featureId;
                        cargoItem.id = cargo.getIdKendaraan();
                        cargoItem.type = cargo.getKendaraanAngkut();
                        cargoItem.price = cargo.getHarga();
                        cargoItem.image = cargo.getFotoKendaraan();
                        cargoItem.dimension = cargo.getDimensiKendaraan();
                        cargoItem.maxWeight = cargo.getMaxweightKendaraan();
                        itemAdapter.add(cargoItem);
                        Log.e("ADD CARGO", cargo.getIdKendaraan() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetKendaraanAngkutResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

   @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

  /*  @Override
    public void onBackPressed(){
        Intent home = new Intent(BoxActivity.this, MainActivity.class);
        startActivity(home);
//        super.onBackPressed();
    }  */
}

