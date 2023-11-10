package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.*
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.SelectiveLinkageActivity
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/6/6.
 */
class MusicLinkageControlActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    /**
     * 空调
     *
     * @return
     */
    @JvmField
    @BindView(R.id.kaiguan_control_radio_model)
    var kaiguan_control_radio_model: RadioGroup? = null

    @JvmField
    @BindView(R.id.mode_control_speed)
    var mode_control_speed: RadioGroup? = null

    @JvmField
    @BindView(R.id.power_control_radio_open_close)
    var power_control_radio_open_close: RadioGroup? = null
    private var music_control_map: MutableMap<*, *> = HashMap<String, String>()
    private val tempture = 0
    private var sensor_map: Map<*, *> = HashMap<Any?, Any?>() //传感器map

    //开关
    @JvmField
    @BindView(R.id.start_one_model)
    var start_one_model: RadioButton? = null

    @JvmField
    @BindView(R.id.pause_two_model)
    var pause_two_model: RadioButton? = null

    @JvmField
    @BindView(R.id.stop_three_model)
    var stop_three_model: RadioButton? = null

    @JvmField
    @BindView(R.id.kaiguan_no_four_model)
    var kaiguan_no_four_model: RadioButton? = null

    //音量
    @JvmField
    @BindView(R.id.high_status_one)
    var high_status_one: RadioButton? = null

    @JvmField
    @BindView(R.id.middle_status_two)
    var middle_status_two: RadioButton? = null

    @JvmField
    @BindView(R.id.lower_status_three)
    var lower_status_three: RadioButton? = null

    @JvmField
    @BindView(R.id.power_no_status_four)
    var power_no_status_four: RadioButton? = null

    //模式
    @JvmField
    @BindView(R.id.all_one_speed)
    var all_one_speed: RadioButton? = null

    @JvmField
    @BindView(R.id.single_two_speed)
    var single_two_speed: RadioButton? = null

    @JvmField
    @BindView(R.id.radiux_three_speed)
    var radiux_three_speed: RadioButton? = null

    @JvmField
    @BindView(R.id.mode_no_four_speed)
    var mode_no_four_speed: RadioButton? = null
    private var map_panel: Map<*, *> = HashMap<String, Any>()
    private val type: String? = null

    /**
     * 空调
     *
     * @return
     */
    override fun viewId(): Int {
        return R.layout.music_linkage_control_act
    }

    override fun onView() {
        // radio_one_model.setText("gdfklgmdfdfg");
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        //        String type = (String) getIntent().getSerializableExtra("type");
        music_control_map = intent.getSerializableExtra("music_control_map") as MutableMap<*, *>
        map_panel = intent.getSerializableExtra("panel_map") as Map<*, *>
        project_select!!.text = music_control_map["name"].toString()
        (music_control_map as HashMap<String, Any>)["name1"] = music_control_map["name"] as String
        (music_control_map as HashMap<String, Any>)["panelName"] = map_panel!!["panelName"] as String
        //intent.putExtra("sensor_map", (Serializable)  sensor_map);
        sensor_map = intent.getSerializableExtra("sensor_map") as Map<*, *>
        back!!.setOnClickListener(this)
        onEvent()
        next_step_txt!!.setOnClickListener(this)
        //
//        getData(true);
        init_action_select()
    }

    override fun onEvent() {
        open_close_control_radio_model()
        music_control_mode()
        power_control_radio_open_close()
    }

    override fun onData() {}

    /**
     * 初始化音乐面板
     */
    private fun init_action_select() { //
        init_music_map()
        //music_control_map["dimmer"]
        mode()
        kaiguan()
        volume()
    }

    private fun init_music_map() {
        (music_control_map as HashMap<String, Any>)["temperature"] = "255"
        // music_control_map["status"]
        (music_control_map as HashMap<String, Any>)["status"] = "255"
        (music_control_map as HashMap<String, Any>)["dimmer"] = "255"
    }

    //循环模式
    private fun mode() {
        val speed = music_control_map["temperature"] as String?
        when (speed) {
            "1" -> mode_control_speed!!.check(R.id.all_one_speed)
            "2" -> mode_control_speed!!.check(R.id.single_two_speed)
            "3" -> mode_control_speed!!.check(R.id.radiux_three_speed)
            "255" -> mode_control_speed!!.check(R.id.mode_no_four_speed)
        }
    }

    //开关
    private fun kaiguan() {
        val model = music_control_map["status"] as String?
        when (model) {
            "1" -> kaiguan_control_radio_model!!.check(R.id.start_one_model)
            "2" -> kaiguan_control_radio_model!!.check(R.id.pause_two_model)
            "3" -> kaiguan_control_radio_model!!.check(R.id.stop_three_model)
            "255" -> kaiguan_control_radio_model!!.check(R.id.kaiguan_no_four_model)
        }
    }

    //音量
    private fun volume() {
        val model = music_control_map["dimmer"] as String?
        val dimmer = model!!.toInt()
        if (dimmer >= 0 && dimmer <= 9) {
            power_control_radio_open_close!!.check(R.id.lower_status_three)
        } else if (dimmer > 9 && dimmer <= 20) {
            power_control_radio_open_close!!.check(R.id.middle_status_two)
        } else if (dimmer > 20 && dimmer <= 30) {
            power_control_radio_open_close!!.check(R.id.high_status_one)
        } else {
            power_control_radio_open_close!!.check(R.id.power_no_status_four)
        }
    }

    /**
     * 模式
     */
    private fun music_control_mode() {
        /**
         * 风速
         */
        for (i in 0 until mode_control_speed!!.childCount) {
            val view = mode_control_speed!!.getChildAt(i)
            view.tag = i
            view.setOnClickListener {
                val position = view.tag as Int
                when (position) {
                    0 -> common_doit("temperature", "1")
                    1 -> common_doit("temperature", "2")
                    2 -> common_doit("temperature", "3")
                    3 -> common_doit("temperature", "255")
                }
            }
        }
    }

    /**
     * 音量
     */
    private fun power_control_radio_open_close() {
        /**
         * 开关
         */
        for (i in 0 until power_control_radio_open_close!!.childCount) {
            val view = power_control_radio_open_close!!.getChildAt(i)
            view.tag = i
            view.setOnClickListener {
                val position = view.tag as Int
                when (position) {
                    0 -> common_doit("dimmer", "30")
                    1 -> common_doit("dimmer", "20")
                    2 -> common_doit("dimmer", "10")
                    3 -> common_doit("dimmer", "255")
                }
            }
        }
    }

    /**
     * 公共项
     *
     * @param status
     * @param value
     */
    private fun common_doit(status: String, value: String) {
        (music_control_map as HashMap<String, Any>)[status] = value
    }

    /**
     *
     * 开关
     */
    private fun open_close_control_radio_model() {
        /**
         * 模式
         */
        for (i in 0 until kaiguan_control_radio_model!!.childCount) {
            val view = kaiguan_control_radio_model!!.getChildAt(i)
            view.tag = i
            view.setOnClickListener {
                val position = view.tag as Int
                when (position) {
                    0 -> common_doit("status", "1")
                    1 -> common_doit("status", "2")
                    2 -> common_doit("status", "3")
                    3 -> common_doit("status", "255")
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
                init_action()
                AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity::class.java)
                AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity::class.java)
                AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
                val intent = Intent(
                        this@MusicLinkageControlActivity,
                        EditLinkDeviceResultActivity::class.java)
                //        map_panel.put("panelType", panelType);
//        map_panel.put("panelName", panelName);
//        map_panel.put("panelMAC", panelMAC);
//        map_panel.put("gatewayMAC", gatewayMAC);
//
                if (map_panel != null) {
                    val gatewayMAC = map_panel.get("gatewayMac").toString()
                    val panelMAC = map_panel.get("panelMac").toString()
                    val panelNumber = map_panel.get("panelNumber").toString()
                    val boxNumber = map_panel.get("boxNumber").toString()
                    //         map_panel.put("panelNumber", panelNumber);
                    //        (areaList[i] as HashMap<String, Any>)["sign"] = "1"
                    (music_control_map as HashMap<String, Any>)["gatewayMac"] = gatewayMAC
                    (music_control_map as HashMap<String, Any>)["panelMac"] = panelMAC
                    (music_control_map as HashMap<String, Any>)["panelNumber"] = panelNumber
                    //           map_panel.put("boxNumber", boxNumber);
                    (music_control_map as HashMap<String, Any>)["boxNumber"] = boxNumber
                }

                intent.putExtra("device_map", music_control_map as Serializable)
                intent.putExtra("sensor_map", sensor_map as Serializable)
                finish()
                startActivity(intent)
            }
            R.id.air_control_tmp_add -> try {
                common_doit("temperature", "" + tempture)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            R.id.air_control_tmp_del -> try {
                common_doit("temperature", "" + tempture)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * 初始化空调动作
     */
    private fun init_action() {
        var temp = operator_status_dimmer_temper()
        common_doit("action", temp.toString())
    }

    private fun operator_status_dimmer_temper(): StringBuffer {
        val temp = StringBuffer()
        val status = music_control_map["status"] as String?
        when (status) {
            "1" -> temp.append("状态:播放")
            "2" -> temp.append("状态:暂停")
            "3" -> temp.append("状态:停止")
            "255" -> temp.append("状态:不变")
        }

        val dimmer = music_control_map["dimmer"] as String?
        when (dimmer) {
            "30" -> temp.append("|音量:高")
            "20" -> temp.append("|音量:中")
            "10" -> temp.append("|音量:低")
            "255" -> temp.append("|音量:不变")
        }

        //修改密码时  本地密码没被修改
        val temperature = music_control_map["temperature"] as String?
        when (temperature) {
            "1" -> temp.append("|循环模式:全部循环")
            "2" -> temp.append("|循环模式:单曲循环")
            "3" -> temp.append("|循环模式:随机循环")
            "255" -> temp.append("|循环模式:不变")
        }
        return temp
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val position_index = ""
        when (seekBar.id) {

        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}