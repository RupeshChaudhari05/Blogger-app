package com.blogger.wallpaper.utils;

import com.blogger.wallpaper.AppConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class AppConfigExt {

    public static final String CATEGORY = "category";
    public static final String SEARCH = "search";
    public static final String API_KEY = "AIzaSyCKWz8dKnoDEZ0eHVWdHIqnJm6qTGacQ5U";
    public static final String BLOG_ID = "8048696154340688049";
    public static final String BLOGGER_URL_ALL_POST = "https://www.googleapis.com/blogger/v3/blogs/" + BLOG_ID + "/posts?key=" + API_KEY + "&maxResults=10";
    public static final String BLOGGER_URL_ALL_POST_TOP_ONE = "https://www.googleapis.com/blogger/v3/blogs/" + BLOG_ID + "/posts?key=" + API_KEY + "&maxResults=1";
    public  static final String BLOGGER_URL_PAGES = "https://www.googleapis.com/blogger/v3/blogs/" + BLOG_ID + "/pages?key=" + API_KEY;
    public static final String BLOGGER_SEARCH_URL = "https://www.googleapis.com/blogger/v3/blogs/" + BLOG_ID + "/posts/search?key=" + API_KEY + "&maxResults=10";
    public static String geturl(String type, String url, String pagetoken) {
        String token = (!pagetoken.isEmpty()) ? "&pageToken=" + pagetoken : "";
        try {
            if (Objects.equals(type, CATEGORY)) {
                if (url != null && !url.isEmpty()) {
                    String encodedLabel = URLEncoder.encode(url, "UTF-8");
                    return BLOGGER_URL_ALL_POST + "&labels=" + encodedLabel + "&orderBy=published" + token;
                } else {
                    return BLOGGER_URL_ALL_POST + "&orderBy=published" + token;
                }
            } else if (Objects.equals(type, SEARCH)) {
                if (url != null && !url.isEmpty()) {
                    String encodedQuery = URLEncoder.encode(url, "UTF-8");
                    return BLOGGER_SEARCH_URL + "&q=" + encodedQuery + "&orderBy=published" + token;
                } else {
                    return BLOGGER_SEARCH_URL + "&orderBy=published" + token;
                }
            } else {
                return BLOGGER_URL_ALL_POST + "&orderBy=published" + token;
            }
        } catch (UnsupportedEncodingException e) {
            // Fallback to unencoded if encoding fails
            if (Objects.equals(type, CATEGORY)) {
                if (url != null && !url.isEmpty()) {
                    return BLOGGER_URL_ALL_POST + "&labels=" + url + "&orderBy=published" + token;
                } else {
                    return BLOGGER_URL_ALL_POST + "&orderBy=published" + token;
                }
            } else if (Objects.equals(type, SEARCH)) {
                if (url != null && !url.isEmpty()) {
                    return BLOGGER_SEARCH_URL + "&q=" + url + "&orderBy=published" + token;
                } else {
                    return BLOGGER_SEARCH_URL + "&orderBy=published" + token;
                }
            } else {
                return BLOGGER_URL_ALL_POST + "&orderBy=published" + token;
            }
        }
    }
    /* --------------- DONE EDIT CODE BELOW ------------------------------------------------------ */

    // define static variable for all config class
    public static AppConfig.General general = new AppConfig.General();
    public static AppConfig.Ads ads = new AppConfig.Ads();
    public static AppConfig.Notification notification = new AppConfig.Notification();

    // Set data from remote config
    public static void setFromRemoteConfig(FirebaseRemoteConfig remote) {

        // fetch General Config with data from remote config

        if (!remote.getString("sort_category_alphabetically").isEmpty()) {
            AppConfig.general.sort_category_alphabetically = Boolean.parseBoolean(remote.getString("sort_category_alphabetically"));
        }

        if (!remote.getString("non_playstore_market_android").isEmpty()) {
            AppConfig.general.non_playstore_market_android = remote.getString("non_playstore_market_android");
        }

        if (!remote.getString("open_link_in_app").isEmpty()) {
            AppConfig.general.open_link_in_app = Boolean.parseBoolean(remote.getString("open_link_in_app"));
        }

        if (!remote.getString("listing_pagination_count").isEmpty()) {
            try {
                AppConfig.general.listing_pagination_count = Integer.parseInt(remote.getString("listing_pagination_count"));
            } catch (Exception e) {
            }
        }

        if (!remote.getString("privacy_policy_url").isEmpty()) {
            AppConfig.general.privacy_policy_url = remote.getString("privacy_policy_url");
        }
        if (!remote.getString("more_apps_url").isEmpty()) {
            AppConfig.general.more_apps_url = remote.getString("more_apps_url");
        }
        if (!remote.getString("contact_us_url").isEmpty()) {
            AppConfig.general.contact_us_url = remote.getString("contact_us_url");
        }

        // fetch Ads Config with data from remote config
        if (!remote.getString("ad_enable").isEmpty()) {
            AppConfig.ads.ad_enable = Boolean.parseBoolean(remote.getString("ad_enable"));
        }
        if (!remote.getString("ad_network").isEmpty()) {
            try {
                // AppConfig.ads.ad_network = AdNetworkType.valueOf(remote.getString("ad_network"));
            } catch (Exception e) {
            }
        }
        if (!remote.getString("ad_enable_gdpr").isEmpty()) {
            AppConfig.ads.ad_enable_gdpr = Boolean.parseBoolean(remote.getString("ad_enable_gdpr"));
        }

        if (!remote.getString("ad_main_banner").isEmpty()) {
            AppConfig.ads.ad_main_banner = Boolean.parseBoolean(remote.getString("ad_main_banner"));
        }
        if (!remote.getString("ad_main_interstitial").isEmpty()) {
            AppConfig.ads.ad_main_interstitial = Boolean.parseBoolean(remote.getString("ad_main_interstitial"));
        }
        if (!remote.getString("ad_listing_details_banner").isEmpty()) {
            AppConfig.ads.ad_listing_details_banner = Boolean.parseBoolean(remote.getString("ad_listing_details_banner"));
        }
        if (!remote.getString("ad_news_details_banner").isEmpty()) {
            AppConfig.ads.ad_news_details_banner = Boolean.parseBoolean(remote.getString("ad_news_details_banner"));
        }
        if (!remote.getString("ad_category_details_banner").isEmpty()) {
            AppConfig.ads.ad_category_details_banner = Boolean.parseBoolean(remote.getString("ad_category_details_banner"));
        }
        if (!remote.getString("ad_search_banner").isEmpty()) {
            AppConfig.ads.ad_search_banner = Boolean.parseBoolean(remote.getString("ad_search_banner"));
        }

        if (!remote.getString("ad_inters_interval").isEmpty()) {
            try {
                AppConfig.ads.ad_inters_interval = Integer.parseInt(remote.getString("ad_inters_interval"));
            } catch (Exception e) {
            }
        }

        if (!remote.getString("ad_admob_publisher_id").isEmpty()) {
            AppConfig.ads.ad_admob_publisher_id = remote.getString("ad_admob_publisher_id");
        }
        if (!remote.getString("ad_admob_banner_unit_id").isEmpty()) {
            AppConfig.ads.ad_admob_banner_unit_id = remote.getString("ad_admob_banner_unit_id");
        }
        if (!remote.getString("ad_admob_interstitial_unit_id").isEmpty()) {
            AppConfig.ads.ad_admob_interstitial_unit_id = remote.getString("ad_admob_interstitial_unit_id");
        }

        if (!remote.getString("ad_fan_banner_unit_id").isEmpty()) {
            AppConfig.ads.ad_fan_banner_unit_id = remote.getString("ad_fan_banner_unit_id");
        }
        if (!remote.getString("ad_fan_interstitial_unit_id").isEmpty()) {
            AppConfig.ads.ad_fan_banner_unit_id = remote.getString("ad_fan_banner_unit_id");
        }

        if (!remote.getString("ad_ironsource_app_key").isEmpty()) {
            AppConfig.ads.ad_ironsource_app_key = remote.getString("ad_ironsource_app_key");
        }
        if (!remote.getString("ad_ironsource_banner_unit_id").isEmpty()) {
            AppConfig.ads.ad_ironsource_banner_unit_id = remote.getString("ad_ironsource_banner_unit_id");
        }
        if (!remote.getString("ad_ironsource_interstitial_unit_id").isEmpty()) {
            AppConfig.ads.ad_ironsource_interstitial_unit_id = remote.getString("ad_ironsource_interstitial_unit_id");
        }

        if (!remote.getString("ad_unity_game_id").isEmpty()) {
            AppConfig.ads.ad_unity_game_id = remote.getString("ad_unity_game_id");
        }
        if (!remote.getString("ad_unity_banner_unit_id").isEmpty()) {
            AppConfig.ads.ad_unity_banner_unit_id = remote.getString("ad_unity_banner_unit_id");
        }
        if (!remote.getString("ad_unity_interstitial_unit_id").isEmpty()) {
            AppConfig.ads.ad_unity_interstitial_unit_id = remote.getString("ad_unity_interstitial_unit_id");
        }

        if (!remote.getString("ad_applovin_banner_unit_id").isEmpty()) {
            AppConfig.ads.ad_applovin_banner_unit_id = remote.getString("ad_applovin_banner_unit_id");
        }
        if (!remote.getString("ad_applovin_interstitial_unit_id").isEmpty()) {
            AppConfig.ads.ad_applovin_interstitial_unit_id = remote.getString("ad_applovin_interstitial_unit_id");
        }

        // fetch Notification Config with data from remote config
        if (!remote.getString("notif_one_signal_appid").isEmpty()) {
            AppConfig.notification.notif_one_signal_appid = remote.getString("notif_one_signal_appid");
        }
    }
}
