package com.wachat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Priti Chatterjee on 25-08-2015.
 */
public class ActivityFindPeople extends BaseActivity {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityFindPeople.class);

    private LinearLayout lnr_myContacts, lnr_nearbyUsers, lnr_aroundGlobe;
    private ImageView iv_myContacts, iv_nearbyUsers, iv_aroundGlobe;
    private TextView tv_myContacts, tv_nearbyUsers, tv_aroundGlobe;
    public Toolbar appBar;
    private String searchText = "";
    // private RelativeLayout rel_search;
    //private EditText et_search_hint;
//    private ImageView iv_cross;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);
        // getBundleData();
        initComponent();
        initViews();


    }

    /*private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
            searchText = mBundle.getString(Constants.B_RESULT);
            Log.i(getClass().getSimpleName(), searchText);
        }
    }*/

    private void initViews() {

        //rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        lnr_myContacts = (LinearLayout) findViewById(R.id.lnr_myContacts);
        lnr_nearbyUsers = (LinearLayout) findViewById(R.id.lnr_nearbyUsers);
        lnr_aroundGlobe = (LinearLayout) findViewById(R.id.lnr_aroundGlobe);
//        et_search_hint = (EditText) findViewById(R.id.et_search_hint);
//        iv_cross = (ImageView) findViewById(R.id.iv_cross);
//        iv_cross.setOnClickListener(this);

        iv_myContacts = (ImageView) lnr_myContacts.findViewById(R.id.iv);
        iv_nearbyUsers = (ImageView) lnr_nearbyUsers.findViewById(R.id.iv);
        iv_aroundGlobe = (ImageView) lnr_aroundGlobe.findViewById(R.id.iv);

        tv_myContacts = (TextView) lnr_myContacts.findViewById(R.id.tv);
        tv_nearbyUsers = (TextView) lnr_nearbyUsers.findViewById(R.id.tv);
        tv_aroundGlobe = (TextView) lnr_aroundGlobe.findViewById(R.id.tv);

        populateView();
        clickListeners();
    }

    private void populateView() {

        // et_search_hint.setText(searchText);

        iv_myContacts.setImageResource(R.drawable.ic_my_contacts_icon);
        iv_nearbyUsers.setImageResource(R.drawable.ic_nearby_users_icon);
        iv_aroundGlobe.setImageResource(R.drawable.ic_globe_icon);

        tv_myContacts.setText(getResources().getString(R.string.my_contacts));
        tv_nearbyUsers.setText(getResources().getString(R.string.nearby_user));
        tv_aroundGlobe.setText(getResources().getString(R.string.around_globe));

    }

    private void clickListeners() {
        lnr_myContacts.setOnClickListener(this);
        lnr_nearbyUsers.setOnClickListener(this);
        lnr_aroundGlobe.setOnClickListener(this);
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
        Intent mIntent = new Intent(this, ActivityDash.class);
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
        tv_act_name.setText(getResources().getString(R.string.findPeople));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.lnr_myContacts:
                CommonMethods.hideSoftKeyboard(this);
//                searchText = et_search_hint.getText().toString();
//                if (!TextUtils.isEmpty(searchText)) {
                Intent mint = new Intent(this, ActivityChoosePeople.class);

                Bundle mBundle = new Bundle();
                mBundle.putString(Constants.B_RESULT, searchText);
                mBundle.putString(Constants.B_TYPE, "FindMyContact");
                mint.putExtras(mBundle);

                startActivity(mint);
//                }
                break;
            case R.id.lnr_nearbyUsers:
                CommonMethods.hideSoftKeyboard(this);
//                searchText = et_search_hint.getText().toString();
//                if (!TextUtils.isEmpty(searchText)) {
                if (StringUtils.equalsIgnoreCase(new TableUserInfo(this).getUser().getIsfindbylocation(), "1")) {
                    Intent int1 = new Intent(this, ActivityChoosePeople.class);

                    Bundle mBundle1 = new Bundle();
                    mBundle1.putString(Constants.B_RESULT, searchText);
                    mBundle1.putString(ConstantDB.lat, AppVhortex.Lat);
                    mBundle1.putString(ConstantDB.lng, AppVhortex.Long);
                    mBundle1.putString(Constants.B_TYPE, "FindNearByLoc");
                    int1.putExtras(mBundle1);

                    startActivity(int1);
                } else {
                    showSettingsAlert();

                }
//                }
                break;
            case R.id.lnr_aroundGlobe:
//                CommonMethods.hideSoftKeyboard(this);
//                searchText = et_search_hint.getText().toString();
//                if (!TextUtils.isEmpty(searchText)) {
                if (StringUtils.equalsIgnoreCase(new TableUserInfo(this).getUser().getIsfindbylocation(), "1")) {
                    Intent int2 = new Intent(this, ActivityAroundTheGlobeNew.class);
                    Bundle mBundle2 = new Bundle();
                    mBundle2.putString(ConstantDB.lat, AppVhortex.Lat);
                    mBundle2.putString(ConstantDB.lng, AppVhortex.Long);
                    int2.putExtras(mBundle2);

                    startActivity(int2);
                } else {
                    showSettingsAlert();

                }
//                }
                break;
            case R.id.iv_cross:
//                et_search_hint.setText("");
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void showSettingsAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(getString(R.string.retry_settings_location_alert));
            builder.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ActivityFindPeople.this, ActivitySettings.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
