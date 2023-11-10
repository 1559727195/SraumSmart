package com.massky.sraum.fragment;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.massky.sraum.R;
import com.massky.sraum.adapter.DynamicFragmentViewPagerAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;


/**
 * Created by zhu on 2017/11/30.
 */
public class PmFragment extends BaseFragment1 {

    @Override
    protected void onData() {
    }

    @Override
    protected void onEvent() {
    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected int viewId() {
        return R.layout.pm_item_lay;
    }

    @Override
    protected void onView(View view) {

    }

    @Override
    public void onClick(View v) {

    }

    public static PmFragment newInstance() {
        PmFragment newFragment = new PmFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }


}
