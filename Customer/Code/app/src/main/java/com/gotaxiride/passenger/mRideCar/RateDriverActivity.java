package com.gotaxiride.passenger.mRideCar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.RateDriverRequestJson;
import com.gotaxiride.passenger.model.json.book.RateDriverResponseJson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateDriverActivity extends AppCompatActivity {

    RateDriverActivity activity;
    float nilai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }


        activity = RateDriverActivity.this;
        TextView butSubmit = (TextView) findViewById(R.id.butSubmit);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final EditText addComment = (EditText) findViewById(R.id.addComment);

        final String idTransaksi = getIntent().getStringExtra("id_transaksi");
        final String idPelanggan = getIntent().getStringExtra("id_pelanggan");
        final String idDriver = getIntent().getStringExtra("id_driver");

//        selectionFitur(orderFitur, logoFitur);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                nilai = v;
            }
        });

//        final JSONObject jRate = new JSONObject();
//        try {
//            jRate.put("id_transaksi", idTransaksi);
//            jRate.put("id_pelanggan", idPelanggan);
//            jRate.put("id_driver", idDriver);
//            jRate.put("rating", (int)nilai);
//            jRate.put("catatan", addComment.getText().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateDriverRequestJson request = new RateDriverRequestJson();
                request.id_transaksi = idTransaksi;
                request.id_pelanggan = idPelanggan;
                request.id_driver = idDriver;
                request.rating = nilai + "";
                request.catatan = addComment.getText().toString();

                ratingUser(request);
//                Toast.makeText(RatingUserActivity.this, "Rating : "+(int)nilai+"\nKomentar : "+addComment.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectionFitur(String fitur, ImageView logo) {

        switch (fitur) {
            case "1":
                logo.setImageResource(R.drawable.ic_mride);
                break;
            case "2":
                logo.setImageResource(R.drawable.ic_mcar);
                break;
            default:
                break;
        }
    }

    private void ratingUser(RateDriverRequestJson request) {

        User loginUser = GoTaxiApplication.getInstance(RateDriverActivity.this).getLoginUser();

        final ProgressDialog pd = showLoading();
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.rateDriver(request).enqueue(new Callback<RateDriverResponseJson>() {
            @Override
            public void onResponse(Call<RateDriverResponseJson> call, Response<RateDriverResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("success")) {
                        finishDialog();
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<RateDriverResponseJson> call, Throwable t) {
                t.printStackTrace();
                pd.dismiss();
                Toast.makeText(RateDriverActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }


    private void finishDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(R.string.rate_message);
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
