package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*

/**
 * Created by zhu on 2018/1/8.
 */
class AddNewMemberActivity : BaseActivity() {

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private val name: String? = null
    private var areaNumber: String? = null

    @JvmField
    @BindView(R.id.edit_password_gateway)
    var edit_password_gateway: ClearLengthEditText? = null

    //member_num_rel
    @JvmField
    @BindView(R.id.member_num_rel)
    var member_num_rel: RelativeLayout? = null

    //member_num
    @JvmField
    @BindView(R.id.member_num)
    var member_num: TextView? = null


    private var personsInfos: MutableList<Map<*, *>> = ArrayList()

    private val ADD_MEMBER = 0x101

    var personNo: String? = null
    private var deviceNumber: String?= null


    override fun viewId(): Int {
        return R.layout.add_new_member
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this) // StatusBar.
    }

    /**
     * 添加房间（APP->网关）
     */
    private fun sraum_addPersonInfo(name: String) {
        var istrue:Boolean? = false
        for (i in personsInfos.indices) {
            var personName: String? = personsInfos[i]["personName"] as String
            // roomsInfos.remove(personNo!!)
            if (personName.equals(name)) {
                istrue = true
                break
            }
        }

        if (istrue!!) {
            ToastUtil.showToast(this@AddNewMemberActivity, "人员名称已存在")
            return
        }


        val map = HashMap<Any?, Any?>()
        map["areaNumber"] = areaNumber
        map["deviceNumber"] = deviceNumber
        map["personNo"] = personNo
        map["personName"] = name
        map["token"] = TokenUtil.getToken(this@AddNewMemberActivity)


//        token：用于身份标识 其它指令都要带该访问号来标识身份信息
//        areaNumber：区域名称
//        name：房间名称
//        type：类型
        MyOkHttp.postMapObject(ApiHelper.sraum_addPersonInfo, map as Map<String, Any>,
                object : Mycallback(AddTogglenInterfacer { sraum_addPersonInfo(name) }, this@AddNewMemberActivity, null) {
                    override fun onSuccess(user: User) {
                        //"roomNumber":"1"
                        finish()
                    }

                    override fun threeCode() {
                        ToastUtil.showToast(this@AddNewMemberActivity, "deviceNumber 不正确")
                    }

                    override fun fourCode() {
                        ToastUtil.showToast(this@AddNewMemberActivity, "personNo 不正确")
                    }

                    override fun fiveCode() {
                        ToastUtil.showToast(this@AddNewMemberActivity, "personName 不\n" +
                                "正确")
                    }

                    override fun sixCode() {
                        ToastUtil.showToast(this@AddNewMemberActivity, "添加失败")
                    }
                })
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        member_num_rel!!.setOnClickListener(this)
    }

    override fun onData() {
        next_step_txt!!.setOnClickListener(this)
        val intent = intent
        areaNumber = intent.getSerializableExtra("areaNumber") as String

        personsInfos = if (intent.getSerializableExtra("personsInfos") == null) ArrayList<Map<*, *>>() else
            intent.getSerializableExtra("personsInfos") as MutableList<Map<*, *>>

        deviceNumber = if (intent.getSerializableExtra("deviceNumber") == null) ""
        else intent.getSerializableExtra("deviceNumber") as String?

        //        if (intent == null) return;
//        String excute = (String) intent.getSerializableExtra("excute");
//        switch (excute) {
//            case "auto"://自动
//                rel_scene_set.setVisibility(View.GONE);
//                break;
//            default:
//                rel_scene_set.setVisibility(View.VISIBLE);
//                break;
//        }
    }
//1
    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_step_txt -> {
                //                GuanLianSceneBtnActivity.this.finish();
                if (edit_password_gateway!!.text.toString() == "") {
                    ToastUtil.showToast(this@AddNewMemberActivity, "人员名称为空")
                    return
                }
                sraum_addPersonInfo(edit_password_gateway!!.text.toString().trim { it <= ' ' })
                //                ApplicationContext.getInstance().finishActivity(AddRoomActivity.class);
//                ApplicationContext.getInstance().finishActivity(ManagerRoomActivity.class);
                finish()
            }
            R.id.back -> finish()
            R.id.member_num_rel -> {
                val intent = Intent(this@AddNewMemberActivity, MemberNumberIdActivity::class.java)
                intent.putExtra("personsInfos", personsInfos as Serializable?)
                startActivityForResult(intent, ADD_MEMBER)
                //  startActivity(Intent(this@AddNewMemberActivity, MemberNumberIdActivity::class.java))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_MEMBER && resultCode == Activity.RESULT_OK) {
            personNo = data!!.getStringExtra("personNo")
            member_num!!.setText(personNo)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}