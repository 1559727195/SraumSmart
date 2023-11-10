package com.massky.sraum.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.percentlayout.widget.PercentRelativeLayout
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.GuJianWangGuanNewActivity
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/8.
 */
class GuJianWangGuanNewActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.banbenxin_linear)
    var banbenxin_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.banben_pic)
    var icon_banbengenxin: ImageView? = null

    @JvmField
    @BindView(R.id.new_gujian_promat_txt)
    var new_gujian_promat_txt: TextView? = null

    @JvmField
    @BindView(R.id.banben_progress_linear)
    var banben_progress_linear: LinearLayout? = null

    //    @BindView(R.id.progress)
    //    ProgressBar progress;
    @JvmField
    @BindView(R.id.current_gujian_version_linear)
    var current_gujian_version_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.current_gujian_version_txt)
    var current_gujian_version_txt: TextView? = null

    @JvmField
    @BindView(R.id.new_gujian_version_txt)
    var new_gujian_version_txt: TextView? = null

    @JvmField
    @BindView(R.id.upgrade_rel)
    var upgrade_rel: PercentRelativeLayout? = null

    @JvmField
    @BindView(R.id.btn_upgrade)
    var btn_upgrade: Button? = null
    private var version: String? = null
    private var isupgrade: Boolean? = false //是否正在更新固态版本 = false
    private val task: TimerTask? = null
    private var activity_destroy = false
    private val newVersion: String? = null
    private var tenMSecs = 0
    private var timer = Timer()
    private var timerTask: TimerTask? = null
    private var showTimeTask: TimerTask? = null

    @JvmField
    @BindView(R.id.progress_second)
    var progress_second: TextView? = null

    @JvmField
    @BindView(R.id.progress_second__ss)
    var progress_second__ss: TextView? = null
    private var mMessageReceiver: MessageReceiver? = null
    private var dialogUtil: DialogUtil? = null

    @JvmField
    @BindView(R.id.second)
    var second_txt: TextView? = null

    @JvmField
    @BindView(R.id.miao)
    var miao: TextView? = null

    //a088_gate_linear
    @JvmField
    @BindView(R.id.a088_gate_linear)
    var a088_gate_linear: LinearLayout? = null

    private var is_index = false
    private var areaNumber: String? = null
    private var gatewayNumber: String? = null
    private var currentVersion: String? = null
    private var type: String? = null
    override fun viewId(): Int {
        return R.layout.gujian_wangguan_btn_new
    }

    override fun onView() {
        registerMessageReceiver()
        dialogUtil = DialogUtil(this)
        val newVersion = intent.getSerializableExtra("newVersion") as String
        currentVersion = intent.getSerializableExtra("currentVersion") as String
        val doit = intent.getSerializableExtra("doit") as String
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        gatewayNumber = intent.getSerializableExtra("number") as String
        type = intent.getSerializableExtra("type") as String
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        when (doit) {
            "doit" -> start_upgrade(newVersion, currentVersion)
            "scuess" -> update_complete_response()
        }
        onEvent1()
        onData1()
    }

    override fun onEvent() {}
    override fun onData() {}

    /**
     * 开始更新
     *
     * @param newVersion
     * @param currentVersion
     */
    private fun start_upgrade(newVersion: String, currentVersion: String?) {
        version = "old_version"
        when (version) {
            "new_version" -> {
                banbenxin_linear!!.visibility = View.VISIBLE
                icon_banbengenxin!!.setImageResource(R.drawable.icon_edition)
                current_gujian_version_linear!!.visibility = View.VISIBLE
            }
            "old_version" -> {
                when(type){
                    "A088"-> {
                        a088_gate_linear!!.visibility = View.VISIBLE
                        btn_upgrade!!.visibility = View.GONE
                    }
                }
                banbenxin_linear!!.visibility = View.VISIBLE
                //                back.setVisibility(View.GONE);
                icon_banbengenxin!!.setImageResource(R.drawable.pic_youshengji)
                new_gujian_promat_txt!!.visibility = View.VISIBLE
                current_gujian_version_linear!!.visibility = View.VISIBLE
                upgrade_rel!!.visibility = View.VISIBLE
                current_gujian_version_txt!!.text = "当前固件版本:$currentVersion"
                new_gujian_version_txt!!.text = "最新固件版本:$newVersion"
            }
        }

        // 向Handler发送消息
        showTimeTask = object : TimerTask() {
            override fun run() {
                handler.sendEmptyMessage(MSG_WHAT_SHOW_TIME)
            }
        }
        // 开始计时
        timer.schedule(showTimeTask, 200, 200)
    }

    protected fun onEvent1() {
        back!!.setOnClickListener(this)
        btn_upgrade!!.setOnClickListener(this)
    }

    private fun onData1() {
        next_step_txt!!.setOnClickListener(this)
        when (type) {
            "网关" -> {
                project_select!!.text = "升级网关"
            }
            else -> {
                project_select!!.text = "升级WIFI设备"
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_step_txt -> //                GuanLianSceneBtnActivity.this.finish();
                finish()
            R.id.back -> if (!isupgrade!!) finish()
            R.id.btn_upgrade -> back_response()
        }
    }

    /**
     * 按返回键的事件响应
     */
    private fun back_response() {
        when (btn_upgrade!!.text.toString()) {
            "取消" -> {
                btn_upgrade!!.text = "升级"
                activity_destroy = true
                banben_progress_linear!!.visibility = View.GONE
                banbenxin_linear!!.visibility = View.VISIBLE
                new_gujian_promat_txt!!.visibility = View.VISIBLE
                current_gujian_version_linear!!.visibility = View.VISIBLE
            }

            "更新" -> {
                btn_upgrade!!.text = "更新中"
                isupgrade = true
                banbenxin_linear!!.visibility = View.GONE
                new_gujian_promat_txt!!.visibility = View.GONE
                banben_progress_linear!!.visibility = View.VISIBLE
                back!!.visibility = View.GONE
                init_timer()
                updatebox_version()
            }
            "返回" -> finish()
        }
    }

    override fun onBackPressed() {
        if (isupgrade!!) { //正在更新中
            back_response()
        } else {
            finish()
        }
    }

    private fun init_timer() {
        val second = intArrayOf(30) //90秒
        val minute = intArrayOf(1) //90秒
        val index = intArrayOf(0)
        is_index = true
        Thread(Runnable {
            while (is_index) {
                try {
                    Thread.sleep(1000)
                    runOnUiThread {
                        second[0]--
                        if (miao == null) {
                            is_index = false
                            return@runOnUiThread
                        }
                        if (second[0] > 9) {
                            miao!!.text = second[0].toString()
                        } else if (second[0] > 0) {
                            miao!!.text = "0" + second[0].toString()
                        } else {
                            index[0]++
                            if (index[0] >= 2) {
                                finish()
                                //停止添加网关
//                                        is_index = false;
                                miao!!.text = "00"
                                second[0] = 0
                                minute[0] = 0
                                miao!!.text = "00"
                                stopTimer()
                                finish()
                                sraum_cancelUpdateGateway()
                            } else {
                                miao!!.text = "00"
                                second[0] = 59
                                minute[0] = 0
                                miao!!.text = "59"
                            }
                        }
                        second_txt!!.text = "0" + minute[0].toString()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    // 开始
    private fun startTimer() {
        if (timerTask == null) {
            timerTask = object : TimerTask() {
                override fun run() {
                    tenMSecs++
                }
            }
            timer.schedule(timerTask, 10, 10)
        }
    }

    // 结束
    private fun stopTimer() {
        is_index = false
    }

    // 取消计时
    fun onDestory() {
        if (timer != null) {
            timer.cancel()
        }
        unregisterReceiver(mMessageReceiver)
    }

    var second = 0
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_WHAT_SHOW_TIME -> {
                    //                    tvHour.setText(tenMSecs/100/60/60);
//                    tvMinute.setText(tenMSecs/100/60%60);
//                    tvSecond.setText(tenMSecs/100%60);
//                    tvMSecond.setText(tenMSecs%100);
                    var temp = 0
                    temp = tenMSecs / 100 % 60
                    if (tenMSecs / 100 / 60 % 60 == 1) {
                        if (second == 1) {
                            onDestory()
                            stopTimer()
                            //                            GuJianWangGuanActivity.this.finish();
                        }
                        second--
                    } else {
                        second = 90 - temp
                    }
                    if (progress_second__ss == null) return
                    progress_second__ss!!.text = (tenMSecs % 100).toString()
                    progress_second!!.text = second.toString() + ""
                }
                else -> {

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity_destroy = true
        is_index = false
    }

    /**
     * 查看固件更新处于什么阶段
     */
    var handler_upgrade: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> update_complete_response()
                1 -> {

                }
            }
        }
    }

    /**
     * 更新完成的页面跳转
     */
    private fun update_complete_response() {
        if (banben_progress_linear == null) return
        banben_progress_linear!!.visibility = View.GONE
        upgrade_rel!!.visibility = View.VISIBLE
        btn_upgrade!!.text = "返回"
        banbenxin_linear!!.visibility = View.VISIBLE
        new_gujian_promat_txt!!.visibility = View.GONE
        icon_banbengenxin!!.setImageResource(R.drawable.icon_edition)
        current_gujian_version_linear!!.visibility = View.VISIBLE
        current_gujian_version_txt!!.text = "当前已经是最新版本"
        new_gujian_version_txt!!.text = currentVersion
    }

    /**
     * 升级网关
     */
    private fun updatebox_version() {
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        //        String phoned = getDeviceId(this);
        val regId = SharedPreferencesUtil.getData(this@GuJianWangGuanNewActivity, "regId", "") as String
        map["token"] = TokenUtil.getToken(this)
        map["regId"] = regId
        map["areaNumber"] = areaNumber
        var api = ""
        when (type) {
            "网关" -> {
                map["boxNumber"] = gatewayNumber
                api = ApiHelper.sraum_updateGateway
            }
            else -> {
                map["deviceId"] = gatewayNumber
                api = ApiHelper.sraum_upgradeWifiDevice!!
            }
        }

        //在这里先调


        //        map.put("status", "0");//进入设置模式
//        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(api, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            updatebox_version()
        }, this@GuJianWangGuanNewActivity, dialogUtil) {
            override fun onSuccess(user: User) {}
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@GuJianWangGuanNewActivity, """
     areaNumer 不
     存在
     """.trimIndent())
            }

            override fun threeCode() {
                //number 不存在
                ToastUtil.showToast(this@GuJianWangGuanNewActivity, "number 不存在")
            }
        }
        )
    }


    /**
     * 取消网关升级
     */
    private fun sraum_cancelUpdateGateway() {
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["gatewayNumber"] = gatewayNumber
        map["areaNumber"] = areaNumber
        MyOkHttp.postMapObject(ApiHelper.sraum_cancelUpdateGateway, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            updatebox_version()
        }, this@GuJianWangGuanNewActivity, dialogUtil) {
            override fun onSuccess(user: User) {}
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@GuJianWangGuanNewActivity, """
     areaNumer 不
     存在
     """.trimIndent())
            }

            override fun threeCode() {
                //number 不存在
                ToastUtil.showToast(this@GuJianWangGuanNewActivity, "gatewayNumber 不存在")
            }
        }
        )
    }

    /**
     * 获取手机唯一标示码
     *
     * @param context
     * @return
     */
    fun getDeviceId(context: Context): String {
        val id: String
        //android.telephony.TelephonyManager
        val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this@GuJianWangGuanNewActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return ""
        }
        id = if (mTelephony.deviceId != null) {
            mTelephony.deviceId
        } else {
            //android.provider.Settings;
            Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return id
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(UPDATE_GRADE_BOX)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == UPDATE_GRADE_BOX) {
                val messflag = intent.getStringExtra("notifactionId")
                val panelid = intent.getStringExtra("panelid")
                if (messflag.equals("50") || messflag.equals("102")) { //notifactionId = 8 ->设置网关模式，sraum_setBox
                    //收到服务器端设置网关成功以后，跳转到修改面板名称，以及该面板下设备列表名称

                    //在网关转圈界面，下去拉设备，判断设备类型，不是我们的。网关不关，是我们的设备类型；在关网关。
                    //然后跳转到显示设备列表界面。
//                    ToastUtil.showToast(MacdeviceActivity.this,"messflag:" + messflag);
//                    getPanel_devices(panelid);
//                    String panelid = "62a001ff1006,1";
                    when (type) {
                        "网关" -> {
                            val items = panelid!!.split(",".toRegex()).toTypedArray()
                            if (items != null) {
                                if (items[1] == "1") {
                                    ToastUtil.showToast(this@GuJianWangGuanNewActivity, "网关" +
                                            "升级成功")
                                    handler_upgrade.sendEmptyMessage(0)
                                    onDestory()
                                    stopTimer()
                                    isupgrade = false
                                    finish()
                                } else {
                                    ToastUtil.showToast(this@GuJianWangGuanNewActivity, "网关" +
                                            "升级失败")
                                    finish()
                                }
                            }
                        }
                        else -> {
                            when(type) {
                                "ADB1"-> {
                                    ToastUtil.showToast(this@GuJianWangGuanNewActivity, "WIFI智能插座" +
                                            "升级成功")
                                } else-> {
                                ToastUtil.showToast(this@GuJianWangGuanNewActivity, "WIFI灯控" +
                                        "升级成功")
                                }
                            }
                            handler_upgrade.sendEmptyMessage(0)
                            onDestory()
                            stopTimer()
                            isupgrade = false
                            finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MSG_WHAT_SHOW_TIME = 1
        var UPDATE_GRADE_BOX = "com.massky.sraum.update_grade_box"
    }
}