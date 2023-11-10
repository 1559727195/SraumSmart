package com.massky.sraum.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.KeyPowerItemSecondAdapter
import com.massky.sraum.adapter.SelectLinkageItemSecondAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.PullToRefreshLayout
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yxc.barchart.ui.step.StepActivity
import okhttp3.Call
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/5.
 */
class KeyPowerActivity : BaseActivity(), AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.refresh_view)
    var refresh_view: PullToRefreshLayout? = null

    @JvmField
    @BindView(R.id.maclistview_id)
    var maclistview_id: ListView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.panel_txt_promat)
    var panel_txt_promat: TextView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null
    private var dialogUtil: DialogUtil? = null
    private var panelNumber: String? = null
    private val deviceList: MutableList<device> = ArrayList()
    private val deviceActionList: MutableList<Map<*, *>> = ArrayList() //执行动作集合
    private val list_type_index = ArrayList<Map<*, *>>() //收集某种设备类型的索引集合
    private var selectexcutesceneresultadapter: KeyPowerItemSecondAdapter? = null
    private val listint: List<Map<*, *>> = ArrayList()
    private val listintwo: List<Map<*, *>> = ArrayList()
    private var panelType: String? = null
    private val list_map: MutableList<MutableMap<*, *>> = ArrayList()
    private var panelName: String? = null

    //    private var sensor_map: Map<*, *> = HashMap<Any?, Any?>() //传感器map
    private var boxName: String? = null
    private var boxNumber: String? = null
    private val panelType1: String? = null
    private val panelName1: String? = null
    private var panelMAC: String? = null
    private var gatewayMAC: String? = null

    /**
     * 空调
     *
     * @return
     */
    override fun viewId(): Int {
        return R.layout.key_power_act
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        panelNumber = intent.getSerializableExtra("panelNumber") as String
        panelType = intent.getSerializableExtra("panelType") as String
        panelName = intent.getSerializableExtra("panelName") as String
        // sensor_map = intent.getSerializableExtra("sensor_map") as Map<*, *>
        //  intent.putExtra("boxName", (Serializable) listbox.get(position));
        boxName = intent.getSerializableExtra("boxName") as String
        boxNumber = intent.getSerializableExtra("boxNumber") as String
        gatewayMAC = intent.getSerializableExtra("gatewayMac") as String
        if (panelName != null) project_select!!.text = panelName

        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4","ADA1","ADA2","ADA3" -> {
                getData(true, "wifi")
            }
            else -> getData(true, "devices")
        }

        selectexcutesceneresultadapter = KeyPowerItemSecondAdapter(this@KeyPowerActivity,
                list_map)
        maclistview_id!!.adapter = selectexcutesceneresultadapter
        maclistview_id!!.onItemClickListener = this
        //        refresh_view.autoRefresh();
        refresh_view!!.noreleasePull()
    }

    override fun onEvent() {}
    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

    private fun getData(flag: Boolean, actions: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@KeyPowerActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@KeyPowerActivity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        var api = ""

        when (actions) {
            "wifi" -> {
                map["deviceId"] = panelNumber
                api = ApiHelper.sraum_getWifiButtons!!
            }
            "devices" -> {
                api = ApiHelper.sraum_getPanelDevices
                map["boxNumber"] = boxNumber
                map["panelNumber"] = panelNumber
            }
        }

        if (flag) {
            dialogUtil!!.loadDialog()
        }

        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { getData(false, actions) }, this@KeyPowerActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        //                        refresh_view.stopRefresh();

//                        adapter = new PanelAdapter(SelectiveLinkageActivity.this, panelList, mySlidingMenu, accountType
//                                , new PanelAdapter.PanelAdapterListener() {
//                            @Override
//                            public void panel_adapter_listener() {//删除item时刷新界面
//                                getData(false);
//                            }
//                        });
                        panelType = user.panelType
                        panelName = user.panelName
                        panelMAC = user.panelMAC
                        gatewayMAC = user.gatewayMAC
                        deviceList.clear()
                        deviceActionList.clear()
                        list_type_index.clear()
                        loop@ for (position in user.deviceList.indices) {
                            val mapdevice: MutableMap<String, Any?> = HashMap()
                            val type_index: MutableMap<String, Any> = HashMap()
                            mapdevice["type"] = user.deviceList[position].type
                            mapdevice["number"] = user.deviceList[position].number
                            mapdevice["status"] = user.deviceList[position].status
                            mapdevice["dimmer"] = user.deviceList[position].dimmer
                            mapdevice["mode"] = user.deviceList[position].mode
                            mapdevice["temperature"] = user.deviceList[position].temperature
                            mapdevice["speed"] = user.deviceList[position].speed
                            mapdevice["name"] = user.deviceList[position].name
                            mapdevice["energy"] = user.deviceList[position].energy
                            mapdevice["boxName"] = boxName
                            mapdevice["button"] = user.deviceList[position].button
                            mapdevice["panelName"] = user.deviceList[position].panelName
                            mapdevice["boxNumber"] = if (boxNumber == null) "" else boxNumber
                            type_index["type"] = user.deviceList[position].type
                            when (user.deviceList[position].type) {
                                "18","113" -> {
                                    continue@loop
                                }
                            }
                            type_index["index"] = position
                            list_type_index.add(type_index)
                            deviceActionList.add(mapdevice)
                        }
                        deviceList.addAll(user.deviceList)
                        setPicture(panelType)
                        selectexcutesceneresultadapter!!.setlist(list_map)
                        selectexcutesceneresultadapter!!.notifyDataSetChanged()
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun setPicture(type: String?) {
        when (type) {
            "A211", "A212", "A213", "A214",
            "A201", "A202", "A203", "A204" -> {
                light("1")
                panel_txt_promat!!.text = "执行开关"
            }
            "A2A1", "A2A2", "A2A3", "A2A4" -> {
                light("111")
                panel_txt_promat!!.text = "执行开关"
            }
            "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331" -> {
                tiaoguang_light()
                panel_txt_promat!!.text = "执行开关"
            }
            "B401", "B402", "B403" -> {
                smart_move()
                panel_txt_promat!!.text = "平移控制器"
            }
            "A421", "A401", "A411", "A412", "A413", "A414" -> {
                curtain_window()
                panel_txt_promat!!.text = "执行开关"
            }

            "A501", "A511" -> {
                panel_txt_promat!!.text = "选择空调"
                air_control_list(type)
            }
            "A601", "A611" -> {
                panel_txt_promat!!.text = "选择新风"
                air_control_list(type)
            }
            "A701", "A711" -> {
                panel_txt_promat!!.text = "选择地暖"
                air_control_list(type)
            }
            "A801" -> {

            }
            "A901" -> {

            }
            "A902", "AB01" -> {

            }
            "AB04" -> {

            }
            "AC01" -> {

            }
            "AD01" -> {

            }
            "AD02" -> {

            }
            "B001" -> {

            }
            "B101" -> {
                panel_txt_promat!!.text = "执行开关"
                //                air_control_list(type);
                chazuo_door()
            }
            "B201" -> {
                panel_txt_promat!!.text = "选择智能门锁"
                air_control_list(type)
            }
            "B301" -> {
                panel_txt_promat!!.text = "选择机械手"
                air_control_list(type)
            }
            "B501" -> {
                panel_txt_promat!!.text = "选择音乐面板"
                air_control_list(type)
            }
            "AD11", "AD12", "AD13","ADA1","ADA2","ADA3" -> {
                AD_control_lists()
                panel_txt_promat!!.text = "执行开关"
            }
        }
        //        ToastUtil.showToast(SelectiveLinkageDeviceDetailSecondActivity.this, "list_map:" + list_map.size());
    }

    private fun AD_control_lists() {
        for (i in deviceActionList.indices) {
            var name = ""
            when (deviceActionList[i]["type"].toString()) {
                "1","111" -> {
                    name = "路灯控"
                }
                "2","112" -> {
                    name = "路调光灯带"
                }
                "3" -> {
                    name = "路空调"
                }
                "4", "18","113" -> {
                    name = "路窗帘"
                }
                "5" -> {
                    name = "路新风"
                }
                "6" -> {
                    name = "路地暖"
                }
            }
            when (deviceActionList[i]["type"].toString()) {
                "18","113" -> common_second("第" + NumberUtils.number2Chinese(Integer.parseInt(deviceActionList[i]["button"].toString())) + name, deviceActionList[i]["name"].toString(), deviceActionList[i]["type"].toString(), "1", "0", "2", i)
                else -> common("第" + NumberUtils.number2Chinese(Integer.parseInt(deviceActionList[i]["button"].toString())) + name, deviceActionList[i]["name"].toString(), deviceActionList[i]["type"].toString(), i)
            }
        }
    }

    private fun smart_move() {
        for (i in deviceActionList.indices) {
            when (deviceActionList[i]["type"].toString()) {
                "19" -> common_step_s("", i, deviceActionList[i]["type"].toString(),
                        deviceActionList[i]["name"].toString(), "上升", "上升", "下降")
                "20" -> common_step_s("", i, deviceActionList[i]["type"].toString(),
                        deviceActionList[i]["name"].toString(), "向左", "向左", "向右")
                "21" -> common_top_middle("", i, deviceActionList[i]["type"].toString(),
                        deviceActionList[i]["name"].toString())
            }
        }
    }

    private fun common_top_middle(tabname: String, position: Int, type: String, name: String) {//
        for (j in 0..3) {
            val map = HashMap<Any?, Any?>()
            when (j) {
                0 -> {
                    map["name"] = "高位"
                    map["status"] = "1"
                    map["action"] = "高位"
                }
                1 -> {
                    map["name"] = "中位"
                    map["status"] = "2"
                    //                    map.put("action", "关闭");
                    map["action"] = "中位"
                }
                2 -> {
                    map["name"] = "低位"
                    map["status"] = "3"
                    map["action"] = "低位"
                }
                3 -> {
                    map["name"] = "暂停"
                    map["status"] = "0"
                    map["action"] = "暂停"
                }
            }
            common_sensor_map(map, "name1", name, tabname, type, "value", "", "tiaoguang", "", "position", position, "number", deviceActionList[position], list_map)
        }
    }

    private fun common_sensor_map(map: HashMap<Any?, Any?>, name1: String, name2: Any?, tabname2: Any?, type2: Any?, value: String, s: Any?, tiaoguang: String, s2: Any?, position2: String, position3: Any?, number: String, map2: Map<*, *>, list_map: MutableList<MutableMap<*, *>>) {
        map[name1] = name2
        map["tabname"] = tabname2
        map["type"] = type2
        map[value] = s
        map[tiaoguang] = s2
        map[position2] = position3
        map[number] = map2[number]
        //            map.put("status", deviceActionList.get(position).get("status"));
        map["dimmer"] = map2["dimmer"]
        map["mode"] = map2["mode"]
        map["temperature"] = map2["temperature"]
        map["speed"] = map2["speed"]
        map["boxName"] = map2["boxName"]
        //            map.put("name", deviceActionList.get(position).get("name"));
        list_map.add(map)
    }


    private fun common_step_s(tabname: String, position: Int, type: String, name: String, action1: String, action2: String, action3: String) {
        //String 左, String 左2, String 右
        for (j in 0..2) {
            val map = HashMap<Any?, Any?>()
            when (j) {
                0 -> {
                    map["name"] = action1
                    map["status"] = "1"
                    map["action"] = action2
                }
                1 -> {
                    map["name"] = action3
                    map["status"] = "2"
                    //                    map.put("action", "关闭");
                    map["action"] = action3
                }
                2 -> {
                    map["name"] = "暂停"
                    map["status"] = "0"
                    map["action"] = "暂停"
                }
            }
            common_sensor_map(map, "name1", name, tabname, type, "value", "", "tiaoguang", "", "position", position, "number", deviceActionList[position], list_map)
        }
    }

    /**
     * 空调控制
     *
     * @param type
     */
    private fun air_control_list(type: String) {
        for (position in deviceActionList.indices) {
            val map = HashMap<Any?, Any?>()
            map["type"] = deviceActionList[position]["type"]
            map["number"] = deviceActionList[position]["number"]
            map["status"] = deviceActionList[position]["status"]
            map["dimmer"] = deviceActionList[position]["dimmer"]
            map["mode"] = deviceActionList[position]["mode"]
            map["temperature"] = deviceActionList[position]["temperature"]
            map["speed"] = deviceActionList[position]["speed"]
            map["boxName"] = deviceActionList[position]["boxName"]
            when (type) {
                "A501", "A601", "A701", "B501",
                "A511", "A611", "A711" -> map["name"] = deviceActionList[position]["name"]
                "B201" -> {
                    map["name"] = deviceActionList[position]["name"].toString() + "打开"
                    map["name1"] = deviceActionList[position]["name"]
                }
                "B301" -> {
                    map["name"] = deviceActionList[position]["name"].toString() + "关闭"
                    map["name1"] = deviceActionList[position]["name"]
                }
            }
            map["boxName"] = deviceActionList[position]["boxName"]
            list_map.add(map)
        }
        selectexcutesceneresultadapter!!.setlist(list_map)
        selectexcutesceneresultadapter!!.notifyDataSetChanged()
    }

    /**
     * 窗帘控制
     */
    private fun curtain_window() {
        //窗帘控制
        for (i in deviceActionList.indices) {
            when (deviceActionList[i]["type"].toString()) {
                "1" -> common("", deviceActionList[i]["name"].toString(), "1", i)
                "4" -> {
//                    com.ezviz.demo.common("", "内纱", "4", i)
//                    com.ezviz.demo.common("", "外纱", "4", i)
//                    com.ezviz.demo.common("", "全部", "4", i)
                    common("", deviceActionList[i]["name"].toString(), "4", i)
                }
                "18","113" -> common("", deviceActionList[i]["name"].toString(), "18", i)
            }
        }
    }

    /**
     * 调光灯
     */
    private fun tiaoguang_light() {
        for (i in deviceActionList.indices) {
            when (deviceActionList[i]["type"].toString()) {
                "1" -> common("第" + NumberUtils.number2Chinese(i + 1) + "路灯控", deviceActionList[i]["name"].toString(), "1", i)
                "2" -> common("第" + NumberUtils.number2Chinese(i + 1) + "路调光灯带", deviceActionList[i]["name"].toString(), "2", i)
            }
        }
    }

    /**
     * 入墙式智能插座
     */
    private fun chazuo_door() {
        for (i in deviceActionList.indices) {
            when (deviceActionList[i]["type"].toString()) {
                "17" -> common("", deviceActionList[i]["name"].toString(), "17", i)
            }
        }
    }

    /**
     * com.ezviz.demo.common
     *
     * @param
     * @param
     * @param i
     */
    private fun common(tabname: String, name: String, type: String, i: Int) { //"第" + (i + 1) + content
        when (name) {
            "全部" -> common_second(tabname, name, type, "1", "0", "", i)
            "内纱" -> common_second(tabname, name, type, "4", "6", "", i)
            "外纱" -> common_second(tabname, name, type, "8", "7", "", i)
            else -> common_second(tabname, name, type, "1", "0", "3", i)
        }
    }

    /**
     * 设备开关状态status字段
     *
     * @param tabname
     * @param name
     * @param type
     * @param value2
     * @param value4
     * @param position
     */
    private fun common_second(tabname: String, name: String, type: String, value2: String, value4: String, value5: String, position: Int) {
        when (type) {
            "1", "111" -> light_select(tabname, name, type, value2, value4, value5, position)
            else -> default_select(tabname, name, type, value2, value4, position)
        }
    }

    /**
     * 灯控选项
     *
     * @param tabname
     * @param name
     * @param type
     * @param value2
     * @param value4
     * @param value5
     * @param position
     */
    private fun light_select(tabname: String, name: String, type: String, value2: String, value4: String, value5: String, position: Int) {
//        for (j in 0..1) {
        val map = HashMap<Any?, Any?>()

        map["name"] = name
        map["status"] = value2

        if (type == "4") {
            return
            map["name1"] = "窗帘"
        } else {
            map["name1"] = name
        }
        map["tabname"] = tabname
        map["type"] = type
        map["value"] = ""
        map["tiaoguang"] = ""
        map["position"] = position
        map["number"] = deviceActionList[position]["number"]
        //            map.put("status", deviceActionList.get(position).get("status"));
        map["dimmer"] = deviceActionList[position]["dimmer"]
        map["mode"] = deviceActionList[position]["mode"]
        map["temperature"] = deviceActionList[position]["temperature"]
        map["speed"] = deviceActionList[position]["speed"]
        map["boxName"] = deviceActionList[position]["boxName"]
        //            map.put("name", deviceActionList.get(position).get("name"));
        list_map.add(map)
//        }
    }

    /**
     * 默认选项
     *
     * @param tabname
     * @param name
     * @param type
     * @param value2
     * @param value4
     * @param position
     */
    private fun default_select(tabname: String, name: String, type: String, value2: String, value4: String, position: Int) {

        val map = HashMap<Any?, Any?>()

        map["name"] = name
        map["status"] = value2
        if (type == "4") {
            return
            map["name1"] = "窗帘"
        } else {
            map["name1"] = name
        }
        map["tabname"] = tabname
        map["type"] = type
        map["value"] = ""
        map["tiaoguang"] = ""
        map["position"] = position
        map["number"] = deviceActionList[position]["number"]
        //            map.put("status", deviceActionList.get(position).get("status"));
        map["dimmer"] = deviceActionList[position]["dimmer"]
        map["mode"] = deviceActionList[position]["mode"]
        map["temperature"] = deviceActionList[position]["temperature"]
        map["speed"] = deviceActionList[position]["speed"]
        map["boxName"] = deviceActionList[position]["boxName"]
        //            map.put("name", deviceActionList.get(position).get("name"));
        list_map.add(map)

    }

    /**
     * 普通灯
     */
    private fun light(type: String) {
        //普通灯
        for (i in deviceActionList.indices) {
            common("第" + NumberUtils.number2Chinese(i + 1) + "路灯控", deviceActionList[i]["name"].toString(), type, i)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {

        when (panelType) {
            "A421",
            "A211", "A212", "A213", "A214",
            "A2A1", "A2A2", "A2A3", "A2A4",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "B101", "AD11", "AD12", "AD13","ADA1","ADA2","ADA3" -> back_onclick(position, view)
            "A401", "A411", "A412", "A413", "A414", "B501" -> back_onclick_window(position)
            "A511" -> send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "A611" -> send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "A711" -> send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "A801" -> {
            }
            "A901" -> {
            }
            "A902", "AB01" -> {
            }
            "AB04" -> {
            }
            "AC01" -> {
            }
            "AD01" -> {
            }
            "AD02" -> {
            }
            "B001" -> {
            }
            "B201" -> //            case "B101":
                send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "B301", "B401", "B402", "B403" -> send_map_to_second(list_map[position] as MutableMap<Any, Any>)
        }
    }

    /**
     * 窗帘设备点击回调
     *
     * @param position
     */
    private fun back_onclick_window(position: Int) {
        val map: Map<*, *> = list_map[position]
        var index_device = 0
        for (i in deviceActionList.indices) {
            if (map["type"].toString() == deviceActionList[i]["type"]) {
                index_device = i
                break
            }
        }
        when (map["type"].toString()) {
            "1", "2" -> send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "3" -> {

            }
            "4", "18", "22","113" -> //                init_device_upgrade_action(index_device, "status", map.get("status").toString());
                send_map_to_second(list_map[position] as MutableMap<Any, Any>)
            "5" -> {

            }
            "6" -> {

            }
        }
    }

    /**
     * 点击之后的返回响应
     *
     * @param position
     * @param view
     */
    private fun back_onclick(position: Int, view: View) {
        val map: Map<*, *> = list_map[position]
//        when (map["type"].toString()) {
//            "1", "17", "111" -> light_and_tiaogaung(position, map)
//            "2" -> light_and_tiaogaung_second(position, map, view)
//            "3" -> {
//            }
//            "4" -> {
//            }
//            "5" -> {
//            }
//            "6" -> {
//            }
//        }
        send_map_to_second(list_map.get(position) as MutableMap<Any, Any>)
    }

    /**
     * 针对调光灯带的打开下拉显示
     *
     * @param position
     * @param map
     * @param view
     */
//    private fun light_and_tiaogaung_second(position: Int, map: Map<*, *>, view: View) {
//        when (map["status"].toString()) {
//            "1" -> {
//                //                String open = (String) view.getTag();
//                val open = list_map[position]["tiaoguang"] as String?
//                if (open != null) {
//                    when (open) {
//                        "open" -> tiaoguang_open(position, map, view)
//                        "close" -> tiaoguang_close(position, map, view)
//                        else -> tiaoguang_open(position, map, view)
//                    }
//                } else { //去执行，选择调光值
//
////                    Log.e("zhu", "value:" + list_map.get(position).get("value"));
//                    (list_map[position] as HashMap<String, Any>)["dimmer"] = list_map[position]["value"] as Any
//                    (list_map[position] as HashMap<String, Any>)["action"] = "调光值:" +
//                            list_map[position]["value"] //拼接调光值
//                    //最终要选择发送的map的地方
//                    send_map_to_second(list_map[position] as MutableMap<Any, Any>)
//                }
//            }
//            "0" -> //                Log.e("zhu", "value:" + list_map.get(position).get("value"));
////                list_map.get(position).put("dimmer", list_map.get(position).get("value"));
//                send_map_to_second(list_map[position] as MutableMap<Any, Any>)
//        }
//    }

    /**
     * 最终发送位置
     *
     * @param map
     */
    private fun send_map_to_second(map: MutableMap<Any, Any>) {
        var intent: Intent? = null
        //        //传感器参数
//        map.put("sensorType", sensor_map.get("sensorType"));
//        map.put("sensorId", sensor_map.get("sensorId"));
//        map.put("sensorName", sensor_map.get("sensorName"));
//        map.put("sensorCondition", sensor_map.get("sensorCondition"));
//        map.put("sensorMinValue", sensor_map.get("sensorMinValue"));
//        map.put("sensorMaxValue", sensor_map.get("sensorMaxValue"));
        if (panelNumber != null) {
            map["panelNumber"] = panelNumber!!
        }
        if (panelMAC != null) {
            map["panelMac"] = panelMAC!!
        }
        if (gatewayMAC != null) {
            map["gatewayMac"] = gatewayMAC!!
        }

        intent = Intent(
                this@KeyPowerActivity,
                StepActivity::class.java
        )
        intent.putExtra("device_map", map as Serializable)
        intent.putExtra("in_type", "device_in")
        // intent.putExtra("sensor_map", sensor_map as Serializable)
        startActivity(intent)

    }
//        when (map["type"].toString()) {
//            "3", "5", "6" -> {
//                intent = Intent(
//                        this@KeyPowerActivity,
//                        AirLinkageControlActivity::class.java
//                )
//                val map_panel = HashMap<Any?, Any?>()
//                map_panel["panelType"] = panelType
//                map_panel["panelName"] = panelName
//                map_panel["panelMac"] = panelMAC
//                map_panel["gatewayMac"] = gatewayMAC
//                map_panel["panelNumber"] = panelNumber
//                map_panel["boxNumber"] = boxNumber
//                intent.putExtra("panel_map", map_panel as Serializable)
//                intent.putExtra("air_control_map", map as Serializable)
//                //intent.putExtra("sensor_map", sensor_map as Serializable)
//                startActivity(intent)
//            }
//            "15", "16" -> init_intent(map)
//            else -> {
//                //
//                AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity::class.java)
//                AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
//                intent = Intent(
//                        this@KeyPowerActivity,
//                        EditLinkDeviceResultActivity::class.java
//                )
//                intent.putExtra("device_map", map as Serializable)
//                // intent.putExtra("sensor_map", sensor_map as Serializable)
//                startActivity(intent)
//                finish()
//            }


//   private fun init_intent(air_control_map: MutableMap<Any, Any>) {
//        init_action(air_control_map)
//        AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity::class.java)
//        AppManager.getAppManager().finishActivity_current(KeyPowerActivity::class.java)
//        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
//        val intent = Intent(
//                this@KeyPowerActivity,
//                EditLinkDeviceResultActivity::class.java)
//        intent.putExtra("device_map", air_control_map as Serializable)
//        //  intent.putExtra("sensor_map", sensor_map as Serializable)
//        finish()
//        startActivity(intent)
//    }

//    private fun init_action(air_control_map: MutableMap<Any, Any>) {
//        when (air_control_map["type"].toString()) {
//            "15" -> {
//                air_control_map["action"] = "打开"
//                air_control_map["status"] = "1"
//            }
//            "16" -> {
//                air_control_map["action"] = "关闭"
//                air_control_map["status"] = "0"
//            }
//        }
//    }

    /**
     * 调光灯关闭
     *
     * @param position
     * @param map
     * @param view
     */
//    private fun tiaoguang_close(position: Int, map: Map<*, *>, view: View) { //关闭时，是应该关闭哪一个灯带的,告诉它这是第几路的灯带列表，
//        val first: MutableList<MutableMap<*, *>> = ArrayList()
//        val second: MutableList<MutableMap<*, *>> = ArrayList()
//        (list_map[position] as HashMap<String, Any>)["tiaoguang"] = "open"
//        Log.e("zhu", "position:$position,open-close:open")
//        for (i in 0 until position + 1) {
//            first.add(list_map[i])
//        }
//        for (i in position + 6 until list_map.size) {
//            second.add(list_map[i])
//        }
//        list_map.clear()
//        list_map.addAll(first)
//        list_map.addAll(second)
//        selectexcutesceneresultadapter!!.setlist(list_map)
//        selectexcutesceneresultadapter!!.notifyDataSetChanged()
//    }

    /**
     * 调光窗体展开
     *
     * @param position
     * @param map
     * @param view
     */
//    private fun tiaoguang_open(position: Int, map: Map<*, *>, view: View) {
//        val first: MutableList<MutableMap<*, *>> = ArrayList()
//        (list_map[position] as HashMap<String, Any>)["tiaoguang"] = "close"
//        Log.e("zhu", "position:$position,open-close:close")
//        for (i in 0..4) {
//            val map_value = HashMap<Any?, Any?>()
//            map_value["value"] = "" + (i + 1) * 20
//            map_value["name"] = map["name"]
//            common_sensor_map(map_value, "status", map["status"], map["tabname"], map["type"], "name1", map["name1"], "action", map["action"], "number", map["number"], "status", map, first)
//        }
//        list_map.addAll(position + 1, first)
//        selectexcutesceneresultadapter!!.setlist(list_map)
//        selectexcutesceneresultadapter!!.notifyDataSetChanged()
//    }

    /**
     * 调光下面的灯的status控制
     *
     * @param position
     * @param map
     */
//    private fun light_and_tiaogaung(position: Int, map: Map<*, *>) {
//        var index_device = 0
//        index_device = if (position % 2 == 0) {
//            position / 2
//        } else {
//            (position - 1) / 2
//        }
//        //        init_device_upgrade_action(index_device, "status", map.get("status").toString());
//        send_map_to_second(list_map[position] as MutableMap<Any, Any>)
//    }

    //    /**
//     * 更新执行设备的动作
//     */
//    private void init_device_upgrade_action(int position, String type, String content) {
////                mapdevice.put("type", doit == true ? content : deviceList.get(position).type);
//        deviceActionList.get(position).put(type, content);
////        deviceActionList.add(mapdevice);
//    }
    override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout) {
//        refresh_view.noreleasePull();
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED)
    }

    override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout) {}

}