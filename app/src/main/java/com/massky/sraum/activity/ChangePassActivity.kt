package com.massky.sraum.activity

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import butterknife.BindView
import com.alibaba.fastjson.JSON
import com.data.LoginService
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Created by zhu on 2018/1/18.
 */
class ChangePassActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    private var dialogUtil: DialogUtil? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null

    @JvmField
    @BindView(R.id.input_pass_old)
    var input_pass_old: ClearEditText? = null

    @JvmField
    @BindView(R.id.input_pass_new)
    var input_pass_new: ClearEditText? = null

    @JvmField
    @BindView(R.id.input_pass_again)
    var input_pass_again: ClearEditText? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway)
    var eyeimageview_id_gateway: ImageView? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway_one)
    var eyeimageview_id_gateway_one: ImageView? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway_two)
    var eyeimageview_id_gateway_two: ImageView? = null
    private var eyeUtil: EyeUtil? = null
    private var eyeUtil_1: EyeUtil? = null
    private var eyeUtil_2: EyeUtil? = null
    override fun viewId(): Int {
      //  window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return R.layout.chag_pass_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this)
        btn_login_gateway!!.setOnClickListener(this)
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftInputMethod(act: Activity) {
        val view: View = act.getWindow().peekDecorView()
        if (view != null) {
            // 隐藏虚拟键盘
            val inputmanger: InputMethodManager = act
                    .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputmanger.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        eyeimageview_id_gateway!!.setOnClickListener(this)
        eyeUtil = EyeUtil(this@ChangePassActivity, eyeimageview_id_gateway, input_pass_old, true)
        eyeimageview_id_gateway_one!!.setOnClickListener(this)
        eyeUtil_1 = EyeUtil(this@ChangePassActivity, eyeimageview_id_gateway_one, input_pass_new, true)
        eyeimageview_id_gateway_two!!.setOnClickListener(this)
        eyeUtil_2 = EyeUtil(this@ChangePassActivity, eyeimageview_id_gateway_two, input_pass_again, true)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.btn_login_gateway -> init_chang_pass()
            R.id.eyeimageview_id_gateway -> eyeUtil!!.EyeStatus()
            R.id.eyeimageview_id_gateway_one -> eyeUtil_1!!.EyeStatus()
            R.id.eyeimageview_id_gateway_two -> eyeUtil_2!!.EyeStatus()
        }
    }

    /**
     * 改变密码
     */
    private fun init_chang_pass() {
        hideSoftInputMethod(ChangePassActivity@this)
        if (input_pass_old!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ChangePassActivity, "旧密码为空")
            return
        }
        if (input_pass_new!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ChangePassActivity, "新密码为空")
            return
        }
        if (input_pass_again!!.text.toString().trim { it <= ' ' } == "") {
            ToastUtil.showToast(this@ChangePassActivity, "确认密码为空")
            return
        }
        if (input_pass_new!!.text.toString().trim { it <= ' ' } != input_pass_again!!.text.toString().trim { it <= ' ' }) {
            ToastUtil.showToast(this@ChangePassActivity, "两次输入密码不一致")
            return
        }
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this@ChangePassActivity)
        map["oldPwd"] = input_pass_old!!.text.toString().trim { it <= ' ' }
        map["newPwd"] = input_pass_new!!.text.toString().trim { it <= ' ' }
        retrofit_post_json(map as HashMap<String, Any>, map["newPwd"] as String)
    }

    /**
     * retrofit发送json数据
     *
     * @param maptwo
     */
    private fun retrofit_post_json(maptwo: Map<String, Any>, pwd: String?) {
        val obj = JSON.toJSONString(maptwo)
        val retrofit = Retrofit.Builder().baseUrl(ApiHelper.api).build()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj)
        val login = retrofit.create(LoginService::class.java)
        val data = login.sraum_setPwd(body)
        data.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var user: User? = null
                try {
                    user = JSON.parseObject(response.body().string(), User::class.java)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                when (user!!.result) {
                    "100" -> {
                        SharedPreferencesUtil.saveData(this@ChangePassActivity, "loginPassword", pwd) //保存密码
                        ToastUtil.showToast(this@ChangePassActivity, "修改成功")
                        finish()
                    }
                    "101" -> ToastUtil.showToast(this@ChangePassActivity, "token 错误")
                    "102" -> ToastUtil.showToast(this@ChangePassActivity, "旧密码错误")
                    "103" -> ToastUtil.showToast(this@ChangePassActivity, "新密码不正确")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }
}