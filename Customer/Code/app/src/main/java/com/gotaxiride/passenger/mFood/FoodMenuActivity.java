package com.gotaxiride.passenger.mFood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.model.FoodResto;
import com.gotaxiride.passenger.model.Makanan;
import com.gotaxiride.passenger.model.MenuMakanan;
import com.gotaxiride.passenger.model.PesananFood;
import com.gotaxiride.passenger.model.Restoran;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.GetFoodRestoRequestJson;
import com.gotaxiride.passenger.model.json.book.GetFoodRestoResponseJson;
import com.gotaxiride.passenger.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodMenuActivity extends AppCompatActivity {

    public static final String ID_RESTO = "idResto";
    public static final String NAMA_RESTO = "namaResto";
    public static final String ALAMAT_RESTO = "alamatResto";
    public static final String DISTANCE_RESTO = "distanceResto";
    public static final String JAM_BUKA = "jamBuka";
    public static final String JAM_TUTUP = "jamTutup";
    public static final String IS_MITRA = "IsMitra";
    public static final String IS_OPEN = "IsOpen";
    public static final String PICTURE_URL = "PicUrl";


    @BindView(R.id.resto_title)
    TextView titleResto;
    @BindView(R.id.food_img)
    ImageView foodImage;
    @BindView(R.id.food_address)
    TextView foodAddress;
    @BindView(R.id.food_info)
    TextView foodInfo;
    @BindView(R.id.menu_recycler)
    RecyclerView menuRecycler;
    @BindView(R.id.foodMenu_bottom)
    RelativeLayout priceContainer;
    @BindView(R.id.qty_text)
    TextView qtyText;
    @BindView(R.id.cost_text)
    TextView costText;
    @BindView(R.id.foodMenu_mitra)
    TextView mitra;
    @BindView(R.id.foodMenu_closed)
    TextView closed;
    private int idResto;
    private String namaResto;
    private String alamatResto;
    private double distanceResto;
    private String jamBuka;
    private String jamTutup;
    private boolean isOpenFromParent;
    private boolean isMitra;
    private String picUrl;

    private boolean isOpen;

    private Realm realm;
    private List<MenuMakanan> menuMakanan;
    private List<Makanan> makanan;
    private FastItemAdapter<MenuItem> menuAdapter;

    private NestedScrollView nestedScrollView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        ButterKnife.bind(this);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        nestedScrollView = findViewById(R.id.foodMenu_content);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        Intent intent = getIntent();
        idResto = intent.getIntExtra(ID_RESTO, -1);
        namaResto = intent.getStringExtra(NAMA_RESTO);
        alamatResto = intent.getStringExtra(ALAMAT_RESTO);
        distanceResto = intent.getDoubleExtra(DISTANCE_RESTO, -8);
        jamBuka = intent.getStringExtra(JAM_BUKA);
        jamTutup = intent.getStringExtra(JAM_TUTUP);
        isOpenFromParent = intent.getBooleanExtra(IS_OPEN, false);
        isMitra = intent.getBooleanExtra(IS_MITRA, false);
        picUrl = intent.getStringExtra(PICTURE_URL);


        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(namaResto);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        nestedScrollView.setVisibility(View.VISIBLE);

        realm = Realm.getDefaultInstance();

        Glide.with(this).load(picUrl).centerCrop().into(foodImage);
        if (isOpenFromParent) {
            isOpen = calculateJamBukaTutup();
        } else {
            isOpen = false;
        }

        closed.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        mitra.setVisibility(isMitra ? View.VISIBLE : View.GONE);

        titleResto.setText(namaResto);
        foodAddress.setText(alamatResto);
        NumberFormat formatter = new DecimalFormat("#0.00");
        foodInfo.setText("" + formatter.format(distanceResto) + " KM | OPEN " + jamBuka + " - " + jamTutup);

        menuAdapter = new FastItemAdapter<>();
        MenuRecycler();
        getMenuResto();

        priceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodMenuActivity.this, BookingActivity.class);
                FoodMenuActivity.this.startActivity(intent);
            }
        });
    }

    private boolean calculateJamBukaTutup() {
        String[] parsedJamBuka = jamBuka.split(":");
        String[] parsedJamTutup = jamTutup.split(":");

        int jamBuka = Integer.parseInt(parsedJamBuka[0]), menitBuka = Integer.parseInt(parsedJamBuka[1]);
        int jamTutup = Integer.parseInt(parsedJamTutup[0]), menitTutup = Integer.parseInt(parsedJamTutup[1]);

        int totalMenitBuka = (jamBuka * 60) + menitBuka;
        int totalMenitTutup = (jamTutup * 60) + menitTutup;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    private void MenuRecycler() {
        menuRecycler.setLayoutManager(new LinearLayoutManager(this));
        menuRecycler.setAdapter(menuAdapter);

        menuAdapter.withOnClickListener(new FastAdapter.OnClickListener<MenuItem>() {
            @Override
            public boolean onClick(View v, IAdapter<MenuItem> adapter, MenuItem item, int position) {
                if (isOpen) {
                    Log.e("BUTTON", "CLICKED");
                    MenuMakanan selectedMenu = menuMakanan.get(position);
                    int idMenu = selectedMenu.getIdMenu();
                    Intent intent = new Intent(FoodMenuActivity.this, MakananActivity.class);
                    intent.putExtra(MakananActivity.ID_MENU, selectedMenu.getIdMenu());
                    intent.putExtra(MakananActivity.NAMA_RESTO, selectedMenu.getMenuMakanan());
                    Log.e("ID RESTO", idMenu + "");
                    startActivity(intent);
                } else {
                    Toast.makeText(FoodMenuActivity.this, "Your restaurant is still closed. :(", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if (quantity > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete current order(s)?")
                    .setMessage("Leaving this page will cause you lose all the order you've made. Continue?")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FoodMenuActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Back", null)
                    .show();
        } else {
            finish();
        }
    }

    private void getMenuResto() {
        User loginUser = GoTaxiApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetFoodRestoRequestJson param = new GetFoodRestoRequestJson();
        param.setIdResto(idResto);

        service.getFoodResto(param).enqueue(new Callback<GetFoodRestoResponseJson>() {
            @Override
            public void onResponse(Call<GetFoodRestoResponseJson> call, Response<GetFoodRestoResponseJson> response) {
                if (response.isSuccessful()) {
                    FoodResto foodResto = response.body().getFoodResto();
                    Restoran restoranList = foodResto.getDetailRestoran().get(0);

                    menuMakanan = foodResto.getMenuMakananList();
                    makanan = foodResto.getMakananList();
                    Log.d(FoodMenuActivity.class.getSimpleName(), "Number of kategori: " + menuMakanan.size());
                    Log.d(FoodMenuActivity.class.getSimpleName(), "Number of restoran: " + makanan.size());
                    Realm realm = GoTaxiApplication.getInstance(FoodMenuActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Makanan.class);
                    realm.copyToRealm(makanan);
                    realm.commitTransaction();

                    realm.beginTransaction();
                    realm.delete(Restoran.class);
                    realm.copyToRealm(restoranList);
                    realm.commitTransaction();

                    MenuItem menuItem;
                    for (int i = 0; i < menuMakanan.size(); i++) {
                        menuItem = new MenuItem(FoodMenuActivity.this);
                        menuItem.idMenu = menuMakanan.get(i).getIdMenu();
                        menuItem.menuMakanan = menuMakanan.get(i).getMenuMakanan();
                        menuAdapter.add(menuItem);
                        Log.e("ADD RESTO", menuItem.idMenu + "");
                        Log.e("ADD RESTO", menuItem.menuMakanan + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetFoodRestoResponseJson> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalculatePrice();
    }

    public void CalculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if (quantity > 0)
            priceContainer.setVisibility(View.VISIBLE);
        else
            priceContainer.setVisibility(View.GONE);

        qtyText.setText("" + quantity);
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, General.MONEY +" %s.00", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
