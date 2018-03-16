package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Gourav Kundu on 28-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContact implements Serializable {

    @JsonProperty("phoneNo")
    public String PhoneNumber = "";


    public String Name = "";

    @JsonProperty("userId")
    public String FriendId = "";

    @JsonProperty("isRegistered")
    public int IsRegistered = 0;
    public String FriendImageLink = "";

    @JsonProperty("chatId")
    public String ChatId = "";

    @JsonProperty("userStatus")
    public String Status = "";

    @JsonProperty("isDeleted")
    public String IsDeleted = "";

    @JsonProperty("name")
    public String AppName = "";

    @JsonProperty("userProfileImage")
    public String AppFriendImageLink = "";
    public String imageUrl = "";

    @JsonProperty("isBlocked")
    public int IsBlocked = 0;

    @JsonProperty("isFavorite")
    public int IsFavorite = 0;

    public int isSynced = 0;

    @JsonProperty("countryCode")
    public String countryCode = "";

    public int isSelectedForGroup = 0;
    @JsonProperty("isfindbyphoneno")
    public String isfindbyphoneno = "0";
    @JsonProperty("gender")
    public String gender = "";

    @JsonProperty("relation")
    public String relation = "";

    public String lat = "";
    public String lng ="";

    public int getIsFriend() {
        return isFriend;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int isFriend  = 1;
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }



    public String getIsfindbyphoneno() {
        return isfindbyphoneno;
    }

    public void setIsfindbyphoneno(String isfindbyphoneno) {
        this.isfindbyphoneno = isfindbyphoneno;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getIsSelectedForGroup() {
        return isSelectedForGroup;
    }

    public void setIsSelectedForGroup(int isSelectedForGroup) {
        this.isSelectedForGroup = isSelectedForGroup;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public int getIsRegistered() {
        return IsRegistered;
    }

    public void setIsRegistered(int isRegistered) {
        IsRegistered = isRegistered;
    }

    public String getFriendImageLink() {
        return FriendImageLink;
    }

    public void setFriendImageLink(String friendImageLink) {
        FriendImageLink = friendImageLink;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        IsDeleted = isDeleted;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getAppFriendImageLink() {
        return AppFriendImageLink;
    }

    public void setAppFriendImageLink(String appFriendImageLink) {
        AppFriendImageLink = appFriendImageLink;
    }

    public int getIsBlocked() {
        return IsBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        IsBlocked = isBlocked;
    }

    public int getIsFavorite() {
        return IsFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        IsFavorite = isFavorite;
    }

    public int getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }
}
