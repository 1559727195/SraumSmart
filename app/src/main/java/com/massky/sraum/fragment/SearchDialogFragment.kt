package com.massky.sraum.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.massky.sraum.EditGateWayResultActivity
import com.massky.sraum.R
import com.massky.sraum.Util.AES
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.activity.SearchGateWayActivity
import com.massky.sraum.adapter.ShowUdpServerAdapter
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.tool.Constants
import java.util.*

/**
 * Created by zhu on 2016/12/23.
 */
class SearchDialogFragment : DialogFragment(), View.OnClickListener, SearchGateWayActivity.SendParamsInterfacer {
    private var dialog1: Dialog? = null
    private var back: ImageView? = null
    private val sao_rel: RelativeLayout? = null
    private val search_rel: RelativeLayout? = null
    private var list_show_rev_item_detail: ListView? = null
    private var showUdpServerAdapter: ShowUdpServerAdapter? = null
    private var list: List<String>? = ArrayList()
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> if (dialog1 != null) dialog1!!.dismiss()
            R.id.sao_rel -> {
                val openCameraIntent = Intent(activity, CaptureActivity::class.java)
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE)
            }
            R.id.search_rel -> {
                val searchGateWayIntent = Intent(activity, SearchGateWayActivity::class.java)
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY)
            }
        }
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
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog1 = Dialog(activity!!, R.style.DialogStyle)
        val inflater = activity!!
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.search_dialog_fragment, null, false)
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
        list_show_rev_item_detail = view.findViewById<View>(R.id.list_show_rev_item_detail) as ListView
        list_show_rev_item_detail!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity, EditGateWayResultActivity::class.java)
            intent.putExtra("gateid", list!![position]) //跳转到编辑网关密码界面
            startActivity(intent)
        }
    }

    private fun initEvent() {
        back!!.setOnClickListener(this)
    }

    interface DialogClickListener {
        fun dialogDismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null) mListener!!.dialogDismiss()
    }

    override fun sendparams(list: List<String>?) {
        this.list = list
        Handler().postDelayed({
            showUdpServerAdapter = ShowUdpServerAdapter(activity, list)
            list_show_rev_item_detail!!.adapter = showUdpServerAdapter
        }, 50)
    }

    companion object {
        private const val DECODED_CONTENT_KEY = "codedContent"
        var mListener: DialogClickListener? = null
        @JvmStatic
        fun newInstance(context1: Context?, title: String?, message: String?, listener: DialogClickListener?): SearchDialogFragment {
            val frag = SearchDialogFragment()
            val b = Bundle()
            b.putString("title", title)
            b.putString("message", message)
            frag.arguments = b
            mListener = listener
            return frag
        }
    }
}