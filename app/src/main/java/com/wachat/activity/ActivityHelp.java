package com.wachat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.util.WebContstants;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivityHelp extends BaseActivity {

    public Toolbar appBar;
    private View progressBarCircularIndetermininate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_faq);
        initComponent();
        initViews();
    }

    private void initViews() {

    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.help));

        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
        WebView helpFaqWebview = (WebView) findViewById(R.id.help_faq_webview);

        helpFaqWebview.getSettings().setJavaScriptEnabled(true);
        helpFaqWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        helpFaqWebview.setWebViewClient(new WebViewClient() {
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
        helpFaqWebview.loadUrl(WebContstants.help_faq);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.rel_about:

                break;
            case R.id.rel_help:

                break;
            case R.id.rel_contact:

                break;
            case R.id.rel_copyright:

                break;
        }
    }
}
