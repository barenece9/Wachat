package com.wachat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wachat.GcmOnCompleteListener;
import com.wachat.GcmUtilities;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.application.BaseFragment;
import com.wachat.async.AsyncVersionCheckApi;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataVersionCheck;
import com.wachat.fragment.FragmentDashChat;
import com.wachat.fragment.FragmentDashContacts;
import com.wachat.fragment.FragmentDashFavorite;
import com.wachat.fragment.FragmentDashGroup;
import com.wachat.fragment.FragmentDashMore;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableGroup;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class ActivityDash extends BaseActivity implements View.OnClickListener {

    private AsyncVersionCheckApi versionCheckApi;
    public View iv_topbar_refresh;
    public ProgressBar topbar_progressbar;

    public void registerCreateGroupButtonClickListener(CreateGroupEventDelegate _createGroupEventDelegate) {
        this.createGroupEventDelegate = _createGroupEventDelegate;
    }

    public void unRegisterCreateGroupButtonClickListener() {
        this.createGroupEventDelegate = null;
    }

    public void registerRefreshEventButtonClickListener(RefreshEventDelegate _refreshEventDelegate) {
        this.refreshEventDelegate = _refreshEventDelegate;
    }

    public void unRegisterRefreshEventButtonClickListener() {
        this.refreshEventDelegate = null;
    }

    public interface CreateGroupEventDelegate {
        void onCreateGroupClick();
    }

    public interface RefreshEventDelegate {
        void onRefreshClick();
    }


    public FrameLayout fl_Container;
    private LinearLayout lnrBottom;
    private Constants.DASH_TYPE mDash_type;
    public Toolbar appBar;

    private FrameLayout fl_Chat, fl_Favorite, fl_Group, fl_Profile, fl_More;
    private ImageView iv_Chat, iv_Favorite, iv_Group, iv_Profile, iv_More;
    private TextView tv_Chat_count, tv_Group;

//    public TableContact mTableContact;
    public TableGroup mTableGroup;

    private CreateGroupEventDelegate createGroupEventDelegate;
    private RefreshEventDelegate refreshEventDelegate;

    private final BroadcastReceiver contactUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Fragment mFragment = mFragmentManager.findFragmentById(fl_Container.getId());
                if (mFragment instanceof BaseFragment) {
                    ((BaseFragment) mFragment).notifyFragment();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(TextUtils.isEmpty(Prefs.getInstance(this).getChatId())){
            Intent mIntent = new Intent(this, ActivitySplash.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(mIntent);
            finish();
            return;
        }
        setContentView(R.layout.activity_dash);
//        startActivity(new Intent(this, ActivityContactSync.class));

        if (!mPrefs.isFirstTimeTutorialShown()) {
            startActivity(new Intent(this, ActivityTutorial.class));

        }else{
                callVersionCheckApi();
        }

//        if (!mPrefs.isFirstTimeProfileEditDone()) {
//            startActivity(new Intent(this, ActivityEditProfile.class));
//        }
        initComponent();
        initComponentBottom();

        iv_topbar_refresh = findViewById(R.id.iv_topbar_refresh);
        topbar_progressbar = (ProgressBar)findViewById(R.id.topbar_progressbar);


        Drawable progressDrawable = topbar_progressbar.getIndeterminateDrawable();
        progressDrawable.mutate();
        progressDrawable.setColorFilter(getResources().getColor(R.color.view_yellow_color),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        topbar_progressbar.setIndeterminateDrawable(progressDrawable);
        if (savedInstanceState != null)
            mDash_type = (Constants.DASH_TYPE) savedInstanceState.getSerializable(Constants.B_RESULT);
        else
            mDash_type = Constants.DASH_TYPE.CHAT;


        onNewIntent(getIntent());
        IntentFilter contactUpdatedIntentFilter = new IntentFilter();
        contactUpdatedIntentFilter.addAction(getString(R.string.ContactBroadcast));
        registerReceiver(contactUpdatedReceiver, contactUpdatedIntentFilter);

        if(TextUtils.isEmpty(mPrefs.getGcmId())){
            getGCMRegistered();
        }else{
            Log.d("ActivityDash","GCM Registration ID: "+mPrefs.getGcmId());
        }

    }

    private void getGCMRegistered() {
        GcmUtilities gcmUtilities = new GcmUtilities(this, new GcmOnCompleteListener() {
            @Override
            public void onComplete() {
                Log.i("ActivityRegistration", "GcmUtilities:onComplete");
            }

            @Override
            public void onError(String message) {
                Log.i("ActivityRegistration", "GcmUtilities:onError");
                showGcmRetryAlert(getString(
                        R.string.alert_failure_server_connection_failed_));
            }

            @Override
            public void onCancel() {
                Log.i("ActivityRegistration", "GcmUtilities:onCancel");
            }
        });
    }

    public void showGcmRetryAlert(String alertText) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(alertText);
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (TextUtils.isEmpty(mPrefs.getGcmId())) {
                        getGCMRegistered();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mDash_type = (Constants.DASH_TYPE) bundle.getSerializable(Constants.B_RESULT);
            }
        }

        if(mDash_type==null){
            mDash_type = Constants.DASH_TYPE.CHAT;
        }

//        NotificationUtils.clearAllNotification(this);
        setFragment(mDash_type);
    }


    private void initComponentBottom() {
        findViewById(R.id.lnrBottom).setBackgroundColor(getResources().getColor(R.color.app_Brown));
        iv_Chat = (ImageView) findViewById(R.id.iv_Chat);
        iv_Favorite = (ImageView) findViewById(R.id.iv_Favorite);
        iv_Group = (ImageView) findViewById(R.id.iv_Group);
        iv_Profile = (ImageView) findViewById(R.id.iv_Profile);
        iv_More = (ImageView) findViewById(R.id.iv_More);

        tv_Chat_count = (TextView) findViewById(R.id.tv_Chat_count);
        tv_Group = (TextView) findViewById(R.id.tv_Group);

//count text
        tv_Chat_count.setVisibility(View.GONE);
        tv_Group.setVisibility(View.GONE);

        fl_Chat = (FrameLayout) findViewById(R.id.fl_Chat);
        fl_Favorite = (FrameLayout) findViewById(R.id.fl_Favorite);
        fl_Group = (FrameLayout) findViewById(R.id.fl_Group);
        fl_Profile = (FrameLayout) findViewById(R.id.fl_Profile);
        fl_More = (FrameLayout) findViewById(R.id.fl_More);

        fl_Chat.setOnClickListener(this);
        fl_Favorite.setOnClickListener(this);
        fl_Group.setOnClickListener(this);
        fl_Profile.setOnClickListener(this);
        fl_More.setOnClickListener(this);
    }


    public void setOnoToOneBadgeCount() {
        TableChat tableChat = new TableChat(this);
        String msgCount = tableChat.getUnreadOnetoOneMessagesCount(ConstantChat.STATUS_DELIVERED, mPrefs.getChatId());
        if (!StringUtils.equals(msgCount, "0")) {
            tv_Chat_count.setVisibility(View.VISIBLE);
            tv_Chat_count.setText(msgCount);
        } else {
            tv_Chat_count.setVisibility(View.GONE);
        }

        String groupMessageCount = tableChat.getUnreadGroupMessagesCount(ConstantChat.STATUS_DELIVERED, mPrefs.getChatId());

        if (!StringUtils.equals(groupMessageCount, "0")) {
            tv_Group.setVisibility(View.VISIBLE);
            tv_Group.setText(groupMessageCount);
        } else {
            tv_Group.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_Chat:
                mDash_type = Constants.DASH_TYPE.CHAT;
                showCreateGroupInsteadOfSearch = false;
                showRefreshButtonOnTopBar = false;
                item_right_refresh.setVisible(false);
                item.setIcon(R.drawable.ic_chats_header_search_icon);
                setFragment(mDash_type);
                break;
            case R.id.fl_Favorite:
                mDash_type = Constants.DASH_TYPE.FAVORITE;
                showCreateGroupInsteadOfSearch = false;
                showRefreshButtonOnTopBar = false;
                item_right_refresh.setVisible(false);
                item.setIcon(R.drawable.ic_chats_header_search_icon);
                setFragment(mDash_type);
                break;
            case R.id.fl_Group:
                mDash_type = Constants.DASH_TYPE.GROUP;
                showCreateGroupInsteadOfSearch = true;
                showRefreshButtonOnTopBar = false;
                item_right_refresh.setVisible(false);
                item.setIcon(R.drawable.ic_group_chat_create_group_icon);
                setFragment(mDash_type);
                break;
            case R.id.fl_Profile:
                mDash_type = Constants.DASH_TYPE.CONTACTS;
                showCreateGroupInsteadOfSearch = false;
                showRefreshButtonOnTopBar = true;
                item_right_refresh.setVisible(true);
                item.setIcon(R.drawable.ic_chats_header_search_icon);
                setFragment(mDash_type);
                break;
            case R.id.fl_More:
                mDash_type = Constants.DASH_TYPE.MORE;
                showCreateGroupInsteadOfSearch = false;
                showRefreshButtonOnTopBar = false;
                item_right_refresh.setVisible(false);
                item.setIcon(R.drawable.ic_chats_header_search_icon);
                setFragment(mDash_type);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onMenuRefreshClick() {
        super.onMenuRefreshClick();

        if(refreshEventDelegate!=null){
            refreshEventDelegate.onRefreshClick();
        }
    }

    @Override
    protected void onMenuCreateGroupClick() {
        super.onMenuCreateGroupClick();

        if (createGroupEventDelegate != null) {
            createGroupEventDelegate.onCreateGroupClick();
        }
    }

    private void setImagesDefault() {
        iv_Chat.setSelected(false);
        iv_Favorite.setSelected(false);
        iv_Group.setSelected(false);
        iv_Profile.setSelected(false);
        iv_More.setSelected(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppVhortex.getInstance().isContactChangeSyncing){
            topbar_progressbar.setVisibility(View.VISIBLE);
        }else{
            topbar_progressbar.setVisibility(View.GONE);
        }
        setOnoToOneBadgeCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = mFragmentManager.findFragmentById(fl_Container.getId());
        if (mFragment instanceof FragmentDashChat) {
            super.onBackPressed();
        } else {
            mDash_type = Constants.DASH_TYPE.CHAT;
            setFragment(mDash_type);
        }
    }

    private void initComponent() {
        fl_Container = (FrameLayout) findViewById(R.id.fl_Container);
        lnrBottom = (LinearLayout) findViewById(R.id.lnrBottom);
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        mTableContact = new TableContact(mActivity);
        mTableGroup = new TableGroup(this);
        initSuperViews();
    }

    public void setFragment(Constants.DASH_TYPE mDash_type) {
        setImagesDefault();
        switch (mDash_type) {
            case CHAT:
                iv_Chat.setSelected(true);
                mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentDashChat.getInstance(), mDash_type.toString()).commit();
                break;
            case FAVORITE:
                iv_Favorite.setSelected(true);
                mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentDashFavorite.getInstance(), mDash_type.toString()).commit();
                break;
            case GROUP:
                iv_Group.setSelected(true);
                mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentDashGroup.getInstance(), mDash_type.toString()).commit();
                break;
            case CONTACTS:
                iv_Profile.setSelected(true);
                mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentDashContacts.getInstance(), mDash_type.toString()).commit();
                break;
            case MORE:
                iv_More.setSelected(true);
                mFragmentManager.beginTransaction().replace(fl_Container.getId(), FragmentDashMore.getInstance(), mDash_type.toString()).commit();
                break;
        }
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {
        Intent mIntent = new Intent(this, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, result);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    protected void CommonMenuClcik() {

    }
    @Override
    public void notifyAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(AppVhortex.getInstance().isContactChangeSyncing){
                    topbar_progressbar.setVisibility(View.VISIBLE);
                }else{
                    topbar_progressbar.setVisibility(View.GONE);
                }
                setOnoToOneBadgeCount();
                Fragment mFragment = mFragmentManager.findFragmentById(fl_Container.getId());
                if (mFragment instanceof BaseFragment) {
                    ((BaseFragment) mFragment).notifyFragment();
                }
            }
        });

    }

    private void callVersionCheckApi() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
//            progressBarCircularIndetermininate.setVisibility(View.GONE);
            return;
        }

//        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        versionCheckApi = new AsyncVersionCheckApi(new WebServiceCallBack() {
            @Override
            public void onSuccess(Object result) {
//                progressBarCircularIndetermininate.setVisibility(View.GONE);
                DataVersionCheck dataVersionCheck = (DataVersionCheck) result;
                LogUtils.d("ActivityDash", "AsyncVersionCheckApi:onSuccess " + dataVersionCheck.toString());
                checkAndValidateCurrentVersion(dataVersionCheck);
                versionCheckApi = null;
            }

            @Override
            public void onFailure(Object result) {
                DataVersionCheck dataVersionCheck = (DataVersionCheck) result;
//                progressBarCircularIndetermininate.setVisibility(View.GONE);
                LogUtils.d("ActivityDash", "AsyncVersionCheckApi:onFailure " + dataVersionCheck.toString());
                String alertText = TextUtils.isEmpty(dataVersionCheck.responseDetails) ?
                        getResources().getString(
                                R.string.alert_failure_server_connection_failed_) :
                        dataVersionCheck.responseDetails;
//                ToastUtils.showAlertToast(ActivitySplash.this,
//                        alertText,
//                        ToastType.FAILURE_ALERT);
                showRetryAlert(alertText);
                versionCheckApi = null;
            }

            @Override
            public void onFailure(Throwable e) {
//                progressBarCircularIndetermininate.setVisibility(View.GONE);
                String alertText = CommonMethods.
                        getAlertMessageFromException(ActivityDash.this, e);
                alertText = TextUtils.isEmpty(alertText) ?
                        getResources().getString(
                                R.string.alert_failure_server_connection_failed_) :
                        alertText;
//                ToastUtils.showAlertToast(ActivitySplash.this, alertText
//                        ,
//                        ToastType.FAILURE_ALERT);

                showRetryAlert(alertText);
                versionCheckApi = null;

            }
        });

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            versionCheckApi.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            versionCheckApi.execute();
        }
    }

    private void checkAndValidateCurrentVersion(DataVersionCheck dataVersionCheck) {
        String myVersionName = AppVhortex.getInstance().getAppVersionName();

        if (!TextUtils.isEmpty(myVersionName)) {
            if (TextUtils.isEmpty(dataVersionCheck.version)) {
//                goToNextStep();
            } else {
                if (myVersionName.equals(dataVersionCheck.version)) {
                    //go to next step
//                    goToNextStep();
                } else {
                    //Assuming we will always use version names in valid double
                    // (i.e 1.0 format not 1.0.0 format).
                    double myVersion = 0;
                    try {
                        myVersion = Double.parseDouble(myVersionName);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    double latestPlayStoreVersion = 0;
                    try {
                        latestPlayStoreVersion = Double.parseDouble(dataVersionCheck.version);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    LogUtils.d("ActivityDash", "myVersion:" + myVersion + " latestPlayStoreVersion: " + latestPlayStoreVersion);
                    if (latestPlayStoreVersion > myVersion) {

                        showVersionUpdateAvailableDialog(dataVersionCheck);
                    } else {
                        //go to next step
//                        goToNextStep();
                    }

                }
            }
        } else {
            //Go to next step
//            goToNextStep();
        }
    }

    private void showVersionUpdateAvailableDialog(DataVersionCheck dataVersionCheck) {

        AlertDialog.Builder alertBuilder;

        alertBuilder = new AlertDialog.Builder(this);

        if (dataVersionCheck.mandetory.equalsIgnoreCase("yes")) {
            // must update
            alertBuilder
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openPlayStore();
                        }
                    });
        } else {
            // can skip
            alertBuilder
                    .setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openPlayStore();
                        }
                    });
            alertBuilder.setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    goToNextStep();
                }
            });
        }

        // TextView titleTextView = new TextView(context);
        // // You Can Customise your Title here
        // titleTextView.setText(context.getResources().getString(
        // R.string.app_name));
        // // title.setBackgroundColor(Color.DKGRAY);
        // titleTextView.setPadding(20, 20, 20, 20);
        // titleTextView.setGravity(Gravity.CENTER);
        // titleTextView.setTextColor(context.getResources().getColor(
        // R.color.app_blue));
        // titleTextView.setTextSize(20);
        // alertBuilder.setCustomTitle(titleTextView);
        alertBuilder.setTitle(getResources().getString(
                R.string.app_name));

        // TextView contentTextView = new TextView(context);
        // // You Can Customise your Title here
        // contentTextView.setText(context.getResources().getString(
        // R.string.alert_dialog_new_version_available));
        // // title.setBackgroundColor(Color.DKGRAY);
        // contentTextView.setPadding(15, 20, 15, 20);
        // contentTextView.setGravity(Gravity.LEFT);
        // contentTextView.setTextColor(Color.BLACK);
        // contentTextView.setTextSize(18);
        // alertBuilder.setView(contentTextView);
        alertBuilder.setMessage("A new version of Vhortext is available");
        // alertBuilder.
        AlertDialog dialog = alertBuilder.create();
        if (dataVersionCheck.mandetory.equalsIgnoreCase("yes")) {
            dialog.setCancelable(false);
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                goToNextStep();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void openPlayStore() {
        try {
            final String my_package_name = "com.wachat";
            String url = "";
            // https://play.google.com/store/apps/details?id=in.mygov.mobile
            try {
                // Check whether Google Play store is installed or not:
                getPackageManager().getPackageInfo("com.android.vending",
                        0);

                url = "market://details?id=" + my_package_name;
            } catch (final Exception e) {
                url = "https://play.google.com/store/apps/details?id="
                        + my_package_name;
            }

            // Open the app page in Google Play store:
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRetryAlert(String alertText) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(alertText);
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Prefs.getInstance(ActivityDash.this).getChatId().equals("")
                            || Prefs.getInstance(ActivityDash.this).getChatId().length() < 0) {
                        callVersionCheckApi();
                    } else {
//                        gotoDash();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (versionCheckApi != null) {
                versionCheckApi.cancel(true);
                versionCheckApi = null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(Prefs.getInstance(this).getChatId())){
            try {
                unregisterReceiver(contactUpdatedReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        super.onDestroy();

    }
}
