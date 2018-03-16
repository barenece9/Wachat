package com.wachat.chatUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import com.wachat.R;
import com.wachat.activity.ActivityDash;
import com.wachat.data.DataProfile;
import com.wachat.data.DataTextChat;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Argha on 07-12-2015.
 */
public class NotificationUtils {

    private String TAG = NotificationUtils.class.getSimpleName();
    public static final int NOTIFICATION_ID = 100;
//    private Context mContext;
//    private DataTextChat mDataTextChat;

    private NotificationUtils() {
//        this.mContext = mContext;
//        this.mDataTextChat = mDataTextChat;
    }


    public static void clearAllNotification(Context mContext) {
        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancelAll();
    }

    public static void clearNotificationWithId(Context mContext,String itemId) {
        NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int id = NOTIFICATION_ID;
        try {
            id = Integer.parseInt(itemId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        notificationManager.cancel(id);
    }

    public static String getNotificationBodyAsPerMessageType(DataTextChat mDataTextChat) {
        String notificatioBody = "";
        if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.CONTACT_TYPE))
            notificatioBody = ConstantChat.CONTACT_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.LOCATION_TYPE))
            notificatioBody = ConstantChat.LOCATION_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.IMAGE_TYPE))
            notificatioBody = ConstantChat.IMAGE_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.IMAGECAPTION_TYPE))
            notificatioBody = ConstantChat.IMAGECAPTION_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.VIDEO_TYPE))
            notificatioBody = ConstantChat.VIDEO_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.YOUTUBE_TYPE))
            notificatioBody = ConstantChat.YOUTUBE_TYPE + " Video";
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.YAHOO_TYPE))
            notificatioBody = ConstantChat.YAHOO_TYPE + " News";
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.SKETCH_TYPE))
            notificatioBody = ConstantChat.SKETCH_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.FIRST_TIME_STICKER_TYPE))
            notificatioBody = ConstantChat.FIRST_TIME_STICKER_LBL;
        else {
            notificatioBody = CommonMethods.getUTFDecodedString(
                    TextUtils.isEmpty(mDataTextChat.getStrTranslatedText()) ?
                            mDataTextChat.getBody() : mDataTextChat.getStrTranslatedText());
        }
        return notificatioBody;
    }

    public static void showNotificationMessage(Intent intent, Context context, DataTextChat mDataTextChat) {

        DataProfile mDataProfile = new TableUserInfo(context.getApplicationContext()).getUser();

        if(mDataProfile==null){
            return;
        }
        if (mDataProfile.getIsNotificationOn().equalsIgnoreCase("0")) {
            return;
        }
        String notificatioBody = "";
        if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.CONTACT_TYPE))
            notificatioBody = ConstantChat.CONTACT_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.LOCATION_TYPE))
            notificatioBody = ConstantChat.LOCATION_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.IMAGE_TYPE))
            notificatioBody = ConstantChat.IMAGE_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.IMAGECAPTION_TYPE))
            notificatioBody = ConstantChat.IMAGECAPTION_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.VIDEO_TYPE))
            notificatioBody = ConstantChat.VIDEO_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.YOUTUBE_TYPE))
            notificatioBody = ConstantChat.YOUTUBE_TYPE + " Video";
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.YAHOO_TYPE))
            notificatioBody = ConstantChat.YAHOO_TYPE + " News";
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.SKETCH_TYPE))
            notificatioBody = ConstantChat.SKETCH_TYPE;
        else if (StringUtils.equalsIgnoreCase(mDataTextChat.getMessageType(), ConstantChat.FIRST_TIME_STICKER_TYPE))
            notificatioBody = ConstantChat.FIRST_TIME_STICKER_LBL;
        else {
            notificatioBody = CommonMethods.getUTFDecodedString(
                    TextUtils.isEmpty(mDataTextChat.getStrTranslatedText()) ?
                            mDataTextChat.getBody() : mDataTextChat.getStrTranslatedText());
        }
        // Check for empty push message
        if (TextUtils.isEmpty(notificatioBody))
            return;
//        notificatioBody = CommonMethods.getUTFDecodedString(notificatioBody);

        // notification icon
        int icon = R.mipmap.app_icon;
        int smallIcon = R.drawable.ic_vhortext_notification_icon;
        int mNotificationId = NOTIFICATION_ID;
        long when = System.currentTimeMillis();
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context.getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.setSummaryText(notificatioBody);
//        inboxStyle.setBigContentTitle("+" + mDataTextChat.getFriendPhoneNo());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context.getApplicationContext());

        String name = "";
        if (mDataTextChat.getChattype().equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            name = mDataTextChat.getGroupName();
            if(!TextUtils.isEmpty(mDataTextChat.getSenderName())){
                String senderName = CommonMethods.getUTFDecodedString(mDataTextChat.getSenderName());

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                notificatioBody = senderName+" : "+notificatioBody;
                try {
                    Spannable sb = new SpannableString(notificatioBody);
                    sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, senderName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    inboxStyle.addLine(sb);
                    mBuilder.setStyle(inboxStyle);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            icon = R.drawable.group_notification_icon;
            String groupId = mDataTextChat.getStrGroupID();
            try {
                mNotificationId = Integer.parseInt(groupId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mNotificationId = NOTIFICATION_ID;
            }
        } else {

            name = mDataTextChat.getFriendName();
            String friendMobileNoWithMCC = mDataTextChat.getFriendPhoneNo();
            try {
                mNotificationId = Integer.parseInt(friendMobileNoWithMCC);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mNotificationId = NOTIFICATION_ID;
            }
        }
        name = CommonMethods.getUTFDecodedString(name);
//        notificatioBody = CommonMethods.getUTFDecodedString(notificatioBody);
        Notification notification = mBuilder
                .setSmallIcon(smallIcon)
                .setTicker(name)
                .setWhen(when)
                .setPriority(NotificationCompat.PRIORITY_MAX)//to pop up notification
                .setAutoCancel(true)
                .setContentTitle(name)
//                .setStyle(inboxStyle)

                .setDefaults(Notification.DEFAULT_ALL | Notification.FLAG_AUTO_CANCEL)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setOnlyAlertOnce(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), icon))
                .setContentText(notificatioBody)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);

    }


    public static void showGroupEventNotificationMessage(Context context,String message) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), ActivityDash.class);
        notificationIntent.putExtra("fromNotification", "true");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        DataProfile mDataProfile = new TableUserInfo(context.getApplicationContext()).getUser();

        if(mDataProfile==null){
            return;
        }
        if (mDataProfile.getIsNotificationOn().equalsIgnoreCase("0")) {
            return;
        }
        String notificatioBody = message;
        // Check for empty push message
        if (TextUtils.isEmpty(notificatioBody))
            return;
//        notificatioBody = CommonMethods.getUTFDecodedString(notificatioBody);

        // notification icon
        int icon = R.drawable.group_notification_icon;
        int smallIcon = R.drawable.ic_vhortext_notification_icon;
        int mNotificationId = NOTIFICATION_ID;
        long when = System.currentTimeMillis();
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context.getApplicationContext(),
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.setSummaryText(notificatioBody);
//        inboxStyle.setBigContentTitle("+" + mDataTextChat.getFriendPhoneNo());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context.getApplicationContext());

        String name = "Push";
        name = CommonMethods.getUTFDecodedString(name);
//        notificatioBody = CommonMethods.getUTFDecodedString(notificatioBody);
        Notification notification = mBuilder
                .setSmallIcon(smallIcon)
                .setTicker(name)
                .setWhen(when)
                .setPriority(NotificationCompat.PRIORITY_MAX)//to pop up notification
                .setAutoCancel(true)
                .setContentTitle(name)
//                .setStyle(inboxStyle)

                .setDefaults(Notification.DEFAULT_ALL | Notification.FLAG_AUTO_CANCEL)
                .setContentIntent(resultPendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setOnlyAlertOnce(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getApplicationContext().getResources(), icon))
                .setContentText(notificatioBody)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, notification);
    }
}
