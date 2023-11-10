package com.massky.sraum.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.broadcom.cooee.Cooee;
import com.google.gson.Gson;
import com.ipcamera.demo.BridgeService;
import com.ipcamera.demo.utils.SystemValue;
import com.massky.sraum.R;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.MyUtil;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.StringUtils;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.GpsUtil;
import com.massky.sraum.Utils.WifiUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.ConfigDialogCameraFragment;
import com.massky.sraum.permissions.RxPermissions;
import com.massky.sraum.view.ClearEditText;
import com.mediatek.demo.smartconnection.JniLoader;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import voice.encoder.DataEncoder;
import voice.encoder.VoicePlayer;
import vstc2.nativecaller.NativeCaller;


/**
 * Created by zhu on 2018/5/30.
 */

public class ConnWifiCameraActivity extends BaseActivity implements BridgeService.AddCameraInterface
        , BridgeService.IpcamClientInterface, BridgeService.CallBackMessageInterface {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.status_view)
    StatusView statusView;
    @BindView(R.id.select_wlan_rel_big)
    PercentRelativeLayout select_wlan_rel_big;
    @BindView(R.id.edit_wifi)
    ClearEditText edit_wifi;
    @BindView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    private ConfigDialogCameraFragment newFragment;
    @BindView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    private int CONNWIFI = 101;
    private EyeUtil eyeUtil;
    @BindView(R.id.conn_btn_dev)
    Button conn_btn_dev;
    private String wifi_name = "";
    private String TAG = "robin debug";
    private Handler handler = new Handler();
    private WifiManager manager;
    private static final String STR_DID = "did";
    private static final String STR_MSG_PARAM = "msgparam";
    //private MyWifiThread myWifiThread = null;
    private boolean blagg = false;
    private static final int SEARCH_TIME = 5000;
    private static final int SEARCH_TIME_SECOND = 6000;
    private static final int ONE_KEY_TIME = 30000;
    List<Map> list_wifi_camera = new ArrayList<>();
    private int index_wifi;

    private String sendMac = null;
    private String wifiName;
    private String currentBssid;

    private JniLoader loader;
    private String type;
    private WifiUtil wifiUtil;
    private boolean first_search;
    private String mode = "old";

    boolean isOpenSmartLink = false;
    String mConnectedSsid = "";
    private byte AuthModeOpen = 0;
    private byte AuthModeWPA1PSKWPA2PSK = 9;
    private byte AuthModeWPA2PSK = 7;
    private byte AuthModeWPAPSK = 4;
    private byte AuthModeWPA1WPA2 = 8;
    private byte AuthModeWPA2 = 6;
    private byte AuthModeWPA = 3;
    private int cameratype = 2;
    private int intentFlag = 1;
    private String resultString = "";
    private String ssidPwd = "";
    private String jsonWiFi;
    private WifiManager mWifiManager;
    private byte mAuthMode;
    private String mAuthString;
    private VoicePlayer player;
    private voice2.encoder.VoicePlayer newplayer;
    private String wifi_mac;
    private String mac_str;
    private boolean dialogDismiss;
    private int mode_index;

    @Override
    protected int viewId() {
        return R.layout.conn_wifi_camera_act;
    }

    //**********************************************************public******************************
//    static {
//        try {
//            System.loadLibrary("voiceRecog2");
//            Log.d("voice_camera_config", "load library success");
//        } catch (Exception ex) {
//            Log.d("voice_camera_config", "load library failed!!!! ex=" + ex);
//        }
//    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        select_wlan_rel_big.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        conn_btn_dev.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        onview();
        init_data();
        init_wifi();
        switch_mode_player();
//        initDialog();
        init_wifi_camera();
    }

    private void switch_mode_player() {
        switch (mode) {
            case "scan":
                break;
            default:
                player = new VoicePlayer();
                newplayer = new voice2.encoder.VoicePlayer();
                break;
        }
    }


    private void open_gpss() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                if (GpsUtil.isOPen(ConnWifiCameraActivity.this)) {
                    initWifiName();
                    initWifiConect();
                } else {
                    openGpsSettings();
                }
            }
        });
    }


    private void init_wifi() {
        wifiUtil = WifiUtil.getInstance(this);
        getWifiSSid();
    }


    private void getWifiSSid() {
        open_gpss();
    }


    /**
     * 打开Gps设置界面
     */
    private void openGpsSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 887);
    }


    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_data() {
        type = (String) getIntent().getSerializableExtra("type");
        mode = (String) getIntent().getSerializableExtra("mode");
    }

    private void one_step_peizhi() { //根据mode 类型 来判断是否要 扫描二维码
        getWifi();
        if (switch_mode()) return;
        boolean res = JniLoader.LoadLib();
        Log.e("SmartConnection", "Load Smart Connection Library Result ：" + res);
        loader = new JniLoader();
        int proV = loader.GetProtoVersion();
        Log.e("SmartConnection", "proV ：" + proV);
        int libV = loader.GetLibVersion();
        Log.e("SmartConnection", "libV ：" + libV);
        String version = "V" + proV + "." + libV;
    }

    private boolean switch_mode() {
        if (mode == null) return true;
        switch (mode) {
            case "scan":
                return true;
        }
        return false;
    }

    private void getWifi() {
        Object obj1;
        label0:
        {
            obj1 = (WifiManager) getSystemService("wifi");
            Object obj = ((WifiManager) (obj1)).getConnectionInfo();
            wifi_name = ((WifiInfo) (obj)).getSSID().toString();
            wifi_mac = ((WifiInfo) (obj)).getMacAddress();
            int i = wifi_name.length();
            boolean flag = false;
            boolean flag1 = false;
            if (i > 2 && wifi_name.charAt(0) == '"') {
                String s1 = wifi_name;
                if (s1.charAt(s1.length() - 1) == '"') {
                    String s2 = wifi_name;
                    wifi_name = s2.substring(1, s2.length() - 1);
                }
            }
            List list = ((WifiManager) (obj1)).getScanResults();
            obj1 = new ArrayList();
            ((ArrayList) (obj1)).clear();
            for (i = 0; i < list.size(); i++)
                ((ArrayList) (obj1)).add(((ScanResult) list.get(i)).BSSID.toString());

            currentBssid = ((WifiInfo) (obj)).getBSSID();
            obj = currentBssid;
            if (obj == null) {
                i = ((flag1) ? 1 : 0);
                do {
                    if (i >= list.size())
                        break;
                    if (((ScanResult) list.get(i)).SSID.toString().equals(wifi_name)) {
                        currentBssid = ((ScanResult) list.get(i)).BSSID.toString();
                        break;
                    }
                    i++;
                } while (true);
                break label0;
            }
            i = ((flag) ? 1 : 0);
            if (!((String) (obj)).equals("00:00:00:00:00:00")) {
                if (!currentBssid.equals(""))
                    break label0;
                i = ((flag) ? 1 : 0);
            }
            do {
                if (i >= list.size())
                    break;
                if (((ScanResult) list.get(i)).SSID.toString().equals(wifi_name)) {
                    currentBssid = ((ScanResult) list.get(i)).BSSID.toString();
                    break;
                }
                i++;
            } while (true);
        }
        String s = currentBssid;
        if (s == null)
            return;
        String as[] = s.split(":");
        int k = currentBssid.split(":").length - 1;
        if (k > -1) {
            for (int j = ((ArrayList) (obj1)).size() - 1; j > -1; j--) {
                if (currentBssid.equals(((ArrayList) (obj1)).get(j)))
                    continue;
                String as1[] = ((String) ((ArrayList) (obj1)).get(j)).split(":");
                if (!as[k].equals(as1[k]))
                    ((ArrayList) (obj1)).remove(j);
            }

            if (((ArrayList) (obj1)).size() != 1 && ((ArrayList) (obj1)).size() != 0) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[4].toString());
                ((StringBuilder) (obj1)).append(as[5].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            if (k == 5) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[k - 1].toString());
                ((StringBuilder) (obj1)).append(as[k].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            if (k == 4) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[k].toString());
                ((StringBuilder) (obj1)).append(as[k + 1].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            if (k == 3) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[k].toString());
                ((StringBuilder) (obj1)).append(as[k + 1].toString());
                ((StringBuilder) (obj1)).append(as[k + 2].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            if (k == 2) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[k].toString());
                ((StringBuilder) (obj1)).append(as[k + 1].toString());
                ((StringBuilder) (obj1)).append(as[k + 2].toString());
                ((StringBuilder) (obj1)).append(as[k + 3].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            if (k == 1) {
                obj1 = new StringBuilder();
                ((StringBuilder) (obj1)).append(as[k].toString());
                ((StringBuilder) (obj1)).append(as[k + 1].toString());
                ((StringBuilder) (obj1)).append(as[k + 2].toString());
                ((StringBuilder) (obj1)).append(as[k + 3].toString());
                ((StringBuilder) (obj1)).append(as[k + 4].toString());
                sendMac = ((StringBuilder) (obj1)).toString();
                return;
            }
            obj1 = new StringBuilder();
            ((StringBuilder) (obj1)).append(as[k].toString());
            ((StringBuilder) (obj1)).append(as[k + 1].toString());
            ((StringBuilder) (obj1)).append(as[k + 2].toString());
            ((StringBuilder) (obj1)).append(as[k + 3].toString());
            ((StringBuilder) (obj1)).append(as[k + 4].toString());
            ((StringBuilder) (obj1)).append(as[k + 5].toString());
            sendMac = ((StringBuilder) (obj1)).toString();
        }
    }

    private void initSound(String mac) {

        byte[] midbytes = null;
        try {
            midbytes = MyUtil.HexString2Bytes(mac);
            MyUtil.printHexString(midbytes);
        } catch (Exception ex) {

        }
        // Misplaced declaration of an exception variable
        int j = midbytes.length;
        byte[] ss = null;
        int i = 0;
        if (j > 6) {
            Toast.makeText(ConnWifiCameraActivity.this, "no support", 0).show();
            return;
        }
        if (midbytes.length == 2) {
            ss = new byte[2];
            ss[0] = midbytes[0];
            ss[1] = midbytes[1];
        } else if (midbytes.length == 3) {
            ss = new byte[3];
            ss[0] = midbytes[0];
            ss[1] = midbytes[1];
            ss[2] = midbytes[2];
        } else if (midbytes.length == 4) {
            ss = new byte[4];
            ss[0] = midbytes[0];
            ss[1] = midbytes[1];
            ss[2] = midbytes[2];
            ss[3] = midbytes[3];
        } else if (midbytes.length == 5) {
            ss = new byte[5];
            ss[0] = midbytes[0];
            ss[1] = midbytes[1];
            ss[2] = midbytes[2];
            ss[3] = midbytes[3];
            ss[4] = midbytes[4];
        } else if (midbytes.length == 6) {
            ss = new byte[6];
            ss[0] = midbytes[0];
            ss[1] = midbytes[1];
            ss[2] = midbytes[2];
            ss[3] = midbytes[3];
            ss[4] = midbytes[4];
            ss[5] = midbytes[5];
        } else {
            if (midbytes.length == 1) {
                ss = new byte[1];
                ss[0] = midbytes[0];
            }
        }
        int ai[] = new int[19];
        ai[0] = 6500;
        int k;
        for (; i < 18; i = k) {
            k = i + 1;
            ai[k] = ai[i] + 200;
        }
        player.setFreqs(ai);
        player.play(DataEncoder.encodeMacWiFi(ss, ssidPwd.trim()), 8L, 1000);
        return;
    }


    private void sendSmartConnection(String s, String s1, String s2, boolean flag) {
//        LANCamera.startSearchLANCamera();
        initSound(s);
        setSmartLink();
        if (loader.InitSmartConnection("", null, 1, 1, 0) != 0)
            // LogTools.debug("camera_config", "MTK InitSmartConnection is error!!!!!!");
            loader.SetSendInterval(0.0F, 0.0F);
        int i = cameratype;
        if (i == 0) {
            i = intentFlag;
            if (i == 1) {
                if (!mConnectedSsid.equals("")) {
                    i = loader.StartSmartConnection(s1, s2, "");
                    isOpenSmartLink = true;
                    if (i != 0) {
                        //  LogTools.debug("camera_config", "MTK StartSmartConnection is error!!!!!!");
                        rHandler.sendEmptyMessageDelayed(1004, 20000L);
                        if (connWifiInterfacer != null) {
                            connWifiInterfacer.search_wifi_fail();
                        }
                    }
                    rHandler.sendEmptyMessage(1005);
                }
            } else if (i == 2)
                if (resultString.length() > 45) {
                    if (!StringUtils.isEmpty(jsonWiFi))
                        if (jsonWiFi.equals("1")) {
                            if (!mConnectedSsid.equals(""))
                                rHandler.sendEmptyMessage(1005);
                        } else if (jsonWiFi.equals("0") && !mConnectedSsid.equals("")) {
                            i = loader.StartSmartConnection(s1, s2, "");
                            isOpenSmartLink = true;
                            if (i != 0) {
                                //  LogTools.debug("camera_config", "MTK StartSmartConnection is error!!!!!!");
                                rHandler.sendEmptyMessageDelayed(1004, 20000L);
                                if (connWifiInterfacer != null) {
                                    connWifiInterfacer.search_wifi_fail();
                                }
                            }
                        }
                } else if (!mConnectedSsid.equals("")) {
                    i = loader.StartSmartConnection(s1, s2, "");
                    isOpenSmartLink = true;
                    if (i != 0) {
                        //  LogTools.debug("camera_config", "MTK StartSmartConnection is error!!!!!!");
                        rHandler.sendEmptyMessageDelayed(1004, 20000L);
                        if (connWifiInterfacer != null) {
                            connWifiInterfacer.search_wifi_fail();
                        }
                    }
                    rHandler.sendEmptyMessage(1005);
                }
        } else if (i == 2) {
            if (intentFlag == 1 && !mConnectedSsid.equals("")) {
                int j = loader.StartSmartConnection(s1, s2, "");
                isOpenSmartLink = true;
                if (j != 0) {
                    //  LogTools.debug("camera_config", "MTK StartSmartConnection is error!!!!!!");
                    rHandler.sendEmptyMessageDelayed(1004, 20000L);
                    if (connWifiInterfacer != null) {
                        connWifiInterfacer.search_wifi_fail();
                    }
                }
                rHandler.sendEmptyMessage(1005);
            }
        } else {
            rHandler.sendEmptyMessage(1005);
        }
    }


    private void setSmartLink() {
        mWifiManager = (WifiManager) getSystemService("wifi");
        if (mWifiManager.isWifiEnabled()) {
            Object obj = mWifiManager.getConnectionInfo();
            mConnectedSsid = ((WifiInfo) (obj)).getSSID();
            mLocalIp = ((WifiInfo) (obj)).getIpAddress();
            int i = mConnectedSsid.length();
            if (mConnectedSsid.startsWith("\"") && mConnectedSsid.endsWith("\""))
                mConnectedSsid = mConnectedSsid.substring(1, i - 1);
            obj = mWifiManager.getScanResults();
            i = 0;
            for (int j = ((List) (obj)).size(); i < j; i++) {
                ScanResult scanresult = (ScanResult) ((List) (obj)).get(i);
                if (!scanresult.SSID.equals(mConnectedSsid))
                    continue;
                boolean flag = scanresult.capabilities.contains("WPA-PSK");
                boolean flag1 = scanresult.capabilities.contains("WPA2-PSK");
                boolean flag2 = scanresult.capabilities.contains("WPA-EAP");
                boolean flag3 = scanresult.capabilities.contains("WPA2-EAP");
                if (scanresult.capabilities.contains("WEP")) {
                    mAuthString = "OPEN-WEP";
                    mAuthMode = AuthModeOpen;
                    return;
                }
                if (flag && flag1) {
                    mAuthString = "WPA-PSK WPA2-PSK";
                    mAuthMode = AuthModeWPA1PSKWPA2PSK;
                    return;
                }
                if (flag1) {
                    mAuthString = "WPA2-PSK";
                    mAuthMode = AuthModeWPA2PSK;
                    return;
                }
                if (flag) {
                    mAuthString = "WPA-PSK";
                    mAuthMode = AuthModeWPAPSK;
                    return;
                }
                if (flag2 && flag3) {
                    mAuthString = "WPA-EAP WPA2-EAP";
                    mAuthMode = AuthModeWPA1WPA2;
                    return;
                }
                if (flag3) {
                    mAuthString = "WPA2-EAP";
                    mAuthMode = AuthModeWPA2;
                    return;
                }
                if (flag2) {
                    mAuthString = "WPA-EAP";
                    mAuthMode = AuthModeWPA;
                    return;
                }
                mAuthString = "OPEN";
                mAuthMode = AuthModeOpen;
            }
        }
    }


    private void startPeiZhi(boolean flag) {
        getWifi();
        sendSmartConnection(sendMac, wifi_name, ssidPwd, flag);
    }


    Handler rHandler = new Handler() {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i != 999) {
                switch (i) {
                    default:
                        return;
                    case 1008:

                        if (!isFinishing()) {
                            if (loader != null && isOpenSmartLink) {
                                isOpenSmartLink = false;
                                loader.StopSmartConnection();
                            }
                            return;
                        }
                        break;

                    case 1007:
                        if (loader != null && isOpenSmartLink) {
                            isOpenSmartLink = false;
                            loader.StopSmartConnection();
                        }
                        break;

                    case 1006:
                        if (boThread != null && boThread.isAlive())
                            boThread.interrupt();
                        if (cameratype == 0) {
                            return;
                        } else {
                            int _tmp = cameratype;
                            return;
                        }

                    case 1005:
                        if (ssidPwd != null) {
                            boThread = new boThread(mConnectedSsid, ssidPwd.trim(), mLocalIp, true);
                            boThread.start();
                        }
                        rHandler.sendEmptyMessageDelayed(1006, 15000L);
                        if (cameratype == 0) {
                            return;
                        } else {
                            int _tmp1 = cameratype;
                            return;
                        }

                    case 1004:
                        if (loader != null && isOpenSmartLink) {
                            isOpenSmartLink = false;
                            loader.StopSmartConnection();
                            return;
                        }
                        break;

                    case 1003:
                        return;
                }
            } else {
                if (!isFinishing()) {
                    if (loader != null && isOpenSmartLink) {
                        isOpenSmartLink = false;
                        loader.StopSmartConnection();
                    }
                }
            }
        }
    };

    int mLocalIp;
    private Thread boThread;

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }


    private class boThread extends Thread {

        public void interrupt() {
            super.interrupt();
            isRun = false;
        }

        public void run() {
            super.run();
            while (isRun)
                Cooee.send(mssid, mpwd, mip);
        }

        private boolean isRun;
        private int mip;
        private String mpwd;
        private String mssid;


        private boThread(String s, String s1, int i, boolean flag) {
//            super();
            isRun = false;
            mssid = s;
            mpwd = s1;
            mip = i;
            isRun = flag;
        }
    }


    private static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static void printHexString(byte[] b) {
        // System.out.print(hint);
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print("aaa" + hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

    private void init_wifi_camera() {
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BridgeService.setAddCameraInterface(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mode_index = 0;
        dialogDismiss = true;
        over_conn_search();
        SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
        NativeCaller.Free();
        Intent intent = new Intent();
        intent.setClass(this, BridgeService.class);
        stopService(intent);
    }

    private boolean isOneKey;
    Runnable updateThread = new Runnable() {
        public void run() {
            NativeCaller.StopSearch();
            switch (doit_onekey) {
                case "search":
                    search_runnable();
                    break;
                case "onekey"://两次一键匹配之后，延时3s在去搜两次，
                    onekey_runnable();
                    break;
            }
        }
    };

    /**
     * 一键快配逻辑
     */
    private void onekey_runnable() {
        isOneKey = true;
        doit_onekey = "search";
        stop_new_peizhi();
        re_searchCamera();
    }

    private void re_searchCamera() {
        new Handler().postDelayed(() -> {
            updateListHandler.removeCallbacks(updateThread);
            doit_onekey = "search";
            first_search = false;
            searchCamera("");
        }, 10000);
    }

    /**
     * 摄像头搜索逻辑
     */
    private void search_runnable() {
        if (first_search) {
            SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", list_wifi_camera);
        }
        index_wifi++;
        if (!isOneKey) {
        }
        Log.e(TAG, "run: " + new Gson().toJson(list_wifi_camera));
        if (mode == null) return;
        switch (mode) {
            case "scan":
                if (index_wifi >= 2) {//搜索2次，6s
                    index_wifi = 0;
                    progress_camera_result();
                } else {
                    updateListHandler.removeCallbacks(updateThread);
                    first_search = false;
                    searchCamera("");
                }
                break;
            default:
                if (index_wifi >= 2) {//搜索2次，6s
                    index_wifi = 0;
                    if (isOneKey) { //2次一健匹配结束,2次搜索完毕
                        isOneKey = false;
                        progress_camera_result();
                    } else {
                        new_one_step_peiwang();
                    }
                } else {
                    updateListHandler.removeCallbacks(updateThread);
                    first_search = false;
                    searchCamera("");
                }
                break;
        }
    }

    private void progress_camera_result() {
        List<Map> list = SharedPreferencesUtil.getInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera");
        Map map_result = new HashMap();
        List<String> list_first = new ArrayList<>();
        List<String> list_second = new ArrayList<>();
        for (Map map : list_wifi_camera) {
            list_second.add(map.get("strMac").toString());
        }
        for (Map map : list) {
            list_first.add(map.get("strMac").toString());
        }
        String mac = "";
        boolean issave = false;
        if (list_second.size() == 0) {
            re_search_or_step_pei();
            return;
        }
        for (String str : list_second) {
            if (!list_first.contains(str)) {
                mac = str;
                issave = false;
                break;
            }
            issave = true;
        }
        if (issave) {//两个list相等  ，去一键快配
            re_search_or_step_pei();
        } else {
            scucess_search_result(map_result, mac);
        }
    }

    private void re_search_or_step_pei() {
        over_conn_search();
        if (mode == null) return;
        switch (mode) {
            case "scan":
                if (!dialogDismiss)
                    re_searchCamera();
                break;
            default:
                new Handler().postDelayed(() -> new_one_step_peiwang(), 6000);
                break;
        }
    }

    private void new_one_step_peiwang() {
        switch (mode) {
            case "new":
                if (currentBssid != null)
                    startNewSound("10000",
                            currentBssid.replace(":", ""), edit_password_gateway.getText().toString().trim());
                break;
            case "old":
                one_step_second();
                break;
        }
    }

    private void scucess_search_result(Map map_result, String mac) {
        for (Map map : list_wifi_camera) {
            if (map.get("strMac").toString().equals(mac)) {
                map_result = map;
            }
        }


        //1,2,3
        //1,2,3,4
//                                map_result = list_wifi_camera.get(list_wifi_camera.size() - 1);

        Intent intent = new Intent(ConnWifiCameraActivity.this,
                AddWifiCameraScucessActivity.class);
        map_result.put("type", type);
        intent.putExtra("wificamera", (Serializable) map_result);
        startActivity(intent);
        if (connWifiInterfacer != null) {
            connWifiInterfacer.conn_wifi_over();
        }

        List<Map> list_wifi_camera = SharedPreferencesUtil.getInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera_first");
        Map map = new HashMap<>();
        map.put("did", map_result.get("strDeviceID"));
        map.put("tag", 0);
        //  ToastUtil.showToast(activity, "更新tag: " + tag);
        boolean ishas = false;
        for (int i = 0; i < list_wifi_camera.size(); i++) {
            if (map.get("did").equals(list_wifi_camera.get(i).get("did"))) {
                ishas = true;
                break;
            }
        }
        if (!ishas)
            list_wifi_camera.add(map);

        for (int i = 0; i < list_wifi_camera.size(); i++) {
            list_wifi_camera.get(i).put("tag", 0);
        }

        SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera_first", list_wifi_camera);

//        if (!is_has) {
//            list_wifi_camera.add(map)
//        }

//        if (index == list_wifi_camera.size()) {
//            list_wifi_camera.add(map);
//        }
        SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
        ConnWifiCameraActivity.this.finish();
    }

    private String doit_onekey = "";
    /**
     * 延时一键匹配5秒后执行,在去搜
     */
    Runnable updateOneKeyThread = new Runnable() {
        public void run() {
            doit_onekey = "onekey";
            updateListHandler.postDelayed(updateThread, SEARCH_TIME);
        }
    };

    /**
     * 一键快搜第二步
     */
    private void one_step_second() {
        startPeiZhi(true);
        updateListHandler.postDelayed(updateOneKeyThread, ONE_KEY_TIME);
    }


    //新声波发送
    private void startNewSound(String userid, String mac, String pwd) {
        Log.d("voice_camera_config", "2 send sound：mac=" + mac + ", userid=" + userid + ", pwd=" + pwd);
        int freqs[] = new int[19];
        int baseFreq = 6500;
        for (int i = 0; i < freqs.length; i++) {
            freqs[i] = baseFreq + i * 150;
        }
        try {
            newplayer.setFreqs(freqs);//设置频率
            String sendStr = mac.replace(":", "") + userid;
            Log.d("voice_camera_config", "2 send sound：sendStr=" + sendStr);
            newplayer.play(voice2.encoder.DataEncoder.encodeSSIDWiFi(sendStr, pwd), 60, 1000);
            //ssid： wifi 的 ssid
            //pwd： wifi 的密码
            //1:播放声波的次数
            //1000:播放声波的间隔时间
            updateListHandler.postDelayed(updateOneKeyThread, ONE_KEY_TIME);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("voice_camera_config", "2 send sound：e=" + e);
            if (connWifiInterfacer != null) {
                connWifiInterfacer.search_wifi_fail();
            }
            return;
        }
    }

    private void stopNewSound() {
        if (newplayer != null) {
            newplayer.stop();
        }
    }

    Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private void searchCamera(String onclick) {
        startSearch(onclick);
    }

    private void startSearch(String onclick) {
        new Thread(new SearchThread()).start();
        switch (mode) {
            case "scan":
                updateListHandler.postDelayed(updateThread, SEARCH_TIME);
                switch (onclick) {
                    case "onclick":
                        if(mode_index == 1)
                            updateListHandler.postDelayed(updateThread, 3000);
                        break;
                }
                break;
            default:
                updateListHandler.postDelayed(updateThread, SEARCH_TIME);
                break;
        }
    }

    private class SearchThread implements Runnable {
        @Override
        public void run() {
            Log.e("robin debug", "startSearch");
            NativeCaller.StartSearch();
        }
    }

    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d("ip", "type:" + type + " param:" + param);
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {
        // TODO Auto-generated method stub
        Log.i("ip", "BSSnapshotNotify---len" + len);
    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1,
                                   String user2, String pwd2, String user3, String pwd3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void CameraStatus(String did, int status) {

    }

    /**
     * BridgeService callback
     **/
    @Override
    public void callBackSearchResultData(int sysver, String strMac,
                                         String strName, String strDeviceID, String strIpAddr, int port) {
        Log.e("AddCameraActivity", strDeviceID + strName);
//        if (!listAdapter.AddCamera(strMac, strName, strDeviceID)) {
//            return;
//        }
        Map map = new HashMap();
        map.put("strMac", strMac);
        map.put("strDeviceID", strDeviceID);
        map.put("strName", strName);
        map.put("wifi", wifi_name);
        list_wifi_camera.add(map);
        Log.e("robin debug", "strDeviceID:" + strDeviceID);
        HashSet<Map> h = new HashSet<Map>(list_wifi_camera);
        list_wifi_camera.clear();
        for (Map map_new : h) {
            list_wifi_camera.add(map_new);
        }
    }

    @Override
    public void CallBackGetStatus(String did, String resultPbuf, int cmd) {

    }

    private void onview() {
        eyeUtil = new EyeUtil(ConnWifiCameraActivity.this, eyeimageview_id_gateway, edit_password_gateway, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ConnWifiCameraActivity.this.finish();
                break;
            case R.id.select_wlan_rel_big:
                showPopwindow();
                break;
            case R.id.conn_btn_dev:
                dialogDismiss = false;
                one_search();
                break;
            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
        }
    }

    /**
     * 第一次搜索wifi摄像头
     */
    private void one_search() {
        if (edit_wifi.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiCameraActivity.this, "WIFI用户名为空");
            return;
        }
        if (edit_password_gateway.getText().toString().trim().equals("")) {
            ToastUtil.showToast(ConnWifiCameraActivity.this, "WIFI密码为空");
            return;
        }
        ssidPwd = edit_password_gateway.getText().toString().trim();
        SystemValue.deviceId = null;
        doit_onekey = "search";
        mode_clear_camera();
        searchCamera("onclick");
        mode_set_qr_bitmap();
        initDialog();
        show_dialog_fragment();
    }

    private void mode_clear_camera() {
        switch (mode) {
            case "scan":
                mode_index++;
                if (mode_index == 1) {
                    SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
                    first_search = true;
                }
                break;
            default:
                SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
                first_search = true;
                break;
        }
    }


    String getMac() {
        String macSerial = null;

        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");

            InputStreamReader ir = new InputStreamReader(pp.getInputStream());

            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial;
    }

    private void mode_set_qr_bitmap() {
        Log.e(TAG, "mode_set_qr_bitmap: mode:" + mode);
        if (mode == null) return;
        switch (mode) {
            case "scan":
                get_qr_map();
                break;
        }
    }

    private void get_qr_map() {
      //  LinkedHashMap map = new LinkedHashMap();
        //
        if (currentBssid == null)
            getWifi();
        Log.e(TAG, "mode_set_qr_bitmap: currentBssid:" + currentBssid);
        if (currentBssid == null) {
            ToastUtil.showToast(ConnWifiCameraActivity.this, "请重新切换WIFI");
            return;
        }
        StringBuffer str_buffer = new StringBuffer();
        str_buffer.append("{\"BS\":").append("\"").append(currentBssid.replace(":", "").trim()).append("\",");
        str_buffer.append("\"P\":").append("\"").append(edit_password_gateway.getText()).append("\",");
        str_buffer.append("\"U\":").append("\"").append("01").append("\",");
        str_buffer.append("\"RS\":").append("\"").append(edit_wifi.getText()).append("\"}");


     //   String strs = "{\"BS\":\"50bd5f25aebe\",\"P\":\"ztl62066206\",\"U\":\"01\",\"RS\":\"Massky_AP\"}";
//        map.put("BS", currentBssid.replace(":", "").trim());
//        map.put("P", edit_password_gateway.getText());
//        map.put("U", "14264307");
//        map.put("RS", edit_wifi.getText());
        mac_str = str_buffer.toString();
        Log.e(TAG, "mode_set_qr_bitmap: " + mac_str);
    }

    private void show_dialog_fragment() {
        if (!newFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newFragment, "dialog");
            ft.commit();
        }
    }

    //自定义dialog,centerDialog删除对话框
    private void showCenterDeleteDialog(String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        View view = LayoutInflater.from(ConnWifiCameraActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        TextView promat_txt;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        RelativeLayout confirm_rel = view.findViewById(R.id.confirm_rel);
        RelativeLayout rel_confirm = view.findViewById(R.id.rel_confirm);
        LinearLayout cancel_confirm_linear = view.findViewById(R.id.cancel_confirm_linear);
        cancel_confirm_linear.setVisibility(View.GONE);
        rel_confirm.setVisibility(View.VISIBLE);
        name_gloud.setText(name);
        tv_title.setVisibility(View.GONE);
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(ConnWifiCameraActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.setCancelable(false);
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        confirm.setOnClickListener(v -> {
            dialog.dismiss();
        });

        rel_confirm.setOnClickListener(v -> {
            dialog.dismiss();
            ConnWifiCameraActivity.this.finish();
        });
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(ConnWifiCameraActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
        TextView promat_txt;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
//        promat_txt = (TextView) view.findViewById(R.id.promat_txt);
        name_gloud.setText("您的手机wifi尚未启动,请先启动您的手机wifi；并连接您的路由器在进行操作。"
        );
//        promat_txt.setText("连接");
        tv_title.setText("是否启动wifi?");
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(ConnWifiCameraActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //连接wifi的相关代码,跳转到WIFI连接界面
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivityForResult(wifiSettingsIntent, CONNWIFI);
                dialog.dismiss();
                handler.sendEmptyMessage(1);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 887:
                if (!GpsUtil.isOPen(ConnWifiCameraActivity.this)) {
                    ToastUtil.showToast(ConnWifiCameraActivity.this, "定位没有加开，无法添加设备");
                } else {
                    initWifiName();
                    initWifiConect();
                }
                break;
            default:
                initWifiName();
                //判断wifi已连接的条件
                // TODO Auto-generated method stub
                //获取系统服务
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取状态
                NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                    one_step_peizhi();//一键配wifi
                }
                break;
        }

    }

    /**
     * 获取连接的wifi名称
     */
    private void initWifiName() {

        // TODO Auto-generated method stub
        String wifiId = wifiUtil.getWIFISSID(ConnWifiCameraActivity.this);
        if (wifiId != null && !wifiId.contains("<unknown ssid>")) {//wifiId在不连WIFI的情况下，去wifi快配wifiId = 0x
            edit_wifi.setText(wifiId);
            wifi_name = wifiId;
            edit_password_gateway.setFocusable(true);
            edit_password_gateway.setFocusableInTouchMode(true);
            edit_password_gateway.requestFocus();
        }
    }

    /**
     * 初始化wifi连接，快配前wifi一定要连接上
     */
    private void initWifiConect() {
        //初始化连接wifi dialog对话框
        // TODO Auto-generated method stub
        //获取系统服务
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取状态
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //判断wifi已连接的条件
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            if (wifiUtil == null) return;
            if (wifiUtil.is5GWifi()) {
                showCenterDeleteDialog("当前WIFI网络是5G，不能添加WIFI设备；请切换到2.4G，在重新操作");
                return;
            }
            one_step_peizhi();//一键配wifi
        } else {//wifi还没有连接上弹出alertDialog对话框
            showCenterDeleteDialog();
        }
    }

    /**
     * 添加红外模块配置入网成功后关闭进度圈
     */
    private ConnWifiInterfacer connWifiInterfacer;

    public interface ConnWifiInterfacer {
        void conn_wifi_over();

        void search_wifi_fail();

    }

    /**
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigDialogCameraFragment.newInstance(ConnWifiCameraActivity.this, mode, mac_str, new ConfigDialogCameraFragment.DialogClickListener() {

            @Override
            public void doRadioWifi() {//wifi快速配置

            }

            @Override
            public void doRadioScanDevice() {
                // searchCamera();
                over_conn_search();
                //SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
                new Handler().postDelayed(() -> {
                    // one_step_second();
                    new_one_step_peiwang();
                }, 6000);
                show_dialog_fragment();
            }

            @Override
            public void dialogDismiss() {
                dialogDismiss = true;
                over_conn_search();
            }
        });//初始化快配和搜索设备dialogFragment
        connWifiInterfacer = (ConnWifiInterfacer) newFragment;
    }

    private void over_conn_search() {
        updateListHandler.removeCallbacks(updateThread);
        updateListHandler.removeCallbacks(updateOneKeyThread);
        NativeCaller.StopSearch();
        stop_new_peizhi();
        doit_onekey = "isover";
        isOneKey = false;
        index_wifi = 0;
        list_wifi_camera.clear();
        //SharedPreferencesUtil.saveData(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
        //   SharedPreferencesUtil.saveInfo_List(ConnWifiCameraActivity.this, "list_wifi_camera", new ArrayList<>());
    }

    private void stop_new_peizhi() {
        if (mode == null) {
            return;
        }
        switch (mode) {
            case "new":
                stopNewSound();
                break;
            case "old":
                stop_old_sound();
                break;
        }
    }

    private void stop_old_sound() {
        if (loader != null && isOpenSmartLink) {
            isOpenSmartLink = false;
            try {
                loader.StopSmartConnection();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (player != null)
            player.stop();
        if (boThread != null && boThread.isAlive())
            boThread.interrupt();
    }


    /**
     * 显示popupWindow
     */
    @SuppressLint("WrongConstant")
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View outerView = inflater.inflate(R.layout.wheel_view, null);

//        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        ListView wv = (ListView) outerView.findViewById(R.id.wheel_view_wv);
//        wv.setOffset(1);
//        wv.setItems(Arrays.asList(PLANETS));
//        wv.setSeletion(2);
//        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
//            }
//        });

        final String[] data = {"类型一", "类型二", "类型三", "类型四"};
        ArrayAdapter<String> array = new ArrayAdapter<>(this,
                R.layout.simple_expandable_list_item_new, data);
        wv.setAdapter(array);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;

        final PopupWindow window = new PopupWindow(outerView,
                displayWidth / 2,
                WindowManager.LayoutParams.WRAP_CONTENT);//高度写死

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;

        getWindow().setAttributes(lp);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.style_pop_animation);
        // 在底部显示
//        window.showAtLocation(WuYeTouSu_NewActivity.this.findViewById(R.id.tousu_select),
//                Gravity.CENTER_HORIZONTAL, 0, 0);
//        window.showAsDropDown(select_wlan_rel_big);

        // 将pixels转为dip
        int xoffInDip = pxTodip(displayWidth) / 4 * 3;

        window.showAsDropDown(select_wlan_rel_big, xoffInDip, xoffInDip / 3);
        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        wv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                tousu_txt1.setText(data[position]);
                window.dismiss();
            }
        });
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int pxTodip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
