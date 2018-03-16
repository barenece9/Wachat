package com.wachat.data.AdminActionResponse;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "groupId",
        "groupName",
        "groupImage",
        "ownerId",
        "creationdateTime",
        "groupMembers",
        "memberdetails"
})
public class GroupDetails {

    @JsonProperty("groupId")
    private String groupId;
    @JsonProperty("groupName")
    private String groupName;
    @JsonProperty("groupImage")
    private String groupImage;
    @JsonProperty("ownerId")
    private String ownerId;
    @JsonProperty("creationdateTime")
    private String creationdateTime;
    @JsonProperty("groupMembers")
    private String groupMembers;
    @JsonProperty("memberdetails")
    private List<Memberdetail> memberdetails = new ArrayList<Memberdetail>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The groupId
     */
    @JsonProperty("groupId")
    public String getGroupId() {
        return groupId;
    }

    /**
     *
     * @param groupId
     * The groupId
     */
    @JsonProperty("groupId")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     *
     * @return
     * The groupName
     */
    @JsonProperty("groupName")
    public String getGroupName() {
        return groupName;
    }

    /**
     *
     * @param groupName
     * The groupName
     */
    @JsonProperty("groupName")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     *
     * @return
     * The groupImage
     */
    @JsonProperty("groupImage")
    public String getGroupImage() {
        return groupImage;
    }

    /**
     *
     * @param groupImage
     * The groupImage
     */
    @JsonProperty("groupImage")
    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    /**
     *
     * @return
     * The ownerId
     */
    @JsonProperty("ownerId")
    public String getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @param ownerId
     * The ownerId
     */
    @JsonProperty("ownerId")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     *
     * @return
     * The creationdateTime
     */
    @JsonProperty("creationdateTime")
    public String getCreationdateTime() {
        return creationdateTime;
    }

    /**
     *
     * @param creationdateTime
     * The creationdateTime
     */
    @JsonProperty("creationdateTime")
    public void setCreationdateTime(String creationdateTime) {
        this.creationdateTime = creationdateTime;
    }

    /**
     *
     * @return
     * The groupMembers
     */
    @JsonProperty("groupMembers")
    public String getGroupMembers() {
        return groupMembers;
    }

    /**
     *
     * @param groupMembers
     * The groupMembers
     */
    @JsonProperty("groupMembers")
    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     *
     * @return
     * The memberdetails
     */
    @JsonProperty("memberdetails")
    public List<Memberdetail> getMemberdetails() {
        return memberdetails;
    }

    /**
     *
     * @param memberdetails
     * The memberdetails
     */
    @JsonProperty("memberdetails")
    public void setMemberdetails(List<Memberdetail> memberdetails) {
        this.memberdetails = memberdetails;
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