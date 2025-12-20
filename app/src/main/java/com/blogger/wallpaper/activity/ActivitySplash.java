package com.blogger.wallpaper.activity;

import static com.blogger.wallpaper.utils.AppConfigExt.BLOGGER_URL_PAGES;

import static com.blogger.wallpaper.utils.Tools.extractUniqueLabelsNew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.R;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.databinding.ActivitySplashBinding;
import com.blogger.wallpaper.utils.AppConfigExt;
import com.blogger.wallpaper.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Objects;


public class ActivitySplash extends AppCompatActivity {

    // activity transition
    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivitySplash.class);
        activity.startActivity(i);
    }

    private ActivitySplashBinding binding;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
        binding.title.startAnimation(animation);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
        binding.shimmer.startAnimation(animation2);

        if (AppConfig.USE_REMOTE_CONFIG) {
            requestRemoteConfig();
            // add timer to prevent too long waiting about 10 sec
            new Handler().postDelayed(() -> {
                if (ActivitySplash.active)
                    dialogFailedRemoteConfig(getString(R.string.message_failed_config));
            }, 10 * 1000);
        } else {
            requestCategoriesData();
        }

        Tools.fullScreen(this);
    }

    private void requestRemoteConfig() {
        Log.d("REMOTE_CONFIG", "requestRemoteConfig");
        FirebaseRemoteConfig firebaseRemoteConfig = ThisApp.get().getFirebaseRemoteConfig();
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, (OnCompleteListener<Boolean>) task -> {
            if (!ActivitySplash.active || (alertDialog != null && alertDialog.isShowing())) {
                return;
            }
            if (task.isSuccessful()) {
                Log.d("REMOTE_CONFIG", "SUCCESS");
                boolean updated = task.getResult();
                AppConfigExt.setFromRemoteConfig(firebaseRemoteConfig);
                requestCategoriesData();
            } else {
                Log.d("REMOTE_CONFIG", "FAILED");
                dialogFailedRemoteConfig(getString(R.string.message_failed_config));
            }
        });
    }

    public void dialogFailedRemoteConfig(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.RETRY, (dialog, which) -> {
            dialog.dismiss();
            requestRemoteConfig();
        });
        alertDialog = builder.show();
    }

    public void dialogFailedCategories(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.RETRY, (dialog, which) -> {
            dialog.dismiss();
            requestRemoteConfig();
        });
        alertDialog = builder.show();
    }
    private void requestCategoriesData() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BLOGGER_URL_PAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle response
                //Log.d("ZZZZ",response);
                ThisApp.get().setCategories(extractUniqueLabelsNew(response));
                startActivityMain();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                if (error != null) Log.e("Error", Objects.requireNonNull(error.getMessage()));
                dialogFailedCategories(getString(R.string.message_failed_server));
            }
        });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(ThisApp.get()).add(stringRequest);
    }

    private void startActivityMain() {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        Intent i = new Intent(ActivitySplash.this, ActivityMain.class);
        startActivity(i);
        finish();
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }
}