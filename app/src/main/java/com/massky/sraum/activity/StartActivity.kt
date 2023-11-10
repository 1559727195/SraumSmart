package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.ipcamera.demo.BridgeService
import com.massky.sraum.R
import com.massky.sraum.activity.StartActivity
import vstc2.nativecaller.NativeCaller

class StartActivity : Activity() {
    private var version: TextView? = null
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val `in` = Intent(this@StartActivity, AddCameraActivity::class.java)
            startActivity(`in`)
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "StartActivity onCreate")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.start)
        version = findViewById<View>(R.id.version) as TextView
        version!!.text = "jni_code:" + NativeCaller.GetVersion()
        val intent = Intent()
        intent.setClass(this@StartActivity, BridgeService::class.java)
        startService(intent)
        Thread {
            try {
                NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL")
                Thread.sleep(3000)
                val msg = Message()
                mHandler.sendMessage(msg)
                Log.e("vst", "path" + applicationContext.filesDir.absolutePath)
                NativeCaller.SetAPPDataPath(applicationContext.filesDir.absolutePath)
            } catch (e: Exception) {
            }
        }.start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) true else super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val LOG_TAG = "StartActivity"
    }
}