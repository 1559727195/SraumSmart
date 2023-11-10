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
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.RoomListNewAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/9.
 */
class RoomListActivity : BaseActivity(), XListView.IXListViewListener {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null

    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null

    @JvmField
    @BindView(R.id.add_room)
    var add_room: ImageView? = null

    //    @BindView(R.id.next_step_txt)
    //    TextView next_step_txt;
    var again_elements = arrayOf("客厅", "卧室", "厨房", "客厅", "餐厅", "阳台", "儿童房", "老年房")
    private var list_hand_scene: List<Map<*, *>>? = null
    private val mHandler = Handler()

    //    @BindView(R.id.add_room)
    //    TextView add_room;
    private var managerroomadapter: RoomListNewAdapter? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var roomsInfos: MutableList<Map<*, *>> = ArrayList()
    private var areaNumber: String? = null
    private var doit: String? = null
    private var authType: String? = null
    override fun viewId(): Int {
        return R.layout.room_list_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        list_hand_scene = ArrayList()
        managerroomadapter = RoomListNewAdapter(this@RoomListActivity, roomsInfos, RoomListNewAdapter.RefreshListener { get_allroominfo() })
        xListView_scan!!.adapter = managerroomadapter
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setFootViewHide()
        xListView_scan!!.setXListViewListener(this)
    }

    override fun onResume() {
        super.onResume()
        get_allroominfo()
        when (authType) {
            "1" -> add_room!!.visibility = View.VISIBLE
            "2" -> add_room!!.visibility = View.GONE
        }
    }

    private fun get_allroominfo() {
        //获取网关名称（APP->网关）
        val map = HashMap<Any?, Any?>()
        map["areaNumber"] = areaNumber!!
        map["token"] = TokenUtil.getToken(this@RoomListActivity)
        MyOkHttp.postMapObject(ApiHelper.sraum_getRoomsInfo, map as Map<String,Object>,
                object : Mycallback(AddTogglenInterfacer { get_allroominfo() }, this@RoomListActivity, null) {
                    override fun onSuccess(user: User) {
//                        project_select.setText(user.name);
                        roomsInfos = ArrayList()
                        for (i in user.roomList.indices) {
                            val map = HashMap<Any?, Any?>()
                            map["number"] = user.roomList[i].number
                            map["name"] = user.roomList[i].name
                            map["count"] = user.roomList[i].count
                            map["authType"] = authType
                            roomsInfos.add(map)
                        }
                        project_select!!.text = "房间列表" + "(" + user.roomList.size + ")"
                        managerroomadapter!!.setList(roomsInfos)
                        managerroomadapter!!.setAreaNumber(areaNumber)
                        managerroomadapter!!.notifyDataSetChanged()
                    }

                    override fun threeCode() {}
                })
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        //        next_step_txt.setOnClickListener(this);
        add_room!!.setOnClickListener(this)
    }

    override fun onData() {

       doit = if (intent.getSerializableExtra("doit") == null) "" else intent.getSerializableExtra("doit") as String?

        when (doit) {
            "sraum_deviceRelatedRoom" -> {
                areaNumber = SharedPreferencesUtil.getData(this@RoomListActivity, "areaNumber", "") as String
                authType = SharedPreferencesUtil.getData(this@RoomListActivity, "authType", "") as String
            }
            else -> {
                areaNumber = intent.getSerializableExtra("areaNumber") as String
                authType = intent.getSerializableExtra("authType") as String
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.add_room -> {
                val intent = Intent(this@RoomListActivity, AddNewRoomActivity::class.java)
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("doit", getIntent().getSerializableExtra("doit"))
                startActivity(intent)
            }
        }
    }

    private fun onLoad() {
        xListView_scan!!.stopRefresh()
        xListView_scan!!.stopLoadMore()
        xListView_scan!!.setRefreshTime("刚刚")
    }

    override fun onRefresh() {
        onLoad()
        get_allroominfo()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
    }
}