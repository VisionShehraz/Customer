package com.gotaxiride.passenger.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.home.ChatActivity;
import com.gotaxiride.passenger.mRideCar.RateDriverActivity;
import com.gotaxiride.passenger.model.Chat;
import com.gotaxiride.passenger.model.json.fcm.DriverResponse;
import com.gotaxiride.passenger.utils.Log;
import com.gotaxiride.passenger.utils.db.DBHandler;
import com.gotaxiride.passenger.utils.db.Queries;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.gotaxiride.passenger.model.FCMType.CHAT;
import static com.gotaxiride.passenger.model.FCMType.ORDER;
import static com.gotaxiride.passenger.model.ResponseCode.ACCEPT;
import static com.gotaxiride.passenger.model.ResponseCode.CANCEL;
import static com.gotaxiride.passenger.model.ResponseCode.FINISH;
import static com.gotaxiride.passenger.model.ResponseCode.REJECT;
import static com.gotaxiride.passenger.model.ResponseCode.START;

/**
 * Created by Androgo on 10/13/2018.
 */

public class GoTaxiMessagingService extends FirebaseMessagingService {
    public static final String BROADCAST_ACTION = "com.gotaxiride.passenger";
    public static final String BROADCAST_ORDER = "order";
    Intent intent;
    Intent intentOrder;
    private String regIdDriver = "";

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        intentOrder = new Intent(BROADCAST_ORDER);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            Log.e("FCM DATA", remoteMessage.getData().toString());
            parseAndSendMessage(remoteMessage.getData());
            messageHandler(remoteMessage);
        }
    }

    private void parseAndSendMessage(Map<String, String> mapResponse) {
        int code = Integer.parseInt(mapResponse.get("type"));
//        int FEATURE = Integer.parseInt(mapResponse.get("order_fitur"));
        Log.e("PUBLISH", mapResponse.toString());
        switch (code) {
            case ORDER:
//                switch (FEATURE){
//                    case 1:
                DriverResponse response = new DriverResponse();
                response.setId(mapResponse.get("id_driver"));
                response.setIdTransaksi(mapResponse.get("id_transaksi"));
                response.setResponse(mapResponse.get("response"));
                EventBus.getDefault().post(response);
//                        break;
//                    case 2:
//                        response = new DriverResponse();
//                        response.setId(mapResponse.get("id_driver"));
//                        response.setIdTransaksi(mapResponse.get("id_transaksi"));
//                        response.setResponse(mapResponse.get("response"));
//                        EventBus.getDefault().post(response);
//                        break;
//                    case 4:
//                        MartDriverResponse martResponse = new MartDriverResponse();
//                        martResponse.setIdDriver(mapResponse.get("id_driver"));
//                        martResponse.setResponse(mapResponse.get("response"));
//                        martResponse.setType(mapResponse.get("type"));
//                        martResponse.setIdTransaksi(mapResponse.get("id_transaksi"));
//                        EventBus.getDefault().post(martResponse);
//                        break;
//                    case 5:
//                        break;
//
//                }

//                break;

            case CHAT:

                break;

        }


    }


    private void messageHandler(RemoteMessage remoteMessage) {
//        Log.e("FCM DATA", remoteMessage.getData().toString());
        int code = Integer.parseInt(remoteMessage.getData().get("type"));
        switch (code) {
            case ORDER:
                orderHandler(remoteMessage);
                break;
            case CHAT:
                chatHandler(remoteMessage);
                break;


        }
    }

    private void orderHandler(RemoteMessage remoteMessage) {

        int code = Integer.parseInt(remoteMessage.getData().get("response"));
        Bundle data = new Bundle();
        data.putInt("code", code);
        intentToOrder(data);
        switch (code) {
            case REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                break;
            case CANCEL:
                Log.e("DRIVER RESPONSE", "cancel");
                break;
            case ACCEPT:
                Log.e("DRIVER RESPONSE", "accept");
                break;
            case START:
                Log.e("DRIVER RESPONSE", "start");
                break;
            case FINISH:
                Log.e("DRIVER RESPONSE", "finish");
                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
//                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Your trip is finished", Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(getApplicationContext(), RateDriverActivity.class);
                intent.putExtra("id_transaksi", remoteMessage.getData().get("id_transaksi"));
                intent.putExtra("id_pelanggan", remoteMessage.getData().get("id_pelanggan"));
                intent.putExtra("driver_photo", remoteMessage.getData().get("driver_foto"));
                intent.putExtra("id_driver", remoteMessage.getData().get("id_driver"));
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                break;
        }
    }

    private void chatHandler(RemoteMessage remoteMessage) {
        Log.e("INCOMING CHAT", remoteMessage.getData().toString());
        playSound();

        regIdDriver = remoteMessage.getData().get("reg_id_tujuan");
        Bundle data = bundlingChat(remoteMessage.getData());
        notificationChatBuilder(data, remoteMessage.getData().get("nama_tujuan"), remoteMessage.getData().get("isi_chat"));
        new Queries(new DBHandler(getApplicationContext())).insertChat((Chat) data.getSerializable("chat"));

        intentToChat(data);
    }

    private Bundle bundlingChat(Map<String, String> remMsg) {
        Bundle bund = new Bundle();
        Chat chat = new Chat();
        chat.id_tujuan = remMsg.get("id_tujuan");
        chat.reg_id_tujuan = remMsg.get("reg_id_tujuan");
        chat.isi_chat = remMsg.get("isi_chat");
        chat.chat_from = Integer.parseInt(remMsg.get("chat_from"));
        chat.nama_tujuan = remMsg.get("nama_tujuan");
        chat.status = 1;
        chat.type = Integer.parseInt(remMsg.get("type"));
        chat.waktu = remMsg.get("waktu");

        bund.putSerializable("chat", chat);
        return bund;
    }


    private void intentToChat(Bundle bundle) {
        intent.putExtras(bundle);
        sendBroadcast(intent);

        if (!isForeground("com.gumcode.mangjek.home.ChatActivity")) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("reg_id", regIdDriver);
            startActivity(intent);
        }
    }

    private void intentToOrder(Bundle bundle) {
        intentOrder.putExtras(bundle);
        sendBroadcast(intentOrder);
    }


    private void notificationChatBuilder(Bundle bundle, String nama, String message) {
        Intent intent1 = new Intent(this, ChatActivity.class);
        intent1.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtras(bundle);

        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
        Notification n = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(this)
                    .setContentTitle(nama)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_motor)
                    .setContentIntent(pIntent1)
                    .setAutoCancel(true).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);
    }

    private void playSound() {
        MediaPlayer BG = MediaPlayer.create(getBaseContext(), R.raw.notification);
        BG.setLooping(false);
        BG.setVolume(100, 100);
        BG.start();
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.e("ACTIVITY", componentInfo.getClassName());
        return componentInfo.getClassName().equals(myPackage);
    }

    public boolean isMainActivityRunning(String packageName) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < tasksInfo.size(); i++) {
            if (tasksInfo.get(i).baseActivity.getPackageName().toString().equals(packageName)) {
                return true;
            }
        }

        return false;
    }

}
