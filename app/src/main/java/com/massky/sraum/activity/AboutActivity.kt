package com.massky.sraum.activity

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.ackpass.ackpass.AppQrCodeActivity
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.Utils.App
import com.massky.sraum.Utils.VersionUtil
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by zhu on 2018/2/12.
 */
class AboutActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.direct_for_use)
    var direct_for_use: Button? = null

    //download_app_btn
    @JvmField
    @BindView(R.id.download_app_btn)
    var download_app_btn: Button? = null


    @JvmField
    @BindView(R.id.btn_privacy_policy)
    var btn_privacy_policy: Button? = null
    private var checkbutton_id: Button? = null
    private var qxbutton_id: Button? = null
    private var dtext_id: TextView? = null
    private var belowtext_id: TextView? = null
    private var viewDialog: DialogUtil? = null
    private var versionCode = 0
    private var Version: String? = null
    private var weakReference: WeakReference<Context>? = null

    @JvmField
    @BindView(R.id.check_btn_version)
    var check_btn_version: Button? = null
    override fun viewId(): Int {
        return R.layout.about_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        versionCode = VersionUtil.getVersionCode(this@AboutActivity).toInt()
        dialog
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        direct_for_use!!.setOnClickListener(this)
        btn_privacy_policy!!.setOnClickListener(this)
        check_btn_version!!.setOnClickListener(this)
        download_app_btn!!.setOnClickListener(this)
    }

    override fun onData() {}

    //用于设置dialog展示
    private val dialog: Unit
        private get() {
            val view = layoutInflater.inflate(R.layout.check, null)
            checkbutton_id = view.findViewById<View>(R.id.checkbutton_id) as Button
            qxbutton_id = view.findViewById<View>(R.id.qxbutton_id) as Button
            dtext_id = view.findViewById<View>(R.id.dtext_id) as TextView
            belowtext_id = view.findViewById<View>(R.id.belowtext_id) as TextView
            dtext_id!!.text = "发现新版本"
            checkbutton_id!!.text = "立即更新"
            qxbutton_id!!.text = "以后再说"
            viewDialog = DialogUtil(this@AboutActivity, view)
            checkbutton_id!!.setOnClickListener(this)
            qxbutton_id!!.setOnClickListener(this)
        }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.direct_for_use -> startActivity(Intent(this@AboutActivity, ProductActivity::class.java))
            R.id.btn_privacy_policy -> {//                var intent = Intent(this@LaunghActivity, YinSiActivity::class.java)
//                intent.putExtra("yinsi", "person")
//                startActivity(intent)
                var intent = Intent(this@AboutActivity, YinSiActivity::class.java)
                intent.putExtra("yinsi", "yinsi")
                startActivity(intent)
            }
            R.id.checkbutton_id -> {
                viewDialog!!.removeviewDialog()
                //                String UpApkUrl = ApiHelper.UpdateApkUrl + "sraum" + Version + ".apk";
//                UpdateManager manager = new UpdateManager(getActivity(), UpApkUrl);
//                manager.showDownloadDialog();
                val broadcast = Intent(MainGateWayActivity.MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT)
                sendBroadcast(broadcast)
            }
            R.id.qxbutton_id -> viewDialog!!.removeviewDialog()
            R.id.check_btn_version -> if (viewDialog != null) {
                viewDialog!!.loadDialog()
                about_togglen()
            }
            R.id.download_app_btn-> {
                startActivity(Intent(this@AboutActivity,AppQrCodeActivity::class.java))
            }
        }
    }

    private fun about_togglen() {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = TokenUtil.getToken(this@AboutActivity)
        MyOkHttp.postMapObject(ApiHelper.sraum_getVersion, map, object : Mycallback(AddTogglenInterfacer { about_togglen() }, this@AboutActivity, viewDialog) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                Version = user.version
                val sracode = user.versionCode.toInt()
                if (versionCode >= sracode) {
                    ToastUtil.showDelToast(this@AboutActivity, "您的应用为最新版本")
                } else {
//                    belowtext_id.setText("版本更新至" + Version);
//                    viewDialog.loadViewdialog();
                    //在这里判断有没有正在更新的apk,文件大小小于总长度即可
                    weakReference = WeakReference(App.getInstance())
                    val apkFile = File(weakReference!!.get()!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app_name.apk")
                    if (apkFile != null && apkFile.exists()) {//
                        var apksize: Long = 0
                        try {
                            apksize = getFileSize(apkFile)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val totalapksize = SharedPreferencesUtil.getData(this@AboutActivity, "apk_fileSize", 0) as Int
                        if (totalapksize == 0) { //则说明，还没有下载过
                            belowtext_id!!.text = "版本更新至$Version"
                            viewDialog!!.loadViewdialog()
                            return
                        }
                        if (apksize - totalapksize == 0L) { //说明正在下载或者下载完毕，安装失败时，//->或者是下载完毕后没有去安装；
//                                    down_load_thread();
                            ToastUtil.showToast(this@AboutActivity, "检测到有新版本，正在下载中")
                        }
                    } else { //不存在，apk文件
                        belowtext_id!!.text = "版本更新至$Version"
                        viewDialog!!.loadViewdialog()
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    /**
     * 获取文件长度
     */
    fun getFileSize(file: File): Long {
        if (file.exists() && file.isFile) {
            val fileName = file.name
            println("文件" + fileName + "的大小是：" + file.length())
            return file.length()
        }
        return 0
    }
}