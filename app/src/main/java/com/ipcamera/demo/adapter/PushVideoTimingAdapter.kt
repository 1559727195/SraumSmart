package com.ipcamera.demo.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.massky.sraum.R
import java.util.*

class PushVideoTimingAdapter(private val mContext: Context, pushVideoTimingListener: PushVideoTimingListener?) : BaseAdapter() {

    //    public ArrayList<Map<Integer, Integer>> movetiming;
    private val inflater: LayoutInflater
    private var holder: ViewHolder? = null
    private val pushVideoTimingListener: PushVideoTimingListener?
    private var list_camera_list: List<Map<*, *>> = ArrayList()
    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return list_camera_list.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return position
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    override fun getView(position1: Int, convertView: View, parent: ViewGroup): View {
        // TODO Auto-generated method stub
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.timing_video_item, null)
            holder = ViewHolder()
            holder!!.tv_timing_time = convertView.findViewById<View>(R.id.tv_timing_time) as TextView
            holder!!.tv_timing_week = convertView.findViewById<View>(R.id.tv_timing_week) as TextView
            holder!!.swipe_context = convertView.findViewById<View>(R.id.swipe_context) as RelativeLayout
            holder!!.swipe_right_menu = convertView.findViewById<View>(R.id.swipe_right_menu) as LinearLayout
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        //        Map<Integer, Integer> item = movetiming.get(position1);
//        int itemplan = item.entrySet().iterator().next().getValue();
//        Log.e("itemplan:*******", "" + itemplan);
        if (list_camera_list.size != 0) {
            val startTime = list_camera_list[position1]["startTime"] as String?
            val endTime = list_camera_list[position1]["endTime"] as String?
            val monday = list_camera_list[position1]["monday"] as String?
            val tuesday = list_camera_list[position1]["tuesday"] as String?
            val wednesday = list_camera_list[position1]["wednesday"] as String?
            val thursday = list_camera_list[position1]["thursday"] as String?
            val friday = list_camera_list[position1]["friday"] as String?
            val saturday = list_camera_list[position1]["saturday"] as String?
            val sunday = list_camera_list[position1]["sunday"] as String?
            val weekdays = getWeeks(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
            holder!!.tv_timing_week!!.text = weekdays
            //        int bStarttime = itemplan & 0x7ff;
//        int bEndTime = (itemplan >> 12) & 0x7ff;
            holder!!.tv_timing_time!!.text = "$startTime-$endTime"
        }

//        int plankey = item.entrySet().iterator().next().getKey();
//        int plantime = item.entrySet().iterator().next().getValue();
//        movetiming.get(position1).put(plankey, plantime);
//            ((SwipeMenuLayout) convertView).setAccountType("1");

//            ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {
//                @Override
//                public void onMenuClick(View v, int position) {
//                    //弹出删除框
//                    if (pushVideoTimingListener != null) {
//                        pushVideoTimingListener.delete(position1);
//                    }
//                }
//
//                @Override
//                public void onItemClick(View v, int position) {
//                    if (pushVideoTimingListener != null) {
//                        pushVideoTimingListener.onItemClick(position1);
//                    }
//                }
//
//                @Override
//                public void onInterceptTouch() {
//
//                }
//
//                @Override
//                public void onInterceptTouch_end() {
//
//                }
//            });
        holder!!.swipe_context!!.setOnClickListener { pushVideoTimingListener?.onItemClick(position1) }
        holder!!.swipe_right_menu!!.setOnClickListener { //弹出删除框
            showCenterDeleteDialog("是否删除该项",position1)
        }
        return convertView
    }


    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name: String, position: Int?) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(mContext).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        tv_title.text = name
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(mContext, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = mContext.resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {     pushVideoTimingListener?.delete(position!!) }
        confirm.setOnClickListener { dialog.dismiss() }
    }


    private fun getWeeks(monday: String?, tuesday: String?, wednesday: String?, thursday: String?, friday: String?, saturday: String?, sunday: String?): String {
        var weekdays = ""
        when (sunday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_seven)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (monday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_one)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (tuesday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_two)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (wednesday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_three)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (thursday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_four)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (friday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_five)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        when (saturday) {
            "1" -> weekdays = (weekdays
                    + mContext.resources.getString(R.string.plug_six)
                    + " ")
            "0" -> weekdays = (weekdays
                    + " ")
        }
        return weekdays
    }

    fun setList(list_camera_list: List<Map<*, *>>) {
        this.list_camera_list = list_camera_list
    }

    private inner class ViewHolder {
        var tv_timing_time: TextView? = null
        var tv_timing_week: TextView? = null
        var swipe_context: RelativeLayout? = null
        var swipe_right_menu: LinearLayout? = null
    }

    private fun getWeekPlan(time: Int): String {
        var weekdays = ""
        for (i in 24..30) {
            val weeks = time shr i and 1
            if (weeks == 1) {
                when (i) {
                    24 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_seven)
                            + " ")
                    25 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_one)
                            + " ")
                    26 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_two)
                            + " ")
                    27 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_three)
                            + " ")
                    28 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_four)
                            + " ")
                    29 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_five)
                            + " ")
                    30 -> weekdays = (weekdays
                            + mContext.resources.getString(R.string.plug_six)
                            + " ")
                    else -> {
                    }
                }
            }
        }
        return weekdays
    }

    private fun getTime(time: Int): String {
        if (time < 60) {
            return if (time < 10) "00:0$time" else "00:$time"
        }
        val h = time / 60
        val m = time - h * 60
        if (h < 10 && m < 10) {
            return "0$h:0$m"
        } else if (h > 9 && m < 10) {
            return "$h:0$m"
        } else if (h < 10 && m > 9) {
            return "0$h:$m"
        }
        return "$h:$m"
    }

    //        public void addPlan ( int key, int value){
    //            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    //            map.put(key, value);
    //            movetiming.add(map);
    //            int size = movetiming.size();
    //            for (int i = 0; i < size - 1; i++) {
    //                for (int j = 1; j < size - i; j++) {
    //                    Map<Integer, Integer> maps;
    //                    if (movetiming
    //                            .get(j - 1)
    //                            .entrySet()
    //                            .iterator()
    //                            .next()
    //                            .getKey()
    //                            .compareTo(
    //                                    movetiming.get(j).entrySet().iterator().next()
    //                                            .getKey()) > 0) {
    //                        maps = movetiming.get(j - 1);
    //                        movetiming.set(j - 1, movetiming.get(j));
    //                        movetiming.set(j, maps);
    //                    }
    //                }
    //            }
    //
    //        }
    //
    //        public void notify ( int key, int value){
    //            int size = movetiming.size();
    //            for (int i = 0; i < size; i++) {
    //                Map<Integer, Integer> map = movetiming.get(i);
    //                if (map.containsKey(key)) {
    //                    map.put(key, value);
    //                    break;
    //                }
    //            }
    //        }
    //
    //        public void removePlan ( int key){
    //            int size = movetiming.size();
    //            for (int i = 0; i < size; i++) {
    //                Map<Integer, Integer> map = movetiming.get(i);
    //                if (map.containsKey(key)) {
    //                    movetiming.remove(i);
    //                    break;
    //                }
    //            }
    //        }
    interface PushVideoTimingListener {
        fun delete(position: Int)
        fun onItemClick(position: Int)
    }

    init {
        // TODO Auto-generated constructor stub
        //        movetiming = new ArrayList<Map<Integer, Integer>>();
        list_camera_list = ArrayList()
        inflater = LayoutInflater.from(mContext)
        this.pushVideoTimingListener = pushVideoTimingListener
    }
}