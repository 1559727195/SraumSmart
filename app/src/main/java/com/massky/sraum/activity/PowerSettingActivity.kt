package com.massky.sraum.activity

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.ManagerRoomAdapter
import com.massky.sraum.adapter.MyAreaListAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import okhttp3.Call
import java.util.*

/**
 * Created by zhu on 2018/2/12.
 */
class PowerSettingActivity : BaseActivity(), XListView.IXListViewListener {
    private var list_hand_scene: List<Map<*, *>>? = null

    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null
    private val mHandler = Handler()

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.btn_cancel_wangguan)
    var btn_cancel_wangguan: Button? = null

    @JvmField
    @BindView(R.id.add_room)
    var add_room: ImageView? = null

    @JvmField
    @BindView(R.id.toolbar_txt)
    var toolbar_txt: TextView? = null
    private var homesettingadapter: MyAreaListAdapter? = null
    private var dialogUtil: DialogUtil? = null
    private var areaList: MutableList<Map<*, *>> = ArrayList()
    private var manageroomadapter: ManagerRoomAdapter? = null
    private var roomList: MutableList<Map<*, *>> = ArrayList() //房间列表
    override fun viewId(): Int {
        return R.layout.power_setting_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        dialogUtil = DialogUtil(this)
        toolbar_txt!!.text = "能耗管理"
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        btn_cancel_wangguan!!.setOnClickListener(this)
        add_room!!.setOnClickListener(this)
    }

    /**
     * 获取所有区域
     */
    private fun sraum_getAllArea() {
        if (dialogUtil != null) {
            dialogUtil!!.loadDialog()
        }
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@PowerSettingActivity)
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getAllArea()
        }, this@PowerSettingActivity, dialogUtil) {
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
                areaList = ArrayList()
                for (i in user.areaList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    mapdevice["name"] = user.areaList[i].areaName
                    mapdevice["number"] = user.areaList[i].number
                    mapdevice["sign"] = user.areaList[i].sign
                    mapdevice["authType"] = user.areaList[i].authType
                    areaList.add(mapdevice)
                }
                if (user.areaList != null && user.areaList.size != 0) { //区域命名
//                            //，加载默认区域下默认房间
//                            if (areaNumber != null || !areaNumber.equals("")) {
//                                sraum_getRoomsInfo(areaNumber);
//                            } else {
//                                current_area_map = areaList.get(0);
//                                sraum_getRoomsInfo(user.areaList.get(0).number);
//                            }
//                            if (user.areaList.size() == 1) {//只有一个区域的话，直接显示房间列表
//                                //去获取房间列表
//                                switch (user.areaList.get(0).authType) {
//                                    case "1"://业主
//                                        add_room.setVisibility(View.VISIBLE);
//                                        break;
//                                    case "2"://成员
//                                        add_room.setVisibility(View.GONE);
//                                        break;
//                                }
//                                sraum_getRoomsInfo(user.areaList.get(0).number,user.areaList.get(0).authType);
//                            } else {//显示区域列表
//                                add_room.setVisibility(View.GONE);
//                                homesettingadapter = new MyAreaListAdapter(HomeSettingActivity.this, areaList);
//                                xListView_scan.setAdapter(homesettingadapter);
//                            }
                    if (add_room != null)
                        add_room!!.visibility = View.GONE
                    homesettingadapter = MyAreaListAdapter(this@PowerSettingActivity, areaList, "powerManager")
                    xListView_scan!!.adapter = homesettingadapter
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sraum_getAllArea()
    }

    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     */
    private fun sraum_getRoomsInfo(areaNumber: String, authType: String) {
        if (dialogUtil != null) {
            dialogUtil!!.loadDialog()
        }
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@PowerSettingActivity)
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getRoomsInfo(areaNumber, authType)
        }, this@PowerSettingActivity, dialogUtil) {
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
                ToastUtil.showToast(this@PowerSettingActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                //qie huan cheng gong ,获取区域的所有房间信息
                roomList = ArrayList()
                for (i in user.roomList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    mapdevice["name"] = user.roomList[i].name
                    mapdevice["number"] = user.roomList[i].number
                    mapdevice["count"] = user.roomList[i].count
                    mapdevice["authType"] = authType
                    roomList.add(mapdevice)
                }
                display_room_list()
            }

            override fun threeCode() {
                ToastUtil.showToast(this@PowerSettingActivity, """
     areaNumber 不
     正确
     """.trimIndent())
            }
        })
    }

    /**
     * 展示房间列表信息
     */
    private fun display_room_list() {
        manageroomadapter = ManagerRoomAdapter(this@PowerSettingActivity, roomList)
        xListView_scan!!.adapter = manageroomadapter
    }

    override fun onData() {
        list_hand_scene = ArrayList()
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setXListViewListener(this)
        xListView_scan!!.setFootViewHide()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.btn_cancel_wangguan, R.id.add_room -> startActivity(Intent(this@PowerSettingActivity, AddAreaActivity::class.java))
        }
    }

    private fun onLoad() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        onLoad()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
    }
}