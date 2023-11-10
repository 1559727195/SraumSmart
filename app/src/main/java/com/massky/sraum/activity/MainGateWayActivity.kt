package com.massky.sraum.activity

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import cn.jpush.android.api.JPushInterface
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.dialog.CommonData
import com.dialog.CommonDialogService
import com.dialog.ToastUtils
import com.example.jpushdemo.Constants
import com.example.jpushdemo.ExampleUtil
import com.example.jpushdemo.Logger
import com.example.jpushdemo.MyReceiver
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode
import com.ipcamera.demo.BridgeService
import com.larksmart7618.sdk.communication.tools.commen.ToastTools
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.*
import com.massky.sraum.activity.MainGateWayActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragmentNew
import com.massky.sraum.fragment.MineFragment
import com.massky.sraum.fragment.SceneFragment
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.view.DownLoadProgressbar
import com.yaokan.sdk.api.YkanSDKManager
import com.yaokan.sdk.ir.InitYkanListener
import com.yaokan.sdk.utils.Utility
import com.yaokan.sdk.wifi.DeviceManager
import com.yaokan.sdk.wifi.GizWifiCallBack
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import vstc2.nativecaller.NativeCaller
import webapp.config.SystemParams
import webapp.download.DownLoadUtils
import webapp.download.DownloadApk
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhu on 2018/1/2.
 */
class MainGateWayActivity : BaseActivity(), InitYkanListener {
    private var commdialog_service: CommonDialogService? = null
    private var fragment1: HomeFragmentNew? = null
    private var fragment2: SceneFragment? = null
    private var fragment3: MineFragment? = null
    private val currentFragment: Fragment? = null

    //    public static String UPDATE_GRADE_BOX = "com.massky.sraum.update_grade_box";
    private var init_jizhiyun //机智云index
            = 0
    private var dialogUtil: DialogUtil? = null
    var popupWindow: PopupWindow? = null

    //    private RelativeLayout addmac_id, addscene_id, addroom_id;
    //主要保存当前显示的是第几个fragment的索引值
    private var exitTime: Long = 0
    private var checkbutton_id: Button? = null
    private var qxbutton_id: Button? = null
    private var dtext_id: TextView? = null
    private var belowtext_id: TextView? = null
    private var viewDialog: DialogUtil? = null
    private val usertype: String? = null
    private var Version: String? = null
    private var versionCode = 0
    private var index = 0
    private val handler_wifi: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
//            init_jizhiyun();
        }
    }

    private var dialog1: Dialog? = null
    private var mMessageReceiver_aboutfragment: MessageReceiver? = null
    private val mMessageReceiver_apk_down_load: MessageReceiver? = null
    private var weakReference: WeakReference<Context>? = null
    private var iswait_down_load //等待NotificationListenerService这个服务唤醒
            = false
    private var onresumes = false
    private val isdo: String? = null
    private var mMessageReceiver_tongzhi_open: MessageReceiver? = null
    private var mDownloadManager: DownLoadUtils? = null
    private var mDownLoadChangeObserver: DownloadChangeObserver? = null
    private var mProgress: DownLoadProgressbar? = null
    override fun viewId(): Int {
        return R.layout.main_gateway_act
    }

    override fun onView() {
        try {
            init_common_dialog_service()
            add_page_select()
            common_second()
            //        iswait_down_load = false;
            init_jizhiyun_1()
            init_video()
            init_receiver()
            init_download_apk()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun init_common_dialog_service() {
        commdialog_service = CommonDialogService().getInstance()
        commdialog_service!!.init()
    }

    /**
     * 打开Gps设置界面
     */
    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, 887)
    }

    private fun init_download_apk() {
        showCenterDeleteDialog("正在下载更新",
                "")
        //1.注册下载广播接收器
        DownloadApk.registerBroadcast(this)
        //2.删除已存在的Apk
        DownloadApk.removeFile(this)
        AppDownloadManager(this@MainGateWayActivity)
    }

    fun AppDownloadManager(context: Context) {
//        activity = (Activity) context;
        weakReference = WeakReference(context)
        //        mDownloadManager = (DownloadManager) weakReference.get().getSystemService(Context.DOWNLOAD_SERVICE);
//        mDownLoadChangeObserver = new AppDownloadManager.DownloadChangeObserver(new Handler());
//        mDownloadReceiver = new AppDownloadManager.DownloadReceiver();
        mDownloadManager = DownLoadUtils.getInstance(applicationContext)
        mDownLoadChangeObserver = DownloadChangeObserver(Handler())
    }

    /**
     * 对应 { Activity#onResume }
     */
    fun resume1() { //
        //设置监听Uri.parse("content://downloads/my_downloads")
        weakReference!!.get()!!.contentResolver.registerContentObserver(Uri.parse("content://downloads/my_downloads"), true,
                mDownLoadChangeObserver!!)
        // 注册广播，监听APK是否下载完成
    }


    /**
     * 对应{ Activity#onPause()} ()}
     */
    fun onPause1() {
        weakReference!!.get()!!.contentResolver.unregisterContentObserver(mDownLoadChangeObserver!!)
    }

    internal inner class DownloadChangeObserver
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run [.onChange] on, or null if none.
     */
    (handler: Handler?) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            updateView()
        }
    }

    private fun updateView() {
        val bytesAndStatus = intArrayOf(0, 0, 0)
        //获取存储的下载ID
        val downloadId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var c: Cursor? = null
        try {
            c = mDownloadManager!!.downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                //状态所在的列索引
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            c?.close()
        }

//        long istext = SDCardSizeTest(apkFile);
//        Log.e("robin debug", "istext:" + istext + "");
        val num = bytesAndStatus[0].toFloat() / bytesAndStatus[1].toFloat()
        val result = (num * 100).toInt() //把获取的浮点数计算结果转换为整数
        Log.e(TAG, "bytesAndStatus[0]：" + bytesAndStatus[0])
        Log.e(TAG, "bytesAndStatus[1]：" + bytesAndStatus[1])
        Log.e(TAG, "下载进度：$result%")
        if (bytesAndStatus[0] != 0) {
            if (!dialog1!!.isShowing) dialog1!!.show()
        } else {
            //获取存储的下载ID
            if (downloadId != -1L) {
                //存在downloadId
                val downLoadUtils = DownLoadUtils.getInstance(this@MainGateWayActivity)
                //获取当前状态
                val status = downLoadUtils.getDownloadStatus(downloadId)
                if (-1 == status) {
                    //下载已经取消
                    if (dialog1 != null) dialog1!!.dismiss()
                    //                    MainfragmentActivity.this.finish();
//                    AppManager.getAppManager().finishAllActivity();
                } else if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    if (dialog1 != null) dialog1!!.dismiss()
                }
            }
        }
        mProgress!!.setMaxValue(100f)
        mProgress!!.setCurrentValue(result.toFloat())
    }


    private fun init_receiver() {
        //在这里发送广播，expires_in是86400->24小时
        val expires_in = SharedPreferencesUtil.getData(this@MainGateWayActivity, "expires_in", "") as String
        val broadcast = Intent("com.massky.sraum.broadcast")
        broadcast.putExtra("expires_in", expires_in)
        broadcast.putExtra("timestamp", TokenUtil.getLogintime(this@MainGateWayActivity))
        sendBroadcast(broadcast)

//        addPopwinwow();
        try {
            versionCode = VersionUtil.getVersionCode(this@MainGateWayActivity).toInt()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        dialog
        registerMessageReceiver()
        registerMessageReceiver_fromAbout()
        registerMessageReceiver_tongzhi_open()
        //        registerMessageReceiver_fromApk_Down();
//        init_notifacation();//通知初始化
        SharedPreferencesUtil.saveData(this@MainGateWayActivity, "loadapk", false) //apk版本正在更新中
    }

    private fun init_jizhiyun_1() {
        init_jizhiyun = 1 //机智云index
        dialogUtil = DialogUtil(this)
        //        toggleNotificationListenerService();
        over_camera_list() //结束wifi摄像头的tag
    }

    private fun init_video() {
        val intent = Intent()
        intent.setClass(this@MainGateWayActivity, BridgeService::class.java)
        startService(intent)
        Thread {
            try {
                NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL")
                Thread.sleep(3000)
                val msg = Message()
                mHandler.sendMessage(msg)
                Log.e("vst", "path" + applicationContext.filesDir.absolutePath)
                NativeCaller.SetAPPDataPath(applicationContext.filesDir.absolutePath)
            } catch (e: java.lang.Exception) {

            }
        }.start()
    }


    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
//            val `in` = Intent(this@MainGateWayActivity, AddCameraActivity::class.java)
//            startActivity(`in`)
//            finish()
            val mIntent = Intent(ACTION_INTENT_CAMERA_RECEIVER)
            sendBroadcast(mIntent)
        }
    }

    /**
     * 获取文件长度
     */
    fun getFileSize(file: File): Long {
        if (file.exists() && file.isFile) {
            val fileName = file.name
            println("文件" + fileName + "的大小是：" + file.length())
            return file.length()
        }
        return 0
    }

    override fun onInitStart() {}
    override fun onInitFinish(status: Int, errorMsg: String) {
//         ToastUtil.showToast(MainfragmentActivity.this,"初始化成功");
//         DeviceManager.instanceDeviceManager(getApplicationContext()).userLoginAnonymous();//匿名登录
        if (dialogUtil != null) dialogUtil!!.removeDialog()
        if (status == InitYkanListener.INIT_SUCCESS) {
            runOnUiThread { //                     ToastUtil.showToast(MainfragmentActivity.this,"SDK初始化成功");
                Handler().postDelayed({ DeviceManager.instanceDeviceManager(applicationContext).userLoginAnonymous() }, 2000)
            }
        } else {
            runOnUiThread {
                ToastTools.short_Toast(this@MainGateWayActivity, errorMsg)
                AlertDialog.Builder(this@MainGateWayActivity).setTitle("error").setMessage(errorMsg).setPositiveButton("ok", null).create().show()
            }
        }
    }

    private val TAG = "robin debug"
    private val mGizWifiCallBack: GizWifiCallBack = object : GizWifiCallBack() {
        override fun didUnbindDeviceCd(result: GizWifiErrorCode, did: String) {
            super.didUnbindDeviceCd(result, did)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, "解除绑定成功")
            } else {
                // 解绑失败
                Logger.d(TAG, "解除绑定失败")
            }
        }

        override fun didBindDeviceCd(result: GizWifiErrorCode, did: String) {
            super.didBindDeviceCd(result, did)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 绑定成功
                Logger.d(TAG, "绑定成功")
                ToastUtil.showToast(this@MainGateWayActivity, "绑定成功")
            } else {
                // 绑定失败
                Logger.d(TAG, "绑定失败")
            }
        }

        override fun didSetSubscribeCd(result: GizWifiErrorCode, device: GizWifiDevice, isSubscribed: Boolean) {
            super.didSetSubscribeCd(result, device, isSubscribed)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, (if (isSubscribed) "订阅" else "取消订阅") + "成功")
                ToastUtil.showToast(this@MainGateWayActivity, (if (isSubscribed) "订阅" else "取消订阅") + "成功")
            } else {
                // 解绑失败
                Logger.d(TAG, "订阅失败")
            }
        }

        override fun discoveredrCb(result: GizWifiErrorCode,
                                   deviceList: List<GizWifiDevice>) {
            Logger.d(TAG,
                    "discoveredrCb -> deviceList size:" + deviceList.size
                            + "  result:" + result)
            when (result) {
                GizWifiErrorCode.GIZ_SDK_SUCCESS -> {
                    Logger.e(TAG, "load device  sucess")
                    update(deviceList)
                }
                else -> {

                }
            }
        }
    }

    fun update(gizWifiDevices: List<GizWifiDevice>?) {
        var mGizWifiDevice: GizWifiDevice? = null
        if (gizWifiDevices != null) {
            Log.e("DeviceListActivity", gizWifiDevices.size.toString() + "")
            Log.e("MainActivity", gizWifiDevices.size.toString() + "")
            //红外转发器绑定设备
            for (i in gizWifiDevices.indices) {
                mGizWifiDevice = gizWifiDevices[i]
                // list_hand_scene
                // 绑定选中项
                if (!Utility.isEmpty(mGizWifiDevice) && !mGizWifiDevice.isBind) {
                    DeviceManager.instanceDeviceManager(applicationContext).bindRemoteDevice(mGizWifiDevice)
                    val finalMGizWifiDevice = mGizWifiDevice
                    handler_wifi.postDelayed({ DeviceManager.instanceDeviceManager(applicationContext).setSubscribe(finalMGizWifiDevice, true) }, 1000)
                }
            }
        }
    }

    private fun init_notifacation() {
        val intent = intent
        if (null != intent) {
            val bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE)
            val title: String? = null //JingRuiApp
            val content: String? = null //2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) {
                SharedPreferencesUtil.saveData(this@MainGateWayActivity, "tongzhi_time", 1)
                //视频监控，极光push不太好用；
                init_nofication(intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) { //进到这里来说明app没有退出去；
        super.onNewIntent(intent)
        SharedPreferencesUtil.saveData(this@MainGateWayActivity, "tongzhi_time", 1)
        //视频监控，极光push不太好用；
        getNotify(intent)
        setIntent(intent)
    }

    private fun getNotify(intent: Intent) {
        init_nofication(intent) //暂时注销掉，fragment->childactivity-> fragment->mainactivity时,执行
        // 这里不是用的commit提交，用的commitAllowingStateLoss方式。commit不允许后台执行，不然会报Deferring update until onResume 错误
        super.onNewIntent(intent)
    }

    /**
     * 初始化通知
     *
     * @param intent
     */
    private fun init_nofication(intent: Intent?) {
        if (null != intent) {
            val bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE)
            //            String title = null;//JingRuiApp
//            String content = null;//2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) { //bundle = null  了
                val extras = bundle.getString(JPushInterface.EXTRA_EXTRA) ////{"type":"2"}
                var json: JSONObject? = null
                try {
                    json = JSONObject(extras)
                    val type = json.getString("type")
                    val uid = json.getString("uid")
                    sendBroad(type, uid)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    //for receive customer msg from jpush server
    private var mMessageReceiver: MessageReceiver? = null
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MESSAGE_RECEIVED_ACTION)
        registerReceiver(mMessageReceiver, filter)
    }

    fun registerMessageReceiver_fromAbout() {
        mMessageReceiver_aboutfragment = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT)
        registerReceiver(mMessageReceiver_aboutfragment, filter)
    }

    fun registerMessageReceiver_tongzhi_open() {
        mMessageReceiver_tongzhi_open = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MyReceiver.ACTION_NOTIFICATION_OPENED_MAIN)
        registerReceiver(mMessageReceiver_tongzhi_open, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION == intent.action) {
                    val messge = intent.getStringExtra(KEY_MESSAGE)
                    val extras = intent.getStringExtra(KEY_EXTRAS)
                    val showMsg = StringBuilder()
                    showMsg.append("""$KEY_MESSAGE : $messge
""")
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append("""$KEY_EXTRAS : $extras
""")
                    }
                    val loginflag = SharedPreferencesUtil.getData(CommonData.mNowContext, "loginflag", false) as Boolean
                    //                    ToastUtil.showToast(CommonData.mNowContext,"MainfragmentActivity-loginflag:" + loginflag);
                    if (loginflag) ToastUtils.getInstances().showDialog("账号在其他地方登录，请重新登录。")
                } else if (MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT == intent.action) {
                    AppManager.getAppManager().finishActivity_current(AboutActivity::class.java)
                    val UpApkUrl = ApiHelper.UpdateApkUrl + "sraum" + Version + ".apk"
                    val apkName = "sraum$Version.apk"
                    Log.e("fei", "UpApkUrl:$UpApkUrl")
                    //                    UpdateManager manager = new UpdateManager(MainGateWayActivity.this, UpApkUrl, apkName);
//                    updateApkListener = (UpdateApkListener) manager;
//                    manager.showDownloadDialog();
                    //3.如果手机已经启动下载程序，执行downloadApk。否则跳转到设置界面
                    if (DownLoadUtils.getInstance(applicationContext).canDownload()) {
                        if (dialog1 != null) {
                            if (!dialog1!!.isShowing) dialog1!!.show()
                        }
                        DownloadApk.downloadApk(applicationContext, UpApkUrl, "众天力智家更新", apkName)
                    } else {
                        DownLoadUtils.getInstance(applicationContext).skipToDownloadManager()
                    }
                } else if (SRAUM_IS_DOWN_LOAD == intent.action) { //apk正在下载
//                    ToastUtil.showToast(MainfragmentActivity.this, "apk正在下载中");
                    iswait_down_load = true
                } else if (MyReceiver.ACTION_NOTIFICATION_OPENED_MAIN == intent.action) {
                    SharedPreferencesUtil.saveData(this@MainGateWayActivity, "tongzhi_time", 1)
                    AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    init_nofication(intent) //暂时注销掉，fragment->childactivity-> fragment->mainactivity时,执行
                }
            } catch (e: Exception) { //SRAUM_IS_DOWN_LOAD
            }
        }
    }

    /*
     * 通知
     * */
    private fun sendBroad(content: String, bundle: String?) {
        setTabSelection(0)
        val mIntent = Intent(MESSAGE_TONGZHI)
        mIntent.putExtra("uid", bundle
                ?: "") //    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
        mIntent.putExtra("type", content)
        sendBroadcast(mIntent)
    }

    private fun updateApk() {
//        boolean tokenflag = TokenUtil.getTokenflag(MainfragmentActivity.this);
//        if (tokenflag) {
//            sraum_get_version();
//        }
        var isdownload = false
        val downloadId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        //获取存储的下载ID
        if (downloadId != -1L) {
            //存在downloadId
            val downLoadUtils = DownLoadUtils.getInstance(this@MainGateWayActivity)
            //获取当前状态
            val status = downLoadUtils.getDownloadStatus(downloadId)
            if (DownloadManager.STATUS_SUCCESSFUL == status) {
                //状态为下载成功
                //获取下载路径URI
                if (dialog1 != null) dialog1!!.dismiss()
            } else if (DownloadManager.STATUS_FAILED == status) {
            } else if (status == -1) {
                if (dialog1 != null) dialog1!!.dismiss()
            } else {
                isdownload = true //apk正在下载中
                //                Log.d(context.getPackageName(), "apk is already downloading");
            }
        }
        if (!isdownload) sraum_get_version()
    }


    private fun sraum_get_version() {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = TokenUtil.getToken(this@MainGateWayActivity)
        MyOkHttp.postMapObject(ApiHelper.sraum_getVersion, map,
                object : Mycallback(AddTogglenInterfacer { sraum_get_version() }, this@MainGateWayActivity, viewDialog) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        Version = user.version
                        Log.e("fei", "Version:$Version")
                        try {
                            catch_str_int(user)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun catch_str_int(user: User) {
        val sracode = user.versionCode.toInt() //user.versionCode
        if (versionCode < sracode) {
            //在这里判断有没有正在更新的apk,文件大小小于总长度即可
//                            weakReference = new WeakReference<Context>(App.getInstance());
//                            File apkFile = new File(weakReference.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app_name.apk");
//                            if (apkFile != null && apkFile.exists()) {
//                                long apksize = 0;
//                                try {
//                                    apksize = getFileSize(apkFile);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                //获取已经下载字节数
////                               String totalSize =  getDataTotalSize(MainfragmentActivity.this,apkFile.getAbsolutePath());
////                              Integer.parseInt(totalSize);
//
//                                int totalapksize = (int) SharedPreferencesUtil.getData(MainGateWayActivity.this, "apk_fileSize", 0);
//                                if (totalapksize == 0) {//则说明，还没有下载过
//                                    belowtext_id.setText("版本更新至" + Version);
//                                    viewDialog.loadViewdialog();
//                                    return;
//                                }
//                                if (apksize - totalapksize == 0) { //说明正在下载或者下载完毕，安装失败时，//->或者是下载完毕后没有去安装；
////                                    down_load_thread();
//                                    ToastUtil.showToast(MainGateWayActivity.this, "检测到有新版本，正在下载中");
//                                }
//                            } else {//不存在，apk文件
//                                belowtext_id.setText("版本更新至" + Version);
//                                viewDialog.loadViewdialog();
//                            }
            belowtext_id!!.text = "版本更新至$Version"
            viewDialog!!.loadViewdialog()
        } else { //没有可更新的apk时
            SharedPreferencesUtil.saveData(this@MainGateWayActivity, "apk_fileSize", 0)
        }
    }

    //用于设置dialog展示
    private val dialog: Unit
        private get() {
            val view = layoutInflater.inflate(R.layout.check, null)
            checkbutton_id = view.findViewById<View>(R.id.checkbutton_id) as Button
            qxbutton_id = view.findViewById<View>(R.id.qxbutton_id) as Button
            dtext_id = view.findViewById<View>(R.id.dtext_id) as TextView
            belowtext_id = view.findViewById<View>(R.id.belowtext_id) as TextView
            dtext_id!!.text = "发现新版本"
            checkbutton_id!!.text = "立即更新"
            qxbutton_id!!.text = "以后再说"
            viewDialog = DialogUtil(this@MainGateWayActivity, view)
            checkbutton_id!!.setOnClickListener(this)
            qxbutton_id!!.setOnClickListener(this)
        }

    /**
     * 切换Fragment
     *
     * @param
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun tongzhi_door(type: String, uid: String) {
        val mIntent = Intent(MESSAGE_TONGZHI_DOOR)
        mIntent.putExtra("type", type)
        mIntent.putExtra("uid", uid)
        sendBroadcast(mIntent)
    }

    private val updateApkListener: UpdateApkListener? = null

    interface UpdateApkListener {
        fun sendTo_UPApk()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.showDelToast(this@MainGateWayActivity, "再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                iswait_down_load = true
                over_camera_list() //结束wifi摄像头的tag
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name1: String?, name2: String?) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@MainGateWayActivity).inflate(R.layout.promat_download_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        mProgress = view.findViewById<View>(R.id.dp_game_progress) as DownLoadProgressbar
        mProgress!!.setMaxValue(100f)
        name_gloud.text = name1
        tv_title.text = name2
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        dialog1 = Dialog(this@MainGateWayActivity, R.style.BottomDialog)
        dialog1!!.setContentView(view)
        dialog1!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog1!!.setCancelable(true) //设置它可以取消
        dialog1!!.setCanceledOnTouchOutside(false)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog1!!.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        p.height = (displayWidth * 0.4).toInt()
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog1!!.window!!.attributes = p //设置生效
        //        dialog1.show();
        cancel.setOnClickListener {
            //                dialog1.dismiss();
//                dialog1.dismiss();
//                over_camera_list();//结束wifi摄像头的tag
//                MainfragmentActivity.this.finish();
            //删除下载任务以及文件
            val downloadId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            //获取存储的下载ID
            if (downloadId != -1L) {
                //存在downloadId
                val downLoadUtils = DownLoadUtils.getInstance(this@MainGateWayActivity)
                downLoadUtils.downloadManager.remove(downloadId)
                dialog1!!.dismiss()
            }
        }
        confirm.setOnClickListener {
            ////                SharedPreferencesUtil.saveData(MainfragmentActivity.this, "loadapk", false);
////                over_broad_apk_load();
//                startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), TONGZHI_APK_UPGRATE);
        }
    }

    private fun over_broad_apk_load() {
//        Intent broadcast = new Intent(MESSAGE_RECEIVED_ACTION_APK_LOAD);
//        sendBroadcast(broadcast);
        updateApkListener?.sendTo_UPApk()
        finish()
        over_camera_list() //结束wifi摄像头的tag
    }

    /**
     * 清除wifi摄像头列表
     */
    private fun over_camera_list() {
//        val list = SharedPreferencesUtil.getInfo_List(this@MainGateWayActivity, "list_wifi_camera_first")
//        val list_second: MutableList<Map<*, *>> = ArrayList()
//        for (i in list.indices) {
//            val map = HashMap<Any?, Any?>()
//            map["did"] = list[i]["did"]
//            map["tag"] = 0
//            list_second.add(map)
//        }
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_wifi_camera_first", ArrayList())
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_second_total", ArrayList())
        // SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_wifi_camera_first", list_second)
    }

    override fun onPause() {
        Log.e("init", "onPause: ")
        //onPause1()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        onresumes = false
        onPause1()
        Log.e("init", "onStop: ")
    }

    override fun onResume() {
        if (!onresumes) {
            onresumes = true
            Log.e("init", "onResume: ")
            init_jizhiyun_onresume()
            resume1()
        }
        super.onResume()
    }

    /**
     * 初始化onResume动作
     */
    private fun init_jizhiyun_onresume() {//
        init_jizhi_cloud() //耗时动作，最好都放在onResume里，此时屏幕已经亮了
        isForegrounds = true
        Log.e("zhu-", "MainfragmentActivity:onResume():isForegrounds:" + isForegrounds)
        val netflag = NetUtils.isNetworkConnected(this@MainGateWayActivity)
        if (netflag) { //获取版本号
            updateApk()
        }
    }

    /**
     * 机智云小苹果初始化
     */
    private fun init_jizhi_cloud() {
        //小苹果机智云初始化
//        init_jizhiyun = 1;
        if (init_jizhiyun == 1) {
            init_jizhiyun = 2
            Thread {
                Looper.prepare()
                //                    init_visible();
                init_jizhiyun()
                Looper.loop()
            }.start()
            Log.e("robin debug", "    Looper.prepare()")
            init_notifacation() //通知初始化,在这里执行，onResume之后屏幕就亮了，屏幕亮后才加载UI资源文件
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //        switch (requestCode) {
//            case TONGZHI_APK_UPGRATE:
//                if (isEnabled()) {//监听通知权限开启
//                    if (dialog1 != null)
//                        dialog1.dismiss();
//                    updateApk();
//                } else {
//                    if (!dialog1.isShowing()) {
//                        dialog1.show();
//                    }
//                }
//                break;
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        ToastUtil.showToast(MainfragmentActivity.this,"MainfragmentActivity:destroy");
        over_camera_list() //结束wifi摄像头的tag
        common_second()
        //        over_broad_apk_load();
        if (dialog1 != null) {
            dialog1!!.dismiss()
        }
        unregisterReceiver(mMessageReceiver_aboutfragment)
        //        unregisterReceiver(mMessageReceiver_apk_down_load);
//        iswait_down_load = false;

//        /**
//         * 开启download监听service
//         */
//        Intent intent_apk_down = new Intent();
//        intent_apk_down.setClass(MainfragmentActivity.this, NotificationMonitorService.class);
//        stopService(intent_apk_down);
        destroy_commnon_service()
    }

    private fun destroy_commnon_service() {
        if (commdialog_service != null)
            commdialog_service!!.ondestroy()
        commdialog_service = null
    }

    /**
     * 初始化机智云小苹果
     */
    private fun init_jizhiyun() {
        initListener()
        // 初始化SDK

//        dialogUtil.loadDialog();
        YkanSDKManager.init(this@MainGateWayActivity, this@MainGateWayActivity)
        //需要剥离机智云的用户调用此方法初始化
//        YkanSDKManager.custInit(this,false);
        // 设置Log信息是否打印
        YkanSDKManager.getInstance().setLogger(true)
    }

    private fun initListener() {
        DeviceManager.instanceDeviceManager(applicationContext).setGizWifiCallBack(object : GizWifiCallBack() {
            override fun didBindDeviceCd(result: GizWifiErrorCode, did: String) {
                super.didBindDeviceCd(result, did)
            }

            override fun didTransAnonymousUser(result: GizWifiErrorCode) {
                super.didTransAnonymousUser(result)
            }

            /** 用于用户登录的回调  */
            override fun userLoginCb(result: GizWifiErrorCode, uid: String, token: String) {
                ToastUtil.showToast(this@MainGateWayActivity, "result:$result")
                if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) { // 登陆成功
                    Constants.UID = uid
                    Constants.TOKEN = token
                    //                    toDeviceList();
                    //登录成功
                    //去绑定订阅
//
//                    ToastUtil.showToast(MainfragmentActivity.this, "小苹果登录成功!");
//                    wiif_login_scuess();
                    SharedPreferencesUtil.saveData(this@MainGateWayActivity, "apple_login", true)
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USER_NOT_EXIST) { // 用户不存在
                } else if (result == GizWifiErrorCode.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR) { //// 用户名或者密码错误
                } else {
//                    toast("登陆失败，请重新登录");
                    index++
                    if (index >= 2) {
                    } else {
                        DeviceManager.instanceDeviceManager(applicationContext).userLoginAnonymous() //匿名登录
                    }
                    SharedPreferencesUtil.saveData(this@MainGateWayActivity, "apple_login", false)
                }
            }

            /** 用于用户注册的回调  */
            override fun registerUserCb(result: GizWifiErrorCode, uid: String, token: String) {}

            /** 用于发送验证码的回调  */
            override fun didRequestSendPhoneSMSCodeCb(result: GizWifiErrorCode) {}

            /** 用于重置密码的回调  */
            override fun didChangeUserPasswordCd(result: GizWifiErrorCode) {}
        })
    }

    /**
     * fragment卡片选择
     */
    private fun add_page_select() {
        for (i in 0..2) {
            setTabSelection(i)
        }
        setTabSelection(0)
        radioGroup = findViewById<View>(R.id.activity_group_radioGroup) as RadioGroup
        radioGroup!!.setOnCheckedChangeListener { group, tabId ->
            if (tabId == R.id.order_process) {
//                    switchFragment(fragment1).commit();
                setTabSelection(0)
            }
            if (tabId == R.id.order_query) {
//                    switchFragment(fragment2).commit();
                setTabSelection(1)
            }
            if (tabId == R.id.merchant_manager) {
//                    switchFragment(fragment3).commit();
                setTabSelection(2)
            }
        }
    }

    fun setTabSelection(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        val frament: Fragment? = null
        when (index) {
            0 -> if (fragment1 == null) {
                // 如果MessageFragment为空，则创建一个并添加到界面上
//                    firstPageFragment = new FirstPageFragment(menu1);
                fragment1 = HomeFragmentNew.newInstance()
                transaction.add(R.id.container, fragment1!!)
            } else {
                // 如果MessageFragment不为空，则直接将它显示出来
                transaction.show(fragment1!!)
            }
            1 -> if (fragment2 == null) {
                // 如果MessageFragment为空，则创建一个并添加到界面上
                fragment2 = SceneFragment.newInstance()
                transaction.add(R.id.container, fragment2!!)
            } else {
                // 如果MessageFragment不为空，则直接将它显示出来
                transaction.show(fragment2!!)
            }
            2 -> if (fragment3 == null) {
                // 如果MessageFragment为空，则创建一个并添加到界面上
                fragment3 = MineFragment.newInstance()
                transaction.add(R.id.container, fragment3!!)
            } else {
                // 如果MessageFragment不为空，则直接将它显示出来
                transaction.show(fragment3!!)
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1!!)
        }
        if (fragment2 != null) {
            transaction.hide(fragment2!!)
        }
        if (fragment3 != null) {
            transaction.hide(fragment3!!)
        }
    }

    override fun onEvent() {}
    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.checkbutton_id -> {
                viewDialog!!.removeviewDialog()
                val UpApkUrl = ApiHelper.UpdateApkUrl + "sraum" + Version + ".apk"
                Log.e("fei", "UpApkUrl:$UpApkUrl")
                val apkName = "sraum$Version.apk"

//                UpdateManager manager = new UpdateManager(MainfragmentActivity.this, UpApkUrl, apkName);
//                updateApkListener = (UpdateApkListener) manager;
//                manager.showDownloadDialog();

                //3.如果手机已经启动下载程序，执行downloadApk。否则跳转到设置界面
                if (DownLoadUtils.getInstance(applicationContext).canDownload()) {
                    if (dialog1 != null) {
                        if (!dialog1!!.isShowing) dialog1!!.show()
                    }
                    DownloadApk.downloadApk(applicationContext, UpApkUrl, "众天力智家更新", apkName)
                } else {
                    DownLoadUtils.getInstance(applicationContext).skipToDownloadManager()
                }
            }
            R.id.qxbutton_id -> viewDialog!!.removeviewDialog()
        }
    }

    private var radioGroup: RadioGroup? = null
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        for (i in 0 until radioGroup!!.childCount) {
            val mTab = radioGroup!!.getChildAt(i) as RadioButton
            val fm = supportFragmentManager
            if (mTab.tag == null) continue
            val fragment = fm.findFragmentByTag(mTab.tag as String)
            val ft = fm.beginTransaction()
            if (fragment != null) {
                if (!mTab.isChecked) {
                    ft.hide(fragment)
                }
            }
            ft.commit()
        }
    }

    /**
     * 清除联动的本地存储
     */
    private fun common_second() {
        SharedPreferencesUtil.saveData(this@MainGateWayActivity, "linkId", "")
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_result", ArrayList())
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_condition", ArrayList())
        SharedPreferencesUtil.saveData(this@MainGateWayActivity, "editlink", false)
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "link_information_list", ArrayList())
        SharedPreferencesUtil.saveData(this@MainGateWayActivity, "add_condition", false)
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "personsInfos", ArrayList())
        SharedPreferencesUtil.saveInfo_List(this@MainGateWayActivity, "list_select", ArrayList())
    }

    companion object {
        const val MESSAGE_TONGZHI = "com.massky.sraum.message_tongzhi"
        const val MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION"
        const val KEY_MESSAGE = "message"
        const val KEY_EXTRAS = "extras"
        var ACTION_SRAUM_SETBOX = "ACTION_SRAUM_SETBOX" //notifactionId = 8 ->设置网关模式，sraum_setBox
        const val MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT = "com.massky.sraum.from_about_fragment"
        private const val TONGZHI_APK_UPGRATE = 0x0012
        const val MESSAGE_RECEIVED_ACTION_APK_LOAD = "com.Util.MESSAGE_RECEIVED_ACTION_APK_LOAD"
        const val MESSAGE_TONGZHI_DOOR = "com.massky.sraum.message.tongzhi.door"

        @JvmField
        var ACTION_INTENT_CAMERA_RECEIVER = "com.massky.camera.receiver"
        const val SRAUM_IS_DOWN_LOAD = "sraum_is_download"
        const val KEY_TITLE = "title"


    }

    //**********************************************************public******************************
    init {
        System.loadLibrary("voiceRecog2")
    }
}