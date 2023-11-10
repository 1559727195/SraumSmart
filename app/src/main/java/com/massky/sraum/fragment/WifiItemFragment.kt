package com.massky.sraum.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.AddTogenInterface.AddTogglenInterfacer
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.*
import com.massky.sraum.adapter.SelectDevTypeAdapter
import com.massky.sraum.adapter.SelectWifiDevAdapter
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.tool.Constants
import com.massky.sraum.widget.ListViewForScrollView
import com.yaokan.sdk.utils.Logger
import com.yaokan.sdk.utils.Utility
import com.yaokan.sdk.wifi.DeviceManager
import com.yaokan.sdk.wifi.GizWifiCallBack
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by masskywcy on 2018-11-13.
 */
class WifiItemFragment : Fragment() {
    private var views: View? = null
    private var title: String? = null
    private var adapter_wifi: SelectWifiDevAdapter? = null
    private val gatewayList: List<Map<*, *>> = ArrayList()

    //        //type：设备类型，1-灯，2-调光，3-空调，4-窗帘，5-新风，6-地暖,7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手,
    //wifi类型
    //wifi类型
    private val types_wifi = arrayOf( //红外转发器类型暂定为hongwai,遥控器类型暂定为yaokong
            "A2A1", "A2A2", "A2A3", "A2A4",
            "hongwai", "yaokong", "101", "103", "102", "104","ADA1", "ADA2", "ADA3","ADB1","ADB2","ADB3"
    )
    private val icon_wifi = intArrayOf(
            R.drawable.icon_type_yijiandk_90, R.drawable.icon_type_liangjiandk_90,
            R.drawable.icon_type_sanjiandk_90,
            R.drawable.icon_type_sijiandk_90,
            R.drawable.icon_type_hongwaizfq_90,
            R.drawable.icon_type_yaokongqi_90,
            R.drawable.icon_type_shexiangtou_90,  // R.drawable.icon_type_pmmofang_90,
            R.drawable.icon_type_keshimenling_90,
            R.drawable.icon_pmmofang_90,//桌面PM2.5
            R.drawable.icon_yingshisxt_90,R.drawable.icon_type_yikaimb_90,
            R.drawable.icon_type_liangkaimb_90, R.drawable.icon_type_sankaimb_90,
            R.drawable.icon_type_zhinengchazuo_90, R.drawable.icon_type_chuanglianmb_90,
            R.drawable.icon_type_pm25_90
    )
    private val iconNam_wifi = intArrayOf(
            R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.hongwai, R.string.yaokongqi, R.string.shexiangtou, R.string.keshimenling, R.string.table_pm, R.string.yingshi_camera,R.string.liangjian_panel, R.string.sijian_panel,
            R.string.liujian_panel,R.string.cha_zuo_2, R.string.wifi_window,R.string.wifi_in_pm)
    //, R.string.pm_mofang


    private val adapter: SelectDevTypeAdapter? = null
    private val newFragment: ConfigDialogFragment? = null
    private val newGatewayFragment: GatewayDialogFragment? = null
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
    private var wifi_list: ListViewForScrollView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        views = inflater.inflate(R.layout.fragment_wifi_selects, null)
        val bundle = arguments
        title = bundle!!.getSerializable("title") as String
        // Log.e("TAG","bbsCategoryList="+bbsCategoryList);
        return views
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //
        super.onViewCreated(view, savedInstanceState)
        onView(view)
        dialogUtil = DialogUtil(activity)
        onWifi()
        onData()
    }

    private fun onView(view: View) {
        wifi_list = view.findViewById(R.id.wifi_list)
    }

    var item = ""
    private fun onData() {
        adapter_wifi = SelectWifiDevAdapter(activity, icon_wifi, iconNam_wifi)
        wifi_list!!.adapter = adapter_wifi
        wifi_list!!.onItemClickListener = AdapterView.OnItemClickListener { parent, views, position, id ->
            var intent_wifi: Intent? = null
            when (types_wifi[position]) {
                "101", "103" -> {
                    intent_wifi = Intent(activity, AddWifiDevActivity::class.java)
                    intent_wifi.putExtra("type", types_wifi[position])
                    startActivity(intent_wifi)
                }
                "102" -> {
                    item = "102"
                    init_permissions()
                }

                "hongwai", "A2A1", "A2A2", "A2A3", "A2A4", "ADA1", "ADA2", "ADA3","ADB1","ADB2","ADB3" -> {
                    intent_wifi = Intent(activity, AddWifiDevActivity::class.java)
                    intent_wifi.putExtra("type", types_wifi[position])
                    startActivity(intent_wifi)
                }
                "yaokong" -> otherDevices


                "104" -> {
                    item = "104"
                    init_permissions()
                }
            }
        }
    }//                                update(mDeviceManager.getCanUseGizWifiDevice());//重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen//刷新togglen数据//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));

    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(context as AppCompatActivity)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                val openCameraIntent = Intent(activity, CaptureActivity::class.java)
                openCameraIntent.putExtra("item", item)
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_WIFI_CODE)
            }
        })
    }


    /**
     * 获取门磁等第三方设备
     */
    private val otherDevices: Unit
        private get() {
            if (dialogUtil != null) {
                dialogUtil!!.loadDialog()
            }
            val mapdevice: MutableMap<String?, String?> = HashMap()
            mapdevice["token"] = TokenUtil.getToken(activity)
            val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
            //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
            mapdevice["areaNumber"] = areaNumber
            MyOkHttp.postMapString(ApiHelper.sraum_getWifiAppleInfos, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
                otherDevices
            }, activity, dialogUtil) {
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
                            ToastUtil.showToast(activity, "请与" + list_hand_scene[0]["name"].toString()
                                    +
                                    "在同一网络后再添加")
                        }
                    } else {
                        val intent_wifi = Intent(activity,
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

    private fun onWifi() {
        mDeviceManager = DeviceManager
                .instanceDeviceManager(activity!!.applicationContext)
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
            ToastUtil.showToast(activity, "请与" + apple_name
                    +
                    "在同一网络后再控制")
            return
        }
        toControlApplianAct()
    }

    private fun toControlApplianAct() {
        val intent = Intent(activity, SelectControlApplianceActivity::class.java)
        if (mGizWifiDevice == null) {
            return
        }
        intent.putExtra("GizWifiDevice", mGizWifiDevice)
        //        intent.putExtra("tid", getIntent().getSerializableExtra("tid"));
        intent.putExtra("number", number)
        startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 扫描二维码/条码回传
        if (requestCode == Constants.SCAN_REQUEST_WIFI_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                when (item) {
                    "102" -> {
                        operate_jiami_data(data)
                    }
                    "104" -> {
                        val content = data.getStringExtra("result")
                        Log.e("robin debug", "content:$content")
                        if (TextUtils.isEmpty(content)) return
                        //www.ys7.com587966106DNHJGUCS-C2W-21WPFR
                        var lists: List<String> = content!!.split("\r")
                        var yanzhengma = ""
                        var srial_number = ""
//                        www.ys7.com
//                        587966106
//                        DNHJGU
//                        CS-C2W-21WPFR  (B)
                        if (lists.size >= 3) {//587966106,DNHJGU
                            srial_number = lists[1]
                            yanzhengma = lists[2]
                            Log.e("robin debug", "srial_number:$srial_number" + ",yanzhengma:$yanzhengma")
                            //HandAddYingShiCameraActivity
                            val intent = Intent(activity, HandAddYingShiCameraActivity::class.java)
                            intent.putExtra("yanzhengma", yanzhengma)
                            intent.putExtra("srial_number", srial_number)
                            startActivity(intent)
                        } else {
                            return
                        }
                    }
                }
            }
        }
    }

    private fun operate_jiami_data(data: Intent) {
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
            ToastUtil.showToast(activity, "此二维码不是PM2.5二维码")
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
                val intent = Intent(activity, EditTablePMActivity::class.java)
                intent.putExtra("mac", stringBuffer.toString().substring(0, stringBuffer.length - 1).toUpperCase())
                intent.putExtra("id", DeString.toUpperCase())
                startActivity(intent)
            }
        }
    }

    companion object {
        fun newInstance(title: String?): WifiItemFragment {
            val bundle = Bundle()
            bundle.putSerializable("title", title)
            val fragment = WifiItemFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}