package com.wachat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.activity.ActivityChat;
import com.wachat.activity.ActivityCreateNewGroup;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivityFindPeople;
import com.wachat.adapter.AdapterDashGroup;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseFragment;
import com.wachat.callBack.AdapterCallback;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataGroup;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class FragmentDashGroup extends BaseFragment implements View.OnClickListener, AdapterCallback, ActivityDash.CreateGroupEventDelegate {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(FragmentDashGroup.class);

    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private ActivityDash mActivityDash;
    private AdapterDashGroup mAdapterDashGroup;
    private RecyclerView rv;
    private ActivityDash mDash;
    private TextView tv_create_group;
    private ArrayList<DataGroup> mListGroup = new ArrayList<DataGroup>();
    private TextView emptyView;

    public static FragmentDashGroup getInstance() {
        FragmentDashGroup mFragmentDashGroup = new FragmentDashGroup();
        return mFragmentDashGroup;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityDash = (ActivityDash) activity;
            mActivityDash.registerCreateGroupButtonClickListener(this);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetach() {
        mActivityDash.unRegisterCreateGroupButtonClickListener();
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            mActivityDash.tv_act_name.setText(mActivityDash.getResources().getString(R.string.groups));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grouprecycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setText(R.string.no_group_list);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate)view.
                findViewById(R.id.progressBarCircularIndetermininate);
        tv_create_group = (TextView) view.findViewById(R.id.tv_create_group);
        // tv_create_group.setPaintFlags(tv_create_group.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_create_group.setOnClickListener(this);
        rv.setLayoutManager(new LinearLayoutManager(mActivityDash, LinearLayoutManager.VERTICAL, false));

        mAdapterDashGroup = new AdapterDashGroup(mActivityDash, mActivityDash.mImageLoader, mListGroup, this);
        rv.setAdapter(mAdapterDashGroup);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAdapter();
    }

    private void setupAdapter() {
        if(getActivity()==null){
            return;
        }
        if (AppVhortex.getInstance().isContactChangeSyncing) {
            mActivityDash.topbar_progressbar.setVisibility(View.VISIBLE);
            progressBarCircularIndetermininate.setVisibility(View.GONE);
        } else {
            mActivityDash.topbar_progressbar.setVisibility(View.GONE);
            progressBarCircularIndetermininate.setVisibility(View.GONE);
        }

        mListGroup = mActivityDash.mTableGroup.getGroupList();


        if (mListGroup != null && mListGroup.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        mAdapterDashGroup.refreshList(mListGroup);

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
            case R.id.tv_create_group:

                Intent mIntent = new Intent(mActivityDash, ActivityCreateNewGroup.class);
                mActivityDash.startActivity(mIntent);
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
    public void OnClickPerformed(Object mObject) {
//        if (getActivity() != null)
//            CommonMethods.MYToast(getActivity(), "Under development");
//        return;
        if (mObject instanceof DataGroup) {
            DataGroup mDataGroup = (DataGroup) mObject;
            Intent mIntent = new Intent(mActivityDash, ActivityChat.class);
            mIntent.putExtra(Constants.B_ID, mDataGroup.getGroupId());
            mIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_GROUPCHAT);
            mActivityDash.startActivity(mIntent);
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
    public void onCreateGroupClick() {

        Intent mIntent = new Intent(mActivityDash, ActivityCreateNewGroup.class);
        mActivityDash.startActivity(mIntent);
    }
}
