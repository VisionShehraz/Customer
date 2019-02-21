package com.gotaxiride.driver.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.adapter.DestinationGboxAdapter;
import com.gotaxiride.driver.adapter.ItemListener;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.DestinationGoBox;
import com.gotaxiride.driver.model.Transaksi;

import java.util.ArrayList;

public class DestinasiMboxActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CALL = 992;
    ArrayList<DestinationGoBox> arrDestinationGoBoxes;
    Queries que;
    DestinasiMboxActivity activity;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviDestinasiMbox;
    private DestinationGboxAdapter destinasiAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinasi_mbox);
        activity = DestinasiMboxActivity.this;
        activity.getSupportActionBar().setTitle(R.string.list_sending);

        Bundle bund = getIntent().getExtras();
        TextView namaBarang = (TextView) findViewById(R.id.namaBarang);
        Transaksi myTrans = (Transaksi) bund.getSerializable("transaction");
        namaBarang.setText(myTrans.nama_barang);

        reviDestinasiMbox = (RecyclerView) findViewById(R.id.reviDestinasi);
        reviDestinasiMbox.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);
        que = new Queries(new DBHandler(activity));
        arrDestinationGoBoxes = que.getAllDestinasiMbox();

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
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                    return;
                }
                showWarningCall(arrDestinationGoBoxes.get(position).telepon_penerima);
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView() {

        reviDestinasiMbox.setLayoutManager(mLayoutManager);
        destinasiAdapter = new DestinationGboxAdapter(arrDestinationGoBoxes, onItemTouchListener);
        reviDestinasiMbox.setAdapter(destinasiAdapter);
    }

    private MaterialDialog showWarningCall(final String nomor) {
        final MaterialDialog md = new MaterialDialog.Builder(this)
                .title(R.string.Cost_Warning)
                .content(R.string.calling)
                .icon(new IconicsDrawable(this)
                        .icon(FontAwesome.Icon.faw_phone)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeText("No")
                .negativeColor(Color.LTGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + nomor));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
    }
}
