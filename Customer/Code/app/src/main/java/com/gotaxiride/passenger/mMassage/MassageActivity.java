package com.gotaxiride.passenger.mMassage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.gotaxiride.passenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Androgo on 12/20/2018.
 */

public class MassageActivity extends AppCompatActivity {

    @BindView(R.id.massage_container)
    FrameLayout container;
    private Realm realm;

    private MenuMassageItem massageItem;
    private MassagePreference massagePreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        MenuMassageFragment menuMassage = new MenuMassageFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.massage_container, menuMassage).commit();
    }

    public void addFragmentBackstack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.massage_container, fragment).addToBackStack(null).commit();
    }

    public MenuMassageItem getMassageItem() {
        return massageItem;
    }

    public void setMassageItem(MenuMassageItem massageItem) {
        this.massageItem = massageItem;
    }

    public MassagePreference getMassagePreference() {
        return massagePreference;
    }

    public void setMassagePreference(MassagePreference massagePreference) {
        this.massagePreference = massagePreference;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
