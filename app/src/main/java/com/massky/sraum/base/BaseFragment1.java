package com.massky.sraum.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.massky.sraum.event.MyDialogEvent;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhu on 2017/7/27.
 */

public abstract class BaseFragment1 extends Fragment implements View.OnClickListener {
    public static boolean isDestroy = false;
    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    private boolean isLoadData = false;

    public static boolean isForegrounds = false;
    private Unbinder mUnbinder;

    private  String TAG = "lazys";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(viewId(), null);
        mUnbinder = ButterKnife.bind(this,rootView);
        isDestroy = false;
        onView(rootView);
        onEvent();
        onData();
        isCreateView = true;
       // Log.e(TAG, "onCreateView: f"+this);
        return rootView;
    }

    //onAttach


    //String TAG = "lazy";
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       // Log.e(TAG, "onAttach: f"+this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Log.e(TAG, "onCreate: f"+this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // Log.e(TAG, "onViewCreated: f"+this);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }
//


    protected abstract void onData();


    protected abstract void onEvent();

    public abstract void onEvent(MyDialogEvent eventData);


    @Override
    public void onStart() {
        super.onStart();
      //  Log.e(TAG, "onStart: f"+this);
    }


    protected abstract int viewId();

    protected abstract void onView(View view);

    private void initViews() {
        //初始化控件
    }

    //此方法在控件初始化前调用，所以不能在此方法中直接操作控件会出现空指针
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
       // Log.e(TAG, "setUserVisibleHint: "+isVisibleToUser+this );
    }


    private void lazyLoad() {
        //如果没有加载过就加载，否则就不再加载了
        if (!isLoadData) {
            //加载数据操作
            isLoadData = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden + this );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一个fragment会调用
        if (getUserVisibleHint())
            lazyLoad();
       // Log.e(TAG, "onActivityCreated: f" +this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
      //  Log.e(TAG, "onDestroyView: f"+this);
    }


    @Override
    public void onPause() {
        isForegrounds = false;
//        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
       // Log.e(TAG, "onPause: f"+this);
    }

    @Override
    public void onStop() {
        super.onStop();
      //  Log.e(TAG, "onStop: f"+this+this);
    }

   // onDestroyView

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }

    @Override
    public void onResume() {
        isForegrounds = true;
//        getActivity().registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
       // Log.e(TAG, "onResume: f" +this);
    }


    @Override
    public void onDestroy() {
        isDestroy = true;
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        this.mUnbinder = null;
        super.onDestroy();
       // Log.e(TAG, "onDestroy: f"+this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  Log.e(TAG, "onDetach: f"+this);
    }
}
