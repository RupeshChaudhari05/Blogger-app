package com.blogger.wallpaper.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.blogger.wallpaper.R;
import com.blogger.wallpaper.room.table.EntityCategory;
import com.blogger.wallpaper.room.table.EntityListing;
import com.blogger.wallpaper.room.table.NotificationEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {EntityListing.class, NotificationEntity.class, EntityCategory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO get();

    private static AppDatabase INSTANCE;
    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class,
                            context.getString(R.string.app_name) + "_database")
                    // FIXED: Removed allowMainThreadQueries() - database operations now use ExecutorService
                    // for background thread execution. Prevents ANR crashes and UI freezing.
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    /**
     * Returns the executor service for background database operations.
     * Use this when making DAO calls to avoid blocking the main thread.
     */
    public static ExecutorService getDatabaseExecutor() {
        return databaseExecutor;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}