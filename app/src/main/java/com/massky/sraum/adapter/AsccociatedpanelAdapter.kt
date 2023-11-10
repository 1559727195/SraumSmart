package com.massky.sraum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.massky.sraum.R
import com.massky.sraum.Util.LogUtil

/**
 * Created by masskywcy on 2017-03-22.
 */
//关联面板适配器管理
class AsccociatedpanelAdapter(context: Context?, list: MutableList<HashMap<String, Any>?>, checkList: List<Boolean>) : BaseAdapter<Any?>(context, list as List<Any?>?) {
    private var checkList: List<Boolean>
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var mHolder: ViewHolder? = null
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.addsignitem, null)
            mHolder = ViewHolder()
            mHolder.imageone = convertView.findViewById<View>(R.id.imageone) as ImageView
            mHolder.macname_id = convertView.findViewById<View>(R.id.macname_id) as TextView
            mHolder.cb = convertView.findViewById<View>(R.id.checkbox) as CheckBox
            convertView.tag = mHolder
        } else {
            mHolder = convertView.tag as ViewHolder
        }
        val type: String = (list[position] as HashMap<String, Any>)["type"].toString().trim { it <= ' ' }
        val name: String = (list[position] as HashMap<String, Any>)["name"].toString().trim { it <= ' ' }
        LogUtil.eLength("数据查看", name + "数据查看" + type)
        when (type) {
            "A201" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_yijiandk_40)
            "A202" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_liangjiandk_40)
            "A203" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sanjiandk_40)
            "A204" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sijiandk_40)
            "A302" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sijiandk_40)
            "A301" -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sijiandk_40)
            "AD11","ADA1" -> {
                mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_yikaimb_40)
            }
            "AD12","ADA2" -> {
                mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_liangkaimb_40)
            }
            "AD13","ADA3" -> {
                mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sankaimb_40)
            }
            else -> mHolder!!.imageone!!.setImageResource(R.drawable.icon_type_sijiandk_40)
        }
        mHolder.macname_id!!.setText(name)
        mHolder.cb!!.isChecked = checkList[position]
        return convertView!!
    }

    fun setLists(checkList1: List<Boolean>) {
        checkList = checkList1
    }

    internal inner class ViewHolder {
        var imageone: ImageView? = null
        var macname_id: TextView? = null
        var cb: CheckBox? = null
    }

    init {
        this.list = list as List<Any?>?
        this.checkList = checkList
    }
}