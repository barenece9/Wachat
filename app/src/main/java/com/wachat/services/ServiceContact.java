package com.wachat.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataContact;
import com.wachat.data.DataContactSync;
import com.wachat.data.DataProfile;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.runnables.RunnableContactList;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableGroup;
import com.wachat.storage.TableSync;
import com.wachat.storage.TableTimeStamp;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Priti Chatterjee on 30-09-2015.
 */
public class ServiceContact extends Service implements InterfaceResponseCallback {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ServiceContact.class);
    private ResultReceiver mResultReceiver = null;
    private ExecutorService mExecutorService;
    private RunnableContactList mRunnableContactList = null;
    private Context mContext;

    private ArrayList<DataContact> mListContact;
    private TableContact mTableContact;
    private TableTimeStamp mTableTimeStamp;

    private TableUserInfo mTableUserInfo;

    private Prefs mPrefs;

    private enum TYPE {
        EMPTY, UPDATE
    }

    public ServiceContact() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private DataProfile mDataProfile = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mRunnableContactList!=null){
            //Already a runnable is created
            return START_NOT_STICKY;
        }
        LogUtils.i(LOG_TAG, "ServiceContact: onStartCommand");

        AppVhortex.getInstance().serviceContactRunning = true;
        if (mRunnableContactList == null) {
            mContext = getApplicationContext();
            mTableUserInfo = new TableUserInfo(mContext);
            mDataProfile = mTableUserInfo.getUser();
            mPrefs = Prefs.getInstance(getApplicationContext());
            mTableContact = new TableContact(mContext);
            mTableTimeStamp = new TableTimeStamp(mContext);
        }
        mListContact = mTableContact.getAllList();
        if (intent != null) {
            mResultReceiver = intent.getParcelableExtra(Constants.B_TYPE);
        } else {
            mResultReceiver = null;
        }
        if (mListContact.size() <= 0) {
            startRunnable(TYPE.EMPTY);
        } else {

            if (new TableSync(getBaseContext()).isSynced()) {
                startRunnable(TYPE.UPDATE);
            } else
                startRunnable(TYPE.EMPTY);

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.i(LOG_TAG, "ServiceContact: OnDestroy");
        AppVhortex.getInstance().serviceContactRunning = false;
        mRunnableContactList = null;
        super.onDestroy();
    }

    public void startRunnable(TYPE mType) {


        mDataProfile = mTableUserInfo.getUser();
        switch (mType) {
            case EMPTY:
                LogUtils.i(LOG_TAG, "startRunnable:EMPTY");
                AppVhortex.getInstance().isContactChangeSyncing = true;
                mExecutorService = Executors.newFixedThreadPool(1);
                mRunnableContactList = new RunnableContactList(
                        mContext,
                        mListContact,
                        (mDataProfile != null) ? mDataProfile.getPhoneNo() : "",
                        mTableContact.getNumbers(),
                        mPrefs.getUserId(),
                        mTableTimeStamp,
                        (mDataProfile != null) ? mDataProfile.getCountryCode() : "",
                        this);

                mExecutorService.execute(mRunnableContactList);
                break;
            case UPDATE:
                LogUtils.i(LOG_TAG, "startRunnable:UPDATE");
                mExecutorService = Executors.newFixedThreadPool(1);
                mRunnableContactList = new RunnableContactList(mContext,
                        mListContact,
                        (mDataProfile != null) ? mDataProfile.getPhoneNo() : "",
                        mTableContact.getNumbers(),
                        mPrefs.getUserId(),
                        mTableTimeStamp,
                        (mDataProfile != null) ? mDataProfile.getCountryCode() : "",
                        this);
                mExecutorService.execute(mRunnableContactList);
                break;
        }
    }

    @Override
    public void onResponseObject(Object mObject) {
        if (mObject instanceof DataContactSync) {
            boolean isNeedToUPdateContact = false;
            boolean isNeedToUPdateGroup = false;
            DataContactSync mDataContactSync = (DataContactSync) mObject;
            TableContact mTableContact = new TableContact(mContext);


            if (mDataContactSync.getmListContact() != null
                    && mDataContactSync.getmListContact().size() > 0) {
                AppVhortex.getInstance().isContactChangeSyncing = true;

                isNeedToUPdateContact = true;
                mTableContact.updateUser(mDataContactSync.getmListContact());
            }

            if (mDataContactSync.currentFriendIds != null) {
                int deleteCount = mTableContact.updateRegisteredStatusForFriendsWhoAreNotRegistered(mDataContactSync.currentFriendIds);
                if (deleteCount > 0) {
                    isNeedToUPdateContact = true;
                }
            }

            TableGroup mTableGroup = new TableGroup(mContext);
            if (mDataContactSync.currentGroupIds != null) {
                int deleteCount = mTableGroup.deleteGroupApartFromCurrentGroupIds(mDataContactSync.currentGroupIds);
                if (deleteCount > 0) {
                    isNeedToUPdateGroup = true;
                    isNeedToUPdateContact = true;
                }
            }
            if (mDataContactSync.getmListGroup() != null && mDataContactSync.getmListGroup().size() > 0) {
                isNeedToUPdateContact = true;
                isNeedToUPdateGroup = true;

                AppVhortex.getInstance().isContactChangeSyncing = true;
                mTableGroup.bulkInsertGroup(mDataContactSync.getmListGroup());
            }
            AppVhortex.getInstance().isContactChangeSyncing = false;
            if (isNeedToUPdateContact || isNeedToUPdateGroup) {

                LogUtils.i(LOG_TAG, "ServiceContact: isNeedToUpdateContact");
                try {
                    Intent mIntent = new Intent(getResources().getString(R.string.ContactBroadcast));
                    Bundle bundle = new Bundle();
//                    if (mDataContactSync.getmListGroup().size() > 0) {
                    bundle.putBoolean("group_changed", isNeedToUPdateGroup);
//                    }
                    sendBroadcast(mIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mResultReceiver != null) {
                Bundle mBundle = new Bundle();
                mRunnableContactList = null;
                mBundle.putBoolean(Constants.B_NEED_UPDATED, isNeedToUPdateContact);
                mBundle.putBoolean(Constants.B_RESULT, new Boolean(true));
                mResultReceiver.send(Constants.ContactSynced, mBundle);

            }

            stopSelf();
        }
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        mListContact = (ArrayList<DataContact>) mList;
//        boolean isSuccess = mTableContact.insertBulk(mListContact);
//        if(isSuccess){
//            new TableSync(getApplicationContext()).setStatus("1");
        startRunnable(TYPE.UPDATE);
//        }
    }

    @Override
    public void onResponseFaliure(String responseText) {
        LogUtils.i(LOG_TAG, "ServiceContact: onResponseFaliure:responseText:"+responseText);
        AppVhortex.getInstance().isContactChangeSyncing = false;
        if (mResultReceiver != null) {
            Bundle mBundle = new Bundle();
            mRunnableContactList = null;

            if(!TextUtils.isEmpty(responseText)) {
                boolean sessionExpired = responseText.equals(String.valueOf(WebContstants.ResponseCode_300));
//            mBundle.putBoolean(Constants.B_NEED_UPDATED, isNeedToUPdateContact);
                LogUtils.i(LOG_TAG, "ServiceContact: onResponseFaliure:sessionExpired:" + sessionExpired);
                mBundle.putBoolean("session_expired", sessionExpired);
            }

            mBundle.putBoolean(Constants.B_RESULT, new Boolean(true));
            mResultReceiver.send(Constants.ContactSynced, mBundle);
        }
        stopSelf();
    }


}
