package com.blogger.wallpaper;

/**
 * Central constants file — edit this single file to adjust all app-wide magic strings and numbers.
 *
 * Categories:
 *  - SharedPreferences file names and keys
 *  - Intent extras (activity navigation)
 *  - Permission request codes
 *  - Social media export dimensions
 *  - Blogger page titles
 *  - Timing / intervals
 *  - Image processing
 *  - Room database table names (informational — must match @Entity annotations)
 */
public final class Constants {

    private Constants() {
        // Utility class — do not instantiate
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SharedPreferences file names
    // ─────────────────────────────────────────────────────────────────────────
    /** Main SharedPreferences file used by SharedPref (data package). */
    public static final String PREF_FILE_MAIN   = "MAIN_PREF";
    /** Secondary SharedPreferences file used by PrefManager. */
    public static final String PREF_FILE_STATUS = "status_app";

    // ─────────────────────────────────────────────────────────────────────────
    // SharedPreferences keys — PREF_FILE_MAIN
    // ─────────────────────────────────────────────────────────────────────────
    public static final String PREF_DARK_THEME       = "DARK_THEME";
    public static final String PREF_FCM_REG_ID       = "FCM_PREF_KEY";
    public static final String PREF_TEXT_SIZE        = "TEXT_SIZE_NEWS";
    public static final String PREF_SUBSCRIBE_NOTIF  = "SUBSCRIBE_NOTIF";
    public static final String PREF_FIRST_LAUNCH     = "FIRST_LAUNCH";
    public static final String PREF_PUSH_NOTIF       = "SETTINGS_PUSH_NOTIF";
    public static final String PREF_RINGTONE         = "SETTINGS_RINGTONE";
    public static final String PREF_NEXT_PAGE_TOKEN  = "NEXT_PAGE_TOKEN_VALUE";
    public static final String PREF_QUOTES_THEME     = "QUOTES_THEME_VALUE";
    public static final String PREF_USER_TAG         = "TAG";
    public static final String PREF_IMAGE_URLS       = "IMAGE_URLS_KEY";
    public static final String PREF_VIDEO_URLS       = "VIDEO_URLS_KEY";
    public static final String PREF_IMAGE_CACHE      = "SETTINGS_IMG_CACHE";
    public static final String PREF_APP_STATUS       = "APP_STATUS";
    public static final String PREF_INTERS_COUNT     = "INTERS_COUNT";

    // ─────────────────────────────────────────────────────────────────────────
    // SharedPreferences keys — PREF_FILE_STATUS (PrefManager)
    // ─────────────────────────────────────────────────────────────────────────
    public static final String PREF_NIGHT_MODE = "NightMode";

    // ─────────────────────────────────────────────────────────────────────────
    // Quotes theme values (stored in PREF_QUOTES_THEME)
    // ─────────────────────────────────────────────────────────────────────────
    public static final String THEME_DEFAULT = "DEF";
    public static final String THEME_YELLOW  = "YELLOW";

    // ─────────────────────────────────────────────────────────────────────────
    // Default preference values
    // ─────────────────────────────────────────────────────────────────────────
    public static final String DEFAULT_RINGTONE_URL = "content://settings/system/notification_sound";
    public static final String DEFAULT_TEXT_SIZE    = "small";

    // ─────────────────────────────────────────────────────────────────────────
    // Intent extras — used as keys when navigating between activities
    // ─────────────────────────────────────────────────────────────────────────
    public static final String EXTRA_OBJECT        = "key.EXTRA_OBJECT";
    public static final String EXTRA_POSITION      = "key.EXTRA_POSITION";
    public static final String EXTRA_CATEGORY      = "key.CATEGORY";
    public static final String EXTRA_WALLPAPER     = "key.WALLPAPER";
    public static final String EXTRA_FROM_NOTIF    = "key.EXTRA_FROM_NOTIF";
    public static final String EXTRA_FROM_POSITION = "key.EXTRA_FROM_POSITION";
    public static final String EXTRA_WEB_URL       = "key.EXTRA_OBJC";
    public static final String EXTRA_FULL_IMG      = "EXTRA_IMG";
    public static final String EXTRA_FULL_POS      = "EXTRA_POS";
    public static final String EXTRA_EDIT_IMAGE    = "image";

    // ─────────────────────────────────────────────────────────────────────────
    // Permission / activity-result request codes
    // ─────────────────────────────────────────────────────────────────────────
    public static final int RC_STORAGE_PERMISSION = 500;
    public static final int RC_GOOGLE_PLAY_UPDATE = 200;
    public static final int RC_PICK_IMAGE         = 1;

    // ─────────────────────────────────────────────────────────────────────────
    // Social media export sizes (pixels)
    // ─────────────────────────────────────────────────────────────────────────
    public static final int SIZE_INSTAGRAM_W = 1080;
    public static final int SIZE_INSTAGRAM_H = 1080;
    public static final int SIZE_FACEBOOK_W  = 1200;
    public static final int SIZE_FACEBOOK_H  = 630;
    public static final int SIZE_WHATSAPP_W  = 1080;
    public static final int SIZE_WHATSAPP_H  = 1920;
    public static final int SIZE_GENERIC_W   = 800;
    public static final int SIZE_GENERIC_H   = 600;

    // ─────────────────────────────────────────────────────────────────────────
    // Blogger page titles (used in Tools.extractUniqueLabelsNew)
    // ─────────────────────────────────────────────────────────────────────────
    public static final String PAGE_TITLE_CATEGORY  = "category";
    public static final String PAGE_TITLE_BG_IMAGES = "Background Images";
    public static final String PAGE_TITLE_BG_VIDEO  = "Background Video";

    // ─────────────────────────────────────────────────────────────────────────
    // Timing / intervals
    // ─────────────────────────────────────────────────────────────────────────
    /** Maximum wait time for Firebase Remote Config fetch before falling back (milliseconds). */
    public static final int REMOTE_CONFIG_TIMEOUT_MS = 10_000;

    // ─────────────────────────────────────────────────────────────────────────
    // Image processing
    // ─────────────────────────────────────────────────────────────────────────
    public static final float DEFAULT_BLUR_RADIUS = 10f;

    // ─────────────────────────────────────────────────────────────────────────
    // Room database table names
    // NOTE: These values must exactly match the tableName in each @Entity annotation.
    //       Renaming after release requires a Room DB migration.
    // ─────────────────────────────────────────────────────────────────────────
    public static final String TABLE_FAVORITE     = "favorite";
    public static final String TABLE_NOTIFICATION = "notification";
    public static final String TABLE_CATEGORY     = "category";
}
