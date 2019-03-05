package com.letsride.passenger.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.letsride.passenger.R;
import com.letsride.passenger.utils.Log;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 12/29/2017.
 */

public class FoodItemHome extends AbstractItem<FoodItemHome, FoodItemHome.ViewHolder> {

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
    public double latitude;
    public double longitude;
   // public boolean isRide;

    Context context;

    public FoodItemHome(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.nearme_item;
    }

    @Override
    public void bindView(FoodItemHome.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.nearmeResto.setText(namaResto);
        NumberFormat formatter = new DecimalFormat("#0.00");
        holder.nearmeInfo.setText( "OPEN " + jamBuka + " - " + jamTutup);
        holder.nearmeAddress.setText( formatter.format(distance)+" Km" + " - " + alamat);
        Picasso.with(context).load(fotoResto).fit().centerCrop().into(holder.nearmeIMG);

        boolean isActuallyOpen = false;

        if (isOpen) {
            isActuallyOpen = calculateJamBukaTutup();
        } else {
            isActuallyOpen = false;
        }

        holder.closed.setVisibility(isActuallyOpen ? View.GONE : View.VISIBLE);
        holder.partner.setVisibility(isMitra ? View.VISIBLE : View.GONE);
        //holder.ride.setVisibility(isRide ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_grocery;
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

       // @BindView(R.id.is_ride)
       // ImageView ride;

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