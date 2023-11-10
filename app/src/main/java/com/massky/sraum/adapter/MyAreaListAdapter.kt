package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.massky.sraum.R
import com.massky.sraum.activity.MyDeviceListActivity
import com.massky.sraum.activity.MyfamilyActivity
import com.massky.sraum.activity.RoomListActivity
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.mcxtzhang.swipemenulib.SwipeMenuLayout.OnMenuClickListener
import com.yxc.barchart.ui.step.StepActivity
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class MyAreaListAdapter(context: Context?, list_new: List<Map<*, *>>, roomManager: String) : BaseAdapter<Any?>(context, list_new) {
    private var list_new: List<Map<*, *>> = ArrayList()

    private var is_open_to_close = false
    private val roomManager: String
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.myarealist_item, null)
            viewHolderContentType.swipe_context = convertView.findViewById<View>(R.id.swipe_context) as RelativeLayout
            viewHolderContentType.area_name_txt = convertView.findViewById<View>(R.id.area_name_txt) as TextView
            viewHolderContentType.rename_btn = convertView.findViewById<View>(R.id.rename_btn) as Button
            viewHolderContentType.swipemenu_layout = convertView.findViewById<View>(R.id.swipemenu_layout) as SwipeMenuLayout
            viewHolderContentType.hand_scene_btn = convertView.findViewById<View>(R.id.hand_scene_btn) as ImageView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        when (list_new.get(position).get("authType").toString()) {
            "1" -> viewHolderContentType.area_name_txt!!.setText(list_new.get(position).get("name").toString() + "(" + "业主" + ")")
            "2" -> viewHolderContentType!!.area_name_txt!!.setText(list_new.get(position).get("name").toString() + "(" +
                    "" + "成员" + ")")
        }
        //        final String authType = list.get(position).get("authType").toString();
//        switch (authType) {
//            case "1":
//                viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
//                break;
//            case "2":
//                viewHolderContentType.swipemenu_layout.setSwipeEnable(false);
//                break;
//        }
        viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
        viewHolderContentType.hand_scene_btn!!.visibility = View.VISIBLE
        val finalViewHolderContentType1 = viewHolderContentType
        viewHolderContentType.swipe_context!!.setOnClickListener { //                switch (authType) {
//                    case "1":
//
//                        break;
//                    case "2":
//                        Intent intent = new Intent(context, RoomListActivity.class);
//                        intent.putExtra("areaNumber", list.get(position).get("number").toString());
//                        intent.putExtra("authType", list.get(position).get("authType").toString());
//                        context.startActivity(intent);
//                        break;
//                }
            var intent: Intent? = null
            when (roomManager) {
                "roomManager" -> intent = Intent(context, RoomListActivity::class.java)
                "deviceManager" -> intent = Intent(context, MyDeviceListActivity::class.java)
                "familyManager" -> intent = Intent(context, MyfamilyActivity::class.java)
                "powerManager" -> {
                    intent = Intent(context, StepActivity::class.java)
                    intent.putExtra("in_type","area_in")
                }
            }
            intent!!.putExtra("areaNumber", list_new.get(position).get("number").toString())
            intent.putExtra("authType", list_new.get(position).get("authType").toString())

            context.startActivity(intent)
        }
        when (if (list_new.get(position).get("sign") == null) "" else list_new.get(position).get("sign").toString()) {
            "1" -> viewHolderContentType.area_name_txt!!.setTextColor(context.resources.getColor(R.color.green))
            "0" -> viewHolderContentType.area_name_txt!!.setTextColor(context.resources.getColor(R.color.black))
        }
        (convertView as SwipeMenuLayout).setOnMenuClickListener(object : OnMenuClickListener {
            override fun onItemClick() {
                val intent = Intent(context, RoomListActivity::class.java)
                intent.putExtra("areaNumber", list_new.get(position).get("number").toString())
                intent.putExtra("authType", list_new.get(position).get("authType").toString())
                context.startActivity(intent)
            }

            override fun onItemClick_By_btn(is_open_to_close1: Boolean) { //SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1
            }
        })
        viewHolderContentType.rename_btn!!.setOnClickListener {
            showRenameDialog()
        }
        return convertView
    }

    internal inner class ViewHolderContentType {
        var device_type_pic: ImageView? = null
        var area_name_txt: TextView? = null
        var hand_gateway_content: TextView? = null
        var rename_btn: Button? = null
        var swipe_context: RelativeLayout? = null
        var swipemenu_layout: SwipeMenuLayout? = null
        var hand_scene_btn: ImageView? = null
    }

    //自定义dialog,自定义重命名dialog
    fun showRenameDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView
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
        cancel.setOnClickListener { dialog.dismiss() }
        confirm.setOnClickListener { dialog.dismiss() }
    }

    init {
        this.list_new = list_new
        this.roomManager = roomManager
    }
}