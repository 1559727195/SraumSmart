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
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.MemberListNewAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/9.
 */
class MemberListActivity : BaseActivity(), XListView.IXListViewListener {
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
    @BindView(R.id.add_member)
    var add_member: ImageView? = null

    //    @BindView(R.id.next_step_txt)
    //    TextView next_step_txt;
    var again_elements = arrayOf("客厅", "卧室", "厨房", "客厅", "餐厅", "阳台", "儿童房", "老年房")
    private var list_hand_scene: List<Map<*, *>>? = null
    private val mHandler = Handler()

    //    @BindView(R.id.add_room)
    //    TextView add_room;
    private var managerpersonadapter: MemberListNewAdapter? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var personsInfos: MutableList<Map<*, *>> = ArrayList()
    private var areaNumber: String? = null
    private var deviceNumber: String? = null
    private var doit = null
    private var authType: String? = ""
    override fun viewId(): Int {
        return R.layout.member_list_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        list_hand_scene = ArrayList()
        managerpersonadapter = MemberListNewAdapter(this@MemberListActivity, authType!!, personsInfos, object : MemberListNewAdapter.RefreshListener {

            override fun refresh() {
                get_allpersoninfo()
            }
        })
        xListView_scan!!.adapter = managerpersonadapter
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setFootViewHide()
        xListView_scan!!.setXListViewListener(this)
    }

    override fun onResume() {
        super.onResume()
        get_allpersoninfo()
        when (authType) {
            "1" -> add_member!!.visibility = View.VISIBLE
            "2" -> add_member!!.visibility = View.GONE
        }
    }

    private fun get_allpersoninfo() {
        //获取网关名称（APP->网关）
        val map = HashMap<Any?, Any?>()
        map["areaNumber"] = areaNumber!!
        map["deviceNumber"] = deviceNumber!!
        map["token"] = TokenUtil.getToken(this@MemberListActivity)
        MyOkHttp.postMapObject(ApiHelper.sraum_getRelatePerson, map as Map<String, Object>,
                object : Mycallback(AddTogglenInterfacer { get_allpersoninfo() }, this@MemberListActivity, null) {
                    override fun onSuccess(user: User) {
//                        project_select.setText(user.name);
                        personsInfos = ArrayList()
                        for (i in user.personList.indices) {
                            val map = HashMap<Any?, Any?>()
                            map["personNo"] = user.personList[i].personNo
                            map["personName"] = user.personList[i].personName
                            map["name_number"] = user.personList[i].personNo + "-" + user.personList[i].personName
                            map["sign"] = user.personList[i].sign
                            personsInfos.add(map)
                        }
                        project_select!!.text = "人员列表" + "(" + user.personList.size + ")"
                        managerpersonadapter!!.setList(personsInfos as List<Any?>?)
                        managerpersonadapter!!.setAreaNumber(areaNumber)
                        managerpersonadapter!!.setAuthType(authType)
                        //deviceNumber
                        managerpersonadapter!!.setDeviceNumber(deviceNumber!!)
                        managerpersonadapter!!.notifyDataSetChanged()
                    }
                    override fun threeCode() {}
                })
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        //        next_step_txt.setOnClickListener(this);
        add_member!!.setOnClickListener(this)
    }

    override fun onData() {
        areaNumber = intent.getSerializableExtra("areaNumber") as String
        authType = intent.getSerializableExtra("authType") as String
        deviceNumber = if (intent.getSerializableExtra("deviceNumber") == null) ""
        else intent.getSerializableExtra("deviceNumber") as String?
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.add_member -> {
                val intent = Intent(this@MemberListActivity, AddNewMemberActivity::class.java)
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("personsInfos", personsInfos as Serializable?)
                intent.putExtra("deviceNumber", deviceNumber)
                startActivity(intent)
//                val intent: Intent? = null
//
//                val list: List<Map<*, *>>? = null
//                intent!!.putExtra("list", list as Serializable?)
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
        get_allpersoninfo()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
    }
}