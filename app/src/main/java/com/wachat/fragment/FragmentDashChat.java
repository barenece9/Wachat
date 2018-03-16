package com.wachat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.wachat.R;
import com.wachat.activity.ActivityChat;
import com.wachat.activity.ActivityDash;
import com.wachat.activity.ActivityFindPeople;
import com.wachat.adapter.AdapterDashChat;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseFragment;
import com.wachat.callBack.AdapterCallback;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ChatTextView;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataTextChat;
import com.wachat.storage.ConstantDB;
import com.wachat.storage.TableChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.util.ArrayList;


/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class FragmentDashChat extends BaseFragment implements AdapterCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(FragmentDashChat.class);

    private ChatTextView tv_empty_text;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private TableChat tableChat;

    public static FragmentDashChat getInstance() {
        FragmentDashChat mFragmentDashChat = new FragmentDashChat();
        return mFragmentDashChat;
    }

    private ActivityDash mActivityDash;
    private SwipeListView swipeList;
    private AdapterDashChat mAdapterDashChat;
    private ArrayList<DataTextChat> mArrayListDataTextChats = new ArrayList<DataTextChat>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityDash = (ActivityDash) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private ArrayList<DataTextChat> getConvertationListFromDB() {
        return tableChat.getConversationList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashchat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeList = (SwipeListView) view.findViewById(R.id.swipeList);
        tv_empty_text = (ChatTextView) view.findViewById(R.id.tv_empty_text);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) view.
                findViewById(R.id.progressBarCircularIndetermininate);
        Handler mhHandler = new Handler();
        mhHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float width = CommonMethods.getScreenWidth(mActivityDash).widthPixels
                        - CommonMethods.convertDpToPixel(mActivityDash.getResources().
                        getDimension(R.dimen.width_swipe_section), mActivityDash);
                swipeList.setOffsetLeft(mActivityDash.getResources().
                        getDimension(R.dimen.width_swipe_section));

                swipeList.setOffsetRight(width);
            }
        }, 1000);

        tableChat = new TableChat(view.getContext());
        //mArrayListDataTextChats = getConvertationListFromDB();
        try {
            mActivityDash.tv_act_name.setText(view.getContext().getResources().getString(R.string.chats));

            mAdapterDashChat = new AdapterDashChat(swipeList,view.getContext(), mArrayListDataTextChats, this);

            swipeList.setAdapter(mAdapterDashChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        swipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(parent.getContext(), "Hello", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
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

        mArrayListDataTextChats = getConvertationListFromDB();
        if (mArrayListDataTextChats != null && mArrayListDataTextChats.size() > 0) {
            swipeList.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.GONE);
            mAdapterDashChat.refreshConversationList(mArrayListDataTextChats);
        } else {
            swipeList.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.VISIBLE);
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
//        if (AppVhortex.getInstance().isContactChangeSyncing) {
//            ToastUtils.showAlertToast(getActivity(),
//                    "Please wait while contacts are being synced", ToastType.SUCESS_ALERT);
//            return;
//        }

        if (mObject instanceof DataTextChat) {
            DataTextChat mDataTextChat = (DataTextChat) mObject;
            Intent mIntent = new Intent(mActivityDash, ActivityChat.class);
            if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
                mIntent.putExtra(Constants.B_NAME, mDataTextChat.getFriendName());
                mIntent.putExtra(Constants.B_ID, mDataTextChat.getFriendId());
                mIntent.putExtra(Constants.B_RESULT, mDataTextChat.getChatId());

                mIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_SINGLECHAT);
                mIntent.putExtra(ConstantDB.ChatId, mDataTextChat.getFriendChatID());
                mIntent.putExtra(ConstantDB.PhoneNo, mDataTextChat.getFriendPhoneNo());
                mIntent.putExtra(ConstantDB.FriendImageLink, mDataTextChat.getAppFriendImageLink());
            } else if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
                mIntent.putExtra(Constants.B_ID, mDataTextChat.getStrGroupID());

                mIntent.putExtra(Constants.B_TYPE, ConstantChat.TYPE_GROUPCHAT);
            }
            startActivity(mIntent);
        }
    }

    @Override
    public void onPagination() {

    }

    @Override
    public void notifyFragment() {

        super.notifyFragment();
        if(getActivity()==null){
            return;
        }
//        if (AppVhortex.getInstance().isContactChangeSyncing) {
//            mActivityDash.topbar_progressbar.setVisibility(View.VISIBLE);
//            progressBarCircularIndetermininate.setVisibility(View.GONE);
//        } else {
//            mActivityDash.topbar_progressbar.setVisibility(View.GONE);
//            progressBarCircularIndetermininate.setVisibility(View.GONE);
//        }

        mArrayListDataTextChats = getConvertationListFromDB();
        if (mArrayListDataTextChats != null && mArrayListDataTextChats.size() > 0) {
            swipeList.setVisibility(View.VISIBLE);
            tv_empty_text.setVisibility(View.GONE);
            mAdapterDashChat.refreshConversationList(mArrayListDataTextChats);
        } else {
            swipeList.setVisibility(View.GONE);
            tv_empty_text.setVisibility(View.VISIBLE);
        }
    }
}
