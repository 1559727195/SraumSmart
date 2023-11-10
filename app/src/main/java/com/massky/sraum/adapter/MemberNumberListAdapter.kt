package com.massky.sraum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.massky.sraum.R
import com.mcxtzhang.swipemenulib.SwipeMenuLayout

/**
 * Created by masskywcy on 2017-05-16.
 */
class MemberNumberListAdapter //        this.list = list;
(context: Context?, list: List<String?>?//    private List<Map> list = new ArrayList<>();
 ) : BaseAdapter<Any?>(context, list) {
    private val areaNumber: String? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.member_num_listnew_item, null)
            viewHolderContentType.room_name_txt = convertView.findViewById<View>(R.id.room_name_txt) as TextView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.room_name_txt!!.text = getList()[position] as String
        return convertView!! //
    }

    interface RefreshListener {
        fun refresh()
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

}