package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataContact;
import com.wachat.httpConnection.HttpConnectionClass;
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
public class AsyncFindPeopleByLocation extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncFindPeopleByLocation.class);


    private InterfaceResponseCallback mCallback;

    private String userId = "";
    private String searchText = "";
    private String lat = "";
    private String mLong = "";
    private String gender = "";
    private String radius = "";
    private ArrayList<DataContact> mArrDataContacts;

    public AsyncFindPeopleByLocation(String userId, String gender ,String searchText, String lat, String mLong, String radius, InterfaceResponseCallback mCallback) {
        this.userId = userId;
        this.gender = gender;
        this.searchText = searchText;
        this.lat = lat;
        this.mLong = mLong;
        this.radius =radius;
        this.mCallback = mCallback;

    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair("userid", userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.searchText, searchText));
        mListPairs.add(new BasicNameValuePair(WebContstants.gender, gender));
        mListPairs.add(new BasicNameValuePair(WebContstants.lat, lat));
        mListPairs.add(new BasicNameValuePair(WebContstants.mlong, mLong));
        mListPairs.add(new BasicNameValuePair(WebContstants.radius, radius));
        LogUtils.i(LOG_TAG,"Url: "+WebContstants.UrlFindPeopleByLocation+"Params: "+ mListPairs);
        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlFindPeopleByLocation);
        String result = mHttpConnectionClass.getResponse(mListPairs);
        if (result != null && result.length() > 0) {
            startParsing(result);
            LogUtils.i(LOG_TAG,"Find me"+ result);
        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode mJsonNode = mapper.readTree(result);
            mArrDataContacts = mapper.readValue(mJsonNode.get("users").toString(), new TypeReference<ArrayList<DataContact>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mArrDataContacts != null) {
            mCallback.onResponseList(mArrDataContacts);
        } else {
            mCallback.onResponseFaliure(CommonMethods.getAlertMessageFromException(
                    AppVhortex.getInstance().getApplicationContext(),
                    new NoResponseFromServerException()));
        }
    }

}
