package com.wachat.data.AdminActionResponse;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "chatId",
        "userId",
        "userName",
        "userPhNo",
        "userCountryId",
        "userImage",
        "isOwner",
        "addedTime",
        "IsGrpadmin",
        "IsGrpblock",
        "gender"
})
public class Memberdetail {

    @JsonProperty("chatId")
    private String chatId;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userPhNo")
    private String userPhNo;
    @JsonProperty("userCountryId")
    private String userCountryId;
    @JsonProperty("userImage")
    private String userImage;
    @JsonProperty("isOwner")
    private Integer isOwner;
    @JsonProperty("addedTime")
    private String addedTime;
    @JsonProperty("IsGrpadmin")
    private String IsGrpadmin;
    @JsonProperty("IsGrpblock")
    private String IsGrpblock;
    @JsonProperty("gender")
    private String gender;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The chatId
     */
    @JsonProperty("chatId")
    public String getChatId() {
        return chatId;
    }

    /**
     *
     * @param chatId
     * The chatId
     */
    @JsonProperty("chatId")
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     *
     * @return
     * The userId
     */
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The userId
     */
    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The userName
     */
    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The userName
     */
    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     * The userPhNo
     */
    @JsonProperty("userPhNo")
    public String getUserPhNo() {
        return userPhNo;
    }

    /**
     *
     * @param userPhNo
     * The userPhNo
     */
    @JsonProperty("userPhNo")
    public void setUserPhNo(String userPhNo) {
        this.userPhNo = userPhNo;
    }

    /**
     *
     * @return
     * The userCountryId
     */
    @JsonProperty("userCountryId")
    public String getUserCountryId() {
        return userCountryId;
    }

    /**
     *
     * @param userCountryId
     * The userCountryId
     */
    @JsonProperty("userCountryId")
    public void setUserCountryId(String userCountryId) {
        this.userCountryId = userCountryId;
    }

    /**
     *
     * @return
     * The userImage
     */
    @JsonProperty("userImage")
    public String getUserImage() {
        return userImage;
    }

    /**
     *
     * @param userImage
     * The userImage
     */
    @JsonProperty("userImage")
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    /**
     *
     * @return
     * The isOwner
     */
    @JsonProperty("isOwner")
    public Integer getIsOwner() {
        return isOwner;
    }

    /**
     *
     * @param isOwner
     * The isOwner
     */
    @JsonProperty("isOwner")
    public void setIsOwner(Integer isOwner) {
        this.isOwner = isOwner;
    }

    /**
     *
     * @return
     * The addedTime
     */
    @JsonProperty("addedTime")
    public String getAddedTime() {
        return addedTime;
    }

    /**
     *
     * @param addedTime
     * The addedTime
     */
    @JsonProperty("addedTime")
    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    /**
     *
     * @return
     * The IsGrpadmin
     */
    @JsonProperty("IsGrpadmin")
    public String getIsGrpadmin() {
        return IsGrpadmin;
    }

    /**
     *
     * @param IsGrpadmin
     * The IsGrpadmin
     */
    @JsonProperty("IsGrpadmin")
    public void setIsGrpadmin(String IsGrpadmin) {
        this.IsGrpadmin = IsGrpadmin;
    }

    /**
     *
     * @return
     * The IsGrpblock
     */
    @JsonProperty("IsGrpblock")
    public String getIsGrpblock() {
        return IsGrpblock;
    }

    /**
     *
     * @param IsGrpblock
     * The IsGrpblock
     */
    @JsonProperty("IsGrpblock")
    public void setIsGrpblock(String IsGrpblock) {
        this.IsGrpblock = IsGrpblock;
    }

    /**
     *
     * @return
     * The gender
     */
    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}