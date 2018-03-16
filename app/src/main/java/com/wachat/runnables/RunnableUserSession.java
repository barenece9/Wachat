package com.wachat.runnables;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.data.BaseResponse;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.WebContstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
public class RunnableUserSession implements Runnable {

    private Context mContext;
    private Handler uiHandler;
    private ArrayList<NameValuePair> mListPair;
    private String userId = "";
    private BaseResponse mBaseResponse;
    HttpConnectionClass mHttpConnectionClass;

    public RunnableUserSession(Context mContext, String userId, Handler uiHandler) {
        this.mContext = mContext;
        this.userId = userId;
        this.uiHandler = uiHandler;
        mListPair = new ArrayList<>();

    }

    @Override
    public void run() {
        String deviceUniqueId = CommonMethods.getDeviceUniqueId(mContext);
        HttpConnectionClass mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlValidateUserSession);
        mListPair = CommonMethods.getCommonRequestParams();
        mListPair.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPair.add(new BasicNameValuePair(WebContstants.deviceUniqueId, deviceUniqueId));
        mListPair.add(new BasicNameValuePair(WebContstants.deviceType, WebContstants.Android));
        Log.d("RunnableUserSession"," userId:"+userId+" deviceUniqueId:"+deviceUniqueId+" deviceType:"+WebContstants.Android);
        String result = mHttpConnectionClass.getResponse(mListPair);

        if (result != null && result.length() > 0) {
            startParsing(result);
        }

        try {
            Thread.sleep(Constants.ContactSyncTiming);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (uiHandler != null) {
            Message message = uiHandler.obtainMessage();
            message.what = 1234;//back to appVhortext
            Bundle mBundle = new Bundle();
            boolean sessionValid = true;
            if(mBaseResponse != null
                    && mBaseResponse.getResponseCode() == WebContstants.ResponseCode_300){
                sessionValid = false;
            }

//            mBundle.putBoolean(Constants.B_ID, (mBaseResponse != null
//                    && mBaseResponse.getResponseCode() == WebContstants.ResponseCode_200) ?
//                    ((mBaseResponse.getResponseCode() == WebContstants.ResponseCode_300) ? false : true) : true);
            mBundle.putBoolean(Constants.B_ID, sessionValid);
//            Log.i("user session xpired",mBaseResponse.getResponseDetails());
            message.setData(mBundle);
            uiHandler.sendMessage(message);
        }
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
