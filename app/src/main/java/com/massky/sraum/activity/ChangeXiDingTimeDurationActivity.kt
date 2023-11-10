package com.massky.sraum.activity

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import com.AddTogenInterface.AddTogglenInterfacer
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/18.
 */
class ChangeXiDingTimeDurationActivity : BaseActivity() {
    private var number: String? = null
    private var name2: String? = null
    private var name1: String? = null

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
    @BindView(R.id.edit_hasPersonInterval)
    var edit_hasPersonInterval: ClearEditText? = null

    @JvmField
    @BindView(R.id.edit_noPersonInterval)
    var edit_noPersonInterval: ClearEditText? = null
    private var dialogUtil: DialogUtil? = null
    var xiding_map = HashMap<String, Any>()
    override fun viewId(): Int {
        return R.layout.change_xiding_time_duration_act
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
        xiding_map = intent.getSerializableExtra("xiding_map") as HashMap<String, Any>
        if (xiding_map != null) {
            name1 = xiding_map.get("name1") as String
            name2 = xiding_map.get("name1") as String
            number = xiding_map.get("number") as String
            if (name1 != null)
                edit_hasPersonInterval!!.setText(name1)
            if (name2 != null)
                edit_noPersonInterval!!.setText(name2)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.complute_setting -> {
                val hasPerson = edit_hasPersonInterval!!.text.toString()
                //                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("account_id", account);
//                intent.putExtras(bundle);
//                this.setResult(RESULT_OK, intent);
//                AccountIdActivity.this.finish();
                if (hasPerson == "") {
                    ToastUtil.showToast(this@ChangeXiDingTimeDurationActivity, "有人间隔为空")
                    return
                }
                val noPerson = edit_noPersonInterval!!.text.toString()
                if (noPerson == "") {
                    ToastUtil.showToast(this@ChangeXiDingTimeDurationActivity, "无人间隔为空")
                    return
                }
                name1 = hasPerson
                name2 = noPerson
                change_xiding_interval()
            }
        }
    }

    private fun change_xiding_interval() {
        val map = HashMap<Any?, Any?>()

        var areaNumber = SharedPreferencesUtil.getData(this@ChangeXiDingTimeDurationActivity,
                "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@ChangeXiDingTimeDurationActivity)
        map["areaNumber"] = areaNumber
        map["number"] = number
        map["hasPersonInterval"] = name1
        map["noPersonInterval"] = name2
        dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_setInductionInterval, map as HashMap<String, Any>,
                object : Mycallback(AddTogglenInterfacer
                //刷新togglen获取新数据
                { change_xiding_interval() }, this@ChangeXiDingTimeDurationActivity, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        finish()
                    }

                    override fun onSuccess(user: User) {
                        this@ChangeXiDingTimeDurationActivity.finish()
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                        ToastUtil.showToast(this@ChangeXiDingTimeDurationActivity, """
         areaNumber 不正
         确
         """.trimIndent())
                    }

                    override fun threeCode() {
                        super.threeCode()
                        ToastUtil.showToast(this@ChangeXiDingTimeDurationActivity,
                                "number 不存在")
                    }


                    override fun fourCode() {
                        super.fourCode()
                        ToastUtil.showToast(this@ChangeXiDingTimeDurationActivity,
                                "设置失败")

                    }
                })
    }
}