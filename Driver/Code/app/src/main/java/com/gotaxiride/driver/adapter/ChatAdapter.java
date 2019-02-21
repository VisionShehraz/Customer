package com.gotaxiride.driver.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.gotaxiride.driver.model.Chat;
import com.gotaxiride.driver.service.MyConfig;
import com.gotaxiride.driver.R;

import java.util.ArrayList;

/**
 * Created by GagahIB on 23/11/2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "NotificationAdapter";
    Context context;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private ArrayList<Chat> mDataSet;
    private int[] mDataSetTypes;

    public ChatAdapter(Context context, ArrayList<Chat> dataSet, int[] dataSetTypes, ItemListener.OnItemTouchListener onItemTouchListener) {
        this.context = context;
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
        this.onItemTouchListener = onItemTouchListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == MyConfig.chatMe) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_chat_me, viewGroup, false);

            return new FromMeViewHolder(v);

        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_chat_you, viewGroup, false);
            return new FromYouViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == MyConfig.chatMe) {
            FromMeViewHolder holder = (FromMeViewHolder) viewHolder;
            holder.isiChat.setText(mDataSet.get(position).isi_chat);
            holder.waktuChat.setText(mDataSet.get(position).waktu);
            holder.status.setImageResource(statusAdapter(mDataSet.get(position).status));

        } else {
            FromYouViewHolder holder = (FromYouViewHolder) viewHolder;
            holder.isiChat.setText(mDataSet.get(position).isi_chat);
            holder.waktuChat.setText(mDataSet.get(position).waktu);
            imageAdapter(holder.image, mDataSet.get(position).nama_tujuan);
        }
    }

    private int statusAdapter(int status) {
        if (status == 0) {
            return R.drawable.ic_status_sent;
        } else {
            return R.drawable.ic_status_deliver;
        }
    }

    private void imageAdapter(ImageView image, String name) {
        TextDrawable drawable1 = TextDrawable.builder()
                .buildRound(name.substring(0, 1).toUpperCase(), R.color.brown);
        image.setImageDrawable(drawable1);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class FromMeViewHolder extends ViewHolder {
        TextView isiChat, waktuChat;
        ImageView image, status;

        public FromMeViewHolder(View v) {
            super(v);
            this.isiChat = (TextView) v.findViewById(R.id.isiChat);
            this.waktuChat = (TextView) v.findViewById(R.id.timeChat);
            this.image = (ImageView) v.findViewById(R.id.imageUser);
            this.status = (ImageView) v.findViewById(R.id.statusChat);
        }
    }

    public class FromYouViewHolder extends ViewHolder {
        TextView isiChat, waktuChat;
        ImageView image;

        public FromYouViewHolder(View v) {
            super(v);
            this.isiChat = (TextView) v.findViewById(R.id.isiChat);
            this.waktuChat = (TextView) v.findViewById(R.id.timeChat);
            this.image = (ImageView) v.findViewById(R.id.imageUser);
        }
    }
}