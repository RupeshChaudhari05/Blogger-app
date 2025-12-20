package com.blogger.wallpaper.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import javax.xml.validation.Validator;

public class SharedPref {

    private static SharedPref mInstance;
    private static final String NEXT_PAGE_TOKEN_VALUE = "NEXT_PAGE_TOKEN_VALUE";
    private static final String QUOTES_THEME_VALUE = "QUOTES_THEME_VALUE";
    private static final String TAG = "TAG";
    public static String DEF ="DEF";
    public static String Yellow ="YELLOW";
    public static synchronized SharedPref get() {
        return mInstance;
    }

    private static String default_ringtone_url = "content://settings/system/notification_sound";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;

    public SharedPref(Context context) {
        mInstance = this;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Preference for theme
    public void setDarkTheme(boolean flag) {
        sharedPreferences.edit().putBoolean("DARK_THEME", flag).apply();
    }

    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean("DARK_THEME", false);
    }

    // Preference for Fcm register
    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString("FCM_PREF_KEY", fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString("FCM_PREF_KEY", null);
    }

    public boolean isFcmRegIdEmpty() {
        return TextUtils.isEmpty(getFcmRegId());
    }

    public void setTextSize(String value) {
        sharedPreferences.edit().putString("TEXT_SIZE_NEWS", value).apply();
    }

    public String getTextSize() {
        return sharedPreferences.getString("TEXT_SIZE_NEWS", "small");
    }

    public void setSubscibeNotif(boolean value) {
        sharedPreferences.edit().putBoolean("SUBSCRIBE_NOTIF", value).apply();
    }

    public boolean isSubscibeNotif() {
        return sharedPreferences.getBoolean("SUBSCRIBE_NOTIF", false);
    }

    // Preference for first launch
    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean("FIRST_LAUNCH", flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean("FIRST_LAUNCH", true);
    }

    // Preference for settings
    public void setPushNotification(boolean value) {
        sharedPreferences.edit().putBoolean("SETTINGS_PUSH_NOTIF", value).apply();
    }

    public boolean getPushNotification() {
        return sharedPreferences.getBoolean("SETTINGS_PUSH_NOTIF", true);
    }

    public String getRingtone() {
        return sharedPreferences.getString("SETTINGS_RINGTONE", default_ringtone_url);
    }
    public void setTokenValue(String value){
        sharedPreferences.edit().putString(NEXT_PAGE_TOKEN_VALUE, value).apply();
    }
    public String getTokenValue(){
       return sharedPreferences.getString(NEXT_PAGE_TOKEN_VALUE,"");
    }

    public void setQuotesThemeValue(String value){
        sharedPreferences.edit().putString(QUOTES_THEME_VALUE, value).apply();
    }
    public String getQuotesThemeValue(){
        return sharedPreferences.getString(QUOTES_THEME_VALUE,DEF);
    }

    public void setUserTagValue(String value){
        sharedPreferences.edit().putString(TAG, value).apply();
    }
    public String getUserTagValue(){
        return sharedPreferences.getString(TAG,"");
    }

    public void setImageUrls(String[] urls) {
        // Convert the array to a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(urls);

        // Save the JSON string to SharedPreferences
        sharedPreferences.edit().putString("IMAGE_URLS_KEY", json).apply();
    }

    public String[] getImageUrls() {
        // Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("IMAGE_URLS_KEY", null);

        // Check if the JSON string is not null
        if (json != null) {
            // Convert the JSON string back to an array of strings
            Gson gson = new Gson();
            return gson.fromJson(json, String[].class);
        } else {
            // If no array is found, return an empty array
            return new String[0];
        }
    }

    public void setVideoUrls(String[] urls) {
        // Convert the array to a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(urls);

        // Save the JSON string to SharedPreferences
        sharedPreferences.edit().putString("VIDEO_URLS_KEY", json).apply();
    }

    public String[] getVideoUrls() {
        // Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("VIDEO_URLS_KEY", null);

        // Check if the JSON string is not null
        if (json != null) {
            // Convert the JSON string back to an array of strings
            Gson gson = new Gson();
            return gson.fromJson(json, String[].class);
        } else {
            // If no array is found, return an empty array
            return new String[0];
        }
    }
    public void setImageCache(boolean value) {
        sharedPreferences.edit().putBoolean("SETTINGS_IMG_CACHE", value).apply();
    }

    public boolean getImageCache() {
        return sharedPreferences.getBoolean("SETTINGS_IMG_CACHE", true);
    }

    public String getAppStatus() {
        return sharedPreferences.getString("APP_STATUS", "");
    }

    public void setAppStatus(String status) {
        sharedPreferences.edit().putString("APP_STATUS", status).apply();
    }

    // Preference for first launch
    public void setIntersCounter(int counter) {
        sharedPreferences.edit().putInt("INTERS_COUNT", counter).apply();
    }

    public int getIntersCounter() {
        return sharedPreferences.getInt("INTERS_COUNT", 0);
    }

    public void clearIntersCounter() {
        sharedPreferences.edit().putInt("INTERS_COUNT", 0).apply();
    }

    /**
     * To save dialog permission state
     */
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

}
