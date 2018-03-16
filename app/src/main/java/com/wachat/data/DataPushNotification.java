package com.wachat.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPushNotification implements Serializable {

    public class Aps {
        @JsonProperty("alert")
        private String alert;
        @JsonProperty("sound")
        private String sound;
        @JsonProperty("badge")
        private Integer badge;
        @JsonProperty("category")
        private String category;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * @return The alert
         */
        @JsonProperty("alert")
        public String getAlert() {
            return alert;
        }

        /**
         * @param alert The alert
         */
        @JsonProperty("alert")
        public void setAlert(String alert) {
            this.alert = alert;
        }

        /**
         * @return The sound
         */
        @JsonProperty("sound")
        public String getSound() {
            return sound;
        }

        /**
         * @param sound The sound
         */
        @JsonProperty("sound")
        public void setSound(String sound) {
            this.sound = sound;
        }

        /**
         * @return The badge
         */
        @JsonProperty("badge")
        public Integer getBadge() {
            return badge;
        }

        /**
         * @param badge The badge
         */
        @JsonProperty("badge")
        public void setBadge(Integer badge) {
            this.badge = badge;
        }

        /**
         * @return The category
         */
        @JsonProperty("category")
        public String getCategory() {
            return category;
        }

        /**
         * @param category The category
         */
        @JsonProperty("category")
        public void setCategory(String category) {
            this.category = category;
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

    @JsonProperty("aps")
    private Aps aps;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The aps
     */
    @JsonProperty("aps")
    public Aps getAps() {
        return aps;
    }

    /**
     * @param aps The aps
     */
    @JsonProperty("aps")
    public void setAps(Aps aps) {
        this.aps = aps;
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
