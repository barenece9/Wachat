package com.wachat.data;

import java.io.Serializable;

/**
 * Created by Argha on 30-10-2015.
 */
public class DataTextChat extends DataContact implements Serializable {


    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getYoutubePublishTime() {
        return youtubePublishTime;
    }

    public void setYoutubePublishTime(String youtubePublishTime) {
        this.youtubePublishTime = youtubePublishTime;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public interface UploadStatus{
        String UPLOADING = "1";
        String UPLOADED = "2";
        String NOT_UPLOADED = "0";
    }

    public interface DownloadStatus{
        String DOWNLOADING = "1";
        String DOWNLOADED = "2";
        String NOT_DOWNLOADED = "0";
    }

    public interface  MaskStatus{
        String MASK_DISABLED = "0";
        String MASK_ENABELD = "1";
    }
    private String messageid = "";
    private String chattype = "";
    private String online = "";
    private String body = "";
    private String timestamp = "";
    private String lang = "";
    private String friendPhoneNo = "";
    private String friendName = "";
    private String senderChatID = "";
    private String messageType = "";
    private String strGroupID = "";


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    private String senderId = "";

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    private String senderName = "";
    public String getGroupJID() {
        return groupJID;
    }

    public void setGroupJID(String groupJID) {
        this.groupJID = groupJID;
    }

    private String groupJID = "";

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private String groupName = "";
    private String deliveryStatus = "0";
    private String strAttachmentID = "";
    private String strTranslatedText = "";
    private String friendChatID = "";
    private String userID = "";
    private String attachContactName = "";
    private String attachContactNo = "";
    private String attachBase64str4Img = "";
    private String loc_lat = "";
    private String loc_long = "";
    private String loc_address = "";

    private String filePath = "";

    public String getBlurredImagePath() {
        return blurredImagePath;
    }

    public void setBlurredImagePath(String blurredImagePath) {
        this.blurredImagePath = blurredImagePath;
    }

    private String blurredImagePath = "";
    private String fileUrl = "";
    private String thumbBase64 = "";
    private String thumbFilePath = "";
    private int progress = 0;
    private String uploadStatus  = UploadStatus.NOT_UPLOADED;
    private String downloadStatus  = DownloadStatus.NOT_DOWNLOADED;
    private int downloadId;
    public int unreadCount = 0;
    //fields for youtube
    private String youtubeTitle = "";
    private String youtubePublishTime = "";
    private String youtubeDescription = "";
    private String youtubeThumbUrl = "";
    private String youtubeVideoId = "";

    //fields for yahoo
    private String yahooTitle = "";
    private String yahooPublishTime = "";
    private String yahooDescription = "";
    private String yahooImageUrl = "";
    private String yahooShareUrl = "";

    public String getYahooTitle() {
        return yahooTitle;
    }

    public void setYahooTitle(String yahooTitle) {
        this.yahooTitle = yahooTitle;
    }

    public String getYahooPublishTime() {
        return yahooPublishTime;
    }

    public void setYahooPublishTime(String yahooPublishTime) {
        this.yahooPublishTime = yahooPublishTime;
    }

    public String getYahooDescription() {
        return yahooDescription;
    }

    public void setYahooDescription(String yahooDescription) {
        this.yahooDescription = yahooDescription;
    }

    public String getYahooImageUrl() {
        return yahooImageUrl;
    }

    public void setYahooImageUrl(String yahooImageUrl) {
        this.yahooImageUrl = yahooImageUrl;
    }

    public String getYahooShareUrl() {
        return yahooShareUrl;
    }

    public void setYahooShareUrl(String yahooShareUrl) {
        this.yahooShareUrl = yahooShareUrl;
    }

    public String getYoutubeTitle() {
        return youtubeTitle;
    }

    public void setYoutubeTitle(String youtubeTitle) {
        this.youtubeTitle = youtubeTitle;
    }


    public String getYoutubeDescription() {
        return youtubeDescription;
    }

    public void setYoutubeDescription(String youtubeDescription) {
        this.youtubeDescription = youtubeDescription;
    }

    public String getYoutubeThumbUrl() {
        return youtubeThumbUrl;
    }

    public void setYoutubeThumbUrl(String youtubeThumbUrl) {
        this.youtubeThumbUrl = youtubeThumbUrl;
    }


    private String isMasked = "0";
    private  String maskEnabled = MaskStatus.MASK_DISABLED;


    public String getIsMasked() {
        return isMasked;
    }

    public void setIsMasked(String isMasked) {
        this.isMasked = isMasked;
    }

    public String getMaskEnabled() {
        return maskEnabled;
    }

    public void setMaskEnabled(String maskEnabled) {
        this.maskEnabled = maskEnabled;
    }

  


    public String getLoc_lat() {
        return loc_lat;
    }

    public void setLoc_lat(String loc_lat) {
        this.loc_lat = loc_lat;
    }

    public String getLoc_long() {
        return loc_long;
    }

    public void setLoc_long(String loc_long) {
        this.loc_long = loc_long;
    }

    public String getLoc_address() {
        return loc_address;
    }

    public void setLoc_address(String loc_address) {
        this.loc_address = loc_address;
    }


    public String getAttachContactName() {
        return attachContactName;
    }

    public void setAttachContactName(String attachContactName) {
        this.attachContactName = attachContactName;
    }

    public String getAttachBase64str4Img() {
        return attachBase64str4Img;
    }

    public void setAttachBase64str4Img(String attachBase64str4Img) {
        this.attachBase64str4Img = attachBase64str4Img;
    }

    public String getAttachContactNo() {
        return attachContactNo;
    }

    public void setAttachContactNo(String attachContactNo) {
        this.attachContactNo = attachContactNo;
    }

    private String sectionDate = "";

    public String getSectionTime() {
        return sectionTime;
    }

    public void setSectionTime(String sectionTime) {
        this.sectionTime = sectionTime;
    }

    private String sectionTime = "";
    private String sectionCount = "";

    public String getFriendChatID() {
        return friendChatID;
    }

    public void setFriendChatID(String friendChatID) {
        this.friendChatID = friendChatID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }



    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getStrGroupID() {
        return strGroupID;
    }

    public void setStrGroupID(String strGroupID) {
        this.strGroupID = strGroupID;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getStrAttachmentID() {
        return strAttachmentID;
    }

    public void setStrAttachmentID(String strAttachmentID) {
        this.strAttachmentID = strAttachmentID;
    }

    public String getStrTranslatedText() {
        return strTranslatedText;
    }

    public void setStrTranslatedText(String strTranslatedText) {
        this.strTranslatedText = strTranslatedText;
    }


    public String getSenderChatID() {
        return senderChatID;
    }

    public void setSenderChatID(String senderChatID) {
        this.senderChatID = senderChatID;
    }


    public String getFriendPhoneNo() {
        return friendPhoneNo;
    }

    public void setFriendPhoneNo(String friendPhoneNo) {
        this.friendPhoneNo = friendPhoneNo;
    }


    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getChattype() {
        return chattype;
    }

    public void setChattype(String chattype) {
        this.chattype = chattype;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSectionDate() {
        return sectionDate;
    }

    public void setSectionDate(String sectionDate) {
        this.sectionDate = sectionDate;
    }

    public String getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(String sectionCount) {
        this.sectionCount = sectionCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
    public String getThumbBase64() {
        return thumbBase64;
    }

    public void setThumbBase64(String thumbBase64) {
        this.thumbBase64 = thumbBase64;
    }

    public String getThumbFilePath() {
        return thumbFilePath;
    }

    public void setThumbFilePath(String thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
