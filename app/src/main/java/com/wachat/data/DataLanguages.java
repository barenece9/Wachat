package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Argha on 12-11-2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataLanguages implements Serializable {
    public  String name = "";
    public String language = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
