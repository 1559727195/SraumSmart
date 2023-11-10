package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.example.jpushdemo.ExampleUtil
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragment
import com.yanzhenjie.statusview.StatusUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by zhu on 2018/1/25.
 */
class Pm25SecondActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.temp_label)
    var temp_label: TextView? = null

    @JvmField
    @BindView(R.id.temp_txt)
    var temp_txt: TextView? = null

    @JvmField
    @BindView(R.id.pm_label)
    var pm_label: TextView? = null

    @JvmField
    @BindView(R.id.pm_txt)
    var pm_txt: TextView? = null


    @JvmField
    @BindView(R.id.pm_label_old)
    var pm_label_old: TextView? = null

    @JvmField
    @BindView(R.id.pm_txt_old)
    var pm_txt_old: TextView? = null

    @JvmField
    @BindView(R.id.shidu_label)
    var shidu_label: TextView? = null

    @JvmField
    @BindView(R.id.shidu_txt)
    var shidu_txt: TextView? = null

    //pm25_new
    @JvmField
    @BindView(R.id.pm25_new)
    var pm25_new: TextView? = null

    //air
    @JvmField
    @BindView(R.id.air)
    var air: TextView? = null

    //pm25_new_linear
    @JvmField
    @BindView(R.id.pm25_new_linear)
    var pm25_new_linear: LinearLayout? = null

    //pm_linear_old,pm_linear_second_new
    @JvmField
    @BindView(R.id.pm_linear_old)
    var pm_linear_old: LinearLayout? = null


    @JvmField
    @BindView(R.id.pm_linear_second_new)
    var pm_linear_second_new: LinearLayout? = null


    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null
    private var loginPhone: String? = null
    private val vibflag = false
    private val musicflag = false
    private var boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private val statusflag: String? = null
    private var dimmer: String? = null
    private val modeflag: String? = null
    private var temperature: String? = null
    private val windflag: String? = null
    private var type: String? = null
    private var number: String? = null
    private var name: String? = null
    private var areaNumber: String? = null
    private var roomNumber: String? = null
    private var mapalldevice: Map<String, Any>? = null
    private var mMessageReceiver: MessageReceiver? = null
    private var speed: String? = null
    private var status: String? = null
    private var mode: String? = null
    override fun viewId(): Int {
        return R.layout.pm25_new_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        registerMessageReceiver()
        loginPhone = SharedPreferencesUtil.getData(this@Pm25SecondActivity, "loginPhone", "") as String
        val preferences = getSharedPreferences("sraum$loginPhone",
                Context.MODE_PRIVATE)
        boxnumber = SharedPreferencesUtil.getData(this@Pm25SecondActivity, "boxnumber", "") as String
        dialogUtil = DialogUtil(this@Pm25SecondActivity)
        init_Data()
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@Pm25SecondActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name = bundle.getString("name")
        project_select!!.text = name
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber") //当前房间编号
        show_pm(bundle)
        if (name != null) project_select!!.text = name
    }

    /**
     * 显示pm2.5
     *
     * @param bundle
     */
    private fun show_pm(bundle: Bundle) {
        pm25_new_linear!!.visibility = View.GONE
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>?
        if (mapalldevice != null) {
            when (type) {
                "10" -> {
                    dimmer = mapalldevice!!["dimmer"] as String? //pm25
                    temperature = mapalldevice!!["temperature"] as String? //温度
                    speed = mapalldevice!!["speed"] as String? //湿度
                    temp_label!!.text = "温度"
                    pm_label_old!!.text = "PM2.5:"
                    shidu_label!!.text = "湿度"
                    temp_txt!!.text = temperature
                    // pm_txt!!.text = dimmer
                    shidu_txt!!.text = speed

                    var pm_25 = Integer.parseInt(dimmer!!)

                    if (pm_25 <= 35) {//优
                        pm_txt_old!!.text = dimmer + "  " + "优"
                    } else if (pm_25 > 35 && pm_25 <= 75) {//良
                        pm_txt_old!!.text = dimmer + "  " + "良"
                    } else if (pm_25 > 75 && pm_25 <= 115) {//轻度污染
                        pm_txt_old!!.text = dimmer + "  " + "轻度污染"
                    } else if (pm_25 > 115 && pm_25 <= 150) {//中度污染
                        pm_txt_old!!.text = dimmer + "  " + "中度污染"
                    } else if (pm_25 > 150 && pm_25 <= 250) {//重度污染
                        pm_txt_old!!.text = dimmer + "  " + "重度污染"
                    } else if (pm_25 > 250) {//严重污染
                        pm_txt_old!!.text = dimmer + "  " + "严重污染"
                    }

                    pm_linear_second_new!!.visibility = View.GONE
                }
                "102" -> {
                    dimmer = mapalldevice!!["dimmer"] as String? //pm25
                    mode = mapalldevice!!["mode"] as String? //pm1.0
                    temperature = mapalldevice!!["temperature"] as String? //pm10
                    temp_label!!.text = "PM1.0"
                    pm_label_old!!.text = "PM2.5:"
                    shidu_label!!.text = "PM10"
                    temp_txt!!.text = mode
                    // pm_txt!!.text = dimmer
                    shidu_txt!!.text = temperature

                    var pm_25 = Integer.parseInt(dimmer!!)

                    if (pm_25 <= 35) {//优
                        pm_txt_old!!.text = dimmer + "  " + "优"
                    } else if (pm_25 > 35 && pm_25 <= 75) {//良
                        pm_txt_old!!.text = dimmer + "  " + "良"
                    } else if (pm_25 > 75 && pm_25 <= 115) {//轻度污染
                        pm_txt_old!!.text = dimmer + "  " + "轻度污染"
                    } else if (pm_25 > 115 && pm_25 <= 150) {//中度污染
                        pm_txt_old!!.text = dimmer + "  " + "中度污染"
                    } else if (pm_25 > 150 && pm_25 <= 250) {//重度污染
                        pm_txt_old!!.text = dimmer + "  " + "重度污染"
                    } else if (pm_25 > 250) {//严重污染
                        pm_txt_old!!.text = dimmer + "  " + "严重污染"
                    }
                    pm_linear_second_new!!.visibility = View.GONE
                }
                "23", "115" -> {
                    dimmer = mapalldevice!!["dimmer"] as String? //pm25
                    temperature = mapalldevice!!["temperature"] as String? //温度
                    speed = mapalldevice!!["speed"] as String? //湿度
                    status = mapalldevice!!["status"] as String? //二氧化碳含量
                    mode = mapalldevice!!["mode"] as String? //空气质量
                    common_pm_control()
                }
            }
        }
    }

    private fun common_pm_control() {
        temp_label!!.text = "温度"
        pm_label!!.text = "湿度"
        shidu_label!!.text = "二氧化碳含量"
        temp_txt!!.text = temperature
        pm_txt!!.text = speed
        if(status != null) {
            if (Integer.parseInt(status!!) < 65535)
                shidu_txt!!.text = status
            else
                shidu_txt!!.text = ""
        }
        var tv_oc = Integer.parseInt(mode!!)

        if (tv_oc <= 2000) {//优
            air!!.text = mode + "  " + "优"
        } else if (tv_oc > 2000 && tv_oc <= 20000) {//良
            air!!.text = mode + "  " + "良"
        } else if (tv_oc > 20000 && tv_oc <= 60000) {//差
            air!!.text = mode + "  " + "差"
        } else  {
            air!!.text = ""
        }

        var pm_25 = Integer.parseInt(dimmer!!)

        if (pm_25 <= 35) {//优
            pm25_new!!.text = dimmer + "  " + "优"
        } else if (pm_25 > 35 && pm_25 <= 75) {//良
            pm25_new!!.text = dimmer + "  " + "良"
        } else if (pm_25 > 75 && pm_25 <= 115) {//轻度污染
            pm25_new!!.text = dimmer + "  " + "轻度污染"
        } else if (pm_25 > 115 && pm_25 <= 150) {//中度污染
            pm25_new!!.text = dimmer + "  " + "中度污染"
        } else if (pm_25 > 150 && pm_25 <= 250) {//重度污染
            pm25_new!!.text = dimmer + "  " + "重度污染"
        } else if (pm_25 > 250) {//严重污染
            pm25_new!!.text = dimmer + "  " + "严重污染"
        }

        pm25_new_linear!!.visibility = View.VISIBLE
        pm_linear_old!!.visibility = View.GONE
    }

    /**
     * 动态注册广播
     */
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE)
        filter.addAction(HomeFragment.ACTION_INTENT_RECEIVER_TABLE_PM)
        registerReceiver(mMessageReceiver, filter)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE) {
                Log.e("zhu", "LamplightActivity:" + "LamplightActivity")
                //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                upload()
            } else if (intent.action == HomeFragment.ACTION_INTENT_RECEIVER_TABLE_PM) {
                val extraJson: JSONObject
                val extras = intent.getStringExtra("extras")
                if (!ExampleUtil.isEmpty(extras)) {
                    try {
                        extraJson = JSONObject(extras)
                        val id = extraJson.getString("id")
                        val pm2_5 = extraJson.getString("pm2.5")
                        val pm1_0 = extraJson.getString("pm1.0")
                        val pm10 = extraJson.getString("pm10")
                        if (number != null) {
                            if (number == id) {
                                temp_txt!!.text = pm1_0
                                pm_txt!!.text = pm2_5
                                shidu_txt!!.text = pm10
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        //下载设备信息并且比较状态（为了显示开关状态）
        private fun upload() {
            val mapdevice: MutableMap<String?, String?> = HashMap()
            mapdevice["areaNumber"] = areaNumber
            mapdevice["roomNumber"] = roomNumber
            mapdevice["token"] = TokenUtil.getToken(this@Pm25SecondActivity)
            dialogUtil!!.loadDialog()
            SharedPreferencesUtil.saveData(this@Pm25SecondActivity, "boxnumber", boxnumber)
            MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //获取togglen成功后重新刷新数据
                upload()
            }, this@Pm25SecondActivity, dialogUtil) {
                override fun onSuccess(user: User) {
                    super.onSuccess(user)
                    //拿到设备状态值
                    jpush_get(user)
                }

                override fun wrongToken() {
                    super.wrongToken()
                }
            })
        }

        /**
         * 收到极光推送后刷新
         *
         * @param user
         */
        private fun jpush_get(user: User) {
            for (d in user.deviceList) {
                if (d.number == number) {
                    //
                    when (if (d.type == null) "" else d.type) {
                        "10" -> {
                            temp_txt!!.text = d.temperature
                            pm_txt!!.text = d.dimmer
                            shidu_txt!!.text = d.speed
                        }
                        "102" -> {
                            temp_txt!!.text = d.mode
                            pm_txt!!.text = d.dimmer
                            shidu_txt!!.text = d.temperature
                        }

                        "23", "115" -> {//空调控制器
                            dimmer = d.dimmer //pm25
                            temperature = d.temperature //温度
                            speed = d.speed//湿度
                            status = d.status//二氧化碳含量
                            mode = d.mode //空气质量
                            common_pm_control()
                        }
                    }
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }
}