package com.massky.sraum.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.iflytek.sunflower.FlowerCollector
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Utils.AppManager

/**
 * Created by zhu on 2017/7/18.
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {
    @JvmField
    var savedInstanceState: Bundle? = null
    private val TAG = "lazy"

    /**
     * 绑定 ButterKnife 时返回的 Unbinder ，用于 ButterKnife 解绑
     */
    private var mUnbinder: Unbinder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewId())
        this.savedInstanceState = savedInstanceState
        AppManager.getAppManager().addActivity(this) //添加activity
        //        ButterKnife.inject(this);
        mUnbinder = ButterKnife.bind(this)
        isDestroy = false
        onView()
        onEvent()
        onData()
        Log.e(TAG, "onCreate: a$this")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart: a$this")
    }

    /**
     * 手势密码两种状态（点击home键和手机屏幕状态进行判定）
     */
    override fun onRestart() {
        super.onRestart()
    }

    override fun onPause() {
        isForegrounds = false
        // 开放统计 移动数据统计分析
        var firstRegister: Boolean = SharedPreferencesUtil.getData(this,
                "firstRegister", false) as Boolean

        if (firstRegister) {
            Log.e(TAG, "onPause: "+ "FlowerCollector" )
            FlowerCollector.onPageEnd(TAG)
            FlowerCollector.onPause(this)
        }
        super.onPause()
        Log.e(TAG, "onPause: a$this")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: a$this")
    }

    // onDestroyView
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (null != this.currentFocus) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                return mInputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
        return super.onTouchEvent(event)
    }

    protected abstract fun viewId(): Int
    override fun onResume() {
        isForegrounds = true
        // 开放统计 移动数据统计分析
        var firstRegister: Boolean = SharedPreferencesUtil.getData(this,
                "firstRegister", false) as Boolean

        if (firstRegister) {
            Log.e(TAG, "onResume: "+ "FlowerCollector" )
            FlowerCollector.onResume(this@BaseActivity)
            FlowerCollector.onPageStart(TAG)
        }
        super.onResume()
        Log.e(TAG, "onResume: a$this")
    }

    protected abstract fun onView()
    protected abstract fun onEvent()
    protected abstract fun onData()

    /**
     * 取消广播状态
     */
    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
        if (mUnbinder != null && mUnbinder !== Unbinder.EMPTY) {
            mUnbinder!!.unbind()
        }
        mUnbinder = null
        Log.e(TAG, "onDestroy: a$this")
    }

    companion object {
        @JvmField
        var isForegrounds = false
        var isDestroy = false
    }
}