package com.wachat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivityEditProfile;
import com.wachat.activity.ActivityFindPeople;
import com.wachat.activity.ActivityInviteFriends;
import com.wachat.activity.ActivitySettings;
import com.wachat.activity.ActivitySplash;
import com.wachat.activity.ActivityUpdateStatus;
import com.wachat.activity.ActivityViewBlockUser;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseFragment;
import com.wachat.async.AsyncDeleteAccount;
import com.wachat.callBack.CallbackFromDialog;
import com.wachat.chatUtils.NotificationUtils;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataCountry;
import com.wachat.data.DataProfile;
import com.wachat.dialog.DialogDeleteAccount;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.services.ServiceContact;
import com.wachat.services.ServiceXmppConnection;
import com.wachat.storage.TableCountry;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class FragmentDashMore extends BaseFragment implements View.OnClickListener, CallbackFromDialog, InterfaceResponseCallback {

    protected DataProfile mDataProfile;
    protected TableUserInfo mTableUserInfo;
    private AsyncDeleteAccount mAsyncDeleteAccount;
    private ActivityDash mActivityDash;
    private ImageView iv_Invite, iv_Edit, iv_Block, iv_Status, iv_Delete, iv_Settings;
    private TextView tv_Invite, tv_Edit, tv_Block, tv_Status, tv_Delete, tv_Settings;
    private RelativeLayout rlv_Invite, rlv_Edit, rlv_Block, rlv_Status, rlv_Delete, rlv_Setting;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;

    public static FragmentDashMore getInstance() {
        FragmentDashMore mFragmentDashMore = new FragmentDashMore();
        return mFragmentDashMore;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityDash = (ActivityDash) activity;
            mTableUserInfo = new TableUserInfo(mActivityDash);
            mDataProfile = mTableUserInfo.getUser();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rlv_Invite = (RelativeLayout) view.findViewById(R.id.rlv_Invite);
        rlv_Edit = (RelativeLayout) view.findViewById(R.id.rlv_Edit);
        rlv_Block = (RelativeLayout) view.findViewById(R.id.rlv_Block);
        rlv_Status = (RelativeLayout) view.findViewById(R.id.rlv_Status);
        rlv_Delete = (RelativeLayout) view.findViewById(R.id.rlv_Delete);
        rlv_Setting = (RelativeLayout) view.findViewById(R.id.rlv_Setting);

        clickListeners();

        iv_Invite = (ImageView) rlv_Invite.findViewById(R.id.iv);
        iv_Edit = (ImageView) rlv_Edit.findViewById(R.id.iv);
        iv_Block = (ImageView) rlv_Block.findViewById(R.id.iv);
        iv_Status = (ImageView) rlv_Status.findViewById(R.id.iv);
        iv_Delete = (ImageView) rlv_Delete.findViewById(R.id.iv);
        iv_Settings = (ImageView) rlv_Setting.findViewById(R.id.iv);

        tv_Invite = (TextView) rlv_Invite.findViewById(R.id.tv);
        tv_Edit = (TextView) rlv_Edit.findViewById(R.id.tv);
        tv_Block = (TextView) rlv_Block.findViewById(R.id.tv);
        tv_Status = (TextView) rlv_Status.findViewById(R.id.tv);
        tv_Delete = (TextView) rlv_Delete.findViewById(R.id.tv);
        tv_Settings = (TextView) rlv_Setting.findViewById(R.id.tv);

        populateView();
        try {
            mActivityDash.tv_act_name.setText(mActivityDash.getResources().getString(R.string.more));
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) view.findViewById(R.id.progressBarCircularIndetermininate);
    }

    private void clickListeners() {
        rlv_Invite.setOnClickListener(this);
        rlv_Edit.setOnClickListener(this);
        rlv_Block.setOnClickListener(this);
        rlv_Status.setOnClickListener(this);
        rlv_Delete.setOnClickListener(this);
        rlv_Setting.setOnClickListener(this);

    }

    private void populateView() {

        iv_Invite.setImageResource(R.drawable.ic_more_invite_friends_icon);
        iv_Edit.setImageResource(R.drawable.ic_more_edit_profile_icon);
        iv_Block.setImageResource(R.drawable.ic_more_view_block_user_icon);
        iv_Status.setImageResource(R.drawable.ic_more_update_status_icon);
        iv_Delete.setImageResource(R.drawable.ic_more_delete_account_icon);
        iv_Settings.setImageResource(R.drawable.ic_more_settings_icon);

        tv_Invite.setText(mActivityDash.getResources().getString(R.string.invite));
        tv_Edit.setText(mActivityDash.getResources().getString(R.string.edit));
        tv_Block.setText(mActivityDash.getResources().getString(R.string.view_block_user));
        tv_Block.setSelected(true);
        tv_Status.setText(mActivityDash.getResources().getString(R.string.update_status));
        tv_Delete.setText(mActivityDash.getResources().getString(R.string.delete_acc));
        tv_Delete.setSelected(true);
        tv_Settings.setText(mActivityDash.getResources().getString(R.string.settings));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rlv_Invite:

                Intent mint = new Intent(mActivityDash, ActivityInviteFriends.class);
                startActivity(mint);


                break;
            case R.id.rlv_Edit:
                Intent mIntent = new Intent(mActivityDash, ActivityEditProfile.class);
                startActivity(mIntent);
                break;


            case R.id.rlv_Block:
                Intent intent2 = new Intent(mActivityDash, ActivityViewBlockUser.class);
                startActivity(intent2);
                break;


            case R.id.rlv_Status:
                Intent intent1 = new Intent(mActivityDash, ActivityUpdateStatus.class);
                startActivity(intent1);
                break;


            case R.id.rlv_Delete:

                DialogDeleteAccount mDialogDelete = new DialogDeleteAccount(mActivityDash, this);
                mDialogDelete.show();
                break;


            case R.id.rlv_Setting:
                Intent intent = new Intent(mActivityDash, ActivitySettings.class);
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onSearchPerformed(String search) {
        Intent mIntent = new Intent(mActivityDash, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, search);
        mIntent.putExtras(mBundle);
        mActivityDash.startActivity(mIntent);
    }


    @Override
    public void OnClickEmail() {

    }

    @Override
    public void OnClickDelete() {

        callWebservice();
    }


    @Override
    public void OnClickBlock() {

    }

    @Override
    public void OnClickClearConversation() {

    }


    //delete acc webservice
    private void callWebservice() {
        if (CommonMethods.isOnline(mActivityDash)) {
            progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
            mAsyncDeleteAccount = new AsyncDeleteAccount(mDataProfile.getUserId(), this);
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncDeleteAccount.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncDeleteAccount.execute();
            }
        } else {
            ToastUtils.showAlertToast(mActivityDash, getString(R.string.no_internet), ToastType.FAILURE_ALERT);
        }
    }

    @Override
    public void onResponseObject(Object mObject) {
        if(getActivity()==null){
            return;
        }
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        // on succes of webservice delete all table data & pref
        CommonMethods.MYToast(mActivityDash, "Account deleted");

        DataCountry mDataCountry = new TableCountry(mActivityDash).
                getCountryCode(AppVhortex.getInstance().countryCode);
        mActivityDash.stopService(new Intent(mActivityDash,ServiceXmppConnection.class));
        mActivityDash.stopService(new Intent(mActivityDash,ServiceContact.class));
        NotificationUtils.clearAllNotification(mActivityDash);
        Intent intent = new Intent(mActivityDash, ActivitySplash.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, (mDataCountry));
        intent.putExtras(mBundle);
        mActivityDash.overridePendingTransition(0, 0);
        mActivityDash.startActivity(intent);
        mActivityDash.finish();
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        if(getActivity()==null){
            return;
        }
        progressBarCircularIndetermininate.setVisibility(View.GONE);
    }

    @Override
    public void onResponseFaliure(String responseText) {
        if(getActivity()==null){
            return;
        }
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        CommonMethods.MYToast(mActivityDash, responseText);
    }
}
