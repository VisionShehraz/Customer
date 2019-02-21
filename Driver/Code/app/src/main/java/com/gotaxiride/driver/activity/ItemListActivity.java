package com.gotaxiride.driver.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gotaxiride.driver.R;
import com.gotaxiride.driver.adapter.ItemShoppingAdapter;
import com.gotaxiride.driver.adapter.ItemListener;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.ItemShopping;
import com.gotaxiride.driver.network.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ItemListActivity extends AppCompatActivity {
    ArrayList<ItemShopping> arrItemShopping;
    Queries que;
    ItemListActivity activity;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviBarangBelanja;
    private ItemShoppingAdapter itemShoppingAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang);
        activity = ItemListActivity.this;
        activity.getSupportActionBar().setTitle(R.string.title_shop);

        reviBarangBelanja = (RecyclerView) findViewById(R.id.reviListBarang);
        reviBarangBelanja.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrItemShopping = que.getAllBarangBelanja();
        Log.d("Isi_barang", arrItemShopping.get(0).nama_barang + " " + arrItemShopping.get(0).isChecked);
        TextView estimasiBiaya = (TextView) findViewById(R.id.estimasiBiaya);
        TextView namaToko = (TextView) findViewById(R.id.namaToko);

        namaToko.setText("Store " + getIntent().getStringExtra("store_name"));
        estimasiBiaya.setText("cost estimation : " + amountAdapter(getIntent().getIntExtra("cost estimation", 0)));
        initListener();
        updateListView();
    }

    private void initListener() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, final int position) {

            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView() {
        reviBarangBelanja.setLayoutManager(mLayoutManager);
        itemShoppingAdapter = new ItemShoppingAdapter(arrItemShopping, onItemTouchListener);
        reviBarangBelanja.setAdapter(itemShoppingAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }

    private String amountAdapter(int amo) {
        return "$ " + NumberFormat.getNumberInstance(Locale.US).format(amo) + ",-";
    }
}
