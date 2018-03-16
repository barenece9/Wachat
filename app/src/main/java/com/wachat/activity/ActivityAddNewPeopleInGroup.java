package com.wachat.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wachat.R;
import com.wachat.adapter.AdapterAddNewPeopleInGroup;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncCreateEditGroup;
import com.wachat.callBack.AdapterCallback;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataContactForGroup;
import com.wachat.data.DataCreateOrEditGroup;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMember;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.services.ConstantXMPP;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;


public class ActivityAddNewPeopleInGroup extends BaseActivity implements AdapterCallback, InterfaceResponseCallback {

    public Toolbar appBar;
    public int slected_member_count = 0;
    private RecyclerView rv;
    private Prefs mPrefs;
    private EditText etSearch;
    private String groupname = "";
    private String groupimgPath = "";
    private String userId = "";
    //    private String groupId = "";
//    private String groupJId = "";
    private AsyncCreateEditGroup mAsyncCreateEditGroup;
    private AdapterAddNewPeopleInGroup mAdapterAddPeopleInGroup;
    private DisplayImageOptions options;
    private ProgressBarCircularIndeterminate mProgressBarCircularIndeterminate;
    private boolean isCreatingGroup = false;
    private DataGroup mDataGroup = null;
    private ArrayList<DataGroupMember> mDataGroupMembersArrayList;

    private TableGroup mTableGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people_group);
        mPrefs = Prefs.getInstance(this);
        mTableGroup = new TableGroup(this);
        getValuesFromIntent();
        initComponent();
        initViews();
        initClickListener();
        getContactDataformDB("");
    }


    private void getValuesFromIntent() {

        groupname = getIntent().getExtras().getString("groupname");
        groupimgPath = getIntent().getExtras().getString("groupimg");
        if (!TextUtils.isEmpty(groupimgPath)) {
            groupimgPath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), Uri.fromFile(new File(groupimgPath)));
        }
        userId = mPrefs.getUserId();

        mDataGroup = (DataGroup) getIntent().getSerializableExtra(Constants.B_RESULT);

        mDataGroupMembersArrayList = mTableGroup.
                getMemberListbyID(mDataGroup.getGroupId());
    }

    private void initClickListener() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                performSearch(s);
            }
        });
    }


    private void getContactDataformDB(String searchTxt) {

        ArrayList<DataContactForGroup> contactList = new TableContact(this).
                getRegisteredUserForGroup(searchTxt, mDataGroup.getGroupId());

        mAdapterAddPeopleInGroup.refreshList(contactList);
    }

    private void initViews() {
        mProgressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(ActivityAddNewPeopleInGroup.this, LinearLayoutManager.VERTICAL, false));
        etSearch = (EditText) findViewById(R.id.et_name);

        mAdapterAddPeopleInGroup = new AdapterAddNewPeopleInGroup(this, mImageLoader, this);
        mAdapterAddPeopleInGroup.addAlreadyMembersInSelectedItems(mDataGroupMembersArrayList);
        rv.setAdapter(mAdapterAddPeopleInGroup);

        OnClickPerformed(null);
    }


    private void performSearch(CharSequence s) {
        getContactDataformDB(String.valueOf(s));
    }

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

    private void initComponent() {

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        setTopBarValue();
        tv_right.setOnClickListener(this);
    }

    private void setTopBarValue() {
        tv_title.setText(groupname);
        tv_subTitle.setText(String.valueOf(slected_member_count) + " selected");
        Bitmap bitmap;
        bitmap = AppVhortex.getInstance().getGroupCreateImageBitmap();
        if (bitmap != null) {
            try {
                Drawable d = new BitmapDrawable(getResources(),
                        CommonMethods.getCircularBitmap(Bitmap.createScaledBitmap(bitmap,
                                getResources().getInteger(R.integer.imageWH), getResources().getInteger(R.integer.imageWH), true)));
                appBar.setLogo(d);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (!TextUtils.isEmpty(mDataGroup.getGroupImage())) {
                mImageLoader.loadImage(mDataGroup.getGroupImage(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage != null) {
                            try {
                                Drawable d = new BitmapDrawable(getResources(),
                                        CommonMethods.getCircularBitmap(Bitmap.createScaledBitmap(loadedImage,
                                                getResources().getInteger(R.integer.imageWH),
                                                getResources().getInteger(R.integer.imageWH), true)));
                                appBar.setLogo(d);
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_new_group_icon);
                            Drawable d = new BitmapDrawable(getResources(),
                                    Bitmap.createScaledBitmap(bitmap, getResources().
                                                    getInteger(R.integer.imageWH),
                                            getResources().getInteger(R.integer.imageWH), true));
                            appBar.setLogo(d);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_new_group_icon);
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, getResources().getInteger(R.integer.imageWH), getResources().getInteger(R.integer.imageWH), true));
                appBar.setLogo(d);
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void OnClickPerformed(Object mObject) {
        slected_member_count = mAdapterAddPeopleInGroup.getSelectedCount();

        if (slected_member_count > 1) {
            tv_subTitle.setText(String.valueOf(slected_member_count) + " members");
        } else {
            tv_subTitle.setText(String.valueOf(slected_member_count) + " member");
        }


    }

    @Override
    public void onPagination() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_right:
                if (!isCreatingGroup)
                    if (slected_member_count >= 2)
                        if (CommonMethods.isOnline(this)) {
                            callWSCreateOrEditGroup();
//                        createGroupInXmpp();
                        } else
                            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                    else
                        ToastUtils.showAlertToast(this, getResources().getString(R.string.add_at_least_two_memeber) + " group", ToastType.FAILURE_ALERT);
                break;
        }
    }

    private void callWSCreateOrEditGroup() {
        isCreatingGroup = true;
//        String groupMemberIds = mAdapterAddPeopleInGroup.getSelectedMemberCommaSeperatedIds();
        String newMembers = getNewMembersCommaSeparated();
        mAsyncCreateEditGroup = new AsyncCreateEditGroup(userId,
                groupname, mDataGroup.getGroupJId() + "@conference." + ServiceXmppConnection.SERVICE_NAME,
                newMembers, String.valueOf(mDataGroup.getGroupJId()),
                mDataGroup.getGroupId(), groupimgPath, this);

        if (mAsyncCreateEditGroup != null && mAsyncCreateEditGroup.getStatus() != AsyncTask.Status.RUNNING) {
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
                mAsyncCreateEditGroup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncCreateEditGroup.execute();
            }

        }
    }

    private String getNewMembersCommaSeparated() {
        TreeSet<String> selectedContactSet = mAdapterAddPeopleInGroup.getSelectedMemberConatactSet();
        if (selectedContactSet != null && selectedContactSet.size() > 0) {
            for (DataGroupMember member : mDataGroupMembersArrayList) {
                if (selectedContactSet.contains(member.getGroupMemberUserId())) {
                    selectedContactSet.remove(member.getGroupMemberUserId());
                }
            }
            return StringUtils.join(
                    selectedContactSet.toArray(new String[selectedContactSet.size()]),
                    ",");
        } else {
            return "";
        }

    }


    @Override
    public void onResponseObject(Object mObject) {
        isCreatingGroup = false;
        DataCreateOrEditGroup dataCreateOrEditGroup = ((DataCreateOrEditGroup) mObject);
        mProgressBarCircularIndeterminate.setVisibility(View.GONE);
        AppVhortex.getInstance().setGroupCreateImageBitmap(null);
        ArrayList<DataCreateOrEditGroup> mDataCreateOrEditGroupsArr = new ArrayList<DataCreateOrEditGroup>();
        mDataCreateOrEditGroupsArr.add(dataCreateOrEditGroup);
        mTableGroup.bulkInsertCreatedGroup(mDataCreateOrEditGroupsArr);
        ToastUtils.showAlertToast(this, "Group edited successfully.", ToastType.SUCESS_ALERT);
        Intent mIntent = new Intent(this, ActivityDash.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mIntent);
        overridePendingTransition(0, 0);
    }

//    private void createGroupInXmpp() {
//        isCreatingGroup = true;
//        if (mBound) {
//            mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
//            Message createGroupMessage = Message.obtain();
//            createGroupMessage.what = ConstantXMPP.CREATE_GROUP;
//            Bundle mBundle = new Bundle();
//            mBundle.putString("group_name",CommonMethods.getUTFEncodedString(groupname));
////            mDataGroup.getGroupJId() = String.valueOf(System.currentTimeMillis());
//            mBundle.putString("group_jid",mDataGroup.getGroupJId());
//            mBundle.putString("group_members", mAdapterAddPeopleInGroup.getSelectedMemberCommaSeperatedChatIds());
//            createGroupMessage.setData(mBundle);
//            try {
//                messengerConnection.send(createGroupMessage);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onXmppGroupCreateComplete(int createGroupResultCode) {
        super.onXmppGroupCreateComplete(createGroupResultCode);
        switch (createGroupResultCode) {
            case ConstantXMPP.CREATE_GROUP_SUCCESS:
                callWSCreateOrEditGroup();
                break;
            case ConstantXMPP.CREATE_GROUP_FAILURE:
                onResponseFaliure("Failed to create group");
                isCreatingGroup = false;
                break;
        }
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        isCreatingGroup = false;
    }

    @Override
    public void onResponseFaliure(String responseText) {
        isCreatingGroup = false;
        mProgressBarCircularIndeterminate.setVisibility(View.GONE);
        ToastUtils.showAlertToast(this, responseText, ToastType.FAILURE_ALERT);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
