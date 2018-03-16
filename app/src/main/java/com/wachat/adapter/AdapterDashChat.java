package com.wachat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.callBack.CallbackFromDialog;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.chatUtils.ConversationEmailTxtComposer;
import com.wachat.chatUtils.SelectFile;
import com.wachat.data.DataContact;
import com.wachat.data.DataProfile;
import com.wachat.data.DataTextChat;
import com.wachat.dialog.DialogDelete;
import com.wachat.dialog.DialogMore;
import com.wachat.storage.TableChat;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;
import com.wachat.util.LogUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 20-08-2015.
 */
public class AdapterDashChat extends BaseAdapter implements View.OnClickListener, CallbackFromDialog {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(AdapterDashChat.class);
    private final DataProfile mDataProfile;
    private Context mContext;
    private ImageLoader mImageLoader;
    private AdapterCallback mAdapterCallback;
    private ArrayList<DataTextChat> mArrayListDataTextChat;
    private DisplayImageOptions options;
    private SwipeListView swipeList;
    int pos;

    public AdapterDashChat(SwipeListView swipeList,Context mContext, ArrayList<DataTextChat> mArrayListDataTextChat, AdapterCallback mAdapterCallback) {
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true).build();
        mImageLoader = ImageLoader.getInstance();
        this.mArrayListDataTextChat = mArrayListDataTextChat;
        this.mAdapterCallback = mAdapterCallback;
        mDataProfile = new TableUserInfo(mContext).getUser();
        this.swipeList = swipeList;

    }

    @Override
    public int getCount() {
        return mArrayListDataTextChat.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder mHolder = null;
        if (convertView == null) {
            convertView = (LayoutInflater.from(mContext)).inflate(R.layout.inflater_chat_screen, parent, false);
            mHolder = new Holder();
            mHolder.lnr_del = ((LinearLayout) convertView.findViewById(R.id.lnr_more));
            mHolder.lnr_more = ((LinearLayout) convertView.findViewById(R.id.lnr_del));
            mHolder.lnrContainer = (LinearLayout) convertView.findViewById(R.id.lnrContainer);
            mHolder.iv_Favourie = (ImageView) convertView.findViewById(R.id.iv_Favourie);
            mHolder.tv_ProfileName = (TextView) convertView.findViewById(R.id.tv_ProfileName);
            mHolder.tv_Status = (TextView) convertView.findViewById(R.id.tv_Status);
            mHolder.iv_status = (ImageView) convertView.findViewById(R.id.iv_status);
            mHolder.iv_ProfileImage = (ImageView) convertView.findViewById(R.id.iv_ProfileImage);
            mHolder.tv_receive_time = (TextView) convertView.findViewById(R.id.tv_receive_time);
            mHolder.tv_Chat_count = (TextView) convertView.findViewById(R.id.tv_Chat_count);
            mHolder.iv_Favourie.setTag(position);
            mHolder.lnrContainer.setTag(position);
            mHolder.lnr_more.setTag(position);
            mHolder.lnr_del.setTag(position);


            // mHolder.iv_Favourie.setOnClickListener(this);
            convertView.setTag(mHolder);
        } else {
            mHolder = (Holder) convertView.getTag();
        }

        mHolder.lnr_del.setOnClickListener(this);
        mHolder.lnr_more.setOnClickListener(this);


        DataTextChat mDataTextChat = mArrayListDataTextChat.get(position);

        try {
//            String name = (TextUtils.isEmpty(mDataTextChat.getAppName())) ? (TextUtils.isEmpty(mDataTextChat.getName()) ?
//                    "+" + mDataTextChat.getFriendPhoneNo() : mDataTextChat.getName()) : mDataTextChat.getAppName();
            mHolder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getFriendName()));

        } catch (Exception e) {
        }


        //if (!TextUtils.isEmpty(mDataTextChat.getBody()))
        switch (mDataTextChat.getMessageType()) {
            case ConstantChat.MESSAGE_TYPE:
                if (!StringUtils.isEmpty(mDataTextChat.getStrTranslatedText()))
                    mHolder.tv_Status.setText(mDataTextChat.getStrTranslatedText());
                else
                    mHolder.tv_Status.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getBody()));
                break;
            case ConstantChat.CONTACT_TYPE:
                mHolder.tv_Status.setText(ConstantChat.CONTACT_TYPE);
                break;
            case ConstantChat.LOCATION_TYPE:
                mHolder.tv_Status.setText(ConstantChat.LOCATION_TYPE);
                break;
            case ConstantChat.IMAGE_TYPE:
                mHolder.tv_Status.setText(ConstantChat.IMAGE_TYPE);
                break;
            case ConstantChat.IMAGECAPTION_TYPE:
                mHolder.tv_Status.setText(ConstantChat.IMAGE_TYPE);
                break;
            case ConstantChat.VIDEO_TYPE:
                mHolder.tv_Status.setText(ConstantChat.VIDEO_TYPE);
                break;
            case ConstantChat.YOUTUBE_TYPE:
                mHolder.tv_Status.setText(ConstantChat.YOUTUBE_TYPE + " Video");
                break;
            case ConstantChat.YAHOO_TYPE:
                mHolder.tv_Status.setText(ConstantChat.YAHOO_TYPE + " News");
                break;
            case ConstantChat.SKETCH_TYPE:
                mHolder.tv_Status.setText(ConstantChat.SKETCH_TYPE);
                break;
            case ConstantChat.TYPE_CLEAR_CONVERSATION:
                mHolder.tv_Status.setText("");
                break;
            case ConstantChat.FIRST_TIME_STICKER_TYPE:
                mHolder.tv_Status.setText(ConstantChat.FIRST_TIME_STICKER_LBL);
                break;
            default:
                break;
        }


//        if (!TextUtils.isEmpty(mDataTextChat.getAppFriendImageLink())) {
//            Picasso.with(mContext).load(mDataTextChat.getAppFriendImageLink())
//                    .into(mHolder.iv_ProfileImage);
        mImageLoader.displayImage(mDataTextChat.getAppFriendImageLink(), mHolder.iv_ProfileImage, options);

//        } else {
//            mHolder.iv_ProfileImage.setImageResource(R.drawable.ic_chats_noimage_profile);
//        }
        mHolder.iv_Favourie.setSelected((mDataTextChat.getIsFavorite() > 0) ? true : false);
        if (!TextUtils.isEmpty(mDataTextChat.getTimestamp())) {

            String sendOrReceive = "";
            if (mDataTextChat.getSenderChatID().equalsIgnoreCase(mDataProfile.getChatId())) {
                sendOrReceive = mContext.getResources().getString(R.string.sent_at);
            } else {
                sendOrReceive = mContext.getResources().getString(R.string.receive_at);
            }
            String lastMsg = DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                    "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss",
                    mContext.getResources().getConfiguration().locale);
            String dayDiff = "";
            //calculation for today and yesterday
//            dayDiff = CommonMethods.getTodayorYesterDay(
//                    mDataTextChat.getTimestamp()
//                    , mContext);
            long thenTime = 0;
            thenTime = Long.parseLong(DateTimeUtil.
                    convertFormattedDateToMillisecond(mDataTextChat.getTimestamp(), "yyyy-MM-dd HH:mm:ss"));
            if (DateUtils.isToday(thenTime)) {
                dayDiff = "Today";
            } else {

            }
            //if not today then show entire date time
            if (TextUtils.isEmpty(dayDiff)) {
//                mHolder.tv_receive_time.setText(sendOrReceive
//                        + " " + DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(), "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy hh:mm a",
//                        mContext.getResources().getConfiguration().locale));

                String date_time = DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                        "yyyy-MM-dd HH:mm:ss", "MMMM d, yyyy hh:mm a",
                        mContext.getResources().getConfiguration().locale);
                String[] dateTimeArray = StringUtils.split(date_time, " ");
                if (dateTimeArray != null && dateTimeArray.length >=5) {
                    String date = dateTimeArray[0]+" "+dateTimeArray[1]+" "+dateTimeArray[2];
                    String time = dateTimeArray[3];

                    String am_pm = dateTimeArray[4];
                    mHolder.tv_receive_time.setText(sendOrReceive
                            + " " + date + " at " + time + " " + am_pm);
                } else {
                    mHolder.tv_receive_time.setText(sendOrReceive
                            + " at " + DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(), "yyyy-MM-dd HH:mm:ss", "MMMM d, yyyy hh:mm a",
                            mContext.getResources().getConfiguration().locale));
                }
            } else {
                //if today show time only
                if (StringUtils.equalsIgnoreCase(dayDiff, "Today")) {
                    String date_time = DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(),
                            "yyyy-MM-dd HH:mm:ss", "MMMM d, yyyy hh:mm a",
                            mContext.getResources().getConfiguration().locale);
                    String[] dateTimeArray = StringUtils.split(date_time, " ");
                    if (dateTimeArray != null && dateTimeArray.length >=5) {
                        String date = dateTimeArray[0]+" "+dateTimeArray[1]+" "+dateTimeArray[2];
                        String time = dateTimeArray[3];
                        String am_pm = dateTimeArray[4];
                        mHolder.tv_receive_time.setText(sendOrReceive
                                + " " + dayDiff + " at " + time + " " + am_pm);
                    }
                } else if (StringUtils.equalsIgnoreCase(dayDiff, "Yesterday")) {
                    mHolder.tv_receive_time.setText(sendOrReceive
                            + " " + dayDiff);
                } else if (StringUtils.equalsIgnoreCase(dayDiff, "Next")) {
                    mHolder.tv_receive_time.setText(sendOrReceive
                            + " " +
                            DateTimeUtil.convertUtcToLocal(mDataTextChat.getTimestamp(), "yyyy-MM-dd HH:mm:ss", "MMMM d, yyyy hh:mm a",
                                    mContext.getResources().getConfiguration().locale));
                }
            }

        }

        if (/*TextUtils.isEmpty(mDataTextChat.getBody()) ||*/
                mDataTextChat.getSenderChatID().equalsIgnoreCase(mDataTextChat.getFriendChatID()))
            mHolder.iv_status.setVisibility(View.GONE);
        else
            mHolder.iv_status.setVisibility(View.VISIBLE);


//set delivery status icon
        if (mDataTextChat.getDeliveryStatus() != null)
            switch (Integer.valueOf(mDataTextChat.getDeliveryStatus())) {
                case 0:
                    mHolder.iv_status.setImageResource(R.drawable.ic_chat_clock_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
                    mHolder.tv_Status.setTypeface(null, Typeface.NORMAL);
                    mHolder.tv_Status.setTextColor(Color.DKGRAY);
                    mHolder.tv_receive_time.setTextColor(Color.DKGRAY);
//                    mHolder.tv_receive_time.setTypeface(null, Typeface.NORMAL);
                    break;

                case 1:
                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_sent_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
                    mHolder.tv_Status.setTextColor(Color.DKGRAY);
                    mHolder.tv_receive_time.setTextColor(Color.DKGRAY);
                    mHolder.tv_Status.setTypeface(null, Typeface.NORMAL);
//                    mHolder.tv_receive_time.setTypeface(null, Typeface.NORMAL);
                    break;

                case 2:
                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_unread_icon);
                    if (mDataTextChat.getSenderChatID().equalsIgnoreCase(mDataTextChat.getFriendChatID())) {
//                        CommonMethods.setCustomFontRegular(mContext, mHolder.tv_Status);
                        mHolder.tv_Status.setTypeface(null, Typeface.NORMAL);
                        mHolder.tv_receive_time.setTextColor(mContext.getResources().getColor(R.color.chat_text_color));
                        mHolder.tv_Status.setTextColor(mContext.getResources().getColor(R.color.chat_text_color));
//                        mHolder.tv_receive_time.setTypeface(null, Typeface.BOLD);
                    }
                    break;

                case 3:
                    mHolder.iv_status.setImageResource(R.drawable.ic_chats_seen_icon);
//                    CommonMethods.setCustomFontLight(mContext, mHolder.tv_Status);
                    mHolder.tv_Status.setTypeface(null, Typeface.NORMAL);
                    mHolder.tv_Status.setTextColor(Color.DKGRAY);
                    mHolder.tv_receive_time.setTextColor(Color.DKGRAY);
//                    mHolder.tv_receive_time.setTypeface(null, Typeface.NORMAL);
                    break;
            }

        if(mDataTextChat.unreadCount>0){
            mHolder.tv_Chat_count.setVisibility(View.VISIBLE);
            mHolder.tv_Chat_count.setText(String.valueOf(mDataTextChat.unreadCount));
        }else{
            mHolder.tv_Chat_count.setVisibility(View.GONE);
            mHolder.tv_Chat_count.setText(String.valueOf(mDataTextChat.unreadCount));
        }

        mHolder.pos = position;

        mHolder.lnrContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrayListDataTextChat.size() > 0) {
                    mAdapterCallback.OnClickPerformed(mArrayListDataTextChat.get(position));
                }
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lnrContainer:
                pos = (Integer) v.getTag();
                LogUtils.i(LOG_TAG, String.valueOf(pos));
                if (mArrayListDataTextChat.size() > 0) {
                    mAdapterCallback.OnClickPerformed(mArrayListDataTextChat.get(pos));
                }
                break;

            case R.id.lnr_more:
                pos = (Integer) v.getTag();
                LogUtils.i(LOG_TAG, String.valueOf(pos));
                DialogMore mDialogMore = new DialogMore(mContext, mArrayListDataTextChat.get(pos), this);
                mDialogMore.show();
                break;

            case R.id.lnr_del:
                pos = (Integer) v.getTag();
                LogUtils.i(LOG_TAG, String.valueOf(pos));
                DialogDelete mDialogDelete = new DialogDelete(mContext, this);
                mDialogDelete.show();
                break;
//            case R.id.iv_Favourie:
//                int position = (Integer) v.getTag();
//                if (v.isSelected()) {
//                    v.setSelected(false);
//                } else
//                    v.setSelected(true);
//                break;
            default:
                break;
        }

    }


    private ArrayList<DataTextChat> getConvertationListFromDB() {
        return new TableChat(mContext).getConversationList();
    }

    @Override
    public void OnClickEmail() {

        ArrayList<DataTextChat> mArrDataChat = new TableChat(mContext).getEmailConversation(mArrayListDataTextChat.get(pos).getFriendChatID());
        String chatConverstion = new ConversationEmailTxtComposer(mContext).getOneToOneChatConversation(mArrDataChat, mArrayListDataTextChat.get(pos).getFriendChatID());
        LogUtils.i(LOG_TAG, "email convertion: " + chatConverstion);

        sendConversationEmail(new TableContact(mContext).
                getFriendDetailsByFrienChatID(mArrayListDataTextChat.get(pos).getFriendChatID()), chatConverstion);

    }

    private void sendConversationEmail(DataContact mDataContact, String chatConverstion) {

        String friendName = (mDataContact.getAppName().equals("")) ? ((mDataContact.getName().equals("")) ? mDataContact.getPhoneNumber() : mDataContact.getName()) : mDataContact.getAppName();
        if (StringUtils.isEmpty(friendName))
            friendName = mArrayListDataTextChat.get(pos).getPhoneNumber();

        try {
            String fileNamewithExtension = mContext.getString(R.string.email_subject) + " " + friendName + ".txt";
            String filePathWithName = "";
            try {
                filePathWithName = SelectFile.mCreateAndSaveConversationEmailFile(mContext,fileNamewithExtension,
                        chatConverstion);
            } catch (Exception e) {
                e.printStackTrace();
            }
////            String pathname = Environment.getExternalStorageDirectory()
////                    .getAbsolutePath();
//            String filename = mContext.getString(R.string.email_subject) + " " + friendName + ".txt";
//            filename = CommonMethods.getUTFDecodedString(filename);
            File file = new File(filePathWithName);
            if(!file.exists()){
                ToastUtils.showAlertToast(mContext,"Failed to create email conversation file",ToastType.FAILURE_ALERT);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");

            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, fileNamewithExtension.replace(".txt", ""));
            intent.putExtra(Intent.EXTRA_EMAIL, "");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            Intent mailer = Intent.createChooser(intent, null);
            mContext.startActivity(mailer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnClickDelete() {
        new TableChat(mContext).deleteChat(mArrayListDataTextChat.get(pos).getFriendChatID());
        mArrayListDataTextChat = getConvertationListFromDB();
        refreshConversationList(mArrayListDataTextChat);
    }

    @Override
    public void OnClickBlock() {
        mArrayListDataTextChat = getConvertationListFromDB();
        refreshConversationList(mArrayListDataTextChat);
    }

    @Override
    public void OnClickClearConversation() {

        new TableChat(mContext).clearConversation(mArrayListDataTextChat.get(pos));
        mArrayListDataTextChat = getConvertationListFromDB();
        refreshConversationList(mArrayListDataTextChat);
        ToastUtils.showAlertToast(mContext, mContext.getResources().getString(R.string.clear_conv), ToastType.SUCESS_ALERT);

    }


    public static class Holder {
        private int pos;
        private LinearLayout lnr_more, lnr_del, lnrContainer;
        private ImageView iv_Favourie, iv_status, iv_ProfileImage;
        private TextView tv_ProfileName, tv_Status, tv_receive_time,tv_Chat_count;

    }

    public void refreshConversationList(ArrayList<DataTextChat> mArrayListDataTextChat) {
        swipeList.closeOpenedItems();
        this.mArrayListDataTextChat = mArrayListDataTextChat;
        this.notifyDataSetChanged();

    }
}
