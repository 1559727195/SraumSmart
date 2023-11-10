package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
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

public class AccountIdActivity extends BaseActivity {
    @BindView(R.id.status_view)
    StatusView statusView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.complute_setting)
    TextView complute_setting;
    @BindView(R.id.edit_nicheng)
    ClearEditText edit_nicheng;
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.account_id_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);
        String account_id = getIntent().getStringExtra("account_id");
        if (account_id != null) {
            edit_nicheng.setText(account_id);
        }
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
                AccountIdActivity.this.finish();
                break;
            case R.id.complute_setting:
                String account = edit_nicheng.getText().toString();
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("account_id", account);
//                intent.putExtras(bundle);
//                this.setResult(RESULT_OK, intent);
//                AccountIdActivity.this.finish();
                if (account.equals("")) {
                    ToastUtil.showToast(AccountIdActivity.this, "用户ID为空");
                    return;
                }
                updateUserId(account);
                break;
        }
    }

    //更新用户名
    private void updateUserId(final String userId) {
        dialogUtil.loadDialog();
        sraum_updateUserId(userId);
    }

    private void sraum_updateUserId(final String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AccountIdActivity.this));
        map.put("userId", userId);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateUserId, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_updateUserId(userId);
                    }
                }, AccountIdActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                AccountIdActivity.this.finish();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(AccountIdActivity.this, "名字已存在");
            }
        });
    }
}
