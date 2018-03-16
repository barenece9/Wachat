package com.wachat.async;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataEditProfile;
import com.wachat.httpConnection.DataResponseWSInvoke;
import com.wachat.httpConnection.HTTPmethods;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.httpConnection.WebServiceUtils;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;
import com.wachat.util.NoResponseFromServerException;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class AsyncEditProfile extends AsyncTask<Void, Void, Void> {
    private InterfaceResponseCallback mCallback;
    private String userId = "";
    private String name = "";
    private String image = "";
    private String language = "";
    private String gender = "";
    private String langCode = "";
    private String country = "";
    private Throwable mException;
    private DataEditProfile mDataEditProfile;

    public AsyncEditProfile(String userId, String name, String image, String language, String langCode, String gender, String country, InterfaceResponseCallback mCallback) {
        this.userId = userId;
        this.name = name;
        this.image = image;
        this.language = language;
        this.gender = gender;
        this.country = country;
        this.langCode = langCode;
        this.mCallback = mCallback;

    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(userId))
            mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
//        if (!TextUtils.isEmpty(name))
        mListPairs.add(new BasicNameValuePair(WebContstants.name, name));
        if (!TextUtils.isEmpty(language)) {
            mListPairs.add(new BasicNameValuePair(WebContstants.language, language));
            mListPairs.add(new BasicNameValuePair(WebContstants.langCode, langCode));
        }
        if (!TextUtils.isEmpty(gender))
            mListPairs.add(new BasicNameValuePair(WebContstants.gender, gender));

        if (!TextUtils.isEmpty(country))
            mListPairs.add(new BasicNameValuePair(WebContstants.countryName, country));

        LogUtils.d("AsyncEditProfile", "Url: " + WebContstants.UrlEditProfile + " Params: userId=" + userId + "&name=" + name + "&gender=" + gender + "");
        try {
            DataResponseWSInvoke mDataResponseWSInvoke;
            if (!TextUtils.isEmpty(image))
                mDataResponseWSInvoke = WebServiceUtils.getHTTPURL_Response(HTTPmethods.POST, WebContstants.UrlEditProfile,
                        mListPairs, WebContstants.image, new File(image), null);
            else
                mDataResponseWSInvoke = WebServiceUtils.getHTTPURL_Response(HTTPmethods.POST, WebContstants.UrlEditProfile,
                        mListPairs, WebContstants.image, null, null);

            String result = mDataResponseWSInvoke.getResponse();

            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncEditProfile", "" + e.getLocalizedMessage());
            mException = e;
        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mDataEditProfile = mapper.readValue(result, DataEditProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mDataEditProfile != null) {
            if (mDataEditProfile.getResponseCode() == VhortextStatusCode.Ok) {

                mCallback.onResponseObject(mDataEditProfile);
            } else {
                mCallback.onResponseFaliure(mDataEditProfile.getResponseDetails());
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
