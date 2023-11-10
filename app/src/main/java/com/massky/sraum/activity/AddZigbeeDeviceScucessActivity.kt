package com.massky.sraum.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.MainGateWayActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/1/8.
 */
class AddZigbeeDeviceScucessActivity : BaseActivity() {
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
    private var deviceList: List<device>? = ArrayList()
    private var panelType: String? = null
    private var panelName: String? = null
    private var panelNumber: String? = null
    private val deviceNumber: String? = null
    private var panelMAC: String? = null

    @JvmField
    @BindView(R.id.dev_name)
    var dev_name: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null
    private val iconName = intArrayOf(R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang, R.string.lianglutiaoguang, R.string.sanlutiao, R.string.window_panel, R.string.air_panel,
            R.string.air_mode, R.string.xinfeng_mode, R.string.dinuan_mode, R.string.menci, R.string.rentiganying, R.string.xiding_rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.jixieshou, R.string.cha_zuo_1, R.string.cha_zuo, R.string.wifi_hongwai,
            R.string.wifi_camera, R.string.one_light_control, R.string.two_light_control, R.string.three_light_control, R.string.two_dimming_one_control, R.string.two_dimming_two_control, R.string.two_dimming_trhee_control, R.string.keshimenling,
            R.string.pingyi_control, R.string.music_panel, R.string.tv_oc, R.string.xiding_rentiganying, R.string.guangzhao_chuangan
    )

    private var type_txt: TextView? = null
    private var mac_txt: TextView? = null
    private var boxNumber: String? = null
    private var areaNumber //窗帘电机
            : String? = null

    override fun viewId(): Int {
        return R.layout.add_zigbee_deivice_scucess
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        btn_login_gateway!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this)
        next_step_txt!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        get_panel_detail()
    }

    override fun onEvent() {}
    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> showPopWindow()
            R.id.btn_login_gateway -> save_panel()
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopWindow() {
        try {
            val view = LayoutInflater.from(this@AddZigbeeDeviceScucessActivity).inflate(
                    R.layout.add_devsucesspopupwindow, null)
            type_txt = view.findViewById<View>(R.id.type) as TextView
            //           //mac
            mac_txt = view.findViewById<View>(R.id.mac) as TextView
            set_type(panelType)
            mac_txt!!.text = panelMAC
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
            val xoff = DisplayUtil.dip2px(this@AddZigbeeDeviceScucessActivity, 20f)
            popupWindow!!.showAsDropDown(next_step_txt, 0, DisplayUtil.dip2px(this@AddZigbeeDeviceScucessActivity, 10f))
            //            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow!!.setOnDismissListener {
                popupWindow = null // 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 根据面板类型命名
     *
     * @param type
     */
    private fun set_type(type: String?) {
        when (type) {
            "A201" -> type_txt!!.setText(iconName[0])
            "A202" -> type_txt!!.setText(iconName[1])
            "A203" -> type_txt!!.setText(iconName[2])
            "A204" -> type_txt!!.setText(iconName[3])
            "A301" -> type_txt!!.setText(iconName[4])
            "A302" -> type_txt!!.setText(iconName[5])
            "A303" -> type_txt!!.setText(iconName[6])
            "A401" -> type_txt!!.setText(iconName[7])
            "A501" -> type_txt!!.setText(iconName[8])
            "A511" -> type_txt!!.setText(iconName[9])
            "A611" -> type_txt!!.setText(iconName[10])
            "A711" -> type_txt!!.setText(iconName[11])
            "A801" -> type_txt!!.setText(iconName[12])
            "A901" -> type_txt!!.setText(iconName[13])
            "A902" -> type_txt!!.setText(iconName[14])
            "AB01" -> type_txt!!.setText(iconName[15])
            "AB04" -> type_txt!!.setText(iconName[16])
            "B001" -> type_txt!!.setText(iconName[17])
            "B201" -> type_txt!!.setText(iconName[18])
            "AD01" -> type_txt!!.setText(iconName[19])
            "AC01" -> type_txt!!.setText(iconName[20])
            "B301" -> type_txt!!.setText(iconName[21])
            "B101" -> type_txt!!.setText(iconName[22])
            "B102" -> type_txt!!.setText(iconName[23])
            "AA02" -> type_txt!!.setText(iconName[24])
            "AA03" -> type_txt!!.setText(iconName[25])
            "A311" -> type_txt!!.setText(iconName[26])
            "A312" -> type_txt!!.setText(iconName[27])
            "A313" -> type_txt!!.setText(iconName[28])
            "A321" -> type_txt!!.setText(iconName[29])
            "A322" -> type_txt!!.setText(iconName[30])
            "A331" -> type_txt!!.setText(iconName[31])
            "AA04" -> type_txt!!.setText(iconName[32])
            "B401", "B402", "B403" -> type_txt!!.setText(iconName[33])
            "B501" -> type_txt!!.setText(iconName[34]) //31音乐面板
            "AD03" -> type_txt!!.setText(iconName[35]) //空气检测器
            "A911" -> type_txt!!.setText(iconName[36]) //吸顶人体感应
            "AE02" -> type_txt!!.setText(iconName[37]) //光照传感器
        }
    }

    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.attributes = lp
    }

    /**
     * 得到面板信息
     */
    private fun get_panel_detail() {
        panelNumber = intent.getStringExtra("panelid")
        //根据panelid去查找相关面板信心
        //根据panelid去遍历所有面板
        boxNumber = intent.getStringExtra("boxNumber")
        val bundle = intent.getBundleExtra("bundle_panel")
        deviceList = bundle!!.getSerializable("deviceList") as List<device>
        panelType = intent.getStringExtra("panelType")
        panelName = intent.getStringExtra("panelName")
        panelMAC = intent.getStringExtra("panelMAC")
        areaNumber = intent.getStringExtra("areaNumber")
        if (panelName != null) dev_name!!.setText(panelName)
    }

    /**
     * 更新面板名称
     *
     * @param panelName
     * @param panelNumber
     */
    private fun sraum_update_panel_name(panelName: String, panelNumber: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        //        final String areaNumber = (String) SharedPreferencesUtil.getData(AddZigbeeDeviceScucessActivity.this, "areaNumber", "");
        map["token"] = TokenUtil.getToken(this@AddZigbeeDeviceScucessActivity)
        map["areaNumber"] = areaNumber
        map["gatewayNumber"] = boxNumber
        map["deviceNumber"] = panelNumber
        map["newName"] = panelName
        MyOkHttp.postMapObject(ApiHelper.sraum_updateDeviceName, map,
                object : Mycallback(AddTogglenInterfacer
                //刷新togglen获取新数据
                { sraum_update_panel_name(panelName, panelNumber) }, this@AddZigbeeDeviceScucessActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        finish()
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        //
                        finish()
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                        val map = HashMap<Any?, Any?>()
                        map["deviceId"] = panelNumber
                        map["deviceType"] = panelType
                        map["type"] = "1"
                        map["areaNumber"] = areaNumber
                        val intent = Intent(this@AddZigbeeDeviceScucessActivity, SelectRoomActivity::class.java)
                        intent.putExtra("map_deivce", map as Serializable)
                        startActivity(intent)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@AddZigbeeDeviceScucessActivity, "$panelName:面板编号不正确")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@AddZigbeeDeviceScucessActivity, "$panelName:面板名字已存在")
                    }
                })
    }

    /**
     * 保存面板
     */
    private fun save_panel() {
        val panelName = dev_name!!.text.toString().trim { it <= ' ' }
        sraum_update_panel_name(panelName, panelNumber)
    }
}