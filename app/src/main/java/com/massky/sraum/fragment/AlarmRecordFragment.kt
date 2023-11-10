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
import com.massky.sraum.activity.MessageDetailActivity
import com.massky.sraum.adapter.SelectDeviceMessageAdapter
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
class AlarmRecordFragment : Basecfragment(), AdapterView.OnItemClickListener, XListView_ForMessage.IXListViewListener, AdapterView.OnItemLongClickListener {
    private var selectdevicemessageAdapter: SelectDeviceMessageAdapter? = null
    private var mMessageReceiver: MessageReceiver? = null
    private var content = ""
    private val account_view: View? = null
    private var dialogUtil: DialogUtil? = null
    private val messageLists: MutableList<MutableMap<*, *>> = ArrayList()
    private var local_time: String? = null

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

    private var messageLists_local: List<Map<*, *>> = ArrayList()
    private var str = "全选"

    @JvmField
    @BindView(R.id.all_select_txt)
    var all_select_txt: TextView? = null


    //pick_data_txt
    @JvmField
    @BindView(R.id.pick_data_txt)
    var pick_data_txt: TextView? = null

    //time_select_lienar
    @JvmField
    @BindView(R.id.time_select_lienar)
    var time_select_lienar: RelativeLayout? = null

    //pvCustomTime

    private var pvCustomTime: TimePickerView? = null


    //time_select_lienar
    @JvmField
    @BindView(R.id.view_middle)
    var view_middle: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pick_time)
    var pick_time: TextView? = null


    private var backfrom_activity = false
    private var messageId: String? = ""

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

    override fun viewId(): Int {
        return R.layout.system_message_fragment
    }

    override fun onStart() { //onStart()-这个方法在屏幕唤醒时调用。
        super.onStart()
        str = "全选"
        if (backfrom_activity) {
            backfrom_activity = false
            for (i in messageLists.indices) {
                if (messageLists[i]["id"].toString() == messageId) {
                    (messageLists[i] as HashMap<String, Any>)["readStatus"] = "1"
                }
            }
            SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
            selectdevicemessageAdapter!!.setList(messageLists)
        } else {
            //get_message(true, "refresh")
            load_more_future(true, "doit")
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

    private fun onLoad_More() {
        // xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        // xListView_scan!!.setRefreshTime("刚刚")
    }

    private fun on_Refresh() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        // on_Refresh()
        pick_data_txt!!.setText(local_time)

        // get_message(true, "refresh")
        load_more_future(true, "refresh")
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

    private fun load_more_future(isRefresh: Boolean, doit: String) {
        if (future != null && !future!!.isDone()) {
            Log.e("robin debug", "onClick: " + "future is not over")
            return
        }
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
////                        messageLists.clear();
////                        SharedPreferencesUtil.saveInfo_List(getActivity(), "messageLists", messageLists);
////                        get_message(false, "");
//                        break;
//                }
                common_del("")
            }
        }
    }

    private var endId = "0"
    private var searchValue: String? = ""
    private var year_month = ""
    private fun get_message(isRefresh: Boolean, doit: String) {
        if (isRefresh) {
            endId = "0"
            year_month = ""
        }
        when (doit) {
            "refresh" -> {
                searchValue = ""
            }
        }
        get_message_by_page(isRefresh, doit)
    }

    private fun get_message_by_page(isRefresh: Boolean, doit: String) {
//        dialogUtil.loadDialog();
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);


        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(context)
        map["areaNumber"] = areaNumber
        map["endId"] = endId
        map["searchValue"] = searchValue

        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getMessageNew, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { get_message_by_page(isRefresh, doit) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                if (isRefresh) {
                    if (messageLists != null)
                        messageLists.clear()
                    SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
                } else {
                    str = "全选"
                }
                //                messageList.addAll(user.messageList);
                for (i in user.messageList.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["id"] = if (user.messageList[i].id == null) "" else user.messageList[i].id
                    map["messageType"] = if (user.messageList[i].messageType == null) "" else user.messageList[i].messageType
                    map["messageTitle"] = if (user.messageList[i].messageTitle == null) "" else user.messageList[i].messageTitle
                    if (user.messageList[i].deviceName != null) {
                        map["deviceName"] = if (user.messageList[i].deviceName == null) "" else user.messageList[i].deviceName
                    } else {
                        map["deviceName"] = ""
                    }
                    map["readStatus"] = if (user.messageList[i].readStatus == null) "" else user.messageList[i].readStatus

                    map["eventTime"] = if (user.messageList[i].eventTime == null) "" else user.messageList[i].eventTime
                    get_date_time(map)

                    map["ischecked"] = false
                    when (if (user.messageList[i].messageType == null) "" else user.messageList[i].messageType) {
                        "100" -> {
                        }
                        else -> messageLists.add(map)
                    }

                    for (i in messageLists.indices) {
                        var map = messageLists[i]
                        common_select_dates(i, map as HashMap<Any?, Any?>)
                    }
                }
                common_del(doit)
                endId = user.endId
                //                if( dialogUtil != null){
//                    dialogUtil.removeviewBottomDialog();
//                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }


    private fun get_date_time(map: HashMap<Any?, Any?>) {
        var eventTime = if (map["eventTime"] == null) "" else map["eventTime"] as String

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date?
        date = format.parse(eventTime)
        val format1 = SimpleDateFormat("MM-dd HH:mm:ss")
        var normal_date = format1.format(date)
        map["event_Time"] = normal_date
    }

    private fun common_select_dates(i: Int?, map: HashMap<Any?, Any?>) {
        var eventTime = if (map["eventTime"] == null) "" else map["eventTime"] as String


        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date?
        date = format.parse(eventTime)
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
    private fun common_del(doit: String) {
        when (content) {
            "编辑" -> {
                if (doit == "") {
                    activity!!.runOnUiThread { //                            dialogUtil.loadViewBottomdialog();
                        enteranimation()
                    }
                }
                var i = 0
                while (i < messageLists.size) {
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
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    //messageLists[i]
                    (messageLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    messageLists_local = SharedPreferencesUtil.getInfo_List(activity, "messageLists")
                    if (messageLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
                        var j = 0
                        while (j < messageLists_local.size) {
                            if (messageLists_local[j]["id"] ==
                                    messageLists[i]["id"]) {
                                //messageLists[i]
                                (messageLists[i] as HashMap<String, Any>)["ischecked"] = messageLists_local[j]["ischecked"] as Boolean
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
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    //messageLists[i]
                    (messageLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    messageLists_local = SharedPreferencesUtil.getInfo_List(activity, "messageLists")
                    if (messageLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
                        var j = 0
                        while (j < messageLists_local.size) {
                            if (messageLists_local[j]["id"] ==
                                    messageLists[i]["id"]) {
                                //messageLists[i]
                                (messageLists[i] as HashMap<String, Any>)["ischecked"] = messageLists_local[j]["ischecked"] as Boolean
                            }
                            j++
                        }
                    }
                    i++
                }
            }
        }
        SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
        selectdevicemessageAdapter!!.setList(messageLists)
    }

    override fun onView() {
        registerMessageReceiver()
        xListView_scan!!.onItemClickListener = this
        xListView_scan!!.onItemLongClickListener = this
        selectdevicemessageAdapter = SelectDeviceMessageAdapter(object : SelectDeviceMessageAdapter.ScrollSelectListener {
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

        }, xListView_scan!!, activity!!, messageLists
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


        val authType = SharedPreferencesUtil.getData(context, "authType", "") as String
        when (authType) {
            "1" -> delete_linear!!.visibility = View.VISIBLE
            "2" -> delete_linear!!.visibility = View.GONE
        }
    }

    override fun initData() { //刷新数据，选择viewpager时刷新数据
//        get_message(false, "doit");
//        if(onDeviceMessageFragListener1 != null)
//        onDeviceMessageFragListener1.ondevice_message_frag();
//        ToastUtil.showToast(getActivity(),"DeviceMessageFragment");
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.all_read_linear ->                 //全部已读
                all_read()
            R.id.all_select_linear -> all_select(str)
            R.id.delete_linear -> delete()
            R.id.time_select_lienar ->          //                showPopFormBottom(null);
                pvCustomTime!!.show() //弹出自定义时间选择器
        }
    }

    /**
     * 删除
     */
    private fun delete() {
        val temp = ","
        val stringBuffer = StringBuffer()
        val string_names = StringBuffer()
        //messageTitle
        for (i in messageLists.indices) {
            if (messageLists[i]["ischecked"] as Boolean) {
                stringBuffer.append(messageLists[i]["id"].toString() + temp)
                string_names.append(messageLists[i]["messageTitle"].toString() + temp)
            }
        }
        if (stringBuffer.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(activity, "请先进行勾选")
            return
        }
        val split = stringBuffer.toString().substring(0, stringBuffer.length - 1)
        val split_names = string_names.toString().substring(0, string_names.length - 1)
        when (str) {
            "全不选" ->
                showCenterDeleteDialog(split_names, split, "all")
            else ->
                showCenterDeleteDialog(split_names, split, "")
        }
    }


    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(split_names: String, split: String?,
                               type: String) {
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
        tv_title.text = split_names
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
            delete_select(split!!, type)
            dialog.dismiss()
        }
        confirm.setOnClickListener { dialog.dismiss() }
    }


    /**
     * 删除选中的
     *
     * @param split
     */
    private fun delete_select(split: String, content: String) {
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(context)
        map["areaNumber"] = areaNumber
        when (content) {
            "all" -> map["messageIds"] = content
            else -> map["messageIds"] = split
        }
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteMessage, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { delete_select(split, content) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                val indexs = split.split(",".toRegex()).toTypedArray()
                for (j in indexs.indices) {
                    for (i in messageLists.indices) {
                        if (messageLists[i]["id"] == indexs[j]) {
                            messageLists.removeAt(i)
                            break
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
                selectdevicemessageAdapter!!.setList(messageLists)
                //                get_message(true, "doit");
                onDeviceMessageFragListener1!!.ondevice_message_frag()
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
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
                while (i < messageLists.size) {
                    //messageLists[i]
                    (messageLists[i] as HashMap<String, Any>)["ischecked"] = true
                    i++
                }
            }
            "全不选" -> {
                this.str = "全选"
                var i = 0
                while (i < messageLists.size) {
                    //messageLists[i]
                    (messageLists[i] as HashMap<String, Any>)["ischecked"] = false
                    i++
                }
            }
        }
        all_select_txt!!.text = this.str
        selectdevicemessageAdapter!!.setList(messageLists)
        SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
    }

    /**
     * 全部标记已读
     */
    private fun all_read() {
//        String temp = ",";
//        StringBuffer stringBuffer = new StringBuffer();
//        for (int i = 0; i < messageLists.size(); i++) {
//            if (i == messageLists.size() - 1) {
//                stringBuffer.append(messageLists.get(i).get("id"));
//            } else {
//                stringBuffer.append(messageLists.get(i).get("id") + temp);
//            }
//        }
        val temp = ","
        val stringBuffer = StringBuffer()
        for (i in messageLists.indices) {
            if (messageLists[i]["ischecked"] as Boolean) {
                stringBuffer.append(messageLists[i]["id"].toString() + temp)
            }
        }
        if (stringBuffer.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(activity, "请先进行勾选")



            return
        }
        val split = stringBuffer.toString().substring(0, stringBuffer.length - 1)
        when (str) {
            "全不选" -> mark_all_read(split, "all")
            else -> mark_all_read(split, "")
        }
    }

    /**
     * 标记为全部已读
     *
     * @param
     */
    private fun mark_all_read(messageIds: String, content: String) {
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(context)
        map["areaNumber"] = areaNumber
        when (content) {
            "all" -> map["messageIds"] = content
            else -> map["messageIds"] = messageIds
        }
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_setReadStatus, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { mark_all_read(messageIds, content) }, activity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(activity, "网络连接超时")

            }

            override fun onSuccess(user: User) {
//                for (int i = 0; i < messageList.size(); i++) {
////                    SelectDeviceMessageAdapter.getIsItemRead().put(i, true);
//                    selectdevicemessageAdapter.setList(messageList);
//                }
//                get_message(true, "doit");
                //标记成功后，局部刷新标记后的数据
                val indexs = messageIds.split(",".toRegex()).toTypedArray()
                for (j in indexs.indices) {
                    for (i in messageLists.indices) {
                        if (messageLists[i]["id"] == indexs[j]) {
                            //messageLists[i]
                            (messageLists[i] as HashMap<String, Any>)["readStatus"] = "1"
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
                selectdevicemessageAdapter!!.setList(messageLists)
                onDeviceMessageFragListener1!!.ondevice_message_frag()
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        when (content) {
            "编辑" -> {
                //                View v = parent.getChildAt(position - xListView_scan.getFirstVisiblePosition());
                val cb = view.findViewById<View>(R.id.checkbox) as CheckBox
                cb.toggle()
                //messageLists[position - 1]
                (messageLists[position - 1] as HashMap<String, Any>)["ischecked"] = cb.isChecked
                SharedPreferencesUtil.saveInfo_List(activity, "messageLists", messageLists)
            }
            "取消" -> {
                //
                val intent = Intent(activity, MessageDetailActivity::class.java)
                intent.putExtra("Message", messageLists[position - 1] as Serializable)
                startActivityForResult(intent, DEVICE_MESSAGE_BACK)
            }
            else -> {
                val intent = Intent(activity, MessageDetailActivity::class.java)
                intent.putExtra("Message", messageLists[position - 1] as Serializable)
                startActivityForResult(intent, DEVICE_MESSAGE_BACK)
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
        SharedPreferencesUtil.saveInfo_List(activity, "messageLists", ArrayList())
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
            pick_data_txt!!.setText(getTime(date))
            searchValue = getTime(date)
            //Thread(Runnable { get_message(true, "doit") }).start()
            load_more_future(true, "doit")
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
                        pvCustomTime!!.dismiss()
                    }
                }
                .setContentSize(18)
                .setType(booleanArrayOf(true, true, false, false, false, false))//
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

    companion object {
        private const val DEVICE_MESSAGE_BACK = 100
        private var onDeviceMessageFragListener1: OnDeviceMessageFragListener? = null
        fun newInstance(onDeviceMessageFragListener: OnDeviceMessageFragListener?): AlarmRecordFragment {
            val newFragment = AlarmRecordFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            onDeviceMessageFragListener1 = onDeviceMessageFragListener
            return newFragment
        }
    }
}