package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.data.BaseResponse;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.WebServiceCallBack;
import com.wachat.util.LogUtils;
import com.wachat.util.NoResponseFromServerException;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class AsyncLeaveGroup extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncLeaveGroup.class);

    private String userId = "";
    private String groupId = "";
    private WebServiceCallBack mCallback;
    private BaseResponse mBaseResponse;
    private Throwable mException;
    String isOwner = "";

    public AsyncLeaveGroup(String userId, String groupId, String _isOwner, WebServiceCallBack mCallback) {
        this.userId = userId;
        this.groupId = groupId;
        this.mCallback = mCallback;

        this.isOwner = _isOwner;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair("isowner", isOwner));
        mListPairs.add(new BasicNameValuePair("groupid", groupId));

        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.LeaveGroup);
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);

            LogUtils.i(LOG_TAG, "LeaveGroup:Response:" + result);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, e.getLocalizedMessage());
            mException = e;
        }

        return null;
    }

    private void startParsing(String result) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mBaseResponse = mapper.readValue(result, BaseResponse.class);
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

        if (mBaseResponse != null) {
            if (mBaseResponse.getResponseCode() == VhortextStatusCode.Ok) {
                mCallback.onSuccess(mBaseResponse);
            } else {
                mCallback.onFailure(mBaseResponse);
            }
        } else {
            mCallback.onFailure(new NoResponseFromServerException());
        }
    }


}
