package com.wachat.async;//package com.wachat.async;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.text.TextUtils;
//
//import com.wachat.R;
//import com.wachat.data.DataCountry;
//import com.wachat.httpConnection.HttpConnectionClass;
//import com.wachat.interfaces.InterfaceResponseCallback;
//import com.wachat.util.WebContstants;
//
//import org.json.JSONObject;
//
///**
// * Created by Priti Chatterjee on 21-09-2015.
// */
//public class AsynsfscCountryCodeSelection extends AsyncTask<Void, Void, Void> {
//
//    HttpConnectionClass mHttpConnectionClass;
//    Context mContext;
//    InterfaceResponseCallback mCallback;
//    DataCountry mDataCountry = null;
//    String country = "";
//
//    public AsyncCountryCodeSelection(Context mContext, InterfaceResponseCallback mCallback) {
//        this.mContext = mContext;
//        this.mCallback = mCallback;
//
//       mHttpConnectionClass = new HttpConnectionClass(WebContstants.UrlselectCountry);
//       // mHttpConnectionClass = new HttpConnectionClass("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyA88BnwjBdFl4gJkLkeEvjM5ocXh1quwOU");
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        String result = mHttpConnectionClass.getResponse(null);
//        if (result != null && result.length() > 0) {
//
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                if (!jsonObject.isNull("country"))
//                    country = jsonObject.getString("country");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//        if (!TextUtils.isEmpty(country))
//            mCallback.onResponseObject(new String(country));
//        else
//            mCallback.onResponseFaliure(mContext.getResources().getString(R.string.no_data));
//    }
//}
