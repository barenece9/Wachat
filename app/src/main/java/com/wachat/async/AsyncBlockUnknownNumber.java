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

import java.util.ArrayList;

public class AsyncBlockUnknownNumber extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncBlockUnknownNumber.class);

    private String userId = "";
    private String friendChatId = "";
    private String blockStatus = "";

    private WebServiceCallBack mCallback;
    private BaseResponse mBaseResponse;
    private Throwable mException;


    public AsyncBlockUnknownNumber(String userId, String friendChatId,
                                   String blockStatus, WebServiceCallBack mCallback) {
        this.userId = userId;
        this.friendChatId = friendChatId;
        this.blockStatus = blockStatus;
        this.mCallback = mCallback;
    }


    @Override
    protected Void doInBackground(Void... params) {

        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlBlockUser);

        String block = blockStatus.equals("0") ? "1" : "0";
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendChatId, friendChatId));
        mListPairs.add(new BasicNameValuePair(WebContstants.blockStatus, block));

        LogUtils.i(LOG_TAG, "Params:userId=" + userId + "&friendChatId=" + friendChatId +
                "&blockStatus=" + block);
        try {
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);

            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, e.getLocalizedMessage());
            mException = e;

        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mBaseResponse = mapper.readValue(result, BaseResponse.class);
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
