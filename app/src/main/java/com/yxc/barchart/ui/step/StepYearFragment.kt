package com.yxc.barchart.ui.step

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AddTogenInterface.AddTogglenInterfacer
import com.google.gson.Gson
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.yxc.barchart.TestData.createYearEntries
import com.yxc.barchart.formatter.XAxisYearFormatter
import com.yxc.barchart.formatter.YearHighLightMarkValueFormatter
import com.yxc.chartlib.attrs.BarChartAttrs
import com.yxc.chartlib.barchart.BarChartAdapter
import com.yxc.chartlib.barchart.SpeedRatioLayoutManager
import com.yxc.chartlib.component.XAxis
import com.yxc.chartlib.component.YAxis
import com.yxc.chartlib.entrys.BarEntry
import com.yxc.chartlib.itemdecoration.BarChartItemDecoration
import com.yxc.chartlib.listener.RecyclerItemGestureListener
import com.yxc.chartlib.listener.SimpleItemGestureListener
import com.yxc.chartlib.util.ChartComputeUtil
import com.yxc.chartlib.util.DecimalUtil
import com.yxc.chartlib.view.BarChartRecyclerView
import com.yxc.commonlib.util.TextUtil
import com.yxc.commonlib.util.TimeDateUtil
import okhttp3.Call
import org.joda.time.LocalDate
import java.text.DecimalFormat
import java.util.*

class StepYearFragment : BaseStepFragment(), View.OnClickListener {
    var recyclerView: BarChartRecyclerView? = null
    var txtLeftLocalDate: TextView? = null
    var txtRightLocalDate: TextView? = null

    var txt_local_show: TextView? = null

    var img_left: ImageView? = null
    var img_right: ImageView? = null
    var txt_layout: TextView? = null

    var textTitle: TextView? = null
    var txtCountStep: TextView? = null
    var rlTitle: RelativeLayout? = null
    var mBarChartAdapter: BarChartAdapter? = null
    var mEntries: MutableList<BarEntry>? = null
    var mItemDecoration: BarChartItemDecoration? = null
    var mYAxis: YAxis? = null
    var mXAxis: XAxis<BarChartAttrs>? = null
    private var displayNumber = 0
    private var mBarChartAttrs: BarChartAttrs? = null
    private var currentLocalDate: LocalDate? = null
    private val preEntrySize = 3
    // var mItemGestureListener: RecyclerItemGestureListener? = null

    private var dialogUtil: DialogUtil? = null

    private var device_map: Map<*, *> = HashMap<Any?, Any?>()

    private val list_year: MutableList<Map<*, *>> = java.util.ArrayList()

    private var step: String? = null
    private var currentTotal: String? = null

    //img_left


    //防止 Fragment重叠
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null) {
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
        }
    }

    var sel_months = 0
    var sel_year = 0
    var local_year = 0
    var ago_year = 0
    private fun initCustomTimePicker() {
        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        val selectedDate = Calendar.getInstance() //系统当前时间

//        val startDate = Calendar.getInstance()
//        startDate[2015, 1] = 31
//        val endDate = Calendar.getInstance()
//        endDate[2027, 2] = 31


//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        val startDate = Calendar.getInstance()
//        startDate[2015, 1] = 31
        val endDate = Calendar.getInstance()
        var sel_month = selectedDate[Calendar.MONTH]
        sel_year = selectedDate[Calendar.YEAR]
        local_year = sel_year
        ago_year = sel_year - 5
        endDate[selectedDate[Calendar.YEAR], sel_month] = 31
        sel_months = sel_month + 1

        var local_time = "$sel_year-$sel_months"

        // txt_layout!!.text = local_time
        //txt_layout!!.text = "$sel_year" + "年" + "$sel_months" + "月"
//        pick_data_txt!!.setText(local_time)

        txt_layout!!.text = "$sel_year" + "年"
    }

    /**
     * 获取设备能耗记录
     *
     * @param linkId
     */
    private fun sraum_getElectricityByDeviceId(deviceId: String?, searchType: String?, deviceType: String?,
                                               searchValue: String?,
                                               i: Int,
                                               action: String) {
        val map: MutableMap<String, Any?> = HashMap()
        var api: String? = null
        if (in_type != null) {
            when (in_type) {
                "area_in" -> {
                    map["areaNumber"] = areaNumber
                    api = ApiHelper.sraum_getElectricityByAreaId
                }
                "device_in" -> {
                    val areaNumber = SharedPreferencesUtil.getData(context, "areaNumber", "") as String
                    map["deviceId"] = deviceId
                    map["areaNumber"] = areaNumber
                    map["deviceId"] = deviceId
                    map["deviceType"] = deviceType
                    api = ApiHelper.sraum_getElectricityByDeviceId
                }
            }
        }
        map["token"] = TokenUtil.getToken(context)
        map["searchType"] = searchType
        map["searchValue"] = searchValue


        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { }, context, dialogUtil) {
                    override fun onError(call: Call, e: Exception, id: Int) {
                        super.onError(call, e, id)
                        //                        refresh_view.stopRefresh(false);
                    }

                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        /*     result：1-json 格式解析失败，100-成功，101-token 错误，102-
                             areaNumber 错误 103-deviceId 不正确,104-searchType 不正确
                             2. step:纵坐标步长(刻度 5 个，0 开始累加)
                             3. currentTotal:50
                             4. data:[
                             {日期：度数}
                             ]*/


                        list_year.clear()
                        for (us in user.data) {
                            val map = HashMap<Any?, Any?>()
                            map["date"] = us.date
                            //map["value"] = us.value
                            //var round = (Math.random() * i / 100)

                            var round = java.lang.Float.parseFloat(us.value)
                            val df = DecimalFormat("##0.00")
                            var round_str = df.format(round).toString()
                            map["value"] = round_str
                            list_year.add(map)
                        }

                        step = user.step
                        //step = (i).toString()
                        currentTotal = user.currentTotal
                        var message = Message.obtain()
                        //message.obj = mEntries!!.size
                        sel_year = i
                        when (action) {
                            "add" -> {
                                //  message.obj = TimeDateUtil.getDaysFromMonths("$sel_year", "$sel_months")
                                message.what = 0
                            }
                            "del" -> message.what = 1
                            "get" -> message.what = 2
                        }
                        Log.e("TAG", "onSuccess: " + Gson().toJson(list_year))
                        handler.sendMessage(message)
                    }

                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }


    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            left_right_btn()
            show_year_step(msg.what)
            reSizeYAxis()
            txt_local_show!!.text = currentTotal
            // txt_layout!!.text = currentLocalDate.toString()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = View.inflate(activity, R.layout.fragment_year_step, null)
        initView(view)
        initData()
        // reSizeYAxis()
        setListener()
        init_data_years()
        return view
    }

    var in_type: String? = null
    var areaNumber: String? = null
    var type: String? = null
    private fun init_data_years() {
//        device_map = activity!!.intent.getSerializableExtra("device_map") as Map<String, Any>
//        if (device_map != null)
//            type = if (device_map!!["type"] == null) "" else device_map!!["type"] as String

        //get_month_dates("1", "1", "2020-12", 12, "get")
        in_type = if (activity!!.intent.getSerializableExtra("in_type") == null) ""
        else activity!!.intent.getSerializableExtra("in_type") as String
        if (in_type != null) {
            when (in_type) {
                "area_in" -> {
                    areaNumber = activity!!.intent.getSerializableExtra("areaNumber") as String?
                }

                "device_in" -> {
                    device_map = activity!!.intent.getSerializableExtra("device_map") as Map<*, *>
                    if (device_map != null)
                        type = if (device_map!!["type"] == null) "" else device_map!!["type"] as String
                }
            }
        }
        get_year_dates("2", type, "$sel_year", sel_year, "get")
    }

    private fun initView(view: View) {
        rlTitle = view.findViewById(R.id.rl_title)
        txtLeftLocalDate = view.findViewById(R.id.txt_left_local_date)
        txtRightLocalDate = view.findViewById(R.id.txt_right_local_date)
        textTitle = view.findViewById(R.id.txt_layout)
        txtCountStep = view.findViewById(R.id.txt_count_Step)
        recyclerView = view.findViewById(R.id.recycler)
        mBarChartAttrs = recyclerView!!.mAttrs


        txt_local_show = view.findViewById(R.id.txt_local_show)

        img_left = view.findViewById(R.id.img_left)
        img_right = view.findViewById(R.id.img_right)
        txt_layout = view.findViewById(R.id.txt_layout)

        img_left!!.setOnClickListener(this)
        img_right!!.setOnClickListener(this)
    }

    private fun initData() {
        displayNumber = mBarChartAttrs!!.displayNumbers
        mEntries = ArrayList()
        val layoutManager = SpeedRatioLayoutManager(activity, mBarChartAttrs)
        mYAxis = YAxis(mBarChartAttrs)
        mXAxis = XAxis<BarChartAttrs>(mBarChartAttrs!!, displayNumber, XAxisYearFormatter())
        mItemDecoration = BarChartItemDecoration(mYAxis, mXAxis, mBarChartAttrs)
        mItemDecoration!!.setHighLightValueFormatter(YearHighLightMarkValueFormatter(0))
        recyclerView!!.addItemDecoration(mItemDecoration!!)


        mBarChartAdapter = BarChartAdapter(activity, mEntries, recyclerView, mXAxis, mBarChartAttrs)
        recyclerView!!.adapter = mBarChartAdapter
        recyclerView!!.layoutManager = layoutManager
        currentLocalDate = TimeDateUtil.getLastMonthOfTheYear(LocalDate.now())
        initCustomTimePicker()
        //show_year_step(2)
        init_weak_data_years()
    }


    private fun init_weak_data_years() {
        list_year.clear()
        for (i in 0..11) {
            val map = HashMap<Any?, Any?>()
            //map["date"] = us.date
            //map["value"] = us.value
            var round = (Math.random() * 0.01)

            // var round = java.lang.Float.parseFloat(us.value)
            val df = DecimalFormat("##0.00")
            var round_str = df.format(round).toString()
            map["value"] = round_str
            list_year.add(map)
        }
        step = "5"
        //step = (i).toString()
        currentTotal = "0"
        var message = Message.obtain()
        //message.obj = mEntries!!.size
        // sel_year = i
        message.what = 2

        // Log.e("TAG", "onSuccess: " + Gson().toJson(list_year))
        handler.sendMessage(message)
    }

    private fun show_year_step(int: Int) {

        //val list: List<Map<*, *>> = ArrayList()

        when (int) {
            0 -> currentLocalDate = currentLocalDate!!.plusMonths(list_year.size)
            1 -> currentLocalDate = currentLocalDate!!.minusMonths(list_year.size)
        }

        val barEntries = createYearEntries(step, currentTotal, list_year, mBarChartAttrs!!, currentLocalDate,
                list_year.size, 0)


        bindBarChartList(barEntries)
        // currentLocalDate = currentLocalDate!!.minusMonths(displayNumber * 5)
        setXAxis(list_year.size)


//        val barEntries = TestData.getMonthEntries(step, currentTotal, list_month, mBarChartAttrs!!, currentLocalDate,
//                list_month.size, 0)

        // Log.e("BarEntry", "initData: " + new Gson().toJson(barEntries));
        // bindBarChartList(barEntries);
        //  bindBarChartList(barEntries)
        // currentLocalDate = currentLocalDate.minusDays(3 * displayNumber);
        // setXAxis(list_month.size)
    }

    private fun left_right_btn() {
        if (sel_year < local_year) {
            img_right!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.next))
        } else {
            img_right!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.next_hui))
        }

        if (ago_year >= sel_year) {
            img_left!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.pre_hui))
        } else {
            img_left!!.setImageDrawable(context!!.resources.getDrawable(R.drawable.pre))
        }
    }


    private fun get_year_dates(searchType: String?,
                               deviceType: String?, searchValue: String?, i: Int, action: String) {
        if (dialogUtil != null)
            dialogUtil!!.loadDialog()
        Thread(Runnable {
            sraum_getElectricityByDeviceId(if (device_map == null || device_map!!.size == 0) "" else
                device_map!!["number"]!!.toString()
                    , searchType, deviceType,
                    searchValue, i, action)
        }).start()
    }


    private fun reSizeYAxis() {
//        recyclerView!!.scrollToPosition(preEntrySize)
//        val visibleEntries: List<BarEntry> = mEntries!!.subList(preEntrySize, preEntrySize + displayNumber + 1)

        val visibleEntries: List<BarEntry> = mEntries!!.subList(0, list_year.size)
        val yAxis = mYAxis!!.resetYAxis(mYAxis, DecimalUtil.getTheMaxNumber(visibleEntries), step)
        mBarChartAdapter!!.notifyDataSetChanged()
        if (yAxis != null) {
            mYAxis = yAxis
            mItemDecoration!!.setYAxis(mYAxis)
        }
        //displayDateAndStep(visibleEntries)
    }

    //滑动监听
    private fun setListener() {
        mItemGestureListener = RecyclerItemGestureListener(activity, recyclerView,
                object : SimpleItemGestureListener() {
                    private var isRightScroll = false
                    override fun onItemSelected(barEntry: BarEntry, position: Int) {
//                        if (null == barEntry || !barEntry.isSelected()) {
//                            rlTitle!!.visibility = View.VISIBLE
//                        } else {
//                            rlTitle!!.visibility = View.INVISIBLE
//                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        // 当不滚动时
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            //左滑
//                            if (recyclerView.canScrollHorizontally(1) && isRightScroll) {
//                                val entries = createYearEntries(currentLocalDate!!, displayNumber, mEntries!!.size)
//                                currentLocalDate = currentLocalDate!!.minusMonths(displayNumber)
//                                mEntries!!.addAll(entries)
//                                mBarChartAdapter!!.notifyDataSetChanged()
//                            }
//                            if (mBarChartAttrs!!.enableScrollToScale) {
//                                val scrollByDx = ChartComputeUtil.computeScrollByXOffset<BarEntry>(recyclerView, displayNumber, TestData.VIEW_YEAR)
//                                recyclerView.scrollBy(scrollByDx, 0)
//                            }
//                            resetYAxis(recyclerView)
//                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        //判断左滑，右滑时，ScrollView的位置不一样。
                        isRightScroll = if (dx < 0) {
                            true
                        } else {
                            false
                        }
                    }
                })
        recyclerView!!.addOnItemTouchListener(mItemGestureListener!!)
    }

    //重新设置Y坐标
    private fun resetYAxis(recyclerView: RecyclerView) {
        val yAxisMaxEntries = ChartComputeUtil.getVisibleEntries<BarEntry>(recyclerView)
        visibleEntries = yAxisMaxEntries.visibleEntries as MutableList<BarEntry>?
        mYAxis = YAxis.getYAxis(mBarChartAttrs, yAxisMaxEntries.yAxisMaximum, "")
        mItemDecoration!!.setYAxis(mYAxis)
    }

    private fun bindBarChartList(entries: List<BarEntry>) {
        if (null == mEntries) {
            mEntries = ArrayList()
        } else {
            mEntries!!.clear()
        }
        mEntries!!.addAll(entries)
    }

    private fun setXAxis(displayNumber: Int) {
        mXAxis = XAxis<BarChartAttrs>(mBarChartAttrs!!, displayNumber)
        mBarChartAdapter!!.setXAxis(mXAxis)
    }

    private fun displayDateAndStep(displayEntries: List<BarEntry>) {
        val rightBarEntry = displayEntries[0]
        val leftBarEntry = displayEntries[displayEntries.size - 1]
        txtLeftLocalDate!!.text = TimeDateUtil.getDateStr(leftBarEntry.timestamp, "yyyy-MM-dd HH:mm:ss")
        txtRightLocalDate!!.text = TimeDateUtil.getDateStr(rightBarEntry.timestamp, "yyyy-MM-dd HH:mm:ss")
        if (TimeDateUtil.isSameYear(leftBarEntry.timestamp, rightBarEntry.timestamp)) {
            textTitle!!.text = TimeDateUtil.getDateStr(leftBarEntry.timestamp, "yyyy年")
        } else {
            val beginDateStr = TimeDateUtil.getDateStr(leftBarEntry.timestamp, "yyyy/MM/dd")
            val endDateStr = TimeDateUtil.getDateStr(rightBarEntry.timestamp, "yyyy/MM/dd")
            val connectStr = " -- "
            textTitle!!.text = beginDateStr + connectStr + endDateStr
        }
        var count: Long = 0
        for (i in displayEntries.indices) {
            val entry = displayEntries[i]
            count += entry.y.toLong()
        }
        val averageStep = (count / displayEntries.size).toInt()
        val childStr = DecimalUtil.addComma(Integer.toString(averageStep))
        val parentStr = String.format(getString(R.string.str_count_step), childStr)
        val spannable = TextUtil.getSpannableStr(activity, parentStr, childStr, 24)
        txtCountStep!!.text = spannable
    }

    override fun resetSelectedEntry() {
        if (mItemGestureListener != null) {
            Log.d("DayFragment", " visibleHint")
            mItemGestureListener!!.resetSelectedBarEntry()
            rlTitle!!.visibility = View.VISIBLE
        }
    }

    override fun displayDateAndRate() {}
    override fun scrollToCurrentCycle() {}
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_left -> {
                delete_date()
            }
            R.id.img_right -> {
                add_date()
            }
        }
    }


    private fun add_date() {
        if (one_minute()) return
        var temp_year = sel_year
        if (temp_year < local_year) {
            temp_year += 1
        } else {
            return
        }
        val pair = get_common_small_year(temp_year)
        var local_time = pair.first
        temp_year = pair.second

        txt_layout!!.text = "$temp_year" + "年"
        get_year_dates("2", type, local_time, temp_year, "add")
    }


    private var lastClick: Long = 0 // 防止网络重新请求
    private fun one_minute(): Boolean {
        if (System.currentTimeMillis() - lastClick <= 800) {
            return true
        }
        lastClick = System.currentTimeMillis()
        return false
    }

    private fun delete_date() {
        if (one_minute()) return
        var temp_year = sel_year
        if (temp_year > ago_year) {
            temp_year -= 1
        } else {
            return
        }
        //var local_time = "$temp_year"
        val pair = get_common_small_year(temp_year)
        var local_time = pair.first
        temp_year = pair.second

        txt_layout!!.text = "$temp_year" + "年"
        get_year_dates("2", type, local_time, temp_year, "del")
    }


    private fun get_common_small_year(temp_year: Int): Pair<String, Int> {
        var temp_year1 = temp_year
        val selectedDate = Calendar.getInstance() //系统当前时间
        if (temp_year1 > selectedDate[Calendar.YEAR])
            temp_year1 = selectedDate[Calendar.YEAR]

        var local_time = "$temp_year1"
        return Pair(local_time, temp_year1)
    }
}