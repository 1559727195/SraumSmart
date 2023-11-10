package com.massky.sraum.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import com.massky.sraum.R
import com.massky.sraum.Util.DisplayUtil
import com.massky.sraum.Util.LogUtil
import com.massky.sraum.Util.SharedPreferencesUtil
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.adapter.FragmentViewPagerAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.base.Basecfragment
import com.massky.sraum.fragment.AlarmRecordFragment
import com.massky.sraum.fragment.OperationRecordsFragment
import com.massky.sraum.fragment.SceneFragment
import com.massky.sraum.fragment.SystemMessageFragment
import com.yanzhenjie.statusview.StatusUtils
import java.io.Serializable
import java.util.*

class MessageActivity : BaseActivity(), OnPageChangeListener {
    @JvmField
    @BindView(R.id.viewpager_id)
    var viewpager_id: ViewPager? = null

    @JvmField
    @BindView(R.id.alarm_record_rel_id)
    var alarm_record_rel_id: RelativeLayout? = null

    @JvmField
    @BindView(R.id.opera_record_rel_id)
    var opera_record_rel_id: RelativeLayout? = null

    @JvmField
    @BindView(R.id.system_message_rel_id)
    var system_message_rel_id: RelativeLayout? = null

    @JvmField
    @BindView(R.id.viewone)
    var viewone: View? = null

    @JvmField
    @BindView(R.id.viewtwo)
    var viewtwo: View? = null

    @JvmField
    @BindView(R.id.viewthree)
    var viewthree: View? = null

    //    @BindView(R.id.viewthree)
    //    View viewthree;
    @JvmField
    @BindView(R.id.addtxt_text_id)
    var addtxt_text_id: TextView? = null

    @JvmField
    @BindView(R.id.mactext_id)
    var mactext_id: TextView? = null

    @JvmField
    @BindView(R.id.scene_id)
    var scene_id: TextView? = null

    //system_message_id
    @JvmField
    @BindView(R.id.system_message_id)
    var system_message_id: TextView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    //linear_dot_operate
    @JvmField
    @BindView(R.id.linear_dot_operate)
    var linear_dot_operate: LinearLayout? = null


    private var fm: FragmentManager? = null
    private val list: MutableList<Fragment?> = ArrayList()

    //    private RoomFragment roomFragment;
    private val sceneFragment: SceneFragment? = null
    private val golden = Color.parseColor("#37CC99")
    private val black = Color.parseColor("#6f6f6f")
    private var adapter: FragmentViewPagerAdapter? = null
    private var flag = false
    private var devicemessagefragment: AlarmRecordFragment? = null

    private var popupWindow: PopupWindow? = null

    //

    //operationRecordsfragment
    private var operationRecordsfragment: OperationRecordsFragment? = null
    private var systemmessagefragment: SystemMessageFragment? = null
    private val common_setting_image: ImageView? = null
    override fun viewId(): Int {
        return R.layout.message_frag_lay
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        addtxt_text_id!!.visibility = View.VISIBLE
        addtxt_text_id!!.text = "编辑"
        LogUtil.i("这是oncreate方法", "onView: ")
        fm = supportFragmentManager
        addFragment()
        onclick()
        startState()
        addtxt_text_id!!.setOnClickListener(this)
        //        addViewid();
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun sendBroad(content: String) {
        val mIntent = Intent(MESSAGE_FRAGMENT)
        mIntent.putExtra("action", content)
        sendBroadcast(mIntent)
    }

    //    @Override
    //    public void onAttach(Activity activity) {
    //        super.onAttach(activity);
    //        myInterface = (MyInterface) activity;
    //    }
    //添加fragment
    private fun addFragment() {
        devicemessagefragment = AlarmRecordFragment.newInstance(object : AlarmRecordFragment.OnDeviceMessageFragListener {
            override fun ondevice_message_frag() {
                addtxt_text_id!!.text = "编辑"
                sendBroad("取消")
            }
        }
        )

        operationRecordsfragment = OperationRecordsFragment.newInstance(object : OperationRecordsFragment.OnDeviceMessageFragListener {
            override fun ondevice_message_frag() {
                addtxt_text_id!!.text = "编辑"
                sendBroad("取消")
            }
        }
        )

        //        roomFragment = new RoomFragment();
        systemmessagefragment = SystemMessageFragment.newInstance(
                object : SystemMessageFragment.OnDeviceMessageFragListener {
                    override fun ondevice_message_frag() {
                        addtxt_text_id!!.text = "编辑"
                        sendBroad("取消")
                    }
                })

        list.add(devicemessagefragment)
        list.add(operationRecordsfragment)
        list.add(systemmessagefragment)
        //        list.add(roomFragment);
        adapter = FragmentViewPagerAdapter(fm, viewpager_id, list)
        adapter!!.onExtraPageChangeListener = object : FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            override fun onExtraPageSelected(i: Int) {
                LogUtil.i("Extra...i:", i.toString() + "onExtraPageSelected: ")
                if (addtxt_text_id!!.text.equals("取消")) {
                    sendBroad("取消")
                    addtxt_text_id!!.text = "编辑"
                }
                when (i) {
                    0, 2 -> {
                        addtxt_text_id!!.visibility = View.VISIBLE
                        linear_dot_operate!!.visibility = View.GONE
                    }
                    1 -> {
                        val authType = SharedPreferencesUtil.getData(this@MessageActivity, "authType", "") as String
                        addtxt_text_id!!.visibility = View.GONE
                        when (authType) {
                            "1" -> linear_dot_operate!!.visibility = View.VISIBLE
                            "2" -> linear_dot_operate!!.visibility = View.GONE
                        }
                    }
                }
            }
        }
        viewpager_id!!.addOnPageChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
//        list.clear()
//        devicemessagefragment = AlarmRecordFragment()
//        //        roomFragment = new RoomFragment();
//        operationRecordsfragment = OperationRecordsFragment()
//        systemmessagefragment = SystemMessageFragment()
//        list.add(devicemessagefragment)
//        list.add(operationRecordsfragment)
//        list.add(systemmessagefragment)
//        //            list.add(roomFragment);
//        adapter = FragmentViewPagerAdapter(fm, viewpager_id, list)
//        startState()
//        addtxt_text_id!!.text = "编辑"
    }

    private fun onclick() {
        val on = MyOnclick()
        alarm_record_rel_id!!.setOnClickListener(on)
        //        roomrelative_id.setOnClickListener(on);
        opera_record_rel_id!!.setOnClickListener(on)
        system_message_rel_id!!.setOnClickListener(on)
        back!!.setOnClickListener(this)
        linear_dot_operate!!.setOnClickListener(this)
    }

    /**
     * 设置一个监听类
     */
    internal inner class MyOnclick : View.OnClickListener {
        override fun onClick(arg0: View) {
            clear()
            viewchange(arg0.id)
        }
    }

    private fun clear() {
        mactext_id!!.setTextColor(black)
        //        roomtext_id.setTextColor(black);
        scene_id!!.setTextColor(black)
        system_message_id!!.setTextColor(black)

        viewone!!.visibility = View.GONE
        viewtwo!!.visibility = View.GONE
        viewthree!!.visibility = View.GONE
        //        viewthree.setVisibility(View.GONE);
    }

    // 进行匹配设置一个方法，判断是否被点击
    private fun viewchange(num: Int) {
        when (num) {
            R.id.alarm_record_rel_id, 0 -> {
                mactext_id!!.setTextColor(golden)
                viewone!!.visibility = View.VISIBLE
                viewpager_id!!.currentItem = 0
            }
            R.id.opera_record_rel_id, 1 -> {
                scene_id!!.setTextColor(golden)
                viewtwo!!.visibility = View.VISIBLE
                viewpager_id!!.currentItem = 1
            }

            R.id.system_message_rel_id, 2 -> {
                system_message_id!!.setTextColor(golden)
                viewthree!!.visibility = View.VISIBLE
                viewpager_id!!.currentItem = 2
            }
            else -> {

            }
        }
    }

    private fun startState() {
        clear()
        viewchange(0)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.linear_dot_operate -> {
                showPopWindow()
            }
            R.id.addtxt_text_id -> when (addtxt_text_id!!.text.toString()) {
                "编辑" -> {
                    addtxt_text_id!!.text = "取消"
                    sendBroad("编辑")
                }
                "取消" -> {
                    addtxt_text_id!!.text = "编辑"
                    sendBroad("取消")
                }
            }
            R.id.all_read_linear -> {
            }
            R.id.all_select_linear -> {
            }
            R.id.delete_linear -> {
            }
            R.id.back -> finish()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        LogUtil.eLength("这是滚动", position.toString() + "")
        if (flag) {
            val fagmentbase = list[position] as Basecfragment?
            fagmentbase!!.initData()
        }
        flag = true
    }


    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private fun showPopWindow() {
        try {
            val view = LayoutInflater.from(this@MessageActivity).inflate(
                    R.layout.operator_del_pop_lay, null)
            //add_hand_scene_txt, add_auto_scene_txt
            //val add_auto_scene_txt = view.findViewById<View>(R.id.add_auto_scene_txt) as TextView
//            val ok_btn = view.findViewById<View>(R.id.ok_btn) as RelativeLayout
//            val no_btn = view.findViewById<View>(R.id.no_btn) as RelativeLayout

            //month_del_rel

            val month_del_rel = view.findViewById<View>(R.id.month_del_rel) as RelativeLayout


            popupWindow = PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow!!.isFocusable = true
            popupWindow!!.isOutsideTouchable = true
            val cd = ColorDrawable(0x00ffffff) // 背景颜色全透明
            popupWindow!!.setBackgroundDrawable(cd)
            val location = IntArray(2)
            linear_dot_operate!!.getLocationOnScreen(location) //获得textview的location位置信息，绝对位置
            popupWindow!!.animationStyle = R.style.style_pop_animation // 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f) // 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            val xoff = DisplayUtil.dip2px(this@MessageActivity, 20f)
            popupWindow!!.showAsDropDown(linear_dot_operate, 0, 0)
            //            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow!!.setOnDismissListener {
                popupWindow = null // 当点击屏幕时，使popupWindow消失
                backgroundAlpha(1.0f) // 当点击屏幕时，使半透明效果取消，1.0f为透明
            }
            month_del_rel.setOnClickListener {
                popupWindow!!.dismiss()
                sendBroad("按月删除")
            }
//            ok_btn.setOnClickListener {
//                popupWindow!!.dismiss()
//                sendBroad("按月删除")
//                //                    startActivity(new Intent(getActivity(), AddAutoSceneActivity.class));
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 设置popupWindow背景半透明
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        lp.alpha = bgAlpha // 0.0-1.0
        window.attributes = lp
    }


    override fun onPageScrollStateChanged(state: Int) {
        clear()
        viewchange(viewpager_id!!.currentItem)
    }

    public override fun onStart() { //onStart方法是屏幕唤醒时弹执行该方法。
        super.onStart()
        addtxt_text_id!!.text = "编辑"
        sendBroad("取消")
    }

    companion object {
        @JvmField
        var MESSAGE_FRAGMENT = "com.fragment.messagefragment"
    }
}