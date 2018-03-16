package com.wachat.application;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;
import com.wachat.R;
import com.wachat.activity.ActivityAbout;
import com.wachat.activity.ActivityAddNewPeopleInGroup;
import com.wachat.activity.ActivityAddPeopleInGroup;
import com.wachat.activity.ActivityAddToContact;
import com.wachat.activity.ActivityAroundTheGlobe;
import com.wachat.activity.ActivityAroundTheGlobeNew;
import com.wachat.activity.ActivityAuthentication;
import com.wachat.activity.ActivityChat;
import com.wachat.activity.ActivityChoosePeople;
import com.wachat.activity.ActivityContactDetails;
import com.wachat.activity.ActivityContactSync;
import com.wachat.activity.ActivityContactUs;
import com.wachat.activity.ActivityCopyright;
import com.wachat.activity.ActivityCreateNewGroup;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivityEditGroup;
import com.wachat.activity.ActivityEditProfile;
import com.wachat.activity.ActivityFindPeople;
import com.wachat.activity.ActivityFriendProfile;
import com.wachat.activity.ActivityGallery;
import com.wachat.activity.ActivityHelp;
import com.wachat.activity.ActivityInviteFriends;
import com.wachat.activity.ActivityMap;
import com.wachat.activity.ActivityPinchToZoom;
import com.wachat.activity.ActivitySelectLanguage;
import com.wachat.activity.ActivitySettings;
import com.wachat.activity.ActivityShareImage;
import com.wachat.activity.ActivitySketch;
import com.wachat.activity.ActivityUpdateStatus;
import com.wachat.activity.ActivityViewBlockUser;
import com.wachat.activity.ActivityViewGroupDetails;
import com.wachat.activity.ActivityViewLocation;
import com.wachat.activity.ActivityYahooNews;
import com.wachat.activity.ActivityYouTubeVideoList;
import com.wachat.activity.crop.CropImage;
import com.wachat.async.AsyncUpdateUserLocation;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.NotificationUtils;
import com.wachat.chatUtils.OneToOneChatJSonCreator;
import com.wachat.data.DataContact;
import com.wachat.data.DataCountry;
import com.wachat.data.DataProfile;
import com.wachat.data.DataStatus;
import com.wachat.data.DataTextChat;
import com.wachat.services.ConstantXMPP;
import com.wachat.services.ServiceContact;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableUserInfo;
import com.wachat.storage.TableUserStatus;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ImageUtils;
import com.wachat.util.LogUtils;
import com.wachat.util.MediaUtils;
import com.wachat.util.Prefs;
import com.wachat.util.RunnableGetImageCamera;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Gourav Kundu on 17-08-2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        View.OnClickListener, SearchView.OnQueryTextListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);
    private static final long INTERVAL = 1000 * 60;
    private static final long FASTEST_INTERVAL = 1000 * 20;
    protected static Uri outputFileUri;
    public FragmentManager mFragmentManager;
    public ImageLoader mImageLoader;
    public LinearLayout lnr_right, lnr_title_subtitle;
    public ImageView iv_tick;
    public View view_verticalbar;
    public boolean isConfirmClicked = true;
    public TextView tv_right, tv_title, tv_subTitle, tv_act_name;

    /* for location listener config*/
    public Prefs mPrefs;
    public MenuItem item, item_globe;
    public Messenger uiMessenger;
    public String imagePath = "";
    public Location mCurrentLocation = null;
    protected AppVhortex mApp;
    //    protected BaseActivity mActivity;
    protected GoogleApiClient mGoogleApiClient;
    protected DataProfile mDataProfile;
    protected DataStatus mDataStatus;
    protected TableUserInfo mTableUserInfo;
    protected TableUserStatus mTableUserStatus;
    //    private LocationRequest mLocationRequest;
    protected LocationManager locationManager;
    protected Thread backgroundThread = null;
    /**
     * Xmpp Cummunication Variables
     */
    protected Messenger messengerConnection;
    protected boolean mBound = false;
    public ServiceXmppConnection serviceXmppConnection = new ServiceXmppConnection();
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messengerConnection = new Messenger(service);

            mBound = true;
            sendOnlineMessage();
            sucessfullyServiceConnectionConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sendOffOnlineMessage();
            mBound = false;

        }


    };
    protected Menu menu;
    protected MenuItem item_right_image;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    AsyncUpdateUserLocation mAsyncUpdateUserLocation;
    //    SearchView sv;
    private String userId = "";
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    /**
     * Variables For Camera Image Operation
     */
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;
    private int Count = 0;
    private int circelWidth, blurHeight, blurWidth;
    //    private DataTextChat mDataTextChat = new DataTextChat();
    protected MenuItem item_right_first_time_option;
    protected MenuItem item_right;
    protected MenuItem item_right_create_group;
    protected MenuItem item_right_refresh;
    protected boolean showRefreshButtonOnTopBar = false;
    public TableContact mTableContact;

    //umi
    public Timer presenceTimer;
    private int TIMER_VALUE = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = Prefs.getInstance(this);
//        if(((AppVhortex)getApplication()).mDbHelper != null){
        insertUserStatusToDB();
//        }
        initComponents();
        AppVhortex.width = CommonMethods.getScreenWidth(this).widthPixels;

//        if (isGooglePlayServicesAvailable()) {
//           // finish();
//            createLocationRequest();
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(LocationServices.API)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
        mDataProfile = mTableUserInfo.getUser();
//        if (mDataProfile != null && StringUtils.equalsIgnoreCase(mDataProfile.getIsfindbylocation(), "1"))
//            locationServiceStart();
       /* if (StringUtils.equalsIgnoreCase(mDataProfile.getIsfindbylocation(), "1"))
            locationServiceStart();*/
//        volleyRequest();
    }

    protected void locationServiceStart() {

        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGooglePlayServicesAvailable()) {
            if (!isGPSEnabled && !isNetworkEnabled) {
                showGPSSettingsAlert();
            } else {
                createLocationRequest();
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API).addApi(Places.GEO_DATA_API).addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                mGoogleApiClient.connect();
            }
        }
        // startLocationUpdates();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    protected void notifySyncComplete() {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void showGPSSettingsAlert() {
        Log.d("BaseActivity", "showGPSSettingsAlert");
        initLoactionSettingsDialog();

        try {
            locationSettingsAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private AlertDialog locationSettingsAlertDialog;


    private void initLoactionSettingsDialog() {
        if (locationSettingsAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    BaseActivity.this);

            builder.setTitle(getString(R.string.gps_settings_header));

            builder
                    .setMessage(getString(R.string.gps_not_enabled_redirect_message));

            builder.setPositiveButton(getString(R.string.settings),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });

            builder.setNegativeButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            ContentValues mContentValues = new ContentValues();
                            mContentValues.put(ConstantDB.IsFindByLocation, "0");
                            mTableUserInfo.updateStatusForLocationContactAndTranslate(mContentValues, mDataProfile.getUserId());
                            setAutoToggleoffOnGPScancel();
//                        if (mActivity instanceof ActivitySettings) {
//                            ((ActivitySettings) mActivity).setAutoToggleoffOnGPScancel();
//                            mPrefs.setSearchByLocation("false");
//                        }
                        }
                    });
            locationSettingsAlertDialog = builder.create();
            // AlertDialog alert = alertDialog.create();
            // alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    protected void setAutoToggleoffOnGPScancel() {
    }

    private void insertUserStatusToDB() {

        if (new TableUserStatus(BaseActivity.this).isDataAvailable(ConstantDB.TableUserStatus) == 0) {
            for (int i = 0; i < Constants.status.length; i++) {
                DataStatus mDataStatus = new DataStatus();
                mDataStatus.setUserStatus(Constants.status[i]);
                new TableUserStatus(BaseActivity.this).insertNewStatus(mDataStatus);
            }

        }
    }

    private void initComponents() {
        mApp = (AppVhortex) getApplication().getApplicationContext();
//        mActivity = BaseActivity.this;
        mFragmentManager = /*mActivity.*/getSupportFragmentManager();
        mImageLoader = ImageLoader.getInstance();
        uiMessenger = new Messenger(new ServiceChatHandler());
        mTableUserInfo = new TableUserInfo(this);
        mTableUserStatus = new TableUserStatus(this);
        // mDataStatus= mTableUserStatus.getStatus();
        mDataProfile = mTableUserInfo.getUser();
        mTableContact = new TableContact(this);
    }

    protected void initSuperViews() {
        lnr_right = (LinearLayout) findViewById(R.id.lnr_right);
        //right component
        iv_tick = (ImageView) findViewById(R.id.iv_tick);
        view_verticalbar = findViewById(R.id.view_verticalbar);

        tv_right = (TextView) findViewById(R.id.tv_right);
        lnr_title_subtitle = (LinearLayout) findViewById(R.id.lnr_title_subtitle);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_subTitle = (TextView) findViewById(R.id.tv_subTitle);
        tv_act_name = (TextView) findViewById(R.id.tv_act_name);
        TopsectionClickListeners();
    }

    protected void TopsectionClickListeners() {
        lnr_title_subtitle.setOnClickListener(this);
        tv_act_name.setOnClickListener(this);
        iv_tick.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initAppBar();

        mDataProfile = mTableUserInfo.getUser();

        if (mDataProfile != null && StringUtils.equalsIgnoreCase(mDataProfile.getIsfindbylocation(), "1")) {
            if (isLocationEnabled(this)) {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                    if (mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                    LogUtils.d(TAG, "Location update resumed .....................");
//                    }
                } else {
                    locationServiceStart();
                }
            } else {
                showGPSSettingsAlert();
            }

        } else {
            stopLocationUpdates();
            if (mGoogleApiClient != null)
                mGoogleApiClient.disconnect();
        }
        //sendPresenceOnRegularInterval();
    }



    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void initAppBar() {
        if (BaseActivity.this.getClass().getSimpleName().equals(ActivityDash.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityEditProfile.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityViewBlockUser.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivitySettings.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityFindPeople.class.getSimpleName()) ||
//                BaseActivity.this.getClass().getSimpleName().equals(ActivitySelectCountry.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityAuthentication.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityContactSync.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivitySelectLanguage.class.getSimpleName()) ||

                BaseActivity.this.getClass().getSimpleName().equals(ActivityAbout.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityCopyright.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityHelp.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivitySketch.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityMap.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityAddToContact.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityContactDetails.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityViewLocation.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityYouTubeVideoList.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityYahooNews.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityContactUs.class.getSimpleName()) ||
                BaseActivity.this.getClass().getSimpleName().equals(ActivityAbout.class.getSimpleName()) ||

                BaseActivity.this.getClass().getSimpleName().equals(ActivityAroundTheGlobe.class.getSimpleName())) {


            lnr_right.setVisibility(View.GONE);
            lnr_title_subtitle.setVisibility(View.GONE);
            tv_act_name.setVisibility(View.VISIBLE);

        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityCreateNewGroup.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.VISIBLE);
            tv_right.setText("Next");

        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityAddPeopleInGroup.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.GONE);
            tv_right.setText("Done");
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityAddNewPeopleInGroup.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.GONE);
            tv_right.setText("Done");
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityUpdateStatus.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.VISIBLE);
            tv_right.setText("");
            tv_right.setBackgroundResource(R.drawable.ic_sketch_button);
            view_verticalbar.setVisibility(View.GONE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityChoosePeople.class.getSimpleName())) {

            lnr_right.setVisibility(View.GONE);
            iv_tick.setVisibility(View.GONE);
            tv_act_name.setVisibility(View.VISIBLE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityFriendProfile.class.getSimpleName())) {

            lnr_right.setVisibility(View.GONE);
            iv_tick.setVisibility(View.GONE);
            tv_act_name.setVisibility(View.VISIBLE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityShareImage.class.getSimpleName())) {

            lnr_right.setVisibility(View.VISIBLE);
            view_verticalbar.setVisibility(View.GONE);
            iv_tick.setVisibility(View.VISIBLE);
            iv_tick.setImageResource(R.drawable.ic_chats_delete_header_icon);
            tv_right.setText("");
            tv_right.setBackgroundResource(R.drawable.selector_mask_tab);
//            tv_right.setPaintFlags(tv_right.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tv_act_name.setVisibility(View.VISIBLE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityChat.class.getSimpleName())) {
            lnr_right.setVisibility(View.GONE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.GONE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityAroundTheGlobeNew.class.getSimpleName())) {
            lnr_right.setVisibility(View.GONE);
            lnr_title_subtitle.setVisibility(View.GONE);
            tv_act_name.setVisibility(View.VISIBLE);
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityEditGroup.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.VISIBLE);
            tv_right.setText("Next");
        } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityInviteFriends.class.getSimpleName())) {
            lnr_right.setVisibility(View.VISIBLE);
            lnr_title_subtitle.setVisibility(View.VISIBLE);
            tv_act_name.setVisibility(View.VISIBLE);

            tv_right.setText("Done");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            stopLocationUpdates();

        /*if (mBound)
            sendOffOnlineMessage();*/

    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        LogUtils.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lnr_title_subtitle:
                onProfileClick(0);
                break;
            case R.id.tv_act_name:
                onProfileClick(0);
                break;


        }
    }

    public void onNetworkMessage(boolean result) {
        onNetworkChange(result);
    }

    /**
     * This Method Will Be Invoked Once User Change The Language Preference On The Application
     */
    protected abstract void onLangChange();

    /**
     * This Method Will Be Invoked Once Device Network Status Changes From The Current Status
     */
    protected abstract void onNetworkChange(boolean isActive);

    protected void setNavigationBack(int resId) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(resId);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected boolean shouldShowFirstTimeChatSendOptionAtTopBar = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            item = menu.findItem(R.id.menu_search);
//            MenuItem searchItem = menu.findItem(R.id.menu_search);
//            sv = new SearchView(mActivity.getSupportActionBar().getThemedContext());
//            sv = new SearchView(this);
//            int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
//            ImageView closeButton = (ImageView) sv.findViewById(searchImgId);
//            closeButton.setImageResource(R.drawable.ic_chats_header_search_icon);
            //   MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            // MenuItemCompat.setActionView(item, sv);
           /* sv.setIconified(true);
            sv.setIconifiedByDefault(false);
            sv.requestFocusFromTouch();
            sv.setFocusable(true);
            sv.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            });


            MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                    sv.setFocusable(true);
//                    showSoftKeyboard(sv);

                    item.getActionView().requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    hideSoftKeyboard(mActivity, lnr_right);
                    return true;
                }
            });
            sv.setOnQueryTextListener(this);*/

            item_right_refresh = menu.findItem(R.id.item_right_refresh);
            item_right_refresh.setVisible(false);
            changeIcon();
            if (BaseActivity.this.getClass().getSimpleName().equals(ActivityDash.class.getSimpleName())) {
                if (showCreateGroupInsteadOfSearch) {
                    item.setVisible(false);
                    item_right_create_group.setVisible(true);
                } else {
                    item.setVisible(true);
                    item_right_create_group.setVisible(false);
                }

                if (showRefreshButtonOnTopBar) {
                    item_right_refresh.setVisible(true);

                } else {
                    item_right_refresh.setVisible(false);
                }
            }
        }
        return true;

    }

    protected boolean shouldShowRadiusFilterInActivityChoosePeaople() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onSearchPerformed(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        System.out.println("tap");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                finish();
                return true;
            case R.id.item_right:
                CommonMenuClcik();
                break;
            case R.id.item_right_first_time_option:
                showFirstTimeFriendOptionDialog(findViewById(R.id.item_right_first_time_option));
                break;
            case R.id.item_globe:

                if (mTableUserInfo.getUser().getIstransalate().equalsIgnoreCase("1")) {
//                    item.setIcon(R.drawable.ic_chats_translator_icon_deselect);
                    //callFindMeByLIcationWS("1");
                    onGlobeIconClick("1", item);
                } else {
//                    item.setIcon(R.drawable.ic_chats_translator_icon_select);
                    //callFindMeByLIcationWS("0");
                    onGlobeIconClick("0", item);
                }

                break;
            case R.id.menu_search:
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                Intent mIntent = new Intent(BaseActivity.this, ActivityFindPeople.class);
                startActivity(mIntent);
                return false;
            case R.id.item_right_image:
                onMenuProfileImageClick();
                break;

            case R.id.item_right_create_group:
                onMenuCreateGroupClick();
                break;

            case R.id.item_right_refresh:
                onMenuRefreshClick();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onMenuRefreshClick() {

    }

    protected void onMenuCreateGroupClick() {

    }

    protected void showFirstTimeFriendOptionDialog(View v) {

    }

    protected void onMenuProfileImageClick() {

    }

    protected boolean showCreateGroupInsteadOfSearch = false;

    private void changeIcon() {

        if (menu != null) {
            item_right = menu.findItem(R.id.item_right);

            item_right_image = menu.findItem(R.id.item_right_image);
            item_globe = menu.findItem(R.id.item_globe);
            item = menu.findItem(R.id.menu_search);
            item_right_create_group = menu.findItem(R.id.item_right_create_group);
            item_right_first_time_option = menu.findItem(R.id.item_right_first_time_option);
            if (item != null) {
                if (BaseActivity.this.getClass().getSimpleName().equals(ActivityChat.class.getSimpleName())) {

                    showHideFirstTimeMenu();


                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityChoosePeople.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(true);
                    item_right.setIcon(R.drawable.ic_new_chat_screen_filter_icon);
                    if (shouldShowRadiusFilterInActivityChoosePeaople()) {
                        item_globe.setVisible(true);
                        item_globe.setIcon(R.drawable.ic_send_current_location_header_radius_icon);
                    } else {
                        item_globe.setVisible(false);
                    }
                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityFindPeople.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivityViewGroupDetails.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivityContactDetails.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivityAddToContact.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivitySettings.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(false);
                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityHelp.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivityCopyright.class.getSimpleName()) ||
                        BaseActivity.this.getClass().getSimpleName().equals(ActivityAbout.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(false);

                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivitySketch.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(true);
                    item_right.setIcon(R.drawable.ic_sketch_button);
                    item_globe.setVisible(false);
                    item_globe.setIcon(R.drawable.ic_send_current_location_header_radius_icon);

                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityFriendProfile.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(false);
                    item_right.setIcon(R.drawable.ic_header_save_to_phone_icon);
                    item_globe.setVisible(false);
                    item_globe.setIcon(R.drawable.ic_send_current_location_header_radius_icon);

                } else if (BaseActivity.this.getClass().getSimpleName().equals(ActivityAroundTheGlobeNew.class.getSimpleName())) {
                    item.setVisible(false);
                    item_right.setVisible(true);
                    item_right.setIcon(R.drawable.ic_new_chat_screen_filter_icon);
                    item_globe.setVisible(true);
                    item_globe.setIcon(R.drawable.ic_send_current_location_header_radius_icon);
                }
            }
        }
    }

    protected void showHideFirstTimeMenu() {
        if (shouldShowFirstTimeChatSendOptionAtTopBar) {
            item.setVisible(false);
            item_right_image.setVisible(true);
            item_right.setVisible(false);
            item_globe.setVisible(false);

            item_right_first_time_option.setVisible(true);
        } else {
            item.setVisible(false);
            item_right_image.setVisible(true);
            item_right.setVisible(true);
            item_right.setIcon(R.drawable.ic_chat_share_header_attachment_icon);
            item_globe.setVisible(true);
            item_right_first_time_option.setVisible(false);
            //set globe icon
            if (mTableUserInfo.getUser().getIstransalate().equalsIgnoreCase("1")) {
                item_globe.setIcon(R.drawable.ic_chats_translator_icon_select);
            } else {
                item_globe.setIcon(R.drawable.ic_chats_translator_icon_deselect);
            }
        }
    }

    protected void onProfileClick(int id) {

    }

    protected void onGlobeIconClick(String str, MenuItem item) {

    }

    protected abstract void onSearchPerformed(String result);

    protected abstract void CommonMenuClcik();

    protected void onFilterClcik() {

    }

    protected void imageIntentChooser(final ImageView mImageView, int circelWidth, int blurWidth, int blurHeight) {

        this.circelWidth = circelWidth;
        this.blurHeight = blurHeight;
        this.blurWidth = blurWidth;

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setTitle("Select Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    startImageIntentCamra();
                } else if (items[item].equals("Choose from Library")) {
                    Crop.pickImage(BaseActivity.this);
                    //captureImageFromGalery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }


    protected void imageIntentChooserWithoutCameraOption(final ImageView mImageView, int circelWidth, int blurWidth, int blurHeight) {

        this.circelWidth = circelWidth;
        this.blurHeight = blurHeight;
        this.blurWidth = blurWidth;

        Crop.pickImage(BaseActivity.this);

    }

    private void beginCrop(Uri source) {

////        String originalPath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), source);
////        Uri.fromFile(new File(MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), source)))
//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Uri sourceModded = Uri.fromFile(new File(MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), source)));
//                Crop.of(sourceModded, destination).asSquare().start(BaseActivity.this);

        String path = "";
        path = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), source);

        if (TextUtils.isEmpty(path)) {
            ToastUtils.showAlertToast(this, "Failed to fetch the image. Please try again", ToastType.FAILURE_ALERT);
            return;
        }
        File file = null;
        try {
            file = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file == null) {
            ToastUtils.showAlertToast(this, "Failed to fetch the image. Please try again", ToastType.FAILURE_ALERT);
            return;
        }

        Uri uri = Uri.fromFile(file);
        ImageUtils.normalizeImageForUri(this, uri);
        String filePath = file.getPath();

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, filePath);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        startActivityForResult(intent, Crop.REQUEST_CROP);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (outputFileUri != null) {

                processCameraImage(outputFileUri);
            } else {
                String mStringUri = Crop.getOutput(result).toString();
                Uri mUri = Uri.parse(mStringUri);
                if (mUri != null) {
                    processCameraImage(mUri);
                }
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void startImageIntentCamra() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        String fileName = "Image" + Count + ".jpg";
        Count++;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        if (mExternalStorageAvailable || mExternalStorageWriteable) {
            outputFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        System.out.println("**** Add Image from Camera clicked.." + "\n");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (outputFileUri != null)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA, 0);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        long freeinternal = CommonMethods.FreeInternalMemory();
        long freeExternal = CommonMethods.FreeExternalMemory();
        if (mExternalStorageAvailable || mExternalStorageWriteable) {
            if (freeExternal > 10) {

                startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Not Enough Memory", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (freeinternal > 10) {
                startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Not Enough Memory", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void captureImageFromGalery() {
        Intent mIntent = new Intent(this, ActivityGallery.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.CHAT_TO_IMAGE);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent, Constants.ImagePickerChat);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle mBundle;

        if (resultCode != RESULT_OK) {
            if (requestCode == Crop.REQUEST_CROP) {
                if (resultCode != RESULT_CANCELED)
                    ToastUtils.showAlertToast(this, "Failed to process the image", ToastType.FAILURE_ALERT);
            }
            outputFileUri = null;
            return;
        }
//        String extension = "";
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            extension = MediaUtils.getExtensionFromFileName(data.getData().toString());
//            if()
            outputFileUri = data.getData();
            beginCrop(outputFileUri);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == Constants.ACTION_IMAGE_CAPTURE) {
//            handleCrop(resultCode, data);
            if (outputFileUri == null) {
                outputFileUri = data.getData();
            }
            beginCrop(outputFileUri);
        }

        /*switch (requestCode) {

            case Constants.ACTION_IMAGE_CAPTURE:
                if (data != null*//**//*) {
                    processCameraImage();
                }
                break;

        }*/
    }

    private void processCameraImage(final Uri outputFileUri) {

        Handler uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Bundle bundle = msg.getData();
                        Bitmap mBitmap = bundle.getParcelable(Constants.IMAGE);
                        Bitmap blurBitmap = bundle.getParcelable(Constants.IMAGE_BLUR);


                        String encodeString = bundle.getString(Constants.ENCODE);
                        setImage(encodeString, mBitmap, blurBitmap, outputFileUri);

                        if (backgroundThread != null && backgroundThread.isAlive()) {
                            backgroundThread.interrupt();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
        backgroundThread = new Thread(new RunnableGetImageCamera(circelWidth, blurWidth, blurHeight, this, outputFileUri,
                uiHandler));
        backgroundThread.start();
    }

    protected void setImage(String encoded, Bitmap mBitmap, Bitmap mBlur, Uri Uri) {
        outputFileUri = null;
    }

    public void hideSoftKeyboard(Context mContext, View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showSoftKeyboard(View mView) {
        InputMethodManager keyboard = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInputFromWindow(mView.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
        mView.requestFocus();
    }

    public void onCountryListFetchSuccess(ArrayList<DataCountry> mListCountries) {

    }

    protected void sucessfullyServiceConnectionConnected() {

        //override from child activity when need to send anything to xmpp onstart.

//        NotificationUtils.clearAllNotification(this);
    }

    private void sendOnlineMessage() {
        if (mBound) {
            Message onLine = Message.obtain();
            onLine.what = ConstantXMPP.ONLINE;
            onLine.replyTo = uiMessenger;
            try {
                messengerConnection.send(onLine);
               // sendPresenceOnRegularInterval();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


   /* protected void sendTextMessageToXMPP(String message) {
        if (mBound) {
            Message chatMessage = Message.obtain();
            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;

            Bundle bundle = new Bundle();
            bundle.putString("trymessage", message);

            // Set the bundle data to the Message
            chatMessage.setData(bundle);
            try {
                messengerConnection.send(chatMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }*/

    private void sendOffOnlineMessage() {
        if (mBound) {
            Message onLine = Message.obtain();
            onLine.what = ConstantXMPP.OFFLINE;
            try {
                messengerConnection.send(onLine);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppVhortex.getInstance().registerActivity(this);
        if (!AppVhortex.getInstance().serviceContactRunning) {
            if (!TextUtils.isEmpty(Prefs.getInstance(getApplicationContext()).getUserId())) {
                LogUtils.d("BaseActivity", "Call getContactService");
                AppVhortex.getInstance().getContactService(this);

            }
        }
        mDataProfile = mTableUserInfo.getUser();
        if (mApp.isSyncedFirst) {
            mApp.sendContactBroadcast();
        }
      /*  if (!checkContactSynced()){
            startService(new Intent(getApplicationContext(), ServiceContact.class));
        }*/
        if (!(BaseActivity.this instanceof ActivityContactDetails)
                && !(BaseActivity.this instanceof ActivityMap) &&
                !(BaseActivity.this instanceof ActivityShareImage)
                && !(BaseActivity.this instanceof ActivityGallery)
                && !(BaseActivity.this instanceof ActivityPinchToZoom)
                && !(BaseActivity.this instanceof ActivitySketch)
                && !(BaseActivity.this instanceof ActivityViewLocation)
                && !(BaseActivity.this instanceof ActivityFriendProfile)
                && !(BaseActivity.this instanceof ActivityAddNewPeopleInGroup)
//                && !(BaseActivity.this instanceof ActivityAddPeopleInGroup)
//                && !(BaseActivity.this instanceof ActivityCreateNewGroup)
                && !(BaseActivity.this instanceof ActivityEditGroup)
                && !(BaseActivity.this instanceof ActivityViewGroupDetails)
                && !(BaseActivity.this instanceof ActivityAddToContact)
                && !(BaseActivity.this instanceof ActivityYouTubeVideoList)
                && !(BaseActivity.this instanceof ActivityYahooNews)) {

            LogUtils.d("BaseActivity", BaseActivity.this.getClass().getSimpleName() + ":Bind Service");
            bindService(new Intent(BaseActivity.this, ServiceXmppConnection.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        }

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        super.onStop();
        AppVhortex.getInstance().unregisterActivity();
        if (!(BaseActivity.this instanceof ActivityContactDetails)
                && !((BaseActivity.this instanceof ActivityChat))
                && !(BaseActivity.this instanceof ActivityMap)
                && !(BaseActivity.this instanceof ActivityGallery)
                && !(BaseActivity.this instanceof ActivitySketch)
                && !(BaseActivity.this instanceof ActivityPinchToZoom)
                && !(BaseActivity.this instanceof ActivityViewLocation)
                && !(BaseActivity.this instanceof ActivityFriendProfile)
                && !(BaseActivity.this instanceof ActivityAddToContact)
                && !(BaseActivity.this instanceof ActivityAddNewPeopleInGroup)
//                && !(BaseActivity.this instanceof ActivityAddPeopleInGroup)
//                && !(BaseActivity.this instanceof ActivityCreateNewGroup)
                && !(BaseActivity.this instanceof ActivityEditGroup)
                && !(BaseActivity.this instanceof ActivityYahooNews)
                && !(BaseActivity.this instanceof ActivityYouTubeVideoList)
                && !(BaseActivity.this instanceof ActivityViewGroupDetails)
                && !(BaseActivity.this instanceof ActivityShareImage)) {
            if (mBound) {
                Log.d("BaseActivity", BaseActivity.this.getClass().getSimpleName() + ":Unbind Service");
//                sendOffOnlineMessage();
                unbindService(mServiceConnection);

                mBound = false;
            }
        }
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
//        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    public boolean checkContactSynced() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean isAvailable = false;
        isAvailable:
        {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ServiceContact.class.getName().equals(service.service.getClassName())) {
                    isAvailable = true;
                    break isAvailable;
                } else {
                    isAvailable = false;
                }
            }
        }


        return isAvailable;
    }


    protected void addNotification(DataTextChat mDataTextChat) {

        /*add intent to notificaition*/
        Intent notificationIntent = new Intent(getApplicationContext(), ActivityChat.class);

//        get data using friend id
        DataContact mDataContact = mTableContact.
                getFriendDetailsByFrienChatID(mDataTextChat.getFriendChatID());
        String friendName = "";
        friendName = mDataTextChat.getFriendName();
        if (TextUtils.isEmpty(friendName)) {
            friendName = mDataTextChat.getSenderName();
        }

        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            if (mDataContact != null) {
                notificationIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
                notificationIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());

                if (TextUtils.isEmpty(friendName)) {
                    friendName = TextUtils.isEmpty(mDataContact.getAppName()) ? (TextUtils.isEmpty(mDataContact.getName()) ? "" : mDataContact.getName()) : mDataContact.getAppName();
                }

            }
            notificationIntent.putExtra(Constants.B_NAME, friendName);
            notificationIntent.putExtra(Constants.B_RESULT, mDataTextChat.getFriendChatID());
            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
            notificationIntent.putExtra(ConstantDB.ChatId, mDataTextChat.getFriendChatID());
            notificationIntent.putExtra(ConstantDB.PhoneNo, mDataTextChat.getFriendPhoneNo());


        } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            notificationIntent.putExtra(Constants.B_ID, mDataTextChat.getStrGroupID());
            mDataTextChat.setGroupName(new TableGroup(this).getGroupById(mDataTextChat.getStrGroupID()).getGroupName());
            notificationIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_GROUPCHAT);
        }

        notificationIntent.putExtra("fromNotification", "true");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        if (TextUtils.isEmpty(friendName)) {
            friendName = "+" + mDataTextChat.getFriendPhoneNo();
        }
        mDataTextChat.setFriendName(friendName);

        NotificationUtils.showNotificationMessage(notificationIntent, this, mDataTextChat);
    }

    protected void registerChat() {

    }

    @Override
    public void onConnected(Bundle bundle) {
        LogUtils.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        if (mDataProfile.getIsfindbylocation().equalsIgnoreCase("1")) {
//            startLocationUpdates();
            if (locationSettingsAlertDialog != null) {
                locationSettingsAlertDialog.dismiss();
                locationSettingsAlertDialog = null;
            }
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Location mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    AppVhortex.Lat = String.valueOf(mLastLocation.getLatitude());
                    AppVhortex.Long = String.valueOf(mLastLocation.getLongitude());
                    LogUtils.d(TAG, "Location update started .. lat:" + mLastLocation.getLatitude() + " long:" + mLastLocation.getLongitude());

                    updateLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));
                } else {
                    if (mGoogleApiClient != null)
                        mGoogleApiClient.connect();
                }
            }
        }
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                AppVhortex.Lat = String.valueOf(mLastLocation.getLatitude());
                AppVhortex.Long = String.valueOf(mLastLocation.getLongitude());
                LogUtils.d(TAG, "Location update started .. " + mLastLocation.getLongitude());
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogUtils.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.d(TAG, "Firing onLocationChanged..");
        mCurrentLocation = location;
        AppVhortex.Lat = String.valueOf(location.getLatitude());
        AppVhortex.Long = String.valueOf(location.getLongitude());

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        // call asynctask
        updateLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
    }

    private void updateLocation(String lat, String lng) {

        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            //If lat or long has changed since the last update to server
            //only then call update webservice
            if (!lat.equalsIgnoreCase(Prefs.getInstance(this).lastUpdatedLatitude())
                    || !lng.equalsIgnoreCase(Prefs.getInstance(this).lastUpdatedLongitude())) {
                mAsyncUpdateUserLocation = new AsyncUpdateUserLocation(mDataProfile.getUserId(), lat, lng);
                if (CommonMethods.checkBuildVersionAsyncTask()) {
                    mAsyncUpdateUserLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    mAsyncUpdateUserLocation.execute();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        mTableContact.removeSelectionForGroup();
        super.onBackPressed();
    }

    protected void onChatStateReceived(String senderId, String groupJid, String type) {

    }

    protected void onXmppGroupCreateComplete(int createGroupResultCode) {

    }

    public void onMessageReceiveFromGroupSync(final DataTextChat mDataTextChat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shouldShowGroupEventNotification())
                    addNotification(mDataTextChat);
                //ToastUtils.showAlertToast(getApplicationContext(), CommonMethods.getUTFDecodedString(mDataTextChat.toString()), ToastType.SUCESS_ALERT);
            }
        });
    }

    protected boolean shouldShowGroupEventNotification() {
        return true;
    }

    public class ServiceChatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
//                    if (mActivity instanceof ActivityAuthentication) {
//
//                    }
                    registerChat();
                    break;
//                BaseActivity.this
                case ConstantXMPP.RECIEVE_ONETOONE_TEXT_MESSAGE:
                    // Receive Message Notify UI
                    Bundle bundle = msg.getData();
                    final DataTextChat mDataTextChat = (DataTextChat) bundle.getSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (BaseActivity.this instanceof ActivityChat)
//                                if (mActivity instanceof ActivityChat) {
//                                    ((ActivityChat) mActivity).RecieveChatFromXMPP(mDataTextChat);
//                                } else if (mActivity instanceof ActivityDash) {
//                                    addNotification(mDataTextChat);
//                                    ((ActivityDash) mActivity).notifyAdapter();
//                                } else {
//                                    addNotification(mDataTextChat);
//                                }

                            if (ApplicationLifecycleManager.isAppVisible()) {
                                if (!RecieveChatFromXMPP(mDataTextChat)) {
                                    addNotification(mDataTextChat);
                                    notifyAdapter();
                                }
                            } else {
                                RecieveChatFromXMPP(mDataTextChat);
                                addNotification(mDataTextChat);
                                notifyAdapter();

                            }
                            //ToastUtils.showAlertToast(getApplicationContext(), CommonMethods.getUTFDecodedString(mDataTextChat.toString()), ToastType.SUCESS_ALERT);
                        }
                    });
                    break;
                case ConstantXMPP.RECIEVE_ONETOONE_IMAGE_MESSAGE:
                    // Receive Message Notify UI
                    Bundle imageBundle = msg.getData();
                    final DataTextChat mDataFileChat = (DataTextChat) imageBundle.getSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (mActivity instanceof ActivityChat) {
//                                ((ActivityChat) mActivity).RecieveChatFromXMPP(mDataFileChat);
//                            } else if (mActivity instanceof ActivityDash) {
//                                addNotification(mDataFileChat);
//                                ((ActivityDash) mActivity).notifyAdapter();
//                            } else {
//                                addNotification(mDataFileChat);
//                            }
                            if (ApplicationLifecycleManager.isAppVisible()) {
                                if (!RecieveChatFromXMPP(mDataFileChat)) {
                                    addNotification(mDataFileChat);
                                    notifyAdapter();
                                }
                            } else {
                                RecieveChatFromXMPP(mDataFileChat);
                                addNotification(mDataFileChat);
                                notifyAdapter();

                            }
                            //ToastUtils.showAlertToast(getApplicationContext(), CommonMethods.getUTFDecodedString(mDataTextChat.toString()), ToastType.SUCESS_ALERT);
                        }
                    });
                    break;

                case ConstantXMPP.RECIEVE_ONETOONE_CONTACT_MESSAGE:

                    Bundle mBundle = msg.getData();
                    final DataTextChat mDataContact = (DataTextChat) mBundle.getSerializable(ConstantChat.RECIEVE_TEXT_CHAT_B_MESSAGE);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if(!RecieveChatFromXMPP(mDataContact))
//                            if (mActivity instanceof ActivityChat) {
//                                RecieveChatFromXMPP(mDataContact);
//                            } else if (mActivity instanceof ActivityDash) {
//                                addNotification(mDataContact);
//                                ((ActivityDash) mActivity).notifyAdapter();
//                            } else {
//                                addNotification(mDataContact);
//                            }

//                            if (!RecieveChatFromXMPP(mDataContact)) {
//                                addNotification(mDataContact);
//                                notifyAdapter();
//                            }

                            if (ApplicationLifecycleManager.isAppVisible()) {
                                if (!RecieveChatFromXMPP(mDataContact)) {
                                    addNotification(mDataContact);
                                    notifyAdapter();
                                }
                            } else {
                                RecieveChatFromXMPP(mDataContact);
                                addNotification(mDataContact);
                                notifyAdapter();
                            }
                            //ToastUtils.showAlertToast(getApplicationContext(), CommonMethods.getUTFDecodedString(mDataTextChat.toString()), ToastType.SUCESS_ALERT);
                        }
                    });
                    break;

                case ConstantXMPP.CREATE_GROUP_SUCCESS:
                    onXmppGroupCreateComplete(ConstantXMPP.CREATE_GROUP_SUCCESS);
                    break;
                case ConstantXMPP.CREATE_GROUP_FAILURE:
                    onXmppGroupCreateComplete(ConstantXMPP.CREATE_GROUP_FAILURE);
                    break;

                case ConstantXMPP.RECEIVE_CHAT_STATE:
                    Bundle chatStateBundle = msg.getData();
                    String senderId = chatStateBundle.getString(OneToOneChatJSonCreator.SENDERID);
                    String type = chatStateBundle.getString(OneToOneChatJSonCreator.TYPE);
                    String groupJid = chatStateBundle.getString(OneToOneChatJSonCreator.GROUP_JID);
                    onChatStateReceived(senderId, groupJid, type);
                    break;
            }

        }

    }


    protected boolean RecieveChatFromXMPP(DataTextChat mDataContact) {
        return false;
    }

    protected void notifyAdapter() {

    }

    public void sendPresenceOnRegularInterval() {
        //send presence every 3 second to
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    serviceXmppConnection.setUpPresenceOnline();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "Wow..Its working", Toast.LENGTH_SHORT).show();
            }
        };
        //======================================
        //Creation of Timer Object (Need to pass TimerTask())
        //======================================
        presenceTimer = new Timer();
        final Handler presenceHandler = new Handler();
        presenceTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                //use handler post event and pass runnable object
                presenceHandler.post(r);

            }
        }, 1000, TIMER_VALUE);
    }

    public void stopTimerToSendPresence() {
        // Don't forget to cancel timer event before you exit from apps.
//        presenceTimer.cancel();
//        //cleaning all time event and calling GC
//        presenceTimer.purge();
    }



}
