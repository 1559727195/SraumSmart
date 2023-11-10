package com.massky.sraum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import com.massky.sraum.R
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class DetailDeviceHomeAdapter(context: Context, list: List<Map<*, *>>) : BaseAdapter() {
    private var list: List<Map<*, *>> = ArrayList()
    var context: Context
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_home_device, null)
            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
            val itemWidth = (context.resources.displayMetrics.widthPixels / 3 * 2 - dip2px(context, 6f)) / 2
            // Calculate the height by your scale rate, I just use itemWidth here
            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
            //  var params = convertView.layoutParams as AbsListView.LayoutParams
            var params = convertView.layoutParams
            if (params == null) {
                params = AbsListView.LayoutParams(
                        itemWidth,
                        itemWidth / 10 * 5)
                convertView.layoutParams = params
            } else {
                params.height = itemWidth //
                params.width = itemWidth
            }
            viewHolderContentType.title_room = convertView.findViewById<View>(R.id.title_room) as TextView
            viewHolderContentType.device_name = convertView.findViewById<View>(R.id.device_name) as TextView
            viewHolderContentType.status_txt = convertView.findViewById<View>(R.id.status_txt) as TextView
            viewHolderContentType.imageitem_id = convertView.findViewById<View>(R.id.image) as ImageView //title_room;//device_name,
            // device_action_or_father_name

            //linear_select
            viewHolderContentType.linear_select = convertView.findViewById<View>(R.id.linear_select) as LinearLayout
            //title_room;//device_name,

            //scene_img;//场景图片，scene_checkbox;//场景选中
            //
            viewHolderContentType.scene_img = convertView.findViewById<View>(R.id.scene_img) as ImageView
            viewHolderContentType.scene_checkbox = convertView.findViewById<View>(R.id.scene_checkbox) as CheckBox
            viewHolderContentType.rel_right = convertView.findViewById<View>(R.id.rel_right) as RelativeLayout
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.scene_img!!.visibility = View.GONE
        val mHolder = viewHolderContentType
        mHolder!!.device_name!!.text = if (list[position]["name"] == null) "" else list[position]["name"].toString()
        when (list[position]["type"].toString()) {
            "1", "111" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.status_txt!!.text = "开" //#E2C891
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_deng_active)
            } else {
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_deng)
            }
            "2","112" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_tiaoguang_70)
                mHolder.status_txt!!.text = "开" //#E2C891
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
            } else {
//                    mHolder.itemrela_id.setBackgro
// undResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_tiaoguang_70_sy)
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "3" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_kongtiaomb_70)
                mHolder.status_txt!!.text = "开" //#E2C891
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_kongtiaomb_70_sy)
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "4" -> {
                val curstatus = list[position]["status"].toString()
                if (curstatus == "1" || curstatus == "3" || curstatus == "4" || curstatus == "4" || curstatus == "5" || curstatus == "8") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    mHolder.status_txt!!.text = "开" //#E2C891
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_chuanglianmb_70)
                } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.status_txt!!.text = "关"
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_chuanglianmb_70_sy)
                }
            }
            "5" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "开" //#E2C891
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_xinfengmokuai_70)
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_xinfengmokuai_70_sy)
            }
            "6" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "开" //#E2C891
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_dinuanmokuai_70)
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_dinuanmokuai_70_sy)
            }
            "7" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_menci_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "打开" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_menci_70_sy)
                mHolder.status_txt!!.text = "关闭"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "8" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_rentiganying_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "有人" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_rentiganying_70_sy)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }

            "24" -> if (list[position]["status"].toString() == "1") {//吸顶人体感应
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_rentiganying_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "有人" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_rentiganying_70_sy)
                mHolder.status_txt!!.text = "无人"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "9" -> if (list[position]["status"].toString() == "1") {//
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_shuijin_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "报警" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_shuijin_40)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_shuijin_70_sy)
            }
            "10" -> {
                if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_pm25_70)
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    //                    mHolder.status_txt.setText("报警");//#E2C891
                    mHolder.status_txt!!.text = "PM2.5:" + list[position]["dimmer"].toString() //#E2C891
                } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_pm25_70_sy)
                    //                    mHolder.status_txt.setText("正常");
                    mHolder.status_txt!!.text = "PM2.5:" + list[position]["dimmer"].toString() //#E2C891
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                }
            }
            "23","115" -> {
                if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_pm25_70)
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    if( list[position]["mode"] != null) {
                        var mode = Integer.parseInt(list[position]["mode"].toString())
                        if(mode < 65536) {
                            mHolder.status_txt!!.text = "空气质量:" + list[position]["mode"].toString() //#E2C891
                        }
                        else
                            mHolder.status_txt!!.text = "空气质量:" //#E2C891
                    }
                    //                    mHolder.status_txt.setText("报警");//#E2C891
                } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_pm25_70)
                    //                    mHolder.status_txt.setText("正常");
                    if( list[position]["mode"] != null && !list[position]["mode"].toString().equals("")) {
                        var mode = Integer.parseInt(list[position]["mode"].toString())
                        if(mode < 65535) {
                            mHolder.status_txt!!.text = "空气质量:" + list[position]["mode"].toString() //#E2C891
                        }
                        else
                            mHolder.status_txt!!.text = "空气质量:" //#E2C891
                    }
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                }
            }
            "25" -> {//光照检测器
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_guangzhao_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "光照值:" + list[position]["dimmer"].toString() + "流明"//#E2C891
            }

            "11" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_jinjianniu_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "报警" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_jinjianniu_70_sy)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "12" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_toa_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "报警" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_toa_70_sy)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "13" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_yanwucgq_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "报警" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_yanwucgq_70_sy)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "14" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_tianranqibjq_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "报警" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_tianranqibjq_70_sy)
                mHolder.status_txt!!.text = "正常"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "15" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_zhinengmensuo_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "打开" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_zhinengmensuo_70_sy)
                mHolder.status_txt!!.text = "关闭"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "16" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_jixieshou_70)
                mHolder.status_txt!!.text = "打开"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            } else {
                //                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_jixieshou_70_sy)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "关闭" //#E2C891
            }
            "17","114" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_zhinengchazuo_70)
                mHolder.status_txt!!.text = "开"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
            } else {
                //                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_zhinengchazuo_70_sy)
                mHolder.status_txt!!.text = "关" //#E2C891
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }

            "18","113" -> {
                val curstatus = list[position]["status"].toString()
                if (curstatus == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    mHolder.status_txt!!.text = "开" //#E2C891
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_chuanglianmb_70)
                } else if (curstatus == "0") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.status_txt!!.text = "关"
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_chuanglianmb_70_sy)
                } else if (curstatus == "2") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.status_txt!!.text = "暂停"
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_chuanglianmb_70_sy)
                }
            }
            "202", "206" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_yaokongqi_70)
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "开" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_yaokongqi_70_sy)
                mHolder.status_txt!!.text = "关"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "101" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_shexiangtou_70)
                //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "正常" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_shexiangtou_70_sy)
                mHolder.status_txt!!.text = "断线"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "103" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_keshimenling_70)
                //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "正常" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_type_keshimenling_70_sy)
                mHolder.status_txt!!.text = "断线"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }

            "104" -> if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yingshisxt_70)
                //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                mHolder.status_txt!!.text = "正常" //#E2C891
            } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yingshisxt_70_sy)
                mHolder.status_txt!!.text = "断线"
                mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
            }
            "100" -> {
                viewHolderContentType!!.scene_img!!.visibility = View.VISIBLE
                if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_changjing)
                    //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    mHolder.status_txt!!.text = "" //#E2C891
                } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_changjing)
                    mHolder.status_txt!!.text = ""
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                }
            }
            "102" -> {
                if (list[position]["status"].toString() == "1") {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_pmmofang_70)
                    //                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                    mHolder.status_txt!!.text = "PM2.5:" + list[position]["dimmer"].toString() //#E2C891
                } else {
//                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
                    mHolder.imageitem_id!!.setImageResource(R.drawable.icon_pmmofang_70_sy)
                    mHolder.status_txt!!.text = "PM2.5:" + list[position]["dimmer"].toString()
                    mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                }
            }


            "19" -> {
                when (list[position]["status"].toString()) {
                    "1" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "上升" //#E2C891
                    }
                    "2" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "下降" //#E2C891
                    }
                    "0" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "暂停" //#E2C891
                    }
                }
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_zhinengshengjiang_70_sy)
            }
            "20" -> {
                when (list[position]["status"].toString()) {
                    "1" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "向左" //#E2C891
                    }
                    "2" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "向右" //#E2C891
                    }
                    "0" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "暂停" //#E2C891
                    }
                }
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_zhinengpingyi_70_sy)
            }
            "21" -> {
                when (list[position]["status"].toString()) {
                    "1" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "高位" //#E2C891
                    }
                    "2" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "中位" //#E2C891
                    }
                    "3" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "低位" //#E2C891
                    }
                    "0" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "暂停" //#E2C891
                    }
                }
                mHolder.imageitem_id!!.setImageResource(R.drawable.icon_zhinenggaozhongdii_70_sy)
            }
            "22" -> { //音乐面板
                when (list[position]["status"].toString()) {
                    "1" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark))
                        mHolder.status_txt!!.text = "播放" //#E2C891
                        mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yinyuemianban_70)
                    }
                    "2" -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "暂停" //#E2C891
                        mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yinyuemianban_70_sy)
                    }
                    "3" -> {//3
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "停止" //#E2C891
                        mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yinyuemianban_70_sy)
                    }
                    else -> {
                        mHolder.status_txt!!.setTextColor(context.resources.getColor(R.color.dark_gray_new))
                        mHolder.status_txt!!.text = "暂停" //#E2C891
                        mHolder.imageitem_id!!.setImageResource(R.drawable.icon_yinyuemianban_70_sy)
                    }
                }
            }
            else -> mHolder.imageitem_id!!.setImageResource(R.drawable.marklamph)
        }
        init_item_params(position, viewHolderContentType)
        return convertView!!
    }

    /**
     * 动态设置textview在布局中的显示方式layoutparams
     *
     * @param position
     * @param viewHolderContentType
     */
    private fun init_item_params(position: Int, viewHolderContentType: ViewHolderContentType?) {
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        when (list[position]["type"].toString()) {
            "100" -> {
                rlp.addRule(RelativeLayout.CENTER_IN_PARENT) //addRule参数对应RelativeLayout XML布局的属性
                viewHolderContentType!!.device_name!!.layoutParams = rlp
            }
            else -> {
                rlp.removeRule(RelativeLayout.CENTER_IN_PARENT)
                viewHolderContentType!!.device_name!!.layoutParams = rlp
            }
        }
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun setList(deviceList: List<Map<*, *>>) {
        list = deviceList
    }

    //    public void setList(List<Map> dataSourceList) {
    //        this.list = new ArrayList<>();
    //        this.list = dataSourceList;
    //        notifyDataSetChanged();
    //    }
    internal inner class ViewHolderContentType {
        var imageitem_id: ImageView? = null
        var title_room //device_name,device_action_or_father_name
                : TextView? = null
        var device_name: TextView? = null
        var status_txt: TextView? = null
        var linear_select: LinearLayout? = null
        var scene_img //场景图片
                : ImageView? = null
        var scene_checkbox //场景选中
                : CheckBox? = null
        var rel_right: RelativeLayout? = null
    }

    init {
        this.list = list
        this.context = context
    }
}