package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView

/**
 * Created by zhu on 2018/1/17.
 */
class SoundCameraTypeSelectActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.new_voice_rel)
    var new_voice_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.old_voice_rel)
    var old_voice_rel: RelativeLayout? = null

    //scan_qrcode_rel
    @JvmField
    @BindView(R.id.scan_qrcode_rel)
    var scan_qrcode_rel: RelativeLayout? = null


    override fun viewId(): Int {
        return R.layout.camera_mode_sel_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        new_voice_rel!!.setOnClickListener(this)
        old_voice_rel!!.setOnClickListener(this)
        scan_qrcode_rel!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        var intent: Intent? = null
        when (v.id) {
            R.id.back -> finish()
            R.id.new_voice_rel -> {
                intent = Intent(this@SoundCameraTypeSelectActivity, ConnWifiCameraActivity::class.java)
                intent.putExtra("type", getIntent().getStringExtra("type"))
                intent.putExtra("mode", "new")
                startActivity(intent)
            }
            R.id.old_voice_rel -> {
                intent = Intent(this@SoundCameraTypeSelectActivity, ConnWifiCameraActivity::class.java)
                intent.putExtra("type", getIntent().getStringExtra("type"))
                intent.putExtra("mode", "old")
                startActivity(intent)
            }
            R.id.scan_qrcode_rel-> {
                intent = Intent(this@SoundCameraTypeSelectActivity, ConnWifiCameraActivity::class.java)
                intent.putExtra("type", getIntent().getStringExtra("type"))
                intent.putExtra("mode", "scan")
                startActivity(intent)
            }
        }
    }
}