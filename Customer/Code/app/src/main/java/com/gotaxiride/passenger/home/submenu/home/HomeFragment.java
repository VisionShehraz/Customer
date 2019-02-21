package com.gotaxiride.passenger.home.submenu.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.adapter.MainAdapterHome;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.UserService;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.home.submenu.TopUpActivity;
import com.gotaxiride.passenger.mBox.BoxActivity;
import com.gotaxiride.passenger.mFood.FoodActivity;
import com.gotaxiride.passenger.mFood.FoodMenuActivity;
import com.gotaxiride.passenger.mFood.FoodItemHome;
import com.gotaxiride.passenger.mMart.MartActivity;
import com.gotaxiride.passenger.mMassage.MassageActivity;
import com.gotaxiride.passenger.mRideCar.RideCarActivity;
import com.gotaxiride.passenger.mSend.SendActivity;
import com.gotaxiride.passenger.mService.mServiceActivity;
import com.gotaxiride.passenger.model.Banner;
import com.gotaxiride.passenger.model.Fitur;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.model.Restoran;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.user.GetBannerResponseJson;
import com.gotaxiride.passenger.model.json.user.GetSaldoRequestJson;
import com.gotaxiride.passenger.model.json.user.GetSaldoResponseJson;
import com.gotaxiride.passenger.splash.SplashActivity;
import com.gotaxiride.passenger.utils.ConnectivityUtils;
import com.gotaxiride.passenger.utils.Log;
import com.gotaxiride.passenger.utils.RecyclerTouchListener;
import com.gotaxiride.passenger.utils.SnackbarController;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Androgo on 10/10/2018.
 */

public class HomeFragment extends Fragment {

    public ArrayList<Banner> banners = new ArrayList<>();

     @BindView(R.id.home_topUpButton)
    Button isisaldo;
    @BindView(R.id.home_mPayBalance)
    TextView mPayBalance;
    @BindView(R.id.loading)
    ProgressBar loading_pay;

    @BindView(R.id.promo_taxi)
    LinearLayout Promo_Taxi;
    @BindView(R.id.promo_gofood)
    LinearLayout Promo_Food;
    @BindView(R.id.text_nearme)
    TextView text_nearme;

    @BindView(R.id.slide_viewPager)
    AutoScrollViewPager slideViewPager;
    @BindView(R.id.slide_viewPager_indicator)
    CircleIndicator slideIndicator;
    private SnackbarController snackbarController;
    private boolean connectionAvailable;
    private boolean isDataLoaded = false;
    private Realm realm;
    private int successfulCall;
    boolean doubleBackToExitPressedOnce = false;
    @BindView(R.id.recyclerView_main_home)
    RecyclerView recyclerView_explore;
    private static final int REQUEST_PERMISSION_LOCATION = 1;

    @BindView(R.id.nearme_recycler)
    RecyclerView nearmeRecycler;
    private RealmResults<Restoran> restoranRealmResults;
    private FastItemAdapter<FoodItemHome> restoranAdapter;

    private String[] name = {

            General.Name_GOCAB,
            General.Name_GOMOTO,
            General.Name_GOSEND,
            General.Name_GOFOOD,
            General.Name_GOMART,
            General.Name_GOMASSAGE,
            General.Name_GOBOX,
            General.Name_GOSERVICE
    };

    private Integer[] image = {
            R.drawable.car,
            R.drawable.ride,
            R.drawable.send,
            R.drawable.food,
            R.drawable.mart,
            R.drawable.massage,
            R.drawable.box,
            R.drawable.auto

    };

    public MainAdapterHome mainAdapterHome;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SnackbarController) {
            snackbarController = (SnackbarController) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        connectionAvailable = false;
        isisaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTopUpClick();
            }
        });
        Promo_Taxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGoCarClick();
            }
        });
        Promo_Food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGoFoodClick();
            }
        });

        realm = GoTaxiApplication.getInstance(getActivity()).getRealmInstance();
        getImageBanner();

        recyclerView_explore.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView_explore.setLayoutManager(layoutManager);
        recyclerView_explore.setNestedScrollingEnabled(false);
        recyclerView_explore.setFocusable(false);
        mainAdapterHome = new MainAdapterHome(getActivity(), name, image);
        recyclerView_explore.setAdapter(mainAdapterHome);
        recyclerView_explore.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView_explore, new MainAdapterHome.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                navigation(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        restoranAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        nearmeRecycler.setNestedScrollingEnabled(false);
        nearmeRecycler.setAdapter(restoranAdapter);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<FoodItemHome>() {
            @Override
            public boolean onClick(View v, IAdapter<FoodItemHome> adapter, FoodItemHome item, int position) {
                Log.e("BUTTON", "CLICKED");
                Restoran selectedResto = realm.where(Restoran.class).
                        equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(getActivity(), FoodMenuActivity.class);
                intent.putExtra(FoodMenuActivity.ID_RESTO, selectedResto.getId());
                intent.putExtra(FoodMenuActivity.NAMA_RESTO, selectedResto.getNamaResto());
                intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, selectedResto.getAlamat());
                intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, selectedResto.getDistance());
                intent.putExtra(FoodMenuActivity.JAM_BUKA, selectedResto.getJamBuka());
                intent.putExtra(FoodMenuActivity.JAM_TUTUP, selectedResto.getJamTutup());
                intent.putExtra(FoodMenuActivity.IS_OPEN, selectedResto.isOpen());
                intent.putExtra(FoodMenuActivity.PICTURE_URL, selectedResto.getFotoResto());
                intent.putExtra(FoodMenuActivity.IS_MITRA, selectedResto.isPartner());
                startActivity(intent);
                return true;
            }
        });

        restoranRealmResults = realm.where(Restoran.class).findAll();
        FoodItemHome restoranItem;
        for (int i = 0; i < restoranRealmResults.size(); i++) {
            restoranItem = new FoodItemHome(getActivity());
            restoranItem.id = restoranRealmResults.get(i).getId();
            restoranItem.namaResto = restoranRealmResults.get(i).getNamaResto();
            restoranItem.alamat = restoranRealmResults.get(i).getAlamat();
            restoranItem.distance = restoranRealmResults.get(i).getDistance();
            restoranItem.jamBuka = restoranRealmResults.get(i).getJamBuka();
            restoranItem.jamTutup = restoranRealmResults.get(i).getJamTutup();
            restoranItem.fotoResto = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isOpen = restoranRealmResults.get(i).isOpen();
            restoranItem.pictureUrl = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isMitra = restoranRealmResults.get(i).isPartner();
            restoranAdapter.add(restoranItem);
            Log.e("RESTO", restoranItem.namaResto + "");
            Log.e("RESTO", restoranItem.alamat + "");
            Log.e("RESTO", restoranItem.jamBuka + "");
            Log.e("RESTO", restoranItem.jamTutup + "");
            Log.e("RESTO", restoranItem.fotoResto + "");
        }
    }

    private void getImageBanner() {
        User loginUser = new User();
        if (GoTaxiApplication.getInstance(getActivity()).getLoginUser() != null) {
            loginUser = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        } else {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }

        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getBanner().enqueue(new Callback<GetBannerResponseJson>() {
            @Override
            public void onResponse(Call<GetBannerResponseJson> call, Response<GetBannerResponseJson> response) {
                if (response.isSuccessful()) {
                    banners = response.body().data;
                    Log.e("Image", response.body().data.get(0).foto);
                    MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager(), banners);
                    slideViewPager.setAdapter(pagerAdapter);
                    slideIndicator.setViewPager(slideViewPager);
                    slideViewPager.setInterval(6000);
                    slideViewPager.startAutoScroll(6000);
                }
            }

            @Override
            public void onFailure(Call<GetBannerResponseJson> call, Throwable t) {

            }
        });

    }

    private void navigation(int position) {

        switch (position) {

            case 0:
                Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();
                Intent intent = new Intent(getActivity(), RideCarActivity.class);
                intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
                getActivity().startActivity(intent);
                break;

            case 1:
                Fitur selectedFitur1 = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
                Intent intent1 = new Intent(getActivity(), RideCarActivity.class);
                intent1.putExtra(RideCarActivity.FITUR_KEY, selectedFitur1.getIdFitur());
                getActivity().startActivity(intent1);
                break;

            case 2:
                Fitur selectedFitur2 = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
                Intent intent2 = new Intent(getActivity(), SendActivity.class);
                intent2.putExtra(SendActivity.FITUR_KEY, selectedFitur2.getIdFitur());
                getActivity().startActivity(intent2);
                break;

            case 3:
                Fitur selectedFitur3 = realm.where(Fitur.class).equalTo("idFitur", 3).findFirst();
                Intent intent3 = new Intent(getActivity(), FoodActivity.class);
                intent3.putExtra(FoodActivity.FITUR_KEY, selectedFitur3.getIdFitur());
                getActivity().startActivity(intent3);
                break;

            case 4:
                Fitur selectedFitur4 = realm.where(Fitur.class).equalTo("idFitur", 4).findFirst();
                Intent intent4 = new Intent(getActivity(), MartActivity.class);
                intent4.putExtra(MartActivity.FITUR_KEY, selectedFitur4.getIdFitur());
                getActivity().startActivity(intent4);

                break;

            case 5:
                Fitur selectedFitur6 = realm.where(Fitur.class).equalTo("idFitur", 6).findFirst();
                Intent intent6 = new Intent(getActivity(), MassageActivity.class);
                intent6.putExtra(mServiceActivity.FITUR_KEY, selectedFitur6.getIdFitur());
                getActivity().startActivity(intent6);
                break;

            case 6:
                Fitur selectedFiturbox = realm.where(Fitur.class).equalTo("idFitur", 7).findFirst();
                Intent intentbox = new Intent(getActivity(), BoxActivity.class);
                intentbox.putExtra(BoxActivity.FITUR_KEY, selectedFiturbox.getIdFitur());
                getActivity().startActivity(intentbox);
                break;

            case 7:
                Fitur selectedFitur8 = realm.where(Fitur.class).equalTo("idFitur", 8).findFirst();
                Intent intent8 = new Intent(getActivity(), mServiceActivity.class);
                intent8.putExtra(mServiceActivity.FITUR_KEY, selectedFitur8.getIdFitur());
                getActivity().startActivity(intent8);
                break;

            case 8:
                featurePro();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();
        successfulCall = 0;
        connectionAvailable = ConnectivityUtils.isConnected(getActivity());
        if (!connectionAvailable) {
            if (snackbarController != null) snackbarController.showSnackbar(
                    R.string.text_noInternet, Snackbar.LENGTH_INDEFINITE, R.string.text_close,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
        } else {
            updateMPayBalance();
            loading_pay.setVisibility(View.VISIBLE);
            mPayBalance.setVisibility(View.GONE);
        }
    }

    private void onTopUpClick() {
        Intent intent = new Intent(getActivity(), TopUpActivity.class);
        startActivity(intent);
    }

    private void onGoCarClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }


    private void onGoFoodClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 3).findFirst();
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void updateMPayBalance() {
        User loginUser = GoTaxiApplication.getInstance(getActivity()).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getEmail(), loginUser.getPassword());

        GetSaldoRequestJson param = new GetSaldoRequestJson();
        param.setId(loginUser.getId());
        userService.getSaldo(param).enqueue(new Callback<GetSaldoResponseJson>() {
            @Override
            public void onResponse(Call<GetSaldoResponseJson> call, Response<GetSaldoResponseJson> response) {
                if (response.isSuccessful()) {
                    String formattedText = String.format(Locale.US, General.MONEY +" %s.00",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    mPayBalance.setText(formattedText);
                    mPayBalance.setVisibility(View.VISIBLE);
                    loading_pay.setVisibility(View.GONE);
                    successfulCall++;

                    if (HomeFragment.this.getActivity() != null) {
                        Realm realm = GoTaxiApplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        User loginUser = GoTaxiApplication.getInstance(HomeFragment.this.getActivity()).getLoginUser();
                        realm.beginTransaction();
                        loginUser.setmPaySaldo(response.body().getData());
                        realm.commitTransaction();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSaldoResponseJson> call, Throwable t) {

            }

        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public ArrayList<Banner> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Banner> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(banners.get(position).id, banners.get(position).foto);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }


    }



    private  void featurePro(){
              String appPackageName = "com.gotaxi.passenger";
              startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }


}
