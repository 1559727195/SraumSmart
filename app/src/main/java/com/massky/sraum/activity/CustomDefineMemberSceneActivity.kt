package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.*
import butterknife.BindView
import com.google.gson.Gson
import com.massky.sraum.R
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.adapter.MemberSceneListNewAdapter
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/9.
 */
class CustomDefineMemberSceneActivity : BaseActivity() {

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: ListView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null

    //var again_elements = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    //private var list: MutableList<TimeSelectBean>? = null
    private var list_hand_scene: MutableList<Map<*, *>>? = null

    // private var againAutoSceneAdapter: AgainAutoDaysSelectSceneAdapter? = null
    private var list_select: MutableList<Map<*, *>> = ArrayList()

    private var personsInfos: MutableList<Map<*, *>> = ArrayList()
    private var areaNumber: String? = null
    private var deviceNumber: String? = null

    private var action: String? = ""
    private var managerpersonadapter: MemberSceneListNewAdapter? = null

    //private var list_select: MutableList<Map<*, *>> = ArrayList()

    override fun viewId(): Int {
        return R.layout.custom_define_member_scene_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
//        list_hand_scene = ArrayList()
//        list = ArrayList()
        list_select = ArrayList()
//        for (element in again_elements) {
//            val map= HashMap<Any?, Any?>()
//            map["name"] = element
//            when (element) {
//                "周一" -> map["time"] = 2
//                "周二" -> map["time"] = 3
//                "周三" -> map["time"] = 4
//                "周四" -> map["time"] = 5
//                "周五" -> map["time"] = 6
//                "周六" -> map["time"] = 7
//                "周日" -> map["time"] = 1
//            }
//            (list_hand_scene as ArrayList<Map<*, *>>).add(map)
//        }
        //againAutoSceneAdapter = AgainAutoDaysSelectSceneAdapter(this@CustomDefineMemberSceneActivity, list_hand_scene)

        personsInfos = intent.getSerializableExtra("personsInfos") as MutableList<Map<*, *>>
        list_select = intent.getSerializableExtra("list_select") as MutableList<Map<*, *>>
        //authType = intent.getSerializableExtra("authType") as String
        action = if (intent.getSerializableExtra("action") == null) ""
        else intent.getSerializableExtra("action") as String?

        if (list_select[0] != null && list_select[0]["content"]!!.equals("所有人")) {
            (personsInfos[0] as HashMap<String, Any>)["check"] = true
            (list_select[0] as HashMap<String, Any>)["check"] = true
        }

        Log.e("member_scene", "list_select: " + Gson().toJson(list_select))
        Log.e("member_scene", "personsInfos: " + Gson().toJson(personsInfos))
//        managerpersonadapter!!.setList(personsInfos as List<Any?>?)
//        managerpersonadapter!!.setSelectList(list_select)
//        managerpersonadapter!!.setAction(action)
//        managerpersonadapter!!.setAreaNumber("")
//
//        //deviceNumber
//        managerpersonadapter!!.setDeviceNumber("")
//        managerpersonadapter!!.notifyDataSetChanged()


        managerpersonadapter = MemberSceneListNewAdapter(this@CustomDefineMemberSceneActivity,
                personsInfos, list_select, action, object : MemberSceneListNewAdapter.RefreshListener {
            override fun refresh() {
                // get_allpersoninfo()
            }

            override fun back_persons(list_select_new: MutableList<Map<*, *>>) {
                list_select = list_select_new
//                if (list_select.size <= 1) {
//                    for (i in personsInfos.indices) {
//                        when (action) {
//                            "edit" -> {
//                                if (personsInfos[i]["sign"].toString().equals("0")) {
//                                    //  (list[i] as HashMap<String, Any>)["status"]
//                                    (personsInfos[i] as HashMap<String, Any>)["sign"] = "1"
//                                }
//                            }
//
//                            "add" -> {
//                                if (personsInfos[i]["sign"].toString().equals("3")) {
//                                    //  (list[i] as HashMap<String, Any>)["status"]
//                                    (personsInfos[i] as HashMap<String, Any>)["sign"] = "0"
//                                }
//                            }
//                        }
//                    }

//                    var map_person = HashMap<Any?, Any?>()
//                    map_person["content"] = "所有人"
//                    map_person["personNo"] = ""
//                    map_person["personName"] = ""
//                    map_person["sign"] = ""
//                    list_select.add(map_person)
                //  managerpersonadapter!!.notifyDataSetChanged()
//                    Log.e("member_scene", "list_select: " + Gson().toJson(list_select))
//                    Log.e("member_scene", "personsInfos: " + Gson().toJson(personsInfos))

            }

            override fun back_persons_list(personsInfos_new: MutableList<Map<*, *>>) {
                personsInfos = personsInfos_new
//                managerpersonadapter!!.setSelectList(list_select)
//                managerpersonadapter!!.notifyDataSetChanged()
            }
        })

        xListView_scan!!.adapter = managerpersonadapter
        //xListView_scan!!.onItemClickListener = this
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        next_step_txt!!.setOnClickListener(this)
    }

    override fun onData() {
        onData_Member()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> next_step()
        }
    }


    override fun onResume() {
        super.onResume()
        //get_allpersoninfo()
    }

    fun onData_Member() {

    }

//    private fun get_allpersoninfo() {
//        //获取网关名称（APP->网关）
//        val map = HashMap<Any?, Any?>()
//        map["areaNumber"] = areaNumber!!
//        map["deviceNumber"] = deviceNumber!!
//        map["token"] = TokenUtil.getToken(this@CustomDefineMemberSceneActivity)
//        MyOkHttp.postMapObject(ApiHelper.sraum_getRelatePerson, map as Map<String, Object>,
//                object : Mycallback(AddTogglenInterfacer { get_allpersoninfo() }, this@CustomDefineMemberSceneActivity, null) {
//                    override fun onSuccess(user: User) {
////                        project_select.setText(user.name);
//                        personsInfos = ArrayList()
//
//                        var map = HashMap<Any?, Any?>()
//                        map["content"] = "所有人"
//                        map["personNo"] = ""
//                        map["personName"] = ""
//                        map["sign"] = ""
//                        personsInfos.add(map)
//
//                        for (i in user.personList.indices) {
//                            val map = HashMap<Any?, Any?>()
//                            map["content"] = user.personList[i].personNo + "-" + user.personList[i].personName
//                            map["personNo"] = user.personList[i].personNo
//                            map["personName"] = user.personList[i].personName
//                            map["sign"] = user.personList[i].sign
//                            personsInfos.add(map)
//                            if (user.personList[i].sign.equals("1"))
//                                list_select.add(map)
//
//                        }
//                        //  project_select!!.text = "人员列表" + "(" + user.roomList.size + ")"
//
//
//                        managerpersonadapter!!.setList(personsInfos as List<Any?>?)
//                        managerpersonadapter!!.setSelectList(list_select)
//                        managerpersonadapter!!.setAreaNumber(areaNumber)
//
//                        //deviceNumber
//                        managerpersonadapter!!.setDeviceNumber(deviceNumber!!)
//                        managerpersonadapter!!.notifyDataSetChanged()
//                    }
//
//                    override fun threeCode() {}
//                })
//    }


    private fun next_step() {
        // list!!.clear()
        val intent = Intent()
        //val bundle = Bundle()
//        if (list_select.size == 0) {
//            // ToastUtil.showToast(this@CustomDefineMemberSceneActivity, "请选择执行条件")
//            intent.putExtra("person_lock_nums", "")
//            intent.putExtra("personsInfos", personsInfos as Serializable)
//            //intent.putExtras(bundle)
//            this.setResult(Activity.RESULT_OK, intent)
//            finish()
//            return
//        }


        if (list_select.size == 0) {
            ToastUtil.showToast(this@CustomDefineMemberSceneActivity, "请选择成员")
            return
        }


        if (list_select.size == 1) {
            if (list_select[0]["content"]!!.equals("所有人") && !(list_select[0]["check"] as Boolean)) {
                ToastUtil.showToast(this@CustomDefineMemberSceneActivity, "请选择成员")
                return
            }
        }


//        for (map in list_select) {
//            list!!.add(TimeSelectBean(map["name"] as String?, map["time"] as Int))
//        }
        // Collections.sort(list)
        val stringBuffer_num = StringBuffer()
        // val stringBuffer_value = StringBuffer()
        for (i in list_select!!.indices) {
            if (i != list_select!!.size - 1) {
                stringBuffer_num.append(list_select!![i]["personNo"] as String + ",")
                //stringBuffer_value.append(list_select!![i].age.toString() + ",")
            } else {
                stringBuffer_num.append(list_select!![i]["personNo"] as String)
                // stringBuffer_value.append(list!![i].age)
            }
        }
        val map = HashMap<Any?, Any?>()
        // map["personNumbers"] = stringBuffer_num
        //map["condition"] = "5"
        // map["minValue"] = stringBuffer_num
        Log.e("custom", "next_step: " + stringBuffer_num)
//        sendBroad(map)
//        AppManager.getAppManager().finishActivity_current(AutoAgainSceneActivity::class.java)


        //bundle.putSerializable("person_lock_nums", map)
        intent.putExtra("person_lock_nums", stringBuffer_num.toString())
        intent.putExtra("personsInfos", personsInfos as Serializable)
        //intent.putExtras(bundle)
        this.setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /*
     * 通知
     * */
    private fun sendBroad(map: Map<*, *>) {
        val mIntent = Intent(TimeExcuteCordinationActivity.MESSAGE_TIME_EXCUTE_ACTION)
        mIntent.putExtra("time_map", map as Serializable)
        sendBroadcast(mIntent)
    }

//    override fun onItemClick(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
//        val cb = view.findViewById<CheckBox>(R.id.scene_checkbox)
//        cb.toggle()
//        if (cb.isChecked) {
//            list_select.add(list_hand_scene!![i])
//        } else {
//            for (map in list_select) {
//                if (map["personNo"] == list_hand_scene!![i]["personNo"]) {
//                    list_select.remove(map)
//                    break
//                }
//            }
//        }
//    }
}