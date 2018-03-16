package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.BaseResponse;
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
public class AsyncContactUs extends AsyncTask<Void, Void, Void> {

    private InterfaceResponseCallback mCallback;
    private String userno, subject, message = "";
    private File mFile;
    private Throwable mException;
    private BaseResponse mBaseResponse;

    public AsyncContactUs(String userno, String subject, String message, String image, InterfaceResponseCallback mCallback) {
        this.userno = userno;
        this.subject = subject;
        this.message = message;
        this.mCallback = mCallback;
        if (image != null)
            if (!image.equalsIgnoreCase(""))
                mFile = new File(image);
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.subject, subject));
        mListPairs.add(new BasicNameValuePair(WebContstants.userno, userno));
        mListPairs.add(new BasicNameValuePair(WebContstants.message, message));
        try {
            DataResponseWSInvoke mDataResponseWSInvoke = WebServiceUtils.
                    getHTTPURL_Response(HTTPmethods.POST,
                            WebContstants.Urlsendmail,
                            mListPairs, WebContstants.image_send, mFile, null);
            String result = mDataResponseWSInvoke.getResponse();
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncContactUs", "" + e.getLocalizedMessage());
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
