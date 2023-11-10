package com.massky.sraum.activity

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.SelectLinkageAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.PullToRefreshLayout
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by zhu on 2018/6/13.
 */
class SelectiveLinkageActivity : BaseActivity(), AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
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
    @BindView(R.id.rel_scene_set)
    var rel_scene_set: RelativeLayout? = null

    @JvmField
    @BindView(R.id.scene_linear)
    var scene_linear: LinearLayout? = null
    private var selectexcutesceneresultadapter: SelectLinkageAdapter? = null
    private var list_hand_scene: List<Map<*, *>> = ArrayList()
    private val mHandler = Handler()

    //    String[] again_elements = {"1", "3"
    //            };
    private val listint: MutableList<Int> = ArrayList()
    private val listintwo: MutableList<Int> = ArrayList()
    private val list_bool: List<Boolean> = ArrayList()
    private val position = 0
    private var dialogUtil: DialogUtil? = null

    //    private List<User.device> list = new ArrayList<>();
    private val listtype: MutableList<String?> = ArrayList<String?>()
    private val panelList = CopyOnWriteArrayList<ConcurrentMap<*, *>>()
    private var map_link: Map<Any?, Any?>? = HashMap<Any?, Any?>()
    private val listpanelNumber: MutableList<String?> = ArrayList<String?>()
    private val listpanelName: MutableList<String?> = ArrayList<String?>()
    private val listbox: MutableList<String?> = ArrayList<String?>()

    //获取面板下的设备按钮列表
//    private val deviceList: MutableList<User.device> = ArrayList()
    private val deviceActionList: MutableList<Map<*, *>> = ArrayList() //执行动作集合


    override fun viewId(): Int {
        return R.layout.selective_linkage_lay
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        rel_scene_set!!.setOnClickListener(this)
        //        onData();
        next_step_txt!!.setOnClickListener(this)
        maclistview_id!!.onItemClickListener = this
        refresh_view!!.setOnRefreshListener(this)
        //        refresh_view.autoRefresh();
//        dialogUtil = new DialogUtil(this);
        map_link = intent.getSerializableExtra("link_map") as Map<Any?, Any?>
        if (map_link == null) return
        val action = map_link!!.get("action") as String?
        when (action) {
            "执行" -> scene_linear!!.visibility = View.GONE
            else -> scene_linear!!.visibility = View.VISIBLE
        }
    }

    override fun onEvent() {}

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    //    R.drawable.magnetic_door_s,
    //    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
    //    R.drawable.emergency_button_s
    override fun onData() {
        list_hand_scene = ArrayList()
        selectexcutesceneresultadapter = SelectLinkageAdapter(this@SelectiveLinkageActivity,
                panelList, listint, listintwo)
        maclistview_id!!.adapter = selectexcutesceneresultadapter
        getData(true)
    }

    private fun getData(flag: Boolean) {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = TokenUtil.getToken(this@SelectiveLinkageActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@SelectiveLinkageActivity,
                "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        if (flag) {
            dialogUtil!!.loadDialog()
        }
        MyOkHttp.postMapObject(ApiHelper.sraum_getLinkController, map,
                object : Mycallback(AddTogglenInterfacer { getData(false) }, this@SelectiveLinkageActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        panelList.clear()
                        //                        panelList = user.panelList;
                        listtype.clear()
                        listint.clear()
                        listintwo.clear()
                        listpanelNumber.clear()
                        for (ud in user.panelList) {
                            val map = ConcurrentHashMap<Any?, Any?>()
                            map["panelName"] = ud.panelName
                            map["panelNumber"] = ud.panelNumber
                            map["boxNumber"] = ud.boxNumber
                            map["boxName"] = ud.boxName
                            map["panelType"] = ud.panelType
                            map["panelMac"] = ud.panelMac
                            map["gatewayMac"] = ud.gatewayMac
                            map["controllerId"] = ""
                            panelList.add(map)
                        }
                        if (user.wifiList != null) for (ud in user.wifiList) {
                            val map = ConcurrentHashMap<Any?, Any?>()
                            map["panelName"] = ud.name
                            map["panelNumber"] = ud.number
                            map["boxNumber"] = ""
                            map["panelType"] = ud.type
                            map["boxName"] = "WIFI"
                            map["controllerId"] = ud.controllerId
                            map["gatewayMac"] = ""
                            panelList.add(map)
                        }
                        for (map in panelList) {
                            listtype.add(map["panelType"].toString())
                            listpanelNumber.add(map["panelNumber"].toString())
                            listpanelName.add(map["panelName"].toString())
                            listbox.add(map["boxName"].toString())
                            setPicture(map["panelType"].toString(), map)
                        }
                        selectexcutesceneresultadapter!!.setlist(panelList, listint, listintwo)
                        selectexcutesceneresultadapter!!.notifyDataSetChanged()
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun setPicture(type: String, map: Map<*, *>) {
        when (type) {

            "A211" -> {//一灯控0场景
                listint.add(R.drawable.icon_type_yijiandkwcj_40)
                listintwo.add(R.drawable.icon_type_yijiandkwcj_40)
            }
            "A212" -> {//两灯控0场景
                listint.add(R.drawable.icon_type_liangjiandkwcj_40)
                listintwo.add(R.drawable.icon_type_liangjiandkwcj_40)
            }

            "A213" -> {//三灯控0场景
                listint.add(R.drawable.icon_type_sanjiandkwcj_40)
                listintwo.add(R.drawable.icon_type_sanjiandkwcj_40)
            }


            "A214" -> {//四灯控0场景
                listint.add(R.drawable.icon_type_sijiandkwcj_40)
                listintwo.add(R.drawable.icon_type_sijiandkwcj_40)
            }

            "A201", "A2A1" -> {
                listint.add(R.drawable.icon_type_yijiandk_40)
                listintwo.add(R.drawable.icon_type_yijiandk_40)
            }
            "A202", "A2A2" -> {
                listint.add(R.drawable.icon_type_liangjiandk_40)
                listintwo.add(R.drawable.icon_type_liangjiandk_40)
            }
            "A203", "A2A3" -> {
                listint.add(R.drawable.icon_type_sanjiandk_40)
                listintwo.add(R.drawable.icon_type_sanjiandk_40)
            }
            "A204", "A2A4" -> {
                listint.add(R.drawable.icon_type_sijiandk_40)
                listintwo.add(R.drawable.icon_type_sijiandk_40)
            }
            "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331" -> {
                listint.add(R.drawable.dimminglights)
                listintwo.add(R.drawable.dimminglights)
            }
            "A401", "A411", "A412", "A413", "A414", "A421","ADB2" -> {
                listint.add(R.drawable.home_curtain)
                listintwo.add(R.drawable.home_curtain)
            }
            "A501", "A511" -> {
                listint.add(R.drawable.icon_kongtiao_40)
                listintwo.add(R.drawable.icon_kongtiao_40)
            }
            "A801" -> {
                listint.add(R.drawable.icon_type_menci_40)
                listintwo.add(R.drawable.icon_type_menci_40)
            }
            "A901","A911" -> {
                listint.add(R.drawable.icon_type_rentiganying_40)
                listintwo.add(R.drawable.icon_type_rentiganying_40)
            }
            "AB01" -> {
                listint.add(R.drawable.icon_type_yanwucgq_40)
                listintwo.add(R.drawable.icon_type_yanwucgq_40)
            }
            "A902" -> {
                listint.add(R.drawable.icon_type_toa_40)
                listintwo.add(R.drawable.icon_type_toa_40)
            }
            "AE02" -> {//光照传感器
                listint.add(R.drawable.icon_type_guangzhao_70)
                listintwo.add(R.drawable.icon_type_guangzhao_70)
            }
            "AB04" -> {
                listint.add(R.drawable.icon_type_tianranqibjq_40)
                listintwo.add(R.drawable.icon_type_tianranqibjq_40)
            }
            "AC01" -> {
                listint.add(R.drawable.icon_type_shuijin_40)
                listintwo.add(R.drawable.icon_type_shuijin_40)
            }
            "AD01", "AD03" -> {
                listint.add(R.drawable.icon_type_pm25_40)
                listintwo.add(R.drawable.icon_type_pm25_40)
            }
            "AD02" -> {
                listint.add(R.drawable.icon_pmmofang_40)
                listintwo.add(R.drawable.icon_pmmofang_40)
            }
            "B001" -> {
                listint.add(R.drawable.icon_type_jinjianniu_40)
                listintwo.add(R.drawable.icon_type_jinjianniu_40)
            }
            "B101", "ADB1" -> {
                listint.add(R.drawable.icon_type_zhinengchazuo_40)
                listintwo.add(R.drawable.icon_type_zhinengchazuo_40)
            }
            "网关" -> {
                listint.add(R.drawable.icon_type_wangguan_40)
                listintwo.add(R.drawable.icon_type_wangguan_40)
            }
            "B201" -> {
                listint.add(R.drawable.icon_type_zhinengmensuo_40)
                listintwo.add(R.drawable.icon_type_zhinengmensuo_40)
            }
            "AA02" -> {
                listint.add(R.drawable.icon_type_hongwaizfq_40)
                listintwo.add(R.drawable.icon_type_hongwaizfq_40)
            }
            "AA03" -> {
                listint.add(R.drawable.icon_type_shexiangtou_40)
                listintwo.add(R.drawable.icon_type_shexiangtou_40)
            }
            "AA04" -> {
                listint.add(R.drawable.icon_type_keshimenling_40)
                listintwo.add(R.drawable.icon_type_keshimenling_40)
            }
            "A611", "A601" -> {
                listint.add(R.drawable.icon_xinfengmokuai_40)
                listintwo.add(R.drawable.icon_xinfengmokuai_40)
            }
            "A711", "A701" -> {
                listint.add(R.drawable.icon_dinuanmokuai_40)
                listintwo.add(R.drawable.icon_dinuanmokuai_40)
            }
            "B301" -> {
                listint.add(R.drawable.icon_jixieshou_40)
                listintwo.add(R.drawable.icon_jixieshou_40)
            }
            "B401" -> {
                listint.add(R.drawable.icon_zhinengshengjiang_40)
                listintwo.add(R.drawable.icon_zhinengshengjiang_40)
            }
            "B402" -> {
                listint.add(R.drawable.icon_zhinengpingyi_40)
                listintwo.add(R.drawable.icon_zhinengpingyi_40)
            }
            "B403" -> {
                listint.add(R.drawable.icon_zhinenggaozhongdii_40)
                listintwo.add(R.drawable.icon_zhinenggaozhongdii_40)
            }
            "B501" -> {
                listint.add(R.drawable.icon_yinyuemianban_40)
                listintwo.add(R.drawable.icon_yinyuemianban_40)
            }
            "AD11", "ADA1" -> {
                listint.add(R.drawable.icon_type_yikaimb_40)
                listintwo.add(R.drawable.icon_type_yikaimb_40)
            }
            "AD12", "ADA2" -> {
                listint.add(R.drawable.icon_type_liangkaimb_40)
                listintwo.add(R.drawable.icon_type_liangkaimb_40)
            }
            "AD13", "ADA3" -> {
                listint.add(R.drawable.icon_type_sankaimb_40)
                listintwo.add(R.drawable.icon_type_sankaimb_40)
            }
            else -> {
                listint.add(R.drawable.defaultpic)
                listintwo.add(R.drawable.defaultpic)
            }
        }
    }

    override fun onClick(v: View) {
        var intent: Intent? = null
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
                intent = Intent(this@SelectiveLinkageActivity, UnderWaterActivity::class.java)
                //                intent.putExtra("type", (Serializable) again_elements[position]);
                startActivityForResult(intent, REQUEST_SENSOR)
            }
            R.id.rel_scene_set -> {
                intent = Intent(this@SelectiveLinkageActivity,
                        ExcuteSomeOneSceneActivity::class.java)
                intent.putExtra("sensor_map", map_link as Serializable?)
                startActivity(intent)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        var intent: Intent? = null
        when (listtype[position]) {
            "B501" -> {
                getAir501Data(listpanelNumber[position], listbox[position],
                        if (panelList[position]["boxNumber"]
                                == null) "" else panelList[position]["boxNumber"].toString(), "music")
            }
            "A501", "A601", "A701" -> getAir501Data(listpanelNumber[position], listbox[position],
                    if (panelList[position]["boxNumber"]
                            == null) "" else panelList[position]["boxNumber"].toString(), "air")
            "AA02" -> {
                intent = Intent(this@SelectiveLinkageActivity, SelectLinkageYaoKongQiActivity::class.java)
                //            intent.putExtra("type", (Serializable) listtype.get(position));
                intent.putExtra("panelNumber", listpanelNumber[position] as Serializable?)
                intent.putExtra("panelType", listtype[position] as Serializable?)
                intent.putExtra("panelName", listpanelName[position] as Serializable?)
                //传感器参数
//                Map mapdevice = new HashMap();
//                mapdevice.put("sensorType", map_link.get("deviceType"));
//                mapdevice.put("sensorId", map_link.get("deviceId"));
//                mapdevice.put("sensorName",map_link.get("name"));
//                mapdevice.put("sensorCondition", map_link.get("condition"));
//                mapdevice.put("sensorMinValue", map_link.get("minValue"));
//                mapdevice.put("sensorMaxValue", map_link.get("maxValue"));
                intent.putExtra("sensor_map", map_link as Serializable?)
                intent.putExtra("boxName", if (listbox[position] as Serializable? == null) "" else listbox[position])
                intent.putExtra("boxNumber", "")
                startActivity(intent)
            }
            else -> base_Activity(position)
        }
//        SelectSensorSingleAdapter.ViewHolderContentType viewHolder = (SelectSensorSingleAdapter.ViewHolderContentType) view.getTag();
//        viewHolder.checkbox.toggle();// 把CheckBox的选中状态改为当前状态的反,gridview确保是单一选
    }

    private fun base_Activity(position: Int) {
        when (listtype[position]) {
            "A421",
            "A211", "A212", "A213", "A214",
            "A2A1", "A2A2", "A2A3", "A2A4", "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A411", "A412", "A413", "A414", "A501", "A511", "A801", "A901", "AB01", "A902", "AB04", "AC01", "AD01", "AD03", "AD02", "B001", "B101", "网关",
            "B201", "AA02", "AA03", "AA04", "A611", "A601", "A711", "A701", "B301", "B401", "B402", "B403",
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3", "ADB1","ADB2","A801","A901","A911","A902","AB01","AB04","AC01" -> {
                //获取按钮列表
                var panelType = if (listtype[position] == null) "" else listtype[position]
                var boxNumber = if (panelList[position]["boxNumber"]
                        == null) "" else panelList[position]["boxNumber"].toString()

                var boxName = if (listbox[position] == null) "" else listbox[position]
                var panelNumber = listpanelNumber[position]
                when (panelType) {
                    "A2A1",
                    "A2A2",
                    "A2A3",
                    "A2A4", "ADA1", "ADA2", "ADA3", "ADB1" ,"ADB2"-> {
                        getDevicesData(true, "wifi", boxNumber, boxName, panelNumber)
                    }
                    else -> getDevicesData(true, "devices", boxNumber, boxName, panelNumber)
                }
            }
            else -> {

            }
        }
    }

    /**
     * 空调面板501
     *
     * @param
     * @param boxName
     */
    private fun getAir501Data(panelNumber: String?, boxName: String?, boxNumber: String, action: String) {
        val map: MutableMap<String, Any?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@SelectiveLinkageActivity, "areaNumber", "") as String
        dialogUtil!!.loadDialog()
        val mapdevice: Map<String, String> = HashMap()
        map["token"] = TokenUtil.getToken(this@SelectiveLinkageActivity)
        map["boxNumber"] = boxNumber
        map["panelNumber"] = panelNumber
        map["areaNumber"] = areaNumber
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                object : Mycallback(AddTogglenInterfacer { getData(false) }, this@SelectiveLinkageActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        val list: MutableList<Map<*, *>> = ArrayList()
                        //面板的详细信息
                        val panelType = user.panelType
                        val panelName = user.panelName
                        val panelMAC = user.panelMAC
                        val gatewayMAC = user.gatewayMAC
                        for (position in user.deviceList.indices) {
                            val mapdevice: MutableMap<String, Any?> = HashMap()
                            mapdevice["type"] = user.deviceList[position].type
                            mapdevice["number"] = user.deviceList[position].number
                            mapdevice["status"] = user.deviceList[position].status
                            mapdevice["dimmer"] = user.deviceList[position].dimmer
                            mapdevice["mode"] = user.deviceList[position].mode
                            mapdevice["temperature"] = user.deviceList[position].temperature
                            mapdevice["speed"] = user.deviceList[position].speed
                            mapdevice["name"] = user.deviceList[position].name
                            mapdevice["panelName"] = user.deviceList[position].panelName
                            mapdevice["button"] = user.deviceList[position].button
                            mapdevice["boxName"] = boxName

//                            //传感器参数
//                            mapdevice.put("sensorType", map_link.get("deviceType"));
//                            mapdevice.put("sensorId", map_link.get("deviceId"));
//                            mapdevice.put("sensorName", map_link.get("name"));
//                            mapdevice.put("sensorCondition", map_link.get("condition"));
//                            mapdevice.put("sensorMinValue", map_link.get("minValue"));
//                            mapdevice.put("sensorMaxValue", map_link.get("maxValue"));

                            //
                            list.add(mapdevice)
                        }

                        val map_panel = HashMap<Any?, Any?>()
                        map_panel["panelType"] = panelType
                        map_panel["panelName"] = panelName
                        map_panel["panelMac"] = panelMAC
                        map_panel["gatewayMac"] = gatewayMAC
                        map_panel["panelNumber"] = panelNumber
                        map_panel["boxNumber"] = boxNumber
                        var intent: Intent? = null
                        when (action) {
                            "air" -> {
                                intent = Intent(
                                        this@SelectiveLinkageActivity,
                                        AirLinkageControlActivity::class.java
                                )
                                intent.putExtra("air_control_map", list[0] as Serializable)
                            }
                            "music" -> {
                                intent = Intent(
                                        this@SelectiveLinkageActivity,
                                        MusicLinkageControlActivity::class.java
                                )
                                intent.putExtra("music_control_map", list[0] as Serializable)
                            }
                        }

                        intent!!.putExtra("panel_map", map_panel as Serializable)
                        intent!!.putExtra("sensor_map", map_link as Serializable?)
                        startActivity(intent)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }


    private fun getDevicesData(flag: Boolean, actions: String, boxNumber: String?, boxName: String?,
                               panelNumber: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@SelectiveLinkageActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@SelectiveLinkageActivity, "areaNumber", "") as String
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
                object : Mycallback(AddTogglenInterfacer { getDevicesData(false, actions, boxNumber, boxName, panelNumber) }, this@SelectiveLinkageActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        var panelType = user.panelType
                        var panelName = user.panelName
                        var panelMAC = user.panelMAC
                        var gatewayMAC = user.gatewayMAC
                        deviceActionList.clear()
                        for (position in user.deviceList.indices) {
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
                            mapdevice["boxName"] = boxName
                            mapdevice["button"] = user.deviceList[position].button
                            mapdevice["panelName"] = user.deviceList[position].panelName
                            mapdevice["boxNumber"] = if (boxNumber == null) "" else boxNumber
                            type_index["type"] = user.deviceList[position].type
                            type_index["index"] = position
                            deviceActionList.add(mapdevice)
                        }
                        if(deviceActionList == null || deviceActionList.size == 0) return
                        to_activity(panelNumber, panelType, panelName, boxName, gatewayMAC, boxNumber,panelMAC)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun to_activity(panelNumber: String?, panelType: String?, panelName: String?, boxName: String?,
                            gatewayMAC: String?, boxNumber: String?, panelMAC: String) {
        var intent = Intent(this@SelectiveLinkageActivity, SelectiveLinkageDeviceDetailSecondActivity::class.java)
        intent.putExtra("panelNumber", panelNumber)
        intent.putExtra("panelType", panelType)
        intent.putExtra("panelName", panelName)
        intent.putExtra("boxName", boxName)
        intent.putExtra("gatewayMac", if(gatewayMAC == null) "" else gatewayMAC)
        intent.putExtra("panelMAC",if(panelMAC == null) "" else panelMAC)
        //boxNumber
        intent.putExtra("boxNumber", boxNumber)
        intent.putExtra("sensor_map", map_link as Serializable?)
        intent.putExtra("deviceActionList", deviceActionList as Serializable?)
        startActivityForResult(intent, REQUEST_SENSOR)
    }

    override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout) {
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED)
        getData(true)
    }

    override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout) {}

    companion object {
        private const val REQUEST_SENSOR = 101
    }
}