package com.massky.sraum.activity

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.MyBindBtnListAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import java.util.*

/**
 * Created by zhu on 2018/1/8.
 */
class MyBindBtnListActivity : BaseActivity(), XListView.IXListViewListener {
    private lateinit var panelType: String
    private lateinit var panelNumber: String
    private var list_hand_scene: MutableList<Map<*, *>>? = null


    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    //add_device
    @JvmField
    @BindView(R.id.add_device)
    var add_device: ImageView? = null


    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null
    private val mHandler = Handler()
    private var mydeviceadapter: MyBindBtnListAdapter? = null
    private var accountType: String? = null
    private var areaNumber: String? = null
    private var boxnumber: String? = null
    private var authType: String? = null
    override fun viewId(): Int {
        return R.layout.mybind_btn_list
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        accountType = SharedPreferencesUtil.getData(this@MyBindBtnListActivity, "accountType", "") as String
        //        areaNumber = (String) SharedPreferencesUtil.getData(MyDeviceListActivity.this, "areaNumber", "");
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        authType = intent.getSerializableExtra("authType") as String
        panelNumber = intent.getSerializableExtra("panelNumber") as String
        panelType = intent.getSerializableExtra("type") as String
        list_hand_scene = ArrayList()
        mydeviceadapter = MyBindBtnListAdapter(this@MyBindBtnListActivity, list_hand_scene as ArrayList<Map<*, *>>,
                authType!!, accountType!!, areaNumber!!, panelNumber,panelType, object : MyBindBtnListAdapter.RefreshListener {
            override fun refresh() {
                onResumes() //界面出现之后，调数据刷新
            }
        })
        xListView_scan!!.adapter = mydeviceadapter
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setFootViewHide()
        xListView_scan!!.setXListViewListener(this@MyBindBtnListActivity)
        if (authType != null) {
            when (authType) {
                "1" -> add_device!!.visibility = View.VISIBLE
                "2" -> add_device!!.visibility = View.GONE
            }
        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        add_device!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.add_device -> {
                to_activity()
            }
        }
    }

    private fun to_activity() {//
        if (list_hand_scene!!.size == 0) {
            to_bind_add_btn_activity(1)
            return
        }
        for (j in 1..100) { //最多100个键
            var flag = false
            for (i in 1..list_hand_scene!!.size) {
                if (Integer.parseInt(list_hand_scene!![i - 1]["switchId"].toString()) == j) {
                    flag = true
                    break
                }
            }
            if (flag == false) {
                to_bind_add_btn_activity(j)
                //to_bind_add_btn_activity(j)
                break
            } else if (j > list_hand_scene!!.size) {
                to_bind_add_btn_activity(j)
                break
            }
        }
    }

    private fun to_bind_add_btn_activity(switchId: Int) { //switchId :Int
        val intent = Intent(MyBindBtnListActivity@ this, EditBindBtnActivity::class.java)
        intent.putExtra("areaNumber", areaNumber)
        intent.putExtra("authType", authType)
        intent.putExtra("switchId", switchId)
        intent.putExtra("panelNumber", panelNumber)
        intent.putExtra("panelType", panelType)
        intent.putExtra("button", "1")
        intent.putExtra("edit", false)
        startActivity(intent)
    }

    private fun onLoad() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        onLoad()
        onResumes()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
    }

    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
    //    R.drawable.magnetic_door_s,
    //    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
    //    R.drawable.emergency_button_s
    private fun onResumes() {
        getOtherDevices(panelNumber)
    }

    /**
     * 获取门磁等第三方设备
     *
     * @param doit
     */
    private fun getOtherDevices(panelNumber: String) {
//        2.areaNumber：区域编号3.boxNumber网关
        var api = ""
        when(panelType) {
            "ADA1","ADA2","ADA3"-> {
                api = ApiHelper.sraum_getWifiExternalSwitchList
            } else-> {
                api = ApiHelper.sraum_getExternalSwitchList!!
            }
        }
        val mapdevice: MutableMap<String, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@MyBindBtnListActivity)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["panelNumber"] = panelNumber
        MyOkHttp.postMapString(api, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            getOtherDevices(panelNumber)
        }, this@MyBindBtnListActivity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                list_hand_scene = ArrayList()
                for (i in user.externalSwitchList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    // 外部开关 ，第一次添加默认 为 默认开关1 ，
                    mapdevice["switchId"] = user.externalSwitchList[i].switchId
                    mapdevice["button"] = user.externalSwitchList[i].button
                    (list_hand_scene as ArrayList<Map<*, *>>).add(mapdevice)
                }
                //                        mydeviceadapter.setLists(list_hand_scene, listint, listintwo);
//                        mydeviceadapter.notifyDataSetChanged();
                init_main()
            }

            private fun init_main() {
                runOnUiThread {
                    mydeviceadapter = MyBindBtnListAdapter(this@MyBindBtnListActivity, list_hand_scene!!,
                            authType!!, accountType!!, areaNumber!!, panelNumber, panelType, object : MyBindBtnListAdapter.RefreshListener {
                        override fun refresh() {
                            onResumes() //界面出现之后，调数据刷新
                        }
                    })
                    xListView_scan!!.adapter = mydeviceadapter
                    xListView_scan!!.setPullLoadEnable(false)
                    xListView_scan!!.setFootViewHide()
                    xListView_scan!!.setXListViewListener(this@MyBindBtnListActivity)
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        onResumes() //界面出现之后，调数据刷新
    }
}