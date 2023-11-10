package com.massky.sraum.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.multidex.MultiDex;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.dialog.CommonData;
import com.dialog.ToastUtils;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.mob.MobSDK;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import webapp.config.SystemParams;


/**
 * Created by masskywcy on 2017-01-04.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private Context context;
    public String calledAcccout;
    private static App _instance;
    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;

    // 开放平台申请的APP key & secret key
    public static String APP_KEY = "ccd38858cc5a459bbeedcf93a25ae6be";
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
    private boolean isForeground;
    private boolean isDoflag;

    private MutableLiveData<String> mBroadcastData;
    private Map<String, Object> mCacheMap;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    mBroadcastData.setValue(action);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
//        MobSDK.submitPolicyGrantResult(true, null); //添加最新隐私协议
//        jpush_speach();
        _instance = this;
        ok_http();
        app_dialog_service();
        ssid_receiver();
    }


    private void ssid_receiver() {
        mCacheMap = new HashMap<>();

        mBroadcastData = new MutableLiveData<>();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
    }

    private void app_dialog_service() {
        //application生命周期
        this.registerActivityLifecycleCallbacks(this);//注册
        CommonData.applicationContext = this;
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        CommonData.ScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
//        Intent dialogservice = new Intent(this, CommonDialogService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//           startForegroundService(dialogservice);
//        } else {
//           startService(dialogservice);
//        }
    }

    private void ok_http() {
        //okhttp网络配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(mReceiver);
    }


    public void observeBroadcast(LifecycleOwner owner, Observer<String> observer) {
        mBroadcastData.observe(owner, observer);
    }

    public void observeBroadcastForever(Observer<String> observer) {
        mBroadcastData.observeForever(observer);
    }

    public void removeBroadcastObserver(Observer<String> observer) {
        mBroadcastData.removeObserver(observer);
    }

    public void putCache(String key, Object value) {
        mCacheMap.put(key, value);
    }

    public Object takeCache(String key) {
        return mCacheMap.remove(key);
    }


    /**
     * @return
     */
    public static App getInstance() {
        return _instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity.getParent() != null) {
            CommonData.mNowContext = activity.getParent();
        } else
            CommonData.mNowContext = activity;
    }


    @Override
    public void onActivityStarted(Activity activity) {
        if (activity.getParent() != null) {
            CommonData.mNowContext = activity.getParent();
        } else
            CommonData.mNowContext = activity;

        activityAount++;
        if (activityAount > 0) {
            if (!isDoflag) {
                isForeground = true;
                isDoflag = true;
                Log.e("zhu-", "isForeground:" + isForeground);
                if (CommonData.mNowContext != null) {
                    boolean loginflag = (boolean) SharedPreferencesUtil.getData(CommonData.mNowContext, "loginflag", false);
                    if (loginflag)
//                        ToastUtil.showToast(CommonData.mNowContext,"App-loginflag:" + loginflag);
                        ToastUtils.getInstances().show_fourbackground("账号在其他地方登录，请重新登录。");
                }
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity.getParent() != null) {
            CommonData.mNowContext = activity.getParent();
        } else
            CommonData.mNowContext = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
//		ToastUtils.getInstances().cancel();// activity死的时候，onActivityPaused(Activity activity)
        //ToastUtils.getInstances().cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityAount--;
        if (activityAount == 0) {
            isForeground = false;
            isDoflag = false;
            Log.e("zhu-", "isForeground:" + isForeground);
            //
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}
