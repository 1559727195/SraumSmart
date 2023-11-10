package com.massky.sraum.activity

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.BindBtnListAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.ClearLengthEditText
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditBindBtnActivity : BaseActivity() {
    private lateinit var panelType: String
    private var edit: Boolean = false
    private lateinit var buttonIndex: String
    private lateinit var switchId: String
    private lateinit var dialogUtil: DialogUtil
    private lateinit var bindBtnListAdapter: BindBtnListAdapter
    private lateinit var panelNumber: String
    private lateinit var areaNumber: String
    private val list_hand_scene: MutableList<Map<*, *>> = ArrayList()
    private var current_map: Map<*, *> = HashMap<Any?, Any?>()


    //xListView_scan
    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: ListViewForScrollView_New? = null

    //next_step_txt
    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    //input_bind_btn_edit
    @JvmField
    @BindView(R.id.input_bind_btn_edit)
    var input_bind_btn_edit: EditText? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null


    override fun viewId(): Int {
        return R.layout.edit_bind_btn_activity
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
    }


    override fun onEvent() {
        next_step_txt!!.setOnClickListener(this)
        back!!.setOnClickListener(this)
    }

    override fun onData() {
        get_intent_data()
        process_bind_btn_loop()
        init_adapter()
    }

    private fun init_adapter() {
        bindBtnListAdapter = BindBtnListAdapter(this@EditBindBtnActivity, list_hand_scene,
                object : BindBtnListAdapter.AgainAutoSceneListener {
                    override fun again_auto_listen(position: Int) {
                        current_map = list_hand_scene[position]
                    }
                })
        xListView_scan!!.setAdapter(bindBtnListAdapter)
    }

    private fun get_intent_data() {
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        panelNumber = intent.getSerializableExtra("panelNumber") as String
        buttonIndex = intent.getSerializableExtra("button") as String
        switchId = intent.getSerializableExtra("switchId").toString()
        //panelType
        panelType = intent.getSerializableExtra("panelType") as String
        edit = intent.getSerializableExtra("edit") as Boolean
        if (switchId != null)
            input_bind_btn_edit!!.setText("外部开关" + switchId)
    }

    private fun process_bind_btn_loop() {
        var total = 8
        when (panelType) {
            "ADA1" -> {
                total = 2
            }
            "ADA2" -> {
                total = 4
            }
            "ADA3" -> {
                total = 6
            }
        }
        loop@ for (element in 1..total) {
            when (panelType) {
                "A201" -> {
                    when (element) {
                        2, 3, 4 -> continue@loop
                    }
                }
                "A202" -> {
                    when (element) {
                        3, 4 -> continue@loop
                    }
                }
                "A203" -> {
                    when (element) {
                        4 -> continue@loop
                    }
                }
            }
            val map = HashMap<Any?, Any?>()
            map["name"] = "按钮" + (element)
            map["index"] = element
            if (Integer.parseInt(buttonIndex) == (element)) {
                map["type"] = "1"
                current_map = map
            } else
                map["type"] = "0"
            list_hand_scene.add(map)
        }
    }


    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private fun sraum_addExternalSwitchRelation(switchId: String, button: String) {
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this)
        mapdevice["areaNumber"] = areaNumber
        var method: String? = null
        when (panelType) {
            "ADA1", "ADA2", "ADA3" -> {
                when (edit) {
                    false -> method = ApiHelper.sraum_addWifiExternalSwitchRelation
                    true -> method = ApiHelper.sraum_updateWifiExternalSwitchRelation
                }
            }
            else -> {
                when (edit) {
                    false -> method = ApiHelper.sraum_addExternalSwitchRelation
                    true -> method = ApiHelper.sraum_updateExternalSwitchRelation
                }
            }
        }



        mapdevice["panelNumber"] = panelNumber
        mapdevice["switchId"] = switchId
        mapdevice["button"] = button


        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_addExternalSwitchRelation(switchId, button)
        }, this@EditBindBtnActivity, dialogUtil) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                ToastUtil.showToast(this@EditBindBtnActivity, "更新失败")
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@EditBindBtnActivity, "panelNumber 不存在")
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun fourCode() {

                ToastUtil.showToast(this@EditBindBtnActivity, "修改失败")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@EditBindBtnActivity, "areaNumber\n" +
                        "错误")
            }

            override fun onSuccess(user: User) {
//                ToastUtil.showToast(this@EditBindBtnActivity, "更新成功")
                finish() //修改完毕
                //                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
//                AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity.class);
                // AppManager.getAppManager().finishActivity_current(MyDeviceItemActivity::class.java)
            }
        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_step_txt -> {
                if (current_map == null) return
                if (current_map["index"] == null) {
                    ToastUtil.showToast(this@EditBindBtnActivity, "请选择按钮")
                    return
                }

                sraum_addExternalSwitchRelation(switchId, current_map["index"].toString())
            }
            R.id.back -> finish()
        }
    }
}