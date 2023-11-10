package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/7/6.
 */
class OperationRecordsDetailActivity : BaseActivity() {
    var recordList: Map<*, *>? = HashMap<Any?, Any?>()

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.time_txt)
    var time_txt: TextView? = null

    //    @BindView(R.id.quyu_name)
    //    TextView quyu_name;
    @JvmField
    @BindView(R.id.device_name)
    var device_name: TextView? = null

    @JvmField
    @BindView(R.id.action_text)
    var action_text: TextView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var dialogUtil: DialogUtil? = null
    private var deviceName: String? = null
    private var messageTitle: String? = null
    private var eventTime: String? = null

    private var userName: String? = null
    private var deviceSceneName: String? = null
    private var deviceSceneType: String? = null
    private var action: String? = null
    private var panelNumber: String? = null
    private var panelName: String? = null
    private var panelAddress: String? = null
    private var gatewayNumber: String? = null
    private var gatewayName: String? = null
    private var wifiName: String? = null

    @JvmField
    @BindView(R.id.userName_txt)
    var userName_txt: TextView? = null

    @JvmField
    @BindView(R.id.deviceSceneName_txt)
    var deviceSceneName_txt: TextView? = null

    @JvmField
    @BindView(R.id.deviceSceneType_txt)
    var deviceSceneType_txt: TextView? = null

    @JvmField
    @BindView(R.id.action_txt)
    var action_txt: TextView? = null

    @JvmField
    @BindView(R.id.panelName_txt)
    var panelName_txt: TextView? = null

    @JvmField
    @BindView(R.id.panelAddress_txt)
    var panelAddress_txt: TextView? = null

    @JvmField
    @BindView(R.id.gatewayNumber_txt)
    var gatewayNumber_txt: TextView? = null

    @JvmField
    @BindView(R.id.gatewayName_txt)
    var gatewayName_txt: TextView? = null


    @JvmField
    @BindView(R.id.wifiName_txt)
    var wifiName_txt: TextView? = null

    @JvmField
    @BindView(R.id.result_txt)
    var result_txt: TextView? = null


    @JvmField
    @BindView(R.id.userName_linear)
    var userName_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.deviceSceneName_linear)
    var deviceSceneName_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.deviceSceneType_linear)
    var deviceSceneType_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.action_linear)
    var action_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelName_linear)
    var panelName_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelAddress_linear)
    var panelAddress_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.gatewayNumber_linear)
    var gatewayNumber_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.gatewayName_linear)
    var gatewayName_linear: LinearLayout? = null


    @JvmField
    @BindView(R.id.wifiName_linear)
    var wifiName_linear: LinearLayout? = null

    //result_linear
    @JvmField
    @BindView(R.id.result_linear)
    var result_linear: LinearLayout? = null

    //linear_show


    @JvmField
    @BindView(R.id.linear_show)
    var linear_show: LinearLayout? = null

    //panelNumber_linear

    @JvmField
    @BindView(R.id.panelNumber_linear)
    var panelNumber_linear: LinearLayout? = null


    //panelNumber_txt
    @JvmField
    @BindView(R.id.panelNumber_txt)
    var panelNumber_txt: TextView? = null

    override fun viewId(): Int {
        return R.layout.operator_records_detail_act
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        recordList = intent.getSerializableExtra("Record") as Map<*, *>
        if (recordList != null) {
            sraum_getOneOperateRecord(recordList!!["id"].toString(),recordList!!["date"].toString())
        }
    }

    override fun onEvent() {}
    override fun onData() {}

    /**
     * 根据编号获取详细详情
     *
     * @param id
     */
    private fun sraum_getOneOperateRecord(id: String?,searchValue:String?) {
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        val areaNumber = SharedPreferencesUtil.getData(this@OperationRecordsDetailActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@OperationRecordsDetailActivity)
        map["areaNumber"] = areaNumber
        //        map.put("projectCode",projectCode);
        map["recordId"] = id
        map["searchValue"] = searchValue
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneOperateRecord, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            sraum_getOneOperateRecord(id,searchValue)
        }, this@OperationRecordsDetailActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@OperationRecordsDetailActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                linear_show!!.visibility = View.VISIBLE
                deviceName = user.deviceName
                messageTitle = user.messageTitle
                eventTime = user.eventTime
                if (deviceName != null) device_name!!.text = deviceName
                if (messageTitle != null) action_text!!.text = messageTitle
                if (eventTime != null) time_txt!!.text = eventTime

                userName = user.recordInfo.userName
                deviceSceneName = user.recordInfo.deviceSceneName
                deviceSceneType = user.recordInfo.deviceSceneType
                var status = if (user.recordInfo.action.status == null) "" else user.recordInfo.action.status
                var dimmer = if (user.recordInfo.action.dimmer == null) "" else user.recordInfo.action.dimmer
                var speed = if (user.recordInfo.action.speed == null) "" else user.recordInfo.action.speed
                var mode = if (user.recordInfo.action.mode == null) "" else user.recordInfo.action.mode
                var temperature = if (user.recordInfo.action.mode == null) "" else user.recordInfo.action.temperature

                var result = if (user.recordInfo.result == null) "" else user.recordInfo.result

                val map = HashMap<Any?, Any?>()
                map["status"] = status
                map["dimmer"] = dimmer
                map["speed"] = speed
                map["mode"] = mode
                map["temperature"] = temperature
                map["type"] = deviceSceneType
                var action = get_action(map)

                panelNumber = user.recordInfo.panelNumber
                panelName = user.recordInfo.panelName
                panelAddress = user.recordInfo.panelAddress
                gatewayNumber = user.recordInfo.gatewayNumber
                gatewayName = user.recordInfo.gatewayName
                wifiName = user.recordInfo.wifiName


                if (panelNumber == null || panelNumber.equals(""))
                    panelNumber_linear!!.visibility = View.GONE
                else
                    panelNumber_txt!!.text = panelNumber


                if (userName == null || userName.equals(""))
                    userName_linear!!.visibility = View.GONE
                else
                    userName_txt!!.text = userName


                //deviceSceneName_txt!!.text = deviceSceneName

                if (deviceSceneName == null)
                    deviceSceneName_linear!!.visibility = View.GONE
                else if (deviceSceneName.equals("")) {
                    deviceSceneName_txt!!.text = "无"
                } else {
                    deviceSceneName_txt!!.text = deviceSceneName
                }



                //  deviceSceneType_txt!!.text = deviceSceneType


                if (deviceSceneType == null || deviceSceneType.equals(""))
                    deviceSceneType_linear!!.visibility = View.GONE
                else {
                    deviceSceneType_content()
                    deviceSceneType_txt!!.text = deviceSceneType
                }


                //   action_txt!!.text = action as String


                if (action == null || action.equals(""))
                    action_linear!!.visibility = View.GONE
                else
                    action_txt!!.text = action

                //panelName_txt!!.text = panelName

                if (panelName == null || panelName.equals(""))
                    panelName_linear!!.visibility = View.GONE
                else
                    panelName_txt!!.text = panelName


                // panelAddress_txt!!.text = panelAddress

                if (panelAddress == null || panelAddress.equals(""))
                    panelAddress_linear!!.visibility = View.GONE
                else
                    panelAddress_txt!!.text = panelAddress


                //gatewayNumber_txt!!.text = gatewayNumber

                if (gatewayNumber == null || gatewayNumber.equals(""))
                    gatewayNumber_linear!!.visibility = View.GONE
                else
                    gatewayNumber_txt!!.text = gatewayNumber


                // gatewayName_txt!!.text = gatewayName

                if (gatewayName == null || gatewayName.equals(""))
                    gatewayName_linear!!.visibility = View.GONE
                else
                    gatewayName_txt!!.text = gatewayName


                // wifiName_txt!!.text = wifiName

                if (wifiName == null || wifiName.equals(""))
                    wifiName_linear!!.visibility = View.GONE
                else
                    wifiName_txt!!.text = wifiName

                if (result == null)
                    result_linear!!.visibility = View.GONE
                else {
                    result = result_select(result)
                    result_txt!!.text = result
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun result_select(result: String): String {
        var result1 = result
        when (result1) {
            "100" -> result1 = "成功"
            "101" -> result1 = "失败"
        }
        return result1
    }

    private fun deviceSceneType_content() {
        when (deviceSceneType) {
            "100" -> deviceSceneType = "网关场景"
            "101" -> deviceSceneType = "云场景"
            "111" -> deviceSceneType = "WiFi灯"
            "0" -> deviceSceneType = "场景"
            //else-> deviceSceneType = "zigbee 设备"
            "1" -> deviceSceneType = "普通灯"
            "2" -> deviceSceneType = "调光灯"
            "3" -> deviceSceneType = "空调"
            "4", "18" -> deviceSceneType = "窗帘"
            "5" -> deviceSceneType = "新风"
            "6" -> deviceSceneType = "地暖"
            "7" -> deviceSceneType = "门磁"
            "8" -> deviceSceneType = "人体感应"
            "9" -> deviceSceneType = "水浸"
            "10" -> deviceSceneType = "PM2.5"
            "11" -> deviceSceneType = "紧急按钮"

            "12" -> deviceSceneType = "久坐报警器"
            "13" -> deviceSceneType = "烟雾报警器"
            "14" -> deviceSceneType = "天然气报警器"
            "15", "16" -> deviceSceneType = "智能门锁"
            "16" -> deviceSceneType = "机械手"
            "17" -> deviceSceneType = "智能插座"

            "202", "206" -> deviceSceneType = "遥控器"

            "102" -> deviceSceneType = "PM魔方"
            "19", "20", "21" -> deviceSceneType = "平移控制器"
            else -> deviceSceneType = "zigbee设备"
        }
    }


    /**
     * 针对调光灯带的打开下拉显示
     *
     * @param map
     */
    private fun get_action(map: Map<*, *>): String? {
        var action: String? = ""
        val type = map["type"].toString()
        when (type) {
            "0" -> action = "执行"
            "1", "17", "111" -> action = init_action_light(map["status"] as String?)
            "100" -> action = map["name1"] as String?
            "2" -> action = init_action_tiaoguang(map["status"] as String?, map["dimmer"] as String?)
            "3" -> action = init_action_kongtiao("3", map["status"] as String?, map["mode"] as String?, map["speed"] as String?, map["temperature"] as String?)
            "4" -> action = init_action_curtain(map["status"] as String?, "4")
            "18" -> action = init_action_curtain(map["status"] as String?, "18")
            "5" -> action = init_action_kongtiao("5", map["status"] as String?, map["mode"] as String?, map["speed"] as String?, map["temperature"] as String?)
            "6" -> action = init_action_kongtiao("6", map["status"] as String?, map["mode"] as String?, map["speed"] as String?, map["temperature"] as String?)
            "202", "206" -> action = init_action_wifi_device(map["status"] as String?)
            "15", "16" -> //            case "17":
                action = init_action_smart_door_lock(map["status"] as String?)
            "19", "20", "21" -> action = init_action_smart_pingyi(map["status"] as String?, type)
        }
        return action
    } //
    /**
     * 窗帘
     */
    /**
     * 窗帘
     */
    private fun init_action_curtain(status: String?, type: String): String {
        var action = ""
        when (type) {
            "4" -> action = old_curtain_window(status, action)
            "18" -> action = new_curtain_window(status, action)
        }
        return action
    }

    /**
     * A411-A414
     *
     * @param status
     * @param action
     * @return
     */
    private fun new_curtain_window(status: String?, action: String): String {
        var action = action
        when (status) {
            "0" -> action = "关闭"
            "1" -> action = "打开"
        }
        return action
    }

    /**
     * A401
     *
     * @param status
     * @param action
     * @return
     */
    private fun old_curtain_window(status: String?, action: String): String {
        var action = action
        when (status) {
            "0" -> action = "全部关闭"
            "1" -> action = "全部打开"
            "4" -> action = "内纱打开"
            "6" -> action = "内纱关闭"
            "7" -> action = "外纱关闭"
            "8" -> action = "外纱打开"
        }
        return action
    }

    /**
     * wifi设备
     */
    private fun init_action_wifi_device(status: String?): String {
        var action = ""
        when (status) {
            "0" -> action = "关"
            "1" -> action = "开"
        }
        return action
    }

    /**
     * 智能门锁
     */
    private fun init_action_smart_door_lock(status: String?): String {
        var action = ""
        when (status) {
            "0" -> action = "关闭"
            "1" -> action = "打开"
        }
        return action
    }

    /**
     * 平移控制器
     */
    private fun init_action_smart_pingyi(status: String?, type: String): String {
        var action = ""
        when (type) {
            "19" -> action = common_select_pingyi(status, action, "上升", "下降")
            "20" -> action = common_select_pingyi(status, action, "向左", "向右")
            "21" -> when (status) {
                "1" -> action = "高位"
                "2" -> action = "中位"
                "3" -> action = "低位"
                "0" -> action = "暂停"
            }
        }
        return action
    }

    private fun common_select_pingyi(status: String?, action: String, action1: String, action2: String): String {
        var action = action
        when (status) {
            "1" -> action = action1
            "2" -> action = action2
            "0" -> action = "停"
        }
        return action
    }

    /**
     * 空调
     *
     * @param
     * @param type
     */
    private fun init_action_kongtiao(type: String, status: String?, model: String?, speed: String?, tempature: String?): String {
        var action = ""
        when (status) {
            "0" -> action = "关闭"
            "1" -> action = init_action_air(type, model, speed, tempature)
        }
        return action
    }

    /**
     * 初始化空调动作
     *
     * @param type
     * @param model
     * @param speed
     * @param tempature
     */
    private fun init_action_air(type: String, model: String?, speed: String?, tempature: String?): String {
        val temp = StringBuffer()
        when (speed) {
            "1" -> temp.append("低风")
            "2" -> temp.append("中风")
            "3" -> temp.append("高风")
            "4" -> temp.append("强力")
            "5" -> temp.append("送风")
            "6" -> temp.append("自动")
        }
        //        String temperature = (String) air_control_map.get("temperature");
        temp.append("  $tempature℃")
        when (type) {
            "3" -> init_common_air(model, temp)
            "6" -> common_mode_dinuan(model, temp)
        }
        //        common_doit("action", temp.toString());
        return temp.toString()
    }

    private fun init_common_air(model: String?, temp: StringBuffer) { //亮堂着
        when (model) {
            "1" -> temp.append("  " + "制冷")
            "2" -> temp.append("  " + "制热")
            "3" -> temp.append("  " + "除湿")
            "4" -> temp.append("  " + "自动")
            "5" -> temp.append("  " + "通风")
        }
    }

    private fun common_mode_dinuan(model: String?, temp: StringBuffer) {
        when (model) {
            "1" -> temp.append("  " + "加热")
            "2" -> temp.append("  " + "睡眠")
            "3" -> temp.append("  " + "外出")
        }
    }

    /**
     * 调光灯
     *
     * @param dimmer
     */
    private fun init_action_tiaoguang(status: String?, dimmer: String?): String {
        var action = ""
        when (status) {
            "0" -> action = "关闭"
            "1" -> action = "调光值:$dimmer"
        }
        return action
    }

    override fun onDestroy() {
        super.onDestroy()
        //        common_second();
    }

    /**
     * 灯控
     */
    private fun init_action_light(status: String?): String {
        var action = ""
        when (status) {
            "0" -> action = "关闭"
            "1" -> action = "打开"
            "3" -> action = "切换"
        }
        return action
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                //数据是使用Intent返回
                val intent = Intent()
                //把返回数据存入Intent
                intent.putExtra("result", recordList!!["id"].toString())
                //设置返回数据
                this@OperationRecordsDetailActivity.setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        //把返回数据存入Intent
        intent.putExtra("result", recordList!!["id"].toString())
        //设置返回数据
        this@OperationRecordsDetailActivity.setResult(Activity.RESULT_OK, intent)
        finish()
    }
}