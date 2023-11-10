package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.bigkoo.pickerview.TimePickerView
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.EditLinkDeviceCondinationAndResultAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/6/20.
 */
class SetSelectLinkActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var pvCustomTime: TimePickerView? = null

    @JvmField
    @BindView(R.id.sleep_time_rel)
    var sleep_time_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.get_up_rel)
    var get_up_rel: RelativeLayout? = null

    //    @BindView(R.id.sleep_time_txt)
    //    TextView sleep_time_txt;
    //    @BindView(R.id.get_up_time_txt)
    //    TextView get_up_time_txt;
    private var hourPicker: String? = null
    private var minutePicker: String? = null
    var time_content: String? = null

    @JvmField
    @BindView(R.id.start_time_txt)
    var start_time_txt: TextView? = null

    @JvmField
    @BindView(R.id.end_time_txt)
    var end_time_txt: TextView? = null
    private var index_select = 0

    @JvmField
    @BindView(R.id.link_name_edit)
    var link_name_edit: ClearLengthEditText? = null

    @JvmField
    @BindView(R.id.time_select_linear)
    var time_select_linear: LinearLayout? = null

    //member_list_rel
//    @JvmField
//    @BindView(R.id.member_list_rel)
//    var member_list_rel: RelativeLayout? = null
//
//    //member_num
//    @JvmField
//    @BindView(R.id.member_num)
//    var member_num: TextView? = null

    //member_list_linear
    @JvmField
    @BindView(R.id.member_list_linear)
    var member_list_linear: LinearLayout? = null


//    //member_list_linear
//    @JvmField
//    @BindView(R.id.txt_member)
//    var txt_member: LinearLayout? = null

    //member_list_linear
    @JvmField
    @BindView(R.id.maclistview_id_member)
    var maclistview_id_member: ListViewForScrollView_New? = null

    //member_list_linear
    @JvmField
    @BindView(R.id.member_edit)
    var member_edit: TextView? = null

    //list_forscrollview

    @JvmField
    @BindView(R.id.list_forscrollview)
    var list_forscrollview: ScrollView? = null


    private var dialogUtil: DialogUtil? = null
    private var list_condition = ArrayList<Map<*, *>>()
    private var list_result = ArrayList<Map<*, *>>()
    private var startTime: String? = ""
    private var endTime: String? = ""
    private var is_editlink = false
    private var linkId: String? = ""

    //  private var link_information: Map<*, *>? = HashMap<Any?, Any?>()
    var link_information: Map<*, *> = HashMap<Any, Any>()
    private var linkName: String? = ""
    private var type: String? = null
    private var areaNumber: String? = null
    private var person_lock_nums: String? = ""
    private var personsInfos: MutableList<Map<*, *>> = ArrayList()
    private var list_select: MutableList<Map<*, *>> = ArrayList()
    private var editLinkDeviceCondinationAndResultAdapter_members: EditLinkDeviceCondinationAndResultAdapter? = null

    private val REQUEST_SCENE_MEMBER_LIST = 0x102
    override fun viewId(): Int {
        return R.layout.set_select_link_lay
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        //        linkId = (String) getIntent().getSerializableExtra("linkId");
        init_get()
        members_adapter()
        init_data()
    }

    override fun onEvent() {
        member_list_linear!!.setOnClickListener(this)
    }

    private fun members_adapter() {
        //场景关联人员列表
        editLinkDeviceCondinationAndResultAdapter_members = EditLinkDeviceCondinationAndResultAdapter("member",
                this@SetSelectLinkActivity, list_select, object : EditLinkDeviceCondinationAndResultAdapter.ExcutecuteListener {
            override fun excute_cordition() {}
            override fun excute_result() {}
        })
        maclistview_id_member!!.adapter = editLinkDeviceCondinationAndResultAdapter_members
    }

    override fun onData() {}
    private fun init_get() {
        link_information = intent.getSerializableExtra("link_information") as Map<*, *>
        //        String link_edit = (String) link_information.get("linkId");
//        map.put("link_edit", true);
//        map.put("linkId", list.get(position).id);
//        map.put("linkName", list.get(position).name);
////                intent.putExtra("link_edit", true);
////                intent.putExtra("linkId", list.get(position).id);
//        intent.putExtra("link_information", (Serializable) map);
        if (link_information != null) { //来自联动列表的编辑按钮
//            list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
//            list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
            //获取接口的联动列表信息，设备联动信息，sraum_deviceLinkInfo，
//            linkId = (String) getIntent().getSerializableExtra("linkId");
            linkId = link_information.get("linkId") as String?
            linkName = link_information.get("linkName") as String?
            startTime = link_information.get("startTime") as String?
            endTime = link_information.get("endTime") as String?
//            start_time_txt!!.text = startTime
//            end_time_txt!!.text = endTime
            link_name_edit!!.setText(linkName)
        } else {
            linkId = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "linkId", "") as String
        }
    }

    private fun init_data() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        initCustomTimePicker()
        sleep_time_rel!!.setOnClickListener(this)
        get_up_rel!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this)
        list_result = SharedPreferencesUtil.getInfo_List(this@SetSelectLinkActivity, "list_result") as ArrayList<Map<*, *>>
        list_condition = SharedPreferencesUtil.getInfo_List(this@SetSelectLinkActivity, "list_condition") as ArrayList<Map<*, *>>
        type = list_condition[0]["type"] as String?
        when (type) {
            "100" -> {
                time_select_linear!!.visibility = View.VISIBLE
                startTime = start_time_txt!!.text.toString()
                endTime = end_time_txt!!.text.toString()
            }
            "101" -> time_select_linear!!.visibility = View.GONE
            "102" -> {
                time_select_linear!!.visibility = View.GONE
                startTime = list_condition[0]["startTime"] as String?
                endTime = list_condition[0]["endTime"] as String?
            }
        }

        if (list_condition[0] != null)
            when (list_condition[0]["deviceType"].toString()) {
                "AD02" -> {
                    member_list_linear!!.visibility = View.GONE
                }
                "15" -> {//智能门锁
                    //(list_condition[0] as HashMap<String,Any>)["minValue"] = person_lock_nums!!
                    member_list_linear!!.visibility = View.VISIBLE
                    Thread(Runnable {
                        get_allpersoninfo()
                    }).start()

                }
                else -> {
                    member_list_linear!!.visibility = View.GONE
                }
            }

        is_editlink = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "editlink", false) as Boolean
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
                val content = link_name_edit!!.text.toString()
                if (content == null || content == "") {
                    ToastUtil.showToast(this@SetSelectLinkActivity, "请输入联动名称")
                } else {
                    if (is_editlink) { //说明联动要更新了，编辑更新
                        when (type) {
                            "100" -> {
                                startTime = start_time_txt!!.text.toString()
                                endTime = end_time_txt!!.text.toString()
                                getData(is_editlink, content, linkId, ApiHelper.sraum_updateDeviceLink)
                            }
                            "102" -> getData(is_editlink, content, linkId, ApiHelper.sraum_updateDeviceLink)
                            "101" -> set_Hand_Data(is_editlink, content, linkId, ApiHelper.sraum_editManuallyScene
                            )
                        }
                    } else { //直接设置联动
                        when (type) {
                            "100" -> {
                                startTime = start_time_txt!!.text.toString()
                                endTime = end_time_txt!!.text.toString()
                                getData(is_editlink, content, linkId, ApiHelper.sraum_setDeviceLink)
                            }
                            "102" -> getData(is_editlink, content, linkId, ApiHelper.sraum_setDeviceLink)
                            "101" -> set_Hand_Data(is_editlink, content, linkId, ApiHelper.sraum_addManuallyScene
                            )
                        }
                    }
                    //                    SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_result", new ArrayList<Map>());
//                    SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_condition", new ArrayList<Map>());
//                }
                }
            }
            R.id.sleep_time_rel -> {
                index_select = 1
                pvCustomTime!!.show() //弹出自定义时间选择器
            }
            R.id.get_up_rel -> {
                index_select = 0
                pvCustomTime!!.show() //弹出自定义时间选择器
            }

            R.id.member_list_linear -> {//人脸锁关联人员列表
                var intent: Intent? = null
                areaNumber = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "areaNumber", "") as String
                val authType = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "authType", "") as String
                intent = Intent(this@SetSelectLinkActivity, CustomDefineMemberSceneActivity::class.java)

//                if (list_condition[0] != null) {
//                    intent.putExtra("deviceNumber", list_condition[0]["deviceId"] as String) //controllerNumber
//                }
//                intent.putExtra("areaNumber", areaNumber)
//                intent.putExtra("authType", authType)
                intent.putExtra("action", "add")
                intent.putExtra("personsInfos", personsInfos as Serializable)
                intent.putExtra("list_select", list_select as Serializable)
                startActivityForResult(intent, REQUEST_SCENE_MEMBER_LIST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCENE_MEMBER_LIST && resultCode == Activity.RESULT_OK) {
            //person_lock_nums
            person_lock_nums = data!!.getStringExtra("person_lock_nums")
            //member_num!!.text = person_lock_nums

            personsInfos = if (data!!.getSerializableExtra("personsInfos") != null) data!!.getSerializableExtra("personsInfos")
                    as MutableList<Map<*, *>>
            else ArrayList()

            //member_num!!.text = person_lock_nums

            when (list_condition[0]["deviceType"]) {
                "15" -> {//智能门锁
                    // (list_condition[0] as HashMap<String, Any>)["minValue"] = person_lock_nums!!
                    (list_condition[0] as HashMap<Any, Any>)["minValue"] = person_lock_nums!!
                    if (personsInfos.size != 0) {
                        list_select = ArrayList()
                        list_sel_item()
                        for (i in personsInfos.indices) {
                            if (personsInfos[i]["sign"]!!.equals("3")) {
                                list_select.add(personsInfos[i])
                            }
                            //  list_select.add(map)
                        }
                    }


                    val stringBuffer_num = StringBuffer()
                    // val stringBuffer_value = StringBuffer()
                    for (i in list_select!!.indices) {
                        if (i != list_select!!.size - 1) {
                            stringBuffer_num.append(list_select!![i]["personNo"] as String + ",")
                            //stringBuffer_value.append(list_select!![i].age.toString() + ",")
                        } else {
                            stringBuffer_num.append(list_select!![i]["personNo"] as String)
                            // stringBuffer_value.append(list!![i].age)
                        }
                    }

                    person_lock_nums = stringBuffer_num.toString()
                    //member_num!!.text = person_lock_nums

                    list_sel_item()
                    editLinkDeviceCondinationAndResultAdapter_members!!.setlist(list_select)
                    (list_condition[0] as HashMap<Any, Any>)["minValue"] = person_lock_nums!!

                }
            }

//            val list = ArrayList<java.util.HashMap<Any, Any>>()
//            list[0]["a"] = "name"
        }
    }

    private fun list_sel_item() {
        if (list_select.size == 0) {
            //member_num!!.text = person_lock_nums
            var map_person = HashMap<Any?, Any?>()
            map_person["content"] = "所有人"
            map_person["personNo"] = ""
            map_person["personName"] = ""
            map_person["sign"] = ""
            list_select.add(map_person)
        } else if (list_select.size > 1 && list_select[0]["content"]!!.equals("所有人")) {
            list_select.removeAt(0)
        }
    }

    private fun initCustomTimePicker() {
        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        val selectedDate = Calendar.getInstance() //系统当前时间
        val startDate = Calendar.getInstance()
        startDate[2014, 1] = 23
        val endDate = Calendar.getInstance()
        endDate[2027, 2] = 28
        //时间选择器 ，自定义布局
        pvCustomTime = TimePickerView.Builder(this, TimePickerView.OnTimeSelectListener { date, v ->
            //选中事件回调
            getTime(date)
            Log.e("robin debug", "getTime(date):" + getTime(date))
            hourPicker = date.hours.toString()
            minutePicker = date.minutes.toString()
            when (minutePicker.toString().length) {
                1 -> minutePicker = "0$minutePicker"
            }
            when (hourPicker.toString().length) {
                1 -> hourPicker = "0$hourPicker"
            }
            time_content = "$hourPicker:$minutePicker"
            handler.sendEmptyMessage(index_select)
            //                start_scenetime.setText(hourPicker + ":" + minutePicker);
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time) { v ->
                    val tvSubmit = v.findViewById<View>(R.id.ok_bt) as ImageView
                    val ivCancel = v.findViewById<View>(R.id.finish_bt) as ImageView
                    tvSubmit.setOnClickListener {
                        pvCustomTime!!.returnData()
                        pvCustomTime!!.dismiss()
                    }
                    ivCancel.setOnClickListener { pvCustomTime!!.dismiss() }
                }
                .setContentSize(18)
                .setType(booleanArrayOf(false, false, false, true, true, false))
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(-0xdb5263)
                .build()
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> start_time_txt!!.text = time_content
                1 -> end_time_txt!!.text = time_content
            }
        }
    }

    private fun getTime(date: Date): String { //可根据需要自行截取数据显示
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    /**
     * 添加手动场景
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private fun set_Hand_Data(flag: Boolean, linkName: String, linkId: String?, apiname: String) {
        val map: MutableMap<String, Any?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@SetSelectLinkActivity)
        map["areaNumber"] = areaNumber
        if (flag) {
            map["number"] = linkId
        } else {
            map["sceneName"] = linkName
            map["sceneType"] = "1"
            map["panelNumber"] = ""
            map["buttonNumber"] = ""
        }
        val list: MutableList<Map<*, *>> = ArrayList()
        for (i in list_result.indices) {
            val map_device = HashMap<Any?, Any?>()
            map_device["type"] = list_result[i]["type"]
            map_device["number"] = list_result[i]["number"]
            map_device["status"] = list_result[i]["status"]
            map_device["dimmer"] = list_result[i]["dimmer"]
            map_device["mode"] = list_result[i]["mode"]
            map_device["temperature"] = list_result[i]["temperature"]
            map_device["speed"] = list_result[i]["speed"]
            map_device["panelMac"] = if (list_result[i]["panelMac"] == null) "" else list_result[i]["panelMac"]
            map_device["gatewayMac"] = if (list_result[i]["gatewayMac"] == null) "" else list_result[i]["gatewayMac"]
            list.add(map_device)
        }
        map["deviceList"] = list
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(apiname, map,
                object : Mycallback(AddTogglenInterfacer { getData(flag, linkName, linkId, apiname) }, this@SetSelectLinkActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                        common()
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        common()
                        val boxNumber = user.boxNumber
                        //                      手动场景 添加的时候我会返回一个boxNumber,这个参数有值，代表你加的是网关场景，然后你可以做关联面板，这个参数没有值，那就是云场景
//                                没有关联面板这个操作
                        if (boxNumber != null && boxNumber != "") { //去关联面板
                            val bundle1 = Bundle()
                            bundle1.putString("sceneName", linkName)
                            bundle1.putString("sceneId", linkId)
                            bundle1.putString("sceneType", "1")
                            bundle1.putString("boxNumber", boxNumber)
                            bundle1.putString("panelType", "")
                            bundle1.putString("panelNumber", "")
                            bundle1.putString("buttonNumber", "")
                            IntentUtil.startActivity(this@SetSelectLinkActivity, AssociatedpanelActivity::class.java, bundle1)
                        }
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                        common()
                    }

                    override fun pullDataError() {
                        common()
                    }

                    override fun sevenCode() {
                        common()
                    }

                    override fun fiveCode() {
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, """
     添加失败（硬件
     返回）
     """.trimIndent())
                    }

                    override fun fourCode() {
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, "设备列表信息有误")
                    }

                    override fun threeCode() {
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, "sceneName 已存在")
                    }

                    override fun wrongBoxnumber() {
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, """
     areaNumber
     不存在
     """.trimIndent())
                    }
                })
    }

    /**
     * 添加自动场景
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private fun getData(flag: Boolean, linkName: String, linkId: String?,
                        apiname: String) {
        val map: MutableMap<String, Any?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@SetSelectLinkActivity)
        map["areaNumber"] = areaNumber
        map["deviceId"] = list_condition[0]["deviceId"]
        map["deviceType"] = list_condition[0]["deviceType"]
        map["linkName"] = linkName
        map["type"] = list_condition[0]["type"]
        when (list_condition[0]["deviceType"].toString()) {
            "AD02" -> {
                map["deviceType"] = "102"
                member_list_linear!!.visibility = View.GONE
            }
            "B201" -> {//智能门锁
                //(list_condition[0] as HashMap<String,Any>)["minValue"] = person_lock_nums!!
                member_list_linear!!.visibility = View.VISIBLE
            }
            else -> {
                member_list_linear!!.visibility = View.GONE
            }
        }
        map["condition"] = list_condition[0]["condition"]
        map["minValue"] = list_condition[0]["minValue"]
        map["maxValue"] = list_condition[0]["maxValue"]
        map["startTime"] = startTime
        map["endTime"] = endTime
        if (flag) {
            map["linkId"] = linkId
        } else {
        }
        val list: MutableList<Map<*, *>> = ArrayList()
        for (i in list_result.indices) {
            val map_device = HashMap<Any?, Any?>()
            map_device["type"] = list_result[i]["type"]
            map_device["number"] = list_result[i]["number"]
            map_device["status"] = list_result[i]["status"]
            map_device["dimmer"] = list_result[i]["dimmer"]
            map_device["mode"] = list_result[i]["mode"]
            map_device["temperature"] = list_result[i]["temperature"]
            map_device["speed"] = list_result[i]["speed"]
            list.add(map_device)
        }
        map["deviceList"] = list
        dialogUtil!!.loadDialog() //loading
        MyOkHttp.postMapObject(apiname, map,
                object : Mycallback(AddTogglenInterfacer { getData(flag, linkName, linkId, apiname) }, this@SetSelectLinkActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                        common()
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        common()
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, """
     areaNumber 错
     误
     """.trimIndent())
                    }

                    override fun pullDataError() {
                        common()
                    }

                    override fun sevenCode() {
                        common()
                    }

                    override fun fiveCode() {
                        common()
                    }

                    override fun fourCode() {
                        common()
                    }

                    override fun threeCode() {
                        common()
                        ToastUtil.showToast(this@SetSelectLinkActivity, "deviceId 错误")
                    }

                    override fun wrongBoxnumber() {
                        common()
                    }
                })
    }

    /**
     * 共同执行的代码
     */
    private fun common() {
        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
        //        Intent intent = new Intent(SetSelectLinkActivity.this, LinkageListActivity.class);
//        startActivity(intent);
        finish()
        SharedPreferencesUtil.saveData(this@SetSelectLinkActivity, "linkId", "")
        SharedPreferencesUtil.saveInfo_List(this@SetSelectLinkActivity, "list_result", ArrayList())
        SharedPreferencesUtil.saveInfo_List(this@SetSelectLinkActivity, "list_condition", ArrayList())
        SharedPreferencesUtil.saveData(this@SetSelectLinkActivity, "editlink", false)
        SharedPreferencesUtil.saveInfo_List(this@SetSelectLinkActivity, "link_information_list", ArrayList())
    }


    private fun get_allpersoninfo() {
        //获取网关名称（APP->网关）
        val map = HashMap<Any?, Any?>()
        map["areaNumber"] = SharedPreferencesUtil.getData(this@SetSelectLinkActivity, "areaNumber", "") as String
        map["deviceNumber"] = list_condition[0]["deviceId"]!!
        map["token"] = TokenUtil.getToken(this@SetSelectLinkActivity)
        MyOkHttp.postMapObject(ApiHelper.sraum_getRelatePerson, map as Map<String, Object>,
                object : Mycallback(AddTogglenInterfacer { get_allpersoninfo() }, this@SetSelectLinkActivity, null) {
                    override fun onSuccess(user: User) {
//                        project_select.setText(user.name);
                        personsInfos = ArrayList()

                        var map = HashMap<Any?, Any?>()
                        map["content"] = "所有人"
                        map["personNo"] = ""
                        map["personName"] = ""
                        map["sign"] = ""
                        personsInfos.add(map)

                        for (i in user.personList.indices) {
                            val map = HashMap<Any?, Any?>()
                            map["content"] = user.personList[i].personNo + "-" + user.personList[i].personName
                            map["personNo"] = user.personList[i].personNo
                            map["personName"] = user.personList[i].personName
                            map["sign"] = user.personList[i].sign
                            personsInfos.add(map)
                            if (user.personList[i].sign.equals("3"))
                                list_select.add(map)

                        }

                        list_sel_item()
                        editLinkDeviceCondinationAndResultAdapter_members!!.setlist(list_select)
                        //  project_select!!.text = "人员列表" + "(" + user.roomList.size + ")"


//                        val stringBuffer_num = StringBuffer()
//                        // val stringBuffer_value = StringBuffer()
//                        for (i in list_select!!.indices) {
//                            if (i != list_select!!.size - 1) {
//                                stringBuffer_num.append(list_select!![i]["personNo"] as String + ",")
//                                //stringBuffer_value.append(list_select!![i].age.toString() + ",")
//                            } else {
//                                stringBuffer_num.append(list_select!![i]["personNo"] as String)
//                                // stringBuffer_value.append(list!![i].age)
//                            }
//                        }
//                        runOnUiThread({
//                            member_num!!.text = person_lock_nums
//                        })

//                        when (list_condition[0]["deviceType"]) {
//                            "15" -> {//智能门锁
//                                (list_condition[0] as HashMap<String, Any>)["minValue"] = person_lock_nums!!
//                            }
//                        }

                    }

                    override fun threeCode() {}
                })
    }
}