package com.massky.sraum

import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.SearchGateWayActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import java.util.*

class EditGateWayResultActivity : BaseActivity() {
    private var gateid: String? = null

    @JvmField
    @BindView(R.id.gatedditexttwo)
    var gatedditexttwo: ClearEditText? = null

    @JvmField
    @BindView(R.id.standard)
    var standard: TextView? = null

    @JvmField
    @BindView(R.id.save_btn)
    var save_btn: Button? = null
    private var dialogUtil: DialogUtil? = null
    private var areaNumber: String? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    override fun viewId(): Int {
        return R.layout.editgateway_result
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
        gateid = intent.getSerializableExtra("gateid") as String
        standard!!.text = gateid
        areaNumber = SharedPreferencesUtil.getData(this@EditGateWayResultActivity, "areaNumber", "") as String
    }

    override fun onEvent() {
        save_btn!!.setOnClickListener(this)
        back!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.save_btn -> {

                val boxPwd = gatedditexttwo!!.text.toString()
                if (boxPwd == "") {
                    if(gatedditexttwo!!.hasFocusChange()) {
                        val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                            if (mInputMethodManager != null)
                                mInputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                        }
                    }
                    ToastUtil.showDelToast(this@EditGateWayResultActivity, "网关密码不能为空")
                } else {
                    addBox(gateid, "123", boxPwd)
                }
            }
            R.id.back -> finish()
        }
    }

    private fun addBox(boxNumber: String?, boxName: String, boxPwd: String) {
        addgateway_act(boxNumber, boxName, boxPwd)
    }

    private fun addgateway_act(boxNumber: String?, boxName: String, boxPwd: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["token"] = TokenUtil.getToken(this@EditGateWayResultActivity)
        map["boxNumber"] = boxNumber
        map["areaNumber"] = areaNumber
        map["boxName"] = boxName
        map["boxPwd"] = boxPwd
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_addBox, map, object : Mycallback(AddTogglenInterfacer { addgateway_act(boxNumber, boxName, boxPwd) }, this@EditGateWayResultActivity, dialogUtil) {
            override fun threeCode() {
                super.threeCode()
                ToastUtil.showDelToast(this@EditGateWayResultActivity, "网关ID 错误")
            }

            override fun fourCode() {
                super.fourCode()
                ToastUtil.showDelToast(this@EditGateWayResultActivity, "网关密码 错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showDelToast(this@EditGateWayResultActivity, """
     区域编号
     错误
     """.trimIndent())
            }

            override fun fiveCode() {
                super.fiveCode()
                ToastUtil.showDelToast(this@EditGateWayResultActivity, "网关已存在")
            }

            override fun onSuccess(user: User) {
                super.onSuccess(user)
                SharedPreferencesUtil.saveData(this@EditGateWayResultActivity, "boxnumber", boxNumber)
                //                AppManager.getAppManager().finishActivity(SearchGateWayActivity.class);
                AppManager.getAppManager().finishActivity_current(SearchGateWayActivity::class.java)
                finish()
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }
}