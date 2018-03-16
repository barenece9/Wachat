package com.wachat.data;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wachat.services.ServiceXmppConnection;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Argha on 05-10-2015.
 */
public class DataCreateOrEditGroup extends BaseResponse implements Serializable {
    public String userId = "";
    public String GroupImage = "";
    public String ChatId = "";
    public String CreationDateTime = "";
    public String GroupId = "";
    public String GroupJId = "";
    public String GroupName = "";
    @JsonProperty("GroupMember")
    public ArrayList<DataGroupMember> GroupMember = new ArrayList<DataGroupMember>();

    public String getGroupJId() {
        return TextUtils.isEmpty(GroupJId)?"":GroupJId.replace("@conference." + ServiceXmppConnection.SERVICE_NAME, "");
    }

    public void setGroupJId(String groupJId) {
        if(!TextUtils.isEmpty(groupJId)) {
            GroupJId = groupJId.replace("@conference." + ServiceXmppConnection.SERVICE_NAME, "");
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }


    public String getGroupImage() {
        return GroupImage;
    }

    public void setGroupImage(String groupImage) {
        GroupImage = groupImage;
    }

    public String getCreationDateTime() {
        return CreationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        CreationDateTime = creationDateTime;
    }


    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }


    public ArrayList<DataGroupMember> getDataGroupMemberArrayList() {
        return GroupMember;
    }

    public void setDataGroupMemberArrayList(ArrayList<DataGroupMember> GroupMember) {
        this.GroupMember = GroupMember;
    }

}
