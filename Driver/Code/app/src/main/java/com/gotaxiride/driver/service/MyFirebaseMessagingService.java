package com.gotaxiride.driver.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.activity.ChatActivity;
import com.gotaxiride.driver.activity.NotificationActivity;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Chat;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.model.Transaksi;
import com.gotaxiride.driver.network.Log;

import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String BROADCAST_ACTION = "net.gumcode.drivermangjek";
    public static final String BROADCAST_ACTION_ORDER = "intent.order";
    private static final String TAG = "MyFMService";
    private final Handler handler = new Handler();
    Bundle bundChat;
    Intent intentChat, intentOrder;

    @Override
    public void onCreate() {
        super.onCreate();
        intentChat = new Intent(BROADCAST_ACTION);
        intentOrder = new Intent(BROADCAST_ACTION_ORDER);
    }

//    private Runnable sendUpdatesToUI = new Runnable() {
//        public void run() {
//            bundChat = new Bundle();
//            intentToChat(bundChat);
//            handler.postDelayed(this, 5000); // 5 seconds
//        }
//    };

//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//        handler.removeCallbacks(sendUpdatesToUI);
//        handler.postDelayed(sendUpdatesToUI, 1000);
//
//        Log.d("Service Started", "yes");
//    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
//       Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
//       Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
//       Log.d(TAG, "FCM Data From: " + remoteMessage.getFrom());

//        justBuilder();
        if (!remoteMessage.getData().isEmpty()) {

//           justBuilder();
            Queries que;
            switch (remoteMessage.getData().get("type")) {
                case "1": {
                    que = new Queries(new DBHandler(this));
                    Driver driver = que.getDriver();
                    String resp = remoteMessage.getData().get("response");
                    if (resp != null) {
                        Transaksi theTrans = que.getInProgressTransaksi();
                        String idT;
                        if (theTrans.id_transaksi == null) {
                            idT = "0";
                        } else {
                            idT = theTrans.id_transaksi;
                        }
                        if (idT.equals(remoteMessage.getData().get("id_transaksi"))) {
                            playSound();
                            Bundle bun = new Bundle();
                            bun.putInt("response", 2);
                            bun.putString("id_transaksi", remoteMessage.getData().get("id_transaksi"));
                            bun.putString("SOURCE", MyConfig.dashFragment);
                            que.updateStatus(1);
                            que.truncate(DBHandler.TABLE_IN_PROGRESS_TRANSAKSI);
                            que.truncate(DBHandler.TABLE_CHAT);
                            que.truncate(DBHandler.TABLE_MAKANAN_BELANJA);
                            que.truncate(DBHandler.TABLE_BARANG_BELANJA);
                            que.truncate(DBHandler.TABLE_DESTINASI_MBOX);
                            intentCancel(bun);
                        }

                    } else {
                        if (driver.status != 1) {
                        } else {
//                           playSound();
                            Bundle data = bundlingData(remoteMessage.getData(), driver);
//                           if(remoteMessage.getData().get("order_fitur").equals("3")){
//                               justBuilder(remoteMessage.getData().get("nama_pelanggan"));
//                           }else{
                            intentToOrder(data);
                            notificationBuilder(data, remoteMessage.getData().get("nama_pelanggan"));
//                           }
                        }
                    }
                    que.closeDatabase();
                    break;
                }
                case "2": {
                    que = new Queries(new DBHandler(this));
                    Bundle data = bundlingChat(remoteMessage.getData());
                    que.insertChat((Chat) data.getSerializable("chat"));
//                   notificationChatBuilder(data, remoteMessage.getData().get("nama_tujuan"), remoteMessage.getData().get("isi_chat"));
                    playSound();
                    intentToChat(data);
                    que.closeDatabase();
                    break;
                }
                default:
                    break;
            }
        }
//        if (remoteMessage.getNotification() != null) {
//            playSound();
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
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
        chat.type = remMsg.get("type");
        chat.waktu = remMsg.get("waktu");

        bund.putSerializable("chat", chat);
        return bund;
    }

    private Bundle bundlingData(Map<String, String> remMsg, Driver driver) {
//        Log.d("data_driver", driver.id+" "+new UserPreference(this).getDriver().longitude);
        Bundle bund = new Bundle();
        String fitur = remMsg.get("order_fitur");
        if (fitur == null) {
        } else {
            switch (fitur) {
                case "1": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = remMsg.get("end_latitude");
                    transaksi.end_longitude = remMsg.get("end_longitude");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.time_accept = remMsg.get("time_accept");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "2": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = remMsg.get("end_latitude");
                    transaksi.end_longitude = remMsg.get("end_longitude");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.time_accept = remMsg.get("time_accept");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "3": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.time_accept = remMsg.get("time_accept");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "4": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.time_accept = remMsg.get("time_accept");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "5": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = remMsg.get("end_latitude");
                    transaksi.end_longitude = remMsg.get("end_longitude");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.nama_barang = remMsg.get("nama_barang");
                    transaksi.time_accept = remMsg.get("time_accept");
                    transaksi.nama_pengirim = remMsg.get("nama_pengirim");
                    transaksi.nama_penerima = remMsg.get("nama_penerima");
                    transaksi.telepon_pengirim = remMsg.get("telepon_pengirim");
                    transaksi.telepon_penerima = remMsg.get("telepon_penerima");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "6": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = remMsg.get("end_latitude");
                    transaksi.end_longitude = remMsg.get("end_longitude");
                    transaksi.jarak = String.valueOf(0);
                    transaksi.harga = remMsg.get("harga");
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.kota = remMsg.get("kota");
                    transaksi.tanggal_pelayanan = remMsg.get("tanggal_pelayanan");
                    transaksi.massage_menu = remMsg.get("massage_menu");
                    transaksi.jam_pelayanan = remMsg.get("jam_pelayanan");
                    transaksi.lama_pelayanan = remMsg.get("lama_pelayanan");
                    transaksi.pelanggan_gender = remMsg.get("pelanggan_gender");
                    transaksi.prefer_gender = remMsg.get("prefer_gender");
                    transaksi.catatan_tambahan = remMsg.get("catatan_tambahan");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "7": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = remMsg.get("end_latitude");
                    transaksi.end_longitude = remMsg.get("end_longitude");
                    transaksi.jarak = remMsg.get("jarak");
                    transaksi.harga = remMsg.get("harga");
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.alamat_tujuan = remMsg.get("alamat_tujuan");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.nama_barang = remMsg.get("nama_barang");
                    transaksi.time_accept = remMsg.get("time_accept");
                    transaksi.nama_pengirim = remMsg.get("nama_pengirim");
                    transaksi.nama_penerima = remMsg.get("nama_penerima");
                    transaksi.telepon_pengirim = remMsg.get("telepon_pengirim");
                    transaksi.telepon_penerima = remMsg.get("telepon_penerima");
//                    transaksi.asuransi = remMsg.get("asuransi");
//                    transaksi.shipper = remMsg.get("shipper");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                case "8": {
                    Transaksi transaksi = new Transaksi();
                    transaksi.id_transaksi = remMsg.get("id_transaksi");
                    transaksi.id_pelanggan = remMsg.get("id_pelanggan");
                    transaksi.reg_id_pelanggan = remMsg.get("reg_id_pelanggan");
                    transaksi.order_fitur = remMsg.get("order_fitur");
                    transaksi.start_latitude = remMsg.get("start_latitude");
                    transaksi.start_longitude = remMsg.get("start_longitude");
                    transaksi.end_latitude = "0";
                    transaksi.end_longitude = "0";
                    transaksi.harga = remMsg.get("harga");
                    transaksi.jarak = String.valueOf(0);
                    transaksi.waktu_order = remMsg.get("waktu_order");
                    transaksi.waktu_selesai = remMsg.get("waktu_selesai");
                    transaksi.alamat_asal = remMsg.get("alamat_asal");
                    transaksi.rate = remMsg.get("rate");
                    transaksi.kredit_promo = remMsg.get("kredit_promo");
                    transaksi.biaya_akhir = String.valueOf(Integer.parseInt(remMsg.get("harga")) + Integer.parseInt(remMsg.get("kredit_promo")));
                    transaksi.kode_promo = remMsg.get("kode_promo");
                    transaksi.pakai_mpay = remMsg.get("pakai_mpay");
                    transaksi.nama_pelanggan = remMsg.get("nama_pelanggan");
                    transaksi.telepon_pelanggan = remMsg.get("telepon");
                    transaksi.time_accept = remMsg.get("time_accept");
                    transaksi.quantity = remMsg.get("quantity");
                    transaksi.residential_type = remMsg.get("residential_type");
                    transaksi.problem = remMsg.get("problem");
                    transaksi.jenis_service = remMsg.get("jenis_service");
                    transaksi.ac_type = remMsg.get("ac_type");
                    bund.putSerializable("data_order", transaksi);
                    bund.putInt("order_fitur", Integer.parseInt(fitur));
                    break;
                }
                default:
                    break;
            }
        }
        return bund;
    }

    private void intentToChat(Bundle bundle) {
        intentChat.putExtras(bundle);
        sendBroadcast(intentChat);

        if (!isForeground("net.gumcode.drivermangjek.activity.ChatActivity")) {
            Chat chat = (Chat) bundle.getSerializable("chat");
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("reg_id", chat.reg_id_tujuan);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void intentStatus(Bundle bundle) {
        intentOrder.putExtras(bundle);
        sendBroadcast(intentOrder);
    }

    private void intentCancel(Bundle bundle) {
        bundle.putInt("response", 2);
        Intent toMain = new Intent(getBaseContext(), MainActivity.class);
        toMain.putExtras(bundle);
        toMain.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMain);
    }

    private void intentToOrder(Bundle bundle) {
        Intent toOrder = new Intent(getBaseContext(), NotificationActivity.class);
        toOrder.putExtras(bundle);
        toOrder.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toOrder);
    }

    private void justBuilder(String nama) {
        Intent intent1 = new Intent(this, NotificationActivity.class);
        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
        Notification n = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(this)
                    .setContentTitle("1 order diterima")
                    .setContentText("Dari " + nama)
                    .setSmallIcon(R.drawable.ic_motor_notif)
                    .setAutoCancel(true).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, n);
    }

    private void notificationBuilder(Bundle bundle, String nama) {
        Intent intent1 = new Intent(this, NotificationActivity.class);
        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtras(bundle);

        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
        Notification n = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(this)
                    .setContentTitle("1 order diterima")
                    .setContentText("Dari " + nama)
                    .setSmallIcon(R.drawable.ic_motor_notif)
                    .setContentIntent(pIntent1)
                    .setAutoCancel(true).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);
    }

    private void notificationChatBuilder(Bundle bundle, String nama, String isi) {
        Intent intent1 = new Intent(this, ChatActivity.class);
        intent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtras(bundle);

        PendingIntent pIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
        Notification n = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            n = new Notification.Builder(this)
                    .setContentTitle(nama)
                    .setContentText(isi)
                    .setSmallIcon(R.drawable.ic_motor_notif)
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

        Vibrator v = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        v.vibrate(2000);
    }

    private void getTransaksiMmart() {

    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.e("ACTIVITY", componentInfo.getClassName());
        return componentInfo.getClassName().equals(myPackage);
    }
}