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
class SelectWindowPowerMechineActivity : BaseActivity() {
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

    private var dialogUtil: DialogUtil? = null
    private var map: Map<*, *> = HashMap<Any?, Any?>()
    override fun viewId(): Int {
        return R.layout.select_window_power_machine_act
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
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                //                SelectSmartDoorLockTwoActivity.this.finish();
                finish()
            }
            R.id.next_step_id -> {
                sraum_setBox_accent()
            }
        }
    }

    private fun init_intent() {
        var intent: Intent? = null
        intent = Intent(this@SelectWindowPowerMechineActivity, AddDoorLockDevActivity::class.java)
        intent.putExtra("map", getIntent().getSerializableExtra("map"))
        startActivity(intent)
    }

    private fun sraum_setBox_accent() {
        val status = map["status"] as String?
        val gateway_number = map["gateway_number"] as String?
        val areaNumber = SharedPreferencesUtil.getData(this@SelectWindowPowerMechineActivity, "areaNumber", "") as String
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        //        String phoned = getDeviceId(SelectZigbeeDeviceActivity.this);
        map["token"] = TokenUtil.getToken(this@SelectWindowPowerMechineActivity)
        map["boxNumber"] = gateway_number
        val regId = SharedPreferencesUtil.getData(this@SelectWindowPowerMechineActivity, "regId", "") as String
        map["regId"] = regId
        map["status"] = status
        map["areaNumber"] = areaNumber
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { sraum_setBox_accent() }, this@SelectWindowPowerMechineActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                init_intent()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                ToastUtil.showToast(this@SelectWindowPowerMechineActivity,
                        "该网关不存在")
            }
        }
        )
    }


    override fun onBackPressed() {
//        SelectSmartDoorLockTwoActivity.this.finish();
        finish()
    }
}