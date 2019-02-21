package com.gotaxiride.passenger.mMassage.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.FCMHelper;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.home.MainActivity;
import com.gotaxiride.passenger.mMassage.event.OnUserCancel;
import com.gotaxiride.passenger.model.DriverMassage;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.fcm.CancelBookRequestJson;
import com.gotaxiride.passenger.model.json.fcm.CancelBookResponseJson;
import com.gotaxiride.passenger.model.json.fcm.DriverResponse;
import com.gotaxiride.passenger.model.json.fcm.FCMMessage;
import com.gotaxiride.passenger.model.json.fcm.MassageDriverRequest;
import com.gotaxiride.passenger.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gotaxiride.passenger.config.General.FCM_KEY;
import static com.gotaxiride.passenger.model.FCMType.ORDER;
import static com.gotaxiride.passenger.model.json.fcm.DriverResponse.REJECT;


/**
 * Created by Androgo on 12/23/2018.
 */

public class SendRequestMassageService extends IntentService {

    public static final String REQUEST_TO_SEND = "RequestToSend";
    public static final String DRIVER_MASSAGE_LIST = "DriverMassageList";
    static final int FINDING_ID = 0;
    static final int FOUND_ID = 1;
    static final int FAILED_ID = 2;
    private static final String TAG = "SendRequestMassageServi";
    NotificationCompat.Builder findingBuilder;
    NotificationCompat.Builder foundBuilder;
    NotificationCompat.Builder failedBuilder;
    private MassageDriverRequest request;
    private List<DriverMassage> driverMassageList;
    private boolean isDriverFound;

    private boolean shouldStopping;

    private int minIndex;
    private int maxIndex;

    private NotificationManager notificationManager;

    public SendRequestMassageService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        request = (MassageDriverRequest) intent.getSerializableExtra(REQUEST_TO_SEND);
        driverMassageList = (List<DriverMassage>) intent.getSerializableExtra(DRIVER_MASSAGE_LIST);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(FINDING_ID, findingBuilder.build());

        int maxLoopLoading = (driverMassageList.size() % 10 == 0) ? driverMassageList.size() / 10 : (driverMassageList.size() / 10) + 1;

        for (int i = 0; i < maxLoopLoading; i++) {
            int startIndex = i * 10;
            int maxIndex = (i + 1) * 10;

            minIndex = startIndex;
            this.maxIndex = maxIndex;

            if (driverMassageList.size() < maxIndex) maxIndex = driverMassageList.size();

            for (int j = startIndex; j < maxIndex; j++) {
                request.setTimeAccept(Calendar.getInstance().getTimeInMillis());

                FCMMessage message = new FCMMessage();
                message.setTo(driverMassageList.get(i).getRegId());
                message.setData(request);

                android.util.Log.d(TAG, "onHandleIntent: To = " + message.getTo() + " Data = " + message.getData());

                FCMHelper.sendMessage(FCM_KEY, message).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        android.util.Log.d(TAG, "onResponse: ");
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
            notificationManager.cancel(FINDING_ID);
            notificationManager.notify(FAILED_ID, failedBuilder.build());
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        isDriverFound = false;

        findingBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Mencari Driver")
                .setContentText("Mohon tunggu, kami sedang mencari driver untuk anda.")
                .setOngoing(true);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        foundBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Driver Ditemukan")
                .setContentText("Driver ditemukan. Tekan untuk mendapatkan detail.")
                .setContentIntent(pendingIntent);

        failedBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Driver Tidak Ditemukan")
                .setContentText("Silahkan coba untuk melakukan order kembali.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageReceived(DriverResponse response) {
//        Toast.makeText(this, "Ada Pesan!!!", Toast.LENGTH_SHORT).show();
        Log.e("DRIVER RESPONSE (W)", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());
        if (response.getResponse().equals(DriverResponse.ACCEPT)) {
            notificationManager.cancel(FINDING_ID);
            notificationManager.notify(FOUND_ID, foundBuilder.build());
            shouldStopping = true;
            isDriverFound = true;
            Log.d("DRIVER RESPONSE", "Terima");
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onUserCancel(OnUserCancel data) {
        shouldStopping = true;

        notificationManager.cancel(FINDING_ID);

        Realm realm = Realm.getDefaultInstance();

        User loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = "D0";

        Log.d("id_transaksi_cancel", this.request.getIdTransaksi());
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new retrofit2.Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(retrofit2.Call<CancelBookResponseJson> call, retrofit2.Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(SendRequestMassageService.this, "Order Canceled!", Toast.LENGTH_SHORT).show();

                        for (int i = minIndex; i < maxIndex; i++) {
                            DriverMassage currentDriver = driverMassageList.get(i);

                            DriverResponse driverResponse = new DriverResponse();
                            driverResponse.type = ORDER;
                            driverResponse.setIdTransaksi(SendRequestMassageService.this.request.getIdTransaksi());
                            driverResponse.setResponse(REJECT);

                            FCMMessage message = new FCMMessage();
                            message.setTo(currentDriver.getRegId());
                            message.setData(response);


                            FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                                    Log.e("CANCEL REQUEST", "sent");

                                }

                                @Override
                                public void onFailure(okhttp3.Call call, IOException e) {
                                    e.printStackTrace();
                                    Log.e("CANCEL REQUEST", "failed");
                                }
                            });
                        }
                    } else {
                        Toast.makeText(SendRequestMassageService.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SendRequestMassageService.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}