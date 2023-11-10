package com.massky.sraum.activity

import android.Manifest
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import cn.jpush.android.api.JPushInterface
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.data.LoginService
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.App
import com.massky.sraum.activity.MainGateWayActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.tool.Constants
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.util.*

/**
 * Created by zhu on 2017/12/29.
 */
class LoginCloudActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.toolbar_txt)
    var toolbar_txt: TextView? = null

    @JvmField
    @BindView(R.id.scan_gateway)
    var scan_gateway: TextView? = null

    @JvmField
    @BindView(R.id.search_gateway_btn)
    var search_gateway_btn: TextView? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway)
    var eyeimageview_id_gateway: ImageView? = null
    private var eyeUtil: EyeUtil? = null

    @JvmField
    @BindView(R.id.regist_new)
    var regist_new: TextView? = null

    @JvmField
    @BindView(R.id.forget_pass)
    var forget_pass: TextView? = null

    @JvmField
    @BindView(R.id.usertext_id)
    var usertext_id: ClearEditText? = null

    @JvmField
    @BindView(R.id.phonepassword)
    var phonepassword: ClearEditText? = null
    private var dialogUtil: DialogUtil? = null
    private var token: String? = null
    override fun viewId(): Int {
        return R.layout.login_cloud
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        if (toolbar_txt != null) {
            toolbar_txt!!.text = "登录网关"
            scan_gateway!!.setOnClickListener(this)
            clear_hository_first_page_datas()
        }
    }

    /**
     * 清空首页首页区域，房间，设备历史信息
     */
    private fun clear_hository_first_page_datas() {
        //areaList，roomList，list，list_old，roomList_old，areaList_old
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "list", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "list_old", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList_old", ArrayList())
        SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList_old", ArrayList())
    }

    override fun onEvent() {
        if (back != null) {
            back!!.setOnClickListener(this)
            search_gateway_btn!!.setOnClickListener(this)
            btn_login_gateway!!.setOnClickListener(this)
            eyeimageview_id_gateway!!.setOnClickListener(this)
            eyeUtil = EyeUtil(this@LoginCloudActivity, eyeimageview_id_gateway, phonepassword, true)
            forget_pass!!.setOnClickListener(this)
            regist_new!!.setOnClickListener(this)
        }
    }

    override fun onData() {
        val loginPhone = SharedPreferencesUtil.getData(this@LoginCloudActivity, "loginPhone", "") as String
        if (loginPhone != null) {
            if (usertext_id == null) return
            usertext_id!!.setText(loginPhone)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.scan_gateway -> {
                init_permissions()
            }
            R.id.search_gateway_btn -> startActivity(Intent(this@LoginCloudActivity, LoginGateWayActivity::class.java))
            R.id.btn_login_gateway ->                 //开始登录
//                startActivity(new Intent(LoginCloudActivity.this, MainGateWayActivity.class));
                login()
            R.id.eyeimageview_id_gateway -> {
                if (eyeUtil == null) return
                eyeUtil!!.EyeStatus()
            }
            R.id.regist_new -> {
                //                startActivity(new Intent(LoginCloudActivity.this, RegistActivity.class));
                if (usertext_id == null) return
                val registerphone = usertext_id!!.text.toString()
                IntentUtil.startActivity(this@LoginCloudActivity, RegistActivity::class.java, "registerphone", registerphone)
            }
            R.id.forget_pass -> {
                //                startActivity(new Intent(LoginCloudActivity.this, ForgetActivity.class));
                if (usertext_id == null) return
                val findonepho = usertext_id!!.text.toString()
                IntentUtil.startActivity(this@LoginCloudActivity, ForgetActivity::class.java, "registerphone", findonepho)
            }
        }
    }

    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(LoginCloudActivity@this)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                val openCameraIntent = Intent(this@LoginCloudActivity, CaptureActivity::class.java)
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE)
            }
        })
    }


    /**
     * 登录方法
     */
    private fun login() {
        if (usertext_id == null) return
        val loginAccount = usertext_id!!.text.toString()
        val pwd = phonepassword!!.text.toString()
        if (loginAccount == "" || pwd == "") {
            ToastUtil.showDelToast(this@LoginCloudActivity, "用户名或密码不能为空")
        } else {
            val time = Timeuti.getTime()
            val maptwo: MutableMap<String, Any> = HashMap()
            maptwo["loginAccount"] = loginAccount
            maptwo["timeStamp"] = time
            maptwo["signature"] = MD5Util.md5(loginAccount + pwd + time)
            LogUtil.eLength("传入时间戳", JSON.toJSONString(maptwo) + "时间戳" + time)
            dialogUtil!!.loadDialog()
            get_token(maptwo)
        } //init_login (?->maptwo)
    }

    /**
     * retrofit发送json数据
     * @param maptwo
     */
    private fun retrofit_post_json(maptwo: Map<String, Any>) {
        val obj = JSON.toJSONString(maptwo)
        val retrofit = Retrofit.Builder().baseUrl(ApiHelper.api).build()
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj)
        val login = retrofit.create(LoginService::class.java)
    }

    /**
     * 获取token
     *
     * @param maptwo
     */
    private fun get_token(maptwo: Map<String, Any>) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_getToken, maptwo, object : MycallbackNest(AddTogglenInterfacer { }, this@LoginCloudActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@LoginCloudActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                val map = getStringObjectMap(user) ?: return
                init_login(map)
            }

            override fun wrongToken() {
                super.wrongToken() //继承父类，实现父类的方法
                ToastUtil.showDelToast(this@LoginCloudActivity, "登录失败，账号未注册")
            }

            override fun wrongBoxnumber() {
                ToastUtil.showDelToast(this@LoginCloudActivity, "用户名或密码错误")
            }
        })
    }

    /**
     * 保存并传值
     *
     * @param user
     * @return
     */
    private fun getStringObjectMap(user: User): Map<String, Any>? {
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "loginPassword", phonepassword!!.text.toString()) //保存密码
        token = user.token
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "tokenTime", true)
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "sraumtoken", user.token)
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "expires_in", user.expires_in)
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "logintime", System.currentTimeMillis().toInt())
        SharedPreferencesUtil.saveData(this@LoginCloudActivity, "tagint", 0)
        val map: MutableMap<String, Any> = HashMap()
        var regId = SharedPreferencesUtil.getData(this@LoginCloudActivity, "regId", "") as String
        if (regId == "") {
            regId = JPushInterface.getRegistrationID(this@LoginCloudActivity)
            SharedPreferencesUtil.saveData(this@LoginCloudActivity, "regId", regId)
        }
        if (regId == "") {
            ToastUtil.showToast(this@LoginCloudActivity, "数据解析错误")
            return null
        }
        map["token"] = user.token
        map["regId"] = regId
        map["phoneId"] = regId
        LogUtil.eLength("查看数据", JSON.toJSONString(map))
        return map
    }

    /**
     * 登录
     *
     * @param map
     */
    private fun init_login(map: Map<String, Any>) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_login, map, object : MycallbackNest(AddTogglenInterfacer { }, this@LoginCloudActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
                ToastUtil.showDelToast(this@LoginCloudActivity, "网络连接超时")
            }

            override fun onSuccess(user: User) {
                if(usertext_id == null) return
                if(user == null) return
                SharedPreferencesUtil.saveData(this@LoginCloudActivity, "loginPhone", usertext_id!!.text.toString())
                SharedPreferencesUtil.saveData(this@LoginCloudActivity, "avatar", user.avatar)
                SharedPreferencesUtil.saveData(this@LoginCloudActivity, "accountType", if (user.accountType == null) "" else user.accountType)
                SharedPreferencesUtil.saveData(this@LoginCloudActivity, "loginflag", true)
                if (user.userName != null && user.userName != "") {
                    SharedPreferencesUtil.saveData(this@LoginCloudActivity, "userName", user.userName)
                }
                IntentUtil.startActivityAndFinishFirst(this@LoginCloudActivity, MainGateWayActivity::class.java)
            }

            override fun wrongToken() {
                super.wrongToken() //继承父类，实现父类的方法
                ToastUtil.showDelToast(this@LoginCloudActivity, "登录失败，账号未注册")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showDelToast(this@LoginCloudActivity, "账号或密码错误")
            }
        })
    }
}