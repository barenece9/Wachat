package com.wachat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {

    // private static final String PREF_GCM_REGID = "gcm_reg_id";
    private static final String PREF_APP_VERSION = "app_version";
    public static final String PREFS_KEY_GCM_ID = "gcm_id";
    public static boolean installed = false;
    private static Prefs instance = null;
    private Context context = null;

    Prefs(Context context) {
        this.context = context;

    }

    public static Prefs getInstance(Context context) {

        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    // public static final String PREF_DEFAULT_GCM_REGID = "0";

    public String getString(String key, String def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String s = prefs.getString(key, def);
        return s;
    }

    public int getInt(String key, int def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        int i = Integer.parseInt(prefs.getString(key, Integer.toString(def)));
        return i;
    }

    public float getFloat(String key, float def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        float f = Float.parseFloat(prefs.getString(key, Float.toString(def)));
        return f;
    }

    public long getLong(String key, long def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        long l = Long.parseLong(prefs.getString(key, Long.toString(def)));
        return l;
    }

    public void setString(String key, String val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor e = prefs.edit();
        e.putString(key, val);
        e.commit();
    }

    public void setBoolean(String key, boolean val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor e = prefs.edit();
        e.putBoolean(key, val);
        e.commit();
    }

    public void setInt(String key, int val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor e = prefs.edit();
        e.putString(key, Integer.toString(val));
        e.commit();
    }

    public void clear() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public void setLong(String key, long val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor e = prefs.edit();
        e.putString(key, Long.toString(val));
        e.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean b = prefs.getBoolean(key, def);
        return b;
    }

    public void setDouble(String key, double val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor e = prefs.edit();
        e.putString(key, Double.toString(val));
        e.commit();
    }

    public double getDouble(String key, double def) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        double b = Double
                .parseDouble(prefs.getString(key, Double.toString(def)));
        return b;
    }

    public int getAppVersion() {
        return Integer.parseInt(getString(PREF_APP_VERSION, "0"));
    }

    public void setAppVersion(int appVersion) {
        setString(PREF_APP_VERSION, String.valueOf(appVersion));
    }

    public String getGcmId(){
        return getString(PREFS_KEY_GCM_ID,"");
    }

    public void setGcmId(String gcmId){
        setString(PREFS_KEY_GCM_ID,gcmId);
    }
    public String getAccountParing() {
        return getString("pairing", "0");
    }

    public void setAccountParing(String pairing) {
        setString("pairing", pairing);
    }

    public String getRegister() {
        return getString("reg", "");
    }

    public void setRegister(String reg) {
        setString("reg", reg);
    }


    public String getUserId() {
        return getString("userId", "");
    }

    public void setUserId(String userId) {
        setString("userId", userId);
    }

    public String getChatId() {
        return getString("ChatId", "");
    }

    public void setChatId(String ChatId) {
        setString("ChatId", ChatId);
    }

    public void setTimeStamp(String time) {
        setString("timeStamp", time);
    }

    public String getTimestamp() {
        return getString("timeStamp", "all");
    }

    //	public String isSynced(){
//		return getString("ContactSync", "0");
//	}
//
//	public void setSync(String syncStatus){
//		setString("ContactSync", syncStatus);
//	}
    public String getCountryName() {
        return getString("countryName", "");
    }

    public void setCountryName(String countryName) {
        setString("countryName", countryName);
    }

    public String getCountryCode() {
        return getString("countryCode", "");
    }

    public void setCountryCode(String countryCode) {
        setString("countryCode", countryCode);
    }

    public String getIsRegClick() {
        return getString("click", "true");
    }

    public void setIsRegClick(String click) {
        setString("click", click);
    }

    public String getLanguage() {
        return getString("EN", "");
    }

    public void setLanguage(String language) {
        setString("language", language);
    }

    public void clearPreferences() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public boolean isFirstTimeTutorialShown() {
        return getBoolean("first_time_tutorial_shown", false);
    }

    public void setFirstTimeTutorialShown(){
        setBoolean("first_time_tutorial_shown", true);
    }


    public boolean isFirstTimeProfileEditDone() {
        return getBoolean("first_time_profile_edit_done", true);
    }

    public void setFirstTimeProfileEditDone(){
        setBoolean("first_time_profile_edit_done", true);
    }

    public void setGroupListTimeStamp(String groupListTimeStamp){
        setString("group_list_timestamp",groupListTimeStamp);
    }

    public String getGroupListTimeStamp(){
        return getString("group_list_timestamp","0");
    }

    public void setLastUpdatedLatitude(String lat) {
        setString("last_updated_latitude",lat);
    }

    public String lastUpdatedLatitude(){
        return getString("last_updated_latitude","");
    }

    public void setLastUpdatedLongitude(String longitude) {
        setString("last_updated_longitude",longitude);
    }

    public String lastUpdatedLongitude(){
        return getString("last_updated_longitude","");
    }
}
