package com.blogger.wallpaper.room;

import android.os.Handler;
import android.os.Looper;

import com.blogger.wallpaper.room.table.EntityCategory;
import com.blogger.wallpaper.room.table.EntityListing;
import com.blogger.wallpaper.room.table.NotificationEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * AsyncDAOHelper - Wrapper for executing DAO operations on background threads
 * 
 * MIGRATION NOTE: This class provides a bridge to execute database operations
 * without blocking the main thread. 
 * 
 * This allows the app to work without allowMainThreadQueries() while maintaining
 * backward compatibility with existing code.
 * 
 * Long-term solution: Migrate to LiveData, ViewModel, or Coroutines for reactive updates.
 * 
 * Usage:
 *   AsyncDAOHelper.with(dao)
 *       .getAllListingByPage(10, 0)
 *       .onResult(items -> updateUI(items))
 *       .execute();
 */
public class AsyncDAOHelper {

    private DAO dao;
    private ExecutorService executor;
    private Handler mainHandler;

    public interface ResultCallback<T> {
        void onResult(T result);
    }

    public interface ErrorCallback {
        void onError(Exception e);
    }

    private AsyncDAOHelper(DAO dao, ExecutorService executor) {
        this.dao = dao;
        this.executor = executor;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public static AsyncDAOHelper with(DAO dao, ExecutorService executor) {
        return new AsyncDAOHelper(dao, executor);
    }

    /**
     * Execute a background task and handle result on main thread
     */
    public <T> void executeAsync(BackgroundTask<T> task, ResultCallback<T> callback, ErrorCallback errorCallback) {
        executor.execute(() -> {
            try {
                T result = task.doInBackground();
                mainHandler.post(() -> callback.onResult(result));
            } catch (Exception e) {
                mainHandler.post(() -> errorCallback.onError(e));
            }
        });
    }

    /**
     * Execute a background task and handle result on main thread (no error handling)
     */
    public <T> void executeAsync(BackgroundTask<T> task, ResultCallback<T> callback) {
        executeAsync(task, callback, e -> {
            android.util.Log.e("AsyncDAOHelper", "Error executing DAO operation", e);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listing Operations
    // ─────────────────────────────────────────────────────────────────────────

    public void insertListing(EntityListing listing, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.insertListing(listing);
            return null;
        }, callback);
    }

    public void insertListing(EntityListing listing) {
        insertListing(listing, v -> {});
    }

    public void deleteListing(String id, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.deleteListing(id);
            return null;
        }, callback);
    }

    public void deleteListing(String id) {
        deleteListing(id, v -> {});
    }

    public void deleteAllListing(ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.deleteAllListing();
            return null;
        }, callback);
    }

    public void deleteAllListing() {
        deleteAllListing(v -> {});
    }

    public void getAllListingByPage(int limit, int offset, ResultCallback<List<EntityListing>> callback) {
        executeAsync(() -> dao.getAllListingByPage(limit, offset), callback);
    }

    public void getListingCount(ResultCallback<Integer> callback) {
        executeAsync(dao::getListingCount, callback);
    }

    public void getListing(String id, ResultCallback<EntityListing> callback) {
        executeAsync(() -> dao.getListing(id), callback);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Category Operations
    // ─────────────────────────────────────────────────────────────────────────

    public void insertCategory(EntityCategory category, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.insertCategory(category);
            return null;
        }, callback);
    }

    public void insertCategory(EntityCategory category) {
        insertCategory(category, v -> {});
    }

    public void insertAllCategories(List<EntityCategory> categories, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.insertAllCategories(categories);
            return null;
        }, callback);
    }

    public void insertAllCategories(List<EntityCategory> categories) {
        insertAllCategories(categories, v -> {});
    }

    public void getAllCategories(ResultCallback<List<EntityCategory>> callback) {
        executeAsync(dao::getAllCategories, callback);
    }

    public void deleteAllCategories(ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.deleteAllCategories();
            return null;
        }, callback);
    }

    public void deleteAllCategories() {
        deleteAllCategories(v -> {});
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Notification Operations
    // ─────────────────────────────────────────────────────────────────────────

    public void insertNotification(NotificationEntity notification, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.insertNotification(notification);
            return null;
        }, callback);
    }

    public void insertNotification(NotificationEntity notification) {
        insertNotification(notification, v -> {});
    }

    public void deleteNotification(long id, ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.deleteNotification(id);
            return null;
        }, callback);
    }

    public void deleteNotification(long id) {
        deleteNotification(id, v -> {});
    }

    public void deleteAllNotification(ResultCallback<Void> callback) {
        executeAsync(() -> {
            dao.deleteAllNotification();
            return null;
        }, callback);
    }

    public void deleteAllNotification() {
        deleteAllNotification(v -> {});
    }

    public void getNotificationByPage(int limit, int offset, ResultCallback<List<NotificationEntity>> callback) {
        executeAsync(() -> dao.getNotificationByPage(limit, offset), callback);
    }

    public void getNotification(long id, ResultCallback<NotificationEntity> callback) {
        executeAsync(() -> dao.getNotification(id), callback);
    }

    public void getNotificationUnreadCount(ResultCallback<Integer> callback) {
        executeAsync(dao::getNotificationUnreadCount, callback);
    }

    public void getNotificationCount(ResultCallback<Integer> callback) {
        executeAsync(dao::getNotificationCount, callback);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Functional Interface
    // ─────────────────────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface BackgroundTask<T> {
        T doInBackground() throws Exception;
    }
}
