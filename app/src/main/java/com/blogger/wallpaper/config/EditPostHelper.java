package com.blogger.wallpaper.config;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Helper class for EditPost activity to provide user-friendly operations
 * Handles saving, sharing, and UI state management
 */
public class EditPostHelper {

    private Activity activity;
    private RelativeLayout targetLayout;
    private Toast currentToast;
    private OnOperationListener operationListener;

    public interface OnOperationListener {
        void onOperationStart(String operation);
        void onOperationSuccess(String message);
        void onOperationError(String error);
        void onOperationComplete();
    }

    public EditPostHelper(Activity activity, RelativeLayout targetLayout) {
        this.activity = activity;
        this.targetLayout = targetLayout;
    }

    /**
     * Set listener for operation callbacks
     */
    public void setOperationListener(OnOperationListener listener) {
        this.operationListener = listener;
    }

    /**
     * Show user-friendly toast message with timeout management
     */
    public void showToast(String message) {
        cancelPreviousToast();
        currentToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    /**
     * Show long toast message
     */
    public void showLongToast(String message) {
        cancelPreviousToast();
        currentToast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        currentToast.show();
    }

    /**
     * Cancel any previous toast to avoid stacking
     */
    private void cancelPreviousToast() {
        if (currentToast != null) {
            currentToast.cancel();
        }
    }

    /**
     * Create bitmap from the layout
     */
    public Bitmap createBitmapFromLayout() {
        try {
            Bitmap bitmap = Bitmap.createBitmap(
                    targetLayout.getWidth(),
                    targetLayout.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            targetLayout.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            notifyError("Failed to create image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Notify operation start
     */
    public void notifyOperationStart(String operation) {
        showToast("Processing: " + operation + "...");
        if (operationListener != null) {
            operationListener.onOperationStart(operation);
        }
    }

    /**
     * Notify operation success
     */
    public void notifySuccess(String message) {
        showToast(message);
        if (operationListener != null) {
            operationListener.onOperationSuccess(message);
        }
    }

    /**
     * Notify operation error
     */
    public void notifyError(String error) {
        showLongToast("Error: " + error);
        if (operationListener != null) {
            operationListener.onOperationError(error);
        }
    }

    /**
     * Notify operation complete
     */
    public void notifyComplete() {
        if (operationListener != null) {
            operationListener.onOperationComplete();
        }
    }

    /**
     * Get a user-friendly operation status message
     */
    public String getStatusMessage(String action) {
        switch (action) {
            case "save":
                return "Quote saved successfully!";
            case "share_text":
                return "Quote ready to share as text";
            case "share_image":
                return "Quote ready to share as image";
            case "font_changed":
                return "Font style updated";
            case "color_changed":
                return "Text color updated";
            case "blur_applied":
                return "Blur effect applied";
            case "background_changed":
                return "Background updated";
            default:
                return action + " completed";
        }
    }

    /**
     * Release resources
     */
    public void cleanup() {
        cancelPreviousToast();
        if (operationListener != null) {
            operationListener.onOperationComplete();
        }
    }
}
