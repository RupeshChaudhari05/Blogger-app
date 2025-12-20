package com.blogger.wallpaper.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.blogger.wallpaper.R;
import com.blogger.wallpaper.room.table.EntityCategory;
import com.blogger.wallpaper.room.table.EntityListing;
import com.blogger.wallpaper.room.table.NotificationEntity;


@Database(entities = {EntityListing.class, NotificationEntity.class, EntityCategory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DAO get();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDb(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, context.getString(R.string.app_name) + "_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}