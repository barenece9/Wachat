package com.wachat.async;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataContactSync;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.storage.TableGroup;
import com.wachat.util.CommonMethods;
import com.wachat.util.GroupDoesntExistException;
import com.wachat.util.LogUtils;
import com.wachat.util.NoResponseFromServerException;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

public class AsyncViewGroupDetails extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncViewGroupDetails.class);

    private String groupId = "";
    private WebServiceCallBack mCallback;
    private DataContactSync mDataGroupSync;
    private Throwable mException;
    private boolean groupDoesntExistsForThisUser = false;
    private boolean isFromGroupDetailsPage = false;

    public AsyncViewGroupDetails(String groupId, boolean isFromGroupDetailsPage,
                                 WebServiceCallBack mCallback) {
        this.groupId = groupId;
        this.isFromGroupDetailsPage = isFromGroupDetailsPage;
        this.mCallback = mCallback;

    }


    @Override
    protected Void doInBackground(Void... params) {
        String groupListTimeStamp = isFromGroupDetailsPage ? "0" : Prefs.getInstance(AppVhortex.getInstance().getApplicationContext()).
                getGroupListTimeStamp();
        ArrayList<NameValuePair> mListPairForContact = CommonMethods.getHeaderValue();
        mListPairForContact.add(new BasicNameValuePair(WebContstants.serverTimeStamp,
                groupListTimeStamp));
        mListPairForContact.add(new BasicNameValuePair("groupId", groupId));

        LogUtils.i(LOG_TAG, "Api:GroupList/Details: groupid: " + groupId + " Timestamp: "
                + groupListTimeStamp);
        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.GroupList);
            String result = mHttpConnectionClass.getResponse(mListPairForContact);
            LogUtils.i(LOG_TAG, result);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }

            updateGroupDetails();
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, e.getLocalizedMessage());
            mException = e;
        }
        return null;
    }

    private void updateGroupDetails() {
        if (mDataGroupSync != null && mDataGroupSync.getResponseCode() == VhortextStatusCode.Ok) {
            TableGroup mTableGroup = new TableGroup(AppVhortex.getInstance().getApplicationContext());
            if (!TextUtils.isEmpty(mDataGroupSync.currentGroupIds)) {
                int deleteCount = mTableGroup.deleteGroupApartFromCurrentGroupIds(mDataGroupSync.currentGroupIds);
            }
            if (mDataGroupSync.getmListGroup() != null && mDataGroupSync.getmListGroup().size() > 0) {

                mTableGroup.bulkInsertGroup(mDataGroupSync.getmListGroup());
            } else {
                //Group must have been deleted
                groupDoesntExistsForThisUser = true;
            }
        }
    }

    private void startParsing(String result) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataGroupSync = mapper.readValue(result, DataContactSync.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mException != null) {
            if (mCallback != null) {
                mCallback.onFailure(mException);
            }
            return;
        }

        if (mCallback == null) {
            return;
        }

        if (mDataGroupSync != null) {
            if (mDataGroupSync.getResponseCode() == VhortextStatusCode.Ok) {
                if (mDataGroupSync.getmListGroup() != null && mDataGroupSync.getmListGroup().size() > 0) {
                    mCallback.onSuccess(mDataGroupSync);
                } else {
                    if (groupDoesntExistsForThisUser) {
                        mCallback.onFailure(new GroupDoesntExistException());
                    }
                }
            } else {
                mCallback.onFailure(mDataGroupSync);
            }
        } else {
            mCallback.onFailure(new NoResponseFromServerException());
        }
    }


}
