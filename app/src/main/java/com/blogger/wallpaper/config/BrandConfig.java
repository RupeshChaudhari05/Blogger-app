package com.blogger.wallpaper.config;

import android.graphics.Color;

/**
 * BrandConfig - Centralized branding configuration for multi-app publishing
 * 
 * This class allows you to publish the same codebase multiple times with:
 * - Different app names
 * - Different colors and themes
 * - Different package names (via Gradle flavors)
 * - Different branding assets
 * 
 * To create a new app variant:
 * 1. Add a new product flavor in build.gradle
 * 2. Create buildConfig variables in that flavor
 * 3. Update the BUILD_VARIANT constant below
 * 
 * All 25 "Waller" references are now consolidated here.
 */
public class BrandConfig {

    // ─────────────────────────────────────────────────────────────────────────
    // BUILD VARIANT CONFIGURATION
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Current build variant - Change this to switch between branding configs
     * Options: "quotes", "inspire", "motivate", "wisdom"
     * 
     * This can also be read from BuildConfig.FLAVOR at runtime
     */
    private static final String BUILD_VARIANT = "quotes";  // Default variant

    // ─────────────────────────────────────────────────────────────────────────
    // APP DISPLAY NAMES (User-Facing)
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Official app name displayed on Play Store and home screen
     * This will be overridden by strings.xml from each variant
     */
    public static final String APP_NAME = "Quotes";
    
    /**
     * Short app name for UI where space is limited (keep under 12 chars)
     */
    public static final String APP_NAME_SHORT = "Quotes";
    
    /**
     * Developer/Creator attribution
     */
    public static final String DEVELOPER_NAME = "RC";
    
    /**
     * Company/Organization name (visible in about dialog)
     */
    public static final String COMPANY_NAME = "Dream Space";

    // ─────────────────────────────────────────────────────────────────────────
    // FILE SYSTEM PATHS & PREFIXES
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Directory where downloaded images are saved
     * Users will see: Android/media/{download_directory}/
     * 
     * Different values per variant:
     * - quotes: "Quotes"
     * - inspire: "InspireMe"
     * - motivate: "MotivateMe"
     * - wisdom: "WisdomHub"
     */
    public static String getDownloadDirectory() {
        return "Quotes";  // Overridden per variant
    }
    
    /**
     * Prefix for downloaded image filenames
     * Images saved as: {prefix}_{timestamp}.jpg
     * 
     * Examples:
     * - quotes_001.jpg
     * - inspire_001.jpg
     * - motivate_001.jpg
     */
    public static String getFilePrefix() {
        return "quotes_";  // Overridden per variant
    }
    
    /**
     * Database name for this variant
     * Allows multiple apps to have separate databases
     */
    public static String getDatabaseName() {
        return "Quotes_database";  // Overridden per variant
    }

    // ─────────────────────────────────────────────────────────────────────────
    // THEME & COLOR CONFIGURATION
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Primary brand color - RGB value
     * Default: Material Design Blue
     */
    public static final int PRIMARY_COLOR = Color.parseColor("#2196F3");
    
    /**
     * Secondary/Accent color
     * Default: Material Design Orange
     */
    public static final int ACCENT_COLOR = Color.parseColor("#FF9800");
    
    /**
     * Text color for dark theme
     */
    public static final int TEXT_COLOR_DARK = Color.parseColor("#212121");
    
    /**
     * Text color for light theme
     */
    public static final int TEXT_COLOR_LIGHT = Color.parseColor("#FFFFFF");
    
    /**
     * Background color for main theme
     */
    public static final int BACKGROUND_COLOR = Color.parseColor("#FAFAFA");
    
    /**
     * Success/Positive action color
     */
    public static final int SUCCESS_COLOR = Color.parseColor("#4CAF50");
    
    /**
     * Error/Warning color
     */
    public static final int ERROR_COLOR = Color.parseColor("#F44336");

    // ─────────────────────────────────────────────────────────────────────────
    // THEME CLASS NAMES (Used in AndroidManifest and resources)
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Main theme style name
     * Maps to: res/values/themes.xml Theme.Quotes
     */
    public static final String THEME_MAIN = "Theme.Quotes";
    
    /**
     * Translucent theme for dialogs
     */
    public static final String THEME_TRANSLUCENT = "Theme.Quotes.Translucent";
    
    /**
     * Dialog theme
     */
    public static final String THEME_DIALOG = "Theme.Quotes.Dialog";
    
    /**
     * App widget theme
     */
    public static final String THEME_WIDGET = "Theme.Quotes.AppWidgetContainer";
    
    /**
     * App widget parent theme
     */
    public static final String THEME_WIDGET_PARENT = "Theme.Quotes.AppWidgetContainerParent";
    
    /**
     * Widget style container
     */
    public static final String STYLE_WIDGET_CONTAINER = "Widget.Quotes.AppWidget.Container";
    
    /**
     * Widget style inner view
     */
    public static final String STYLE_WIDGET_INNER = "Widget.Quotes.AppWidget.InnerView";

    // ─────────────────────────────────────────────────────────────────────────
    // NOTIFICATION CONFIGURATION
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Notification channel name (visible in Android settings)
     */
    public static final String NOTIFICATION_CHANNEL_NAME = "Quotes";
    
    /**
     * Notification channel ID (internal, no spaces)
     */
    public static final String NOTIFICATION_CHANNEL_ID = "quotes_channel";
    
    /**
     * Default notification sound
     */
    public static final String NOTIFICATION_SOUND_URI = "content://settings/system/notification_sound";

    // ─────────────────────────────────────────────────────────────────────────
    // BRANDING VARIANT PRESETS
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Get branding config for specific variant
     * Useful when building multiple APKs from same source
     */
    public static BrandingVariant getVariant(String variantName) {
        switch (variantName != null ? variantName.toLowerCase() : BUILD_VARIANT) {
            case "inspire":
                return new BrandingVariant(
                    "InspireMe",
                    "InspireMe",
                    "InspireMe",
                    "inspire_",
                    "InspireMe_database",
                    Color.parseColor("#E91E63"), // Pink
                    Color.parseColor("#00BCD4")  // Cyan
                );
                
            case "motivate":
                return new BrandingVariant(
                    "MotivateMe",
                    "MotivateMe",
                    "MotivateMe",
                    "motivate_",
                    "MotivateMe_database",
                    Color.parseColor("#9C27B0"), // Purple
                    Color.parseColor("#8BC34A")  // Light Green
                );
                
            case "wisdom":
                return new BrandingVariant(
                    "WisdomHub",
                    "Wisdom",
                    "WisdomHub",
                    "wisdom_",
                    "WisdomHub_database",
                    Color.parseColor("#3F51B5"), // Indigo
                    Color.parseColor("#FFC107")  // Amber
                );
                
            case "quotes":
            default:
                return new BrandingVariant(
                    "Quotes",
                    "Quotes",
                    "Quotes",
                    "quotes_",
                    "Quotes_database",
                    Color.parseColor("#2196F3"), // Blue
                    Color.parseColor("#FF9800")  // Orange
                );
        }
    }
    
    // ─────────────────────────────────────────────────────────────────────────
    // BRANDING VARIANT DATA CLASS
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Encapsulates branding for a specific app variant
     */
    public static class BrandingVariant {
        public final String appName;
        public final String appNameShort;
        public final String downloadDirectory;
        public final String filePrefix;
        public final String databaseName;
        public final int primaryColor;
        public final int accentColor;
        
        public BrandingVariant(String appName, String appNameShort, String downloadDirectory,
                              String filePrefix, String databaseName,
                              int primaryColor, int accentColor) {
            this.appName = appName;
            this.appNameShort = appNameShort;
            this.downloadDirectory = downloadDirectory;
            this.filePrefix = filePrefix;
            this.databaseName = databaseName;
            this.primaryColor = primaryColor;
            this.accentColor = accentColor;
        }
    }
    
    // ─────────────────────────────────────────────────────────────────────────
    // RUNTIME DETECTION
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Get current variant from BuildConfig.FLAVOR (set by Gradle)
     * This is the recommended way at runtime
     */
    public static String getCurrentVariant() {
        // This will be automatically set by Gradle during build
        // return BuildConfig.FLAVOR;  // Uncomment after Gradle setup
        return BUILD_VARIANT;
    }
    
    /**
     * Get branding for current build variant
     */
    public static BrandingVariant getCurrentBranding() {
        return getVariant(getCurrentVariant());
    }
}
