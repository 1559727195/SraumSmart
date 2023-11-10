package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.FastEditPanelActivity
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2017/11/9.
 */
class FastEditPanelActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    //    @BindView(R.id.addscroll)
    //    ScrollView addscroll;
    private var mMessageReceiver: MessageReceiver? = null
    private val dialogUtil: DialogUtil? = null
    private var panelType: String? = null
    private var panelName: String? = null
    private val panelNumber: String? = null
    private val deviceNumber: String? = null
    private var panelMAC: String? = null
    private val deviceList: MutableList<device> = ArrayList()
    private val panelList: MutableList<panellist> = ArrayList()
    private var is_index = false

    @JvmField
    @BindView(R.id.second)
    var second_txt: TextView? = null

    @JvmField
    @BindView(R.id.miao)
    var miao: TextView? = null
    private var gateway_number = ""
    private var areaNumber: String? = null
    private var isfirst_edit = 0
    override fun viewId(): Int {
        return R.layout.fast_edit_panel_act
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        //                addscroll.setVisibility(View.GONE);
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        registerMessageReceiver()
        init_timer()
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun init_timer() {
        val second = intArrayOf(30) //90秒
        val minute = intArrayOf(1) //90秒
        val index = intArrayOf(0)
        is_index = true
        Thread(Runnable {
            while (is_index) {
                try {
                    Thread.sleep(1000)
                    if (miao == null) {
                        is_index = false
                        break
                    }
                    runOnUiThread(Runnable {
                        second[0]--
                        if (second[0] > 9) {
                            if (miao == null) return@Runnable
                            miao!!.text = second[0].toString()
                        } else if (second[0] > 0) {
                            miao!!.text = "0" + second[0].toString()
                        } else {
                            index[0]++
                            if (index[0] >= 2) {
                                finish()
                                //停止添加网关
                                is_index = false
                                if (miao == null) return@Runnable
                                miao!!.text = "00"
                                second[0] = 0
                                minute[0] = 0
                                miao!!.text = "00"
                            } else {
                                miao!!.text = "00"
                                second[0] = 59
                                minute[0] = 0
                                miao!!.text = "59"
                            }
                        }
                        second_txt!!.text = "0" + minute[0].toString()
                    })
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    /*
     * 将秒数转为时分秒
     * */
    fun change(second: Int): String {
        var h = 0
        var d = 0
        var s = 0
        val temp = second % 3600
        if (second > 3600) {
            h = second / 3600
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60
                    if (temp % 60 != 0) {
                        s = temp % 60
                    }
                } else {
                    s = temp
                }
            }
        } else {
            d = second / 60
            if (second % 60 != 0) {
                s = second % 60
            }
        }
        return "$h:$d:$s"
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(ACTION_SRAUM_FAST_EDIT)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == ACTION_SRAUM_FAST_EDIT) {

                val extras = intent.getStringExtra("extras")
                var extraJson = JSONObject(extras)
                val panelid: String = extraJson.getString("panelid")


                // val messflag = extraJson.getString("notifactionId")
                // val panelid = intent.getStringExtra("panelid")
                gateway_number = extraJson.optString("gatewayid")
                //  if (messflag == 1) { //快捷编辑。在我的面板添加快捷编辑按钮。
                //在这个界面时，type=1.找面板下的设备列表。在编辑面板的界面。找面板按钮，找到面板后，
                getPanel_devices(panelid)
                //}
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
    private fun getPanel_devices(panelid: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@FastEditPanelActivity)
        map["areaNumber"] = areaNumber
        var api = ""
        when (gateway_number) {
            "" -> {//wifi

                // String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
                map["deviceId"] = panelid
                api = ApiHelper.sraum_getWifiButtons!!
            }
            else -> {//zigbee
                map["boxNumber"] = gateway_number
                map["panelNumber"] = panelid
                api = ApiHelper.sraum_getPanelDevices
            }
        }

        //val map: MutableMap<String, Any?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(FastEditPanelActivity.this, "areaNumber", "");
        //  map["token"] = TokenUtil.getToken(this@FastEditPanelActivity)
        //   map["areaNumber"] = areaNumber

        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { getPanel_devices(panelid) }, this@FastEditPanelActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        panelList.clear()
                        deviceList.clear()
                        deviceList.addAll(user.deviceList)

                        //面板的详细信息
                        panelType = user.panelType
                        panelName = user.panelName
                        panelMAC = user.panelMAC
                        isfirst_edit++
                        if (isfirst_edit == 1) {
                            intent_edit_page(panelid)
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun wrongBoxnumber() {
                        ToastUtil.showToast(this@FastEditPanelActivity, "该网关不存在")
                    }
                }
        )
    }

    private fun intent_edit_page(panelid: String) {
        var intent: Intent? = null
        if (panelid != "") {
            when (panelType) {
                "A421",
                "A211", "A212", "A213", "A214",
                "A2A1", "A2A2", "A2A3", "A2A4", "A201", "A202", "A203", "A204", "A301",
                "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A411",
                "A412", "A413", "A414", "A501", "A601", "A701", "A511", "A611", "A711",
                "A801", "A901", "A902", "AB01", "AB04", "AC01", "AD01", "AD02",
                "B001", "B101", "B201", "B202", "B301", "B401", "B402", "B403",
                "AD11","AD12","AD13"-> intent = Intent(this@FastEditPanelActivity,
                        ChangePanelAndDeviceActivity::class.java)
              else-> return
            }
            intent!!.putExtra("panelid", panelid)
            intent.putExtra("boxNumber", gateway_number)
            val bundle = Bundle()
            bundle.putSerializable("deviceList", deviceList as Serializable)
            //                            intent.putExtra("deviceList", (Serializable) deviceList);
            intent.putExtra("panelType", panelType)
            intent.putExtra("panelName", panelName)
            intent.putExtra("panelMAC", panelMAC)
            intent.putExtra("bundle_panel", bundle)
            intent.putExtra("findpaneltype", "fastedit")
            intent.putExtra("areaNumber", areaNumber)
            startActivity(intent)
            //                                    FastEditPanelActivity.this.finish();
            is_index = false
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        is_index = false
        isfirst_edit = 0
        unregisterReceiver(mMessageReceiver)
    }

    companion object {
        @JvmField
        var ACTION_SRAUM_FAST_EDIT = "ACTION_SRAUM_FAST_EDIT" //notifactionId = 8 ->设置网关模式，sraum_setBox
    }
}