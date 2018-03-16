package com.wachat.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wachat.R;
import com.wachat.adapter.AdapterViewGroupDetails;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncAdminActionApi;
import com.wachat.async.AsyncDeleteGroup;
import com.wachat.async.AsyncLeaveGroup;
import com.wachat.async.AsyncViewGroupDetails;
import com.wachat.callBack.AdapterCallback;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.AdminActionResponse.DataGroupAdminActionResponse;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMember;
import com.wachat.data.DataProfile;
import com.wachat.data.DataTextChat;
import com.wachat.dialog.DialogGroupDetails;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.services.ConstantXMPP;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.DateTimeUtil;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

public class ActivityViewGroupDetails extends BaseActivity implements View.OnClickListener, DialogGroupDetails.GroupMemberActionEvent, AdapterCallback {

    public static final String GROUP_ADMIN_ACTION_PROMOTE = "1";
    public static final String GROUP_ADMIN_ACTION_REMOVE = "2";
    public static final String GROUP_ADMIN_ACTION_BLOCK = "3";
    public static final String GROUP_ADMIN_ACTION_FLAG_UNDO = "0";
    public static final String GROUP_ADMIN_ACTION_FLAG_DO = "1";

    public FragmentManager mFragmentManager;
    DialogGroupDetails mDialogGroupDetails;
    ArrayList<DataGroupMember> mDataGroupAdminList = new ArrayList<DataGroupMember>();
    ArrayList<DataGroupMember> mDataGroupBlockedList = new ArrayList<DataGroupMember>();
    private ImageView iv_GroupImage;
    private RelativeLayout group_details_rl_edit;
    private RelativeLayout group_details_rl_delete;
    private TextView tv_name, tv_createdBy, tv_dateTime, member_count, group_details_media_count;
    private ListView lv_participants, lv_block_participants;
    private LinearLayout lnr_imageContainer;
    private int width;
    private DataGroup mDataGroup;
    private String imageUrl = "";
    private ArrayList<DataGroupMember> mDataGroupMembersArrayList = new ArrayList<DataGroupMember>();
    private AdapterViewGroupDetails mAdapterViewGroupDetails;
    private DataGroupMember dataGroupOwner;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isAdmin = false;
    private boolean isOwner = false;
    private View group_details_exit_group;
    private AsyncLeaveGroup asyncLeaveGroup;
    private AsyncDeleteGroup asyncDeleteGroup;
    private ProgressDialog mPageFilterDialog;
    private DataProfile mDataProfile;
    private AsyncAdminActionApi asyncAdminAction;
    private LinearLayout ll_admin_list_container;
    private AdapterViewGroupDetails mAdapterViewBlockedUserList;
    private LinearLayout ll_blocked_user_list_section;
    private TextView blocked_member_count;
    private TextView admin_count;
    private Context mContext;

    private ScrollView scroll;
    private AsyncViewGroupDetails asyncViewGroupDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_details);
        mFragmentManager = this.getSupportFragmentManager();
        mImageLoader = ImageLoader.getInstance();
        this.mContext = ActivityViewGroupDetails.this;

        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_chats_noimage_profile)
                .showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chat_share_header_back_icon);

        getValueFromIntent();

        mDataProfile = new TableUserInfo(this).getUser();
        initViews();

        setUpMediaList();
        setValues();
        setGroupHeaderDetails();

        callGroupDetailsApi();

    }

    @Override
    protected void notifySyncComplete() {
        super.notifySyncComplete();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getGroupFromDb()) {
                    setUpMediaList();
                    setValues();
                    setGroupHeaderDetails();
                } else {
                    CommonMethods.MYToast(ActivityViewGroupDetails.this, getString(R.string.alert_leave_group_success));
                    Intent intent = new Intent(ActivityViewGroupDetails.this, ActivityDash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(Constants.B_RESULT, Constants.DASH_TYPE.GROUP);
                    intent.putExtras(mBundle);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private boolean getGroupFromDb() {
        TableGroup tableGroup = new TableGroup(this);
        mDataGroup = tableGroup.getGroupById(mDataGroup.getGroupId());

        return !TextUtils.isEmpty(mDataGroup.getGroupId());
    }

    private void callGroupDetailsApi() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            return;
        }
        asyncViewGroupDetails = new AsyncViewGroupDetails(mDataGroup.getGroupId(),true,
                new WebServiceCallBack<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse result) {
                        onGroupDetailsApiCallFinished();
                        asyncViewGroupDetails = null;
                    }

                    @Override
                    public void onFailure(BaseResponse result) {
                        onGroupDetailsApiCallFinished();
                        asyncViewGroupDetails = null;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        onGroupDetailsApiCallFinished();
                        asyncViewGroupDetails = null;
                    }
                });

        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.VISIBLE);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncViewGroupDetails.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncViewGroupDetails.execute();
        }
    }

    private void onGroupDetailsApiCallFinished(){
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.GONE);
        if (getGroupFromDb()) {
            setUpMediaList();
            setValues();
            setGroupHeaderDetails();
        } else {
            CommonMethods.MYToast(ActivityViewGroupDetails.this, getString(R.string.alert_leave_group_success));
            Intent intent = new Intent(ActivityViewGroupDetails.this, ActivityDash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Constants.B_RESULT, Constants.DASH_TYPE.GROUP);
            intent.putExtras(mBundle);
            overridePendingTransition(0, 0);
            startActivity(intent);
            finish();
        }
    }

    //set value in ui
    private void setValues() {
        getMemberFromDb();
        sortOutOwnerAdminAndParticipant();


        setUpAdminList();
        setUpMemberList();

        setUpBlockedList();
    }

    private void setUpAdminList() {

        try {
            ll_admin_list_container.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDataGroupAdminList != null && mDataGroupAdminList.size() > 0) {
            for (int i = 0; i < mDataGroupAdminList.size(); i++) {
                final DataGroupMember admin = mDataGroupAdminList.get(i);
                if (admin != null) {
                    View mView = (LayoutInflater.from(getBaseContext())).inflate(R.layout.include_view_group_details_screen, ll_admin_list_container, false);
                    TextView tv_ProfileName = (TextView) mView.findViewById(R.id.tv_ProfileName);
                    TextView tv_Status = (TextView) mView.findViewById(R.id.tv_Status);
                    ImageView iv_ProfileImage = (ImageView) mView.findViewById(R.id.iv_ProfileImage);


                    String adminName = "";
                    if (mDataProfile.getUserId().equalsIgnoreCase(admin.getGroupMemberUserId())) {
                        adminName = getString(R.string.you);
                    } else {
                        adminName = admin.getMemberName();

                        if (TextUtils.isEmpty(adminName)) {
                            //get friend name from contact table
                            adminName = new TableContact(ActivityViewGroupDetails.this).getFriendDetailsByID(admin.getGroupMemberUserId()).getName();
                        }
                        if (TextUtils.isEmpty(adminName)) {
                            adminName = "+" + admin.getMemberCountryId() + admin.getMemberPhNo();
                        }
                    }
                    tv_ProfileName.setText(adminName);
                    if (!TextUtils.isEmpty(admin.getStatus()))
                        tv_Status.setText(CommonMethods.getUTFDecodedString(admin.getStatus()));

                    if (TextUtils.isEmpty(admin.getMemberImage())) {
                        imageUrl = "";
                    }
                    imageUrl = admin.getMemberImage();
                    mImageLoader.displayImage(imageUrl, iv_ProfileImage, options);


                    mView.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (!admin.getGroupMemberUserId().equalsIgnoreCase(mDataProfile.getUserId())) {

                                        Bundle bundle = new Bundle();
                                        if (admin.getIsOwner().equalsIgnoreCase("0") && isAdmin) {
                                            bundle.putBoolean("isProfileview", false);
                                            bundle.putBoolean("is_another_admin", true);
                                        } else {
                                            bundle.putBoolean("isProfileview", true);
                                        }

                                        bundle.putSerializable("mDataGroupMembers", admin);
                                        mDialogGroupDetails = new DialogGroupDetails();
                                        mDialogGroupDetails.setArguments(bundle);
                                        mDialogGroupDetails.show(mFragmentManager, DialogGroupDetails.class.getSimpleName());
                                    }
                                }
                            });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mView.setNestedScrollingEnabled(true);
                    }

                    ll_admin_list_container.addView(mView);
                }
            }

            admin_count.setText(mDataGroupAdminList.size() + "");
        }


    }

    private void sortOutOwnerAdminAndParticipant() {
        if (mDataGroupMembersArrayList != null && mDataGroupMembersArrayList.size() > 0) {
            //get the owner
            for (int i = 0; i < mDataGroupMembersArrayList.size(); i++) {
                if (mDataGroupMembersArrayList.get(i).getIsOwner().equalsIgnoreCase("1")) {
                    dataGroupOwner = mDataGroupMembersArrayList.get(i);
                    break;
                }

            }

            //get Admin list

            mDataGroupAdminList = new ArrayList<DataGroupMember>();
            for (int i = 0; i < mDataGroupMembersArrayList.size(); i++) {
                if (mDataGroupMembersArrayList.get(i).getIsGrpadmin().equalsIgnoreCase("1")) {
                    if (mDataGroupMembersArrayList.get(i).getGroupMemberUserId().equalsIgnoreCase(mDataProfile.getUserId())) {
                        isAdmin = true;
                    }
                    mDataGroupAdminList.add(mDataGroupMembersArrayList.get(i));
                }

            }

            //remove the admins from the participants list
            for (int i = 0; i < mDataGroupAdminList.size(); i++) {
                mDataGroupMembersArrayList.remove(mDataGroupAdminList.get(i));

            }


            //get Blocked list
            mDataGroupBlockedList = new ArrayList<DataGroupMember>();
            for (int i = 0; i < mDataGroupMembersArrayList.size(); i++) {
                if (mDataGroupMembersArrayList.get(i).getIsGrpblock().equalsIgnoreCase("1")) {
                    mDataGroupBlockedList.add(mDataGroupMembersArrayList.get(i));
                }
            }
            //remove the blocked from the participants list
            for (int i = 0; i < mDataGroupBlockedList.size(); i++) {
                mDataGroupMembersArrayList.remove(mDataGroupBlockedList.get(i));

            }

        }

        if (dataGroupOwner != null &&
                dataGroupOwner.getGroupMemberUserId().
                        equalsIgnoreCase(mDataProfile.getUserId())) {
            isOwner = true;
//            group_details_exit_group.setVisibility(View.GONE);
        }

//        if (!isOwner) {
//            group_details_exit_group.setVisibility(View.VISIBLE);
//        }

//        if (!isOwner) {
        group_details_exit_group.setVisibility(View.VISIBLE);
//        }

        if (isAdmin) {
            group_details_rl_delete.setVisibility(View.VISIBLE);
            group_details_rl_edit.setVisibility(View.VISIBLE);
        } else {
            group_details_rl_delete.setVisibility(View.GONE);
            group_details_rl_edit.setVisibility(View.GONE);
        }
    }

    private void setUpMemberList() {

        mAdapterViewGroupDetails = new AdapterViewGroupDetails(this, mDataGroupMembersArrayList, mImageLoader, this);
        lv_participants.setAdapter(mAdapterViewGroupDetails);
        CommonMethods.setListViewHeightBasedOnChildren(this, lv_participants);
        member_count.setText(mDataGroupMembersArrayList.size() + "");
    }

    private void setUpBlockedList() {

        if (mDataGroupBlockedList != null && mDataGroupBlockedList.size() > 0) {
            ll_blocked_user_list_section.setVisibility(View.VISIBLE);
            mAdapterViewBlockedUserList = new AdapterViewGroupDetails(this, mDataGroupBlockedList, mImageLoader, new AdapterCallback() {
                @Override
                public void OnClickPerformed(Object mObject) {
                    String position = (String) mObject;

                    int positionValue = Integer.parseInt(position);


                    if (!mDataProfile.getUserId().equalsIgnoreCase(mDataGroupBlockedList.get(positionValue).getGroupMemberUserId())) {
                        Bundle bundle = new Bundle();

                        bundle.putBoolean("isProfileview", !isAdmin);
                        bundle.putBoolean("isBlocked", true);
                        bundle.putSerializable("mDataGroupMembers", mDataGroupBlockedList.get(positionValue));

                        mDialogGroupDetails = new DialogGroupDetails();
                        mDialogGroupDetails.setArguments(bundle);
                        mDialogGroupDetails.show(mFragmentManager, DialogGroupDetails.class.getSimpleName());
                    }
                }

                @Override
                public void onPagination() {

                }
            });
            lv_block_participants.setAdapter(mAdapterViewBlockedUserList);
            CommonMethods.setListViewHeightBasedOnChildren(this, lv_block_participants);

            blocked_member_count.setText(mDataGroupBlockedList.size() + "");
        } else {
            ll_blocked_user_list_section.setVisibility(View.GONE);
        }

    }

    private void setUpMediaList() {
        try {
            lnr_imageContainer.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            ArrayList<DataTextChat> mediaList = new TableChat(this).getAllGroupMediaChat(mDataGroup.getGroupId());

            for (int i = 0; i < mediaList.size(); i++) {
                View mView = (LayoutInflater.from(getBaseContext())).inflate(R.layout.imageview_layout, lnr_imageContainer, false);
                ImageView iv = (ImageView) mView.findViewById(R.id.iv);
                ImageView play = (ImageView) mView.findViewById(R.id.cell_video_play);

                if (mediaList.get(i).getMessageType().equalsIgnoreCase(ConstantChat.IMAGE_TYPE)
                        || mediaList.get(i).getMessageType().equalsIgnoreCase(ConstantChat.IMAGECAPTION_TYPE)
                        || mediaList.get(i).getMessageType().equalsIgnoreCase(ConstantChat.SKETCH_TYPE)) {

                    if (!TextUtils.isEmpty(mediaList.get(i).getFilePath())) {
                        final String path = mediaList.get(i).getFilePath();
                        mImageLoader.displayImage("file://" + mediaList.get(i).getFilePath(), iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent mIntent = new Intent(ActivityViewGroupDetails.this, ActivityPinchToZoom.class);
                                Bundle mBundle = new Bundle();
                                mBundle.putString(Constants.B_RESULT, path);
                                mIntent.putExtras(mBundle);
                                startActivity(mIntent);
                            }
                        });
                        play.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(width, width);
                        mLayoutParams.setMargins(5, 5, 5, 5);
                        iv.setLayoutParams(mLayoutParams);
                        lnr_imageContainer.addView(mView);
                    }
                } else if (mediaList.get(i).getMessageType().equalsIgnoreCase(ConstantChat.VIDEO_TYPE)) {
                    if (!TextUtils.isEmpty(mediaList.get(i).getThumbFilePath()) && !TextUtils.isEmpty(mediaList.get(i).getFilePath())) {
                        final String path = mediaList.get(i).getFilePath();
                        mImageLoader.displayImage("file://" + mediaList.get(i).getThumbFilePath(), iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    try {
                                        File file = new File(path);
                                        intent.setDataAndType(Uri.fromFile(file), "video/*");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtils.showAlertToast(ActivityViewGroupDetails.this, getString(R.string.alert_failure_video_path_empty), ToastType.FAILURE_ALERT);
                                    }
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    ToastUtils.showAlertToast(ActivityViewGroupDetails.this, getString(R.string.alert_failure_video_player_not_found), ToastType.FAILURE_ALERT);
                                }
                            }
                        });

                        play.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(width, width);
                        mLayoutParams.setMargins(5, 5, 5, 5);
                        iv.setLayoutParams(mLayoutParams);
                        iv.setTag(i);
                        lnr_imageContainer.addView(mView);
                    }
                }

            }

            int count = lnr_imageContainer.getChildCount();
            group_details_media_count.setText(String.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getValueFromIntent() {

        mDataGroup = (DataGroup) getIntent().getSerializableExtra(Constants.B_RESULT);
    }

    private void initViews() {
        width = CommonMethods.getScreenWidth(this).widthPixels / 4;
        group_details_media_count = (TextView) findViewById(R.id.group_details_media_count);
        //horizental scroll
        lnr_imageContainer = (LinearLayout) findViewById(R.id.lnr_imageContainer);
        group_details_exit_group = findViewById(R.id.group_details_exit_group);
        group_details_exit_group.setOnClickListener(this);

        //top section
        iv_GroupImage = (ImageView) findViewById(R.id.iv_GroupImage);
//        edit = (ImageView) mView.findViewById(R.id.edit);
//        tv_name = (TextView) mView.findViewById(R.id.tv_name);
//        tv_createdBy = (TextView) mView.findViewById(R.id.tv_createdBy);
//        tv_dateTime = (TextView) mView.findViewById(R.id.tv_dateTime);
        group_details_rl_delete = (RelativeLayout) findViewById(R.id.group_details_rl_delete);
        group_details_rl_edit = (RelativeLayout) findViewById(R.id.group_details_rl_edit);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_createdBy = (TextView) findViewById(R.id.tv_createdBy);
        tv_dateTime = (TextView) findViewById(R.id.tv_dateTime);
        //click
        group_details_rl_edit.setOnClickListener(this);
        group_details_rl_delete.setOnClickListener(this);
        //will contain the admin list
        ll_admin_list_container = (LinearLayout) findViewById(R.id.ll_admin_list_container);
//        ll_admin_list_container.setDividerDrawable(getDrawable(R.id.));
        admin_count = (TextView) findViewById(R.id.admin_count);
        //admin section
//        iv_ProfileImage = (ImageView) findViewById(R.id.iv_ProfileImage);
//        tv_ProfileName = (TextView) findViewById(R.id.tv_ProfileName);
//        tv_Status = (TextView) findViewById(R.id.tv_Status);

        //participants listview
        member_count = (TextView) findViewById(R.id.member_count);

        lv_participants = (ListView) findViewById(R.id.lv_participants);


        //block participants
        lv_block_participants = (ListView) findViewById(R.id.lv_block_participants);
        ll_blocked_user_list_section = (LinearLayout) findViewById(R.id.ll_blocked_user_list_section);
        blocked_member_count = (TextView) findViewById(R.id.blocked_member_count);

        scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                int x = (int) toolbar.getX();
                int y = (int) toolbar.getY();
                scroll.smoothScrollTo(x, y);
            }
        });

//        findViewById(R.id.include_group_details_ll_media).requestFocus(0)
//        findViewById(R.id.rel_top).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isAdmin) {
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("isProfileview", true);
////                    bundle.putSerializable("mDataGroupMembers", dataGroupMember);
//                    mDialogGroupDetails = new DialogGroupDetails();
//                    mDialogGroupDetails.setArguments(bundle);
//                    mDialogGroupDetails.show(mFragmentManager, DialogGroupDetails.class.getSimpleName());
//                }
//            }
//        });


//        lv_participants.setOnTouchListener(scrollListTouchListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ll_admin_list_container.setNestedScrollingEnabled(true);
            lv_participants.setNestedScrollingEnabled(true);
            lv_block_participants.setNestedScrollingEnabled(true);
        }
//        lv_block_participants.setOnTouchListener(scrollListTouchListener);

    }


    private void setGroupHeaderDetails() {
        if (mDataGroup.getGroupImage() != null && !mDataGroup.getGroupImage().equals("")) {
            imageUrl = mDataGroup.getGroupImage();
            mImageLoader.displayImage(imageUrl, iv_GroupImage);
        }

        tv_name.setText(mDataGroup.getGroupName());

        if (dataGroupOwner == null) {
            return;
        }
        tv_createdBy.setText("Created on:");
//        tv_createdBy.setVisibility(View.GONE);
//        if (mDataProfile.getUserId().equalsIgnoreCase(dataGroupOwner.getGroupMemberUserId())) {
//            tv_createdBy.setText("created by You");
//        } else {
//            if (TextUtils.isEmpty(dataGroupOwner.getMemberName()))
//                tv_createdBy.setText("created by " + "+" + dataGroupOwner.getMemberCountryId() + dataGroupOwner.getMemberPhNo());
//            else
//                tv_createdBy.setText("created by " + CommonMethods.getUTFDecodedString(dataGroupOwner.getMemberName()));
//        }
        String creationTime = "";
        if (!TextUtils.isEmpty(mDataGroup.getCreationdateTime()) &&
                TextUtils.isDigitsOnly(mDataGroup.getCreationdateTime())) {
            creationTime = DateTimeUtil.convertUtcToLocal(
                    DateTimeUtil.getUTCDateFromMilliSecond(mDataGroup.getCreationdateTime(),
                            "yyyy-MM-dd HH:mm:ss"),
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    mContext.getResources().getConfiguration().locale);


        } else {
            creationTime = DateTimeUtil.convertUtcToLocal(mDataGroup.getCreationdateTime(), "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    mContext.getResources().getConfiguration().locale);

        }
        String createdOnFormattedTime = "";
        String date_time = DateTimeUtil.getFormattedDate(creationTime, "yyyy-MM-dd HH:mm:ss"
                , "MMMM d, yyyy hh:mm a");
        String[] dateTimeArray = StringUtils.split(date_time, " ");
        if (dateTimeArray != null && dateTimeArray.length >= 5) {
            String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
            String time = dateTimeArray[3];

            String am_pm = dateTimeArray[4];
            createdOnFormattedTime = /*"Created on " + " " +*/ date + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
        } else {
            createdOnFormattedTime = creationTime;
        }

        tv_dateTime.setText(createdOnFormattedTime);
    }


    private void getMemberFromDb() {
        mDataGroupMembersArrayList = new TableGroup(ActivityViewGroupDetails.this).
                getMemberListbyID(mDataGroup.getGroupId());
//        mAdapterViewGroupDetails.refreshList(mDataGroupMembersArrayList);
//        CommonMethods.setListViewHeightBasedOnChildren(this, lv_participants);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.group_details_rl_delete:
                if (isAdmin || isOwner) {
                    confirmDeleteGroup();
                } else {
                    //Wont happen coz we are not showing delete only to admin
                }
                break;

            case R.id.group_details_rl_edit:

                Intent mIntent = new Intent(mContext, ActivityEditGroup.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(Constants.B_RESULT, mDataGroup);
                mIntent.putExtras(mBundle);
                startActivityForResult(mIntent, Constants.editGrp);

                break;
            case R.id.group_details_exit_group:

                if (isAdmin || isOwner) {
                    if (mDataGroupAdminList.size() == 1) {
                        showPromoteAnotherAdminInOrderToLeaveGroupAlert();
                    } else {
                        confirmExitGroup();
                    }
                } else {
                    confirmExitGroup();
                }
//                if (checkIfOnlyOneAdmin()) {
//                    if (isOwner) {
//
//                        showPromoteAnotherAdminInOrderToLeaveGroupAlert();
//                    } else {
//                        confirmExitGroup();
//                    }
//                }
                break;
            default:

                break;
        }
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }


    private void confirmDeleteGroup() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(getString(R.string.alert_delete_group));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callDeleteGroupWebService();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void callDeleteGroupWebService() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            return;
        }
        asyncDeleteGroup = new AsyncDeleteGroup(mDataGroup.getGroupId(),
                new WebServiceCallBack<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse result) {
                        onDeleteGroupSuccess(result);
                        asyncDeleteGroup = null;
                    }

                    @Override
                    public void onFailure(BaseResponse result) {

                        onDeleteGroupFailure(result.getResponseDetails());
                        asyncDeleteGroup = null;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        onDeleteGroupFailure(CommonMethods.getAlertMessageFromException(ActivityViewGroupDetails.this, e));
                        asyncDeleteGroup = null;
                    }
                });

        showPageFilterProgress(true);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncDeleteGroup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncDeleteGroup.execute();
        }
    }


    private void onDeleteGroupSuccess(BaseResponse result) {
        deleteAllEntriesForThisGroup();
//        DataCountry mDataCountry = new TableCountry(this).getCountryCode(AppVhortex.getInstance().countryName);

        CommonMethods.MYToast(this, getString(R.string.alert_delete_group_success));
        Intent leaveGroupBroadCastIntent = new Intent("modify_group");
        Bundle bundle = new Bundle();
        bundle.putInt("action", ConstantXMPP.LEAVE_GROUP);
        bundle.putString("group_jid", mDataGroup.getGroupJId());
        leaveGroupBroadCastIntent.putExtras(bundle);
        sendBroadcast(leaveGroupBroadCastIntent);

        showPageFilterProgress(false);
        Intent intent = new Intent(this, ActivityDash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, Constants.DASH_TYPE.GROUP);
        intent.putExtras(mBundle);
        overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    private void onDeleteGroupFailure(String failureAlert) {
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, TextUtils.isEmpty(failureAlert) ? getString(R.string.alert_failure_unknown_error) : failureAlert, ToastType.FAILURE_ALERT);
    }

    private void showPromoteAnotherAdminInOrderToLeaveGroupAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
//            builder.setMessage(getString(R.string.alert_promote_before_leaving_group));
            builder.setMessage(getString(R.string.alert_promote_before_leaving_group));
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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


    private boolean checkIfOnlyOneAdmin() {
        if (mDataGroupAdminList != null && mDataGroupAdminList.size() > 0) {
            for (DataGroupMember admin : mDataGroupAdminList) {
                if (mDataProfile.getUserId().equalsIgnoreCase(admin.getGroupMemberUserId())) {

                    if (mDataGroupAdminList.size() == 1) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    private void confirmExitGroup() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(getString(R.string.alert_leave_group));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callExitGroupWebService();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void callExitGroupWebService() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            return;
        }

        String userId = new TableUserInfo(ActivityViewGroupDetails.this).getUser().getUserId();

        asyncLeaveGroup = new AsyncLeaveGroup(userId, mDataGroup.getGroupId(), isOwner ? "1" : "0",
                new WebServiceCallBack<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse result) {
                        onLeaveGroupSuccess(result);
                        asyncLeaveGroup = null;
                    }

                    @Override
                    public void onFailure(BaseResponse result) {

                        onLeaveGroupFailure(result.getResponseDetails());
                        asyncLeaveGroup = null;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        onLeaveGroupFailure(CommonMethods.getAlertMessageFromException(ActivityViewGroupDetails.this, e));
                        asyncLeaveGroup = null;
                    }
                });

        showPageFilterProgress(true);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncLeaveGroup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncLeaveGroup.execute();
        }
    }

    private void onLeaveGroupFailure(String failureAlert) {
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, TextUtils.isEmpty(failureAlert) ? getString(R.string.alert_failure_unknown_error) : failureAlert, ToastType.FAILURE_ALERT);
    }


//    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        return dependency instanceof AppBarLayout;
//    }
//
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        // check the behavior triggered
//        android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior();
//        if(behavior instanceof AppBarLayout.Behavior) {
//            // do stuff here
//            CommonMethods.MYToast(this,"Scrolling");
//        }
//        return false;
//    }

    private void onLeaveGroupSuccess(BaseResponse result) {
        deleteAllEntriesForThisGroup();
//        DataCountry mDataCountry = new TableCountry(this).getCountryCode(AppVhortex.getInstance().countryName);

        CommonMethods.MYToast(this, getString(R.string.alert_leave_group_success));
        Intent leaveGroupBroadCastIntent = new Intent("modify_group");
        Bundle bundle = new Bundle();
        bundle.putInt("action", ConstantXMPP.LEAVE_GROUP);
        bundle.putString("group_jid", mDataGroup.getGroupJId());
        leaveGroupBroadCastIntent.putExtras(bundle);
        sendBroadcast(leaveGroupBroadCastIntent);

        showPageFilterProgress(false);
        Intent intent = new Intent(this, ActivityDash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, Constants.DASH_TYPE.GROUP);
        intent.putExtras(mBundle);
        overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    private void deleteAllEntriesForThisGroup() {
        TableGroup tableGroup = new TableGroup(this);
        tableGroup.deleteGroup(mDataGroup.getGroupId());

        TableChat tableChat = new TableChat(this);
        tableChat.deleteGroupChat(mDataGroup.getGroupId());

    }

    @Override
    public void OnClickPerformed(Object mObject) {
        String position = (String) mObject;

        int positionValue = Integer.parseInt(position);


        if (!mDataProfile.getUserId().equalsIgnoreCase(mDataGroupMembersArrayList.get(positionValue).getGroupMemberUserId())) {
            Bundle bundle = new Bundle();

            bundle.putBoolean("isProfileview", !isAdmin);
            bundle.putSerializable("mDataGroupMembers", mDataGroupMembersArrayList.get(positionValue));

            mDialogGroupDetails = new DialogGroupDetails();
            mDialogGroupDetails.setArguments(bundle);
            mDialogGroupDetails.show(mFragmentManager, DialogGroupDetails.class.getSimpleName());
        }


    }

    @Override
    public void onPagination() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_deatils, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        super.onCreateOptionsMenu(menu);
//        this.menu = menu;
//        getMenuInflater().inflate(R.menu.menu_group_deatils, menu);
//        MenuItem item = menu.findItem(R.id.item_edit);
//        return true;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.item_right:
                ToastUtils.showToastForUnderDevelopment(this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }

    private void showPageFilterProgress(boolean show) {
        if (show) {

            if (mPageFilterDialog == null) {
                initPageFilterProgressDialog();
            }
            mPageFilterDialog.show();
        } else {
            if (mPageFilterDialog != null && mPageFilterDialog.isShowing()) {
                mPageFilterDialog.dismiss();
            }
        }
    }

    private void initPageFilterProgressDialog() {
        mPageFilterDialog = new ProgressDialog(this);
        mPageFilterDialog.setMessage(getString(R.string.please_wait));
        mPageFilterDialog.setCanceledOnTouchOutside(false);
        mPageFilterDialog.setCancelable(false);
    }

    @Override
    public void onActionItemClick(DataGroupMember dataGroupMember, int clickedViewId) {
        switch (clickedViewId) {
            case R.id.tv_promote:

                if (dataGroupMember.getIsGrpadmin().equalsIgnoreCase("0")) {
                    if (mDataGroupAdminList != null && mDataGroupAdminList.size() < 2) {
                        callAdminActionApi(dataGroupMember, GROUP_ADMIN_ACTION_PROMOTE, GROUP_ADMIN_ACTION_FLAG_DO);
                    } else {
                        CommonMethods.MYToast(this, "Maximum 2 admins are allowed in this group");
                    }
                } else if (dataGroupMember.getIsGrpadmin().equalsIgnoreCase("1")) {
                    callAdminActionApi(dataGroupMember, GROUP_ADMIN_ACTION_PROMOTE, GROUP_ADMIN_ACTION_FLAG_UNDO);
                }


                break;
            case R.id.tv_remove:
                if (dataGroupMember.getIsGrpadmin().equalsIgnoreCase("0")) {
                    confirmRemoveMemberFromGroup(dataGroupMember);

                }
                /*else if(dataGroupMember.getIsGrpadmin().equalsIgnoreCase("1")){
                    CommonMethods.MYToast(this,"");
                }*/


                break;
            case R.id.tv_block:
                if (dataGroupMember.getIsGrpblock().equalsIgnoreCase("0")) {
                    callAdminActionApi(dataGroupMember, GROUP_ADMIN_ACTION_BLOCK, GROUP_ADMIN_ACTION_FLAG_DO);
                } else {
                    callAdminActionApi(dataGroupMember, GROUP_ADMIN_ACTION_BLOCK, GROUP_ADMIN_ACTION_FLAG_UNDO);
                }
                break;
        }
    }


    private void confirmRemoveMemberFromGroup(final DataGroupMember dataGroupMember) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vhortext");
            builder.setMessage(getString(R.string.alert_remove_member_from_group));
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callAdminActionApi(dataGroupMember, GROUP_ADMIN_ACTION_REMOVE, "");
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void callAdminActionApi(final DataGroupMember dataGroupMember,
                                    final String action, final String flag) {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            return;
        }


        asyncAdminAction = new AsyncAdminActionApi(mDataProfile.getUserId(),
                mDataGroup.getGroupId(), dataGroupMember.getGroupMemberUserId(),
                action, flag, new WebServiceCallBack<DataGroupAdminActionResponse>() {

            @Override
            public void onSuccess(DataGroupAdminActionResponse result) {
                onAdminActionApiCallSuccess(result, dataGroupMember, action, flag);
                asyncAdminAction = null;
            }

            @Override
            public void onFailure(DataGroupAdminActionResponse result) {

                onAdminActionApiCallFailure(result.getResponseDetails());
                asyncAdminAction = null;
            }

            @Override
            public void onFailure(Throwable e) {
                onAdminActionApiCallFailure(CommonMethods.
                        getAlertMessageFromException(ActivityViewGroupDetails.this, e));
                asyncAdminAction = null;
            }
        });

        showPageFilterProgress(true);

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncAdminAction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncAdminAction.execute();
        }
    }


    private void onAdminActionApiCallFailure(String failureAlert) {
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, TextUtils.isEmpty(failureAlert) ? getString(R.string.alert_failure_unknown_error) : failureAlert, ToastType.FAILURE_ALERT);
    }


//    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        return dependency instanceof AppBarLayout;
//    }
//
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        // check the behavior triggered
//        android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior();
//        if(behavior instanceof AppBarLayout.Behavior) {
//            // do stuff here
//            CommonMethods.MYToast(this,"Scrolling");
//        }
//        return false;
//    }

    private void onAdminActionApiCallSuccess(DataGroupAdminActionResponse result,
                                             final DataGroupMember dataGroupMember,
                                             final String action, final String flag) {
        showPageFilterProgress(false);
        String message = "";
        if (result != null && result.getResponseCode().equalsIgnoreCase(String.valueOf(VhortextStatusCode.Ok))) {
            TableGroup mTableGroup = new TableGroup(getBaseContext());

            if (action.equalsIgnoreCase(GROUP_ADMIN_ACTION_REMOVE)) {
                message = "Successfully removed " + dataGroupMember.getMemberName();
                mTableGroup.removeGroupMemberAfterAdminActionChange(result.getGroupDetails().getGroupId(),
                        dataGroupMember.getGroupMemberUserId());
            } else if (action.equalsIgnoreCase(GROUP_ADMIN_ACTION_PROMOTE)) {
                if(flag.equals(GROUP_ADMIN_ACTION_FLAG_DO)) {
                    message = "Successfully promoted " + dataGroupMember.getMemberName();
                }else{
                    message = "Successfully demoted " + dataGroupMember.getMemberName();
                }
                mTableGroup.promoteMemberAfterAdminActionChange(result.getGroupDetails().getGroupId(),
                        dataGroupMember.getGroupMemberUserId(), flag);
            } else if (action.equalsIgnoreCase(GROUP_ADMIN_ACTION_BLOCK)) {
                if(flag.equals(GROUP_ADMIN_ACTION_FLAG_DO)) {
                    message = "Successfully blocked " + dataGroupMember.getMemberName();
                }else{
                    message = "Successfully unblocked " + dataGroupMember.getMemberName();
                }
                mTableGroup.blockMemberAfterAdminActionChange(result.getGroupDetails().getGroupId(), dataGroupMember.getGroupMemberUserId(), flag);
            }

            setValues();

//            switch (action) {
//                case GROUP_ADMIN_ACTION_PROMOTE:
//                    removeMemberFromXmppGroup(dataGroupMember);
//                    break;
//            }
        }
        ToastUtils.showAlertToast(this,
                TextUtils.isEmpty(message) ? result.getResponseDetails() : message,
                ToastType.FAILURE_ALERT);
    }

    private void removeMemberFromXmppGroup(DataGroupMember dataGroupMember) {
        if (dataGroupMember != null) {
            showPageFilterProgress(true);
            Intent leaveGroupBroadCastIntent = new Intent("modify_group");
            Bundle bundle = new Bundle();
            bundle.putInt("action", ConstantXMPP.REMOVE_GROUP_MEMBER);
            bundle.putString("group_jid", mDataGroup.getGroupJId());
            bundle.putString("group_member_jid", dataGroupMember.getChatId());
            leaveGroupBroadCastIntent.putExtras(bundle);
            sendBroadcast(leaveGroupBroadCastIntent);
        }
    }


    @Override
    protected void onDestroy() {

        try {
            if (asyncViewGroupDetails != null) {
                asyncViewGroupDetails.cancel(true);
                asyncViewGroupDetails = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (asyncLeaveGroup != null) {
                asyncLeaveGroup.cancel(true);
                asyncLeaveGroup = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (asyncDeleteGroup != null) {
                asyncDeleteGroup.cancel(true);
                asyncDeleteGroup = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (asyncAdminAction != null) {
                asyncAdminAction.cancel(true);
                asyncAdminAction = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Please, use a final int instead of hardcoded int value
        if (requestCode == Constants.editGrp) {
            if (resultCode == RESULT_OK) {
                mDataGroup = new TableGroup(ActivityViewGroupDetails.this).getGroupById(mDataGroup.getGroupId());
                setGroupHeaderDetails();
            }
        }
    }
}


