package com.massky.sraum.adapter

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.percentlayout.widget.PercentRelativeLayout
import com.massky.sraum.R
import com.massky.sraum.User.device
import com.massky.sraum.view.ClearLengthEditText
import java.util.*

class NormalAdapter(context: Context, text: List<Map<*, *>>, deviceList: List<device>, backToMainListener: BackToMainListener?) : BaseAdapter() {
    private val mContext: Context
    private var index = 0
    private var power_index = 0
    private var mWatcher: myWatcher? = null
    private var mPowerWatcher: myWatcher? = null

    //    private String[] text = new String[]{};
    private var text: List<Map<*, *>> = ArrayList()
    private val backToMainListener: BackToMainListener?
    private var deviceList: List<device> = ArrayList()
    private val h: Handler? = null
    override fun getCount(): Int {
        return deviceList.size
    }

    override fun getItem(position: Int): Any {
        return deviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?,
                         parent: ViewGroup): View {
        var convertView = convertView
        Log.e("tag", parent.toString())
        var mHolder: Holder? = null
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_new, null)
            mHolder = Holder()
            mHolder.edtInput = convertView
                    .findViewById<View>(R.id.edtGroupContent) as ClearLengthEditText
            mHolder.button_one_id = convertView.findViewById<View>(R.id.button_one_id) as ImageView
            mHolder.first_txt = convertView.findViewById<View>(R.id.first_txt) as TextView
            mHolder.edit_one_power = convertView.findViewById<View>(R.id.edit_one_power) as ClearLengthEditText
            mHolder.power_first_rel = convertView.findViewById<View>(R.id.power_first_rel) as PercentRelativeLayout
            mHolder.power_edit_first_rel = convertView.findViewById<View>(R.id.power_edit_first_rel) as PercentRelativeLayout
            //power_edit_first_rel
            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as Holder
        }
        mHolder.button_one_id!!.setOnClickListener { //                ToastUtil.showToast(mContext, "position:" + position);
            //找设备
            backToMainListener!!.finddevice(position)
        } //先获取焦点
        val finalMHolder = mHolder
        air_onTouch(mHolder, position, finalMHolder)
        power_onTouch(mHolder, position, finalMHolder)
        air_name_edit(mHolder)
        power_name_edit(position, mHolder)
        air_text_show(position, mHolder)
        // power_text_show(position, mHolder)
        common_txt_show(mHolder, position)
        return convertView!!
    }

    private fun power_onTouch(mHolder: Holder, position: Int, finalMHolder: Holder?) {
        mHolder!!.edit_one_power!!.setOnTouchListener { v, event -> // TODO Auto-generated method stub
            if (event.action == MotionEvent.ACTION_UP) {
                power_index = position
            }
            (v.parent.parent as ViewGroup).descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            //                changeScrollView(finalMHolder.scrollView);
            backToMainListener?.srcolltotop(finalMHolder!!.edit_one_power)
            false
        }
    }

    private fun air_onTouch(mHolder: Holder?, position: Int, finalMHolder: Holder?) {
        mHolder!!.edtInput!!.setOnTouchListener { v, event -> // TODO Auto-generated method stub
            if (event.action == MotionEvent.ACTION_UP) {
                index = position
            }
            (v.parent.parent as ViewGroup).descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            //                changeScrollView(finalMHolder.scrollView);
            backToMainListener?.srcolltotop(finalMHolder!!.edtInput)
            false
        }
    }

    private fun air_text_show(position: Int, mHolder: Holder?) {
        mHolder!!.edtInput!!.clearFocus() //防止点击以后弹出键盘，重新getview导致的焦点丢失

        if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            mHolder.edtInput!!.requestFocus()
        }
    }

    private fun power_text_show(position: Int, mHolder: Holder?) {
        mHolder!!.edit_one_power!!.clearFocus() //防止点击以后弹出键盘，重新getview导致的焦点丢失

        if (power_index != -1 && power_index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            mHolder.edit_one_power!!.requestFocus()
        }
    }

    private fun common_txt_show(mHolder: Holder, position: Int) {
        mHolder.edtInput!!.setText(deviceList[position].name) //这一定要放在clearFocus()之后，否则最后输入的内容在拉回来时会消失
        mHolder.edtInput!!.setSelection(mHolder.edtInput!!.text!!.length)
        mHolder.edtInput!!.hint = text[position]["name"].toString()
        mHolder.first_txt!!.text = text[position]["name"].toString()
        mHolder.edit_one_power!!.setText(text[position]["energy"].toString())
    }

    private fun air_name_edit(mHolder: Holder?) {
        mHolder!!.edtInput!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

            //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
            val et = v as EditText
            if (mWatcher == null) {
                mWatcher = myWatcher("air")
            }
            if (hasFocus) {
                if (et.text.toString() == "") {
                    deviceList[index].name = ""
                    backToMainListener!!.sendToMain(deviceList)
                }
                et.addTextChangedListener(mWatcher) //设置edittext内容监听
            } else {
                et.removeTextChangedListener(mWatcher)
            }
        }
    }

    private fun power_name_edit(position: Int, mHolder: Holder?) {
        when (deviceList[position].type) {
            "18","113" -> {
               mHolder!!.power_edit_first_rel!!.visibility = View.GONE
                mHolder!!.power_first_rel!!.visibility = View.GONE
            }
            else -> {
                mHolder!!.power_edit_first_rel!!.visibility = View.VISIBLE
                mHolder!!.power_first_rel!!.visibility = View.VISIBLE
            }
        }
        mHolder!!.edit_one_power!!.setOnClickListener {
            power_text_show(position, mHolder)
        }
        mHolder!!.edit_one_power!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

            //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
            val et = v as EditText
            if (mPowerWatcher == null) {
                mPowerWatcher = myWatcher("power")
            }
            if (hasFocus) {
                if (et.text.toString() == "") {
                    deviceList[power_index].energy = ""
                    backToMainListener!!.sendToMain(deviceList)
                }
                et.addTextChangedListener(mPowerWatcher) //设置edittext内容监听
            } else {
                et.removeTextChangedListener(mPowerWatcher)
            }
        }
    }

    internal inner class myWatcher(type: String?) : TextWatcher {
        private var type: String? = null
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
            // TODO Auto-generated method stub
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            // TODO Auto-generated method stub
        }

        override fun afterTextChanged(s: Editable) {
//            text[index] = s.toString();//为输入的位置内容设置数组管理器，防止item重用机制导致的上下内容一样的问题
//            text.get(index).put("name",s.toString());
            when (type) {
                "power" -> deviceList[power_index].energy = s.toString()
                "air" -> deviceList[index].name = s.toString()
            }
            // deviceList[index].name = s.toString()
            backToMainListener!!.sendToMain(deviceList)
        }

        init {
            this.type = type
        }
    }

    private class Holder {
        //edit_one_power
        var edit_one_power: ClearLengthEditText? = null
        var edtInput: ClearLengthEditText? = null
        var button_one_id: ImageView? = null
        var first_txt: TextView? = null
        var power_first_rel: PercentRelativeLayout? = null
        var power_edit_first_rel:PercentRelativeLayout? = null
    }

    interface BackToMainListener {
        fun sendToMain(strings: List<device>?)
        fun finddevice(position: Int)
        fun srcolltotop(edtInput: ClearLengthEditText?)
    }

    init {
        this.text = text
        mContext = context
        this.backToMainListener = backToMainListener
        this.deviceList = deviceList
    }
}