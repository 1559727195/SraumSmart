package com.massky.sraum.activity

import android.content.Intent
import android.opengl.Visibility
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView


/**
 * Created by zhu on 2018/5/30.
 */
class AddWifiDevActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null


    @JvmField
    @BindView(R.id.next_step_id)
    var next_step_id: Button? = null

    @JvmField
    @BindView(R.id.img_show_zigbee)
    var img_show_zigbee: ImageView? = null

    @JvmField
    @BindView(R.id.manager_room_txt)
    var manager_room_txt: TextView? = null


    @JvmField
    @BindView(R.id.txt_title)
    var txt_title: TextView? = null
    private val icon_wifi = intArrayOf(
            R.drawable.pic_wifi_hongwai, R.drawable.pic_zigbee_kaiguan_1, R.drawable.pic_zigbee_kaiguan_2,
            R.drawable.pic_zigbee_kaiguan_3, R.drawable.pic_zigbee_kaiguan_4,R.drawable.pic_wifi_1kai,R.drawable.pic_wifi_2kai,
            R.drawable.pic_wifi_3kai,R.drawable.pic_zigbee_chazuo,R.drawable.pic_zigebee_chuangliandianji,
            R.drawable.pic_zigbee_pm250
    )

    private val iconString = intArrayOf(
            R.string.yijianlight_promat,
            R.string.zigbee_other_promat, R.string.zigbee_promat_btn_down, R.string.zhinengchazuo_promat, R.string.duogongneng_promat
    ,R.string.wifi_kaiguan_pormat, R.string.wifi_promact,R.string.wifi_curtain_promat
    )

    private var type = ""
    override fun viewId(): Int {
        return R.layout.add_wifi_dev_act_new
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        next_step_id!!.setOnClickListener(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
        manager_room_txt!!.setOnClickListener(this)
        type = intent.getSerializableExtra("type") as String
        when (type) {
            "A2A1" -> {
                img_show_zigbee!!.setImageResource(icon_wifi[1])
                txt_title!!.setText(iconString[0])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "A2A2" -> {
                img_show_zigbee!!.setImageResource(icon_wifi[2])
                txt_title!!.setText(iconString[0])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "A2A3" -> {
                img_show_zigbee!!.setImageResource(icon_wifi[3])
                txt_title!!.setText(iconString[0])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "A2A4" -> {
                img_show_zigbee!!.setImageResource(icon_wifi[4])
                txt_title!!.setText(iconString[0])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "101" -> {
                img_show_zigbee!!.setImageResource(R.drawable.pic_wifi_shexiangtou)
                txt_title!!.text = resources.getString(R.string.camera_promat_str)
            }
            "103" -> {
                img_show_zigbee!!.setImageResource(R.drawable.pic_wifi_dianzimenling)
                txt_title!!.text = resources.getString(R.string.door_camera_promat_str)
            }
            "hongwai" -> img_show_zigbee!!.setImageResource(icon_wifi[0])
            "102" -> {
            }
            "yaokong" -> img_show_zigbee!!.setImageResource(icon_wifi[0])
            "ADA1"->{
                img_show_zigbee!!.setImageResource(icon_wifi[5])
                txt_title!!.setText(iconString[5])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "ADA2"->{
                img_show_zigbee!!.setImageResource(icon_wifi[6])
                txt_title!!.setText(iconString[5])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "ADA3"-> {
                img_show_zigbee!!.setImageResource(icon_wifi[7])
                txt_title!!.setText(iconString[5])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "ADB1"-> {
                img_show_zigbee!!.setImageResource(icon_wifi[8])
                txt_title!!.setText(iconString[6])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "ADB2"-> {
                img_show_zigbee!!.setImageResource(icon_wifi[9])
                txt_title!!.setText(iconString[7])
                manager_room_txt!!.visibility = View.VISIBLE
            }
            "ADB3" -> {
                img_show_zigbee!!.setImageResource(icon_wifi[10])
                txt_title!!.text = "用针捅小孔，连续按设备组网键3次"
                manager_room_txt!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onEvent() {}
    override fun onData() {}
    override fun onClick(v: View) {
        var intent: Intent? = null
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_id -> when (type) {
                "101", "103" -> {
//                    intent = Intent(this@AddWifiDevActivity, ConnWifiCameraActivity::class.java)
//                    intent.putExtra("type", type)
//                    startActivity(intent)
                    intent = Intent(this@AddWifiDevActivity, SoundCameraTypeSelectActivity::class.java)
                    intent.putExtra("type", type)
                    startActivity(intent)
                }
                "hongwai" -> {
                    intent = Intent(this@AddWifiDevActivity, ConnWifiActivity::class.java)
                    startActivity(intent)
                }
                "A2A1", "A2A2", "A2A3", "A2A4" , "ADA1", "ADA2", "ADA3","ADB1","ADB2","ADB3"-> {
                    intent = Intent(this@AddWifiDevActivity, ConnApWifiActivity::class.java)
                    startActivity(intent)
                }
                "102" -> {
                }
                "yaokong" -> {
                }
            }
            R.id.manager_room_txt -> {
                intent = Intent(this@AddWifiDevActivity, AddApModeWifiDevActivity::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
            }
        }
    }
}