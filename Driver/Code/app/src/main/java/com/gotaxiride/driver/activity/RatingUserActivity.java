package com.gotaxiride.driver.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;
import com.gotaxiride.driver.service.MyConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingUserActivity extends AppCompatActivity {

    RatingUserActivity activity;
    float nilai;
    int maxRetry = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_user);

        activity = RatingUserActivity.this;

        TextView butSubmit = (TextView) findViewById(R.id.butSubmit);
        TextView namaPelanggan = (TextView) findViewById(R.id.namaPelanggan);
        namaPelanggan.setText(getIntent().getStringExtra("nama_pelanggan"));
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final EditText addComment = (EditText) findViewById(R.id.addComment);
        ImageView logoFitur = (ImageView) findViewById(R.id.logoFitur);

        String orderFitur = getIntent().getStringExtra("order_fitur");

        selectionFitur(orderFitur, logoFitur);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                nilai = v;
            }
        });

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final JSONObject jRate = new JSONObject();
                try {
                    jRate.put("id_transaksi", getIntent().getStringExtra("id_transaksi"));
                    jRate.put("id_pelanggan", getIntent().getStringExtra("id_pelanggan"));
                    jRate.put("id_driver", getIntent().getStringExtra("id_driver"));
                    jRate.put("rating", (int) nilai);
                    jRate.put("catatan", addComment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ratingUser(jRate);
//                Toast.makeText(RatingUserActivity.this, "Rating : "+(int)nilai+"\nKomentar : "+addComment.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectionFitur(String fitur, ImageView logo) {

        switch (fitur) {
            case "1":
                logo.setImageResource(R.mipmap.ic_fitur_mride);
                break;
            case "2":
                logo.setImageResource(R.mipmap.ic_fitur_mcar);
                break;
            case "3":
                logo.setImageResource(R.mipmap.ic_fitur_mfood);
                break;
            case "4":
                logo.setImageResource(R.mipmap.ic_fitur_mmart);
                break;
            case "5":
                logo.setImageResource(R.mipmap.ic_fitur_msend);
                break;
            case "6":
                logo.setImageResource(R.mipmap.ic_fitur_mmassage);
                break;
            case "7":
                logo.setImageResource(R.mipmap.ic_fitur_mbox);
                break;
            case "8":
                logo.setImageResource(R.mipmap.ic_fitur_mservice);
                break;
            default:
                break;
        }
    }

    private void ratingUser(final JSONObject jRate) {

        final ProgressDialog pd = showLoading();
        HTTPHelper.getInstance(activity).rateUser(jRate, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                pd.dismiss();
                maxRetry = 4;
                try {
                    if (obj.getString("message").equals("success")) {
                        showFinishMessage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                pd.dismiss();
            }

            @Override
            public void onError(String message) {
                if (maxRetry == 0) {
                    pd.dismiss();
                    Toast.makeText(activity, "Connection problem..", Toast.LENGTH_SHORT).show();
                    Log.d("data_sync_mfood", "Retrieving Data Null");
                    maxRetry = 4;
                } else {
                    ratingUser(jRate);
                    maxRetry--;
                    Log.d("Try_ke_rating ", String.valueOf(maxRetry));
                    pd.dismiss();
                }
            }
        });
    }


    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }


    private MaterialDialog showFinishMessage() {
        final MaterialDialog md = new MaterialDialog.Builder(activity)
                .title("Thank You")
                .content("Happy working again")
                .icon(new IconicsDrawable(activity)
                        .color(Color.BLUE)
                        .sizeDp(24))
                .positiveText("Close")
                .cancelable(false)
                .positiveColor(Color.DKGRAY)
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                Bundle data = new Bundle();
                Intent toMaps = new Intent(activity, MainActivity.class);
                toMaps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toMaps.putExtra("SOURCE", MyConfig.dashFragment);
                startActivity(toMaps);
                finish();
            }
        });
        return md;
    }

}
