package com.wachat.async;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.BaseResponse;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;
import com.wachat.util.NoResponseFromServerException;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class AsyncBlockUserFromChat extends AsyncTask<Void, Void, Void> {


    private Context mContext;
    private InterfaceResponseCallback mCallback;
    private String userId = "";
    private String friendChatId = "";
    private String blockStatus = "";
    private Throwable mException;

    private BaseResponse mBaseResponse;

    public AsyncBlockUserFromChat(Context mContext, String userId, String friendChatId, String blockStatus, InterfaceResponseCallback mCallback) {

        this.mContext = mContext;
        this.userId = userId;
        this.friendChatId = friendChatId;
        this.blockStatus = blockStatus;
        this.mCallback = mCallback;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendChatId, friendChatId));
        mListPairs.add(new BasicNameValuePair(WebContstants.blockStatus, (blockStatus.equals("0")) ? "1" : "0"));


        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlBlockUser);
            String result = mHttpConnectionClass.getResponse(mListPairs);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncBlockUserFromChat", "" + e.getLocalizedMessage());
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

        if (mBaseResponse != null) {
            if (mBaseResponse.getResponseCode() == VhortextStatusCode.Ok) {
                mCallback.onResponseObject(mBaseResponse);
            } else {
                mCallback.onResponseObject(mBaseResponse.getResponseDetails());
            }
        } else {
            String error = "";
            if (mException == null) {
                mException = new NoResponseFromServerException();
            }
            error = CommonMethods.getAlertMessageFromException(
                    AppVhortex.getInstance().getApplicationContext(), mException);
            mCallback.onResponseFaliure(error);
        }

    }


}
