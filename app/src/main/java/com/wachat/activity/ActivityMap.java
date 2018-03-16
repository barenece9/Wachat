package com.wachat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.dataClasses.LocationGetSet;
import com.wachat.fragment.FragmentMap;
import com.wachat.util.Constants;

/**
 * Created by Priti Chatterjee on 16-09-2015.
 */
public class ActivityMap extends BaseActivity {

    public FrameLayout fl_Container;
    public Toolbar appBar;
    private RelativeLayout rel_search;
    private EditText et_search_hint;
    private ImageView iv_cross;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initViews();
        initComponent();
    }

    private void initViews() {

        fl_Container = (FrameLayout) findViewById(R.id.fl_Container);
        mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentMap.getInstance(),
                FragmentMap.class.getSimpleName().toString()).commit();
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.setLocation));

    }

    public void backToChat(LocationGetSet mLocationGetSet) {

        Intent resultIntent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, mLocationGetSet);
        resultIntent.putExtras(mBundle);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
}
