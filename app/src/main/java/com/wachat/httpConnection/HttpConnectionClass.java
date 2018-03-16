package com.wachat.httpConnection;

import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;

public class HttpConnectionClass implements HttpRequestRetryHandler {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(HttpConnectionClass.class);
    private final String url;
    HttpClient mHttpClient = null;
    HttpPost mHttpPost = null;

    int timeoutConnection = 50000;
    int timeoutSocket = 100000;

    private int NumOfRetry = 0;

    public HttpConnectionClass(String url) {

        this.url = url;
        mHttpClient = new DefaultHttpClient(setParam());
        this.mHttpPost = new HttpPost(url);
    }

    private HttpParams setParam() {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        return httpParameters;
    }

    public void cancelRequest() {
        try {
            mHttpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            LogUtils.i(LOG_TAG, "Canceling Exception");
            e.printStackTrace();
        }
    }

    public String getResponse(ArrayList<NameValuePair> mNameValuePairs) {
        String result = "";
        try {
            for (NameValuePair mNameValuePair : CommonMethods.getHeaderValue()) {
                mHttpPost.addHeader(mNameValuePair.getName(), mNameValuePair.getValue());
            }
            if (mNameValuePairs != null)
                mHttpPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs,
                        "UTF-8"));
            ((AbstractHttpClient) mHttpClient).setHttpRequestRetryHandler(null);
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            StringBuilder mBuilder = new StringBuilder();
            String data = "";
            InputStream mInputStream = mHttpEntity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    mInputStream));
            while ((data = br.readLine()) != null) {
                mBuilder.append(data);
            }
            result = mBuilder.toString();
            LogUtils.i(LOG_TAG,"url:"+url+" Result: "+ result);

        } catch (Exception e) {
            LogUtils.i(LOG_TAG,"url:"+url+" Error: "+e.getLocalizedMessage());
            e.printStackTrace();
        }
        return result;
    }


    public String getResponseWithException(ArrayList<NameValuePair> mNameValuePairs) throws Exception {
        String result = "";
        for (NameValuePair mNameValuePair : CommonMethods.getHeaderValue()) {
            mHttpPost.addHeader(mNameValuePair.getName(), mNameValuePair.getValue());
        }
        if (mNameValuePairs != null)
            mHttpPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs,
                    "UTF-8"));
        ((AbstractHttpClient) mHttpClient).setHttpRequestRetryHandler(null);
        HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
        HttpEntity mHttpEntity = mHttpResponse.getEntity();
        StringBuilder mBuilder = new StringBuilder();
        String data = "";
        InputStream mInputStream = mHttpEntity.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                mInputStream));
        while ((data = br.readLine()) != null) {
            mBuilder.append(data);
        }
        result = mBuilder.toString();
        LogUtils.i(LOG_TAG,"url:"+url+" Result: "+ result);

        return result;
    }
    // public DataStatusCode getResponse(ArrayList<NameValuePair>
    // mNameValuePairs,
    // String imageTag, String imagePath) throws Exception {
    // InputStream inputStream = null;
    //
    // String result = "";
    // DataStatusCode mDataStatusCode = null;
    //
    // MultipartEntity reqEntity = new MultipartEntity(
    // HttpMultipartMode.BROWSER_COMPATIBLE);
    // File lfile = null;
    // if (mNameValuePairs != null && mNameValuePairs.size() > 0) {
    // for (int i = 0; i < mNameValuePairs.size(); i++) {
    // reqEntity.addPart(mNameValuePairs.get(i).getName(),
    // new StringBody(mNameValuePairs.get(i).getValue()));
    // }
    // }
    //
    // if (imagePath != null && imagePath.length() > 0) {
    // // Compressing Image with 70% of the original quality
    // Bitmap bmp = BitmapFactory.decodeFile(imagePath);
    // ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // bmp.compress(CompressFormat.JPEG, 70, bos);
    // InputStream in = new ByteArrayInputStream(bos.toByteArray());
    // ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");
    // reqEntity.addPart(imageTag, foto);
    // }
    //
    // mHttpPost.setEntity(reqEntity);
    // HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
    // HttpEntity mHttpEntity = mHttpResponse.getEntity();
    // StringBuilder mBuilder = new StringBuilder();
    // String data = "";
    // InputStream mInputStream = mHttpEntity.getContent();
    // BufferedReader br = new BufferedReader(new InputStreamReader(
    // mInputStream));
    // while ((data = br.readLine()) != null) {
    // mBuilder.append(data);
    // }
    // result = mBuilder.toString();
    // mDataStatusCode = new DataStatusCode(mHttpResponse.getStatusLine()
    // .getStatusCode(), result);
    // return mDataStatusCode;
    // }

//	public DataStatusCode getResponse(ArrayList<NameValuePair> mNameValuePairs,
//			String imageTag, Bitmap imageBitmap) throws Exception {
//		InputStream inputStream = null;
//		String result = "";
//		DataStatusCode mDataStatusCode = null;
//		MultipartEntity reqEntity = new MultipartEntity(
//				HttpMultipartMode.BROWSER_COMPATIBLE);
//		File lfile = null;
//		if (mNameValuePairs != null && mNameValuePairs.size() > 0) {
//			for (int i = 0; i < mNameValuePairs.size(); i++) {
//				reqEntity.addPart(mNameValuePairs.get(i).getName(),
//						new StringBody(mNameValuePairs.get(i).getValue()));
//			}
//		}
//
//		if (imageBitmap != null) {
//			// Compressing Image with 70% of the original quality
//			Bitmap bmp = imageBitmap;
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			bmp.compress(CompressFormat.JPEG, 70, bos);
//			InputStream in = new ByteArrayInputStream(bos.toByteArray());
//			ContentBody foto = new InputStreamBody(in, "image/jpeg", "filename");
//			reqEntity.addPart(imageTag, foto);
//		}
//
//		mHttpPost.setEntity(reqEntity);
//		HttpResponse mHttpResponse = mHttpClient.execute(mHttpPost);
//		HttpEntity mHttpEntity = mHttpResponse.getEntity();
//		StringBuilder mBuilder = new StringBuilder();
//		String data = "";
//		InputStream mInputStream = mHttpEntity.getContent();
//		BufferedReader br = new BufferedReader(new InputStreamReader(
//				mInputStream));
//		while ((data = br.readLine()) != null) {
//			mBuilder.append(data);
//		}
//		result = mBuilder.toString();
//		mDataStatusCode = new DataStatusCode(mHttpResponse.getStatusLine()
//				.getStatusCode(), result);
//		return mDataStatusCode;
//	}

    @Override
    public boolean retryRequest(IOException exception, int executionCount,
                                HttpContext context) {
        if (exception instanceof IOException) {
            if (NumOfRetry == 2) {
                return false;
            } else {
                NumOfRetry++;
                return true;
            }
        } else if (exception instanceof SocketException) {
            if (NumOfRetry == 2) {
                return false;
            } else {
                NumOfRetry++;
                return true;
            }
        } else {
            return false;
        }
    }
}
