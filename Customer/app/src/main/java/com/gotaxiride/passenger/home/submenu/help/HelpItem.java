package com.gotaxiride.passenger.home.submenu.help;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import com.gotaxiride.passenger.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 10/30/2018.
 */

public class HelpItem extends AbstractItem<HelpItem, HelpItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @DrawableRes
    private int iconRes;

    @StringRes
    private int nameRes;


    public HelpItem(int iconRes, int nameRes) {
        this.iconRes = iconRes;
        this.nameRes = nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    @Override
    public int getType() {
        return R.id.itemHelp_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_help;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.iconView.setImageResource(iconRes);
        holder.titleView.setText(nameRes);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemHelp_icon)
        ImageView iconView;

        @BindView(R.id.itemHelp_title)
        TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ViewHolder.this, itemView);

        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }


}
