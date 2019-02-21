package com.gotaxiride.driver.adapter;

/**
 * Created by GagahIB on 27/11/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gotaxiride.driver.R;
import com.gotaxiride.driver.model.Feedback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import at.blogc.android.views.ExpandableTextView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {

    ArrayList<Feedback> prodList = new ArrayList<>();
    private ItemListener.OnItemTouchListener onItemTouchListener;

    public FeedbackAdapter(ArrayList<Feedback> prodList, ItemListener.OnItemTouchListener onItemTouchListener) {
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_feedback, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        timeConverter(holder, prodList.get(position).waktu);
        holder.catatan.setText(prodList.get(position).catatan);
    }

    private void timeConverter(MyViewHolder holder, long waktu) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        Date timing = new Date(waktu * 1000);
        holder.waktu.setText(sdf.format(timing));
    }

    @Override
    public int getItemCount() {
        return prodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ExpandableTextView catatan;
        public TextView waktu;
        public ImageView more;
        LinearLayout butExpanding;

        public MyViewHolder(View itemView) {
            super(itemView);

            catatan = (ExpandableTextView) itemView.findViewById(R.id.isiFeedback);
            waktu = (TextView) itemView.findViewById(R.id.waktuFeedback);
            butExpanding = (LinearLayout) itemView.findViewById(R.id.butExpanding);
            more = (ImageView) itemView.findViewById(R.id.expandBut);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());

                }


            });

            butExpanding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemTouchListener.onButton1Click(view, getLayoutPosition());
                }
            });

        }


    }


}