package com.massky.sraum.fragment;

import android.app.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.Util.DipUtil;
import com.massky.sraum.Util.QrBitmapUtil;
import com.massky.sraum.activity.ConnWifiCameraActivity;
import com.massky.sraum.view.RoundProgressBar_ChangePosition;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by zhu on 2016/12/23.
 */

public class ConfigDialogCameraFragment extends DialogFragment implements View.OnClickListener,
        ConnWifiCameraActivity.ConnWifiInterfacer {

    private ImageView force_close;
    private Dialog dialog;
    private Button ip_config;
    private EditText ip_txt;
    private SharedPreferences sp_ip;
    private SharedPreferences.Editor ip_edtior;
    private Button start_btn_store;
    //	private  RadarScanView radarScanView;//雷达弹出视图
    private static Context context;
    private Toolbar toolbars;
    private TextView tvPrevTitle;
    private ImageView ivBack;
    private RoundProgressBar_ChangePosition roundProgressBar2;
    private ImageView back;
    private LinearLayout progress_loading_linear;
    private LinearLayout loading_error_linear;
    private boolean is_index;
    private Button conn_btn_cancel;
    private Button conn_btn_again;
    private LinearLayout scan_qrcode_linear;
    private ImageView erweima_img;
    private TextView time_txt;
    private String mode;
    private int times_s;
    private String qr_json;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                is_index = false;
                if (dialog != null) dialog.dismiss();
                break;
            case R.id.conn_btn_again:
                progress_loading_linear.setVisibility(View.VISIBLE);
                loading_error_linear.setVisibility(View.GONE);
                mListener.doRadioScanDevice();
                init_status_bar();
                break;
            case R.id.conn_btn_cancel:
                mListener.dialogDismiss();
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void conn_wifi_over() {
        is_index = false;
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void search_wifi_fail() {
        is_index = false;
        getActivity().runOnUiThread(() -> {
            if (dialog != null) dialog.dismiss();
        });
    }

    public void setQrBitmap(String qr_json) {
        this.qr_json = qr_json;
    }


    public interface DialogClickListener {
        void doRadioWifi();

        void doRadioScanDevice();

        void dialogDismiss();
    }

    static DialogClickListener mListener;

    public ConfigDialogCameraFragment() {

    }

    public static ConfigDialogCameraFragment newInstance(Context context1, String title, String message, DialogClickListener listener) {
        ConfigDialogCameraFragment frag = new ConfigDialogCameraFragment();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        frag.setArguments(b);
        mListener = listener;
        context = context1;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.config_dialog_camera_fragment, null, false);
        mode = getArguments().getString("title");
        String message = getArguments().getString("message");
        if(message != null) qr_json = message;
        initView(view);
        switch_view();
        initEvent();
        init_status_bar();
        //在这里配置wifi
        dialog.setContentView(view);
        setCancelable(true);//这句话调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用 -setCancelable (false);按返回键也不起作用
//        StatusBarCompat.compat(getActivity(), getResources().getColor(R.color.colorgraystatusbar));//更改标题栏的颜色
        return dialog;
    }

    private void switch_view() {
        if (mode == null) return;
        switch (mode) {
            case "scan":
                times_s = 155;
                progress_loading_linear.setVisibility(View.GONE);
                scan_qrcode_linear.setVisibility(View.VISIBLE);
                if (qr_json == null) return;
                Bitmap bitmap = QrBitmapUtil.createQRImage(qr_json,
                        DipUtil.dipToPixel(getActivity(),300),
                        DipUtil.dipToPixel(getActivity(),300));
                if (bitmap == null) return;
                erweima_img.setImageBitmap(bitmap);
                break;
            default:
                times_s = 255;
                progress_loading_linear.setVisibility(View.VISIBLE);
                scan_qrcode_linear.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 让dialogFragment铺满整个屏幕的好办法
     */
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        android.view.WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }


    private void initView(View view) {
        roundProgressBar2 = (RoundProgressBar_ChangePosition) view.findViewById(R.id.roundProgressBar2);
        roundProgressBar2.setAdd_Delete("delete");
        back = (ImageView) view.findViewById(R.id.back);//progress_loading_linear,loading_error_linear
        progress_loading_linear = (LinearLayout) view.findViewById(R.id.progress_loading_linear);
        loading_error_linear = (LinearLayout) view.findViewById(R.id.loading_error_linear);
        conn_btn_cancel = (Button) view.findViewById(R.id.conn_btn_cancel);
        conn_btn_again = (Button) view.findViewById(R.id.conn_btn_again);
        scan_qrcode_linear = view.findViewById(R.id.scan_qrcode_linear);
        erweima_img = view.findViewById(R.id.erweima_img);
        time_txt = view.findViewById(R.id.time_txt);
    }

    private void initEvent() {
        back.setOnClickListener(this);
        conn_btn_again.setOnClickListener(this);
        conn_btn_cancel.setOnClickListener(this);
    }


    private void init_status_bar() {
        roundProgressBar2.setMax(times_s);
        final int[] index = {times_s};
        new Thread(() -> {
            int i = 0;
            is_index = true;
            while (is_index) {
                try {
                    Thread.sleep(1000);
                    Log.e("TAG", "init_status_bar: " + index[0]);
                    switch (mode) {
                        case "scan":
                            int finalI = i;
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                if (time_txt != null)
                                    time_txt.setText(index[0] + "s");
                            });
                            break;
                        default:
                            roundProgressBar2.setProgress(i);
                            break;
                    }
                    i++;
                    index[0]--;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (index[0] < 0) {
                    is_index = false;
                    if (mListener != null)
                        mListener.dialogDismiss();
                    getActivity().runOnUiThread(() -> {
                        if (dialog != null) dialog.dismiss();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        is_index = false;
        if (mListener != null)
            mListener.dialogDismiss();
    }

    public interface OnConnWifiOverListener {
        void onconnwifi_over();
    }
}
