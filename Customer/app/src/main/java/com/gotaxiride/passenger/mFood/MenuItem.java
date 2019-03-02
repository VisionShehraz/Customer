package com.gotaxiride.passenger.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import com.gotaxiride.passenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 12/31/2018.
 */

public class MenuItem extends AbstractItem<MenuItem, MenuItem.ViewHolder> {

    public int idMenu;
    public String menuMakanan;
    Context context;

    public MenuItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.menu_item;
    }

    @Override
    public void bindView(MenuItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.menuText.setText(menuMakanan);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_foodmenu;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_text)
        TextView menuText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
