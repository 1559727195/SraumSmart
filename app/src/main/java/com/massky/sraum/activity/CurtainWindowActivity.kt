package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragment
import com.massky.sraum.view.RangeSeekBar
import com.yanzhenjie.statusview.StatusUtils
import java.math.BigDecimal
import java.util.*

/**
 * Created by zhu on 2018/1/30.
 */
class CurtainWindowActivity : BaseActivity() {
    //
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    private var status: String? = null
    private var number: String? = null
    private var type: String? = null
    private var name: String? = null
    private var name1: String? = null
    private var name2: String? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.name1_txt)
    var name1_txt: TextView? = null

    @JvmField
    @BindView(R.id.name2_txt)
    var name2_txt: TextView? = null

    //one_key_txt
    @JvmField
    @BindView(R.id.one_key_txt)
    var one_key_txt: TextView? = null

    @JvmField
    @BindView(R.id.radio_group_out)
    var radio_group_out: LinearLayout? = null

    @JvmField
    @BindView(R.id.radio_group_in)
    var radio_group_in: LinearLayout? = null

    //open_close_linear
    @JvmField
    @BindView(R.id.open_close_linear)
    var open_close_linear: LinearLayout? = null

    //ranger
    @JvmField
    @BindView(R.id.ranger)
    var ranger: RangeSeekBar? = null


    @JvmField
    @BindView(R.id.radio_group_all)
    var radio_group_all: LinearLayout? = null
    private val loginPhone: String? = null
    private val vibflag = false
    private val musicflag = false
    private val boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private var statusflag: String? = null
    private var flagone: String? = null
    private var flagtwo: String? = null
    private var flagthree: String? = null
    private val whriteone = true
    private val whritetwo = true
    private val whritethree = true
    private var curtain: String? = null
    private var dimmer: String? = null
    private val modeflag: String? = null
    private val temperature: String? = null
    private val windflag: String? = null
    var statusm: String? = null
    private var mMessageReceiver: MessageReceiver? = null
    private val mapflag = false
    private val statusbo = false
    private var areaNumber: String? = null
    private var roomNumber: String? = null
    private var mapalldevice: Map<String, Any>? = HashMap()
    override fun viewId(): Int {
        return R.layout.curtain_window_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        dialogUtil = DialogUtil(this)
        //        init_receiver_control();
        registerMessageReceiver()
        init_event()
        init_range_event()
    }

    private var range: String? = null

    private fun init_range_event() {
        ranger!!.setOnRangeChangedListener { view, min, max, isFromUser ->
            if (isFromUser) {
                val text2Draw: String = getRangeValue(min)!!
                Log.e("range", "onRangeChanged: min :$text2Draw,max:$max") //提交后台
                range = text2Draw
            }
        }
    }

    private fun getRangeValue(min: Float): String? {
        val b = BigDecimal(min.toDouble())
        val f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
        val content = (f1.toString() + "").split("\\.".toRegex()).toTypedArray()
        var text2Draw = "10"
        text2Draw = if (content[1].toInt() >= 5) {
            //   text2Draw = content[0] + ".5";
            ((content[0].toInt() + 1) * 10).toString() + ""
        } else {
            (content[0].toInt() * 10).toString() + ""
        }
        if (text2Draw != null) {
            if (text2Draw == "0") text2Draw = "10"
        }
        return text2Draw
    }

    private fun init_type() {
        when (type) {
            "18", "113" -> {
                one_key_txt!!.visibility = View.GONE
                name1_txt!!.visibility = View.GONE
                name2_txt!!.visibility = View.GONE
                radio_group_out!!.visibility = View.GONE
                radio_group_in!!.visibility = View.GONE
            }
        }
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
        mapdevice["token"] = TokenUtil.getToken(this@CurtainWindowActivity)
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //获取togglen成功后重新刷新数据
            upload()
        }, this@CurtainWindowActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                //拿到设备状态值
                var list: MutableList<Map<*, *>> = ArrayList()
                get_devices_list(user, list)
                if (list.size == 0) return
                for (d in list) {
                    if (d["number"] == number) {
                        //匹配状值设置当前状态
                        if (d["status"] != null) {
                            //进行判断是否为窗帘
                            statusflag = d["status"] as String?
                            LogUtil.eLength("下载数据", statusflag)
                            change_status_toui(type, d["status"] as String?)
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

    /**
     * 根据status去切换UI显示
     */
    private fun change_status_toui(type: String?, status: String?) {
        if (type == "4" || type == "18" || type.equals("113")) {
            when (status) {
                "0" -> {
                    flagone = "2" // 2 -全关，1- 开 ， 3 -
                    flagtwo = "2"
                    flagthree = "2"
                    //                    radio_group_out.getChildCount();
                    common_select("全关")
                }
                "1" -> {
                    flagone = "1"
                    flagtwo = "1"
                    flagthree = "1"
                    common_select("全开")
                }
                "2" -> {
                    flagone = "1"
                    flagtwo = "1"
                    flagthree = "1"
                    common_select("暂停")
                }
                "3" -> {
                    flagone = "1"
                    flagtwo = "3"
                    flagthree = "0"
                    common_select("组1开组2关")
                }
                "4" -> {
                    flagone = "1"
                    flagtwo = "2"
                    flagthree = "0"
                    common_select("组1开组2暂停")
                }
                "5" -> {
                    flagone = "3"
                    flagtwo = "1"
                    flagthree = "0"
                    common_select("组1关组2开")
                }
                "6" -> {
                    flagone = "3"
                    flagtwo = "2"
                    flagthree = "0"
                    common_select("组1关组2暂停")
                }
                "7" -> {
                    flagone = "2"
                    flagtwo = "3"
                    flagthree = "0"
                    common_select("组1暂停组2关")
                }
                "8" -> {
                    flagone = "2"
                    flagtwo = "1"
                    flagthree = "0"
                    common_select("组1暂停组2开")
                }
            }
        }
    }

    /**
     * 根据状态码status，显示相应UI
     */
    private fun common_select(item: String) {
        common()
        when (item) {
            "全开" -> {
                ((radio_group_out!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
                ((radio_group_in!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
                ((radio_group_all!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
            }
            "全关" -> {
                ((radio_group_out!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
                ((radio_group_in!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
                ((radio_group_all!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
            }
            "暂停" -> {
                ((radio_group_out!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
                ((radio_group_in!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
                ((radio_group_all!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
            }
            "组1开组2关" -> {
                ((radio_group_out!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
                ((radio_group_in!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
            }
            "组1开组2暂停" -> {
                ((radio_group_out!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
                ((radio_group_in!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
            }
            "组1关组2开" -> {
                ((radio_group_out!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
                ((radio_group_in!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
            }
            "组1关组2暂停" -> {
                ((radio_group_out!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
                ((radio_group_in!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
            }
            "组1暂停组2关" -> {
                ((radio_group_out!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
                ((radio_group_in!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close_active)
            }
            "组1暂停组2开" -> {
                ((radio_group_out!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting_active)
                ((radio_group_in!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open_active)
            }
        }
    }

    private fun common() {
        ((radio_group_out!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open)
        ((radio_group_out!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting)
        ((radio_group_out!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close)
        ((radio_group_in!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open)
        ((radio_group_in!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting)
        ((radio_group_in!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close)
        ((radio_group_all!!.getChildAt(0) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_open)
        ((radio_group_all!!.getChildAt(1) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_zanting)
        ((radio_group_all!!.getChildAt(2) as RelativeLayout).getChildAt(0) as ImageView).setImageResource(R.drawable.icon_cl_close)
    }

    private fun statusClear() {
        if (whriteone) {
        }
        if (whritetwo) {
        }
        if (whritethree) {
        }
    }

    /**
     * 初始化监听事件
     */
    private fun init_event() {
        for (i in 0 until radio_group_out!!.childCount) { //外纱
            val child = radio_group_out!!.getChildAt(i) as RelativeLayout
            child.tag = i
            child.setOnClickListener {
                when (child.tag as Int) {
                    0 -> curtain = "1"
                    1 -> curtain = "2"
                    2 -> curtain = "3"
                }
                mapdevice
            }
        }
        for (i in 0 until radio_group_in!!.childCount) { //内纱
            val child = radio_group_in!!.getChildAt(i) as RelativeLayout
            child.tag = i
            child.setOnClickListener {
                when (child.tag as Int) {
                    0 -> curtain = "4"
                    1 -> curtain = "5"
                    2 -> curtain = "6"
                }
                mapdevice
            }
        }
        for (i in 0 until radio_group_all!!.childCount) { //全部
            val child = radio_group_all!!.getChildAt(i) as RelativeLayout
            child.tag = i
            child.setOnClickListener {
                when (child.tag as Int) {
                    0 -> curtain = "7"
                    1 -> curtain = "8"
                    2 -> curtain = "9"
                }
                mapdevice
            }
        }
    }//看窗帘2//看窗帘2//两个特别全开全关设置

    //控制设备
    private val mapdevice: Unit
        private get() {
            //两个特别全开全关设置
            if (type == "4" || type == "18" || type.equals("113")) {
                statusm = ""
                when (curtain) {
                    "1" ->                     //看窗帘2
                        statusm = out_click(statusm!!, flagtwo, "1", "4", "3")
                    "2" ->                     //看窗帘2
                        statusm = out_click(statusm!!, flagtwo, "8", "2", "7")
                    "3" -> statusm = out_click(statusm!!, flagtwo, "5", "6", "0")
                    "4" -> statusm = out_click(statusm!!, flagone, "1", "8", "5")
                    "5" -> statusm = out_click(statusm!!, flagone, "4", "2", "6")
                    "6" -> statusm = out_click(statusm!!, flagone, "3", "7", "0")
                    "7" -> statusm = "1"
                    "8" -> statusm = "2"
                    "9" -> statusm = "0"
                    else -> {
                    }
                }
                sraum_device_control()
            }
        }

    /***
     * 结合外纱和内纱控制窗帘开或者关，暂停
     * @param statusm
     * @param flagtwo
     * @param s
     * @param s2
     * @param s3
     * @return
     */
    private fun out_click(statusm: String, flagtwo: String?, s: String, s2: String, s3: String): String {
        var statusm = statusm
        when (flagtwo) {
            "1" -> statusm = s
            "2" -> statusm = s2
            "3" -> statusm = s3
        }
        return statusm
    }

    private fun sraum_device_control() {
        var api = ""
        val mapalldevice1: MutableMap<String, Any?> = HashMap()
        val listobj: MutableList<Map<*, *>> = ArrayList()
        val map = HashMap<Any?, Any?>()
        map["type"] = mapalldevice!!["type"].toString()
        map["number"] = mapalldevice!!["number"].toString()
        map["name"] = mapalldevice!!["name"].toString()
        map["status"] = statusm
        map["mode"] = mapalldevice!!["mode"].toString()
        map["dimmer"] = mapalldevice!!["dimmer"].toString()
        if(curtainHas != null) {
            when(curtainHas) {
                "1"-> {
                    map["dimmer"] = if(range == null) "10" else range
                }
            }
        }
        map["temperature"] = mapalldevice!!["temperature"].toString()
        map["speed"] = mapalldevice!!["speed"].toString()
      //  listobj.add(map)
        mapalldevice1["token"] = TokenUtil.getToken(this@CurtainWindowActivity)
        mapalldevice1["areaNumber"] = areaNumber
        when (type) {
            "113" -> {
                api = ApiHelper.sraum_controlWifiButton
                listobj.add(map)
                mapalldevice1["deviceInfo"] = map
            }
            else -> {
                api = ApiHelper.sraum_deviceControl
                listobj.add(map)
                mapalldevice1["deviceInfo"] = listobj
            }
        }
        MyOkHttp.postMapObject(api, mapalldevice1, object : Mycallback(AddTogglenInterfacer { sraum_device_control() }, this@CurtainWindowActivity, dialogUtil) {
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@CurtainWindowActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(this@CurtainWindowActivity, "控制失败")
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@CurtainWindowActivity, "deviceInfo 不正确")
            }

            override fun defaultCode() {
                ToastUtil.showToast(this@CurtainWindowActivity, "操作失败")
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@CurtainWindowActivity, "操作失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                change_status_toui(type, statusm)
                if (vibflag) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    MusicUtil.startMusic(this@CurtainWindowActivity, 1, "")
                } else {
                    MusicUtil.stopMusic(this@CurtainWindowActivity, "")
                }
            }
        })
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onData() {
        init_Data()
        init_type()
    }

    override fun onResume() {
        super.onResume()
        when (type) {
            "18" -> {
                sraum_getCurtainInfo()
            }
        }
    }

    private fun sraum_getCurtainInfo() {
        var api = ""
        val map_check: MutableMap<String, Any?> = HashMap()
        map_check["deviceNumber"] = mapalldevice!!["number"].toString()
        map_check["token"] = TokenUtil.getToken(this@CurtainWindowActivity)
        map_check["areaNumber"] = areaNumber
        api = ApiHelper.sraum_getCurtainInfo

        MyOkHttp.postMapObject(api, map_check, object : Mycallback(AddTogglenInterfacer { sraum_device_control() }, this@CurtainWindowActivity, dialogUtil) {
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@CurtainWindowActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun fourCode() {
                super.fourCode()
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@CurtainWindowActivity, "103 设备编号错误")
            }

            override fun defaultCode() {

            }

            override fun pullDataError() {
                ToastUtil.showToast(this@CurtainWindowActivity, "操作失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                process_curtainHas(user)
            }
        })
    }

    var curtainHas: String? = null
    private fun process_curtainHas(user: User) {
        if (user.curtainHas == null) return
       this.curtainHas = user.curtainHas
        if (ranger == null) return
        when (user.curtainHas) {
            "1" -> {
                if (dimmer != null) {
                    if (dimmer.equals("0")) {
                        ranger!!.setValue(10)
                        range = "10"
                    } else {
                        ranger!!.setValue(Integer.parseInt(dimmer!!))
                    }
                } else {
                    ranger!!.setValue(10)
                    range = "10"
                }
                open_close_linear!!.visibility = View.VISIBLE
            }
            else -> {
                open_close_linear!!.visibility = View.GONE
            }
        }
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@CurtainWindowActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name1 = bundle.getString("name1")
        name2 = bundle.getString("name2")
        name = bundle.getString("name")
        status = bundle.getString("status")
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber") //当前房间编号
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>?
        if (mapalldevice != null) {
            type = mapalldevice!!["type"] as String?
            dimmer = if(mapalldevice!!["dimmer"] == null) null
            else mapalldevice!!["dimmer"] as String
            //初始化窗帘参数
            change_status_toui(type, status)
        }
        if(name != null)
            project_select!!.text = name
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }
}