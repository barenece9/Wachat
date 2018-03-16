package com.wachat.httpConnection;

import com.wachat.util.WebContstants;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Argha on 23-09-2015.
 */
public class WebServiceUtils {

    public static DataResponseWSInvoke getHTTPURL_Response(HTTPmethods methods,
                                                       String url, List<NameValuePair> nameValuePairs, String imgKey ,File file_image,
                                                       File file_video) {

        DataResponseWSInvoke mDataResponse = new DataResponseWSInvoke();
        String res = "";
        HttpGet httprequest_get = null;
        HttpPost httprequest_post = null;
        if (methods == HTTPmethods.GET) {
            httprequest_get = new HttpGet(url);
            httprequest_get.setHeader(WebContstants.apikey, WebContstants.apikeyValue);
        } else {
            httprequest_post = new HttpPost(url);
            httprequest_post.setHeader(WebContstants.apikey,WebContstants.apikeyValue);

            try {
                if (file_image == null && file_video == null
                        && nameValuePairs != null)
                    httprequest_post.setEntity(new UrlEncodedFormEntity
                            (nameValuePairs));

                if (file_image != null || file_video != null) {
                    MultipartEntity reqEntityImage = new MultipartEntity();

                    if (file_image != null && file_image.exists())
                        reqEntityImage.addPart(imgKey,
                                new FileBody(file_image ));

                    if (file_video != null && file_video.exists())
                        reqEntityImage.addPart(WebContstants.video,
                                new FileBody(file_video));

                    if (nameValuePairs != null)
                        for (int i = 0; i < nameValuePairs.size(); i++) {
                            reqEntityImage.addPart(nameValuePairs.get(i)
                                            .getName().toString(),
                                    new StringBody(nameValuePairs.get(i)
                                            .getValue().toString()));
                        }
                    httprequest_post.setEntity(reqEntityImage);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 40000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                40000);
        HttpResponse httpResponse = null;
        try {
            if (methods == HTTPmethods.GET) {
                httpResponse = httpclient.execute(httprequest_get);
            } else {
                httpResponse = httpclient.execute(httprequest_post);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (httpResponse != null) {
                mDataResponse.setStatus_line(httpResponse.getStatusLine()
                        .getStatusCode());
                mDataResponse.setResponse(EntityUtils.toString(httpResponse
                        .getEntity()));
            } else {
                mDataResponse.setStatus_line(0);
                mDataResponse.setResponse("");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mDataResponse;
    }


}
