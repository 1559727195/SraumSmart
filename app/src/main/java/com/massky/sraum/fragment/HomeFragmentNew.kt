package com.massky.sraum.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.andview.refreshview.XRefreshView
import com.andview.refreshview.XRefreshView.SimpleXRefreshListener
import com.dialog.ToastUtils
import com.example.jpushdemo.ExampleUtil
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode
import com.iflytek.speech.util.SpeechUtil
import com.ipcamera.demo.BridgeService
import com.ipcamera.demo.BridgeService.CallBackMessageInterface
import com.ipcamera.demo.BridgeService.IpcamClientInterface
import com.ipcamera.demo.PlayActivity
import com.ipcamera.demo.utils.ContentCommon
import com.ipcamera.demo.utils.SystemValue
import com.ipcamera.demo.utils.VuidUtils
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.App
import com.massky.sraum.Utils.ParceUtil
import com.massky.sraum.activity.*
import com.massky.sraum.adapter.AreaListAdapter
import com.massky.sraum.adapter.DetailDeviceHomeAdapter
import com.massky.sraum.adapter.HomeDeviceListAdapter
import com.massky.sraum.base.BaseFragment1
import com.massky.sraum.event.MyDialogEvent
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.view.ClearLengthEditText
import com.massky.sraum.view.ListViewAdaptWidth
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yaokan.sdk.ir.YKanHttpListener
import com.yaokan.sdk.ir.YkanIRInterfaceImpl
import com.yaokan.sdk.model.BaseResult
import com.yaokan.sdk.model.RemoteControl
import com.yaokan.sdk.model.YKError
import com.yaokan.sdk.utils.Logger
import com.yaokan.sdk.wifi.DeviceManager
import com.yaokan.sdk.wifi.GizWifiCallBack
import com.ypy.eventbus.EventBus
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Call
import org.json.JSONException
import org.json.JSONObject
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.VerticalTabLayout.OnTabClickListener
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView.*
import q.rorbin.verticaltablayout.widget.TabView
import vstc2.nativecaller.NativeCaller
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by zhu on 2017/11/30.
 */
class HomeFragmentNew : BaseFragment1(), AdapterView.OnItemClickListener, IpcamClientInterface, CallBackMessageInterface, OnTabClickListener {
    private var camera_init_data: Boolean = false
    private var popupWindow: PopupWindow? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.area_name_txt)
    var area_name_txt: TextView? = null

    @JvmField
    @BindView(R.id.dragGridView)
    var mDragGridView: GridView? = null

    @JvmField
    @BindView(R.id.add_device)
    var add_device: ImageView? = null

    //    @BindView(R.id.home_listview)
    //    ListView home_listview;
    @JvmField
    @BindView(R.id.tablayout_vertical)
    var tablayout_vertical: VerticalTabLayout? = null

    @JvmField
    @BindView(R.id.viewpager)
    var viewpager: ViewPager? = null
    private var list_homedev_items = ArrayList<Map<String, Any>>()
    private val homeDeviceListAdapter: HomeDeviceListAdapter? = null
    private var deviceListAdapter: DetailDeviceHomeAdapter? = null
    private var account_view: View? = null
    private var dialogUtil: DialogUtil? = null
    private var common_setting_linear: LinearLayout? = null
    private var rename_scene_linear: LinearLayout? = null
    private var delete_scene_linear: LinearLayout? = null
    private var cancel_scene_linear: LinearLayout? = null
    private var common_setting_image: ImageView? = null

    @JvmField
    @BindView(R.id.back_rel)
    var back_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.refresh_view)
    var refresh_view: XRefreshView? = null
    private val roomsInfos: List<Map<*, *>> = ArrayList()
    private val singleRoomAssoList: List<Map<*, *>> = ArrayList()

    //    @BindView(R.id.dragGridView)
    //    GridView dragGridView;
    @JvmField
    @BindView(R.id.all_room_rel)
    var all_room_rel: RelativeLayout? = null//

    @JvmField
    @BindView(R.id.area_rel)
    var area_rel: RelativeLayout? = null
    private var deivce_number: String? = null
    private var roomNumber: String? = ""
    private val device_type: String? = ""
    private var areaList: MutableList<Map<*, *>> = ArrayList()
    private var current_area_map: Map<*, *> = HashMap<Any?, Any?>() // select the area
    private var roomList: MutableList<Map<*, *>> = ArrayList() //fang jian lie biao
    private val deviceList: List<Map<*, *>> = ArrayList() //单个房间设备列表信息
    private var deviceCount //设备数目
            : String? = null
    private var deivce_type: String? = null
    private var current_room_number: String? = null
    private var listob: MutableList<Map<String, Any>> = ArrayList()
    private val listtype = ArrayList<Any?>()
    private var status: String? = null
    private val mapdevice1: Map<*, *> = HashMap<Any?, Any?>()

    //震动和音乐的判断值
    private var vibflag = false
    private var musicflag = false
    private var loginPhone: String? = null
    var mMessageReceiver: MessageReceiver? = null
    private var areaNumber: String? = null
    private var mapdevice = HashMap<String, Any?>()
    private val manager: WifiManager? = null
    private var option = ContentCommon.INVALID_OPTION
    private val CameraType = ContentCommon.CAMERA_TYPE_MJPEG

    //    List<Map> list_wifi_camera = new ArrayList<>();
    private var playactivityfinsh = true
    private var videofrom = ""
    private var video_item = HashMap<Any?, Any?>() //来自devicefragment
    private var remoteControl: RemoteControl? = null
    private var mGizWifiDevice: GizWifiDevice? = null
    private var list_remotecontrol_air: MutableList<Map<*, *>> = ArrayList()
    private var remoteControl_map_air: Map<*, *> = HashMap<Any?, Any?>()
    private val index_wifi = 0
    private val is_control_air = false
    private var doit_wifi = ""
    private val mGizWifiDevice1: GizWifiDevice? = null
    private val wifi_apple_list: List<Map<*, *>> = ArrayList()
    private val index_click = 0
    private val deviceInfo = ""
    private var list_hand_scene: MutableList<Map<*, *>> = ArrayList()
    private var blagg = false
    private var intentbrod: Intent? = null
    private val info: WifiInfo? = null
    private var type = ""
    private var strUser: String? = null
    private var strDID: String? = null
    private var strPwd: String? = null
    private val inde_video = 1
    private var list: MutableList<Map<*, *>> = ArrayList()
    private var onresumes = false

    /**
     * 小苹果绑定列表
     */
    private var mDeviceManager: DeviceManager? = null
    var wifiDevices: MutableList<GizWifiDevice> = ArrayList()
    private val deviceNames: MutableList<String> = ArrayList()
    private var ykanInterface: YkanIRInterfaceImpl? = null
    private var index = 0
    private var again_connection = false
    private val handler: Handler = object : Handler() {

        //B501 音乐面板   type = 22
        override fun handleMessage(msg: Message) {
//            ToastUtil.showToast(getActivity(), "mggiz在线");
//            if (dialogUtil != null) dialogUtil.loadDialog();
            when (msg.what) {
                0 -> activity!!.runOnUiThread {
                    if (dialogUtil != null) {
                        dialogUtil!!.loadDialog()
                    } else {
                        dialogUtil = DialogUtil(activity)
                        dialogUtil!!.loadDialog()
                    }
                }
                1 -> {

                }
                2 -> when (doit_wifi) {
                    "air" -> test_air_control(mapdevice["panelMac"].toString())
                    "tv" -> test_tv(mapdevice["panelMac"].toString())
                }
                3 -> if (dialogUtil != null) dialogUtil!!.removeDialog()
                6 -> {
                    index++
                    if (index >= 2) {
                        dialogUtil!!.removeDialog()
                        index = 0
                        return
                    }
                    this.postDelayed({
                        when (doit_wifi) {
                            "air" -> test_air_control(mapdevice["panelMac"].toString())
                            "tv" -> test_tv(mapdevice["panelMac"].toString())
                        }
                    }, 10000) //两次下载遥控器码值时间间隔10s,
                }
                7 -> when (doit_wifi) {
                    "air" -> test_air_control(mapdevice["panelMac"].toString())
                    "tv" -> test_tv(mapdevice["panelMac"].toString())
                }
                10 -> when (videofrom) {
                    "macfragment" -> mac_fragment_video_ok()
                    "devicefragment" -> tongzhi_to_video("1")
                }
                11 -> when (videofrom) {
                    "macfragment" -> onitem_wifi_shexiangtou(mapdevice)
                    "devicefragment" -> common_video(video_item as Map<String, Any>)//
                }
            }
        }
    }

    private var intfirst_time = 0
    private var receiver: MyBroadCast? = null
    private var dialog: Dialog? = null
    private var authType //（1 业主 2 成员）
            : String? = null
    private var device_name: String? = null
    private var device_gatewayNumber: String? = null
    private var device_name1: String? = null
    private var device_name2: String? = null
    private var qiehuan: String? = null
    private val timer: Timer? = null
    private val timerTask: TimerTask? = null
    private var myPagerAdapter: MyPagerAdapter? = null
    private var position_room = 0

    /**
     * 发送给MyDeviceItemActivity视频的状态，获取摄像头状态，并返回
     */
    private fun tongzhi_to_video(status: String) {
        val event = MyEvent()
        event.msg = status
        //...设置event
        EventBus.getDefault().post(event)
    }

    /**
     * macfragment video ok
     */
    private fun mac_fragment_video_ok() {
        val content = stringbuffer.toString()
        val splits = content.split(",".toRegex()).toTypedArray()
        if (splits.size == 3) {
            if (splits[0] == "未知状态" && splits[1] == "正在连接" && splits[2] == "在线") {
                stringbuffer = StringBuffer()
                //                            AppManager.getAppManager().finishActivity_current(PlayActivity.class);
                val intent = Intent("play_finish")
                activity!!.sendBroadcast(intent)
                again_connection = true
            }
        } else {
            if (!playactivityfinsh) {
//                        AppManager.getAppManager().finishActivity_current(PlayActivity.class);
                SystemValue.deviceName = strUser
                SystemValue.deviceId = strDID
                SystemValue.devicePass = strPwd
                val intent = Intent(activity, PlayActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions((context as AppCompatActivity?)!!)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        })
    }

    override fun onData() {//
        share_getData()
        init_device_onclick() //监听首页设备点击事件
        // room_list_show_adapter();
        device_list_show_adapter()
        initTab0()
    }

    private fun share_getData() {
        init_music_flag()
        //成员，业主accountType->addrelative_id
        val accountType = SharedPreferencesUtil.getData(activity, "accountType", "") as String
        when (accountType) {
            "1" -> add_device!!.visibility = View.VISIBLE
            "2" -> add_device!!.visibility = View.GONE
        }
    }

    private fun init_music_flag() {//
        loginPhone = SharedPreferencesUtil.getData(activity, "loginPhone", "") as String
        val preferences = activity!!.getSharedPreferences("sraum$loginPhone",
                Context.MODE_PRIVATE)
        vibflag = preferences.getBoolean("vibflag", false)
        //        musicflag = preferences.getBoolean("musicflag", false);
        musicflag = SharedPreferencesUtil.getData(activity, "musicflag", false) as Boolean
    }
    /**
     * 侧栏房间列数据显示
     */
    //    private void room_list_show_adapter() {
    //        roomList = new ArrayList<>();
    //        homeDeviceListAdapter = new HomeDeviceListAdapter(getActivity(), roomList, new HomeDeviceListAdapter.HomeDeviceItemClickListener() {
    //            @Override
    //            public void homedeviceClick(String number) {//获取单个房间关联信息（APP->网关）
    //                sraum_getOneRoomInfo(number);
    //            }
    //        });
    //        home_listview.setAdapter(homeDeviceListAdapter);//设备侧栏列表
    //        home_listview.setOnItemClickListener(this);
    //    }
    /**
     * 具体房间下的设备列表显示
     */
    private fun device_list_show_adapter() {
        roomList = ArrayList()
        deviceListAdapter = DetailDeviceHomeAdapter(context!!, deviceList)
        mDragGridView!!.adapter = deviceListAdapter //设备侧栏列表
        // home_listview.setOnItemClickListener(this);
    }

    private fun initTab0() {
        myPagerAdapter = MyPagerAdapter()
        viewpager!!.adapter = myPagerAdapter
        tablayout_vertical!!.setupWithViewPager(viewpager)
        //        tablayout.setTabBadge(7, 32);
//        tablayout.setTabBadge(2, -1);
//        tablayout.setTabBadge(3, -1);
//        tablayout.setTabBadge(4, -1);
        tablayout_vertical!!.setOnTabClickListener(this)
    }

    override fun onTabSelected(tab: TabView?, position: Int) {
        refresh_room_item(position)
    }

    private fun refresh_room_item(position: Int) {
        //编写点击房间去显示该房间下的设备列表
        if (position > roomList.size || roomList.size == 0) {
            return
        }
        position_room = position
        current_room_number = roomList[position]["number"].toString()
        Thread(Runnable { sraum_getOneRoomInfo(current_room_number) }).start()
    }

    private inner class MyPagerAdapter : PagerAdapter(), TabAdapter {
        override fun getCount(): Int {
            return roomList.size
        }

        override fun getBadge(position: Int): TabBadge? {
            return null
        }

        override fun getIcon(position: Int): TabIcon? {
            return null
        }

        override fun getTitle(position: Int): TabTitle {
            val dimen = resources.getDimension(R.dimen.x3)
            Log.e("dimen", "dimen->x3:" + dimen)

//            val r: Resources = context!!.resources // 取得手機資源
//
//            val px = spToPixel(context!!,4.0f)

            //Log.e("build", "getTitle: " + "dimen:" + px)
            return TabTitle.Builder()
                    .setContent(roomList[position]["name"].toString() + "(" + roomList[
                            position]["count"].toString() + ")")
                    // .setTextSize(DipUtil.pixelToDip(activity, 3.5f)) // .setTextColor(R.color.green, R.color.black)
                    .setTextSize(dimen.toInt())
                    .build()
        }

        //将sp转换成pixel
        fun spToPixel(context: Context, spValue: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            val pixelValue = (spValue * scaledDensity + 0.5f)
            println("spToPixel---> spValue=$spValue,scaledDensity=$scaledDensity,pixelValue=$pixelValue")
            return pixelValue
        }


        override fun getList(): MutableList<Map<*, *>> {
            return roomList
        }


        override fun getBackground(position: Int): Int {
            return 0
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return super.instantiateItem(container, position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    /**
     * 初始化设备点击事件
     */
    private fun init_device_onclick() {
        mDragGridView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val mapalldevice: MutableMap<String, Any> = HashMap()
            listob = ArrayList()
            if (position > listtype.size) return@OnItemClickListener
            if (list.size == 0) return@OnItemClickListener
            when (list[position]["type"].toString()) {
                "22", "23","115" -> {
                    status = listtype[position] as String
                }
                else -> {
                    status = if (listtype[position] == "1") {
                        "0"
                    } else {
                        "1"
                    }
                }
            }

            val mapdevice1 = HashMap<String, Any?>()
            mapdevice1["type"] = list[position]["type"].toString()
            mapdevice1["number"] = list[position]["number"].toString()
            when (list[position]["type"].toString()) {
                "11" -> mapdevice1["status"] = "0"
                "15" -> //            case "17":
                    mapdevice1["status"] = "1"
                else -> mapdevice1["status"] = status!!
            }
            mapdevice1["dimmer"] = list[position]["dimmer"].toString()
            mapdevice1["mode"] = list[position]["mode"].toString()
            mapdevice1["temperature"] = list[position]["temperature"].toString()
            mapdevice1["speed"] = list[position]["speed"].toString()
            mapdevice1["mac"] = if (list[position]["mac"] == null) "" else list[position]["mac"].toString()
            mapdevice1["deviceId"] = if (list[position]["deviceId"] == null) "" else list[position]["deviceId"].toString()
            mapdevice1["name"] = list[position]["name"].toString()
            mapdevice1["panelName"] = if (list[position]["panelName"] == null) "" else list[position]["panelName"].toString()
            mapdevice1["panelMac"] = if (list[position]["panelMac"] == null) "" else list[position]["panelMac"].toString()
            listob.add(mapdevice1 as Map<String, Any>)
            mapalldevice["token"] = TokenUtil.getToken(activity)
            mapalldevice["boxNumber"] = TokenUtil.getBoxnumber(activity)
            mapalldevice["deviceInfo"] = listob
            var name = ""
            when (list[position]["type"].toString()) {
                "7" -> {
                    name = "门磁"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "8" -> {
                    name = "桌面人体感应"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "24" -> {
                    name = "吸顶人体感应"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "25" -> {
                    name = "光照检测器"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "10" -> name = "PM2.5"
                "12" -> {
                    name = "久坐报警器"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "13" -> {
                    name = "烟雾报警器"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
                "14" -> {
                    name = "天然气报警器"
                    ToastUtil.showToast(activity,
                            name + "无控制功能")
                }
            }
            when (list[position]["type"].toString()) {
                "7", "8", "9", "12", "13", "14" -> {

                }
                "202" -> {
                    mapdevice = mapdevice1
                    test_tv(mapdevice["panelMac"].toString())
                }
                "206" -> {
                    mapdevice = mapdevice1
                    test_air_control(mapdevice1["panelMac"].toString())
                }
                "101", "103" -> {
                    //                map.put("token", TokenUtil.getToken(AddWifiCameraScucessActivity.this));
//                map.put("type", "AA03");
//                map.put("name", name);
//                map.put("mac", wificamera.get("strMac"));
//                map.put("controllerId", wificamera.get("strDeviceID"));
//                map.put("user", wificamera.get("strName"));
//                map.put("password", "888888");
//                map.put("wifi", wificamera.get("wifi"));
                    init_permissions()
                    mapdevice = mapdevice1
                    videofrom = "macfragment"
                    onitem_wifi_shexiangtou(mapdevice)
                }
                "104" -> {
                    sraum_getCameraLiveUrl(mapdevice1["panelMac"].toString())
                }
                else -> curtains_and_light(position, mapdevice1)
            }
        }

        mDragGridView!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            if (list.size > 0) {
                deivce_number = list[position]["number"] as String? //设备编号
                deivce_type = list[position]["type"] as String? //设备编号
                device_name = list[position]["name"] as String?
                device_gatewayNumber = list[position]["boxNumber"] as String?
                when (deivce_type) {
                    "4" -> {
                        device_name1 = list[position]["name1"] as String?
                        device_name2 = list[position]["name2"] as String?
                    }
                }
                when (roomNumber) {
                    "" -> {
                    }
                    else -> common_setting_linear!!.visibility = View.VISIBLE
                }
                dialogUtil!!.loadViewBottomdialog()
            }
            true
        }
    }

    /**
     * 通用版获取 萤石云
     */
    private fun sraum_getCameraLiveUrl(panelNumber: String?) {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(activity)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        mapdevice["areaNumber"] = areaNumber
        mapdevice["number"] = panelNumber

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getCameraLiveUrl, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getCameraLiveUrl(panelNumber)
        }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {

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
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                var accessToken = user.accessToken
                var url = user.url
                var mAppKey = "ccd38858cc5a459bbeedcf93a25ae6be"
                //启动播放页面
                //启动播放页面
                com.ezuikit.open.PlayYingShiActivity.startPlayActivity(activity, mAppKey, accessToken, url)
            }
        })
    }

    /**
     * 测试调光灯
     */
    private fun test_tiaoguanglight() {
        val intent_tiaoguang = Intent(activity, TiaoGuangLightActivity::class.java)
        startActivity(intent_tiaoguang)
    }

    /**
     * 测试空调
     */
    private fun test_air_control() {
        val intent_air_control = Intent(activity, AirControlActivity::class.java)
        startActivity(intent_air_control)
    }

    /**
     * 测试窗帘控制
     */
    private fun test_control_curtain() {
        val intent_air_control = Intent(activity, CurtainWindowActivity::class.java)
        startActivity(intent_air_control)
    }

    /**
     * 测试数据
     */
    private fun test_device_data() {
        list_homedev_items = ArrayList()
        var itemHashMap = HashMap<Any, Any>()
        itemHashMap["item_image"] = "1"
        itemHashMap["name"] = "常用"
        itemHashMap["type"] = "0"
        list_homedev_items.add(itemHashMap as Map<String, Any>)
        itemHashMap = HashMap<Any, Any>()
        itemHashMap["item_image"] = "2"
        itemHashMap["name"] = "南卧室"
        itemHashMap["type"] = "0"
        list_homedev_items.add(itemHashMap as Map<String, Any>)
        itemHashMap = HashMap<Any, Any>()
        itemHashMap["item_image"] = "3"
        itemHashMap["name"] = "客厅"
        itemHashMap["type"] = "0"
        list_homedev_items.add(itemHashMap as Map<String, Any>)
    }

    /**
     * 底部弹出拍照，相册弹出框
     */
    private fun addViewid() {
        account_view = LayoutInflater.from(activity).inflate(R.layout.edit_bottom_scene, null)
        common_setting_linear = account_view!!.findViewById<View>(R.id.common_setting_linear) as LinearLayout
        rename_scene_linear = account_view!!.findViewById<View>(R.id.rename_scene_linear) as LinearLayout
        delete_scene_linear = account_view!!.findViewById<View>(R.id.delete_scene_linear) as LinearLayout
        cancel_scene_linear = account_view!!.findViewById<View>(R.id.cancel_scene_linear) as LinearLayout
        common_setting_linear!!.setOnClickListener(this)
        rename_scene_linear!!.setOnClickListener(this)
        delete_scene_linear!!.setOnClickListener(this)
        cancel_scene_linear!!.setOnClickListener(this)
        //common_setting_image
        common_setting_image = account_view!!.findViewById<View>(R.id.common_setting_image) as ImageView
        dialogUtil = DialogUtil(activity, account_view, 2)
    }

    override fun onEvent() {
        addViewid() //底部弹出拍照，相册弹出框
        add_device!!.setOnClickListener(this)
        back_rel!!.setOnClickListener(this)
        area_rel!!.setOnClickListener(this)
        refresh_view!!.setScrollBackDuration(300)
        refresh_view!!.setPinnedTime(1000)
        refresh_view!!.pullLoadEnable = false
        refresh_view!!.setXRefreshViewListener(object : SimpleXRefreshListener() {
            override fun onRefresh(isPullDown: Boolean) {
                super.onRefresh(isPullDown)
                refresh_view!!.stopRefresh()
                refresh_room_item(position_room)
            }

            override fun onLoadMore(isSilence: Boolean) {
                super.onLoadMore(isSilence)
            }
        })
    }

    /*--------------------------------切换房间选项卡-----------------------------*/
    fun switch_room_dialog(context: Context?) {

//        dialogUtil.loadDialog();
        val view = LayoutInflater.from(context).inflate(R.layout.credit_card_num_pop, null)
        val xiala_shuaka_rel = view.findViewById<View>(R.id.xiala_shuaka_rel) as RelativeLayout
        val btn_xiala_img = view.findViewById<View>(R.id.btn_xiala_img) as ImageView
        val xiala_shuaka_txt = view.findViewById<View>(R.id.xiala_shuaka_txt) as TextView
        val forget_shuaka_btn = view.findViewById<View>(R.id.forget_shuaka_btn) as Button
        val confirm_shuaka_btn = view.findViewById<View>(R.id.confirm_shuaka_btn) as Button
        val room_select = view.findViewById<View>(R.id.room_select) as ListView
        val roomNames: MutableList<String?> = ArrayList()
        for (i in roomList.indices) {
            roomNames.add(roomList[i]["name"] as String?)
        }
        room_select.adapter = ArrayAdapter(activity!!, R.layout.simple_expandable_list_item_new, roomNames)
        room_select.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> modify_correl_infor(position, roomNames) }
        xiala_shuaka_rel.setOnClickListener {
            //                showPopwindow_room(xiala_shuaka_rel, xiala_shuaka_txt);
        }
        //// ImageView btn_xiala_img;

        //显示数据
        dialog = Dialog(activity!!)
        dialog!!.setContentView(view)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
        val dm = context!!.resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog!!.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.7).toInt() //宽度设置为屏幕的0.5
        if (roomNames.size >= 6) p.height = (displayHeight * 0.5).toInt() //宽度设置为屏幕的0.5
        //        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog!!.window!!.attributes = p //设置生效
        //        dialogUtil.loadDialog();
        forget_shuaka_btn.setOnClickListener { dialog!!.dismiss() }
        confirm_shuaka_btn.setOnClickListener {
            //充电绑定提交
            //xiala_shuaka_txt->cardTime 刷卡次数
//                charging_binding_submission(dialog, xiala_shuaka_txt.getText().toString(), driect);
        }
    }

    /**
     * 修改房间关联信息（APP->网关）
     *
     * @param position
     * @param roomNames
     */
    private fun modify_correl_infor(position: Int, roomNames: List<String?>) {
        val name = roomNames[position] //新的房间名称
        //roomNumber = "";
        //将当前房间下的设备放到其他房间下
        //拿到房间编号，拿到设备编号
        for (i in roomList.indices) {
            if (name == roomList[i]["name"]) {
                roomNumber = roomList[i]["number"] as String?
                break
            }
        }
        if (current_room_number == roomNumber) {
            ToastUtil.showToast(activity, "请移动到别的房间")
            if (dialog != null) dialog!!.dismiss()
            if (dialogUtil != null) dialogUtil!!.removeviewBottomDialog()
            return
        }
        //deivce_number,roomNumber;
        //修改房间关联信息（APP->网关）
        val map = HashMap<Any?, Any?>() // current_room_number
        map["token"] = TokenUtil.getToken(activity)
        map["areaNumber"] = areaNumber
        map["roomNumberNew"] = roomNumber
        map["roomNumberOld"] = current_room_number
        map["number"] = deivce_number
        map["type"] = deivce_type


//        map.put("number", deivce_number);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateRoomInfo, map as Map<String, Any>,
                object : Mycallback(AddTogglenInterfacer { }, activity, null) {
                    override fun onSuccess(user: User) {
//                        project_select.setText(user.name);
                        if (dialog != null) dialog!!.dismiss()
                        if (dialogUtil != null) dialogUtil!!.removeviewBottomDialog()
                        when (current_room_number) {
                            "" -> {
                            }
                            else -> {
                                //                                sraum_getOneRoomInfo(current_room_number);
                                qiehuan = ""
                                Thread(Runnable { sraum_getRoomsInfo(areaNumber, qiehuan!!) }).start()
                            }
                        }
                    }

                    override fun wrongBoxnumber() {
                        super.wrongBoxnumber()
                        ToastUtil.showToast(activity, """
     areaNumber
     不存在
     """.trimIndent())
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(activity, "roomNumberNew 不存在")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(activity, "number 不存在")
                    }
                })
    }

    override fun onEvent(eventData: MyDialogEvent) {}
    override fun viewId(): Int {
        return R.layout.first_page_act_new
    }

    override fun onView(view: View) {
        registerMessageReceiver()
        StatusUtils.setFullToStatusBar(activity) // StatusBar.
        //        room_list_show_adapter();
        intfirst_time = 1
        //        SpeechUtil.startSpeech(getActivity(), 1, getResources().getString(R.string.text_tts_source),"time_push");
        init_first_sraum()
        //        init_time();
//        test_pm25();
        init_wifi_camera()
    }

    override fun onResume() { //视图可见后，去加载接口数据
        super.onResume()
        if (!onresumes) {
            onresumes = true
            activity!!.window.decorView.postDelayed({
                initPermission()
            },1000)
        }
        //SpeechUtil.startSpeech(getActivity(),1,getResources().getString(R.string.text_tts_source));
        areaList = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "areaList")
        init_music_flag()
        show_old_areaList()
        show_old_roomList()
        show_old_deviceList()
        if (areaList.size != 0) {
            Thread(Runnable { com_from_laungh() }).start()
        } else {
            Thread(Runnable { sraum_getAllArea() }).start()
        }
        blagg = true
        mDeviceManager!!.setGizWifiCallBack(mGizWifiCallBack)
        update(mDeviceManager!!.canUseGizWifiDevice)
        Thread(Runnable { otherDevices }).start()
    }

    private fun initPermission() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
//        val permissions = RxPermissions(activity as AppCompatActivity)
//        //Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
//        permissions.request(
//        Manifest.permission.READ_PHONE_STATE).subscribe(object : Observer<Boolean?> {
//            override fun onSubscribe(d: Disposable) {}
//            override fun onNext(aBoolean: Boolean?) {}
//            override fun onError(e: Throwable) {}
//            override fun onComplete() {
//                val mobilePhone = SharedPreferencesUtil.getData(activity, "loginPhone", "") as String
//                val szImei = SharedPreferencesUtil.getData(activity, "regId", "") as String
//                init_islogin(mobilePhone, szImei)
//            }
//        })
        val mobilePhone = SharedPreferencesUtil.getData(activity, "loginPhone", "") as String
        val szImei = SharedPreferencesUtil.getData(activity, "regId", "") as String
        init_islogin(mobilePhone, szImei)
    }

    /**
     * 初始化登录
     *
     * @param
     * @param mobilePhone
     * @param szImei
     */
    private fun init_islogin(mobilePhone: String, szImei: String) {
        val map: MutableMap<String, Any> = java.util.HashMap()
        map["mobilePhone"] = mobilePhone
        map["phoneId"] = szImei
        LogUtil.eLength("查看数据", JSON.toJSONString(map))
        MyOkHttp.postMapObject(ApiHelper.sraum_isLogin, map, object : Mycallback(AddTogglenInterfacer { init_islogin(mobilePhone, szImei) }, activity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                Log.e("init_islogin", "success")
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun threeCode() {
                //103已经登录，需要退出app
//                dialog.show();
                Log.e("init_islogin", "账号在其他地方登录，请重新登录。")
                ToastUtils.getInstances().showDialog("账号在其他地方登录，请重新登录。")
            }
        })
    }


    private fun show_old_roomList() {
        roomList = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "roomList_old")
        if (roomList.size != 0) {
            display_room_list(position_room)
        }
    }

    /**
     * 显示历史区域
     */
    private fun show_old_areaList() {
        areaList = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "areaList_old")
        if (areaList.size != 0) {
            for (map in areaList) {
                if ("1" == map["sign"].toString()) {
                    current_area_map = map
                    handler_laungh.sendEmptyMessage(0)
                    areaNumber = current_area_map.get("number").toString()

                    authType = current_area_map.get("authType").toString() //（1 业主 2 成员）
                    SharedPreferencesUtil.saveData(activity, "areaNumber", areaNumber)
                    SharedPreferencesUtil.saveData(activity, "areaName",
                            current_area_map.get("name").toString())

                    SharedPreferencesUtil.saveData(activity, "authType", authType)
                    handler_laungh.sendEmptyMessage(1)
                    break
                }
            }
        }
    }


    /**
     * 首页显示历史设备数据
     */
    private fun show_old_deviceList() {
        val isnewProcess = SharedPreferencesUtil.getData(activity, "newProcess", false) as Boolean
        if (isnewProcess) { //新进程下首页默认填充设备历史数据（历史数据只供显示填补空白）（待完成）；
            SharedPreferencesUtil.saveData(activity, "newProcess", false)
            list = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "list_old")

            listtype.clear()
            if (list.size != 0) {
                for (map in list) {
                    listtype.add(if (map["status"] == null) "1" else map["status"].toString())
                }
                //展示首页设备列表
                handler_laungh.sendEmptyMessage(5)
            }
        }
    }

    var handler_laungh: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> if (current_area_map != null) when (current_area_map.get("authType").toString()) {
                    "1" -> area_name_txt!!.text = current_area_map.get("name").toString()
                    "2" -> area_name_txt!!.text = current_area_map.get("name").toString()
                }
                1 -> when (authType) {
                    "1" -> add_device!!.visibility = View.VISIBLE
                    "2" -> add_device!!.visibility = View.GONE
                }
                3 -> common_room_show()
                4 -> when (qiehuan) {
                    "qiehuan" -> {
                        current_room_number = roomList[0]["number"].toString()
                        Thread(Runnable { sraum_getOneRoomInfo(roomList[0]["number"].toString()) }).start()
                        position_room = 0
                        display_room_list(position_room)
                    }
                    else -> common_room_show()
                }
                5 -> display_home_device_list()
                6 -> {
                    when (authType) {
                        "1" -> add_device!!.visibility = View.VISIBLE
                        "2" -> add_device!!.visibility = View.GONE
                    }
                    when (current_area_map!!["authType"].toString()) {
                        "1" -> area_name_txt!!.text = current_area_map!!["name"].toString()
                        "2" -> area_name_txt!!.text = current_area_map!!["name"].toString()
                    }
                    qiehuan = "qiehuan"
                    Thread(Runnable { sraum_getRoomsInfo(areaNumber, qiehuan!!) }).start()
                    if (popupWindow != null) popupWindow!!.dismiss()
                }
            }
        }
    }

    private fun common_room_show() {
        if (roomList.size != 0) {
            list = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "list")
            listtype.clear()
            if (current_room_number != null) {
                common_device_list()
                for (i in roomList.indices) {
                    if (roomList[i]["number"].toString() == current_room_number) {
                        position_room = i
                        display_room_list(position_room)
                        break
                    }
                }
            } else {
                current_room_number = roomList[0]["number"].toString()
                common_device_list()
                position_room = 0
                display_room_list(position_room)
            }
        }
    }

    /**
     * 显示房间下的设备列表
     */
    private fun common_device_list() {
        if (list.size != 0) {
            for (map in list) {
                listtype.add(if (map["status"] == null) "1" else map["status"].toString())
            }
            //展示首页设备列表
            handler_laungh.sendEmptyMessage(5)
        } else {
            Thread(Runnable { sraum_getOneRoomInfo(current_room_number) }).start()
        }
    }

    private fun com_from_laungh() {
        for (map in areaList) {
            if ("1" == map["sign"].toString()) {
                current_area_map = map
                handler_laungh.sendEmptyMessage(0)
                areaNumber = current_area_map.get("number").toString()
                authType = current_area_map.get("authType").toString() //（1 业主 2 成员）
                SharedPreferencesUtil.saveData(activity, "areaNumber", areaNumber)
                SharedPreferencesUtil.saveData(activity, "areaName",
                        current_area_map.get("name").toString())
                SharedPreferencesUtil.saveData(activity, "authType", authType)
                handler_laungh.sendEmptyMessage(1)
                roomList = SharedPreferencesUtil.getInfo_Second_List(App.getInstance().applicationContext, "roomList")
                if (roomList.size != 0) {
                    handler_laungh.sendEmptyMessage(3)
                } else {
                    qiehuan = ""
                    Thread(Runnable { sraum_getRoomsInfo(current_area_map.get("number").toString(), qiehuan!!) }).start()
                }
                break
            }
        }
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "list", ArrayList())
    }

    private fun init_first_sraum() {
        EventBus.getDefault().register(this)
//        init_wifi_camera()
        registerMessageReceiver()
        // 遥控云数据接口分装对象对象
        ykanInterface = YkanIRInterfaceImpl(activity!!.applicationContext)
        // 遥控云数据接口分装对象对象
        ykanInterface = YkanIRInterfaceImpl(activity!!.applicationContext)
        /**
         * 小苹果初始化
         */
        mDeviceManager = DeviceManager
                .instanceDeviceManager(activity!!.applicationContext)
        //        get_wifidevice(0);
    }


    internal inner class StartPPPPThread : Runnable {
        override fun run() {
            try {
                Thread.sleep(100)
                if (VuidUtils.isVuid(SystemValue.deviceId)) {
                    val vuidStatus = NativeCaller.StartVUID("0", SystemValue.devicePass, 1, "", "", 0, SystemValue.deviceId, 0)
                    Log.e("vst", "vuidStatus$vuidStatus")
                    if (vuidStatus == -2) {
                        // TODO: 2019-11-25 VUID  无效
                        val bd = Bundle()
                        val msg: Message = PPPPMsgHandler.obtainMessage()
                        msg.what = ContentCommon.PPPP_MSG_VSNET_NOTIFY_TYPE_VUIDSTATUS
                        bd.putInt(STR_MSG_PARAM, -2)
                        bd.putString(STR_DID, SystemValue.deviceId)
                        msg.data = bd
                        PPPPMsgHandler.sendMessage(msg)
                    }
                } else {
                    startCameraPPPP()
                }
            } catch (e: java.lang.Exception) {
            }
        }
    }


    private fun startCameraPPPP() {
        try {
            Thread.sleep(100)
        } catch (e: java.lang.Exception) {
        }
        if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstd")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstf")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vste")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("pisr")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstg")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vsth")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJLEKGOFMEDHAMHHJNAGGFABMCOBGJOLHLJDFAFCPPHGILKIKLMANNHKEDKOINIBNCPJOMK:vstarcam2018", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstb") || SystemValue.deviceId.toLowerCase().startsWith("vstc")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstj")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJNEOHEFBEIGANCHHMBHIFEAHDEAMJCKCKJDJAFDDPPHLKJIHLMBENHKDCHPHNJBODA:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstk")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EBGDEJBJKGJFGJJBEFHPFCEKHGNMHNNMHMFFBICPAJJNLDLLDHACCNONGLLPJGLKANMJLDDHODMEBOCIJEMA:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstm")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EBGEEOBOKHJNHGJGEAGAEPEPHDMGHINBGIECBBCBBJIKLKLCCDBBCFODHLKLJJKPBOMELECKPNMNAICEJCNNJH:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstn")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBBKBIFGAIAFGHDFLFJGJNIGEMOHFFPAMDMAAIIKBKNCDBDDMOGHLKCJCKFBFMPLMCBPEMG:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstl")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJIEIGNFPEEHGNMHPNBGOFIBECEBLJDLMLGDKAPCNPFGOLLJFLJAOMKLBDFOGMAAFCJJPNFJP:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstp")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJLEIGJFLENHLNBHCNMGAFGBNCOAIJMLKKODNALCCPKGBLHJLLHAHMBKNDFOGNGBDCIJFMB:vstarcam2019", 0)
        } else {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "", 0)
        }
    }

    private inner class MyBroadCast : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {

//            AddCameraActivity.this.finish();
            Log.d("ip", "AddCameraActivity.this.finish()")
            if (arg1.action == "finish") {
                playactivityfinsh = true
                if (again_connection) {
                    val intent_new = Intent(activity, PlayActivity::class.java)
                    startActivity(intent_new)
                    again_connection = false
                }
            }
        }
    }

    private fun stopCameraPPPP() {
        NativeCaller.StopPPPP(SystemValue.deviceId)
    }

    private fun init_wifi_camera() {
//        BridgeService.setAddCameraInterface(this);
        if (activity == null) return
        BridgeService.setCallBackMessage(this)
        BridgeService.setIpcamClientInterface(this)
        NativeCaller.Init()
        receiver = MyBroadCast()
        val filter = IntentFilter()
        filter.addAction("finish")

        activity!!.registerReceiver(receiver, filter)
        intentbrod = Intent("drop")
    }

    private val list_mac_wifi: MutableList<Map<*, *>> = ArrayList()
    private val mGizWifiCallBack: GizWifiCallBack = object : GizWifiCallBack() {
        override fun didUnbindDeviceCd(result: GizWifiErrorCode, did: String) {
            super.didUnbindDeviceCd(result, did)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, "解除绑定成功")
            } else {
                // 解绑失败
                Logger.d(TAG, "解除绑定失败")
            }
        }

        override fun didBindDeviceCd(result: GizWifiErrorCode, did: String) {
            super.didBindDeviceCd(result, did)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 绑定成功
                Logger.d(TAG, "绑定成功")
            } else {
                // 绑定失败
                Logger.d(TAG, "绑定失败")
            }
        }

        override fun didSetSubscribeCd(result: GizWifiErrorCode, device: GizWifiDevice, isSubscribed: Boolean) {
            super.didSetSubscribeCd(result, device, isSubscribed)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Logger.d(TAG, (if (isSubscribed) "订阅" else "取消订阅") + "成功")
                val map = HashMap<Any?, Any?>()
                map["mac"] = device.macAddress
                val str = ParceUtil.object2String(device)
                //                mGizWifiDevice1 = ParceUtil.unmarshall(str, GizWifiDevice.CREATOR);
                map["code"] = str
                list_mac_wifi.add(map)
                SharedPreferencesUtil.saveInfo_List(activity, "list_mac_wifi", list_mac_wifi)
            } else {
                // 解绑失败
                Logger.d(TAG, "订阅失败")
            }
        }

        override fun discoveredrCb(result: GizWifiErrorCode,
                                   deviceList: List<GizWifiDevice>) {
            Logger.d(TAG,
                    "discoveredrCb -> deviceList size:" + deviceList.size
                            + "  result:" + result)
            when (result) {
                GizWifiErrorCode.GIZ_SDK_SUCCESS -> {
                    Logger.e(TAG, "load device  sucess")
                    update(deviceList)
                }
                else -> {
                }
            }
        }
    }

    fun update(gizWifiDevices: List<GizWifiDevice>?) {
        val mGizWifiDevice: GizWifiDevice? = null
        if (gizWifiDevices == null) {
            deviceNames.clear()
        } else if (gizWifiDevices != null && gizWifiDevices.size >= 1) {
//            wifiDevices.clear();
            wifiDevices.addAll(gizWifiDevices)
            val h = HashSet(wifiDevices)
            wifiDevices.clear()
            for (gizWifiDevice in h) {
                wifiDevices.add(gizWifiDevice)
            }
            deviceNames.clear()
            //            for (int i = 0; i < wifiDevices.size(); i++) {
////                deviceNames.add(wifiDevices.get(i).getProductName() + "("
////                        + wifiDevices.get(i).getMacAddress() + ") "
////                        + getBindInfo(wifiDevices.get(i).isBind()) + "\n"
////                        + getLANInfo(wifiDevices.get(i).isLAN()) + "  " + getOnlineInfo(wifiDevices.get(i).getNetStatus()));
//                mGizWifiDevice = wifiDevices.get(i);
//                // list_hand_scene
//                // 绑定选中项
//                if (!Utility.isEmpty(mGizWifiDevice)) { //
//                    mDeviceManager.bindRemoteDevice(mGizWifiDevice);
//                    final GizWifiDevice finalMGizWifiDevice = mGizWifiDevice;
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mDeviceManager.setSubscribe(finalMGizWifiDevice, true);
//                        }
//                    }, 1000);
//                }
//            }
            //去绑定和订阅
//            ToastUtil.showToast(getActivity(),"wifiDevices.size():" + wifiDevices.size());
        }
        //        adapter.notifyDataSetChanged();
    }

    private fun getBindInfo(isBind: Boolean): String {
        var strReturn = ""
        strReturn = if (isBind == true) "已绑定" else "未绑定"
        return strReturn
    }

    private fun getLANInfo(isLAN: Boolean): String {
        var strReturn = ""
        strReturn = if (isLAN == true) "局域网内设备" else "远程设备"
        return strReturn
    }

    private fun getOnlineInfo(netStatus: GizWifiDeviceNetStatus): String {
        var strReturn = ""
        strReturn = if (mDeviceManager!!.isOnline(netStatus) == true) //判断是否在线的方法
            "在线" else "离线"
        return strReturn
    }

    private val PROCESS_NAME = "com.massky.sraum" //进程名称//第一次创建,系统中还不存在该process,所以一定是主进程

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    val isAppMainProcess: Boolean
        get() = try {
            val pid = Process.myPid()
            val process = getAppNameByPID(App.getInstance(), pid)
            if (TextUtils.isEmpty(process)) {
                //第一次创建,系统中还不存在该process,所以一定是主进程
                true
            } else if (PROCESS_NAME.equals(process, ignoreCase = true)) {
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }

    /**
     * 根据Pid得到进程名
     */
    fun getAppNameByPID(context: Context, pid: Int): String {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                //主进程的pid是否和当前的pid相同,若相同,则对应的包名就是app的包名
                return processInfo.processName
            }
        }
        return ""
    }

    /**
     * 摄像头click
     *
     * @param mapdevice
     */
    private fun onitem_wifi_shexiangtou(mapdevice: Map<String, Any?>) {
        playactivityfinsh = false
        again_connection = false
        this.mapdevice = mapdevice as HashMap<String, Any?>
        common_video(mapdevice as Map<String, Any>)
    }

    /**
     * 共同的视频
     *
     * @param mapdevice
     */
    private fun common_video(mapdevice: Map<String, Any>) {
        val list = SharedPreferencesUtil.getInfo_List(activity, "list_wifi_camera_first")
        Log.e("list_camera-s", JSON.toJSONString(list))
        if (list.size == 0) return
        var tag = 0
        var is_has: Boolean? = false
        for (i in list.indices) {
            if (mapdevice["dimmer"].toString()
                    == list[i]["did"]) {
                tag = list[i]["tag"] as Int
                is_has = true
                break
            }
        }
        if (!is_has!!) return
        if (tag == 1) {

//            in.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
//            in.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
//            in.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
//            String strUser, String strPwd, String strDID
            strUser = mapdevice["mode"].toString()
            strDID = mapdevice["dimmer"].toString()
            strPwd = mapdevice["temperature"].toString()
            handler.sendEmptyMessage(10) //设备已经在线了
            //            Toast.makeText(getActivity(), "设备已经是在线状态了", Toast.LENGTH_SHORT).show();
        } else if (tag == 2) {
            when (videofrom) {
                "macfragment" -> Toast.makeText(activity, "设备不在线", Toast.LENGTH_SHORT).show()
                "devicefragment" -> tongzhi_to_video("0")
            }
            //Toast.makeText(activity, "tag :" + 2 + "不在线", Toast.LENGTH_SHORT).show()
        } else {
            done(mapdevice["mode"].toString(), mapdevice["temperature"].toString(), mapdevice["dimmer"].toString()) //String strUser,String strPwd,String strDID
        }
    }

    private fun done(strUser: String, strPwd: String, strDID: String) {
        val `in` = Intent()
        //        String strUser = userEdit.getText().toString();
//        String strPwd = pwdEdit.getText().toString();
//        String strDID = didEdit.getText().toString();
        if (strDID.length == 0) {
            Toast.makeText(activity,
                    resources.getString(R.string.input_camera_id), Toast.LENGTH_SHORT).show()
            return
        } //
        if (strUser.length == 0) {
            Toast.makeText(activity,
                    resources.getString(R.string.input_camera_user), Toast.LENGTH_SHORT).show()
            return
        }
        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA
        }
        `in`.putExtra(ContentCommon.CAMERA_OPTION, option)
        `in`.putExtra(ContentCommon.STR_CAMERA_ID, strDID)
        `in`.putExtra(ContentCommon.STR_CAMERA_USER, strUser)
        `in`.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd)
        `in`.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType)
        //        progressBar.setVisibility(View.VISIBLE);
        if (dialogUtil != null) dialogUtil!!.loadDialog()
        this.strDID = strDID
        this.strPwd = strPwd
        this.strUser = strUser
        SystemValue.deviceName = strUser
        SystemValue.deviceId = strDID
        SystemValue.devicePass = strPwd
        BridgeService.setIpcamClientInterface(this)
        NativeCaller.Init()
        Thread(StartPPPPThread()).start()
    }

    /**
     * 测试空调
     */
    private fun test_air_control(mac: String) {
        doit_wifi = "air"
        common_control("air", mac)
    }

    /**
     * 共同控制
     */
    private fun common_control(doit: String, mac: String) {
        var apple_name = ""
        for (i in list_hand_scene.indices) {
            if (list_hand_scene[i]["controllerId"] == mac) {
                apple_name = list_hand_scene[i]["name"].toString()
            }
        }
        if (wifiDevices.size != 0) {
            get_to_wifi(mac, apple_name) //绑定订阅
            to_control(doit)
        } else {
            ToastUtil.showToast(activity, "请与" + apple_name
                    +
                    "在同一网络后再控制")
        }
    }//重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen//刷新togglen数据// ----
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (dialogUtil != null) {
//                    dialogUtil.loadDialog();
//                }
//            }
//        });

    /**
     * 获取门磁等第三方设备
     */
    private val otherDevices: Unit
        private get() {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (dialogUtil != null) {
//                    dialogUtil.loadDialog();
//                }
//            }
//        });
            val mapdevice: MutableMap<String, String?> = HashMap()
            mapdevice["token"] = TokenUtil.getToken(activity)
            mapdevice["areaNumber"] = areaNumber
            MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
                otherDevices
            }, activity, null) {
                override fun onError(call: Call, e: Exception, id: Int) {
                    super.onError(call, e, id)
                }

                override fun pullDataError() {
                    super.pullDataError()
                }

                override fun emptyResult() {
                    super.emptyResult()
                }

                override fun wrongToken() {
                    super.wrongToken()
                    //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                }

                override fun wrongBoxnumber() {
                    super.wrongBoxnumber()
                }

                override fun onSuccess(user: User) {
                    list_hand_scene = ArrayList()
                    for (i in user.controllerList.indices) {
                        val mapdevice: MutableMap<String, String> = HashMap()
                        mapdevice["name"] = user.controllerList[i].name
                        mapdevice["number"] = user.controllerList[i].number
                        mapdevice["type"] = user.controllerList[i].type
                        mapdevice["controllerId"] = user.controllerList[i].controllerId
                        list_hand_scene.add(mapdevice)
                    }
                }
            })
        }

    /**
     * get_to_wifi
     *
     * @param mac
     * @param apple_name
     */
    private fun get_to_wifi(mac: String, apple_name: String) {
        mGizWifiDevice = null
        for (i in wifiDevices.indices) {
            if (wifiDevices[i].macAddress == mac) {
                mGizWifiDevice = wifiDevices[i]
            }
        }
        //  if (!Utility.isEmpty(mGizWifiDevice)) { //
        if (mGizWifiDevice != null) {
//            Object json = JSON.toJSON(mGizWifiDevice);
//            String str = new Gson().toJson(json);
            mDeviceManager!!.bindRemoteDevice(mGizWifiDevice)
            val finalMGizWifiDevice = mGizWifiDevice
            handler.postDelayed({ mDeviceManager!!.setSubscribe(finalMGizWifiDevice, true) }, 1000)
        } else {
            ToastUtil.showToast(activity, "请与" + apple_name
                    +
                    "在同一网络后再控制")
            return
        }
    }

    /**
     * 跳转到空调控制界面
     */
    private fun to_control(doit: String) {
        val mac = mapdevice["panelMac"].toString()
        val rid = mapdevice["dimmer"].toString()
        var issame = false
        list_remotecontrol_air = SharedPreferencesUtil.getInfo_List(activity, "remoteairlist")
        for (i in list_remotecontrol_air.indices) {
            if (rid == list_remotecontrol_air[i]["rid"]) {
                issame = true
                remoteControl_map_air = list_remotecontrol_air[i]
                break
            } else {
                issame = false
            }
        }
        if (issame) {
            when (doit) {
                "air" -> toControl(mapdevice)
                "tv" -> toControl_TV(mapdevice)
            }
        } else {
            when (doit) {
                "air" -> DownloadThread("getDetailByRCID", mac, rid).start() //下载该遥控器编码
                "tv" -> DownloadThread("getDetailByRCID_TV", mac, rid).start() //下载该遥控器编码
            }
        }
    }

    /**
     * 跳转到控制界面
     */
    private fun toControl(mapdevice: Map<*, *>) {
        if (mGizWifiDevice == null) {
//            ToastUtil.showToast(getActivity(), "设备，null");
            return
        }
        val intent = Intent(activity, WifiAirControlActivity::class.java)
        intent.putExtra("GizWifiDevice", mGizWifiDevice)
        intent.putExtra("mapdevice", mapdevice as Serializable)
        //remoteControl
//        intent.putExtra("remoteControl", remoteControl);
//        Map map_rcommand = remoteControl.getRcCommand();
        intent.putExtra("map_rcommand", remoteControl_map_air as Serializable)
        startActivity(intent)
    }

    /**
     * 跳转到TV 控制界面
     */
    private fun toControl_TV(mapdevice: Map<*, *>) {
        if (mGizWifiDevice == null) {

//            ToastUtil.showToast(getActivity(), "设备，null");
            return
        }
        val intent = Intent(activity, TVShowActivity::class.java)
        intent.putExtra("GizWifiDevice", mGizWifiDevice)
        intent.putExtra("mapdevice", mapdevice as Serializable)
        //remoteControl
//        intent.putExtra("remoteControl", remoteControl);
//        Map map_rcommand = remoteControl.getRcCommand();
        intent.putExtra("map_rcommand", remoteControl_map_air as Serializable)
        startActivity(intent)
    }

    internal inner class DownloadThread(private val viewId: String, private val mac: String, private val rid: String) : Thread() {
        var result = ""
        override fun run() {
            val message = mHandler.obtainMessage()
            when (viewId) {
                "getDetailByRCID" -> get_air_control_brand(message)
                "getDetailByRCID_TV" -> get_air_control_brand_tv(message)
                else -> {
                }
            }
        }

        /**
         * 获取空调编码
         *
         * @param message
         */
        private fun get_air_control_brand(message: Message) {
            if (mac != "") {
                if (ykanInterface == null) return
                activity!!.runOnUiThread { dialogUtil!!.loadDialog() }
                ykanInterface!!
                        .getRemoteDetails(mac, rid, object : YKanHttpListener {
                            override fun onSuccess(baseResult: BaseResult) {
                                if (baseResult != null) {
                                    add_remotes(baseResult as RemoteControl)
                                }
                            }

                            /**
                             * 添加遥控器
                             * @param baseResult
                             */
                            private fun add_remotes(baseResult: RemoteControl) {
                                index = 0
                                remoteControl = baseResult
                                result = remoteControl.toString()

                                //在这里保持遥控器红外码列表
                                val map = remoteControl!!.rcCommand
                                val map_send = HashMap<Any?, Any?>()
                                map_send["mac"] = mac
                                map_send["rid"] = rid
                                val set: Set<String> = map.keys
                                for (s in set) {
                                    if (map[s] == null) {
                                        continue
                                    }
                                    if (map[s]!!.srcCode == null) {
                                        continue
                                    }
                                    map_send[s] = map[s]!!.srcCode
                                }
                                list_remotecontrol_air.add(map_send)
                                remoteControl_map_air = map_send
                                activity!!.runOnUiThread {//
                                    SharedPreferencesUtil.saveInfo_List(activity, "remoteairlist", list_remotecontrol_air)
                                    dialogUtil!!.removeDialog()
                                    message.what = 1
                                    message.obj = result
                                    mHandler.sendMessage(message)
                                }
                                //下载好遥控码后
                            }

                            override fun onFail(ykError: YKError) {
                                Log.e(TAG, "ykError:$ykError")
                                handler.sendEmptyMessage(6)
                                result = "error"
                                activity!!.runOnUiThread {
                                    //                                        dialogUtil.removeDialog();
//                                        ToastUtil.showToast(getActivity(),
//                                                ykError.getError());
                                }
                            }
                        })
            } else {
                result = "请调用匹配数据接口"
                Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表")
            }
            Log.d(TAG, " getDetailByRCID result:$result")
            //            if (result.equals("")) {
//                handler.sendEmptyMessage(7);//
//            }
        }

        /**
         * 获取电视机遥控码
         *
         * @param message
         */
        private fun get_air_control_brand_tv(message: Message) {
            if (mac != "") {
                if (ykanInterface == null) return
                activity!!.runOnUiThread { dialogUtil!!.loadDialog() }
                ykanInterface!!
                        .getRemoteDetails(mac, rid, object : YKanHttpListener {
                            override fun onSuccess(baseResult: BaseResult) {
                                if (baseResult != null) {
                                    add_remotes(baseResult as RemoteControl)
                                }
                            }

                            /**
                             * 添加遥控器
                             * @param baseResult
                             */
                            private fun add_remotes(baseResult: RemoteControl) {
                                index = 0
                                remoteControl = baseResult
                                result = remoteControl.toString()
                                //                                            list_remotecontrol = new ArrayList<>();
                                //在这里保持遥控器红外码列表
                                val map = remoteControl!!.rcCommand
                                val map_send = HashMap<Any?, Any?>()
                                map_send["mac"] = mac
                                map_send["rid"] = rid
                                val set: Set<String> = map.keys
                                for (s in set) {
                                    if (map[s] == null) {
                                        continue
                                    }
                                    if (map[s]!!.srcCode == null) {
                                        continue
                                    }
                                    map_send[s] = map[s]!!.srcCode
                                }
                                list_remotecontrol_air.add(map_send)
                                remoteControl_map_air = map_send
                                activity!!.runOnUiThread {
                                    SharedPreferencesUtil.saveInfo_List(activity, "remoteairlist", list_remotecontrol_air)
                                    dialogUtil!!.removeDialog()
                                    message.what = 2
                                    message.obj = result
                                    mHandler.sendMessage(message)
                                }
                                //下载好遥控码后
                            }

                            override fun onFail(ykError: YKError) {
                                Log.e(TAG, "ykError:$ykError")
                                handler.sendEmptyMessage(6)
                                result = "error"
                                activity!!.runOnUiThread {
                                    //                                        dialogUtil.removeDialog();
//                                        ToastUtil.showToast(getActivity(),
//                                                ykError.getError());
                                }
                            }
                        })
            } else {
                result = "请调用匹配数据接口"
                Log.e(TAG, " getDetailByRCID 没有遥控器设备对象列表")
            }
            Log.d(TAG, " getDetailByRCID result:$result")
            //            if (result.equals("")) {
//                handler.sendEmptyMessage(7);//
//            }
        }
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> //                    if (!Utility.isEmpty(remoteControl)) {
//                        airVerSion = remoteControl.getVersion();
//                        codeDatas = remoteControl.getRcCommand();
//                        airEvent = getAirEvent(codeDatas);
//                    }
                    toControl(mapdevice)
                2 -> toControl_TV(mapdevice)
            }
        }
    }

    /**
     * 测试电视
     */
    private fun test_tv(mac: String) {
        doit_wifi = "tv"
        common_control("tv", mac)
    }

    /**
     * 测试pm2.5
     */
    private fun test_pm25() {
        val intent = Intent(activity, Pm25SecondActivity::class.java)
        intent.putExtra("mapdevice", mapdevice as Serializable)
        startActivity(intent)
    }

    /**
     * 门磁，水浸，人体感应，入墙PM2.5
     *
     * @param mapalldevice
     */
    private fun special_type_device(mapalldevice: Map<String, Any>) {
        val bundle = Bundle()
        bundle.putString("type", mapalldevice["type"] as String?)
        bundle.putString("name", mapalldevice["name"] as String?)
        bundle.putString("number", mapalldevice["number"] as String?)
        bundle.putString("name1", mapalldevice["name1"] as String?)
        bundle.putString("name2", mapalldevice["name2"] as String?)
        bundle.putString("panelName", mapalldevice["panelName"] as String?)
        bundle.putString("status", mapalldevice["status"] as String?)
        bundle.putString("dimmer", mapalldevice["dimmer"] as String?)
        bundle.putString("mode", mapalldevice["mode"] as String?)
        //        IntentUtil.startActivity(getActivity(), MacdetailActivity.class, bundle);
    }

    /**
     * curtains and light,窗帘与灯
     *
     * @param position
     * @param mapdevice1
     */
    private fun curtains_and_light(position: Int, mapdevice1: Map<String, Any?>) {
        if (list[position]["type"].toString() == "111" ||
                list[position]["type"].toString() == "1" ||
                list[position]["type"].toString() == "11" ||
                list[position]["type"].toString() == "16" ||
                list[position]["type"].toString() == "15" ||
                list[position]["type"].toString() == "17" ||
                list[position]["type"].toString() == "100" ||
                list[position]["type"].toString() == "114"
        ) {//114
            when (list[position]["type"].toString()) {
                "100" -> sraum_manualSceneControl(list[position]["number"].toString())
                else -> sraum_device_control(list[position]["type"].toString(), position, mapdevice1)
            }
        } else {
            /*窗帘所需要的属性值*/
            Log.e("zhu", "chuanglian:" + "窗帘所需要的属性值")
            val bundle = Bundle()
            bundle.putString("type", list[position]["type"].toString())
            bundle.putString("number", list[position]["number"].toString())
            bundle.putString("name1", list[position]["name1"].toString())
            bundle.putString("name2", list[position]["name2"].toString())
            bundle.putString("name", list[position]["name"].toString())
            bundle.putString("status", list[position]["status"].toString())
            bundle.putString("boxNumber",
                    if (list[position]["boxNumber"] == null) "" else list[position]["boxNumber"].toString())
            bundle.putString("areaNumber", areaNumber)
            bundle.putString("roomNumber", current_room_number)
            bundle.putSerializable("mapalldevice", mapdevice1 as Serializable)
            LogUtil.eLength("名字", list[position]["name1"].toString() + list[position]["name2"].toString())
            when (list[position]["type"].toString()) {
                "2", "112" -> IntentUtil.startActivity(activity, TiaoGuangLightActivity::class.java, bundle)
                "4", "18", "113" -> IntentUtil.startActivity(activity, CurtainWindowActivity::class.java, bundle)
                "19", "20", "21" -> IntentUtil.startActivity(activity, UpDownLeftRightActivity::class.java, bundle)
                "3", "5", "6" -> IntentUtil.startActivity(activity, AirControlActivity::class.java, bundle)
                "10", "102", "23","115" -> IntentUtil.startActivity(activity, Pm25SecondActivity::class.java, bundle)
                "22" -> {//音乐面板
                    IntentUtil.startActivity(activity, MusicPanelActivity::class.java, bundle)
                }
            }
        }
    }

    /**
     * 控制手动场景
     */
    private fun sraum_manualSceneControl(sceneId: String) {
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(activity)
        val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        map["sceneId"] = sceneId
        MyOkHttp.postMapObject(ApiHelper.sraum_manualSceneControl, map as Map<String, Any>, object : Mycallback(AddTogglenInterfacer { sraum_manualSceneControl(sceneId) }, activity, dialogUtil) {
            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(activity, "控制失败")
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(activity, "sceneId 错误")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                // ToastUtil.showToast(activity, "操作成功")
                if (vibflag) {
                    val vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    LogUtil.i("铃声响起")
                    MusicUtil.startMusic(activity, 1, "")
                } else {
                    MusicUtil.stopMusic(activity, "")
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * sraum_device_control
     *
     * @param
     * @param mapdevice1
     */
    private fun sraum_device_control(type: String?, position: Int, mapdevice1: Map<String, Any?>) {
        val mapalldevice = HashMap<String, Any?>()
        val listobj: MutableList<Map<*, *>> = ArrayList()
        val map_listobj = HashMap<String, Any?>()
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

        mapalldevice["token"] = TokenUtil.getToken(activity)
        mapalldevice["areaNumber"] = areaNumber
        when (type) {
            "111", "114" -> {
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


        MyOkHttp.postMapObject(api, mapalldevice, object : Mycallback(AddTogglenInterfacer { sraum_device_control(type, position, mapdevice1) }, activity, dialogUtil) {
            override fun fourCode() {
                super.fourCode()
                when (listob[0]["type"].toString()) {
                    "11" -> ToastUtil.showToast(activity, "恢复失败")
                    else -> ToastUtil.showToast(activity, "操作失败")
                }
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                when (listob[0]["type"].toString()) {
                    "11" -> {
                    } //ToastUtil.showToast(activity, "恢复成功")
                    else -> {
                    } //ToastUtil.showToast(activity, "操作成功")
                }
                if (vibflag) {
                    val vibrator = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    LogUtil.i("铃声响起")
                    MusicUtil.startMusic(activity, 1, "")
                } else {
                    MusicUtil.stopMusic(activity, "")
                }
                listtype[position] = status

                for (i in list.indices) {
                    if (i == position) {
                        (list[i] as HashMap<String, Any>)["status"] = map["status"] as String
                        //展示首页设备列表
                        handler_laungh.sendEmptyMessage(5)
                        break
                    }
                }


//                when(map["type"]) {
//                    "111"-> {//wifi灯控
//
//
//                    }
//                    else -> {
//                        if (current_room_number != null) //获取当前房间下的设备
//                            Thread(Runnable {
//                                sraum_getOneRoomInfo(current_room_number)
//                            }).start()
//                    }
//                }

            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private var tag = 0
    override fun onDestroy() {
        activity!!.unregisterReceiver(mMessageReceiver)
        activity!!.unregisterReceiver(receiver)
        NativeCaller.Free()
        val intent = Intent()
        intent.setClass(activity!!, BridgeService::class.java)
        activity!!.stopService(intent)
        tag = 0
        EventBus.getDefault().unregister(this)
        //        over_camera_list();
        camera_init_data = false


        super.onDestroy()
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(ACTION_INTENT_RECEIVER)
        filter.addAction(ACTION_INTENT_CAMERA_RECEIVER)
        filter.addAction(MainGateWayActivity.MESSAGE_TONGZHI)
        filter.addAction(MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE)
        filter.addAction(ACTION_INTENT_RECEIVER_TABLE_PM)
        filter.addAction(ACTION_INTENT_RECEIVER_ROOM_NUMBER)
        filter.addAction(ACTION_INTENT_CAMERA_RECOMING)
        activity!!.registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == ACTION_INTENT_RECEIVER) {
                val messflag = intent.getStringExtra("notifactionId")
                if (messflag == "1" || messflag == "3" || messflag == "4" || messflag == "5"
                        || messflag == "101") {
//                upload(false);//控制部分，推送刷新；主动推送刷新。
                    Log.e("zhu", "upload(false):upload(false)messflag:$messflag")
                    if (current_room_number != null) {
                        Thread(Runnable { sraum_getOneRoomInfo(current_room_number) }).start()
                    }
                    //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                    val panelid = intent.getStringExtra("panelid")
                    val gateway_number = intent.getStringExtra("gatewayid")
                    sendBroad(panelid!!, gateway_number!!)
                    //推送过来的
//                    ToastUtil.showToast(getActivity(), "我控制的设备时推送过来的" + ",messflag:" + messflag);
                }
            } else if (intent.action == MainGateWayActivity.MESSAGE_TONGZHI) { //门铃视频预览
                type = intent.getSerializableExtra("type") as String
                val mapdevice = HashMap<Any?, Any?>()
                mapdevice["dimmer"] = intent.getSerializableExtra("uid") as String
                mapdevice["temperature"] = "888888"
                mapdevice["mode"] = "admin" //
                when (type) {
                    "52", "53" -> door_rill(mapdevice)
                }
            } else if (intent.action == MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE) { //来自设备页获取摄像头状态的通知
                videofrom = "devicefragment"
                video_item = intent.getSerializableExtra("video_item") as HashMap<Any?, Any?>
                common_video(video_item as HashMap<String, Any>)
            } else if (intent.action == ACTION_INTENT_RECEIVER_TABLE_PM) { //桌面PM2.5接收receiver
                //
                show_table_pm(intent)
            } else if (intent.action == ACTION_INTENT_RECEIVER_ROOM_NUMBER) { //桌面PM2.5接收receiver
                current_room_number = intent.getSerializableExtra("roomNumber") as String
            } else if (intent.action == ACTION_INTENT_CAMERA_RECEIVER) { //
                // init_wifi_camera()
            } else if (intent.action == ACTION_INTENT_CAMERA_RECOMING) {
                var std = intent.getStringExtra("std")
                init_Camera(std!!, 0)
                common_video(mapdevice as HashMap<String, Any>)
            }
        }
    }


    /**
     * 展示桌面PM2.5
     *
     * @param intent
     */
    private fun show_table_pm(intent: Intent) {
        val extraJson: JSONObject
        val extras = intent.getStringExtra("extras")
        if (!ExampleUtil.isEmpty(extras)) {
            try {
                extraJson = JSONObject(extras)
                val id = extraJson.getString("id")
                val pm2_5 = extraJson.getString("pm2.5")
                for (i in list.indices) {
                    if (list[i]["number"].toString() == id) {
                        (list[i] as HashMap<String, Any>)["dimmer"] = pm2_5
                        break
                    }
                }
                display_home_device_list()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 门禁报警
     *
     * @param mapdevice
     */
    private fun door_rill(mapdevice: Map<*, *>) {
        videofrom = "macfragment"
        onitem_wifi_shexiangtou(mapdevice as Map<String, Any?>)
    }

    private fun sendBroad(panelid: String, gateway_number: String) {
        val mIntent = Intent(ACTION_INTENT_RECEIVER_TO_SECOND_PAGE)
        mIntent.putExtra("panelid", panelid)
        mIntent.putExtra("gatewayid", gateway_number)
        if (activity != null)
            activity!!.sendBroadcast(mIntent)
    }

    private fun sendBroad_pm25(map: Map<*, *>) {
        val mIntent_pm25 = Intent(MACFRAGMENT_PM25)
        mIntent_pm25.putExtra("mapdevice", mapdevice as Serializable)
        activity!!.sendBroadcast(mIntent_pm25)
    }

    /**
     * 获取单个房间关联信息（APP->网关）
     *
     * @param number
     */
    private fun sraum_getOneRoomInfo(number: String?) {
        val map = HashMap<Any?, Any?>()
        val areaNumber = current_area_map!!["number"] as String?
        map["areaNumber"] = areaNumber
        map["roomNumber"] = number
        map["token"] = TokenUtil.getToken(activity)
        //        boolean isnewProcess = (boolean) SharedPreferencesUtil.getData(getActivity(), "newProcess", false);
//        if (isnewProcess) {
//            SharedPreferencesUtil.saveData(getActivity(), "newProcess", false);
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (dialogUtil != null) {
//                        dialogUtil.loadDialog();
//                    }
//                }
//            });
//        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, map as MutableMap<String, String>?, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getOneRoomInfo(number)
        }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {//
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {//
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                deviceCount = user.count
                list = ArrayList()
                listtype.clear()
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

                if (!camera_init_data) {//首次初始化
                    init_camera_data(0)
                    camera_init_data = true
                } else {//更新
                    init_camera_data(1)
                }

                if (list.size != 0) {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "list_old", list as List<Map<String, Any>>)
                } else {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "list_old", ArrayList())
                }
                if (list.size != 0) {
                    for (map in list) {
                        listtype.add(if (map["status"] == null) "1" else map["status"].toString())
                    }
                }


                //展示首页设备列表
                handler_laungh.sendEmptyMessage(5)
            }
        })
    }

    var list_second_total: MutableList<Map<*, *>> = ArrayList()
    private fun init_camera_data(i: Int) {
//        val list_second: MutableList<Map<*, *>> = ArrayList()
        list_second_total = SharedPreferencesUtil.getInfo_List(activity, "list_second_total")
        for (i in list.indices) {
            when (list[i]["type"]) {
                "101", "102" -> {
                    var ishas_val = false
                    if (list_second_total != null && list_second_total.size != 0)
                        for (j in list_second_total.indices) {
                            if (list_second_total[j]["did"]!!.equals(list[i]["dimmer"])) {
                                ishas_val = true
                                break
                            }
                        }
                    if (!ishas_val) {
                        val map = HashMap<Any?, Any?>()
                        map["did"] = list[i]["dimmer"]
                        map["tag"] = 0
                        list_second_total.add(map)
                    }
                }
            }
        }

        when (i) {
            1 -> {
                val list_wifi_camera = SharedPreferencesUtil.getInfo_List(activity, "list_wifi_camera_first")
                for (j in list_second_total.indices) {
                    for (i in list_wifi_camera.indices) {
                        if (list_second_total[j]["did"]!!.equals(list_wifi_camera[i]["did"])) {
                            (list_second_total[j] as HashMap<String, Any>).put("tag", list_wifi_camera[i]["tag"] as Int)
                        }
                    }
                }
            }
        }

        SharedPreferencesUtil.saveInfo_List(activity, "list_wifi_camera_first", list_second_total)
        SharedPreferencesUtil.saveInfo_List(activity, "list_second_total", list_second_total)
        Log.e("list_camera", JSON.toJSONString(list_second_total))
    }


    /**
     * 展示首页设备列表
     */
    private fun display_home_device_list() {
        deviceListAdapter!!.setList(list as List<Map<String, Any>>)
        deviceListAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.area_name_txt -> {
            }
            R.id.add_device -> startActivity(Intent(activity, SelectZigbeeDeviceActivityNew::class.java))
            R.id.common_setting_linear -> when (deivce_type) {
                "100" -> {
                    ToastUtil.showToast(activity, "场景不能进行移动")
                    dialogUtil!!.removeviewBottomDialog()
                }
                else -> switch_room_dialog(activity)
            }
            R.id.rename_scene_linear -> when (authType) {
                "1" -> showRenameDialog()
                "2" -> {
                    ToastUtil.showToast(activity, "成员不能重命名")
                    dialogUtil!!.removeviewBottomDialog()
                }
            }
            R.id.delete_scene_linear -> showCenterDeleteDialog()
            R.id.cancel_scene_linear -> dialogUtil!!.removeviewBottomDialog()
            R.id.back_rel -> startActivity(Intent(activity, MessageActivity::class.java))
            R.id.all_room_rel -> {

            }
            R.id.area_rel -> if (areaList.size > 1) //区域列表大于1时，显示
                showPopWindow()
        }
    }

    /**
     * 切换房间获取map
     *
     * @return
     */
    private fun getmap_chage_room(): Map<*, *> {
        val map = HashMap<Any?, Any?>()

//                current_area_map = areaList.get(position);
//                sraum_changeArea(areaList.get(position).get("areaNumber").toString());
        if (current_area_map!!["areaNumber"] != null) {
            val areaNumber = current_area_map!!["areaNumber"].toString()
            if (areaNumber != null) map["areaNumber"] = areaNumber
        }
        if (deivce_number != null) map["number"] = deivce_number
        if (device_type != null) map["type"] = device_type
        return map
    }

    //自定义dialog,centerDialog
    fun showCenterDeleteDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(activity!!, R.style.BottomDialog)
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
        cancel.setOnClickListener { dialog.dismiss() }
        confirm.setOnClickListener { dialog.dismiss() }
    }

    //自定义dialog,自定义重命名dialog
    fun showRenameDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.editscene_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        // tv_title = view.findViewById<View>(R.id.tv_title) as TextView
        val edit_password_gateway = view.findViewById<View>(R.id.edit_password_gateway) as ClearLengthEditText
        //        tv_title.setText("是否拨打119");
        edit_password_gateway.setText(device_name)
        //        content.setText(message);
        //显示数据
        val dialog = Dialog(activity!!, R.style.BottomDialog)
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
        cancel.setOnClickListener(View.OnClickListener {
            if (edit_password_gateway.text.toString() == null || edit_password_gateway.text.toString().trim { it <= ' ' } == "") {
                ToastUtil.showToast(activity, "按钮名称为空")
                return@OnClickListener
            }
            val isexist = false
            val `var` = edit_password_gateway.text.toString()
            if (device_name == `var`) {
            } else {
                var method = ""
                when (deivce_type) {
                    "101", "103", "104" -> {
                        method = ApiHelper.sraum_updateWifiCameraName
                        sraum_updateWifiAppleName(deivce_number, `var`, method)
                    }
                    "202", "206" -> {
                        method = ApiHelper.sraum_updateWifiAppleDeviceName
                        sraum_updateWifiAppleName(deivce_number, `var`, method)
                    }
                    "111", "112", "113", "114" -> {
                        method = ApiHelper.sraum_updateWifiButtonName
                        sraum_update_s(`var`, null, null, deivce_number, deivce_type, null, method)
                    }
                    else -> {
                        method = ApiHelper.sraum_updateButtonName
                        sraum_update_s(`var`, device_name1, device_name2, deivce_number, deivce_type, device_gatewayNumber, method)
                    }
                }
                dialog.dismiss()
            }
        })
        confirm.setOnClickListener { dialog.dismiss() }
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private fun sraum_updateWifiAppleName(number: String?, newName: String, method: String) {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(activity)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["number"] = number
        mapdevice["name"] = newName
        MyOkHttp.postMapString(method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_updateWifiAppleName(number, newName, method)
        }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(activity, "更新失败")
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun fourCode() {
                ToastUtil.showToast(activity, "name 已存在")
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(activity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                ToastUtil.showToast(activity, "number 不存在")
            }

            override fun onSuccess(user: User) {
                Thread(Runnable { if (current_room_number != null) sraum_getOneRoomInfo(current_room_number) }).start()
                dialogUtil!!.removeviewBottomDialog()
            }
        })
    }

    /**
     * 更新按钮名称
     *
     * @param customName
     * @param name1
     * @param name2
     * @param deviceNumber
     * @param type
     * @param boxNumber
     */
    private fun sraum_update_s(customName: String, name1: String?, name2: String?, deviceNumber: String?, type: String?,
                               boxNumber: String?, method: String) {
        val map: MutableMap<String, Any?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        map["token"] = TokenUtil.getToken(activity)
        map["areaNumber"] = areaNumber
        if (boxNumber != null) {
            map["gatewayNumber"] = boxNumber
            map["deviceNumber"] = deviceNumber
            map["newName"] = customName
            if (type == "4") {
                map["newName1"] = name1
                map["newName2"] = name2
            }
        } else {
            map["buttonNumber"] = deviceNumber
            map["newName"] = customName
        }
        MyOkHttp.postMapObject(method, map, object : Mycallback(AddTogglenInterfacer { sraum_update_s(customName, name1, name2, deviceNumber, type, boxNumber, method) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(activity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                ToastUtil.showToast(activity, "gatewayNumber 不存在")
            }

            override fun fourCode() {
                ToastUtil.showToast(activity, "deviceNumber 不存在")
            }

            override fun onSuccess(user: User) {
                Thread(Runnable { if (current_room_number != null) sraum_getOneRoomInfo(current_room_number) }).start()
                dialogUtil!!.removeviewBottomDialog()
            }

            override fun fiveCode() {
                ToastUtil.showToast(activity, """
     type
     类型不存在
     """.trimIndent())
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }


    /**
     * 获取所有区域
     */
    private fun sraum_getAllArea() {
        val mapdevice: MutableMap<String, String> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(activity)
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getAllArea()
        }, activity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                areaList = ArrayList()
                for (i in user.areaList.indices) {
                    val mapdevice = HashMap<String, String>()
                    when (user.areaList[i].authType) {
                        "1" -> mapdevice["name"] = user.areaList[i].areaName + "(" + "业主" + ")"
                        "2" -> mapdevice["name"] = user.areaList[i].areaName + "(" + "成员" + ")"
                    }
                    //
//                            mapdevice.put("name", user.areaList.get(i).areaName);
                    mapdevice["number"] = user.areaList[i].number
                    mapdevice["sign"] = user.areaList[i].sign
                    mapdevice["authType"] = user.areaList[i].authType
                    mapdevice["roomCount"] = user.areaList[i].roomCount
                    areaList.add(mapdevice)
                }
                if (areaList.size != 0) {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList_old", areaList
                    )
                } else {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList_old", ArrayList())
                }
                if (user.areaList != null && user.areaList.size != 0) { //区域命名
                    for (map in areaList) {
                        if ("1" == map["sign"].toString()) {
                            current_area_map = map
                            when (map["authType"].toString()) {
                                "1" -> area_name_txt!!.text = current_area_map.get("name").toString()
                                "2" -> area_name_txt!!.text = current_area_map.get("name").toString()
                            }
                            qiehuan = ""
                            Thread(Runnable { sraum_getRoomsInfo(current_area_map.get("number").toString(), qiehuan!!) }).start()
                            areaNumber = current_area_map.get("number").toString()
                            authType = current_area_map.get("authType").toString() //（1 业主 2 成员）
                            SharedPreferencesUtil.saveData(activity, "areaNumber", areaNumber)
                            SharedPreferencesUtil.saveData(activity, "areaName",
                                    current_area_map.get("name").toString())
                            SharedPreferencesUtil.saveData(activity, "authType", authType)
                            when (authType) {
                                "1" -> add_device!!.visibility = View.VISIBLE
                                "2" -> add_device!!.visibility = View.GONE
                            }
                            break
                        }
                    }
                }
            }
        })
    }

    /**
     * 切换区域
     */
    private fun sraum_changeArea(areaNumber: String, authType: String,
                                 wv: ListViewAdaptWidth, areaListAdapter: AreaListAdapter) {
//        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }
        val mapdevice: MutableMap<String, String> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(activity)
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_changeArea, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_changeArea(areaNumber, authType, wv, areaListAdapter)
        }, activity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
//                        ToastUtil.showToast(getActivity(), "区域切换成功");
                //qie huan cheng gong ,获取区域的所有房间信息
                SharedPreferencesUtil.saveData(activity, "areaNumber", areaNumber)
                for (i in areaList.indices) {
                    if (areaList[i]["number"] == areaNumber) {
                        (areaList[i] as HashMap<String, Any>)["sign"] = "1"
                        SharedPreferencesUtil.saveData(activity, "areaName", areaList[i]["name"])
                    } else {
                        (areaList[i] as HashMap<String, Any>)["sign"] = "0"
                    }
                }
                SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList_old", areaList)
                SharedPreferencesUtil.saveData(activity, "authType", authType)
                handler_laungh.sendEmptyMessage(6)
            }

            override fun threeCode() {
                ToastUtil.showToast(activity, """
     areaNumber 不
     正确
     """.trimIndent())
            }
        })
    }

    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     * @param qiehuan
     */
    private fun sraum_getRoomsInfo(areaNumber: String?, qiehuan: String) {
//        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(activity)
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getRoomsInfo(areaNumber, qiehuan)
        }, activity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(activity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                //qie huan cheng gong ,获取区域的所有房间信息
                roomList = ArrayList()
                for (i in user.roomList.indices) {
                    val mapdevice = HashMap<Any, Any>()
                    mapdevice["name"] = user.roomList[i].name
                    mapdevice["number"] = user.roomList[i].number
                    mapdevice["count"] = user.roomList[i].count
                    mapdevice["room_index"] = i
                    roomList.add(mapdevice)
                }
                if (roomList.size != 0) {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList_old", roomList)
                } else {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList_old", ArrayList())
                }
                handler_laungh.sendEmptyMessage(4)
            }

            override fun threeCode() {
                ToastUtil.showToast(activity, """
     areaNumber 不
     正确
     """.trimIndent())
            }
        })
    }

    /**
     * //去显示房间列表
     */
    private fun display_room_list(position: Int) {


//        homeDeviceListAdapter.setList1(roomList);
//        homeDeviceListAdapter.notifyDataSetChanged();
//        for (int i = 0; i < roomList.size(); i++) {
//            if (i == position) {
//                HomeDeviceListAdapter.getIsSelected().put(i, true);
//            } else {
//                HomeDeviceListAdapter.getIsSelected().put(i, false);
//            }
//        }
//        homeDeviceListAdapter.notifyDataSetChanged();
        if (position > roomList.size || roomList.size == 0) {
            return
        }

//        Collections.addAll(titles, "123", "456", "789");
        tablayout_vertical!!.setupWithViewPager(viewpager)
        tablayout_vertical!!.setTabSelected(position, "refresh")
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopWindow() {
        try {
            val view = LayoutInflater.from(activity).inflate(
                    R.layout.popup_listview_window, null)
            val wv = view.findViewById<View>(R.id.wheel_view_wv) as ListViewAdaptWidth
            val areaListAdapter = AreaListAdapter(activity, areaList)
            wv.adapter = areaListAdapter
            wv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> //xuan zhong mou ge quyu
                current_area_map = areaList[position]
                areaNumber = areaList[position]["number"].toString()
                authType = areaList[position]["authType"].toString()
                Thread(Runnable {
                    sraum_changeArea(areaList[position]["number"].toString(),
                            areaList[position]["authType"].toString(), wv, areaListAdapter)
                }).start()
            }
            pop_set(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * popWindow  canshu peizhi
     *
     * @param view
     */
    @SuppressLint("WrongConstant")
    private fun pop_set(view: View) { //
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//水平
        // 初始化自定义的适配器
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        popupWindow = PopupWindow(view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true) //高度写死

//            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow!!.isFocusable = true
        popupWindow!!.isOutsideTouchable = true
        val cd = ColorDrawable(0x00ffffff) // 背景颜色全透明
        popupWindow!!.setBackgroundDrawable(cd)
        val location = IntArray(2)
        area_rel!!.getLocationOnScreen(location) //获得textview的location位置信息，绝对位置
        popupWindow!!.animationStyle = R.style.style_pop_animation // 动画效果必须放在showAsDropDown()方法上边，否则无效
        backgroundAlpha(1.0f) // 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
        // 将pixels转为dip
//            int xoffInDip = pxTodip(displayWidth) / 2;
        popupWindow!!.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xPos = (displayWidth - popupWindow!!.contentView.measuredWidth) / 2
        //            popupWindow.showAsDropDown(project_select, 0, dip2px(getActivity(), 10));
        popupWindow!!.showAtLocation(area_rel, Gravity.NO_GRAVITY, xPos, location[1] + area_rel!!.height
        )
        popupWindow!!.setOnDismissListener {
            popupWindow = null // 当点击屏幕时，使popupWindow消失
            backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
        }
    }

    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = activity!!.window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        activity!!.window.attributes = lp
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        //ho
//        roomList.get(position).put("isselect",true);
        for (i in roomList.indices) {
            if (i == position) {
                HomeDeviceListAdapter.getIsSelected()[i] = true
            } else {
                HomeDeviceListAdapter.getIsSelected()[i] = false
            }
        }
        homeDeviceListAdapter!!.notifyDataSetChanged()
        //编写点击房间去显示该房间下的设备列表
        if (position > roomList.size) {
            return
        }
        current_room_number = roomList[position]["number"].toString()
        Thread(Runnable { sraum_getOneRoomInfo(current_room_number) }).start()
    }

    var stringbuffer = StringBuffer()
    private var connection_wifi_camera_index = 0
    private val PPPPMsgHandler: Handler = object : Handler() {
        //
        override fun handleMessage(msg: Message) {
            val bd = msg.data
            val msgParam = bd.getInt(STR_MSG_PARAM)
            //        bd.putString(STR_DID, did);
//            String  did = bd.getString(STR_DID);
            val msgType = msg.what
            Log.i("aaa", "====$msgType--msgParam:$msgParam")
            val did = bd.getString(STR_DID)
            when (msgType) {
                ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS -> {
                    common_camera_status(msgParam, did)
                }
                ContentCommon.PPPP_MSG_TYPE_PPPP_MODE -> {//
                }
                ContentCommon.PPPP_MSG_VSNET_NOTIFY_TYPE_VUIDSTATUS -> {
                    common_camera_status(msgParam, did)
                }
            }
        }
    }

    private fun common_camera_status(msgParam: Int, did: String?) {
        val resid: Int
        when (msgParam) {
            ContentCommon.PPPP_STATUS_CONNECTING -> {
                resid = R.string.pppp_status_connecting
                Log.e("PPPP->", "resid:" + "正在连接")
                if (stringbuffer.toString().contains("未知状态,")) {
                    if (!stringbuffer.toString().contains("未知状态,正在连接"))
                        stringbuffer.append("正在连接")
                }
                tag = 2
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_CONNECT_FAILED -> {
                resid = R.string.pppp_status_connect_failed
                Log.e("PPPP->", "resid:" + "连接失败")
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
                connection_wifi_camera_index++
                if (connection_wifi_camera_index <= 10) handler.sendEmptyMessage(11)
            }
            ContentCommon.PPPP_STATUS_DISCONNECT -> {
                resid = R.string.pppp_status_disconnect
                Log.e("PPPP->", "resid:" + "断线")
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_INITIALING -> {
                resid = R.string.pppp_status_initialing
                Log.e("PPPP->", "resid:" + "已连接吗，正在初始化")
                tag = 2
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_INVALID_ID -> {
                resid = R.string.pppp_status_invalid_id
                Log.e("PPPP->", "resid:" + "ID号无效")
                //                            progressBar.setVisibility(View.GONE);
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_ON_LINE -> {
                resid = R.string.pppp_status_online
                Log.e("PPPP->", "resid:" + "在线")
                connection_wifi_camera_index = 0
                if (stringbuffer.toString().contains("未知状态,正在连接")) {
                    if (!stringbuffer.toString().contains("未知状态,正在连接,在线"))
                        stringbuffer.append(",在线")
                }
                //摄像机在线之后读取摄像机类型
                val cmd = ("get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                        + "&user=admin&pwd=" + SystemValue.devicePass)
                NativeCaller.TransferMessage(did, cmd, 1)
                tag = 1
                if (dialogUtil != null) dialogUtil!!.removeDialog()
                handler.sendEmptyMessage(10) //设备已经在线了
            }
            ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE -> {//通知记录，
                resid = R.string.device_not_on_line
                Log.e("PPPP->", "resid:" + "摄像机不在线")
                //                            ToastUtil.showToast(getActivity(),"摄像不在线");
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT -> {
                resid = R.string.pppp_status_connect_timeout
                Log.e("PPPP->", "resid:" + "连接超时")
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            ContentCommon.PPPP_STATUS_CONNECT_ERRER -> {
                resid = R.string.pppp_status_pwd_error
                Log.e("PPPP->", "resid:" + "错误密码")
                tag = 0
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
            else -> {
                resid = R.string.pppp_status_unknown
                Log.e("PPPP->", "resid:" + "未知状态")
                stringbuffer = StringBuffer()
                stringbuffer.append("未知状态,")
                if (dialogUtil != null) dialogUtil!!.removeDialog()
            }
        }

        Log.e("list_camera", "did:" + SystemValue.deviceId + ",tag:" + tag)
        // init_Camera(did!!)
        if (SystemValue.deviceId != null)
            init_Camera(SystemValue.deviceId, tag)

        if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
            NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS)
            NativeCaller.TransferMessage(did,
                    "get_factory_param.cgi?loginuse=admin&loginpas="
                            + SystemValue.devicePass + "&user=admin&pwd=" + SystemValue.devicePass, 1) // 检测push值
        }
        if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
            NativeCaller.StopPPPP(did)
        }
    }

    /**
     * 初始化摄像头列表
     *
     * @param did
     */
    private fun init_Camera(did: String, tag: Int) { //修改并完善如果Id相同就更新，没有该Id就添加
        val list_wifi_camera = SharedPreferencesUtil.getInfo_List(activity, "list_wifi_camera_first")
        val map = HashMap<Any?, Any?>()
        map["did"] = did
        map["tag"] = tag
        //  ToastUtil.showToast(activity, "tag: " + tag)
        /* if(tag = 2) {
             ToastUtil.showToast(activity, "正在加载中" + tag)
         }*/
        //var is_has = false
        for (i in list_wifi_camera.indices) {
            if (list_wifi_camera[i]["did"] == did) {
                list_wifi_camera[i]["tag"] = tag
                //   is_has = true
                break
            }
        }
//        if (!is_has) {
//            list_wifi_camera.add(map)
//        }

//        if (index == list_wifi_camera.size()) {
//            list_wifi_camera.add(map);
//        }

//        Map<Integer, Integer> item = list_wifi_camera.get(position);
//        int itemplan = item.entrySet().iterator().next().getValue();
//        int itemplanKey = item.entrySet().iterator().next().getKey();
        SharedPreferencesUtil.saveInfo_List(activity, "list_wifi_camera_first", list_wifi_camera)
    }

    override fun BSMsgNotifyData(did: String, type: Int, param: Int) {
        Log.d("ip", "type:$type param:$param")
        val bd = Bundle()
        val msg = PPPPMsgHandler.obtainMessage()
        msg.what = type
        bd.putInt(STR_MSG_PARAM, param)
        bd.putString(STR_DID, did)
        msg.data = bd
        PPPPMsgHandler.sendMessage(msg)
        if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            intentbrod!!.putExtra("ifdrop", param)
            activity!!.sendBroadcast(intentbrod)
        }
    }

    override fun BSSnapshotNotify(did: String, bImage: ByteArray, len: Int) {
        // TODO Auto-generated method stub
        Log.i("ip", "BSSnapshotNotify---len$len")
    }

    override fun callBackUserParams(did: String, user1: String, pwd1: String,
                                    user2: String, pwd2: String, user3: String, pwd3: String) {
        // TODO Auto-generated method stub
    }

    override fun CameraStatus(did: String, status: Int) {

    }
    override fun CallBackGetStatus(did: String, resultPbuf: String, cmd: Int) {
        // TODO Auto-generated method stub
        if (cmd == ContentCommon.CGI_IEGET_STATUS) {
            val cameraType = spitValue(resultPbuf, "upnp_status=")
            val intType = cameraType.toInt()
            var type14 = (intType shr 16) and 1 // 14位 来判断是否报警联动摄像机
            if (intType == 2147483647) { // 特殊值
                type14 = 0
            }
            if (type14 == 1) {

            }
        }
    }

    private fun spitValue(name: String, tag: String): String {
        val strs = name.split(";".toRegex()).toTypedArray()
        for (i in strs.indices) {
            var str1 = strs[i].trim { it <= ' ' }
            if (str1.startsWith("var")) {
                str1 = str1.substring(4, str1.length)
            }
            if (str1.startsWith(tag)) {
                return str1.substring(str1.indexOf("=") + 1)
            }
        }
        return (-1).toString() + ""
    }

    /**
     * 从网上加载并保存到本地
     *
     * @param onclick
     */
    var isok = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (intfirst_time == 1) {
                intfirst_time = 2
            } else {
                Thread(Runnable { sraum_getAllArea() }).start()
                Thread(Runnable { otherDevices }).start()
            }
        }
    }

    companion object {
        private const val TAG = "robin debug"
        var ACTION_INTENT_RECEIVER_TO_SECOND_PAGE = "com.massky.secondpage.treceiver"
        var ACTION_INTENT_RECEIVER_TABLE_PM = "com.massky.receiver.table.pm"
        const val MESSAGE_TONGZHI_DOOR_FIRST = "com.fragment.massky.message.tongzhi.door.first"

        @JvmField
        var ACTION_INTENT_RECEIVER_ROOM_NUMBER = "com.massky.receiver.room.number"

        @JvmField
        var ACTION_INTENT_RECEIVER = "com.massky.jr.treceiver"

        @JvmField
        var ACTION_INTENT_CAMERA_RECEIVER = "com.massky.camera.receiver"

        //recoming.camera
        @JvmField
        var ACTION_INTENT_CAMERA_RECOMING = "recoming.camera"

        const val MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE = "com.sraum.massky.from.mydevice"
        private const val SEARCH_TIME = 3000
        private const val STR_DID = "did"
        private const val STR_MSG_PARAM = "msgparam"
        var MACFRAGMENT_PM25 = "com.fragment.pm25"
        fun int2ip(ipInt: Long): String {
            val sb = StringBuilder()
            sb.append(ipInt and 0xFF).append(".")
            sb.append(ipInt shr 8 and 0xFF).append(".")
            sb.append(ipInt shr 16 and 0xFF).append(".")
            sb.append(ipInt shr 24 and 0xFF)
            return sb.toString()
        }

        @JvmStatic
        fun newInstance(): HomeFragmentNew {
            val newFragment = HomeFragmentNew()
            val bundle = Bundle()
            newFragment.arguments = bundle
            return newFragment
        }
    }


}