package com.gotaxiride.passenger.mMassage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.mMassage.event.OnUserCancel;
import com.gotaxiride.passenger.mMassage.service.SendRequestMassageService;
import com.gotaxiride.passenger.model.DriverMassage;
import com.gotaxiride.passenger.model.ItemHistory;
import com.gotaxiride.passenger.model.TransaksiMassage;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.fcm.DriverResponse;
import com.gotaxiride.passenger.model.json.fcm.MassageDriverRequest;
import com.gotaxiride.passenger.model.json.menu.HistoryRequestJson;
import com.gotaxiride.passenger.model.json.menu.HistoryResponseJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 12/30/2018.
 */

public class WaitingMassageActivity extends AppCompatActivity {

    public static final String MASSAGE_RESPONSE = "MassageResponse";
    public static final String DRIVER_LIST = "DriverList";
    @BindView(R.id.waiting_tipeLayanan)
    TextView waitingTipeLayanan;
    @BindView(R.id.waiting_cancel)
    Button waitingButton;
    private TransaksiMassage transaksiMassage;
    private List<DriverMassage> driverMassageList;
    private User loginUser;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting);

        ButterKnife.bind(this);

        loginUser = GoTaxiApplication.getInstance(this).getRealmInstance().copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());

        transaksiMassage = (TransaksiMassage) getIntent().getSerializableExtra(MASSAGE_RESPONSE);
        driverMassageList = (List<DriverMassage>) getIntent().getSerializableExtra(DRIVER_LIST);

        waitingTipeLayanan.setText(R.string.home_mMassage);

        MassageDriverRequest request = new MassageDriverRequest();
        request.setIdTransaksi(transaksiMassage.getIdTransaksi());
        request.setHarga(transaksiMassage.getHarga());
        request.setOrderFitur(transaksiMassage.getOrderFitur());
        request.setAlamatAsal(transaksiMassage.getAlamatAsal());
        request.setWaktuOrder(transaksiMassage.getWaktuOrder());
        request.setKreditPromo(transaksiMassage.getKreditPromo());
        request.setPakaiMpay(transaksiMassage.isPakaiMpay());
        request.setKota(transaksiMassage.getKota());
        request.setTanggalPelayanan(transaksiMassage.getTanggalPelayanan());
        request.setMassageMenu(transaksiMassage.getMassageMenu());
        request.setJamPelayanan(transaksiMassage.getJamPelayanan());
        request.setLamaPelayanan(transaksiMassage.getLamaPelayanan());
        request.setPreferGender(transaksiMassage.getPreferGender());
        request.setPelangganGender(transaksiMassage.getPelangganGender());
        request.setCatatanTambahan(transaksiMassage.getCatatanTambahan());

        request.setStartLongitude(transaksiMassage.getStartLongitude());
        request.setStartLatitiude(transaksiMassage.getStartLatitude());
        request.setIdPelanggan(transaksiMassage.getIdPelanggan());
        request.setStatusTransaksi(transaksiMassage.getStatusTransaksi());
        request.setTimeAccept(Calendar.getInstance().getTimeInMillis());

        String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
        request.setNamaPelanggan(namaLengkap);
        request.setTelepon(loginUser.getNoTelepon());

        request.setType(1);
        request.setRegIdPelanggan(loginUser.getRegId());

        Intent intent = new Intent(this, SendRequestMassageService.class);
        intent.putExtra(SendRequestMassageService.REQUEST_TO_SEND, request);
        intent.putExtra(SendRequestMassageService.DRIVER_MASSAGE_LIST, (Serializable) driverMassageList);
        startService(intent);

        waitingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new OnUserCancel());
                finish();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageOrder(DriverResponse response) {
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = loginUser.getId();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getOnProgressHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;
                    ItemHistory currentHistory = null;

                    for (ItemHistory ih : data) {
                        if (ih.id_transaksi.equalsIgnoreCase(transaksiMassage.getIdTransaksi())) {
                            currentHistory = ih;
                            break;
                        }
                    }
                    Intent intentMassage = new Intent(WaitingMassageActivity.this, InProgressFinishedMassageActivity.class);
                    intentMassage.putExtra(InProgressFinishedMassageActivity.IS_COMPLETED_ID, false);
                    intentMassage.putExtra(InProgressFinishedMassageActivity.ITEM_HISTORY_ID, currentHistory);
                    startActivity(intentMassage);
                    finish();
                } else {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                Toast.makeText(WaitingMassageActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}