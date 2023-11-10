package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.percentlayout.widget.PercentRelativeLayout
import com.AddTogenInterface.AddTogglenInterfacer
import com.google.gson.Gson
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.MyOkHttp
import com.massky.sraum.Util.Mycallback
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Util.TokenUtil
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.view.ClearLengthEditText
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by masskywcy on 2017-05-16.
 */
class MemberSceneListNewAdapter //        this.list = list;
(context: Context?, list: List<Map<*, *>?>?, //    private List<Map> list = new ArrayList<>();
 list_select: MutableList<Map<*, *>>,
 action: String?,
 private var refreshListener: RefreshListener) : BaseAdapter<Any?>(context, list) {
    private var areaNumber: String? = null
    private var action: String? = ""
    private var is_open_to_close = false
    private var list_select: MutableList<Map<*, *>> = ArrayList()

    init {
        this.list_select = list_select
        this.action = action
    }


    private var deviceNumber: String? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.member_scene_listnew_item, null)
            viewHolderContentType.room_name_txt = convertView.findViewById<View>(R.id.room_name_txt) as TextView
            viewHolderContentType.img_again_autoscene = convertView.findViewById<View>(R.id.img_again_autoscene) as ImageView
            //pic_room_img

            viewHolderContentType.swipe_context = convertView.findViewById<View>(R.id.swipe_context) as PercentRelativeLayout
            viewHolderContentType.pic_room_img = convertView.findViewById<View>(R.id.pic_room_img) as ImageView
            viewHolderContentType.txt_device_num = convertView.findViewById<View>(R.id.txt_device_num) as TextView
            viewHolderContentType.rename_btn = convertView.findViewById<View>(R.id.rename_btn) as Button
            viewHolderContentType.remove_btn = convertView.findViewById<View>(R.id.remove_btn) as Button
            viewHolderContentType.swipemenu_layout = convertView.findViewById<View>(R.id.swipemenu_layout) as SwipeMenuLayout
            viewHolderContentType.scene_checkbox = convertView.findViewById<View>(R.id.scene_checkbox) as CheckBox
            //scene_image
            viewHolderContentType.scene_image = convertView.findViewById<View>(R.id.scene_image) as ImageView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
//        viewHolderContentType.room_name_txt!!.text = (getList()[position] as Map<*, *>)["personNo"].toString() + "-"+
//        (getList()[position] as Map<*, *>)["personName"].toString()

        viewHolderContentType.room_name_txt!!.text = (getList()[position] as Map<*, *>)["content"].toString()
//        when (position) {
//            0 -> {
//                viewHolderContentType.scene_checkbox!!.visibility = View.GONE
//            }
//        }

        val authType = (getList()[position] as Map<*, *>)["authType"].toString()
//        when (authType) {
//            "1" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
//            "2" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
//        }
        viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
        when (action) {
            "edit" -> {
                when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                    "1" -> {//1,未关联,
                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = false
                    }
                    "0" -> {//0,被当前场景关联
                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = true
                    }
                    "2" -> {//2,表示其他场景关联
                        viewHolderContentType.scene_checkbox!!.visibility = View.GONE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = false
//                        viewHolderContentType.swipemenu_layout!!.setBackgroundColor(
//                                context.resources.getColor(R.color.gray)
                        viewHolderContentType.room_name_txt!!.setTextColor(context.resources.getColor(R.color.gray))
                    }
                }
            }
            "add" -> {
                when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                    "0" -> {
                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = false
                    }
                    "3" -> {//0,未关联,
                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = true
                    }
                    "1" -> {//1,被其他场景关联
                        viewHolderContentType.scene_checkbox!!.visibility = View.GONE
                        viewHolderContentType!!.scene_checkbox!!.isChecked = false
//                        viewHolderContentType.swipemenu_layout!!.setBackgroundColor(
//                                context.resources.getColor(R.color.gray))
                        viewHolderContentType.room_name_txt!!.setTextColor(context.resources.getColor(R.color.gray))
                    }
                }
            }


//            "" -> {//所有
//                if (list_select.size != 0 && !list_select[0]["sign"]!!.equals("")) {
//                    viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
//                    viewHolderContentType!!.scene_checkbox!!.isChecked = false
//                } else {
//                    viewHolderContentType.scene_checkbox!!.visibility = View.GONE
//                    viewHolderContentType!!.scene_checkbox!!.isChecked = true
//                }
//            }
        }

        when (position) {
            0 -> {
//                when (list_select[0]["sign"]) {
//                    "4" -> {
//                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
//                        viewHolderContentType!!.scene_checkbox!!.isChecked = false
//                    }
//                    "" -> {
//                        viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
//                        viewHolderContentType!!.scene_checkbox!!.isChecked = true
//                    }
//                }

                var check = if ((getList()[position] as Map<*, *>)["check"] == null) false else (getList()[position] as Map<*, *>)["check"]
                        as Boolean
                if (check) {
                    viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                    viewHolderContentType!!.scene_checkbox!!.isChecked = true
                } else {
                    viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                    viewHolderContentType!!.scene_checkbox!!.isChecked = false
                }

                //  viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
                viewHolderContentType.room_name_txt!!.setTextColor(context.resources.getColor(R.color.black))
            }
        }
//        when ((getList()[position] as Map<*, *>)["sign"].toString()) {
//            "0" -> {//0,未关联,
//                viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
//                viewHolderContentType!!.scene_checkbox!!.isChecked = false
//            }
//            "1" -> {//1,当前场景关联
//                viewHolderContentType.scene_checkbox!!.visibility = View.VISIBLE
//                viewHolderContentType!!.scene_checkbox!!.isChecked = true
//
//            }
//            "2" -> {//2,表示其他场景关联
//                viewHolderContentType.scene_checkbox!!.visibility = View.GONE
//            }
//        }


        val finalViewHolderContentType = viewHolderContentType
        (convertView as SwipeMenuLayout).setOnMenuClickListener(object : SwipeMenuLayout.OnMenuClickListener {
            override fun onItemClick() {//merg
//                Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                context.startActivity(intent);
                // to_activity(position)

                to_activity(position, finalViewHolderContentType)
            }

            override fun onItemClick_By_btn(is_open_to_close1: Boolean) { //SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1
            }
        })

        viewHolderContentType.swipe_context!!.setOnClickListener {
//            when (authType) {
//                "1" -> {
//
//                }
//                "2" -> to_activity(position)
//            }
            to_activity(position, finalViewHolderContentType)
        }


        viewHolderContentType!!.rename_btn!!.setOnClickListener {
            finalViewHolderContentType!!.swipemenu_layout!!.quickClose()
            showRenameDialog((getList()[position] as Map<*, *>)["personName"].toString(),
                    (getList()[position] as Map<*, *>)["personNo"].toString())
        }
        viewHolderContentType.remove_btn!!.setOnClickListener {
            finalViewHolderContentType!!.swipemenu_layout!!.quickClose()
            showCenterDeleteDialog((getList()[position] as Map<*, *>)["personName"].toString(),
                    (getList()[position] as Map<*, *>)["personNo"].toString(), areaNumber)
        }
        return convertView //
    }

    private fun to_activity(position: Int, finalViewHolderContentType: ViewHolderContentType) {

        when (position) {
            0 -> {
                zero_on_item_click(finalViewHolderContentType)
            }
            else -> {
                other_onitem_click(position, finalViewHolderContentType)
            }
        }
    }

    private fun zero_on_item_click(finalViewHolderContentType: ViewHolderContentType) {
        finalViewHolderContentType.scene_checkbox!!.isChecked = true
        list_select = ArrayList()
        if (finalViewHolderContentType.scene_checkbox!!.isChecked) {
            var map_person = HashMap<Any?, Any?>()
            map_person["content"] = "所有人"
            map_person["personNo"] = ""
            map_person["personName"] = ""
            map_person["sign"] = ""
            map_person["check"] = true
            list_select.add(map_person)
            // list_select.add(getList()[position] as Map<*, *>)
        }
        refreshListener!!.back_persons(list_select)
        //common_back_select(finalViewHolderContentType, position)

        for (i in getList().indices) {
            when (action) {
                "edit" -> {
                    if ((getList()[i] as Map<*, *>)["sign"].toString().equals("0")) {
                        //  (list[i] as HashMap<String, Any>)["status"]
                        (getList()[i] as HashMap<String, Any>)["sign"] = "1"
                    }
                }

                "add" -> {
                    if ((getList()[i] as Map<*, *>)["sign"].toString().equals("3")) {
                        //  (list[i] as HashMap<String, Any>)["status"]
                        (getList()[i] as HashMap<String, Any>)["sign"] = "0"
                    }
                }
            }
        }
        (getList()[0] as HashMap<String, Any>)["check"] = true
        notifyDataSetChanged()
        Log.e("member_scene", "list_select: " + Gson().toJson(list_select))
        Log.e("member_scene", "personsInfos: " + Gson().toJson(getList()))
    }

    private fun other_onitem_click(position: Int, finalViewHolderContentType: ViewHolderContentType) {

        when (action) {
            "edit" -> {
                when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                    "1", "0" -> {//1,未关联,
                        when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                            "0" -> {
                                (getList()[position] as HashMap<String, Any>)["sign"] = "1"
                            }

                            "1" -> {
                                (getList()[position] as HashMap<String, Any>)["sign"] = "0"
                            }
                        }
                        common_back_select(finalViewHolderContentType, position)
                    }
                    "2" -> {//2,表示其他场景关联

                    }
                }
            }

            "add" -> {
                when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                    "0" -> {//0,未关联,
                        //  (getList()[position] as HashMap<String, Any>)["sign"] = "3"
                        when ((getList()[position] as Map<*, *>)["sign"].toString()) {
                            "0" -> {
                                (getList()[position] as HashMap<String, Any>)["sign"] = "3"
                            }
                            "3" -> {
                                (getList()[position] as HashMap<String, Any>)["sign"] = "0"
                            }
                        }
                        common_back_select(finalViewHolderContentType, position)
                    }
                    "1" -> {//1,被其他场景关联


                    }
                    "3" -> {//被自己的场景关联
                        (getList()[position] as HashMap<String, Any>)["sign"] = "0"
                        common_back_select(finalViewHolderContentType, position)
                    }
                }
            }
        }

        var check = if ((getList()[0] as Map<*, *>)["check"] == null) false else (getList()[0] as Map<*, *>)["check"] as Boolean
        if (check) {
            (getList()[0] as HashMap<String, Any>)["check"] = false
        }

        if (list_select.size != 0)
            (list_select[0] as HashMap<String, Any>)["check"] = false
        notifyDataSetChanged()
        Log.e("member_scene", "list_select: " + Gson().toJson(list_select))
        Log.e("member_scene", "personsInfos: " + Gson().toJson(getList()))
        // refreshListener!!.back_persons_list(getList() as MutableList<Map<*, *>>)


//        when ((getList()[position] as Map<*, *>)["sign"].toString()) {
//            "1", "0" -> {//1,未关联,0,当前场景关联
//
//            }
//            "2" -> {//2,表示其他场景关联
//
//            }
//        }
    }

    private fun common_back_select(finalViewHolderContentType: ViewHolderContentType, position: Int) {
        finalViewHolderContentType.scene_checkbox!!.toggle()
        if (finalViewHolderContentType.scene_checkbox!!.isChecked) {
            list_select.add(getList()[position] as Map<*, *>)

        } else {
            for (map in list_select) {
                if (map["personNo"] == (getList()[position] as Map<*, *>)["personNo"]) {
                    list_select.remove(map)
                    break
                }
            }
        }

        //(list_select[0] as HashMap<String, Any>)["sign"] = "4"
        Log.e("member_scene", "list_select: " + Gson().toJson(list_select))
        refreshListener!!.back_persons(list_select)
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name: String?, number: String, areaNumber: String?) {
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
        cancel.setOnClickListener {
            sraum_deletePerson(deviceNumber, areaNumber, number)
            dialog.dismiss()
        }
        confirm.setOnClickListener { //                linkage_delete(linkId, dialog);
            dialog.dismiss()
        }
    }

    /**
     * 删除房间（）APP->网关
     */
    private fun sraum_deletePerson(deviceNumber: String?, areaNumber: String?, personNo: String) {
        val mapdevice: MutableMap<String, Any?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["deviceNumber"] = deviceNumber
        //personNo
        mapdevice["personNo"] = personNo
        MyOkHttp.postMapObject(ApiHelper.sraum_deletePersonInfo, mapdevice,
                object : Mycallback(AddTogglenInterfacer { }, context, null) {
                    override fun onSuccess(user: User) {
                        refreshListener?.refresh()
                    }

                    override fun wrongBoxnumber() {
                        ToastUtil.showToast(context, """
     areaNumber
     不存在
     """.trimIndent())
                    }

                    override fun threeCode() {
                        ToastUtil.showToast(context, "deviceNumber 错误")
                    }

                    override fun fourCode() {
                        ToastUtil.showToast(context, "personNo 不正确")
                    }

                    override fun fiveCode() {
                        ToastUtil.showToast(context, "删除失败")
                    }
                })
    }

    /**
     * 修改房间名称
     */
    private fun sraum_updateRoomName(deviceNumber: String?, areaNumber: String?,
                                     newPersonName: String, personNo: String) {
        //获取网关名称（APP->网关）
        val mapdevice: MutableMap<String, Any?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["deviceNumber"] = deviceNumber
        mapdevice["personNo"] = personNo
        mapdevice["newPersonName"] = newPersonName
        MyOkHttp.postMapObject(ApiHelper.sraum_updatePersonInfo, mapdevice,
                object : Mycallback(AddTogglenInterfacer { sraum_updateRoomName(deviceNumber, areaNumber, newPersonName, personNo) }, context, null) {
                    override fun onSuccess(user: User) {
                        refreshListener?.refresh()
                    }

                    override fun wrongBoxnumber() {
                        ToastUtil.showToast(context, """
     areaNumber
     不存在
     """.trimIndent())
                    }

                    override fun threeCode() {
                        ToastUtil.showToast(context, "deviceNumber 错误")
                    }

                    override fun fourCode() {
                        ToastUtil.showToast(context, "personNo 不正确")
                    }

                    override fun fiveCode() {
                        ToastUtil.showToast(context, """
     personName 不正
     确
     """.trimIndent())
                    }

                    override fun sixCode() {
                        ToastUtil.showToast(context, "修改失败")
                    }
                })
    }

    fun setDeviceNumber(deviceNumber: String) {
        this.deviceNumber = deviceNumber
    }

    interface RefreshListener {
        fun refresh()
        fun back_persons(list_select: MutableList<Map<*, *>>)
        fun back_persons_list(listSelect: MutableList<Map<*, *>>)
    }

    //自定义dialog,自定义重命名dialog
    fun showRenameDialog(name: String?, personNo: String) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView
        val edit_password_gateway = view.findViewById<View>(R.id.edit_password_gateway) as ClearLengthEditText
        edit_password_gateway.setText(name)
        edit_password_gateway.setSelection(edit_password_gateway.text!!.length)
        //        tv_title.setText("name");
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
        cancel.setOnClickListener(View.OnClickListener {
            if (edit_password_gateway.text.toString() == null || edit_password_gateway.text.toString().trim { it <= ' ' } == "") {
                ToastUtil.showToast(context, "区域名称为空")
                return@OnClickListener
            }
            sraum_updateRoomName(deviceNumber, areaNumber, edit_password_gateway.text.toString(), personNo) //修改房间名称(areaNumber, edit_password_gateway.getText().toString());
            dialog.dismiss()
        })
        confirm.setOnClickListener { dialog.dismiss() }
    }

    fun setAreaNumber(areaNumber: String?) {
        this.areaNumber = areaNumber
    }

    fun setSelectList(list: MutableList<Map<*, *>>) {
        this.list_select = list
    }

    fun setAction(action: String?) {
        this.action = action
    }

    internal inner class ViewHolderContentType {
        var swipe_context: PercentRelativeLayout? = null
        var img_again_autoscene: ImageView? = null
        var room_name_txt: TextView? = null
        var pic_room_img: ImageView? = null
        var txt_device_num: TextView? = null
        var rename_btn: Button? = null
        var remove_btn: Button? = null
        var swipemenu_layout: SwipeMenuLayout? = null
        var scene_checkbox: CheckBox? = null
        var scene_image: ImageView? = null
    }

}