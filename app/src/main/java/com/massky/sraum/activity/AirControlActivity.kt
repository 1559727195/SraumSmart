package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.DialogUtil
import com.massky.sraum.Util.IntentUtil
import com.massky.sraum.Util.LogUtil
import com.massky.sraum.Util.MusicUtil
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.VolumeView_New
import com.yanzhenjie.statusview.StatusUtils
import com.zanelove.aircontrolprogressbar.ColorArcAirControlProgressBar_New
import java.util.ArrayList
import java.util.HashMap
import okhttp3.Call


/**
 * Created by zhu on 2018/1/30.
 */

class AirControlActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.bar1)
     var bar1: ColorArcAirControlProgressBar_New? = null

    @JvmField
    @BindView(R.id.back)
     var back: ImageView? = null

    private var status: String? = null
    private var number: String? = null
    private var type: String? = null
    private var name: String? = null

    @JvmField
    @BindView(R.id.project_select)
     var project_select: TextView? = null
    private var mode: String? = null
    private var speed: String? = null

    @JvmField
    @BindView(R.id.volumeView)
     var volumeView: VolumeView_New? = null

    @JvmField
    @BindView(R.id.moshi_rel)
     var moshi_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.fengsu_rel)
     var fengsu_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.openbtn_tiao_guang)
     var openbtn_tiao_guang: ImageView? = null
    private var speed_txt: String? = null
    private var mode_txt: String? = null

    private var vibflag: Boolean = false
    private var musicflag: Boolean = false
    private val boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private var statusflag: String? = null
    private var dimmer: String? = null
    private var modeflag: String? = null
    private var temperature: String? = null
    private var windflag: String? = null
    private var loginPhone: String? = null
    private var mMessageReceiver: MessageReceiver? = null
    private val mapflag: Boolean = false
    private var addflag: Boolean = false
    private var statusbo: Boolean = false
    private var name1: String? = null
    private var name2: String? = null
    private var areaNumber: String? = null
    private var roomNumber: String? = null
    private var mapalldevice: Map<String, Any>? = HashMap()
    private val gateway_number: String? = null
    private val deviceList = ArrayList<User.device>()
    private val panelList = ArrayList<User.panellist>()

    override fun viewId(): Int {
        return R.layout.air_control_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        registerMessageReceiver()
        loginPhone = SharedPreferencesUtil.getData(this@AirControlActivity, "loginPhone", "") as String
        val preferences = getSharedPreferences("sraum" + loginPhone!!,
                Context.MODE_PRIVATE)
        vibflag = preferences.getBoolean("vibflag", false)
        musicflag = preferences.getBoolean("musicflag", false)
        dialogUtil = DialogUtil(this@AirControlActivity)
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@AirControlActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name1 = bundle.getString("name1")
        name2 = bundle.getString("name2")
        name = bundle.getString("name")
        statusflag = bundle.getString("status")
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber")//当前房间编号
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>
        if (mapalldevice != null) {
            type = mapalldevice!!["type"] as String?
            modeflag = mapalldevice!!["mode"] as String?
            windflag = mapalldevice!!["speed"] as String?
            temperature = mapalldevice!!["temperature"] as String?
            dimmer = mapalldevice!!["dimmer"] as String?
            when (type) {
                "3"//空调
                -> {
                    moshi_rel!!.visibility = View.VISIBLE
                    fengsu_rel!!.visibility = View.VISIBLE
                    project_select!!.text = "空调"
                }
                "5"//新风
                -> {
                    project_select!!.text = "新风"
                    moshi_rel!!.visibility = View.GONE
                    fengsu_rel!!.visibility = View.VISIBLE
                }
                "6"//地暖
                -> {
                    project_select!!.text = "地暖"
                    fengsu_rel!!.visibility = View.GONE
                    moshi_rel!!.visibility = View.VISIBLE
                }
            }

            if (type == "3" || type == "5" || type == "6") {
                //初始化窗帘参数
                //空调
                //判断展示值是否加16
                addflag = false
                bar1!!.setCurrentValues(Integer.parseInt(temperature!!).toFloat(), 0f)
                volumeView!!.set_temperature(Integer.parseInt(temperature!!))
                setModetwo()
                setSpeed()
                doit_open(statusflag!!)
            }
            if (name != null)
                project_select!!.text = name
        }
    }


    //下载设备信息并且比较状态（为了显示开关状态）
    private fun upload() {
        val mapdevice = HashMap<String, String>()
        mapdevice["token"] = TokenUtil.getToken(this@AirControlActivity)
        mapdevice["boxNumber"] = boxnumber!!
        dialogUtil!!.loadDialog()
        // SharedPreferencesUtil.saveData(AirControlActivity.this, "boxnumber", boxnumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getAllDevice, mapdevice, object : Mycallback(AddTogglenInterfacer {
            //获取togglen成功后重新刷新数据
            upload()
        }, this@AirControlActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                //拿到设备状态值
                for (d in user.deviceList) {
                    if (d.number == number) {
                        //匹配状值设置当前状态
                        if (d.status != null) {
                            //进行判断是否为窗帘
                            statusflag = d.status
                            LogUtil.eLength("下载数据", statusflag)

                            //不为窗帘开关状态
                            dimmer = d.dimmer
                            Log.e("zhu", "d.dimmer:" + dimmer!!)
                            modeflag = d.mode
                            temperature = d.temperature
                            windflag = d.speed
                            if (type == "3" || type == "5" || type == "6") {
                                if (temperature != null && temperature != "") {
                                    //                                        tempimage_id.setText(temperature);
                                    bar1!!.setCurrentValues(Integer.parseInt(temperature!!).toFloat(), 0f)
                                    //                                        id_seekBar.setProgress(Integer.parseInt(temperature) - 16);
                                    volumeView!!.set_temperature(Integer.parseInt(temperature!!))
                                }
                            }
                            setModetwo()
                            setSpeed()
                            LogUtil.eLength("查看是否进去", temperature + "进入方法" + dimmer)
                        }
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }


    private fun setModetwo() {
        var strmode = ""
        when (type) {
            "3"//空调
            -> strmode = get_air_modetwo(strmode)
            "6"//地暖
            -> strmode = get_dinuan_modetwo(strmode)
        }
        //        tempstate_id.setText(strmode);
        bar1!!.setMode(strmode)
    }

    private fun get_air_modetwo(strmode: String): String {
        var strmode = strmode
        when (modeflag) {
            "1" -> strmode = "制冷"
            "2" -> strmode = "制热"
            "3" -> strmode = "除湿"
            "4" -> strmode = "自动"
            "5" -> strmode = "通风"
            else -> {

            }
        }
        return strmode
    }

    private fun get_dinuan_modetwo(strmode: String): String {
        var strmode = strmode
        when (modeflag) {
            "1" -> strmode = "加热"
            "2" -> strmode = "睡眠"
            "3" -> strmode = "外出"
            else -> {

            }
        }
        return strmode
    }


    private fun setSpeed() {
        var strwind = ""
        when (windflag) {
            "1" -> strwind = "低风"
            "2" -> strwind = "中风"
            "3" -> strwind = "高风"
            "4" -> strwind = "强力"
            "5" -> strwind = "送风"
            "6" -> strwind = "自动"
            else -> {
            }
        }
        //        windspeedtwo_id.setText(strwind);
        //        windspeed_id.setText(strwind);
        if (type == "6") {
            bar1!!.setSpeed("")
        } else {
            bar1!!.setSpeed(strwind)
        }
    }


    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(ACTION_AIRCONTROL_RECEIVER)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == ACTION_AIRCONTROL_RECEIVER) {
                Log.e("zhu", "LamplightActivity:" + "LamplightActivity")
                //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                val panelid = intent.getStringExtra("panelid")
                val gateway_number = intent.getStringExtra("gatewayid")
                //在这个界面时，type=1.找面板下的设备列表。在编辑面板的界面。找面板按钮，找到面板后，
                getPanel_devices(panelid!!, gateway_number!!)
                // upload();
            }
        }
    }


    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    private fun getPanel_devices(panelid: String, gateway_number: String) {
        val map = HashMap<String, Any>()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(FastEditPanelActivity.this, "areaNumber", "");
        map["token"] = TokenUtil.getToken(this@AirControlActivity)
        map["areaNumber"] = areaNumber!!
        map["boxNumber"] = gateway_number
        map["panelNumber"] = panelid
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                object : Mycallback(AddTogglenInterfacer { getPanel_devices(panelid, gateway_number) }, this@AirControlActivity, dialogUtil) {

                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        //拿到设备状态值
                        for (d in user.deviceList) {
                            if (d.number == number) {
                                //匹配状值设置当前状态
                                if (d.status != null) {
                                    //进行判断是否为窗帘
                                    //statusbo = d.status;
                                    doit_open(d.status)
                                    LogUtil.eLength("下载数据", statusflag)

                                    //不为窗帘开关状态
                                    dimmer = d.dimmer
                                    Log.e("zhu", "d.dimmer:" + dimmer!!)
                                    modeflag = d.mode
                                    temperature = d.temperature
                                    windflag = d.speed
                                    if (type == "3" || type == "5" || type == "6") {
                                        if (temperature != null && temperature != "") {
                                            //                                        tempimage_id.setText(temperature);
                                            bar1!!.setCurrentValues(Integer.parseInt(temperature!!).toFloat(), 0f)
                                            //                                        id_seekBar.setProgress(Integer.parseInt(temperature) - 16);
                                            volumeView!!.set_temperature(Integer.parseInt(temperature!!))
                                        }
                                    }
                                    setModetwo()
                                    setSpeed()
                                    LogUtil.eLength("查看是否进去", temperature + "进入方法" + dimmer)
                                }
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun wrongBoxnumber() {
                        ToastUtil.showToast(this@AirControlActivity, "该网关不存在")
                    }
                }
        )
    }


    //控制设备
    private fun getMapdevice(doit: String) {
        val mapdevice = HashMap<String, Any>()
        mapdevice["type"] = type!!
        mapdevice["status"] = statusflag!!
        mapdevice["number"] = number!!
        mapdevice["name"] = name!!
        mapdevice["dimmer"] = dimmer!!
        mapdevice["mode"] = modeflag!!
        mapdevice["temperature"] = temperature!!
        mapdevice["speed"] = windflag!!
        sraum_device_control(mapdevice, doit)
    }

    private fun sraum_device_control(mapdevice1: Map<String, Any>, doit: String) {
        val mapalldevice = HashMap<String, Any>()
        val listobj = ArrayList<Map<*, *>>()
        val map = HashMap<String, Any>()
        map.put("type", mapdevice1["type"]!!.toString())
        map.put("number", mapdevice1["number"]!!.toString())
        map.put("name", mapdevice1["name"]!!.toString())
        when (doit) {
            "onclick" -> map.put("status", statusflag!!)
            else -> if (statusbo) {
                map.put("status", "1")
            } else {
                map.put("status", "0")
            }
        }


        map.put("mode", mapdevice1["mode"]!!.toString())
        map.put("dimmer", mapdevice1["dimmer"]!!.toString())
        map.put("temperature", mapdevice1["temperature"]!!.toString())
        map.put("speed", mapdevice1["speed"]!!.toString())
        listobj.add(map)
        mapalldevice["token"] = TokenUtil.getToken(this@AirControlActivity)
        mapalldevice["areaNumber"] = areaNumber!!
        mapalldevice["deviceInfo"] = listobj
        val status = map.get("status") as String
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice, object : Mycallback(AddTogglenInterfacer {
            val map = HashMap<String, Any>()
            map["token"] = TokenUtil.getToken(this@AirControlActivity)
            map["boxNumber"] = boxnumber!!
            sraum_device_control(map, doit)
        }, this@AirControlActivity, dialogUtil) {

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@AirControlActivity, "操作失败")
            }

            override fun defaultCode() {
                ToastUtil.showToast(this@AirControlActivity, "操作失败")
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@AirControlActivity, "操作失败")
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(this@AirControlActivity, "控制失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (doit) {
                    "onclick" -> doit_open(status)
                }
                if (vibflag) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    MusicUtil.startMusic(this@AirControlActivity, 1, "")
                } else {
                    MusicUtil.stopMusic(this@AirControlActivity, "")
                }
            }
        })
    }

    private fun doit_open(status: String) {
        if (status == "1") {
            openbtn_tiao_guang!!.setImageResource(R.drawable.btn_kt_open)//icon_cl_close
            statusflag = "0"
            statusbo = true
            volumeView!!.setVolumeCliable(true)
        } else {
            //调光灯开关状态
            openbtn_tiao_guang!!.setImageResource(R.drawable.btn_kt_close)
            statusflag = "1"
            statusbo = false
            volumeView!!.setVolumeCliable(false)
        }
    }

    /**
     * 初始化值
     */
    private fun init_value() {
        init_speed()
        init_mode()
        bar1!!.setSpeed(speed_txt)
        bar1!!.setMode(mode)
        //volumeView
        volumeView!!.set_temperature(Integer.parseInt(temperature!!))
    }

    private fun init_mode() {
        when (type) {
            "3"//空调
            -> air_mode()
            "6"//地暖
            -> dinuan_mode()
        }
    }

    private fun air_mode() {
        when (mode) {
            "1" -> mode_txt = "制冷"
            "2" -> mode_txt = "制热"
            "3" -> mode_txt = "除湿"
            "4" -> mode_txt = "自动"
            "5" -> mode_txt = "通风"
        }
    }


    private fun dinuan_mode() {
        when (mode) {
            "1" -> mode_txt = "加热"
            "2" -> mode_txt = "睡眠"
            "3" -> mode_txt = "外出"
        }
    }

    private fun init_speed() {
        when (speed) {
            "1" -> speed_txt = "低风"
            "2" -> speed_txt = "中风"
            "3" -> speed_txt = "高风"
            "4" -> speed_txt = "强力"
            "5" -> speed_txt = "送风"
            "6" -> speed_txt = "自动"
        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        volumeView!!.setOnChangeListener(object : VolumeView_New.OnChangeListener {
            override fun onChange(count: Int, value: Int) {
                //显示空调温度控制，
                //temperature = count + "";
                temperature = value.toString() + ""
                bar1!!.setCurrentValues(count.toFloat(), value.toFloat())
            }

            override fun onChange_up(count: Int, value: Int) {
                //            //显示空调温度控制，
                Log.e("robin debug", "count:$temperature")
                //temperature = count + "";
                getMapdevice("")
            }
        })


//            count, value ->
//            //显示空调温度控制，
//            Log.e("robin debug", "count:$count")
//            //temperature = count + "";
//            temperature = value.toString() + ""
//            bar1!!.setCurrentValues(count.toFloat(), value.toFloat())
//            getMapdevice("")

        openbtn_tiao_guang!!.setOnClickListener(this)
        //控制空调开关
        moshi_rel!!.setOnClickListener(this
        )
        fengsu_rel!!.setOnClickListener(this)
    }


    override fun onData() {
        init_Data()
    }

    /**
     * 获取单个设备或是按钮信息（APP->网关）
     *
     * @param map
     */
    private fun get_single_device_info(map: Map<String, *>) {
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
                object : Mycallback(AddTogglenInterfacer { get_single_device_info(map) }, this@AirControlActivity, null) {
                    override fun onSuccess(user: User) {
                        //       switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
                        //                        String type = (String) map.get("type");
                        //                        map.put("status",user.status);
                        //                        map.put("name",user.name)
                        status = user.status as String
                        mode = user.mode
                        temperature = user.temperature
                        speed = user.speed
                        //                        bar1.setCurrentValues(Integer.parseInt(temperature));
                        ////        bar1.setUnit("你好");//风速
                        ////        bar1.setTitle("hello");//模式
                        //                        bar1.setSpeed(speed);
                        //                        bar1.setMode(mode);
                        //volumeView
                        init_value()
                        //                        volumeView.set_temperature(Integer.parseInt(temperature));
                    }
                })
    }

    /**
     * 空调模式选择
     */
    private fun moshi_kongtiao() {
        if (statusbo) {
            when (type) {
                "3"//空调
                -> setMode()
                "6"//地暖
                -> setMode_DiNuan()
            }
            getMapdevice("")
        }
    }

    //
    private fun setMode() {
        //模式状态
        if (statusbo)
            when (modeflag) {
                "1" -> {
                    //                tempstate_id.setText("制热");
                    bar1!!.setMode("制热")
                    modeflag = "2"
                }
                "2" -> {
                    //                tempstate_id.setText("除湿");
                    bar1!!.setMode("除湿")
                    modeflag = "3"
                }
                "3" -> {
                    //                tempstate_id.setText("自动");
                    bar1!!.setMode("自动")
                    modeflag = "4"
                }
                "4" -> {
                    //                tempstate_id.setText("通风");
                    bar1!!.setMode("通风")
                    modeflag = "5"
                }
                "5" -> {
                    //                tempstate_id.setText("制冷");
                    bar1!!.setMode("制冷")
                    modeflag = "1"
                }
                else -> {
                }
            }
    }


    /**
     * 地暖模式
     */
    private fun setMode_DiNuan() {
        //模式状态
        if (statusbo)
            when (modeflag) {
                "1" -> {
                    //                tempstate_id.setText("制热");
                    bar1!!.setMode("睡眠")
                    modeflag = "2"
                }
                "2" -> {
                    //                tempstate_id.setText("除湿");
                    bar1!!.setMode("外出")
                    modeflag = "3"
                }
                "3" -> {
                    //                tempstate_id.setText("自动");
                    bar1!!.setMode("加热")
                    modeflag = "1"
                }
                else -> {
                }
            }
    }


    /**
     * 空调风速选择
     */
    private fun speed_kongtiao() {
        //风速状态
        if (statusbo) {
            control_wind()
            getMapdevice("")
        }
    }

    /**
     * 控制风速
     */
    private fun control_wind() {
        when (windflag) {
            "1" -> {
                //                    windspeed_id.setText("中风");
                bar1!!.setSpeed("中风")
                windflag = "2"
            }
            "2" -> {
                //                    windspeed_id.setText("高风");
                bar1!!.setSpeed("高风")
                windflag = "3"
            }
            "3" -> {
                bar1!!.setSpeed("强力")
                //                    windspeed_id.setText("强力");
                windflag = "4"
            }
            "4" -> {
                bar1!!.setSpeed("送风")
                //                    windspeed_id.setText("送风");
                windflag = "5"
            }
            "5" -> {
                //                    windspeed_id.setText("自动");
                bar1!!.setSpeed("自动")
                windflag = "6"
            }
            "6" -> {
                //                    windspeed_id.setText("低风");
                bar1!!.setSpeed("低风")
                windflag = "1"
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> this@AirControlActivity.finish()
            R.id.openbtn_tiao_guang//控制空调开关
            -> control_air("onclick")
            R.id.moshi_rel//模式
            -> moshi_kongtiao()
            R.id.fengsu_rel//风速
            -> speed_kongtiao()
        }
    }

    /**
     * 控制空调开关
     */
    private fun control_air(doit: String) {
        getMapdevice(doit)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }

    companion object {

        var ACTION_AIRCONTROL_RECEIVER = "com.massky.air.control.receiver"
    }
}
