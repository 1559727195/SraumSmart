package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.yanzhenjie.statusview.StatusView
import com.yanzhenjie.statusview.StatusUtils
import com.massky.sraum.activity.SelectPmTwoActivity
import com.massky.sraum.base.BaseActivity
import java.io.Serializable
import java.util.HashMap

/**
 * Created by zhu on 2018/6/19.
 */
class SelectPmOneActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.wendu_linear)
    var wendu_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.shidu_rel)
    var shidu_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pm25_rel)
    var pm25_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.panel_scene_name_txt)
    var panel_scene_name_txt: TextView? = null

    @JvmField
    @BindView(R.id.door_open_txt)
    var door_open_txt: TextView? = null

    @JvmField
    @BindView(R.id.door_close_txt)
    var door_close_txt: TextView? = null

    //tv_oc_rel,tv_oc_view
    @JvmField
    @BindView(R.id.tv_oc_rel)
    var tv_oc_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.tv_oc_view)
    var tv_oc_view: View? = null


    private val condition = "0"
    private var map_link = HashMap<Any?, Any?>()
    private var deviceType: String? = null
    override fun viewId(): Int {
        return R.layout.select_pm_one_lay
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        onEvent()
        map_link = intent.getSerializableExtra("map_link") as HashMap<Any?, Any?>
        if (map_link == null) return
        deviceType = map_link!!["deviceType"].toString()
        when (deviceType) {
            "10" -> {
                panel_scene_name_txt!!.text = "温度"
                door_open_txt!!.text = "湿度"
                door_close_txt!!.text = "PM2.5"
            }
            "23" ,"115"-> {
                tv_oc_rel!!.visibility = View.VISIBLE
                tv_oc_view!!.visibility = View.VISIBLE
                panel_scene_name_txt!!.text = "温度"
                door_open_txt!!.text = "湿度"
                door_close_txt!!.text = "PM2.5"
            }

            "102" -> {
                panel_scene_name_txt!!.text = "PM1.0"
                door_open_txt!!.text = "PM2.5"
                door_close_txt!!.text = "PM10"
            }
        }
        setPicture()
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        wendu_linear!!.setOnClickListener(this)
        shidu_rel!!.setOnClickListener(this)
        pm25_rel!!.setOnClickListener(this)
        tv_oc_rel!!.setOnClickListener(this)
    }

    override fun onData() {}
    private fun setPicture() {
        project_select!!.text = map_link!!["name"].toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                finish()
                return
            }
            R.id.wendu_linear -> map_link!!["pm_action"] = "0"
            R.id.shidu_rel -> map_link!!["pm_action"] = "1"
            R.id.pm25_rel -> map_link!!["pm_action"] = "2"
            R.id.tv_oc_rel->map_link!!["pm_action"] = "3"
        }
        init_intent()
    }

    private fun init_intent() {
        var intent: Intent? = null
        intent = Intent(this@SelectPmOneActivity, SelectPmTwoActivity::class.java)
        map_link!!["condition"] = condition
        map_link!!["minValue"] = ""
        map_link!!["maxValue"] = ""
        map_link!!["name1"] = map_link!!["name"]
        intent.putExtra("map_link", map_link as Serializable?)
        startActivity(intent)
    }
}