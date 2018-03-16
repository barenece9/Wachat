package com.wachat.async;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.application.AppVhortex;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataCreateOrEditGroup;
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
 * Created by Argha on 06-10-2015.
 */
public class AsyncCreateEditGroup extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncCreateEditGroup.class);

    private InterfaceResponseCallback mCallback;
    private DataCreateOrEditGroup mDataCreateOrEditGroup;
    private BaseResponse mBaseResponse;
    private Throwable mException;
    private String groupJId = "";
    private String userId = "";
    private String groupName = "";
    private String groupMemberUserId = "";
    private String creationDateTime = "";
    private String groupId = "";
    private String groupImage = "";


    public AsyncCreateEditGroup(String userId, String groupName, String groupJId,
                                String groupMemberUserId, String creationDateTime, String groupId, String groupImage,
                                InterfaceResponseCallback mCallback) {
        this.userId = userId;
        this.groupName = groupName;
        this.groupMemberUserId = groupMemberUserId;
        this.creationDateTime = creationDateTime;
        this.groupImage = groupImage;
        this.groupId = groupId;
        this.groupJId = groupJId;
        this.mCallback = mCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<NameValuePair> mListPairs = new ArrayList<NameValuePair>();
        mListPairs.add(new BasicNameValuePair(WebContstants.userId, userId));
        mListPairs.add(new BasicNameValuePair(WebContstants.groupName, CommonMethods.getUTFEncodedString(groupName)));
        mListPairs.add(new BasicNameValuePair(WebContstants.groupMemberUserId, groupMemberUserId));
        mListPairs.add(new BasicNameValuePair(WebContstants.creationDateTime, creationDateTime));
        mListPairs.add(new BasicNameValuePair(WebContstants.groupId, groupId));
        mListPairs.add(new BasicNameValuePair(WebContstants.groupJId, groupJId));
        try {
            DataResponseWSInvoke mDataResponseWSInvoke = WebServiceUtils.
                    getHTTPURL_Response(HTTPmethods.POST, WebContstants.UrlCreateOrEditGroup,
                            mListPairs, WebContstants.groupImage, new File(groupImage), null);

            String result = mDataResponseWSInvoke.getResponse();
            LogUtils.i(LOG_TAG, "create group: " + result);
            if (result != null && result.length() > 0) {
                startParsing(result);
            }
        } catch (Throwable e) {
            LogUtils.i("AsyncCreateEditGroup", "" + e.getLocalizedMessage());
            mException = e;
        }
        return null;
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mBaseResponse = mapper.readValue(result, BaseResponse.class);
            if (mBaseResponse.getResponseCode() == WebContstants.ResponseCode_200) {
                mDataCreateOrEditGroup = mapper.readValue(result, DataCreateOrEditGroup.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mBaseResponse != null) {
            if (mBaseResponse.getResponseCode() == VhortextStatusCode.Ok) {
                if (mDataCreateOrEditGroup != null) {
                    mCallback.onResponseObject(mDataCreateOrEditGroup);
                } else {
                    mCallback.onResponseFaliure(CommonMethods.getAlertMessageFromException(
                            AppVhortex.getInstance().getApplicationContext(),
                            new NoResponseFromServerException()));
                }
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
