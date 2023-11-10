package com.massky.sraum.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.Util.DialogUtil
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView

/**
 * Created by zhu on 2017/8/25.
 */
class YinSiActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.wvMain)
    var wvMain: WebView? = null

    @JvmField
    @BindView(R.id.pbMain)
    var pbMain: ProgressBar? = null//

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    var dialogUtils:DialogUtil? = null

    var action: String? = null
    private var _url = "http://app.sraum.com/sraumApp/yinsi/index.html"
    override fun viewId(): Int {
        return R.layout.yinsi_act
    }

    override fun onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) { // Dark font for StatusBar.
            statusView!!.setBackgroundColor(Color.BLACK)
        }

        dialogUtils = DialogUtil(this@YinSiActivity)
        dialogUtils!!.loadDialog()

        action = getIntent().getStringExtra("yinsi")
        when(action) {
            "yinsi"-> {
                _url = "http://app.sraum.com/sraumApp/yinsi/index.html"
            }
            "person"-> {
                _url = "https://app.sraum.com/sraumApp/useragreement/"
            }
        }

        StatusUtils.setFullToStatusBar(this)
        wvMain!!.getSettings().setJavaScriptEnabled(true)
        wvMain!!.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // TODO Auto-generated method stub
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {//
                super.onPageFinished(view, url)
                dialogUtils!!.removeDialog()
            }

        })
        wvMain!!.loadUrl(_url)
        back!!.setOnClickListener(this)
    }

    override fun onEvent() {}
    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }
}