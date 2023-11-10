package com.massky.sraum.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import butterknife.BindView
import cn.jpush.android.api.JPushInterface
import com.AddTogenInterface.AddTogglenInterfacer
import com.example.jpushdemo.Constants
import com.iflytek.cloud.SpeechUtility
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.App
import com.massky.sraum.Utils.GpsUtil
import com.massky.sraum.base.BaseActivity
import com.mob.MobSDK
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import okhttp3.Call
import webapp.config.SystemParams
import java.io.IOException
import java.util.*

/**
 * Created by zhu on 2017/12/25.
 */
class LaunghActivity : BaseActivity() {
    private var timer: Timer? = null
    private var task: TimerTask? = null
    private var activity_destroy //activity是否被销毁
            = false

    @JvmField
    @BindView(R.id.btn_next_step)
    var btn_next_step: Button? = null

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.three_second_btn)
    var three_second_btn: Button? = null
    private var areaList: MutableList<Map<*, *>> = ArrayList()
    private var roomList: MutableList<Map<*, *>> = ArrayList() //fang jian lie biao
    private val list: List<Map<*, *>> = ArrayList()
    private val _url = "http://app.sraum.com/sraumApp/yinsi/index.html"
    override fun viewId(): Int {
        return R.layout.laugh_layout
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        setAnyBarAlpha(0) //设置状态栏的颜色为透明
        //        StatusUtils.setFullToNavigationBar(this); // NavigationBar.

        var firstRegister: Boolean = SharedPreferencesUtil.getData(this@LaunghActivity,
                "firstRegister", false) as Boolean

        initTimer()
        if (firstRegister) {
            init_other_sdk()
            isAppMainProcess
            if (timer != null) {
                timer!!.schedule(task, 1, 1000) // 1s后执行task,经过1ms再次执行
            }
        } else {
            showCenterDeleteDialog("")
        }
        // Request()
    }

    private fun init_other_sdk() {
        Log.e("init_ohter_sdk", "init_other_sdk: ")
        MobSDK.submitPolicyGrantResult(true, null); //添加最新隐私协议
        jpush_speach()
    }


    private fun jpush_speach() {

        //        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        SpeechUtility.createUtility(this@LaunghActivity, "appid=" + getString(R.string.app_id))

        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
        // Setting.setShowLog(false);
        JPushInterface.init(this) // 初始化 JPush
        SystemParams.init(this)
        // CrashHandlerUtil.getInstance().init_crash(_instance);
        //用于判断log值是否打印
        LogUtil.isDebug = true
    }


    fun Request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        }
    }

    /**
     * 打开Gps设置界面
     */
    private fun openGpsSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, 887)
    }
    //则说明app没有被杀死,直接跳转//4731


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            1 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO request success
                if (GpsUtil.isOPen(this@LaunghActivity)) {
                    //initWifiName()
                } else {
                    openGpsSettings()
                }
            } else {
                ToastUtil.showToast(this, "权限被拒绝")
            }
        }
    }

    fun showCenterDeleteDialog(name: String?) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@LaunghActivity).inflate(R.layout.promat_dialog_yinsi, null)
        var call_confirm: TextView //确定按钮
        var call_cancel: TextView //确定按钮
        var promat_second: TextView
        //        final TextView content; //内容
        call_confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        call_cancel = view.findViewById<View>(R.id.call_cancel) as TextView

        promat_second = view.findViewById<View>(R.id.promat_second) as TextView


        //创建可扩展字符串并输入内容


//        common_spann(promat_second, "请你务必审慎阅读、充分理解《用户服务协议》和", "", 13, 20)
//        common_spann(promat_third, "请你务必审慎阅读、充分理解《用户服务协议》和", "", 13, 20)

//        promat_second.setMovementMethod(LinkMovementMethod.getInstance()) //使超链接可点击
//
//        promat_second.setText(setTextLink(this, conent))

        promat_second.setText(getClickableSpan())

        //此行必须有
        promat_second.setMovementMethod(LinkMovementMethod.getInstance())

        //显示数据
        val dialog = Dialog(this@LaunghActivity, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth / 3 * 2) //宽度设置为屏幕的0.5
        p.height = (displayHeight / 2)
        //        p.height = (int) (p.width / 3 * 2); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.setCancelable(false)
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        call_cancel.setOnClickListener({
            LaunghActivity@ this.finish()
            dialog.dismiss()
        })

        call_confirm.setOnClickListener({
            init_other_sdk()
            if (timer != null) {
                timer!!.schedule(task, 1, 1000) // 1s后执行task,经过1ms再次执行
            }
            isAppMainProcess
            SharedPreferencesUtil.saveData(this@LaunghActivity, "firstRegister", true)
            dialog.dismiss()
            //Request()
        })
    }


    private fun getClickableSpan(): SpannableString? {

        //监听器
        //监听器


        //监听器
        val listener_one = View.OnClickListener {

            var intent = Intent(this@LaunghActivity, YinSiActivity::class.java)
            intent.putExtra("yinsi", "person")
            startActivity(intent)


        }
        //
        val listener_two = View.OnClickListener {
            var intent = Intent(this@LaunghActivity, YinSiActivity::class.java)
            intent.putExtra("yinsi", "yinsi")
            startActivity(intent)
        }
        //  val listener = View.OnClickListener { Toast.makeText(this@LaunghActivity, "Click Success", Toast.LENGTH_SHORT).show() }
        val spanableInfo = SpannableString("请你务必审慎阅读、充分理解《用户服务协议》和《隐私政策》各项条款，" +
                "包括但不限于获取位置信息、个人信息等。" +
                "如你同意，请点击“同意”开始接受我们的服务。")


        //可以为多部分设置超链接
        spanableInfo.setSpan(Clickable(listener_one), 13, 21, Spanned.SPAN_MARK_MARK)
        spanableInfo.setSpan(Clickable(listener_two), 23, 28, Spanned.SPAN_MARK_MARK)
        return spanableInfo
    }


    private class Clickable(private val mListener: View.OnClickListener) : ClickableSpan(), View.OnClickListener {
        override fun onClick(view: View) {
            mListener.onClick(view)
        }
    }


    fun setTextLink(context: Context?,
                    answerstring: String?): SpannableStringBuilder? {
        if (!TextUtils.isEmpty(answerstring)) {

            //fromHtml(String source)在Android N中已经弃用，推荐使用fromHtml(String source, int
            // flags)，flags 参数说明，
            // Html.FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
            // Html.FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
            val htmlString = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(answerstring, Html.FROM_HTML_MODE_COMPACT)
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            if (htmlString is SpannableStringBuilder) {
                val spannablestringbuilder = htmlString
                //取得与a标签相关的span
                val objs: Array<Any>? = arrayOf(spannablestringbuilder.getSpans<URLSpan>(0,
                        spannablestringbuilder.length, URLSpan::class.java))
                if (null != objs && objs.size != 0) {
                    for (obj in objs) {
                        val start = spannablestringbuilder.getSpanStart(obj)
                        val end = spannablestringbuilder.getSpanEnd(obj)
                        if (obj is URLSpan) {
                            //先移除这个span，再新添加一个自己实现的span。
                            val url = obj.url
                            spannablestringbuilder.removeSpan(obj)
                            spannablestringbuilder.setSpan(object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    //这里可以实现自己的跳转逻辑
                                    Toast.makeText(this@LaunghActivity, url,
                                            Toast.LENGTH_LONG).show()
                                }
                            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
                return spannablestringbuilder
            }
        }
        return SpannableStringBuilder(answerstring)
    }


    private fun common_spann(textView: TextView, content: String?, url: String?, start: Int, end: Int) {
        //创建可扩展字符串并输入内容
        val spannableString = SpannableString(content)
        // 设置url链接
        // 设置url链接
        val urlSpan = URLSpan(url)
        // 一参：url对象； 二参三参：url生效的字符起始位置； 四参：模式
        // 一参：url对象； 二参三参：url生效的字符起始位置； 四参：模式
        spannableString.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // 设置textView可以点击链接进行跳转（不设置的话点击无反应）
        // 设置textView可以点击链接进行跳转（不设置的话点击无反应）
        textView.setMovementMethod(LinkMovementMethod())
        //把可扩展字符串设置到textView
        //把可扩展字符串设置到textView
        textView.setText(spannableString)
    }

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    val isAppMainProcess: Unit
        get() {
            val pid = Process.myPid() //4731
            val pid_past = SharedPreferencesUtil.getData(this@LaunghActivity, "pid", 0) as Int
            if (pid_past == pid) { //则说明app没有被杀死,直接跳转
                tiaozhuan()
                SharedPreferencesUtil.saveData(this@LaunghActivity, "newProcess", false)
                return
            } else {
                SharedPreferencesUtil.saveData(this@LaunghActivity, "newProcess", true)
                saveProcess()
            }
        }

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    fun saveProcess() {
        val pid = Process.myPid() //4731
        SharedPreferencesUtil.saveData(this@LaunghActivity, "pid", pid)
    }

    override fun onEvent() {
        btn_next_step!!.setOnClickListener(this)
        three_second_btn!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onResume() {
        Thread { sraum_getAllArea() }.start()
        super.onResume()
    }

    /**
     * 获取所有区域
     */
    private fun sraum_getAllArea() {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@LaunghActivity)
        //        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getAllArea()
        }, this@LaunghActivity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {
                super.wrongBoxnumber()
            }

            override fun onSuccess(user: User) {
                areaList = ArrayList()
                for (i in user.areaList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    when (user.areaList[i].authType) {
                        "1" -> mapdevice["name"] = user.areaList[i].areaName + "(" + "业主" + ")"
                        "2" -> mapdevice["name"] = user.areaList[i].areaName + "(" + "成员" + ")"
                    }
                    //
//                            mapdevice.put("name", user.areaList.get(i).areaName);
                    mapdevice["number"] = user.areaList[i].number
                    mapdevice["sign"] = user.areaList[i].sign
                    mapdevice["authType"] = user.areaList[i].authType
                    mapdevice["roomCount"] = user.areaList[i].roomCount
                    areaList.add(mapdevice)
                }
                if (areaList.size != 0) {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList", areaList)
                } else {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "areaList", ArrayList())
                }
                if (areaList.size != 0) { //区域命名
                    for (map in areaList) {
                        if ("1" == map["sign"].toString()) {
                            sraum_getRoomsInfo(map["number"].toString())
                            break
                        }
                    }
                }
            }
        })
    }

    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     */
    private fun sraum_getRoomsInfo(areaNumber: String) {
        val mapdevice: MutableMap<String?, String?> = HashMap()
        mapdevice["token"] = TokenUtil.getToken(this@LaunghActivity)
        mapdevice["areaNumber"] = areaNumber
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo, mapdevice, object : Mycallback(AddTogglenInterfacer { //刷新togglen数据
            sraum_getRoomsInfo(areaNumber)
        }, this@LaunghActivity, null) {
            override fun onError(call: Call, e: Exception, id: Int) {
                super.onError(call, e, id)
            }

            override fun pullDataError() {
                super.pullDataError()
            }

            override fun emptyResult() {
                super.emptyResult()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {}
            override fun onSuccess(user: User) {
                //qie huan cheng gong ,获取区域的所有房间信息
                roomList = ArrayList()
                for (i in user.roomList.indices) {
                    val mapdevice: MutableMap<String, String> = HashMap()
                    mapdevice["name"] = user.roomList[i].name
                    mapdevice["number"] = user.roomList[i].number
                    mapdevice["count"] = user.roomList[i].count
                    roomList.add(mapdevice)
                }
                if (roomList.size != 0) {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList", roomList)
                } else {
                    SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().applicationContext, "roomList", ArrayList())
                }
            }

            override fun threeCode() {}
        })
    }

    private var add = 3

    /**
     * 初始化定时器
     */
    private fun initTimer() {
        if (timer == null) timer = Timer()
        //3min
        //关闭clientSocket即客户端socket
        if (task == null) task = object : TimerTask() {
            override fun run() {
                runOnUiThread(Runnable {
                    if (three_second_btn == null) {
                        return@Runnable
                    }
                    three_second_btn!!.text = add.toString() + "秒跳过"
                    add--
                })
                Log.e("robin debug", "add:$add")
                if (add <= 0) { //3min= 1000 * 60 * 3
                    try {
                        add = 0
                        closeTimer()
                        tiaozhuan()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (activity_destroy) { //长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                    try {
                        closeTimer()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * 跳转到主界面
     */
    private fun tiaozhuan() {
        val flag = SharedPreferencesUtil.getData(this@LaunghActivity, "loginflag", false) as Boolean
        //登录状态保存
        if (flag) {
//            IntentUtil.startActivityAndFinishFirst(LoginActivity.this, MainfragmentActivity.class);
            val intent = Intent(this@LaunghActivity, MainGateWayActivity::class.java)
            if (getIntent().getBundleExtra(Constants.EXTRA_BUNDLE) != null) {
                intent.putExtra(Constants.EXTRA_BUNDLE,
                        getIntent().getBundleExtra(Constants.EXTRA_BUNDLE))
            }
            startActivity(intent)
            //            SharedPreferencesUtil.saveData(LoginActivity.this,"loginflag",true);
            finish()
        } else {
            //跳转到 启动第二页
            startActivity(Intent(this@LaunghActivity, LaunghSecondActivity::class.java))
            finish()
        }
    }

    /**
     * 关闭定时器和socket客户端
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun closeTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        if (task != null) {
            task!!.cancel()
            task = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_next_step -> startActivity(Intent(this@LaunghActivity, NextStepActivity::class.java))
            R.id.three_second_btn -> {
                add = 0
                try {
                    closeTimer()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                tiaozhuan()
                activity_destroy = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity_destroy = true
    }

    /*
     *动态设置状态栏的颜色
     */
    private fun setAnyBarAlpha(alpha: Int) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
        statusView!!.background.mutate().alpha = alpha
    }

    companion object {
        private const val PROCESS_NAME = "com.massky.sraum"
    }
}