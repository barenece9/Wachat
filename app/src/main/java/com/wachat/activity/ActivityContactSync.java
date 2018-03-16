package com.wachat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataTextChat;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

public class ActivityContactSync extends BaseActivity {

    public Toolbar appBar;
    private ProgressBarCircularIndeterminate mProgressBarCircularIndeterminate;

    private final BroadcastReceiver contactUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
//                if (!syncDoneGoingToNextScreen) {
                    syncDoneGoingToNextScreen = true;
                    tv_continue.setClickable(true);
                    tv_syncing_message.setText("Contact sync is complete. You can continue now.");
                    tv_continue.setAlpha(1.0f);
                    mProgressBarCircularIndeterminate.setVisibility(View.GONE);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private boolean syncDoneGoingToNextScreen = false;
    private TextView tv_continue;
    private TextView tv_syncing_message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sync);
//        mPrefs = Prefs.getInstance(mActivity);
        initComponent();
        iniViews();

        IntentFilter contactUpdatedIntentFilter = new IntentFilter();
        contactUpdatedIntentFilter.addAction(getString(R.string.ContactBroadcast));
        registerReceiver(contactUpdatedReceiver, contactUpdatedIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppVhortex.getInstance().isContactChangeSyncing) {
            mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void notifySyncComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (!syncDoneGoingToNextScreen) {
                    syncDoneGoingToNextScreen = true;
                    tv_continue.setClickable(true);
                    tv_syncing_message.setText("Contact sync is complete. You can continue now.");
                    tv_continue.setAlpha(1.0f);
                    mProgressBarCircularIndeterminate.setVisibility(View.GONE);
//                }
            }
        });
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);

        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getString(R.string.activity_contact_sync));

    }


    private void iniViews() {
        mProgressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        tv_continue = (TextView) findViewById(R.id.tv_continue);
        tv_syncing_message = (TextView) findViewById(R.id.tv_syncing_message);
        tv_continue.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        tv_continue.setAlpha(0.6f);
        tv_continue.setClickable(false);
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!syncDoneGoingToNextScreen) {
                    ToastUtils.showAlertToast(ActivityContactSync.this,
                            "Please wait while your contacts are being synced", ToastType.FAILURE_ALERT);
                } else {
                    startActivity(new Intent(ActivityContactSync.this, ActivityDash.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(contactUpdatedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }
    @Override
    public boolean RecieveChatFromXMPP(DataTextChat mdDataTextChat) {
        return true;
    }

    @Override
    public boolean shouldShowGroupEventNotification() {
        return false;
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
