package com.example.jpushdemo

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import cn.jpush.android.api.JPushInterface
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.iflytek.speech.util.SpeechUtil
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.SystemUtils
import com.massky.sraum.activity.*
import com.massky.sraum.activity.AirControlActivity.Companion.ACTION_AIRCONTROL_RECEIVER
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
 * 自定义接收器
 *
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
class MyReceiver : BroadcastReceiver() {
    private var context: Context? = null
    private var action = ""
    private val player: MediaPlayer? = null
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val bundle = intent.extras
        LogUtil.i(TAG, "[MyReceiver] onReceive - " + intent.action + ", extras: " + printBundle(bundle))
        //        ToastUtil.showToast(context,"接收到推送下来的自定义消息");
        if (JPushInterface.ACTION_REGISTRATION_ID == intent.action) {
            val regId = bundle!!.getString(JPushInterface.EXTRA_REGISTRATION_ID)
            LogUtil.i(TAG, "[MyReceiver] 接收Registration Id : $regId")
            SharedPreferencesUtil.saveData(context, "regId", regId)
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED == intent.action) {
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle!!.getString(JPushInterface.EXTRA_MESSAGE))
            //            ToastUtil.showToast(context,"接收到推送下来的自定义消息");
            processCustomMessage_toMainActivity(context, bundle)
            Log.e("processCustomMessage", "processCustomMessage")
            //接收下来的json数据
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            val user = JSON.parseObject(bundle.getString(JPushInterface.EXTRA_EXTRA), User::class.java)
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知")
            Log.e(TAG, "onReceive: " + Gson().toJson(user))
            val notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
            LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: $notifactionId")
            //进行广播通知是否刷新设备和场景

            processCustomMessage(user.type, bundle)
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED == intent.action) {
            //接收下来的json数据
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            val user = JSON.parseObject(bundle!!.getString(JPushInterface.EXTRA_EXTRA), User::class.java)
            LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知")
            val notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
            LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: $notifactionId")
            //进行广播通知是否刷新设备和场景
            processCustomMessage(user.type, bundle)
            Log.e("processCustomMessage", "processCustomMessage")
            LogUtil.i(TAG, user.type + "")
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED == intent.action) {
//            User user = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).
//                    create().fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA), User.class);
            val user = JSON.parseObject(bundle!!.getString(JPushInterface.EXTRA_EXTRA), User::class.java)
            SharedPreferencesUtil.saveData(context, "usertype", user.type)
            //2代表场景信息1代表设备刷新信息
            //Intent i = null;
            val extras = bundle.getString(JPushInterface.EXTRA_EXTRA) ////{"type":"2"}
            var json: JSONObject? = null
            try {
                json = JSONObject(extras)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var type: String? = null
            var typeno: String? = null
            var alert: String? = null
            try {
                type = json!!.getString("type")
                typeno = json.getString("typeno")
                alert = json.getString("alert")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            //判断app进程是否存活
            if (SystemUtils.isAppAlive(context, "com.massky.sraum")) {
                when (type) {
                    "1", "2", "53" -> SharedPreferencesUtil.saveData(context, "tongzhi_time", 1) //门铃报警通知次数
                    "51" -> {
                        if (typeno != null) {
                            if (typeno == "B001") {
                                var firstRegister: Boolean = SharedPreferencesUtil.getData(context,
                                        "firstRegister", false) as Boolean

                                if (firstRegister) {
                                    SpeechUtil.startSpeech(context, 1, alert, "time_push")
                                }

                            }
                        }
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1) //门铃报警通知次数
                    }
                    "52" -> {
                        //processCustomMessage_charge(context, bundle);
                        //打开自定义的Activity
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1)
                        //                        if (soundPool != null) soundPool.pause(sampleId1);
                        MusicUtil.stopMusic(context, "doorbell")
                    }
                }
                //
                Log.e("robin debug", "MyReceive->context:$context")
                if (BaseActivity.isForegrounds || Basecfragment.isForegrounds || Basecfragmentactivity.isForegrounds) {
                    common_main_tongzhi(context, bundle) //发送广播,可视
                } else if (BaseActivity.isDestroy || Basecfragment.isDestroy || Basecfragmentactivity.isDestroy) {
                    common_tongzhi(context, bundle, "create") //app退出去了，但进程没有被杀死
                    // 说明系统中不存在这个activity，或者说在后台
                } else { //在后台,去切换到前台，
                    common_tongzhi(context, bundle, "resume")
                }
            } else {
                Log.i("NotificationReceiver", "the app process is dead")
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.massky.sraum")
                when (type) {
                    "1", "2", "53" -> SharedPreferencesUtil.saveData(context, "tongzhi_time", 1) //门铃报警通知次数
                    "51" -> {
                        if (typeno != null) {
                            if (typeno == "B001") {
                                var firstRegister: Boolean = SharedPreferencesUtil.getData(context,
                                        "firstRegister", false) as Boolean
                                if (firstRegister) {
                                    SpeechUtil.startSpeech(context, 1, alert, "time_push")
                                }
                            }
                        }
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1) //门铃报警通知次数
                    }
                    "52" -> {
                        SharedPreferencesUtil.saveData(context, "tongzhi_time", 1) //门铃报警通知次数
                        //                        if (soundPool != null) soundPool.pause(sampleId1);
                        MusicUtil.stopMusic(context, "doorbell")
                    }
                }
                launchIntent!!.putExtra(Constants.EXTRA_BUNDLE, bundle)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                context.startActivity(launchIntent)
            }
            //            if (context instanceof MainfragmentActivity) {
//                MainfragmentActivity fca = (MainfragmentActivity) context;
//                if (user.type.equals("2")) {
//                    fca.setTabSelection(5);
//                } else if (user.type.equals("1")) {
//                    fca.setTabSelection(0);
//                } else if (user.type.equals("51")) {
//                    fca.setTabSelection(1);
//                }
//            } else {
//                //IntentUtil.startActivity(context, MainfragmentActivity.class, "usertype", user.type);
//                /*
//                *   i = new Intent(context, MainfragmentActivity.class);
//                if (i != null) {
//                    //打开自定义的Activity
//                    i.putExtras(bundle);
//                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    context.startActivity(i);
//                }*/
//
//
//            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK == intent.action) {
            LogUtil.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle!!.getString(JPushInterface.EXTRA_EXTRA))
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE == intent.action) {
            val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
            LogUtil.i(TAG, "[MyReceiver]" + intent.action + " connected state change to " + connected)
        } else {
            LogUtil.i(TAG, "[MyReceiver] Unhandled intent - " + intent.action)
        }
    }

    /**
     * 判断进程是否存活
     */
    fun isProcessExist(context: Context, pid: Int): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val lists: List<ActivityManager.RunningAppProcessInfo>
        if (am != null) {
            lists = am.runningAppProcesses
            for (appProcess in lists) {
                if (appProcess.pid == pid) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 跳转到主界面的通知点开事件
     *
     * @param context
     * @param bundle
     */
    private fun common_main_tongzhi(context: Context, bundle: Bundle?) {
        val mIntent = Intent(ACTION_NOTIFICATION_OPENED_MAIN)
        mIntent.putExtra(Constants.EXTRA_BUNDLE, bundle)
        context.sendBroadcast(mIntent)
    }

    private fun common_tongzhi(context: Context, bundle: Bundle?, create: String) {
        when (create) {
            "create" -> {
            }
            "resume" -> {
            }
        }
        val i_charge = Intent(context, MainGateWayActivity::class.java)
        i_charge.putExtra(Constants.EXTRA_BUNDLE, bundle) //    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i_charge.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(i_charge)
    }

    private fun processCustomMessage_toMainActivity(context1: Context, bundle: Bundle?) {
        val message = bundle!!.getString(JPushInterface.EXTRA_MESSAGE) //账号已在别处登录！
        val extras = bundle.getString(JPushInterface.EXTRA_EXTRA) ////{"type":"2"}
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

    //进行广播通知是否刷新设备和场景
    private fun processCustomMessage(notifactionId: String, bundle: Bundle?) {
        Log.e("MyReceiver", "MyReceiver.notifactionId:$notifactionId")
        action = ""
        val extras = bundle!!.getString(JPushInterface.EXTRA_EXTRA) ////{"type":"2"}

        if (!ExampleUtil.isEmpty(extras)) { //设备状态改变
            var extraJson: JSONObject? = null
            extraJson = JSONObject(extras)
            val panelid = extraJson.optString("panelid")
            val gatewayid = extraJson.optString("gatewayid")
            Log.e(TAG, "processCustomMessage: " + gatewayid)

            when (notifactionId) {
                "2" -> {
                    if (TokenUtil.getPagetag(context) == "3") {
                        action = SceneFragment.ACTION_INTENT_RECEIVER
                    } else if (TokenUtil.getPagetag(context) == "6") {
//                action = MysceneFragment.ACTION_INTENT_RECEIVER;
                    }
                    sendBroad(notifactionId, "", null)
                }

                "1", "101" -> {
                    // 在extras中增加了字段panelid
                    // receiver_1_101(extras, notifactionId)
                    action = HomeFragmentNew.ACTION_INTENT_RECEIVER
                    sendBroad(notifactionId, panelid, gatewayid)
                    Log.e(TAG, "processCustomMessage: panelid:$panelid")
                    action = ACTION_AIRCONTROL_RECEIVER
                    sendBroad(notifactionId, panelid, gatewayid)
                    action = MusicPanelActivity.ACTION_MUSICCONTROL_RECEIVER
                    sendBroad(notifactionId, panelid, gatewayid)

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
                    Log.e("notifactionId", notifactionId.toString() + "")
                    action = MainGateWayActivity.ACTION_SRAUM_SETBOX
                    sendBroad(notifactionId, panelid, gatewayid)
                }
                "50", "102" -> {
                    action = GuJianWangGuanNewActivity.UPDATE_GRADE_BOX
                    sendBroad(notifactionId, panelid, gatewayid)
                }
                "52" -> {
                    //构建对象
                    init_soundPool()
                }
                "51" -> {
                    val extraJson: JSONObject
                    var typeno: String? = null
                    val alert = bundle.getString(JPushInterface.EXTRA_ALERT) ////{"type":"2"}
                    if (!ExampleUtil.isEmpty(extras)) {
                        try {
                            extraJson = JSONObject(extras)
                            typeno = extraJson.getString("typeno")
                            if (typeno != null) {
                                if (typeno == "B001") {
                                    var firstRegister: Boolean = SharedPreferencesUtil.getData(context,
                                            "firstRegister", false) as Boolean
                                    if (firstRegister) {
                                        SpeechUtil.startSpeech(context, 1, alert, "time_push")
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

    }


    /**
     * 门铃报警
     */
    private fun init_soundPool() {
        MusicUtil.startMusic(context, 1, "doorbell")
    }

    private fun sendBroad(notifactionId: String, second: String, gatewayid: String?) {
        val mIntent = Intent(action)
        mIntent.putExtra("notifactionId", notifactionId)
        mIntent.putExtra("panelid", second)
        mIntent.putExtra("gatewayid", gatewayid)
        context!!.sendBroadcast(mIntent)
    }

    companion object {
        private const val TAG = "JPush"
        const val ACTION_NOTIFICATION_OPENED_MAIN = "com.massky.sraum.action.notification_open"

        // 打印所有的 intent extra 数据
        private fun printBundle(bundle: Bundle?): String {
            val sb = StringBuilder()
            for (key in bundle!!.keySet()) {
                if (key == JPushInterface.EXTRA_NOTIFICATION_ID) {
                    sb.append("""

    key:$key, value:${bundle.getInt(key)}
    """.trimIndent())
                } else if (key == JPushInterface.EXTRA_CONNECTION_CHANGE) {
                    sb.append("""

    key:$key, value:${bundle.getBoolean(key)}
    """.trimIndent())
                } else if (key == JPushInterface.EXTRA_EXTRA) {
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Log.i(TAG, "This message has no Extra data")
                        continue
                    }
                    LogUtil.e(TAG, bundle.getString(JPushInterface.EXTRA_EXTRA) + "数据")
                    try {
                        val json = JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA))
                        val it = json.keys()
                        while (it.hasNext()) {
                            val myKey = it.next().toString()
                            sb.append("""
key:$key, value: [$myKey - ${json.optString(myKey)}]1""")
                        }
                    } catch (e: JSONException) {
                        LogUtil.e(TAG, "Get message extra JSON error!")
                    }
                } else {
                    sb.append("""

    key:$key, value:${bundle.getString(key)}
    """.trimIndent())
                }
            }
            return sb.toString()
        }
    }
}