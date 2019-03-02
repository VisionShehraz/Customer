package com.gotaxiride.passenger.home.submenu.history.details.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import com.gotaxiride.passenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fathony on 24/02/2017.
 */

public class MMartItem extends AbstractItem<MMartItem, MMartItem.ViewHolder> {

    private String namaBarang;
    private long jumlah;

    public MMartItem(String namaBarang, long jumlah) {
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
    }

    @Override
    public int getType() {
        return R.id.itemMMart;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_mmart;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.namaBarang.setText(namaBarang);
        holder.qty.setText(String.valueOf(jumlah));
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemMMart_nama)
        TextView namaBarang;
        @BindView(R.id.itemMMart_qty)
        TextView qty;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ViewHolder.this, itemView);
        }
    }
}
