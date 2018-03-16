package com.wachat.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncFriendProfile;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataContact;
import com.wachat.data.DataFriendProfile;
import com.wachat.dialog.DialogGenderChoice;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableContact;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public class ActivityFriendProfile extends BaseActivity implements View.OnClickListener, InterfaceResponseCallback {

    public Toolbar appBar;
    private TextView tv_gender, tv_language, tv_cancel, tv_save, et_name,/*tv_country,*/
            tv_phone_number;
    private DialogGenderChoice mDialogGenderChoice;
    private ImageView iv_blur_bg;
    private AsyncFriendProfile mAsyncFriendProfile;
    private String friendId, chatId;
    private DataContact mDataContact;
    private String language = "";
    private RelativeLayout rel_phone_number;
    private TableContact tableContact;
    private ProgressBarCircularIndeterminate progressBar_profile_image_load;
    private View profile_iv_placeholder;
    private static final int REQUEST_ADD_TO_CONTACT = 1234;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        getValuesFromIntent();
        tableContact = new TableContact(this);
        getValueFromDB();


        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initViews();
        setValues();
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.VISIBLE);
        if (CommonMethods.isOnline(this)) {
            callWebserviceAndSetValues();
        } else {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
        }
    }

    private void getValuesFromIntent() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_ID) && mBundle.containsKey(Constants.B_RESULT)) {
            friendId = mBundle.getString(Constants.B_ID);
            chatId = mBundle.getString(Constants.B_RESULT);
            mDataContact = (DataContact) mBundle.getSerializable(Constants.B_OBJ);
        }
    }

    private void getValueFromDB() {
        if (tableContact.checkExistanceByUserId(friendId)) {
            mDataContact = tableContact.getFriendDetailsByID(friendId);
        }
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.friendProfile));

    }

    private void initViews() {
        et_name = (TextView) findViewById(R.id.et_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_language = (TextView) findViewById(R.id.tv_language);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_save = (TextView) findViewById(R.id.tv_save);

        iv_blur_bg = (ImageView) findViewById(R.id.iv_blur_bg);
//        tv_country = (TextView) findViewById(R.id.tv_country);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        rel_phone_number = (RelativeLayout)findViewById(R.id.rel_phone_number);
        progressBar_profile_image_load = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar_profile_image_load);
        profile_iv_placeholder = findViewById(R.id.profile_iv_placeholder);

        iv_blur_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mDataContact.getImageUrl())){
                    Intent mIntent = new Intent(ActivityFriendProfile.this, ActivityPinchToZoom.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean("url_image",true);
                    mBundle.putString(Constants.B_RESULT, mDataContact.getImageUrl());
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }
            }
        });
    }


    private void callWebserviceAndSetValues() {
        String chatId = "";
        if (mDataContact != null && !mDataContact.getChatId().equals("")) {
            chatId = mDataContact.getChatId();
        } else {
            chatId = this.chatId;
        }
        if (CommonMethods.isOnline(this)) {
            mAsyncFriendProfile = new AsyncFriendProfile(mDataProfile.getUserId(),friendId, chatId, this);
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncFriendProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncFriendProfile.execute();
            }
        } else {
            ToastUtils.showAlertToast(this, getString(R.string.no_internet), ToastType.FAILURE_ALERT);
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

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME,
                CommonMethods.getUTFDecodedString(mDataContact.getAppName()));
        //gupi for phone number as it is coming without country code
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, "+" + mDataContact.getPhoneNumber());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.putExtra("finishActivityOnSaveCompleted", true);
        }
        startActivityForResult(intent, REQUEST_ADD_TO_CONTACT);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bundle mBundle;

        switch (requestCode) {
            case REQUEST_ADD_TO_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    mDataContact.setIsFriend(1);
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(ConstantDB.FriendId, mDataContact.getFriendId());
                    mContentValues.put(ConstantDB.isFriend, mDataContact.getIsFriend());
                    mContentValues.put(ConstantDB.relation, mDataContact.getRelation());
                    tableContact.updateFriendProfile(mContentValues,mDataContact);
                    item_right.setVisible(false);
                    ToastUtils.showAlertToast(this, "Please wait while the contact is being synced", ToastType.FAILURE_ALERT);
                }
                break;
            case Constants.EditToLang:
                if (data != null) {
                    mBundle = data.getExtras();
                    if (mBundle != null) {
                        String selection = mBundle.getString(Constants.B_RESULT);
                        // Toast.makeText(mActivity, selection, Toast.LENGTH_SHORT).show();
                        tv_language.setText(selection);
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
//        iv_blur_bg.setImageBitmap(mBlur);
//        my_acc_profile_img.setImageBitmap(mBitmap);
    }

    @Override
    public void onResponseObject(Object mObject) {
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.GONE);

        if (mObject != null) {
            DataFriendProfile dataFriendProfile = (DataFriendProfile) mObject;

            if (!TextUtils.isEmpty(dataFriendProfile.getFriendName())) {
                mDataContact.setName(CommonMethods.getUTFDecodedString(dataFriendProfile.getFriendName()));
                mDataContact.setAppName(dataFriendProfile.getFriendName());
            }

            if (!TextUtils.isEmpty(dataFriendProfile.getGender())) {
                mDataContact.setGender(dataFriendProfile.getGender());
            }

            if (!TextUtils.isEmpty(dataFriendProfile.getFriendImage())) {
                mDataContact.setImageUrl(dataFriendProfile.getFriendImage());
            }
            if (!TextUtils.isEmpty(dataFriendProfile.getFriendPhno())) {
                mDataContact.setPhoneNumber(dataFriendProfile.getFriendPhno());
            }
            language = dataFriendProfile.getLanguage();
            mDataContact.setIsfindbyphoneno(dataFriendProfile.getIsfindbyphoneno());
            if(!TextUtils.isEmpty(dataFriendProfile.relation)) {
                mDataContact.setRelation(dataFriendProfile.relation);
            }
        }

        updateFriendsProfileToDb();

        if(mDataContact.getIsFriend()!=1){
            if(!TextUtils.isEmpty(mDataContact.getRelation())){
                item.setVisible(false);
                item_right.setVisible(true);
                item_globe.setVisible(false);
            }
        }
        setValues();


    }

    private void updateFriendsProfileToDb() {
        if (tableContact.checkExistanceByUserId(friendId)) {
            ContentValues mContentValues = new ContentValues();

            mContentValues.put(ConstantDB.FriendId, mDataContact.getFriendId());


//            mContentValues.put(ConstantDB.PhoneNumber, mDataContact.getCountryCode().replace("+", "") + mDataContact.getPhoneNumber());
            if(!TextUtils.isEmpty(mDataContact.getName())) {
                mContentValues.put(ConstantDB.AppName, mDataContact.getName());
            }

            if(!TextUtils.isEmpty(mDataContact.getAppName())) {
                mContentValues.put(ConstantDB.AppName, mDataContact.getAppName());
            }
            mContentValues.put(ConstantDB.AppFriendImageLink, mDataContact.getImageUrl());
            mContentValues.put(ConstantDB.gender, mDataContact.getGender());
            mContentValues.put(ConstantDB.isfindbyphoneno, mDataContact.getIsfindbyphoneno());
            mContentValues.put(ConstantDB.relation, mDataContact.getRelation());
            tableContact.updateFriendProfile(mContentValues,mDataContact);
//            mDataContact = tableContact.getFriendDetailsByID(friendId);
        }
    }

    private void setValues() {
        String name = mDataContact.getAppName();
        if(TextUtils.isEmpty(name)){
            name = mDataContact.getName();
        }
        if(TextUtils.isEmpty(name)){
            name = mDataContact.getPhoneNumber();
        }
        et_name.setText(CommonMethods.getUTFDecodedString(name));

        String gender = "";
        gender = mDataContact.getGender();
        if (TextUtils.isEmpty(gender) || gender.equals("0")||gender.equalsIgnoreCase("Select Gender")) {
            tv_gender.setText(R.string.not_specified);
        } else {
            tv_gender.setText(gender);
        }


        if (TextUtils.isEmpty(language)) {
            tv_language.setText(getResources().getString(R.string.english));
        } else {
            tv_language.setText(language);
        }
        if (TextUtils.isEmpty(mDataContact.getImageUrl())) {
            progressBar_profile_image_load.setVisibility(View.GONE);
            profile_iv_placeholder.setVisibility(View.VISIBLE);
//            imagePath = "drawable://" + R.drawable.ic_chats_noimage_profile;
        } else {
            imagePath = mDataContact.getImageUrl();


            mImageLoader.displayImage(imagePath, iv_blur_bg, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar_profile_image_load.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    profile_iv_placeholder.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    iv_blur_bg.setVisibility(View.VISIBLE);
                    profile_iv_placeholder.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    profile_iv_placeholder.setVisibility(View.VISIBLE);
                }
            });
        }

        if(mDataContact.getIsFriend()==1){
            rel_phone_number.setVisibility(View.VISIBLE);
            if (mDataContact.getPhoneNumber().equals("")) {
                tv_phone_number.setText("");
            } else
                tv_phone_number.setText("+" + mDataContact.getCountryCode() + mDataContact.getPhoneNumber());
        }else {
            if (mDataContact.getIsfindbyphoneno().equals("0")) {
                rel_phone_number.setVisibility(View.GONE);
            } else {
                rel_phone_number.setVisibility(View.VISIBLE);
                if (mDataContact.getPhoneNumber().equals("")) {
                    tv_phone_number.setText("");
                } else
                    tv_phone_number.setText("+" + mDataContact.getCountryCode() + mDataContact.getPhoneNumber());
            }

        }
//        Picasso.with(this).load(imagePath)
//                .transform(new BlurTransformation(this, CompressImageUtils.BLUR_RADIUS)).into(iv_blur_bg);
    }


    private void getFriendDataFromDb() {
        DataContact mDataContact = mTableContact.getFriendDetailsByID(friendId);
        String name = (mDataContact.getAppName().equals("")) ? ((mDataContact.getName().equals("")) ? mDataContact.getPhoneNumber() : mDataContact.getName()) : mDataContact.getAppName();
        et_name.setText(CommonMethods.getUTFDecodedString(name));
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.GONE);
    }

    @Override
    public void onResponseFaliure(String responseText) {
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(View.GONE);
        ToastUtils.showAlertToast(this, responseText, ToastType.FAILURE_ALERT);


    }
}
