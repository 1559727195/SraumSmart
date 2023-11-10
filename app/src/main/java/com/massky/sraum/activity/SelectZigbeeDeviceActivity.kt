package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.SelectDevTypeAdapter
import com.massky.sraum.adapter.SelectWifiDevAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.ConfigDialogFragment
import com.massky.sraum.fragment.GatewayDialogFragment
import com.massky.sraum.fragment.GatewayDialogFragment.Companion.newInstance
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.tool.Constants
import com.massky.sraum.widget.ListViewForScrollView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yaokan.sdk.utils.Logger
import com.yaokan.sdk.utils.Utility
import com.yaokan.sdk.wifi.DeviceManager
import com.yaokan.sdk.wifi.GizWifiCallBack
import okhttp3.Call
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/3.
 */
class SelectZigbeeDeviceActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.mineRoom_list)
    var macfragritview_id: ListViewForScrollView? = null

    @JvmField
    @BindView(R.id.mac_wifi_dev_id)
    var mac_wifi_dev_id: ListViewForScrollView? = null
    private var adapter_wifi: SelectWifiDevAdapter? = null
    private var gatewayList: MutableList<Map<*, *>> = ArrayList()
    private val icon = intArrayOf(
            R.drawable.icon_type_yijiandk_90, R.drawable.icon_type_liangjiandk_90,
            R.drawable.icon_type_sanjiandk_90, R.drawable.icon_type_sijiandk_90, R.drawable.icon_type_yilutiaoguang_90,
            R.drawable.icon_type_lianglutiaoguang_90, R.drawable.icon_type_sanlutiaoguang_90, R.drawable.icon_type_chuanglianmb_90,
            R.drawable.icon_type_kongtiaomb_90,
            R.drawable.icon_type_menci_90, R.drawable.icon_type_rentiganying_90,
            R.drawable.icon_type_toa_90, R.drawable.icon_type_yanwucgq_90, R.drawable.icon_type_tianranqibjq_90,
            R.drawable.icon_type_jinjianniu_90, R.drawable.icon_type_zhinengmensuo_90, R.drawable.icon_type_pm25_90,
            R.drawable.icon_type_shuijin_90, R.drawable.icon_type_duogongneng_90, R.drawable.icon_type_zhinengchazuo_90,
            R.drawable.pic_zigbee_pingyikzq,
            R.drawable.icon_type_wangguan_90, R.drawable.duogongneng1
    )

    //"B301"暂时为多功能模块
    private val types = arrayOf(
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401", "A501",
            "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AC01", "B301", "B101", "平移控制器", "网关", "多功能面板"
    )

    //        //type：设备类型，1-灯，2-调光，3-空调，4-窗帘，5-新风，6-地暖,7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    //wifi类型
    //wifi类型
    private val types_wifi = arrayOf( //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            "hongwai", "yaokong", "101", "103", "102"
    )
    private val icon_wifi = intArrayOf(
            R.drawable.icon_type_hongwaizfq_90,
            R.drawable.icon_type_yaokongqi_90,
            R.drawable.icon_type_shexiangtou_90,  // R.drawable.icon_type_pmmofang_90,
            R.drawable.icon_type_keshimenling_90,
            R.drawable.icon_pmmofang_90 //桌面PM2.5
    )
    private val iconNam_wifi = intArrayOf(R.string.hongwai, R.string.yaokongqi, R.string.shexiangtou, R.string.keshimenling, R.string.table_pm) //, R.string.pm_mofang
    private val iconName = intArrayOf(R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang1, R.string.lianglutiaoguang1, R.string.sanlutiao1, R.string.window_panel1, R.string.kongtiao_panel, R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.duogongneng, R.string.cha_zuo_2, R.string.pingyi_control, R.string.wangguan, R.string.duogongneng1
    )
    private var adapter: SelectDevTypeAdapter? = null
    private var newFragment: ConfigDialogFragment? = null
    private var newGatewayFragment: GatewayDialogFragment? = null
    private var dialogUtil: DialogUtil? = null
    private var list_hand_scene: MutableList<Map<*, *>> = ArrayList()

    /**
     * 小苹果绑定列表
     */
    private var mDeviceManager: DeviceManager? = null
    var wifiDevices: MutableList<GizWifiDevice> = ArrayList()
    private val deviceNames: MutableList<String> = ArrayList()
    private var mGizWifiDevice: GizWifiDevice? = null
    private val wifi_apple_list: List<Map<*, *>> = ArrayList()
    private val TAG = "robin debug"
    private var mac: String? = null
    private var number: String? = null
    private val handler = Handler()
    override fun viewId(): Int {
        return R.layout.add_device_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
        initDialog()
        initGatewayDialog()
        onWifi()
    }

    private fun onWifi() {
        mDeviceManager = DeviceManager
                .instanceDeviceManager(applicationContext)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    /*
     * 初始化dialog
     */
    private fun initDialog() {
        // TODO Auto-generated method stub
        newFragment = ConfigDialogFragment.newInstance(this@SelectZigbeeDeviceActivity, "", "") //初始化快配和搜索设备dialogFragment
    }

    /*
     * 初始化dialog
     */
    private fun initGatewayDialog() {
        // TODO Auto-generated method stub
        newGatewayFragment = newInstance(this@SelectZigbeeDeviceActivity, "", "") //初始化快配和搜索设备dialogFragment
        //        newGatewayFragment = (GatewayDialogFragment) getGatewayInterfacer;
        // getGatewayInterfacer = (GetGatewayInterfacer) newGatewayFragment;
    }

    /**
     * 展示全窗体dialog对话框
     */
    private fun show_dialog_fragment() {
        if (!newFragment!!.isAdded) { //DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.add(newFragment!!, "dialog")
            ft.commit()
        }
    }

    /**
     * 展示网关列表全窗体dialog对话框
     */
    private fun show_gateway_dialog_fragment() {
        if (!newGatewayFragment!!.isAdded) { //DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.add(newGatewayFragment!!, "dialog")
            ft.commit()
        }
    }

    /**
     * 获取选择区域下的所有网关
     *
     * @param map
     */
    private fun sraum_getAllGateWays(map: HashMap<Any?, Any?>) {
        if (dialogUtil != null) {
            dialogUtil!!.loadDialog()
        }
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@SelectZigbeeDeviceActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@SelectZigbeeDeviceActivity, "areaNumber", "") as String
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllBox, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getAllGateWays(map)
        }, this@SelectZigbeeDeviceActivity, dialogUtil) {
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
                gatewayList = ArrayList()
                for (i in user.gatewayList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    mapdevice["name"] = user.gatewayList[i].name
                    mapdevice["number"] = user.gatewayList[i].number
                    gatewayList.add(mapdevice)
                }
                //
//                        if (user.gatewayList != null && user.gatewayList.size() != 0) {//区域命名
//                            showGatewayListAdapter = new ShowGatewayListAdapter(getActivity()
//                                    , gatewayList);
//                            list_show_rev_item.setAdapter(showGatewayListAdapter);
//                        }
                if (gatewayList.size != 0) {
                    if (gatewayList.size == 1) {
                        val type = map["type"] as String?
                        val gateway_number = gatewayList[0]["number"] as String?
                        when (type) {
                            "B201" -> {
                                val intent_position = Intent(this@SelectZigbeeDeviceActivity, SelectSmartDoorLockActivity::class.java)
                              //  ( map as MutableMap<String,Any>)["gateway_number"]  = gateway_number
                                map.put("gateway_number",gateway_number!!)
                                intent_position.putExtra("map", map as Serializable)
                                startActivity(intent_position)
                            }
                            else -> set_gateway(0, map)
                        }
                    } else {
                        show_gateway_dialog_fragment()
                        getGatewayInterfacer?.sendGateWayparams(map, gatewayList)
                    }
                } else {
                    ToastUtil.showToast(this@SelectZigbeeDeviceActivity, "网关列表为空")
                }
            }
        })
    }

    /**
     * 设置网关开启模式
     *
     * @param position
     */
    private fun set_gateway(position: Int, map1: Map<*, *>) {
        //去设置设置网关模式
        val type = map1["type"] as String?
        val status = map1["status"] as String?
        val gateway_number = gatewayList[position]["number"] as String?
        val areaNumber = SharedPreferencesUtil.getData(this@SelectZigbeeDeviceActivity, "areaNumber", "") as String
        //在这里先调
        //设置网关模式-sraum-setBox
        val map= HashMap<Any?, Any?>()
        //        String phoned = getDeviceId(SelectZigbeeDeviceActivity.this);
        map["token"] = TokenUtil.getToken(this@SelectZigbeeDeviceActivity)
        map["boxNumber"] = gateway_number
        val regId = SharedPreferencesUtil.getData(this@SelectZigbeeDeviceActivity, "regId", "") as String
        map["regId"] = regId
        map["status"] = status
        map["areaNumber"] = areaNumber
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map as HashMap<String,Any>, object : Mycallback(AddTogglenInterfacer { }, this@SelectZigbeeDeviceActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                var intent_position: Intent? = null
                intent_position = Intent(this@SelectZigbeeDeviceActivity, AddZigbeeDevActivity::class.java)
                intent_position.putExtra("type", type)
                intent_position.putExtra("boxNumber", gateway_number)
                startActivity(intent_position)
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@SelectZigbeeDeviceActivity,
                        "该网关不存在")
            }
        }
        )
    }

    /**
     * 给全屏窗体传参数
     */
    private val getGatewayInterfacer: GetGatewayInterfacer? = null

    interface GetGatewayInterfacer {
        fun sendGateWayparams(map: Map<*, *>?, gatewayList: List<Map<*, *>>?)
    }

    override fun onData() {
        adapter = SelectDevTypeAdapter(this@SelectZigbeeDeviceActivity, icon, iconName)
        macfragritview_id!!.adapter = adapter
        adapter_wifi = SelectWifiDevAdapter(this@SelectZigbeeDeviceActivity, icon_wifi, iconNam_wifi)
        mac_wifi_dev_id!!.adapter = adapter_wifi
        macfragritview_id!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (types[position]) {
                "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401", "B301", "B101", "A902", "AD01", "A501", "B201", "多功能面板", "平移控制器" -> //                    case "A511":
//                        sraum_setBox_accent(types[position], "normal");
                    sraum_setBox_accent(types[position], "normal")
                "A801", "A901", "AB01", "AB04", "B001", "AC01" -> //                        sraum_setBox_accent(types[position], "zigbee");
                    sraum_setBox_accent(types[position], "zigbee")
                "网关" -> show_dialog_fragment()
            }
            //                ToastUtil.showToast(SelectZigbeeDeviceActivity.this, "添加网关");
        }
        mac_wifi_dev_id!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            var intent_wifi: Intent? = null
            when (types_wifi[position]) {
                "101", "103" -> {
                    intent_wifi = Intent(this@SelectZigbeeDeviceActivity, AddWifiDevActivity::class.java)
                    intent_wifi.putExtra("type", types_wifi[position])
                    startActivity(intent_wifi)
                }
                "102" -> {
                    val openCameraIntent = Intent(this@SelectZigbeeDeviceActivity, CaptureActivity::class.java)
                    startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE)
                }
                "hongwai" -> {
                    intent_wifi = Intent(this@SelectZigbeeDeviceActivity, AddWifiDevActivity::class.java)
                    intent_wifi.putExtra("type", types_wifi[position])
                    startActivity(intent_wifi)
                }
                "yaokong" -> otherDevices
            }
        }
    }

    /**
     * 设置网关开始模式
     *
     * @param type
     * @param normal
     */
    private fun sraum_setBox_accent(type: String, normal: String) {
        val map = HashMap<Any?, Any?>()
        map["type"] = type
        when (normal) {
            "normal" -> map["status"] = "1" //普通进入设置模式
            "zigbee" -> map["status"] = "12" //zigbee进入设置模式
        }
        sraum_getAllGateWays(map)
    }//                                update(mDeviceManager.getCanUseGizWifiDevice());//重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen//刷新togglen数据//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));

    /**
     * 获取门磁等第三方设备
     */
    private val otherDevices: Unit
        private get() {
            if (dialogUtil != null) {
                dialogUtil!!.loadDialog()
            }
            val mapdevice: MutableMap<String?, String?> = HashMap()
            mapdevice["token"] = TokenUtil.getToken(this@SelectZigbeeDeviceActivity)
            val areaNumber = SharedPreferencesUtil.getData(this@SelectZigbeeDeviceActivity, "areaNumber", "") as String
            //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
            mapdevice["areaNumber"] = areaNumber
            MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
                otherDevices
            }, this@SelectZigbeeDeviceActivity, dialogUtil) {
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
                    if (list_hand_scene.size == 1) {
                        if (wifiDevices.size != 0) {
                            choose_the_brand(0)
                        } else {
//                                update(mDeviceManager.getCanUseGizWifiDevice());
                            ToastUtil.showToast(this@SelectZigbeeDeviceActivity, "请与" + list_hand_scene[0]["name"].toString()
                                    +
                                    "在同一网络后再添加")
                        }
                    } else {
                        val intent_wifi = Intent(this@SelectZigbeeDeviceActivity,
                                SelectInfraredForwardActivity::class.java)
                        intent_wifi.putExtra("list_hand_scene", list_hand_scene as Serializable)
                        startActivity(intent_wifi)
                    }
                }
            })
        }

    override fun onResume() {
        super.onResume()
        mDeviceManager!!.setGizWifiCallBack(mGizWifiCallBack)
        update(mDeviceManager!!.canUseGizWifiDevice)
    }

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

        override fun didBindDeviceCd(result: GizWifiErrorCode, did: String) {    //
            super.didBindDeviceCd(result, did)
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) { //
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

    /**
     * 跳转到选择品牌界面
     *
     * @param position
     */
    private fun choose_the_brand(position: Int) {
        mac = list_hand_scene[position]["controllerId"] as String?
        number = list_hand_scene[position]["number"].toString()
        //去根据mac去服务器端下载GizWifiDevice
        var apple_name = ""
        for (i in list_hand_scene.indices) {
            if (list_hand_scene[i]["controllerId"] == mac) {
                apple_name = list_hand_scene[i]["name"].toString()
            }
        }
        get_to_wifi(mac, apple_name)
    }

    private fun get_to_wifi(mac: String?, apple_name: String) {
        for (i in wifiDevices.indices) {
            if (wifiDevices[i].macAddress == mac) {
                mGizWifiDevice = wifiDevices[i]
            }
        }
        if (!Utility.isEmpty(mGizWifiDevice)) { //
            mDeviceManager!!.bindRemoteDevice(mGizWifiDevice)
            val finalMGizWifiDevice = mGizWifiDevice
            handler.postDelayed({ mDeviceManager!!.setSubscribe(finalMGizWifiDevice, true) }, 1000)
        } else {
            ToastUtil.showToast(this@SelectZigbeeDeviceActivity, "请与" + apple_name
                    +
                    "在同一网络后再控制")
            return
        }
        toControlApplianAct()
    }

    private fun toControlApplianAct() {
        val intent = Intent(this@SelectZigbeeDeviceActivity, SelectControlApplianceActivity::class.java)
        if (mGizWifiDevice == null) {
            return
        }
        intent.putExtra("GizWifiDevice", mGizWifiDevice)
        //        intent.putExtra("tid", getIntent().getSerializableExtra("tid"));
        intent.putExtra("number", number)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
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
        }
        //        adapter.notifyDataSetChanged();
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 扫描二维码/条码回传
        if (requestCode == Constants.SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val content = data.getStringExtra("result")
                Log.e("robin debug", "content:$content")
                if (TextUtils.isEmpty(content)) return
                //在这里解析二维码，变成房号
                // 密钥
                val key = "ztlmassky6206ztl"
                // 解密
                var DeString: String? = null
                try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                    DeString = AES.Decrypt(content, key)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (DeString == null) {
                    ToastUtil.showToast(this@SelectZigbeeDeviceActivity, "此二维码不是PM2.5二维码")
                } else {
//                    scanlinear.setVisibility(View.GONE);
//                    detairela.setVisibility(View.VISIBLE);
//                    gateid.setText(DeString);
//                    gatedditexttwo.setText("");
                    //跳转到编辑网关界面
                    if (DeString.length == 12) {
                        val stringBuffer = StringBuffer()
                        for (i in 0..5) {
                            stringBuffer.append(DeString.substring(i * 2, i * 2 + 2)).append(":")
                        }
                        val intent = Intent(this@SelectZigbeeDeviceActivity, EditTablePMActivity::class.java)
                        intent.putExtra("mac", stringBuffer.toString().substring(0, stringBuffer.length - 1).toUpperCase())
                        intent.putExtra("id", DeString.toUpperCase())
                        startActivity(intent)
                    }
                }
            }
        }
    }
}