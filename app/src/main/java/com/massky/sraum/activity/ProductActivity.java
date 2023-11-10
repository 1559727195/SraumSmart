package com.massky.sraum.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.BindView;

/**
 * Created by zhu on 2017/8/25.
 */

public class ProductActivity extends BaseActivity {
    @BindView(R.id.wvMain)
    WebView wvMain;
    @BindView(R.id.pbMain)
    ProgressBar pbMain;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.status_view)
    StatusView statusView;
    private String _url = "https://app.sraum.com/sraumApp/instructions/index.html";

    @Override
    protected int viewId() {
        return R.layout.product_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);
        wvMain.getSettings().setJavaScriptEnabled(true);
        wvMain.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                pbMain.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (pbMain != null)
                    pbMain.setVisibility(View.GONE);
            }
        });
        wvMain.loadUrl(_url);
        back.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
