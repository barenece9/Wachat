package com.wachat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.wachat.application.AppVhortex;
import com.wachat.util.LogUtils;

/**
 * Created by Priti Chatterjee on 16-10-2015.
 */
public class ContactReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ContactReceiver.class);

    private AppVhortex mAppVhortex;
    public static boolean isAlreadyRequested = false;
    private ReceiverHandler mReceiverHandler = new ReceiverHandler();

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            mAppVhortex = (AppVhortex)context.getApplicationContext();
        }catch(ClassCastException e){
            LogUtils.i(LOG_TAG, "Application Cast Exception");
        }

        LogUtils.i(LOG_TAG, "Requesting");
//        if(!isAlreadyRequested){
//            isAlreadyRequested = true;
//            Log.i(getClass().getSimpleName(), "Request Processing");
//            Thread mThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
////                        Thread.sleep(Constants.ContactSyncTiming);
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i(getClass().getSimpleName(), "Processing Request");
//                    mReceiverHandler.sendEmptyMessage(0);
//                }
//            });
//            mThread.start();
//        }
    }

    private class ReceiverHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mAppVhortex != null){
                isAlreadyRequested = false;
//                mAppVhortex.getContactService(mAppVhortex);
            }
        }
    }
}
