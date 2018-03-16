package com.wachat.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncEnableNotification;
import com.wachat.async.AsyncFindMeByMyLocation;
import com.wachat.async.AsyncFindMeByMyPhoneNumber;
import com.wachat.async.AsyncForUserTranslate;
import com.wachat.data.DataProfile;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.WebContstants;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivitySettings extends BaseActivity implements InterfaceResponseCallback {

    public Toolbar appBar;
    private ToggleButton tgbtn_location, tgbtn_phoneNo, tgbtn_translation;
    private String userId = "";
    private DataProfile mDataProfile = null;
    private ToggleButton tgbtn_notification;
    private TextView tv_toggle_notification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initComponent();
        initViews();
        setValues();
        initClickListeners();

    }


    private void initViews() {
        findViewById(R.id.rel_about).setOnClickListener(this);
        findViewById(R.id.rel_help).setOnClickListener(this);
        findViewById(R.id.rel_contact).setOnClickListener(this);
        findViewById(R.id.rel_copyright).setOnClickListener(this);
        tgbtn_location = (ToggleButton) findViewById(R.id.tgbtn_location);
        tgbtn_phoneNo = (ToggleButton) findViewById(R.id.tgbtn_phoneNo);
        tgbtn_translation = (ToggleButton) findViewById(R.id.tgbtn_translation);
        tgbtn_notification = (ToggleButton) findViewById(R.id.tgbtn_notification);
        tv_toggle_notification = (TextView) findViewById(R.id.tv_toggle_notification);
    }

    private void setValues() {
        mDataProfile = new TableUserInfo(this).getUser();
        userId = mDataProfile.getUserId();

        if (mDataProfile.getIstransalate().equalsIgnoreCase("0")) {
            tgbtn_translation.setChecked(false);
            ((TextView) findViewById(R.id.tv_translate)).setText(getResources().getString(R.string.translation_off));
        } else {
            tgbtn_translation.setChecked(true);
            ((TextView) findViewById(R.id.tv_translate)).setText(getResources().getString(R.string.translation_on));
        }

        if (mDataProfile.getIsfindbylocation().equalsIgnoreCase("0"))
            tgbtn_location.setChecked(false);
        else {
            tgbtn_location.setChecked(true);
            if (CommonMethods.isOnline(this))
                callFindMeByLIcationWS("1");

            locationServiceStart();
        }

        if (mDataProfile.getIsfindbyphoneno().equalsIgnoreCase("0")||mDataProfile.getIsfindbyphoneno().equalsIgnoreCase(""))
            tgbtn_phoneNo.setChecked(true);
        else
            tgbtn_phoneNo.setChecked(false);

        if (mDataProfile.getIsNotificationOn().equalsIgnoreCase("0")) {
            tgbtn_notification.setChecked(false);
            tv_toggle_notification.setText("Notification Off");
        } else {
            tgbtn_notification.setChecked(true);
            tv_toggle_notification.setText("Notification On");
        }

    }


    private void initClickListeners() {


        tgbtn_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (CommonMethods.isOnline(ActivitySettings.this)) {
                        callFindMeByLIcationWS("1");
                        locationServiceStart();
                        // startLocationUpdates();
//                    CommonMethods.MYToast(mActivity, "search enable");
                    }
                } else {
                    if (CommonMethods.isOnline(ActivitySettings.this)) {
                        callFindMeByLIcationWS("0");
                        stopLocationUpdates();
                        if (mGoogleApiClient != null)
                            mGoogleApiClient.disconnect();
//                    CommonMethods.MYToast(mActivity, "search disable");
                    }
                }
            }
        });

        tgbtn_phoneNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callFindMeByContactWS("0");
                } else {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callFindMeByContactWS("1");
                }
            }
        });

        tgbtn_translation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callISTranslatedtWS("1");
                    ((TextView) findViewById(R.id.tv_translate)).setText(getResources().getString(R.string.translation_on));
                } else {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callISTranslatedtWS("0");
                    ((TextView) findViewById(R.id.tv_translate)).setText(getResources().getString(R.string.translation_off));
                }
            }
        });

        tgbtn_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callToggleNotificationApi("1");
                } else {
                    if (CommonMethods.isOnline(ActivitySettings.this))
                        callToggleNotificationApi("0");
                }
            }
        });
    }


    private void callFindMeByLIcationWS(String status) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsFindByLocation, status);
        mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, userId);

        AsyncFindMeByMyLocation mAsyncFindMeByMyLocation =
                new AsyncFindMeByMyLocation(userId, status, this);

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncFindMeByMyLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncFindMeByMyLocation.execute();
        }


    }


    private void callFindMeByContactWS(String status) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsFindByPhoneno, status);
        mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, userId);

        AsyncFindMeByMyPhoneNumber mAsyncFindMeByMyPhoneNumber = new AsyncFindMeByMyPhoneNumber(userId, status, this);


        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncFindMeByMyPhoneNumber.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncFindMeByMyPhoneNumber.execute();
        }


    }


    private void callToggleNotificationApi(String status) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.isNotificationOn, status);


        mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, userId);

        AsyncEnableNotification mAsyncEnableNotification = new AsyncEnableNotification(userId,
                status, new WebServiceCallBack() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(Object result) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });


        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncEnableNotification.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncEnableNotification.execute();
        }


    }

    private void callISTranslatedtWS(String status) {

        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.IsTranslated, status);
        mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, userId);

        AsyncForUserTranslate mAsyncForUserTranslate = new AsyncForUserTranslate(userId, status, this);

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncForUserTranslate.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncForUserTranslate.execute();
        }


    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.settings));
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rel_about:
                Intent mIntent1 = new Intent(ActivitySettings.this, ActivityAbout.class);
                Bundle bundle = new Bundle();
                bundle.putString(ActivityAbout.TARGET_URL, WebContstants.about_us);
                bundle.putString(ActivityAbout.TITLE,getString(R.string.about));
                mIntent1.putExtras(bundle);
                startActivity(mIntent1);
                break;
            case R.id.rel_help:
                Intent mIntent2 = new Intent(ActivitySettings.this, ActivityHelp.class);
                startActivity(mIntent2);
                break;
            case R.id.rel_contact:
                Intent mIntentc = new Intent(ActivitySettings.this, ActivityContactUs.class);
                startActivity(mIntentc);
                break;
            case R.id.rel_copyright:
                Intent mIntent3 = new Intent(ActivitySettings.this, ActivityCopyright.class);
                startActivity(mIntent3);
                break;
        }
    }

    @Override
    public void onResponseObject(Object mObject) {

    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {

    }

    @Override
    protected void setAutoToggleoffOnGPScancel() {
        if (mDataProfile.getIsfindbylocation().equalsIgnoreCase("0")) {
            tgbtn_location.setChecked(false);
            stopLocationUpdates();
            if (mGoogleApiClient != null)
                mGoogleApiClient.disconnect();
        }
    }
}
