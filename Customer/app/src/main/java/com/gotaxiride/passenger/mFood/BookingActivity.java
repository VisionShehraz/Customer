package com.gotaxiride.passenger.mFood;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.MapDirectionAPI;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.Makanan;
import com.gotaxiride.passenger.model.MfoodMitra;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.model.Restoran;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.RequestFoodRequestJson;
import com.gotaxiride.passenger.utils.Log;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class BookingActivity extends AppCompatActivity implements MakananItem.OnCalculatePrice {

    private final int DESTINATION_ID = 1;
    @BindView(R.id.btn_home)
    ImageView backButton;
    @BindView(R.id.food_orders)
    RecyclerView foodOrders;
    @BindView(R.id.booking_location)
    LinearLayout locationButton;
    @BindView(R.id.food_destination)
    TextView locationText;
    @BindView(R.id.food_addNotes)
    EditText addNotes;
    @BindView(R.id.food_cost)
    TextView foodCost;
    @BindView(R.id.delivery_cost)
    TextView deliveryCost;
    @BindView(R.id.value_service)
    TextView totalCost;
    @BindView(R.id.food_cash)
    TextView cashCost;
    @BindView(R.id.service_paymentgroup)
    RadioGroup paymentGroup;
    @BindView(R.id.radio_mpay)
    RadioButton mpayPayment;
    @BindView(R.id.radio_cash)
    RadioButton cashPayment;
    @BindView(R.id.mpay_balance)
    TextView mPayBalance;
    @BindView(R.id.mpay_discount)
    TextView mPayDiscount;
    @BindView(R.id.mpay_topup)
    Button topupButton;
    @BindView(R.id.order_btn)
    Button orderButton;
    private FastItemAdapter<MakananItem> pesananAdapter;
    private Realm realm;
    private User loginUser;
    private LatLng destinationLocation;
    private boolean isRestoranMitra;
    private Restoran restoran;
    private Fitur designedFitur;
    private MfoodMitra mitraHarga;
    private long foodCostLong = 0;
    private long deliveryCostBeforeDiscLong = 0;
    private long deliveryCostLong = 0;
    private long totalLong = 0;
    private long timeDistance;
    private double jarak = -99.0;
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(BookingActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(BookingActivity.this, json);
                if (distance >= 0) {
                    BookingActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDistance(distance);
                            timeDistance = time / 60;
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        realm = Realm.getDefaultInstance();
        loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());

        restoran = realm.copyFromRealm(realm.where(Restoran.class).findFirst());

        isRestoranMitra = restoran.isPartner();

        designedFitur = realm.copyFromRealm(realm.where(Fitur.class).equalTo("idFitur", 3).findFirst());
        mitraHarga = GoTaxiApplication.getInstance(this).getMfoodMitra();

        pesananAdapter = new FastItemAdapter<>();
        foodOrders.setLayoutManager(new LinearLayoutManager(this));
        foodOrders.setAdapter(pesananAdapter);
        loadMakanan();

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationPicker();
            }
        });

        updateCostPrice();
        updateEstimatedFoodCost();

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToOrder()) sendOrder();
            }
        });

        topupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingActivity.this, TopUpActivity.class));
            }
        });

        if (isRestoranMitra) {
            mPayDiscount.setText("Diskon " + mitraHarga.getDiskon() + " if using a wallet");
        } else {
            mPayDiscount.setText("Diskon " + designedFitur.getDiskon() + " if using a wallet");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateCostPrice();
            }
        });
    }

    private void sendOrder() {
        List<PesananFood> existingFood = realm.copyFromRealm(realm.where(PesananFood.class).findAll());

        for (PesananFood pesanan : existingFood) {
            if (pesanan.getCatatan() == null || pesanan.getCatatan().trim().equals(""))
                pesanan.setCatatan("");
        }

        RequestFoodRequestJson param = new RequestFoodRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur("3");
        param.setStartLatitude(destinationLocation.latitude);
        param.setStartLongitude(destinationLocation.longitude);
        param.setTokoLatitude(restoran.getLatitude());
        param.setTokoLongitude(restoran.getLongitude());
        param.setAlamatAsal(locationText.getText().toString());
        param.setAlamatResto(restoran.getAlamat());
        param.setJarak(jarak);
        param.setHarga(deliveryCostLong);
        param.setPakaiMPay(paymentGroup.getCheckedRadioButtonId() == R.id.radio_mpay);
        param.setIdResto(String.valueOf(restoran.getId()));
        param.setTotalBiayaBelanja(foodCostLong);
        param.setCatatan(addNotes.getText().toString());
        param.setPesanan(existingFood);

        Log.d("BookingAct", ServiceGenerator.gson.toJson(param));

        Intent intent = new Intent(this, FoodWaitingActivity.class);
        intent.putExtra(FoodWaitingActivity.REQUEST_PARAM, param);
        intent.putExtra(FoodWaitingActivity.TIME_DISTANCE, timeDistance);
        startActivity(intent);
    }

    private void showLocationPicker() {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationPickerActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getStringExtra(LocationPickerActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(LocationPickerActivity.LOCATION_LATLNG);
                locationText.setText(address);
                destinationLocation = latLng;
            }
        }
        requestRoute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        User userLogin = GoTaxiApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalance.setText(formattedText);

        long biayaAkhir = 0;
        if (isRestoranMitra) {
            biayaAkhir = (long) (totalLong * mitraHarga.getBiayaAkhir());
        } else {
            biayaAkhir = (long) (totalLong * designedFitur.getBiayaAkhir());
        }

        if (userLogin.getmPaySaldo() < biayaAkhir) {
            mpayPayment.setEnabled(false);
            cashPayment.toggle();
        } else {
            mpayPayment.setEnabled(true);
        }
    }

    private boolean readyToOrder() {
        if (destinationLocation == null) {
            Toast.makeText(this, "Please select your location.", Toast.LENGTH_SHORT).show();
            return false;
        }

        List<PesananFood> existingFood = realm.copyFromRealm(realm.where(PesananFood.class).findAll());

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if (quantity == 0) {
            Toast.makeText(this, "Please order at least 1 item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (jarak == -99.0) {
            Toast.makeText(this, "Please wait a moment...", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void updateEstimatedFoodCost() {
        List<PesananFood> existingFood = realm.copyFromRealm(realm.where(PesananFood.class).findAll());

        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            cost += existingFood.get(p).getTotalHarga();
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);

        foodCostLong = cost;

        foodCost.setText(formattedText);

        updateTotal();
    }

    private void updateCostPrice() {
        if (isRestoranMitra) {
            deliveryCostBeforeDiscLong = mitraHarga.getBiaya();
        } else {
            deliveryCostBeforeDiscLong = designedFitur.getBiaya();
        }

        double biayaAkhir = 1.0;

        if (paymentGroup.getCheckedRadioButtonId() == R.id.radio_mpay) {
            if (isRestoranMitra) {
                biayaAkhir = mitraHarga.getBiayaAkhir();
            } else {
                biayaAkhir = designedFitur.getBiayaAkhir();
            }
        }

        deliveryCostLong = (long) (deliveryCostBeforeDiscLong * biayaAkhir);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(deliveryCostLong);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        deliveryCost.setText(formattedText);

        updateTotal();
    }

    private void updateTotal() {
        totalLong = foodCostLong + deliveryCostLong;

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalLong);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);

        totalCost.setText(formattedText);
        cashCost.setText(formattedText);
    }

    private void loadMakanan() {
        List<Makanan> makananList = realm.copyFromRealm(realm.where(Makanan.class).findAll());
        List<PesananFood> pesananFoods = realm.copyFromRealm(realm.where(PesananFood.class).findAll());
        pesananAdapter.clear();
        for (PesananFood pesanan : pesananFoods) {
            MakananItem makananItem = new MakananItem(this, this);
            makananItem.quantity = 0;
            for (Makanan makanan : makananList) {
                if (makanan.getId() == pesanan.getIdMakanan()) {
                    makananItem.quantity = pesanan.getQty();
                    makananItem.id = makanan.getId();
                    makananItem.namaMenu = makanan.getNamaMenu();
                    makananItem.deskripsiMenu = makanan.getDeskripsiMenu();
                    makananItem.harga = makanan.getHarga();
                    makananItem.cost = makanan.getHarga();
                    makananItem.catatan = pesanan.getCatatan();

                    break;
                }
            }

            pesananAdapter.add(makananItem);
        }

        pesananAdapter.notifyDataSetChanged();
    }

    @Override
    public void calculatePrice() {
        updateEstimatedFoodCost();
    }

    private void updateDistance(long distance) {
        float km = ((float) distance) / 1000f;

        this.jarak = km;
    }

    private void requestRoute() {
        if (destinationLocation != null) {
            LatLng restoranLocation = new LatLng(restoran.getLatitude(), restoran.getLongitude());
            MapDirectionAPI.getDirection(restoranLocation, destinationLocation).enqueue(updateRouteCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
