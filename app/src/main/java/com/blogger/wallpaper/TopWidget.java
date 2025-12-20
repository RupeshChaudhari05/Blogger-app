package com.blogger.wallpaper;

import static com.blogger.wallpaper.utils.AppConfigExt.BLOGGER_URL_ALL_POST_TOP_ONE;
import static com.blogger.wallpaper.utils.AppConfigExt.CATEGORY;
import static com.blogger.wallpaper.utils.AppConfigExt.geturl;
import static com.blogger.wallpaper.utils.Tools.parseJsonResponse;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.model.Listing;
import com.blogger.wallpaper.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;


public class TopWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.top_widget);

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BLOGGER_URL_ALL_POST_TOP_ONE;
        Log.d("QQQ",url);
        // Request a string response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse your JSON response and update the widget
                        List<Listing> sample = parseJsonResponse(String.valueOf(response),context);
                        Document doc = Jsoup.parse(sample.get(0).content);
                        // Select all list items within unordered list
                        Elements listItems = doc.select("ul > li,blockquote,p");
                        // Get the text from the list item
                        String text = "";
                        for (Element listItem : listItems) {
                            text += listItem.text() + "\n";
                        }

                        views.setTextViewText(R.id.appwidget_text, text);

                        views.setFloat(R.id.appwidget_text, "setTextSize", 15);
                        // Update the widget
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);

    }
}
