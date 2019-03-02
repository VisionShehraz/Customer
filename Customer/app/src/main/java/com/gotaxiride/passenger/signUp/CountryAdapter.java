package com.gotaxiride.passenger.signUp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.signUp.beans.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Androgo on 06 Sep 2018
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CustomViewHolder> {
    private List<Country> employees;
    private OnItemClickListener mItemClickListener;

    public CountryAdapter(List<Country> employees) {
        this.employees = employees;
    }
    public void updateList(ArrayList<Country> employees) {
        this.employees = employees;
        notifyDataSetChanged();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_list, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Country employee = employees.get(position);
        holder.employeeName.setText(employee.getName());
        holder.email.setText(employee.getDialCode());
        holder.designation.setText(employee.getCode());

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    private Country getItem(int position) {
        return employees.get(position);
    }


    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Country employee);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView employeeName, designation, email;

        public CustomViewHolder(View ItemView) {
            super(ItemView);
            employeeName = (TextView) ItemView.findViewById(R.id.employeeName);
            email = (TextView) ItemView.findViewById(R.id.email);
            designation = (TextView) ItemView.findViewById(R.id.designation);

            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(view, getAdapterPosition(), employees.get(getAdapterPosition()));

                }
            });

        }




    }
}
