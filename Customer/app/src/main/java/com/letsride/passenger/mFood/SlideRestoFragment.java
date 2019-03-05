package com.letsride.passenger.mFood;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.letsride.passenger.GoTaxiApplication;
import com.letsride.passenger.R;
import com.letsride.passenger.api.ServiceGenerator;
import com.letsride.passenger.api.service.BookService;
import com.letsride.passenger.model.Restoran;
import com.letsride.passenger.model.User;
import com.letsride.passenger.model.json.book.GetFoodRestoRequestJson;
import com.letsride.passenger.model.json.book.GetFoodRestoResponseJson;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlideRestoFragment extends Fragment {

    private String image;
    private int id;
    private int idResto;

    public static SlideRestoFragment newInstance(int id, String image, int idResto) {
        SlideRestoFragment fragmentFirst = new SlideRestoFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        args.putString("someString", image);
        args.putInt("idResto", idResto);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt("someInt", 0);
        image = getArguments().getString("someString");
        idResto = getArguments().getInt("idResto", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.slide_image);

//        Picasso.with(getContext()).load(image).into(imageView);
        Glide.with(getContext())
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Please wait...", false, false);
                GetFoodRestoRequestJson param = new GetFoodRestoRequestJson();
                param.setIdResto(idResto);
                Realm realm = GoTaxiApplication.getInstance(context).getRealmInstance();
                // error
                User loginUser = realm.copyFromRealm(GoTaxiApplication.getInstance(context).getLoginUser());
                BookService bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

                bookService.getFoodResto(param).enqueue(new Callback<GetFoodRestoResponseJson>() {
                    @Override
                    public void onResponse(Call<GetFoodRestoResponseJson> call, Response<GetFoodRestoResponseJson> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            Restoran restoran = response.body().getFoodResto().getDetailRestoran().get(0);

                            Intent intent = new Intent(context, FoodMenuActivity.class);
                            intent.putExtra(FoodMenuActivity.ID_RESTO, restoran.getId());
                            intent.putExtra(FoodMenuActivity.NAMA_RESTO, restoran.getNamaResto());
                            intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, restoran.getAlamat());
                            intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, 0);
                            intent.putExtra(FoodMenuActivity.JAM_BUKA, restoran.getJamBuka());
                            intent.putExtra(FoodMenuActivity.JAM_TUTUP, restoran.getJamTutup());
                            intent.putExtra(FoodMenuActivity.IS_OPEN, restoran.isOpen());
                            intent.putExtra(FoodMenuActivity.PICTURE_URL, restoran.getFotoResto());
                            intent.putExtra(FoodMenuActivity.IS_MITRA, restoran.isPartner());
                            startActivity(intent);
                        } else {
                            onFailure(call, new RuntimeException("Check internet connection."));
                        }
                    }

                    @Override
                    public void onFailure(Call<GetFoodRestoResponseJson> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        switch (page){
//            case 0:
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.banner_1));
//                break;
//            case 1:
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.banner_2));
//                break;
//            case 2:
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.banner_3));
//                break;
//            case 3:
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.banner_4));
//                break;
//            case 4:
//                imageView.setImageDrawable(getResources().getDrawable(R.drawable.banner_5));
//                break;
//
//        }
        return view;
    }

}
