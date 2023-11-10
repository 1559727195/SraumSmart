package com.massky.sraum.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.adapter.MemberNumberListAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.view.XListView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.util.*

/**
 * Created by zhu on 2018/1/9.
 */
class MemberNumberIdActivity : BaseActivity(), XListView.IXListViewListener {
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

    private var list_hand_scene: List<Map<*, *>>? = null
    private val mHandler = Handler()

    //    @BindView(R.id.add_room)
    //    TextView add_room;
    private var member_num_list_adapter: MemberNumberListAdapter? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var roomsInfos: MutableList<String> = ArrayList()
    private var personsInfos: MutableList<Map<*, *>> = ArrayList()


    override fun viewId(): Int {
        return R.layout.member_num_list_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        list_hand_scene = ArrayList()

        personsInfos = if (intent.getSerializableExtra("personsInfos") == null) ArrayList<Map<*, *>>() else
            intent.getSerializableExtra("personsInfos") as MutableList<Map<*, *>>

        for (i in 1..100) {
            roomsInfos.add(i - 1, i.toString())
            // roomsInfos[i - 1] = i.toString()
        }

        for (i in personsInfos.indices) {
            var personNo: String? = personsInfos[i]["personNo"] as String
            roomsInfos.remove(personNo!!)
        }

        member_num_list_adapter = MemberNumberListAdapter(this@MemberNumberIdActivity, roomsInfos)
        xListView_scan!!.adapter = member_num_list_adapter
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setFootViewHide()
        xListView_scan!!.setXListViewListener(this)
        xListView_scan!!.setOnItemClickListener(AdapterView.OnItemClickListener() { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
            Log.e("click", "onView: " + i)
            val its = Intent()
            its.putExtra("personNo", roomsInfos[i - 1])
            setResult( Activity.RESULT_OK, its)

            //设置返回数据
            this@MemberNumberIdActivity.setResult(Activity.RESULT_OK, its)
            this@MemberNumberIdActivity.finish()
        })
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onEvent() {
        back!!.setOnClickListener(this)
        //        next_step_txt.setOnClickListener(this);
        add_room!!.setOnClickListener(this)
    }

    override fun onData() {
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
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