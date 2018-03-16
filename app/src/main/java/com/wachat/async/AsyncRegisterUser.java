package com.wachat.async;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.R;
import com.wachat.callBack.DialogCallback;
import com.wachat.data.DataRegistration;
import com.wachat.dialog.CommonDialog;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class AsyncRegisterUser extends AsyncTask<Void, Void, Void> implements DialogCallback {
    //
//    private ArrayList<NameValuePair> mListPairs;
//    private HttpConnectionClass mHttpConnectionClass;
    private Context mContext;
    private InterfaceResponseCallback mCallback;
    private String countryCode = "";
    private String phoneNo = "";
    private String deviceId = "";
    private String userName = "";
    private String countryName = "";
    private DataRegistration mDataRegistration;
    Prefs mPrefs;

    public AsyncRegisterUser(Context mContext, String deviceId, String userName,
                             String countryCode, String countryName, String phoneNo,
                             InterfaceResponseCallback mCallback) {
        this.countryCode = countryCode;
        this.phoneNo = phoneNo;
        this.countryName = countryName;
        this.userName = userName;
        this.mCallback = mCallback;
        this.deviceId = deviceId;

        this.mContext = mContext;
        mPrefs = Prefs.getInstance(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {

//        String deviceId = CommonMethods.getDeviceUniqueId(mContext);

        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.countryCode, countryCode));
        mListPairs.add(new BasicNameValuePair(WebContstants.phoneNo, phoneNo));
        mListPairs.add(new BasicNameValuePair(WebContstants.countryName, countryName));
        mListPairs.add(new BasicNameValuePair(WebContstants.userName, userName));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceUniqueId, deviceId));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceGcmToken, mPrefs.getGcmId()));
        mListPairs.add(new BasicNameValuePair(WebContstants.deviceType, WebContstants.Android));

        LogUtils.d("AsyncRegisterUser", "Url:" + WebContstants.UrlRegisterUser
                + " Params: countryCode=" + countryCode
                + "&phoneNo=" + phoneNo + "&countryName=" + countryName + "&userName=" + userName
                + "&deviceUniqueId=" + deviceId +
                "&deviceType=" + WebContstants.Android);
        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlRegisterUser);
        String result = mHttpConnectionClass.getResponse(mListPairs);
        if (result != null && result.length() > 0) {
            startParsing(result);
        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataRegistration = mapper.readValue(result, DataRegistration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mDataRegistration != null) {
            if (mDataRegistration.getResponseCode() == VhortextStatusCode.Ok) {

                mCallback.onResponseObject(mDataRegistration);
            } else if (mDataRegistration.getResponseCode() == 210) {

                CommonDialog.ShowDialog(mContext, this, mDataRegistration);
            } else {
                mCallback.onResponseFaliure(mContext.getResources().getString(R.string.no_data));
            }
        } else {
            mCallback.onResponseFaliure(mContext.getResources().getString(R.string.no_data));
        }
    }

    @Override
    public void OnClickYes(Object mObject) {
        mPrefs.setAccountParing("1");
        mCallback.onResponseObject(mObject);

    }

    @Override
    public void OnClickNo(Object mObject) {
        mPrefs.setIsRegClick("true");
        mCallback.onResponseFaliure("");
    }

    @Override
    public void onYes(int pos) {

    }
}
