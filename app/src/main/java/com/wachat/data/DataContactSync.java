package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 01-10-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContactSync extends BaseResponse implements Serializable {

    public int timeStamp = 0;
    @JsonProperty("contactUserLists")
    public ArrayList<DataContact> mListContact = new ArrayList<DataContact>();
    @JsonProperty("groupDetails")
    public ArrayList<DataGroup> mListGroup = new ArrayList<DataGroup>();

    @JsonProperty("currentGroupIds")
    public String currentGroupIds = "";
    @JsonProperty("userlist")
    public String currentFriendIds = "";
    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<DataContact> getmListContact() {
        return mListContact;
    }

    public void setmListContact(ArrayList<DataContact> mListContact) {
        this.mListContact = mListContact;
    }

    public ArrayList<DataGroup> getmListGroup() {
        return mListGroup;
    }

    public void setmListGroup(ArrayList<DataGroup> mListGroup) {
        this.mListGroup = mListGroup;
    }
}
