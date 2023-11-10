package com.massky.sraum.activity

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import butterknife.BindView
import cn.forward.androids.views.BitmapScrollPicker
import com.AddTogenInterface.AddTogglenInterfacer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.tool.Constants
import com.massky.sraum.widget.SlideSwitchButton
import com.massky.sraum.widget.TakePhotoPopWin
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.chenglei.widget.datepicker.DatePicker
import org.chenglei.widget.datepicker.Sound
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by zhu on 2018/1/17.
 */
class PersonMessageActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.toolbar_txt)
    var toolbar_txt: TextView? = null
    private var account_view: View? = null
    private var cancelbtn_id: Button? = null
    private var camera_id: Button? = null
    private var photoalbum: Button? = null
    private var dialogUtil: DialogUtil? = null
    private var linear_popcamera: LinearLayout? = null

    @JvmField
    @BindView(R.id.touxiang_select)
    var touxiang_select: CircleImageView? = null

    @JvmField
    @BindView(R.id.account_nicheng)
    var account_nicheng: RelativeLayout? = null

    @JvmField
    @BindView(R.id.nicheng_txt)
    var nicheng_txt: TextView? = null

    //    @BindView(R.id.xingbie_pic)
    //    ImageView xingbie_pic;//性别选择图片
    @JvmField
    @BindView(R.id.xingbie_rel)
    var xingbie_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.account_year)
    var account_year: RelativeLayout? = null

    @JvmField
    @BindView(R.id.birthday)
    var birthday: TextView? = null

    @JvmField
    @BindView(R.id.account_id_rel)
    var account_id_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.accountid_txt)
    var accountid_txt: TextView? = null

    @JvmField
    @BindView(R.id.change_phone)
    var change_phone: RelativeLayout? = null

    @JvmField
    @BindView(R.id.change_phone_txt)
    var change_phone_txt //手机号修改
            : TextView? = null

    //调用照相机返回图片临时文件
    private var tempFile: File? = null
    private var nicheng //昵称
            : String? = null
    private var mPicker02 //ScrollBitmapView//竖直滑动图片
            : BitmapScrollPicker? = null
    private val account_id: String? = null
    private var change_phone_string: String? = null
    private var userinfo: User.userinfo? = null

    @JvmField
    @BindView(R.id.slide_btn)
    var slide_btn: SlideSwitchButton? = null

    @JvmField
    @BindView(R.id.txt_nan)
    var txt_nan: TextView? = null

    @JvmField
    @BindView(R.id.txt_nv)
    var txt_nv: TextView? = null

    @JvmField
    @BindView(R.id.change_pass)
    var change_pass: RelativeLayout? = null
    private var gender: String? = null
    private var birthday1: String? = null
    private var birthday_str = ""
    override fun viewId(): Int {
        return R.layout.personmessage_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        addViewid()
        createCameraTempFile(savedInstanceState)
    }


    override fun onEvent() {
        back!!.setOnClickListener(this)
        touxiang_select!!.setOnClickListener(this)
        camera_id!!.setOnClickListener(this)
        photoalbum!!.setOnClickListener(this)
        cancelbtn_id!!.setOnClickListener(this)
        account_nicheng!!.setOnClickListener(this)
        xingbie_rel!!.setOnClickListener(this)
        account_year!!.setOnClickListener(this)
        account_id_rel!!.setOnClickListener(this)
        change_phone!!.setOnClickListener(this)
        slide_btn!!.setSlideListener(MySlideSwitchButtonListener())
        change_pass!!.setOnClickListener(this)
    }

    override fun onData() {
        toolbar_txt!!.text = "个人资料"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.touxiang_select -> dialogUtil!!.loadViewBottomdialog()
            R.id.camera_id -> {
                //跳转到调用系统相机
                dialogUtil!!.removeviewBottomDialog()
                init_permissions(1)
            }
            R.id.photoalbum -> {
                //跳转到调用系统图库
                dialogUtil!!.removeviewBottomDialog()
                init_permissions(2)
            }
            R.id.cancelbtn_id -> dialogUtil!!.removeviewBottomDialog()
            R.id.account_nicheng -> {
                val intent_nicheng = Intent(this@PersonMessageActivity,
                        AccountNiChengActivity::class.java)
                intent_nicheng.putExtra("nicheng_txt", nicheng_txt!!.text.toString())
                startActivityForResult(intent_nicheng, Constants.MyNICheng)
            }
            R.id.xingbie_rel -> {

            }
            R.id.account_year -> showPopFormBottom_select_datapicker()
            R.id.account_id_rel -> {
                val intent_account_id = Intent(this@PersonMessageActivity,
                        AccountIdActivity::class.java)
                intent_account_id.putExtra("account_id", accountid_txt!!.text.toString())
                startActivityForResult(intent_account_id, Constants.MyAccountId)
            }
            R.id.change_phone -> {

            }
            R.id.change_pass -> startActivity(Intent(this@PersonMessageActivity, ChangePassActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        Thread({ accountInfo }).start()
        touxiang_select!!.setImageBitmap(BitmapUtil.stringtoBitmap(SharedPreferencesUtil.getData(this@PersonMessageActivity, "avatar", "") as String))
    }

    //账号个人基本信息
    private val accountInfo: Unit
        private get() {
            account_number_Act()
        }

    private fun account_number_Act() {
        val map: MutableMap<String, Any> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@PersonMessageActivity, "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@PersonMessageActivity)
        map["areaNumber"] = areaNumber
        MyOkHttp.postMapObject(ApiHelper.sraum_getAccountInfo, map, object : Mycallback(AddTogglenInterfacer { account_number_Act() }, this@PersonMessageActivity, null) {
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

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (accountid_txt == null) return
            if(userinfo == null) return
            accountid_txt!!.text = userinfo!!.userId
            //                SharedPreferencesUtil.saveData(PersonMessageActivity.this, "userName", username.getText().toString());
            nicheng_txt!!.text = userinfo!!.nickName
            //                sextext_id.setText(userinfo.gender);
//            Log.e("TAG", "avator:1 " + SharedPreferencesUtil.getData(this@PersonMessageActivity, "avatar", "") )
//            SharedPreferencesUtil.saveData(this@PersonMessageActivity,
//                    "avatar", if (userinfo!!.avatar == null) "" else userinfo!!.avatar)
            gender = userinfo!!.gender
            when (userinfo!!.gender) {
                "男" -> slide_btn!!.changeOpenState(true)
                "女" -> slide_btn!!.changeOpenState(false)
            }
            birthday!!.text = userinfo!!.birthDay
            change_phone_txt!!.text = userinfo!!.mobilePhone
        }
    }

    //用于更新用户
    private fun updateAccountInfo(nickName: String, gender: String?, birthDay: String, mobilePhone: String) {
        account_num_sraum_update(nickName, gender, birthDay, mobilePhone)
    }

    private fun account_num_sraum_update(nickName: String, gender: String?, birthDay: String, mobilePhone: String) {
        val map: MutableMap<String, Any> = HashMap()
        val mapInfo: MutableMap<String, Any?> = HashMap()
        mapInfo["nickName"] = nickName
        mapInfo["gender"] = gender
        mapInfo["birthDay"] = birthDay
        mapInfo["mobilePhone"] = mobilePhone
        map["token"] = TokenUtil.getToken(this@PersonMessageActivity)
        map["userInfo"] = mapInfo
        // dialogUtil!!.loadDialog()
        MyOkHttp.postMapObject(ApiHelper.sraum_updateAccountInfo, map, object : Mycallback(AddTogglenInterfacer { account_num_sraum_update(nickName, gender, birthDay, mobilePhone) }, this@PersonMessageActivity, null) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)

            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private inner class MySlideSwitchButtonListener : SlideSwitchButton.SlideListener {
        override fun openState(isOpen: Boolean, view: View, from: Boolean) {
            if (isOpen) {
                txt_nan!!.visibility = View.VISIBLE
                gender = "男"
                txt_nv!!.visibility = View.GONE
            } else {
                txt_nan!!.visibility = View.GONE
                txt_nv!!.visibility = View.VISIBLE
                gender = "女"
            }
            //            upgrate_single_information();
            updateAccountInfo(nicheng_txt!!.text.toString(), gender,
                    birthday!!.text.toString(), change_phone_txt!!.text.toString())
        }
    }

    /**
     * 跳转到照相机
     */
    private fun gotoCarema() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI = FileProvider.getUriForFile(this@PersonMessageActivity,
                applicationContext.packageName + ".provider",
                tempFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, REQUEST_CAPTURE)
    }

    /**
     * 底部弹出拍照，相册弹出框
     */
    private fun addViewid() {
        account_view = LayoutInflater.from(this@PersonMessageActivity).inflate(R.layout.camera, null)
        linear_popcamera = account_view!!.findViewById<View>(R.id.linear_popcamera) as LinearLayout
        cancelbtn_id = account_view!!.findViewById<View>(R.id.cancelbtn_id) as Button
        photoalbum = account_view!!.findViewById<View>(R.id.photoalbum) as Button
        camera_id = account_view!!.findViewById<View>(R.id.camera_id) as Button
        dialogUtil = DialogUtil(this@PersonMessageActivity, account_view, 1)
    }

    /**
     * 跳转到相册
     */
    private fun gotoPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                gotoClipActivity(Uri.fromFile(tempFile))
            }
            REQUEST_PICK -> if (resultCode == Activity.RESULT_OK) {
                val uri = intent!!.data
                gotoClipActivity(uri)
            }
            REQUEST_CROP_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                val uri = intent!!.data ?: return
                val cropImagePath = getRealFilePathFromUri(applicationContext, uri)
                val options = RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(this)
                        .load(cropImagePath)
                        .apply(options)
                        .into(touxiang_select)
                val bitMap = BitmapFactory.decodeFile(cropImagePath)
                //此处后面可以将bitMap转为二进制上传后台网络
                updateAvatar(bitMap)
            }
            Constants.MyNICheng -> if (intent != null) {
                nicheng = intent.getStringExtra("nicheng") // http://weixin.qq.com/r/bWTZwbXEsOjPrfGi9zF-
                nicheng_txt!!.text = nicheng
                updateAccountInfo(nicheng_txt!!.text.toString(), gender,
                        birthday!!.text.toString(), change_phone_txt!!.text.toString())
            }
            Constants.MyAccountId -> {
            }
            Constants.MyChangePhoneNumber -> if (intent != null) {
                change_phone_string = intent.getStringExtra("account_change_phone") // http://weixin.qq.com/r/bWTZwbXEsOjPrfGi9zF-
                change_phone_txt!!.text = change_phone_string
                updateAccountInfo(nicheng_txt!!.text.toString(), gender,
                        birthday!!.text.toString(), change_phone_txt!!.text.toString())
            }
        }
    }

    //更新头像
    private fun updateAvatar(bitMap: Bitmap) {
        //dialogUtil!!.loadDialog()
        sraum_updateAvatar(bitMap)
    }

    private fun sraum_updateAvatar(bitMap: Bitmap) {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = TokenUtil.getToken(this@PersonMessageActivity)
        map["avatar"] = BitmapUtil.bitmaptoString(bitMap)
        MyOkHttp.postMapObject(ApiHelper.sraum_updateAvatar, map, object : Mycallback(AddTogglenInterfacer { sraum_updateAvatar(bitMap) }, this@PersonMessageActivity, null) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                SharedPreferencesUtil.saveData(this@PersonMessageActivity, "avatar",
                        BitmapUtil.bitmaptoString(bitMap))
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    // 1: qq, 2: weixin
    private val type = 1

    /**
     * 打开截图界面
     *
     * @param uri
     */
    fun gotoClipActivity(uri: Uri?) {
        if (uri == null) {
            return
        }
        val intent = Intent()
        intent.setClass(this, ClipImageActivity::class.java)
        intent.putExtra("type", type)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private fun createCameraTempFile(savedInstanceState: Bundle?) {
        tempFile = if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            savedInstanceState.getSerializable("tempFile") as File?
        } else {
            File(checkDirPath(Environment.getExternalStorageDirectory().path + "/image/"), System.currentTimeMillis().toString() + ".jpg")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("tempFile", tempFile)
    }

    private fun init_permissions(index: Int) {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this)
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_SETTINGS).subscribe(object : Observer<Boolean?> {
            // permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {


            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                when (index) {
                    1 -> {
                        if (CameraUtil.booleanisCameraUseable())
                            gotoCarema()
                    }
                    2 -> {
                        gotoPhoto()
                    }
                }
            }
        })
    }

    /**
     * 从底部弹出的popwindow
     *
     * @param
     */
    fun showPopFormBottom() {
        val bitmaps = CopyOnWriteArrayList<Bitmap>()
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.slot_01))
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.slot_02))
        val view = LayoutInflater.from(this).inflate(R.layout.xingbie_select, null)
        mPicker02 = view.findViewById<View>(R.id.picker_02) as BitmapScrollPicker
        mPicker02!!.data = bitmaps
        mPicker02!!.setOnSelectedListener { scrollPickerView, position -> }
        val btn_cancel = view.findViewById<View>(R.id.btn_cancel) as TextView //btn_onfirm
        val btn_onfirm = view.findViewById<View>(R.id.btn_onfirm) as TextView //btn_onfirm

//        mPicker02.setSelectedPosition(0);//使ScrollPickerView<T>某一个处于选择位置,默认选择一个
        val takePhotoPopWin = TakePhotoPopWin(this, {
            //确定选择具体日期，在某天的具体日期
        }, view)
        // 取消按钮
        btn_cancel.setOnClickListener { // 销毁弹出框
            takePhotoPopWin.dismiss()
        }
        btn_onfirm.setOnClickListener { // 销毁弹出框
            takePhotoPopWin.dismiss()
        }


//        设置Popupwindow显示位置（从底部弹出）
        takePhotoPopWin.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        val params = arrayOf(window.attributes)
        //当弹出Popupwindow时，背景变半透明
        params[0].alpha = 0.7f
        window.attributes = params[0]
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener {
            params[0] = window.attributes
            params[0].alpha = 1f
            window.attributes = params[0]
        }
    }

    /**
     * 从底部弹出的popwindow
     *
     * @param
     */
    fun showPopFormBottom_select_datapicker() {
        val bitmaps = CopyOnWriteArrayList<Bitmap>()
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.slot_01))
        bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.slot_02))
        val view = LayoutInflater.from(this).inflate(R.layout.age_select, null)
        //        mPicker02 = (BitmapScrollPicker) view.findViewById(R.id.picker_02);
//        mPicker02.setData(bitmaps);
//        mPicker02.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
//            @Override
//            public void onSelected(ScrollPickerView scrollPickerView, int position) {
//
//            }
//        });
        val mDatePicker1 = view.findViewById<View>(R.id.date_picker1) as DatePicker
        val sound1 = Sound(this)
        mDatePicker1.setSoundEffect(sound1)
                .setTextColor(Color.BLACK)
                .setFlagTextColor(Color.GRAY)
                .setTextSize(40f)
                .setFlagTextSize(25f).isSoundEffectsEnabled = true
        mDatePicker1.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            birthday1 = year.toString() + "年" + monthOfYear + "月" + dayOfMonth + "日"
            birthday_str = "$year-$monthOfYear-$dayOfMonth"
            //                date = year + "-" + monthOfYear + "-" + dayOfMonth;
//                birthday.setText(birthday1);
//                updateAccountInfo(nicheng_txt.getText().toString(), gender,
//                        birthday.getText().toString(), change_phone_txt.getText().toString());
        }
        val btn_cancel = view.findViewById<View>(R.id.btn_cancel) as TextView //btn_onfirm
        val btn_onfirm = view.findViewById<View>(R.id.btn_onfirm) as TextView //btn_onfirm

//        mPicker02.setSelectedPosition(0);//使ScrollPickerView<T>某一个处于选择位置,默认选择一个
        val takePhotoPopWin = TakePhotoPopWin(this, {
            //确定选择具体日期，在某天的具体日期
        }, view)

        // 取消按钮
        btn_cancel.setOnClickListener { // 销毁弹出框
            takePhotoPopWin.dismiss()
        }
        btn_onfirm.setOnClickListener { // 销毁弹出框
            takePhotoPopWin.dismiss()
            if (birthday1 == null) { //getMonth(), getDayOfMonth()
                birthday1 = mDatePicker1.year.toString() + "年" + mDatePicker1.month + "月" + mDatePicker1.dayOfMonth + "日"
                birthday_str = "${mDatePicker1.year}-${mDatePicker1.dayOfMonth}-${mDatePicker1.month}"
            }
            birthday!!.text = birthday1
            updateAccountInfo(nicheng_txt!!.text.toString(), gender,
                    birthday!!.text.toString(), change_phone_txt!!.text.toString())
        }

//        设置Popupwindow显示位置（从底部弹出）
        takePhotoPopWin.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        val params = arrayOf(window.attributes)
        //当弹出Popupwindow时，背景变半透明
        params[0].alpha = 0.7f
        window.attributes = params[0]
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener {
            params[0] = window.attributes
            params[0].alpha = 1f
            window.attributes = params[0]
        }
    }

    companion object {
        //请求相机
        private const val REQUEST_CAPTURE = 100

        //请求相册
        private const val REQUEST_PICK = 101

        //请求截图
        private const val REQUEST_CROP_PHOTO = 102

        //请求访问外部存储
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 103

        //请求写入外部存储
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104

        /**
         * 根据Uri返回文件绝对路径
         * 兼容了file:///开头的 和 content://开头的情况
         *
         * @param context
         * @param uri
         * @return the file path or null
         */
        fun getRealFilePathFromUri(context: Context, uri: Uri?): String? {
            if (null == uri) return null
            val scheme = uri.scheme
            var data: String? = null
            if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
                data = uri.path
            } else if (ContentResolver.SCHEME_CONTENT == scheme) {
                val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                        if (index > -1) {
                            data = cursor.getString(index)
                        }
                    }
                    cursor.close()
                }
            }
            return data
        }

        /**
         * 检查文件是否存在
         */
        private fun checkDirPath(dirPath: String): String {
            if (TextUtils.isEmpty(dirPath)) {
                return ""
            }
            val dir = File(dirPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dirPath
        }
    }
}