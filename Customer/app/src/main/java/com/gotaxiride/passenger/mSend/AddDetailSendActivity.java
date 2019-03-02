package com.gotaxiride.passenger.mSend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.RequestSendRequestJson;
import com.gotaxiride.passenger.utils.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static com.gotaxiride.passenger.mSend.SendActivity.FITUR_KEY;

public class AddDetailSendActivity extends AppCompatActivity {
    @BindView(R.id.mSend_distance)
    TextView distanceText;
    @BindView(R.id.mSend_price)
    TextView priceText;
    @BindView(R.id.mSend_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.mSend_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.mSend_cashPayment)
    RadioButton cashButton;
    @BindView(R.id.mSend_topUp)
    Button topUpButton;
    @BindView(R.id.mSend_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.mSend_order)
    Button orderButton;
    @BindView(R.id.mSend_goods_description)
    EditText goodsDescription;
    @BindView(R.id.mSend_sender_name)
    EditText senderName;
    @BindView(R.id.mSend_sender_phone)
    EditText senderPhone;
    @BindView(R.id.mSend_receiver_name)
    EditText receiverName;
    @BindView(R.id.mSend_receiver_phone)
    EditText receiverPhone;
    @BindView(R.id.discountText)
    TextView discountText;
    Realm realm;
    private double distance;
    private long price;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private String pickup;
    private String destination;
    private ArrayList<Driver> driverAvailable;
    private double timeDistance = 0;
    //    DiskonMpay diskonMpay;
    private long mpayBalance;
    private Fitur fitur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_add_detail);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        realm = Realm.getDefaultInstance();
        User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        mpayBalance = userLogin.getmPaySaldo();
//        diskonMpay = GoTaxiApplication.getInstance(this).getDiskonMpay();
        Intent intent = getIntent();
        if (intent.hasExtra("distance")) {
            distance = intent.getDoubleExtra("distance", 0);
            price = intent.getLongExtra("price", 0);
            pickUpLatLang = intent.getParcelableExtra("pickup_latlng");
            destinationLatLang = intent.getParcelableExtra("destination_latlng");
            pickup = intent.getStringExtra("pickup");
            destination = intent.getStringExtra("destination");
            timeDistance = intent.getDoubleExtra("time_distance", 0);
            driverAvailable = (ArrayList<Driver>) intent.getSerializableExtra("driver");
            int selectedFitur = intent.getIntExtra(FITUR_KEY, -1);

            if (selectedFitur != -1)
                fitur = realm.where(Fitur.class).equalTo("idFitur", selectedFitur).findFirst();

            discountText.setText("Diskon " + fitur.getDiskon() + " if using a wallet");
        }

        long biayaTotal = price;
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        priceText.setText(formattedText);

        float km = ((float) distance);
        String format = String.format(Locale.US, "Distance %.2f " + General.UNIT_OF_DISTANCE, km);
        distanceText.setText(format);
        if (mpayBalance < (price * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }

        cashButton.setChecked(true);
        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.mSend_mPayPayment:
                        long biayaTotal = (long) (price * fitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case R.id.mSend_cashPayment:
                        biayaTotal = price;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    default:
                        biayaTotal = price;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                }
            }
        });


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderButtonClick();
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.mSend_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Sorry, driver not available!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = (long) (this.price * fitur.getBiayaAkhir());
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();


                    Log.e("M-PAY", "used");
                    param.pakaiMpay = 1;

                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }


                break;
            case R.id.mSend_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Sorry, driver not available!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = this.price;
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();

                    Log.e("M-PAY", "not using m pay");
                    param.pakaiMpay = 0;


                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));

        mPayBalanceText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
