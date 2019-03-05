package com.letsride.passenger.mFood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.FCMHelper;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.config.General;
import com.letsride.passenger.home.MainActivity;
import com.letsride.passenger.mRideCar.InProgressActivity;
import com.letsride.passenger.model.Driver;
import com.letsride.passenger.model.TransaksiFood;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.CheckStatusTransaksiRequest;
import com.letsride.passenger.model.json.book.CheckStatusTransaksiResponse;
import com.letsride.passenger.model.json.book.GetNearRideCarRequestJson;
import com.letsride.passenger.model.json.book.GetNearRideCarResponseJson;
import com.letsride.passenger.model.json.book.RequestFoodRequestJson;
import com.letsride.passenger.model.json.book.RequestFoodResponseJson;
import com.letsride.passenger.model.json.fcm.DriverRequest;
import com.letsride.passenger.model.json.fcm.DriverResponse;
import com.letsride.passenger.model.json.fcm.FCMMessage;
import com.letsride.passenger.model.json.fcm.FoodDriverRequest;
import com.letsride.passenger.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.letsride.passenger.config.General.FCM_KEY;

/**
 * Created by fathony on 1/26/2017.
 */

public class FoodWaitingActivity extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String TIME_DISTANCE = "TimeDistance";

    private List<Driver> driverList;
    private RequestFoodRequestJson param;

    private Realm realm;
    private User loginUser;
    private BookService bookService;

    private int minIndex;
    private int maxIndex;

    private boolean isDriverFound;

    private boolean shouldStopping;

    private long timeDistance;

    private FoodDriverRequest foodDriverRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        Intent intent = getIntent();
        param = (RequestFoodRequestJson) intent.getSerializableExtra(REQUEST_PARAM);
        timeDistance = intent.getLongExtra(TIME_DISTANCE, -1);

        realm = Realm.getDefaultInstance();
        loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        GetNearRideCarRequestJson getRideParam = new GetNearRideCarRequestJson();
        getRideParam.setLatitude(param.getStartLatitude());
        getRideParam.setLongitude(param.getStartLongitude());

        bookService.getNearRide(getRideParam).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    driverList = response.body().getData();
                    if (driverList.isEmpty()) {
                        Toast.makeText(FoodWaitingActivity.this, "Tidak ada driver disekitar.", Toast.LENGTH_SHORT).show();
                        FoodWaitingActivity.this.finish();
                    } else {
                        sendToServer();
                    }
                } else {
                    onFailure(call, new RuntimeException("Check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {
                Toast.makeText(FoodWaitingActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                FoodWaitingActivity.this.finish();
            }
        });
    }


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


    private void sendToServer() {
        bookService.requestTransaksiMFood(param).enqueue(new Callback<RequestFoodResponseJson>() {
            @Override
            public void onResponse(Call<RequestFoodResponseJson> call, Response<RequestFoodResponseJson> response) {
                if (response.isSuccessful()) {
                    broadcastFcm(response.body().getData().get(0));
                } else {
                    onFailure(call, new RuntimeException("Check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<RequestFoodResponseJson> call, Throwable t) {
                Toast.makeText(FoodWaitingActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                FoodWaitingActivity.this.finish();
            }
        });
    }

    private void broadcastFcm(TransaksiFood transaksiFood) {
        final FoodDriverRequest requestToSend = generateFcmRequest(transaksiFood);
        foodDriverRequest = requestToSend;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(requestToSend);
            }
        }).start();
    }

    private void sendMessage(FoodDriverRequest request) {
        for (int i = 0; i < driverList.size(); i++) {
            request.setTimeAccept(new Date().getTime());

            FCMMessage message = new FCMMessage();
            message.setTo(driverList.get(i).getRegId());
            message.setData(request);

            Log.d("FoodWaiting", ServiceGenerator.gson.toJson(request));

            Log.d("FoodWaiting", "onHandleIntent: To = " + message.getTo() + " Data = " + message.getData());

            FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    Log.d("FoodWaiting", "onResponse: ");
                }
            });
        }

        try {
            Thread.sleep(20 * 1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CheckStatusTransaksiRequest parameter = new CheckStatusTransaksiRequest();
        parameter.setIdTransaksi(foodDriverRequest.getIdTransaksi());
        bookService.checkStatusTransaksi(parameter).enqueue(new Callback<CheckStatusTransaksiResponse>() {
            @Override
            public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                if (response.isSuccessful()) {
                    CheckStatusTransaksiResponse checkStatus = response.body();
                    if (checkStatus.isStatus()) {
                        DriverRequest request = new DriverRequest();
                        request.setAlamatAsal(foodDriverRequest.getAlamatAsal());
                        request.setAlamatTujuan(foodDriverRequest.getAlamatTujuan());
                        request.setJarak(foodDriverRequest.getJarak());
                        request.setHarga(foodDriverRequest.getHarga());
                        request.setIdTransaksi(foodDriverRequest.getIdTransaksi());
                        request.setStartLatitude(param.getStartLatitude());
                        request.setStartLongitude(param.getStartLongitude());
                        request.setEndLatitude(param.getTokoLatitude());
                        request.setEndLongitude(param.getTokoLongitude());

                        Intent intent = new Intent(FoodWaitingActivity.this, InProgressActivity.class);
                        intent.putExtra("driver", checkStatus.getListDriver().get(0));
                        intent.putExtra("request", request);
                        intent.putExtra("time_distance", timeDistance);
                        startActivity(intent);
                    } else {
                        Log.e("DRIVER STATUS", "Driver not found!");
                        FoodWaitingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FoodWaitingActivity.this, "Your driver seem busy!\npleas try again.", Toast.LENGTH_LONG).show();
                            }
                        });

                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckStatusTransaksiResponse> call, Throwable t) {
                Log.e("DRIVER STATUS", "Driver not found!");
                FoodWaitingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FoodWaitingActivity.this, "Don't get a driver, please try again.", Toast.LENGTH_LONG).show();
                    }
                });

                finish();
            }
        });
    }

    private void sendMessageLegacy(FoodDriverRequest request) {
        int maxLoopLoading = (driverList.size() % 10 == 0) ? driverList.size() / 10 : (driverList.size() / 10) + 1;
        for (int i = 0; i < maxLoopLoading; i++) {
            int startIndex = i * 10;
            int maxIndex = (i + 1) * 10;

            minIndex = startIndex;
            this.maxIndex = maxIndex;

            if (driverList.size() < maxIndex) maxIndex = driverList.size();

            for (int j = startIndex; j < maxIndex; j++) {

                request.setTimeAccept(new Date().getTime());

                FCMMessage message = new FCMMessage();
                message.setTo(driverList.get(i).getRegId());
                message.setData(request);

                Log.d("FoodWaiting", ServiceGenerator.gson.toJson(request));

                Log.d("FoodWaiting", "onHandleIntent: To = " + message.getTo() + " Data = " + message.getData());

                FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        Log.d("FoodWaiting", "onResponse: ");
                    }
                });
            }

            try {
                Thread.sleep(20 * 1000);

                if (shouldStopping) break;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isDriverFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FoodWaitingActivity.this, "No driver available.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FoodWaitingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    FoodWaitingActivity.this.startActivity(intent);
                }
            });
        }
    }

    private FoodDriverRequest generateFcmRequest(TransaksiFood transaksiFood) {
        FoodDriverRequest request = new FoodDriverRequest();
        request.setIdTransaksi(transaksiFood.getId());
        request.setIdPelanggan(transaksiFood.getIdPelanggan());
        request.setRegIdPelanggan(loginUser.getRegId());
        request.setOrderFitur(transaksiFood.getOrderFitur());
        request.setHarga(transaksiFood.getHarga());
        request.setJarak(transaksiFood.getJarak());
        request.setAlamatAsal(transaksiFood.getAlamatAsal());
        request.setKreditPromo(transaksiFood.getKreditPromo());
        request.setAlamatTujuan(transaksiFood.getAlamatTujuan());
        request.setKodePromo(transaksiFood.getKodePromo());
        request.setPakaiMPay(transaksiFood.isPakaiMPay());
        request.setType("1");

        String namaPelanggan = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
        request.setNamaPelanggan(namaPelanggan);
        request.setTelepon(loginUser.getNoTelepon());
        request.setTimeAccept(new Date().getTime());

        return request;
    }

    @Override
    public void onBackPressed() {

    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DriverResponse response) {
        Log.e("MartWaiting", "you got a broadcast " + response.getResponse());
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
            shouldStopping = true;
            isDriverFound = true;

            Driver driver = null;
            for (Driver d : driverList) {
                if (d.getId().equalsIgnoreCase(response.getId())) driver = d;
                break;
            }

            DriverRequest request = new DriverRequest();
            request.setAlamatAsal(foodDriverRequest.getAlamatAsal());
            request.setAlamatTujuan(foodDriverRequest.getAlamatTujuan());
            request.setJarak(foodDriverRequest.getJarak());
            request.setHarga(foodDriverRequest.getHarga());
            request.setIdTransaksi(foodDriverRequest.getIdTransaksi());
            request.setStartLatitude(param.getStartLatitude());
            request.setStartLongitude(param.getStartLongitude());
            request.setEndLatitude(param.getTokoLatitude());
            request.setEndLongitude(param.getTokoLongitude());

            Intent intent = new Intent(this, InProgressActivity.class);
            intent.putExtra("driver", driver);
            intent.putExtra("request", request);
            intent.putExtra("time_distance", timeDistance);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
