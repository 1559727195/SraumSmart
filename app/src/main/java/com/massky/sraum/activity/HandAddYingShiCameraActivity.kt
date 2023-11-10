package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.Util.DialogUtil
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/1/18.
 */
class HandAddYingShiCameraActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.complute_setting)
    var complute_setting: TextView? = null

    @JvmField
    @BindView(R.id.edit_dev_serial_number)
    var edit_dev_serial_number: ClearEditText? = null

    @JvmField
    @BindView(R.id.edit_dev_yanzhengma)
    var edit_dev_yanzhengma: ClearEditText? = null
    private var dialogUtil: DialogUtil? = null
    var yanzhengma: String? = null
    var srial_number: String? = null
    override fun viewId(): Int {
        return R.layout.hand_add_yingshi_camera_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this)
        //        String account_id = getIntent().getStringExtra("account_id");
//        if (account_id != null) {
//            edit_nicheng.setText(account_id);
//        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        complute_setting!!.setOnClickListener(this)
    }

    override fun onData() {
        yanzhengma = intent.getStringExtra("yanzhengma")
        srial_number = intent.getStringExtra("srial_number")
        if (yanzhengma != null) {
            edit_dev_yanzhengma!!.setText(yanzhengma)
            edit_dev_serial_number!!.setText(srial_number)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.complute_setting -> {
                val yanzhengma = edit_dev_yanzhengma!!.text.toString()
                //                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("account_id", account);
//                intent.putExtras(bundle);
//                this.setResult(RESULT_OK, intent);
//                AccountIdActivity.this.finish();
                if (yanzhengma == "") {
                    ToastUtil.showToast(this@HandAddYingShiCameraActivity, "设备验证码为空")
                    return
                }
                val serial_number = edit_dev_serial_number!!.text.toString()
                if (serial_number == "") {
                    ToastUtil.showToast(this@HandAddYingShiCameraActivity, "设备序列号为空")
                    return
                }
                this.yanzhengma = yanzhengma
                srial_number = serial_number
                val map = HashMap<Any?, Any?>()
                map["type"] = "104"
                map["strMac"] = yanzhengma
                map["strDeviceID"] = srial_number
                map["user"] = yanzhengma
                map["password"] = yanzhengma
                map["wifi"] = yanzhengma
                map["name"] = "新摄像头"
                val intent = Intent(this@HandAddYingShiCameraActivity,
                        AddWifiCameraScucessActivity::class.java)
                intent.putExtra("wificamera", map as Serializable)
                startActivity(intent)
            }
        }
    }
}