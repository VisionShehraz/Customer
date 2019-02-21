package com.gotaxiride.driver.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gotaxiride.driver.MainActivity;
import com.gotaxiride.driver.R;
import com.gotaxiride.driver.adapter.ItemListener;
import com.gotaxiride.driver.adapter.TransactionHistoryAdapter;
import com.gotaxiride.driver.database.DBHandler;
import com.gotaxiride.driver.database.Queries;
import com.gotaxiride.driver.model.TransactionHistory;
import com.gotaxiride.driver.network.HTTPHelper;
import com.gotaxiride.driver.network.Log;
import com.gotaxiride.driver.network.NetworkActionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import net.gumcode.drivermangjek.preference.UserPreference;


public class TransactionHistoryFragment extends Fragment {
    private static final String TAG = TransactionHistoryFragment.class.getSimpleName();
    MainActivity activity;
    ArrayList<TransactionHistory> arrRiwayat;
    Queries que;
    int maxRetry = 4;
    private View rootView;
    private ItemListener.OnItemTouchListener onItemTouchListener;
    private RecyclerView reviRiwayat;
    private TransactionHistoryAdapter riwayatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipe;

    public TransactionHistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.riwayat_transaksi_fragments, container, false);

        activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.Transaction_History);

        que = new Queries(new DBHandler(activity));
        reviRiwayat = (RecyclerView) rootView.findViewById(R.id.reviRiwayat);
        reviRiwayat.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activity);

        arrRiwayat = que.getAllRiwayatTransaksi();
//        initData();
        initListener();
        updateListView();

        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeFeedback);
        swipe.setRefreshing(false);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        swipe.post(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });

        return rootView;
    }

    private void initData() {
        final ProgressDialog sl = showLoading();
        JSONObject jFeed = new JSONObject();
        try {
            jFeed.put("id", que.getDriver().id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPHelper.getInstance(activity).getRiwayatTransaksi(jFeed, new NetworkActionResult() {
            @Override
            public void onSuccess(JSONObject obj) {
                arrRiwayat = HTTPHelper.getInstance(activity).parseRiwayatTransaksi(obj);
                que.truncate(DBHandler.TABLE_RIWAYAT_TRANSAKSI);
                que.insertRiwayatTransaksi(arrRiwayat);
                swipe.setRefreshing(false);
                updateListView();
                sl.dismiss();
                maxRetry = 4;
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onError(String message) {
                if (maxRetry == 0) {
                    sl.dismiss();
                    Toast.makeText(activity, "Connection problem..", Toast.LENGTH_SHORT).show();
                    maxRetry = 4;
                    swipe.setRefreshing(false);
                } else {
                    initData();
                    maxRetry--;
                    Log.d("Try_ke_feedback", String.valueOf(maxRetry));
                    sl.dismiss();
                }
            }
        });
    }

    private void initListener() {
        onItemTouchListener = new ItemListener.OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
            }

            @Override
            public void onButton1Click(View view, int position) {
            }

            @Override
            public void onButton2Click(View view, int position) {
            }
        };
    }

    private void updateListView() {
        reviRiwayat.setLayoutManager(mLayoutManager);
        arrRiwayat = que.getAllRiwayatTransaksi();
        riwayatAdapter = new TransactionHistoryAdapter(arrRiwayat, onItemTouchListener, activity);
        reviRiwayat.setAdapter(riwayatAdapter);
        reviRiwayat.setVerticalScrollbarPosition(arrRiwayat.size() - 1);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        que.closeDatabase();
    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading data...", true);
        return ad;
    }

}
