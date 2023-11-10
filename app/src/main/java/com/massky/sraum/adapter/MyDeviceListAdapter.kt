package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import com.AddTogenInterface.AddTogglenInterfacer
import com.alibaba.fastjson.JSON
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.MyDeviceItemActivity
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.mcxtzhang.swipemenulib.SwipeMenuLayout.OnMenuClickListener
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class MyDeviceListAdapter(context: Context, list: List<Map<*, *>>, listint: List<Int>, listintwo: List<Int>, authType: String, accountType: String, areaNumber: String, refreshListener: RefreshListener?) : BaseAdapter() {
    private var list: List<Map<*, *>> = ArrayList()
    private var is_open_to_close = false
    private var listint: List<Int> = ArrayList()
    private var listintwo: List<Int> = ArrayList()
    private val temp = -1
    private val context //上下文
            : Context
    private val accountType: String
    private val refreshListener: RefreshListener?
    private var authType = ""
    private val areaNumber: String
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.mydevicelist_item, null)
            viewHolderContentType.device_type_pic = convertView.findViewById<View>(R.id.device_type_pic) as ImageView
            viewHolderContentType.hand_device_content = convertView.findViewById<View>(R.id.hand_device_content) as TextView
            viewHolderContentType.swipe_context = convertView.findViewById<View>(R.id.swipe_context) as LinearLayout
            //            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.hand_scene_btn = convertView.findViewById<View>(R.id.hand_scene_btn) as ImageView
            viewHolderContentType.swipe_layout = convertView.findViewById<View>(R.id.swipe_layout) as SwipeMenuLayout
            viewHolderContentType.delete_btn = convertView.findViewById<View>(R.id.delete_btn) as Button
            viewHolderContentType.hand_gateway_name = convertView.findViewById<View>(R.id.hand_gateway_name) as TextView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        setPicture(list[position]["type"].toString(), viewHolderContentType.device_type_pic)
        viewHolderContentType!!.hand_device_content!!.text = list[position]["name"].toString()
        //        final String authType = (String) SharedPreferencesUtil.getData(context, "authType", "");
        viewHolderContentType.hand_gateway_name!!.visibility = View.VISIBLE
        when (list[position]["type"].toString()) {
            //"网关" -> viewHolderContentType.hand_gateway_name!!.visibility = View.GONE
            "A2A1", "A2A2", "A2A3", "A2A4", "AA02", "AA03", "AD02" ,"AA05"-> viewHolderContentType.hand_gateway_name!!.text = "WIFI"
            else -> viewHolderContentType.hand_gateway_name!!.text = list[position]["boxName"].toString()
        }
        when (authType) {
            "1" -> viewHolderContentType.swipe_layout!!.isSwipeEnable = true
            "2" -> viewHolderContentType.swipe_layout!!.isSwipeEnable = false
        }
        val finalViewHolderContentType1 = viewHolderContentType
        viewHolderContentType.swipe_context!!.setOnClickListener {
            when (authType) {
                "1" -> {

                }
                "2" -> to_activity(position)
            }
        }

        (convertView as SwipeMenuLayout).setOnMenuClickListener(object : OnMenuClickListener {
            override fun onItemClick() {
//                Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                context.startActivity(intent);
                to_activity(position)
            }

            override fun onItemClick_By_btn(is_open_to_close1: Boolean) { //SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1
            }
        })

        val finalViewHolderContentType = viewHolderContentType
        viewHolderContentType.delete_btn!!.setOnClickListener { //                ToastUtil.showToast(context,"onDelete");
            //弹出删除对话框
            finalViewHolderContentType.swipe_layout!!.quickClose()
            showCenterDeleteDialog(list[position]["name"].toString(), list[position]["number"].toString(), list[position]["type"].toString(), if (list[position]["boxNumber"] == null) "" else list[position]["boxNumber"].toString())
        }
        return convertView
    }

    private fun to_activity(position: Int) {//
        when (list[position]["type"].toString()) {
            "A211", "A212", "A213", "A214", "A421",
            "A2A1", "A2A2", "A2A3", "A2A4", "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331", "A401", "A411", "A412", "A413", "A414", "A501",
            "A511", "A801", "A901","A911", "AB01", "A902", "AB04", "AC01", "AE02","AD01", "AD02","AD03", "B001", "B101", "网关","A088",
            "B201", "AA02", "AA03", "AA04","AA05", "A611", "A601", "A711", "A701", "B301", "B401", "B402", "B403", "B501"
            ,"AD11","AD12","AD13","ADA1","ADA2","ADA3","ADB1","ADB2","ADB3"-> {
                val intent = Intent(context, MyDeviceItemActivity::class.java)
                intent.putExtra("panelItem", list[position] as Serializable)
                intent.putExtra("imgtype", listint[position] as Serializable)
                intent.putExtra("areaNumber", areaNumber)
                intent.putExtra("authType", authType)
                context.startActivity(intent)
            }
            else -> {

            }
        }
    }

    private fun setPicture(type: String, device_type_pic: ImageView?) {
        when (type) {

            "A211" -> {//一灯控0场景
                device_type_pic!!.setImageResource(R.drawable.icon_type_yijiandkwcj_40)
            }
            "A212" -> {//两灯控0场景
                device_type_pic!!.setImageResource(R.drawable.icon_type_liangjiandkwcj_40)
            }

            "A213" -> {//三灯控0场景
                device_type_pic!!.setImageResource(R.drawable.icon_type_sanjiandkwcj_40)
            }


            "A214" -> {//四灯控0场景
                device_type_pic!!.setImageResource(R.drawable.icon_type_sijiandkwcj_40)
            }

            "A201", "A2A1" -> device_type_pic!!.setImageResource(R.drawable.icon_type_yijiandk_40)
            "A202", "A2A2" -> device_type_pic!!.setImageResource(R.drawable.icon_type_liangjiandk_40)
            "A203", "A2A3" -> device_type_pic!!.setImageResource(R.drawable.icon_type_sanjiandk_40)
            "A204", "A2A4" -> device_type_pic!!.setImageResource(R.drawable.icon_type_sijiandk_40)
            "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331" -> device_type_pic!!.setImageResource(R.drawable.dimminglights)
            "A401", "A411", "A412", "A413", "A414", "A421","ADB2" -> device_type_pic!!.setImageResource(R.drawable.home_curtain)
            "A501", "A511" -> device_type_pic!!.setImageResource(R.drawable.icon_kongtiao_40)
            "A801" -> device_type_pic!!.setImageResource(R.drawable.icon_type_menci_40)
            "A901"-> device_type_pic!!.setImageResource(R.drawable.icon_type_rentiganying_40)
            "AB01" -> device_type_pic!!.setImageResource(R.drawable.icon_type_yanwucgq_40)
            "A902" -> device_type_pic!!.setImageResource(R.drawable.icon_type_toa_40)
            "AE02" -> device_type_pic!!.setImageResource(R.drawable.icon_type_guangzhao_45_gray)
            "AB04" -> device_type_pic!!.setImageResource(R.drawable.icon_type_tianranqibjq_40)
            "AC01","A911" -> device_type_pic!!.setImageResource(R.drawable.icon_type_shuijin_40)
            "AD01","AD03","ADB3" -> device_type_pic!!.setImageResource(R.drawable.icon_type_pm25_40)
            "AD02" -> device_type_pic!!.setImageResource(R.drawable.icon_pmmofang_40)
            "B001" -> device_type_pic!!.setImageResource(R.drawable.icon_type_jinjianniu_40)
            "B101","ADB1" -> device_type_pic!!.setImageResource(R.drawable.icon_type_zhinengchazuo_40)
            "网关" -> device_type_pic!!.setImageResource(R.drawable.icon_type_wangguan_40)
            "A088" -> device_type_pic!!.setImageResource(R.drawable.icon_szhihuizhongkong_40)
            "B201" -> device_type_pic!!.setImageResource(R.drawable.icon_type_zhinengmensuo_40)
            "AA02" -> device_type_pic!!.setImageResource(R.drawable.icon_type_hongwaizfq_40)
            "AA03" -> device_type_pic!!.setImageResource(R.drawable.icon_type_shexiangtou_40)
            "AA04" -> device_type_pic!!.setImageResource(R.drawable.icon_type_keshimenling_40)
            "AA05" -> device_type_pic!!.setImageResource(R.drawable.icon_yingshisxt_40)
            "A611", "A601" -> device_type_pic!!.setImageResource(R.drawable.icon_xinfengmokuai_40)
            "A711", "A701" -> device_type_pic!!.setImageResource(R.drawable.icon_dinuanmokuai_40)
            "B301" -> device_type_pic!!.setImageResource(R.drawable.icon_jixieshou_40)
            "B401" -> device_type_pic!!.setImageResource(R.drawable.icon_zhinengshengjiang_40)
            "B402" -> device_type_pic!!.setImageResource(R.drawable.icon_zhinengpingyi_40)
            "B403" -> device_type_pic!!.setImageResource(R.drawable.icon_zhinenggaozhongdii_40)
            "B501" -> device_type_pic!!.setImageResource(R.drawable.icon_yinyuemianban_40)
            "AD11","ADA1" -> device_type_pic!!.setImageResource(R.drawable.icon_type_yikaimb_40)
            "AD12","ADA2" -> device_type_pic!!.setImageResource(R.drawable.icon_type_liangkaimb_40)
            "AD13" ,"ADA3"-> device_type_pic!!.setImageResource(R.drawable.icon_type_sankaimb_40)
            else ->                 //device_type_pic.setImageResource(R.drawable.);
                //device_type_pic.setVisibility(View.GONE);
                device_type_pic!!.setImageDrawable(context.resources.getDrawable(R.color.transparent))
        }
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name: String?, number: String, type: String, boxNumber: String) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        var name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        //        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        tv_title.text = name
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(context, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = context.resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener { //
//                String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
            sraum_deleteDevice(areaNumber, number, type, boxNumber)
            dialog.dismiss()
        }
        confirm.setOnClickListener { //                linkage_delete(linkId, dialog);
            dialog.dismiss()
        }
    }

    /**
     * 删除设备
     * *
     *
     * @param areaNumber
     */
    private fun sraum_deleteDevice(areaNumber: String, number: String, type: String,
                                   boxNumber: String) {
        val mapdevice: MutableMap<String, String> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        var send_method: String? = ""
        when (type) {
            "AA02" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiApple
            }
            "AA03", "AA04","AA05" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiCamera
            }
            "网关","A088" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteGateway
            }
            "AD02",
            "A2A1",
            "A2A2",
            "A2A3",
            "A2A4","ADA1","ADA2","ADA3","ADB1","ADB2","ADB3" -> {
                mapdevice["number"] = number
                send_method = ApiHelper.sraum_deleteWifiDeviceCommon
            }


            else -> {
                mapdevice["gatewayNumber"] = boxNumber
                mapdevice["deviceNumber"] = number
                send_method = ApiHelper.sraum_deleteDevice
            }
        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(send_method, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_deleteDevice(areaNumber, number, type, boxNumber)
        }, context, null) {
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
                ToastUtil.showToast(context, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun onSuccess(user: User) {
//                refreshLayout.autoRefresh();
                refreshListener?.refresh()
                when (type) {
                    "AA02" -> {
                        SharedPreferencesUtil.saveInfo_List(context, "remoteairlist",
                                ArrayList())
                        SharedPreferencesUtil.saveData(context, "mGizWifiDevice", "")
                    }

                    "AA03", "AA04" -> {//删除摄像头成功后
                        //ToastUtil.showToast(this@MyDeviceItemActivity, number)
                        over_camera_list(number)
                    }
                }


            }
        })
    }

    /**
     * 清除wifi摄像头列表
     */
    private fun over_camera_list(did: String?) {

//        for (i in list_wifi_camera.indices) {
//            if (list_wifi_camera[i]["did"] == did) {
//                list_wifi_camera[i]["tag"] = tag
//                is_has = true
//                break
//            }
//        }
        val list = SharedPreferencesUtil.getInfo_List(context, "list_wifi_camera_first")
        for (i in list.indices) {
            if (i == list.size) break
            if (list[i]["did"] == did) {
                list.removeAt(i)
            }
        }

        SharedPreferencesUtil.saveInfo_List(context, "list_wifi_camera_first", list)

        Log.e("list_camera", "list_wifi_camera_first:" + JSON.toJSONString(list))

        var list_second_total = SharedPreferencesUtil.getInfo_List(context, "list_second_total")
        for (i in list_second_total.indices) {
            if (i == list_second_total.size) break
            if (list_second_total[i]["did"] == did) {
                list_second_total.removeAt(i)
            }
        }


        Log.e("list_camera", "list_second_total:" + JSON.toJSONString(list_second_total))
        SharedPreferencesUtil.saveInfo_List(context, "list_second_total", list_second_total)
    }


    fun setLists(list_hand_scene: List<Map<*, *>>, listint: List<Int>, listintwo: List<Int>) {
        list = list_hand_scene
        this.listint = listint
        this.listintwo = listintwo
    }

    internal inner class ViewHolderContentType {
        var delete_btn: Button? = null
        var hand_gateway_name: TextView? = null
        var device_type_pic: ImageView? = null
        var hand_device_content: TextView? = null
        var hand_gateway_content: TextView? = null
        var hand_scene_btn: ImageView? = null
        var swipe_context: LinearLayout? = null
        var swipe_layout: SwipeMenuLayout? = null
    }

    interface RefreshListener {
        fun refresh()
    }

    init {
        this.list = list
        this.context = context
        this.accountType = accountType
        this.refreshListener = refreshListener
        this.authType = authType
        this.areaNumber = areaNumber
        this.listint = listint
        this.listintwo = listintwo
    }
}