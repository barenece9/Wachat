package com.wachat.activity;

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
import android.widget.ImageView;

import com.wachat.R;
import com.wachat.adapter.AdapterStatus;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncUpdateStatus;
import com.wachat.callBack.AdapterCallback;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataStatus;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public class ActivityUpdateStatus extends BaseActivity implements AdapterCallback, InterfaceResponseCallback, TextWatcher {

    RecyclerView rv;
    public Toolbar appBar;
    private ImageView iv_edit;
    private EditText et_status;
    private AdapterStatus mAdapterStatus;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private ArrayList<DataStatus> mStatusArrayList = new ArrayList<DataStatus>();
    AsyncUpdateStatus mAsyncUpdateStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);
        CommonMethods.hideSoftKeyboard(this);
        initComponent();
        initViews();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        getStatusFromDb();
        setCurrentStatusToView();
    }


    private void initViews() {
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(ActivityUpdateStatus.this, LinearLayoutManager.VERTICAL, false));
        mAdapterStatus = new AdapterStatus(this, mStatusArrayList, this);
        rv.setAdapter(mAdapterStatus);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(this);
        et_status = (EditText) findViewById(R.id.et_status);
        et_status.setEnabled(false);
        iv_tick.setVisibility(View.GONE);
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.updateStatus));
        lnr_right.setAlpha(0.5f);
        tv_right.setOnClickListener(this);
        tv_right.setClickable(false);
        mStatusArrayList = new ArrayList<>();
    }


    private void getStatusFromDb() {
        mStatusArrayList = mTableUserStatus.getStatus();
        mAdapterStatus.refreshList(mStatusArrayList);
    }

    private void setCurrentStatusToView() {
        String currentStatus = "";
        for (int i = 0; i < mStatusArrayList.size(); i++) {
            if (mStatusArrayList.get(i).getIsSelected().equals("1")) {
                currentStatus = mStatusArrayList.get(i).getUserStatus();
                if (!TextUtils.isEmpty(currentStatus)) {
                    et_status.setText(currentStatus);
                } else {
                    et_status.setText("");
                }
                mAdapterStatus.selectedPosition = i;
                mAdapterStatus.notifyDataSetChanged();
                break;
            }
        }

        if (TextUtils.isEmpty(currentStatus)) {
            setDefaultStatus();
        }

    }

    private void setDefaultStatus() {
        for (int i = 0; i < mStatusArrayList.size(); i++) {
            if (mStatusArrayList.get(i).getUserStatus().equalsIgnoreCase(getString(R.string.be_the_best_you))) {
                String currentStatus = mStatusArrayList.get(i).getUserStatus();
                et_status.setText(getString(R.string.be_the_best_you));
                mAdapterStatus.selectedPosition = i;
                mAdapterStatus.notifyDataSetChanged();
                break;
            }
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

    }

    @Override
    protected void CommonMenuClcik() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.iv_edit:
                et_status.setEnabled(true);
                et_status.addTextChangedListener(this);
                et_status.requestFocus();
                et_status.post(new Runnable() {
                    @Override
                    public void run() {
                        et_status.setSelection(et_status.getText().length());
                    }
                });
                showSoftKeyboard(et_status);
                //   et_status.setFocusable(true);
//                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                break;

            case R.id.tv_right:
                //save status

                //call webservice
                if (CommonMethods.isOnline(this)) {
//                    boolean isMatched = false;
//                    String statusInput = et_status.getText().toString();
//                    for (int i = 0; i < mStatusArrayList.size(); i++) {
//                        if (statusInput.equalsIgnoreCase(mStatusArrayList.get(i).getUserStatus())) {
//                            isMatched = true;
//                            break;
//                        } else {
//                            isMatched = false;
//                        }
//                    }
//                    if (!isMatched)
                    if (!TextUtils.isEmpty(et_status.getText().toString().trim())) {
                        hideSoftKeyboard(this, et_status);
                        et_status.setEnabled(false);
                        updateStatus();
                    }else{
                        ToastUtils.showAlertToast(this, getString(R.string.blank_status_alert), ToastType.FAILURE_ALERT);


                    }
                } else
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                break;
        }
    }

    private void updateStatus() {
        mAsyncUpdateStatus = new AsyncUpdateStatus(mDataProfile.getUserId(), et_status.getText().toString().trim(), this);
        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        iv_edit.setVisibility(View.GONE);
        if (mAsyncUpdateStatus != null && mAsyncUpdateStatus.getStatus() != AsyncTask.Status.RUNNING)
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncUpdateStatus.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncUpdateStatus.execute();
            }
    }

    @Override
    public void OnClickPerformed(Object mObject) {

        tv_right.setAlpha(1f);
        tv_right.setClickable(true);

        et_status.setText(((DataStatus) mObject).getUserStatus().toString());
        et_status.post(new Runnable() {
            @Override
            public void run() {
                et_status.setSelection(et_status.getText().length());
            }
        });

    }

    @Override
    public void onPagination() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard(this, et_status);
    }

    @Override
    public void onResponseObject(Object mObject) {

        // api success
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        iv_edit.setVisibility(View.VISIBLE);
        ToastUtils.showAlertToast(this, "Status updated successfully", ToastType.SUCESS_ALERT);
        boolean isMatched = false;
        DataStatus mDataStatus = new DataStatus();
        String statusInput = et_status.getText().toString();
        for (int i = 0; i < mStatusArrayList.size(); i++) {
            if (statusInput.equalsIgnoreCase(mStatusArrayList.get(i).getUserStatus())) {
                isMatched = true;
                mDataStatus = mStatusArrayList.get(i);
                mAdapterStatus.selectedPosition = i;
                mAdapterStatus.notifyDataSetChanged();
                break;
            } else {
                isMatched = false;
            }
        }
        if (!isMatched)

        {
            mTableUserStatus.updateAllStatusDeSelected();


            mDataStatus.setUserStatus(et_status.getText().toString());
            mDataStatus.setIsSelected("1");

            mTableUserStatus.insertNewStatus(mDataStatus);

            //update ui
            mStatusArrayList.add(0, mDataStatus);
            mAdapterStatus.refreshList(mStatusArrayList);
            //update user info table
            mTableUserInfo.updateStatus(mDataProfile.getUserId(), mDataStatus.getUserStatus());


        } else {

            mTableUserStatus.updateAllStatusDeSelected();
            mTableUserStatus.updateStatusSelected(mDataStatus.getStatusId());
            //update user info table
            mTableUserInfo.updateStatus(mDataProfile.getUserId(), mDataStatus.getUserStatus());
        }

        finish();
    }
//    else {
//            ToastUtils.showAlertToast(mActivity, "Status update failed!", ToastType.FAILURE_ALERT);
//        }
    // finish();
    //  }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (et_status.getText().toString().length() > 0) {
            tv_right.setAlpha(1f);
            tv_right.setClickable(true);
        } else {
            tv_right.setAlpha(0.5f);
            tv_right.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {


    }
}
