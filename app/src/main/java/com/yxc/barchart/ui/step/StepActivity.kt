package com.yxc.barchart.ui.step

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.ConnApModeWifiActivity
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.yxc.barchart.tab.OnTabSelectListener
import com.yxc.barchart.tab.TopTabLayout
import com.yxc.barchart.ui.base.BaseChartFragment
import com.yxc.commonlib.util.ColorUtil
import okhttp3.Call

class StepActivity : BaseActivity() {
    private val mTitles = arrayOf("月", "年")
    var mTabLayout: TopTabLayout? = null
    var toolbar: Toolbar? = null
    var container: FrameLayout? = null
    private var currentFragment: BaseChartFragment? = null



    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null




    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back -> finish()
        }
    }

    override fun viewId(): Int {
        return R.layout.activity_step
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        initView()
        initTableLayout()
    }


    override fun onData() {

    }


    private fun initView() {
        mTabLayout = findViewById(R.id.topTabLayout)
        container = findViewById(R.id.container)
        //  toolbar = findViewById(R.id.toolBar);
//        toolbar.setTitle(TimeDateUtil.getDateStr(TimeDateUtil.localDateToTimestamp(LocalDate.now()), "M月dd日"));
//        toolbar.setNavigationIcon(R.drawable.ic_navigation_left_black_45dp);
//        setSupportActionBar(toolbar);
//
        switchTab(StepMonthFragment::class.java, "MonthFragment")
    }

    private fun initTableLayout() {
        mTabLayout!!.currentTab = 0
        mTabLayout!!.indicatorColor = ColorUtil.getResourcesColor(this, R.color.green)
        mTabLayout!!.textUnselectColor = ColorUtil.getResourcesColor(this, R.color.green)
        mTabLayout!!.dividerColor = ColorUtil.getResourcesColor(this, R.color.green)
        mTabLayout!!.setTabData(mTitles)
        mTabLayout!!.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
//                if (position == 0) { // 创建 月视图的数据
//                    // switchTab(StepDayFragment.class, "DayFragment");
//                } else if (position == 1) { //创建Week视图的数据
//                    //  switchTab(StepWeekFragment.class, "WeekFragment");
//                } else if (position == 2) { //创建Month视图的数据
//                    switchTab(StepMonthFragment::class.java, "MonthFragment")
//                } else if (position == 3) { //创建Year视图的数据
//                    switchTab(StepYearFragment::class.java, "YearFragment")
//                }

                when (position) {
                    0 -> switchTab(StepMonthFragment::class.java, "MonthFragment")
                    1 -> switchTab(StepYearFragment::class.java, "YearFragment")
                }
            }
            override fun onTabReselect(position: Int) {}
        })
        mTabLayout!!.currentTab = 0
    }

    fun switchTab(clz: Class<*>, tag: String?) {
        val ft = supportFragmentManager.beginTransaction()
        var fragment = supportFragmentManager.findFragmentByTag(tag) as BaseChartFragment?
        if (currentFragment != null) {
            currentFragment!!.resetSelectedEntry()
            ft.hide(currentFragment!!)
        }
        if (fragment == null) {
            try {
                fragment = clz.newInstance() as BaseChartFragment
                ft.add(R.id.container, fragment, tag)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
        } else {
            ft.show(fragment)
        }
        ft.commitAllowingStateLoss()
        currentFragment = fragment
    }

    override fun onStop() {
        super.onStop()
        if (currentFragment != null) {
            currentFragment!!.resetSelectedEntry()
        }
    }
}