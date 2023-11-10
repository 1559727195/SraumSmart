package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/5/30.
 */
class SelectSmartDoorLockTwoActivity : BaseActivity() {
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
    @BindView(R.id.open_door_lock)
    var open_door_lock: TextView? = null

    @JvmField
    @BindView(R.id.open_door_lock_second)
    var open_door_lock_second: TextView? = null

    @JvmField
    @BindView(R.id.img_door_lock)
    var img_door_lock: ImageView? = null
    var contents = intArrayOf(R.string.open_door_lock, R.string.open_door_lock_txt_one, R.string.open_door_lock_txt_two, R.string.open_door_lock_txt_three
            , R.string.open_door_lock_txt_four, R.string.open_door_lock_txt_five, R.string.open_door_lock_txt_six)

    var contents_lock = intArrayOf(R.string.open_door_lock, R.string.open_door_lock_txt_one_lock, R.string.open_door_lock_txt_two_lock, R.string.open_door_lock_txt_three_lock
            , R.string.open_door_lock_txt_four_lock, R.string.open_door_lock_txt_five_lock, R.string.open_door_lock_txt_six_lock)


    var imgs = intArrayOf(R.drawable.pic_zigebee_zhinengmensuo_1_2x, R.drawable.pic_zigebee_zhinengmensuo_2_2x,
            R.drawable.pic_zigebee_zhinengmensuo_3_2x
            , R.drawable.pic_zigebee_zhinengmensuo_4_2x, R.drawable.pic_zigebee_zhinengmensuo_5_2x,
            R.drawable.pic_zigebee_zhinengmensuo_6_2x,
            R.drawable.pic_zigebee_zhinengmensuo_7_2x)

    var imgs_lock = intArrayOf(R.drawable.pic_zigebee_zhinengmensuo_1, R.drawable.pic_zigebee_zhinengmensuo_2,
            R.drawable.pic_zigebee_zhinengmensuo_3
            , R.drawable.pic_zigebee_zhinengmensuo_4, R.drawable.pic_zigebee_zhinengmensuo_5,
            R.drawable.pic_zigebee_zhinengmensuo_6
    )
    private var page_index = 0
    private var dialogUtil: DialogUtil? = null
    private var type: String? = ""
    private var map: Map<*, *> = HashMap<Any?, Any?>()
    override fun viewId(): Int {
        return R.layout.select_smart_door_two_act
    }

    override fun onView() {
        back!!.setOnClickListener(this)
        next_step_id!!.setOnClickListener(this)
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        back!!.setOnClickListener(this)
    }

    override fun onEvent() {}
    override fun onData() {
        map = intent.getSerializableExtra("map") as Map<*, *>
        type = intent.getStringExtra("type")
        common_item()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                //                SelectSmartDoorLockTwoActivity.this.finish();
                page_index--
                if (page_index < 0) {
                    page_index = 0
                    finish()
                }
                common_item()
            }
            R.id.next_step_id -> {
                page_index++
                when (type) {
                    "B201" -> {
                        if (page_index > 6) {
                            page_index = 6
                            sraum_setBox_accent()
                        }
                    }
                    "智能门锁"->{
                        if (page_index > 5) {
                            page_index = 5
                            sraum_setBox_accent()
                        }
                    }
                }
                common_item()
            }
        }
    }

    private fun init_intent() {
        var intent: Intent? = null
        intent = Intent(this@SelectSmartDoorLockTwoActivity, AddDoorLockDevActivity::class.java)
        intent.putExtra("map", getIntent().getSerializableExtra("map"))
        startActivity(intent)
    }

    private fun sraum_setBox_accent() {
        val status = map["status"] as String?
        val gateway_number = map["gateway_number"] as String?
        val areaNumber = SharedPreferencesUtil.getData(this@SelectSmartDoorLockTwoActivity, "areaNumber", "") as String
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        //        String phoned = getDeviceId(SelectZigbeeDeviceActivity.this);
        map["token"] = TokenUtil.getToken(this@SelectSmartDoorLockTwoActivity)
        map["boxNumber"] = gateway_number
        val regId = SharedPreferencesUtil.getData(this@SelectSmartDoorLockTwoActivity, "regId", "") as String
        map["regId"] = regId
        map["status"] = status
        map["areaNumber"] = areaNumber
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { sraum_setBox_accent() }, this@SelectSmartDoorLockTwoActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                init_intent()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@SelectSmartDoorLockTwoActivity,
                        "该网关不存在")
            }
        }
        )
    }

    private fun common_item() {
        when (type) {
            "B201" -> {
                when (page_index) {
                    0 -> {
                        open_door_lock_second!!.visibility = View.VISIBLE
                        open_door_lock_second!!.setText(R.string.open_door_lock_txt)
                    }
                    1 -> open_door_lock_second!!.visibility = View.GONE
                    2 -> open_door_lock_second!!.visibility = View.GONE
                    3 -> open_door_lock_second!!.visibility = View.GONE
                    4 -> open_door_lock_second!!.visibility = View.GONE
                    5 -> open_door_lock_second!!.visibility = View.GONE
                    6 -> open_door_lock_second!!.visibility = View.GONE
                }
                open_door_lock!!.setText(contents[page_index])
                img_door_lock!!.setImageResource(imgs[page_index])
            }
            "智能门锁" -> {
                when (page_index) {
                    0 -> {
                        open_door_lock_second!!.visibility = View.VISIBLE
                        open_door_lock_second!!.setText(R.string.open_door_lock_txt)
                    }
                    1 -> open_door_lock_second!!.visibility = View.GONE
                    2 -> open_door_lock_second!!.visibility = View.GONE
                    3 -> open_door_lock_second!!.visibility = View.GONE
                    4 -> open_door_lock_second!!.visibility = View.GONE
                    5 -> open_door_lock_second!!.visibility = View.GONE
                }
                open_door_lock!!.setText(contents_lock[page_index])
                img_door_lock!!.setImageResource(imgs_lock[page_index])
            }
        }
    }

    override fun onBackPressed() {
//        SelectSmartDoorLockTwoActivity.this.finish();
        page_index--
        if (page_index < 0) {
            page_index = 0
            finish()
        }
        common_item()
    }
}