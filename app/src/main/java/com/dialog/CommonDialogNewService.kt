package com.dialog

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.dialog.CommonDialogService
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.LoginCloudActivity
import okhttp3.Call
import java.util.*

/**
 * Created by zhu on 2016/9/8.
 *
 * @version ${VERSION}
 * @decpter
 */
class CommonDialogNewService : Service(), CommonDialogListener {
    private var belowtext_id: TextView? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    val CHANNEL_ID_STRING = "service_01"
    override fun onCreate() {
        super.onCreate()
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var mChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(
                    CHANNEL_ID_STRING, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(mChannel);
            val notification =
                    Notification.Builder(applicationContext, CHANNEL_ID_STRING)
                            .setContentText("sraum正在运行中")
                            .build()
            startForeground(1, notification)
        }
        ToastUtils.getInstances().setListener(this) //绑定
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.cancel()
            dialog = null
        }
    }

    private fun showDialog(str: String) {
        if (dialog != null) {
            dialog!!.cancel()
            dialog = null
        }
        if (dialog == null && CommonData.mNowContext != null) {
            dialog = Dialog(CommonData.mNowContext)
            view = LayoutInflater.from(this).inflate(R.layout.check_is_status, null, false)
            //belowtext_id
            val checkbutton_id = view!!.findViewById<View>(R.id.checkbutton_id) as Button
            belowtext_id = view!!.findViewById<View>(R.id.belowtext_id) as TextView
            belowtext_id!!.text = str
            checkbutton_id.setOnClickListener {
                dialog!!.dismiss()
                val nowactivty = CommonData.mNowContext as Activity
                //                    App.getInstance().removeActivity_but_activity(nowactivty);
                AppManager.getAppManager().removeActivity_but_activity(nowactivty as AppCompatActivity)
                SharedPreferencesUtil.saveData(CommonData.mNowContext, "loginflag", false)
                val intent = Intent(CommonData.mNowContext, LoginCloudActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                nowactivty.finish()
            }
            dialog!!.setContentView(view!!)
            dialog!!.show()
            val lp = dialog!!.window!!
                    .getAttributes()
            //            if(CommonData.ScreenWidth!=0)
//            lp.width =  CommonData.ScreenWidth/ 3;
//
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog!!.setCancelable(false)
            //        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
            val p = dialog!!.window!!.attributes //获取对话框当前的参数值
            //        p.width = (int) (displayWidth * 0.55); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            dialog!!.window!!.attributes = p //设置生效
        } else {
//            Toast.makeText(CommonData.applicationContext,"有误", Toast.LENGTH_SHORT).show();
        }
    }

    override fun show(str: String) {
        showDialog(str)
    }

    override fun cancel() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    override fun show_isfourbackground(str: String) {
        //延时100ms发广播 --MESSAGE_RECEIVED_ACTION

        //发送Action为com.example.communication.RECEIVER的广播
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                intent.putExtra("progress", "");
//                sendBroadcast(intent);
//            }
//        },100);
        init_jlogin(str)
    }

    private fun init_jlogin(str: String) {
        val mobilePhone = SharedPreferencesUtil.getData(CommonData.mNowContext, "loginPhone", "") as String
        val TelephonyMgr = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        //        String szImei = TelephonyMgr.getDeviceId();
        val szImei = SharedPreferencesUtil.getData(this@CommonDialogNewService, "regId", "") as String
        init_islogin(mobilePhone, szImei, str)
    }

    /**
     * 初始化登录
     * @param
     * @param mobilePhone
     * @param szImei
     * @param str
     */
    private fun init_islogin(mobilePhone: String, szImei: String, str: String) {
        val map: MutableMap<String, Any> = HashMap()
        map["mobilePhone"] = mobilePhone
        map["phoneId"] = szImei
        LogUtil.eLength("查看数据", JSON.toJSONString(map))
        MyOkHttp.postMapObject(ApiHelper.sraum_isLogin, map, object : Mycallback(AddTogglenInterfacer { init_islogin(mobilePhone, szImei, str) }, CommonData.mNowContext, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(CommonData.mNowContext, "网络连接超时")
            }

            override fun onSuccess(user: User) {}
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun threeCode() {
                //103已经登录，需要退出app
//                dialog.show();
                if (CommonData.mNowContext != null) {
                    val loginflag = SharedPreferencesUtil.getData(CommonData.mNowContext, "loginflag", false) as Boolean
                    if (loginflag) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                MyService.stopNet();
////                                String result = null;
////                                try {
////                                    result = jsonObject.getString("result");
////                                    if ("ok".equals(result)) {
//////                ToastUtil.showToast(MainfragmentActivity.this,"智能家居退出成功");
////                                        Log.e("fei", "result:" + result);
////                                    }
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
//                            }
//                        }).start();
                        ToastUtils.getInstances().showDialog(str)
                        //                        showDialog(str);
                    }
                }
            }
        })
    }

    companion object {
        private var dialog: Dialog? = null
        private var view: View? = null
    }
}