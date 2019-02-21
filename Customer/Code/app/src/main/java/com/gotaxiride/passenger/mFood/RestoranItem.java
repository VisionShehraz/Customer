package com.gotaxiride.passenger.mFood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.squareup.picasso.Picasso;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 08/29/2018.
 */

public class RestoranItem extends AbstractItem<RestoranItem, RestoranItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public int id;
    public String namaResto;
    public String alamat;
    public double distance;
    public String jamBuka;
    public String jamTutup;
    public String fotoResto;
    public boolean isOpen;
    public boolean isMitra;
    public String pictureUrl;
    private Context context;

    public RestoranItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.nearme_item;
    }


    @Override
    public void bindView(RestoranItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.nearmeResto.setText(namaResto);
        holder.nearmeAddress.setText(alamat);
        NumberFormat formatter = new DecimalFormat("#0.00");
        holder.nearmeInfo.setText("" + formatter.format(distance) + " KM | OPEN " + jamBuka + " - " + jamTutup);
        Picasso.with(context).load(fotoResto).fit().centerCrop().into(holder.nearmeIMG);

        boolean isActuallyOpen = false;

        if (isOpen) {
            isActuallyOpen = calculateJamBukaTutup();
        } else {
            isActuallyOpen = false;
        }

        holder.closed.setVisibility(isActuallyOpen ? View.GONE : View.VISIBLE);
        holder.partner.setVisibility(isMitra ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_nearme;
    }

    private boolean calculateJamBukaTutup() {
        String[] parsedJamBuka = jamBuka.split(":");
        String[] parsedJamTutup = jamTutup.split(":");

        int jamBuka = Integer.parseInt(parsedJamBuka[0]), menitBuka = Integer.parseInt(parsedJamBuka[1]);
        int jamTutup = Integer.parseInt(parsedJamTutup[0]), menitTutup = Integer.parseInt(parsedJamTutup[1]);

        int totalMenitBuka = (jamBuka * 60) + menitBuka;
        int totalMenitTutup = (jamTutup * 60) + menitTutup;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        Log.d("RestoranItem", "NOW HOUR = " + now.get(Calendar.HOUR_OF_DAY) + " Now Minutes ");

        Log.d("RestoranItem", "totalMenitBuka = " + totalMenitBuka + ", totalMenitTutup = " + totalMenitTutup + ", totalMenitNow = " + totalMenitNow);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nearme_img)
        ImageView nearmeIMG;

        @BindView(R.id.nearme_resto)
        TextView nearmeResto;

        @BindView(R.id.nearme_address)
        TextView nearmeAddress;

        @BindView(R.id.nearme_info)
        TextView nearmeInfo;


        @BindView(R.id.nearme_closed)
        TextView closed;

        @BindView(R.id.nearme_mitra)
        TextView partner;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

}
