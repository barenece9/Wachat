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

public class AsyncSendPushNotification extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncSendPushNotification.class);
    private String FromUserId = "";
    private String ToChatId = "";
    private String GroupID = "";
    private String Message = "";
    private String Type = "";
    private String GroupName = "";

    private WebServiceCallBack mCallback;
    private BaseResponse mBaseResponse;
    private Throwable mException;


    public AsyncSendPushNotification(String FromUserId,
                                     String ToChatId,
                                     String GroupID,
                                     String Message,
                                     String Type,String GroupName, WebServiceCallBack mCallback) {
        this.FromUserId = FromUserId;
        this.ToChatId = ToChatId;
        this.GroupID = GroupID;
        this.Message = Message;
        this.Type = Type;
        this.GroupName = GroupName;
        this.mCallback = mCallback;
    }


    @Override
    protected Void doInBackground(Void... params) {

        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair("FromUserId", FromUserId));
        mListPairs.add(new BasicNameValuePair("ToChatId", ToChatId));
        mListPairs.add(new BasicNameValuePair("GroupID", GroupID));
        mListPairs.add(new BasicNameValuePair("Message", Message));
        mListPairs.add(new BasicNameValuePair("Type", Type));
        mListPairs.add(new BasicNameValuePair("GroupName", GroupName));

        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.SendNotification);
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);
            LogUtils.i(LOG_TAG, "AsyncSendPushNotification:Params:" + mListPairs.toString() + "Response:" + result);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i(LOG_TAG, "AsyncSendPushNotification" + e.getLocalizedMessage());
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
