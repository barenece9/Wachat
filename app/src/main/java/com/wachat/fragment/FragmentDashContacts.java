package com.wachat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.activity.ActivityChat;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivityFindPeople;
import com.wachat.adapter.AdapterDashContact;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseFragment;
import com.wachat.callBack.AdapterCallback;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataContact;
import com.wachat.storage.ConstantDB;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class FragmentDashContacts extends BaseFragment implements AdapterCallback, ActivityDash.RefreshEventDelegate {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(FragmentDashContacts.class);
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;

    public static FragmentDashContacts getInstance() {
        FragmentDashContacts mFragmentDashContacts = new FragmentDashContacts();
        return mFragmentDashContacts;
    }

    private ActivityDash mActivityDash;
    private RecyclerView rv;
    private TextView emptyView;
    private ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
    private AdapterDashContact mAdapterDashContact;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityDash = (ActivityDash) activity;
            mActivityDash.registerRefreshEventButtonClickListener(this);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDetach() {
        mActivityDash.unRegisterCreateGroupButtonClickListener();
        mActivityDash.unRegisterRefreshEventButtonClickListener();
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashrecycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setText(R.string.no_contacts_list);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate)view.findViewById(R.id.progressBarCircularIndetermininate);
        rv.setLayoutManager(new LinearLayoutManager(mActivityDash, LinearLayoutManager.VERTICAL, false));
        mAdapterDashContact = new AdapterDashContact(mActivityDash, mListContact, this);
        rv.setAdapter(mAdapterDashContact);
        setupAdapter();
        try {
            mActivityDash.tv_act_name.setText(mActivityDash.getResources().getString(R.string.contacts));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    private void setupAdapter() {
        if(getActivity()==null){
            return;
        }
//        if(AppVhortex.getInstance().isContactChangeSyncing){
//            mActivityDash.topbar_progressbar.setVisibility(View.VISIBLE);
//            progressBarCircularIndetermininate.setVisibility(View.GONE);
//        }else{
//            mActivityDash.topbar_progressbar.setVisibility(View.GONE);
//            progressBarCircularIndetermininate.setVisibility(View.GONE);
//        }
        mListContact = mActivityDash.mTableContact.getRegisteredUser();

        if (mListContact != null && mListContact.isEmpty()) {
            mListContact = new ArrayList<DataContact>();
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        mAdapterDashContact.refreshData(mListContact);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppVhortex.getInstance().isContactChangeSyncing) {
            mActivityDash.topbar_progressbar.setVisibility(View.VISIBLE);
            progressBarCircularIndetermininate.setVisibility(View.GONE);
        } else {
            mActivityDash.topbar_progressbar.setVisibility(View.GONE);
            progressBarCircularIndetermininate.setVisibility(View.GONE);
        }
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
    public void onSearchPerformed(String search) {
        LogUtils.i(LOG_TAG, search);
        Intent mIntent = new Intent(mActivityDash, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, search);
        mIntent.putExtras(mBundle);
        mActivityDash.startActivity(mIntent);
    }

    @Override
    public void OnClickPerformed(Object mObject) {

        if(getActivity()==null){
            return;
        }
        if (mObject instanceof DataContact) {
            DataContact mDataContact = (DataContact) mObject;
            Intent mIntent = new Intent(mActivityDash, ActivityChat.class);
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
            mIntent.putExtra(ConstantDB.PhoneNo, mDataContact.getPhoneNumber());
            mIntent.putExtra(ConstantDB.FriendImageLink, mDataContact.getAppFriendImageLink());
            startActivity(mIntent);
        }
    }


    @Override
    public void onPagination() {

    }

    @Override
    public void notifyFragment() {

        super.notifyFragment();
        setupAdapter();
    }

    @Override
    public void onRefreshClick() {
        if (!AppVhortex.getInstance().isContactChangeSyncing) {
            AppVhortex.getInstance().isContactChangeSyncing = true;
            mActivityDash.topbar_progressbar.setVisibility(View.VISIBLE);
            AppVhortex.getInstance().setSyncStatus("0");
            Prefs.getInstance(getActivity()).setGroupListTimeStamp("0");
        }
    }
}