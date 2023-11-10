package com.yxc.barchart.ui.step

import android.view.View
import com.yxc.barchart.ui.base.BaseChartFragment
import com.yxc.chartlib.entrys.BarEntry
import com.yxc.chartlib.listener.RecyclerItemGestureListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author yxc
 * @since 2019-05-13
 */
abstract class BaseStepFragment : BaseChartFragment() {
    protected var visibleEntries: MutableList<BarEntry>? = null
    protected var mRateSelectChangedListener: OnRateSelectChangedListener? = null
    protected var mItemGestureListener: RecyclerItemGestureListener? = null

    interface OnRateSelectChangedListener {
        fun onDayChanged(visibleEntries: List<BarEntry?>?)
        fun onMonthChanged(visibleEntries: List<BarEntry?>?)
        fun onWeekSelectChanged(visibleEntries: List<BarEntry?>?)
        fun onYearSelectChanged(visibleEntries: List<BarEntry?>?)
    }

    fun setOnRateSelectChangedListener(listener: OnRateSelectChangedListener?) {
        mRateSelectChangedListener = listener
    }

    //防止 Fragment重叠
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null) {
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
        }
    }


    protected fun setVisibleEntries(barEntries: ArrayList<BarEntry>?) {
        if (null == visibleEntries) {
            visibleEntries = ArrayList()
        } else {
            visibleEntries!!.clear()
        }
        visibleEntries!!.addAll(barEntries!!)
        displayDateAndRate()
    }

//    protected fun setVisibleEntries(barEntries: List<BarEntry>?) {
//        if (null == visibleEntries) {
//            visibleEntries = ArrayList()
//        } else {
//            visibleEntries!!.clear()
//        }
//        visibleEntries!!.addAll(barEntries!!)
//        displayDateAndRate()
//    }

    override fun resetSelectedEntry() {
        if (mItemGestureListener != null) {
            mItemGestureListener!!.resetSelectedBarEntry()
        }
    }

    abstract fun displayDateAndRate()
    abstract fun scrollToCurrentCycle() //回到当前周期
}