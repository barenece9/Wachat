/*Background service class where app listen for all push
    notification and process accordingly.
*/

package com.wachat;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.GCMBaseIntentService;
import com.wachat.application.AppVhortex;
import com.wachat.data.DataPushNotification;
import com.wachat.util.Prefs;

import java.util.Random;

public class GCMIntentService extends GCMBaseIntentService {
    private Prefs prefs;
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(GcmUtilities.SENDER_ID);
        prefs = AppVhortex.sharedPrefs();
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        prefs.setGcmId(registrationId);
        Log.i(TAG, "onRegistered: " + registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        prefs.setGcmId("");
        Log.i(TAG, "onUnregistered: " + registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        //Push notification received, check if user logged in then show notification.
        String message = intent.getExtras().getString("message");
        Log.i(TAG, "Received message: " + message);

        DataPushNotification dataPushNotification = null;
        if(!TextUtils.isEmpty(message)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                dataPushNotification = mapper.readValue(message, DataPushNotification.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

//        if (message != null && !message.equals("")) {
//            String isLoggedIn = prefs.getString(Prefs.PREFS_KEY_USER_ID);
//            if (isLoggedIn != null && !isLoggedIn.equalsIgnoreCase(""))
//                sendNotification(context, message);
//        }
        }

        if(dataPushNotification!=null){
            if(dataPushNotification.getAps()!=null){
                String category = dataPushNotification.getAps().getCategory();
                if(!TextUtils.isEmpty(category)){
                    long groupId = Long.parseLong(category);
                    if(groupId>1){
                        String alert = dataPushNotification.getAps().getAlert();
                        if(TextUtils.isEmpty(alert)) {
//                            NotificationUtils.showGroupEventNotificationMessage(context, "You have a notification");
                        }else{
                            AppVhortex.getInstance().getContactService(this);
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onDeletedMessages(Context context, int total) {

    }

    @Override
    public void onError(Context context, String errorId) {
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }

    // check and verify the type of notification, and send appropriate data to the user
//    private void sendNotification(final Context context, final String message) {
//        Log.i(TAG, "new message= " + message);
//        if (!TextUtils.isEmpty(message)) {
//            try {
//                JSONObject mJsonObject = new JSONObject(message);
//                String gcmNotiType = mJsonObject.getString("notitype");
//
//                //gcmNotiType tells the purpose of any notification.
//                if (gcmNotiType.equalsIgnoreCase("followpanic")) {
//                    Intent inStart = new Intent(context, SplashActivity.class);
//                    PendingIntent pending = PendingIntent.getActivity(context,
//                            2, inStart, 0);
//
//                    DisplayNotiAsync displayNotiAsync = new DisplayNotiAsync(context, "Message", "Your ride started!", pending, generateRandomNotiId());
//                    displayNotiAsync.execute();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //create and through notification in background
//    private class DisplayNotiAsync extends AsyncTask<Void, Void, Void> {
//        private Context ctx;
//        private String notiMess;
//        private String notiTitle;
//        private PendingIntent pendingIntent;
//        private int notiId;
//
//        public DisplayNotiAsync(Context ctx, String notiMess, String notiTitle,
//                                PendingIntent pendingIntent, int notiId) {
//            this.ctx = ctx;
//            this.notiMess = notiMess;
//            this.notiTitle = notiTitle;
//            this.pendingIntent = pendingIntent;
//            this.notiId = notiId;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            Notification notification = new NotificationCompat.Builder(ctx)
//                    .setContentTitle(notiTitle).setContentText(notiMess)
//                    .setContentIntent(pendingIntent)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notiMess))
//                    .build();
//
//            NotificationManager notificationmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationmanager.notify(notiId, notification);
//            return null;
//        }
//    }

    // generate a unique notification id, so each and every notification can be treated separate through out the application.
    private int generateRandomNotiId() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
}