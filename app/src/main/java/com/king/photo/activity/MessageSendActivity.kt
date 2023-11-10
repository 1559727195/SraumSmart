package com.king.photo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.king.photo.util.*
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.HistoryBackActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.view.ClearEditText
import com.massky.sraum.widget.ListViewForScrollView
import com.yanzhenjie.statusview.StatusUtils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.File
import java.util.*

/**
 * 首页面activity
 *
 * @author king
 * @version 2014年10月18日  下午11:48:34
 * @QQ:595163260
 */
class MessageSendActivity : BaseActivity() {
    private var noScrollgridview: ListViewForScrollView? = null
    private var adapter: GridAdapter? = null

    //	private View parentView;
    private val pop: PopupWindow? = null
    private val ll_popup: LinearLayout? = null

    @JvmField
    @BindView(R.id.pic_select_linear)
    var pic_select_linear: LinearLayout? = null

    @JvmField
    @BindView(R.id.list_forscrollview)
    var listViewForScrollView: ScrollView? = null
    private var account_view: View? = null
    private var cancelbtn_id: Button? = null
    private var camera_id: Button? = null
    private var photoalbum: Button? = null
    private var dialogUtil: DialogUtil? = null
    private var linear_popcamera: LinearLayout? = null

    @JvmField
    @BindView(R.id.hostory_back_txt)
    var hostory_back_txt: TextView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.btn_cancel_wangguan)
    var btn_cancel_wangguan: Button? = null

    @JvmField
    @BindView(R.id.activity_group_radioGroup_light)
    var activity_group_radioGroup_light: RadioGroup? = null

    @JvmField
    @BindView(R.id.edit_text_message)
    var edit_text_message: ClearEditText? = null

    @JvmField
    @BindView(R.id.phone_number)
    var phone_number: ClearEditText? = null
    private var type = "1" //功能建议
    private var dialogUtil_bottom: DialogUtil? = null

    /**
     * 底部弹出拍照，相册弹出框
     */
    private fun addViewid() {
        account_view = LayoutInflater.from(this@MessageSendActivity).inflate(R.layout.camera, null)
        linear_popcamera = account_view!!.findViewById<View>(R.id.linear_popcamera) as LinearLayout
        cancelbtn_id = account_view!!.findViewById<View>(R.id.cancelbtn_id) as Button
        photoalbum = account_view!!.findViewById<View>(R.id.photoalbum) as Button
        camera_id = account_view!!.findViewById<View>(R.id.camera_id) as Button
        dialogUtil_bottom = DialogUtil(this@MessageSendActivity, account_view, 1)
        camera_id!!.setOnClickListener(this)
        photoalbum!!.setOnClickListener(this)
        cancelbtn_id!!.setOnClickListener(this)
        hostory_back_txt!!.setOnClickListener(this)
        back!!.setOnClickListener(this)
        init_radio_group()
    }

    private fun init_radio_group() {
        for (i in 0 until activity_group_radioGroup_light!!.childCount) {
            activity_group_radioGroup_light!!.getChildAt(i).setOnClickListener {
                when (i) {
                    0 -> type = "1"
                    1 -> type = "2"
                    2 -> type = "3"
                }
            }
        }
    }

    fun Init() {
        addViewid() //添加底部弹出拍照，选择系统相册。
        //
        noScrollgridview = findViewById<View>(R.id.noScrollgridview) as ListViewForScrollView
        noScrollgridview!!.selector = ColorDrawable(Color.TRANSPARENT)
        adapter = GridAdapter(this)
        adapter!!.update()
        noScrollgridview!!.adapter = adapter
        noScrollgridview!!.onItemClickListener = AdapterView.OnItemClickListener { arg0, arg1, arg2, arg3 ->
            if (arg2 == Bimp.tempSelectBitmap.size) {
                Log.i("ddddddd", "----------")
                if (dialogUtil_bottom != null) dialogUtil_bottom!!.loadViewBottomdialog()
            } else {
                val intent = Intent(this@MessageSendActivity,
                        GalleryActivity::class.java)
                intent.putExtra("position", "1")
                intent.putExtra("ID", arg2)
                startActivity(intent)
            }
        }
    }


    private fun init_permissions(index: Int) {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(MessageSendActivity@ this)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
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
                        val intent = Intent(this@MessageSendActivity,
                                AlbumActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out)
                    }
                }
            }
        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.camera_id -> {
                //跳转到调用系统相机
                dialogUtil_bottom!!.removeviewBottomDialog()
                init_permissions(1)
            }
            R.id.photoalbum -> {
                //跳转到调用系统图库
                dialogUtil_bottom!!.removeviewBottomDialog()
                init_permissions(2)
            }
            R.id.cancelbtn_id -> dialogUtil_bottom!!.removeviewBottomDialog()
            R.id.hostory_back_txt -> startActivity(Intent(this@MessageSendActivity, HistoryBackActivity::class.java))
            R.id.back -> finish()
            R.id.btn_cancel_wangguan -> {
                //                MessageSendActivity.this.finish();
                val content = edit_text_message!!.text.toString()
                if (content == "") {
                    ToastUtil.showToast(this@MessageSendActivity, "请简要描述你的问题和意见")
                    return
                }
                var bitmap: Bitmap? = null
                var img: String? = null
                if (Bimp.tempSelectBitmap.size != 0) {
//                    Bitmap bitmap = Bimp.tempSelectBitmap.get(0).getBitmap();
//                    Bitmap nBM = BitmapUtil.scaleBitmap(bitmap, 0.3f);
////                    Bitmap nBM = BitmapUtil.cropBitmap(bitmap);
//                    img = BitmapUtil.bitmaptoString(nBM);
                    bitmap = Bimp.tempSelectBitmap[0].bitmap
                    Log.e("bitmap", "bitmap: " + bitmap )
//                    val x = DisplayUtil.dip2px(this@MessageSendActivity, 80f)
//                    val y = DisplayUtil.dip2px(this@MessageSendActivity, 80f)
                    img = BitmapUtil.bitmapToString(bitmap)
                    Log.e("bitmap", "img: " )
                }
                val mobilePhone = phone_number!!.text.toString()
                if (mobilePhone.length != 11) {
                    ToastUtil.showToast(this@MessageSendActivity, "请输入正确手机号")
                    return
                }
                if (mobilePhone == "") {
                    ToastUtil.showToast(this@MessageSendActivity, "请输入您的手机号，方便工作人员联系")
                    return
                }
                if (img == null) return
                sraum_setComplaint(type, img, content, mobilePhone)
            }
        }
    }

    private fun sraum_setComplaint(type: String, img: String, content: String, mobilePhone: String) {
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = TokenUtil.getToken(this@MessageSendActivity)
        map["content"] = content
        map["type"] = type
        map["img"] = img
        map["mobilePhone"] = mobilePhone
        MyOkHttp.postMapObject(ApiHelper.sraum_setComplaint, map, object : Mycallback(AddTogglenInterfacer { }, this@MessageSendActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                finish()
            }

            override fun wrongToken() {
                super.wrongToken()
                ToastUtil.showToast(this@MessageSendActivity, "datetime 错误")
            }

            override fun threeCode() {
                super.threeCode()
                ToastUtil.showToast(this@MessageSendActivity, "图片错误")
            }
        })
    }

    @SuppressLint("HandlerLeak")
    inner class GridAdapter(context: Context?) : BaseAdapter() {
        private val inflater: LayoutInflater
        var selectedPosition = -1
        var isShape = false
        fun update() {
            loading()
        }

        override fun getCount(): Int {
            return if (Bimp.tempSelectBitmap.size == 9) {
                9
            } else Bimp.tempSelectBitmap.size + 1
        }

        override fun getItem(arg0: Int): Any? {
            return null
        }

        override fun getItemId(arg0: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            var holder: ViewHolder? = null
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false)
                holder = ViewHolder()
                holder!!.image = convertView
                        .findViewById<View>(R.id.item_grida_image) as ImageView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            if (position == Bimp.tempSelectBitmap.size) {
                holder.image!!.setImageBitmap(BitmapFactory.decodeResource(
                        resources, R.drawable.btn_charutupian))
                if (position == 9) {
                    holder!!.image!!.visibility = View.GONE
                }
            } else {
                holder!!.image!!.setImageBitmap(Bimp.tempSelectBitmap[position].bitmap)
            }
            return convertView!!
        }

        inner class ViewHolder {
            var image: ImageView? = null
        }

        var handler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> adapter!!.notifyDataSetChanged()
                }
                super.handleMessage(msg)
            }
        }

        fun loading() {
            Thread {
                while (true) {
                    if (Bimp.max >= Bimp.tempSelectBitmap.size) {
                        val message = Message()
                        message.what = 1
                        handler.sendMessage(message)
                        break
                    } else {
                        Bimp.max += 1
                        val message = Message()
                        message.what = 1
                        handler.sendMessage(message)
                    }
                }
            }.start()
        }

        init {
            inflater = LayoutInflater.from(context)
        }
    }

    fun getString(s: String?): String? {
        val path: String? = null
        if (s == null) return ""
        var i = s.length - 1
        while (i > 0) {
            s[i]
            i++
        }
        return path
    }

    override fun onRestart() {
        adapter!!.update()
        super.onRestart()
    }

    override fun viewId(): Int {
        return R.layout.activity_selectimg
    }

    override fun onView() {
        Res.init(this)
        bimap = BitmapFactory.decodeResource(
                resources,
                R.drawable.icon_addpic_unfocused)
        createCameraTempFile(savedInstanceState)
        Init()
        StatusUtils.setFullToStatusBar(this)
        listViewForScrollView!!.smoothScrollTo(0, 0)
        dialogUtil = DialogUtil(this)
    }


    //调用照相机返回图片临时文件
    private var tempFile: File? = null

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("tempFile", tempFile)
    }

    override fun onEvent() {
        btn_cancel_wangguan!!.setOnClickListener(this)
    }

    override fun onData() {}

    /**
     * 跳转到照相机
     */
    private fun gotoCarema() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoURI = FileProvider.getUriForFile(this@MessageSendActivity,
                applicationContext.packageName + ".provider",
                tempFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, REQUEST_CAPTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAPTURE -> if (Bimp.tempSelectBitmap.size < 9 && resultCode == Activity.RESULT_OK) {
                // val fileName = System.currentTimeMillis().toString()
//                val bm = data!!.extras!!["data"] as Bitmap?
                // FileUtils.saveBitmap(bm, fileName)
                if (tempFile == null) return
                var uri = Uri.fromFile(tempFile)
                if (uri == null) return
                val filePath = getRealFilePathFromUri(applicationContext, uri)
              //  val bm = BitmapFactory.decodeFile(cropImagePath)

                val x = DisplayUtil.dip2px(this@MessageSendActivity, 80f)
                val y = DisplayUtil.dip2px(this@MessageSendActivity, 80f)
                val bm = BitmapUtil.getSmallBitmap(filePath, x, y)
                val takePhoto = ImageItem()
                takePhoto.bitmap = bm
                Bimp.tempSelectBitmap.add(takePhoto)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        for (i in PublicWay.activityList.indices) {
//            if (null != PublicWay.activityList[i]) {
//                PublicWay.activityList[i].finish()
//                PublicWay.activityList.removeAt(i)
//            }
//        }
        var iterator = PublicWay.activityList.iterator()
        while (iterator.hasNext()) {
            var item = iterator.next()
            if (item == null) continue
            item.finish()
            iterator.remove()
        }
        Bimp.tempSelectBitmap.clear()
        Bimp.max = 0
    }

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

    companion object {
        @JvmField
        var bimap: Bitmap? = null
        private const val TAKE_PICTURE = 0x000001

        //请求相机
        private const val REQUEST_CAPTURE = 100
    }
}