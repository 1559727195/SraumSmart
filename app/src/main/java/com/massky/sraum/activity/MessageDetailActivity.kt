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

/**
 * Created by zhu on 2018/7/6.
 */
class MessageDetailActivity : BaseActivity() {
    var messageList: Map<*, *>? = HashMap<Any?, Any?>()

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

    @JvmField
    @BindView(R.id.first_linear)
    var first_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.second_linear)
    var second_linear: LinearLayout? = null
    override fun viewId(): Int {
        return R.layout.message_detail_act
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        messageList = intent.getSerializableExtra("Message") as Map<*, *>
        if (messageList != null) {
            sraum_getMessageById(messageList!!["id"] as String?)
        }
    }

    override fun onEvent() {}
    override fun onData() {}

    /**
     * 根据编号获取详细详情
     *
     * @param id
     */
    private fun sraum_getMessageById(id: String?) {
        val map= HashMap<String?, Any?>()
        var areaNumber = SharedPreferencesUtil.getData(this@MessageDetailActivity, "areaNumber", "") as String
        map["areaNumber"]= areaNumber
        //        String roomNo = roomNums.get(roomIndex);
        map["token"] = TokenUtil.getToken(this@MessageDetailActivity)
        //        map.put("projectCode",projectCode);
        map["messageId"] = id

//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getMessageById, map, object : Mycallback(AddTogglenInterfacer { sraum_getMessageById(id) }, this@MessageDetailActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@MessageDetailActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                deviceName = user.deviceName
                messageTitle = user.messageTitle
                eventTime = user.eventTime
                if (deviceName != null) device_name!!.text = deviceName
                if (messageTitle != null) action_text!!.text = messageTitle
                if (eventTime != null) time_txt!!.text = eventTime
                action_text!!.visibility = View.VISIBLE
                first_linear!!.visibility = View.VISIBLE
                second_linear!!.visibility = View.VISIBLE
                //设置消息已读
                all_read(id)
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * 全部标记已读
     */
    private fun all_read(id: String?) {
        mark_all_read(id)
    }

    /**
     * 标记为全部已读
     *
     * @param
     */
    private fun mark_all_read(messageIds: String?) {
        val map= HashMap<String?, Any?>()
        //        dialogUtil.loadDialog();
//        String roomNo = roomNums.get(roomIndex);
        map["token"] = TokenUtil.getToken(this@MessageDetailActivity)
        //        map.put("projectCode",projectCode);
        map["messageIds"] = messageIds
        //        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_setReadStatus, map, object : Mycallback(AddTogglenInterfacer { mark_all_read(messageIds) }, this@MessageDetailActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@MessageDetailActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
//                for (int i = 0; i < messageList.size(); i++) {
////                    SelectDeviceMessageAdapter.getIsItemRead().put(i, true);
//                    selectdevicemessageAdapter.setList(messageList);
//                }
//                get_message(false);
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                //数据是使用Intent返回
                val intent = Intent()
                //把返回数据存入Intent
                intent.putExtra("result", messageList!!["id"].toString())
                //设置返回数据
                this@MessageDetailActivity.setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        //把返回数据存入Intent
        intent.putExtra("result", messageList!!["id"].toString())
        //设置返回数据
        this@MessageDetailActivity.setResult(Activity.RESULT_OK, intent)
        finish()
    }
}