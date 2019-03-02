package com.gotaxiride.passenger.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.items.AbstractItem;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.utils.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Androgo on 1/3/2017.
 */

public class MakananItem extends AbstractItem<MakananItem, MakananItem.ViewHolder> {

    public int id;
    public String namaMenu;
    public String deskripsiMenu;
    public long harga;
    public long cost;
    public int quantity;
    public String catatan;
    public String foto;
    Context context;
    OnCalculatePrice calculatePrice;
    private Realm realm;

    private TextWatcher catatanUpdater;

    public MakananItem(Context context, OnCalculatePrice calculatePrice) {
        this.context = context;
        this.calculatePrice = calculatePrice;

        catatanUpdater = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                catatan = s.toString();
                if (quantity > 0) UpdatePesanan(id, cost, quantity, catatan);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public int getType() {
        return R.id.makanan_item;
    }

    @Override
    public void bindView(final MakananItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        realm = GoTaxiApplication.getInstance(context).getRealmInstance();

        holder.makananText.setText(namaMenu);
        holder.deskripsiText.setText(deskripsiMenu);
        holder.hargaText.setText(getFormattedPrice(harga));
        holder.quantityText.setText(String.valueOf(quantity));
        holder.notesText.setEnabled(quantity > 0);
        holder.notesText.setText(catatan);
        Glide.with(holder.imageproduct.getContext()).load(foto).centerCrop().into(holder.imageproduct);


        holder.notesText.addTextChangedListener(catatanUpdater);

        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;

                holder.quantityText.setText("" + quantity);
                holder.notesText.setEnabled(true);
                CalculateCost();
                if (quantity == 1) {
                    AddPesanan(id, cost, quantity, catatan);
                } else if (quantity > 1) {
                    UpdatePesanan(id, cost, quantity, catatan);
                }

                if (calculatePrice != null) calculatePrice.calculatePrice();
            }
        });

        holder.removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity - 1 >= 0) {
                    quantity--;
                    holder.quantityText.setText(String.valueOf(quantity));
                    CalculateCost();
                    UpdatePesanan(id, cost, quantity, catatan);

                    if (quantity == 0) {
                        DeletePesanan(id);
                        holder.notesText.setText("");
                        holder.notesText.setEnabled(false);
                    }
                }

                if (calculatePrice != null) calculatePrice.calculatePrice();
            }
        });
    }

    private void CalculateCost() {
        cost = quantity * harga;
        //Log.e("Cost", cost+"");
    }

    private void AddPesanan(int idMakanan, long totalHarga, int qty, String notes) {
        PesananFood pesananfood = new PesananFood();
        pesananfood.setIdMakanan(idMakanan);
        pesananfood.setTotalHarga(totalHarga);
        pesananfood.setQty(qty);
        pesananfood.setCatatan(notes);
        realm.beginTransaction();
        realm.copyToRealm(pesananfood);
        realm.commitTransaction();

        Log.e("Added", idMakanan + "");
        Log.e("Added", qty + "");
    }

    private void UpdatePesanan(int idMakanan, long totalHarga, int qty, String notes) {
        realm.beginTransaction();
        PesananFood updateFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        updateFood.setTotalHarga(totalHarga);
        updateFood.setQty(qty);
        updateFood.setCatatan(notes);
        realm.copyToRealm(updateFood);
        realm.commitTransaction();

        Log.e("Updated", qty + "");
    }

    private void DeletePesanan(int idMakanan) {
        realm.beginTransaction();
        PesananFood deleteFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        deleteFood.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_makanan;
    }

    private String getFormattedPrice(long price) {
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(price);
        return String.format(Locale.US, General.MONEY +" %s .00", formattedTotal);
    }

    public interface OnCalculatePrice {
        void calculatePrice();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.makanan_text)
        TextView makananText;

        @BindView(R.id.deskripsi_text)
        TextView deskripsiText;

        @BindView(R.id.harga_text)
        TextView hargaText;

        @BindView(R.id.notes_text)
        EditText notesText;

        @BindView(R.id.add_quantity)
        TextView addQuantity;

        @BindView(R.id.quantity_text)
        TextView quantityText;

        @BindView(R.id.remove_quantity)
        TextView removeQuantity;

        @BindView(R.id.image_product)
        ImageView imageproduct;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
