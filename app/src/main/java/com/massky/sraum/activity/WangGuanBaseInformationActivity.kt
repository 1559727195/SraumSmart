package com.massky.sraum.activity

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.sql.Types
import java.util.*

/**
 * Created by zhu on 2018/1/16.
 */
class WangGuanBaseInformationActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var gateway_map = HashMap<Any?, Any?>()

    @JvmField
    @BindView(R.id.type)
    var type: TextView? = null

    @JvmField
    @BindView(R.id.mac)
    var mac: TextView? = null

    @JvmField
    @BindView(R.id.version_hard_txt)
    var version_hard_txt: TextView? = null

    //version_hard
    @JvmField
    @BindView(R.id.version_hard)
    var version_hard: TextView? = null

    //version_gujian_rel
    @JvmField
    @BindView(R.id.version_gujian_rel)
    var version_gujian_rel: RelativeLayout? = null

    //version_gujian
    @JvmField
    @BindView(R.id.version_gujian)
    var version_gujian: TextView? = null

    //gujian_view
    @JvmField
    @BindView(R.id.gujian_view)
    var gujian_view: View? = null

    //xindao_rel
    @JvmField
    @BindView(R.id.xindao_rel)
    var xindao_rel: RelativeLayout? = null

    //xindao_view
    @JvmField
    @BindView(R.id.xindao_view)
    var xindao_view: View? = null

    //panel_id_rel
    @JvmField
    @BindView(R.id.panel_id_rel)
    var panel_id_rel: RelativeLayout? = null

    //panel_id_view
    @JvmField
    @BindView(R.id.panel_id_view)
    var panel_id_view: View? = null


    @JvmField
    @BindView(R.id.pannel)
    var pannel: TextView? = null

    @JvmField
    @BindView(R.id.xindao_txt)
    var xindao_txt: TextView? = null

    @JvmField
    @BindView(R.id.pan_txt)
    var pan_txt: TextView? = null

    @JvmField
    @BindView(R.id.pan_id)
    var pan_id: TextView? = null
    private var gatewayNumber //网关编号
            : String? = null
    private var areaNumber: String? = null
    private var panelItem_map = HashMap<Any?, Any?>()
    private var wifi_map = HashMap<Any?, Any?>()
    private var types: String? = null


    override fun viewId(): Int {
        return R.layout.wangguan_baseinfor_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        gatewayNumber = intent.getSerializableExtra("number") as String
        panelItem_map = intent.getSerializableExtra("panelItem_map") as HashMap<Any?, Any?>
        types = panelItem_map["type"] as String?

        when (types) {
            "A2A1", "A2A2", "A2A3", "A2A4","ADA1","ADA2","ADA3","ADB1","ADB2","ADB3" -> {
                wifi_map = intent.getSerializableExtra("wifi_map") as HashMap<Any?, Any?>
            }
        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onData() { //"主卫水浸","防盗门门磁","防盗门猫眼","人体监测","防盗门门锁"
    }

    override fun onResume() {
        when (types) {
            "网关" ,"A088"-> {
                version_gujian_rel!!.visibility = View.VISIBLE
                gujian_view!!.visibility = View.VISIBLE
                xindao_rel!!.visibility = View.GONE
                panel_id_rel!!.visibility = View.GONE
                xindao_view!!.visibility = View.GONE
                panel_id_view!!.visibility = View.GONE
                version_hard_txt!!.text="硬件版本"
                sraum_getGatewayInfo()
            }
            else -> {

                //显示
                mac!!.text = panelItem_map["mac"].toString()
                type!!.text = panelItem_map["type"].toString()
                version_hard!!.text = ""
                pannel!!.text = ""
                pan_id!!.text = ""


                when (types) {
                    "A2A1", "A2A2", "A2A3", "A2A4" ,"ADA1","ADA2","ADA3","ADB1","ADB2","ADB3"-> {

                        mac!!.text = wifi_map["mac"].toString()
                        type!!.text = wifi_map["type"].toString()
                        version_hard!!.text = wifi_map["custom1"].toString()
                        xindao_txt!!.text = "WIFI"
                        pan_txt!!.text = "固件"
                        pannel!!.text = wifi_map["wifi"].toString()
                        pan_id!!.text = wifi_map["custom2"].toString()
                    }
                }
            }
        }
        super.onResume()
    }

    /**
     * 获取网关基本信息（APP->网关）
     */
    private fun sraum_getGatewayInfo() {
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["number"] = gatewayNumber
        map["areaNumber"] = areaNumber
        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayInfo, map as Map<String, Any>,
                object : Mycallback(AddTogglenInterfacer { }, this@WangGuanBaseInformationActivity, null) {
                    override fun onSuccess(user: User) {
                        //"roomNumber":"1"
                        gateway_map = HashMap<Any?, Any?>()
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
                        mac!!.text = user.mac
                        type!!.text = user.type
                        version_hard!!.text = user.hardware
                        version_gujian!!.text = user.firmware
                        pannel!!.text = user.channel
                        pan_id!!.text = user.panid
                    }
                })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish() //
        }
    }
}