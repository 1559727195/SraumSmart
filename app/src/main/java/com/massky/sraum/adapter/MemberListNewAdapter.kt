package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.AddTogenInterface.AddTogglenInterfacer
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

/**
 * Created by masskywcy on 2017-05-16.
 */
class MemberListNewAdapter //        this.list = list;
(context: Context?, authType: String, list:MutableList<Map<*, *>>, //    private List<Map> list = new ArrayList<>();
 private val refreshListener: RefreshListener?) : BaseAdapter<Any?>(context, list as List<Any>?) {
    private var areaNumber: String? = null
    private var authType = ""
    private var deviceNumber: String? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.member_listnew_item, null)
            viewHolderContentType.room_name_txt = convertView.findViewById<View>(R.id.room_name_txt) as TextView
            viewHolderContentType.img_again_autoscene = convertView.findViewById<View>(R.id.img_again_autoscene) as ImageView
            //pic_room_img
            viewHolderContentType.pic_room_img = convertView.findViewById<View>(R.id.pic_room_img) as ImageView
            viewHolderContentType.txt_device_num = convertView.findViewById<View>(R.id.txt_device_num) as TextView
            viewHolderContentType.rename_btn = convertView.findViewById<View>(R.id.rename_btn) as Button
            viewHolderContentType.remove_btn = convertView.findViewById<View>(R.id.remove_btn) as Button
            viewHolderContentType.swipemenu_layout = convertView.findViewById<View>(R.id.swipemenu_layout) as SwipeMenuLayout
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.room_name_txt!!.text = (getList()[position] as Map<*, *>)["name_number"].toString()
        //val authType = (getList()[position] as Map<*, *>)["authType"].toString()
        when (authType) {
            "1" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = true
            "2" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
        }
        val finalViewHolderContentType = viewHolderContentType
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
        return convertView!! //
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
      //  tv_title = view.findViewById<View>(R.id.tv_title) as TextView
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

    fun setAuthType(authType: String?) {
        this.authType = authType!!
    }

    internal inner class ViewHolderContentType {
        var img_again_autoscene: ImageView? = null
        var room_name_txt: TextView? = null
        var pic_room_img: ImageView? = null
        var txt_device_num: TextView? = null
        var rename_btn: Button? = null
        var remove_btn: Button? = null
        var swipemenu_layout: SwipeMenuLayout? = null
    }

    init {
        this.authType = authType
    }



}