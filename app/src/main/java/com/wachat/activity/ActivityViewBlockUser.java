package com.wachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterViewBlockUser;
import com.wachat.application.BaseActivity;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataContact;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public class ActivityViewBlockUser extends BaseActivity implements AdapterCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityViewBlockUser.class);
    RecyclerView rv;
    public Toolbar appBar;
    private ArrayList<DataContact> mContactArrayList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_block_user);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initViews();
    }


    private void initViews() {
        rv = (RecyclerView) findViewById(R.id.rv);
        TextView emptyView = (TextView)findViewById(android.R.id.empty);
        emptyView.setText(getString(R.string.error_label_no_blocked_user));
        rv.setLayoutManager(new LinearLayoutManager(ActivityViewBlockUser.this, LinearLayoutManager.VERTICAL, false));
        mContactArrayList=mTableContact.getBlockUser();

        if (mContactArrayList.isEmpty()) {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        rv.setAdapter(new AdapterViewBlockUser(this,mContactArrayList,mImageLoader, this,emptyView));
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {
        LogUtils.i(LOG_TAG, result);
        Intent mIntent = new Intent(this, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, result);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    protected void CommonMenuClcik() {

    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.blockUser));
    }

    @Override
    public void OnClickPerformed(Object mObject) {

        if (mObject instanceof DataContact) {
            DataContact mDataContact = (DataContact) mObject;
            Intent mIntent = new Intent(this, ActivityFriendProfile.class);
            mIntent.putExtra(Constants.B_ID ,mDataContact.getFriendId());
            mIntent.putExtra(Constants.B_RESULT,mDataContact.getChatId());
            startActivity(mIntent);
        }

    }

    @Override
    public void onPagination() {

    }
}
