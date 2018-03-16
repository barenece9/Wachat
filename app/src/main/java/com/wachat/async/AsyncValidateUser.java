package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataValidateUser;
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
public class AsyncValidateUser extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncValidateUser.class);
//    private ArrayList<NameValuePair> mListPairs;
//    private HttpConnectionClass mHttpConnectionClass;
//    private Context mContext;
    private InterfaceResponseCallback mCallback;
    private String userId = "";
    private String deviceId = "";
    private String verificationCode = "";
    private String changeAccountParing = "";

    private DataValidateUser mDataValidateUser;
    private Throwable mException;
    private String gcmId="";

    public AsyncValidateUser(String gcmId,String deviceId, String userId,
                             String verificationCode,
                             String changeAccountParing, InterfaceResponseCallback mCallback) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.verificationCode = verificationCode;
        this.changeAccountParing = changeAccountParing;
        this.mCallback = mCallback;
        this.gcmId = gcmId;


    }


    @Override
    protected Void doInBackground(Void... params) {

//        String deviceId = CommonMethods.getDeviceUniqueId(mContext);
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.verificationCode, verificationCode));
        mListPairs.add(new BasicNameValuePair(WebContstants.changeAccountParing, changeAccountParing));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceUniqueId,
                deviceId));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceGcmToken, gcmId));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceType, WebContstants.Android));
        LogUtils.i(LOG_TAG, "userId=" + userId + "&verificationCode=" + verificationCode
                + "&changeAccountParing=" + changeAccountParing
                + "&deviceUniqueId=" + deviceId
                + "&deviceType=" + deviceId);
        try {
            HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlValidateUser);
            String result = mHttpConnectionClass.getResponseWithException(mListPairs);
            if (result != null && result.length() > 0) {
                LogUtils.i(LOG_TAG, "Authenticate" + result);
                startParsing(result);
            }
        } catch (Exception e) {
            LogUtils.i(LOG_TAG, e.getLocalizedMessage());
            mException = e;
        }
        return null;
    }

    private void startParsing(String result) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mDataValidateUser = mapper.readValue(result, DataValidateUser.class);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mException != null) {
            if (mCallback != null) {
                mCallback.onResponseFaliure(CommonMethods.
                        getAlertMessageFromException(AppVhortex.applicationContext, mException));
            }
            return;
        }

        if (mCallback == null) {
            return;
        }

        if (mDataValidateUser != null) {
            if (mDataValidateUser.getResponseCode() == VhortextStatusCode.Ok) {
                mCallback.onResponseObject(mDataValidateUser);
            } else {
                mCallback.onResponseFaliure(mDataValidateUser.getResponseDetails());
            }
        } else {
            mCallback.onResponseFaliure(CommonMethods.getAlertMessageFromException(AppVhortex.applicationContext, new NoResponseFromServerException()));
        }


    }


}
