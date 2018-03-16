package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 22-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataVersionCheck extends BaseResponse implements Serializable {

    public String version = "";
    public String type = "";
    public String mandetory = "";

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataVersionCheck{");
        sb.append("version='").append(version).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", mandetory='").append(mandetory).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
