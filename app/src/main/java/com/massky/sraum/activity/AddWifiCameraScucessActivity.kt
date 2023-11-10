package com.massky.sraum.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.gizwits.gizwifisdk.api.GizWifiDevice
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yaokan.sdk.wifi.DeviceManager
import okhttp3.Call
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/8.
 */
class AddWifiCameraScucessActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: ImageView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var popupWindow: PopupWindow? = null
    private val device_name: String? = null
    private val panelList: List<panellist> = ArrayList()
    private var dialogUtil: DialogUtil? = null
    private val deviceList: List<device> = ArrayList()
    private val panelType: String? = null
    private val panelName: String? = null
    private val panelNumber: String? = null
    private val deviceNumber: String? = null
    private val panelMAC: String? = null

    @JvmField
    @BindView(R.id.dev_name)
    var dev_name: ClearEditText? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null
    private val TAG = "robin debug"
    var wifiDevices: List<GizWifiDevice> = ArrayList()
    private val deviceNames: List<String> = ArrayList()
    private val mDeviceManager: DeviceManager? = null
    private val wifi_name = ""
    private val macDevice: String? = null
    private val map_device: Map<*, *> = HashMap<Any?, Any?>()
    private val gizWifiDevices: List<GizWifiDevice> = ArrayList()
    private val list_mac_wifi: List<Map<*, *>> = ArrayList()
    private val deviceInfo = ""
    private val currGizWifiDevice: GizWifiDevice? = null
    private val deviceInfo1: String? = null
    private var wificamera: Map<*, *> = HashMap<Any?, Any?>()
    private var areaNumber: String? = null
    override fun viewId(): Int {
        return R.layout.add_wifi_camera_scucess
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        btn_login_gateway!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this)
        next_step_txt!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        //        get_panel_detail();
        wificamera = intent.getSerializableExtra("wificamera") as Map<*, *>
        //intent.putExtra("wificamera",(Serializable) map_result);
        next_step_txt!!.visibility = View.GONE
    }

    override fun onEvent() {}
    override fun onData() {
        var name = wificamera["name"]
        if (name != null) {
            dev_name!!.setText(name as String)
        }
    }

    /**
     * 添加 wifi 红外转发设备
     */
    private fun sraum_addWifiCamera(name: String) {
        var type = ""
        //        String deviceInfo  = add_bind_dingyue();
        val map = HashMap<Any?, Any?>()
        areaNumber = SharedPreferencesUtil.getData(this@AddWifiCameraScucessActivity,
                "areaNumber", "") as String
        dialogUtil!!.loadDialog()
        map["token"] = TokenUtil.getToken(this@AddWifiCameraScucessActivity)
        map["areaNumber"] = areaNumber
        map["name"] = name
        map["mac"] = wificamera["strMac"]
        map["controllerId"] = wificamera["strDeviceID"]
        map["wifi"] = wificamera["wifi"]
        when (wificamera["type"].toString()) {
            "101" -> {
                map["type"] = "AA03"
                map["user"] = "admin"
                map["password"] = "888888"
                type = "AA03"
            }
            "103" -> {
                map["type"] = "AA04"
                type = "AA04"
            }
            "104" -> {
                map["type"] = "AA05"
                map["user"] = wificamera["strMac"]
                map["password"] = wificamera["strMac"]
                type = "AA05"
            }
        }


//        map.put("strMac", strMac);
//        map.put("strDeviceID", strDeviceID);
//        map.put("strName", strName);
//        map.put("wifi",wifi_name);
        val finalType = type
        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiCamera, map as HashMap<String, Any>,
                object : Mycallback(AddTogglenInterfacer
                //刷新togglen获取新数据
                { sraum_addWifiCamera(name) }, this@AddWifiCameraScucessActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        finish()
                    }

                    override fun onSuccess(user: User) {
                        //成功添加小苹果红外模块
//                        AddWifiCameraScucessActivity.this.finish();
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        val map = HashMap<Any?, Any?>()
                        map["deviceId"] = wificamera["strDeviceID"]
                        map["deviceType"] = finalType
                        map["areaNumber"] = areaNumber
                        map["type"] = "2"
                        val intent = Intent(this@AddWifiCameraScucessActivity, SelectRoomActivity::class.java)
                        intent.putExtra("map_deivce", map as Serializable)
                        startActivity(intent)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                        ToastUtil.showToast(this@AddWifiCameraScucessActivity, """
     areaNumber 不正
     确
     """.trimIndent())
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@AddWifiCameraScucessActivity, "名字已存在")
                    }

                    override fun fourCode() {
                        super.fourCode()
                    }
                })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
                val type = map_device["type"] as String?
                val mac = map_device["mac"] as String?
                showPopWindow(type, mac)
            }
            R.id.btn_login_gateway -> {
                if (dev_name!!.text.toString().trim { it <= ' ' } == "") {
                    ToastUtil.showToast(this@AddWifiCameraScucessActivity, "" +
                            "设备名称为空")
                    return
                }
                sraum_addWifiCamera(dev_name!!.text.toString().trim { it <= ' ' })
            }
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopWindow(type_txt: String?, macDevice: String?) {
        try {
            val view = LayoutInflater.from(this@AddWifiCameraScucessActivity).inflate(
                    R.layout.add_devsucesspopupwindow, null)
            val type = view.findViewById<View>(R.id.type) as TextView
            val mac = view.findViewById<View>(R.id.mac) as TextView
            type.text = type_txt
            mac.text = macDevice
            popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow!!.isFocusable = true
            popupWindow!!.isOutsideTouchable = true
            val cd = ColorDrawable(0x00ffffff) // 背景颜色全透明
            popupWindow!!.setBackgroundDrawable(cd)
            val location = IntArray(2)
            next_step_txt!!.getLocationOnScreen(location) //获得textview的location位置信息，绝对位置
            popupWindow!!.animationStyle = R.style.style_pop_animation // 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f) // 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            val xoff = DisplayUtil.dip2px(this@AddWifiCameraScucessActivity, 20f)
            popupWindow!!.showAsDropDown(next_step_txt, 0, DisplayUtil.dip2px(this@AddWifiCameraScucessActivity, 10f))
            //            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow!!.setOnDismissListener {
                popupWindow = null // 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.attributes = lp
    }
}