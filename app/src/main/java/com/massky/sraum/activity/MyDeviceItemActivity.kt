package com.massky.sraum.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.percentlayout.widget.PercentRelativeLayout
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.ipcamera.demo.BridgeService
import com.ipcamera.demo.BridgeService.AlarmInterface
import com.ipcamera.demo.PlayBackTFActivity
import com.ipcamera.demo.SettingSDCardActivity
import com.ipcamera.demo.bean.AlermBean
import com.ipcamera.demo.utils.ContentCommon
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.MyEvent
import com.massky.sraum.widget.SlideSwitchButton
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.ypy.eventbus.EventBus
import com.yxc.barchart.ui.step.StepActivity
import okhttp3.Call
import org.greenrobot.eventbus.Subscribe
import vstc2.nativecaller.NativeCaller
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/16.
 */
class MyDeviceItemActivity : BaseActivity(), SlideSwitchButton.SlideListener, AlarmInterface {
    private var open_time_bool: Boolean ? = false
    private var dialog1: Dialog? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var panelItem_map = HashMap<Any?, Any?>()

    @JvmField
    @BindView(R.id.device_name_txt)
    var device_name_txt: TextView? = null

    //
    @JvmField
    @BindView(R.id.mac_txt)
    var mac_txt: TextView? = null

    @JvmField
    @BindView(R.id.status_txt)
    var status_txt: TextView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.wangguan_set_rel)
    var wangguan_set_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.scene_list_rel)
    var scene_list_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.list_gujian_rel)
    var list_gujian_rel: PercentRelativeLayout? = null

    @JvmField
    @BindView(R.id.banben_txt)
    var banben_txt: TextView? = null

    @JvmField
    @BindView(R.id.panid_txt)
    var panid_txt: TextView? = null

    @JvmField
    @BindView(R.id.xindao_txt)
    var xindao_txt: TextView? = null

    @JvmField
    @BindView(R.id.gateway_id_txt)
    var gateway_id_txt: TextView? = null

    @JvmField
    @BindView(R.id.other_jiantou)
    var other_jiantou: ImageView? = null

    @JvmField
    @BindView(R.id.delete_device_rel)
    var delete_device_rel: RelativeLayout? = null
    private var dialogUtil: DialogUtil? = null
    private var panelNumber = ""

    @JvmField
    @BindView(R.id.next_step_id)
    var next_step_id: Button? = null

    @JvmField
    @BindView(R.id.wangguan_set)
    var wangguan_set: ImageView? = null

    @JvmField
    @BindView(R.id.rel_yaokongqi)
    var rel_yaokongqi: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_yaokongqi)
    var view_yaokongqi: View? = null

    @JvmField
    @BindView(R.id.dev_txt)
    var dev_txt: TextView? = null

    @JvmField
    @BindView(R.id.slide_btn)
    var slide_btn: SlideSwitchButton? = null

    @JvmField
    @BindView(R.id.view_bufang)
    var view_bufang: View? = null

    @JvmField
    @BindView(R.id.rel_bufang)
    var rel_bufang: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_bufang_baojing)
    var view_bufang_baojing: View? = null

    @JvmField
    @BindView(R.id.rel_bufang_baojing)
    var rel_bufang_baojing: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_bufang_plan)
    var view_bufang_plan: View? = null

    @JvmField
    @BindView(R.id.rel_bufang_plan)
    var rel_bufang_plan: RelativeLayout? = null

    //slide_btn_baojing
    @JvmField
    @BindView(R.id.slide_btn_baojing)
    var slide_btn_baojing: SlideSwitchButton? = null

    @JvmField
    @BindView(R.id.view_sd_set)
    var view_sd_set: View? = null

    @JvmField
    @BindView(R.id.rel_sd_set)
    var rel_sd_set: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_sdcard_remotevideo)
    var view_sdcard_remotevideo: View? = null

    @JvmField
    @BindView(R.id.rel_sdcard_remotevideo)
    var rel_sdcard_remotevideo: RelativeLayout? = null

    @JvmField
    @BindView(R.id.base_linear)
    var base_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.wangguan_linear)
    var wangguan_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.basic_information_wangguan)
    var basic_information_wangguan: RelativeLayout? = null

    @JvmField
    @BindView(R.id.gujian_upgrade_rel)
    var gujian_upgrade_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.change_password_rel)
    var change_password_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_gujian_upgrade)
    var view_gujian_upgrade: View? = null

    @JvmField
    @BindView(R.id.view_change_pass)
    var view_change_pass: View? = null

    @JvmField
    @BindView(R.id.status_txt_gateway)
    var status_txt_gateway: TextView? = null

    @JvmField
    @BindView(R.id.bufang_txt)
    var bufang_txt: TextView? = null

    @JvmField
    @BindView(R.id.update_time_rel)
    var update_time_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.update_time_view)
    var update_time_view: View? = null

    @JvmField
    @BindView(R.id.slide_btn_plan)
    var slide_btn_plan: SlideSwitchButton? = null

    //gujian_txt
    @JvmField
    @BindView(R.id.gujian_txt)
    var gujian_txt: TextView? = null

    //yingjian_txt
    @JvmField
    @BindView(R.id.yingjian_txt)
    var yingjian_txt: TextView? = null


    //renyuan_list_rel
    @JvmField
    @BindView(R.id.renyuan_list_rel)
    var renyuan_list_rel: RelativeLayout? = null

    //basic_power

    //renyuan_list_rel
    @JvmField
    @BindView(R.id.basic_power)
    var basic_power: RelativeLayout? = null

    @JvmField
    @BindView(R.id.bind_btn_rel)
    var bind_btn_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_bind_btn)
    var view_bind_btn: View? = null

    @JvmField
    @BindView(R.id.bind_btn_wifi_rel)
    var bind_btn_wifi_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_bind_wifi_btn)
    var view_bind_wifi_btn: View? = null


    //power_view
    @JvmField
    @BindView(R.id.power_view)
    var power_view: View? = null

    //base_view
    @JvmField
    @BindView(R.id.base_view)
    var base_view: View? = null

    //mac_view
    @JvmField
    @BindView(R.id.mac_view)
    var mac_view: TextView? = null

    //gujian_view,yingjian_view
    @JvmField
    @BindView(R.id.gujian_view)
    var gujian_view: TextView? = null

    @JvmField
    @BindView(R.id.yingjian_view)
    var yingjian_view: TextView? = null
    //取消，注销（红色）

    //view_xidingbaojing,rel_xidingbaojing

    @JvmField
    @BindView(R.id.view_xidingbaojing)
    var view_xidingbaojing: View? = null

    @JvmField
    @BindView(R.id.rel_xidingbaojing)
    var rel_xidingbaojing: RelativeLayout? = null

    //open_btn_rel
    @JvmField
    @BindView(R.id.open_btn_rel)
    var open_btn_rel: RelativeLayout? = null

    //view_open_btn
    @JvmField
    @BindView(R.id.view_open_btn)
    var view_open_btn: View? = null

    //open_time_txt
    @JvmField
    @BindView(R.id.open_time_txt)
    var open_time_txt: TextView? = null


    @JvmField
    @BindView(R.id.view_member)
    var view_member: View? = null

    @JvmField
    @BindView(R.id.view_power)
    var view_power: View? = null

    @JvmField
    @BindView(R.id.basic_information)
    var basic_information: RelativeLayout? = null

    @JvmField
    @BindView(R.id.six_rel)
    var six_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.view_six)
    var view_six: View? = null


    private val ALERMPARAMS = 3
    private val iconName = intArrayOf(R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang, R.string.lianglutiaoguang, R.string.sanlutiao, R.string.window_panel, R.string.air_panel,
            R.string.fresh_panel, R.string.dinuan_panel,
            R.string.air_mode, R.string.xinfeng_mode, R.string.dinuan_mode, R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.jixieshou, R.string.cha_zuo_1, R.string.cha_zuo, R.string.wifi_hongwai,
            R.string.wifi_camera, R.string.one_light_control, R.string.two_light_control, R.string.three_light_control, R.string.two_dimming_one_control, R.string.two_dimming_two_control, R.string.two_dimming_trhee_control, R.string.keshimenling, R.string.zhinengwangguan_2, R.string.one_curtain_zero_light, R.string.one_curtain_one_light, R.string.one_curtain_two_light,
            R.string.two_curtain, R.string.table_pm, R.string.smart_up_down, R.string.smart_pingyi,
            R.string.smart_top_lower, R.string.wifi_one_key, R.string.wifi_two_key, R.string.wifi_three_key,
            R.string.wifi_four_key, R.string.one_light_zero_scene,
            R.string.two_light_zero_scene, R.string.three_light_zero_scene, R.string.four_light_zero_scene, R.string.curtain_mechine, R.string.music_panel, R.string.tv_oc, R.string.yingshi_camera, R.string.xiding_rentiganying, R.string.guangzhao_chuangan,
            R.string.liangjian_panel, R.string.sijian_panel, R.string.liujian_panel, R.string.wifi_window_1
    )
    private var isUse: String? = null
    private val option = ContentCommon.INVALID_OPTION
    private val CameraType = ContentCommon.CAMERA_TYPE_MJPEG
    var list_wifi_camera: List<Map<*, *>> = ArrayList()
    private val tag = 0
    private val connection_wifi_camera_index = 0
    private val again_connection = false
    private var mapdevice = HashMap<Any?, Any?>()
    private var alermBean: AlermBean? = null
    private var strDID = ""
    private val isfrom = ""
    private val isFirst = false
    private var boxNumber: String? = null
    private var authType: String? = null
    private var areaNumber: String? = null
    private val boxStatus: String? = null
    private var currentVersion: String? = null
    private var newVersion: String? = null
    private var wifi_map = HashMap<Any?, Any?>()
    private var deviceList: MutableList<User.device> = ArrayList()
    private var gateway_map = java.util.HashMap<Any?, Any?>()
    private val list_map: MutableList<MutableMap<*, *>> = ArrayList()

    override fun viewId(): Int {
        return R.layout.my_device_item_act
    }

    override fun onView() {
//        isFirst = true;//第一次进入该界面
        EventBus.getDefault().register(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
        onEvent1()
        panelItem_map = intent.getSerializableExtra("panelItem") as HashMap<Any?, Any?>
        val imgtype = intent.getSerializableExtra("imgtype") as Int
        if (panelItem_map != null) {
            device_name_txt!!.text = panelItem_map.get("name").toString()
            project_select!!.text = panelItem_map.get("name").toString()
            mac_txt!!.text = panelItem_map.get("mac").toString()

            gujian_txt!!.text = panelItem_map.get("firmware").toString()
            yingjian_txt!!.text = panelItem_map.get("hardware").toString()

            boxNumber = panelItem_map.get("boxNumber").toString()
            //            banben_txt.setText(panelItem_map.get("firmware").toString());
//            panid_txt.setText(panelItem_map.get("hardware").toString());
            panelNumber = panelItem_map.get("number").toString()
            isUse = if (panelItem_map.get("isUse") == null) "" else panelItem_map["isUse"].toString()
            get_sensor_isUse()
            wangguan_set!!.setImageResource(imgtype)
            set_type(panelItem_map.get("type").toString())
            //成员，业主accountType->addrelative_id
//            authType = (String) SharedPreferencesUtil.getData(MyDeviceItemActivity.this, "authType", "");
            areaNumber = intent.getSerializableExtra("areaNumber") as String
            authType = intent.getSerializableExtra("authType") as String
            when (authType) {
                "1" -> {
                    delete_device_rel!!.visibility = View.VISIBLE
                    //                    wangguan_set_rel.setEnabled(true);
                    gujian_upgrade_rel!!.visibility = View.VISIBLE
                    change_password_rel!!.visibility = View.VISIBLE
                    view_change_pass!!.visibility = View.VISIBLE
                    view_gujian_upgrade!!.visibility = View.VISIBLE
                }

                "2" -> {
                    delete_device_rel!!.visibility = View.GONE
                    update_time_view!!.visibility = View.GONE
                    gujian_upgrade_rel!!.visibility = View.GONE
                    change_password_rel!!.visibility = View.GONE
                    view_change_pass!!.visibility = View.GONE
                    view_gujian_upgrade!!.visibility = View.GONE
                }
            }
            when (panelItem_map.get("type").toString()) {
                "A2A1",
                "A2A2",
                "A2A3",
                "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> {
                    wangguan_linear!!.visibility = View.VISIBLE
                    view_change_pass!!.visibility = View.VISIBLE
                    change_password_rel!!.visibility = View.GONE
                    update_time_view!!.visibility = View.GONE
                    update_time_rel!!.visibility = View.GONE
                    view_change_pass!!.visibility = View.GONE
                    scene_list_rel!!.visibility = View.GONE
                    view_power!!.visibility = View.GONE
                }
                "B201" -> {
                    renyuan_list_rel!!.visibility = View.VISIBLE
                    view_member!!.visibility = View.VISIBLE
                }
                "网关" -> {
                    wangguan_linear!!.visibility = View.VISIBLE
                    view_change_pass!!.visibility = View.VISIBLE
                    view_power!!.visibility = View.GONE
                }
                "A088" -> {
                    gujian_upgrade_rel!!.visibility = View.GONE
                    wangguan_linear!!.visibility = View.VISIBLE
                    view_power!!.visibility = View.GONE
                    update_time_view!!.visibility = View.GONE
                    update_time_rel!!.visibility = View.GONE
                    view_gujian_upgrade!!.visibility = View.GONE
                }
                else -> base_linear!!.visibility = View.VISIBLE
            }

            when (panelItem_map.get("type").toString()) {
                "A2A1", "A2A2", "A2A3", "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> {
//                    power_view!!.visibility = View.VISIBLE
//                    basic_power!!.visibility = View.GONE
                }
                "A211", "A212", "A213", "A214",
                "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322",
                "A331", "A501", "A511", "A611", "A601", "A711", "A701", "A401", "A412", "A413", "B501" -> {
                    basic_power!!.visibility = View.VISIBLE
                    power_view!!.visibility = View.VISIBLE
                }
                else -> {
                    basic_power!!.visibility = View.GONE
                    power_view!!.visibility = View.GONE
                }
            }

            when (panelItem_map.get("type").toString()) {
                "A201", "A202", "A203", "A204" -> {
                    var firmware = panelItem_map.get("firmware").toString()
                    var objects = firmware.split("V")
                    if (objects[1] == null) return
                    if (objects[1].toDouble() >= 5.0) {
                        bind_btn_rel!!.visibility = View.VISIBLE
                        view_bind_btn!!.visibility = View.VISIBLE
                    }
                    Log.e("TAG", "objects[1]: " + objects[1])
                }
                "ADA1", "ADA2", "ADA3" -> {
                    bind_btn_wifi_rel!!.visibility = View.VISIBLE
                    view_bind_wifi_btn!!.visibility = View.VISIBLE
                }
            }
        }
        //        onEvent();
    }

    /**
     * 摄像头和传感器获取布防状态的共用方法
     *
     * @param slide_btn_baojing
     */
    private fun camera_bufang_isuse(slide_btn_baojing: SlideSwitchButton?) {
        when (isUse) {
            "1" -> slide_btn_baojing!!.changeOpenState(true)
            "0" -> slide_btn_baojing!!.changeOpenState(false)
        }
    }

    private fun onEvent1() {
        back!!.setOnClickListener(this)
        wangguan_set_rel!!.setOnClickListener(this)
        scene_list_rel!!.setOnClickListener(this)
        next_step_id!!.setOnClickListener(this)
        rel_sdcard_remotevideo!!.setOnClickListener(this)
        update_time_rel!!.setOnClickListener(this)
        basic_power!!.setOnClickListener(this)
        bind_btn_wifi_rel!!.setOnClickListener(this)
    }

    private fun init_status() {
        when (if (panelItem_map!!["status"] == null) "" else panelItem_map!!["status"].toString()) {
            "0" -> {
                status_txt!!.text = "离线"
                status_txt_gateway!!.text = "离线"
            }
            else -> {
                status_txt!!.text = "在线"
                status_txt_gateway!!.text = "在线"
            }
        }
    }

    //MESSAGE_TONGZHI_VIDEO_TO_MYDEVICE
    /**
     * 传感器类设置是否启用布防
     */
    private fun sensor_set_protection(isUse: String, type: String) {
        dialogUtil!!.loadDialog()
        var method: String? = ""
        val mapbox: MutableMap<String, Any?> = HashMap()
        mapbox["token"] = TokenUtil.getToken(this@MyDeviceItemActivity)
        mapbox["isUse"] = isUse
        mapbox["areaNumber"] = areaNumber
        when (type) {
            "AD02" -> {
                method = ApiHelper.sraum_setWifiDeviceIsUseCommon
                mapbox["number"] = panelNumber
            }
            else -> {
                method = ApiHelper.sraum_setLinkSensorPanelIsUse
                mapbox["panelNumber"] = panelNumber
            }
        }

        MyOkHttp.postMapObject(method, mapbox, object : Mycallback(AddTogglenInterfacer { sensor_set_protection(isUse, type) }, this@MyDeviceItemActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (type) {//音乐面板能耗
                    "AD02" -> when (isUse) {
                        "1" -> ToastUtil.showToast(this@MyDeviceItemActivity, "启用成功")
                        "0" -> ToastUtil.showToast(this@MyDeviceItemActivity, "禁用成功")
                    }
                    else -> when (isUse) {
                        "1" -> ToastUtil.showToast(this@MyDeviceItemActivity, "布防成功")
                        "0" -> ToastUtil.showToast(this@MyDeviceItemActivity, "撤防成功")
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
                ToastUtil.showToast(this@MyDeviceItemActivity, "token 错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@MyDeviceItemActivity, """
     panelNumber
     不正确
     """.trimIndent())
            }
        })
    }

    //MESSAGE_TONGZHI_VIDEO_TO_MYDEVICE
    /**
     * 摄像头设置是否启用移动侦测布防
     */
    private fun sraum_setWifiCameraIsUse(isUse: String) {
        dialogUtil!!.loadDialog()
        val mapbox: MutableMap<String, Any?> = HashMap()
        mapbox["token"] = TokenUtil.getToken(this@MyDeviceItemActivity)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(MyDeviceItemActivity.this,"areaNumber","");
        mapbox["number"] = panelNumber
        mapbox["areaNumber"] = areaNumber
        mapbox["isUse"] = isUse
        MyOkHttp.postMapObject(ApiHelper.sraum_setWifiCameraIsUse, mapbox, object : Mycallback(AddTogglenInterfacer { sensor_set_protection(isUse, "") }, this@MyDeviceItemActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (isUse) {
                }
            }

            override fun wrongToken() {
                super.wrongToken()
                ToastUtil.showToast(this@MyDeviceItemActivity, "token 错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@MyDeviceItemActivity, """
     panelNumber
     不正确
     """.trimIndent())
            }
        })
    }

    override fun onEvent() {
        rel_yaokongqi!!.setOnClickListener(this)
        slide_btn!!.setSlideListener(this)
        slide_btn_baojing!!.setSlideListener(this)
        slide_btn_plan!!.setSlideListener(this)
        rel_bufang_plan!!.setOnClickListener(this)
        rel_sd_set!!.setOnClickListener(this)
        basic_information_wangguan!!.setOnClickListener(this)
        gujian_upgrade_rel!!.setOnClickListener(this)
        change_password_rel!!.setOnClickListener(this)
        renyuan_list_rel!!.setOnClickListener(this)
        rel_xidingbaojing!!.setOnClickListener(this)
        bind_btn_rel!!.setOnClickListener(this)
        open_btn_rel!!.setOnClickListener(this)
    }

    override fun onData() {}

    private fun set_type(type: String) {
        banben_txt!!.text = type
        when (type) {
            "A201" -> gateway_id_txt!!.setText(iconName[0])
            "A202" -> gateway_id_txt!!.setText(iconName[1])
            "A203" -> gateway_id_txt!!.setText(iconName[2])
            "A204" -> gateway_id_txt!!.setText(iconName[3])
            "A301" -> gateway_id_txt!!.setText(iconName[4])
            "A302" -> gateway_id_txt!!.setText(iconName[5])
            "A303" -> gateway_id_txt!!.setText(iconName[6])
            "A401" -> gateway_id_txt!!.setText(iconName[7])
            "A501" -> gateway_id_txt!!.setText(iconName[8])
            "A601" -> gateway_id_txt!!.setText(iconName[9])
            "A701" -> gateway_id_txt!!.setText(iconName[10])
            "A511" -> gateway_id_txt!!.setText(iconName[11])
            "A611" -> gateway_id_txt!!.setText(iconName[12])
            "A711" -> gateway_id_txt!!.setText(iconName[13])
            "A801" -> {
                gateway_id_txt!!.setText(iconName[14])
                sensor_common_select()
            }
            "A901" -> {
                gateway_id_txt!!.setText(iconName[15])
                sensor_common_select()
            }
            "A902" -> {
                gateway_id_txt!!.setText(iconName[16])
                sensor_common_select()
            }
            "AB01" -> {
                gateway_id_txt!!.setText(iconName[17])
                sensor_common_select()
            }
            "AB04" -> {
                gateway_id_txt!!.setText(iconName[18])
                sensor_common_select()
            }
            "B001" -> gateway_id_txt!!.setText(iconName[19])
            "B201" -> gateway_id_txt!!.setText(iconName[20])
            "AD01" -> gateway_id_txt!!.setText(iconName[21])
            "AC01" -> {
                gateway_id_txt!!.setText(iconName[22])
                sensor_common_select()
            }
            "B301" -> gateway_id_txt!!.setText(iconName[23])
            "B101" -> gateway_id_txt!!.setText(iconName[24])
            "ADB1" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[24])
            }
            "B102" -> gateway_id_txt!!.setText(iconName[25])
            "AA02" -> {
                gateway_id_txt!!.setText(iconName[26])
                rel_yaokongqi!!.visibility = View.VISIBLE
                view_yaokongqi!!.visibility = View.VISIBLE
                dev_txt!!.text = "WIFI"
                banben_txt!!.text = panelItem_map!!["wifi"].toString()
                //controllerId
                mac_txt!!.text = if (panelItem_map!!["number"].toString() == null) "" else panelItem_map!!["number"].toString()
            }
            "AA03" -> common_parameter(27)
            "A311" -> gateway_id_txt!!.setText(iconName[28])
            "A312" -> gateway_id_txt!!.setText(iconName[29])
            "A313" -> gateway_id_txt!!.setText(iconName[30])
            "A321" -> gateway_id_txt!!.setText(iconName[31])
            "A322" -> gateway_id_txt!!.setText(iconName[32])
            "A331" -> gateway_id_txt!!.setText(iconName[33])
            "AA04" -> {
                //                common_parameter(32);
                gateway_id_txt!!.setText(iconName[34])
                dev_txt!!.text = "WIFI"
                banben_txt!!.text = panelItem_map!!["wifi"].toString()
                //controllerId
                mac_txt!!.text = panelItem_map!!["controllerId"].toString()

            }
            "AA05" -> {
                mac_view!!.setText("序列号")
                gujian_view!!.setText("验证码")
                mac_txt!!.setText(panelItem_map!!["number"].toString())
                gujian_txt!!.setText(panelItem_map!!["wifi"].toString())
                six_rel!!.visibility = View.GONE
                view_six!!.visibility = View.GONE
                gateway_id_txt!!.setText("新摄像头")
            }
            "网关" -> {
                scene_list_rel!!.visibility = View.GONE
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[35])
            }
            "A088" -> {
                scene_list_rel!!.visibility = View.GONE
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText("A088")
            }
            "A411" -> gateway_id_txt!!.setText(iconName[36])
            "A412" -> gateway_id_txt!!.setText(iconName[37])
            "A413" -> gateway_id_txt!!.setText(iconName[38])
            "A414" -> gateway_id_txt!!.setText(iconName[39])
            "AD02" -> {
                gateway_id_txt!!.setText(iconName[40])
                sensor_common_select()
                bufang_txt!!.text = "启用"
            }
            "B401" -> gateway_id_txt!!.setText(iconName[41])
            "B402" -> gateway_id_txt!!.setText(iconName[42])
            "B403" -> gateway_id_txt!!.setText(iconName[43])
            "A2A1" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[44])
            }
            "A2A2" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[45])
            }
            "A2A3" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[46])
            }
            "A2A4" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[47])
            }
            "A211" -> gateway_id_txt!!.setText(iconName[48])
            "A212" -> gateway_id_txt!!.setText(iconName[49])
            "A213" -> gateway_id_txt!!.setText(iconName[50])
            "A214" -> gateway_id_txt!!.setText(iconName[51])
            "A421" -> {//窗帘电机
                gateway_id_txt!!.setText(iconName[52])
                open_time_show()
            }
            "B501" -> gateway_id_txt!!.setText(iconName[53])
            "AD03" -> gateway_id_txt!!.setText(iconName[54])
            "AA05" -> gateway_id_txt!!.setText(iconName[55])
            "A911" -> {//吸顶人体感应
                gateway_id_txt!!.setText(iconName[56])
                sensor_common_select()
                view_xidingbaojing!!.visibility = View.VISIBLE
                rel_xidingbaojing!!.visibility = View.VISIBLE
            }
            "AE02" -> {//光照传感器
                gateway_id_txt!!.setText(iconName[57])
//                sensor_common_select()
            }
            "AD11" -> {
                gateway_id_txt!!.setText(iconName[58])
            }
            "AD12" -> {
                gateway_id_txt!!.setText(iconName[59])
            }
            "AD13" -> {
                gateway_id_txt!!.setText(iconName[60])
            }
            "ADA1" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[58])
            }
            "ADA2" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[59])
            }
            "ADA3" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[60])
            }
            "ADB2" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText(iconName[61])
            }
            "ADB3" -> {
                basic_information!!.visibility = View.GONE
                base_view!!.visibility = View.GONE
                gateway_id_txt!!.setText("入墙PM2.5")
            }
        }
    }

    private fun open_time_show() {
        var firmware = if (panelItem_map.get("firmware") == null) null
        else panelItem_map.get("firmware").toString()
        if (firmware == null) return //v1.6
        if (firmware.length < 4) return
        var hex_firm = firmware.substring(1, 2) + firmware.substring(3, 4)
        if (Integer.parseInt(hex_firm, 16) <= 21) return // 16进制15转换为十进制21
        open_time_bool = true
        open_btn_rel!!.visibility = View.VISIBLE
        view_open_btn!!.visibility = View.VISIBLE
    }

    /**
     * 摄像头，和门铃共同的代码
     *
     * @param i
     */
    private fun common_parameter(i: Int) {
        gateway_id_txt!!.setText(iconName[i])
        dev_txt!!.text = "WIFI"
        banben_txt!!.text = panelItem_map!!["wifi"].toString()
        //controllerId
        mac_txt!!.text = panelItem_map!!["number"].toString()

        //去获取摄像头的状态
//                this.mapdevice = mapdevice;
        mapdevice = HashMap<Any?, Any?>()
        //dimmer,temperature,mode
        mapdevice["dimmer"] = panelItem_map!!["number"].toString()
        mapdevice["temperature"] = "888888"
        mapdevice["mode"] = "admin"
        //                onitem_wifi_shexiangtou(mapdevice);//(String strUser, String strPwd, String strDID
        //去首页获取状态，发送广播
        tongzhi_video(mapdevice)
        init_nativeCaller()
    }

    /**
     * 初始化布防参数
     */
    private fun init_nativeCaller() {
        if (dialogUtil != null) dialogUtil!!.loadDialog()
        strDID = panelItem_map!!["number"].toString()
        //        NativeCaller.PPPPGetSystemParams(strDID,
//                ContentCommon.MSG_TYPE_GET_PARAMS);
        alermBean = AlermBean()
        BridgeService.setAlarmInterface(this)
    }

    /**
     * 发送给首页，获取摄像头状态，并返回
     */
    private fun tongzhi_video(map: Map<*, *>) {
        val mIntent = Intent(MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE)
        mIntent.putExtra("video_item", map as Serializable)
        sendBroadcast(mIntent)
    }

    /**
     * 传感器共有
     */
    private fun sensor_common_select() {
        view_bufang!!.visibility = View.VISIBLE
        rel_bufang!!.visibility = View.VISIBLE
    }

    private fun base_Activity() {
        when (panelItem_map.get("type").toString()) {
            "A2A1", "A2A2", "A2A3", "A2A4",
            "A211", "A212", "A213", "A214",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303",
            "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A412", "A413", "A501", "A511", "A611", "A601", "A711", "A701", "B501", "AD11", "AD12", "AD13" -> {

                if (deviceList.size > 1) {
                    key_power_activity()
                } else if (deviceList.size == 1) {
                    step_activity_power()
                }
            }
            "ADA1", "ADA2", "ADA3" -> {
                key_power_activity()
            }
        }
    }

    private fun step_activity_power() {
        var intent: Intent? = null
        list_map.clear()
        for (position in deviceList.indices) {
            val map = HashMap<Any?, Any?>()
            map["type"] = deviceList[position].type
            map["number"] = deviceList[position].number
            map["status"] = deviceList[position].status
            map["dimmer"] = deviceList[position].dimmer
            map["mode"] = deviceList[position].mode
            map["temperature"] = deviceList[position].temperature
            map["speed"] = deviceList[position].speed
            map["boxName"] = deviceList[position].boxName
            map["name"] = deviceList[position].name
            list_map.add(map)
        }

        intent = Intent(
                this@MyDeviceItemActivity,
                StepActivity::class.java
        )
        intent.putExtra("device_map", list_map[0] as MutableMap<Any, Any> as Serializable)
        intent.putExtra("in_type", "device_in")
        // intent.putExtra("sensor_map", sensor_map as Serializable)
        startActivity(intent)
    }

    private fun key_power_activity() {
        var intent: Intent? = null
        intent = Intent(this@MyDeviceItemActivity, KeyPowerActivity::class.java)
        //           intent.putExtra("type", (Serializable) listtype.get(position));
        intent.putExtra("panelNumber", panelItem_map!!["number"].toString() as Serializable?)
        intent.putExtra("panelType", panelItem_map.get("type") as Serializable?)
        intent.putExtra("panelName", panelItem_map.get("name") as Serializable?)
        intent.putExtra("boxName",
                if (panelItem_map.get("boxName") as Serializable? == null) "" else panelItem_map.get("boxName") as String)
        intent.putExtra("gatewayMac",
                if (panelItem_map.get("gatewayMac") as Serializable? == null) "" else panelItem_map.get("gatewayMac") as String)
        //boxNumber
        intent.putExtra("boxNumber", if (panelItem_map.get("boxNumber") as Serializable?
                == null) "" else panelItem_map.get("boxNumber").toString())
        //传感器参数
        //                Map mapdevice = new HashMap();
        //                mapdevice.put("sensorType", map_link.get("deviceType"));
        //                mapdevice.put("sensorId", map_link.get("deviceId"));
        //                mapdevice.put("sensorName",map_link.get("name"));
        //                mapdevice.put("sensorCondition", map_link.get("condition"));
        //                mapdevice.put("sensorMinValue", map_link.get("minValue"));
        //                mapdevice.put("sensorMaxValue", map_link.get("maxValue"));
        // intent.putExtra("sensor_map", map_link as Serializable?)

        startActivity(intent)
    }

    override fun onClick(v: View) {
        var intent: Intent? = null
        when (v.id) {
            R.id.basic_power -> {//能耗
                base_Activity()
//                intent = Intent(this@MyDeviceItemActivity, StepActivity::class.java)
//                startActivity(intent)
            }

            R.id.update_time_rel -> when (authType) {
                "1" -> sraum_setGatewayTime()
                "2" -> ToastUtil.showToast(this@MyDeviceItemActivity, "家庭成员不能同步时间")
            }

            R.id.back -> finish()
            R.id.wangguan_set_rel -> if (panelItem_map != null) {
                intent = Intent(this@MyDeviceItemActivity, EditMyDeviceActivity::class.java)
                intent.putExtra("panelItem", panelItem_map as Serializable?)
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("authType", authType)
                startActivity(intent)
            }
            R.id.scene_list_rel -> if (list_gujian_rel!!.visibility == View.VISIBLE) {
                list_gujian_rel!!.visibility = View.GONE
                other_jiantou!!.setImageResource(R.drawable.bg_you)
            } else {
                list_gujian_rel!!.visibility = View.VISIBLE
                other_jiantou!!.setImageResource(R.drawable.bg_xia)
            }
            R.id.next_step_id -> showCenterDeleteDialog(panelNumber, panelItem_map!!["name"].toString(), panelItem_map!!["type"].toString())
            R.id.rel_yaokongqi -> {
                intent = Intent(this@MyDeviceItemActivity, SelectYaoKongQiActivity::class.java)
                intent.putExtra("controllerNumber", panelItem_map!!["number"].toString()) //controllerNumber
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("authType", authType)
                startActivity(intent)
            }
            R.id.rel_bufang_plan -> {
                //                startActivity(new Intent(MyDeviceItemActivity.this,BuFangBaoJingPlanActivity.class));
                val intentalam = Intent(this@MyDeviceItemActivity, BuFangBaoJingPlanActivity::class.java)
                intentalam.putExtra(ContentCommon.STR_CAMERA_PWD, "888888")
                intentalam.putExtra(ContentCommon.STR_CAMERA_ID, strDID)
                intentalam.putExtra("areaNumber", areaNumber)
                startActivity(intentalam)
            }
            R.id.rel_sd_set -> {
                //                startActivity(new Intent(MyDeviceItemActivity.this,BuFangBaoJingPlanActivity.class));
                val intent_sd_set = Intent(this@MyDeviceItemActivity, SettingSDCardActivity::class.java)
                intent_sd_set.putExtra(ContentCommon.STR_CAMERA_PWD, "888888")
                intent_sd_set.putExtra(ContentCommon.STR_CAMERA_ID, strDID)
                startActivity(intent_sd_set)
            }
            R.id.rel_sdcard_remotevideo -> {
                val intentVid = Intent(this@MyDeviceItemActivity, PlayBackTFActivity::class.java)
                intentVid.putExtra(ContentCommon.STR_CAMERA_NAME, "admin")
                intentVid.putExtra(ContentCommon.STR_CAMERA_ID, strDID)
                intentVid.putExtra(ContentCommon.STR_CAMERA_PWD, "888888")
                intentVid.putExtra(ContentCommon.STR_CAMERA_USER, "admin")
                startActivity(intentVid)
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
            }
            R.id.basic_information_wangguan -> {
                val intent_gateway = Intent(this@MyDeviceItemActivity, WangGuanBaseInformationActivity::class.java
                )
                intent_gateway.putExtra("areaNumber", areaNumber)
                intent_gateway.putExtra("number", panelItem_map!!["number"].toString())
                intent_gateway.putExtra("panelItem_map", panelItem_map)

                when (if (panelItem_map!!["type"] == null) "" else panelItem_map!!["type"].toString()) {
                    "A2A1", "A2A2", "A2A3", "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> intent_gateway.putExtra("wifi_map", wifi_map)
                }
                startActivity(intent_gateway)
            }

            R.id.gujian_upgrade_rel -> getgateway_version(panelItem_map.get("type").toString())
            R.id.change_password_rel -> {
                val bundle_change_boxpass = Bundle()
                bundle_change_boxpass.putString("boxName", panelItem_map!!["name"].toString())
                bundle_change_boxpass.putString("boxnumber", panelItem_map!!["number"].toString())
                bundle_change_boxpass.putString("boxName", panelItem_map!!["name"].toString())
                bundle_change_boxpass.putString("areaNumber", areaNumber)
                IntentUtil.startActivity(this@MyDeviceItemActivity, ChangeWangGuanpassActivity::class.java, bundle_change_boxpass)
            }

            R.id.renyuan_list_rel -> {//人员列表信息
                intent = Intent(this@MyDeviceItemActivity, MemberListActivity::class.java)
                if (deviceList.size != 0) {
                    intent.putExtra("deviceNumber", deviceList[0].number) //controllerNumber
                }
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("authType", authType)
                startActivity(intent)
            }
            R.id.rel_xidingbaojing -> {

                if (deviceList.size == 0 || deviceList == null) return
                val map = HashMap<Any?, Any?>()
                loop@ for (position in deviceList.indices) {
                    when (deviceList[position].type) {
                        "24" -> {
                            map["name1"] = deviceList[position].name1
                            map["name2"] = deviceList[position].name2
                            map["number"] = deviceList[position].number
                            break@loop
                        }
                    }
                }

                intent = Intent(this@MyDeviceItemActivity, ChangeXiDingTimeDurationActivity::class.java)
                intent.putExtra("xiding_map", map)
                startActivity(intent)
            }
            R.id.bind_btn_rel, R.id.bind_btn_wifi_rel -> {//去绑定外部开关，,R.id.bind_btn_rel
                intent = Intent(this@MyDeviceItemActivity, MyBindBtnListActivity::class.java)
                intent!!.putExtra("areaNumber", areaNumber)
                intent.putExtra("authType", authType)
                //panelNumber
                intent.putExtra("panelNumber", panelNumber)
                intent.putExtra("type", panelItem_map.get("type").toString())
                startActivity(intent)
            }

            R.id.open_btn_rel-> {
                //开合度时间

                if(deviceList.size == 0) return
                intent = Intent(this@MyDeviceItemActivity, OpenCurtainTimeActivity::class.java)
                intent!!.putExtra("mode", if(deviceList[0].mode == null) "" else
                    deviceList[0].mode)
                intent!!.putExtra("areaNumber", areaNumber)
                intent!!.putExtra("deviceNumber", deviceList[0].number)
                startActivity(intent)
            }
        }
    }

    /**
     * 添加面板下的设备信息
     */
    private fun getData(flag: Boolean, actions: String) {
//            val map: MutableMap<String, Any?> = java.util.HashMap()
//            map["token"] = TokenUtil.getToken(this@MyDeviceItemActivity)
//            //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
//            map["boxNumber"] = boxNumber
//            map["panelNumber"] = panelNumber
//            map["areaNumber"] = areaNumber

        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@MyDeviceItemActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@MyDeviceItemActivity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        var api = ""
        when (actions) {
            "wifi" -> {
                map["deviceId"] = panelNumber
                api = ApiHelper.sraum_getWifiButtons!!
            }
            "devices" -> {
                api = ApiHelper.sraum_getPanelDevices
                map["boxNumber"] = boxNumber
                map["panelNumber"] = panelNumber
            }
        }
        common_devices_show(map, api)
    }

    private fun common_devices_show(map: MutableMap<String, Any?>, api: String?) {
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { common_devices_show(map, api) }, this@MyDeviceItemActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        deviceList.clear()
                        deviceList.addAll(user.deviceList)
                        Log.e("my", "onSuccess:deviceList " + Gson().toJson(deviceList))
                        show_new_devices_powers()
                        isUse = if (user.isUse == null) "" else user.isUse
                        get_sensor_isUse()
                        progress_open_curtain_value()
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun progress_open_curtain_value() {
        //open_time_bool
        if(!open_time_bool!!) return
        if(deviceList.size == 0) return
        if(open_time_txt == null) return
        open_time_txt!!.setText(deviceList[0].mode + "s")
    }

    private fun get_sensor_isUse() {
        when (panelItem_map.get("type").toString()) {
            "AA03" -> camera_bufang_isuse(slide_btn_baojing)
            else -> {
                camera_bufang_isuse(slide_btn)
                init_status() //初始化状态
            }
        }
    }

    private fun show_new_devices_powers() {
        when (panelItem_map.get("type").toString()) { // toString(), type = "18"
            "AD11", "AD12", "AD13" -> {
                loop@ for (i in deviceList.indices) {
                    if (progress_zigbee_curtain(i)) break@loop
                }
            }
            "ADA1", "ADA2", "ADA3" -> {
                loop@ for (i in deviceList.indices) {
                    when (deviceList[i].type) {
                        "111", "112" -> {
                            if (basic_power == null) return
                            basic_power!!.visibility = View.VISIBLE
                            power_view!!.visibility = View.VISIBLE
                            break@loop
                        }
                    }
                }
            }
        }
    }

    private fun progress_zigbee_curtain(i: Int): Boolean {
        if (basic_power == null) return false
        if (deviceList.size == 1)
            when (deviceList[i].type) {
                "18" -> {
                    basic_power!!.visibility = View.GONE
                    power_view!!.visibility = View.GONE
                    return true
                }
                else -> {
                    basic_power!!.visibility = View.VISIBLE
                    power_view!!.visibility = View.VISIBLE
                }
            }
        else {
            when (deviceList[i].type) {
                "18" -> {
                    basic_power!!.visibility = View.GONE
                    power_view!!.visibility = View.GONE
                }
                else -> {
                    basic_power!!.visibility = View.VISIBLE
                    power_view!!.visibility = View.VISIBLE
                    return true
                }
            }
        }
        return false
    }

    /**
     * 通用版获取 wifi 设备详情
     */
    private fun sraum_getWifiDeviceCommon() {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        mapdevice["areaNumber"] = areaNumber
        mapdevice["number"] = panelNumber

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getWifiDeviceCommon, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getWifiDeviceCommon()
        }, this@MyDeviceItemActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@MyDeviceItemActivity, "更新失败")
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun fourCode() {}
            override fun threeCode() {
                ToastUtil.showToast(this@MyDeviceItemActivity, "number错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@MyDeviceItemActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                mac_txt!!.text = user.mac
                //  mac_wifi = user.mac
                wifi_map["mac"] = user.mac
                wifi_map["wifi"] = user.wifi
                wifi_map["type"] = user.type

                wifi_map["custom1"] = user.custom1
                wifi_map["custom2"] = user.custom2
                wifi_map["custom3"] = user.custom3
                wifi_map["custom4"] = user.custom4
                wifi_map["custom5"] = user.custom5
                when (if (user.status == null) "" else user.status) {
                    "0" -> status_txt!!.text = "离线"
                    else -> status_txt!!.text = "在线"
                }
            }
        })
    }


    // var mac_wifi:String? = null


    /**
     * 新增网关同步时间
     */
    private fun sraum_setGatewayTime() {
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["boxNumber"] = panelNumber
        map["areaNumber"] = areaNumber
        if (dialogUtil != null) dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_setGatewayTime, map as Map<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            sraum_setGatewayTime()
        }, this@MyDeviceItemActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                ToastUtil.showToast(this@MyDeviceItemActivity, "同步时间成功")
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@MyDeviceItemActivity, "areaNumber 错 误")
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@MyDeviceItemActivity, "网关编号不正确")
            }
        })
    }

    /**
     * 获取网关版本号
     */
    private fun getgateway_version(type: String) {
        //在这里先调
        //设置网关模式-sraum-setBox
        when (type) {
            "网关", "A088" -> {//type = A088网关
                upgrade_wangguan(type)
            }
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> {
                if (wifi_map != null)
                    compare_new_current_version(wifi_map["custom2"] as String?, wifi_map["custom4"] as String?, type)
            }
        }
    }


    private fun upgrade_wangguan(type: String) {
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["number"] = panelItem_map!!["number"].toString()
        map["areaNumber"] = areaNumber
        //        map.put("phoneId", phoned);
        //        map.put("status", "0");//进入设置模式
        //        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayUpdate, map as Map<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            //
            getgateway_version(panelItem_map.get("type").toString())
        }, this@MyDeviceItemActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                compare_new_current_version(user.currentVersion, user.newVersion, type)
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@MyDeviceItemActivity, "该网关不存在")
            }
        })
    }

    private fun compare_new_current_version(currentVersion_user: String?, newVersion_user: String?, type: String?) {
        currentVersion = currentVersion_user
        newVersion = newVersion_user
        if (newVersion != null) {
            // ToastUtil.showToast(this@MyDeviceItemActivity, "type:" + gateway_map["type"])
            when (type) {
                "网关", "A088" -> {
                    upgrade_gateway(type)
                }
                "A2A1",
                "A2A2",
                "A2A3",
                "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> {
                    upgrade_wifi_light(type)
                }
            }
        }
    }

    private fun upgrade_wifi_light(type: String?) {
        if (Integer.valueOf(newVersion!!) != Integer.valueOf(currentVersion!!)) {//A002 不判断最低版本，A001则判断最低版本
            //int d = Integer.valueOf("ff",16);   //d=255
            updatebox_version(newVersion!!, currentVersion, "doit", type)
        } else {
            updatebox_version(newVersion!!, currentVersion, "scuess", type)
        }
    }

    private fun upgrade_gateway(type: String?) {
        if (gateway_map != null && gateway_map["type"] != null)
            when (gateway_map["type"] as String) {
                "A088" -> {//A088
                    if (gateway_map["firmware"] == null) return
                    if (newVersion == null) return
                    if (Integer.parseInt(newVersion!!) > Integer.parseInt(gateway_map["firmware"].toString())) {//A002 不判断最低版本，A001则判断最低版本
                        //int d = Integer.valueOf("ff",16);   //d=255
//                        var stringBuffer = StringBuffer()
//                        stringBuffer.append("当前版本是: " + gateway_map["firmware"] + "\n")
//                        stringBuffer.append("最新版本是: " + newVersion + "\n")
//                        stringBuffer.append("请到A088网关屏上进行版本更新:" + "\n")
//                        stringBuffer.append("第一步: 请点击主页左上角设置图标" + "\n")
//                        stringBuffer.append("第二步: 点击右下角设置按钮进入设置页" + "\n")
//                        stringBuffer.append("第三步: 点击检查更新进行版本更新操作")
//
//                        showCenterDeleteDialog(stringBuffer.toString(), "")
                        updatebox_version(newVersion!!, currentVersion, "doit", gateway_map["type"].toString())
                    } else {
                        // ToastUtil.showToast(this@MyDeviceItemActivity, "当前固件版本为最新版本")
                        updatebox_version(newVersion!!, currentVersion, "scuess", gateway_map["type"].toString())
                    }
                }
                "A002" -> {
                    jurge_gujian_version(type)
                }
                "A001" -> {
                    if (Integer.valueOf(newVersion!!, 16) >= Integer.valueOf("56", 16)) {//A002 不判断最低版本，A001则判断最低版本
                        //int d = Integer.valueOf("ff",16);   //d=255
                        jurge_gujian_version(type)
                    } else {
                        ToastUtil.showToast(this@MyDeviceItemActivity, "固件版本过低不支持" +
                                "更新")
                    }
                }
                else -> {

                }
            }
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name1: String?, name2: String?) { //请在A088屏上操作版本升级：
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@MyDeviceItemActivity).inflate(R.layout.promat_new_version_dialog, null)

        val tv_title: TextView
        val name_gloud: TextView
        val confirm_rel: RelativeLayout
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        confirm_rel = view.findViewById(R.id.confirm_rel) as RelativeLayout
        name_gloud.text = name1
        tv_title.text = name2
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        dialog1 = Dialog(this@MyDeviceItemActivity, R.style.BottomDialog)
        dialog1!!.setContentView(view)
        dialog1!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog1!!.setCancelable(true) //设置它可以取消
        dialog1!!.setCanceledOnTouchOutside(false)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog1!!.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        p.height = (displayWidth * 0.8).toInt()
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog1!!.window!!.attributes = p //设置生效
        dialog1!!.show()
        confirm_rel.setOnClickListener {
            dialog1!!.dismiss()
        }
    }


    private fun jurge_gujian_version(type: String?) {
        if (Integer.valueOf(newVersion!!, 16) != Integer.valueOf(currentVersion!!, 16)) {
            updatebox_version(newVersion!!, currentVersion, "doit", type)
        } else {
            //                                    ToastUtil.showToast(DetailActivity.this, "网关版本已最新");
            //                                    is_index = false;
            updatebox_version(newVersion!!, currentVersion, "scuess", type)
            //停止添加网关
        }
    }

    /**
     * 网关版本更新
     *
     * @param newVersion
     * @param currentVersion
     * @param doit
     */
    private fun updatebox_version(newVersion: String, currentVersion: String?, doit: String, type: String?) {
        val intent = Intent(this@MyDeviceItemActivity,
                GuJianWangGuanNewActivity::class.java)
        intent.putExtra("newVersion", newVersion)
        intent.putExtra("currentVersion", currentVersion)
        intent.putExtra("doit", doit)
        intent.putExtra("type", type)
        intent.putExtra("areaNumber", areaNumber)
        intent.putExtra("number", panelItem_map!!["number"].toString())
        startActivity(intent)
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(panelNumber: String, name: String?,
                               type: String) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@MyDeviceItemActivity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        tv_title.text = name
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@MyDeviceItemActivity, R.style.BottomDialog)
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
        cancel.setOnClickListener { sraum_deletepanel(panelNumber, type, dialog) }
        confirm.setOnClickListener { dialog.dismiss() }
    }

    /**
     * 删除设备
     *
     * @param
     * @param dialog
     */
    private fun sraum_deletepanel(number: String, type: String,
                                  dialog: Dialog) {
        if (dialogUtil != null) dialogUtil!!.loadDialog()
        val mapdevice: MutableMap<String, String?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(MyDeviceItemActivity.this,"areaNumber","");
        mapdevice["token"] = TokenUtil.getToken(this@MyDeviceItemActivity)
        mapdevice["areaNumber"] = areaNumber
        var send_method: String? = ""
        when (type) {
            "AA02" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiApple
            }
            "AA03", "AA04", "AA05" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiCamera
            }
            "网关", "A088" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteGateway
            }
            "AD02",
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiDeviceCommon
            }
            else -> {
                mapdevice["gatewayNumber"] = boxNumber
                mapdevice["deviceNumber"] = number
                send_method = ApiHelper.sraum_deleteDevice
            }
        }

        MyOkHttp.postMapString(send_method, mapdevice,
                object : Mycallback(AddTogglenInterfacer { sraum_deletepanel(panelNumber, type, dialog) }, this@MyDeviceItemActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        dialog.dismiss()
                        finish()
                        when (type) {
                            "AA02" -> {
                                SharedPreferencesUtil.saveInfo_List(this@MyDeviceItemActivity, "remoteairlist",
                                        ArrayList())
                                SharedPreferencesUtil.saveData(this@MyDeviceItemActivity, "mGizWifiDevice", "")
                            }

                            "AA03", "AA04" -> {//删除摄像头成功后
                                //ToastUtil.showToast(this@MyDeviceItemActivity, number)
                                over_camera_list(number)
                            }
                        }
                    }

                    override fun fourCode() {
                        dialog.dismiss()
                        ToastUtil.showToast(this@MyDeviceItemActivity, "删除失败")
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }


    /**
     * 清除wifi摄像头列表
     */
    private fun over_camera_list(did: String?) {

//        for (i in list_wifi_camera.indices) {
//            if (list_wifi_camera[i]["did"] == did) {
//                list_wifi_camera[i]["tag"] = tag
//                is_has = true
//                break
//            }
//        }
        val list = SharedPreferencesUtil.getInfo_List(this@MyDeviceItemActivity, "list_wifi_camera_first")
        for (i in list.indices) {
            if (i == list.size) break
            if (list[i]["did"] == did) {
                list.removeAt(i)
            }
        }

        SharedPreferencesUtil.saveInfo_List(this@MyDeviceItemActivity, "list_wifi_camera_first", list)

        Log.e("list_camera", "list_wifi_camera_first:" + JSON.toJSONString(list))

        var list_second_total = SharedPreferencesUtil.getInfo_List(this@MyDeviceItemActivity, "list_second_total")
        for (i in list_second_total.indices) {
            if (i == list_second_total.size) break
            if (list_second_total[i]["did"] == did) {
                list_second_total.removeAt(i)
            }
        }


        Log.e("list_camera", "list_second_total:" + JSON.toJSONString(list_second_total))
        SharedPreferencesUtil.saveInfo_List(this@MyDeviceItemActivity, "list_second_total", list_second_total)
    }

    override fun onResume() {
        super.onResume()
        when (if (panelItem_map!!["type"] == null) "" else panelItem_map!!["type"].toString()) {
            "AD02", "A2A1", "A2A2", "A2A3", "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> sraum_getWifiDeviceCommon()
        }
        Thread(
                Runnable {
                    when (if (panelItem_map!!["type"] == null) "" else panelItem_map!!["type"].toString()) {
                        "AD02", "A2A1", "A2A2", "A2A3", "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2", "ADB3" -> getData(false, "wifi")
                        "网关", "A088" -> {
                            sraum_getGatewayInfo(panelItem_map!!["number"].toString())
                        }
                        else -> getData(false, "devices")
                    }
                }
        ).start()
    }


    /**
     * 获取网关基本信息（APP->网关）
     */
    private fun sraum_getGatewayInfo(number: String) {
        val map = java.util.HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["number"] = number
        map["areaNumber"] = areaNumber
        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayInfo, map as Map<String, Any>,
                object : Mycallback(AddTogglenInterfacer { }, this@MyDeviceItemActivity, null) {
                    override fun onSuccess(user: User) {
                        //"roomNumber":"1"
                        gateway_map = java.util.HashMap<Any?, Any?>()
                        gateway_map["type"] = user.type
                        gateway_map["status"] = user.status
                        gateway_map["name"] = user.name
                        gateway_map["mac"] = user.mac
                        gateway_map["versionType"] = user.versionType
                        gateway_map["hardware"] = user.hardware
                        gateway_map["firmware"] = user.firmware
                        gateway_map["bootloader"] = user.bootloader
                        gateway_map["coordinator"] = user.coordinator
                        gateway_map["panid"] = user.panid
                        gateway_map["channel"] = user.channel
                        gateway_map["deviceCount"] = user.deviceCount
                        gateway_map["sceneCount"] = user.sceneCount

                        //显示
//                        mac!!.text = user.mac
//                        type!!.text = user.type
//                        version!!.text = user.hardware
//                        pannel!!.text = user.channel
//                        pan_id!!.text = user.panid
                    }
                })
    }

    private fun setAlerm() {
//        if (successFlag) {
        Log.e("setAlerm", "setAlermTemp: " + alermBean!!.alarm_temp)
        NativeCaller.PPPPAlarmSetting(strDID, alermBean!!.alarm_audio,
                alermBean!!.motion_armed,
                alermBean!!.motion_sensitivity,
                alermBean!!.input_armed, alermBean!!.ioin_level,
                alermBean!!.ioout_level, alermBean!!.iolinkage,
                alermBean!!.alermpresetsit, alermBean!!.mail,
                alermBean!!.snapshot, alermBean!!.record,
                alermBean!!.upload_interval,
                alermBean!!.schedule_enable,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -0x1, -0x1, -0x1,
                -0x1, -0x1, -1)
        //        } else {
//            showToast(R.string.alerm_set_failed);
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEvent(event: MyEvent) {
        val status = event.msg
        when (status) {
            "1" -> {
                view_bufang_baojing!!.visibility = View.VISIBLE
                rel_bufang_baojing!!.visibility = View.VISIBLE
                rel_bufang_plan!!.visibility = View.VISIBLE
                view_bufang_plan!!.visibility = View.VISIBLE
                rel_sd_set!!.visibility = View.VISIBLE
                view_sd_set!!.visibility = View.VISIBLE
                view_sdcard_remotevideo!!.visibility = View.VISIBLE
                rel_sdcard_remotevideo!!.visibility = View.VISIBLE
            }
            "0" -> {
                view_bufang_baojing!!.visibility = View.GONE
                rel_bufang_baojing!!.visibility = View.GONE
                rel_bufang_plan!!.visibility = View.GONE
                view_bufang_plan!!.visibility = View.GONE
                rel_sd_set!!.visibility = View.GONE
                view_sd_set!!.visibility = View.GONE
                view_sdcard_remotevideo!!.visibility = View.GONE
                rel_sdcard_remotevideo!!.visibility = View.GONE
            }
        }
        when (status) {
            "0" -> status_txt!!.text = "离线"
            else -> status_txt!!.text = "在线"
        }
        if (dialogUtil != null) dialogUtil!!.removeDialog()
        //        ToastUtil.showToast(MyDeviceItemActivity.this, "status:" + status);
    }

    override fun callBackAlarmParams(did: String, alarm_audio: Int, motion_armed: Int,
                                     motion_sensitivity: Int, input_armed: Int, ioin_level: Int,
                                     iolinkage: Int, ioout_level: Int, alarmpresetsit: Int, mail: Int,
                                     snapshot: Int, record: Int, upload_interval: Int,
                                     schedule_enable: Int, schedule_sun_0: Int, schedule_sun_1: Int,
                                     schedule_sun_2: Int, schedule_mon_0: Int, schedule_mon_1: Int,
                                     schedule_mon_2: Int, schedule_tue_0: Int, schedule_tue_1: Int,
                                     schedule_tue_2: Int, schedule_wed_0: Int, schedule_wed_1: Int,
                                     schedule_wed_2: Int, schedule_thu_0: Int, schedule_thu_1: Int,
                                     schedule_thu_2: Int, schedule_fri_0: Int, schedule_fri_1: Int,
                                     schedule_fri_2: Int, schedule_sat_0: Int, schedule_sat_1: Int,
                                     schedule_sat_2: Int) {
        alermBean!!.did = did
        alermBean!!.motion_armed = motion_armed
        alermBean!!.motion_sensitivity = motion_sensitivity
        alermBean!!.input_armed = input_armed
        alermBean!!.ioin_level = ioin_level
        alermBean!!.iolinkage = iolinkage
        alermBean!!.ioout_level = ioout_level
        alermBean!!.alermpresetsit = alarmpresetsit
        alermBean!!.mail = mail
        alermBean!!.snapshot = snapshot
        alermBean!!.record = record
        alermBean!!.upload_interval = upload_interval
        alermBean!!.alarm_audio = alarm_audio
        alermBean!!.alarm_temp = input_armed
        alermBean!!.schedule_enable = schedule_enable
        mHandler.sendEmptyMessage(ALERMPARAMS) //这个跳转到这个界面后的获取的
    }

    override fun callBackSetSystemParamsResult(did: String, paramType: Int, result: Int) { //这个是报警开关后的直接，设置后的直接回调
        mHandler.sendEmptyMessage(result)
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> ToastUtil.showToast(this@MyDeviceItemActivity, "报警设置失败")
                1 -> {
                }
                ALERMPARAMS -> {
                    //                    if (dialogUtil != null) dialogUtil.removeDialog();
//                    isfrom = "ALERMPARAMS";
//                    if (isFirst) {
//                        isFirst = false;
//                    }
//                    if (0 == alermBean.getMotion_armed()) {//首次读取报警设置
//                        slide_btn_baojing.setChecked(false);
//                    } else {
//                        slide_btn_baojing.setChecked(true);
//                    }
                    if (0 == alermBean!!.input_armed) {
                    } else {
                    }
                    if (0 == alermBean!!.ioin_level) {
                    } else {
                    }
                    if (0 == alermBean!!.alarm_audio) {
                    } else {
                        if (1 == alermBean!!.alarm_audio) {
                        } else if (2 == alermBean!!.alarm_audio) {
                        } else if (3 == alermBean!!.alarm_audio) {
                        }
                    }
                    if (0 == alermBean!!.alarm_temp) {
                    } else {
                        if (1 == alermBean!!.alarm_temp) {
                        } else if (2 == alermBean!!.alarm_temp) {
                        } else if (3 == alermBean!!.alarm_temp) {
                        }
                    }
                    if (0 == alermBean!!.iolinkage) {
                    } else {
                    }
                    if (0 == alermBean!!.ioout_level) {
                    } else {
                    }
                    if (alermBean!!.alermpresetsit == 0) {
                    } else {
                    }
                    if (1 == alermBean!!.motion_armed || 1 == alermBean!!.input_armed || alermBean!!.alarm_audio != 0) {
                    } else {

                    }
                }
                else -> {

                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun openState(isChecked: Boolean, view: View, from: Boolean) {
        when (view.id) {
            R.id.slide_btn -> {
                if (from) return
                if (isChecked) {
                    sensor_set_protection("1", panelItem_map!!["type"].toString())
                } else {
                    sensor_set_protection("0", panelItem_map!!["type"].toString())
                }
            }
            R.id.slide_btn_baojing -> {
                //
//                switch (isfrom) {
//                    case "ALERMPARAMS"://回调的
//                        isfrom = "";
//
//                        break;
//                    default://主动控制的,去响应
//                        if (isChecked) {
////                    sensor_set_protection("1");
//                            alermBean.setMotion_armed(1);
//                        } else {
////                    sensor_set_protection("0");
//                            alermBean.setMotion_armed(0);
//                        }
//                        setAlerm();
//                        break;
//                }
                if (isChecked) {
//                    sensor_set_protection("1");
                    alermBean!!.motion_armed = 1
                    sraum_setWifiCameraIsUse("1")
                } else {
//                    sensor_set_protection("0");
                    alermBean!!.motion_armed = 0
                    sraum_setWifiCameraIsUse("0")
                }
                setAlerm()
            }
            R.id.slide_btn_plan -> if (isChecked) {
//                    sensor_set_protection("1");
            } else {
//                    sensor_set_protection("0");
            }
        }
    }

    companion object {
        const val MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE = "com.sraum.massky.from.mydevice"
        private const val STR_DID = "did"
        private const val STR_MSG_PARAM = "msgparam"
    }
}