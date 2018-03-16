package com.wachat.data;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wachat.services.ServiceXmppConnection;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 01-10-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataGroup implements Serializable {

    public String groupId="";
    public String lastMessageType = "";
    public String lastMessage="";
    public String lastMessageTime="";
    public String lastMessageSenderId="";
    public String lastMessageSenderName="";
    public String groupJId="";
    public String creationdateTime="";
    public String groupImage="";
    public String groupMembers="";
    public String groupName="";
    public int unreadCount = 0;
    public ArrayList<DataGroupMembers> memberdetailsdelete=new ArrayList<DataGroupMembers>();
    public String ownerId="";
    private String strTranslatedText = "";


    public String getGroupJId() {
        return groupJId.replace("@conference." + ServiceXmppConnection.SERVICE_NAME, "");
    }

    public void setGroupJId(String groupJId) {

        if(!TextUtils.isEmpty(groupJId)) {
            this.groupJId = groupJId.replace("@conference." + ServiceXmppConnection.SERVICE_NAME, "");
        }
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }



    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }



    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }



    public String getLastMessageSenderName() {
        return lastMessageSenderName;
    }

    public void setLastMessageSenderName(String lastMessageSenderName) {
        this.lastMessageSenderName = lastMessageSenderName;
    }


    public ArrayList<DataGroupMembers> memberdetails=new ArrayList<DataGroupMembers>();

    public ArrayList<DataGroupMembers> getMemberdetailsdelete() {
        return memberdetailsdelete;
    }

    public void setMemberdetailsdelete(ArrayList<DataGroupMembers> memberdetailsdelete) {
        this.memberdetailsdelete = memberdetailsdelete;
    }



    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreationdateTime() {
        return creationdateTime;
    }

    public void setCreationdateTime(String creationdateTime) {
        this.creationdateTime = creationdateTime;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<DataGroupMembers> getMemberdetails() {
        return memberdetails;
    }

    public void setMemberdetails(ArrayList<DataGroupMembers> memberdetails) {
        this.memberdetails = memberdetails;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getStrTranslatedText() {
        return strTranslatedText;
    }

    public void setStrTranslatedText(String strTranslatedText) {
        this.strTranslatedText = strTranslatedText;
    }
}
