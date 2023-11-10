package com.massky.sraum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.massky.sraum.R
import java.util.*
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by masskywcy on 2017-05-16.
 */
class SelectLinkageAdapter(context: Context, list: CopyOnWriteArrayList<ConcurrentMap<*, *>>, listint: List<Int>, listintwo: List<Int>) : BaseAdapter() {
    private var list = CopyOnWriteArrayList<ConcurrentMap<*, *>>()
    private var listint: List<Int> = ArrayList()
    private var listintwo: List<Int> = ArrayList()
    private val context: Context

    // 初始化isSelected的数据
    private fun initDate() {
        for (i in list.indices) {
            isSelected[i] = false
        }
    }

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
            convertView = LayoutInflater.from(context).inflate(R.layout.select_sensor_item, null)
            viewHolderContentType.img_guan_scene = convertView.findViewById<View>(R.id.img_guan_scene) as ImageView
            viewHolderContentType.panel_scene_name_txt = convertView.findViewById<View>(R.id.panel_scene_name_txt) as TextView
            viewHolderContentType.execute_scene_txt = convertView.findViewById<View>(R.id.execute_scene_txt) as TextView
            viewHolderContentType.checkbox = convertView.findViewById<View>(R.id.checkbox) as CheckBox
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = convertView.findViewById<View>(R.id.gateway_name_txt) as TextView
            viewHolderContentType.wifi_name = convertView.findViewById<View>(R.id.wifi_name) as TextView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }


//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.img_guan_scene.setImageResource(element);
//        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("name").toString());


//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, DeviceExcuteOpenActivity.class);
////                context.startActivity(intent);
//
//
//            }
//        });
        viewHolderContentType.panel_scene_name_txt!!.text = list[position]["panelName"].toString()
        val panelType = list[position]["panelType"].toString()
        if (panelType != null) {
            viewHolderContentType!!.gateway_name_txt!!.text = list[position]["boxName"].toString()
            //when (panelType) {
//            "AA02" -> {
//                viewHolderContentType!!.gateway_name_txt!!.visibility = View.GONE
//                viewHolderContentType.wifi_name!!.visibility = View.VISIBLE
//                viewHolderContentType.panel_scene_name_txt!!.visibility = View.GONE
//                viewHolderContentType.wifi_name!!.text = list[position]["panelName"].toString()
//            }
//            else -> viewHolderContentType!!.gateway_name_txt!!.text = list[position]["boxName"].toString()
        }
        viewHolderContentType!!.checkbox!!.isChecked = isSelected[position]!!
        if (isSelected[position]!!) {
            viewHolderContentType.img_guan_scene!!.setImageResource(listintwo[position])
        } else {
            viewHolderContentType.img_guan_scene!!.setImageResource(listint[position])
        }
        to_activity(position, viewHolderContentType.img_guan_scene)
        return convertView
    }

    private fun to_activity(position: Int, img_guan_scene: ImageView?) {
        when (list[position]["panelType"].toString()) {
            "A201", "A202", "A203", "A204", "A301", "A302", "A303", "A311", "A312", "A313", "A321", "A322", "A331",
            "A401", "A411", "A412", "A413", "A414",
            "A501", "A511",
            "A801", "A901", "AB01", "A902",
            "AB04", "AC01", "AD01", "AD02", "B001", "B101", "网关", "B201", "AA02", "AA03", "AA04", "A611", "A601",
            "A711", "A701", "B301", "B401", "B402", "B403", "A2A1", "A2A2", "A2A3", "A2A4" -> {
            }
            else -> img_guan_scene!!.visibility = View.GONE
        }
        img_guan_scene!!.visibility = View.VISIBLE

    }

    fun setlist(list: CopyOnWriteArrayList<ConcurrentMap<*, *>>, listint: List<Int>, listintwo: List<Int>) {
        this.list = list
        initDate()
        this.listint = listint
        this.listintwo = listintwo
    }

    internal inner class ViewHolderContentType {
        var img_guan_scene: ImageView? = null
        var panel_scene_name_txt: TextView? = null
        var execute_scene_txt: TextView? = null
        var checkbox: CheckBox? = null
        var gateway_name_txt: TextView? = null
        var wifi_name: TextView? = null
    }

    companion object {
        // 用来控制CheckBox的选中状况
        var isSelected = HashMap<Int, Boolean>()

        fun setIsSelected(isSelected: HashMap<Int?, Boolean?>?) {
            var isSelected = isSelected
            isSelected = isSelected
        }
    }

    init {
        this.list = list
        this.listint = listint
        this.listintwo = listintwo
        isSelected = HashMap()
        initDate()
        this.context = context
    }
}