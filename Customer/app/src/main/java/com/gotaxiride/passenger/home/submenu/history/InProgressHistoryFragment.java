package com.gotaxiride.passenger.home.submenu.history;


import android.content.Intent;
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
import com.gotaxiride.passenger.splash.SplashActivity;
import com.gotaxiride.passenger.utils.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InProgressHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(R.id.inProgress_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.no_history_img)
    ImageView history_img;
    @BindView(R.id.no_history_text)
    TextView history_text;
    @BindView(R.id.no_history_textsub)
    TextView history_textsub;

    HistoryAdapter historyAdapter;
    User user;

    public InProgressHistoryFragment() {
    }

    public static InProgressHistoryFragment newInstance() {
        InProgressHistoryFragment fragment = new InProgressHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_in_progress_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (GoTaxiApplication.getInstance(getActivity()).getLoginUser() != null) {
            user = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        } else {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }
        requestData();
    }


    private void requestData() {
        if (GoTaxiApplication.getInstance(getActivity()).getLoginUser() != null) {
            user = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        }
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getOnProgressHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;

                    history_img.setVisibility(View.GONE);
                    history_text.setVisibility(View.GONE);
                    history_textsub.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    Log.e("HISTORY", data.get(0).toString());

                    for (int i = 0; i < data.size(); i++) {
                        switch (data.get(i).order_fitur) {
                            case "Go-Moto":
                                data.get(i).image_id = R.drawable.ride;
                                break;
                            case "Go-Cab":
                                data.get(i).image_id = R.drawable.ic_mcar;
                                break;
                            case "Go-Send":
                                data.get(i).image_id = R.drawable.send;
                                break;
                            case "GO-Mart":
                                data.get(i).image_id = R.drawable.mart;
                                break;
                            case "Go-Box":
                                data.get(i).image_id = R.drawable.box;
                                break;
                            case "Go-Service":
                                data.get(i).image_id = R.drawable.auto;
                                break;
                            case "Go-Massage":
                                data.get(i).image_id = R.drawable.ic_mmassage;
                                break;
                            case "Go-Food":
                                data.get(i).image_id = R.drawable.food;
                                break;

                            default:
                                data.get(i).image_id = R.drawable.ic_mride;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, false);
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
