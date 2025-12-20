package com.blogger.wallpaper.utils;

import com.blogger.wallpaper.model.Listing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataFormatter {

    public static Listing formatData(String id, String type, String title, String published, String updated, String content, String link, List<String> category) {
        Listing listing = new Listing();

        // Set basic fields
        listing.id = id;
        listing.type = type;
        listing.title = title;
        listing.content = content;
        listing.link = link;
        listing.category = category;

        // Format date fields
        listing.published = formatDate(published);
        listing.updated = formatDate(updated);

        return listing;
    }

    private static String formatDate(String dateString) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
