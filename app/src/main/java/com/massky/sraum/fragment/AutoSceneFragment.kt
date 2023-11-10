package com.massky.sraum.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.deviceLinkList
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.AutoSceneAdapter
import com.massky.sraum.base.BaseFragment1
import com.massky.sraum.event.MyDialogEvent
import com.massky.sraum.view.XListView
import com.ypy.eventbus.EventBus
import okhttp3.Call
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * Created by zhu on 2017/11/30.
 */
class AutoSceneFragment : BaseFragment1(), XListView.IXListViewListener {
    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null
    private val mHandler = Handler()
    private val autoElements = arrayOf("人体传感联动", "厨房联动", "PM2.5联动", "地下室", "防盗门打开"
            , "漏水")
    private val list_hand_scene: List<Map<*, *>> = ArrayList()
    private var autoSceneAdapter: AutoSceneAdapter? = null
    private var dialogUtil: DialogUtil? = null
    private val list: List<deviceLinkList> = ArrayList()
    private var list_atuo_scene: MutableList<Map<*, *>> = ArrayList()
    private var first_add = 0
    private var loginPhone: String? = null
    private var vibflag = false
    private var musicflag = false
    override fun onData() {}
    override fun onEvent() {}
    override fun onEvent(eventData: MyDialogEvent) {}
    override fun viewId(): Int {
        return R.layout.auto_scene_lay
    }

    override fun onView(view: View) {
        dialogUtil = DialogUtil(activity)
        EventBus.getDefault().register(this)
        autoSceneAdapter = AutoSceneAdapter(activity!!, list_atuo_scene, dialogUtil!!, vibflag, musicflag, object : AutoSceneAdapter.RefreshListener { //                get_myDeviceLink();
            override fun refresh() {
                Thread(Runnable { sraum_getAutoScenes() }).start()
                common_second()
                val event = MyEvent()
                event.msg = "刷新"
                EventBus.getDefault().post(event)
            }
        })
        xListView_scan!!.adapter = autoSceneAdapter
        xListView_scan!!.setPullLoadEnable(false)
        xListView_scan!!.setXListViewListener(this)
        xListView_scan!!.setFootViewHide()
        first_add = 1
    }

    override fun onClick(v: View) {}
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

    override fun onResume() {
        super.onResume()
        init_music_flag()
        Thread(Runnable { sraum_getAutoScenes() }).start()
        common_second()
        if (first_add == 1) {
            first_add = 2
        } else {
            val event = MyEvent()
            event.msg = "刷新"
            //...设置event
            EventBus.getDefault().post(event)
        }
    }

    /**
     * 获取自动场景
     */
    private fun sraum_getAutoScenes() {
        val map = HashMap<Any?, Any?>()
        val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
        val order = SharedPreferencesUtil.getData(activity, "order", "1") as String
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(activity)
        map["order"] = order
        //        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAutoScenes
                , map, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getAutoScenes()
        }, activity, dialogUtil) {
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
                list_atuo_scene = ArrayList()
                for (i in user.deviceLinkList.indices) {
                    val map = HashMap<Any?, Any?>()
                    map["id"] = user.deviceLinkList[i].id
                    map["name"] = user.deviceLinkList[i].name
                    map["isUse"] = user.deviceLinkList[i].isUse
                    map["type"] = user.deviceLinkList[i].type
                    map["orderNo"] = user.deviceLinkList[i].orderNo
                    list_atuo_scene.add(map)
                }
                handler.sendEmptyMessage(0)
            }
        })
    }

    private fun init_music_flag() {
        loginPhone = SharedPreferencesUtil.getData(activity, "loginPhone", "") as String
        val preferences = activity!!.getSharedPreferences("sraum$loginPhone",
                Context.MODE_PRIVATE)
        vibflag = preferences.getBoolean("vibflag", false)
        //        musicflag = preferences.getBoolean("musicflag", false);
        musicflag = SharedPreferencesUtil.getData(activity, "musicflag", false) as Boolean
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    if (list_atuo_scene.size == 0) {
                        xListView_scan!!.visibility = View.GONE
                    } else {
                        xListView_scan!!.visibility = View.VISIBLE
                    }
                    autoSceneAdapter!!.addAll(list_atuo_scene) //不要new adapter
                    autoSceneAdapter!!.addFlag(vibflag, musicflag)
                    autoSceneAdapter!!.notifyDataSetChanged()
                }
                1 -> {
                }
            }
        }
    }

    /**
     * 清除联动信息
     */
    private fun common_second() {
        SharedPreferencesUtil.saveData(activity, "linkId", "")
        SharedPreferencesUtil.saveInfo_List(activity, "list_result", ArrayList())
        SharedPreferencesUtil.saveInfo_List(activity, "list_condition", ArrayList())
        SharedPreferencesUtil.saveData(activity, "editlink", false)
        SharedPreferencesUtil.saveInfo_List(activity, "link_information_list", ArrayList())
        SharedPreferencesUtil.saveData(activity, "add_condition", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEvent(event: MyEvent) {
        val status = event.msg
        when (status) {
            "scene_second" -> Thread(Runnable { sraum_getAutoScenes() }).start()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AutoSceneFragment {
            val newFragment = AutoSceneFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            return newFragment
        }
    }
}