package com.wachat.dataClasses;

import java.io.Serializable;

/**
 * Created by Argha on 22-12-2015.
 */
public class AttachmentDetailsGetSet implements Serializable {
    String attachmentID = "";
    String httpURL = "";
    String filePath = "";
    String fileType = "";
    String thumbnailName = "";
    String friendOrGrpID = "";
    String assetURL = "";


    public String getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
    }

    public String getHttpURL() {
        return httpURL;
    }

    public void setHttpURL(String httpURL) {
        this.httpURL = httpURL;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public String getFriendOrGrpID() {
        return friendOrGrpID;
    }

    public void setFriendOrGrpID(String friendOrGrpID) {
        this.friendOrGrpID = friendOrGrpID;
    }

    public String getAssetURL() {
        return assetURL;
    }

    public void setAssetURL(String assetURL) {
        this.assetURL = assetURL;
    }


}
