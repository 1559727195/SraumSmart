package com.massky.sraum.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.AppManager
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.widget.SlideSwitchButton
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zhu on 2018/1/17.
 */
class SettingActivity : BaseActivity(), SlideSwitchButton.SlideListener {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.toolbar_txt)
    var toolbar_txt: TextView? = null
    private val account_view: View? = null
    private val cancelbtn_id: Button? = null
    private val camera_id: Button? = null
    private val photoalbum: Button? = null
    private val dialogUtil: DialogUtil? = null
    private val linear_popcamera: LinearLayout? = null

    //    @BindView(R.id.account_nicheng)
    //    RelativeLayout account_nicheng;
    //    @BindView(R.id.xingbie_pic)
    //    ImageView xingbie_pic;//性别选择图片
    @JvmField
    @BindView(R.id.xingbie_rel)
    var xingbie_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.account_year)
    var account_year: RelativeLayout? = null

    @JvmField
    @BindView(R.id.account_id_rel)
    var account_id_rel: RelativeLayout? = null

    //
    //    @BindView(R.id.change_phone)
    //    RelativeLayout change_phone;
    @JvmField
    @BindView(R.id.home_room_manger_rel)
    var home_room_manger_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.infor_setting_rel)
    var infor_setting_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.btn_quit_gateway)
    var btn_quit_gateway: Button? = null

    @JvmField
    @BindView(R.id.slide_btn_music)
    var slide_btn_music: SlideSwitchButton? = null

    //btn_un_register
    @JvmField
    @BindView(R.id.btn_un_register)
    var btn_un_register: Button? = null

    @JvmField
    @BindView(R.id.slide_zhendong)
    var slide_zhendong: SlideSwitchButton? = null
    private var loginPhone: String? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var vibflag = false
    private var musicflag = false
    private var vibrator: Vibrator? = null

    @JvmField
    @BindView(R.id.get_cache_rel)
    var get_cache_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.get_cache_txt)
    var get_cache_txt: TextView? = null

    //slide_zhendong
    override fun viewId(): Int {
        return R.layout.setting_act
    }

    override fun onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this)
        init_permissions()
        loginPhone = SharedPreferencesUtil.getData(this@SettingActivity, "loginPhone", "") as String
        preferences = getSharedPreferences("sraum$loginPhone", Context.MODE_PRIVATE)
        editor = preferences!!.edit()
        vibflag = preferences!!.getBoolean("vibflag", false)
        //        musicflag = preferences.getBoolean("musicflag", false);
        musicflag = SharedPreferencesUtil.getData(this@SettingActivity, "musicflag", false) as Boolean
        //        swibtntwo.setChecked(musicflag);
//        swibtnone.setChecked(vibflag);
        slide_btn_music!!.changeOpenState(musicflag)
        slide_zhendong!!.changeOpenState(vibflag)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        home_room_manger_rel!!.setOnClickListener(this)
        infor_setting_rel!!.setOnClickListener(this)
        btn_quit_gateway!!.setOnClickListener(this)
        slide_btn_music!!.setSlideListener(this)
        slide_zhendong!!.setSlideListener(this)
        get_cache_rel!!.setOnClickListener(this)
        btn_un_register!!.setOnClickListener(this)
    }

    override fun onData() {
        get_cache()
    }

    /**
     * 获取缓存
     */
    private fun get_cache() {
        try {
            val size = DataCleanManager.getTotalCacheSize(this@SettingActivity)
            get_cache_txt!!.text = "清除缓存($size)"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.home_room_manger_rel -> startActivity(Intent(this@SettingActivity, HomeSettingActivity::class.java))
            R.id.infor_setting_rel -> startActivity(Intent(this@SettingActivity, InformationSettingActivity::class.java))
            R.id.btn_quit_gateway -> //                ApplicationContext.getInstance().removeActivity();
//                startActivity(new Intent(SettingActivity.this,LoginCloudActivity.class));
//                SharedPreferencesUtil.saveData(SettingActivity.this, "editFlag", false);
//                SharedPreferencesUtil.saveData(SettingActivity.this, "loginflag", false);
//                IntentUtil.startActivityAndFinishFirst(SettingActivity.this, LoginCloudActivity.class);
//                AppManager.getAppManager().finishAllActivity();
                //退出登录
                showCenterDeleteDialog("是否退出登录?", 1)
            R.id.get_cache_rel -> showCenterDeleteDialog("是否清除缓存?", 2)
            R.id.btn_un_register -> showCenterDeleteDialog("注销将删除所有数据，谨慎操作！", 3)
        }
    }

    //自定义dialog,centerDialog删除对话框
    fun showCenterDeleteDialog(name: String?, index: Int) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();
        val view = LayoutInflater.from(this@SettingActivity).inflate(R.layout.promat_dialog, null)
        val confirm: TextView //确定按钮
        val cancel: TextView //确定按钮
        val tv_title: TextView
        val name_gloud: TextView
        val confirm_rel: RelativeLayout
        //        final TextView content; //内容
        cancel = view.findViewById<View>(R.id.call_cancel) as TextView
        confirm = view.findViewById<View>(R.id.call_confirm) as TextView
        tv_title = view.findViewById<View>(R.id.tv_title) as TextView //name_gloud
        name_gloud = view.findViewById<View>(R.id.name_gloud) as TextView
        confirm_rel = view.findViewById<RelativeLayout>(R.id.confirm_rel) as RelativeLayout
        name_gloud.text = name
        tv_title.visibility = View.GONE
        if(index == 3){
            confirm_rel.background = resources.getDrawable(R.drawable.dialog_red_corner_bg)
            cancel.setTextColor(resources.getColor(R.color.white))
            confirm.text = "取消"
            cancel.text = "注销"
        }
        //        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        val dialog = Dialog(this@SettingActivity, R.style.BottomDialog)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val dm = resources.displayMetrics
        val displayWidth = dm.widthPixels
        val displayHeight = dm.heightPixels
        val p = dialog.window!!.attributes //获取对话框当前的参数值
        p.width = (displayWidth * 0.8).toInt() //宽度设置为屏幕的0.5
        //        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.window!!.attributes = p //设置生效
        dialog.show()
        cancel.setOnClickListener {
            when (index) {
                2 -> {
                    DataCleanManager.clearAllCache(this@SettingActivity)
                    get_cache()
                }
                1 -> logout()
                3 -> {
                    sraum_cancellation()
                }
            }
            dialog.dismiss()
        }
        confirm.setOnClickListener { dialog.dismiss() }
    }

    private fun sraum_cancellation() {

        //sraum_cancellation,注销功能
        val map = HashMap<Any?, Any?>()
        map["token"] = TokenUtil.getToken(this@SettingActivity)
        dialogUtil?.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_cancellation, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { logout() }, this@SettingActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                SharedPreferencesUtil.saveData(this@SettingActivity, "editFlag", false)
                SharedPreferencesUtil.saveData(this@SettingActivity, "loginflag", false)
                IntentUtil.startActivityAndFinishFirst(this@SettingActivity, LoginCloudActivity::class.java)
                AppManager.getAppManager().finishAllActivity()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {}
        }
        )
    }

    private fun logout() {
        //在这里先调
        //设置网关模式-sraum-setBox
        val map = HashMap<Any?, Any?>()
        //        String phoned = getDeviceId(getActivity());
        map["token"] = TokenUtil.getToken(this@SettingActivity)
        dialogUtil?.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_logout, map as HashMap<String, Any>, object : Mycallback(AddTogglenInterfacer { logout() }, this@SettingActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                SharedPreferencesUtil.saveData(this@SettingActivity, "editFlag", false)
                SharedPreferencesUtil.saveData(this@SettingActivity, "loginflag", false)
                IntentUtil.startActivityAndFinishFirst(this@SettingActivity, LoginCloudActivity::class.java)
                AppManager.getAppManager().finishAllActivity()
            }

            override fun wrongToken() {
                super.wrongToken()
            }

            override fun wrongBoxnumber() {}
        }
        )
    }

    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this)
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_SETTINGS).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        })
    }

    override fun openState(isOpen: Boolean, view: View, from: Boolean) {
        when (view.id) {
            R.id.slide_btn_music -> {
                if (isOpen) {
                    MusicUtil.startMusic(this@SettingActivity, 1, "")
                } else {
                    MusicUtil.stopMusic(this@SettingActivity, "")
                }
                //                editor.putBoolean("musicflag", isOpen);
//                editor.commit();
                SharedPreferencesUtil.saveData(this@SettingActivity, "musicflag", isOpen)
            }
            R.id.slide_zhendong -> {
                editor!!.putBoolean("vibflag", isOpen)
                editor!!.commit()
                if (isOpen) {
                    vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator!!.vibrate(200)
                }
            }
        }
    }
}