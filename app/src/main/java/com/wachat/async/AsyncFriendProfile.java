package com.wachat.async;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataFriendProfile;
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
public class AsyncFriendProfile extends AsyncTask<Void, Void, Void> {


    private InterfaceResponseCallback mCallback;
    private String friendId = "";
    private String friendChatId = "";
    private String userId = "";
    private Throwable mException;
    private DataFriendProfile mDataFriendProfile;

    public AsyncFriendProfile(String userId,String friendId, String friendChatId, InterfaceResponseCallback mCallback) {
        this.friendId = friendId;
        this.userId = userId;
        this.friendChatId = friendChatId;
        this.mCallback = mCallback;

    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendId, friendId));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendChatId, friendChatId));

        Log.d("AsyncFriendProfile", " friendId:" + friendId + " friendChatId:" + friendChatId);
        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlFriendProfile);
            String result = mHttpConnectionClass.getResponse(mListPairs);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncFriendProfile", "" + e.getLocalizedMessage());
            mException = e;
        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataFriendProfile = mapper.readValue(result, DataFriendProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mDataFriendProfile != null) {
            if (mDataFriendProfile.getResponseCode() == VhortextStatusCode.Ok) {

                mCallback.onResponseObject(mDataFriendProfile);
            } else {
                mCallback.onResponseFaliure(mDataFriendProfile.getResponseDetails());
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
