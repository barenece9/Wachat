package com.wachat.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

import com.wachat.data.DataCountry;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.runnables.RunnableCountryList;
import com.wachat.storage.TableCountry;
import com.wachat.util.Constants;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
public class ServiceCountry extends Service implements InterfaceResponseCallback {

    private ResultReceiver mResultReceiver = null;
    private ExecutorService mExecutorService;
    private RunnableCountryList mRunnableCountryList = null;

    private Context mContext;

    public ServiceCountry() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mRunnableCountryList == null) {
            mContext = getApplicationContext();
            if (intent != null) {
                mResultReceiver = intent.getParcelableExtra(Constants.B_TYPE);
            } else {
                mResultReceiver = null;
            }
            mExecutorService = Executors.newFixedThreadPool(1);
            mRunnableCountryList = new RunnableCountryList(this);
            mExecutorService.execute(mRunnableCountryList);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onResponseObject(Object mObject) {
        stopSelf();
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
        TableCountry mTableCountry = new TableCountry(mContext);
        if (mTableCountry.insertCategory((ArrayList<DataCountry>) mList)) {
            if (mResultReceiver != null) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(Constants.B_RESULT, mList);
                try {
                    mResultReceiver.send(0, mBundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        stopSelf();
    }

    @Override
    public void onResponseFaliure(String responseText) {
//        ToastUtils.showAlertToast(getBaseContext(), responseText, ToastType.FAILURE_ALERT);
        stopSelf();
    }
}
