package com.massky.sraum.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.massky.sraum.R
import com.massky.sraum.activity.CustomDefineDaySceneActivity
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by masskywcy on 2017-05-16.
 */
class BindBtnListAdapter(context: Context?, list: List<Map<*, *>?>, againAutoSceneListener:AgainAutoSceneListener?) : BaseAdapter<Any?>(context, list) {
    private var lists: List<Map<*, *>> = ArrayList()
    private val againAutoSceneListener: AgainAutoSceneListener?
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.bind_btn_list_item, null)
            viewHolderContentType!!.txt_again_autoscene = convertView.findViewById<View>(R.id.txt_again_autoscene) as TextView
            viewHolderContentType.img_again_autoscene = convertView.findViewById<View>(R.id.img_again_autoscene) as ImageView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        val type = lists.get(position).get("type") as String
        val name = lists.get(position).get("name") as String
        when (type) {
            "0" -> viewHolderContentType!!.img_again_autoscene!!.visibility = View.GONE //
            "1" -> viewHolderContentType!!.img_again_autoscene!!.visibility = View.VISIBLE //
        }
        viewHolderContentType!!.txt_again_autoscene!!.setText(lists.get(position).get("name").toString())
        val finalViewHolderContentType = viewHolderContentType
        convertView!!.setOnClickListener {
            //                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);
            for (i in lists.indices) {
                (lists.get(i) as HashMap<String, Any>).put("type", "0")
                if (i == position) {
                    if (finalViewHolderContentType.img_again_autoscene!!.visibility == View.VISIBLE) {
                        (lists.get(i) as HashMap<String, Any>).put("type", "0")
                    } else {
                        (lists.get(i) as HashMap<String, Any>).put("type", "1")
                    }
                }
            }
            notifyDataSetChanged()
            when (name) {
                "自定义" -> context.startActivity(Intent(context, CustomDefineDaySceneActivity::class.java))
                else -> againAutoSceneListener?.again_auto_listen(position)
            }

            //跳转到时间界面
        }
        return convertView
    }

    internal inner class ViewHolderContentType {
        var img_again_autoscene: ImageView? = null
        var txt_again_autoscene: TextView? = null
    }

    interface AgainAutoSceneListener {
        fun again_auto_listen(position: Int)
    }

    init {
        this.lists = list as List<Map<*, *>>
        this.againAutoSceneListener = againAutoSceneListener
    }
}