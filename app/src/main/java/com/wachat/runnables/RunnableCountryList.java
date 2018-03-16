package com.wachat.runnables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataCountry;
import com.wachat.httpConnection.HttpConnectionClass;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.CommonMethods;
import com.wachat.util.WebContstants;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class RunnableCountryList implements Runnable {

    private InterfaceResponseCallback mInterface;
    HttpConnectionClass mHttpConnectionClass;
    private BaseResponse mBaseResponse;
    private ArrayList<DataCountry> mListCountry;

    public RunnableCountryList(InterfaceResponseCallback mInterface) {
        this.mInterface = mInterface;
        mListCountry = new ArrayList<DataCountry>();
        mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlCountrylist);
    }

    @Override
    public void run() {
        String result = mHttpConnectionClass.getResponse(null);
        if (result != null && result.length() > 0) {
            startParsing(result);
            if (mListCountry != null && mListCountry.size() > 0) {
                mInterface.onResponseList(mListCountry);
            } else
                mInterface.onResponseFaliure("");
        }
    }

    private void startParsing(String result) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            mBaseResponse = mapper.readValue(result, BaseResponse.class);
            if (mBaseResponse.getResponseCode() == WebContstants.ResponseCode_200) {
                JsonNode mJsonNode = mapper.readTree(result);
                mListCountry = mapper.readValue(mJsonNode.get(WebContstants.countries).toString(), new TypeReference<ArrayList<DataCountry>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
