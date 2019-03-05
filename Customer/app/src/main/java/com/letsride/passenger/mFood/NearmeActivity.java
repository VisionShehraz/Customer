package com.letsride.passenger.mFood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letsride.passenger.config.General;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.letsride.passenger.R;
import com.letsride.passenger.model.PesananFood;
import com.letsride.passenger.model.Restoran;
import com.letsride.passenger.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class NearmeActivity extends AppCompatActivity {

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
    private RealmResults<Restoran> restoranRealmResults;
    private FastItemAdapter<RestoranItem> restoranAdapter;

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

        showRecycler();
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearmeActivity.this, SearchRestoranActivity.class);
                NearmeActivity.this.startActivity(intent);
            }
        });

        restoranAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearmeRecycler.setAdapter(restoranAdapter);
        restoranAdapter.withSelectable(true);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItem> adapter, RestoranItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Restoran selectedResto = realm.where(Restoran.class).
                        equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(NearmeActivity.this, FoodMenuActivity.class);
                intent.putExtra(FoodMenuActivity.ID_RESTO, selectedResto.getId());
                intent.putExtra(FoodMenuActivity.NAMA_RESTO, selectedResto.getNamaResto());
                intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, selectedResto.getAlamat());
                intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, selectedResto.getDistance());
                intent.putExtra(FoodMenuActivity.JAM_BUKA, selectedResto.getJamBuka());
                intent.putExtra(FoodMenuActivity.JAM_TUTUP, selectedResto.getJamTutup());
                intent.putExtra(FoodMenuActivity.IS_OPEN, selectedResto.isOpen());
                intent.putExtra(FoodMenuActivity.PICTURE_URL, selectedResto.getFotoResto());
                intent.putExtra(FoodMenuActivity.IS_MITRA, selectedResto.isPartner());
                startActivity(intent);
                return true;
            }
        });

        restoranRealmResults = realm.where(Restoran.class).findAll();
        RestoranItem restoranItem;
        for (int i = 0; i < restoranRealmResults.size(); i++) {
            restoranItem = new RestoranItem(NearmeActivity.this);
           restoranItem.id = restoranRealmResults.get(i).getId();
            restoranItem.namaResto = restoranRealmResults.get(i).getNamaResto();
            restoranItem.alamat = restoranRealmResults.get(i).getAlamat();
            restoranItem.distance = restoranRealmResults.get(i).getDistance();
            restoranItem.jamBuka = restoranRealmResults.get(i).getJamBuka();
            restoranItem.jamTutup = restoranRealmResults.get(i).getJamTutup();
            restoranItem.fotoResto = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isOpen = restoranRealmResults.get(i).isOpen();
            restoranItem.pictureUrl = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isMitra = restoranRealmResults.get(i).isPartner();
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
