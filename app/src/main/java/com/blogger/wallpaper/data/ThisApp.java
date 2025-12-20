package com.blogger.wallpaper.data;

import android.app.Application;
import android.location.Location;

import androidx.multidex.BuildConfig;

import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.advertise.AdNetworkHelper;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.notification.NotificationHelper;
import com.blogger.wallpaper.room.AppDatabase;
import com.blogger.wallpaper.room.DAO;
import com.blogger.wallpaper.room.table.NotificationEntity;
import com.blogger.wallpaper.utils.Tools;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class ThisApp extends Application {

    private static ThisApp mInstance;
    private static SharedPref sharedPref;
    private static DAO dao;

    public static synchronized SharedPref pref() {
        return sharedPref;
    }

    public static synchronized DAO dao() {
        return dao;
    }

    public static synchronized ThisApp get() {
        return mInstance;
    }

    private Location location = null;
    private List<String> categories = new ArrayList<>();

    private NotificationEntity notification;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    // used for swipe left right
    public static List<Wallpaper> itemsWallpaper = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPref = new SharedPref(this);
        dao = AppDatabase.getDb(this).get();

        initFirebase();
        initRemoteConfig();

        Tools.applyTheme(ThisApp.pref().isDarkTheme());

        // Init Firebase Notification and One Signal
        NotificationHelper.init(this);
    }

    private void initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        if (!AppConfig.USE_REMOTE_CONFIG) return;
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 60)
                .setFetchTimeoutInSeconds(4)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
    }

    private void initFirebase() {
        // Obtain the Firebase Analytics.
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics.getInstance(this);
    }


    public void initAds(){
        // initialize ad network
        AdNetworkHelper.init(this);
    }


    public FirebaseRemoteConfig getFirebaseRemoteConfig() {
        return firebaseRemoteConfig;
    }

    public NotificationEntity getNotification() {
        return notification;
    }

    public void setNotification(NotificationEntity notification) {
        this.notification = notification;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        if (AppConfig.general.sort_category_alphabetically) Collections.sort(categories);
        this.categories = categories;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
