package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.ConfigZigbeeConnDialogFragment
import com.massky.sraum.view.RoundProgressBar_ChangePosition
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/5/30.
 */
class AddZigbeeDevActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.next_step_id)
    var next_step_id: Button? = null
    private var newFragment: ConfigZigbeeConnDialogFragment? = null

    //    private int[] icon = {R.drawable.icon_type_switch, R.drawable.menci_big,
    //            R.drawable.human_ganying_big, R.drawable.water, R.drawable.pm25,
    //            R.drawable.emergency_button};
    private val icon = intArrayOf(R.drawable.pic_zigbee_kaiguan_1, R.drawable.pic_zigbee_kaiguan_2,
            R.drawable.pic_zigbee_kaiguan_3, R.drawable.pic_zigbee_kaiguan_4,
            R.drawable.pic_zigbee_kaiguan_1_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_2_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_3_tiaoguang,
            R.drawable.pic_zigbee_kaiguan_chuanglian,
            R.drawable.pic_zigbee_kaiguan_1,
            R.drawable.pic_zigbee_menci,
            R.drawable.pic_zigbee_rentiganying,
            R.drawable.pic_zigbee_jiuzuo,
            R.drawable.pic_zigbee_yanwu,
            R.drawable.pic_zigbee_tianranqi,
            R.drawable.pic_zigbee_jinjianniu,
            R.drawable.pic_zigbee_zhinengmensuo,
            R.drawable.pic_zigbee_pm250,
            R.drawable.pic_zigbee_shuijin,
            R.drawable.pic_zigbee_duogongneng,
            R.drawable.pic_zigbee_chazuo,
            R.drawable.duogongneng2,
            R.drawable.pic_zigbee_pingyikzq,
            R.drawable.pic_wifi_yinyuemianban_4,
            R.drawable.pic_zigbee_kaiguan_1_0cj,
            R.drawable.pic_zigbee_kaiguan_2_0cj,
            R.drawable.pic_zigbee_kaiguan_3_0cj,
            R.drawable.pic_zigbee_kaiguan_4_0cj,
            R.drawable.guangzhao_zuwang,
            R.drawable.pic_zigbee_1kai,
            R.drawable.pic_zigbee_2kai,
            R.drawable.pic_zigbee_3kai
    )

    private val iconString = intArrayOf(
            R.string.yijianlight_promat,
            R.string.zigbee_other_promat,
            R.string.zigbee_promat_btn_down,
            R.string.zhinengchazuo_promat,
            R.string.duogongneng_promat,
            R.string.yijian_chazuo,
            R.string.mode_show,
            R.string.guanzhao_promat,
            R.string.zigbee_renti_promat
    )

    @JvmField
    @BindView(R.id.img_show_zigbee)
    var img_show_zigbee: ImageView? = null

    @JvmField
    @BindView(R.id.promat_zigbee_txt)
    var promat_zigbee_txt: TextView? = null

    @JvmField
    @BindView(R.id.roundProgressBar2)
    var roundProgressBar2: RoundProgressBar_ChangePosition? = null

    @JvmField
    @BindView(R.id.txt_remain_time)
    var txt_remain_time: TextView? = null


    //promat_zigbee_txt_one
    @JvmField
    @BindView(R.id.promat_zigbee_txt_one)
    var promat_zigbee_txt_one: TextView? = null

    private var is_index = false

    //private val position1 //灯控，zigbee设备 = 0
    private var mMessageReceiver: MessageReceiver? = null
    private var dialogUtil: DialogUtil? = null
    private var panelType: String? = null
    private var panelName: String? = null
    private val panelNumber: String? = null
    private val deviceNumber: String? = null
    private var panelMAC: String? = null
    private val deviceList: MutableList<device> = ArrayList()
    private val panelList: MutableList<panellist> = ArrayList()
    private var type = ""
    private var gateway_number: String? = null
    private var areaNumber: String? = null
    private var isfirst = false
    override fun viewId(): Int {
        return R.layout.add_zigbee_new_dev_act
    }

    override fun onView() {
        isfirst = true
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        registerMessageReceiver()
        back!!.setOnClickListener(this)
        next_step_id!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this@AddZigbeeDevActivity)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        roundProgressBar2!!.setAdd_Delete("delete")
        initDialog()
        type = intent.getSerializableExtra("type") as String
        gateway_number = intent.getSerializableExtra("boxNumber") as String //网关编号
        when (type) {
            "A201" -> {
                img_show_zigbee!!.setImageResource(icon[0])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A202" -> {
                img_show_zigbee!!.setImageResource(icon[1])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A203" -> {
                img_show_zigbee!!.setImageResource(icon[2])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A204" -> {
                img_show_zigbee!!.setImageResource(icon[3])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A301" -> {
                img_show_zigbee!!.setImageResource(icon[4])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A302" -> {
                img_show_zigbee!!.setImageResource(icon[5])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A303" -> {
                img_show_zigbee!!.setImageResource(icon[6])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A401", "A421" -> {
                img_show_zigbee!!.setImageResource(icon[7])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A501" -> {
                img_show_zigbee!!.setImageResource(icon[8])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "A801" -> {
                img_show_zigbee!!.setImageResource(icon[9])
                promat_zigbee_txt!!.setText(iconString[1])
            }
            "A901","A911" -> {
                img_show_zigbee!!.setImageResource(icon[10])
                promat_zigbee_txt!!.setText(iconString[8])
            }

            "A902" -> {
                img_show_zigbee!!.setImageResource(icon[11])
                promat_zigbee_txt!!.setText(iconString[2])
            }
            "AB01" -> {
                img_show_zigbee!!.setImageResource(icon[12])
                promat_zigbee_txt!!.setText(iconString[1])
            }
            "AB04" -> {
                img_show_zigbee!!.setImageResource(icon[13])
                promat_zigbee_txt!!.setText(iconString[1])
            }
            "B001" -> {
                img_show_zigbee!!.setImageResource(icon[14])
                promat_zigbee_txt!!.setText(iconString[1])
            }
            "B201" -> {
                //智能门锁
                img_show_zigbee!!.setImageResource(icon[15])
                promat_zigbee_txt!!.setText(iconString[2])
            }
            "AD01", "AD03" -> {
                img_show_zigbee!!.setImageResource(icon[16])
                promat_zigbee_txt!!.text = "用针捅小孔，连续按设备组网键3次"
            }
            "AC01" -> {
                img_show_zigbee!!.setImageResource(icon[17])
                promat_zigbee_txt!!.setText(iconString[1])
            }
//            "A911" -> {//吸顶人体感应
//                img_show_zigbee!!.setImageResource(icon[10])
//                promat_zigbee_txt!!.setText(iconString[1])
//            }
            "B301" -> {
                img_show_zigbee!!.setImageResource(icon[18])
                promat_zigbee_txt!!.setText(iconString[2])
            }
            "B101" -> {
                img_show_zigbee!!.setImageResource(icon[19])
                promat_zigbee_txt!!.setText(iconString[5])
            }
            "多功能面板" -> {
                img_show_zigbee!!.setImageResource(icon[20])
                promat_zigbee_txt!!.setText(iconString[4])
            }
            "平移控制器" -> {
                img_show_zigbee!!.setImageResource(icon[21])
                promat_zigbee_txt!!.setText(iconString[2])
            }

            "B501" -> {//音乐面板
                img_show_zigbee!!.setImageResource(icon[22])
                promat_zigbee_txt!!.setText(iconString[6])
            }

            "A211" -> {
                img_show_zigbee!!.setImageResource(icon[23])
                promat_zigbee_txt!!.setText(iconString[0])
                promat_zigbee_txt_one!!.visibility = View.VISIBLE
            }

            "A212" -> {
                img_show_zigbee!!.setImageResource(icon[24])
                promat_zigbee_txt!!.setText(iconString[0])
                promat_zigbee_txt_one!!.visibility = View.VISIBLE
            }

            "A213" -> {
                img_show_zigbee!!.setImageResource(icon[25])
                promat_zigbee_txt!!.setText(iconString[0])
                promat_zigbee_txt_one!!.visibility = View.VISIBLE
            }

            "A214" -> {
                img_show_zigbee!!.setImageResource(icon[26])
                promat_zigbee_txt!!.setText(iconString[0])
                promat_zigbee_txt_one!!.visibility = View.VISIBLE
            }
            "AE02" -> {//光照传感器
                img_show_zigbee!!.setImageResource(icon[27])//长按设置组网键3秒
                promat_zigbee_txt!!.setText(iconString[7])
            }
            "AD11" -> {
                img_show_zigbee!!.setImageResource(icon[28])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "AD12" -> {
                img_show_zigbee!!.setImageResource(icon[29])
                promat_zigbee_txt!!.setText(iconString[0])
            }
            "AD13" -> {
                img_show_zigbee!!.setImageResource(icon[30])
                promat_zigbee_txt!!.setText(iconString[0])
            }
        }
        init_status_bar()
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
                    if (roundProgressBar2 == null) {
                        is_index = false
                        return@Runnable
                    }//
                    roundProgressBar2!!.progress = i.toFloat()
                    i++
                    runOnUiThread {
                        //progress_loading_linear.setVisibility(View.GONE);
//                                loading_error_linear.setVisibility(View.VISIBLE);
                        if (index[0] >= 0) {
                            txt_remain_time!!.text = "剩余" + index[0] + "秒"
                        } else {
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
            R.id.back -> {
                sraum_setBox_quit("")
                is_index = false
                finish()
            }
            R.id.next_step_id -> {
                sraum_setBox_quit("")
                is_index = false
                finish()
            }
        }
    }

    override fun onBackPressed() {
        sraum_setBox_quit("")
        is_index = false
        finish()
    }

    /**
     * 初始化dialog
     */
    private fun initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigZigbeeConnDialogFragment.newInstance(this@AddZigbeeDevActivity, "", "", object : ConfigZigbeeConnDialogFragment.DialogClickListener {
            override fun doRadioWifi() { //wifi快速配置
            }

            override fun doRadioScanDevice() {}
            override fun dialogDismiss() {}
        }) //初始化快配和搜索设备dialogFragment
        connWifiInterfacer = newFragment
    } //

    private var connWifiInterfacer: ConnWifiInterfacer? = null

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
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map as MutableMap<String, Any>?, object : Mycallback(AddTogglenInterfacer {
            //                        sraum_setBox_quit(pannelid, position1);
        }, this@AddZigbeeDevActivity, null) {
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
                if (pannelid != "") {
                    when (panelType) {
                        "A421",
                        "A211", "A212", "A213", "A214",
                        "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A411", "A412", "A413", "A414", "A501", "A601", "A701", "B401", "B402", "B403"
                        ,"AD11", "AD12", "AD13"
                        -> intent = Intent(this@AddZigbeeDevActivity,
                                ChangePanelAndDeviceActivity::class.java)
                        "A511", "A611", "A711", "A801", "A901", "AE02", "A902", "AB01", "AB04", "AC01", "A911", "AD01", "AD03", "AD02", "B001", "B101", "B201", "B202", "B301", "B501" -> intent = Intent(this@AddZigbeeDevActivity,
                                AddZigbeeDeviceScucessActivity::class.java)
                    }
                    intent!!.putExtra("panelid", pannelid)
                    intent.putExtra("boxNumber", gateway_number)
                    val bundle = Bundle()
                    bundle.putSerializable("deviceList", deviceList as Serializable)
                    //                            intent.putExtra("deviceList", (Serializable) deviceList);
                    intent.putExtra("panelType", panelType)
                    intent.putExtra("panelName", panelName)
                    intent.putExtra("panelMAC", panelMAC)
                    intent.putExtra("bundle_panel", bundle)
                    intent.putExtra("findpaneltype", "wangguan_status")
                    intent.putExtra("areaNumber", areaNumber)
                    startActivity(intent)
                    finish()
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@AddZigbeeDevActivity, "该网关不存在")
            }
        })
    }

    private fun send_type(): Map<*, *> {
        val map = HashMap<Any?, Any?>()
        val regId = SharedPreferencesUtil.getData(this@AddZigbeeDevActivity, "regId", "") as String
        map["regId"] = regId
        map["token"] = TokenUtil.getToken(this)
        //        String boxnumber = (String) SharedPreferencesUtil.getData(this, "boxnumber", "");
        map["boxNumber"] = gateway_number

//
//        final String type = (String) map.get("type");
//        String status = (String) map.get("status");
//        String gateway_number = (String) gatewayList.get(position1).get("number");
        val areaNumber = SharedPreferencesUtil.getData(this@AddZigbeeDevActivity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        when (type) {
            "A421",
            "A211", "A212", "A213", "A214",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401", "A411", "A412", "A413", "A414", "B101", "B201", "B301", "A902",
            "A501", "A601", "A701", "A511", "A611", "A711", "AD01", "AD03", "多功能面板", "平移控制器", "B501","AD11", "AD12", "AD13" -> //                    case "A501":
//                    case "A511":
                map["status"] = "0" //进入设置模式
            "A801", "A901", "AE02", "AB01", "AB04", "B001", "AC01", "A911" -> map["status"] = "13" //进入设置模式
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
                Log.e("notifactionId", "receiver")
                var messflag = intent.getStringExtra("notifactionId")
                val panelid = intent.getStringExtra("panelid")
                val gatewayid = intent.getStringExtra("gatewayid")
                Log.e("Jpush", "onReceive: panelid->" + panelid)
                if (gateway_number != null) {
                    if (gatewayid != null) {
                        if (gatewayid == gateway_number) {
                            if (messflag == "8") { //notifactionId = 8 ->设置网关模式，sraum_setBox
                                //收到服务器端设置网关成功以后，跳转到修改面板名称，以及该面板下设备列表名称

                                //在网关转圈界面，下去拉设备，判断设备类型，不是我们的。网关不关，是我们的设备类型；在关网关。
                                //然后跳转到显示设备列表界面。
//                    ToastUtil.showToast(MacdeviceActivity.this,"messflag:" + messflag);
                                Log.e("Jpush", "onReceive: isfirst->" + isfirst)
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


    override fun onDestroy() {
        super.onDestroy()
        isfirst = false
    }

    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    private fun getPanel_devices(panelid: String) {
        val map: MutableMap<String, Any?> = HashMap()
        areaNumber = SharedPreferencesUtil.getData(this@AddZigbeeDevActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@AddZigbeeDevActivity)
        map["areaNumber"] = areaNumber
        map["boxNumber"] = gateway_number
        map["panelNumber"] = panelid
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                object : Mycallback(AddTogglenInterfacer { getPanel_devices(panelid) }, this@AddZigbeeDevActivity, dialogUtil) {
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
                        Log.e("Jpush", "onSuccess: panelType->" + panelType)
                        when (panelType) {
                            "A421",
                            "A211", "A212", "A213", "A214",
                            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A411", "A412", "A413", "A414", "A511", "A611", "A711", "A501", "A601", "A701", "A801", "A901", "A911", "A902", "AB01", "AB04", "AC01", "AE02", "AD01", "AD03",
                            "AD11", "AD12", "AD13",
                            "AD02", "B001", "B101", "B201", "B301", "B401", "B402", "B403", "B501" -> sraum_setBox_quit(panelid)
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