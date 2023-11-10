package com.massky.sraum.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragment
import com.massky.sraum.view.NestedRadioLayout
import com.massky.sraum.view.NestedSampleRadioGroup
import com.yanzhenjie.statusview.StatusUtils
import java.util.*

/**
 * Created by zhu on 2018/1/30.
 */
class UpDownLeftRightActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    private var status: String? = null
    private var number: String? = null
    private var type: String? = null
    private var name: String? = null
    private var name1: String? = null
    private var name2: String? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    //    @BindView(R.id.name1_txt)
    //    TextView name1_txt;
    //    @BindView(R.id.name2_txt)
    //    TextView name2_txt;
    //    @BindView(R.id.radio_group_out)
    //    LinearLayout radio_group_out;
    //    @BindView(R.id.radio_group_in)
    //    LinearLayout radio_group_in;
    //    @BindView(R.id.radio_group_all)
    //    LinearLayout radio_group_all;
    private val loginPhone: String? = null
    private val vibflag = false
    private val musicflag = false
    private val boxnumber: String? = null
    private var dialogUtil: DialogUtil? = null
    private var statusflag: String? = null
    private val flagone: String? = null
    private val flagtwo: String? = null
    private val flagthree: String? = null
    private val whriteone = true
    private val whritetwo = true
    private val whritethree = true
    private val curtain: String? = null
    private val dimmer: String? = null
    private val modeflag: String? = null
    private val temperature: String? = null
    private val windflag: String? = null
    var statusm: String? = null

    //private MessageReceiver mMessageReceiver;
    private val mapflag = false
    private val statusbo = false
    private var areaNumber: String? = null
    private var roomNumber: String? = null
    private var mapalldevice: Map<String, Any>? = HashMap()

    @JvmField
    @BindView(R.id.first_txt)
    var first_txt: TextView? = null

    @JvmField
    @BindView(R.id.second_txt)
    var second_txt: TextView? = null

    @JvmField
    @BindView(R.id.third_txt)
    var three_txt: TextView? = null

    @JvmField
    @BindView(R.id.four_txt)
    var four_txt: TextView? = null

    @JvmField
    @BindView(R.id.nestedGroup)
    var radioLayout: NestedSampleRadioGroup? = null

    @JvmField
    @BindView(R.id.first_linear)
    var first_linear: NestedRadioLayout? = null

    @JvmField
    @BindView(R.id.second_linear)
    var second_linear: NestedRadioLayout? = null

    @JvmField
    @BindView(R.id.third_linear)
    var third_linear: NestedRadioLayout? = null

    @JvmField
    @BindView(R.id.four_linear)
    var four_linear: NestedRadioLayout? = null
    var first_times = false
    override fun viewId(): Int {
        return R.layout.updown_leftright_window_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        dialogUtil = DialogUtil(this)
        //        init_receiver_control();
        init_View()
        registerMessageReceiver()
    }

    private fun init_View() {
//        radioLayout.setOnCheckedChangeListener((group, checkedId) -> {
//            Log.e("MainActivity", checkedId + "");
//            //sraum_control_dev((Object) checkedId);
//        },(checkedId)->{
//
//        });
        radioLayout!!.setOnCheckedChangeListener(object : NestedSampleRadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: NestedSampleRadioGroup, checkedId: Int) {
                sraum_control_dev(checkedId)
            }

            override fun onCheckedByItemSelf(checkedId: Int) {
                sraum_control_dev(checkedId)
            }
        })
    }

    private fun sraum_control_dev(checkedId: Int) {
        when (checkedId) {
            R.id.first_linear -> when (type) {
                "19" -> statusm = "1"
                "20" -> statusm = "1"
                "21" -> statusm = "1"
            }
            R.id.second_linear -> when (type) {
                "19" -> statusm = "2"
                "20" -> statusm = "2"
                "21" -> statusm = "2"
            }
            R.id.third_linear -> when (type) {
                "19" -> statusm = "0"
                "20" -> statusm = "0"
                "21" -> statusm = "3"
            }
            R.id.four_linear -> when (type) {
                "21" -> statusm = "4"
            }
        }
        if (first_times) {
            first_times = false
            return
        }
        sraum_device_control()
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onData() {
        init_Data()
        init_type()
    }

    private fun init_Data() {
        val bundle = IntentUtil.getIntentBundle(this@UpDownLeftRightActivity)
        type = bundle.getString("type")
        number = bundle.getString("number")
        name1 = bundle.getString("name1")
        name2 = bundle.getString("name2")
        name = bundle.getString("name")
        status = bundle.getString("status")
        areaNumber = bundle.getString("areaNumber")
        roomNumber = bundle.getString("roomNumber") //当前房间编号
        mapalldevice = bundle.getSerializable("mapalldevice") as Map<String, Any>?
        if (mapalldevice != null) {
            type = mapalldevice!!["type"] as String?
            //初始化窗帘参数
            statusm = status
            first_times = true
            change_status_toui(type, status)
        }
        if (name != null) project_select!!.text = name
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.back -> finish()
        }
    }

    private fun init_type() {
        when (type) {
            "19" -> {
                four_linear!!.visibility = View.GONE
                first_txt!!.text = "上升"
                second_txt!!.text = "下降"
                three_txt!!.text = "暂停"
                project_select!!.text = "智能升降"
            }
            "20" -> {
                four_linear!!.visibility = View.GONE
                first_txt!!.text = "向左"
                second_txt!!.text = "向右"
                three_txt!!.text = "暂停"
                project_select!!.text = "智能平移"
            }
            "21" -> {
                four_linear!!.visibility = View.VISIBLE
                first_txt!!.text = "高位"
                second_txt!!.text = "中位"
                three_txt!!.text = "低位"
                four_txt!!.text = "暂停"
                project_select!!.text = "智能高中低"
            }
        }
    }

    private var mMessageReceiver: MessageReceiver? = null
    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.addAction(HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE)
        registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            if (intent.action == HomeFragment.ACTION_INTENT_RECEIVER_TO_SECOND_PAGE) {
                Log.e("zhu", "LamplightActivity:" + "LamplightActivity")
                //控制部分的二级页面进去要同步更新推送的信息显示 （推送的是消息）。
                upload()
            }
        }
    }

    //下载设备信息并且比较状态（为了显示开关状态）
    private fun upload() {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["areaNumber"] = areaNumber
        mapdevice["roomNumber"] = roomNumber
        mapdevice["token"] = TokenUtil.getToken(this)
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapString(ApiHelper.sraum_getOneRoomInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //获取togglen成功后重新刷新数据
            upload()
        }, this, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                //拿到设备状态值
                for (d in user.deviceList) {
                    if (d.number == number) {
                        //匹配状值设置当前状态
                        if (d.status != null) {
                            //进行判断是否为窗帘
                            statusflag = d.status
                            LogUtil.eLength("下载数据", statusflag)
                            change_status_toui(type, d.status)
                        }
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * 根据status去切换UI显示
     */
    private fun change_status_toui(type: String?, status: String?) {
        when (type) {
            "19" -> when (status) {
                "1" -> first_linear!!.isChecked = true
                "2" -> second_linear!!.isChecked = true
                "0" -> third_linear!!.isChecked = true
            }
            "20" -> when (status) {
                "1" -> first_linear!!.isChecked = true
                "2" -> second_linear!!.isChecked = true
                "0" -> third_linear!!.isChecked = true
            }
            "21" -> when (status) {
                "1" -> first_linear!!.isChecked = true
                "2" -> second_linear!!.isChecked = true
                "3" -> third_linear!!.isChecked = true
                "0" -> four_linear!!.isChecked = true
            }
        }
    }

    private fun sraum_device_control() {
        val mapalldevice1: MutableMap<String, Any?> = HashMap()
        val listobj: MutableList<Map<*, *>> = ArrayList()
        val map = HashMap<Any?, Any?>()
        map["type"] = mapalldevice!!["type"].toString()
        map["number"] = mapalldevice!!["number"].toString()
        map["name"] = mapalldevice!!["name"].toString()
        map["status"] = statusm
        map["mode"] = mapalldevice!!["mode"].toString()
        map["dimmer"] = mapalldevice!!["dimmer"].toString()
        map["temperature"] = mapalldevice!!["temperature"].toString()
        map["speed"] = mapalldevice!!["speed"].toString()
        listobj.add(map)
        mapalldevice1["token"] = TokenUtil.getToken(this@UpDownLeftRightActivity)
        mapalldevice1["areaNumber"] = areaNumber
        mapalldevice1["deviceInfo"] = listobj
        MyOkHttp.postMapObject(ApiHelper.sraum_deviceControl, mapalldevice1, object : Mycallback(AddTogglenInterfacer { sraum_device_control() }, this@UpDownLeftRightActivity, dialogUtil) {
            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@UpDownLeftRightActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun fourCode() {
                super.fourCode()
                change_status_toui(type, status)
                ToastUtil.showToast(this@UpDownLeftRightActivity, "控制失败")
            }

            override fun threeCode() {
                super.threeCode()
                change_status_toui(type, status)
                ToastUtil.showToast(this@UpDownLeftRightActivity, "deviceInfo 不正确")
            }

            override fun defaultCode() {
                change_status_toui(type, status)
                ToastUtil.showToast(this@UpDownLeftRightActivity, "操作失败")
            }

            override fun pullDataError() {
                change_status_toui(type, status)
                ToastUtil.showToast(this@UpDownLeftRightActivity, "操作失败")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                status = statusm
                if (vibflag) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    MusicUtil.startMusic(this@UpDownLeftRightActivity, 1, "")
                } else {
                    MusicUtil.stopMusic(this@UpDownLeftRightActivity, "")
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMessageReceiver)
    }
}