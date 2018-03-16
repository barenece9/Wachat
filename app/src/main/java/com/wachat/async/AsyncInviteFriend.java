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
public class AsyncInviteFriend extends AsyncTask<Void, Void, Void> {




    private String myPhoneNumber = "";
    private String friendPhno = "";
    private Throwable mException;
    private BaseResponse mBaseResponse;
    private InterfaceResponseCallback mCallback;

    public AsyncInviteFriend(String myPhoneNumber, String friendPhno, InterfaceResponseCallback mCallback) {
        this.friendPhno = friendPhno;
        this.myPhoneNumber = myPhoneNumber;
        this.mCallback = mCallback;

    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<>();
        mListPairs.add(new BasicNameValuePair(WebContstants.phoneNo, myPhoneNumber));
        mListPairs.add(new BasicNameValuePair(WebContstants.friendNo, friendPhno));
        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlInviteFriend);
            String result = mHttpConnectionClass.getResponse(mListPairs);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        }catch (Throwable e) {
            LogUtils.i("AsyncInviteFriend",""+e.getLocalizedMessage());
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
