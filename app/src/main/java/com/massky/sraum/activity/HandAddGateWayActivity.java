package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.EditGateWayResultActivity;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by zhu on 2018/1/18.
 */

public class HandAddGateWayActivity extends BaseActivity {
    @BindView(R.id.status_view)
    StatusView statusView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.complute_setting)
    TextView complute_setting;
    @BindView(R.id.edit_dev_mac)
    ClearEditText edit_dev_mac;
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.hand_add_gateway_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);
//        String account_id = getIntent().getStringExtra("account_id");
//        if (account_id != null) {
//            edit_nicheng.setText(account_id);
//        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        complute_setting.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HandAddGateWayActivity.this.finish();
                break;
            case R.id.complute_setting:
                String account = edit_dev_mac.getText().toString();
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("account_id", account);
//                intent.putExtras(bundle);
//                this.setResult(RESULT_OK, intent);
//                AccountIdActivity.this.finish();
                if (account.equals("")) {
                    ToastUtil.showToast(HandAddGateWayActivity.this, "设备MAC为空");
                    return;
                }

                if (account.length() < 12) {
                    ToastUtil.showToast(HandAddGateWayActivity.this, "设备MAC长度不满12位");
                    return;
                }


                Intent intent = new Intent(HandAddGateWayActivity.this, EditGateWayResultActivity.class);
                intent.putExtra("gateid", account);
                startActivity(intent);
                HandAddGateWayActivity.this.finish();
                //  updateUserId(account);
                break;
        }
    }
}
