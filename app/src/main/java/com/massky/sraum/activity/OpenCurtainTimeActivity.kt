package com.massky.sraum.activity

import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Util.TimerUtil.OnTimerChangeListener
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils

/**
 * Created by zhu on 2017/8/25.
 */
class OpenCurtainTimeActivity : BaseActivity() {

    private var deviceNumber: String? = null
    private var areaNumber: String? = null
    private var mode: String? = null

    private lateinit var timerUtil: TimerUtil

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    //next_step_txt
    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    //open_time_txt
    @JvmField
    @BindView(R.id.open_time_txt)
    var open_time_edit: ClearLengthEditText? = null

    //btn_test_time
    @JvmField
    @BindView(R.id.btn_test_time)
    var btn_test_time: Button? = null


    //qian_txt
    @JvmField
    @BindView(R.id.qian_txt)
    var qian_txt: TextView? = null

    @JvmField
    @BindView(R.id.bai_txt)
    var bai_txt: TextView? = null

    @JvmField
    @BindView(R.id.ge_txt)
    var ge_txt: TextView? = null

    private val COUNT_TIME = (1//计时总数，单位/秒
            ).toLong()
    private var currConnTimes = COUNT_TIME

    override fun viewId(): Int {
        return R.layout.open_curtain_time_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        initTimerUtil()
    }

    var time_content_qian = "0"
    var time_content_bai = "0"
    var time_content_ge = "0"

    private fun initTimerUtil() {
        val onTimerChangeListener = OnTimerChangeListener {
            process_currConnTimes()
        }
        timerUtil = TimerUtil(0, 1000, onTimerChangeListener)
    }

    private fun process_currConnTimes() {
        currConnTimes++
        if (currConnTimes >= 255) {
            currConnTimes = COUNT_TIME
        }
        Log.e("TAG", "initTimerUtil: " + currConnTimes)
        // mTvTime.setText(timerUtil.secondConvertHourMinSecond(currConnTimes))
        process_currConnTimes_show()
    }

    private fun process_currConnTimes_show() {
        if (currConnTimes >= 1 && currConnTimes < 10) {
            time_content_qian = "0"
            time_content_bai = "0"
            time_content_ge = currConnTimes.toString()
        } else if (currConnTimes >= 10 && currConnTimes < 100) {
            time_content_qian = "0"
            time_content_bai = currConnTimes.toString().substring(0, 1)
            time_content_ge = currConnTimes.toString().substring(1, 2)
        } else if (currConnTimes >= 100 && currConnTimes < 255) {
            time_content_qian = currConnTimes.toString().substring(0, 1)
            time_content_bai = currConnTimes.toString().substring(1, 2)
            time_content_ge = currConnTimes.toString().substring(2, 3)
        } else {
            time_content_qian = "0"
            time_content_bai = "0"
            time_content_ge = currConnTimes.toString()
        }
        qian_txt!!.setText(time_content_qian)
        bai_txt!!.setText(time_content_bai)
        ge_txt!!.setText(time_content_ge)
    }


    override fun onEvent() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        btn_test_time!!.setOnClickListener(this)
    }

    /**
     * 新增网关同步时间
     */
    private fun sraum_setCurtainTime() {
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this)
        map["deviceNumber"] = deviceNumber
        if (areaNumber == null) return
        map["areaNumber"] = areaNumber
        map["curtainTime"] = open_time_edit!!.text
        MyOkHttp.postMapObject(ApiHelper.sraum_setCurtainTime, map as Map<String, Any>, object : Mycallback(AddTogglenInterfacer { //
            sraum_setCurtainTime()
        }, this@OpenCurtainTimeActivity, null) {
            override fun onSuccess(user: User) {
                if (timerUtil == null) return
                timerUtil.timeStop()
                this@OpenCurtainTimeActivity.finish()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@OpenCurtainTimeActivity, "areaNumber 错 误")
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@OpenCurtainTimeActivity, "deviceNumber 不存在")
            }
        })
    }


    override fun onData() {
        mode = intent.getStringExtra("mode")
        areaNumber = intent.getStringExtra("areaNumber")
        deviceNumber = intent.getStringExtra("deviceNumber")
        if (mode != null) open_time_edit!!.setText(mode)
        if (mode.equals("")) return
        currConnTimes = Integer.parseInt(mode!!).toLong()
        process_currConnTimes_show()
        open_time_edit!!.setFilters(arrayOf<InputFilter>(MaxValueInputFilter(255)))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                if (timerUtil == null) return
                timerUtil.timeStop()
                finish()
            }
            R.id.next_step_txt -> {
                if (btn_test_time!!.text.equals("停止测算")) {
                    ToastUtil.showToast(this@OpenCurtainTimeActivity, "请先停止测算")
                    return
                }
                if (open_time_edit!!.text == null || open_time_edit!!.text!!.equals("")) {
                    ToastUtil.showToast(this@OpenCurtainTimeActivity, "开合度时间为空")
                    return
                }
                sraum_setCurtainTime()
            }
            R.id.btn_test_time -> {
                if (btn_test_time!!.text.equals("开始测算")) {
                    currConnTimes = 0
                    if (timerUtil == null) return
                    timerUtil.timeStart()
                    btn_test_time!!.text = "停止测算"
                } else {
                    if (timerUtil == null) return
                    timerUtil.timeStop()
                    btn_test_time!!.text = "开始测算"
                    open_time_edit!!.setText(currConnTimes.toString())
                }
            }
        }
    }
}