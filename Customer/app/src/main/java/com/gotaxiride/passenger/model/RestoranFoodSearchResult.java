package com.gotaxiride.passenger.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import com.gotaxiride.passenger.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fathony on 23/01/2017.
 */

public class RestoranFoodSearchResult extends AbstractItem<RestoranFoodSearchResult, RestoranFoodSearchResult.ViewHolder> {

    @Expose
    @SerializedName("id_restoran")
    private int idRestoran;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("kategori")
    private String kategori;

    @Expose
    @SerializedName("distance")
    private double distance;

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public int getIdRestoran() {
        return idRestoran;
    }

    public void setIdRestoran(int idRestoran) {
        this.idRestoran = idRestoran;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int getType() {
        return R.id.restoranFoodSearchResult;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_restoran_food_search_result;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.content.setText(toTitleCase(nama));
        holder.description.setText(toTitleCase(kategori));
        holder.distance.setText(String.format(Locale.US, "%.2f km", distance));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.restoranFoodSearchResult_content)
        TextView content;

        @BindView(R.id.restoranFoodSearchResult_description)
        TextView description;

        @BindView(R.id.restoranFoodSearchResult_distance)
        TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
