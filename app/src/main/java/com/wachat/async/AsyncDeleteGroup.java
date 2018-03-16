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

public class AsyncDeleteGroup extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncDeleteGroup.class);

    private String groupId = "";
    private WebServiceCallBack mCallback;

    private BaseResponse mBaseResponse;
    private Throwable mException;

    public AsyncDeleteGroup(String groupId, WebServiceCallBack mCallback) {
        this.groupId = groupId;
        this.mCallback = mCallback;

    }


    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair("groupid", groupId));

        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.DeleteGroup);
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);

            LogUtils.i(LOG_TAG, "AsyncDeleteGroup:Response:" + result);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, e.getLocalizedMessage());
            mException = e;
        }

        if (mBaseResponse != null && mBaseResponse.getResponseCode() == VhortextStatusCode.Ok) {

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
