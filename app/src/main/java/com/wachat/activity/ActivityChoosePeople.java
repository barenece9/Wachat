package com.wachat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterChoosePeople;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncFindPeopleByLocation;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataContact;
import com.wachat.dialog.DialogGenderChoice;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.ConstantDB;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 29-08-2015.
 */
public class ActivityChoosePeople extends BaseActivity implements View.OnClickListener, InterfaceResponseCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityChoosePeople.class);

    private RelativeLayout rel_search;
    private EditText et_search_hint;
    private GridView gv_choose_people;
    public Toolbar appBar;
    private DialogGenderChoice mDialogGenderChoice;
    private ImageView iv_cross;
    private String searchText = "", gender = "", Lat = "", Long = "";
    private String isFrom = "";
    private ArrayList<DataContact> mListContact;
    private AdapterChoosePeople mAdapterChoosePeople;
//    private DataProfile mDataProfile;
    private AsyncFindPeopleByLocation mAsyncFindPeopleByLocation;
    private AlertDialog levelDialog;
    private String radius = "150";
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private CharSequence[] radiusItems = {" 0-10 miles ", " 10-50 miles ", " 50-100 miles ", " 100-150 miles "};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_people);
        getBundleData();
//        mDataProfile = new TableUserInfo(mActivity).getUser();
        initViews();
        initComponent();
        initListeners();

        if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc")) {

            getDataFromAPI(searchText);
        } else {

            mListContact = getDataFromDB(searchText, gender);
            mAdapterChoosePeople.refreshList(mListContact);
        }


    }

    private void getDataFromAPI(String searchText) {

        if (!TextUtils.isEmpty(Lat) && !TextUtils.isEmpty(Long) && CommonMethods.isOnline(this)) {
            LogUtils.i(LOG_TAG, "Lat" + AppVhortex.Lat);
            LogUtils.i(LOG_TAG, "Long" + AppVhortex.Long);
            String radiusInKm = String.valueOf(Math.floor(Integer.parseInt(radius) * 1.60934 * 10000000) / 10000000);
            mAsyncFindPeopleByLocation = new AsyncFindPeopleByLocation(mDataProfile.getUserId(),
                    gender, searchText, Lat,
                    Long, radiusInKm, this);
            progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncFindPeopleByLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncFindPeopleByLocation.execute();
            }
        }
    }

    private ArrayList<DataContact> getDataFromDB(String searchText, String gender) {
        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        mListContact = mTableContact.getRegisteredFindMeUser(searchText, gender);
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        return mListContact;
    }

    private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT)) {
            searchText = mBundle.getString(Constants.B_RESULT);
            isFrom = mBundle.getString(Constants.B_TYPE);
            Lat = mBundle.getString(ConstantDB.lat);
            Long = mBundle.getString(ConstantDB.lng);
        }
    }

    private void initViews() {
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        et_search_hint = (EditText) rel_search.findViewById(R.id.et_search_hint);

        iv_cross = (ImageView) findViewById(R.id.iv_cross);
        iv_cross.setOnClickListener(this);

        gv_choose_people = (GridView) findViewById(R.id.gv_choose_people);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_people_found);
        gv_choose_people.setEmptyView(emptyView);

        mAdapterChoosePeople = new AdapterChoosePeople(this, mListContact);


        gv_choose_people.setAdapter(mAdapterChoosePeople);
//set data
        if (!TextUtils.isEmpty(searchText)) {
            et_search_hint.setText(searchText);
            et_search_hint.setSelection(searchText.length());

        } else
            et_search_hint.setHint(getResources().getString(R.string.et_search_hint));
    }


    private void initListeners() {


        et_search_hint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc")) {
                        if (mAsyncFindPeopleByLocation != null && mAsyncFindPeopleByLocation.getStatus() == AsyncTask.Status.RUNNING)
                            mAsyncFindPeopleByLocation.cancel(true);
                        getDataFromAPI(v.getText().toString());

                    } else {
                        mListContact = getDataFromDB(v.getText().toString(), gender);
                        mAdapterChoosePeople.refreshList(mListContact);
                    }
                    return true;
                }
                return false;
            }
        });


        et_search_hint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc")) {
                    if (mAsyncFindPeopleByLocation != null && mAsyncFindPeopleByLocation.getStatus() == AsyncTask.Status.RUNNING)
                        mAsyncFindPeopleByLocation.cancel(true);
                    getDataFromAPI(et_search_hint.getText().toString());

                } else {
                    mListContact = getDataFromDB(s.toString(), gender);
                    mAdapterChoosePeople.refreshList(mListContact);
                }
//                CommonMethods.hideSoftKeyboard(mActivity);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        gv_choose_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataContact mDataContact = mListContact.get(position);
                Intent mIntent;
                if (mDataContact.getFriendId().equals(mDataProfile.getUserId())) {
                    mIntent = new Intent(ActivityChoosePeople.this, ActivityEditProfile.class);
                } else {
                    mIntent = new Intent(ActivityChoosePeople.this, ActivityChat.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.B_OBJ, mDataContact);
                    mIntent.putExtras(bundle);
                    String name = mDataContact.getAppName();
                    if(TextUtils.isEmpty(name)){
                        name = mDataContact.getName();
                    }

                    if(TextUtils.isEmpty(name)){
                        name = "";
                    }
                    mIntent.putExtra(Constants.B_NAME, name);
                    mIntent.putExtra(Constants.B_ID, mDataContact.getFriendId());
                    mIntent.putExtra(Constants.B_RESULT, mDataContact.getChatId());
                    mIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
                    mIntent.putExtra(ConstantDB.ChatId, mDataContact.getChatId());
                    mIntent.putExtra(ConstantDB.PhoneNo, mDataContact.getCountryCode() + mDataContact.getPhoneNumber());
                    mIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());
                }
        /*not required now , It will be required when user need to be saved*/
//                if (!new TableContact(mActivity).checkExistance(mDataContact.getPhoneNumber())) {
//                    new TableContact(mActivity).insertBulkForUnknownNUmber(mDataContact);
//                }


                startActivity(mIntent);
            }
        });
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String search) {
        LogUtils.i(LOG_TAG, search);
        Intent mIntent = new Intent(ActivityChoosePeople.this, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, search);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    protected void CommonMenuClcik() {

        mDialogGenderChoice = new DialogGenderChoice();
        mDialogGenderChoice.show(mFragmentManager, DialogGenderChoice.class.getSimpleName());
    }


    @Override
    protected void onGlobeIconClick(String str, MenuItem item) {
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select radius");
        builder.setItems(radiusItems, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        radius = "10";
                        getDataFromAPI(searchText);
                        break;
                    case 1:
                        radius = "50";
                        getDataFromAPI(searchText);
                        break;
                    case 2:
                        radius = "100";
                        getDataFromAPI(searchText);
                        break;
                    case 3:
                        radius = "150";
                        getDataFromAPI(searchText);
                        break;

                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.choosePeople));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_male:
                gender = "Male";

                if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc"))
                    getDataFromAPI(searchText);
                else {
                    mListContact = getDataFromDB(searchText, gender);
                    mAdapterChoosePeople.refreshList(mListContact);
                }
                mDialogGenderChoice.dismiss();
                break;
            case R.id.tv_female:
                gender = "Female";

                if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc"))
                    getDataFromAPI(searchText);
                else {
                    mListContact = getDataFromDB(searchText, gender);
                    mAdapterChoosePeople.refreshList(mListContact);
                }
                mDialogGenderChoice.dismiss();
                break;
            case R.id.tv_both:
                gender = "";
                if (StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc")) {
                    getDataFromAPI(searchText);
                } else {
                    mListContact = getDataFromDB(searchText, gender);
                    mAdapterChoosePeople.refreshList(mListContact);
                }
                mDialogGenderChoice.dismiss();
                break;
            case R.id.iv_cross:
                et_search_hint.setText("");
                break;
            default:
                super.onClick(v);
                break;
        }
    }


    @Override
    public void onResponseObject(Object mObject) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);

    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        if (mList != null) {
            mAdapterChoosePeople.refreshList((ArrayList<DataContact>) mList);
            mListContact = (ArrayList<DataContact>) mList;
        }
    }

    @Override
    public void onResponseFaliure(String responseText) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
    }

    @Override
    protected boolean shouldShowRadiusFilterInActivityChoosePeaople() {
        return StringUtils.equalsIgnoreCase(isFrom, "FindNearByLoc");
    }
}
