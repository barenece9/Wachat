package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gourav Kundu on 01-10-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataGroupMembers implements Serializable {

    public String chatId = "";
    public String addedTime = "";
    public String delTime = "";
    public String type = "";
    public String gender = "";
    public String isOwner = "";

    public String getIsGrpadmin() {
        return IsGrpadmin;
    }

    public void setIsGrpadmin(String isGrpadmin) {
        IsGrpadmin = isGrpadmin;
    }

    public String IsGrpadmin = "";

    public String getIsGrpblock() {
        return IsGrpblock;
    }

    public void setIsGrpblock(String isGrpblock) {
        IsGrpblock = isGrpblock;
    }

    public String IsGrpblock = "";
    public String userCountryId = "";
    public String userId = "";
    public String userImage = "";
    public String userName = "";
    public String userPhNo = "";

    public String status = "";
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    public String getUserCountryId() {
        return userCountryId;
    }

    public void setUserCountryId(String userCountryId) {
        this.userCountryId = userCountryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhNo() {
        return userPhNo;
    }

    public void setUserPhNo(String userPhNo) {
        this.userPhNo = userPhNo;
    }
}
