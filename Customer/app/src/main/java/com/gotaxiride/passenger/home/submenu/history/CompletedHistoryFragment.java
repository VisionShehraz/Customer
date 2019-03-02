package com.gotaxiride.passenger.home.submenu.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.adapter.HistoryAdapter;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.model.ItemHistory;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.menu.HistoryRequestJson;
import com.gotaxiride.passenger.model.json.menu.HistoryResponseJson;
import com.gotaxiride.passenger.utils.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompletedHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(R.id.completed_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.no_history_img)
    ImageView history_img;
    @BindView(R.id.no_history_text)
    TextView history_text;
    @BindView(R.id.no_history_textsub)
    TextView history_textsub;

    HistoryAdapter historyAdapter;

    public CompletedHistoryFragment() {
    }

    public static CompletedHistoryFragment newInstance() {
        CompletedHistoryFragment fragment = new CompletedHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        requestData();

    }

    private void requestData() {
        User user = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getCompleteHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;
                    history_img.setVisibility(View.GONE);
                    history_text.setVisibility(View.GONE);
                    history_textsub.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    for (int i = 0; i < data.size(); i++) {
                        switch (data.get(i).order_fitur) {
                            case "Go-Moto":
                                data.get(i).image_id = R.drawable.ride;
                                break;
                            case "Go-Cab":
                                data.get(i).image_id = R.drawable.car;
                                break;
                            case "Go-Send":
                                data.get(i).image_id = R.drawable.send;
                                break;
                            case "Go-Mart":
                                data.get(i).image_id = R.drawable.mart;
                                break;
                            case "Go-Box":
                                data.get(i).image_id = R.drawable.ic_mbox;
                                break;
                            case "GO-Service":
                                data.get(i).image_id = R.drawable.ic_mservice;
                                break;
                            case "Go-Massage":
                                data.get(i).image_id = R.drawable.massage;
                                break;
                            case "Go-Food":
                                data.get(i).image_id = R.drawable.ic_mfood;
                                break;

                            default:
                                data.get(i).image_id = R.drawable.car;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, true);
                    recyclerView.setAdapter(historyAdapter);
                    if (response.body().data.size() == 0) {
                        history_img.setVisibility(View.VISIBLE);
                        history_text.setVisibility(View.VISIBLE);
                        history_textsub.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Log.d("HISTORY", "Empty");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("System error:", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }


    @Override
    public void onRefresh() {
        requestData();
    }


}
