package com.gotaxiride.passenger.mService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.FCMHelper;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.mRideCar.InProgressActivity;
import com.gotaxiride.passenger.model.Driver;
import com.gotaxiride.passenger.model.Transaksi;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.CheckStatusTransaksiRequest;
import com.gotaxiride.passenger.model.json.book.CheckStatusTransaksiResponse;
import com.gotaxiride.passenger.model.json.book.MserviceRequestJson;
import com.gotaxiride.passenger.model.json.book.MserviceResponseJson;
import com.gotaxiride.passenger.model.json.fcm.CancelBookRequestJson;
import com.gotaxiride.passenger.model.json.fcm.CancelBookResponseJson;
import com.gotaxiride.passenger.model.json.fcm.DriverRequest;
import com.gotaxiride.passenger.model.json.fcm.DriverResponse;
import com.gotaxiride.passenger.model.json.fcm.FCMMessage;
import com.gotaxiride.passenger.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gotaxiride.passenger.config.General.FCM_KEY;
import static com.gotaxiride.passenger.model.FCMType.ORDER;
import static com.gotaxiride.passenger.model.json.fcm.DriverResponse.REJECT;

public class mServiceWaiting extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String DRIVER_LIST = "DriverList";
    public static final String JENIS_STRING = "JenisString";
    public static final String ACTYPE_STRING = "AcString";
    @BindView(R.id.waiting_cancel)
    Button btnCancel;
    Transaksi transaksi;
    AppCompatActivity activity;
    Thread thread;
    boolean threadRun = true;
    private String jenisString;
    private String acString;
    private MserviceRequestJson param;
    private List<Driver> driverList;
    private DriverRequest request;
    private Driver driver;
    private int currentLoop;
    private double timeDistance;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mservice_waiting);
        ButterKnife.bind(this);

        activity = this;

        Intent getData = getIntent();
        jenisString = getData.getStringExtra(JENIS_STRING);
        acString = getData.getStringExtra(ACTYPE_STRING);

        param = (MserviceRequestJson) getIntent().getSerializableExtra(REQUEST_PARAM);
        driverList = (List<Driver>) getIntent().getSerializableExtra(DRIVER_LIST);

        //timeDistance = getIntent().getDoubleExtra("time_distance", 0);
        currentLoop = 0;

        sendRequestTransaksi();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (request != null) {
                    cancelOrder();
                }
            }
        });
    }

    private void sendRequestTransaksi() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.requestTransaksi(param).enqueue(new Callback<MserviceResponseJson>() {
            @Override
            public void onResponse(Call<MserviceResponseJson> call, Response<MserviceResponseJson> response) {
                if (response.isSuccessful()) {
                    buildDriverRequest(response.body());
                    //fcmBroadcast(currentLoop);


                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < driverList.size(); i++) {
                                if (threadRun) {
                                    fcmBroadcast(currentLoop);
                                }
                            }
                            try {
                                Thread.sleep(25000);
//                                    if(!threadRun){
//                                        thread.stop();
//                                    }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (threadRun) {
                                CheckStatusTransaksiRequest param = new CheckStatusTransaksiRequest();
                                param.setIdTransaksi(transaksi.getId());
                                service.checkStatusTransaksi(param).enqueue(new Callback<CheckStatusTransaksiResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                                        if (response.isSuccessful()) {
                                            CheckStatusTransaksiResponse checkStatus = response.body();
                                            if (checkStatus.isStatus()) {
                                                Intent intent = new Intent(activity, InProgressActivity.class);
                                                intent.putExtra("driver", checkStatus.getListDriver().get(0));
                                                intent.putExtra("request", request);
                                                intent.putExtra("time_distance", timeDistance);
                                                startActivity(intent);
                                            } else {
                                                Log.e("DRIVER STATUS", "Driver not found!");
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(mServiceWaiting.this, "Your driver seem busy!\npleas try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckStatusTransaksiResponse> call, Throwable t) {
                                        Log.e("DRIVER STATUS", "Driver not found!");
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mServiceWaiting.this, "Your driver seem busy!\npleas try again.", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        finish();
                                    }
                                });
                            }
                        }
                    });
                    thread.start();
                }
            }

            @Override
            public void onFailure(Call<MserviceResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder() {
        User loginUser = GoTaxiApplication.getInstance(mServiceWaiting.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = "D0";
        Log.d("id_transaksi_cancel", this.request.getIdTransaksi());
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(mServiceWaiting.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        threadRun = false;
                        finish();
                    } else {
                        Toast.makeText(mServiceWaiting.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mServiceWaiting.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driverList.get(currentLoop - 1).getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
                threadRun = false;

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    private void buildDriverRequest(MserviceResponseJson response) {
        transaksi = response.getData().get(0);
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        if (request == null) {
            request = new DriverRequest();
            request.setIdTransaksi(transaksi.getId());
            request.setIdPelanggan(transaksi.getIdPelanggan());
            request.setRegIdPelanggan(loginUser.getRegId());
            request.setOrderFitur(transaksi.getOrderFitur());
            request.setStartLatitude(transaksi.getStartLatitude());
            request.setStartLongitude(transaksi.getStartLongitude());
            request.setHarga(transaksi.getHarga());
            request.setWaktuOrder(transaksi.getWaktuOrder());
            request.setAlamatAsal(transaksi.getAlamatAsal());
            request.setKodePromo(transaksi.getKodePromo());
            request.setKreditPromo(transaksi.getKreditPromo());
            request.setPakaiMPay(transaksi.isPakaiMpay());

            String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
            request.setNamaPelanggan(namaLengkap);
            request.setTelepon(loginUser.getNoTelepon());
            request.setType(ORDER);

            request.setTanggalPelayanan(param.getTglService());
            request.setJamPelayanan(param.getJamService());
            request.setQuantity(param.getQuantity());
            request.setResidentialType(param.getResidentialType());
            request.setProblem(param.getProblem());
            request.setJenisService(jenisString);
            request.setAcType(acString);
        }
    }

    private void fcmBroadcast(int index) {
        Driver driverToSend = driverList.get(index);
        currentLoop++;
        request.setTime_accept(new Date().getTime() + "");
        FCMMessage message = new FCMMessage();
        message.setTo(driverToSend.getRegId());
        message.setData(request);

//        Log.e("REQUEST TO DRIVER", message.getData().toString());
        driver = driverToSend;

        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("DRIVER RESPONSE (W)", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());
//        if (currentLoop < driverList.size()) {
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
            Log.d("DRIVER RESPONSE", "Terima");
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    threadRun = false;
                    for (Driver cDriver : driverList) {
                        if (cDriver.getId().equals(response.getId())) {
                            driver = cDriver;
                        }
                    }
//                        saveTransaction(transaksi);
//                        saveDriver(driver);
//                        Toast.makeText(getApplicationContext(), "Transaksi " + response.getIdTransaksi() + " ada yang mau!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, mServiceProgress.class);
                    intent.putExtra("driver", driver);
                    intent.putExtra("request", request);
                    //intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                    finish();
                }
            });
        }
//        else {
//            Log.d("DRIVER RESPONSE", "Tolak");
//            if(currentLoop == (driverList.size()-1)){
//                Intent intent = new Intent(activity, mServiceProgress.class);
//                intent.putExtra("driver", driver);
//                intent.putExtra("request", request);
//                //intent.putExtra("time_distance", timeDistance);
//                threadRun = false;
//                startActivity(intent);
//                finish();
//            }else{
//                fcmBroadcast(++currentLoop);
//                 //currentLoop++;
//            }
//        }
//        }
    }

}
