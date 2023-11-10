package com.massky.sraum.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.massky.sraum.EditGateWayResultActivity
import com.massky.sraum.R
import com.massky.sraum.Util.AES
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.Utils.App
import com.massky.sraum.activity.SearchGateWayActivity
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.tool.Constants
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by zhu on 2016/12/23.
 */
class ConfigDialogFragment : DialogFragment(), View.OnClickListener {
    private var dialog1: Dialog? = null
    private var back: ImageView? = null
    private var sao_rel: RelativeLayout? = null
    private var search_rel: RelativeLayout? = null
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> if (dialog1 != null) dialog1!!.dismiss()
            R.id.sao_rel -> {
                init_permissions()
            }
            R.id.search_rel -> {
                val searchGateWayIntent = Intent(activity, SearchGateWayActivity::class.java)
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY)
                if (dialog1 != null) dialog1!!.dismiss()
            }
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 扫描二维码/条码回传
        if (requestCode == Constants.SCAN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val content = data.getStringExtra("result")
                Log.e("robin debug", "content:$content")
                if (TextUtils.isEmpty(content)) return
                //在这里解析二维码，变成房号
                // 密钥
                val key = "ztlmassky6206ztl"
                // 解密
                var DeString: String? = null
                try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                    DeString = AES.Decrypt(content, key)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (DeString == null) {
                    ToastUtil.showToast(activity, "此二维码不是网关二维码")
                    //                    scanlinear.setVisibility(View.VISIBLE);
//                    detairela.setVisibility(View.GONE);
                } else {
//                    scanlinear.setVisibility(View.GONE);
//                    detairela.setVisibility(View.VISIBLE);
//                    gateid.setText(DeString);
//                    gatedditexttwo.setText("");
                    //跳转到编辑网关界面
                    val intent = Intent(activity, EditGateWayResultActivity::class.java)
                    intent.putExtra("gateid", DeString)
                    startActivity(intent)
                    if (dialog1 != null) dialog1!!.dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog1 = Dialog(activity!!, R.style.DialogStyle)
        val inflater = activity!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.config_dialog_fragment, null, false)
        //添加这一行
//       LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear);
//        linearLayout.getBackground().setAlpha(255);//0~255透明度值
        val title = arguments!!.getString("title")
        val message = arguments!!.getString("message")
        initView(view)
        initEvent()
        //在这里配置wifi
        dialog1!!.setContentView(view)
        isCancelable = true //这句话调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用 -setCancelable (false);按返回键也不起作用
        //        StatusBarCompat.compat(getActivity(), getResources().getColor(R.color.colorgraystatusbar));//更改标题栏的颜色
        return dialog1!!
    }

    /**
     * 让dialogFragment铺满整个屏幕的好办法
     */
    override fun onStart() {
        // TODO Auto-generated method stub
        super.onStart()
        val win = getDialog()!!.window
        // 一定要设置Background，如果不设置，window属性设置无效
        win!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.black)))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win!!.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win!!.attributes = params
    }

    private fun initView(view: View) {
        back = view.findViewById<View>(R.id.back) as ImageView //progress_loading_linear,loading_error_linear
        sao_rel = view.findViewById<View>(R.id.sao_rel) as RelativeLayout
        search_rel = view.findViewById<View>(R.id.search_rel) as RelativeLayout
    }

    private fun initEvent() {
        back!!.setOnClickListener(this)
        sao_rel!!.setOnClickListener(this)
        search_rel!!.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val DECODED_CONTENT_KEY = "codedContent"
        fun newInstance(context1: Context?, title: String?, message: String?): ConfigDialogFragment {
            val frag = ConfigDialogFragment()
            val b = Bundle()
            b.putString("title", title)
            b.putString("message", message)
            frag.arguments = b
            return frag
        }
    }
}