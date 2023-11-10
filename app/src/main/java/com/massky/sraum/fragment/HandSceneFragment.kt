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
import com.massky.sraum.User.scenelist
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.HandSceneAdapter
import com.massky.sraum.adapter.SelectYaoKongQiAdapter
import com.massky.sraum.base.BaseFragment1
import com.massky.sraum.event.MyDialogEvent
import com.massky.sraum.view.XListView
import com.ypy.eventbus.EventBus
import okhttp3.Call
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2017/11/30.
 */
class HandSceneFragment : BaseFragment1(), XListView.IXListViewListener ,HandSceneAdapter.RefreshListener{
    @JvmField
    @BindView(R.id.xListView_scan)
    var xListView_scan: XListView? = null
    private val mHandler = Handler()
    private val list_hand_scene: List<Map<*, *>> = ArrayList()
    private var handSceneAdapter: HandSceneAdapter? = null
    var again_elements = arrayOf("全屋灯光全开", "全屋灯光全开", "回家模式",
            "离家", "吃饭模式", "看电视", "睡觉", "K歌")
    private var dialogUtil: DialogUtil? = null
    private var scenelist: List<scenelist> = ArrayList()
    private val list: MutableList<Map<*, *>> = ArrayList()
    private val listtype: Any? = ArrayList<Any?>()
    private val listint: MutableList<Int> = ArrayList()
    private val listintwo: MutableList<Int> = ArrayList()
    private var first_add = 0
    private var loginPhone: String? = null
    private var vibflag = false
    private var musicflag = false
    override fun onData() {}
    override fun onEvent() {}
    override fun onEvent(eventData: MyDialogEvent) {}
    override fun onResume() {
        super.onResume()
        init_music_flag()
        Thread(Runnable { sraum_getManuallyScenes() }).start()
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

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (list.size == 0) {
                xListView_scan!!.visibility = View.GONE
            } else {
                xListView_scan!!.visibility = View.VISIBLE
            }
            handSceneAdapter!!.setList_s(list, listint, listintwo, true)
            handSceneAdapter!!.setflag(vibflag, musicflag)
            handSceneAdapter!!.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun onEvent(event: MyEvent) {
        val status = event.msg
        when (status) {
            "scene_second" -> Thread(Runnable { sraum_getManuallyScenes() }).start()
        }
    }


    /**
     * 获取手动场景
     */
    private fun sraum_getManuallyScenes() {
        val map= HashMap<String, Any>()
        val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
        val order = SharedPreferencesUtil.getData(activity, "order", "1") as String
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(activity)
        map["order"] = order
        //        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getManuallyScenes
                , map, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getManuallyScenes()
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
                scenelist = user.sceneList
                list.clear()
                for (us in scenelist) {
                    val map = HashMap<Any?, Any?>()
                    map["type"] = us.type
                    map["name"] = us.name
                    map["number"] = us.number
                    map["boxNumber"] = us.boxNumber
                    map["panelNumber"] = us.panelNumber
                    map["panelType"] = us.panelType
                    //buttonNumber
                    map["buttonNumber"] = us.buttonNumber
                    map["orderNo"] = us.orderNo
                    list.add(map)
                    setPicture(us.type)
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

    private fun setPicture(type: String) {
        when (type) {
            "1" -> {
                listint.add(R.drawable.add_scene_homein)
                listintwo.add(R.drawable.gohome2)
            }
            "2" -> {
                listint.add(R.drawable.add_scene_homeout)
                listintwo.add(R.drawable.leavehome2)
            }
            "3" -> {
                listint.add(R.drawable.add_scene_sleep)
                listintwo.add(R.drawable.sleep2)
            }
            "4" -> {
                listint.add(R.drawable.add_scene_nightlamp)
                listintwo.add(R.drawable.getup_night2)
            }
            "5" -> {
                listint.add(R.drawable.add_scene_getup)
                listintwo.add(R.drawable.getup_morning2)
            }
            "6" -> {
                listint.add(R.drawable.add_scene_cup)
                listintwo.add(R.drawable.rest2)
            }
            "7" -> {
                listint.add(R.drawable.add_scene_book)
                listintwo.add(R.drawable.study2)
            }
            "8" -> {
                listint.add(R.drawable.add_scene_moive)
                listintwo.add(R.drawable.movie2)
            }
            "9" -> {
                listint.add(R.drawable.add_scene_meeting)
                listintwo.add(R.drawable.meeting2)
            }
            "10" -> {
                listint.add(R.drawable.add_scene_cycle)
                listintwo.add(R.drawable.sport2)
            }
            "11" -> {
                listint.add(R.drawable.add_scene_noddle)
                listintwo.add(R.drawable.dinner2)
            }
            "12" -> {
                listint.add(R.drawable.add_scene_lampon)
                listintwo.add(R.drawable.open_all2)
            }
            "13" -> {
                listint.add(R.drawable.add_scene_lampoff)
                listintwo.add(R.drawable.close_all2)
            }
            "14" -> {
                listint.add(R.drawable.defaultpic)
                listintwo.add(R.drawable.defaultpicheck)
            }
            "101" -> {
                listint.add(R.drawable.defaultpic)
                listintwo.add(R.drawable.defaultpicheck)
            }
            else -> {
                listint.add(R.drawable.defaultpic)
                listintwo.add(R.drawable.defaultpicheck)
            }
        }
    }

    override fun viewId(): Int {
        return R.layout.hand_scene_lay
    }

    override fun onView(view: View) {
        dialogUtil = DialogUtil(activity)
        EventBus.getDefault().register(this)
        handSceneAdapter = HandSceneAdapter(activity, list, listint, listintwo, dialogUtil!!, vibflag, musicflag,
                this)

      //  HandSceneAdapter.RefreshListener{




//            Thread(Runnable { sraum_getManuallyScenes() }).start()
//            common_second()
//            val event = MyEvent()
//            event.msg = "刷新"
//            EventBus.getDefault().post(event)
       // })

             xListView_scan!!.adapter = handSceneAdapter
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
        Thread(Runnable { sraum_getManuallyScenes() }).start()
        common_second()
    }

    override fun onLoadMore() {
        mHandler.postDelayed({ onLoad() }, 1000)
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

    companion object {
        @JvmStatic
        fun newInstance(): HandSceneFragment {
            val newFragment = HandSceneFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            return newFragment
        }
    }

    override fun refresh() {
        Thread(Runnable { sraum_getManuallyScenes() }).start()
        common_second()
        val event = MyEvent()
        event.msg = "刷新"
        EventBus.getDefault().post(event)
    }
}