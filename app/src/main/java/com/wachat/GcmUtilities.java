package com.wachat;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.wachat.application.AppVhortex;
import com.wachat.util.Prefs;

/**
 * Created by Debopam Sikder on 25-05-2016.
 */
public class GcmUtilities {
    /**Dev*/
//    public static final String SENDER_ID = "338094692368";
//    API_Key: AIzaSyBPXBBw0i-px00r_3ctRTFoWD0PQP7m96s

    /**Client*/
    public static final String SENDER_ID = "240062030604";
//    API_Key: AIzaSyCJq7wPbksPnOrWPY1Er3Daia_e1RDUMH0


    public Activity activityContext;
    GcmOnCompleteListener listener;
    Prefs prefs;
    private int count = 0;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public GcmUtilities(Activity activityContext, GcmOnCompleteListener listener) {
        this.activityContext = activityContext;
        this.listener = listener;
        prefs = AppVhortex.sharedPrefs();
        gcm_register();
    }

    protected void gcm_register() {
        /* registering receiver */
        try {
            try {
                GCMStartup();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * TODO This is the part that will register to the device to our specific
     * GCM API.
     */
    private void GCMStartup() {
        GCMRegistrar.checkDevice(activityContext);
        GCMRegistrar.checkManifest(activityContext);
        if (checkPlayServices()) {
            registerGCM();
        }

        new AsyncTask<Void, Integer, Void>() {
            private int sleepValues = 500;

            @Override
            protected Void doInBackground(Void... arg0) {
                for (int i = 0; i <= 6000; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    } finally {
                        publishProgress(sleepValues);
                    }
                    i = sleepValues;
                    sleepValues += 500;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
                sleepValues = 11000;
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    if (!TextUtils.isEmpty(prefs.getGcmId()))
                        listener.onComplete();
                    else {
                        if (count < 3) {
                            GCMStartup();
                            count++;
                            return;
                        }
//                        Alert_Confirmation.getAlert(context, AppConstants.DIALOG_RETRY, "RETRY", "CANCEL",
//                                new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                GCMStartup();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                listener.onCancel();
//                            }
//                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activityContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activityContext,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private void registerGCM() {
        String gcmId = GCMRegistrar.getRegistrationId(activityContext);
        if (TextUtils.isEmpty(gcmId) ){
            GCMRegistrar.register(activityContext,
                    GcmUtilities.SENDER_ID);
        }else {
            prefs.setGcmId(gcmId);
        }
        Log.i("GCM", "RegId:" + GCMRegistrar.getRegistrationId(activityContext));
    }
}
