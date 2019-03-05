package com.letsride.passenger.home.submenu.history;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;

import com.letsride.passenger.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Androgo on 10/31/2018.
 */

public class HistoryFragment extends Fragment {

    @BindView(R.id.history_tabLayout)
    SmartTabLayout historyTabLayout;

    @BindView(R.id.history_viewPager)
    ViewPager historyViewPager;

    @BindView(R.id.history_swipeRefresh)
    SwipeRefreshLayout historySwipeRefresh;
    InProgressHistoryFragment inProgressFragment;
    CompletedHistoryFragment completedHistoryFragment;
    private ViewPagerItemAdapter adapter;
    private MyPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupViewPagerAdapter();
        setupSwipeRefresh();
    }

    private void setupViewPagerAdapter() {

        mAdapter = new MyPagerAdapter(getChildFragmentManager());
        historyViewPager.setAdapter(mAdapter);
        historyTabLayout.setViewPager(historyViewPager);

        historyViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupViewPagerAdapter();
    }

    private void setupSwipeRefresh() {
        historySwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupViewPagerAdapter();
                new CountDownTimer(5000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        historySwipeRefresh.setRefreshing(false);
                    }
                }.start();
            }
        });
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.history_viewPager, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void enableSwipeRefresh(boolean isEnable) {
        if (!historySwipeRefresh.isRefreshing()) historySwipeRefresh.setEnabled(isEnable);
    }

    public interface OnSwipeRefresh {
        void onRefresh();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    inProgressFragment = InProgressHistoryFragment.newInstance();
                    return inProgressFragment;
                case 1:
                    completedHistoryFragment = CompletedHistoryFragment.newInstance();
                    return completedHistoryFragment;
                default:
                    return inProgressFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PROCESS";
                case 1:
                    return "COMPLETE";
                default:
                    return "title";

            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
