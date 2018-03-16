package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProfile extends BaseResponse implements Serializable {

    @JsonProperty("userId")
    public String userId = "";
    public String username = "";
    public String profileImage = "";
    public String phoneNo = "";
    public String countryCode = "";
    public String chatId = "";
    public String UserStatus = "";
    public String IsRegistered = "";
    public String IsVerified = "";
    public String PairingValue = "";
    public String VerificationCode = "";
    public String Language = "";
    public String LanguageIdentifire="";
    public String CountryName="";
    public String Gender ="";
    public String istransalate= "";
    public String isfindbylocation ="";
    public String isfindbyphoneno ="";
    public String isNotificationOn ="";


    public String getIsNotificationOn() {
        return isNotificationOn;
    }

    public void setIsNotificationOn(String isNotificationOn) {
        this.isNotificationOn = isNotificationOn;
    }


    public String getIstransalate() {
        return istransalate;
    }

    public void setIstransalate(String istransalate) {
        this.istransalate = istransalate;
    }

    public String getIsfindbylocation() {
        return isfindbylocation;
    }

    public void setIsfindbylocation(String isfindbylocation) {
        this.isfindbylocation = isfindbylocation;
    }

    public String getIsfindbyphoneno() {
        return isfindbyphoneno;
    }

    public void setIsfindbyphoneno(String isfindbyphoneno) {
        this.isfindbyphoneno = isfindbyphoneno;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getIsRegistered() {
        return IsRegistered;
    }

    public void setIsRegistered(String isRegistered) {
        IsRegistered = isRegistered;
    }

    public String getIsVerified() {
        return IsVerified;
    }

    public void setIsVerified(String isVerified) {
        IsVerified = isVerified;
    }

    public String getPairingValue() {
        return PairingValue;
    }

    public void setPairingValue(String pairingValue) {
        PairingValue = pairingValue;
    }

    public String getVerificationCode() {
        return VerificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        VerificationCode = verificationCode;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getLanguageIdentifire() {
        return LanguageIdentifire.toLowerCase();
    }

    public void setLanguageIdentifire(String languageIdentifire) {
        LanguageIdentifire = languageIdentifire;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
