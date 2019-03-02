package com.gotaxiride.passenger.home.submenu.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 10/30/2018.
 */

public class HelpFragment extends Fragment {

    @BindView(R.id.help_recyclerView)
    RecyclerView recyclerView;

    private FastItemAdapter<HelpItem> adapter;

    private List<HelpItem> helpItemList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        context = getContext();

        helpItemList = new ArrayList<>();
        helpItemList.add(new HelpItem(R.drawable.car, R.string.home_mCar));
        helpItemList.add(new HelpItem(R.drawable.ride, R.string.home_mRide));
        helpItemList.add(new HelpItem(R.drawable.send, R.string.home_mSend));
        helpItemList.add(new HelpItem(R.drawable.box, R.string.home_mBox));
        helpItemList.add(new HelpItem(R.drawable.massage, R.string.home_mMassage));
        helpItemList.add(new HelpItem(R.drawable.food, R.string.home_mFood));
        helpItemList.add(new HelpItem(R.drawable.mart, R.string.home_mMart));
        helpItemList.add(new HelpItem(R.drawable.auto, R.string.home_mService));


        adapter = new FastItemAdapter<>();
        adapter.add(helpItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent helpIntent = new Intent(context, HelpActivity.class);
                helpIntent.putExtra("id", position);
                startActivity(helpIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));


    }
}
