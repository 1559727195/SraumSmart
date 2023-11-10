package com.massky.sraum.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.EditLinkDeviceResultActivity
import com.massky.sraum.view.ClearLengthEditText
import com.massky.sraum.view.PullToRefreshLayout
import com.massky.sraum.widget.SlideSwitchForSwitchDeleteButton
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.mcxtzhang.swipemenulib.SwipeMenuLayout.OnMenuClickListener
import okhttp3.Call
import java.io.Serializable
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class AutoSceneAdapter(context: Context, list: MutableList<Map<*, *>>, private val dialogUtil: DialogUtil, vibflag: Boolean, musicflag: Boolean, private val refreshListener: RefreshListener?) : BaseAdapter() {
    private val context: Context
    private var vibflag: Boolean
    private var musicflag: Boolean
    private var is_open_to_close = false
    private val view: View? = null
    private val refreshLayout: PullToRefreshLayout? = null
    private var list: MutableList<Map<*, *>> = ArrayList()
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any {
        return list[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_scene_item, null)
            viewHolderContentType.device_type_pic = convertView.findViewById<View>(R.id.device_type_pic) as ImageView
            viewHolderContentType.hand_device_content = convertView.findViewById<View>(R.id.hand_device_content) as TextView
            viewHolderContentType.hand_gateway_content = convertView.findViewById<View>(R.id.hand_gateway_content) as TextView
            viewHolderContentType.hand_scene_btn = convertView.findViewById<View>(R.id.slide_btn) as SlideSwitchForSwitchDeleteButton
            viewHolderContentType.swipemenu_layout = convertView.findViewById<View>(R.id.swipemenu_layout) as SwipeMenuLayout
            viewHolderContentType.btn_rename = convertView.findViewById<View>(R.id.btn_rename) as Button
            viewHolderContentType.rename_rel = convertView.findViewById<View>(R.id.rename_rel) as RelativeLayout
            viewHolderContentType.edit_rel = convertView.findViewById<View>(R.id.edit_rel) as Button
            viewHolderContentType.delete_rel = convertView.findViewById<View>(R.id.delete_rel) as Button
            viewHolderContentType.swipe_content_linear = convertView.findViewById<View>(R.id.swipe_content_linear) as LinearLayout
            viewHolderContentType.zhiding_btn = convertView.findViewById<View>(R.id.zhiding_btn) as Button
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.hand_device_content!!.text = list[position]["name"] as String?
        val finalViewHolderContentType = viewHolderContentType
        val authType = SharedPreferencesUtil.getData(context, "authType", "") as String

        when (authType) {
            "1" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = true
            "2" -> viewHolderContentType!!.swipemenu_layout!!.isSwipeEnable = false
        }

        var orderNo = list.get(position).get("orderNo")

        if (orderNo != null) when (orderNo) {
            "" -> {//未置顶
                viewHolderContentType.zhiding_btn!!.text = "置顶"
            }
            else -> {//已置顶
                viewHolderContentType.zhiding_btn!!.text = "取消置顶"
            }
        }

        val type = list[position]["type"].toString()
        when (type) {
            "100" -> viewHolderContentType!!.hand_gateway_content!!.text = "触发场景"
            "102" -> viewHolderContentType!!.hand_gateway_content!!.text = "定时场景"
        }
        val isUse = list[position]["isUse"].toString()
        if (isUse != null) {
            when (isUse) {
                "1" -> viewHolderContentType!!.hand_scene_btn!!.changeOpenState(true)
                "0" -> viewHolderContentType!!.hand_scene_btn!!.changeOpenState(false)
            }
        }
        viewHolderContentType!!.hand_scene_btn!!.setSlideSwitchListener {
            //滑动时，子view滑动时，父view不能滑动
            if (finalViewHolderContentType!!.hand_scene_btn!!.isOpen) { //启用和禁止启用，
//                    ToastUtil.showToast(context, "打开了");
                linkage_setting(list[position]["id"].toString(), "0")
            } else {
//                    ToastUtil.showToast(context, "关闭了");
                linkage_setting(list[position]["id"].toString(), "1")
            }
        }
        viewHolderContentType.edit_rel!!.setOnClickListener { //                ToastUtil.showToast(context, "编辑");
            goto_editlink(position)
            finalViewHolderContentType!!.swipemenu_layout!!.quickClose()
        }
        viewHolderContentType.btn_rename!!.setOnClickListener {
            showRenameDialog(list[position]["id"].toString(), list[position]["name"].toString(), position)
            finalViewHolderContentType!!.swipemenu_layout!!.quickClose()
        }

        viewHolderContentType.zhiding_btn!!.setOnClickListener {
            showCenterDeleteDialog(orderNo as String, "zhiding", list.get(position).get("id").toString(), list.get(position).get("name").toString())
            finalViewHolderContentType.swipemenu_layout!!.quickClose()
        }

        viewHolderContentType.delete_rel!!.setOnClickListener { //                ToastUtil.showToast(context, "删除");
            showCenterDeleteDialog(orderNo as String,"delete",list[position]["id"].toString(), list[position]["name"].toString())
            finalViewHolderContentType!!.swipemenu_layout!!.quickClose()
        }
        convertView!!.setOnClickListener {
            //                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);
        }
        (convertView as SwipeMenuLayout).setOnMenuClickListener(object : OnMenuClickListener {
            override fun onItemClick() {
                goto_editlink(position)
            }

            override fun onItemClick_By_btn(is_open_to_close1: Boolean) {
                is_open_to_close = is_open_to_close1
            }
        })
        return convertView
    }

    /**
     * 去编辑联动
     *
     * @param position
     */
    private fun goto_editlink(position: Int) {
        val intent = Intent(context, EditLinkDeviceResultActivity::class.java)
        val map = HashMap<Any?, Any?>()
        map["link_edit"] = true
        map["linkId"] = list[position]["id"].toString()
        map["linkName"] = list[position]["name"].toString()
        map["type"] = list[position]["type"].toString() //自动场景
        //                intent.putExtra("link_edit", true);
//                intent.putExtra("linkId", list.get(position).id);
        intent.putExtra("link_information", map as Serializable)
        context.startActivity(intent)
        SharedPreferencesUtil.saveData(context, "link_first", true)
    }

    //自定义dialog,自定义重命名dialog
    fun showRenameDialog(id: String, name: String, position: Int) {
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
        var tv_title: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        val edit_password_gateway = view.findViewById<View>(R.id.edit_password_gateway) as ClearLengthEditText
        edit_password_gateway.setText(name)
        edit_password_gateway.setSelection(edit_password_gateway.text!!.length)
        //        tv_title = (TextView) view.findViewById(R.id.tv_title);
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
        confirm.setOnClickListener { dialog.dismiss() }
        cancel.setOnClickListener(View.OnClickListener {
            if (edit_password_gateway.text.toString() == null || edit_password_gateway.text.toString().trim { it <= ' ' } == "") {
                ToastUtil.showToast(context, "云场景名称为空")
                return@OnClickListener
            }
            var isexist = false
            val `var` = edit_password_gateway.text.toString()
            for (i in list.indices) {
                if (i == position) {
                    continue
                }
                if (`var` == list[position]["name"].toString()) { //list.get(position).get("number").toString(), list.get(position).get("name").toString()
                    isexist = true
                }
            }
            if (isexist) {
                ToastUtil.showToast(context, "云场景名称已存在")
                return@OnClickListener
            }
            if (name == `var`) {
                dialog.dismiss()
            } else {
                linkage_rename(id, if (edit_password_gateway.text.toString() == null) "" else edit_password_gateway.text.toString()
                        , dialog)
            }
        })
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(orderNo: String?, action: String?, linkId: String, name: String?) {
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
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        name_gloud.visibility = View.VISIBLE
        tv_title.text = name
        when (action) {
            "delete" ->
                name_gloud.text = "确定要删除？"
            "zhiding" ->

                if (orderNo != null) when (orderNo) {
                    "" -> {//未置顶
                        name_gloud.text = "确定要置顶？"
                    }
                    else -> {//已置顶
                        name_gloud.text = "确定要取消置顶？"
                    }
                }

        }
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
        confirm.setOnClickListener { dialog.dismiss() }
        cancel.setOnClickListener {
            linkage_delete(orderNo, action, linkId, dialog)
        }
    }

    /**
     * 删除联动设备
     *
     * @param
     * @param dialog
     */
    private fun linkage_delete(orderNo: String?, action: String?, linkId: String, dialog: Dialog) {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        var api = ""
        when (action) {
            "delete" -> {
                mapdevice["linkId"] = linkId
                api = ApiHelper.sraum_deleteDeviceLink
            }
            "zhiding" -> {
                mapdevice["number"] = linkId
                api = ApiHelper.sraum_stickyAutoScene
                if (orderNo != null) when (orderNo) {
                    "" -> {//未置顶
                        mapdevice["action"] = "1"
                    }
                    else -> {//已置顶
                        mapdevice["action"] = "2"
                    }
                }
            }
        }
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(api
                , mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            linkage_delete(orderNo,action,linkId, dialog)
        }, context, dialogUtil) {
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
                ToastUtil.showToast(context, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(context, "number 不存在")
            }

            override fun fourCode() {
                ToastUtil.showToast(context, "操作失败")
            }

            override fun onSuccess(user: User) {
                dialog.dismiss()
                //                refreshLayout.autoRefresh();
                refreshListener?.refresh()
            }
        })
    }

    /**
     * 联动设置启用与否
     *
     * @param isUse
     */
    private fun linkage_setting(linkId: String, isUse: String) {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["areaNumber"] = areaNumber
        mapdevice["linkId"] = linkId
        mapdevice["isUse"] = isUse
        dialogUtil.loadDialog()
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_setDeviceLinkIsUse
                , mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            linkage_setting(linkId, isUse)
        }, context, dialogUtil) {
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
                ToastUtil.showToast(context, "areaNumber不正确")
            }

            override fun threeCode() {
                ToastUtil.showToast(context, "linkId 错误")
            }

            override fun onSuccess(user: User) {
//                refreshLayout.autoRefresh();
                ToastUtil.showToast(context, "操作成功")

//                        if (refreshListener != null)
//                            refreshListener.refresh();
                if (vibflag) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                }
                if (musicflag) {
                    LogUtil.i("铃声响起")
                    MusicUtil.startMusic(context, 1, "")
                } else {
                    MusicUtil.stopMusic(context, "")
                }
            }
        })
    }

    /**
     * 联动重命名
     *
     * @param
     * @param dialog
     */
    private fun linkage_rename(linkId: String, newName: String, dialog: Dialog) {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
        mapdevice["token"] = TokenUtil.getToken(context)
        mapdevice["linkId"] = linkId
        mapdevice["newName"] = newName
        mapdevice["areaNumber"] = areaNumber
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_updateDeviceLinkName
                , mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            linkage_rename(linkId, newName, dialog)
        }, context, dialogUtil) {
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

            override fun fourCode() {
                ToastUtil.showToast(context, "名字已存在")
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
                ToastUtil.showToast(context, """
     areaNumber
     不存在
     """.trimIndent())
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(context, "number 不存在")
            }

            override fun onSuccess(user: User) {
                dialog.dismiss()
                //                refreshLayout.autoRefresh();
                refreshListener?.refresh()
            }
        })
    }

    fun clear() {
        list.clear()
    }

    fun addAll(list: MutableList<Map<*, *>>) {
        this.list.clear()
        this.list = list
    }

    fun addFlag(vibflag1: Boolean, musicflag1: Boolean) {
        vibflag = vibflag1
        musicflag = musicflag1
    }

    internal inner class ViewHolderContentType {
         var zhiding_btn: Button?= null
        var device_type_pic: ImageView? = null
        var hand_device_content: TextView? = null
        var hand_gateway_content: TextView? = null
        var hand_scene_btn: SlideSwitchForSwitchDeleteButton? = null
        var swipemenu_layout: SwipeMenuLayout? = null
        var swipe_content_linear: LinearLayout? = null
        var rename_rel: RelativeLayout? = null
        var edit_rel: Button? = null
        var delete_rel: Button? = null
        var btn_rename: Button? = null
    }

    interface RefreshListener {
        fun refresh()
    }

    init {
//        this.list = list;
        this.list = list
        this.context = context
        this.vibflag = vibflag
        this.musicflag = musicflag
    }
}