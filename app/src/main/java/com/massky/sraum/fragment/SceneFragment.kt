package com.massky.sraum.fragment

import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.google.android.material.tabs.TabLayout
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.activity.EditLinkDeviceResultActivity
import com.massky.sraum.activity.SelectSensorActivity
import com.massky.sraum.activity.SelectiveLinkageActivity
import com.massky.sraum.adapter.DynamicFragmentViewPagerAdapter
import com.massky.sraum.base.BaseFragment1
import com.massky.sraum.event.MyDialogEvent
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import com.ypy.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.Serializable
import java.lang.reflect.Field
import java.util.*

/**
 * Created by zhu on 2017/11/30.
 */
class SceneFragment : BaseFragment1() {
    private val _fragments: MutableList<Fragment> = ArrayList()

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.add_scene)
    var add_scene: ImageView? = null

    @JvmField
    @BindView(R.id.paixu_img)
    var paixu_img: ImageView? = null
    private val _navItemLayouts: Array<LinearLayout>? = null
    private var handSceneFragment: HandSceneFragment? = null
    private var autoSceneFragment: AutoSceneFragment? = null
    private val views: Array<View>? = null
    private var popupWindow: PopupWindow? = null

    @JvmField
    @BindView(R.id.tab_FindFragment_title)
    var tab_FindFragment_title: TabLayout? = null

    @JvmField
    @BindView(R.id.vp_FindFragment_pager)
    var vp_FindFragment_pager: ViewPager? = null

    //project_select
    @JvmField
    @BindView(R.id.project_select)
    var project_select: TextView? = null


    private val list_smart_frag: List<Fragment>? = null
    private var list_title: MutableList<String>? = null
    private var mCurrentPageIndex = 0
    private var fragmentViewPagerAdapter: DynamicFragmentViewPagerAdapter? = null
    private var dialogUtil: DialogUtil? = null
    private var manuallyCount: String? = null
    private var autoCount: String? = null
    private var intfirst = 0
    private var authType: String? = null
    private var isleft = true
    private var popupWindow_sort: PopupWindow? = null
    override fun onData() {
        //成员，业主accountType->addrelative_id
        val accountType = SharedPreferencesUtil.getData(activity, "accountType", "") as String
        when (accountType) {
            "1" -> add_scene!!.visibility = View.VISIBLE
            "2" -> add_scene!!.visibility = View.GONE
        }
        get_area_name()
    }

    private fun get_area_name() {
        val areaName = if (SharedPreferencesUtil.getData(activity, "areaName", "")
                == null) "" else SharedPreferencesUtil.getData(activity, "areaName", "") as String


        if (areaName.equals("")) {
            project_select!!.text = "场景"
        } else {
            var string_name: String? = areaName.substring(0, areaName.lastIndexOf("("))
            project_select!!.text = "场景" + "(" + string_name + ")"
        }
    }

    override fun onEvent() {
        add_scene!!.setOnClickListener(this)
        paixu_img!!.setOnClickListener(this)
    }

    override fun onEvent(eventData: MyDialogEvent) {}
    override fun viewId(): Int {
        return R.layout.scene_frag
    }

    override fun onView(view: View) {
        StatusUtils.setFullToStatusBar(activity) // StatusBar.
        EventBus.getDefault().register(this)
        //        initView();
        intfirst = 1
        dialogUtil = DialogUtil(activity)
        initControls()
    }

    override fun onResume() {
        super.onResume()
        Thread(Runnable { sraum_getAllScenesCount() }).start()
        authType = SharedPreferencesUtil.getData(activity, "authType", "") as String
        when (authType) {
            "1" -> add_scene!!.visibility = View.VISIBLE
            "2" -> add_scene!!.visibility = View.GONE
        }
        get_area_name()
        change_select()
    }

    /**
     * 选择切换
     */
    private fun change_select() {
        when (fragmentViewPagerAdapter!!.currentPageIndex) {
            0 -> if (!isleft) { //判断当前viewpager选中的是否是leftFragment,
                vp_FindFragment_pager!!.currentItem = 1
            }
            1 -> if (isleft) { //判断当前viewpager选中的是否是leftFragment,
                vp_FindFragment_pager!!.currentItem = 0
            }
        }
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            init_tab_layout()
        }
    }

    /**
     * 获取第二页场景详情
     */
    private fun get_scene_second_page() {
        val event = MyEvent()
        event.msg = "scene_second"
        //...设置event
        EventBus.getDefault().post(event)
    }
    /**
     * 获取场景数量
     */
    /**
     * 获取网关在线状态
     */
    private fun sraum_getAllScenesCount() {
        val map = HashMap<Any?, Any?>()
        val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        map["token"] = TokenUtil.getToken(activity)
        //        if (dialogUtil != null) {
//            dialogUtil.loadDialog();
//        }
        MyOkHttp.postMapString(ApiHelper.sraum_getAllScenesCount, map, object : Mycallback(AddTogglenInterfacer { //这个是获取togglen来刷新数据
            sraum_getAllScenesCount()
        }, activity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                manuallyCount = user.manuallyCount
                autoCount = user.autoCount
                handler.sendEmptyMessage(0)
            }
        })
    }

    /**
     * 初始化tablayout的数据加载
     */
    private fun init_tab_layout() {
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = ArrayList()
        manuallyCount = if (manuallyCount == null) "0" else manuallyCount
        autoCount = if (autoCount == null) "0" else autoCount
        (list_title as ArrayList<String>).add("手动($manuallyCount)")
        (list_title as ArrayList<String>).add("自动($autoCount)")
        if (tab_FindFragment_title == null) return
        val tabCount = tab_FindFragment_title!!.tabCount
        for (i in 0 until tabCount) {
            //这里tab可能为null 根据实际情况处理吧
            val tab = tab_FindFragment_title!!.getTabAt(i)
            //这里使用到反射，拿到Tab对象后获取Class
            tab!!.text = (list_title as ArrayList<String>).get(i)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_scene -> showPopWindow()
            R.id.paixu_img -> showPopSortingWindow()
        }
    }

    /**
     * 初始化各控件
     *
     * @param
     */
    private fun initControls() {
        handSceneFragment = HandSceneFragment.newInstance()
        autoSceneFragment = AutoSceneFragment.newInstance() //new PropertyFragment () 物业
        _fragments.add(handSceneFragment!!)
        _fragments.add(autoSceneFragment!!)

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = ArrayList()
        manuallyCount = if (manuallyCount == null) "0" else manuallyCount
        autoCount = if (autoCount == null) "0" else autoCount
        (list_title as ArrayList<String>).add("手动($manuallyCount)")
        (list_title as ArrayList<String>).add("自动($autoCount)")

        //设置TabLayout的模式
        tab_FindFragment_title!!.tabMode = TabLayout.MODE_FIXED
        //        tab_FindFragment_title.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        for (i in 0..1) {
            tab_FindFragment_title!!.addTab(tab_FindFragment_title!!.newTab().setText((list_title as ArrayList<String>).get(i)))
        }
        //        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(3)));

//        fAdapter = new Find_Smart_Home_Adapter(getSupportFragmentManager(),list_smart_frag,list_title);
        fragmentViewPagerAdapter = DynamicFragmentViewPagerAdapter(activity!!.supportFragmentManager,
                vp_FindFragment_pager, _fragments, list_title)
        //viewpager加载adapter
        vp_FindFragment_pager!!.adapter = fragmentViewPagerAdapter
        vp_FindFragment_pager!!.offscreenPageLimit = 2 //设置这句话的好处就是在viewapger下可以同时刷新3个fragment
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tab_FindFragment_title!!.setupWithViewPager(vp_FindFragment_pager)
        //tab_FindFragment_title.set
        setPageChangeListener()
        tab_FindFragment_title!!.post { setIndicator_new(tab_FindFragment_title) }
    }

    /**
     * 添加tablayout指引器事件监听
     *
     * @param tab_findFragment_title
     */
    private fun setIndicator_new(tab_findFragment_title: TabLayout?) {
        val tabCount = tab_findFragment_title!!.tabCount
        for (i in 0 until tabCount) {
            //这里tab可能为null 根据实际情况处理吧
            val tab = tab_findFragment_title.getTabAt(i)
            //这里使用到反射，拿到Tab对象后获取Class
            val c: Class<*> = tab!!.javaClass
            try {
                //c.getDeclaredField 获取私有属性。

                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                val field = c.getDeclaredField("view") ?: continue
                field.isAccessible = true
                val view = field[tab] as View ?: continue
                view.tag = i
                view.setOnClickListener { //这里就可以根据业务需求处理事件了。
                    mCurrentPageIndex = view.tag as Int
                    vp_FindFragment_pager!!.setCurrentItem(mCurrentPageIndex, false)
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    //更新ViewPager的Title信息
    private fun upgrate_title() {
        if (fragmentViewPagerAdapter != null) {
            for (i in 0..1) fragmentViewPagerAdapter!!.setPageTitle(i, "")
        }
    }

    private val index = 0
    private fun setPageChangeListener() {
        vp_FindFragment_pager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
//                mCurrentViewPagerName = mChannelNames.get(position);
//                mCurrentPageIndex = position;
                when (position) {
                    1 -> isleft = false
                    0 -> isleft = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    /**
     * 通过反射调整TabLayout指引器的宽度
     *
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
    fun setIndicator(tabs: TabLayout, leftDip: Int, rightDip: Int) {
        val tabLayout: Class<*> = tabs.javaClass
        var tabStrip: Field? = null
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        tabStrip!!.isAccessible = true
        var llTab: LinearLayout? = null
        try {
            llTab = tabStrip[tabs] as LinearLayout
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip.toFloat(), Resources.getSystem().displayMetrics).toInt()
        val right = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip.toFloat(), Resources.getSystem().displayMetrics).toInt()
        for (i in 0 until llTab!!.childCount) {
            val child = llTab.getChildAt(i)
            child.setPadding(0, 0, 0, 0)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            params.leftMargin = left
            params.rightMargin = right
            child.layoutParams = params
            child.invalidate()
            child.tag = i
            child.setOnClickListener {
                val position = child.tag as Int
                //                    vp_FindFragment_pager.setCurrentItem(position,false);
                Log.e("robin debug", "position:$position") //监听viewpager+Tablayout -》item点击事件
            }
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopWindow() {
        try {
            val view = LayoutInflater.from(activity).inflate(
                    R.layout.add_scene_pop_lay, null)
            //add_hand_scene_txt, add_auto_scene_txt
            val add_auto_scene_txt = view.findViewById<View>(R.id.add_auto_scene_txt) as TextView
            val add_hand_scene_txt = view.findViewById<View>(R.id.add_hand_scene_txt) as TextView
            popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow!!.isFocusable = true
            popupWindow!!.isOutsideTouchable = true
            val cd = ColorDrawable(0x00ffffff) // 背景颜色全透明
            popupWindow!!.setBackgroundDrawable(cd)
            val location = IntArray(2)
            add_scene!!.getLocationOnScreen(location) //获得textview的location位置信息，绝对位置
            popupWindow!!.animationStyle = R.style.style_pop_animation // 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f) // 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            val xoff = DisplayUtil.dip2px(activity, 20f)
            popupWindow!!.showAsDropDown(add_scene, 0, 0)
            //            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow!!.setOnDismissListener {
                popupWindow = null // 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
            add_hand_scene_txt.setOnClickListener {
                popupWindow!!.dismiss()
                //                    startActivity(new Intent(getActivity(), AddHandSceneActivity.class));
                var intent: Intent? = null
                val map_link = HashMap<Any?, Any?>()
                map_link["type"] = "101"
                map_link["deviceType"] = ""
                map_link["deviceId"] = ""
                map_link["name"] = "手动执行"
                map_link["action"] = "执行"
                map_link["condition"] = ""
                map_link["minValue"] = ""
                map_link["maxValue"] = ""
                map_link["boxName"] = ""
                map_link["name1"] = "手动执行"
                val add_condition = SharedPreferencesUtil.getData(activity, "add_condition", false) as Boolean
                if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity::class.java)
                    intent = Intent(activity, EditLinkDeviceResultActivity::class.java)
                    intent.putExtra("sensor_map", map_link as Serializable)
                    startActivity(intent)
                } else {
                    intent = Intent(activity,
                            SelectiveLinkageActivity::class.java)
                    intent.putExtra("link_map", map_link as Serializable)
                    startActivity(intent)
                }
                isleft = true
            }
            add_auto_scene_txt.setOnClickListener { //添加自动场景
                popupWindow!!.dismiss()
                //                    startActivity(new Intent(getActivity(), AddAutoSceneActivity.class));
                val intent = Intent(activity, SelectSensorActivity::class.java)
                intent.putExtra("type", "100||102") //自动场景
                startActivity(intent)
                isleft = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopSortingWindow() {
        try {
            val view = LayoutInflater.from(activity).inflate(
                    R.layout.add_sort_pop_lay, null)
            //add_hand_scene_txt, add_auto_scene_txt
            val add_auto_scene_txt = view.findViewById<View>(R.id.add_auto_scene_txt) as TextView
            val add_hand_scene_txt = view.findViewById<View>(R.id.add_hand_scene_txt) as TextView
            popupWindow_sort = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow_sort!!.isFocusable = true
            popupWindow_sort!!.isOutsideTouchable = true
            val cd = ColorDrawable(0x00ffffff) // 背景颜色全透明
            popupWindow_sort!!.setBackgroundDrawable(cd)
            val location = IntArray(2)
            paixu_img!!.getLocationOnScreen(location) //获得textview的location位置信息，绝对位置
            popupWindow_sort!!.animationStyle = R.style.style_pop_animation // 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f) // 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            val xoff = DisplayUtil.dip2px(activity, 15f)
            popupWindow_sort!!.showAsDropDown(paixu_img, -location[0] / 3 - xoff, 0)
            //            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow_sort!!.setOnDismissListener {
                popupWindow_sort = null // 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
            add_hand_scene_txt.setOnClickListener { //正序
                popupWindow_sort!!.dismiss()
                SharedPreferencesUtil.saveData(activity, "order", "1")
                get_scene_second_page()
            }
            add_auto_scene_txt.setOnClickListener { //倒序
                popupWindow_sort!!.dismiss()
                SharedPreferencesUtil.saveData(activity, "order", "2")
                get_scene_second_page()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = activity!!.window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        activity!!.window.attributes = lp
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (intfirst == 1) {
                intfirst = 2
            } else {
                Thread(Runnable {
                    sraum_getAllScenesCount()
                    get_scene_second_page()
                }).start()
                authType = SharedPreferencesUtil.getData(activity, "authType", "") as String
                when (authType) {
                    "1" -> add_scene!!.visibility = View.VISIBLE
                    "2" -> add_scene!!.visibility = View.GONE
                }
                get_area_name()
            }
        }
    }

    @Subscribe
    fun onEvent(event: MyEvent) {
        val status = event.msg
        when (status) {
            "刷新" -> Thread(Runnable { sraum_getAllScenesCount() }).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        var ACTION_INTENT_RECEIVER = "com.massky.sraum.sceceiver"

        @JvmStatic
        fun newInstance(): SceneFragment {
            val newFragment = SceneFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            return newFragment
        }
    }
}