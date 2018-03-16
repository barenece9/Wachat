package com.wachat.runnables;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataTextChat;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;

public class RunnableProcessVideoChat implements Runnable {

    private String selectedVideoPath;
    private Handler uiHandler;
    private Context mContext;
    private String myChatId, friendChatId, myPhoneNum, friendPhoneNum;
    private String userId = "";
    private String chatType = "", groupId = "";
    private String groupJId  = "";
    private String userName = "";
    public RunnableProcessVideoChat(Context mContext,String chatType,
                                    String groupId,String groupJId,
                                    String userId,String userName,
                                    String myChatId,
                                    String friendChatId, String myPhoneNum,
                                    String friendPhoneNum, String selectedVideoPath, Handler uiHandler) {
        super();
        this.userId = userId;
        this.myChatId = myChatId;
        this.friendChatId = friendChatId;
        this.myPhoneNum = myPhoneNum;
        this.friendPhoneNum = friendPhoneNum;
        this.selectedVideoPath = selectedVideoPath;
        this.uiHandler = uiHandler;
        this.mContext = mContext;
        this.chatType = chatType;
        this.groupId = groupId;
        this.groupJId = groupJId;
        this.userName = userName;
    }

    @Override
    public void run() {

        DataTextChat mDataVideoChat = new DataTextChat();
        mDataVideoChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
        mDataVideoChat.setOnline(ConstantChat.ISONLINE);
        mDataVideoChat.setMessageType(ConstantChat.VIDEO_TYPE);

//        mDataImageChat.setBody(CommonMethods.getUTFEncodedString(mDataShareImage.getCaption()));
        mDataVideoChat.setLang(TextUtils.isEmpty(new TableUserInfo(mContext).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(mContext).getUser().getLanguageIdentifire());
        mDataVideoChat.setSenderChatID(myChatId);
        mDataVideoChat.setFriendChatID(friendChatId);
        mDataVideoChat.setFriendPhoneNo(myPhoneNum);
        mDataVideoChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
        mDataVideoChat.setChattype(chatType);
        mDataVideoChat.setStrGroupID(groupId);
        mDataVideoChat.setSenderName(userName);
        mDataVideoChat.setGroupJID(groupJId);
        mDataVideoChat.setUserID(userId);

//        String filePath = CompressImageUtils.compressImage(mContext, mDataShareImage.getImgUrl());
        mDataVideoChat.setFilePath(selectedVideoPath);

//                Toast.makeText(mContext, "image selected : " +filePath, Toast.LENGTH_SHORT).show();

        Bitmap thumb = MediaUtils.getVideoThumbnail(selectedVideoPath);
        mDataVideoChat.setThumbFilePath(CommonMethods.saveThumbBitmap(thumb, selectedVideoPath, mContext));
        mDataVideoChat.setThumbBase64(CommonMethods.getThumbBase64(thumb));

        if (uiHandler != null) {
            Message message = uiHandler.obtainMessage();
            message.what = 1;
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Constants.B_RESULT, mDataVideoChat);
            message.setData(mBundle);
            uiHandler.sendMessage(message);
        }


    }


}
