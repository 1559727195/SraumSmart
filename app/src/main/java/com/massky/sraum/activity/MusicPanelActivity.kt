package com.massky.sraum.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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

import com.massky.sraum.view.VerticalSeekBarNew
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import java.util.*


/**
 * Created by zhu on 2018/1/30.
 */

class MusicPanelActivity : BaseActivity() {
    //    @BindView(R.id.bar1)
//    private var bar1: ColorArcAirControlProgressBar_New? = null
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
//    @BindView(R.id.volumeView)
//    private var volumeView: VolumeView_New? = null

    //    @BindView(R.id.moshi_rel)
//    private var moshi_rel: RelativeLayout? = null
//    @BindView(R.id.fengsu_rel)
//    private var fengsu_rel: RelativeLayout? = null
//    @BindView(R.id.openbtn_tiao_guang)
//    private var openbtn_tiao_guang: ImageView? = null
    private var speed_txt: String? = null
    private var mode_txt: String? = null

    private var vibflag: Boolean = false
    private var musicflag: Boolean = false
    private var boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private var statusflag: String? = null
    private var dimmer: String? = null
    private var modeflag: String? = null
    private var temperature: String? = null
    private var windflag: String? = null
    private var loginPhone: String? = null
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

    //music_seek_bar
    @JvmField
    @BindView(R.id.music_seek_bar)
    var music_seek_bar: VerticalSeekBarNew? = null

    //root_layout
    @JvmField
    @BindView(R.id.root_layout)
    var root_layout: LinearLayout? = null


    //loop_rel
    @JvmField
    @BindView(R.id.loop_rel)
    var loop_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pre_rel)
    var pre_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.begin_rel)
    var begin_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.next_rel)
    var next_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.volume_rel)
    var volume_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.loop_img)
    var loop_img: ImageView? = null

    @JvmField
    @BindView(R.id.pre_img)
    var pre_img: ImageView? = null

    @JvmField
    @BindView(R.id.begin_img)
    var begin_img: ImageView? = null

    @JvmField
    @BindView(R.id.next_img)
    var next_img: ImageView? = null

    //btn_mode_blue_local
    @JvmField
    @BindView(R.id.btn_mode_blue_local)
    var btn_mode_blue_local: ImageView? = null


    override fun viewId(): Int {
        return R.layout.music_panel_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        registerMessageReceiver()
        loginPhone = SharedPreferencesUtil.getData(this@MusicPanelActivity, "loginPhone", "") as String
        val preferences = getSharedPreferences("sraum" + loginPhone!!,
                Context.MODE_PRIVATE)
        vibflag = preferences.getBoolean("vibflag", false)
        musicflag = preferences.getBoolean("musicflag", false)
        dialogUtil = DialogUtil(this@MusicPanelActivity)
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@MusicPanelActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name1 = bundle.getString("name1")
        name2 = bundle.getString("name2")
        name = bundle.getString("name")
        statusflag = bundle.getString("status")
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber")//当前房间编号
        //boxNumber
        boxnumber = bundle.getString("boxNumber")
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>
        if (mapalldevice != null) {
            type = mapalldevice!!["type"] as String?
            modeflag = mapalldevice!!["mode"] as String?
            windflag = mapalldevice!!["speed"] as String?
            temperature = mapalldevice!!["temperature"] as String?
            dimmer = mapalldevice!!["dimmer"] as String?
        }
        if (name != null) project_select!!.text = name

        //播放状态
        result_status()
        result_volume()
        //循环模式
        result_loop()
        result_mode()
    }

    private fun result_mode() {
        when (modeflag) {
            "0" -> {//本地
                btn_mode_blue_local!!.setImageResource(R.drawable.btn_local)//icon_cl_close
            }
            "1" -> {//蓝牙
                btn_mode_blue_local!!.setImageResource(R.drawable.btn_bluetooth)//icon_cl_close
            }
        }
    }

    private fun result_loop() {
        when (temperature) {
            "1" -> {//全部循环
                loop_img!!.setImageResource(R.drawable.btn_loop)//icon_cl_close
            }
            "2" -> {//单曲循环
                loop_img!!.setImageResource(R.drawable.btn_single)//icon_cl_close
            }
            "3" -> {//随机循环
                loop_img!!.setImageResource(R.drawable.btn_random)//icon_cl_close
            }
        }
    }

    private fun result_volume() {
        music_seek_bar!!.setProgress(Integer.parseInt(dimmer!!))
    }

    private fun result_status() {
        when (statusflag) {
            "1" -> {//播放
                begin_img!!.setImageResource(R.drawable.btn_stop_music)//icon_cl_close
            }
            "2" -> {//暂停
                begin_img!!.setImageResource(R.drawable.btn_begin)//icon_cl_close
            }
            "3" -> {//停止
                begin_img!!.setImageResource(R.drawable.btn_begin)//icon_cl_close
            }
        }
    }


    //设置上一首
    private fun setPre(): String {
        var status: String? = null
        //模式状态
        status = "17"
        return status!!
    }

    //设置下一首
    private fun setNext(): String {
        var status: String? = null
        //模式状态
        status = "18"
        return status!!
    }


    //设置音量
    private fun setVolume(progress: Int): String {
        var status: String? = null
        var integers = 128 + progress
        if (integers < 128) {
            integers = 128
        }
        if (integers > 158) {
            integers = 158
        }
        //模式状态
        status = "$integers"
        return status!!
    }


    //设置循环模式
    private fun setLoop(): String {
        var status: String? = null
        status = temperature
        //模式状态
        when (status) {
            "1" -> {
                status = "34"
            }
            "2" -> {
                status = "35"
            }
            "3" -> {
                status = "33"
            }
        }
        return status!!
    }


    //设置播放状态
    private fun setBegin(): String {
        var local_status: String? = null
        local_status = statusflag
        //模式状态

        when (local_status) {
            "1" -> {
                local_status = "3"
            }
            "2" -> {
                local_status = "1"
            }
            "3" -> {
                local_status = "1"
            }
        }
        return local_status!!
    }


    //设置Mode
    private fun setMode(): String {
        var local_model: String? = null
        local_model = modeflag
        //模式状态
        when (local_model) {
            "0" -> {//蓝牙
                local_model = "50"
                showCenterDeleteDialog("温馨提示", "已进入蓝牙播放模式，若没播放音乐，请完成以下操作！\n第一步，打开蓝牙并连接BT201-AUDIO设备配对。\n第二步，打开音乐APP。")
            }
            "1" -> {
                local_model = "49"
            }
        }
        return local_model!!
    }


    private val onSeekBarChangeListener: VerticalSeekBarNew.OnSeekBarChangeListener = object : VerticalSeekBarNew.OnSeekBarChangeListener {
        override fun onProgressChanged(VerticalSeekBar: VerticalSeekBarNew?, progress: Int,
                                       fromUser: Boolean) {
            Log.e("vcc", "now  $progress")
        }

        override fun onStartTrackingTouch(VerticalSeekBar: VerticalSeekBarNew) {
            Log.e("vcc", "before  " + VerticalSeekBar.getProgress())
        }

        override fun onStopTrackingTouch(VerticalSeekBar: VerticalSeekBarNew) {
            Log.e("vcc", "after  "
                    + VerticalSeekBar.getProgress())
            //  setVolume(VerticalSeekBar.getProgress())
            getMapdevice(setVolume(VerticalSeekBar.getProgress()), "volume")
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
        map["token"] = TokenUtil.getToken(this@MusicPanelActivity)
        map["areaNumber"] = areaNumber!!
        map["boxNumber"] = gateway_number
        map["panelNumber"] = panelid
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                object : Mycallback(AddTogglenInterfacer { getPanel_devices(panelid, gateway_number) }, this@MusicPanelActivity, dialogUtil) {

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
                                    //  doit_open(d.status)
                                    //不为窗帘开关状态
                                    dimmer = d.dimmer
                                    modeflag = d.mode
                                    temperature = d.temperature
                                    windflag = d.speed
                                    statusflag = d.status
                                    //播放状态
                                    result_status()
                                    result_volume()
                                    //循环模式
                                    result_loop()
                                    result_mode()
                                }
                                break
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun wrongBoxnumber() {
                        ToastUtil.showToast(this@MusicPanelActivity, "该网关不存在")
                    }
                }
        )
    }


    //控制设备
    private fun getMapdevice(status: String, action: String) {
        val mapdevice = HashMap<String, Any>()
        mapdevice["type"] = type!!
        mapdevice["status"] = status!!
        mapdevice["number"] = number!!
        mapdevice["name"] = name!!
        mapdevice["dimmer"] = dimmer!!
        mapdevice["mode"] = modeflag!!
        mapdevice["temperature"] = temperature!!
        mapdevice["speed"] = windflag!!
        sraum_device_control(mapdevice, action)
    }

    private fun sraum_device_control(mapdevice1: Map<String, Any>, action: String) {
        val mapalldevice = HashMap<String, Any>()
        val listobj = ArrayList<Map<*, *>>()
        val map = HashMap<String, Any>()
        map.put("type", mapdevice1["type"]!!.toString())
        map.put("number", mapdevice1["number"]!!.toString())
        map.put("name", mapdevice1["name"]!!.toString())
        map.put("status", mapdevice1["status"]!!.toString())
//        when (doit) {
//            "onclick" -> map.put("status", statusflag!!)
//            else -> if (statusbo) {
//                map.put("status", "1")
//            } else {
//                map.put("status", "0")
//            }
//        }

        map.put("mode", mapdevice1["mode"]!!.toString())
        map.put("dimmer", mapdevice1["dimmer"]!!.toString())
        map.put("temperature", mapdevice1["temperature"]!!.toString())
        map.put("speed", mapdevice1["speed"]!!.toString())
        listobj.add(map)
        mapalldevice["token"] = TokenUtil.getToken(this@MusicPanelActivity)
        mapalldevice["areaNumber"] = areaNumber!!
        mapalldevice["deviceInfo"] = listobj
        val status = map.get("status") as String
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice, object : Mycallback(AddTogglenInterfacer {

        }, this@MusicPanelActivity, dialogUtil) {

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@MusicPanelActivity, "操作失败")
            }

            override fun defaultCode() {
                ToastUtil.showToast(this@MusicPanelActivity, "操作失败")
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@MusicPanelActivity, "操作失败")
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(this@MusicPanelActivity, "控制失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (action) {
                    "loop" -> {
                        when (temperature) {
                            "1" -> {
                                temperature = "2"
                            }
                            "2" -> {
                                temperature = "3"
                            }
                            "3" -> {
                                temperature = "1"
                            }
                        }
                        result_loop()
                    }

                    "begin" -> {
                        when (statusflag) {
                            "1" -> {
                                statusflag = "2"
                            }
                            "2" -> {
                                statusflag = "1"
                            }
                            "3" -> {
                                statusflag = "1"
                            }
                        }
                        result_status()
                    }
                    "mode" -> {
                        when (modeflag) {
                            "0" -> {
                                modeflag = "1"
                                getMapdevice("1", "begin")
                            }
                            "1" -> {
                                modeflag = "0"
                            }
                        }
                        result_mode()
                    }
                }
            }
        })
    }


    override fun onEvent() {
        back!!.setOnClickListener(this)
        loop_rel!!.setOnClickListener(this)
        root_layout!!.setOnClickListener(this)
        // pre_rel!!.setOnClickListener(this)
        // pre_img!!.addClickScale("pre")
        begin_rel!!.setOnClickListener(this)
        //next_rel!!.setOnClickListener(this)
        //next_img!!.addClickScale("next")
        next_click_img()
        pre_click_img()
        volume_rel!!.setOnClickListener(this)
        loop_img!!.setOnClickListener(this)
        // pre_img!!.setOnClickListener(this)
        begin_img!!.setOnClickListener(this)
        // next_img!!.setOnClickListener(this)
        music_seek_bar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
        btn_mode_blue_local!!.setOnClickListener(this)
    }

    private fun next_click_img() {
        next_img!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("TAG", "onEvent: down")
                    next_img!!.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).start()
                    getMapdevice(setNext(), "next")
                }

                MotionEvent.ACTION_UP -> {
                    Log.e("TAG", "onEvent: up")
                    next_img!!.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
            }
            true
        }
    }

    private fun pre_click_img() {
        pre_img!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.e("TAG", "onEvent: down")
                    pre_img!!.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).start()
                    getMapdevice(setPre(), "pre")
                }

                MotionEvent.ACTION_UP -> {
                    Log.e("TAG", "onEvent: up")
                    pre_img!!.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
            }
            true
        }
    }


    override fun onData() {
        init_Data()
    }


    /**
     * 添加点击缩放效果
     */
    fun View.addClickScale(action: String, scale: Float = 0.9f, duration: Long = 150) {
        this.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when (action) {
                        "pre" -> {
                            pre_img!!.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
                            getMapdevice(setPre(), "pre")
                        }
                        "next" -> {
                            next_img!!.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
                            getMapdevice(setNext(), "next")
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    when (action) {
                        "pre" -> {
                            pre_img!!.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                        }
                        "next" -> {
                            next_img!!.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                        }
                    }
                }
            }
            // 点击事件处理，交给View自身
            this.onTouchEvent(event)
        }
    }

    var volume_show: Boolean? = false
    override fun onClick(v: View) {//
        when (v.id) {
            R.id.back -> this@MusicPanelActivity.finish()
            R.id.volume_rel -> //声音调节{
            {
                if (music_seek_bar!!.visibility == View.VISIBLE) {
                    music_seek_bar!!.visibility = View.GONE
                } else {
                    music_seek_bar!!.visibility = View.VISIBLE
                }
            }
            R.id.loop_img -> {
                getMapdevice(setLoop(), "loop")
            }
            R.id.pre_img -> {
                getMapdevice(setPre(), "pre")
            }
            R.id.begin_img -> {
                getMapdevice(setBegin(), "begin")
            }
            R.id.next_img -> {
                getMapdevice(setNext(), "next")
            }
            R.id.root_layout -> {
                music_seek_bar!!.visibility = View.GONE
            }
            R.id.btn_mode_blue_local -> {//蓝牙和本地切换
                getMapdevice(setMode(), "mode")
            }
        }
    }


    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(title: String?,
                               content: String?) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@MusicPanelActivity).inflate(R.layout.promat_dialog_music, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val tv_promat: TextView
        val name_gloud: TextView
        val rel_confirm: RelativeLayout
        val common_linear: LinearLayout
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        tv_promat = view.findViewById<View>(R.id.tv_promat) as TextView
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        rel_confirm = view.findViewById<View>(R.id.rel_confirm) as RelativeLayout
        common_linear = view.findViewById<View>(R.id.common_linear) as LinearLayout
        tv_promat.text = title
        tv_title.text = content
        name_gloud.visibility = View.GONE
        rel_confirm.visibility = View.VISIBLE
        common_linear.visibility = View.GONE
        tv_promat.textSize = 18.0f
        tv_promat.typeface = Typeface.DEFAULT_BOLD


        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@MusicPanelActivity, R.style.BottomDialog)
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
        cancel.setOnClickListener { }
        confirm.setOnClickListener { dialog.dismiss() }
        rel_confirm.setOnClickListener { dialog.dismiss() }
    }


    private var mMessageReceiver: MessageReceiver? = null

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(ACTION_MUSICCONTROL_RECEIVER)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == ACTION_MUSICCONTROL_RECEIVER) {
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

    override fun onResume() {
        super.onResume()
        //getPanel_devices(panelid: String, gateway_number: String)
//        if(number != null && boxnumber != null)
//         getPanel_devices(number!!,boxnumber!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }

    companion object {
        var ACTION_MUSICCONTROL_RECEIVER = "com.massky.music.control.receiver"
    }
}
