package com.massky.sraum.activity

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.massky.sraum.R
import com.massky.sraum.adapter.SelectZigbeeDeviceAdapter
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView

/**
 * Created by zhu on 2018/1/3.
 */
class SelectZigbeeDeviceActivityNew : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.newstype)
    var newstype: TabLayout? = null

    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null

    private var adapter:SelectZigbeeDeviceAdapter? = null

    var list_title = ArrayList<String>()

    override fun viewId(): Int {
        return R.layout.add_device_act_new
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        initforum_view()
        init_select_zigbee_data()
    }

    private fun init_select_zigbee_data() {

        list_title.add("ZigBee设备")
        list_title.add("Wi-Fi设备")


         adapter!!.setData(list_title)
        newstype!!.removeAllTabs()
        for (tabs1 in list_title) {
            newstype!!.addTab(newstype!!.newTab().setText(tabs1))
        }
        newstype!!.scrollTo(0, 0)
    }

    private fun initforum_view() {
        viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(newstype))
        viewPager!!.setOffscreenPageLimit(1)
        newstype!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        adapter = SelectZigbeeDeviceAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT)
        viewPager!!.setAdapter(adapter)
    }

    override fun onData() {

    }


    override fun onEvent() {
        back!!.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
        }
    }

}