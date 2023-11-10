package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.RoundProgressBar_ChangePosition
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/5/30.
 */
class AddDoorLockDevActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.next_step_id)
    var next_step_id: Button? = null

    //    private int[] icon = {R.drawable.icon_type_switch, R.drawable.menci_big,
    //            R.drawable.human_ganying_big, R.drawable.water, R.drawable.pm25,
    //            R.drawable.emergency_button};
    @JvmField
    @BindView(R.id.roundProgressBar2)
    var roundProgressBar2: RoundProgressBar_ChangePosition? = null

    @JvmField
    @BindView(R.id.txt_remain_time)
    var txt_remain_time: TextView? = null
    private var is_index = false
    //private val position //灯控，zigbee设备 = 0

    //    public static String ACTION_SRAUM_SETBOX = "ACTION_SRAUM_SETBOX";//notifactionId = 8 ->设置网关模式，sraum_setBox
    private var mMessageReceiver: MessageReceiver? = null
    private var dialogUtil: DialogUtil? = null
    private var panelType: String? = null
    private var panelName: String? = null
    private val panelNumber: String? = null
    private val deviceNumber: String? = null
    private var panelMAC: String? = null
    private val deviceList: MutableList<device> = ArrayList()
    private val panelList: MutableList<panellist> = ArrayList()
    private var type: String? = ""
    private var map: Map<*, *> = HashMap<Any?, Any?>()
    private var status: String? = null
    private var gateway_number: String? = null

    // private var areaNumber: String? = null
    private var isfirst = false
    override fun viewId(): Int {
        return R.layout.add_door_lock_act
    }

    override fun onView() {
        isfirst = true
        registerMessageReceiver()
        back!!.setOnClickListener(this)
        next_step_id!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this@AddDoorLockDevActivity)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        roundProgressBar2!!.setAdd_Delete("delete")//
        map = intent.getSerializableExtra("map") as Map<*, *>
        status = map["status"] as String?
        type = map["type"] as String?
        gateway_number = map["gateway_number"] as String?
        init_status_bar()
    }

    override fun onDestroy() {
        super.onDestroy()
        isfirst = false
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun init_status_bar() {
        roundProgressBar2!!.max = 255
        val index = intArrayOf(255)
        //        double c = (double) 100 / 90;//c = (10.0/3) = 3.333333
//        final float process = (float) c; //剩余30秒
        Thread(Runnable {
            var i = 0
            is_index = true
            while (is_index) {
                try {
                    Thread.sleep(1000)
                    if (roundProgressBar2 != null)
                        roundProgressBar2!!.progress = i.toFloat()

                    i++
                    runOnUiThread { //                                progress_loading_linear.setVisibility(View.GONE);
//                                loading_error_linear.setVisibility(View.VISIBLE);
                        if (index[0] >= 0) {
                            if (txt_remain_time != null)
                                txt_remain_time!!.text = "剩余" + index[0] + "秒"
                        } else {
                            if (txt_remain_time != null)
                                txt_remain_time!!.text = "剩余" + 0 + "秒"
                        }
                        index[0]--
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (index[0] < 0) {
                    sraum_setBox_quit("")
                    is_index = false
                    //                        if(getActivity() != null)
//                        txt_remain_time.setText("剩余" + 0 + "秒");
                    finish()
                }
            }
        }).start()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> common_qiut()
            R.id.next_step_id -> common_qiut()
        }
    }

    override fun onBackPressed() {
        common_qiut()
    }

    private fun common_qiut() {
        sraum_setBox_quit("")
        is_index = false
        finish()
        AppManager.getAppManager().finishActivity_current(
                SelectSmartDoorLockActivity::class.java
        )
        AppManager.getAppManager().finishActivity_current(
                SelectSmartDoorLockTwoActivity::class.java
        )
    }

    private val connWifiInterfacer: ConnWifiInterfacer? = null

    interface ConnWifiInterfacer {
        fun conn_wifi_interface()
    }

    private fun sraum_setBox_quit(pannelid: String) {
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = send_type()
        doit(pannelid, map)
    }

    private fun doit(pannelid: String, map: Map<*, *>) {
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer {
            //                        sraum_setBox_quit(pannelid, position);
        }, this@AddDoorLockDevActivity, null) {
            //请拿到盖板后操作
            override fun onSuccess(user: User) {
                //退出设置网关模式成功，后，
//                        if (!panelid.equals("")) {
//                            Intent intent = new Intent(MacdeviceActivity.this,
//                                    ChangePanelAndDeviceActivity.class);
//                            intent.putExtra("panelid", panelid);
//
//                            startActivity(intent);
//                            MacdeviceActivity.this.finish();
//                        }

//
//                        deviceList.addAll(user.deviceList);
//
//                        //面板的详细信息
//                        panelType = user.panelType;
//                        panelName = user.panelName;
//                        panelMAC = user.panelMAC;
                var intent: Intent? = null
                val areaNumber = SharedPreferencesUtil.getData(this@AddDoorLockDevActivity, "areaNumber", "") as String
                if (pannelid != "") {
                    when (panelType) {
                        "B201" -> intent = Intent(this@AddDoorLockDevActivity,
                                AddZigbeeDeviceScucessActivity::class.java)

                        //ChangePanelAndDeviceActivity
                        "A421" -> intent = Intent(this@AddDoorLockDevActivity,
                                ChangePanelAndDeviceActivity::class.java)
                    }
                    intent!!.putExtra("panelid", pannelid)
                    val bundle = Bundle()
                    bundle.putSerializable("deviceList", deviceList as Serializable)
                    //                            intent.putExtra("deviceList", (Serializable) deviceList);
                    intent.putExtra("panelType", panelType)
                    intent.putExtra("boxNumber", gateway_number)
                    intent.putExtra("panelName", panelName)
                    intent.putExtra("panelMAC", panelMAC)
                    intent.putExtra("areaNumber", areaNumber)
                    intent.putExtra("bundle_panel", bundle)
                    intent.putExtra("findpaneltype", "wangguan_status")
                    startActivity(intent)
                    finish()
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@AddDoorLockDevActivity, "该网关不存在")
            }
        }
        )
    }

    private fun send_type(): Map<*, *> {
        val map = HashMap<Any?, Any?>()
        val regId = SharedPreferencesUtil.getData(this@AddDoorLockDevActivity, "regId", "") as String
        map["regId"] = regId
        map["token"] = TokenUtil.getToken(this)
        //        String boxnumber = (String) SharedPreferencesUtil.getData(this, "boxnumber", "");
        map["boxNumber"] = gateway_number

//
//        final String type = (String) map.get("type");
//        String status = (String) map.get("status");
//        String gateway_number = (String) gatewayList.get(position).get("number");
        val areaNumber = SharedPreferencesUtil.getData(this@AddDoorLockDevActivity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        when (type) {
            "B201", "A421" -> map["status"] = "0" //进入设置模式
        }
        return map
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(MainGateWayActivity.ACTION_SRAUM_SETBOX)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == MainGateWayActivity.ACTION_SRAUM_SETBOX) {
                val messflag = intent.getStringExtra("notifactionId")
                val panelid = intent.getStringExtra("panelid")
                val gatewayid = intent.getStringExtra("gatewayid")
                if (gateway_number != null) {
                    if (gatewayid != null) {
                        if (gatewayid == gateway_number) {
                            if (messflag.equals("8")) { //notifactionId = 8 ->设置网关模式，sraum_setBox
                                //收到服务器端设置网关成功以后，跳转到修改面板名称，以及该面板下设备列表名称

                                //在网关转圈界面，下去拉设备，判断设备类型，不是我们的。网关不关，是我们的设备类型；在关网关。
                                //然后跳转到显示设备列表界面。
//                    ToastUtil.showToast(MacdeviceActivity.this,"messflag:" + messflag);
                                if (isfirst) {
                                    isfirst = false
                                    getPanel_devices(panelid!!)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    private fun getPanel_devices(panelid: String) {
        val map: MutableMap<String, Any?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@AddDoorLockDevActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@AddDoorLockDevActivity)
        map["areaNumber"] = areaNumber
        map["boxNumber"] = gateway_number
        map["panelNumber"] = panelid
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                object : Mycallback(AddTogglenInterfacer { getPanel_devices(panelid) }, this@AddDoorLockDevActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        panelList.clear()
                        deviceList.clear()
                        deviceList.addAll(user.deviceList)

                        //面板的详细信息
                        panelType = user.panelType
                        panelName = user.panelName
                        panelMAC = user.panelMAC
                        when (panelType) {
                            "B201", "A421" ->                                 //智能门锁
                                sraum_setBox_quit(panelid)
                            else -> {
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }
}