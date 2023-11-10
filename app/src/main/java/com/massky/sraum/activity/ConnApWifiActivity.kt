package com.massky.sraum.activity

import android.Manifest
import android.app.Dialog
import android.content.*
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.percentlayout.widget.PercentRelativeLayout
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.espressif.esptouch.android.EspTouchActivityAbs
import com.espressif.esptouch.android.v1.EspTouchViewModel
import com.espressif.iot.esptouch1.EsptouchTask
import com.espressif.iot.esptouch1.IEsptouchListener
import com.espressif.iot.esptouch1.IEsptouchResult
import com.espressif.iot.esptouch1.IEsptouchTask
import com.espressif.iot.esptouch1.util.ByteUtil
import com.espressif.iot.esptouch1.util.TouchNetUtil
import com.google.gson.Gson
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.Utils.GpsUtil
import com.massky.sraum.fragment.ConfigApDialogFragment
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.receiver.LocalBroadcastManager
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import io.reactivex.disposables.Disposable
import okhttp3.Call
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/5/30.
 */
class ConnApWifiActivity : EspTouchActivityAbs() {
    private var add_wifi_device: Boolean? = false

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
    private var newFragment: ConfigApDialogFragment? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway)
    var eyeimageview_id_gateway: ImageView? = null
    private var eyeUtil: EyeUtil? = null

    @JvmField
    @BindView(R.id.conn_btn_dev)
    var conn_btn_dev: Button? = null

    private var mTask: EsptouchAsyncTask4? = null
    private var mViewModel: EspTouchViewModel? = null

    private val CONNWIFI = 101

    //    var scheduledThreadPool: ScheduledExecutorService? = null
    var isresult: Boolean? = false
    var device_id: String? = null
    var areaNumber_new: String? = null
    var bssid: String? = null
    var dialog_show: Boolean? = false


    override fun viewId(): Int {
        return R.layout.conn_ap_wifi_act
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        select_wlan_rel_big!!.setOnClickListener(this)
        eyeimageview_id_gateway!!.setOnClickListener(this)
        conn_btn_dev!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        onview()
        initDialog()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)//ACCESS_COARSE_LOCATION
//            requestPermissions(permissions, REQUEST_PERMISSION)
//        }
        open_gpss()
//        App.getInstance().observeBroadcast(this, Observer { broadcast: String ->
//            Log.d(TAG, "onCreate: Broadcast=$broadcast")
//            onWifiChanged()
//        })
        //scheduledThreadPool = Executors.newScheduledThreadPool(3)
    }

    private fun open_gpss() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this@ConnApWifiActivity)
        permissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION).subscribe(object : io.reactivex.Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() { //
                // TODO request success
                if (GpsUtil.isOPen(this@ConnApWifiActivity)) {
                    //initWifiName()
                    onWifiChanged()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            887 -> if (!GpsUtil.isOPen(this@ConnApWifiActivity)) {
                ToastUtil.showToast(this@ConnApWifiActivity, "定位没有加开，无法添加设备")
            } else {
                onWifiChanged()
            }
            else -> onWifiChanged()
        }
    }


    /**
     * 显示倒计时对话框
     */
    private fun executeEsptouch() {

//        String str = etPsw.getText().toString();
        if (edit_wifi!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ConnApWifiActivity, "WIFI用户名为空")
            return
        }

        if (edit_password_gateway!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ConnApWifiActivity, "WIFI密码为空")
            return
        }
        val viewModel = mViewModel
        val ssid = if (viewModel!!.ssidBytes == null) ByteUtil.getBytesByString(viewModel.ssid) else viewModel.ssidBytes
        val pwdStr: CharSequence? = edit_password_gateway!!.text
        val password = if (pwdStr == null) null else ByteUtil.getBytesByString(pwdStr.toString())
        val bssid = TouchNetUtil.parseBssid2bytes(viewModel.bssid)
        val devCountStr: CharSequence? = "1"
        val deviceCount = devCountStr?.toString()?.toByteArray() ?: ByteArray(0)
        val broadcast = byteArrayOf((1).toByte())
        if (mTask != null) {
            mTask!!.cancelEsptouch()
        }
        if (connWifiInterfacer != null) {
            connWifiInterfacer!!.conn_wifi_over()
        }
        show_dialog_fragment()
        mTask = EsptouchAsyncTask4(this)
        mTask!!.execute(ssid, bssid, password, deviceCount, broadcast)
    }


    fun show_dialog_fragment() {
        if (!newFragment!!.isAdded) { //DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.add(newFragment!!, "dialog")
            ft.commit()
        }
    }


    private fun check(): StateResult {
        var result = checkPermission()
        if (!result.permissionGranted) {
            return result
        }
        result = checkLocation()
        result.permissionGranted = true
        if (result.locationRequirement) {
            return result
        }
        result = checkWifi()
        result.permissionGranted = true
        result.locationRequirement = false
        return result
    }

    private fun onWifiChanged() {
        val stateResult = check()
        mViewModel!!.message = stateResult.message
        mViewModel!!.ssid = stateResult.ssid
        mViewModel!!.ssidBytes = stateResult.ssidBytes
        mViewModel!!.bssid = stateResult.bssid
        mViewModel!!.confirmEnable = false
        if (stateResult.wifiConnected) {
            mViewModel!!.confirmEnable = true
            if (stateResult.is5G) {
                mViewModel!!.message = getString(R.string.esptouch1_wifi_5g_message)
                showCenterDeleteDialog("当前WIFI网络是5G，不能添加WIFI设备；请切换到2.4G，在重新操作")
                return
            }
        } else {
            if (mTask != null) {
                mTask!!.cancelEsptouch()
                mTask = null
                if (connWifiInterfacer != null) {
                    connWifiInterfacer!!.conn_wifi_over()
                }
                AlertDialog.Builder(this@ConnApWifiActivity)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
            }

            mViewModel!!.message = ""
            //
            //wifi还没有连接上弹出alertDialog对话框
            if (!dialog_show!!)
                showCenterDeleteDialog()
        }
        edit_wifi!!.setText(mViewModel!!.ssid)
        if (mViewModel!!.ssid != null) {
            if (dialog != null)
                dialog!!.dismiss()
        }
        mViewModel!!.invalidateAll()
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
        val view = LayoutInflater.from(this@ConnApWifiActivity).inflate(R.layout.promat_dialog, null)
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
        val dialog = Dialog(this@ConnApWifiActivity, R.style.BottomDialog)
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
            ConnApWifiActivity@ this.finish()
        }
    }


    var dialog: Dialog? = null

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
        val view = LayoutInflater.from(this@ConnApWifiActivity).inflate(R.layout.promat_dialog, null)
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
        dialog = Dialog(this@ConnApWifiActivity, R.style.BottomDialog)
        dialog!!.setContentView(view)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog!!.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog!!.window!!.attributes = p //设置生效

        dialog!!.show()
        dialog_show = true
        cancel.setOnClickListener {
            val wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
            startActivityForResult(wifiSettingsIntent, CONNWIFI)
            dialog!!.dismiss()
            dialog_show = false
            handler.sendEmptyMessage(1)
        }
        confirm.setOnClickListener { //连接wifi的相关代码,跳转到WIFI连接界面
            dialog!!.dismiss()
            dialog_show = false
            handler.sendEmptyMessage(0)
        }
    }

    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> edit_wifi!!.isEnabled = false
                1 -> edit_wifi!!.isEnabled = true
            }
        }
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
                    ToastUtil.showToast(this@ConnApWifiActivity, "登录成功")
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun onview() {
        mViewModel = EspTouchViewModel()
        mViewModel!!.edit_password_gateway = edit_password_gateway
        mViewModel!!.edit_wifi = edit_wifi
        eyeUtil = EyeUtil(this@ConnApWifiActivity, eyeimageview_id_gateway, edit_password_gateway, true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.conn_btn_dev -> {
                executeEsptouch()
            }
            R.id.eyeimageview_id_gateway -> eyeUtil!!.EyeStatus()
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
        newFragment = ConfigApDialogFragment.newInstance(this@ConnApWifiActivity, "", "", null) //初始化快配和搜索设备dialogFragment
        //
        connWifiInterfacer = newFragment as ConnWifiInterfacer?
    }


    override fun onDestroy() {
        super.onDestroy()
        if (connWifiInterfacer != null) {
            connWifiInterfacer!!.conn_wifi_over()
        }

        if (mTask != null) {
            mTask!!.cancelEsptouch()
        }
        // scheduledThreadPool?.shutdown()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun pxTodip(pxValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    override fun onResume() {
        super.onResume()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (requestCode == REQUEST_PERMISSION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                onWifiChanged()
//            } else {
//                AlertDialog.Builder(this)
//                        .setTitle(R.string.esptouch1_location_permission_title)
//                        .setMessage(R.string.esptouch1_location_permission_message)
//                        .setCancelable(false)
//                        .setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, which: Int -> finish() }
//                        .show()
//            }
//            return
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }

    override fun getEspTouchVersion(): String {
        return getString(R.string.esptouch1_about_version, IEsptouchTask.ESPTOUCH_VERSION)
    }

    private class EsptouchAsyncTask4 internal constructor(activity: ConnApWifiActivity) : AsyncTask<ByteArray?, IEsptouchResult?, List<IEsptouchResult>?>() {
        private val mActivity: WeakReference<ConnApWifiActivity>
        private val mLock = Any()

        //        private var mProgressDialog: ProgressDialog? = null
//        private var mResultDialog: AlertDialog? = null
        private var mEsptouchTask: IEsptouchTask? = null
        fun cancelEsptouch() {
            cancel(true)
//            if (mProgressDialog != null) {
//                mProgressDialog!!.dismiss()
//            }
//            if (mResultDialog != null) {
//                mResultDialog!!.dismiss()
//            }
            if (mEsptouchTask != null) {
                mEsptouchTask!!.interrupt()
            }
        }

        override fun onPreExecute() {
            val activity = mActivity.get()
//            mProgressDialog = ProgressDialog(activity)
//            mProgressDialog!!.setMessage(activity!!.getString(R.string.esptouch1_configuring_message))
//            mProgressDialog!!.setCanceledOnTouchOutside(false)
//            mProgressDialog!!.setOnCancelListener { dialog: DialogInterface? ->
//                synchronized(mLock) {
//                    if (mEsptouchTask != null) {
//                        mEsptouchTask!!.interrupt()
//                    }
//                }
//            }
//            mProgressDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel)
//            ) { dialog: DialogInterface?, which: Int ->
//                synchronized(mLock) {
//                    if (mEsptouchTask != null) {
//                        mEsptouchTask!!.interrupt()
//                    }
//                }
//            }
//            mProgressDialog!!.show()
//        }
            //显示dialog
            //show_dialog_fragment()

        }


        override fun onProgressUpdate(vararg values: IEsptouchResult?) {
//            val context: Context? = mActivity.get()
//            if (context != null) {
//                val result = values[0]
//                Log.i(TAG, "EspTouchResult: $result")
//                val text = result!!.bssid + " is connected to the wifi"
//                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
//            }
        }

        override fun doInBackground(vararg params: ByteArray?): List<IEsptouchResult>? {
            val activity = mActivity.get()
            var taskResultCount: Int
            synchronized(mLock) {
                val apSsid = params[0]
                val apBssid = params[1]
                val apPassword = params[2]
                val deviceCountData = params[3]
                val broadcastData = params[4]
                taskResultCount = if (deviceCountData!!.size == 0) -1 else String(deviceCountData).toInt()
                val context = activity!!.applicationContext
                mEsptouchTask = EsptouchTask(apSsid, apBssid, apPassword, context)
                mEsptouchTask!!.setPackageBroadcast(true)
                mEsptouchTask!!.setEsptouchListener(IEsptouchListener { values: IEsptouchResult? -> publishProgress(values) })
            }
            return mEsptouchTask!!.executeForResults(taskResultCount)
        }


        override fun onPostExecute(result: List<IEsptouchResult>?) {
            val activity = mActivity.get()
            activity!!.mTask = null
            // mProgressDialog!!.dismiss()
            Log.e("onPost", "onPostExecute: " + Gson().toJson(result))
            if (result == null) {
                //mResultDialog = AlertDialog.Builder(activity)
                //        .setMessage(R.string.esptouch1_configure_result_failed_port)
                //        .setPositiveButton(android.R.string.ok, null)
                //        .show()
                //mResultDialog!!.setCanceledOnTouchOutside(false)
                activity.runOnUiThread {
                    ToastUtil.showToast(activity, "添加WIFI设备失败")
                }
                activity!!.over_connAp(activity)
                //  AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
                return
            }

            // check whether the task is cancelled and no results received
            val firstResult = result[0] //bssid=68c63ab9b669, address=192.168.169.216, suc=true, cancel=false
            if (firstResult.isCancelled) {
                activity.runOnUiThread {
                    ToastUtil.showToast(activity, "添加WIFI设备失败")
                }
                activity!!.over_connAp(activity)
                return
            }
            // the task received some results including cancelled while
            // executing before receiving enough results
            if (!firstResult.isSuc) {
                //mResultDialog = AlertDialog.Builder(activity)
                //        .setMessage(R.string.esptouch1_configure_result_failed)
                //        .setPositiveButton(android.R.string.ok, null)
                //        .show()
                //mResultDialog!!.setCanceledOnTouchOutside(false)
                activity.runOnUiThread {
                    ToastUtil.showToast(activity, "添加WIFI设备失败")
                }
                activity!!.over_connAp(activity)
                return
            }

            val resultMsgList = ArrayList<CharSequence>(result.size)
            for (touchResult in result) {
                val message = activity.getString(R.string.esptouch1_configure_result_success_item,
                        touchResult.bssid, touchResult.inetAddress.hostAddress)
                resultMsgList.add(message)
            }
            val items = arrayOfNulls<CharSequence>(resultMsgList.size)
            //mResultDialog = AlertDialog.Builder(activity)
            //        .setTitle(R.string.esptouch1_configure_result_success)
            //        .setItems(resultMsgList.toArray(items), null)
            //        .setPositiveButton(android.R.string.ok, null)
            //        .show()
            //mResultDialog!!.setCanceledOnTouchOutside(false)
            //拿到bssid
            activity!!.bssid = result[0].bssid
            Log.e("onPost", "sraum_addWifiDevice")
            if (activity!!.add_wifi_device!!) return
            activity!!.sraum_addWifiDevice(result[0].bssid)//bssid=ecfabcb389be, address=192.168.169.206, suc=true, cancel=false
        }

        init {
            mActivity = WeakReference(activity)
        }
    }


    private fun over_connAp(activity: ConnApWifiActivity?) {
        isresult = true
        if (activity!!.connWifiInterfacer != null) {
            activity!!.connWifiInterfacer!!.conn_wifi_over()
        }
        AppManager.getAppManager().finishActivity_current(ConnApWifiActivity::class.java)
        AppManager.getAppManager().finishActivity_current(AddWifiDevActivity::class.java)
    }

    /**
     * 添加 wifi 面板
     */
    private fun sraum_addWifiDevice(mac: String) {
        var type = ""
        //        String deviceInfo  = add_bind_dingyue();
        val map = HashMap<Any?, Any?>()
        var areaNumber = SharedPreferencesUtil.getData(this@ConnApWifiActivity,
                "areaNumber", "") as String
        //dialogUtil.loadDialog()
        map["token"] = TokenUtil.getToken(this@ConnApWifiActivity)
        map["areaNumber"] = areaNumber
        map["mac"] = mac

//        map.put("strMac", strMac);
//        map.put("strDeviceID", strDeviceID);
//        map.put("strName", strName);
//        map.put("wifi",wifi_name);
        val finalType = type
        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiDevice, map as HashMap<String, Any>,
                object : Mycallback(AddTogglenInterfacer
                //刷新togglen获取新数据
                { sraum_addWifiDevice(mac) }, this@ConnApWifiActivity, null) {
                    override fun onError(call: Call, e: java.lang.Exception, id: Int) {
                        super.onError(call, e, id)
                        this@ConnApWifiActivity.finish()
                    }

                    override fun onSuccess(user: User) {
                        //跳转到设备详情页， 改名字
                        device_id = user.id
                        areaNumber_new = areaNumber
                        wifi_devices(user.id, areaNumber)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                        ToastUtil.showToast(this@ConnApWifiActivity, """
     areaNumber 不正
     确
     """.trimIndent())
                    }

                    override fun threeCode() {
                        super.threeCode()
                        //   over_connAp(this@ConnApWifiActivity)
                        schedule_start()
                        // ToastUtil.showToast(this@ConnApWifiActivity, "设备添加失败")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        over_connAp(this@ConnApWifiActivity)
                    }
                })
    }

    /**
     * 获取WIFI设备下按钮列表
     */
    private fun wifi_devices(number: String, areaNumber: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@ConnApWifiActivity)
        // String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        map["deviceId"] = number
        map["areaNumber"] = areaNumber
        common_devices_show(number, areaNumber, map, ApiHelper.sraum_getWifiButtons)
    }


    private fun common_devices_show(number: String, areaNumber: String, map: MutableMap<String, Any?>, api: String?) {
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { common_devices_show(number, areaNumber, map, ApiHelper.sraum_getWifiButtons) },
                        this@ConnApWifiActivity, null) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        add_wifi_device = true
                        deviceList.clear()
                        deviceList.addAll(user.deviceList)
                        //面板的详细信息
                        panelType = user.panelType
                        panelName = user.panelName
                        panelMAC = user.panelMAC

                        if (connWifiInterfacer != null) {
                            connWifiInterfacer!!.conn_wifi_over()
                        }

                        var intent = Intent(this@ConnApWifiActivity,
                                ChangePanelAndDeviceActivity::class.java)
                        intent!!.putExtra("panelid", number)
                        intent.putExtra("boxNumber", "")
                        val bundle = Bundle()
                        bundle.putSerializable("deviceList", deviceList as Serializable)
                        //                            intent.putExtra("deviceList", (Serializable) deviceList);
                        intent.putExtra("panelType", panelType)
                        intent.putExtra("panelName", panelName)
                        intent.putExtra("panelMAC", panelMAC)
                        intent.putExtra("bundle_panel", bundle)
                        intent.putExtra("findpaneltype", "wifi_status")
                        intent.putExtra("areaNumber", areaNumber)
                        startActivity(intent)
                        //runOnUiThread()
                        Handler().postDelayed({
                            over_connAp(this@ConnApWifiActivity)
                        }, 1000)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun threeCode() {
                        super.threeCode()
                    }
                })
    }

    private fun schedule_start() {
        Log.e("onPost", "enter: " + "schedule-before")
        if (add_wifi_device!!) return
        handler.postDelayed({
            Log.e("onPost", "enter: " + "schedule-now")
            sraum_addWifiDevice(bssid!!)

        }, 5000)
//        scheduledThreadPool!!.scheduleAtFixedRate({
//            Log.e("onPost", "enter: " + "schedule-now")
//            sraum_addWifiDevice(bssid!!)
//        }, 0, 5, TimeUnit.SECONDS)
    }


    private var panelType: String? = null
    private var panelName: String? = null
    private var panelMAC: String? = null
    private var deviceList: MutableList<User.device> = ArrayList()


    companion object {
        const val MESSAGE_RECEIVED_ACTION_ConnWifi = "com.massky.sraum.ConnWifiActivity.MESSAGE_RECEIVED_ACTION"
        private val TAG = ConnApWifiActivity::class.java.simpleName
        private const val REQUEST_PERMISSION = 0x01
    }
}