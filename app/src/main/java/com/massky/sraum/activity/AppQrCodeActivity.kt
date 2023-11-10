package com.ackpass.ackpass

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils

class AppQrCodeActivity : BaseActivity() {

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.back -> {
                AppQrCodeActivity@this.finish()
            }
        }
    }

    override fun viewId(): Int {
        return R.layout.activity_app_qr_code_layout
    }


    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        back!!.setOnClickListener(this)
    }

    override fun onEvent() {

    }

    override fun onData() {

    }

}