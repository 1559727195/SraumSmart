package com.massky.sraum.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.massky.sraum.R
import com.massky.sraum.view.XListView_ForMessage
import java.util.*

/**
 * Created by masskywcy on 2017-05-16.
 */
class SelectDeviceMessageAdapter(scroll_listener: ScrollSelectListener,
                                 xListView_scan: XListView_ForMessage, context: Context, list: List<Map<*, *>>) : BaseAdapter() {
    private val context: Context
    private val scroll_listener: ScrollSelectListener
    private val xListView_scan: XListView_ForMessage

    // 用来控制CheckBox的选中状况
    // 用来显示全部空的checkbox的
    var isCheckBoxVisiable = HashMap<Int, Boolean>()
    private var list: List<Map<*, *>> = ArrayList()

    //    public static HashMap<Integer, Boolean> getIsItemRead() {
    //
    //        return isItemRead;
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var viewHolderContentType: ViewHolderContentType? = null
        if (null == convertView) {
            viewHolderContentType = ViewHolderContentType()
            convertView = LayoutInflater.from(context).inflate(R.layout.select_devicemessage_item, null)
            viewHolderContentType.img_guan_read = convertView.findViewById<View>(R.id.img_guan_read) as ImageView
            viewHolderContentType.panel_scene_name_txt = convertView.findViewById<View>(R.id.panel_scene_name_txt) as TextView
            viewHolderContentType.execute_scene_txt = convertView.findViewById<View>(R.id.execute_scene_txt) as TextView
            viewHolderContentType.checkbox = convertView.findViewById<View>(R.id.checkbox) as CheckBox
            viewHolderContentType.gateway_name_txt = convertView.findViewById<View>(R.id.gateway_name_txt) as TextView
            //event_time_txt
            viewHolderContentType.event_time_txt = convertView.findViewById<View>(R.id.event_time_txt) as TextView
            //date_time_txt_top
            viewHolderContentType.date_time_txt_top = convertView.findViewById<View>(R.id.date_time_txt_top) as TextView
            convertView.tag = viewHolderContentType
        } else {
            viewHolderContentType = convertView.tag as ViewHolderContentType
        }
        viewHolderContentType.panel_scene_name_txt!!.text = list[position]["messageTitle"].toString()
        //gateway_name_txt
        viewHolderContentType!!.gateway_name_txt!!.text = list[position]["deviceName"].toString()


        //viewHolderContentType.event_time_txt!!.text = list[position]["eventTime"].toString()

        var show_date = if (list[position]["show_date"] == null) false else list[position]["show_date"] as Boolean
        var date = if (list[position]["date"] == null) "" else list[position]["date"] as String
        if (show_date) {
            viewHolderContentType.date_time_txt_top!!.visibility = View.VISIBLE
            viewHolderContentType.date_time_txt_top!!.text = date
        } else {
            viewHolderContentType.date_time_txt_top!!.visibility = View.GONE
        }

        viewHolderContentType.event_time_txt!!.text = list[position]["event_Time"].toString()



        if (isCheckBoxVisiable[position]!!) {
            viewHolderContentType.checkbox!!.visibility = View.VISIBLE
            viewHolderContentType.event_time_txt!!.visibility = View.GONE
        } else {
            viewHolderContentType.checkbox!!.visibility = View.GONE
            viewHolderContentType.event_time_txt!!.visibility = View.VISIBLE
        }

//        if(getIsSelected().get(position) != null) {
//            viewHolderContentType.checkbox.setChecked(getIsSelected().get(position));
//        }
        val ischecked = list[position]["ischecked"] as Boolean
        viewHolderContentType.checkbox!!.isChecked = ischecked
        val readStatus = list[position]["readStatus"].toString()
        if (readStatus != null) {
            when (readStatus) {
                "1" -> viewHolderContentType.img_guan_read!!.setImageResource(R.drawable.icon_yidu)
                "0" -> viewHolderContentType.img_guan_read!!.setImageResource(R.drawable.icon_weidu)
            }
        }
        return convertView!!
    }


    private fun scroll_nasy() {
        var isFirstClick: Boolean? = true
        var lastY: Float? = -1F
//        xListView_scan!!.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//
//            Log.e("scroll", "scroll_nasy: " + "scrollX:" + scrollX + ",scrollY:"
//            +",oldScrollX:" + oldScrollX + ",oldScrollY:" + oldScrollY)
//
//        }


        val scrollListener1: AbsListView.OnScrollListener = object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    //autoPlay()
                }

            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

                Log.e("scroll", "onScroll: " + "firstVisibleItem:" + firstVisibleItem + "," +
                        "visibleItemCount:" + visibleItemCount +
                        ",totalItemCount:" + totalItemCount)

                if (firstVisibleItem <= totalItemCount - 1) {
                    if (scroll_listener != null) {
                        if (list.size == 0) return
                        scroll_listener.scroll_select_date(list[firstVisibleItem]["date"] as String
                                , if (list[firstVisibleItem]["show_date"] == null) false else list[firstVisibleItem]["show_date"] as Boolean)
                    }
                }


//                if (xListView_scan.getChildCount() <= 0) {
//                    return
//                }
//
//                if (!owner.isShown() || !owner.isAttachedToWindow()) {
//                    return false
//                }
//
//
//                val location_xListView = IntArray(2)
//                xListView_scan.getLocationOnScreen(location_xListView)
//                val top = location_xListView[1]
//                val bottom: Int = top + xListView_scan.getHeight()
//
//
//
//
//                val location = IntArray(2)
//                owner.getLocationOnScreen(location)
//
//                val center: Int = location[1] + owner.getHeight() / 2
//
//
//                //承载视频播放画面的ViewGroup它需要至少一半的大小 在RecyclerView上下范围内
//
//                //承载视频播放画面的ViewGroup它需要至少一半的大小 在RecyclerView上下范围内
//                return center >= rvLocation.first && center <= rvLocation.second


            }
        }


        // xListView_scan!!.getAdapter().registerDataSetObserver(mDataObserver)
        xListView_scan!!.setOnScrollListener(scrollListener1)
        // xListView_scan!!.setOnScrollListener(scrollListener1)


//
//        xListView_scan!!.setOnTouchListener({ v, event ->
//            if (isFirstClick!!) { //只有第一次进来的时候用获取位置的方法给lastY赋值，后面的值都是上一次的move坐标
//                //如果不做此判断，每次的lasty和movey是相同的值，这是因为在此处获取的的y值其实就是move的值，
//                //是因为在listview中，down事件是默认传递进去给条目的，在此处无法响应down事件。
//                lastY = event.y
//                isFirstClick = false //初始值是true，此处置为false。
//            }
//            when (event.action) {
//                MotionEvent.ACTION_MOVE -> {
//                    val moveY = event.y
//                    Log.e("moveY_START", moveY.toString() + "")
//                    if (moveY + DipUtil.dipToPixel(context,16.0f)  < lastY!!) {//向上滑动
//                        view_middle!!.visibility = View.VISIBLE
//                    } else {//向下滑动
//                        view_middle!!.visibility = View.GONE
//                    }
//                    lastY = moveY
//                }
//            }
//            false
//        })
    }

    interface ScrollSelectListener {
        fun scroll_select_date(string: String, show_date: Boolean)
    }


    fun setList(list: List<Map<*, *>>) {
        this.list = list
        notifyDataSetChanged()
    }

    internal inner class ViewHolderContentType {
        var img_guan_read: ImageView? = null
        var panel_scene_name_txt: TextView? = null
        var execute_scene_txt: TextView? = null
        var checkbox: CheckBox? = null
        var gateway_name_txt: TextView? = null
        var event_time_txt: TextView? = null
        var date_time_txt_top: TextView? = null
    }

    init {
        isCheckBoxVisiable = HashMap()
        this.list = list
        this.context = context
        this.scroll_listener = scroll_listener
        this.xListView_scan = xListView_scan
        scroll_nasy()
    }
}