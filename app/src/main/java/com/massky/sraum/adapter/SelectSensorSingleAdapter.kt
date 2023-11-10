package com.massky.sraum.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.massky.sraum.R
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class SelectSensorSingleAdapter(context: AppCompatActivity, list: List<Map<*, *>>, listint: List<Int>, listintwo: List<Int>, selectSensorListener:SelectSensorListener) : BaseAdapter() {
    private var list: List<Map<*, *>> = ArrayList()
    private var listint: List<Int> = ArrayList()
    private var listintwo: List<Int> = ArrayList()
    private var temp = -1
    private var activity //上下文
            : AppCompatActivity

    //
    //    // 初始化isSelected的数据
    //    private void initDate() {
    //        for (int i = 0; i < list_bool.size(); i++) {
    //            getIsSelected().put(i, false);
    //        }
    //    }
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.select_sensor_item, null)
            viewHolderContentType.img_guan_scene = convertView.findViewById<View>(R.id.img_guan_scene) as ImageView
            viewHolderContentType.panel_scene_name_txt = convertView.findViewById<View>(R.id.panel_scene_name_txt) as TextView
            viewHolderContentType.execute_scene_txt = convertView.findViewById<View>(R.id.execute_scene_txt) as TextView
            viewHolderContentType.checkbox = convertView.findViewById<View>(R.id.checkbox) as CheckBox
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = convertView.findViewById<View>(R.id.gateway_name_txt) as TextView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.panel_scene_name_txt!!.text = list[position]["name"] as String?
        viewHolderContentType!!.gateway_name_txt!!.text = list[position]["boxName"] as String?
        //        switch ((String) list.get(position).get("type")) {
//            case "AD02":
//                viewHolderContentType.gateway_name_txt.setVisibility(View.GONE);
//                break;
//            default:
//                viewHolderContentType.gateway_name_txt.setVisibility(View.VISIBLE);
//                break;
//        }
        viewHolderContentType.checkbox!!.id = position //对checkbox的id进行重新设置为当前的position
        viewHolderContentType.checkbox!!.setOnCheckedChangeListener { buttonView, isChecked ->

            //把上次被选中的checkbox设为false
            if (isChecked) { //实现checkbox的单选功能,同样适用于radiobutton
                if (temp != -1) {
                    //找到上次点击的checkbox,并把它设置为false,对重新选择时可以将以前的关掉
//                        CheckBox tempCheckBox = (CheckBox) activity.findViewById(temp);
//                        if (tempCheckBox != null)
//                            tempCheckBox.setChecked(false);
                }
                temp = buttonView.id //保存当前选中的checkbox的id值
            } else {
//                    CheckBox tempCheckBox = (CheckBox) activity.findViewById(temp);
//                    if (tempCheckBox != null)
//                        tempCheckBox.setChecked(false);
            }
            notifyDataSetChanged()
        }
        //System.out.println("temp:"+temp);
        //System.out.println("position:"+position);
        if (position == temp) { //比对position和当前的temp是否一致
//            viewHolderContentType.checkbox.setChecked(true);
//            viewHolderContentType.checkbox.toggle();
            if (viewHolderContentType.checkbox!!.isChecked) {
                viewHolderContentType.img_guan_scene!!.setImageResource(listintwo[position])
                viewHolderContentType.panel_scene_name_txt!!.setTextColor(activity.resources.getColor(R.color.gold_color))
                if (selectSensorListener != null) selectSensorListener!!.selectsensor(position)
            } else {
                viewHolderContentType.img_guan_scene!!.setImageResource(listint[position])
                viewHolderContentType.panel_scene_name_txt!!.setTextColor(activity.resources.getColor(R.color.black_color))
            }
        } else {
            viewHolderContentType.checkbox!!.isChecked = false
            viewHolderContentType.img_guan_scene!!.setImageResource(listint[position])
            viewHolderContentType.panel_scene_name_txt!!.setTextColor(activity.resources.getColor(R.color.black_color))
        }
        return convertView
    }

    fun setLists(list_hand_scene: List<Map<*, *>>, listint: List<Int>, listintwo: List<Int>) {
        list = list_hand_scene
        this.listint = listint
        this.listintwo = listintwo
    }

    //    public static HashMap<Integer, Boolean> getIsSelected() {
    //        return isSelected;
    //    }
    //
    //    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
    //        SelectSensorSingleAdapter.isSelected = isSelected;
    //    }
    class ViewHolderContentType {
        var img_guan_scene: ImageView? = null
        var panel_scene_name_txt: TextView? = null
        var execute_scene_txt: TextView? = null
        var checkbox: CheckBox? = null
        var gateway_name_txt: TextView? = null
    }

    var selectSensorListener: SelectSensorListener?

    interface SelectSensorListener {
        fun selectsensor(position: Int)
    }

    companion object {
        // 用来控制CheckBox的选中状况
        private val isSelected = HashMap<Int, Boolean>()
    }

    init {
        this.list = list
        this.listint = listint
        this.listintwo = listintwo
        activity = context
        this.selectSensorListener = selectSensorListener
        activity = context
        //        isSelected = new HashMap<Integer, Boolean>();
//        initDate();
    }
}