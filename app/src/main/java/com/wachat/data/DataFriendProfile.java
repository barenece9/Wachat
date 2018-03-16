package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFriendProfile extends BaseResponse implements Serializable {

    public String friendName   = "";
    public String friendImage   = "";
    public String language  = "";
    public String gender  = "";
    public String friendPhno = "";
    public String relation = "";

    public String getIsshownumber() {
        return isshownumber;
    }

    public void setIsshownumber(String isshownumber) {
        this.isshownumber = isshownumber;
    }

    public String getIsfindbyphoneno() {
        return isfindbyphoneno;
    }

    public void setIsfindbyphoneno(String isfindbyphoneno) {
        this.isfindbyphoneno = isfindbyphoneno;
    }

    public String isshownumber = "";
    public String isfindbyphoneno = "";
    public String getFriendPhno() {
        return friendPhno;
    }

    public void setFriendPhno(String friendPhno) {
        this.friendPhno = friendPhno;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(String friendImage) {
        this.friendImage = friendImage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
