package com.massky.sraum.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.percentlayout.widget.PercentRelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.BindView
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode
import com.massky.sraum.R
import com.massky.sraum.Util.EyeUtil
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Utils.GpsUtil
import com.massky.sraum.Utils.WifiUtil
import com.massky.sraum.activity.ConnWifiActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.ConfigAppleDialogFragment
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.receiver.LocalBroadcastManager
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yaokan.sdk.wifi.DeviceConfig
import com.yaokan.sdk.wifi.DeviceManager
import com.yaokan.sdk.wifi.listener.IDeviceConfigListener
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/5/30.
 */
class ConnWifiActivity : BaseActivity(), IDeviceConfigListener {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.select_wlan_rel_big)
    var select_wlan_rel_big: PercentRelativeLayout? = null

    @JvmField
    @BindView(R.id.edit_wifi)
    var edit_wifi: ClearEditText? = null

    @JvmField
    @BindView(R.id.edit_password_gateway)
    var edit_password_gateway: ClearEditText? = null
    private var newFragment: ConfigAppleDialogFragment? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway)
    var eyeimageview_id_gateway: ImageView? = null
    private val CONNWIFI = 101
    private var eyeUtil: EyeUtil? = null
    private var deviceConfig: DeviceConfig? = null
    private val wifill: LinearLayout? = null
    private var workSSID: String? = null

    @JvmField
    @BindView(R.id.conn_btn_dev)
    var conn_btn_dev: Button? = null
    private var wifi_name = ""
    private var gizWifiDevice: GizWifiDevice? = null
    private var mDeviceManager: DeviceManager? = null
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> edit_wifi!!.isEnabled = false
                1 -> edit_wifi!!.isEnabled = true
            }
        }
    }
    private val TAG = "robin debug"
    private var wifiUtil: WifiUtil? = null
    private var giz_time = 4
    override fun viewId(): Int {
        return R.layout.conn_wifi_act
    }

    /**
     * The handler.
     */
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val key = HandlerKey.values()[msg.what]
            when (key) {
                HandlerKey.TIMER_TEXT -> {
                }
                HandlerKey.START_TIMER -> {
                }
                HandlerKey.SUCCESSFUL -> {
                    //                    Toast.makeText(getApplicationContext(), R.string.configuration_successful, toa).show();
//                    ToastUtil.showToast(ConnWifiActivity.this, R.string.configuration_successful + "");
                    if (connWifiInterfacer != null) {
                        connWifiInterfacer!!.conn_wifi_over()
                    }
                    stopConfig(true)
                }
                HandlerKey.FAILED -> {
                    //                    Toast.makeText(ConnWifiActivity.this, (String) msg.obj, ConnWifiActivity.this.toastTime).show();
                    ToastUtil.showToast(this@ConnWifiActivity, msg.obj as String + "")
                    if (connWifiInterfacer != null) {
                        connWifiInterfacer!!.conn_wifi_over()
                    }
                    stopConfig(false)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 启动配置遥控中心
     */
    private fun startConfigAirlink() {
//        String str = etPsw.getText().toString();
        if (edit_wifi!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ConnWifiActivity, "WIFI用户名为空")
            return
        }
        if (edit_password_gateway!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ConnWifiActivity, "WIFI密码为空")
            return
        }
        show_dialog_fragment()
        startAirlink_out()
    }

    private fun startAirlink_out() {
        if(deviceConfig == null) return
        workSSID = edit_wifi!!.text.toString().trim { it <= ' ' }
        val str = edit_password_gateway!!.text.toString().trim { it <= ' ' }
        deviceConfig!!.setPwdSSID(workSSID, str)
        deviceConfig!!.startAirlink(workSSID, str)
        mHandler.sendEmptyMessage(HandlerKey.START_TIMER.ordinal)
        mHandler.sendEmptyMessage(HandlerKey.TIMER_TEXT.ordinal)
    }

    override fun onView() {
        //实例化配置对象
        deviceConfig = DeviceConfig(applicationContext, this)
        back!!.setOnClickListener(this)
        select_wlan_rel_big!!.setOnClickListener(this)
        eyeimageview_id_gateway!!.setOnClickListener(this)
        conn_btn_dev!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        onview()
        init_wifi()
        initDialog()
        mDeviceManager = DeviceManager
                .instanceDeviceManager(applicationContext)
        //        /**
//         * 开启机智云小苹果服务(service)
//         */
//        Intent intentService = new Intent(this,SimpleIntentService.class);
//        startService(intentService);
    }

    private fun init_wifi() {
        wifiUtil = WifiUtil.getInstance(this)
        wifiSSid
    }

    private val wifiSSid: Unit
        private get() {
            open_gpss()
        }

    private fun open_gpss() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this)
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                if (GpsUtil.isOPen(this@ConnWifiActivity)) {
                    initWifiName()
                    initWifiConect()
                } else {
                    openGpsSettings()
                }
            }
        })
    }

    /**
     * 打开Gps设置界面
     */
    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, 887)
    }

    override fun onEvent() {}
    override fun onData() {}

    //for receive customer msg from jpush server
    private var mMessageReceiver: MessageReceiver? = null
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MESSAGE_RECEIVED_ACTION_ConnWifi)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_ConnWifi == intent.action) {
                    ToastUtil.showToast(this@ConnWifiActivity, "登录成功")
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun onview() {
        eyeUtil = EyeUtil(this@ConnWifiActivity, eyeimageview_id_gateway, edit_password_gateway, true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.select_wlan_rel_big -> showPopwindow()
            R.id.conn_btn_dev -> //                startActivity(new Intent(ConnWifiActivity.this,));
                //在这里弹出dialogFragment对话框
                startConfigAirlink()
            R.id.eyeimageview_id_gateway -> eyeUtil!!.EyeStatus()
        }
    }

    private fun show_dialog_fragment() {
        if (!newFragment!!.isAdded) { //DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.add(newFragment!!, "dialog")
            ft.commit()
        }
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@ConnWifiActivity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        var promat_txt: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        //        promat_txt = (TextView) view.findViewById(R.id.promat_txt);
        name_gloud.text = "您的手机wifi尚未启动,请先启动您的手机wifi；并连接您的路由器在进行操作。"
        //        promat_txt.setText("连接");
        tv_title.text = "是否启动wifi?"
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@ConnWifiActivity, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {
            val wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
            startActivityForResult(wifiSettingsIntent, CONNWIFI)
            dialog.dismiss()
            handler.sendEmptyMessage(1)
        }
        confirm.setOnClickListener { //连接wifi的相关代码,跳转到WIFI连接界面
            dialog.dismiss()
            handler.sendEmptyMessage(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            887 -> if (!GpsUtil.isOPen(this@ConnWifiActivity)) {
                ToastUtil.showToast(this@ConnWifiActivity, "定位没有加开，无法添加设备")
            } else {
                initWifiName()
                initWifiConect()
            }
            else -> initWifiName()
        }
    }

    /**
     * 获取连接的wifi名称
     */
    private fun initWifiName() {
        // TODO Auto-generated method stub
        val wifiId = wifiUtil!!.getWIFISSID(this@ConnWifiActivity)
        if (wifiId != null && !wifiId.contains("<unknown ssid>")) { //wifiId在不连WIFI的情况下，去wifi快配wifiId = 0x
            edit_wifi!!.setText(wifiId)
            wifi_name = wifiId
            edit_password_gateway!!.isFocusable = true
            edit_password_gateway!!.isFocusableInTouchMode = true
            edit_password_gateway!!.requestFocus()
        }
    }

    /**
     * 初始化wifi连接，快配前wifi一定要连接上
     */
    private fun initWifiConect() {
        //初始化连接wifi dialog对话框
        // TODO Auto-generated method stub
        //获取系统服务
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //获取状态
        val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state
        //判断wifi已连接的条件
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            if (wifiUtil == null) return
            if (wifiUtil!!.is5GWifi) {
                showCenterDeleteDialog("当前WIFI网络是5G，不能添加WIFI设备；请切换到2.4G，在重新操作")
                return
            }
        } else { //wifi还没有连接上弹出alertDialog对话框
            showCenterDeleteDialog()
        }
    }




    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name: String?) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@ConnWifiActivity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        val confirm_rel: RelativeLayout
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        confirm_rel = view.findViewById<RelativeLayout>(R.id.confirm_rel) as RelativeLayout
        var rel_confirm = view.findViewById<RelativeLayout>(R.id.rel_confirm)
        var cancel_confirm_linear = view.findViewById<LinearLayout>(R.id.cancel_confirm_linear)
        cancel_confirm_linear.visibility = View.GONE
        rel_confirm.visibility = View.VISIBLE
        name_gloud.text = name
        tv_title.visibility = View.GONE
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@ConnWifiActivity, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(false)// 设置点击屏幕Dialog不消失
        dialog.setCancelable(false)  //
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        confirm.setOnClickListener { dialog.dismiss() }
        rel_confirm.setOnClickListener {
            dialog.dismiss()
            ConnWifiActivity@ this.finish()
        }
    }

    /**
     * 添加红外模块配置入网成功后关闭进度圈
     */
    private var connWifiInterfacer: ConnWifiInterfacer? = null

    interface ConnWifiInterfacer {
        fun conn_wifi_over()
    }

    /**
     * 初始化dialog
     */
    private fun initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigAppleDialogFragment.newInstance(this@ConnWifiActivity, "", "", null) //初始化快配和搜索设备dialogFragment
        //
        connWifiInterfacer = newFragment
    }

    /**
     * 显示popupWindow
     */
    @SuppressLint("WrongConstant")
    private fun showPopwindow() {
        // 利用layoutInflater获得View
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val outerView = inflater.inflate(R.layout.wheel_view, null)

//        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        val wv = outerView.findViewById<View>(R.id.wheel_view_wv) as ListView
        //        wv.setOffset(1);
//        wv.setItems(Arrays.asList(PLANETS));
//        wv.setSeletion(2);
//        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
//            }
//        });
        val data = arrayOf("类型一", "类型二", "类型三", "类型四")
        val array = ArrayAdapter(this,
                R.layout.simple_expandable_list_item_new, data)
        wv.adapter = array
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        //        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val window = PopupWindow(outerView,
                displayWidth / 2,
                WindowManager.LayoutParams.WRAP_CONTENT) //高度写死

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.isFocusable = true
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(-0x50000000)
        window.setBackgroundDrawable(dw)
        // 设置背景颜色变暗
        val lp = getWindow().attributes
        lp.alpha = 0.7f
        getWindow().attributes = lp
        // 设置popWindow的显示和消失动画
        window.animationStyle = R.style.style_pop_animation
        // 在底部显示
//        window.showAtLocation(WuYeTouSu_NewActivity.this.findViewById(R.id.tousu_select),
//                Gravity.CENTER_HORIZONTAL, 0, 0);
//        window.showAsDropDown(select_wlan_rel_big);

        // 将pixels转为dip
        val xoffInDip = pxTodip(displayWidth.toFloat()) / 4 * 3
        window.showAsDropDown(select_wlan_rel_big, xoffInDip, xoffInDip / 3)
        //popWindow消失监听方法
        window.setOnDismissListener {
            println("popWindow消失")
            val lp = getWindow().attributes
            lp.alpha = 1f
            getWindow().attributes = lp
        }
        wv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> //                tousu_txt1.setText(data[position]);
            window.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        giz_time = 0
        deviceConfig_over()
    }

    private fun deviceConfig_over() {
        if (deviceConfig != null) {
            deviceConfig!!.stopDeviceOnboarding()
            deviceConfig = null
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun pxTodip(pxValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    protected fun stopConfig(successful: Boolean) {
        if (successful) {
//            finish();
            //快配成功页面跳转
            if (gizWifiDevice != null) {
//                add_bind_dingyue();
                val intent = Intent(this@ConnWifiActivity, AddWifiHongWaiScucessActivity::class.java)
                //                intent.putExtra("wifi_name", wifi_name);
//                intent.putExtra("deviceInfo", deviceInfo);
                //            macDevice = wifiDevices.get(wifiDevices.size() - 1).getMacAddress();
                intent.putExtra("GizWifiDevice", gizWifiDevice)
                val mac = gizWifiDevice!!.macAddress
                val map_device = HashMap<Any?, Any?>()
                map_device["type"] = "AA02"
                map_device["mac"] = mac
                map_device["controllerId"] = mac
                map_device["wifi"] = wifi_name
                //                map_device.put("deviceInfo", deviceInfo);
                intent.putExtra("gizWifiDevice", map_device as Serializable)
                startActivity(intent)
            }
        }
    }

    enum class HandlerKey {
        /**
         * 倒计时通知
         */
        TIMER_TEXT,

        /**
         * 倒计时开始
         */
        START_TIMER,

        /**
         * 配置成功
         */
        SUCCESSFUL,

        /**
         * 配置失败
         */
        FAILED
    }

    override fun didSetDeviceOnboarding(result: GizWifiErrorCode, mac: String,
                                        did: String, productKey: String) {
    }

    override fun didSetDeviceOnboardingX(result: GizWifiErrorCode ?, gizWifiDevice: GizWifiDevice?) {
        val message = Message()
        if(result == null || gizWifiDevice == null) {
            giz_time--
            if(giz_time == 0) {
                deviceConfig_over()
                ConnWifiActivity@this.finish()
            } else {
                Log.e(TAG, "didSetDeviceOnboardingX: fail" )
                startAirlink_out()
            }
            return
        }
        if (GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING == result) {
            return
        }
        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
            message.obj = gizWifiDevice.macAddress
            message.what = HandlerKey.SUCCESSFUL.ordinal
            this.gizWifiDevice = gizWifiDevice
        } else {
            message.what = HandlerKey.FAILED.ordinal
            message.obj = toastError(this, result)
        }
        mHandler.sendMessage(message)
    }

    override fun onResume() {
        super.onResume()
        //        mDeviceManager.setGizWifiCallBack(mGizWifiCallBack);
    }

    //    private GizWifiCallBack mGizWifiCallBack = new GizWifiCallBack() {
    //
    //        @Override
    //        public void didUnbindDeviceCd(GizWifiErrorCode result, String did) {
    //            super.didUnbindDeviceCd(result, did);
    //            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
    //                // 解绑成功
    //                Logger.d(TAG, "解除绑定成功");
    //            } else {
    //                // 解绑失败
    //                Logger.d(TAG, "解除绑定失败");
    //            }
    //        }
    //
    //        @Override
    //        public void didBindDeviceCd(GizWifiErrorCode result, String did) {
    //            super.didBindDeviceCd(result, did);
    //            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
    //                // 绑定成功
    //                Logger.d(TAG, "绑定成功");
    //            } else {
    //                // 绑定失败
    //                Logger.d(TAG, "绑定失败");
    //            }
    //        }
    //
    //        @Override
    //        public void didSetSubscribeCd(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
    //            super.didSetSubscribeCd(result, device, isSubscribed);
    //            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
    //                // 解绑成功
    //                Logger.d(TAG, (isSubscribed ? "订阅" : "取消订阅") + "成功");
    //
    //                if(isSubscribed){
    //
    //                }
    //            } else {
    //                // 解绑失败
    //                Logger.d(TAG, "订阅失败");
    //            }
    //        }
    //
    //        @Override
    //        public void discoveredrCb(GizWifiErrorCode result,
    //                                  List<GizWifiDevice> deviceList) {
    //            Logger.d(TAG,
    //                    "discoveredrCb -> deviceList size:" + deviceList.size()
    //                            + "  result:" + result);
    //            switch (result) {
    //                case GIZ_SDK_SUCCESS:
    //                    Logger.e(TAG, "load device  sucess");
    ////                    update(deviceList);
    ////                    if(deviceList.get(0).getNetStatus()==GizWifiDeviceNetStatus.GizDeviceOffline)
    //
    //                    break;
    //                default:
    //                    break;
    //
    //            }
    //
    //        }
    //    };
    private fun toastError(ctx: Context, errorCode: GizWifiErrorCode): String? {
        var errorString = ctx.resources.getText(R.string.UNKNOWN_ERROR) as String
        errorString = when (errorCode) {
            GizWifiErrorCode.GIZ_SDK_PARAM_FORM_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_PARAM_FORM_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_CLIENT_NOT_AUTHEN -> ctx.resources.getText(R.string.GIZ_SDK_CLIENT_NOT_AUTHEN) as String
            GizWifiErrorCode.GIZ_SDK_CLIENT_VERSION_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_CLIENT_VERSION_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_UDP_PORT_BIND_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_UDP_PORT_BIND_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DAEMON_EXCEPTION -> ctx.resources.getText(R.string.GIZ_SDK_DAEMON_EXCEPTION) as String
            GizWifiErrorCode.GIZ_SDK_PARAM_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_PARAM_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_APPID_LENGTH_ERROR -> ctx.resources.getText(R.string.GIZ_SDK_APPID_LENGTH_ERROR) as String
            GizWifiErrorCode.GIZ_SDK_LOG_PATH_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_LOG_PATH_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_LOG_LEVEL_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_LOG_LEVEL_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_SEND_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONFIG_SEND_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONFIG_IS_RUNNING) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_TIMEOUT -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONFIG_TIMEOUT) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_DID_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_DID_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_MAC_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_MAC_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_PASSCODE_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_PASSCODE_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_NOT_SUBSCRIBED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_NOT_SUBSCRIBED) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_NO_RESPONSE -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_NO_RESPONSE) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_NOT_READY -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_NOT_READY) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_NOT_BINDED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_NOT_BINDED) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONTROL_WITH_INVALID_COMMAND) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_GET_STATUS_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_GET_STATUS_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_TYPE_ERROR) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONTROL_VALUE_OUT_OF_RANGE) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONTROL_NOT_WRITABLE_COMMAND) as String
            GizWifiErrorCode.GIZ_SDK_BIND_DEVICE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_BIND_DEVICE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_UNBIND_DEVICE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_UNBIND_DEVICE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DNS_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_DNS_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_M2M_CONNECTION_SUCCESS -> ctx.resources.getText(R.string.GIZ_SDK_M2M_CONNECTION_SUCCESS) as String
            GizWifiErrorCode.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_SET_SOCKET_NON_BLOCK_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_CONNECTION_TIMEOUT -> ctx.resources.getText(R.string.GIZ_SDK_CONNECTION_TIMEOUT) as String
            GizWifiErrorCode.GIZ_SDK_CONNECTION_REFUSED -> ctx.resources.getText(R.string.GIZ_SDK_CONNECTION_REFUSED) as String
            GizWifiErrorCode.GIZ_SDK_CONNECTION_ERROR -> ctx.resources.getText(R.string.GIZ_SDK_CONNECTION_ERROR) as String
            GizWifiErrorCode.GIZ_SDK_CONNECTION_CLOSED -> ctx.resources.getText(R.string.GIZ_SDK_CONNECTION_CLOSED) as String
            GizWifiErrorCode.GIZ_SDK_SSL_HANDSHAKE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_SSL_HANDSHAKE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_LOGIN_VERIFY_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_INTERNET_NOT_REACHABLE -> ctx.resources.getText(R.string.GIZ_SDK_INTERNET_NOT_REACHABLE) as String
            GizWifiErrorCode.GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR -> ctx.resources.getText(R.string.GIZ_SDK_HTTP_ANSWER_FORMAT_ERROR) as String
            GizWifiErrorCode.GIZ_SDK_HTTP_ANSWER_PARAM_ERROR -> ctx.resources.getText(R.string.GIZ_SDK_HTTP_ANSWER_PARAM_ERROR) as String
            GizWifiErrorCode.GIZ_SDK_HTTP_SERVER_NO_ANSWER -> ctx.resources.getText(R.string.GIZ_SDK_HTTP_SERVER_NO_ANSWER) as String
            GizWifiErrorCode.GIZ_SDK_HTTP_REQUEST_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_HTTP_REQUEST_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_OTHERWISE -> ctx.resources.getText(R.string.GIZ_SDK_OTHERWISE) as String
            GizWifiErrorCode.GIZ_SDK_MEMORY_MALLOC_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_MEMORY_MALLOC_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_THREAD_CREATE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_THREAD_CREATE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_TOKEN_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_TOKEN_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_GROUP_ID_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_GROUP_ID_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_GROUP_PRODUCTKEY_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_GROUP_PRODUCTKEY_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_GROUP_GET_DEVICE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_GROUP_GET_DEVICE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_DATAPOINT_NOT_DOWNLOAD -> ctx.resources.getText(R.string.GIZ_SDK_DATAPOINT_NOT_DOWNLOAD) as String
            GizWifiErrorCode.GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE -> ctx.resources.getText(R.string.GIZ_SDK_DATAPOINT_SERVICE_UNAVAILABLE) as String
            GizWifiErrorCode.GIZ_SDK_DATAPOINT_PARSE_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_DATAPOINT_PARSE_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_APK_CONTEXT_IS_NULL -> ctx.resources.getText(R.string.GIZ_SDK_APK_CONTEXT_IS_NULL) as String
            GizWifiErrorCode.GIZ_SDK_APK_PERMISSION_NOT_SET -> ctx.resources.getText(R.string.GIZ_SDK_APK_PERMISSION_NOT_SET) as String
            GizWifiErrorCode.GIZ_SDK_CHMOD_DAEMON_REFUSED -> ctx.resources.getText(R.string.GIZ_SDK_CHMOD_DAEMON_REFUSED) as String
            GizWifiErrorCode.GIZ_SDK_EXEC_DAEMON_FAILED -> ctx.resources.getText(R.string.GIZ_SDK_EXEC_DAEMON_FAILED) as String
            GizWifiErrorCode.GIZ_SDK_EXEC_CATCH_EXCEPTION -> ctx.resources.getText(R.string.GIZ_SDK_EXEC_CATCH_EXCEPTION) as String
            GizWifiErrorCode.GIZ_SDK_APPID_IS_EMPTY -> ctx.resources.getText(R.string.GIZ_SDK_APPID_IS_EMPTY) as String
            GizWifiErrorCode.GIZ_SDK_UNSUPPORTED_API -> ctx.resources.getText(R.string.GIZ_SDK_UNSUPPORTED_API) as String
            GizWifiErrorCode.GIZ_SDK_REQUEST_TIMEOUT -> ctx.resources.getText(R.string.GIZ_SDK_REQUEST_TIMEOUT) as String
            GizWifiErrorCode.GIZ_SDK_DAEMON_VERSION_INVALID -> ctx.resources.getText(R.string.GIZ_SDK_DAEMON_VERSION_INVALID) as String
            GizWifiErrorCode.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID -> ctx.resources.getText(R.string.GIZ_SDK_PHONE_NOT_CONNECT_TO_SOFTAP_SSID) as String
            GizWifiErrorCode.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED -> ctx.resources.getText(R.string.GIZ_SDK_DEVICE_CONFIG_SSID_NOT_MATCHED) as String
            GizWifiErrorCode.GIZ_SDK_NOT_IN_SOFTAPMODE -> ctx.resources.getText(R.string.GIZ_SDK_NOT_IN_SOFTAPMODE) as String
            GizWifiErrorCode.GIZ_SDK_RAW_DATA_TRANSMIT -> ctx.resources.getText(R.string.GIZ_SDK_RAW_DATA_TRANSMIT) as String
            GizWifiErrorCode.GIZ_SDK_PRODUCT_IS_DOWNLOADING -> ctx.resources.getText(R.string.GIZ_SDK_PRODUCT_IS_DOWNLOADING) as String
            GizWifiErrorCode.GIZ_SDK_START_SUCCESS -> ctx.resources.getText(R.string.GIZ_SDK_START_SUCCESS) as String
            GizWifiErrorCode.GIZ_SITE_PRODUCTKEY_INVALID -> ctx.resources.getText(R.string.GIZ_SITE_PRODUCTKEY_INVALID) as String
            GizWifiErrorCode.GIZ_SITE_DATAPOINTS_NOT_DEFINED -> ctx.resources.getText(R.string.GIZ_SITE_DATAPOINTS_NOT_DEFINED) as String
            GizWifiErrorCode.GIZ_SITE_DATAPOINTS_NOT_MALFORME -> ctx.resources.getText(R.string.GIZ_SITE_DATAPOINTS_NOT_MALFORME) as String
            GizWifiErrorCode.GIZ_OPENAPI_MAC_ALREADY_REGISTERED -> ctx.resources.getText(R.string.GIZ_OPENAPI_MAC_ALREADY_REGISTERED) as String
            GizWifiErrorCode.GIZ_OPENAPI_PRODUCT_KEY_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_PRODUCT_KEY_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_APPID_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_APPID_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_TOKEN_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_TOKEN_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_USER_NOT_EXIST -> ctx.resources.getText(R.string.GIZ_OPENAPI_USER_NOT_EXIST) as String
            GizWifiErrorCode.GIZ_OPENAPI_TOKEN_EXPIRED -> ctx.resources.getText(R.string.GIZ_OPENAPI_TOKEN_EXPIRED) as String
            GizWifiErrorCode.GIZ_OPENAPI_M2M_ID_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_M2M_ID_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_SERVER_ERROR -> ctx.resources.getText(R.string.GIZ_OPENAPI_SERVER_ERROR) as String
            GizWifiErrorCode.GIZ_OPENAPI_CODE_EXPIRED -> ctx.resources.getText(R.string.GIZ_OPENAPI_CODE_EXPIRED) as String
            GizWifiErrorCode.GIZ_OPENAPI_CODE_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_CODE_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED -> ctx.resources.getText(R.string.GIZ_OPENAPI_SANDBOX_SCALE_QUOTA_EXHAUSTED) as String
            GizWifiErrorCode.GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED -> ctx.resources.getText(R.string.GIZ_OPENAPI_PRODUCTION_SCALE_QUOTA_EXHAUSTED) as String
            GizWifiErrorCode.GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE -> ctx.resources.getText(R.string.GIZ_OPENAPI_PRODUCT_HAS_NO_REQUEST_SCALE) as String
            GizWifiErrorCode.GIZ_OPENAPI_DEVICE_NOT_FOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_DEVICE_NOT_FOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_FORM_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_FORM_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_DID_PASSCODE_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_DID_PASSCODE_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_DEVICE_NOT_BOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_DEVICE_NOT_BOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_PHONE_UNAVALIABLE -> ctx.resources.getText(R.string.GIZ_OPENAPI_PHONE_UNAVALIABLE) as String
            GizWifiErrorCode.GIZ_OPENAPI_USERNAME_UNAVALIABLE -> ctx.resources.getText(R.string.GIZ_OPENAPI_USERNAME_UNAVALIABLE) as String
            GizWifiErrorCode.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR -> ctx.resources.getText(R.string.GIZ_OPENAPI_USERNAME_PASSWORD_ERROR) as String
            GizWifiErrorCode.GIZ_OPENAPI_SEND_COMMAND_FAILED -> ctx.resources.getText(R.string.GIZ_OPENAPI_SEND_COMMAND_FAILED) as String
            GizWifiErrorCode.GIZ_OPENAPI_EMAIL_UNAVALIABLE -> ctx.resources.getText(R.string.GIZ_OPENAPI_EMAIL_UNAVALIABLE) as String
            GizWifiErrorCode.GIZ_OPENAPI_DEVICE_DISABLED -> ctx.resources.getText(R.string.GIZ_OPENAPI_DEVICE_DISABLED) as String
            GizWifiErrorCode.GIZ_OPENAPI_FAILED_NOTIFY_M2M -> ctx.resources.getText(R.string.GIZ_OPENAPI_FAILED_NOTIFY_M2M) as String
            GizWifiErrorCode.GIZ_OPENAPI_ATTR_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_ATTR_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_USER_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_USER_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_FIRMWARE_NOT_FOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_FIRMWARE_NOT_FOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_JD_PRODUCT_NOT_FOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_DATAPOINT_DATA_NOT_FOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_SCHEDULER_NOT_FOUND -> ctx.resources.getText(R.string.GIZ_OPENAPI_SCHEDULER_NOT_FOUND) as String
            GizWifiErrorCode.GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_QQ_OAUTH_KEY_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE -> ctx.resources.getText(R.string.GIZ_OPENAPI_OTA_SERVICE_OK_BUT_IN_IDLE) as String
            GizWifiErrorCode.GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED -> ctx.resources.getText(R.string.GIZ_OPENAPI_BT_FIRMWARE_UNVERIFIED) as String
            GizWifiErrorCode.GIZ_OPENAPI_BT_FIRMWARE_NOTHING_TO_UPGRADE -> ctx.resources.getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR) as String
            GizWifiErrorCode.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR -> ctx.resources.getText(R.string.GIZ_OPENAPI_SAVE_KAIROSDB_ERROR) as String
            GizWifiErrorCode.GIZ_OPENAPI_EVENT_NOT_DEFINED -> ctx.resources.getText(R.string.GIZ_OPENAPI_EVENT_NOT_DEFINED) as String
            GizWifiErrorCode.GIZ_OPENAPI_SEND_SMS_FAILED -> ctx.resources.getText(R.string.GIZ_OPENAPI_SEND_SMS_FAILED) as String
            GizWifiErrorCode.GIZ_OPENAPI_NOT_ALLOWED_CALL_API -> ctx.resources.getText(R.string.GIZ_OPENAPI_NOT_ALLOWED_CALL_API) as String
            GizWifiErrorCode.GIZ_OPENAPI_BAD_QRCODE_CONTENT -> ctx.resources.getText(R.string.GIZ_OPENAPI_BAD_QRCODE_CONTENT) as String
            GizWifiErrorCode.GIZ_OPENAPI_REQUEST_THROTTLED -> ctx.resources.getText(R.string.GIZ_OPENAPI_REQUEST_THROTTLED) as String
            GizWifiErrorCode.GIZ_OPENAPI_DEVICE_OFFLINE -> ctx.resources.getText(R.string.GIZ_OPENAPI_DEVICE_OFFLINE) as String
            GizWifiErrorCode.GIZ_OPENAPI_TIMESTAMP_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_TIMESTAMP_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_SIGNATURE_INVALID -> ctx.resources.getText(R.string.GIZ_OPENAPI_SIGNATURE_INVALID) as String
            GizWifiErrorCode.GIZ_OPENAPI_DEPRECATED_API -> ctx.resources.getText(R.string.GIZ_OPENAPI_DEPRECATED_API) as String
            GizWifiErrorCode.GIZ_OPENAPI_RESERVED -> ctx.resources.getText(R.string.GIZ_OPENAPI_RESERVED) as String
            GizWifiErrorCode.GIZ_PUSHAPI_BODY_JSON_INVALID -> ctx.resources.getText(R.string.GIZ_PUSHAPI_BODY_JSON_INVALID) as String
            GizWifiErrorCode.GIZ_PUSHAPI_DATA_NOT_EXIST -> ctx.resources.getText(R.string.GIZ_PUSHAPI_DATA_NOT_EXIST) as String
            GizWifiErrorCode.GIZ_PUSHAPI_NO_CLIENT_CONFIG -> ctx.resources.getText(R.string.GIZ_PUSHAPI_NO_CLIENT_CONFIG) as String
            GizWifiErrorCode.GIZ_PUSHAPI_NO_SERVER_DATA -> ctx.resources.getText(R.string.GIZ_PUSHAPI_NO_SERVER_DATA) as String
            GizWifiErrorCode.GIZ_PUSHAPI_GIZWITS_APPID_EXIST -> ctx.resources.getText(R.string.GIZ_PUSHAPI_GIZWITS_APPID_EXIST) as String
            GizWifiErrorCode.GIZ_PUSHAPI_PARAM_ERROR -> ctx.resources.getText(R.string.GIZ_PUSHAPI_PARAM_ERROR) as String
            GizWifiErrorCode.GIZ_PUSHAPI_AUTH_KEY_INVALID -> ctx.resources.getText(R.string.GIZ_PUSHAPI_AUTH_KEY_INVALID) as String
            GizWifiErrorCode.GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR -> ctx.resources.getText(R.string.GIZ_PUSHAPI_APPID_OR_TOKEN_ERROR) as String
            GizWifiErrorCode.GIZ_PUSHAPI_TYPE_PARAM_ERROR -> ctx.resources.getText(R.string.GIZ_PUSHAPI_TYPE_PARAM_ERROR) as String
            GizWifiErrorCode.GIZ_PUSHAPI_ID_PARAM_ERROR -> ctx.resources.getText(R.string.GIZ_PUSHAPI_ID_PARAM_ERROR) as String
            GizWifiErrorCode.GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID -> ctx.resources.getText(R.string.GIZ_PUSHAPI_APPKEY_SECRETKEY_INVALID) as String
            GizWifiErrorCode.GIZ_PUSHAPI_CHANNELID_ERROR_INVALID -> ctx.resources.getText(R.string.GIZ_PUSHAPI_CHANNELID_ERROR_INVALID) as String
            GizWifiErrorCode.GIZ_PUSHAPI_PUSH_ERROR -> ctx.resources.getText(R.string.GIZ_PUSHAPI_PUSH_ERROR) as String
            else -> ctx.resources.getText(R.string.UNKNOWN_ERROR) as String
        }
        return errorString
    }

    companion object {
        const val MESSAGE_RECEIVED_ACTION_ConnWifi = "com.massky.sraum.ConnWifiActivity.MESSAGE_RECEIVED_ACTION"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_EXTRAS = "extras"
    }
}