package com.massky.sraum.activity

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.ipcamera.demo.MyListView
import com.ipcamera.demo.SCameraSetPushVideoTimingActivity
import com.ipcamera.demo.adapter.PushVideoTimingAdapter
import com.ipcamera.demo.bean.AlermBean
import com.ipcamera.demo.bean.SwitchBean
import com.ipcamera.demo.utils.ContentCommon
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.BuFangBaoJingPlanActivity
import com.massky.sraum.base.BaseActivity
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.io.Serializable
import java.util.*

public class BuFangBaoJingPlanActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private var dialogUtil: DialogUtil? = null

    @JvmField
    @BindView(R.id.lv_info_plan)
    var lv_info_plan: MyListView? = null

    @JvmField
    @BindView(R.id.next_step_txt)
    var next_step_txt: TextView? = null
    private var pushAdapter: PushVideoTimingAdapter? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    private val list_camera_list: MutableList<Map<*, *>> = ArrayList() //录像列表
    private var areaNumber: String? = null
    override fun viewId(): Int {
        return R.layout.bufang_baojing_plan
    }

    override fun onView() {
        dialogUtil = DialogUtil(this)
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        dataFromOther
        switchBean = SwitchBean()
        alermBean = AlermBean()
        findView()
        setLister()
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun setLister() {
        next_step_txt!!.setOnClickListener(this)
        back!!.setOnClickListener(this)
    }

    private val dataFromOther: Unit
        private get() {
            val intent = intent
            strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID)
            strPWD = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD)
            areaNumber = getIntent().getSerializableExtra("areaNumber") as String
        }

    //初始化控件
    fun findView() {

        // 移动侦测推送
        pushplan = HashMap()
        pushAdapter = PushVideoTimingAdapter(this@BuFangBaoJingPlanActivity, object : PushVideoTimingAdapter.PushVideoTimingListener {
            override fun delete(position: Int) {
                //弹出框
                val item = list_camera_list[position]
                //sraum_deleteWifiCameraTimeZone
                sraum_deleteWifiCameraTimeZone(item["number"] as String?)


//                deleteHandler.sendMessage(msg);
            }

            override fun onItemClick(position: Int) {
//                Map<Integer, Integer> item = pushAdapter.movetiming.get(position);
//                int itemplan = item.entrySet().iterator().next().getValue();
//                int itemplanKey = item.entrySet().iterator().next().getKey();
                val it = Intent(this@BuFangBaoJingPlanActivity,
                        SCameraSetPushVideoTimingActivity::class.java)
                it.putExtra("type", 1) //编辑
                val item = list_camera_list[position]
                it.putExtra("map_item_record", item as Serializable)
                it.putExtra("areaNumber", areaNumber)
                //                it.putExtra("value", itemplan);
//                it.putExtra("key", itemplanKey);
                startActivityForResult(it, 1)
            }
        })
        lv_info_plan!!.adapter = pushAdapter
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(panelNumber: String?, name: String?, type: String?,
                               finalConvertView: SwipeMenuLayout?) {
        val view = LayoutInflater.from(this@BuFangBaoJingPlanActivity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        name_gloud.text = name
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@BuFangBaoJingPlanActivity, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {
            dialog.dismiss()
            runOnUiThread {
                //                        finalConvertView.closeMenu();
            }
        }
        confirm.setOnClickListener {
            //                sraum_deletepanel(panelNumber, type, dialog, finalConvertView);
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.back -> finish()
            R.id.next_step_txt -> {
                val intent1 = Intent(this@BuFangBaoJingPlanActivity,
                        SCameraSetPushVideoTimingActivity::class.java)
                intent1.putExtra("type", 0)
                intent1.putExtra("areaNumber", areaNumber)
                startActivityForResult(intent1, 0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sraum_getWifiCameraTimeZone()
    }

    /**
     * 获取摄像头移动侦测布防计划列表
     */
    private fun sraum_getWifiCameraTimeZone() {
        dialogUtil!!.loadDialog()
        val mapbox: MutableMap<String, Any?> = HashMap()
        mapbox["token"] = TokenUtil.getToken(this@BuFangBaoJingPlanActivity)
        //        String areaNumber = (String) SharedPreferencesUtil.getData(BuFangBaoJingPlanActivity.this,"areaNumber","");
        mapbox["number"] = strDID
        mapbox["areaNumber"] = areaNumber
        MyOkHttp.postMapObject(ApiHelper.sraum_getWifiCameraTimeZone, mapbox, object : Mycallback(AddTogglenInterfacer { sraum_getWifiCameraTimeZone() }, this@BuFangBaoJingPlanActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                list_camera_list.clear()
                for (i in user.list.indices) {
                    val map_camera_time_zone= HashMap<Any?, Any?>()
                    map_camera_time_zone["number"] = user.list[i].number
                    map_camera_time_zone["startTime"] = user.list[i].startTime
                    map_camera_time_zone["endTime"] = user.list[i].endTime
                    map_camera_time_zone["monday"] = user.list[i].monday
                    map_camera_time_zone["tuesday"] = user.list[i].tuesday
                    map_camera_time_zone["wednesday"] = user.list[i].wednesday
                    map_camera_time_zone["thursday"] = user.list[i].thursday
                    map_camera_time_zone["friday"] = user.list[i].friday
                    map_camera_time_zone["saturday"] = user.list[i].saturday
                    map_camera_time_zone["sunday"] = user.list[i].sunday
                    list_camera_list.add(map_camera_time_zone)
                }
                pushAdapter!!.setList(list_camera_list)
                pushAdapter!!.notifyDataSetChanged()
            }

            override fun wrongToken() {
                super.wrongToken()
                ToastUtil.showToast(this@BuFangBaoJingPlanActivity, "token 错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@BuFangBaoJingPlanActivity, """
     Number
     不正确
     """.trimIndent())
            }
        })
    }

    /**
     * 删除摄像头移动侦测布防计划
     */
    private fun sraum_deleteWifiCameraTimeZone(id: String?) {
        dialogUtil!!.loadDialog()
        val mapbox: MutableMap<String, Any?> = HashMap()
        mapbox["token"] = TokenUtil.getToken(this@BuFangBaoJingPlanActivity)
        mapbox["id"] = id
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteWifiCameraTimeZone, mapbox, object : Mycallback(AddTogglenInterfacer { sraum_deleteWifiCameraTimeZone(id) }, this@BuFangBaoJingPlanActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                for (i in list_camera_list.indices) {
                    if (list_camera_list[i]["number"].toString() == id) {
                        list_camera_list.removeAt(i)
                    }
                }
                pushAdapter!!.setList(list_camera_list)
                pushAdapter!!.notifyDataSetChanged()
            }

            override fun wrongToken() {
                super.wrongToken()
                ToastUtil.showToast(this@BuFangBaoJingPlanActivity, "token 错误")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(this@BuFangBaoJingPlanActivity, """
     Number
     不正确
     """.trimIndent())
            }
        })
    }

    companion object {
        @JvmField
        var strDID: String? = null
        private var strPWD: String? = null
        private var switchBean: SwitchBean? = null
        private var alermBean: AlermBean? = null
        private const val pushmark = "147258369"
        private var pushplan: HashMap<Int, Int>? = null
    }
}