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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.model.Transaksi;
import com.gotaxiride.driver.R;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CALL = 992;
    DeliveryDetailsActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengiriman);

        activity = DeliveryDetailsActivity.this;
        Transaksi transaksi = (Transaksi) getIntent().getSerializableExtra("data_order");
        initView(transaksi);

    }

    private void initView(final Transaksi transaksi) {
        TextView namaBarang, namaPengirim, namaPenerima, nomorPengirim, nomorPenerima;
        Button callPengirim, callPenerima;

        namaBarang = (TextView) findViewById(R.id.namaBarang);
        namaPengirim = (TextView) findViewById(R.id.namaPengirim);
        namaPenerima = (TextView) findViewById(R.id.namaPenerima);
        nomorPengirim = (TextView) findViewById(R.id.nomorPengirim);
        nomorPenerima = (TextView) findViewById(R.id.nomorPenerima);

        callPengirim = (Button) findViewById(R.id.callPengirim);
        callPenerima = (Button) findViewById(R.id.callPenerima);

        namaBarang.setText(transaksi.nama_barang);
        namaPengirim.setText(transaksi.nama_pengirim);
        namaPenerima.setText(transaksi.nama_penerima);
        nomorPengirim.setText(transaksi.telepon_pengirim);
        nomorPenerima.setText(transaksi.telepon_penerima);

        callPengirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                    return;
                }
                showWarningCall(transaksi.telepon_pengirim);
            }
        });

        callPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                    return;
                }
                showWarningCall(transaksi.telepon_penerima);
            }
        });
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
}
