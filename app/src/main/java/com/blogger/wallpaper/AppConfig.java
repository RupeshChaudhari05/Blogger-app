package com.blogger.wallpaper;

import com.blogger.wallpaper.config.BrandConfig;
import com.blogger.wallpaper.utils.AppConfigExt;

import java.io.Serializable;

// import dreamspace.ads.sdk.data.AdNetworkType;

public class AppConfig extends AppConfigExt implements Serializable {

    /* -------------------------------------- INSTRUCTION : ----------------------------------------
     * This is config file used for this app, you can configure Ads, Notification, and General data from this file
     * some values are not explained and can be understood easily according to the variable name
     * value can change remotely (optional), please read documentation to follow instruction
     *
     * variable with UPPERCASE name will NOT fetch / replace with remote config
     * variable with LOWERCASE name will fetch / replace with remote config
     * See video Remote Config tutorial https://www.youtube.com/watch?v=tOKXwOTqOzA
     
     * BRANDING CONFIGURATION:
     * All "Waller" references have been replaced with BrandConfig for multi-app publishing
     * To change app name, colors, download directory: See BrandConfig.java
     * To publish multiple branded versions: Use Gradle product flavors
     * See MULTI_APP_PUBLISHING.md for detailed instructions
     
     ----------------------------------------------------------------------------------------------*/

    /* set true for fetch config with firebase remote config, */
    public static final boolean USE_REMOTE_CONFIG = false;

    /* force rtl layout direction */
    public static final boolean RTL_LAYOUT = false;

    /* config for General Application */
    public static class General {

        /* prefix name for image file saved on device - USES BRANDING CONFIG */
        public String prefix_filename = BrandConfig.getFilePrefix();
        
        /* download directory for saved images - USES BRANDING CONFIG */
        public String download_directory = BrandConfig.getDownloadDirectory();

        /* true for sort category alphabetically */
        public boolean sort_category_alphabetically = true;

        /* fill this values when you publish app not in google play */
        public String non_playstore_market_android = "";

        /* true for open link in internal app browser, not external app browser */
        public boolean open_link_in_app = true;

        /* amount data each api request listing, used on menu Home and Activity Category Details */
        public Integer listing_pagination_count = 10;

        /* 3 links below will use on setting page */
        public String privacy_policy_url = "https://quotes-on-success-english.blogspot.com/p/category.html";
        public String more_apps_url = "https://codecanyon.net/user/dream_space/portfolio?order_by=sales";
        public String contact_us_url = "https://dream-space.web.id/";
    }

    /* config for Ad Network */
    public static class Ads {

        /* enable disable ads */
        public boolean ad_enable = true;

        /* Ad networks selection,
         * Available ad networks ADMOB, FAN, UNITY, IRON SOURCE, APPLOVIN */
        // public AdNetworkType ad_network = AdNetworkType.ADMOB;

        public boolean ad_enable_gdpr = true;

        /* disable enable ads each page */
        public boolean ad_main_banner = true;
        public boolean ad_main_interstitial = true;
        public boolean ad_listing_details_banner = true;
        public boolean ad_news_details_banner = true;
        public boolean ad_category_details_banner = true;
        public boolean ad_search_banner = true;

        /* show interstitial after several action, this value for action counter */
        public int ad_inters_interval = 5;

        /* ad unit for ADMOB */
        public String ad_admob_publisher_id = "pub-3239677920600357";
        public String ad_admob_banner_unit_id = "ca-app-pub-3940256099942544/6300978111";
        public String ad_admob_interstitial_unit_id = "ca-app-pub-3940256099942544/1033173712";

        /* ad unit for FAN */
        public String ad_fan_banner_unit_id = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";
        public String ad_fan_interstitial_unit_id = "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID";

        /* ad unit for IRON SOURCE */
        public String ad_ironsource_app_key = "172a53645";
        public String ad_ironsource_banner_unit_id = "DefaultBanner";
        public String ad_ironsource_interstitial_unit_id = "DefaultInterstitial";

        /* ad unit for UNITY */
        public String ad_unity_game_id = "4297717";
        public String ad_unity_banner_unit_id = "Banner_Android";
        public String ad_unity_interstitial_unit_id = "Interstitial_Android";

        /* ad unit for APPLOVIN */
        public String ad_applovin_banner_unit_id = "ac0e1dddcf0e7584";
        public String ad_applovin_interstitial_unit_id = "cc553585d3e313f6";
    }

    /* One Signal Notification */
    public static class Notification {
        public String notif_one_signal_appid = "519bc5b1-a117-49fa-9ec5-d73ffb379e34";
    }

}
