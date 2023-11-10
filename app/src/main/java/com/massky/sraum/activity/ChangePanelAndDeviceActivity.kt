package com.massky.sraum.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.NormalAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2017/10/27.
 */
class ChangePanelAndDeviceActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: ImageView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null
    private val popupWindow: PopupWindow? = null
    private val device_name: String? = null

    @JvmField
    @BindView(R.id.light_control_panel)
    var light_control_panel: LinearLayout? = null

    @JvmField
    @BindView(R.id.window_linear)
    var window_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.onekey_device)
    var onekey_device: TextView? = null

    @JvmField
    @BindView(R.id.twokey_device)
    var twokey_device: TextView? = null

    @JvmField
    @BindView(R.id.threekey_device)
    var threekey_device: TextView? = null

    @JvmField
    @BindView(R.id.fourkey_device)
    var fourkey_device: TextView? = null

    @JvmField
    @BindView(R.id.onekey_device_txt)
    var onekey_device_txt: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.twokey_device_txt)
    var twokey_device_txt: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.threekey_device_txt)
    var threekey_device_txt: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.fourkey_device_txt)
    var fourkey_device_txt: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.linear_one_only)
    var linear_one_only: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_two_only)
    var linear_two_only: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_three_only)
    var linear_three_only: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_four_only)
    var linear_four_only: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelname)
    var panelname: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.findButton_four)
    var findButton_four: ImageView? = null

    @JvmField
    @BindView(R.id.findButton_three)
    var findButton_three: ImageView? = null

    @JvmField
    @BindView(R.id.findButton_two)
    var findButton_two: ImageView? = null

    @JvmField
    @BindView(R.id.maclistview_id_condition)
    var list_view: ListViewForScrollView_New? = null

    @JvmField
    @BindView(R.id.list_for_air_mode)
    var list_for_air_mode: LinearLayout? = null

    @JvmField
    @BindView(R.id.findButton_one)
    var findButton_one: ImageView? = null

    @JvmField
    @BindView(R.id.find_panel_btn)
    var find_panel_btn: ImageView? = null

    private val panelList: List<panellist> = ArrayList()
    private var dialogUtil: DialogUtil? = null
    private var deviceList: List<device>? = ArrayList()
    private var panelType: String? = null
    private var panelName: String? = null
    private var panelNumber: String? = null
    private var deviceNumber: String? = null
    private var panelMAC: String? = null
    private var onekey_device_txt_str: String? = null
    private var twokey_device_txt_str: String? = null
    private var threekey_device_txt_str: String? = null
    private var fourkey_device_txt_str: String? = null
    private var device_index = 0
    private var isPanelAndDeviceSame = false
    private var findpaneltype: String? = null
    private val upgradete = false
    private var boxNumber: String? = null
    private var input_panel_name_edit_txt_str: String? = null
    private var areaNumber: String? = null
    override fun viewId(): Int {
        return R.layout.changepanel_and_device
    }

    override fun onView() {// StatusUtils.setFullToStatusBar
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
        //根据面板类型，显示不同的设备列表UI
        get_panel_detail()
        //调取面板下的设备信息
//        getPanel_devices();
        show_device_from_panel(panelType)
        panel_and_device_information()
        //        save_panel.setOnClickListener(this);
//        backrela_id.setOnClickListener(this);
        panelname!!.setClearIconVisible(false)
        onekey_device_txt!!.setClearIconVisible(false)
        twokey_device_txt!!.setClearIconVisible(false)
        threekey_device_txt!!.setClearIconVisible(false)
        fourkey_device_txt!!.setClearIconVisible(false)
        //        findpanel.setOnClickListener(this);
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        btn_login_gateway!!.setOnClickListener(this)
        findButton_four!!.setOnClickListener(this)
        findButton_three!!.setOnClickListener(this)
        findButton_two!!.setOnClickListener(this)
        findButton_one!!.setOnClickListener(this)
        find_panel_btn!!.setOnClickListener(this)
    }

    override fun onData() {}

    /**
     * 显示面板和设备内容信息
     */
    private fun panel_and_device_information() {
        when (panelType) {
            "A421", "A201", "A411", "A2A1", "A211" -> onekey_device_txt!!.setText(deviceList!![0].name)
            "A212", "A202", "A311", "A412", "A414", "A2A2" -> {
                onekey_device_txt!!.setText(deviceList!![0].name)
                twokey_device_txt!!.setText(deviceList!![1].name)
            }
            "A213", "A203", "A312", "A321", "A413", "A2A3" -> {
                onekey_device_txt!!.setText(deviceList!![0].name)
                twokey_device_txt!!.setText(deviceList!![1].name)
                threekey_device_txt!!.setText(deviceList!![2].name)
            }
            "A214", "A204", "A313", "A322", "A331", "A2A4" -> {
                onekey_device_txt!!.setText(deviceList!![0].name)
                twokey_device_txt!!.setText(deviceList!![1].name)
                threekey_device_txt!!.setText(deviceList!![2].name)
                fourkey_device_txt!!.setText(deviceList!![3].name)
            }
            "A301", "A302", "A303" -> {
                //            case "A304"://四键调光
                onekey_device_txt!!.setText(deviceList!![0].name)
                twokey_device_txt!!.setText(deviceList!![1].name)
                threekey_device_txt!!.setText(deviceList!![2].name)
                fourkey_device_txt!!.setText(deviceList!![3].name)
            }
            "A401" -> {
                onekey_device_txt!!.setText(deviceList!![0].name)
                twokey_device_txt!!.setText(deviceList!![0].name1)
                threekey_device_txt!!.setText(deviceList!![0].name2)
                fourkey_device_txt!!.setText(deviceList!![1].name)
            }
            "A501", "A511", "A601", "A701", "A611", "A711", "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AD03", "AC01", "B301", "B401", "B402", "B403","ADB3" -> onekey_device_txt!!.setText(deviceList!![0].name)
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3", "ADB2" -> init_common(panelType!!)
            "ADB1" -> find_panel_btn!!.visibility = View.GONE
            else -> {

            }
        }
    }

    private fun init_common(panelType: String) {
        var name = ""
        when (panelType) {
            "A511" -> name = "路空调名称"
            "A611" -> name = "路新风名称"
            "A711" -> name = "路地暖名称"
        }
        val list: MutableList<Map<*, *>> = ArrayList()
        for (i in deviceList!!.indices) {
            val map = HashMap<Any?, Any?>()
            when (panelType) {
                "A511",
                "A611",
                "A711" -> {
                    map["name"] = "第" + NumberUtils.number2Chinese(i + 1) + name + "(" + deviceList!![i].roomName + ")"
                }
                "A501" -> map["name"] = "空调名称" + "(" + deviceList!![i].roomName + ")"
                "A601" -> map["name"] = "新风名称" + "(" + deviceList!![i].roomName + ")"
                "A701" -> map["name"] = "地暖名称" + "(" + deviceList!![i].roomName + ")"
                "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3", "ADB2" -> {
                    when (deviceList!![i].type) {
                        "1", "111" -> {
                            name = "路灯控名称"
                        }
                        "2", "112" -> {
                            name = "路调光灯名称"
                        }
                        "3" -> {
                            name = "路空调名称"
                        }
                        "4", "18", "113" -> {
                            name = "路窗帘名称"
                        }
                        "5" -> {
                            name = "路新风名称"
                        }
                        "6" -> {
                            name = "路地暖名称"
                        }
                    }
                    map["name"] = "第" + NumberUtils.number2Chinese(Integer.parseInt(deviceList!![i].button)) + name + "(" + deviceList!![i].roomName + ")"
                }
            }
            when (deviceList!![i].type) {
                "18", "113" -> {
                }
                else -> map["energy"] = deviceList!![i].energy
            }
            list.add(map)
        }
        val normalAdapter = NormalAdapter(this@ChangePanelAndDeviceActivity, list,
                deviceList!!,
                object : NormalAdapter.BackToMainListener {
                    override fun sendToMain(strings: List<device>?) {
//                                deviceList.clear();
                        deviceList = strings as MutableList<device>
                        for (i in strings.indices) {
//                                    if (s.name.equals("")) {
//                                        s.name = "empty";
//                                    }
                            Log.e("zhu", "name:" + deviceList!![i].name + ",position:" + i
                                    + ",energy:" + deviceList!![i].energy)
                        }
                    }

                    override fun finddevice(position: Int) { //找设备
                        find_common_dev(deviceList!![position].number)
                    }

                    override fun srcolltotop(edtInput: ClearLengthEditText?) {

                    }

//                    override fun srcolltotop(edtInput: ClearLengthEditText) { //把edittext推到上面去
//                    }
                })
        list_view!!.adapter = normalAdapter
    }

    private fun find_common_dev(number2: String) {
        when (panelType) {
            "A211",
            "A212",
            "A213",
            "A214",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> find_device(number2)
            "A421",
            "A401", "A411", "A412", "A413", "A414", "A511", "A611", "A711" -> find_device(number2)
        }
    }

    /**
     * 找设备
     */
    private fun find_device(buttonNumber: String) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> {
                map["buttonId"] = buttonNumber
                api = ApiHelper.sraum_findWifiButton
            }
            else -> {
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = panelNumber
                map["buttonNumber"] = buttonNumber
                api = ApiHelper.sraum_findButton
            }
        }
        //        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { find_device(buttonNumber) }, this@ChangePanelAndDeviceActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        when (user.result) {
                            "100" -> ToastUtil.showDelToast(this@ChangePanelAndDeviceActivity, "操作完成，查看对应设备")
                            "103" -> ToastUtil.showDelToast(this@ChangePanelAndDeviceActivity, "设备编号不存在")
                            else -> {
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }


    /**
     * 得到面板信息
     */
    private fun get_panel_detail() {
        panelNumber = intent.getStringExtra("panelid")
        //         intent.putExtra("boxNumber", gateway_number);
        boxNumber = intent.getStringExtra("boxNumber")
        val bundle = intent.getBundleExtra("bundle_panel")
        deviceList = bundle!!.getSerializable("deviceList") as List<device>
        panelType = intent.getStringExtra("panelType")
        panelName = intent.getStringExtra("panelName")
        panelMAC = intent.getStringExtra("panelMAC")
        findpaneltype = intent.getStringExtra("findpaneltype")
        areaNumber = intent.getStringExtra("areaNumber")
        panelname!!.setText(panelName)
    }

    /**
     * 根据面板类型显示相应设备信息
     *
     * @param type
     */
    private fun show_device_from_panel(type: String?) {
        when (type) {
            "A201" -> {   //,"A211"
                show_one_item()
                onekey_device!!.text = "一路灯控名称"
            }

            "A211" -> {
                show_one_item()
                onekey_device!!.text = "一灯零场景名称"
            }


            "A2A1" -> {
                show_one_item()
                onekey_device!!.text = "一路WIFI灯控名称"
            }
            "A411" -> {
                show_one_item()
                onekey_device!!.text = "一路窗帘名称"
            }
            "A421" -> {
                show_one_item()
                onekey_device!!.text = "一路窗帘电机名称"
            }
            "A202" -> {
                show_two_item()
                onekey_device!!.text = "一路灯控名称"
                twokey_device!!.text = "二路灯控名称"
            }

            "A212" -> {
                show_two_item()
                onekey_device!!.text = "一灯零场景名称"
                twokey_device!!.text = "两灯零场景名称"
            }

            "A2A2" -> {
                show_two_item()
                onekey_device!!.text = "一WIFI路灯控名称"
                twokey_device!!.text = "二WIFI路灯控名称"
            }

            "A412" -> {
                show_two_item()
                onekey_device!!.text = "一路窗帘名称"
                twokey_device!!.text = "一路灯控名称"
            }
            "A414" -> {
                show_two_item()
                onekey_device!!.text = "一路窗帘名称"
                twokey_device!!.text = "二路窗帘名称"
            }
            "A311" -> {
                show_two_item()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "一路灯控名称"
            }
            "A312" -> {
                show_three_item()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "一路灯控名称"
                threekey_device!!.text = "二路灯控名称"
            }
            "A413" -> {
                show_three_item()
                onekey_device!!.text = "一路窗帘名称"
                twokey_device!!.text = "一路灯控名称"
                threekey_device!!.text = "二路灯控名称"
            }
            "A313" -> {
                //                show_three_item();
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "一路灯控名称"
                threekey_device!!.text = "二路灯控名称"
                fourkey_device!!.text = "三路灯控名称"
            }
            "A321" -> {
                show_three_item()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路调光名称"
                threekey_device!!.text = "一路灯控名称"
            }
            "A322" -> {
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路调光名称"
                threekey_device!!.text = "一路灯控名称"
                fourkey_device!!.text = "二路灯控名称"
            }
            "A331" -> {
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路调光名称"
                threekey_device!!.text = "三路调光名称"
                fourkey_device!!.text = "一路灯控名称"
            }
            "A203" -> {
                show_three_item()
                onekey_device!!.text = "一路灯控名称"
                twokey_device!!.text = "二路灯控名称"
                threekey_device!!.text = "三路灯控名称"
            }

            "A213" -> {
                show_three_item()
                onekey_device!!.text = "一灯零场景名称"
                twokey_device!!.text = "两灯零场景名称"
                threekey_device!!.text = "三灯零场景名称"
            }

            "A2A3" -> {
                show_three_item()
                onekey_device!!.text = "一路WIFI灯控名称"
                twokey_device!!.text = "二路WIFI灯控名称"
                threekey_device!!.text = "三路WIFI灯控名称"
            }


            "A204" -> {
                four_all_show()
                onekey_device!!.text = "一路灯控名称"
                twokey_device!!.text = "二路灯控名称"
                threekey_device!!.text = "三路灯控名称"
                fourkey_device!!.text = "四路灯控名称"
            }

            "A214" -> {
                four_all_show()
                onekey_device!!.text = "一灯零场景名称"
                twokey_device!!.text = "两灯零场景名称"
                threekey_device!!.text = "三灯零场景名称"
                fourkey_device!!.text = "四灯零场景名称"
            }

            "A2A4" -> {
                four_all_show()
                onekey_device!!.text = "一路WIFI灯控名称"
                twokey_device!!.text = "二路WIFI灯控名称"
                threekey_device!!.text = "三路WIFI灯控名称"
                fourkey_device!!.text = "四路WIFI灯控名称"
            }
            "A301" -> {
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路灯控名称"
                threekey_device!!.text = "三路灯控名称"
                fourkey_device!!.text = "四路灯控名称"
            }
            "A302" -> {
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路调光名称"
                threekey_device!!.text = "三路灯控名称"
                fourkey_device!!.text = "四路灯控名称"
            }
            "A303" -> {
                four_all_show()
                onekey_device!!.text = "一路调光名称"
                twokey_device!!.text = "二路调光名称"
                threekey_device!!.text = "三路调光名称"
                fourkey_device!!.text = "四路灯控名称"
            }
            "A401" -> {
                four_all_show()
                onekey_device!!.text = "窗帘名称"
                twokey_device!!.text = "一路窗帘名称"
                threekey_device!!.text = "二路窗帘名称"
                fourkey_device!!.text = "八路灯控名称"
            }
            "A501" -> {
                show_one_item()
                onekey_device!!.text = "空调名称"
            }
            "A601", "A611" -> {
                show_one_item()
                onekey_device!!.text = "新风名称"
            }
            "A701", "A711" -> {
                show_one_item()
                onekey_device!!.text = "地暖名称"
                show_one_item()
                onekey_device!!.text = "空调名称"
                show_one_item()
                onekey_device!!.text = "门磁名称"
            }
            "A511" -> {
                show_one_item()
                onekey_device!!.text = "空调名称"
                show_one_item()
                onekey_device!!.text = "门磁名称"
            }
            "A801" -> {
                show_one_item()
                onekey_device!!.text = "门磁名称"
            }
            "A901" -> {
                show_one_item()
                onekey_device!!.text = "人体感应名称"
            }
            "A902" -> {
                show_one_item()
                onekey_device!!.text = "地暖名称"
            }
            "AB01" -> {
                show_one_item()
                onekey_device!!.text = "久坐报警器名称"
            }
            "AB04" -> {
                show_one_item()
                onekey_device!!.text = "天然气报警名称"
            }
            "B001" -> {
                show_one_item()
                onekey_device!!.text = "紧急按钮名称"
            }
            "B201" -> {
                show_one_item()
                onekey_device!!.text = "智能门锁名称"
            }
            "AD01" -> {
                show_one_item()
                onekey_device!!.text = "PM2.5名称"
            }
            "ADB3" -> {
                show_one_item()
                onekey_device!!.text = "入墙PM2.5名称"
            }
            "AD03" -> {
                show_one_item()
                onekey_device!!.text = "空气检测器名称"
            }
            "AC01" -> {
                show_one_item()
                onekey_device!!.text = "水浸传感器名称"
            }
            "B301" -> {
                show_one_item()
                onekey_device!!.text = "直流电机名称"
            }
            "B401", "B402", "B403" -> {
                show_one_item()
                onekey_device!!.text = "平移控制器名称"
            }
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" ->
                list_for_air_mode!!.visibility = View.VISIBLE
            else -> {

            }
        }
    }

    /**
     * 显示三个
     */
    private fun show_three_item() {
        onekey_device!!.visibility = View.VISIBLE
        twokey_device!!.visibility = View.VISIBLE
        threekey_device!!.visibility = View.VISIBLE
        fourkey_device!!.visibility = View.GONE
        onekey_device_txt!!.visibility = View.VISIBLE
        twokey_device_txt!!.visibility = View.VISIBLE
        threekey_device_txt!!.visibility = View.VISIBLE
        fourkey_device_txt!!.visibility = View.GONE
        linear_one_only!!.visibility = View.VISIBLE
        linear_two_only!!.visibility = View.VISIBLE
        linear_three_only!!.visibility = View.VISIBLE
    }

    /**
     * 显示两个
     */
    private fun show_two_item() {
        onekey_device!!.visibility = View.VISIBLE
        twokey_device!!.visibility = View.VISIBLE
        threekey_device!!.visibility = View.GONE
        fourkey_device!!.visibility = View.GONE
        onekey_device_txt!!.visibility = View.VISIBLE
        twokey_device_txt!!.visibility = View.VISIBLE
        threekey_device_txt!!.visibility = View.GONE
        fourkey_device_txt!!.visibility = View.GONE
        linear_one_only!!.visibility = View.VISIBLE
        linear_two_only!!.visibility = View.VISIBLE
    }

    /**
     * 显示第一个
     */
    private fun show_one_item() {
        onekey_device!!.visibility = View.VISIBLE
        twokey_device!!.visibility = View.GONE
        threekey_device!!.visibility = View.GONE
        fourkey_device!!.visibility = View.GONE
        onekey_device_txt!!.visibility = View.VISIBLE
        twokey_device_txt!!.visibility = View.GONE
        threekey_device_txt!!.visibility = View.GONE
        fourkey_device_txt!!.visibility = View.GONE
        linear_one_only!!.visibility = View.VISIBLE
    }

    /**
     * 四个全部显示
     */
    private fun four_all_show() {
        onekey_device!!.visibility = View.VISIBLE
        twokey_device!!.visibility = View.VISIBLE
        threekey_device!!.visibility = View.VISIBLE
        fourkey_device!!.visibility = View.VISIBLE
        onekey_device_txt!!.visibility = View.VISIBLE
        twokey_device_txt!!.visibility = View.VISIBLE
        threekey_device_txt!!.visibility = View.VISIBLE
        fourkey_device_txt!!.visibility = View.VISIBLE
        linear_one_only!!.visibility = View.VISIBLE
        linear_two_only!!.visibility = View.VISIBLE
        linear_three_only!!.visibility = View.VISIBLE
        linear_four_only!!.visibility = View.VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.btn_login_gateway -> when (panelType) {
                "A211", "A212", "A213", "A214",
                "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401", "A411", "A412", "A413", "A414", "A311", "A312", "A313", "A321", "A322", "A331", "A2A1",
                "A2A2",
                "A2A3",
                "A2A4", "A421" -> save_panel()
                "A511", "A611", "A711", "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> save_air_model()
                "B201", "B101", "B301", "B401", "B402", "B403", "A501", "A601", "A701", "A801", "A901", "AB01", "A902", "AB04", "AC01", "AD01", "AD03", "B001", "ADB1","ADB3" -> {
                    dialogUtil!!.loadDialog()
                    input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                            || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "设备名称为空")
                    } else {
                        if (panelName == input_panel_name_edit_txt_str) {
//                                AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                            updateDeviceInfo() //更新设备信息
                        } else {
                            sraum_update_panel_name(input_panel_name_edit_txt_str!!, panelNumber) //更新面板信息
                        }
                    }
                }
                "AA02" -> {
                    input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                            || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "设备名称为空")
                    } else {
                        if (panelName == input_panel_name_edit_txt_str) {
                            AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                        } else {
                            sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str!!)
                        }
                    }
                }
                "AA03", "AA04" -> {
                    input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                            || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "设备名称为空")
                    } else {
                        if (panelName == input_panel_name_edit_txt_str) {
                            AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                        } else {
                            sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str!!)
                        }
                    }
                }
                "202", "206" -> {
                    input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                            || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "设备名称为空")
                    } else {
                        if (panelName == input_panel_name_edit_txt_str) {
                            AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                        } else {
                            sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str!!)
                        }
                    }
                }
            }
            R.id.findButton_four -> sraum_find_outer(3)
            R.id.findButton_three -> sraum_find_outer(2)
            R.id.findButton_two -> sraum_find_outer(1)
            R.id.findButton_one -> sraum_find_outer(0)
            R.id.find_panel_btn -> sraum_find_panel()//sraum_findWifiDevice
        }
    }

    private fun sraum_find_outer(position: Int) {
        when (panelType) {
            "A211", "A212", "A213", "A214", "ADA1", "ADA2", "ADA3",
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "A201", "A202", "A311", "A203", "A312", "A321", "A204", "A313", "A322", "A331", "A301", "A302", "A303","ADB2" -> sraum_find_button(deviceList!![position].number)
            "A401" -> when (position) {
                0, 1, 2 -> sraum_find_button(deviceList!![0].number)
                3 -> sraum_find_button(deviceList!![1].number)
            }
            "A411", "A412", "A413", "A414", "A501", "A511", "A601", "A701", "A611", "A711", "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AD03", "AC01", "B301", "B401", "B402", "B403","ADB3" -> sraum_find_button(deviceList!![position].number)
        }
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private fun sraum_updateWifiAppleName(number: String?, newName: String) {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this
//                , "areaNumber", "");
        mapdevice["areaNumber"] = areaNumber
        var method: String? = ""
        when (panelType) {
            "AA02" -> method = ApiHelper.sraum_updateWifiAppleName
            "AA03", "AA04" -> method = ApiHelper.sraum_updateWifiCameraName
            "202", "206" -> method = ApiHelper.sraum_updateWifiAppleDeviceName
        }
        mapdevice["number"] = number
        mapdevice["name"] = newName
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_updateWifiAppleName(number, newName)
        }, this@ChangePanelAndDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "更新失败")
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun fourCode() {}
            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                when (panelType) {
                    "AA02", "AA03", "AA04" -> {
                        val map = HashMap<Any?, Any?>()
                        map["deviceId"] = panelNumber
                        map["deviceType"] = panelType
                        map["areaNumber"] = areaNumber
                        map["findpaneltype"] = findpaneltype
                        map["type"] = "2"
                        val intent = Intent(this@ChangePanelAndDeviceActivity, SelectRoomActivity::class.java)
                        intent.putExtra("map_deivce", map as Serializable)
                        startActivity(intent)
                    }
                    "202", "206" -> {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "更新成功")
                        finish() //修改完毕
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    }
                }
            }
        })
    }

    /**
     * 保存空调模块
     */
    private fun save_air_model() {
        val count_device = deviceList!!.size
        //判断面板和设备名称是否相同，相同就不提交
        input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
        val list: MutableList<String> = ArrayList()
       // list.add(input_panel_name_edit_txt_str!!)
        for (i in deviceList!!.indices) {
            list.add(deviceList!![i].name)
        }

        //遍历面板和设备名称有相同的吗
        isPanelAndDeviceSame = false
        for (i in 0 until list.size - 1) {
            for (j in i + 1 until list.size) {
                if (list[i] == list[j] && list[i] != ""
                        && list[j] != "") {
                    isPanelAndDeviceSame = true
                    break
                }
            }
        }
        if (!isPanelAndDeviceSame) {
            for (i in 0 until count_device) {
                if (list[i] == "") {
                    isPanelAndDeviceSame = true
                }
            }
            if (isPanelAndDeviceSame) {
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "输入框不能为空")
            } else {
                dialogUtil!!.loadDialog()
                if (panelName == input_panel_name_edit_txt_str) {
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    updateDeviceInfo() //更新设备信息
                } else {
                    sraum_update_panel_name(input_panel_name_edit_txt_str!!, panelNumber) //更新面板信息
                }
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息\
            }
        } else {
            ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "所输入内容重复")
        }
    }

    private fun setFindpanel() {
        dialogUtil!!.loadDialog()
        sraum_find_panel()
    }

    private fun sraum_find_panel() {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> {
                api = ApiHelper.sraum_findWifiDevice
                map["deviceId"] = panelNumber
            }
            else -> {
                api = ApiHelper.sraum_findDevice
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = panelNumber
            }
        }
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { //刷新togglen获取新数据
                    sraum_find_panel()
                }, this@ChangePanelAndDeviceActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "操作完成，查看对应面板")
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "面板未找到")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "面板未找到")
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    /**
     * 查找按钮
     */
    private fun sraum_find_button(buttonNumber: String) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4" -> {
                map["buttonId"] = buttonNumber
                api = ApiHelper.sraum_findWifiButton
            }
            else -> {
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = panelNumber
                map["buttonNumber"] = buttonNumber
                api = ApiHelper.sraum_findButton
            }
        }
        //        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { //刷新togglen获取新数据
                    sraum_find_panel()
                }, this@ChangePanelAndDeviceActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "操作完成，查看对应面板")
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "gatewayNumber 不存在")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, """
     -deviceNumber 不
     存在
     """.trimIndent())
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun wrongBoxnumber() {
                        //areaNumber 不存在
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "areaNumber 不存在")
                    }

                    override fun fiveCode() {
                        //buttonNumber 不存在
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "buttonNumber 不存在")
                    }
                })
    }

    /**
     * 保存面板
     */
    private fun save_panel() {
        //                panelmac.setText(panelList.get(i).mac);
//                paneltype.setText(panelList.get(i).type);
//                panelname.setText(panelList.get(i).name);
        val panelName = panelname!!.text.toString().trim { it <= ' ' }
        val count_device = deviceList!!.size
        //判断面板和设备名称是否相同，相同就不提交
        onekey_device_txt_str = if (onekey_device_txt!!.text.toString().trim { it <= ' ' } == null
                || onekey_device_txt!!.text.toString().trim { it <= ' ' } === "") "" else onekey_device_txt!!.text.toString().trim { it <= ' ' }
        twokey_device_txt_str = if (twokey_device_txt!!.text.toString().trim { it <= ' ' } == null
                || twokey_device_txt!!.text.toString().trim { it <= ' ' } === "") "" else twokey_device_txt!!.text.toString().trim { it <= ' ' }
        threekey_device_txt_str = if (threekey_device_txt!!.text.toString().trim { it <= ' ' } == null
                || threekey_device_txt!!.text.toString().trim { it <= ' ' } === "") "" else threekey_device_txt!!.text.toString().trim { it <= ' ' }
        fourkey_device_txt_str = if (fourkey_device_txt!!.text.toString().trim { it <= ' ' } == null
                || fourkey_device_txt!!.text.toString().trim { it <= ' ' } === "") "" else fourkey_device_txt!!.text.toString().trim { it <= ' ' }
        val list: MutableList<String> = ArrayList()
       // list.add(panelName)
        list.add(onekey_device_txt_str!!)
        list.add(twokey_device_txt_str!!)
        list.add(threekey_device_txt_str!!)
        list.add(fourkey_device_txt_str!!)

//                switch (count_device) {
//                    case 1:
//
//                        break;
//                    case 2:
//
//                        break;
//                    case 3:
//
//                        break;
//                    case 4:
//
//                        break;
//                }

        //遍历面板和设备名称有相同的吗
        isPanelAndDeviceSame = false
        for (i in 0 until list.size - 1) {
            for (j in i + 1 until list.size) {
                if (list[i] == list[j] && list[i] != ""
                        && list[j] != "") {
                    isPanelAndDeviceSame = true
                    break
                }
            }
        }

        if (!isPanelAndDeviceSame) {
            for (i in 0 until count_device) {
                if (i >= list.size) break
                if (list[i] == "") {
                    isPanelAndDeviceSame = true
                }
            }
            if (isPanelAndDeviceSame) {
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "输入框不能为空")
                return
            } else {
                dialogUtil!!.loadDialog()
                sraum_update_panel_name(panelName, panelNumber) //更新面板信息
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息
            }
        } else {
            ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "所输入内容重复")
            return
        }
    }

    /**
     * 更新设备信息
     */
    private fun updateDeviceInfo() {
        when (panelType) {
            "A421", "A201", "A411", "A2A1", "A211" -> {
                onekey_device_txt_str = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                device_index = 0
                control_device_name_change_one(onekey_device_txt_str, 0)
            }
            "A202", "A212", "A311", "A412", "A414", "A2A2" -> {
                onekey_device_txt_str = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                twokey_device_txt_str = twokey_device_txt!!.text.toString().trim { it <= ' ' }
                device_index = 1
                control_device_name_change_one(onekey_device_txt_str, 0) //从0 -1 开始
            }
            "A203", "A213", "A312", "A321", "A413", "A2A3" -> {
                onekey_device_txt_str = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                twokey_device_txt_str = twokey_device_txt!!.text.toString().trim { it <= ' ' }
                threekey_device_txt_str = threekey_device_txt!!.text.toString().trim { it <= ' ' }
                device_index = 2
                control_device_name_change_one(onekey_device_txt_str, 0) //从0 - 2开始
            }
            "A204", "A214", "A313", "A322", "A331", "A2A4" -> {
                onekey_device_txt_str = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                twokey_device_txt_str = twokey_device_txt!!.text.toString().trim { it <= ' ' }
                threekey_device_txt_str = threekey_device_txt!!.text.toString().trim { it <= ' ' }
                fourkey_device_txt_str = fourkey_device_txt!!.text.toString().trim { it <= ' ' }
                device_index = 3
                control_device_name_change_one(onekey_device_txt_str, 0) //从0-3开始
            }
            "A301", "A302", "A303" -> {
                //            case "A304"://四键调光
//                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(0).number, "");
//                updateDeviceInfo(twokey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(1).number, "");
//                updateDeviceInfo(threekey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(2).number, "");
//                updateDeviceInfo(fourkey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(3).number, "");
                onekey_device_txt_str = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                twokey_device_txt_str = twokey_device_txt!!.text.toString().trim { it <= ' ' }
                threekey_device_txt_str = threekey_device_txt!!.text.toString().trim { it <= ' ' }
                fourkey_device_txt_str = fourkey_device_txt!!.text.toString().trim { it <= ' ' }
                device_index = 3
                control_device_name_change_one(onekey_device_txt_str, 0) //从0-3开始
            }
            "A401" -> {
                val customName = onekey_device_txt!!.text.toString().trim { it <= ' ' }
                val name1 = twokey_device_txt!!.text.toString().trim { it <= ' ' }
                val name2 = threekey_device_txt!!.text.toString().trim { it <= ' ' }
                Thread(Runnable {
                    val deviceNumber = deviceList!![0].number
                    updateDeviceInfo(customName, name1, name2, deviceNumber, "窗帘前3", 0)
                }).start()
            }
            "A501" -> updateDeviceInfo(onekey_device_txt!!.text.toString().trim { it <= ' ' }, "", "",
                    deviceList!![0].number, "", 0)
            "A601" -> updateDeviceInfo(onekey_device_txt!!.text.toString().trim { it <= ' ' }, "", "",
                    deviceList!![0].number, "", 0)
            "A701" -> updateDeviceInfo(onekey_device_txt!!.text.toString().trim { it <= ' ' }, "", "",
                    deviceList!![0].number, "", 0)
            "A801", "A901", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AD03", "AC01", "B301", "B401", "B402", "B403", "A511", "A611", "A711","ADB3" -> updateDeviceInfo(onekey_device_txt!!.text.toString().trim { it <= ' ' }, "", "",
                    deviceList!![0].number, "", 0)
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> {
                if (deviceList == null || deviceList!!.size == 0) {
                    AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    return
                }
                var i = 0
                while (i < deviceList!!.size) {
                    sraum_update_others(deviceList!![i].name, deviceList!![i].number, i)
                    i++
                }
            }
            "ADB1" -> {
                var i = 0
                while (i < deviceList!!.size) {
                    input_panel_name_edit_txt_str = if (panelname!!.text.toString().trim { it <= ' ' } == null
                            || panelname!!.text.toString().trim { it <= ' ' } === "") "" else panelname!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "设备名称为空")
                        i++
                        continue
                    } else {
                        sraum_update_others(input_panel_name_edit_txt_str!!, deviceList!![i].number, i)
                    }
                    i++
                }
            }
            else -> {

            }
        }
    }

    /**
     * 更新其他的设备
     *
     * @param customName
     * @param index
     */
    private fun sraum_update_others(customName: String, deviceNumber: String, index: Int) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
        map["areaNumber"] = areaNumber
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2","ADB3" -> {
                api = ApiHelper.sraum_updateWifiButtonName
                map["buttonNumber"] = deviceNumber
                map["newName"] = customName
            }
            else -> {
                api = ApiHelper.sraum_updateButtonName
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = deviceNumber
                map["newName"] = customName
            }
        }
        MyOkHttp.postMapObject(api, map, object : Mycallback(AddTogglenInterfacer { sraum_update_others(customName, deviceNumber, index) }, this@ChangePanelAndDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, """
     -areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                super.threeCode()
                //gatewayNumber 不存在
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "gatewayNumber 不存在")
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "deviceNumber 不存在")
            }

            override fun fiveCode() {
                super.fiveCode()
                ToastUtil.showToast(this@ChangePanelAndDeviceActivity, """
     type
     类型不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                if (index == deviceList!!.size - 1) {
                    finish()
                    ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "更新成功")
                    when (findpaneltype) {
                        "fastedit" -> AppManager.getAppManager().finishActivity_current(FastEditPanelActivity::class.java)
                        "wangguan_status", "wifi_status" -> {
                        }
                    }
                    //                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    //添加完设备区关联房间
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = panelNumber
                    map["deviceType"] = panelType
                    map["areaNumber"] = areaNumber
                    map["findpaneltype"] = findpaneltype
                    when (panelType) {
                        "A2A1",
                        "A2A2",
                        "A2A3",
                        "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2","ADB3"-> {
                            map["type"] = "2"
                        }
                        else -> {
                            map["type"] = "1"
                        }
                    }
                    val intent = Intent(this@ChangePanelAndDeviceActivity, SelectRoomActivity::class.java)
                    intent.putExtra("map_deivce", map as Serializable)
                    startActivity(intent)
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * 窗帘第八键设备控制
     *
     * @param chuanglian
     */
    private fun select_window_bajian(chuanglian: String) {
        deviceNumber = deviceList!![1].number //
        val customName_window = fourkey_device_txt!!.text.toString().trim { it <= ' ' }
        Thread(Runnable { updateDeviceInfo(customName_window, "", "", deviceNumber, chuanglian, 0) }).start()
    }

    /**
     * 第一个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_one(device_name: String?, index: Int) {
        Thread(Runnable {
            if (index <= deviceList!!.size - 1) //
                updateDeviceInfo(device_name, "", "",
                        deviceList!![index].number, "", index)
        }).start()
    }

    /**
     * 第三个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_three(device_name: String?, index: Int) {
        Thread(Runnable {
            if (index <= deviceList!!.size - 1) //
                updateDeviceInfo(device_name, "", "",
                        deviceList!![index].number, "", index)
        }).start()
    }

    /**
     * 第二个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_two(device_name: String?, index: Int) {
        Thread(Runnable {
            if (index <= deviceList!!.size - 1) //
                updateDeviceInfo(device_name, "", "",
                        deviceList!![index].number, "", index)
        }).start()
    }

    /**
     * 第四个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_four(device_name: String?, index: Int) {
        Thread(Runnable {
            if (index <= deviceList!!.size - 1) //
                updateDeviceInfo(device_name, "", "",
                        deviceList!![index].number, "", index)
        }).start()
    }

    private fun updateDeviceInfo(customName: String?, name1: String, name2: String, deviceNumber: String?, chuanglian: String, index: Int) {
        sraum_update_s(customName, name1, name2, deviceNumber, chuanglian, index)
    }

    private fun sraum_update_s(customName: String?, name1: String, name2: String, deviceNumber: String?, chuanglian: String, index: Int) {
        val map: MutableMap<String, Any?> = HashMap()


        var api = ""
        when (panelType) { //窗帘第八键 - "ADB2"
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4","ADB3" -> {
                api = ApiHelper.sraum_updateWifiButtonName
                map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
                map["areaNumber"] = areaNumber
                map["buttonNumber"] = deviceNumber
                map["newName"] = customName
            }
            else -> {
                api = ApiHelper.sraum_updateButtonName
                map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
                map["areaNumber"] = areaNumber
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = deviceNumber
                map["newName"] = customName
                if (chuanglian == "窗帘前3") {
                    map["newName1"] = name1
                    map["newName2"] = name2
                }
            }
        }

        MyOkHttp.postMapObject(api, map, object : Mycallback(AddTogglenInterfacer { sraum_update_s(customName, name1, name2, deviceNumber, chuanglian, index) }, this@ChangePanelAndDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                select_device_byway("不存在")
            }

            override fun threeCode() {
                super.threeCode()
                //                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "设备编号不正确");
                select_device_byway("设备编号不正确")
            }

            override fun fourCode() {
                super.fourCode()
                //                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "自定义名称重复");
                select_device_byway("自定义名称重复")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                //                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "上传成功");
                select_device_byway("上传成功")
            }

            /**
             * 选设备4-1，依次执行
             * @param content
             */
            private fun select_device_byway(content: String) { //失败就提示添加失败名称，和失败类型
                if (chuanglian == "窗帘前3") {
                    if (content == "上传成功") { //当前不成功，就不继续加下去了
                        select_window_bajian("窗帘第八键") //控制窗帘第八键
                    } else {
                        ToastUtil.showDelToast(this@ChangePanelAndDeviceActivity, "$customName:$content")
                    }
                    return
                }

                //窗帘第八键
                if (chuanglian == "窗帘第八键") { // 窗帘第八键
                    if (content == "上传成功") { //当前不成功，就不继续加下去了
                        //
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "更新成功")
                        when (findpaneltype) {
                            "fastedit" -> AppManager.getAppManager().finishActivity_current(FastEditPanelActivity::class.java)
                            "wangguan_status", "wifi_status" -> {
                            }
                        }
                        //                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        val map = HashMap<Any?, Any?>()
                        map["deviceId"] = panelNumber
                        map["deviceType"] = panelType
                        map["areaNumber"] = areaNumber
                        map["findpaneltype"] = findpaneltype

                        when (panelType) {
                            "A2A1",
                            "A2A2",
                            "A2A3",
                            "A2A4","ADB3" -> {
                                map["type"] = "2"
                            }
                            else -> {
                                map["type"] = "1"
                            }
                        }

                        val intent = Intent(this@ChangePanelAndDeviceActivity, SelectRoomActivity::class.java)
                        intent.putExtra("map_deivce", map as Serializable)
                        startActivity(intent)
                        //添加完，添加完设备区关联房间
                    } else {
                        ToastUtil.showDelToast(this@ChangePanelAndDeviceActivity, "$customName:$content")
                    }
                    return
                }
                var index_now = index
                index_now++
                if (content == "上传成功") { //当前不成功，就不继续加下去了
                    when (index_now) {
                        0 -> control_device_name_change_one(onekey_device_txt_str, 0)
                        1 -> control_device_name_change_two(twokey_device_txt_str, 1)
                        2 -> control_device_name_change_three(threekey_device_txt_str, 2)
                        3 -> control_device_name_change_four(fourkey_device_txt_str, 3)
                    }
                    if (index_now > device_index) {
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "更新成功")
                        when (findpaneltype) {
                            "fastedit" -> AppManager.getAppManager().finishActivity_current(FastEditPanelActivity::class.java)
                            "wangguan_status", "wifi_status" -> {
                            }
                        }
                        //                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        //添加完设备区关联房间
                        val map = HashMap<Any?, Any?>()
                        map["deviceId"] = panelNumber
                        map["deviceType"] = panelType
                        map["areaNumber"] = areaNumber
                        map["findpaneltype"] = findpaneltype
                        when (panelType) {
                            "A2A1",
                            "A2A2",
                            "A2A3",
                            "A2A4","ADB3" -> {
                                map["type"] = "2"
                            }
                            else -> {
                                map["type"] = "1"
                            }
                        }

                        val intent = Intent(this@ChangePanelAndDeviceActivity, SelectRoomActivity::class.java)
                        intent.putExtra("map_deivce", map as Serializable)
                        startActivity(intent)
                        return
                    }
                } else {
                    ToastUtil.showDelToast(this@ChangePanelAndDeviceActivity, "$customName:$content")
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun sraum_update_panel_name(panelName: String, panelNumber: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = "";
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3", "ADB1", "ADB2","ADB3" -> {//WIFI 面板
                api = ApiHelper.sraum_updateWifiDeviceNameCommon

                map["token"] = TokenUtil.getToken(this)
                // String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
                map["areaNumber"] = areaNumber
                map["number"] = panelNumber
                map["name"] = panelName
            }
            else -> {
                map["token"] = TokenUtil.getToken(this@ChangePanelAndDeviceActivity)
                map["areaNumber"] = areaNumber
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = panelNumber
                map["newName"] = panelName
                api = ApiHelper.sraum_updateDeviceName
            }
        }



        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer
                //刷新togglen获取新数据
                { sraum_update_panel_name(panelName, panelNumber) }, this@ChangePanelAndDeviceActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        finish()
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        //                        ChangePanelAndDeviceActivity.this.finish();
//                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, panelName+":"+"面板名字更新成功");
                        updateDeviceInfo() //更新设备信息
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "$panelName:面板编号不正确")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@ChangePanelAndDeviceActivity, "$panelName:面板名字已存在")
                    }
                })
    }

    //    private void clickanimation() {
    //        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation_small);
    //        findpanel.startAnimation(clickAnimation);
    //
    //        //如果你想要点下去然后弹上来就这样
    //        clickAnimation.setAnimationListener(new Animation.AnimationListener() {
    //
    //            @Override
    //            public void onAnimationStart(Animation animation) {
    //
    //            }
    //
    //            @Override
    //            public void onAnimationRepeat(Animation animation) {
    //
    //            }
    //
    //            @Override
    //            public void onAnimationEnd(Animation animation) {
    //                //动画执行完后的动作
    //                findpanel.clearAnimation();
    //                Animation clickAnimation = AnimationUtils.loadAnimation(ChangePanelAndDeviceActivity.this, R.anim.scale_animation_big);
    //                findpanel.startAnimation(clickAnimation);
    //            }
    //        });
    //    }
    override fun onDestroy() {
        super.onDestroy()
    }
}