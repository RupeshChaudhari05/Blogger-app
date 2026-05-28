package com.blogger.wallpaper.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.blogger.wallpaper.Constants;
import com.google.gson.Gson;

public class SharedPref {

    private static SharedPref mInstance;

    /** Kept for backward compatibility — static imports exist in Tools, EditPost, AdapterListing. */
    public static final String DEF    = Constants.THEME_DEFAULT;
    public static final String Yellow = Constants.THEME_YELLOW;

    public static synchronized SharedPref get() {
        return mInstance;
    }

    private Context context;
    private SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        mInstance = this;
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE_MAIN, Context.MODE_PRIVATE);
    }

    // Preference for theme
    public void setDarkTheme(boolean flag) {
        sharedPreferences.edit().putBoolean(Constants.PREF_DARK_THEME, flag).apply();
    }

    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean(Constants.PREF_DARK_THEME, false);
    }

    // Preference for Fcm register
    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString(Constants.PREF_FCM_REG_ID, fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString(Constants.PREF_FCM_REG_ID, null);
    }

    public boolean isFcmRegIdEmpty() {
        return TextUtils.isEmpty(getFcmRegId());
    }

    public void setTextSize(String value) {
        sharedPreferences.edit().putString(Constants.PREF_TEXT_SIZE, value).apply();
    }

    public String getTextSize() {
        return sharedPreferences.getString(Constants.PREF_TEXT_SIZE, Constants.DEFAULT_TEXT_SIZE);
    }

    public void setSubscibeNotif(boolean value) {
        sharedPreferences.edit().putBoolean(Constants.PREF_SUBSCRIBE_NOTIF, value).apply();
    }

    public boolean isSubscibeNotif() {
        return sharedPreferences.getBoolean(Constants.PREF_SUBSCRIBE_NOTIF, false);
    }

    // Preference for first launch
    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean(Constants.PREF_FIRST_LAUNCH, flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(Constants.PREF_FIRST_LAUNCH, true);
    }

    // Preference for settings
    public void setPushNotification(boolean value) {
        sharedPreferences.edit().putBoolean(Constants.PREF_PUSH_NOTIF, value).apply();
    }

    public boolean getPushNotification() {
        return sharedPreferences.getBoolean(Constants.PREF_PUSH_NOTIF, true);
    }

    public String getRingtone() {
        return sharedPreferences.getString(Constants.PREF_RINGTONE, Constants.DEFAULT_RINGTONE_URL);
    }

    public void setTokenValue(String value) {
        sharedPreferences.edit().putString(Constants.PREF_NEXT_PAGE_TOKEN, value).apply();
    }

    public String getTokenValue() {
        return sharedPreferences.getString(Constants.PREF_NEXT_PAGE_TOKEN, "");
    }

    public void setQuotesThemeValue(String value) {
        sharedPreferences.edit().putString(Constants.PREF_QUOTES_THEME, value).apply();
    }

    public String getQuotesThemeValue() {
        return sharedPreferences.getString(Constants.PREF_QUOTES_THEME, Constants.THEME_DEFAULT);
    }

    public void setUserTagValue(String value) {
        sharedPreferences.edit().putString(Constants.PREF_USER_TAG, value).apply();
    }

    public String getUserTagValue() {
        return sharedPreferences.getString(Constants.PREF_USER_TAG, "");
    }

    public void setImageUrls(String[] urls) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(Constants.PREF_IMAGE_URLS, gson.toJson(urls)).apply();
    }

    public String[] getImageUrls() {
        String json = sharedPreferences.getString(Constants.PREF_IMAGE_URLS, null);
        if (json != null) {
            return new Gson().fromJson(json, String[].class);
        }
        return new String[0];
    }

    public void setVideoUrls(String[] urls) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(Constants.PREF_VIDEO_URLS, gson.toJson(urls)).apply();
    }

    public String[] getVideoUrls() {
        String json = sharedPreferences.getString(Constants.PREF_VIDEO_URLS, null);
        if (json != null) {
            return new Gson().fromJson(json, String[].class);
        }
        return new String[0];
    }

    public void setImageCache(boolean value) {
        sharedPreferences.edit().putBoolean(Constants.PREF_IMAGE_CACHE, value).apply();
    }

    public boolean getImageCache() {
        return sharedPreferences.getBoolean(Constants.PREF_IMAGE_CACHE, true);
    }

    public String getAppStatus() {
        return sharedPreferences.getString(Constants.PREF_APP_STATUS, "");
    }

    public void setAppStatus(String status) {
        sharedPreferences.edit().putString(Constants.PREF_APP_STATUS, status).apply();
    }

    public void setIntersCounter(int counter) {
        sharedPreferences.edit().putInt(Constants.PREF_INTERS_COUNT, counter).apply();
    }

    public int getIntersCounter() {
        return sharedPreferences.getInt(Constants.PREF_INTERS_COUNT, 0);
    }

    public void clearIntersCounter() {
        sharedPreferences.edit().putInt(Constants.PREF_INTERS_COUNT, 0).apply();
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
