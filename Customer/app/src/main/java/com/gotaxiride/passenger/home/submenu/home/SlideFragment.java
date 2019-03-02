package com.gotaxiride.passenger.home.submenu.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.gotaxiride.passenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlideFragment extends Fragment {

    private String image;
    private int id;

    public static SlideFragment newInstance(int id, String image) {
        SlideFragment fragmentFirst = new SlideFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        args.putString("someString", image);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt("someInt", 0);
        image = getArguments().getString("someString");
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
