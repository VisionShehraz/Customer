package com.gotaxiride.passenger.mMassage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.gotaxiride.passenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 12/22/2018.
 */

public class MassagePreferenceFragment extends Fragment {

    public static final String MASSAGE_ITEM_ID = "MassageItem";

    @BindView(R.id.massagePreference_image)
    ImageView massagePicture;

    @BindView(R.id.massagePreference_massageService)
    TextView massageDesc;

    @BindView(R.id.massagePreference_yourGenderGroup)
    RadioGroup yourGenderGroup;

    @BindView(R.id.massagePreference_durationGroup)
    RadioGroup durationGroup;

    @BindView(R.id.massagePreference_therapistGroup)
    RadioGroup therapistGroup;

    @BindView(R.id.massagePreference_next)
    Button nextButton;

    private MassagePreference preference;
    private MenuMassageItem massageItem;

    private MassageActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MassageActivity) activity = (MassageActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_massage_preference, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        preference = new MassagePreference();
        preference.setIdGender(2);
        preference.setGender("Male");
        preference.setDurasi(1);
        preference.setDurasiText("60 minutes");
        preference.setIdTherapist(2);
        preference.setTherapist("Male");

        massageItem = activity.getMassageItem();
//        Picasso.with(getContext()).load(massageItem.getFoto()).fit().centerCrop().into(massagePicture);
        Glide.with(getContext()).load(massageItem.getFoto()).centerCrop().into(massagePicture);
        massageDesc.setText(massageItem.getLayanan());

        yourGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.massagePreference_yourGenderMale:
                        preference.setIdGender(2);
                        preference.setGender("Male");
                        break;
                    case R.id.massagePreference_yourGenderFemale:
                        preference.setIdGender(1);
                        preference.setGender("Female");
                        break;
                }
            }
        });

        durationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.massagePreference_duration60:
                        preference.setDurasi(1);
                        preference.setDurasiText("60 minutes");
                        break;
                    case R.id.massagePreference_duration90:
                        preference.setDurasi(1.5);
                        preference.setDurasiText("90 minutes");
                        break;
                    case R.id.massagePreference_duration120:
                        preference.setDurasi(2);
                        preference.setDurasiText("120 minutes");
                        break;
                }
            }
        });

        therapistGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.massagePreference_therapistMale:
                        preference.setIdTherapist(2);
                        preference.setTherapist("Male");
                        break;
                    case R.id.massagePreference_therapistFemale:
                        preference.setIdTherapist(1);
                        preference.setTherapist("Female");
                        break;
                    case R.id.massagePreference_therapistNoPrefs:
                        preference.setIdTherapist(3);
                        preference.setTherapist("No Preferences");
                        break;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setMassagePreference(preference);
                activity.addFragmentBackstack(new ConfirmMassageFragment());
            }
        });
    }
}
