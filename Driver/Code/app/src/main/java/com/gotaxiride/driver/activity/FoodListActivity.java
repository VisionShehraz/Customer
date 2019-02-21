package com.gotaxiride.driver.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.adapter.ItemListener;
import com.gotaxiride.driver.adapter.FoodShoppingAdapter;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.FoodShopping;
import com.gotaxiride.driver.network.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodListActivity extends AppCompatActivity {
    ArrayList<FoodShopping> arrFoodShopping;
    Queries que;
    FoodListActivity activity;
    TextView estimasiBiaya, namaResto;
    Button callResto;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviMakananBelanja;
    private FoodShoppingAdapter barangBelanjaAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_makanan);
        activity = FoodListActivity.this;
        activity.getSupportActionBar().setTitle(R.string.Shopping_list);

        reviMakananBelanja = (RecyclerView) findViewById(R.id.reviListBarang);
        reviMakananBelanja.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrFoodShopping = que.getAllMakananBelanja();
        Log.d("Isi_makanan", arrFoodShopping.get(0).nama_makanan);
        estimasiBiaya = (TextView) findViewById(R.id.estimasiBiaya);
        namaResto = (TextView) findViewById(R.id.namaResto);
        callResto = (Button) findViewById(R.id.callResto);

        estimasiBiaya.setText("Estimated costs : " + amountAdapter(getIntent().getIntExtra("estimasi_biaya", 0)));
        namaResto.setText(getIntent().getStringExtra("nama_resto"));
        callResto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningCall(getIntent().getStringExtra("telepon_resto"));
            }
        });

        initListener();
        updateListView();
    }

    private void initListener() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
//                Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton1Click(View view, final int position) {
//                Toast.makeText(activity, "harga ke click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView() {
        reviMakananBelanja.setLayoutManager(mLayoutManager);
        barangBelanjaAdapter = new FoodShoppingAdapter(arrFoodShopping, onItemTouchListener);
        reviMakananBelanja.setAdapter(barangBelanjaAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }

    private String amountAdapter(int amo) {
        return "$ " + NumberFormat.getNumberInstance(Locale.US).format(amo) + ".00";
    }

    private MaterialDialog showWarningCall(final String nomor) {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title(R.string.Cost_Warning)
                .content("Do you want to call this number?")
                .icon(new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_phone)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeText("Cancel")
                .negativeColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + nomor));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(phoneIntent);
                md.dismiss();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }
}
