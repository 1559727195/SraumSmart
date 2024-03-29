package com.massky.sraum.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.MycallbackNest;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

/**
 * Created by zhu on 2017/12/29.
 */

public class RegistActivity extends BaseActivity {
    @BindView(R.id.status_view)
    StatusView statusView;
    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.phone_id)
    ClearEditText phone_id;
    @BindView(R.id.registbtn_id)
    Button registbtn_id;
    private DialogUtil dialogUtil;


    @Override
    protected int viewId() {
        return R.layout.register_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        phone_id.setText(IntentUtil.getIntentString(RegistActivity.this, "registerphone"));
        phone_id.setSelection(phone_id.getText().length());
        registbtn_id.setOnClickListener(this);
//        getSms();
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                RegistActivity.this.finish();
                break;
            case R.id.registbtn_id:
                checkNum();
                break;//登录网关
        }
    }


    //验证手机号是否注册，发送验证码
    private void checkNum() {
        String mobilePhone = phone_id.getText().toString();
        if (mobilePhone.equals("")) {
            ToastUtil.showToast(RegistActivity.this, "手机号不能为空");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("mobilePhone", mobilePhone);
            dialogUtil.loadDialog();
            regist_togglen(map);
        }
    }


    /**
     * ApiHelper.sraum_checkMobilePhon
     *
     * @param map
     */
    private void regist_togglen(final Map<String, Object> map) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_checkMobilePhone, map, new MycallbackNest(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
//                regist_togglen(map);
            }
        }, RegistActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(RegistActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                switch (user.result) {
                    case "100":
//                        getCode();
                        String registerphone = phone_id.getText().toString();
                        Intent intent = new Intent(RegistActivity.this, InputCheckCodeActivity.class);
                        intent.putExtra("registerphone",registerphone);
                        intent.putExtra("from","register");
                        startActivity(intent);
                        //IntentUtil.startActivity(RegistActivity.this, InputCheckCodeActivity.class, "registerphone", registerphone);
                        break;
                    case "101":
                        ToastUtil.showToast(RegistActivity.this, "手机号已注册");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(RegistActivity.this, "手机号已注册");
            }
        });
    }
}
