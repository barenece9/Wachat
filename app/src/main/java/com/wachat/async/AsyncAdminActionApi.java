package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.data.AdminActionResponse.DataGroupAdminActionResponse;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.util.LogUtils;
import com.wachat.util.NoResponseFromServerException;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class AsyncAdminActionApi extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncAdminActionApi.class);

    private String userId = "";
    private String groupId = "";
    private String memberId = "";
    private String action = "";
    private String flag = "";

    private WebServiceCallBack mCallback;
    private DataGroupAdminActionResponse mBaseResponse;
    private Throwable mException;


    public AsyncAdminActionApi(String userId, String groupId, String memberId, String action, String flag, WebServiceCallBack mCallback) {
        this.userId = userId;
        this.groupId = groupId;
        this.memberId = memberId;
        this.action = action;
        this.flag = flag;

        this.mCallback = mCallback;


    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair("userid", userId));

        mListPairs.add(new BasicNameValuePair("groupid", groupId));
        mListPairs.add(new BasicNameValuePair("memberid", memberId));
        mListPairs.add(new BasicNameValuePair("action", action));
        mListPairs.add(new BasicNameValuePair("flag", flag));
        LogUtils.d(LOG_TAG, "Params:userid=" + userId
                + "&groupid=" + groupId + "&action=" + action + "&flag=" + flag);

        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.GROUP_ADMIN_ACTION);
        try {
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);

            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, "AsyncAdminActionApi" + e.getLocalizedMessage());
            mException = e;

        }


        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mBaseResponse = mapper.readValue(result, DataGroupAdminActionResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mCallback == null) {
            return;
        }
        if (mException != null) {
            if (mCallback != null) {
                mCallback.onFailure(mException);
            }
            return;
        }


        if (mBaseResponse != null) {
            if (mBaseResponse.getResponseCode().equalsIgnoreCase(String.valueOf(VhortextStatusCode.Ok))) {
                mCallback.onSuccess(mBaseResponse);
            } else {
                mCallback.onFailure(mBaseResponse);
            }
        } else {
            mCallback.onFailure(new NoResponseFromServerException());
        }
    }


}
