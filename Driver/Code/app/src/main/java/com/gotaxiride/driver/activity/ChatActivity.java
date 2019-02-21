package com.gotaxiride.driver.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.gotaxiride.driver.R;
import com.gotaxiride.driver.adapter.ChatAdapter;
import com.gotaxiride.driver.adapter.ItemListener;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.Chat;
import com.gotaxiride.driver.model.Content;
import com.gotaxiride.driver.model.Driver;
import com.gotaxiride.driver.network.AsyncTaskHelperNoLoad;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.service.MyFirebaseMessagingService;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ChatActivity activity;
    ImageView image;
    ArrayList<Chat> arrChat;
    int mDatasetTypes[];
    Queries que;
    EditText inputText;
    ImageView butSend;
    Driver driver;
    Bundle recv;
    Intent newNotif;
    String regID = "";
    int status;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviChat;
    private ChatAdapter chatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            recv = intent.getExtras();
            Chat chatting = (Chat) recv.getSerializable("chat");
            regID = chatting.reg_id_tujuan;
            updateListView();
            removeNotif();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat);

        activity = ChatActivity.this;
        image = (ImageView) findViewById(R.id.imageUser);
        reviChat = (RecyclerView) findViewById(R.id.reviChat);
        reviChat.setHasFixedSize(true);

//        recv = getIntent().getExtras();
//        Log.d("isi_ntoif", recv.get("dataku").toString());

        inputText = (EditText) findViewById(R.id.inputText);
        butSend = (ImageView) findViewById(R.id.butSend);

        que = new Queries(new DBHandler(this));

//        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        if (regID == "") {
            regID = getIntent().getStringExtra("reg_id");
        }

        driver = que.getDriver();
        initList();

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isi = inputText.getText().toString();
                if (!isi.equals("")) {
                    Date date = new Date();
                    String waktu = date.getHours() + ":" + date.getMinutes();
                    Chat chat = new Chat();
                    chat.isi_chat = isi;
                    chat.reg_id_tujuan = driver.gcm_id;
                    chat.nama_tujuan = driver.name;
                    chat.chat_from = 1;
                    chat.id_tujuan = driver.id;
                    chat.waktu = waktu;
                    announceToUser(regID, chat);
                    inputText.setText("");
                    inputText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        removeNotif();
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private ArrayList<Chat> initDataDummy() {
        ArrayList<Chat> arrD = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Chat chat = new Chat();
            chat.id_tujuan = "id" + i;
            chat.nama_tujuan = i + "_nama";
            if (i % 2 == 0) {
                chat.status = 1;
                chat.chat_from = 1;
            } else {
                chat.status = 0;
                chat.chat_from = 0;
            }
            chat.isi_chat = "isi_chat " + i;
            chat.reg_id_tujuan = "reg_id" + i;
            arrD.add(chat);
        }
        return arrD;
    }

    private void setmDatasetTypesFiller() {
        mDatasetTypes = new int[arrChat.size()];

        for (int i = 0; i < arrChat.size(); i++) {
            mDatasetTypes[i] = arrChat.get(i).chat_from;
        }
    }

    private void initList() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, int position) {
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
        updateListView();
    }

    private void updateListView() {
//        arrChat = initDataDummy();
        arrChat = que.getAllChat();
        setmDatasetTypesFiller();

        mLayoutManager = new LinearLayoutManager(activity);
        reviChat.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(activity, arrChat, mDatasetTypes, onItemTouchListener);
        reviChat.setAdapter(chatAdapter);
        reviChat.scrollToPosition(arrChat.size() - 1);
    }

    private void notifyDataSetChanged() {
//        chatAdapter.notifyDataSetChanged();
    }

    private void announceToUser(String regIDTujuan, Chat chat) {

        Content content = new Content();
        content.addRegId(regIDTujuan);
        content.createDataChat(chat);
        sendResponseToPelanggan(content, chat);

        Chat newChat = chat;
        newChat.status = 1;
        newChat.chat_from = 0;
        que.insertChat(newChat);
        updateListView();
    }

    private void sendResponseToPelanggan(final Content content, final Chat chat) {

        AsyncTaskHelperNoLoad asyncTask = new AsyncTaskHelperNoLoad(activity, true);
        asyncTask.setAsyncTaskListener(new AsyncTaskHelperNoLoad.OnAsyncTaskListener() {
            @Override
            public void onAsyncTaskDoInBackground(AsyncTaskHelperNoLoad asyncTask) {
                status = HTTPHelper.sendToGCMServer(content);
            }

            @Override
            public void onAsyncTaskProgressUpdate(AsyncTaskHelperNoLoad asyncTask) {
            }

            @Override
            public void onAsyncTaskPostExecute(AsyncTaskHelperNoLoad asyncTask) {
                if (status == 1) {
//                    Chat newChat = chat;
//                    newChat.status = 1;
//                    newChat.chat_from = 0;
//                    que.insertChat(newChat);
                } else {
//                    Chat newChat = chat;
//                    newChat.status = 0;
//                    newChat.chat_from = 0;
//                    que.insertChat(newChat);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAsyncTaskPreExecute(AsyncTaskHelperNoLoad asyncTask) {
            }
        });
        asyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        que.closeDatabase();
//        unregisterReceiver(broadcastReceiver);
    }

    private void removeNotif() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}
