package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataStatus extends BaseResponse implements Serializable {

    @JsonProperty("userId")
    public String StatusId = "";
    public String UserStatus = "";
    public String IsSelected = "0";

    public String getStatusId() {
        return StatusId;
    }

    public void setStatusId(String statusId) {
        StatusId = statusId;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getIsSelected() {
        return IsSelected;
    }

    public void setIsSelected(String isSelected) {
        IsSelected = isSelected;
    }





}
