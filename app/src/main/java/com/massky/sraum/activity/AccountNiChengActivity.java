package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.widget.SlideSwitchButton;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.BindView;

/**
 * Created by zhu on 2018/1/18.
 */

public class AccountNiChengActivity extends BaseActivity{
    @BindView(R.id.status_view)
    StatusView statusView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.complute_setting)
    TextView complute_setting;
    @BindView(R.id.edit_nicheng)
    ClearEditText edit_nicheng;


    @Override
    protected int viewId() {
        return R.layout.account_nicheng_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
        String account_txt =  getIntent().getStringExtra("nicheng_txt");
        if (account_txt != null) {
            edit_nicheng.setText(account_txt);
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
                AccountNiChengActivity.this.finish();
                break;
            case R.id.complute_setting:
                String account = edit_nicheng.getText().toString();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("nicheng", account);
                intent.putExtras(bundle);
                this.setResult(RESULT_OK, intent);
                AccountNiChengActivity.this.finish();
                break;
        }
    }
}
