package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.*
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.SelectSensorActivity
import com.massky.sraum.activity.SelectiveLinkageActivity
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/6/19.
 */
class UnderWaterActivity : BaseActivity() {
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
    @BindView(R.id.rel_scene_set)
    var rel_scene_set: RelativeLayout? = null

    @JvmField
    @BindView(R.id.checkbox)
    var checkbox: CheckBox? = null

    @JvmField
    @BindView(R.id.panel_scene_name_txt)
    var panel_scene_name_txt: TextView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.fangdao_linear)
    var fangdao_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_fangdao_open)
    var rel_fangdao_open: RelativeLayout? = null

    @JvmField
    @BindView(R.id.door_open_txt)
    var door_open_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_fangdao_open)
    var checkbox_fangdao_open: CheckBox? = null

    @JvmField
    @BindView(R.id.rel_fangdao_close)
    var rel_fangdao_close: RelativeLayout? = null

    @JvmField
    @BindView(R.id.door_close_txt)
    var door_close_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_fangdao_close)
    var checkbox_fangdao_close: CheckBox? = null

    @JvmField
    @BindView(R.id.humancheck_linear)
    var humancheck_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_humancheck)
    var rel_humancheck: RelativeLayout? = null

    @JvmField
    @BindView(R.id.humancheck_txt)
    var humancheck_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_humancheck)
    var checkbox_humancheck: CheckBox? = null

    @JvmField
    @BindView(R.id.jiuzuo_linear)
    var jiuzuo_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_jiuzuo)
    var rel_jiuzuo: RelativeLayout? = null

    @JvmField
    @BindView(R.id.jiuzuo_txt)
    var jiuzuo_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_jiuzuo)
    var checkbox_jiuzuo: CheckBox? = null

    @JvmField
    @BindView(R.id.gas_linear)
    var gas_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_gas)
    var rel_gas: RelativeLayout? = null

    @JvmField
    @BindView(R.id.gas_txt)
    var gas_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_gas)
    var checkbox_gas: CheckBox? = null

    @JvmField
    @BindView(R.id.smoke_linear)
    var smoke_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_smoke)
    var rel_smoke: RelativeLayout? = null

    @JvmField
    @BindView(R.id.smoke_txt)
    var smoke_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_smoke)
    var checkbox_smoke: CheckBox? = null

    @JvmField
    @BindView(R.id.emergcybtn_linear)
    var emergcybtn_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.rel_emergcybtn)
    var rel_emergcybtn: RelativeLayout? = null

    @JvmField
    @BindView(R.id.emergcybtn_txt)
    var emergcybtn_txt: TextView? = null

    @JvmField
    @BindView(R.id.checkbox_emergcybtn)
    var checkbox_emergcybtn: CheckBox? = null
    private var condition = "0"
    private var map_link = HashMap<Any?, Any?>()
    override fun viewId(): Int { //
        return R.layout.under_water_lay
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        rel_scene_set!!.setOnClickListener(this)
        //        onEvent();
        map_link = intent.getSerializableExtra("map_link") as HashMap<Any?, Any?>
        if (map_link == null) return
        setPicture(map_link!!["deviceType"].toString())
    }

    override fun onEvent() {
        rel_fangdao_open!!.setOnClickListener(this)
        rel_fangdao_close!!.setOnClickListener(this)
        rel_humancheck!!.setOnClickListener(this)
        rel_jiuzuo!!.setOnClickListener(this)
        rel_gas!!.setOnClickListener(this)
        rel_smoke!!.setOnClickListener(this)
        rel_emergcybtn!!.setOnClickListener(this)
    }

    override fun onData() {}

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    private fun setPicture(type: String) {
        when (type) {
            "7" -> fangdao_linear!!.visibility = View.VISIBLE
            "24" -> {//吸顶人体感应
                fangdao_linear!!.visibility = View.VISIBLE
                door_open_txt!!.text = "有人"
                door_close_txt!!.text = "无人"
            }
            "8" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("人体检测");
                humancheck_txt!!.text = "有人经过"
            }
            "9" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("主卧水浸");
                humancheck_txt!!.text = "报警"
            }
            "11" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("紧急按钮");
                humancheck_txt!!.text = "按下"
            }
            "12" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("如厕检测");
                humancheck_txt!!.text = "报警"
            }
            "13" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("烟雾检测");
                humancheck_txt!!.text = "报警"
            }
            "14" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("天然气检测");
                humancheck_txt!!.text = "报警"
            }
            "15" -> {
                //                project_select.setText("智能门锁");
                fangdao_linear!!.visibility = View.VISIBLE
                rel_fangdao_close!!.visibility = View.GONE
            }
            "16" -> {
            }
            "25" -> {
                humancheck_linear!!.visibility = View.VISIBLE
                //                project_select.setText("天然气检测");
                humancheck_txt!!.text = "流明"
            }
        }
        project_select!!.text = map_link!!["name"].toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
            }
            R.id.rel_fangdao_open -> {
                condition = "1"
                //                checkbox_fangdao_open.setChecked(true);
//                checkbox_fangdao_close.setChecked(false);
//                door_open_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                door_close_txt.setTextColor(getResources().getColor(R.color.black_color));
                init_intent()
            }
            R.id.rel_fangdao_close -> {
                condition = "0"
                //                checkbox_fangdao_open.setChecked(false);
//                checkbox_fangdao_close.setChecked(true);
//                door_close_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                door_open_txt.setTextColor(getResources().getColor(R.color.black_color));
                init_intent()
            }
            R.id.rel_humancheck -> {
                //                checkbox_humancheck.toggle();
//                if (checkbox_humancheck.isChecked()) {
//                    condition = "1";
//                    humancheck_txt.setTextColor(getResources().getColor(R.color.gold_color));
//                } else {
//                    condition = "0";
//                    humancheck_txt.setTextColor(getResources().getColor(R.color.black_color));
//                }
                condition = "1"
                init_intent()
            }
        }
    }

    private fun init_intent() {
        var intent: Intent? = null
        setAction(map_link!!["deviceType"].toString(), condition)
        map_link!!["condition"] = condition
        map_link!!["minValue"] = ""
        map_link!!["maxValue"] = ""
        map_link!!["name1"] = map_link!!["name"]

        if (map_link!!["deviceType"].toString().equals("25")) {//光照传感器
            intent = Intent(this@UnderWaterActivity, SelectPmTwoActivity::class.java)
            intent.putExtra("map_link", map_link as Serializable?)
            startActivity(intent)
            return
        }

        val add_condition = SharedPreferencesUtil.getData(this@UnderWaterActivity, "add_condition", false) as Boolean
        if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
            AppManager.getAppManager().finishActivity_current(SelectSensorActivity::class.java)
            AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
            intent = Intent(this@UnderWaterActivity, EditLinkDeviceResultActivity::class.java)
            intent.putExtra("sensor_map", map_link as Serializable?)
            startActivity(intent)
            finish()
        } else {
            intent = Intent(this@UnderWaterActivity,
                    SelectiveLinkageActivity::class.java)
            intent.putExtra("link_map", map_link as Serializable?)
            startActivity(intent)
        }
    }

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    private fun setAction(type: String, action: String): Map<*, *>? {
        when (type) {
            "7" -> if (action == "1") {
                map_link!!["action"] = "打开"
            } else {
                map_link!!["action"] = "闭合"
            }
            "8" -> if (action == "1") {
                map_link!!["action"] = "有人经过"
            }
            "9" -> if (action == "1") {
                map_link!!["action"] = "报警"
            }
            "10" -> {
            }
            "11" -> if (action == "1") {
                map_link!!["action"] = "按下"
            }
            "12" -> if (action == "1") {
                map_link!!["action"] = "报警"
            }
            "13" -> if (action == "1") {
                map_link!!["action"] = "报警"
            }
            "14" -> if (action == "1") {
                map_link!!["action"] = "报警"
            }
            "15" -> if (action == "1") {
                map_link!!["action"] = "打开"
            } else {
                map_link!!["action"] = "闭合"
            }
            "16" -> {
            }
            "24" -> when (action) {
                "1" -> map_link!!["action"] = "有人"
                "0" -> map_link!!["action"] = "无人"
            }
            "25" -> if (action == "1") {
                map_link!!["action"] = "流明"
            }
        }
        return map_link
    }
}