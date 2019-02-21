package com.gotaxiride.passenger.home.submenu.history.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.gotaxiride.passenger.config.General;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import com.gotaxiride.passenger.GoTaxiApplication;
import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;
import com.gotaxiride.passenger.api.service.BookService;
import com.gotaxiride.passenger.home.submenu.history.details.items.MMartItem;
import com.gotaxiride.passenger.model.MMartDetailTransaksi;
import com.gotaxiride.passenger.model.MMartItemRemote;
import com.gotaxiride.passenger.model.User;
import com.gotaxiride.passenger.model.json.book.detailTransaksi.GetDataTransaksiMMartResponse;
import com.gotaxiride.passenger.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fathony on 24/02/2017.
 */

public class MMartDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(R.id.mMartDetail_title)
    TextView title;
    @BindView(R.id.mMartDetail_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.mMartDetail_total)
    TextView totalField;

    FastItemAdapter<MMartItem> adapter;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmart_detail);
        ButterKnife.bind(this);

        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = GoTaxiApplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());


        title.setText("Detail Go-Mart");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FastItemAdapter<>();
        recyclerView.setAdapter(adapter);

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMMart(param).enqueue(new Callback<GetDataTransaksiMMartResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMMartResponse> call, Response<GetDataTransaksiMMartResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        MMartDetailTransaksi detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail, response.body().getListBarang());
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMMartResponse> call, Throwable t) {
                Toast.makeText(MMartDetailActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(MMartDetailTransaksi transaksi, List<MMartItemRemote> items) {
        List<MMartItem> mMartItems = new ArrayList<>();
        for (MMartItemRemote item : items) {
            mMartItems.add(new MMartItem(item.getNamaBarang(), item.getJumlah()));
        }
        adapter.clear();
        adapter.set(mMartItems);
        adapter.notifyDataSetChanged();
        String total = "Total : " + String.format(Locale.US, General.MONEY +" %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getEstimasiBiaya()));
        totalField.setText(total);
    }
}
