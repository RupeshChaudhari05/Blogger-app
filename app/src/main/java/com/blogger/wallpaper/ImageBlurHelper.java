package com.blogger.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ImageBlurHelper {
    // Method to blur the given bitmap
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {
        // Create a new bitmap to store the blurred image
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        // Initialize RenderScript
        RenderScript rs = RenderScript.create(context);

        // Create an Allocation for the bitmap
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);

        // Create an Allocation for the blurred bitmap
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Create a ScriptIntrinsicBlur object
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Set the input for the ScriptIntrinsicBlur
        script.setInput(input);

        // Set the blur radius (0 < radius <= 25)
        script.setRadius(radius);

        // Perform the blur operation
        script.forEach(output);

        // Copy the blurred image back to the bitmap
        output.copyTo(blurredBitmap);

        // Release resources
        rs.destroy();

        return blurredBitmap;
    }

    // Method to decode a bitmap from resource
    public static Bitmap decodeBitmapFromResource(Context context, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    // Method to calculate the sample size for bitmap decoding
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}