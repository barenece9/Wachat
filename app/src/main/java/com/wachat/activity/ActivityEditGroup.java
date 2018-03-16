package com.wachat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncCreateEditGroup;
import com.wachat.customViews.ChatEditText;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.customViews.components.ChatCustomRelativeWithEmoji;
import com.wachat.data.DataCreateOrEditGroup;
import com.wachat.data.DataGroup;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.TableGroup;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Sanjit on 22-03-2016.
 */
public class ActivityEditGroup extends BaseActivity implements TextWatcher,
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, InterfaceResponseCallback {
    public Toolbar appBar;
    private ImageView iv_Group, iv_smiley;
    private boolean isEmojiOpen = false;
    private RelativeLayout emojicons;
    private boolean keyboardVisible = false;
    private ChatCustomRelativeWithEmoji mChatCustomRelativeWithEmoji;
    private ChatEditText et_name;
    private LinearLayout main_container;
    int smileyHeight = 10;
    private DataGroup mDataGroup = null;
    private ProgressBarCircularIndeterminate mProgressBarCircularIndeterminate;
//    private DataProfile mDataProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        initComponent();
        iniViews();
        getValueFromIntent();
        setEmojiconFragment(false);
        showSmiley(false);
//        mDataProfile = new TableUserInfo(mActivity).getUser();
        setValues();
    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.editGroup));
        tv_right.setOnClickListener(this);
    }

    private void iniViews() {
        mProgressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        emojicons = (RelativeLayout) findViewById(R.id.emojicons);
        main_container = (LinearLayout) findViewById(R.id.main_container);
        mChatCustomRelativeWithEmoji = (ChatCustomRelativeWithEmoji) findViewById(R.id.rel_custom);
        emojicons = (RelativeLayout) findViewById(R.id.emojicons);
        int h = CommonMethods.getScreen(this).heightPixels / 2;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
//        lp.addRule(ViewGroup.ALIGN_PARENT_BOTTOM);
        emojicons.setLayoutParams(lp);

        et_name = mChatCustomRelativeWithEmoji.et_name;
        et_name.setOnClickListener(this);
        iv_Group = (ImageView) findViewById(R.id.iv_Group);
        iv_Group.setOnClickListener(this);
        et_name.addTextChangedListener(this);
        iv_smiley = (ImageView) findViewById(R.id.iv_smiley);
        iv_smiley.setOnClickListener(this);
    }


    private void getValueFromIntent() {

        mDataGroup = (DataGroup) getIntent().getSerializableExtra(Constants.B_RESULT);
    }

    private void setValues() {
        et_name.setText(mDataGroup.getGroupName().trim());
        et_name.setSelection(mDataGroup.getGroupName().length());
        if (!TextUtils.isEmpty(mDataGroup.getGroupImage()))
            mImageLoader.displayImage(mDataGroup.getGroupImage(), iv_Group);

    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private void showSmiley(boolean show) {
        emojicons.setVisibility((emojicons.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(et_name);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(et_name, emojicon);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_right:
                if (et_name.getText().toString().trim().length() > 0) {
                    showSmiley(false);
                    if (emojicons.getVisibility() == View.VISIBLE) {
                        emojicons.setVisibility(View.GONE);

                    }
//                    if (mDataGroup.getGroupName().equalsIgnoreCase(et_name.getText().toString().trim())
//                            && TextUtils.isEmpty(imagePath))
//                        return;

//                    callWSCreateOrEditGroup();
//call asynctask
                    Intent mIntent = new Intent(ActivityEditGroup.this, ActivityAddNewPeopleInGroup.class);
                    mIntent.putExtra("groupname", et_name.getText().toString().trim());
                    mIntent.putExtra("groupimg", imagePath);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(Constants.B_RESULT, mDataGroup);
                    mIntent.putExtras(mBundle);

                    startActivity(mIntent);
                    // finish();
                } else {
                    ToastUtils.showAlertToast(this, "Group name cannot be left blank.", ToastType.FAILURE_ALERT);
                }
                break;

            case R.id.iv_Group:
                imageIntentChooser(iv_Group, Math.max(iv_Group.getWidth(), iv_Group.getHeight()), iv_Group.getWidth(), iv_Group.getHeight());
//                imageIntentChooserWithoutCameraOption(iv_Group, Math.max(iv_Group.getWidth(), iv_Group.getHeight()), iv_Group.getWidth(), iv_Group.getHeight());
                break;

            case R.id.iv_smiley:

//                if (!keyboardVisible) {
//                    keyboardEvent();
//                    showSmiley(true);
//                    hideSoftKeyboard(mActivity, et_name);
//                    keyboardVisible = true;
//                    setEmojiconFragment(false);
//                } else {
//                    showSmiley(false);
//                    showSoftKeyboard(main_container);
//                    keyboardVisible = false;
//
//                }
                keyboardEvent();
                hideSoftKeyboard(this, v);
//
//                //int h = CommonMethods.getScreen(mActivity).heightPixels / 2;
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, smileyHeight);
////        lp.addRule(ViewGroup.ALIGN_PARENT_BOTTOM);
//                emojicons.setLayoutParams(lp);
                showSmiley(true);
                break;

            case R.id.et_name:
                showSmiley(false);
                if (emojicons.getVisibility() == View.VISIBLE) {
                    emojicons.setVisibility(View.GONE);

                }
                break;
        }


    }

    private boolean keyboardEvent() {
        main_container = (LinearLayout) findViewById(R.id.main_container);
        //view = findViewById(R.id.view);
        main_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if (main_container == null) {
                        return;
                    }

                    Rect r = new Rect();
                    main_container.getWindowVisibleDisplayFrame(r);

                    int heightDiff = main_container.getRootView().getHeight() - (r.bottom - r.top);
//                    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//                    if (resourceId > 0) {
//                        heightDiff -= getResources().getDimensionPixelSize(resourceId);
//                    }
                    if (heightDiff > dpToPx(100)) {
                        if (!keyboardVisible) {
                            keyboardVisible = true;
                            //  setbackground(keyboardVisible);
//                            showSoftKeyboard(et_name);
//                            showSmiley(false);

                        }
                    } else {
                        if (keyboardVisible) {
                            keyboardVisible = false;
                            // setbackground(keyboardVisible);
//                            hideSoftKeyboard(mActivity, et_name);
//                            showSmiley(true);
//                            setEmojiconFragment(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return keyboardVisible;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle mBundle;

        switch (requestCode) {
            case Constants.ImagePickerChat:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (data != null) {
                    mBundle = data.getExtras();
                    if (mBundle != null) {
                        String url = mBundle.getString(Constants.B_RESULT);
                        mImageLoader.displayImage(Constants.file + url, iv_Group);
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;


        }
    }

    @Override
    protected void setImage(String encoded, Bitmap mBitmap, Bitmap mBlur, Uri outputFileUri) {
        super.setImage(encoded, mBitmap, mBlur, outputFileUri);
        showSmiley(false);
        if (emojicons.getVisibility() == View.VISIBLE) {
            emojicons.setVisibility(View.GONE);

        }

        imagePath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), outputFileUri);
        AppVhortex.getInstance().setGroupCreateImageBitmap(mBitmap);

        iv_Group.setImageBitmap(mBitmap);
    }

    private AsyncCreateEditGroup mAsyncCreateEditGroup;

    private void callWSCreateOrEditGroup() {
//        String groupMemberIds = CommonMethods.commaSeperatedsStringsFromArray(mGroupIDList);
        if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
            String groupname = et_name.getText().toString().trim();
            mAsyncCreateEditGroup = new AsyncCreateEditGroup(mDataProfile.getUserId(),
                    groupname, mDataGroup.getGroupJId() + "@conference." + ServiceXmppConnection.SERVICE_NAME, "",
                    "", mDataGroup.getGroupId(), imagePath, this);

            if (mAsyncCreateEditGroup != null && mAsyncCreateEditGroup.getStatus() != AsyncTask.Status.RUNNING) {
                if (CommonMethods.checkBuildVersionAsyncTask()) {
                    mProgressBarCircularIndeterminate.setVisibility(View.VISIBLE);
                    mAsyncCreateEditGroup.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    mAsyncCreateEditGroup.execute();
                }

            }
        }
    }

    @Override
    public void onResponseObject(Object mObject) {
        DataCreateOrEditGroup dataCreateOrEditGroup = ((DataCreateOrEditGroup) mObject);
        mProgressBarCircularIndeterminate.setVisibility(View.GONE);
        AppVhortex.getInstance().setGroupCreateImageBitmap(null);
        ArrayList<DataCreateOrEditGroup> mDataCreateOrEditGroupsArr = new ArrayList<DataCreateOrEditGroup>();
        mDataCreateOrEditGroupsArr.add(dataCreateOrEditGroup);
        TableGroup mTableGroup = new TableGroup(getBaseContext());
        mTableGroup.bulkInsertCreatedGroup(mDataCreateOrEditGroupsArr);
        ToastUtils.showAlertToast(this, "Group edited successfully.", ToastType.SUCESS_ALERT);
//        createGroupInXmpp(dataCreateOrEditGroup);

       /* Intent mIntent = new Intent(mActivity, ActivityDash.class);
//        mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mIntent);
        overridePendingTransition(0, 0);*/

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {
        mProgressBarCircularIndeterminate.setVisibility(View.GONE);
        ToastUtils.showAlertToast(this, responseText, ToastType.FAILURE_ALERT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
