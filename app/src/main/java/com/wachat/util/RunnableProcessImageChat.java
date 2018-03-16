package com.wachat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataShareImage;
import com.wachat.data.DataTextChat;
import com.wachat.storage.TableUserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class RunnableProcessImageChat implements Runnable {

    private ArrayList<DataShareImage> selectedImageFilePathArray;
    private Handler uiHandler;
    private Context mContext;
    private String myChatId, friendChatId, myPhoneNum, friendPhoneNum;
    private String userId = "";
    private String chatType = "", groupId = "";
    private String groupJId = "";
    private String userName = "";

    public RunnableProcessImageChat(Context mContext, String chatType,
                                    String groupId, String groupJId,
                                    String userId, String userName, String myChatId,
                                    String friendChatId, String myPhoneNum,
                                    String friendPhoneNum, ArrayList<DataShareImage> selectedImageFilePathArray, Handler uiHandler) {
        super();
        this.userId = userId;
        this.myChatId = myChatId;
        this.friendChatId = friendChatId;
        this.myPhoneNum = myPhoneNum;
        this.friendPhoneNum = friendPhoneNum;
        this.selectedImageFilePathArray = selectedImageFilePathArray;
        this.uiHandler = uiHandler;
        this.mContext = mContext;
        this.chatType = chatType;
        this.groupId = groupId;
        this.groupJId = groupJId;
        this.userName = userName;
    }

    @Override
    public void run() {

        if (selectedImageFilePathArray != null) {
            for (DataShareImage mDataShareImage : selectedImageFilePathArray) {
                DataTextChat mDataImageChat = new DataTextChat();
                mDataImageChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
                mDataImageChat.setOnline(ConstantChat.ISONLINE);

                if (TextUtils.isEmpty(mDataShareImage.getCaption()))
                    mDataImageChat.setMessageType(ConstantChat.IMAGE_TYPE);
                else
                    mDataImageChat.setMessageType(ConstantChat.IMAGECAPTION_TYPE);

                if (mDataShareImage.isSketchType())
                    mDataImageChat.setMessageType(ConstantChat.SKETCH_TYPE);
                if (mDataShareImage.isMasked()) {
                    mDataImageChat.setIsMasked("1");
                    mDataImageChat.setMaskEnabled("1");
                }

                mDataImageChat.setBody(CommonMethods.getUTFEncodedString(mDataShareImage.getCaption()));
                mDataImageChat.setLang(TextUtils.isEmpty(new TableUserInfo(mContext).getUser().getLanguageIdentifire()) ? "en" : new TableUserInfo(mContext).getUser().getLanguageIdentifire());
                mDataImageChat.setSenderChatID(myChatId);
                mDataImageChat.setFriendChatID(friendChatId);
                mDataImageChat.setFriendPhoneNo(myPhoneNum);
                mDataImageChat.setTimestamp(CommonMethods.getDateTime(String.valueOf(System.currentTimeMillis())));
                mDataImageChat.setSenderName(userName);
                mDataImageChat.setChattype(chatType);
                mDataImageChat.setStrGroupID(groupId);

                mDataImageChat.setGroupJID(groupJId);
                mDataImageChat.setUserID(userId);

                String filePath = "";
                //try with original image - not compressing it
                filePath = mDataShareImage.getImgUrl();
//                try {
//                    filePath = CompressImageUtils.compressImage(mContext, mDataShareImage.getImgUrl());
//                } catch (OutOfMemoryError e) {
//                    e.printStackTrace();
//                    filePath = mDataShareImage.getImgUrl();
//                }

                try {
                    File imageFile = new File(filePath) ;

//                    if(!imageFile.exists()){
//                        try {
//                            imageFile.createNewFile();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }else{
//                        imageFile.delete();
//                        try {
//                            imageFile.createNewFile();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap data = BitmapFactory.decodeFile(filePath, options);
                    Bitmap fixedRotationBitmap = MediaUtils.fixImageRotation(data, Uri.fromFile(imageFile));


                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(imageFile);
                        if (fos != null) {
                            fixedRotationBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        fixedRotationBitmap.recycle();
                        data.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDataImageChat.setFilePath(filePath);

//                Toast.makeText(mContext, "image selected : " +filePath, Toast.LENGTH_SHORT).show();

                try {
                    Bitmap thumb = CommonMethods.getImageThumbnail(filePath);
                    mDataImageChat.setThumbFilePath(CommonMethods.saveThumbBitmap(thumb, filePath, mContext));
                    mDataImageChat.setThumbBase64(CommonMethods.getThumbBase64(thumb));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (uiHandler != null) {
                    Message message = uiHandler.obtainMessage();
                    message.what = 1;
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(Constants.B_RESULT, mDataImageChat);
                    message.setData(mBundle);
                    uiHandler.sendMessage(message);
                }

            }
        }
    }


}
