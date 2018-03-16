package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Priti Chatterjee on 21-09-2015.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class DataCountry implements Serializable {

    public String countryCode = "", countryName = "";

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
