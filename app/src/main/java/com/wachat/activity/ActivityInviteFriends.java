package com.wachat.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterInviteFriends;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncInviteFriend;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataContact;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 03-09-2015.
 */
public class ActivityInviteFriends extends BaseActivity implements InterfaceResponseCallback {

    public Toolbar appBar;
    EditText et_search_hint;
    private RecyclerView rv;
    private String searchText;
    private RelativeLayout rel_search;
    private ImageView iv_cross;
    private AdapterInviteFriends mAdapterInviteFriends;
    private ArrayList<DataContact> mListContact;
    public int slected_member_count = 0;
    //    private TextView tv_header, tv_pic_no;
//    private TextView tv_ok;
    private AsyncInviteFriend mAsyncInviteFriend;
    private final AdapterCallback mCallBack = new AdapterCallback() {
        @Override
        public void OnClickPerformed(Object mObject) {
            slected_member_count = mAdapterInviteFriends.getSelectedCount();
            tv_subTitle.setText(String.valueOf(slected_member_count) + " selected");
        }

        @Override
        public void onPagination() {

        }
    };
    private ProgressDialog mPageFilterDialog;
//    private ProgressBarCircularIndeterminate mProgressBarCircularIndeterminate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        iniViews();
        getDataFromDb("");
        initClickListener();

        slected_member_count = mAdapterInviteFriends.getSelectedCount();
        tv_subTitle.setText(String.valueOf(slected_member_count) + " selected");
    }

    private void initClickListener() {
        et_search_hint.addTextChangedListener(new TextWatcher() {

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
                getDataFromDb(String.valueOf(s));
            }
        });

        et_search_hint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND) {

                    CommonMethods.hideSoftKeyboard(ActivityInviteFriends.this);

                    return true;
                }
                return false;
            }
        });

    }

    private void getDataFromDb(String searchTxt) {
//        mListContact = new ArrayList<DataContact>();
        mListContact = mTableContact.getList(searchTxt);
        mAdapterInviteFriends.refreshList(mListContact);
    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initSuperViews();
//        tv_act_name.setText(getResources().getString(R.string.inviteFriends));
        tv_title.setText(getResources().getString(R.string.inviteFriends));
    }


    private void iniViews() {
//        tv_header = (TextView) findViewById(R.id.tv_header);
//        tv_header.setOnClickListener(this);
//        tv_pic_no = (TextView) findViewById(R.id.tv_pic_no);
//        tv_pic_no.setOnClickListener(this);
//
//        tv_ok = (TextView) findViewById(R.id.tv_ok);
//        tv_ok.setOnClickListener(this);

        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        et_search_hint = (EditText) findViewById(R.id.et_search_hint);
        et_search_hint.setHint(getResources().getString(R.string.et_search_hint));
        iv_cross = (ImageView) findViewById(R.id.iv_cross);
        iv_cross.setOnClickListener(this);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(ActivityInviteFriends.this, LinearLayoutManager.VERTICAL, false));
        mAdapterInviteFriends = new AdapterInviteFriends(this, mImageLoader, mCallBack);
        rv.setAdapter(mAdapterInviteFriends);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_cross:
                et_search_hint.setText("");

                break;
            case R.id.tv_right:
                if (slected_member_count >= 1)
                    if (CommonMethods.isOnline(this)) {

                        inviteFriends();
                    } else
                        ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                else
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.select_at_least_one_friend), ToastType.FAILURE_ALERT);
                break;
        }
    }

    private void inviteFriends() {
//        DialogInviteFriend mDialogInviteFriend = new DialogInviteFriend(this, AdapterInviteFriends.this, pos,name);
////                mDialogInviteFriend.show();
        showPageFilterProgress(true);
        mAsyncInviteFriend = new AsyncInviteFriend("+"+mDataProfile.getCountryCode() /*+"-"*/+ mDataProfile.getPhoneNo(),
                mAdapterInviteFriends.getSelectedFriendNumbers(), this);
        if (mAsyncInviteFriend != null && mAsyncInviteFriend.getStatus() != AsyncTask.Status.RUNNING) {
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncInviteFriend.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncInviteFriend.execute();
            }
        }
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
    public void onResponseObject(Object mObject) {
        //on success of invite friend
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, getString(R.string.invite_send_succ), ToastType.SUCESS_ALERT);
        finish();
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        showPageFilterProgress(false);
    }

    @Override
    public void onResponseFaliure(String responseText) {
        showPageFilterProgress(false);
        ToastUtils.showAlertToast(this, getString(R.string.invite_unsucc), ToastType.FAILURE_ALERT);
    }
}
