package com.wachat.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.wachat.application.AppVhortex;
import com.wachat.chatUtils.ConstantChat;
import com.wachat.data.DataContact;
import com.wachat.data.DataGroup;
import com.wachat.data.DataGroupMembers;
import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.LogUtils;
import com.wachat.util.Prefs;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

/**
 * Created by Argha on 31-10-2015.
 */
public class TableChat {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(TableChat.class);

    private Context mContext;
    private AppVhortex app;

//    TableGroup tableGroup;


    public TableChat(Context mContext) {
        this.mContext = mContext;
        if (app == null)
            app = (AppVhortex) this.mContext.getApplicationContext();

//        tableGroup = new TableGroup(mContext);
    }


    public boolean updateUploadStatus(DataTextChat mDataFileChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.uploadStatus, mDataFileChat.getUploadStatus());
        count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataFileChat.getMessageid()});
        return (count > 0) ? true : false;
    }

    public boolean updateFileChatAfterUploadComplete(DataTextChat mDataFileChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.fileUrl, mDataFileChat.getFileUrl());
        mContentValues.put(ConstantDB.uploadStatus, mDataFileChat.getUploadStatus());
        count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataFileChat.getMessageid()});
        return (count > 0) ? true : false;
    }

    public boolean updateFileChatAfterDownloadComplete(DataTextChat mDataFileChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.fileLocalPath, mDataFileChat.getFilePath());
        if (!TextUtils.isEmpty(mDataFileChat.getBlurredImagePath())) {
            mContentValues.put(ConstantDB.maskImagePath, mDataFileChat.getBlurredImagePath());
        }
        count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataFileChat.getMessageid()});
        return (count > 0) ? true : false;
    }

    public boolean updateFileChatAfterBlurComplete(DataTextChat mDataFileChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        if (!TextUtils.isEmpty(mDataFileChat.getBlurredImagePath())) {
            mContentValues.put(ConstantDB.maskImagePath, mDataFileChat.getBlurredImagePath());
            count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataFileChat.getMessageid()});
            return (count > 0) ? true : false;
        }
        return false;
    }

    public boolean updateFileChatMaskEnabled(DataTextChat mDataFileChat) {
        int count = 0;
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(ConstantDB.maskEnabled, mDataFileChat.getMaskEnabled());
        count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataFileChat.getMessageid()});
        return (count > 0) ? true : false;
    }

    public boolean checkIfMessageExists(String messageId) {
        boolean status = false;
        String SQL = "SELECT " + ConstantDB.messageID + " FROM " +
                ConstantDB.TableChat + " WHERE " + ConstantDB.messageID + " LIKE '%" + messageId + "%'";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return status;
    }

    public synchronized boolean insertChat(DataTextChat mDataTextChat) {
        boolean isSuccess = false;

        String sqlQuery = "INSERT OR REPLACE INTO " + ConstantDB.TableChat + " ( "
                + ConstantDB.messageID + ","
                + ConstantDB.messageBody + ","
                + ConstantDB.senderChatID + ","
                + ConstantDB.chatType + ","
                + ConstantDB.messageType + ","
                + ConstantDB.strGroupID + ","
                + ConstantDB.messageDateTime + ","
                + ConstantDB.deliveryStatus + ","
                + ConstantDB.strAttachmentID + ","
                + ConstantDB.strTranslatedText + ","
                + ConstantDB.friendPhoneNo + ","
                + ConstantDB.friendChatID + ","
                + ConstantDB.userID + ","
                + ConstantDB.attachContactName + ","
                + ConstantDB.attachContactNo + ","
                + ConstantDB.attachBase64str4Img + ","
                + ConstantDB.loc_lat + ","
                + ConstantDB.loc_long + ","
                + ConstantDB.loc_address + ","
                + ConstantDB.fileLocalPath + ","

                + ConstantDB.fileUrl + ","
                + ConstantDB.thumbBase64 + ","
                + ConstantDB.thumbFilePath + ","
                + ConstantDB.downloadStatus + ","
                + ConstantDB.uploadStatus + ","
                + ConstantDB.youtubeTitle + ","
                + ConstantDB.youtubeDesc + ","
                + ConstantDB.youtubePublishTime + ","
                + ConstantDB.youtubeThumbUrl + ","
                + ConstantDB.youtubeVidId + ","
                + ConstantDB.isMasked + ","
                + ConstantDB.maskEnabled + ","
                + ConstantDB.yahooTitle + ","
                + ConstantDB.yahooDesc + ","
                + ConstantDB.yahooPublishTime + ","
                + ConstantDB.yahooShareLink + ","
                + ConstantDB.yahooImageUrl + ","
                + ConstantDB.friend_name + ","
                + ConstantDB.sender_id + ","
                + ConstantDB.sender_name + ","
                + ConstantDB.FriendId + ","
                + ConstantDB.maskImagePath +
                " ) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            app.mDbHelper.getDB().beginTransactionNonExclusive();
            SQLiteStatement stmt = app.mDbHelper.getDB().compileStatement(sqlQuery);

            stmt.bindString(1, mDataTextChat.getMessageid());
            stmt.bindString(2, mDataTextChat.getBody());
            stmt.bindString(3, mDataTextChat.getSenderChatID());
            stmt.bindString(4, mDataTextChat.getChattype());
            stmt.bindString(5, mDataTextChat.getMessageType());
            stmt.bindString(6, mDataTextChat.getStrGroupID());
            stmt.bindString(7, mDataTextChat.getTimestamp().trim());
            stmt.bindLong(8, Long.parseLong(mDataTextChat.getDeliveryStatus()));
            stmt.bindString(9, mDataTextChat.getStrAttachmentID());
            stmt.bindString(10, mDataTextChat.getStrTranslatedText());
            stmt.bindString(11, mDataTextChat.getFriendPhoneNo());
            stmt.bindString(12, mDataTextChat.getFriendChatID());
            stmt.bindString(13, mDataTextChat.getUserID());
            stmt.bindString(14, mDataTextChat.getAttachContactName());
            stmt.bindString(15, mDataTextChat.getAttachContactNo());
            stmt.bindString(16, mDataTextChat.getAttachBase64str4Img());
            stmt.bindString(17, mDataTextChat.getLoc_lat());
            stmt.bindString(18, mDataTextChat.getLoc_long());
            stmt.bindString(19, mDataTextChat.getLoc_address());
            stmt.bindString(20, mDataTextChat.getFilePath());
            stmt.bindString(21, mDataTextChat.getFileUrl());
            stmt.bindString(22, mDataTextChat.getThumbBase64());
            stmt.bindString(23, mDataTextChat.getThumbFilePath());
            stmt.bindString(24, mDataTextChat.getDownloadStatus());
            stmt.bindString(25, mDataTextChat.getUploadStatus());
            stmt.bindString(26, mDataTextChat.getYoutubeTitle());
            stmt.bindString(27, mDataTextChat.getYoutubeDescription());
            stmt.bindString(28, mDataTextChat.getYoutubePublishTime());
            stmt.bindString(29, mDataTextChat.getYoutubeThumbUrl());
            stmt.bindString(30, mDataTextChat.getYoutubeVideoId());
            stmt.bindString(31, mDataTextChat.getIsMasked());
            stmt.bindString(32, mDataTextChat.getMaskEnabled());
            stmt.bindString(33, mDataTextChat.getYahooTitle());
            stmt.bindString(34, mDataTextChat.getYahooDescription());
            stmt.bindString(35, mDataTextChat.getYahooPublishTime());
            stmt.bindString(36, mDataTextChat.getYahooShareUrl());
            stmt.bindString(37, mDataTextChat.getYahooImageUrl());
            stmt.bindString(38, mDataTextChat.getFriendName());
            stmt.bindString(39, mDataTextChat.getSenderId());
            stmt.bindString(40, mDataTextChat.getSenderName());
            stmt.bindString(41, mDataTextChat.getFriendId());
            stmt.bindString(42, mDataTextChat.getBlurredImagePath());
            stmt.execute();

            stmt.clearBindings();
            app.mDbHelper.getDB().setTransactionSuccessful();
            app.mDbHelper.getDB().endTransaction();
            isSuccess = true;

        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }

        return isSuccess;
    }

    public int getMyChatCountWithThisFriend(String FriendChatId, String myChatId) {
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE " + ConstantDB.FriendChatId + " = '" + FriendChatId
                + "' AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_SINGLECHAT
                + "' AND " + ConstantDB.senderChatID + " = '" + myChatId
                + "' ORDER BY  " + ConstantDB.messageDateTime + " ASC ;";


        int chatCount = 0;
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.moveToFirst()) {
                chatCount = c.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return chatCount;
    }


    public ArrayList<DataTextChat> getAllChat(String FriendChatId) {
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE "
                + ConstantDB.FriendChatId + " = '" + FriendChatId
                + "' AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_SINGLECHAT
                + "' ORDER BY  " + ConstantDB.messageDateTime + " ASC ;";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
                    if (!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                        DataTextChat mDataTextChat = new DataTextChat();
                        mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                        mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                        mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                        mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                        mDataTextChat.setMessageType(messageType);
                        mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                        mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                        mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                        mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                        mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                        mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                        mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                        mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                        mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                        mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                        mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                        mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                        mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                        mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                        mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                        mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                        mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                        mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                        mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                        mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                        mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                        mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                        mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                        mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                        mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                        mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                        mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                        mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                        mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                        mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                        mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                        mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                        mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                        mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                        mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                        mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));
                        mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                        mArrDataTextChats.add(mDataTextChat);
                    }

                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        LogUtils.i(LOG_TAG, "getAllChat");
        return mArrDataTextChats;
    }

    public ArrayList<DataTextChat> getAllGroupMediaChat(String groupId) {
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE "
                + ConstantDB.strGroupID + " = '" + groupId
                + "' AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_GROUPCHAT
                + "' AND ("
                + ConstantDB.messageType + " = '" + ConstantChat.IMAGE_TYPE + "' OR "
                + ConstantDB.messageType + " = '" + ConstantChat.IMAGECAPTION_TYPE + "' OR "
                + ConstantDB.messageType + " = '" + ConstantChat.SKETCH_TYPE + "' OR "
                + ConstantDB.messageType + " = '" + ConstantChat.VIDEO_TYPE + "')  ORDER BY  "
                + ConstantDB.messageDateTime + " ASC ;";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
                    if (!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                        DataTextChat mDataTextChat = new DataTextChat();
                        mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                        mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                        mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                        mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                        mDataTextChat.setMessageType(messageType);
                        mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                        mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                        mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                        mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                        mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                        mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                        mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                        mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                        mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                        mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                        mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                        mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                        mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                        mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                        mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                        mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                        mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                        mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                        mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                        mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                        mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                        mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                        mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                        mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                        mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                        mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                        mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                        mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                        mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                        mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                        mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                        mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                        mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                        mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                        mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                        mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                        mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));
                        mArrDataTextChats.add(mDataTextChat);
                    }

                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        LogUtils.i(LOG_TAG, "getAllGroupMediaChat");
        return mArrDataTextChats;
    }

    public ArrayList<DataTextChat> getAllGroupChat(String groupChatId) {
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE " + ConstantDB.strGroupID
                + " = '" + groupChatId + "' AND "
                + ConstantDB.chatType + " = '" + ConstantChat.TYPE_GROUPCHAT
                + "' ORDER BY  " + ConstantDB.messageDateTime + " ASC ;";

        TableGroup tableGroup = new TableGroup(mContext);
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
                    if (!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                        DataTextChat mDataTextChat = new DataTextChat();
                        mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                        mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                        mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                        mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                        mDataTextChat.setMessageType(messageType);
                        mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                        mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                        mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                        mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                        mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                        mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                        mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));

                        mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                        mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                        mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                        mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                        mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                        mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                        mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                        mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                        mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                        mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                        mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                        mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                        mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                        mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                        mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                        mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                        mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                        mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                        mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                        mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                        mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                        mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                        mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                        mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                        mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                        mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                        mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                        mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                        mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                        mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));

                        if (TextUtils.isEmpty(mDataTextChat.getSenderName())) {
                            DataGroupMembers groupMember = tableGroup.getMemberInAGroup(
                                    mDataTextChat.getStrGroupID(), mDataTextChat.getSenderId());
                            if (groupMember != null) {
                                String name = groupMember.getUserName();

                                if (!TextUtils.isEmpty(name)) {
//                                mDataTextChat.setFriendName(name);
                                    mDataTextChat.setSenderName(name);
                                }
                            }

                        }
                        mArrDataTextChats.add(mDataTextChat);
                    }

                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        LogUtils.i(LOG_TAG, "getAllGroupChat");
        return mArrDataTextChats;
    }


    public DataTextChat getLastGroupChat(String groupChatId) {
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE " + ConstantDB.strGroupID
                + " = '" + groupChatId
                + "' AND "
                + ConstantDB.chatType + " = '" + ConstantChat.TYPE_GROUPCHAT +
                "' ORDER BY  " + ConstantDB.messageDateTime + " DESC ;";

        DataTextChat mDataTextChat = null;
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
                if (!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                    mDataTextChat = new DataTextChat();
                    mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                    mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                    mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                    mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                    mDataTextChat.setMessageType(messageType);
                    mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                    mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                    mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                    mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                    mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                    mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                    mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                    mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                    mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                    mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                    mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                    mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                    mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                    mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                    mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                    mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                    mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                    mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                    mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                    mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                    mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                    mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                    mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                    mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                    mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                    mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                    mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                    mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                    mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                    mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                    mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                    mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                    mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                    mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                    mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                    mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                    mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataTextChat;
    }

    public ArrayList<DataTextChat> getConversationList() {
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
       /* String SQL = "SELECT * FROM " + ConstantDB.TableChat + " LEFT JOIN  " + ConstantDB.TableContactList + "  ON" +
                " " + ConstantDB.TableChat + "." + ConstantDB.friendChatID + "" +
                " =" + ConstantDB.TableContactList + "." + ConstantDB.ChatId + " GROUP BY " + ConstantDB.FriendChatId + " ;";*/
/*Modified in 06-01-2016*/
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE " + ConstantDB.chatType + " = '"
                + ConstantChat.TYPE_SINGLECHAT + "' GROUP BY " + ConstantDB.FriendChatId
                + " ORDER BY messageDateTime DESC;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataTextChat mDataTextChat;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
//                    if(!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                    mDataTextChat = new DataTextChat();
                    mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                    mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                    mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                    String chatType = c.getString(c.getColumnIndex(ConstantDB.chatType));
                    String groupId = c.getString(c.getColumnIndex(ConstantDB.strGroupID));
                    mDataTextChat.setChattype(chatType);
                    mDataTextChat.setStrGroupID(chatType);
//                    if(chatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)&& !TextUtils.isEmpty(groupId)){
//                        getGroupDetails(mDataTextChat);
//                    }

                    mDataTextChat.setMessageType(messageType);

                    mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                    mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                    mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                    mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                    mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                    mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                    mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                    /*from contact*/
                    getFriendDetailsByFrienChatID(mDataTextChat);
                  /*  mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataTextChat.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataTextChat.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataTextChat.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataTextChat.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataTextChat.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));*/
                    /*----------------*/
                    mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                    mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                    mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                    mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                    mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                    mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                    mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                    mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                    mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                    mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                    mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                    mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                    mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                    mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                    mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                    mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                    mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                    mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                    mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                    mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                    mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                    mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                    mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                    mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                    mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));

                    if (TextUtils.isEmpty(mDataTextChat.getAppName())) {
                        mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                    } else {
                        mDataTextChat.setFriendName(mDataTextChat.getAppName());
                    }
                    mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                    mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));

                    int unreadCount = getUnreadCountForThisItem(mDataTextChat.getFriendChatID(), mDataTextChat.getSenderChatID());
                    mDataTextChat.unreadCount = unreadCount;
                    mArrDataTextChats.add(mDataTextChat);
//                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mArrDataTextChats;
    }

    public int getUnreadCountForThisGroup(String groupId) {
        int msgCount = 0;
        String SQL = "SELECT messageID FROM "
                + ConstantDB.TableChat + " WHERE " +
                "" + ConstantDB.deliveryStatus + "  = '2' AND " + ConstantDB.chatType
                + " = '" + ConstantChat.TYPE_GROUPCHAT
                + "' AND " + ConstantDB.senderChatID + " <> '" + Prefs.getInstance(mContext).getChatId()
                + "' AND " + ConstantDB.strGroupID + " = '" + groupId + "';";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null) {
                c.moveToFirst();

                msgCount = c.getCount();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return msgCount;
    }

    private int getUnreadCountForThisItem(String friendChatId, String senderChatId) {
        int msgCount = 0;
        String SQL = "SELECT friendChatID FROM "
                + ConstantDB.TableChat + " WHERE " +
                "" + ConstantDB.deliveryStatus + "  = '2' AND " + ConstantDB.chatType
                + " = '" + ConstantChat.TYPE_SINGLECHAT
                + "' AND " + ConstantDB.senderChatID + " <> '" + Prefs.getInstance(mContext).getChatId()
                + "' AND " + ConstantDB.friendChatID + " = '" + friendChatId + "';";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null) {
                c.moveToFirst();

                msgCount = c.getCount();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return msgCount;
    }


    public void getGroupDetails(DataTextChat mDataTextChat) {
        DataGroup mDataGroup = new DataGroup();
        String SQL = "SELECT * FROM " + ConstantDB.TableGroupDetails + " WHERE " + ConstantDB.GroupId
                + " = '" + mDataTextChat.getStrGroupID() + "';";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0 && c.moveToFirst()) {

                String groupJId = c.getString(c.getColumnIndex(ConstantDB.GroupJId));

                String groupName = CommonMethods.getUTFDecodedString(c.getString(c.getColumnIndex(ConstantDB.GroupName)));
                mDataTextChat.setGroupName(groupName);
                mDataTextChat.setGroupJID(groupJId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
    }

    public DataContact getFriendDetailsByFrienChatID(DataTextChat mDataTextChat) {

        String SQL = "SELECT * FROM " + ConstantDB.TableContactList + " WHERE " +
                ConstantDB.ChatId + " ='" + mDataTextChat.getFriendChatID() + "'  ;";
        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);

            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                mDataTextChat.setPhoneNumber(c.getString(c.getColumnIndex(ConstantDB.PhoneNumber)));
                mDataTextChat.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                mDataTextChat.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                mDataTextChat.setStatus(c.getString(c.getColumnIndex(ConstantDB.Status)));
                mDataTextChat.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                mDataTextChat.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                mDataTextChat.setImageUrl(c.getString(c.getColumnIndex(ConstantDB.imageUrl)));
                mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                mDataTextChat.setChatId(c.getString(c.getColumnIndex(ConstantDB.ChatId)));
                mDataTextChat.setIsBlocked(c.getInt(c.getColumnIndex(ConstantDB.IsBlocked)));
                mDataTextChat.setGender(c.getString(c.getColumnIndex(ConstantDB.gender)));
                mDataTextChat.setIsfindbyphoneno(c.getString(c.getColumnIndex(ConstantDB.isfindbyphoneno)));
                mDataTextChat.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mDataTextChat;
    }

    public boolean updateChatDeliveryStatus(DataTextChat mDataTextChat) {
        int count = 0;
        try {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(ConstantDB.deliveryStatus, mDataTextChat.getDeliveryStatus());
            count = app.mDbHelper.getDB().update(ConstantDB.TableChat, mContentValues, ConstantDB.messageID + "=?", new String[]{mDataTextChat.getMessageid()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (count > 0) ? true : false;
    }

    public String getUnredButDeliveredMessages(String deliveryStatus, String friendChatId) {
        String unreadMessageIDs = "";
        String SQL = "SELECT GROUP_CONCAT(" + ConstantDB.messageID
                + " , ',' ) AS UNREADMESSAGEIDS FROM " + ConstantDB.TableChat + " WHERE "
                + ConstantDB.deliveryStatus + " = '" + deliveryStatus + "' AND "
                + ConstantDB.chatType + " = '" + ConstantChat.TYPE_SINGLECHAT + "' AND "
                + ConstantDB.senderChatID + " = '" + friendChatId + "' AND "
                + ConstantDB.friendChatID + " = '" + friendChatId + "';";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                unreadMessageIDs = c.getString(c.getColumnIndex("UNREADMESSAGEIDS"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        //  Log.i("chat list", unreadMessageIDs.toString());
        return unreadMessageIDs;
    }

    public String getUnredButDeliveredGroupMessages(String deliveryStatus, String groupId) {
        String unreadMessageIDs = "";
        String SQL = "SELECT GROUP_CONCAT(" + ConstantDB.messageID + " , ',' ) AS UNREADMESSAGEIDS FROM "
                + ConstantDB.TableChat + " WHERE "
                + ConstantDB.deliveryStatus + " = '" + deliveryStatus + "' AND "
                + ConstantDB.strGroupID + " = '" + groupId + "' AND "
                + ConstantDB.senderChatID + " <> '" + Prefs.getInstance(AppVhortex.getInstance().getApplicationContext()).getChatId() + "' AND "
                + ConstantDB.chatType + " = '"
                + ConstantChat.TYPE_GROUPCHAT + "';";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                unreadMessageIDs = c.getString(c.getColumnIndex("UNREADMESSAGEIDS"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        //  Log.i("chat list", unreadMessageIDs.toString());
        return unreadMessageIDs;
    }


    public ArrayList<DataTextChat> getNotSentChatMessage(String deliveryStatus) {
        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " WHERE " + ConstantDB.deliveryStatus
                + " = '" + deliveryStatus + "' ;";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            DataTextChat mDataTextChat;
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String messageType = c.getString(c.getColumnIndex(ConstantDB.messageType));
                    if (!messageType.equalsIgnoreCase(ConstantChat.TYPE_CLEAR_CONVERSATION)) {
                        mDataTextChat = new DataTextChat();
                        mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                        mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                        mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                        mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                        mDataTextChat.setMessageType(messageType);
                        mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                        mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                        mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                        mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                        mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                        mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                        mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                        mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                        mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                        mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                        mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                        mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                        mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                        mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                        mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                        mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                        mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                        mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                        mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                        mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                        mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                        mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                        mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                        mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                        mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                        mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                        mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                        mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                        mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                        mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                        mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                        mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                        mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                        mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                        mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                        mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                        mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));
                        mArrDataTextChats.add(mDataTextChat);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mArrDataTextChats;
    }

    public ArrayList<DataTextChat> getChatSections(String FriendChatId) {
        ArrayList<DataTextChat> mArrDataTxtXhat = new ArrayList<DataTextChat>();
        DataTextChat mDataTextChat;
        String SQL = "SELECT STRFTIME('%m-%d-%Y', " + ConstantDB.messageDateTime + ") AS valYear"
                + ", STRFTIME('%m-%d-%Y %H:%M:%S', " + ConstantDB.messageDateTime + ") AS valTime" +
                " ,COUNT(" + ConstantDB.messageID + ") as count  FROM " + ConstantDB.TableChat + " " +
                "where " + ConstantDB.friendChatID + " = '" + FriendChatId + "' AND "
                + ConstantDB.messageType + " <> '" + ConstantChat.TYPE_CLEAR_CONVERSATION
                + "' AND chatType='singlechat' GROUP BY valYear ORDER BY " + ConstantDB.messageDateTime + " asc";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataTextChat = new DataTextChat();
                    mDataTextChat.setSectionDate(c.getString(c.getColumnIndex("valYear")));
                    mDataTextChat.setSectionTime(c.getString(c.getColumnIndex("valTime")));
                    mDataTextChat.setSectionCount(c.getString(c.getColumnIndex("count")));

                    mArrDataTxtXhat.add(mDataTextChat);
                } while (c.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }

        return mArrDataTxtXhat;
    }


    public ArrayList<DataTextChat> getGroupChatSections(String groupChatId) {
        ArrayList<DataTextChat> mArrDataTxtXhat = new ArrayList<DataTextChat>();
        DataTextChat mDataTextChat;
        String SQL = "SELECT STRFTIME('%m-%d-%Y', " + ConstantDB.messageDateTime + ") AS valYear"
                + ", STRFTIME('%m-%d-%Y %H:%M:%S', " + ConstantDB.messageDateTime + ") AS valTime" +
                " ,COUNT(" + ConstantDB.messageID + ") as count  FROM " + ConstantDB.TableChat + " " +
                "where " + ConstantDB.strGroupID + " = '" + groupChatId + "' AND "
                + ConstantDB.messageType + " <> '" + ConstantChat.TYPE_CLEAR_CONVERSATION
                + "' AND chatType='groupchat' GROUP BY valYear ORDER BY " + ConstantDB.messageDateTime + " asc";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataTextChat = new DataTextChat();
                    mDataTextChat.setSectionDate(c.getString(c.getColumnIndex("valYear")));
                    mDataTextChat.setSectionTime(c.getString(c.getColumnIndex("valTime")));
                    mDataTextChat.setSectionCount(c.getString(c.getColumnIndex("count")));

                    mArrDataTxtXhat.add(mDataTextChat);
                } while (c.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }

        return mArrDataTxtXhat;
    }

    public void deleteChat(String friendChatID) {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableChat, ConstantDB.friendChatID
                    + "=? AND " + ConstantDB.chatType
                    + " = ?", new String[]{friendChatID, ConstantChat.TYPE_SINGLECHAT});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteGroupChat(String groupId) {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableChat, ConstantDB.strGroupID
                    + "=? AND " + ConstantDB.chatType
                    + " = ?", new String[]{groupId, ConstantChat.TYPE_GROUPCHAT});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearConversation(DataTextChat mDataTextChat) {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableChat, ConstantDB.friendChatID
                            + "=? AND " + ConstantDB.chatType
                            + " = ?",
                    new String[]{mDataTextChat.getFriendChatID(), ConstantChat.TYPE_SINGLECHAT});
        } catch (Exception e) {
            e.printStackTrace();
        }
        insertBlankChat(mDataTextChat);
    }

    private synchronized void insertBlankChat(DataTextChat mDataTextChat) {

        DataTextChat mTextChat = new DataTextChat();
        mTextChat.setMessageid(new Message().getStanzaId());
        mTextChat.setMessageType(ConstantChat.TYPE_CLEAR_CONVERSATION);
        mTextChat.setSenderChatID(mDataTextChat.getSenderChatID());
        mTextChat.setFriendPhoneNo(mDataTextChat.getFriendPhoneNo());
        mTextChat.setFriendChatID(mDataTextChat.getFriendChatID());
        mTextChat.setFriendId(mDataTextChat.getFriendId());
        mTextChat.setTimestamp(mDataTextChat.getTimestamp());
        insertChat(mTextChat);
    }


    public ArrayList<DataTextChat> getEmailConversation(String friendChatId) {

        ArrayList<DataTextChat> mArrDataTextChats = new ArrayList<DataTextChat>();
        DataTextChat mDataTextChat;
        String SQL = "SELECT * FROM " + ConstantDB.TableChat + " LEFT JOIN  "
                + ConstantDB.TableContactList + "  ON" +
                " " + ConstantDB.TableChat + "." + ConstantDB.senderChatID + "" +
                " =" + ConstantDB.TableContactList + "." + ConstantDB.ChatId + " " +
                " WHERE " + ConstantDB.friendChatID + " = '" + friendChatId
                + "'  AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_SINGLECHAT +

                "' ORDER BY " + ConstantDB.messageDateTime + "  ASC  ;";
        Cursor c = null;

        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    mDataTextChat = new DataTextChat();
                    mDataTextChat.setMessageid(c.getString(c.getColumnIndex(ConstantDB.messageID)));
                    mDataTextChat.setBody(c.getString(c.getColumnIndex(ConstantDB.messageBody)));
                    mDataTextChat.setSenderChatID(c.getString(c.getColumnIndex(ConstantDB.senderChatID)));
                    mDataTextChat.setChattype(c.getString(c.getColumnIndex(ConstantDB.chatType)));
                    mDataTextChat.setMessageType(c.getString(c.getColumnIndex(ConstantDB.messageType)));
                    mDataTextChat.setStrGroupID(c.getString(c.getColumnIndex(ConstantDB.strGroupID)));
                    mDataTextChat.setTimestamp(c.getString(c.getColumnIndex(ConstantDB.messageDateTime)));
                    mDataTextChat.setDeliveryStatus(c.getString(c.getColumnIndex(ConstantDB.deliveryStatus)));
                    mDataTextChat.setStrAttachmentID(c.getString(c.getColumnIndex(ConstantDB.strAttachmentID)));
                    mDataTextChat.setStrTranslatedText(c.getString(c.getColumnIndex(ConstantDB.strTranslatedText)));
                    mDataTextChat.setFriendPhoneNo(c.getString(c.getColumnIndex(ConstantDB.friendPhoneNo)));
                    mDataTextChat.setFriendChatID(c.getString(c.getColumnIndex(ConstantDB.friendChatID)));
                    mDataTextChat.setUserID(c.getString(c.getColumnIndex(ConstantDB.userID)));
                    mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mDataTextChat.setName(c.getString(c.getColumnIndex(ConstantDB.Name)));
                    mDataTextChat.setAppName(c.getString(c.getColumnIndex(ConstantDB.AppName)));
                    mDataTextChat.setFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.FriendImageLink)));
                    mDataTextChat.setAppFriendImageLink(c.getString(c.getColumnIndex(ConstantDB.AppFriendImageLink)));
                    mDataTextChat.setIsFavorite(c.getInt(c.getColumnIndex(ConstantDB.IsFavorite)));
                    mDataTextChat.setAttachContactName(c.getString(c.getColumnIndex(ConstantDB.attachContactName)));
                    mDataTextChat.setAttachContactNo(c.getString(c.getColumnIndex(ConstantDB.attachContactNo)));
                    mDataTextChat.setAttachBase64str4Img(c.getString(c.getColumnIndex(ConstantDB.attachBase64str4Img)));
                    mDataTextChat.setLoc_lat(c.getString(c.getColumnIndex(ConstantDB.loc_lat)));
                    mDataTextChat.setLoc_long(c.getString(c.getColumnIndex(ConstantDB.loc_long)));
                    mDataTextChat.setLoc_address(c.getString(c.getColumnIndex(ConstantDB.loc_address)));
                    mDataTextChat.setFilePath(c.getString(c.getColumnIndex(ConstantDB.fileLocalPath)));
                    mDataTextChat.setBlurredImagePath(c.getString(c.getColumnIndex(ConstantDB.maskImagePath)));
                    mDataTextChat.setFileUrl(c.getString(c.getColumnIndex(ConstantDB.fileUrl)));
                    mDataTextChat.setThumbBase64(c.getString(c.getColumnIndex(ConstantDB.thumbBase64)));
                    mDataTextChat.setThumbFilePath(c.getString(c.getColumnIndex(ConstantDB.thumbFilePath)));
                    mDataTextChat.setUploadStatus(c.getString(c.getColumnIndex(ConstantDB.uploadStatus)));
                    mDataTextChat.setDownloadStatus(c.getString(c.getColumnIndex(ConstantDB.downloadStatus)));
                    mDataTextChat.setYoutubeTitle(c.getString(c.getColumnIndex(ConstantDB.youtubeTitle)));
                    mDataTextChat.setYoutubeDescription(c.getString(c.getColumnIndex(ConstantDB.youtubeDesc)));
                    mDataTextChat.setYoutubePublishTime(c.getString(c.getColumnIndex(ConstantDB.youtubePublishTime)));
                    mDataTextChat.setYoutubeThumbUrl(c.getString(c.getColumnIndex(ConstantDB.youtubeThumbUrl)));
                    mDataTextChat.setYoutubeVideoId(c.getString(c.getColumnIndex(ConstantDB.youtubeVidId)));
                    mDataTextChat.setIsMasked(c.getString(c.getColumnIndex(ConstantDB.isMasked)));
                    mDataTextChat.setMaskEnabled(c.getString(c.getColumnIndex(ConstantDB.maskEnabled)));
                    mDataTextChat.setYahooTitle(c.getString(c.getColumnIndex(ConstantDB.yahooTitle)));
                    mDataTextChat.setYahooDescription(c.getString(c.getColumnIndex(ConstantDB.yahooDesc)));
                    mDataTextChat.setYahooPublishTime(c.getString(c.getColumnIndex(ConstantDB.yahooPublishTime)));
                    mDataTextChat.setYahooShareUrl(c.getString(c.getColumnIndex(ConstantDB.yahooShareLink)));
                    mDataTextChat.setYahooImageUrl(c.getString(c.getColumnIndex(ConstantDB.yahooImageUrl)));
                    mDataTextChat.setFriendName(c.getString(c.getColumnIndex(ConstantDB.friend_name)));
                    mDataTextChat.setSenderId(c.getString(c.getColumnIndex(ConstantDB.sender_id)));
                    mDataTextChat.setSenderName(c.getString(c.getColumnIndex(ConstantDB.sender_name)));
                    mDataTextChat.setFriendId(c.getString(c.getColumnIndex(ConstantDB.FriendId)));
                    mArrDataTextChats.add(mDataTextChat);
                } while (c.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return mArrDataTextChats;
    }

    public String getUnreadOnetoOneMessagesCount(String deliveryStatus, String userChatId) {
        String badgeCount = "0";
        int msgCount = 0;
        String SQL = "SELECT COUNT(DISTINCT friendChatID) AS badgeCount FROM "
                + ConstantDB.TableChat + " WHERE " +
                "" + ConstantDB.deliveryStatus + "  = '" + deliveryStatus
                + "' AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_SINGLECHAT
                + "' AND " + ConstantDB.senderChatID + " <> '" + userChatId + "';";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                badgeCount = c.getString(c.getColumnIndex("badgeCount"));
                msgCount += Integer.parseInt(badgeCount);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        //  Log.i("chat list", unreadMessageIDs.toString());
        return String.valueOf(msgCount);
    }


    public String getUnreadGroupMessagesCount(String deliveryStatus, String userChatId) {
        String badgeCount = "0";
        int msgCount = 0;
        String SQL = "SELECT COUNT(DISTINCT strGroupID) AS badgeCount FROM "
                + ConstantDB.TableChat + ",GroupDetails "
                + " WHERE " +
                "" + ConstantDB.deliveryStatus + "  = '" + deliveryStatus
                + "' AND " + ConstantDB.chatType + " = '" + ConstantChat.TYPE_GROUPCHAT
                + "' AND " + ConstantDB.senderChatID + " <> '" + userChatId + "' AND Chat.strGroupID=GroupDetails .GroupId ;";


        Cursor c = null;
        try {
            c = app.mDbHelper.getDB().rawQuery(SQL, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                badgeCount = c.getString(c.getColumnIndex("badgeCount"));
                msgCount += Integer.parseInt(badgeCount);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        //  Log.i("chat list", unreadMessageIDs.toString());
        return String.valueOf(msgCount);
    }

    public void deleteAll() {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableChat, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteChatItem(DataTextChat dataItem) {
        try {
            app.mDbHelper.getDB().delete(ConstantDB.TableChat, ConstantDB.messageID
                    + "=?", new String[]{dataItem.getMessageid()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
