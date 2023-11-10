package com.massky.sraum.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.espressif.esptouch.android.NetUtils;
import com.massky.sraum.R;

import java.util.ArrayList;
import java.util.List;

public class WifiUtil {

    private WifiManager mWifiManager;
    private static WifiUtil mInstance;

    private WifiUtil(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (WifiUtil.class) {
                if (mInstance == null) {
                    mInstance = new WifiUtil(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    /***
     * 打开wifi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }


    /**
     * 获取SSID
     *
     * @param activity 上下文
     * @return WIFI 的SSID
     */
    public String getWIFISSID(Context activity) {
        String ssid = "unknown id";

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            WifiManager mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            assert mWifiManager != null;
            WifiInfo info = mWifiManager.getConnectionInfo();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return info.getSSID();
            } else {
                return info.getSSID().replace("\"", "");
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {

            ConnectivityManager connManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connManager != null;
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "");
                }
            }
        }
        return ssid;
    }


    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 判断是否是5G wifi  网络
     */
    public boolean is5GWifi() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(NetUtils.is5G(wifiInfo.getFrequency())) {
                return true;
            }
        }
        return  false;
    }




    /**
     * 获取wifi扫描结果
     */
    public List<ScanResult> getWifiScanResult() {
        List<ScanResult> mScanResultList = new ArrayList<>();
        List<ScanResult> scanResultList = mWifiManager.getScanResults();
        if (scanResultList != null && scanResultList.size() > 0) {
            for (int i = 0; i < scanResultList.size(); i++) {
                ScanResult scanResult = scanResultList.get(i);
                if (scanResult != null && !TextUtils.isEmpty(scanResult.SSID)) {
                    mScanResultList.add(scanResult);
                } else {
                    continue;
                }
            }
        }
        return mScanResultList;
    }

    /**
     * 获取wifi等级，总共分为四级
     *
     * @param rssi
     * @return
     */
    public int getWifiSignal(int rssi) {
        if (rssi == Integer.MAX_VALUE) {
            return -1;
        }
        return mWifiManager.calculateSignalLevel(rssi, 4);
    }

    /**
     * 判断是否2.4Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is24GHzWifi(int frequency) {
        return frequency > 2400 && frequency < 2500;
    }

    /**
     * 判断是否5Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is5GHzWifi(int frequency) {
        return frequency > 4900 && frequency < 5900;
    }

}
