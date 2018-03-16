package com.wachat.async;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wachat.data.DataLanguages;
import com.wachat.httpConnection.DataResponseWSInvoke;
import com.wachat.httpConnection.HTTPmethods;
import com.wachat.httpConnection.WebServiceUtils;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.WebContstants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Argha on 12-11-2015.
 */
public class AsyncLanguages extends AsyncTask<Void, Void, Void> {

    private InterfaceResponseCallback mInterface;
    private DataResponseWSInvoke mDataResponseWSInvoke;
    private InterfaceResponseCallback mCallback;
    private ArrayList<DataLanguages> ArrlanguagesList;
    private String LangResult = "";

    public AsyncLanguages(InterfaceResponseCallback mInterface) {
        this.mInterface = mInterface;
        ArrlanguagesList = new ArrayList<DataLanguages>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            mDataResponseWSInvoke = WebServiceUtils.getHTTPURL_Response(HTTPmethods.GET,
                    WebContstants.UrlGapiForLang,
                    null, WebContstants.image, null, null);
            LangResult = mDataResponseWSInvoke.getResponse();
            if (!TextUtils.isEmpty(LangResult)) {

                JSONObject jsonObject = new JSONObject(LangResult);
                if (!jsonObject.isNull("data"))
                    LangResult = jsonObject.getString("data");
                StartParsing();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //parse languages


        if (ArrlanguagesList != null && ArrlanguagesList.size() > 0) {
            mInterface.onResponseList(ArrlanguagesList);
        } else
            mInterface.onResponseFaliure("");


    }

    private void StartParsing() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode mJsonNode = mapper.readTree(LangResult);
            ArrlanguagesList = mapper.readValue(mJsonNode.get(WebContstants.languages).toString(),
                    new TypeReference<ArrayList<DataLanguages>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
