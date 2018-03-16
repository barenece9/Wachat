package com.wachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.ChatTextView;
import com.wachat.data.DataGroup;
import com.wachat.data.DataProfile;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;
import com.wachat.util.Prefs;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 20-08-2015.
 */
public class AdapterDashGroup extends RecyclerView.Adapter<AdapterDashGroup.ViewHolder> implements View.OnClickListener {

    private final DataProfile mDataProfile;
    private Context mContext;
    private ImageLoader mImageLoader;
    private AdapterCallback mCallback;
    private ArrayList<DataGroup> mListGroup;
    private String /*imageUrl = "",*/ usrId = "";
    private DisplayImageOptions options;


    public AdapterDashGroup(Context mContext, ImageLoader mImageLoader, ArrayList<DataGroup> mListGroup, AdapterCallback mCallback) {
        this.mContext = mContext;
        this.mImageLoader = mImageLoader;
        this.mListGroup = mListGroup;
        this.usrId = new TableUserInfo(mContext).getUser().getUserId();
        this.mCallback = mCallback;
        options = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_new_group_icon).
                showImageOnFail(R.drawable.ic_new_group_icon).
                cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.ic_new_group_icon)
                .considerExifParams(true)
                .build();

        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        mDataProfile = new TableUserInfo(mContext).getUser();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.inflater_group_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataGroup mDataGroup = mListGroup.get(position);
        String groupName = mDataGroup.getGroupName();
        holder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(groupName));
        holder.tv_ProfileName.setSelected(true);
        String meberStatus = "";
        for (int i = 0; i < mDataGroup.getMemberdetails().size(); i++) {
            if (usrId.equals(mDataGroup.getMemberdetails().get(i).getUserId())) {
                if (mDataGroup.getMemberdetails().get(i).getIsGrpadmin().equals("1")) {
                    meberStatus = mContext.getResources().getString(R.string.text_admin);
                } else {
                    meberStatus = mContext.getResources().getString(R.string.text_member);
                }
            }

        }
        holder.tv_member_Status.setText(meberStatus);

        String time = "";
        time = getTimeString(mDataGroup);
        holder.tv_receive_time.setText(time);

        String status = "";
        status = getStatusString(mDataGroup);
        holder.tv_Status.setText(CommonMethods.getUTFDecodedString(status));
//        holder.tv_Status.setSelected(true);
        if (mDataGroup.unreadCount > 0) {
            holder.tv_Chat_count.setVisibility(View.VISIBLE);
            holder.tv_Chat_count.setText(String.valueOf(mDataGroup.unreadCount));
        } else {
            holder.tv_Chat_count.setVisibility(View.GONE);
            holder.tv_Chat_count.setText(String.valueOf(mDataGroup.unreadCount));
        }
        // Create configuration for ImageLoader (all options are optional)
        //Picasso.with(mContext).load(mDataGroup.getGroupImage()).placeholder(R.drawable.ic_new_group_icon).into(holder.iv_ProfileImage);


        setUpReadUnReadStatus();


        mImageLoader.displayImage(mDataGroup.getGroupImage(), holder.iv_ProfileImage, options);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                if (mListGroup.size() > 0)
                    mCallback.OnClickPerformed(mListGroup.get(pos));
            }
        });
    }

    private String getStatusString(DataGroup mDataGroup) {
        String senderName = "";
        String message = "";
        if (TextUtils.isEmpty(mDataGroup.getLastMessage())) {
            if (mDataGroup.getOwnerId().equalsIgnoreCase(Prefs.getInstance(mContext).getUserId())) {
                message = "You have created this group";
            } else {
                message = "You have been added to this group";
            }
        } else {
            message = mDataGroup.getLastMessage();
            if (!TextUtils.isEmpty(mDataGroup.lastMessageType)
                    && (mDataGroup.lastMessageType.equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_ADDED)
                    || mDataGroup.lastMessageType.equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_REMOVED)
                    || mDataGroup.lastMessageType.equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_LEFT)
                    || mDataGroup.lastMessageType.equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_CREATED))) {

            } else {
                if (mDataGroup.getLastMessageSenderId().equalsIgnoreCase(mDataProfile.getUserId())) {
                    senderName = "You: ";
                } else {
                    senderName = mDataGroup.getLastMessageSenderName();
                }
            }
        }
        if (!TextUtils.isEmpty(senderName)) {
            message = senderName + ": " + message;
        }
        return message;
    }

    private String getTimeString(DataGroup mDataGroup) {
//        String relativeTime = "";
        String msgTime = "";
        String creationTime = "";
        String dayDiff = "";


        if (!TextUtils.isEmpty(mDataGroup.getLastMessage())) {
            String sendOrReceive = "";

            if (mDataGroup.getLastMessageSenderId().equalsIgnoreCase(mDataProfile.getUserId())) {
                sendOrReceive = mContext.getResources().getString(R.string.sent_at);
            } else {
                sendOrReceive = mContext.getResources().getString(R.string.receive_at);
            }
            if (!TextUtils.isEmpty(mDataGroup.getLastMessageTime())) {
                String lastMsg = DateTimeUtil.convertUtcToLocal(mDataGroup.getLastMessageTime(),
                        "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", mContext.getResources().getConfiguration().locale);
//                dayDiff = CommonMethods.getTodayorYesterDay(
//                        lastMsg
//                        , mContext);
//                dayDiff = CommonMethods.getTodayorYesterDayForLocalDate(
//                        lastMsg
//                        , mContext);

                long thenTime = 0;
                try {
                    thenTime = Long.parseLong(DateTimeUtil.
                            convertFormattedDateToMillisecond(mDataGroup.getLastMessageTime(),
                                    "yyyy-MM-dd HH:mm:ss"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (DateUtils.isToday(thenTime)) {
                    dayDiff = "Today";
                } else {

                }
                //if not today then show entire date time
                if (TextUtils.isEmpty(dayDiff)) {
//                    relativeTime = DateTimeUtil.convertUtcToLocal(lastMsg, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                            mContext.getResources().getConfiguration().locale);
//                    String inputdateTimeFormat = "yyyy-MM-dd HH:mm:ss";
//                    String outputdateTimeFormat = "MMMM d, yyyy hh:mm a";
                    String dateTime = DateTimeUtil.getFormattedDate(lastMsg, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(dateTime, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        String time = dateTimeArray[3];

                        String am_pm = dateTimeArray[4];
                        msgTime = sendOrReceive + " " + date + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                    }
                } else {
                    //if today show time only
                    if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
//                        String date_time = DateTimeUtil.convertUtcToLocal(mDataGroup.getLastMessageTime(), "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                                mContext.getResources().getConfiguration().locale);
                        String date_time = DateTimeUtil.getFormattedDate(lastMsg, "yyyy-MM-dd HH:mm:ss"
                                , "MMMM d, yyyy hh:mm a");

                        String[] dateTimeArray = StringUtils.split(date_time, " ");

                        if (dateTimeArray != null && dateTimeArray.length >= 5) {
                            String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                            String time = dateTimeArray[3];

                            String am_pm = dateTimeArray[4];
                            msgTime = sendOrReceive
                                    + " " + dayDiff + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                        }
                    } else {
//                        relativeTime = /*DateTimeUtil.convertUtcToLocal(lastMsg, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                                mContext.getResources().getConfiguration().locale);*/lastMsg;
//                        relativeTime = DateTimeUtil.getFormattedDate(lastMsg);
                        String date_time = DateTimeUtil.getFormattedDate(lastMsg, "yyyy-MM-dd HH:mm:ss"
                                , "MMMM d, yyyy hh:mm a");
                        String[] dateTimeArray = StringUtils.split(date_time, " ");
                        if (dateTimeArray != null && dateTimeArray.length >= 5) {
                            String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                            String time = dateTimeArray[3];

                            String am_pm = dateTimeArray[4];
                            msgTime = sendOrReceive + " " + date + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                        }
                    }
                }
//                msgTime = sendOrReceive + " " + relativeTime;


            }
        } else {
            if (!TextUtils.isEmpty(mDataGroup.getCreationdateTime()) &&
                    TextUtils.isDigitsOnly(mDataGroup.getCreationdateTime())) {
                creationTime = DateTimeUtil.convertUtcToLocal(
                        DateTimeUtil.getUTCDateFromMilliSecond(mDataGroup.getCreationdateTime(),
                                "yyyy-MM-dd HH:mm:ss"),
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd HH:mm:ss",
                        mContext.getResources().getConfiguration().locale);

            } else {
                creationTime = DateTimeUtil.convertUtcToLocal(mDataGroup.getCreationdateTime(), "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd HH:mm:ss",
                        mContext.getResources().getConfiguration().locale);
            }


//            dayDiff = CommonMethods.getTodayorYesterDayForLocalDate(
//                    creationTime
//                    , mContext);

            long thenTime = 0;
            try {
                thenTime = Long.parseLong(DateTimeUtil.
                        convertFormattedDateToMillisecond(creationTime, "yyyy-MM-dd HH:mm:ss"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }
            //if not today then show entire date time
            if (TextUtils.isEmpty(dayDiff)) {
//                relativeTime = DateTimeUtil.convertUtcToLocal(creationTime, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                        mContext.getResources().getConfiguration().locale);
//                relativeTime = DateTimeUtil.getFormattedDate(creationTime);
                String date_time = DateTimeUtil.getFormattedDate(creationTime, "yyyy-MM-dd HH:mm:ss"
                        , "MMMM d, yyyy hh:mm a");
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >= 5) {
                    String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                    String time = dateTimeArray[3];

                    String am_pm = dateTimeArray[4];
                    msgTime = "Created on " + " " + date + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                }
//                msgTime = "Created on " + relativeTime;

            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
//                    String date_time = DateTimeUtil.convertUtcToLocal(creationTime, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                            mContext.getResources().getConfiguration().locale);
                    String date_time = DateTimeUtil.getFormattedDate(creationTime, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        String time = dateTimeArray[3];

                        String am_pm = dateTimeArray[4];
                        msgTime = "Created on " + " " + dayDiff + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                    }
                } else {
//                    relativeTime = DateTimeUtil.convertUtcToLocal(creationTime, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                            mContext.getResources().getConfiguration().locale);
//                    relativeTime = DateTimeUtil.getFormattedDate(creationTime);
                    String date_time = DateTimeUtil.getFormattedDate(creationTime, "yyyy-MM-dd HH:mm:ss"
                            , "MMMM d, yyyy hh:mm a");
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >= 5) {
                        String date = dateTimeArray[0] + " " + dateTimeArray[1] + " " + dateTimeArray[2];
                        String time = dateTimeArray[3];

                        String am_pm = dateTimeArray[4];
                        msgTime = "Created on " + " " + date + " at " + time + " " + am_pm;
//                        mHolder.tv_receive_time.setText(sendOrReceive
//                                + " " + dayDiff + " at " + time + " " + am_pm);
                    }
                }
            }
//            msgTime = "Created on " + relativeTime;
        }
        return msgTime;
    }

    private void setUpReadUnReadStatus() {
        //For read/unread message state
//        if (mDataTextChat.getDeliveryStatus() != null) {
//            switch (Integer.valueOf(mDataTextChat.getDeliveryStatus())) {
//                case 0:
//                    mHolder.iv_status.setImageResource(R.drawable.ic_chat_clock_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
//                    break;
//
//                case 1:
//                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_sent_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
//                    break;
//
//                case 2:
//                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_unread_icon);
//                    if (mDataTextChat.getSenderChatID().equalsIgnoreCase(mDataTextChat.getFriendChatID())) {
//                        CommonMethods.setCustomFontRegular(mContext, mHolder.tv_Status);
//                    }
//                    break;
//
//                case 3:
//                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_seen_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
//                    break;
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return this.mListGroup.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    public void refreshList(ArrayList<DataGroup> mListGroup) {
        this.mListGroup = mListGroup;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EmojiconTextView tv_ProfileName;
        public TextView tv_member_Status;
        public TextView tv_receive_time;
        public ImageView iv_ProfileImage;
        public RelativeLayout rel_top;
        private ChatTextView tv_Status;
        private TextView tv_Chat_count;

        public ViewHolder(View v) {
            super(v);
            tv_ProfileName = (EmojiconTextView) v
                    .findViewById(R.id.tv_ProfileName);
            tv_member_Status = (ChatTextView) v.findViewById(R.id.tv_member_Status);
            tv_Status = (ChatTextView) v.findViewById(R.id.tv_Status);
            iv_ProfileImage = (ImageView) v.findViewById(R.id.iv_ProfileImage);
            tv_receive_time = (TextView) v.findViewById(R.id.tv_receive_time);
            rel_top = (RelativeLayout) v.findViewById(R.id.rel_top);
            tv_Chat_count = (TextView) v.findViewById(R.id.tv_Chat_count);

        }
    }
}