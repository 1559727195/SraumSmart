package com.massky.sraum.activity

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.SystemMessageActivity
import com.massky.sraum.adapter.SelectDeviceMessageAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.SystemMessageFragment
import com.massky.sraum.view.XListView_ForMessage
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/2/12.
 */
class SystemMessageActivity : BaseActivity(), AdapterView.OnItemClickListener, XListView_ForMessage.IXListViewListener, AdapterView.OnItemLongClickListener {
    private var selectdevicemessageAdapter: SelectDeviceMessageAdapter? = null
    private var content = ""
    private val account_view: View? = null
    private var dialogUtil: DialogUtil? = null
    private val messageLists: MutableList<MutableMap<*, *>> = ArrayList()

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
    private val mHandler = Handler()
    private var messageLists_local: List<Map<*, *>> = ArrayList()
    private var str = "全选"

    @JvmField
    @BindView(R.id.all_select_txt)
    var all_select_txt: TextView? = null
    private var backfrom_activity = false
    private var messageId: String? = ""

    @JvmField
    @BindView(R.id.addtxt_text_id)
    var addtxt_text_id: TextView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    //time_select_lienar
    @JvmField
    @BindView(R.id.view_middle)
    var view_middle: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pick_time)
    var pick_time: TextView? = null


    override fun viewId(): Int {
        return R.layout.system_message_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        addtxt_text_id!!.visibility = View.VISIBLE
        addtxt_text_id!!.text = "编辑"
        content = "取消"
        common_del("")
        addtxt_text_id!!.setOnClickListener(this)
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

        },xListView_scan!!,this@SystemMessageActivity, messageLists)
        xListView_scan!!.adapter = selectdevicemessageAdapter
        xListView_scan!!.setPullLoadEnable(true)
        xListView_scan!!.setXListViewListener(this)
        //        String stringBuffer = "1,2,3,";
//        String split = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
//        addViewid();
        all_read_linear!!.setOnClickListener(this)
        all_select_linear!!.setOnClickListener(this)
        delete_linear!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this@SystemMessageActivity)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.addtxt_text_id -> {
                when (addtxt_text_id!!.text.toString()) {
                    "编辑" -> {
                        addtxt_text_id!!.text = "取消"
                        content = "编辑"
                    }
                    "取消" -> {
                        content = "取消"
                        addtxt_text_id!!.text = "编辑"
                    }
                }
                common_del("")
            }
            R.id.all_read_linear ->                 //全部已读
                all_read()
            R.id.all_select_linear -> all_select(str)
            R.id.delete_linear -> delete()
        }
    }

    public override fun onStart() { //onStart()-这个方法在屏幕唤醒时调用。
        super.onStart()
        addtxt_text_id!!.text = "编辑"
        content = "取消"
        common_del("")
        str = "全选"
        if (backfrom_activity) {
            backfrom_activity = false
            for (i in messageLists.indices) {
                if (messageLists[i]["id"].toString() == messageId) {
                    ( messageLists[i] as HashMap<String,Any>)["readStatus"] = "1"
                }
            }
            SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
            selectdevicemessageAdapter!!.setList(messageLists)
        } else {
            get_message(true, "doit")
        }
    }

    /**
     * 退出动画
     */
    private fun exitanimation() {
        val animation = AnimationUtils.loadAnimation(
                this@SystemMessageActivity, R.anim.dialog_exit)
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
                this@SystemMessageActivity, R.anim.dialog_enter)
        linear_popcamera!!.clearAnimation()
        linear_popcamera!!.animation = animation
        linear_popcamera!!.visibility = View.VISIBLE
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun onLoad() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        onLoad()
        get_message(true, "doit")
    }

    override fun onLoadMore() {
        onLoad()
        Thread(Runnable { get_message(false, "doit") }).start()
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        return true
    }

    private var page = 1
    private fun get_message(isRefresh: Boolean, doit: String) {
        if (isRefresh) {
            page = 1
        }
        get_message_by_page(isRefresh, doit)
    }

    private fun get_message_by_page(isRefresh: Boolean, doit: String) {
        runOnUiThread { dialogUtil!!.loadDialog() }
        val map= HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        map["token"] = TokenUtil.getToken(this@SystemMessageActivity)
        //        map.put("projectCode",projectCode);
        map["page"] = page
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getMessage, map as HashMap<String,Any>, object : Mycallback(AddTogglenInterfacer { get_message_by_page(isRefresh, doit) }, this@SystemMessageActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@SystemMessageActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                if (isRefresh) {
                    messageLists.clear()
                    SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
                } else {
                    str = "全选"
                }
                //                messageList.addAll(user.messageList);
                for (i in user.messageList.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["id"] = user.messageList[i].id
                    map["messageType"] = user.messageList[i].messageType
                    map["messageTitle"] = user.messageList[i].messageTitle
                    if (user.messageList[i].deviceName != null) {
                        map["deviceName"] = user.messageList[i].deviceName
                    } else {
                        map["deviceName"] = ""
                    }
                    map["readStatus"] = user.messageList[i].readStatus
                    map["eventTime"] = user.messageList[i].eventTime
                    map["ischecked"] = false
                    when (user.messageList[i].messageType) {
                        "100" -> messageLists.add(map)
                        else -> {
                        }
                    }
                }
                common_del(doit)
                page++
                //                if( dialogUtil != null){
//                    dialogUtil.removeviewBottomDialog();
//                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * 公共处理方法
     */
    private fun common_del(doit: String) {
        when (content) {
            "编辑" -> {
                if (doit == "") {
                    runOnUiThread { //                            dialogUtil.loadViewBottomdialog();
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
                runOnUiThread { //                            dialogUtil.loadViewBottomdialog();
                    str = "全选"
                    all_select_txt!!.text = str
                    exitanimation()
                }
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    (messageLists[i] as HashMap<String,Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    messageLists_local = SharedPreferencesUtil.getInfo_List(this@SystemMessageActivity, "messageLists")
                    if (messageLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
                        var j = 0
                        while (j < messageLists_local.size) {
                            if (messageLists_local[j]["id"] ==
                                    messageLists[i]["id"]) {
                                (messageLists[i] as HashMap<String,Any>)["ischecked"] = messageLists_local[j]["ischecked"] as Boolean
                            }
                            j++
                        }
                    }
                    i++
                }
            }
            else -> if (doit == "") {
                runOnUiThread {
                    str = "全选"
                    all_select_txt!!.text = str
                    exitanimation()
                }
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    (messageLists[i] as HashMap<String,Any>)["ischecked"] = false
                    i++
                }
            } else {
                var i = 0
                while (i < messageLists.size) {
                    selectdevicemessageAdapter!!.isCheckBoxVisiable[i] = false
                    messageLists_local = SharedPreferencesUtil.getInfo_List(this@SystemMessageActivity, "messageLists")
                    if (messageLists_local.size != 0) {
                        SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
                        var j = 0
                        while (j < messageLists_local.size) {
                            if (messageLists_local[j]["id"] ==
                                    messageLists[i]["id"]) {
                                (messageLists[i] as HashMap<String,Any>)["ischecked"] = messageLists_local[j]["ischecked"] as Boolean
                            }
                            j++
                        }
                    }
                    i++
                }
            }
        }
        SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
        if (selectdevicemessageAdapter != null) selectdevicemessageAdapter!!.setList(messageLists)
    }

    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.attributes = lp
    }

    /**
     * 删除
     */
    private fun delete() {
        val temp = ","
        val stringBuffer = StringBuffer()
        for (i in messageLists.indices) {
            if (messageLists[i]["ischecked"] as Boolean) {
                stringBuffer.append(messageLists[i]["id"].toString() + temp)
            }
        }
        if (stringBuffer.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@SystemMessageActivity, "请先进行勾选")
            return
        }
        val split = stringBuffer.toString().substring(0, stringBuffer.length - 1)
        when (str) {
            "全不选" -> delete_select(split, "all")
            else -> delete_select(split, "")
        }
    }

    /**
     * 删除选中的
     *
     * @param split
     */
    private fun delete_select(split: String, content: String) {
        val map = HashMap<Any?, Any?>()
        //        String roomNo = roomNums.get(roomIndex);
        map["token"] = TokenUtil.getToken(this@SystemMessageActivity)
        when (content) {
            "all" -> map["messageIds"] = content
            else -> map["messageIds"] = split
        }
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteMessage, map as HashMap<String,Any>, object : Mycallback(AddTogglenInterfacer { delete_select(split, content) }, this@SystemMessageActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@SystemMessageActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                val indexs = split.split(",".toRegex()).toTypedArray()
                for (j in indexs.indices) {
                    for (i in messageLists.indices) {
                        if (messageLists[i]["id"] == indexs[j]) {
                            messageLists.removeAt(i)
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
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
                    (messageLists[i] as HashMap<String,Any>)["ischecked"] = true
                    i++
                }
            }
            "全不选" -> {
                this.str = "全选"
                var i = 0
                while (i < messageLists.size) {
                    (messageLists[i] as HashMap<String,Any>)["ischecked"] = false
                    i++
                }
            }
        }
        all_select_txt!!.text = this.str
        selectdevicemessageAdapter!!.setList(messageLists)
        SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
    }

    /**
     * 全部标记已读
     */
    private fun all_read() {
        val temp = ","
        val stringBuffer = StringBuffer()
        for (i in messageLists.indices) {
            if (messageLists[i]["ischecked"] as Boolean) {
                stringBuffer.append(messageLists[i]["id"].toString() + temp)
            }
        }
        if (stringBuffer.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@SystemMessageActivity, "请先进行勾选")
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
        map["token"] = TokenUtil.getToken(this@SystemMessageActivity)
        when (content) {
            "all" -> map["messageIds"] = content
            else -> map["messageIds"] = messageIds
        }
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_setReadStatus, map as HashMap<String,Any>, object : Mycallback(AddTogglenInterfacer { mark_all_read(messageIds, content) }, this@SystemMessageActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@SystemMessageActivity, "网络连接超时")
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
                            (messageLists[i] as HashMap<String,Any>)["readStatus"] = "1"
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
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
                (messageLists[position - 1] as HashMap<String,Any>)["ischecked"] = cb.isChecked as Boolean
                SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", messageLists)
            }
            "取消" -> {
                //
                val intent = Intent(this@SystemMessageActivity, MessageDetailActivity::class.java)
                intent.putExtra("Message", messageLists[position - 1] as Serializable)
                startActivityForResult(intent, DEVICE_MESSAGE_BACK)
            }
            else -> {
                val intent = Intent(this@SystemMessageActivity, MessageDetailActivity::class.java)
                intent.putExtra("Message", messageLists[position - 1] as Serializable)
                startActivityForResult(intent, DEVICE_MESSAGE_BACK)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.e("peng", "MacFragment->onResume:name:")
    }

    public override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesUtil.saveInfo_List(this@SystemMessageActivity, "messageLists", ArrayList())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //得到新Activity 关闭后返回的数据
        messageId = data!!.extras!!.getString("result")
        if (messageId != null) {
            Log.e("zhu", "result:$messageId")
            backfrom_activity = true
        }
    }

    companion object {
        private const val DEVICE_MESSAGE_BACK = 100
        private val onDeviceMessageFragListener1: SystemMessageFragment.OnDeviceMessageFragListener? = null
    }
}