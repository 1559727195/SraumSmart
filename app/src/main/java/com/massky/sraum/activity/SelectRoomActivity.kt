package com.massky.sraum.activity

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.ipcamera.demo.BridgeService
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.SelectRoomActivity
import com.massky.sraum.adapter.RoomListAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.HomeFragmentNew
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import vstc2.nativecaller.NativeCaller
import java.util.*

/**
 * Created by zhu on 2018/1/9.
 */
class SelectRoomActivity : BaseActivity(), XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private var deviceType: String? = ""

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null
    var again_elements = arrayOf("客厅", "卧室", "厨房", "客厅", "餐厅", "阳台", "儿童房", "老年房")
    private val list_hand_scene: List<Map<*, *>>? = null
    private var roomlistadapter: RoomListAdapter? = null
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
//            val `in` = Intent(this@MainGateWayActivity, AddCameraActivity::class.java)
//            startActivity(`in`)
//            finish()
            val mIntent = Intent(MainGateWayActivity.ACTION_INTENT_CAMERA_RECEIVER)
            sendBroadcast(mIntent)
        }
    }

    @JvmField
    @BindView(R.id.save_select_room)
    var save_select_room: Button? = null

    @JvmField
    @BindView(R.id.manager_room_txt)
    var manager_room_txt: TextView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var map_device = HashMap<Any?, Any?>()
    private var roomList: MutableList<Map<*, *>> = ArrayList()
    private var dialogUtil: DialogUtil? = null
    private var position = 0
    private var areaNumber: String? = null
    private var findpaneltype: String? = null
    override fun viewId(): Int {
        return R.layout.select_room_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dialogUtil = DialogUtil(this)
//        map_device = if (intent.getSerializableExtra("map_deivce") == null) null else
//            intent.getSerializableExtra("map_deivce") as HashMap<Any?, Any?>

        if (intent.getSerializableExtra("map_deivce") != null) {
            map_device = intent.getSerializableExtra("map_deivce") as HashMap<Any?, Any?>
            findpaneltype = if (map_device.get("findpaneltype") == null) "" else map_device.get("findpaneltype").toString()
            areaNumber = map_device.get("areaNumber").toString()
            deviceType = map_device.get("deviceType").toString()
        }
        if (areaNumber == null) {
            areaNumber = SharedPreferencesUtil.getData(this@SelectRoomActivity, "areaNumber", "") as String
        }
        room_list_show_adapter()
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setFootViewHide()
        xListView_scan!!.setXListViewListener(this)
    }

    /**
     * 侧栏房间列数据显示
     */
    private fun room_list_show_adapter() {
        roomList = ArrayList()
        roomlistadapter = RoomListAdapter(this@SelectRoomActivity, roomList, RoomListAdapter.HomeDeviceItemClickListener {
            //获取单个房间关联信息（APP->网关）
        })
        xListView_scan!!.adapter = roomlistadapter //设备侧栏列表
        xListView_scan!!.onItemClickListener = this
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        manager_room_txt!!.setOnClickListener(this)
        save_select_room!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        sraum_getRoomsInfo(areaNumber)
    }

    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     */
    private fun sraum_getRoomsInfo(areaNumber: String?) {
        if (dialogUtil != null) {
            dialogUtil!!.loadDialog()
        }
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@SelectRoomActivity)
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getRoomsInfo(areaNumber)
        }, this@SelectRoomActivity, dialogUtil) {
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
                ToastUtil.showToast(this@SelectRoomActivity, """
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
                    roomList.add(mapdevice)
                }

                //加载默认房间下的设备列表
                if (roomList.size != 0) {
                    display_room_list(0)
                }
            }

            override fun threeCode() {
                ToastUtil.showToast(this@SelectRoomActivity, """
     areaNumber 不
     正确
     """.trimIndent())
            }
        })
    }

    /**
     * //去显示房间列表
     */
    private fun display_room_list(position: Int) {
        roomlistadapter!!.setList1(roomList)
        roomlistadapter!!.notifyDataSetChanged()
        for (i in roomList.indices) {
            if (i == position) {
                RoomListAdapter.getIsSelected()[i] = true
            } else {
                RoomListAdapter.getIsSelected()[i] = false
            }
        }
        roomlistadapter!!.notifyDataSetChanged()
    }

    override fun onData() {
//        areaNumber = (String) SharedPreferencesUtil.getData(SelectRoomActivity.this, "areaNumber", "");
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.manager_room_txt -> {
                val intent = Intent(this@SelectRoomActivity, RoomListActivity::class.java)
                intent.putExtra("doit", "sraum_deviceRelatedRoom")
                startActivity(intent)
            }
            R.id.save_select_room -> {
                val roomNumber = roomList[position]["number"] as String?
                sraum_deviceRelatedRoom(roomNumber)
            }
        }
    }

    private fun onLoad() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        sraum_getRoomsInfo(areaNumber)
        onLoad()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position1: Int, id: Long) {
        position = position1 - 1
        for (i in roomList.indices) {
            if (i == position) {
                RoomListAdapter.getIsSelected()[i] = true
            } else {
                RoomListAdapter.getIsSelected()[i] = false
            }
        }
        roomlistadapter!!.notifyDataSetChanged()
    }

    /**
     * 添加完设备关联房间
     */
    private fun sraum_deviceRelatedRoom(roomNumber: String?) {
        if (dialogUtil != null) {
            dialogUtil!!.loadDialog()
        }
        map_device!!["token"] = TokenUtil.getToken(this@SelectRoomActivity)
        //        map_device.put("areaNumber", areaNumber);
        map_device!!["roomNumber"] = roomNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deviceRelatedRoom, map_device, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getRoomsInfo(areaNumber)
        }, this@SelectRoomActivity, dialogUtil) {
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
                ToastUtil.showToast(this@SelectRoomActivity, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
                var action = HomeFragmentNew.ACTION_INTENT_RECEIVER_ROOM_NUMBER
                sendBroad(action, roomNumber!!)
                when (if (findpaneltype == null) "" else findpaneltype) {
                    "fastedit" -> {
                        AppManager.getAppManager().finishActivity_current(SelectRoomActivity::class.java)
                        AppManager.getAppManager().finishActivity_current(FastEditPanelActivity::class.java)
                        AppManager.getAppManager().finishActivity_current(ChangePanelAndDeviceActivity::class.java)
                    }
                    "wangguan_status" -> AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                    else -> {

                        when (deviceType) {
                            "AA03", "AA04" -> {
                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                                init_video()
                            }
                            else -> {
                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity::class.java)
                            }
                        }
                    }
                }
            }

            override fun threeCode() {
                ToastUtil.showToast(this@SelectRoomActivity, "roomNumber 不正确")
            }

            override fun fourCode() {
                ToastUtil.showToast(this@SelectRoomActivity, "deviceId 不正确")
            }

            override fun fiveCode() {
                ToastUtil.showToast(this@SelectRoomActivity, "type 不正确")
            }
        })
    }

    private fun init_video() {
        val intent = Intent()
        intent.setClass(this@SelectRoomActivity, BridgeService::class.java)
        startService(intent)
        Thread {
            try {
                NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL")
                Thread.sleep(3000)
                val msg = Message()
                mHandler.sendMessage(msg)
                Log.e("vst", "path" + applicationContext.filesDir.absolutePath)
                NativeCaller.SetAPPDataPath(applicationContext.filesDir.absolutePath)
            } catch (e: java.lang.Exception) {
            }
        }.start()
    }

    private fun sendBroad(action: String, roomNumber: String?) {
        val mIntent = Intent(action)
        mIntent.putExtra("roomNumber", roomNumber)
        sendBroadcast(mIntent)
    }
}