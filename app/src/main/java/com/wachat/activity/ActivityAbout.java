package com.wachat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wachat.R;
import com.wachat.application.BaseActivity;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivityAbout extends BaseActivity {

    public static final String TARGET_URL = "target_url";
    public static final String TITLE = "title";

    public Toolbar appBar;
    private View progressBarCircularIndetermininate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initComponent();
    }



    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();


        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
        WebView aboutUsWebview = (WebView) findViewById(R.id.about_us_webview);

        aboutUsWebview.getSettings().setJavaScriptEnabled(true);

        aboutUsWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        aboutUsWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBarCircularIndetermininate.setVisibility(View.GONE);
            }

        });


        String targetUrl = getIntent().getExtras().getString(TARGET_URL,"");

        String title = getIntent().getExtras().getString(TITLE,"");

        tv_act_name.setText(title);
//        aboutUsWebview.loadUrl(WebContstants.about_us);
        aboutUsWebview.loadUrl(targetUrl);

    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }

}
