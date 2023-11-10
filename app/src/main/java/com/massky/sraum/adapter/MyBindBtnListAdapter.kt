package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.EditBindBtnActivity
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.mcxtzhang.swipemenulib.SwipeMenuLayout.OnMenuClickListener
import okhttp3.Call
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class MyBindBtnListAdapter(context: Context, list: List<Map<*, *>>, authType: String, accountType: String,
                           areaNumber: String, panelNumber: String, panelType: String, refreshListener: RefreshListener?) : BaseAdapter() {
    private var panelType: String
    private var panelNumber: String
    private var list: List<Map<*, *>> = ArrayList()
    private var is_open_to_close = false
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
            convertView = LayoutInflater.from(context).inflate(R.layout.mybind_btn_list_item, null)
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
        viewHolderContentType!!.hand_device_content!!.text = "外部开关" +
                list[position]["switchId"].toString()
        //        final String authType = (String) SharedPreferencesUtil.getData(context, "authType", "");
        viewHolderContentType.hand_gateway_name!!.visibility = View.VISIBLE
        viewHolderContentType.hand_gateway_name!!.text = "按钮" + list[position]["button"].toString()

        when (authType) {
            "1" -> viewHolderContentType.swipe_layout!!.isSwipeEnable = true
            "2" -> viewHolderContentType.swipe_layout!!.isSwipeEnable = false
        }
        viewHolderContentType.swipe_context!!.setOnClickListener {
            when (authType) {
                "1" -> {

                }
                "2" -> to_bind_add_btn_activity(list[position]["switchId"].toString(),list[position]["button"].toString())
            }
        }

        (convertView as SwipeMenuLayout).setOnMenuClickListener(object : OnMenuClickListener {
            override fun onItemClick() {
//                Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                context.startActivity(intent);
                to_bind_add_btn_activity(list[position]["switchId"].toString(),list[position]["button"].toString())
            }

            override fun onItemClick_By_btn(is_open_to_close1: Boolean) { //SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1
            }
        })

        val finalViewHolderContentType = viewHolderContentType
        viewHolderContentType.delete_btn!!.setOnClickListener { //                ToastUtil.showToast(context,"onDelete");
            //弹出删除对话框
            finalViewHolderContentType.swipe_layout!!.quickClose()
            showCenterDeleteDialog(list[position]["switchId"].toString())
        }
        return convertView
    }


    private fun to_bind_add_btn_activity(switchId: String,button:String) { //switchId :Int
        val intent = Intent(context, EditBindBtnActivity::class.java)
        intent.putExtra("areaNumber", areaNumber)
        intent.putExtra("authType", authType)
        intent.putExtra("switchId", switchId)
        intent.putExtra("panelNumber",panelNumber)
        intent.putExtra("button",button)
        intent.putExtra("panelType",panelType)
        intent.putExtra("edit", true)
        context.startActivity(intent)
    }


    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(switchId: String?) {
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
        tv_title.text = "外部开关" + switchId
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
            sraum_deleteDevice(switchId)
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
    private fun sraum_deleteDevice(switchId: String?) {
        val mapdevice: MutableMap<String, String> = HashMap()
        var api = ""
        when(panelType) {
            "ADA1","ADA2","ADA3"-> {
                api = ApiHelper.sraum_deleteWifiExternalSwitchRelation!!
            } else-> {
                api = ApiHelper.sraum_deleteExternalSwitchRelation!!
            }
        }
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["panelNumber"] = panelNumber
        mapdevice["switchId"] = switchId!!
        MyOkHttp.postMapString(api, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_deleteDevice(switchId)
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
            }
        })
    }


    fun setLists(list_hand_scene: List<Map<*, *>>) {
        list = list_hand_scene
    }

    internal inner class ViewHolderContentType {
        var delete_btn: Button? = null
        var hand_gateway_name: TextView? = null
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
        this.panelNumber = panelNumber
        this.panelType = panelType
    }
}