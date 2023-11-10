package com.yxc.barchart

import android.util.Log
import com.google.gson.Gson
import com.yxc.chartlib.attrs.BarChartAttrs
import com.yxc.chartlib.entrys.BarEntry
import com.yxc.commonlib.util.TimeDateUtil
import org.joda.time.LocalDate
import java.util.*

/**
 * @author yxc
 * @date 2019/4/10
 */
object TestData {
    const val VIEW_DAY = 0
    const val VIEW_WEEK = 1
    const val VIEW_MONTH = 2
    const val VIEW_YEAR = 3

    //  @Nullable
    //    public static List<BarEntry> getMonthEntries(@Nullable String step, @Nullable String currentTotal, @NotNull List<Map<?, ?>> listMonth, @Nullable BarChartAttrs mBarChartAttrs, @Nullable LocalDate currentLocalDate, int displayNumber, int size) {
    //        return null;
    //    }
    // 创建 月视图的数据
    fun getMonthEntries(step: String?, currentTotal: String?, listMonth: List<Map<*, *>?>?, attrs: BarChartAttrs, localDate: LocalDate?, length: Int, originEntrySize: Int): List<BarEntry> {
        val entries: MutableList<BarEntry> = ArrayList()
        Collections.reverse(listMonth)
        var timestamp = TimeDateUtil.changZeroOfTheDay(localDate)
        for (i in originEntrySize until originEntrySize + listMonth!!.size) {
            if (i > originEntrySize) {
                timestamp = timestamp - TimeDateUtil.TIME_DAY
            }
            val mult = 10f
            var value: Float
            //            if (i > 500) {
//                value = (float) (Math.random() * 30000) + mult;
//            } else if (i > 400) {
//                value = (float) (Math.random() * 3000) + mult;
//            } else if (i > 300) {
//                value = (float) (Math.random() * 20000) + mult;
//            } else if (i > 200) {
//                value = (float) (Math.random() * 5000) + mult;
//            } else if (i > 100) {
//                value = (float) (Math.random() * 300) + mult;
//            } else {
//                value = (float) (Math.random() * 6000) + mult;
//            }


            // value = (float) (Math.random() * 1);
            var data = listMonth[i]!!["value"]
            var double_data = if (data == null) "0" else data as String
            value = double_data.toFloat()
            var type = BarEntry.TYPE_XAXIS_THIRD
            val localDateEntry = TimeDateUtil.timestampToLocalDate(timestamp)
            val isLastDayOfMonth = TimeDateUtil.isLastDayOfMonth(localDateEntry)
            val dayOfYear = localDateEntry.dayOfYear
            // Log.d("TestData", "dayOfYear:" + dayOfYear + " localDate:" + localDateEntry);
            if (isLastDayOfMonth && (dayOfYear + 1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SPECIAL
            } else if (isLastDayOfMonth) {
                type = BarEntry.TYPE_XAXIS_FIRST
            } else if ((dayOfYear + 1) % attrs.xAxisScaleDistance == 0) {
                type = BarEntry.TYPE_XAXIS_SECOND
            }
            if (TimeDateUtil.isFuture(localDateEntry)) {
                value = 0f
            }
            val barEntry = BarEntry(i.toFloat(), value, timestamp, type, localDateEntry.toString())
            barEntry.localDate = localDateEntry
            entries.add(barEntry)
        }
        Collections.sort(entries)
        //Log.e("TestData", "getMonthEntries: " + new Gson().toJson(entries));
        //Log.e("TestData", "getMonthEntries: " + Gson().toJson(entries) )
        return entries
    }

    //创建Week视图的数据
    fun createWeekEntries(localDate: LocalDate, length: Int, originEntrySize: Int): List<BarEntry> {
        val entries: MutableList<BarEntry> = ArrayList()
        var timestamp = TimeDateUtil.changZeroOfTheDay(localDate)
        for (i in originEntrySize until originEntrySize + length) {
            if (i > originEntrySize) {
                timestamp = timestamp - TimeDateUtil.TIME_DAY
            }
            val mult = 10f
            var value = 0f
            value = if (i > 500) {
                (Math.random() * 30000).toFloat() + mult
            } else if (i > 400) {
                (Math.random() * 3000).toFloat() + mult
            } else if (i > 300) {
                (Math.random() * 20000).toFloat() + mult
            } else if (i > 200) {
                (Math.random() * 5000).toFloat() + mult
            } else if (i > 100) {
                (Math.random() * 300).toFloat() + mult
            } else {
                (Math.random() * 6000).toFloat() + mult
            }
            value = Math.round(value).toFloat()
            var type = BarEntry.TYPE_XAXIS_SECOND
            val localDateEntry = TimeDateUtil.timestampToLocalDate(timestamp)
            val isSunday = TimeDateUtil.isSunday(localDateEntry)
            if (isSunday) {
                type = BarEntry.TYPE_XAXIS_FIRST
            }
            if (TimeDateUtil.isFuture(localDateEntry)) {
                value = 0f
            }
            val barEntry = BarEntry(i.toFloat(), value, timestamp, type, localDate.toString())
            barEntry.localDate = localDateEntry
            entries.add(barEntry)
        }
        Collections.sort(entries)
        return entries
    }

    //创建 Day视图的数据
    fun createDayEntries(attrs: BarChartAttrs, timestamp: Long, length: Int, originEntrySize: Int, zeroValue: Boolean): List<BarEntry> {
        var timestamp = timestamp
        val entries: MutableList<BarEntry> = ArrayList()
        for (i in originEntrySize until length + originEntrySize)
        Collections.sort(entries)
        return entries
    }

    //创建 year视图的数据
    @JvmStatic
    fun createYearEntries(step: String?, currentTotal: String?, listYear: List<Map<*, *>?>?, attrs: BarChartAttrs, localDate: LocalDate?, length: Int, originEntrySize: Int): List<BarEntry> {
        var localDate = localDate
        val entries: MutableList<BarEntry> = ArrayList()
        Collections.reverse(listYear)
        for (i in originEntrySize until listYear!!.size + originEntrySize) {
            if (i > originEntrySize) {
                localDate = localDate!!.minusMonths(1)
            }
            val mult = 10f
            var value = 0f
//            value = if (i > 500) {
//                (Math.random() * 30000).toFloat() + mult
//            } else if (i > 400) {
//                (Math.random() * 3000).toFloat() + mult
//            } else if (i > 300) {
//                (Math.random() * 20000).toFloat() + mult
//            } else if (i > 200) {
//                (Math.random() * 5000).toFloat() + mult
//            } else if (i > 100) {
//                (Math.random() * 300).toFloat() + mult
//            } else {
//                (Math.random() * 6000).toFloat() + mult
//            }

            var data = listYear[i]!!["value"]
            var double_data = if (data == null) "0" else data as String
            value = double_data.toFloat()
           // value = Math.round(value).toFloat()
            var type = BarEntry.TYPE_XAXIS_SECOND
            val isLastMonthOfTheYear = TimeDateUtil.isLastMonthOfTheYear(localDate)
            if (isLastMonthOfTheYear) {
                type = BarEntry.TYPE_XAXIS_FIRST
            }
            val timestamp = TimeDateUtil.changZeroOfTheDay(localDate)
            if (TimeDateUtil.isFuture(localDate)) {
                value = 0f
            }
            val barEntry = BarEntry(i.toFloat(), value, timestamp, type, localDate.toString())
            barEntry.localDate = localDate
            entries.add(barEntry)
        }
        Collections.sort(entries)
        return entries
    }
}