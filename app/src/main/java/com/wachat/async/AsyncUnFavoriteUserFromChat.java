package com.wachat.async;

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
public class AsyncUnFavoriteUserFromChat extends AsyncTask<Void, Void, Void> {


//    private ArrayList<NameValuePair> mListPairs;
//    private HttpConnectionClass mHttpConnectionClass;
//    private Context mContext;
    private InterfaceResponseCallback mCallback;
    private String userId = "";
    private String friendChatId = "";
    private String favoriteStatus = "";

    private Throwable mException;
    private BaseResponse mBaseResponse;


    public AsyncUnFavoriteUserFromChat(String userId, String friendChatId, String favoriteStatus, InterfaceResponseCallback mCallback) {
        this.userId = userId;
        this.friendChatId = friendChatId;
        this.favoriteStatus = favoriteStatus;
        this.mCallback = mCallback;
    }


    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendChatId, friendChatId));
        mListPairs.add(new BasicNameValuePair(WebContstants.favoriteStatus, (favoriteStatus.equals("0")) ? "1" : "0"));

        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlFavoriteUser);
            String result = mHttpConnectionClass.getResponse(mListPairs);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncUnFavoriteUserFromChat",""+e.getLocalizedMessage());
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
                mCallback.onResponseFaliure(mBaseResponse.getResponseDetails());
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
