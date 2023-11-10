package com.example.jpushdemo

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.*
import cn.jpush.android.service.JPushMessageReceiver
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.iflytek.speech.util.SpeechUtil
import com.massky.sraum.User
import com.massky.sraum.Util.LogUtil
import com.massky.sraum.Util.MusicUtil
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.activity.*
import com.massky.sraum.activity.MessageActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.base.Basecfragment
import com.massky.sraum.base.Basecfragmentactivity
import com.massky.sraum.fragment.HomeFragment
import com.massky.sraum.fragment.HomeFragmentNew
import com.massky.sraum.fragment.SceneFragment
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 */
class MyJPushMessageReceiver : JPushMessageReceiver() {
    private var action = ""
    override fun onMessage(context: Context, customMessage: CustomMessage) {
        Log.e(TAG, "[onMessage] $customMessage")
        //            ToastUtil.showToast(context,"接收到推送下来的自定义消息");
        processCustomMessage_toMainActivity(context, customMessage)
        Log.e("processCustomMessage", "processCustomMessage")
        val message = customMessage.message
        val extras = customMessage.extra
        processCustomMessage(context, message, extras)
    }

    override fun onNotifyMessageOpened(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageOpened] $message")
       // processCustomMessage(context, customMessage)
        try {
            //打开自定义的Activity
            val i = Intent(context, MessageActivity::class.java)
            val bundle = Bundle()
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle)
            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent)
            i.putExtras(bundle)
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)
        } catch (throwable: Throwable) {

        }
    }

    override fun onMultiActionClicked(context: Context, intent: Intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        val nActionExtra = intent.extras!!.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null")
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }
    }

    override fun onNotifyMessageArrived(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageArrived] $message")
        val message_content = message.notificationContent
        val extras = message.notificationExtras
        processCustomMessage(context, message_content, extras)
        //NotificationControlManager.getInstance().notify("文件上传完成", "文件上传完成,请点击查看详情",MessageActivity.class);
    }

    private fun processCustomMessage_toMainActivity(context: Context, customMessage: CustomMessage) {
        val message = customMessage.message
        val extras = customMessage.extra
//        val message = bundle!!.getString(JPushInterface.EXTRA_MESSAGE) //账号已在别处登录！
//        val extras = bundle.getString(JPushInterface.EXTRA_EXTRA) ////{"type":"2"}
        if (BaseActivity.isForegrounds || Basecfragment.isForegrounds || Basecfragmentactivity.isForegrounds) { //app可见时，才发送消息
            val msgIntent = Intent(MainGateWayActivity.MESSAGE_RECEIVED_ACTION)
            msgIntent.putExtra(MainGateWayActivity.KEY_MESSAGE, message)
            var extraJson: JSONObject? = null
            var type = ""
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    extraJson = JSONObject(extras)
                    type = extraJson.getString("type")
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainGateWayActivity.KEY_EXTRAS, extras)
                    }
                } catch (e: JSONException) {
                }
            }
            if (type == "7") {
                context!!.sendBroadcast(msgIntent)
            }
        } else { //app不可见,保存本地
//			SharedPreferencesUtil.saveData(context,"extras_login",extras);
        }
    }

    override fun onNotifyMessageDismiss(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageDismiss] $message")
    }

    override fun onRegister(context: Context, registrationId: String) {
        Log.e(TAG, "[onRegister] $registrationId")
        SharedPreferencesUtil.saveData(context, "regId", registrationId)
    }

    override fun onConnected(context: Context, isConnected: Boolean) {
        Log.e(TAG, "[onConnected] $isConnected")
    }

    override fun onCommandResult(context: Context, cmdMessage: CmdMessage) {
        Log.e(TAG, "[onCommandResult] $cmdMessage")
    }

    override fun onTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
        //   TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(context: Context, jPushMessage: JPushMessage) {
        // TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(context: Context, jPushMessage: JPushMessage) {
        //TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage)
    }

    override fun onMobileNumberOperatorResult(context: Context, jPushMessage: JPushMessage) {
        //TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }

    //send msg to MainActivity
    private fun processCustomMessage(context: Context, message: String, extras: String) {
//        if (MainActivity.isForeground) {
        //接收下来的json数据
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
        val user = JSON.parseObject(extras, User::class.java)
        if(user == null || user.type == null) return
        LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知")
        Log.e(TAG, "onReceive: " + Gson().toJson(user))
        //进行广播通知是否刷新设备和场景

        if (!ExampleUtil.isEmpty(extras)) { //设备状态改变
            var extraJson: JSONObject? = null
            extraJson = JSONObject(extras)
            val panelid = extraJson.optString("panelid")
            val gatewayid = extraJson.optString("gatewayid")
            Log.e(TAG, "processCustomMessage: " + gatewayid)

            when (user.type) {
                "2" -> {
                    if (TokenUtil.getPagetag(context) == "3") {
                        action = SceneFragment.ACTION_INTENT_RECEIVER
                    } else if (TokenUtil.getPagetag(context) == "6") {
//                action = MysceneFragment.ACTION_INTENT_RECEIVER;
                    }
                    sendBroad(user.type, "", null, context)
                }

                "1", "101" -> {
                    // 在extras中增加了字段panelid
                    // receiver_1_101(extras, notifactionId)
                    action = HomeFragmentNew.ACTION_INTENT_RECEIVER
                    sendBroad(user.type, panelid, gatewayid, context)
                    Log.e(TAG, "processCustomMessage: panelid:$panelid")
                    action = AirControlActivity.ACTION_AIRCONTROL_RECEIVER
                    sendBroad(user.type, panelid, gatewayid, context)
                    action = MusicPanelActivity.ACTION_MUSICCONTROL_RECEIVER
                    sendBroad(user.type, panelid, gatewayid, context)

                    //                        action = ACTION_SRAUM_FAST_EDIT;
                    //                        sendBroad(notifactionId, panelid, gatewayid);
                    val mIntent = Intent(FastEditPanelActivity.ACTION_SRAUM_FAST_EDIT)
                    mIntent.putExtra("extras", extras)
                    context!!.sendBroadcast(mIntent)
                }
                "3", "4", "5" -> {
                    val mapbox: MutableMap<String, Any> = HashMap()
                    mapbox["token"] = TokenUtil.getToken(context)
                }
                "8" -> {
                    Log.e("notifactionId", user.type.toString() + "")
                    action = MainGateWayActivity.ACTION_SRAUM_SETBOX
                    sendBroad(user.type, panelid, gatewayid, context)
                }
                "50", "102" -> {
                    action = GuJianWangGuanNewActivity.UPDATE_GRADE_BOX
                    sendBroad(user.type, panelid, gatewayid, context)
                }
                "52" -> {
                    //构建对象
                    init_soundPool(context)
                }
                "51" -> {
                    val extraJson: JSONObject
                    var typeno: String? = null
                    //val alert = bundle.getString(JPushInterface.EXTRA_ALERT) ////{"type":"2"}
                    if (message == null) return
                    if (!ExampleUtil.isEmpty(extras)) {
                        try {
                            extraJson = JSONObject(extras)
                            typeno = extraJson.getString("typeno")
                            if (typeno != null) {
                                if (typeno == "B001") {
                                    var firstRegister: Boolean = SharedPreferencesUtil.getData(context,
                                            "firstRegister", false) as Boolean
                                    if (firstRegister) {
                                        SpeechUtil.startSpeech(context, 1, message, "time_push")
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
                "54" -> {
                    var extraJson: JSONObject
                    if (!ExampleUtil.isEmpty(extras)) { //设备状态改变
                        // 在extras中增加了字段panelid
//                        extraJson = new JSONObject(extras);
                        val mIntent = Intent(HomeFragment.ACTION_INTENT_RECEIVER_TABLE_PM)
                        mIntent.putExtra("extras", extras)
                        context!!.sendBroadcast(mIntent)
                    }
                }
                "103" -> {
                    var extraJson: JSONObject
                    if (!ExampleUtil.isEmpty(extras)) { //设备状态改变
                        // 在extras中增加了字段panelid
//                        extraJson = new JSONObject(extras);
                        val mIntent = Intent(ConnApModeWifiActivity.MESSAGE_RECEIVED_ACTION_APMODE_ConnWifi)
                        mIntent.putExtra("extras", extras)
                        context!!.sendBroadcast(mIntent)
                    }
                }

            }
        }


//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
    }

    /**
     * 门铃报警
     */
    private fun init_soundPool(context: Context) {
        MusicUtil.startMusic(context, 1, "doorbell")
    }



    private fun sendBroad(notifactionId: String, second: String, gatewayid: String?, context: Context) {
        val mIntent = Intent(action)
        mIntent.putExtra("notifactionId", notifactionId)
        mIntent.putExtra("panelid", second)
        mIntent.putExtra("gatewayid", gatewayid)
        context!!.sendBroadcast(mIntent)
    }
    override fun onNotificationSettingsCheck(context: Context, isOn: Boolean, source: Int) {
        super.onNotificationSettingsCheck(context, isOn, source)
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:$isOn,source:$source")
    }

    //send msg to MainActivity
   // private fun processCustomMessage(context: Context, message: String, extras: String) {
//        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//        if (MainActivity.isForeground) {
//        Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//        msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//        if (!TextUtils.isEmpty(extras)) {
//            try {
//                JSONObject extraJson = new JSONObject(extras);
//                Log.e("TAG","extraJson="+extraJson);
//                if (extraJson.length() > 0) {
//                    msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                }
//            } catch (JSONException e) {
//
//            }
//
//        }
//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        // }
  //  }

    companion object {
        private const val TAG = "PushMessageReceiver"
    }
}