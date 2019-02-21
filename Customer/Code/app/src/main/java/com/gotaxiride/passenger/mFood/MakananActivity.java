package com.gotaxiride.passenger.mFood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.model.Makanan;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.utils.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MakananActivity extends AppCompatActivity implements MakananItem.OnCalculatePrice {

    public static final String ID_MENU = "idMenu";
    public static final String NAMA_RESTO = "namaResto";
    public static final String DISTANCE_RESTO = "distanceResto";
    public static final String LATITUDE = "maps_latitude";
    public static final String LONGITUDE = "maps_longitude";

    @BindView(R.id.btn_home)
    ImageView btnHome;
    @BindView(R.id.menu_title)
    TextView menuTitle;
    @BindView(R.id.makanan_recycler)
    RecyclerView makananRecycler;
    @BindView(R.id.price_container)
    RelativeLayout priceContainer;
    @BindView(R.id.qty_text)
    TextView qtyText;
    @BindView(R.id.cost_text)
    TextView costText;
    private int idMenu;
    private String namaMenu;
    private double distanceResto;
    private double maps_latitude;
    private double maps_longitude;

    private FastItemAdapter<MakananItem> makananAdapter;
    private RealmResults<Makanan> makananRealmResults;
    private RealmResults<PesananFood> existingFood;
    private MakananItem makananItem;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makanan);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        Intent intent = getIntent();
        idMenu = intent.getIntExtra(ID_MENU, -3);
        namaMenu = intent.getStringExtra(NAMA_RESTO);
        distanceResto  = intent.getDoubleExtra(DISTANCE_RESTO, -8);
        maps_latitude = intent.getDoubleExtra(LATITUDE, 0);
        maps_longitude = intent.getDoubleExtra(LONGITUDE, 0);

        realm = Realm.getDefaultInstance();

        makananAdapter = new FastItemAdapter<>();


        makananRecycler.setLayoutManager(new LinearLayoutManager(this));
        makananRecycler.setAdapter(makananAdapter);


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        menuTitle.setText(namaMenu);

        priceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakananActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadMakanan();
        calculatePrice();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void LoadMakanan() {
        makananAdapter.clear();

        makananRealmResults = realm.where(Makanan.class).
                equalTo("kategoriMenuMakanan", idMenu).findAll();
        existingFood = realm.where(PesananFood.class).findAll();

        for (int e = 0; e < existingFood.size(); e++) {
            existingFood.get(e).getIdMakanan();
            existingFood.get(e).getQty();
            Log.d("db id", existingFood.get(e).getIdMakanan() + " ");
            Log.d("db qty", existingFood.get(e).getQty() + " ");
        }

        int[] exiF = new int[makananRealmResults.size()];

        for (int i = 0; i < makananRealmResults.size(); i++) {
            makananItem = new MakananItem(MakananActivity.this, this);

            makananItem.quantity = 0;
            for (int j = 0; j < existingFood.size(); j++) {
                if (existingFood.get(j).getIdMakanan() == makananRealmResults.get(i).getId()) {
                    makananItem.quantity = existingFood.get(j).getQty();
                    makananItem.catatan = existingFood.get(j).getCatatan();
                    exiF[i] = existingFood.get(j).getQty();
                    Log.d("isi_exist_quantity", existingFood.get(j).getQty() + " list" + i);
                }
            }

            makananItem.id = makananRealmResults.get(i).getId();
            makananItem.namaMenu = makananRealmResults.get(i).getNamaMenu();
            makananItem.deskripsiMenu = makananRealmResults.get(i).getDeskripsiMenu();
            makananItem.harga = makananRealmResults.get(i).getHarga();
            makananItem.foto = makananRealmResults.get(i).getFoto();
            makananAdapter.add(makananItem);
        }

        for (int x = 0; x < exiF.length; x++) {
            Log.d("isi_x", exiF[x] + "");
        }

        makananAdapter.notifyDataSetChanged();
    }

    @Override
    public void calculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if (quantity > 0)
            priceContainer.setVisibility(View.VISIBLE);
        else
            priceContainer.setVisibility(View.GONE);

        String totalItem = quantity + " item";

        qtyText.setText(totalItem);
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
