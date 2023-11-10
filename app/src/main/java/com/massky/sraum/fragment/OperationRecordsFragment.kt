package com.massky.sraum.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.bigkoo.pickerview.TimePickerView
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.MessageActivity
import com.massky.sraum.activity.OperationRecordsDetailActivity
import com.massky.sraum.adapter.SelectOperateRecordsAdapter
import com.massky.sraum.base.Basecfragment
import com.massky.sraum.view.XListView_ForMessage
import okhttp3.Call
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.HashMap

/**
 * Created by masskywcy on 2016-09-05.
 */
/*用于第一个fragment主界面*/
class OperationRecordsFragment : Basecfragment(), AdapterView.OnItemClickListener, XListView_ForMessage.IXListViewListener, AdapterView.OnItemLongClickListener {
    private var selectdevicemessageAdapter: SelectOperateRecordsAdapter? = null
    private var mMessageReceiver: MessageReceiver? = null
    private var content = ""
    private val account_view: View? = null
    private var dialogUtil: DialogUtil? = null
    private val recordLists: MutableList<MutableMap<*, *>> = ArrayList()

    private val es = Executors.newSingleThreadExecutor()
    private var future: Future<String>? = null


    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {//refresh
                    on_Refresh()
                }
                1 -> {//load
                    Log.e("load", "handleMessage: " + "onLoad")
                    onLoad_More()
                }
            }
        }
    }


    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView_ForMessage? = null

    @JvmField
    @BindView(R.id.linear_popcamera)
    var linear_popcamera: LinearLayout? = null

    @JvmField
    @BindView(R.id.all_read_linear)
    var all_read_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.all_select_linear)
    var all_select_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.delete_linear)
    var delete_linear: LinearLayout? = null


    //pick_data_txt
    @JvmField
    @BindView(R.id.pick_data_txt)
    var pick_data_txt: TextView? = null

    //private val mHandler = Handler()
    private var recordLists_local: List<Map<*, *>> = ArrayList()
    private var str = "全选"
    //
    //pvCustomTime

    private var pvCustomTime: TimePickerView? = null
    private var local_time: String? = null

    @JvmField
    @BindView(R.id.all_select_txt)
    var all_select_txt: TextView? = null

    //time_select_lienar
    @JvmField
    @BindView(R.id.time_select_lienar)
    var time_select_lienar: RelativeLayout? = null

    //time_select_lienar
    @JvmField
    @BindView(R.id.view_middle)
    var view_middle: RelativeLayout? = null

    //pick_time

    @JvmField
    @BindView(R.id.pick_time)
    var pick_time: TextView? = null


    private var backfrom_activity = false
    private var messageId: String? = ""

    private var del_action: Boolean? = false
    override fun viewId(): Int {
        return R.layout.operation_records_fragment
    }

    override fun onStart() { //onStart()-这个方法在屏幕唤醒时调用。
        super.onStart()
        str = "全选"
        if (backfrom_activity) {
            backfrom_activity = false
            for (i in recordLists.indices) {
                if (recordLists[i]["id"].toString() == messageId) {
                    //
                    //recordLists[i]
                    (recordLists[i] as HashMap<String, Any>)["readStatus"] = "1"
                }
            }
            SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
            selectdevicemessageAdapter!!.setList(recordLists)
        } else {
            // get_message(true, "refresh")
            load_more_future(true, "refresh")
        }
    }

    /**
     * 退出动画
     */
    private fun exitanimation() {
        val animation = AnimationUtils.loadAnimation(
                activity, R.anim.dialog_exit)
        linear_popcamera!!.clearAnimation()
        linear_popcamera!!.animation = animation
        linear_popcamera!!.visibility = View.GONE
        xListView_scan!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 1.0f)
        linear_popcamera!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0.0f)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * 进入动画
     */
    private fun enteranimation() {
        xListView_scan!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 1.0f)
        linear_popcamera!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0.0f)
        val animation = AnimationUtils.loadAnimation(
                activity, R.anim.dialog_enter)
        linear_popcamera!!.clearAnimation()
        linear_popcamera!!.animation = animation
        linear_popcamera!!.visibility = View.VISIBLE
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * listview 局部刷新 最新load数据
     */

    private fun updateView(recordLists: MutableList<MutableMap<*, *>>, itemIndex: Int) {
        //得到第一个可显示控件的位置，
        val visiblePosition: Int = xListView_scan!!.getFirstVisiblePosition()
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            //得到要更新的item的view
            val view: View = xListView_scan!!.getChildAt(itemIndex - visiblePosition)
            //调用adapter更新界面
            selectdevicemessageAdapter!!.updateView(view, itemIndex, recordLists)
        }
    }


    private fun scroll_nasy() {
        var isFirstClick: Boolean? = true
        var lastY: Float? = -1F

        xListView_scan!!.setOnTouchListener({ v, event ->
            if (isFirstClick!!) { //只有第一次进来的时候用获取位置的方法给lastY赋值，后面的值都是上一次的move坐标
                //如果不做此判断，每次的lasty和movey是相同的值，这是因为在此处获取的的y值其实就是move的值，
                //是因为在listview中，down事件是默认传递进去给条目的，在此处无法响应down事件。
                lastY = event.y
                isFirstClick = false //初始值是true，此处置为false。
            }
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val moveY = event.y
                    Log.e("moveY_START", moveY.toString() + "")
                    if (moveY < lastY!!) {//向上滑动
                        view_middle!!.visibility = View.VISIBLE
                    } else {//向下滑动
                        view_middle!!.visibility = View.GONE
                    }
                    lastY = moveY
                }
            }
            false
        })
    }


    private fun onLoad_More() {
        // xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        // xListView_scan!!.setRefreshTime("刚刚")
    }

    private fun on_Refresh() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.setRefreshTime("刚刚")
    }


//    private fun onLoad() {
//        xListView_scan!!.stopRefresh()
//        xListView_scan!!.stopLoadMore()
//        xListView_scan!!.setRefreshTime("刚刚")
//    }
//
//    override fun onRefresh() {
//        onLoad()
//        pick_data_txt!!.setText(local_time)
//        get_message(true, "refresh")
//    }


    override fun onRefresh() {
        // on_Refresh()
        pick_data_txt!!.setText(local_time)

        // get_message(true, "refresh")
        load_more_future(true, "refresh")
//        Thread(
//                Runnable {
//                    while (true) {
//                        load_more_future(true, "load")
//                        Thread.sleep(3000)
//                    }
//                }
//        ).start()

    }

    override fun onLoadMore() {
        // onLoad_More()
        //Thread(Runnable { get_message(false, "doit") }).start()
        load_more_future(false, "load")
    }


    private fun stop_future() {
        if (future != null && !future!!.isDone()) {
            future!!.cancel(true)
        }
    }


//    override fun onLoadMore() {
//        onLoad()
//        Thread(Runnable { get_message(false, "doit") }).start()
//    }
    //    /**
    //     * 底部弹出拍照，相册弹出框
    //     */
    //    private void addViewid() {
    //        account_view = LayoutInflater.from(getActivity()).inflate(R.layout.message_function_select, null);
    //        all_read_linear = (LinearLayout) account_view.findViewById(R.id.all_read_linear);
    //        all_select_linear = (LinearLayout) account_view.findViewById(R.id.all_select_linear);
    //        delete_linear = (LinearLayout) account_view.findViewById(R.id.delete_linear);
    //
    //        all_read_linear.setOnClickListener(this);
    //        all_select_linear.setOnClickListener(this);
    //        delete_linear.setOnClickListener(this);
    //
    ////        //common_setting_image
    ////        common_setting_image = (ImageView) account_view.findViewById(R.id.common_setting_image);
    //        dialogUtil = new DialogUtil(getActivity(), account_view, 2);
    //    }
    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(MessageActivity.MESSAGE_FRAGMENT)
        activity!!.registerReceiver(mMessageReceiver, filter)
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        return true
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == MessageActivity.MESSAGE_FRAGMENT) {
                content = intent.getSerializableExtra("action") as String
                //                switch (content) {
//                    case "编辑":
//                        common_del("");
//                        break;
//                    case "取消":
////                        recordLists.clear();
////                        SharedPreferencesUtil.saveInfo_List(getActivity(), "recordLists", recordLists);
////                        get_message(false, "");
//                        break;
//                }
                common_del("", null)
            }
        }
    }

    private var endId = "0"
    private var searchValue: String? = ""
    private var local_searchValue: String? = ""
    private var nextSearchValue: String? = ""
    private var year_month = ""
    private fun get_message(isRefresh: Boolean, doit: String) {
        if (isRefresh) {
            endId = "0"
            year_month = ""
        }
        when (doit) {
            "refresh" -> {
                searchValue = local_searchValue
            }
        }
        sraum_getOperateRecords(isRefresh, doit)
    }

    var last_index: Int? = 0
    private fun sraum_getOperateRecords(isRefresh: Boolean, doit: String) {
//        dialogUtil.loadDialog();
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(context)
        map["areaNumber"] = areaNumber
        map["endId"] = endId
        map["searchValue"] = searchValue

        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getOperateRecordsNew, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { sraum_getOperateRecords(isRefresh, doit) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                if (isRefresh) {
                    recordLists.clear()
                    SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
                } else {
                    str = "全选"
                }
                last_index = recordLists.lastIndex
                Log.e("last", "onSuccess: " + last_index)
                //                recordList.addAll(user.recordList);
                val recordLists_new: MutableList<MutableMap<*, *>> = ArrayList()

                for (i in user.recordList.indices) {
                    val map = HashMap<Any?, Any?>()
                    product_maps(map, user, i)
                    when (if (user.recordList[i].recordTitile == null) "" else user.recordList[i].recordTitile) {
                        "100" -> {

                        }
                        else -> {
                            recordLists.add(map)
                            recordLists_new.add(map)
                        }
                    }
                }
                for (i in recordLists.indices) {
                    var map = recordLists[i]
                    common_select_dates(i, map as HashMap<Any?, Any?>)
                }
                common_del(doit, recordLists_new)
                endId = user.endId
                searchValue = user.nextSearchValue
                //                if( dialogUtil != null){
//                    dialogUtil.removeviewBottomDialog();
//                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun product_maps(map: HashMap<Any?, Any?>, user: User, i: Int) {
        map["id"] = if (user.recordList[i].id == null) "" else user.recordList[i].id
        map["recordTitile"] = if (user.recordList[i].recordTitile == null) "" else user.recordList[i].recordTitile
        map["controlType"] = if (user.recordList[i].controlType == null) "" else user.recordList[i].controlType
        //                    if (user.recordList[i].deviceName != null) {
        //                        map["deviceName"] = if (user.recordList[i].deviceName == null) "" else user.recordList[i].deviceName
        //                    } else {
        //                        map["deviceName"] = ""
        //                    }
        //                    map["readStatus"] = if (user.recordList[i].readStatus == null) "" else user.recordList[i].readStatus
        map["dateTime"] = if (user.recordList[i].dateTime == null) "" else user.recordList[i].dateTime
        get_date_time(map)
        //common_select_dates(user, i, map)
        map["ischecked"] = false
    }

    private fun get_date_time(map: HashMap<Any?, Any?>) {
        var date_time = if (map["dateTime"] == null) "" else map["dateTime"] as String

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date?
        date = format.parse(date_time)
        val format1 = SimpleDateFormat("MM-dd HH:mm:ss")
        var normal_date = format1.format(date)
        map["date_time"] = normal_date
    }

    private fun common_select_dates(i: Int?, map: HashMap<Any?, Any?>) {
        var date_time = if (map["dateTime"] == null) "" else map["dateTime"] as String


        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date?
        date = format.parse(date_time)
        val format1 = SimpleDateFormat("yyyy-MM")
        var normal_date = format1.format(date)
        map["date"] = normal_date

        if (i == 0) {
            year_month = normal_date
            map["show_date"] = true
        } else {
            if (year_month.equals(normal_date)) {
                map["show_date"] = false
            } else {
                year_month = normal_date
                map["show_date"] = true
            }
        }
    }

    /**
     * 公共处理方法
     */
    private fun common_del(doit: String, recordlistsNew: MutableList<MutableMap<*, *>>?) {
        when (content) {
            "按月删除" -> {
                // content = ""
                del_action = true
                pvCustomTime!!.show() //弹出自定义时间选择器
            }
            "编辑" -> {
                if (doit == "") {
                    activity!!.runOnUiThread { //                            dialogUtil.loadViewBottomdialog();
                        enteranimation()
                    }
                }
                var i = 0
                while (i < recordLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = true
                    i++
                }
            }
            "取消" -> if (doit == "") {
                activity!!.runOnUiThread { //                            dialogUtil.loadViewBottomdialog();
                    str = "全选"
                    all_select_txt!!.text = str
                    exitanimation()
                }
                var i = 0
                while (i < recordLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    // recordLists[i]
                    (recordLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < recordLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    recordLists_local = SharedPreferencesUtil.getInfo_List(activity, "recordLists")
                    if (recordLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
                        var j = 0
                        while (j < recordLists_local.size) {
                            if (recordLists_local[j]["id"] ==
                                    recordLists[i]["id"]) {
                                //  recordLists[i]
                                (recordLists[i] as HashMap<String, Any>)["ischecked"] = recordLists_local[j]["ischecked"] as Boolean
                            }
                            j++
                        }
                    }
                    i++
                }
            }
            else -> if (doit == "") {
                activity!!.runOnUiThread {
                    str = "全选"
                    all_select_txt!!.text = str
                    exitanimation()
                }
                var i = 0
                while (i < recordLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    // recordLists[i]
                    (recordLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < recordLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    recordLists_local = SharedPreferencesUtil.getInfo_List(activity, "recordLists")
                    if (recordLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
                        var j = 0
                        while (j < recordLists_local.size) {
                            if (recordLists_local[j]["id"] ==
                                    recordLists[i]["id"]) {
                                // recordLists[i]
                                (recordLists[i] as HashMap<String, Any>)["ischecked"] = recordLists_local[j]["ischecked"] as Boolean
                            }
                            j++
                        }
                    }
                    i++
                }
            }
        }
        SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
        //  when (last_index) {
//            -1 -> {
//                selectdevicemessageAdapter!!.setList(recordLists)
//            }
//            else -> {
//
//                selectdevicemessageAdapter!!.setLoadList(recordlistsNew!!)
        //selectdevicemessageAdapter!!.notifyDataSetChanged()
//                var i: Int = 0
//
//                while (i < this!!.last_index!!) {
//                    i++
//                    updateView(recordLists,i)
//                }
//
//                while (i < recordLists.size - 1) {
//                    i++
//                    updateView(recordLists,i)
//                }
//            }
        // }
        selectdevicemessageAdapter!!.setList(recordLists)
    }

    override fun onView() {
        registerMessageReceiver()
        xListView_scan!!.onItemClickListener = this
        xListView_scan!!.onItemLongClickListener = this
        selectdevicemessageAdapter = SelectOperateRecordsAdapter(object : SelectOperateRecordsAdapter.ScrollSelectListener {
            override fun scroll_select_date(string: String, show_date: Boolean) {
                pick_time!!.text = string
                if (show_date) {
                    view_middle!!.visibility = View.GONE
                } else {
                    view_middle!!.visibility = View.VISIBLE
                }

//
//                if (moveY < lastY!!) {//向上滑动
//                    view_middle!!.visibility = View.VISIBLE
//                } else {//向下滑动
//                    view_middle!!.visibility = View.GONE
//                }
            }

            override fun onByItemClick(view: View, position: Int) {
                onItemClickMeth(view, position)
            }

            override fun onItemDel(position: Int) {
                delete(recordLists[position]["date"].toString(), recordLists[position]["recordTitile"].toString(),
                        recordLists[position]["id"].toString()
                )
            }

        }, xListView_scan!!, context!!, recordLists
        )
        xListView_scan!!.adapter = selectdevicemessageAdapter
        xListView_scan!!.setPullLoadEnable(true)
        xListView_scan!!.setXListViewListener(this)
        //        String stringBuffer = "1,2,3,";
//        String split = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
//        addViewid();
        all_read_linear!!.setOnClickListener(this)
        all_select_linear!!.setOnClickListener(this)
        delete_linear!!.setOnClickListener(this)
        time_select_lienar!!.setOnClickListener(this)
        dialogUtil = DialogUtil(activity)
        initCustomTimePicker()
        //scroll_nasy()
    }

    override fun initData() { //刷新数据，选择viewendIdr时刷新数据
//        get_message(false, "doit");
//        if(onDeviceMessageFragListener1 != null)
//        onDeviceMessageFragListener1.ondevice_message_frag();
//        ToastUtil.showToast(getActivity(),"DeviceMessageFragment");
    }

    override fun onClick(v: View) {
        when (v.id) {
            //  R.id.all_read_linear ->                 //全部已读
            // all_read()
            R.id.all_select_linear -> all_select(str)
            R.id.delete_linear -> delete(searchValue, "", "")
            R.id.time_select_lienar ->          //                showPopFormBottom(null);
                pvCustomTime!!.show() //弹出自定义时间选择器
        }
    }


    /**
     * 删除
     */
    private fun delete(action_value: String?, name: String?, ids: String?) {
//        val temp = ","
//        val stringBuffer = StringBuffer()
//        val string_names = StringBuffer()
//        for (i in recordLists.indices) {
//            if (recordLists[i]["ischecked"] as Boolean) {
//                stringBuffer.append(recordLists[i]["id"].toString() + temp)
//                string_names.append(recordLists[i]["recordTitile"].toString() + temp)
//            }
//        }
//        var split: String? = ""
//        var split_names: String? = ""
//        if (stringBuffer.toString().trim { it <= ' ' } == "" && ids.equals("")) {
//            // ToastUtil.showToast(activity, "请先进行勾选")
//            return
//        }
//        if (stringBuffer.length != 0) {
//            split = stringBuffer.toString().substring(0, stringBuffer.length - 1)
//            split_names = string_names.toString().substring(0, string_names.length - 1)
//        }
        // showCenterDeleteDialog(split_names,split,"all")

        when (str) {
            "全不选" ->
                showCenterDeleteDialog(action_value, name!!, "all")
            else -> showCenterDeleteDialog(action_value, name!!, ids!!)
        }
    }


    /**
     * 删除选中的
     *
     * @param split
     */
    private fun delete_select(action_value: String?, ids: String) {
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(context)
        map["areaNumber"] = areaNumber
        map["searchValue"] = action_value
//        when (content) {
//            "all" -> map["recordIds"] = content
//            else -> map["recordIds"] = split
//        }
        map["recordIds"] = ids
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteOperateRecord, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { delete_select(action_value, ids) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                when (content) {
                    "按月删除" -> {
                        content = ""
                        for (j in recordLists.indices) {
                            for (i in recordLists.indices) {
                                if (recordLists[i]["date"]!!.equals(action_value)) {
                                    recordLists.removeAt(i)
                                    break
                                }
                            }
                        }
                    }
                    else -> {
                        // val indexs = split.split(",".toRegex()).toTypedArray()
                        for (j in recordLists.indices) {
                            for (i in recordLists.indices) {
                                if (recordLists[i]["id"]!!.equals(ids)) {
                                    recordLists.removeAt(i)
                                    break
                                }
                            }
                        }
                    }
                }

                Log.e("record", "onSuccess: " + "delete")

                for (i in recordLists.indices) {
                    var map = recordLists[i]
                    common_select_dates(i, map as HashMap<Any?, Any?>)
                }

                SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
                selectdevicemessageAdapter!!.setList(recordLists)
                //                get_message(true, "doit");
                onDeviceMessageFragListener1!!.ondevice_message_frag()
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }


    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(action_value: String?, name: String,
                               ids: String) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        tv_title.text = name
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(context!!, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {
            delete_select(action_value, ids)
            dialog.dismiss()
        }
        confirm.setOnClickListener {
            content = ""
            dialog.dismiss()
        }
    }

    /**
     * 消息全选
     *
     * @param str
     */
    private fun all_select(str: String) {
        when (str) {
            "全选" -> {
                this.str = "全不选"
                var i = 0
                while (i < recordLists.size) {
                    // recordLists[i]
                    (recordLists[i] as HashMap<String, Any>)["ischecked"] = true
                    i++
                }
            }
            "全不选" -> {
                this.str = "全选"
                var i = 0
                while (i < recordLists.size) {
                    // recordLists[i]
                    (recordLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            }
        }
        all_select_txt!!.text = this.str
        selectdevicemessageAdapter!!.setList(recordLists)
        SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        onItemClickMeth(view, position)
    }

    private fun onItemClickMeth(view: View, position: Int) {
        when (content) {
            "编辑" -> {
                //                View v = parent.getChildAt(position - xListView_scan.getFirstVisiblePosition());
                val cb = view.findViewById<View>(R.id.checkbox) as CheckBox
                cb.toggle()
                //recordLists[position - 1]
                (recordLists[position] as HashMap<String, Any>)["ischecked"] = cb.isChecked
                SharedPreferencesUtil.saveInfo_List(activity, "recordLists", recordLists)
            }
            "取消" -> {
                //
                val intent = Intent(activity, OperationRecordsDetailActivity::class.java)
                intent.putExtra("Record", recordLists[position] as Serializable)
                startActivityForResult(intent, RECORD_MESSAGE_BACK)
            }
            else -> {
                val intent = Intent(activity, OperationRecordsDetailActivity::class.java)
                intent.putExtra("Record", recordLists[position] as Serializable)
                startActivityForResult(intent, RECORD_MESSAGE_BACK)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("peng", "MacFragment->onResume:name:")
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mMessageReceiver)
        SharedPreferencesUtil.saveInfo_List(activity, "recordLists", ArrayList())
        stop_future()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //得到新Activity 关闭后返回的数据
        messageId = data!!.extras!!.getString("result")
        if (messageId != null) {
            Log.e("zhu", "result:$messageId")
            backfrom_activity = true
        }
    }

    interface OnDeviceMessageFragListener {
        fun ondevice_message_frag()
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

//        val startDate = Calendar.getInstance()
//        startDate[2015, 1] = 31
//        val endDate = Calendar.getInstance()
//        endDate[2027, 2] = 31


//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
        val startDate = Calendar.getInstance()
        startDate[2015, 1] = 31
        val endDate = Calendar.getInstance()
        var sel_month = selectedDate[Calendar.MONTH]
        var sel_year = selectedDate[Calendar.YEAR]
        endDate[selectedDate[Calendar.YEAR], sel_month] = 31
        var sel_months = sel_month + 1

        if (sel_months <= 9)
            local_time = "$sel_year-0$sel_months"
        else
            local_time = "$sel_year-$sel_months"


        local_searchValue = local_time
        pick_data_txt!!.setText(local_time)

        //时间选择器 ，自定义布局
        pvCustomTime = TimePickerView.Builder(context, TimePickerView.OnTimeSelectListener { date, v -> //选中事件回调

//            Log.e("robin debug", "getTime(date):" + getTime(date))
//            var yearPicker = date.year.toString()
//            var monthPicker = (date.month + 1).toString()
////            when (yearPicker.length) {
////                1 -> minutePicker = "0$minutePicker"
////            }
//            when (monthPicker.length) {
//                1 -> monthPicker = "0$monthPicker"
//            }
            //pick_data_txt!!.setText("$yearPicker-$monthPicker")

            var time = getTime(date)
            if (del_action!!) {
                del_action = false
                // ToastUtil.showDelToast(activity, "按月删除")
                delete(time, "", "all")
            } else {

                pick_data_txt!!.setText(time)
                searchValue = getTime(date)
                //local_searchValue = searchValue

                //Thread(Runnable { get_message(true, "select_time") }).start()
                load_more_future(true, "select_time")
            }
            //get_message(true, "doit")
        }) /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               / *.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.gravity(Gravity.RIGHT)// default is center*/
                //.setDate(selectedDate)
                .setDate(selectedDate)
                .setRangDate(startDate, selectedDate)
                .setLayoutRes(R.layout.pickerview_custom_time) { v ->
                    val tvSubmit = v.findViewById<View>(R.id.ok_bt) as ImageView
                    val ivCancel = v.findViewById<View>(R.id.finish_bt) as ImageView
                    tvSubmit.setOnClickListener {
                        pvCustomTime!!.returnData()
                        pvCustomTime!!.dismiss()
                    }
                    ivCancel.setOnClickListener {
                        content = ""
                        pvCustomTime!!.dismiss()
                    }
                }
                .setContentSize(18)
                .setType(booleanArrayOf(true, true, false, false, false, false))
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(-0xdb5263)
                .build()
    }

    private fun getTime(date: Date): String? { //可根据需要自行截取数据显示
        //val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val format = SimpleDateFormat("yyyy-MM")
        return format.format(date)
    }

    private fun load_more_future(isRefresh: Boolean, doit: String) {
        if (future != null && !future!!.isDone()) {
            Log.e("robin debug", "onClick: " + "future is not over")
            return
        }
        dialogUtil!!.loadDialog()
        future = es.submit {
            try {
//                getSearch()
//                Thread.sleep(5000)
                get_message(isRefresh, doit)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e("TAG", "run: " + e.message)
            }

            Log.e("load", "handleMessage: " + "onLoad:" + doit)
            when (doit) {
                "refresh" -> handler.sendEmptyMessage(0)
                "load" -> handler.sendEmptyMessage(1)
            }

            //mHandler.sendEmptyMessage(1);
//            stop_scan()
            Log.e("robin debug", "onClick: " + "future is  over")
        } as Future<String>?


    }

    companion object {
        private const val RECORD_MESSAGE_BACK = 100
        private var onDeviceMessageFragListener1: OnDeviceMessageFragListener? = null
        fun newInstance(onDeviceMessageFragListener: OnDeviceMessageFragListener?): OperationRecordsFragment {
            val newFragment = OperationRecordsFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            onDeviceMessageFragListener1 = onDeviceMessageFragListener
            return newFragment
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("operat", "onPause: ")
    }
}