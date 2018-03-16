package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContactList extends BaseResponse implements Serializable {

    @JsonProperty("userId")
    public String timeStamp = "";
    public String contactUserLists = "";
    public String userId = "";
    public String chatId = "";
    public String phoneNo = "";
    public String countryCode = "";

}
