package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragment
import com.yanzhenjie.statusview.StatusUtils
import com.zanelove.aircontrolprogressbar.ColorArcProgressBar
import java.util.*

/**
 * Created by zhu on 2018/1/30.
 */
class TiaoGuangLightActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.id_seekBar)
    var id_seekBar: SeekBar? = null

    //project_select
    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null


    @JvmField
    @BindView(R.id.openbtn_tiao_guang)
    var openbtn_tiao_guang: ImageView? = null
    private val progress_now: String? = null
    private val status: String? = null
    private var number: String? = null
    private var type: String? = null
    private var mMessageReceiver: MessageReceiver? = null
    private var loginPhone: String? = null
    private var vibflag = false
    private var musicflag = false
    private var boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private var statusflag: String? = null
    private var dimmer: String? = null
    private var modeflag: String? = null
    private var temperature: String? = null
    private var windflag: String? = null

    @JvmField
    @BindView(R.id.bar1)
    var bar1: ColorArcProgressBar? = null
    private val addflag = false
    private val mapflag = false
    private var statusbo = false
    private var name1: String? = null
    private var name2: String? = null
    private var name: String? = null
    private var areaNumber: String? = null
    private var roomNumber: String? = null
    private var mapalldevice: Map<String, Any>? = HashMap()
    override fun viewId(): Int {
        return R.layout.tiaoguanglight_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        registerMessageReceiver()
        loginPhone = SharedPreferencesUtil.getData(this@TiaoGuangLightActivity, "loginPhone", "") as String
        val preferences = getSharedPreferences("sraum$loginPhone",
                Context.MODE_PRIVATE)
        boxnumber = SharedPreferencesUtil.getData(this@TiaoGuangLightActivity, "boxnumber", "") as String
        dialogUtil = DialogUtil(this@TiaoGuangLightActivity)
        vibflag = preferences.getBoolean("vibflag", false)
        musicflag = preferences.getBoolean("musicflag", false)
        LogUtil.i("查看值状态$musicflag")
        init_Data()
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@TiaoGuangLightActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name1 = bundle.getString("name1")
        name2 = bundle.getString("name2")
        name = bundle.getString("name")
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber") //当前房间编号
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>?
        if (mapalldevice != null) {
            statusflag = bundle.getString("status")
            dimmer = mapalldevice!!["dimmer"] as String?
            try {
                if (dimmer!!.toInt() == 0) {
                    bar1!!.setCurrentValues((dimmer!!.toInt() + 10).toFloat())
                    id_seekBar!!.progress = dimmer!!.toInt() + 10
                } else {
                    bar1!!.setCurrentValues(dimmer!!.toInt().toFloat())
                    id_seekBar!!.progress = dimmer!!.toInt()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            doit_open()
        }
        if(name != null)
            project_select!!.text = name
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE)
        registerReceiver(mMessageReceiver, filter)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        Log.e("zhu", "onProgressChanged: $progress")
        bar1!!.setCurrentValues((progress + 10).toFloat())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        LogUtil.i("开始滑动", "onStartTrackingTouch: ")
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        LogUtil.i("停止滑动", "onStopTrackingTouch: ")
        //停止滑动是的状态
        if (statusbo) {
//            statusflag = "1";
            getMapdevice("slop") //控制
        }
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE) {
                Log.e("zhu", "LamplightActivity:" + "LamplightActivity")
                //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                upload()
            }
        }
    }

    //下载设备信息并且比较状态（为了显示开关状态）
    private fun upload() {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["areaNumber"] = areaNumber
        mapdevice["roomNumber"] = roomNumber
        mapdevice["token"] = TokenUtil.getToken(this@TiaoGuangLightActivity)
        dialogUtil!!.loadDialog()
        SharedPreferencesUtil.saveData(this@TiaoGuangLightActivity, "boxnumber", boxnumber)
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //获取togglen成功后重新刷新数据
            upload()
        }, this@TiaoGuangLightActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                var list: MutableList<Map<*, *>> = ArrayList()
                get_devices_list(user, list)
                if(list.size == 0) return
                //拿到设备状态值
                for (d in list) {
                    if (d["number"] == number) {
                        //匹配状值设置当前状态
                        if (d["status"] != null) {
                            //进行判断是否为窗帘
                            statusflag = d["status"] as String?
                            //不为窗帘开关状态
                            dimmer = d["dimmer"] as String?
                            Log.e("zhu", "d.dimmer:$dimmer")
                            if (type == "2" || type.equals("112")) {
                                if (dimmer != null && dimmer != "") {
                                    bar1!!.setCurrentValues(dimmer!!.toInt().toFloat())
                                    id_seekBar!!.progress = dimmer!!.toInt()
                                    doit_open()
                                }
                            }
                            statusbo = if (d["status"] == "0") {
                                false
                            } else {
                                true
                            }
                        }
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun get_devices_list(user: User, list: MutableList<Map<*, *>>) {
        for (i in user.deviceList.indices) {
            val map = HashMap<Any?, Any?>()
            map["name"] = if (user.deviceList[i].name == null) "" else user.deviceList[i].name
            map["number"] = user.deviceList[i].number
            map["type"] = user.deviceList[i].type
            map["status"] = if (user.deviceList[i].status == null) "" else user.deviceList[i].status
            map["mode"] = user.deviceList[i].mode
            map["dimmer"] = user.deviceList[i].dimmer
            map["temperature"] = user.deviceList[i].temperature
            map["speed"] = user.deviceList[i].speed
            map["boxNumber"] = if (user.deviceList[i].boxNumber == null) "" else user.deviceList[i].boxNumber
            //                            map.put("boxNumber", user.deviceList.get(i).boxNumber);
    //                            map.put("boxName", user.deviceList.get(i).boxName);
            //name1
            //name2
            //flag
            map["name1"] = user.deviceList[i].name1
            map["name2"] = user.deviceList[i].name2
            //                            map.put("flag", user.deviceList.get(i).flag);
            map["panelName"] = user.deviceList[i].panelName
            map["panelMac"] = user.deviceList[i].panelMac
            //                            map.put("deviceId", "");
            map["mac"] = ""
            map["deviceId"] = ""
            list!!.add(map as Map<String, Any>)
        }
        if (user.wifiList != null) {
            for (i in user.wifiList.indices) {
                val map = HashMap<Any?, Any?>()
                map["name"] = user.wifiList[i].name
                map["number"] = user.wifiList[i].number
                map["type"] = user.wifiList[i].type
                map["status"] = user.wifiList[i].status
                map["mode"] = user.wifiList[i].mode
                map["dimmer"] = user.wifiList[i].dimmer
                map["temperature"] = user.wifiList[i].temperature
                map["speed"] = user.wifiList[i].speed
                map["boxNumber"] = ""
                map["boxName"] = ""
                map["mac"] = user.wifiList[i].mac
                map["name1"] = ""
                map["name2"] = ""
                map["flag"] = ""
                map["panelName"] = ""
                map["deviceId"] = user.wifiList[i].deviceId
                map["panelMac"] = user.wifiList[i].panelMac
                map["boxNumber"] = ""
                list.add(map as Map<String, Any>)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        id_seekBar!!.setOnSeekBarChangeListener(this)
        id_seekBar!!.setOnTouchListener { v, event ->

            //这个是根据网关状态在线情况，不在线的话，seekBar就不能滑动了，
            Log.e("zhu", "id_seekBar->:$statusbo")
            !statusbo
        }
        //调光控制
        openbtn_tiao_guang!!.setOnClickListener(this)
    }

    //控制设备
    private fun getMapdevice(slop: String) {
        val mapalldevice: Map<String, Any> = HashMap()
        val listob: List<Map<String, Any>> = ArrayList()
        val mapdevice: MutableMap<String, Any?> = HashMap()
        when (type) {
            "2","112" -> {
                dimmer = id_seekBar!!.progress.toString() + ""
                dimmer = removeTrim(dimmer!!)
                val item = dimmer!!.toFloat()
                dimmer = "" + Math.round(item)
                run {
                    modeflag = ""
                    temperature = modeflag
                }
                windflag = ""
            }
        }
        mapdevice["type"] = type
        when (slop) {
            "onclick" -> {
                mapdevice["status"] = statusflag
                mapdevice["dimmer"] = dimmer
            }
            else -> {
                if (statusbo) {
                    mapdevice["status"] = "1"
                } else {
                    mapdevice["status"] = "0"
                }
                mapdevice["dimmer"] = dimmer!!.toInt() + 10
            }
        }
        mapdevice["number"] = number
        mapdevice["name"] = name
        mapdevice["mode"] = modeflag
        mapdevice["temperature"] = temperature
        mapdevice["speed"] = windflag
        sraum_device_control(mapdevice, slop)
    }

    fun removeTrim(str: String): String {
        var str = str
        if (str.indexOf(".") > 0) {
            str = str.replace("0+?$".toRegex(), "") //去掉多余的0
            str = str.replace("[.]$".toRegex(), "") //如最后一位是.则去掉
        }
        return str
    }

    private fun sraum_device_control(mapdevice1: Map<String, Any?>, slop: String) {
        val mapalldevice: MutableMap<String, Any?> = HashMap()
        val listobj: MutableList<Map<*, *>> = ArrayList()
        val map = HashMap<Any?, Any?>()
        var api = ""
        map["type"] = mapdevice1["type"].toString()
        map["number"] = mapdevice1["number"].toString()
        map["name"] = mapdevice1["name"].toString()
        map["status"] = mapdevice1["status"].toString()
        map["mode"] = mapdevice1["mode"].toString()
        map["dimmer"] = mapdevice1["dimmer"].toString()
        map["temperature"] = mapdevice1["temperature"].toString()
        map["speed"] = mapdevice1["speed"].toString()
        listobj.add(map)
        mapalldevice["token"] = TokenUtil.getToken(this@TiaoGuangLightActivity)
        mapalldevice["areaNumber"] = areaNumber
        when (type) {
            "112" -> {
                api = ApiHelper.sraum_controlWifiButton
//                listobj.add(map)
                mapalldevice["deviceInfo"] = map
            }
            else -> {
                api = ApiHelper.sraum_deviceControl
                listobj.add(map)
                mapalldevice["deviceInfo"] = listobj
            }
        }
        MyOkHttp.postMapObject(api, mapalldevice, object : Mycallback(AddTogglenInterfacer { sraum_device_control(mapdevice1, slop) }, this@TiaoGuangLightActivity, dialogUtil) {
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@TiaoGuangLightActivity, "操作失败")
            }

            override fun defaultCode() {
                ToastUtil.showToast(this@TiaoGuangLightActivity, "操作失败")
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@TiaoGuangLightActivity, "操作失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (slop) {
                    "onclick" -> doit_open()
                }
                if (vibflag) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    MusicUtil.startMusic(this@TiaoGuangLightActivity, 1, "")
                } else {
                    MusicUtil.stopMusic(this@TiaoGuangLightActivity, "")
                }
            }
        })
    }

    private fun doit_open() {
        if (statusflag == "1") {
            openbtn_tiao_guang!!.setImageResource(R.drawable.icon_cl_open_active)
            statusflag = "0"
            statusbo = true
        } else {
            //调光灯开关状态
            openbtn_tiao_guang!!.setImageResource(R.drawable.icon_cl_close)
            statusflag = "1"
            statusbo = false
        }
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.openbtn_tiao_guang -> getMapdevice("onclick")
        }
    }
}