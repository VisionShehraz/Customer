package com.letsride.passenger.home.submenu.history.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.model.DetailTransaksiMSend;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.detailTransaksi.GetDataTransaksiMSendResponse;
import com.letsride.passenger.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.SimpleDateFormat;
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

public class MSendDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(R.id.mSendDetail_title)
    TextView title;

    @BindView(R.id.mSendDetail_tanggal)
    TextView tanggalPengiriman;
    @BindView(R.id.mSendDetail_alamatAsal)
    TextView alamatAsal;
    @BindView(R.id.mSendDetail_alamatTujuan)
    TextView alamatTujuan;
    @BindView(R.id.mSendDetail_namaPenerima)
    TextView namaPenerima;
    @BindView(R.id.mSendDetail_nomorPenerima)
    TextView nomorPenerima;
    @BindView(R.id.mSendDetail_namaBarang)
    TextView namaBarang;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msend_detail);
        ButterKnife.bind(this);

        title.setText("Detail Go-Send");
        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = GoTaxiApplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMSend(param).enqueue(new Callback<GetDataTransaksiMSendResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMSendResponse> call, Response<GetDataTransaksiMSendResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        DetailTransaksiMSend detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail);
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMSendResponse> call, Throwable t) {
                Toast.makeText(MSendDetailActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(DetailTransaksiMSend detail) {
        SimpleDateFormat sdfTo = new SimpleDateFormat("dd MM yyyy hh:mm", Locale.US);

        String formattedDate = sdfTo.format(detail.getWaktuOrder());
        tanggalPengiriman.setText(formattedDate);
        alamatAsal.setText(detail.getAlamatAsal());
        alamatTujuan.setText(detail.getAlamatTujuan());
        namaPenerima.setText(detail.getNamaPenerima());
        nomorPenerima.setText(detail.getTeleponPenerima());
        namaBarang.setText(detail.getNamaBarang());
    }
}
