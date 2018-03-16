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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wachat.R;
import com.wachat.adapter.AdapterChoosePeople;
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

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 29-08-2015.
 */
public class ActivityAroundTheGlobeNew extends BaseActivity implements View.OnClickListener, InterfaceResponseCallback, PlaceSelectionListener {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityAroundTheGlobeNew.class);
    public Toolbar appBar;
    private RelativeLayout rel_search;
    private GridView gv_choose_people;
    private DialogGenderChoice mDialogGenderChoice;
    private ImageView iv_cross;
    private String searchText = "";
    private String gender = "";
    private String Lat = "";
    private String Long = "";
    private String isFrom = "";
    private ArrayList<DataContact> mListContact;
    private AdapterChoosePeople mAdapterChoosePeople;
//    private DataProfile mDataProfile;
    private AsyncFindPeopleByLocation mAsyncFindPeopleByLocation;
    private AlertDialog levelDialog;
    private String radius = "200";
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private ImageView iv_toggle, iv_select_loc, iv_select_name;
    private RelativeLayout search_option_wrapper, rl_name_srch_Wrapper;
    private CharSequence[] radiusItems = {" 0-10 miles ", " 10-50 miles ", " 50-100 miles ", " 100-150 miles "," 150-200 miles "};
    private TextView tv_srch_loc_option, tv_srch_name_option;
    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private EditText et_name_srch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_the_globe_new);
        getBundleData();
//        mDataProfile = new TableUserInfo(this).getUser();
        initViews();
        initComponent();
        initListeners();
        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));

    }

    private void getDataFromAPI(LatLng mLatLng) {
        if (mLatLng.latitude != 0.0 && mLatLng.longitude != 0.0 && CommonMethods.isOnline(this)) {

            String radiusInKm = String.valueOf(Math.floor(Integer.parseInt(radius)* 1.60934*10000000)/10000000);
            mAsyncFindPeopleByLocation = new AsyncFindPeopleByLocation(mDataProfile.getUserId(),
                    gender, searchText, String.valueOf(mLatLng.latitude),
                    String.valueOf(mLatLng.longitude), radiusInKm, this);
            progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncFindPeopleByLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } else {
                mAsyncFindPeopleByLocation.execute();
            }

        }
    }


    private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();
        Lat = mBundle.getString(ConstantDB.lat);
        Long = mBundle.getString(ConstantDB.lng);
    }

    private void initViews() {
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);

        gv_choose_people = (GridView) findViewById(R.id.gv_choose_people);

        mAdapterChoosePeople = new AdapterChoosePeople(this, mListContact);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);

        gv_choose_people.setAdapter(mAdapterChoosePeople);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        emptyView.setText(R.string.no_people_found);
        gv_choose_people.setEmptyView(emptyView);

        iv_toggle = (ImageView) findViewById(R.id.iv_toggle);
        iv_select_loc = (ImageView) findViewById(R.id.iv_select_loc);
        iv_select_name = (ImageView) findViewById(R.id.iv_select_name);
        search_option_wrapper = (RelativeLayout) findViewById(R.id.search_option_wrapper);
        tv_srch_loc_option = (TextView) findViewById(R.id.tv_srch_loc_option);
        tv_srch_name_option = (TextView) findViewById(R.id.tv_srch_name_option);
        rl_name_srch_Wrapper = (RelativeLayout) findViewById(R.id.rl_name_srch_Wrapper);
        iv_select_loc.setSelected(true);
        et_name_srch = (EditText) findViewById(R.id.et_name_srch);

        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                this.getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Country, Place, Location");

        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    private void initListeners() {

        iv_toggle.setOnClickListener(this);
        iv_select_loc.setOnClickListener(this);
        iv_select_name.setOnClickListener(this);
        tv_srch_loc_option.setOnClickListener(this);
        tv_srch_name_option.setOnClickListener(this);
        rl_name_srch_Wrapper.setOnClickListener(this);
        gv_choose_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataContact mDataContact = mListContact.get(position);
                Intent mIntent;
                if (mDataContact.getFriendId().equals(mDataProfile.getUserId())) {
                    mIntent = new Intent(ActivityAroundTheGlobeNew.this, ActivityEditProfile.class);
                } else {
                    mIntent = new Intent(ActivityAroundTheGlobeNew.this, ActivityChat.class);
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
        /*not required now , It will be required when user need to be saved*/
             /*   if (!new TableContact(mActivity).checkExistance(mDataContact.getPhoneNumber())) {
                    new TableContact(mActivity).insertBulkForUnknownNUmber(mDataContact);
                }*/

                }
                startActivity(mIntent);


            }
        });
        et_name_srch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString().trim();
                if (mAsyncFindPeopleByLocation != null && mAsyncFindPeopleByLocation.getStatus() == AsyncTask.Status.RUNNING)
                    mAsyncFindPeopleByLocation.cancel(true);
                getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        Intent mIntent = new Intent(this, ActivityFindPeople.class);
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
                        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                        break;
                    case 1:
                        radius = "50";
                        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                        break;
                    case 2:
                        radius = "100";
                        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                        break;
                    case 3:
                        radius = "150";
                        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                        break;
                    case 4:
                        radius = "200";
                        getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
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

                getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                mDialogGenderChoice.dismiss();
                break;
            case R.id.tv_female:
                gender = "Female";

                getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                mDialogGenderChoice.dismiss();
                break;
            case R.id.tv_both:
                gender = "";
                getDataFromAPI(new LatLng(Double.parseDouble(Lat), Double.parseDouble(Long)));
                mDialogGenderChoice.dismiss();
                break;

            case R.id.iv_toggle:
                if (search_option_wrapper.isShown()) {
                    search_option_wrapper.setVisibility(View.GONE);
                } else {
                    search_option_wrapper.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_select_loc:
                if (!v.isSelected()) {
                    searchText = "";
                    et_name_srch.setText("");
                    v.setSelected(true);
                    iv_select_name.setSelected(false);
                    rl_name_srch_Wrapper.setVisibility(View.GONE);
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    search_option_wrapper.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_srch_loc_option:
                if (!iv_select_loc.isSelected()) {
                    searchText = "";
                    et_name_srch.setText("");
                    iv_select_loc.setSelected(true);
                    iv_select_name.setSelected(false);
                    rl_name_srch_Wrapper.setVisibility(View.GONE);
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    search_option_wrapper.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_select_name:
                if (!v.isSelected()) {
                    v.setSelected(true);
                    iv_select_loc.setSelected(false);
                    autocompleteFragment.getView().setVisibility(View.GONE);
                    rl_name_srch_Wrapper.setVisibility(View.VISIBLE);
                    search_option_wrapper.setVisibility(View.GONE);
                }
                break;

            case R.id.tv_srch_name_option:
                if (!iv_select_name.isSelected()) {
                    iv_select_name.setSelected(true);
                    iv_select_loc.setSelected(false);
                    autocompleteFragment.getView().setVisibility(View.GONE);
                    rl_name_srch_Wrapper.setVisibility(View.VISIBLE);
                    search_option_wrapper.setVisibility(View.GONE);
                }

                break;

            case R.id.rl_name_srch_Wrapper:
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onResponseObject(Object mObject) {

        mAsyncFindPeopleByLocation = null;
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        try {
            progressBarCircularIndetermininate.setVisibility(View.GONE);
            search_option_wrapper.setVisibility(View.GONE);
            if (mList != null) {
                mAdapterChoosePeople.refreshList((ArrayList<DataContact>) mList);
                TextView emptyView = (TextView) findViewById(R.id.empty_view);
                emptyView.setText(R.string.no_people_found);
                gv_choose_people.setEmptyView(emptyView);
                mListContact = (ArrayList<DataContact>) mList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAsyncFindPeopleByLocation = null;
    }

    @Override
    public void onResponseFaliure(String responseText) {
        try {
            progressBarCircularIndetermininate.setVisibility(View.GONE);

            mAsyncFindPeopleByLocation = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        try {
            if (place != null) {
                iv_toggle.setVisibility(View.VISIBLE);
                getDataFromAPI(place.getLatLng());
                Lat = String.valueOf(place.getLatLng().latitude);
                Long = String.valueOf(place.getLatLng().longitude);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Status status) {
        LogUtils.i(LOG_TAG, "Place : An error occurred: " + status);
    }

    @Override
    protected void onDestroy() {
        if(mAsyncFindPeopleByLocation!=null){
            mAsyncFindPeopleByLocation.cancel(true);
            mAsyncFindPeopleByLocation = null;
        }
        super.onDestroy();
    }
}
