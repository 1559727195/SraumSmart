package com.massky.sraum.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.king.photo.activity.MessageSendActivity
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.User.userinfo
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.*
import com.massky.sraum.base.BaseFragment1
import com.massky.sraum.event.MyDialogEvent
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.tool.Constants
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by zhu on 2017/11/30.
 */
class MineFragment : BaseFragment1() {
    private var list_alarm: List<Map<*, *>> = ArrayList()
    private var currentpage = 1
    private val mHandler = Handler()

    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null
    private val account_view: View? = null
    private val cancelbtn_id: Button? = null
    private val camera_id: Button? = null
    private val photoalbum: Button? = null
    private var dialogUtil: DialogUtil? = null

    @JvmField
    @BindView(R.id.headportrait)
    var headportrait: CircleImageView? = null

    @JvmField
    @BindView(R.id.wode_device_share_rel)
    var wode_device_share_rel: RelativeLayout? = null
    private val linear_popcamera: LinearLayout? = null

    @JvmField
    @BindView(R.id.wangguan_list_rel)
    var wangguan_list_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.sugestion_rel)
    var sugestion_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.wode_sao_rel)
    var wode_sao_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.system_infor_rel)
    var system_infor_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.about_rel)
    var about_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.setting_rel)
    var setting_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.dev_manager_rel)
    var dev_manager_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.room_manager_rel)
    var room_manager_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.area_manager_rel)
    var area_manager_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.add_family_rel)
    var add_family_rel: RelativeLayout? = null

    //dev_power_rel
    @JvmField
    @BindView(R.id.dev_power_rel)
    var dev_power_rel: RelativeLayout? = null


    @JvmField
    @BindView(R.id.nicheng_name)
    var nicheng_name: TextView? = null
    private var userinfo: userinfo? = null
    private var intfirst = 0
    override fun onData() {}
    override fun onEvent() {
        headportrait!!.setOnClickListener(this)
        wode_device_share_rel!!.setOnClickListener(this)
        wangguan_list_rel!!.setOnClickListener(this)
        sugestion_rel!!.setOnClickListener(this)
        wode_sao_rel!!.setOnClickListener(this)
        system_infor_rel!!.setOnClickListener(this)
        about_rel!!.setOnClickListener(this)
        setting_rel!!.setOnClickListener(this)
        dev_manager_rel!!.setOnClickListener(this)
        room_manager_rel!!.setOnClickListener(this)
        area_manager_rel!!.setOnClickListener(this)
        add_family_rel!!.setOnClickListener(this)
        dev_power_rel!!.setOnClickListener(this)
    }//        dialogUtil.loadDialog();

    //账号个人基本信息
    private val accountInfo: Unit
        private get() {
//        dialogUtil.loadDialog();
            account_number_Act()
        }

    private fun account_number_Act() {
        val map: MutableMap<String, Any> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(activity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(activity)
        //        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getAccountInfo, map, object : Mycallback(AddTogglenInterfacer { account_number_Act() }, activity, null) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                userinfo = user.userInfo
                handler.sendEmptyMessage(0)
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    override fun onEvent(eventData: MyDialogEvent) {
        currentpage = 1
    }

    override fun viewId(): Int {
        return R.layout.mine_frag
    }

    override fun onResume() {
        super.onResume()
        Thread({ accountInfo }).start()
        Log.e("TAG", "avator: " + SharedPreferencesUtil.getData(activity, "avatar", "") )
        headportrait!!.setImageBitmap(BitmapUtil.stringtoBitmap(SharedPreferencesUtil.getData(activity, "avatar", "") as String))
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if(nicheng_name == null) return
            if(userinfo == null) return
            if(userinfo!!.userId == null) return
            nicheng_name!!.text = userinfo!!.userId
            SharedPreferencesUtil.saveData(activity, "userName", nicheng_name!!.text.toString())
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (intfirst == 1) {
                intfirst = 2
            } else {
                Thread(Runnable { accountInfo }).start()
            }
        }
    }

    override fun onView(view: View) {
        list_alarm = ArrayList()
        dialogUtil = DialogUtil(activity)
        StatusUtils.setFullToStatusBar(activity) // StatusBar.
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.headportrait -> //                dialogUtil.loadViewBottomdialog();
                startActivity(Intent(activity, PersonMessageActivity::class.java))
            R.id.wode_device_share_rel -> startActivity(Intent(activity, ShareDeviceActivity::class.java))
            R.id.wangguan_list_rel -> startActivity(Intent(activity, WangGuanListActivity::class.java))
            R.id.sugestion_rel -> startActivity(Intent(activity, MessageSendActivity::class.java))
            R.id.wode_sao_rel -> {
                init_permissions()
            }
            R.id.system_infor_rel -> startActivity(Intent(activity, SystemMessageActivity::class.java))
            R.id.about_rel -> startActivity(Intent(activity, AboutActivity::class.java))
            R.id.setting_rel -> startActivity(Intent(activity, SettingActivity::class.java))
            R.id.dev_manager_rel -> startActivity(Intent(activity, DeviceSettingActivity::class.java))
            R.id.room_manager_rel -> startActivity(Intent(activity, HomeSettingActivity::class.java))
            R.id.area_manager_rel -> startActivity(Intent(activity, AreaSettingActivity::class.java))
            R.id.add_family_rel -> {
                val mintent = Intent(activity, FamilySettingActivity::class.java)
                startActivity(mintent)
            }
            R.id.dev_power_rel -> startActivity(Intent(activity, PowerSettingActivity::class.java))
        }
    }

    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(context as AppCompatActivity)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                val openCameraIntent = Intent(context as AppCompatActivity, CaptureActivity::class.java)
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(): MineFragment {
            val newFragment = MineFragment()
            val bundle = Bundle()
            newFragment.arguments = bundle
            return newFragment
        }
    }
}