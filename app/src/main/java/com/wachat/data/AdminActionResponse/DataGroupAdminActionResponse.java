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
        "responseCode",
        "responseDetails",
        "responseFlag",
        "groupDetails"
})
public class DataGroupAdminActionResponse {

    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("responseDetails")
    private String responseDetails;
    @JsonProperty("responseFlag")
    private String responseFlag;
    @JsonProperty("groupDetails")
    private GroupDetails groupDetails;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The responseCode
     */
    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    /**
     *
     * @param responseCode
     * The responseCode
     */
    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     *
     * @return
     * The responseDetails
     */
    @JsonProperty("responseDetails")
    public String getResponseDetails() {
        return responseDetails;
    }

    /**
     *
     * @param responseDetails
     * The responseDetails
     */
    @JsonProperty("responseDetails")
    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }

    /**
     *
     * @return
     * The responseFlag
     */
    @JsonProperty("responseFlag")
    public String getResponseFlag() {
        return responseFlag;
    }

    /**
     *
     * @param responseFlag
     * The responseFlag
     */
    @JsonProperty("responseFlag")
    public void setResponseFlag(String responseFlag) {
        this.responseFlag = responseFlag;
    }

    /**
     *
     * @return
     * The groupDetails
     */
    @JsonProperty("groupDetails")
    public GroupDetails getGroupDetails() {
        return groupDetails;
    }

    /**
     *
     * @param groupDetails
     * The groupDetails
     */
    @JsonProperty("groupDetails")
    public void setGroupDetails(GroupDetails groupDetails) {
        this.groupDetails = groupDetails;
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