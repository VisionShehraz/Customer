package com.gotaxiride.driver.adapter;

/**
 * Created by GagahIB on 27/11/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gotaxiride.driver.model.ItemShopping;
import com.gotaxiride.driver.R;

import java.util.ArrayList;

public class ItemShoppingAdapter extends RecyclerView.Adapter<ItemShoppingAdapter.MyViewHolder> {

    ArrayList<ItemShopping> prodList = new ArrayList<>();
    private ItemListener.OnItemTouchListener onItemTouchListener;

    public ItemShoppingAdapter(ArrayList<ItemShopping> prodList, ItemListener.OnItemTouchListener onItemTouchListener) {
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_barang, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.jumlahBarang.setText(prodList.get(position).jumlah_barang);
        holder.namaBarang.setText(prodList.get(position).nama_barang);
//        if(prodList.get(position).isChecked == 1){
//            holder.isChecked.setChecked(true);
//        }else{
//            holder.isChecked.setChecked(false);
//        }
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

        public TextView jumlahBarang, namaBarang;
        public CheckBox isChecked;

        public MyViewHolder(View itemView) {
            super(itemView);

            jumlahBarang = (TextView) itemView.findViewById(R.id.jumlahBarang);
            namaBarang = (TextView) itemView.findViewById(R.id.namaBarang);
//            isChecked = (CheckBox) itemView.findViewById(R.id.cekBarang);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());

                }


            });

        }


    }


}