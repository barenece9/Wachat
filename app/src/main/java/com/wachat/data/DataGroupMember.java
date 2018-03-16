package com.wachat.data;

import java.io.Serializable;

/**
 * Created by Argha on 05-10-2015.
 */
public class DataGroupMember implements Serializable {
    public String GroupMemberUserId = "";

    public String ChatId = "";



    public String userId = "";
    public String MemberName = "";
    public String MemberPhNo = "";
    public String MemberCountryId = "";

    public String MemberImage = "";

    public String IsOwner = "";
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
    public String status= "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemberName() {
        return MemberName;
    }

    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getMemberPhNo() {
        return MemberPhNo;
    }

    public void setMemberPhNo(String memberPhNo) {
        MemberPhNo = memberPhNo;
    }



    public String getMemberCountryId() {
        return MemberCountryId;
    }

    public void setMemberCountryId(String memberCountryId) {
        MemberCountryId = memberCountryId;
    }


    public String getMemberImage() {
        return MemberImage;
    }

    public void setMemberImage(String memberImage) {
        MemberImage = memberImage;
    }

    public String getIsOwner() {
        return IsOwner;
    }

    public void setIsOwner(String isOwner) {
        IsOwner = isOwner;
    }




    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }


    public String getGroupMemberUserId() {
        return GroupMemberUserId;
    }

    public void setGroupMemberUserId(String groupMemberUserId) {
        GroupMemberUserId = groupMemberUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
