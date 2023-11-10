package com.massky.sraum.activity

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.IntegerRes
import androidx.percentlayout.widget.PercentRelativeLayout
import butterknife.BindView
import com.massky.sraum.R
import com.yanzhenjie.statusview.StatusView
import com.wheel.widget.TosAdapterView
import com.yanzhenjie.statusview.StatusUtils
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.wheel.Utils
import com.wheel.widget.TosGallery
import com.wheel.widget.WheelView
import java.io.Serializable
import java.util.HashMap

/**
 * Created by masskywcy on 2016-11-14.
 */
class SelectPmDataActivity : BaseActivity() {
    //    @BindView(R.id.datePicker)
    //    DatePicker datePicker;
    private val yearb = 0
    private val monthb = 0
    private val dayb = 0

    @JvmField
    @BindView(R.id.wheel1)
    var wheel1: WheelView? = null

    @JvmField
    @BindView(R.id.wheel2)
    var wheel2: WheelView? = null

    @JvmField
    @BindView(R.id.wheel3)
    var wheel3: WheelView? = null

    @JvmField
    @BindView(R.id.wheel4)
    var wheel4: WheelView? = null

    @JvmField
    @BindView(R.id.wheel5)
    var wheel5: WheelView? = null

    //div_five,div_four
    @JvmField
    @BindView(R.id.div_four)
    var div_four: RelativeLayout? = null

    @JvmField
    @BindView(R.id.div_five)
    var div_five: RelativeLayout? = null


    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    //guang_zhao_rel
    @JvmField
    @BindView(R.id.guang_zhao_rel)
    var guang_zhao_rel: PercentRelativeLayout? = null

    //edit_guangzhao_value

    @JvmField
    @BindView(R.id.edit_guangzhao_value)
    var edit_guangzhao_value: ClearEditText? = null

    //sel_pm_rel
    @JvmField
    @BindView(R.id.sel_pm_rel)
    var sel_pm_rel: RelativeLayout? = null


    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null
    var mData: IntArray? = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    var text_pm = ""
    private var map_link = HashMap<Any?, Any?>()
    private var condition = "0"
    private var deviceType: String? = null
    override fun viewId(): Int {
        return R.layout.select_pm_data
    }

    var mDecorView: View? = null
    var mStart = false
    private val mListener: TosAdapterView.OnItemSelectedListener = object : TosAdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: TosAdapterView<*>?, view: View?, position: Int, id: Long) {
            formatData()
        }

        override fun onNothingSelected(parent: TosAdapterView<*>?) {}
    }

    private fun formatData() {
        val pos1 = wheel1!!.selectedItemPosition
        val pos2 = wheel2!!.selectedItemPosition
        val pos3 = wheel3!!.selectedItemPosition
        val pos4 = wheel4!!.selectedItemPosition
        val pos5 = wheel5!!.selectedItemPosition
        init_form_data(pos1, pos2, pos3, pos4, pos5)

        Log.e("robin debug", "text_pm:$text_pm")
        //        mTextView.setText(text);
//        condition = "1";
    }

    private fun init_form_data(pos1: Int, pos2: Int, pos3: Int, pos4: Int, pos5: Int) {
        if (map_link != null)
            when (map_link!!["pm_action"].toString()) {
                "3" -> {//0,0,0,0,0->空气质量
                    if (pos1 == 0) {
                        text_pm = String.format("%d%d%d%d", pos2, pos3, pos4, pos5)
                        if (pos2 == 0) {
                            text_pm = String.format("%d%d%d", pos3, pos4, pos5)
                            if (pos3 == 0) {
                                text_pm = String.format("%d%d", pos4, pos5)
                                if (pos4 == 0) {
                                    text_pm = String.format("%d", pos5)
                                } else {
                                    text_pm = String.format("%d%d", pos4, pos5)
                                }
                            } else {
                                text_pm = String.format("%d%d%d", pos3, pos4, pos5)
                            }
                        } else {
                            text_pm = String.format("%d%d%d%d", pos2, pos3, pos4, pos5)
                        }
                    } else {
                        text_pm = String.format("%d%d%d%d%d", pos1, pos2, pos3, pos4, pos5)
                    }
                }
                else -> {
                    if (pos1 == 0) {
                        text_pm = String.format("%d%d", pos2, pos3)
                        text_pm = if (pos2 == 0) {
                            String.format("%d", pos3)
                        } else {
                            String.format("%d%d", pos2, pos3)
                        }
                    } else {
                        text_pm = String.format("%d%d%d", pos1, pos2, pos3)
                    }
                }
            }
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        init_data()
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun init_data() {
        init_view()
        map_link = intent.getSerializableExtra("map_link") as HashMap<Any?, Any?>
        if (map_link == null) return
        init_select_title()
        init_select_data()
    }

    private fun init_select_data() {
        when (deviceType) {
            "25" -> {
                guang_zhao_rel!!.visibility = View.VISIBLE
                sel_pm_rel!!.visibility = View.GONE
                return
            }
        }
        when (map_link!!["pm_action"].toString()) {
            "0" -> {
                init_common_data("温度", 0, 2, 6, 0, 0)
                condition = "2"
            }
            "1" -> {
                condition = "3"
                init_common_data("湿度", 0, 5, 0, 0, 0)
            }
            "2" -> {
                condition = "1"
                init_common_data("PM2.5", 1, 0, 0, 0, 0)
            }
            "3" -> {//空气质量
                condition = "4"
                init_common_data("空气质量", 0, 1, 0, 0, 0)
            }
        }
    }

    /**
     * 标题赋值
     */
    private fun init_select_title() {
        //选择PM值-project_select
        deviceType = map_link!!["deviceType"].toString()
        when (deviceType) {
            "10" -> when (map_link!!["pm_action"].toString()) {
                "0" -> {
                    condition = "2"
                    project_select!!.text = "选择" + "温度" + "值"
                }
                "1" -> {
                    condition = "3"
                    project_select!!.text = "选择" + "湿度" + "值"
                }
                "2" -> {
                    condition = "1"
                    project_select!!.text = "选择" + "PM2.5" + "值"
                }
            }
            "23" ,"115"-> {
                when (map_link!!["pm_action"].toString()) {
                    "0" -> {
                        condition = "2"
                        project_select!!.text = "选择" + "温度" + "值"
                    }
                    "1" -> {
                        condition = "3"
                        project_select!!.text = "选择" + "湿度" + "值"
                    }
                    "2" -> {
                        condition = "1"
                        project_select!!.text = "选择" + "PM2.5" + "值"
                    }

                    "3" -> {
                        div_five!!.visibility = View.VISIBLE
                        div_four!!.visibility = View.VISIBLE
                        wheel4!!.visibility = View.VISIBLE
                        wheel5!!.visibility = View.VISIBLE
                        condition = "4"
                        project_select!!.text = "选择" + "空气质量" + "值"
                    }
                }
            }
            "102" -> when (map_link!!["pm_action"].toString()) {
                "0" -> {
                    condition = "2"
                    project_select!!.text = "选择" + "PM1.0" + "值"
                }
                "1" -> {
                    condition = "1"
                    project_select!!.text = "选择" + "PM2.5" + "值"
                }
                "2" -> {
                    condition = "3"
                    project_select!!.text = "选择" + "PM10" + "值"
                }

            }

            "25" -> {//光照传感器
                condition = "1"
                project_select!!.text = "选择" + "光照" + "值"
            }
        }
    }

    private fun init_common_data(s: String, one: Int, two: Int, thee: Int, i: Int, i1: Int) {
        project_select!!.text = s
        //        wheel1.setSelection(9);
//        wheel2.setSelection(9);
//        wheel3.setSelection(9);
        wheel1!!.setSelection(one)
        wheel2!!.setSelection(two)
        wheel3!!.setSelection(thee)
        wheel4!!.setSelection(thee)
        wheel5!!.setSelection(thee)
    }

    private fun init_view() {
        wheel1!!.isScrollCycle = true
        wheel2!!.isScrollCycle = true
        wheel3!!.isScrollCycle = true
        wheel4!!.isScrollCycle = true
        wheel5!!.isScrollCycle = true
        wheel1!!.adapter = NumberAdapter()
        wheel2!!.adapter = NumberAdapter()
        wheel3!!.adapter = NumberAdapter()
        wheel4!!.adapter = NumberAdapter()
        wheel5!!.adapter = NumberAdapter()
        wheel1!!.onItemSelectedListener = mListener
        wheel2!!.onItemSelectedListener = mListener
        wheel3!!.onItemSelectedListener = mListener
        wheel4!!.onItemSelectedListener = mListener
        wheel5!!.onItemSelectedListener = mListener


//        formatData();
        mDecorView = window.decorView
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> result_selectpmdata()
        }
    }

    private fun result_selectpmdata() {
        val add_condition = SharedPreferencesUtil.getData(this@SelectPmDataActivity, "add_condition", false) as Boolean
        var intent: Intent? = null
        map_link!!["condition"] = condition
        val deviceType = map_link!!["deviceType"].toString()

        //                map_link.put("pm_condition", "0");
        var temp = ""
        when (map_link!!["pm_condition"].toString()) {
            "0" -> temp = "大于等于 "
            "1" -> temp = "小于等于 "
        }

        when (deviceType) {
            "25" -> {
                text_pm = edit_guangzhao_value!!.text.toString()
            }
        }

        when (map_link!!["pm_condition"].toString()) {
            "0" -> map_link!!["maxValue"] = text_pm
            "1" -> map_link!!["minValue"] = text_pm
        }

        when (deviceType) {
            "10" -> when (map_link!!["pm_action"].toString()) {
                "0" -> map_link!!["action"] = "温度 $temp$text_pm℃"
                "1" -> map_link!!["action"] = "湿度 $temp$text_pm%"
                "2" -> map_link!!["action"] = "PM2.5 $temp$text_pm"
            }
            "23","115" -> when (map_link!!["pm_action"].toString()) {
                "0" -> map_link!!["action"] = "温度 $temp$text_pm℃"
                "1" -> map_link!!["action"] = "湿度 $temp$text_pm%"
                "2" -> map_link!!["action"] = "PM2.5 $temp$text_pm"
                "3" -> map_link!!["action"] = "空气质量 $temp$text_pm"
            }
            "102" -> when (map_link!!["pm_action"].toString()) {
                "0" -> map_link!!["action"] = "PM1.0 $temp$text_pm"
                "1" -> map_link!!["action"] = "PM2.5 $temp$text_pm"
                "2" -> map_link!!["action"] = "PM10 $temp$text_pm"
            }

            "25" -> {//光照传感器
                if (edit_guangzhao_value!!.text!!.toString().trim().equals("")) {
                    ToastUtil.showToast(this@SelectPmDataActivity, "光照值为空")
                    return
                }
                if (Integer.parseInt(text_pm) > 200000) {
                    ToastUtil.showToast(this@SelectPmDataActivity, "光照值大于最大范围值")
                    return
                }
                map_link!!["action"] = "光照值 $temp$text_pm"
            }
        }
        if (add_condition) { //
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
            //                AppManager.getAppManager().finishActivity_current(AirLinkageControlActivity.class);
//                AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
            AppManager.getAppManager().finishActivity_current(SelectSensorActivity::class.java)
            AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
            intent = Intent(this@SelectPmDataActivity, EditLinkDeviceResultActivity::class.java)
            intent.putExtra("sensor_map", map_link as Serializable?)
            startActivity(intent)
            finish()
        } else { //
            intent = Intent(this@SelectPmDataActivity,
                    SelectiveLinkageActivity::class.java)
            intent.putExtra("link_map", map_link as Serializable?)
            startActivity(intent)
        }
    }

    private inner class NumberAdapter : BaseAdapter() {
        var mHeight = 50
        override fun getCount(): Int {
            return if (null != mData) mData!!.size else 0
        }

        override fun getItem(arg0: Int): Any? {
            return null
        }

        override fun getItemId(arg0: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView: View? = convertView
            var txtView: TextView? = null
            if (null == convertView) {
                convertView = TextView(this@SelectPmDataActivity)
                convertView.setLayoutParams(TosGallery.LayoutParams(-1, mHeight))
                txtView = convertView
                txtView!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25f)
                txtView.setTextColor(Color.GRAY)
                txtView.gravity = Gravity.CENTER
            }
            val text = mData!![position].toString()
            if (null == txtView) {
                txtView = convertView as TextView?
            }
            txtView!!.text = text
            return convertView
        }

        init {
            mHeight = Utils.pixelToDp(this@SelectPmDataActivity, mHeight.toFloat()).toInt()
        }
    }
}