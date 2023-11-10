package com.massky.sraum.activity

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.percentlayout.widget.PercentRelativeLayout
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.device
import com.massky.sraum.User.panellist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.AirModeListAdapter
import com.massky.sraum.adapter.NormalAdapter
import com.massky.sraum.adapter.NormalAdapter.BackToMainListener
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.util.*
import java.util.regex.Pattern

/**
 * Created by zhu on 2018/1/8.
 */
class EditMyDeviceActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.input_panel_name_edit)
    var input_panel_name_edit: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.edit_one)
    var edit_one: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.button_one_id)
    var button_one_id: ImageView? = null

    @JvmField
    @BindView(R.id.edit_two)
    var edit_two: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.button_two_id)
    var button_two_id: ImageView? = null

    @JvmField
    @BindView(R.id.edit_three)
    var edit_three: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.button_three_id)
    var button_three_id: ImageView? = null

    @JvmField
    @BindView(R.id.edit_four)
    var edit_four: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.button_four_id)
    var button_four_id: ImageView? = null

    @JvmField
    @BindView(R.id.linear_one)
    var linear_one: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_two)
    var linear_two: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_three)
    var linear_three: LinearLayout? = null

    @JvmField
    @BindView(R.id.linear_four)
    var linear_four: LinearLayout? = null

    @JvmField
    @BindView(R.id.first_txt)
    var first_txt: TextView? = null

    @JvmField
    @BindView(R.id.second_txt)
    var second_txt: TextView? = null

    @JvmField
    @BindView(R.id.three_txt)
    var three_txt: TextView? = null

    @JvmField
    @BindView(R.id.four_txt)
    var four_txt: TextView? = null
    private var panelItem_map = HashMap<Any?, Any?>()

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.txt_dev)
    var txt_dev: TextView? = null
    private var type = ""
    private var edit_one_txt_str = ""
    private var edit_two_txt_str = ""
    private var edit_three_txt_str = ""
    private var edit_four_txt_str = ""
    private var name = ""
    private var isPanelAndDeviceSame = false
    private var number = ""
    private val panelList: MutableList<panellist> = ArrayList()
    private var dialogUtil: DialogUtil? = null
    private var deviceList: MutableList<device> = ArrayList()
    private var panelType: String? = null
    private var panelName: String? = null
    private var panelMAC: String? = null
    private var device_index = 0
    private var deviceNumber: String? = null
    private var input_panel_name_edit_txt_str: String? = null

    @JvmField
    @BindView(R.id.maclistview_id_condition)
    var list_view: ListViewForScrollView_New? = null

    @JvmField
    @BindView(R.id.find_panel_btn)
    var find_panel_btn: ImageView? = null
    private val airmodeadapter: AirModeListAdapter? = null

    @JvmField
    @BindView(R.id.list_for_air_mode)
    var list_for_air_mode: LinearLayout? = null


    //edit_one_power
    @JvmField
    @BindView(R.id.edit_one_power)
    var edit_one_power: ClearLengthEditText? = null

    //edit_second_power
    @JvmField
    @BindView(R.id.edit_second_power)
    var edit_second_power: ClearLengthEditText? = null

    //edit_three_power
    @JvmField
    @BindView(R.id.edit_three_power)
    var edit_three_power: ClearLengthEditText? = null

    //edit_four_power
    @JvmField
    @BindView(R.id.edit_four_power)
    var edit_four_power: ClearLengthEditText? = null


    @JvmField
    @BindView(R.id.power_edit_four_rel)
    var power_edit_four_rel: PercentRelativeLayout? = null

    //power_four_rel
    @JvmField
    @BindView(R.id.power_four_rel)
    var power_four_rel: PercentRelativeLayout? = null


    @JvmField
    @BindView(R.id.power_edit_third_rel)
    var power_edit_third_rel: PercentRelativeLayout? = null

    //power_four_rel
    @JvmField
    @BindView(R.id.power_third_rel)
    var power_third_rel: PercentRelativeLayout? = null


    @JvmField
    @BindView(R.id.power_edit_second_rel)
    var power_edit_second_rel: PercentRelativeLayout? = null

    //power_four_rel
    @JvmField
    @BindView(R.id.power_second_rel)
    var power_second_rel: PercentRelativeLayout? = null


    @JvmField
    @BindView(R.id.power_edit_first_rel)
    var power_edit_first_rel: PercentRelativeLayout? = null

    //power_four_rel
    @JvmField
    @BindView(R.id.power_first_rel)
    var power_first_rel: PercentRelativeLayout? = null

    //first_per_pro
    @JvmField
    @BindView(R.id.first_per_pro)
    var first_per_pro: PercentRelativeLayout? = null

    @JvmField
    @BindView(R.id.first_per_edt)
    var first_per_edt: PercentRelativeLayout? = null


    //linear_top
    @JvmField
    @BindView(R.id.linear_top)
    var linear_top: LinearLayout? = null


    private var boxNumber: String? = null
    private var authType: String? = null
    private var areaNumber: String? = null
    private var roomName: String? = null
    override fun viewId(): Int {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return R.layout.edit_my_device_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
        get_view(linear_top as ViewGroup)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        button_one_id!!.setOnClickListener(this)
        button_two_id!!.setOnClickListener(this)
        button_three_id!!.setOnClickListener(this)
        button_four_id!!.setOnClickListener(this)
        find_panel_btn!!.setOnClickListener(this)
    }

    override fun onData() {
        panelItem_map = intent.getSerializableExtra("panelItem") as HashMap<Any?, Any?>
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        authType = if (intent.getSerializableExtra("authType") == null) "" else
            intent.getSerializableExtra("authType").toString()
        when (authType) {
            "1" -> next_step_txt!!.visibility = View.VISIBLE
            "2" -> next_step_txt!!.visibility = View.GONE
            else -> next_step_txt!!.visibility = View.VISIBLE
        }//
        if (panelItem_map != null) {
//            device_name_txt.setText(panelItem_map.get("name").toString());
//            project_select.setText(panelItem_map.get("name").toString());
//            mac_txt.setText(panelItem_map.get("mac").toString());
            type = panelItem_map.get("type").toString()
            name = if (panelItem_map.get("name") == null) "" else panelItem_map.get("name").toString()
            number = panelItem_map.get("number").toString()
            boxNumber = if (panelItem_map.get("boxNumber") == null) "" else panelItem_map.get("boxNumber").toString()
            input_panel_name_edit!!.setText(name)
            setCommon(type)
            when (type) {
                "AA02", "AA03", "AA04", "AA05" -> txt_dev!!.text = "设备名称" + "(" + panelItem_map.get("roomName").toString() + ")"
                "202", "206", "网关","A088" -> {

                }
                "A2A1",
                "A2A2",
                "A2A3",
                "A2A4", "ADA1", "ADA2", "ADA3","ADB1","ADB2","ADB3" -> {
                    wifi_devices
                }
                else -> panel_devices
            }
        }
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private fun sraum_updateWifiAppleName(number: String, newName: String) {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        mapdevice["areaNumber"] = areaNumber
        var method: String? = ""
        when (type) {
            "AA02" -> method = ApiHelper.sraum_updateWifiAppleName
            "AA03", "AA04", "AA05" -> method = ApiHelper.sraum_updateWifiCameraName
            "202", "206" -> method = ApiHelper.sraum_updateWifiAppleDeviceName
            "AD02" -> method = ApiHelper.sraum_updateWifiDeviceNameCommon
        }
        mapdevice["number"] = number
        mapdevice["name"] = newName
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_updateWifiAppleName(number, newName)
        }, this@EditMyDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@EditMyDeviceActivity, "更新失败")
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
                ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                finish() //修改完毕
                //                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
//                AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity.class);
                AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
            }
        })
    }


    private fun get_view(linear_root: ViewGroup) {
        for (i in 0 until linear_root.childCount) {
            val view = linear_root.getChildAt(i)
            if (view is AdapterView<*> ||
                    view is ClearLengthEditText || view is ClearEditText) {
                continue
            }
            Log.e("TAG", "get_view: $view")
            view.setOnClickListener {
                //cameratop_id.setVisibility(View.GONE)
                hideInput()
            }
            if (view is ViewGroup) {
                get_view(view)
            }
        }
    }


    /**
     * 隐藏键盘
     */
    protected fun hideInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v: View = getWindow().peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }


    private fun update_dev_powers() {
        when (panelType) {
            "A421", "A201", "A411", "A2A1", "A211", "B501" -> {
                //edit_one_power!!.setText(deviceList[0].energy)
                val list: MutableList<String> = ArrayList()
                list.add(if (edit_one_power!!.text == null) "" else edit_one_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
                for (i in list.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = this.deviceList[i].number
                    map["energy"] = list[i]
                    deviceList.add(map)
                }

                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)
            }

            "A202", "A311", "A2A2", "A412", "A414", "A212" -> {
//                edit_one_power!!.setText(deviceList[0].energy)
//                edit_second_power!!.setText(deviceList[1].energy)
                val list: MutableList<String> = ArrayList()
                list.add(if (edit_one_power!!.text == null) "" else edit_one_power!!.text.toString())
                list.add(if (edit_second_power!!.text == null) "" else edit_second_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
                for (i in list.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = this.deviceList[i].number
                    map["energy"] = list[i]
                    deviceList.add(map)
                }

                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)//
            }
            "A203", "A312", "A321", "A2A3", "A413", "A213" -> {
//                edit_one_power!!.setText(deviceList[0].energy)
//                edit_second_power!!.setText(deviceList[1].energy)
//                edit_three_power!!.setText(deviceList[2].energy)
                val list: MutableList<String> = ArrayList()
                list.add(if (edit_one_power!!.text == null) "" else edit_one_power!!.text.toString())
                list.add(if (edit_second_power!!.text == null) "" else edit_second_power!!.text.toString())
                list.add(if (edit_three_power!!.text == null) "" else edit_three_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
                for (i in list.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = this.deviceList[i].number
                    map["energy"] = list[i]
                    deviceList.add(map)
                }

                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)
            }

            "A204", "A2A4", "A214" -> {
//                edit_one_power!!.setText(deviceList[0].energy)
//                edit_second_power!!.setText(deviceList[1].energy)
//                edit_three_power!!.setText(deviceList[2].energy)
//                edit_four_power!!.setText(deviceList[3].energy)

                val list: MutableList<String> = ArrayList()
                list.add(if (edit_one_power!!.text == null) "" else edit_one_power!!.text.toString())
                list.add(if (edit_second_power!!.text == null) "" else edit_second_power!!.text.toString())
                list.add(if (edit_three_power!!.text == null) "" else edit_three_power!!.text.toString())
                list.add(if (edit_four_power!!.text == null) "" else edit_four_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
                for (i in list.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = this.deviceList[i].number
                    map["energy"] = list[i]
                    deviceList.add(map)
                }


                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)
            }
            "A301", "A302", "A303", "A313", "A322", "A331" -> {
                //            case "A304"://四键调光
//                edit_one_power!!.setText(deviceList[0].energy)
//                edit_second_power!!.setText(deviceList[1].energy)
//                edit_three_power!!.setText(deviceList[2].energy)
//                edit_four_power!!.setText(deviceList[3].energy)
                val list: MutableList<String> = ArrayList()
                list.add(if (edit_one_power!!.text == null) "" else edit_one_power!!.text.toString())
                list.add(if (edit_second_power!!.text == null) "" else edit_second_power!!.text.toString())
                list.add(if (edit_three_power!!.text == null) "" else edit_three_power!!.text.toString())
                list.add(if (edit_four_power!!.text == null) "" else edit_four_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
                for (i in list.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["deviceId"] = this.deviceList[i].number
                    map["energy"] = list[i]
                    deviceList.add(map)
                }

                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)
            }
            "A401" -> {
                //edit_four_power!!.setText(deviceList[1].energy)

                val list: MutableList<String> = ArrayList()
                list.add(if (edit_four_power!!.text == null) "" else edit_four_power!!.text.toString())
                val deviceList: MutableList<Map<*, *>> = ArrayList()
//                for (i in list.indices) {
                val map_item = HashMap<Any?, Any?>()
                map_item["deviceId"] = this.deviceList[1].number
                map_item["energy"] = list[0]
                deviceList.add(map_item)
//                }

                val map = HashMap<String, Any>()
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber!!
                map["deviceList"] = deviceList
                sraum_update_powers(map)
            }
            "A511", "A501", "A601", "A701", "A611", "A711", "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> update_air_power_common()
            else -> {

            }
        }
    }


    private fun sraum_update_powers(map: HashMap<String, Any>) {
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");

        var api = ""
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> {
                api = ApiHelper.sraum_setWifiButtonEnergy!!
            }
            else ->
                api = ApiHelper.sraum_setButtonEnergy!!
        }


        MyOkHttp.postMapObject(api, map, object : Mycallback(AddTogglenInterfacer {
            sraum_update_powers(map)
        }, this@EditMyDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun threeCode() {
                super.threeCode()
                //                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "设备编号不正确");
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showDelToast(this@EditMyDeviceActivity, "修改失败");
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                //                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "上传成功");
            }


            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_step_txt -> {
                update_dev_powers()
                panel_dev_name()
            }
            R.id.back -> finish()
            R.id.button_one_id -> sraum_find_outer(0)
            R.id.button_two_id -> sraum_find_outer(1)
            R.id.button_three_id -> sraum_find_outer(2)
            R.id.button_four_id -> sraum_find_outer(3)
            R.id.find_panel_btn -> sraum_find_panel(number)
        }
    }

    private fun panel_dev_name() {
        when (type) {
            "A421",
            "A211",
            "A212",
            "A213",
            "A214",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A401", "A411", "A412", "A413", "A414", "A311", "A312", "A313", "A321", "A322", "A331", "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "B501" -> {
                save_panel()
            }
            "A511", "A611", "A711", "AD11", "AD12", "AD13","ADA1","ADA2","ADA3","ADB2" -> save_air_model()
            "B201", "B101", "B301", "B401", "B402", "B403", "A501", "A601", "A701", "A801", "A901", "A911", "AB01",
            "A902", "AB04", "AC01", "AE02", "AD01", "AD03", "B001", "B501" ,"ADB1","ADB3"-> {
                dialogUtil!!.loadDialog()
                input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                        || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                if (input_panel_name_edit_txt_str == "") {
                    ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                } else {
                    if (name == input_panel_name_edit_txt_str) {
                        //                                AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                        //                                updateDeviceInfo();//更新设备信息
                        finish()
                        AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                    } else {
                        sraum_update_panel_name(type, input_panel_name_edit_txt_str!!, number, false) //更新面板信息
                    }
                }
            }
            "AA02", "AD02" -> {
                input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                        || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                if (input_panel_name_edit_txt_str == "") {
                    ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                } else {
                    if (name == input_panel_name_edit_txt_str) {
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    } else {
                        sraum_updateWifiAppleName(number, input_panel_name_edit_txt_str!!)
                    }
                }
            }
            "AA03", "AA04", "AA05" -> {
                input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                        || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                if (input_panel_name_edit_txt_str == "") {
                    ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                } else {
                    if (name == input_panel_name_edit_txt_str) {
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    } else {
                        sraum_updateWifiAppleName(number, input_panel_name_edit_txt_str!!)
                    }
                }
            }
            "202", "206" -> {
                input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                        || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                if (input_panel_name_edit_txt_str == "") {
                    ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                } else {
                    if (name == input_panel_name_edit_txt_str) {
                        //                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        finish()
                        AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                    } else {
                        sraum_updateWifiAppleName(number, input_panel_name_edit_txt_str!!)
                    }
                }
            }
            "网关" ,"A088"-> {
                input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                        || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                if (input_panel_name_edit_txt_str == "") {
                    ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                } else {
                    if (name == input_panel_name_edit_txt_str) {
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    } else {
                        sraum_updateGatewayName(number, input_panel_name_edit_txt_str!!)
                    }
                }
            }
        }
    }

    private fun sraum_find_outer(position: Int) {
        when (panelType) {
            "A211",
            "A212",
            "A213",
            "A214",
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4",
            "A201", "A202", "A311", "A203", "A312", "A321", "A204", "A313", "A322", "A331", "A301", "A302", "A303","ADB2" -> find_device(deviceList[position].number)
            "A401" -> when (position) {
                0, 1, 2 -> find_device(deviceList[0].number)
                3 -> find_device(deviceList[1].number)
            }
            "A421",
            "A411", "A412", "A413", "A414", "A501", "A511", "A601", "A701", "A611", "A711", "A801", "A901", "A911", "A902", "AB01", "AB04", "B001", "B201", "AD01", "AD03", "AC01", "AE02", "B301", "B401", "B402", "B403","ADB3" -> find_device(deviceList[position].number)
        }
    }

    /**
     * 更新网关的名字
     *
     * @param number
     */
    private fun sraum_updateGatewayName(number: String, newName: String) {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        mapdevice["areaNumber"] = areaNumber
        var method: String? = ""
        when (type) {
            "网关","A088" -> method = ApiHelper.sraum_updateGatewayName
        }
        mapdevice["number"] = number
        mapdevice["newName"] = newName
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_updateWifiAppleName(number, newName)
        }, this@EditMyDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@EditMyDeviceActivity, "更新失败")
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun fourCode() {}
            override fun threeCode() {
                ToastUtil.showToast(this@EditMyDeviceActivity, "number 不存在")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@EditMyDeviceActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                finish() //修改完毕
                AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                //                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
            }
        })
    }

    /**
     * 保存空调模块
     */
    private fun save_air_model() {
        val count_device = deviceList.size
        //判断面板和设备名称是否相同，相同就不提交
        input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
        val list: MutableList<String> = ArrayList()
       // list.add(input_panel_name_edit_txt_str!!)
        for (i in deviceList.indices) {
            list.add(deviceList[i].name)
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
                ToastUtil.showToast(this@EditMyDeviceActivity, "输入框不能为空")
            } else {
                dialogUtil!!.loadDialog()
                if (name == input_panel_name_edit_txt_str) {
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    updateDeviceInfo() //更新设备信息
                } else {
                    sraum_update_panel_name(type, input_panel_name_edit_txt_str!!, number, false) //更新面板信息
                }
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息\
            }
        } else {
            ToastUtil.showToast(this@EditMyDeviceActivity, "所输入内容重复")
        }
    }

    private fun find_common_dev(number2: String) {
        when (panelType) {
            "A211",
            "A212",
            "A213",
            "A214",
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> find_device(number2)
            "A421",
            "A401", "A411", "A412", "A413", "A414", "A511", "A611", "A711" -> find_device(number2)
        }
    }
    //    /**
    //     * 保存数据
    //     */
    //    private void save_data() {
    //
    //    }
    /**
     * 保存面板
     */
    private fun save_panel() {
        //                panelmac.setText(panelList.get(i).mac);
//                paneltype.setText(panelList.get(i).type);
//                panelname.setText(panelList.get(i).name);
//        String panelName = panelname.getText().toString().trim();
        val count_device = deviceList.size
        //判断面板和设备名称是否相同，相同就不提交
        input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
        edit_one_txt_str = if (edit_one!!.text.toString().trim { it <= ' ' } == null
                || edit_one!!.text.toString().trim { it <= ' ' } === "") "" else edit_one!!.text.toString().trim { it <= ' ' }
        edit_two_txt_str = if (edit_two!!.text.toString().trim { it <= ' ' } == null
                || edit_two!!.text.toString().trim { it <= ' ' } === "") "" else edit_two!!.text.toString().trim { it <= ' ' }
        edit_three_txt_str = if (edit_three!!.text.toString().trim { it <= ' ' } == null
                || edit_three!!.text.toString().trim { it <= ' ' } === "") "" else edit_three!!.text.toString().trim { it <= ' ' }
        edit_four_txt_str = if (edit_four!!.text.toString().trim { it <= ' ' } == null
                || edit_four!!.text.toString().trim { it <= ' ' } === "") "" else edit_four!!.text.toString().trim { it <= ' ' }


        val list: MutableList<String> = ArrayList()
       // list.add(input_panel_name_edit_txt_str!!)
        list.add(edit_one_txt_str)
        list.add(edit_two_txt_str)
        list.add(edit_three_txt_str)
        list.add(edit_four_txt_str)

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
                ToastUtil.showToast(this@EditMyDeviceActivity, "输入框不能为空")
            } else {
                dialogUtil!!.loadDialog()
                if (name == input_panel_name_edit_txt_str) {
                    updateDeviceInfo() //更新设备信息
                    //                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                } else {
                    sraum_update_panel_name(type, input_panel_name_edit_txt_str!!, number, false) //更新面板信息
                }
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息
            }
        } else {
            ToastUtil.showToast(this@EditMyDeviceActivity, "所输入内容重复")
        }
    }

    /**
     * 找面板
     *
     * @param panelnumber
     */
    private fun sraum_find_panel(panelnumber: String) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB2","ADB3" -> {
                map["deviceId"] = number
                api = ApiHelper.sraum_findWifiDevice
            }

            else -> {
                map["gatewayNumber"] = boxNumber
                map["deviceNumber"] = number
                api = ApiHelper.sraum_findDevice
            }
        }

        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { //刷新togglen获取新数据
                    sraum_find_panel(panelnumber)
                }, this@EditMyDeviceActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        ToastUtil.showToast(this@EditMyDeviceActivity, "操作完成，查看对应面板")
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@EditMyDeviceActivity, "面板未找到")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@EditMyDeviceActivity, "面板未找到")
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }//

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
                map["deviceNumber"] = number
                map["buttonNumber"] = buttonNumber
                api = ApiHelper.sraum_findButton
            }
        }
        //        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { find_device(buttonNumber) }, this@EditMyDeviceActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        when (user.result) {
                            "100" -> ToastUtil.showDelToast(this@EditMyDeviceActivity, "操作完成，查看对应设备")
                            "103" -> ToastUtil.showDelToast(this@EditMyDeviceActivity, "设备编号不存在")
                            else -> {
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    fun replaceBlank(src: String?): String {
        var dest = ""
        if (src != null) {
            val pattern = Pattern.compile("\t|\r|\n|\\s*")
            val matcher = pattern.matcher(src)
            dest = matcher.replaceAll("")
        }
        return dest
    }//面板的详细信息//        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");


    /**
     * 获取WIFI设备下按钮列表
     */
    private val wifi_devices: Unit
        private get() {
            val map: MutableMap<String, Any?> = HashMap()
            map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
            //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
            map["deviceId"] = number
            map["areaNumber"] = areaNumber
            common_devices_show(map, ApiHelper.sraum_getWifiButtons)
        }

    /**
     * 添加面板下的设备信息
     */
    private val panel_devices: Unit
        private get() {
            val map: MutableMap<String, Any?> = HashMap()
            map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
            //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
            map["boxNumber"] = boxNumber
            map["panelNumber"] = number
            map["areaNumber"] = areaNumber
            common_devices_show(map, ApiHelper.sraum_getPanelDevices)
        }

    private fun common_devices_show(map: MutableMap<String, Any?>, api: String?) {
        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { panel_devices }, this@EditMyDeviceActivity, dialogUtil) {
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
                        roomName = user.roomName
                        panel_and_device_information()
                        if (roomName != null) {
                            if (roomName!!.trim { it <= ' ' } != "") {
                                txt_dev!!.text = "设备名称($roomName)"
                            }
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    /**
     * 显示面板和设备内容信息
     */
    private fun panel_and_device_information() {
        when (panelType) {
            "A421", "A201", "A411", "A2A1", "A211", "B501" -> {
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                edit_one_power!!.setText(deviceList[0].energy)
            }
            "A202", "A311", "A2A2", "A412", "A414", "A212" -> {
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                edit_two!!.setText(replaceBlank(deviceList[1].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                second_txt!!.text = second_txt!!.text.toString() + "(" + deviceList[1].roomName + ")"
                edit_one_power!!.setText(deviceList[0].energy)
                edit_second_power!!.setText(deviceList[1].energy)

            }
            "A203", "A312", "A321", "A2A3", "A413", "A213" -> {
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                edit_two!!.setText(replaceBlank(deviceList[1].name))
                edit_three!!.setText(replaceBlank(deviceList[2].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                second_txt!!.text = second_txt!!.text.toString() + "(" + deviceList[1].roomName + ")"
                three_txt!!.text = three_txt!!.text.toString() + "(" + deviceList[2].roomName + ")"
                edit_one_power!!.setText(deviceList[0].energy)
                edit_second_power!!.setText(deviceList[1].energy)
                edit_three_power!!.setText(deviceList[2].energy)
            }
            "A204", "A2A4", "A214" -> {
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                edit_two!!.setText(replaceBlank(deviceList[1].name))
                edit_three!!.setText(replaceBlank(deviceList[2].name))
                edit_four!!.setText(replaceBlank(deviceList[3].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                second_txt!!.text = second_txt!!.text.toString() + "(" + deviceList[1].roomName + ")"
                three_txt!!.text = three_txt!!.text.toString() + "(" + deviceList[2].roomName + ")"
                four_txt!!.text = four_txt!!.text.toString() + "(" + deviceList[3].roomName + ")"
                edit_one_power!!.setText(deviceList[0].energy)
                edit_second_power!!.setText(deviceList[1].energy)
                edit_three_power!!.setText(deviceList[2].energy)
                edit_four_power!!.setText(deviceList[3].energy)
            }
            "A301", "A302", "A303", "A313", "A322", "A331" -> {
                //            case "A304"://四键调光
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                edit_two!!.setText(replaceBlank(deviceList[1].name))
                edit_three!!.setText(replaceBlank(deviceList[2].name))
                edit_four!!.setText(replaceBlank(deviceList[3].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                second_txt!!.text = second_txt!!.text.toString() + "(" + deviceList[1].roomName + ")"
                three_txt!!.text = three_txt!!.text.toString() + "(" + deviceList[2].roomName + ")"
                four_txt!!.text = four_txt!!.text.toString() + "(" + deviceList[3].roomName + ")"
                edit_one_power!!.setText(deviceList[0].energy)
                edit_second_power!!.setText(deviceList[1].energy)
                edit_three_power!!.setText(deviceList[2].energy)
                edit_four_power!!.setText(deviceList[3].energy)
            }
            "A401" -> {//
                edit_one!!.setText(replaceBlank(deviceList[0].name))
                edit_two!!.setText(replaceBlank(deviceList[0].name1))
                edit_three!!.setText(replaceBlank(deviceList[0].name2))
                edit_four!!.setText(replaceBlank(deviceList[1].name))
                first_txt!!.text = first_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                second_txt!!.text = second_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                three_txt!!.text = three_txt!!.text.toString() + "(" + deviceList[0].roomName + ")"
                four_txt!!.text = four_txt!!.text.toString() + "(" + deviceList[1].roomName + ")"
//                edit_one_power!!.setText(deviceList[0].energy)
//                edit_second_power!!.setText(deviceList[1].energy)
//                edit_three_power!!.setText(deviceList[2].energy)
                edit_four_power!!.setText(deviceList[1].energy)
            }
            "A511" -> init_common(panelType!!)
            "A501", "A601", "A701" -> {
                init_common(panelType!!)
            }
            "A611" -> init_common(panelType!!)
            "A711" -> init_common(panelType!!)
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> init_common(panelType!!)
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
        for (i in deviceList.indices) {
            val map = HashMap<Any?, Any?>()
//            when(i) {
//                0-> map["name"] = "第一" + name + "(" + deviceList[i].roomName + ")"
//                1-> map["name"] = "第二" + name + "(" + deviceList[i].roomName + ")"
//            }
            when (panelType) {
                "A511",
                "A611",
                "A711" -> {
                    map["name"] = "第" + NumberUtils.number2Chinese(i + 1) + name + "(" + deviceList[i].roomName + ")"
                }
                "A501" -> map["name"] = "空调名称" + "(" + deviceList[i].roomName + ")"
                "A601" -> map["name"] = "新风名称" + "(" + deviceList[i].roomName + ")"
                "A701" -> map["name"] = "地暖名称" + "(" + deviceList[i].roomName + ")"
                "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> {
                    when (deviceList[i].type) {
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
                    map["name"] = "第" + NumberUtils.number2Chinese(Integer.parseInt(deviceList[i].button)) + name + "(" + deviceList[i].roomName + ")"
                }
            }
            when (deviceList[i].type) {
                "18" -> {
                }
                else -> map["energy"] = deviceList[i].energy
            }
            list.add(map)
        }
        val normalAdapter = NormalAdapter(this@EditMyDeviceActivity, list,
                deviceList,
                object : BackToMainListener {
                    override fun sendToMain(strings: List<device>?) {
//                                deviceList.clear();
                        deviceList = strings as MutableList<device>
                        for (i in strings.indices) {
//                                    if (s.name.equals("")) {
//                                        s.name = "empty";
//                                    }
                            Log.e("zhu", "name:" + deviceList[i].name + ",position:" + i
                                    + ",energy:" + deviceList[i].energy)
                        }
                    }

                    override fun finddevice(position: Int) { //找设备
                        find_common_dev(deviceList[position].number)
                    }

                    override fun srcolltotop(edtInput: ClearLengthEditText?) {

                    }

//                    override fun srcolltotop(edtInput: ClearLengthEditText) { //把edittext推到上面去
//                    }
                })
        list_view!!.adapter = normalAdapter
    }


    private fun update_air_power_common() {


        // val list: MutableList<Map<*, *>> = ArrayList()
//        for (i in deviceList.indices) {
//            val map = HashMap<Any?, Any?>()
//            map["name"] = "第" + (i + 1) + name + "(" + deviceList[i].roomName + ")"
//            map["energy"] = deviceList[i].energy
//            list.add(map)
//        }

        val deviceList: MutableList<Map<*, *>> = ArrayList()
        for (i in this.deviceList.indices) {
            if(this.deviceList[i].energy == null) continue
            val map = HashMap<Any?, Any?>()
            map["deviceId"] = this.deviceList[i].number
            map["energy"] = this.deviceList[i].energy
            deviceList.add(map)
        }

        if(deviceList.size == 0) return
        val map = HashMap<String, Any>()
        map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
        map["areaNumber"] = areaNumber!!
        map["deviceList"] = deviceList
        sraum_update_powers(map)

    }

    /**
     * 更新设备信息
     */
    private fun updateDeviceInfo() {
        when (panelType) {
            "A421", "A201", "A411", "A2A1", "A211", "B501" -> {
                edit_one_txt_str = edit_one!!.text.toString().trim { it <= ' ' }
                device_index = 0
                control_device_name_change_one(panelType!!, edit_one_txt_str, 0)
            }
            "A202", "A311", "A412", "A414", "A2A2", "A212" -> {
                edit_one_txt_str = edit_one!!.text.toString().trim { it <= ' ' }
                edit_two_txt_str = edit_two!!.text.toString().trim { it <= ' ' }
                device_index = 1
                control_device_name_change_one(panelType!!, edit_one_txt_str, 0) //从0 -1 开始
            }
            "A203", "A312", "A321", "A413", "A2A3", "A213" -> {
                edit_one_txt_str = edit_one!!.text.toString().trim { it <= ' ' }
                edit_two_txt_str = edit_two!!.text.toString().trim { it <= ' ' }
                edit_three_txt_str = edit_three!!.text.toString().trim { it <= ' ' }
                device_index = 2
                control_device_name_change_one(panelType!!, edit_one_txt_str, 0) //从0 - 2开始
            }
            "A204", "A313", "A322", "A331", "A2A4", "A214" -> {
                edit_one_txt_str = edit_one!!.text.toString().trim { it <= ' ' }
                edit_two_txt_str = edit_two!!.text.toString().trim { it <= ' ' }
                edit_three_txt_str = edit_three!!.text.toString().trim { it <= ' ' }
                edit_four_txt_str = edit_four!!.text.toString().trim { it <= ' ' }
                device_index = 3
                control_device_name_change_one(panelType!!, edit_one_txt_str, 0) //从0-3开始
            }
            "A301", "A302", "A303" -> {
                edit_one_txt_str = edit_one!!.text.toString().trim { it <= ' ' }
                edit_two_txt_str = edit_two!!.text.toString().trim { it <= ' ' }
                edit_three_txt_str = edit_three!!.text.toString().trim { it <= ' ' }
                edit_four_txt_str = edit_four!!.text.toString().trim { it <= ' ' }
                device_index = 3
                control_device_name_change_one(panelType!!, edit_one_txt_str, 0) //从0-3开始
            }
            "A401" -> {
                val customName = edit_one!!.text.toString().trim { it <= ' ' }
                val name1 = edit_two!!.text.toString().trim { it <= ' ' }
                val name2 = edit_three!!.text.toString().trim { it <= ' ' }
                Thread(Runnable {
                    val deviceNumber = deviceList[0].number
                    updateDeviceInfo(panelType!!, customName, name1, name2, deviceNumber, "窗帘前3", 0)
                }).start()
            }

            "A511", "A611", "A711", "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3","ADB2" -> {
                var i = 0
                while (i < deviceList.size) {
                    sraum_update_others(deviceList[i].name, deviceList[i].number, i)
                    i++
                }
            }
            "A501", "A601", "A701", "A801", "A901", "A911", "AB01", "A902", "AB04", "AC01", "AE02", "AD01", "AD03", "B001", "B101", "B201",
            "B301", "B401", "B402", "B403", "B501","ADB1","ADB3"-> {
                var i = 0
                while (i < deviceList.size) {
                    input_panel_name_edit_txt_str = if (input_panel_name_edit!!.text.toString().trim { it <= ' ' } == null
                            || input_panel_name_edit!!.text.toString().trim { it <= ' ' } === "") "" else input_panel_name_edit!!.text.toString().trim { it <= ' ' }
                    if (input_panel_name_edit_txt_str == "") {
                        ToastUtil.showToast(this@EditMyDeviceActivity, "设备名称为空")
                        i++
                        continue
                    } else {
                        sraum_update_others(input_panel_name_edit_txt_str!!, deviceList[i].number, i)
                    }
                    i++
                }
            }
            else -> {

            }
        }
    }

    /**
     * 窗帘第八键设备控制
     *
     * @param chuanglian
     */
    private fun select_window_bajian(chuanglian: String) {
        deviceNumber = deviceList[1].number //
        val customName_window = edit_four!!.text.toString().trim { it <= ' ' }
        Thread(Runnable { updateDeviceInfo(panelType!!, customName_window, "", "", deviceNumber, chuanglian, 0) }).start()
    }

    /**
     * 第一个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_one(panelType: String, device_name: String, index: Int) {
        Thread(Runnable {
            if (index <= deviceList.size - 1) //
                updateDeviceInfo(panelType, device_name, "", "",
                        deviceList[index].number, "", index)
        }).start()
    }

    /**
     * 第三个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_three(panelType: String, device_name: String, index: Int) {
        Thread(Runnable {
            if (index <= deviceList.size - 1) //
                updateDeviceInfo(panelType!!, device_name, "", "",
                        deviceList[index].number, "", index)
        }).start()
    }//sraum_getWifiButtons

    /**
     * 第二个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_two(panelType: String, device_name: String, index: Int) {
        Thread(Runnable {
            if (index <= deviceList.size - 1) //
                updateDeviceInfo(panelType, device_name, "", "",
                        deviceList[index].number, "", index)
        }).start()
    }

    /**
     * 第四个设备控制
     *
     * @param device_name
     * @param index
     */
    private fun control_device_name_change_four(panelType: String, device_name: String, index: Int) {
        Thread(Runnable {
            if (index <= deviceList.size - 1) //
                updateDeviceInfo(panelType, device_name, "", "",
                        deviceList[index].number, "", index)
        }).start()
    }

    private fun updateDeviceInfo(panelType: String, customName: String, name1: String, name2: String, deviceNumber: String?, chuanglian: String, index: Int) {
        sraum_update_s(panelType, customName, name1, name2, deviceNumber, chuanglian, index)
    }//67

    private fun sraum_update_s(panelType: String, customName: String, name1: String, name2: String, deviceNumber: String?, chuanglian: String, index: Int) {
        val map: MutableMap<String, Any?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        var api = ""
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB1","ADB2","ADB3" -> {
                api = ApiHelper.sraum_updateWifiButtonName
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
                map["areaNumber"] = areaNumber
                map["buttonNumber"] = deviceNumber
                map["newName"] = customName
            }
            else -> {
                api = ApiHelper.sraum_updateButtonName
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
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


        MyOkHttp.postMapObject(api, map, object : Mycallback(AddTogglenInterfacer { sraum_update_s(panelType, customName, name1, name2, deviceNumber, chuanglian, index) }, this@EditMyDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                select_device_byway("网关不正确")
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
                        ToastUtil.showDelToast(this@EditMyDeviceActivity, "$customName:$content")
                    }
                    return
                }

                //窗帘第八键
                if (chuanglian == "窗帘第八键") {
                    if (content == "上传成功") { //当前不成功，就不继续加下去了
                        //
                        ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                        //                        switch (findpaneltype) {
//                            case "fastedit"://快速编辑
//                                AppManager.getAppManager().finishActivity_current(FastEditPanelActivity.class);
//                                break;
//                            case "wangguan_status":
//                                break;
//                        }
//                        EditMyDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        finish()
                        AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                    } else {
                        ToastUtil.showDelToast(this@EditMyDeviceActivity, "$customName:$content")
                    }
                    return
                }
                var index_now = index
                index_now++
                if (content == "上传成功") { //当前不成功，就不继续加下去了
                    when (index_now) {
                        0 -> control_device_name_change_one(panelType, edit_one_txt_str, 0)
                        1 -> control_device_name_change_two(panelType, edit_two_txt_str, 1)
                        2 -> control_device_name_change_three(panelType, edit_three_txt_str, 2)
                        3 -> control_device_name_change_four(panelType, edit_four_txt_str, 3)
                    }
                    if (index_now > device_index) {
                        ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                        //                        switch (findpaneltype) {
//                            case "fastedit"://快速编辑
//                                AppManager.getAppManager().finishActivity_current(FastEditPanelActivity.class);
//                                break;
//                            case "wangguan_status":
//                                break;
//                        }
//                        EditMyDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        finish()
                        AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                        return
                    }
                } else {
                    ToastUtil.showDelToast(this@EditMyDeviceActivity, "$customName:$content")
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }//

    /**
     * 更新其他的设备
     *
     * @param customName
     * @param index
     */
    private fun sraum_update_others(customName: String, deviceNumber: String, index: Int) {
        val map: MutableMap<String, Any?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        var api = ""
        map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
        map["areaNumber"] = areaNumber
        when (panelType) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4","ADA1","ADA2","ADA3","ADB1","ADB2","ADB3" -> {
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
        MyOkHttp.postMapObject(api, map, object : Mycallback(AddTogglenInterfacer { sraum_update_others(customName, deviceNumber, index) }, this@EditMyDeviceActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@EditMyDeviceActivity, """
     -areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                super.threeCode()
                //gatewayNumber 不存在
                ToastUtil.showToast(this@EditMyDeviceActivity, "gatewayNumber 不存在")
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showToast(this@EditMyDeviceActivity, "deviceNumber 不存在")
            }

            override fun fiveCode() {
                super.fiveCode()
                ToastUtil.showToast(this@EditMyDeviceActivity, """
     type
     类型不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                if (index == deviceList.size - 1) {
                    finish()
                    AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                    ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun sraum_update_panel_name(type: String, panelName: String, panelNumber: String, istrue: Boolean) {
        val map: MutableMap<String, Any?> = HashMap()
        //        String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
        var api = "";
        when (type) {
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4", "ADA1", "ADA2", "ADA3","ADB1","ADB1","ADB2","ADB3" -> {//WIFI 面板
                api = ApiHelper.sraum_updateWifiDeviceNameCommon

                map["token"] = TokenUtil.getToken(this)
                // String areaNumber = (String) SharedPreferencesUtil.getData(EditMyDeviceActivity.this, "areaNumber", "");
                map["areaNumber"] = areaNumber
                map["number"] = number
                map["name"] = panelName
            }
            else -> {
                map["token"] = TokenUtil.getToken(this@EditMyDeviceActivity)
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
                { sraum_update_panel_name(type, panelName, panelNumber, istrue) }, this@EditMyDeviceActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        finish()
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        //                        ChangePanelAndDeviceActivity.this.finish();
//                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, panelName+":"+"面板名字更新成功");
                        if (!istrue) {
                            updateDeviceInfo() //更新设备信息
                        } else {
//                            EditMyDeviceActivity.this.finish();
//                            ToastUtil.showToast(EditMyDeviceActivity.this, "更新成功");
                            finish()
                            AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
                            ToastUtil.showToast(this@EditMyDeviceActivity, "更新成功")
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@EditMyDeviceActivity, "$panelName:面板编号不正确")
                    }

                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@EditMyDeviceActivity, "deviceNumber 不存在")
                    }

                    override fun fiveCode() {
                        super.fiveCode()
                        ToastUtil.showToast(this@EditMyDeviceActivity, "$panelName:面板名字已存在")
                    }
                })
    }

    private fun setCommon(type: String) {//
        when (type) {
            "A201" -> {
                linear_one!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                edit_one!!.hint = "输入第一路灯控自定义名称"
            }

            "A211" -> {
                linear_one!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路零场景灯控名称"
                edit_one!!.hint = "输入第一路零场景灯控名称"
            }


            "A2A1" -> {
                linear_one!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                edit_one!!.hint = "输入第一路灯控名称"
            }

            "A411" -> {
                linear_one!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路窗帘名称"
                edit_one!!.hint = "输入第一路窗帘自定义名称"
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE

            }

            "A421" -> {
                linear_one!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路窗帘电机名称"
                edit_one!!.hint = "输入第一路窗帘电机名称"
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE
            }

            "B501" -> {
                linear_one!!.visibility = View.VISIBLE
                first_per_pro!!.visibility = View.GONE
                first_per_edt!!.visibility = View.GONE
                // first_txt!!.text = "音乐面板名称"
                //edit_one!!.hint = "输入音乐面板名称"
                power_first_rel!!.visibility = View.VISIBLE
                power_edit_first_rel!!.visibility = View.VISIBLE
            }

            "A202" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                second_txt!!.text = "第二路灯控名称"
                edit_one!!.hint = "输入第一路灯控自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
            }

            "A212" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路零场景灯控名称"
                second_txt!!.text = "第二路零场景灯控名称"
                edit_one!!.hint = "输入第一路零场景灯控名称"
                edit_two!!.hint = "输入第二路零场景灯控名称"
            }

            "A2A2" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                second_txt!!.text = "第二路灯控名称"
                edit_one!!.hint = "输入第一路灯控名称"
                edit_two!!.hint = "输入第二路灯控名称"
            }

            "A414" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路窗帘名称"
                second_txt!!.text = "第二路窗帘名称"
                edit_one!!.hint = "输入第一路窗帘自定义名称"
                edit_two!!.hint = "输入第二路窗帘自定义名称"
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE
                power_second_rel!!.visibility = View.GONE
                power_edit_second_rel!!.visibility = View.GONE

            }

            "A412" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路窗帘名称"
                second_txt!!.text = "第二路灯控名称"
                edit_one!!.hint = "输入第一路窗帘自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE
            }

            "A311" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路调光名称"
                second_txt!!.text = "第二路灯控名称"
                edit_one!!.hint = "输入第一路调光自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
            }

            "A203" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                second_txt!!.text = "第二路灯控名称"
                three_txt!!.text = "第三路灯控名称"
                edit_one!!.hint = "输入第一路灯控自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
                edit_three!!.hint = "输入第三路灯控自定义名称"
            }

            "A213" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路零场景灯控名称"
                second_txt!!.text = "第二路零场景灯控名称"
                three_txt!!.text = "第三路零场景灯控名称"
                edit_one!!.hint = "输入第一路零场景灯控名称"
                edit_two!!.hint = "输入第二路零场景灯控名称"
                edit_three!!.hint = "输入第三路零场景灯控名称"
            }


            "A2A3" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路灯控名称"
                second_txt!!.text = "第二路灯控名称"
                three_txt!!.text = "第三路灯控名称"
                edit_one!!.hint = "输入第一路灯控名称"
                edit_two!!.hint = "输入第二路灯控名称"
                edit_three!!.hint = "输入第三路灯控名称"
            }

            "A413" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路窗帘名称"
                second_txt!!.text = "第二路灯控名称"
                three_txt!!.text = "第三路灯控名称"
                edit_one!!.hint = "输入第一路窗帘自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
                edit_three!!.hint = "输入第三路灯控自定义名称"
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE
            }
            "A312" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路调光名称"
                second_txt!!.text = "第二路灯控名称"
                three_txt!!.text = "第三路灯控名称"
                edit_one!!.hint = "输入第一路调光自定义名称"
                edit_two!!.hint = "输入第二路灯控自定义名称"
                edit_three!!.hint = "输入第三路灯控自定义名称"
            }
            "A321" -> {
                linear_one!!.visibility = View.VISIBLE
                linear_two!!.visibility = View.VISIBLE
                linear_three!!.visibility = View.VISIBLE
                first_txt!!.text = "第一路调光名称"
                second_txt!!.text = "第二路调光名称"
                three_txt!!.text = "第三路灯控名称"
                edit_one!!.hint = "输入第一路调光自定义名称"
                edit_two!!.hint = "输入第二路调光自定义名称"
                edit_three!!.hint = "输入第三路灯控自定义名称"
            }
            "A322" -> common_second("第一路调光名称", "第二路调光名称", "第三路灯控名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路调光自定义名称", "输入第三路灯控自定义名称",
                    "输入第四路灯控自定义名称")
            "A331" -> common_second("第一路调光名称", "第二路调光名称", "第三路调光名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路调光自定义名称", "输入第三路调光自定义名称",
                    "输入第四路灯控自定义名称")
            "A313" -> common_second("第一路调光名称", "第二路灯控名称", "第三路灯控名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路灯控自定义名称", "输入第三路灯控自定义名称",
                    "输入第四路灯控自定义名称")
            "A204" -> common_second("第一路灯控名称", "第二路灯控名称", "第三路灯控名称", "第四路灯控名称",
                    "输入第一路灯控自定义名称", "输入第二路灯控自定义名称", "输入第三路灯控自定义名称",
                    "输入第四路灯控自定义名称")
            "A214" -> common_second("第一路零场景灯控名称", "第二路零场景灯控名称", "第三路零场景灯控名称", "第四路零场景灯控名称",
                    "输入第一路零场景灯控名称", "输入第二路零场景灯控名称", "输入第三路零场景灯控名称",
                    "输入第四路零场景灯控名称")
            "A2A4" -> common_second("第一路灯控名称", "第二路灯控名称", "第三路灯控名称",
                    "第四路灯控名称",
                    "输入第一路灯控名称", "输入第二路灯控名称", "输入第三路灯控名称",
                    "输入第四路灯控名称")
            "A301" -> common_second("第一路调光名称", "第二路灯控名称", "第三路灯控名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路灯控自定义名称", "输入第三路灯控自定义名称",
                    "输入第四路灯控自定义名称")
            "A302" -> common_second("第一路调光名称", "第二路调光名称", "第三路灯控名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路调光自定义名称", "输入第三路灯控自定义名称",
                    "输入第四路灯控自定义名称")
            "A303" -> common_second("第一路调光名称", "第二路调光名称", "第三路调光名称", "第四路灯控名称",
                    "输入第一路调光自定义名称", "输入第二路调光自定义名称", "输入第三路调光自定义名称",
                    "输入第四路灯控自定义名称")
            "A401" -> {
                power_first_rel!!.visibility = View.GONE
                power_edit_first_rel!!.visibility = View.GONE
                power_second_rel!!.visibility = View.GONE
                power_edit_second_rel!!.visibility = View.GONE
                power_third_rel!!.visibility = View.GONE
                power_edit_third_rel!!.visibility = View.GONE
                common_second("窗帘名称", "第一路窗帘名称", "第二路窗帘名称", "第八路灯控名称",
                        "输入窗帘自定义名称", "输入第一路窗帘自定义名称", "输入第二路窗帘自定义名称",
                        "输入第八路灯控自定义名称")
            }
            "A511", "A611", "A711", "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3","ADB2" -> //                init_air_mode();
                list_for_air_mode!!.visibility = View.VISIBLE
            "A501", "A601", "A701" -> {
                list_for_air_mode!!.visibility = View.VISIBLE
            }
            "A801", "A901", "A911", "AB01", "A902", "AB04", "AC01", "AE02", "AD01", "AD02", "AD03", "B001", "B201", "B101", "B202", "B301", "B401", "B402", "B403", "AA02", "202", "206", "AA03", "AA04", "AA05", "网关","A088", "B501","ADB1","ADB3" -> find_panel_btn!!.visibility = View.GONE
        }
    }

    private fun common_second(name1: String, name2: String,
                              name3: String, name4: String,
                              name5: String, name6: String,
                              name7: String, name8: String) {
        common_promat(name1, name2, name3, name4)
        edit_one!!.hint = name5
        edit_two!!.hint = name6
        edit_three!!.hint = name7
        edit_four!!.hint = name8
    }

    /**
     * 提示公共方法
     *
     * @param name1
     * @param name2
     * @param name3
     * @param name4
     */
    private fun common_promat(name1: String, name2: String, name3: String, name4: String) {
        linear_one!!.visibility = View.VISIBLE
        linear_two!!.visibility = View.VISIBLE
        linear_three!!.visibility = View.VISIBLE
        linear_four!!.visibility = View.VISIBLE
        first_txt!!.text = name1
        second_txt!!.text = name2
        three_txt!!.text = name3
        four_txt!!.text = name4
    }
}