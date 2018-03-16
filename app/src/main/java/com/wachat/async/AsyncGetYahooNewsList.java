package com.wachat.async;

import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;

import com.wachat.data.DataYahooNews;
import com.wachat.util.LogUtils;
import com.wachat.util.WebContstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class AsyncGetYahooNewsList extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AsyncGetYahooNewsList.class);

    private YahooNewsListCallBack callBack;
    private Exception mException;
    private ArrayList<DataYahooNews> dataYahooNewsList = null;
    public AsyncGetYahooNewsList(YahooNewsListCallBack callBack) {
        this.callBack = callBack;
    }

    public AsyncGetYahooNewsList(ArrayList<DataYahooNews> dataYahooNewsList, YahooNewsListCallBack callBack) {
        this.dataYahooNewsList = dataYahooNewsList;
        this.callBack = callBack;
    }

    public static String get(String url)
            throws Exception {

        HttpURLConnection httpConn = null;
        String response = "";
        try {
            InputStream in = null;
            int rescode = -1;

            URLConnection conn = new URL(url).openConnection();

            httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            rescode = httpConn.getResponseCode();
            if (rescode == HttpURLConnection.HTTP_OK) {
                try {
                    in = httpConn.getInputStream();

                    BufferedReader rd = new BufferedReader(
                            new InputStreamReader(in));
                    String line;
                    StringBuffer sb = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                        sb.append('\r');
                    }
                    rd.close();

                    response = sb.toString();
                } catch (Exception e) {
                    LogUtils.i(LOG_TAG,"Buffer Error: Error converting result " + e.toString());
                }
            } else {
                throw new Exception(
                        "ResponseCode:" + rescode + " Message: "
                                + httpConn.getResponseMessage() != null ? httpConn
                                .getResponseMessage()
                                : "Failed to get response");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // LogUtils.d("RequestClient", "Post: exception" + e);
            throw e;
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        LogUtils.i(LOG_TAG, "Post: url:" + url + " Response: "
                + response);
        // response.length()
        return response;
    }

    @Override
    protected void onPostExecute(Void resultList) {
        super.onPostExecute(resultList);
        if (callBack == null) {
            return;
        }
        if (mException != null) {
            callBack.onFailure(mException);
            return;
        }

        callBack.onSuccess(dataYahooNewsList);
    }


    @Override
    protected Void doInBackground(Void... params) {


        String result = null;
        try {
            result = get(WebContstants.UrlYahooNewsList);
        } catch (Exception e) {
            this.mException = e;
        }
        if (!TextUtils.isEmpty(result)) {
//            startParsing(result);
            parseResponse(result);
        }
        return null;
    }

    private void parseResponse(String result) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonObject != null) {

            JSONObject queryJsonObject = jsonObject.optJSONObject("query");
            if (queryJsonObject != null) {
                JSONObject resultsJsonObject = queryJsonObject.optJSONObject("results");
                if (resultsJsonObject != null) {
                    JSONArray itemsArray = resultsJsonObject.optJSONArray("item");

                    if (itemsArray != null) {
                        if (dataYahooNewsList == null) {
                            dataYahooNewsList = new ArrayList<DataYahooNews>();
                        }
                        for (int i = 0; i <= itemsArray.length(); i++) {
                            JSONObject itemObj = itemsArray.optJSONObject(i);
                            if (itemObj != null) {
                                DataYahooNews dataYahooNews = new DataYahooNews();

                                dataYahooNews.setTitle(itemObj.optString("title", ""));
                                dataYahooNews.setDescription(getContentDescription(itemObj.optString("description", "")));
                                dataYahooNews.setLink(itemObj.optString("link", ""));

                                JSONObject contentJsonObject = itemObj.optJSONObject("content");
                                if (contentJsonObject != null) {
                                    String imgURL  =contentJsonObject.optString("url", "");
                                    if(imgURL.contains("http")){
                                        String[] splittedImgURL = imgURL.split("http");
                                        dataYahooNews.setUrl("http"+splittedImgURL[splittedImgURL.length-1]);
                                    }
//                                    if(imgURL.contains("--/")) {
//                                        String[] splittedImgURL = imgURL.split("--/");
//                                        dataYahooNews.setUrl(splittedImgURL[splittedImgURL.length-1]);
//                                    }else{
//                                        dataYahooNews.setUrl(imgURL);
//                                    }

                                }
                                String pubDate = itemObj.optString("pubDate", "");
                                dataYahooNews.setPubDate(itemObj.optString("pubDate", ""));

                                dataYahooNewsList.add(dataYahooNews);


                            }
                        }

                    }
                }
            }

        }


    }

//    private ArrayList<YouTubeVideo> startParsing(String result) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            mDataValidateUser = mapper.readValue(result, DataValidateUser.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private String getContentDescription(String result) {
        String content = "";
        try {
            content = result.substring(result.indexOf("</a>"), result.lastIndexOf("</p>"));
            Html.fromHtml(content).toString();
            content = Html.fromHtml(content).toString();
        } catch (Exception e) {
            content = "";
        }

        return content;
    }


    public interface YahooNewsListCallBack {
        void onSuccess(ArrayList<DataYahooNews> dataYahooNewsArrayList);

        void onFailure(Exception e);
    }


}
