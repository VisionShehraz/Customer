package com.gotaxiride.driver.adapter;

/**
 * Created by GagahIB on 11/8/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gotaxiride.driver.R;
import com.gotaxiride.driver.model.TransactionHistory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    ArrayList<TransactionHistory> prodList = new ArrayList<>();
    Context context;
    private ItemListener.OnItemTouchListener onItemTouchListener;

    public TransactionHistoryAdapter(ArrayList<TransactionHistory> prodList, ItemListener.OnItemTouchListener onItemTouchListener, Context context) {
        this.prodList = prodList;
        this.onItemTouchListener = onItemTouchListener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tanggalRT.setText(tanggalAdapter(prodList.get(position).waktu_riwayat));
        holder.saldoAkhirRT.setText("ENDING BALANCE " + amountAdapter(prodList.get(position).saldo));
        switch (prodList.get(position).tipe_transaksi) {
            case "4": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction: Top-Up Balance");
                holder.keterangan.setText("Balance topup succeeded");
                holder.nominalRT.setText(amountAdapter(prodList.get(position).kredit));
                break;
            }
            case "5": {
                holder.idRT.setText("ID " + prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaction : Order " + prodList.get(position).fitur);
                holder.keterangan.setText("User " + prodList.get(position).nama_depan + "\n" +
                        "Distance " + convertJarak(Double.parseDouble(prodList.get(position).jarak)));
                holder.nominalRT.setText("-" + amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorRed));
                break;
            }
            case "6": {
                holder.idRT.setText(prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaction: Order " + prodList.get(position).fitur);
                holder.keterangan.setText("receive payment");
                holder.nominalRT.setText("+" + amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "7": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction: Accept Bonus");
                holder.keterangan.setText("Accept Bonus from GoTaxi Company");
                holder.nominalRT.setText("+" + amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "8": {
                holder.idRT.setText(prodList.get(position).id_transaksi);
                holder.namaRT.setText("Transaction : Order " + prodList.get(position).fitur);
                holder.keterangan.setText("Accept Tips from " + prodList.get(position).nama_depan);
                holder.nominalRT.setText("+" + amountAdapter(prodList.get(position).kredit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "9": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction: penalty");
                holder.keterangan.setText(prodList.get(position).keterangan);
                holder.nominalRT.setText("-" + amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorRed));
                break;
            }
            case "10": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction : Withdraw");
                holder.keterangan.setText("Withdraw money from account is successful");
                holder.nominalRT.setText("-" + amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            }

            case "11": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction: Accept Bonus");
                holder.keterangan.setText("Accept Bonus from GoTaxi Company");
                holder.nominalRT.setText("+" + amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            case "12": {
                holder.idRT.setVisibility(View.GONE);
                holder.namaRT.setText("Transaction: Fine");
                holder.keterangan.setText(prodList.get(position).keterangan);
                holder.nominalRT.setText("+" + amountAdapter(prodList.get(position).debit));
                holder.nominalRT.setTextColor(context.getResources().getColor(R.color.textColorGreen));
                break;
            }
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return prodList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String tanggalAdapter(String tgl) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(Long.parseLong(tgl) * 1000));
    }

    private String amountAdapter(int amo) {
        return "$ " + NumberFormat.getNumberInstance(Locale.US).format(amo) + ".00";
    }

    private String convertJarak(Double jarak) {
        int range = (int) (jarak * 10);
        jarak = (double) range / 10;
        return String.valueOf(jarak) + " KM";
    }

    private long timeAdapter(long timestamp) {
        return (timestamp * 1000);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tanggalRT;
        public TextView idRT;
        public TextView namaRT;
        public TextView keterangan;
        public TextView nominalRT;
        public TextView saldoAkhirRT;

        public MyViewHolder(View itemView) {
            super(itemView);
            tanggalRT = (TextView) itemView.findViewById(R.id.tanggalRT);
            idRT = (TextView) itemView.findViewById(R.id.idRT);
            namaRT = (TextView) itemView.findViewById(R.id.namaRT);
            keterangan = (TextView) itemView.findViewById(R.id.keterangRT);
            nominalRT = (TextView) itemView.findViewById(R.id.nominalRT);
            saldoAkhirRT = (TextView) itemView.findViewById(R.id.saldoAkhirRT);
            saldoAkhirRT = (TextView) itemView.findViewById(R.id.saldoAkhirRT);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                }
            });

        }
    }
}