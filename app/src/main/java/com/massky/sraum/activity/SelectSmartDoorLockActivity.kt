package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView

/**
 * Created by zhu on 2018/6/19.
 */
class SelectSmartDoorLockActivity : BaseActivity() {
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
    @BindView(R.id.project_select)
    var project_select: TextView? = null
    override fun viewId(): Int {
        return R.layout.select_smart_door_lock_lay
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        onEvent()
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
        wendu_linear!!.setOnClickListener(this)
        shidu_rel!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.wendu_linear -> init_intent()
            R.id.shidu_rel -> {
            }
        }
        //        init_intent();
    }

    private fun init_intent() {
        var intent: Intent? = null
        intent = Intent(this@SelectSmartDoorLockActivity, SelectSmartDoorLockTwoActivity::class.java)
        intent.putExtra("map", getIntent().getSerializableExtra("map"))
        intent.putExtra("type", getIntent().getStringExtra("type"))
        startActivity(intent)
    }
}