package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.BaseResponse;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.httpConnection.VhortextStatusCode;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class AsyncUpdateUserLocation extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncUpdateUserLocation.class);
    private String userId = "";
    private String lat = "";
    private String lng = "";
    private BaseResponse mBaseResponse;

    public AsyncUpdateUserLocation(String userId, String lat, String lng) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userid, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.lat, lat));
        mListPairs.add(new BasicNameValuePair(WebContstants.lng, lng));

        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlUpdateUserLocation);
        String result = mHttpConnectionClass.getResponse(mListPairs);
        LogUtils.i(LOG_TAG,"UpdateUserLocation"+lat+":"+lng+result.toString());

        if (result != null && result.length() > 0) {
            startParsing(result);

            if(mBaseResponse!=null&&mBaseResponse.getResponseCode()==VhortextStatusCode.Ok){
                Prefs.getInstance(AppVhortex.getInstance().getApplicationContext()).setLastUpdatedLatitude(lat);
                Prefs.getInstance(AppVhortex.getInstance().getApplicationContext()).setLastUpdatedLongitude(lng);
            }
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


}
