package com.blogger.wallpaper.activity;

import static com.blogger.wallpaper.utils.AppConfigExt.CATEGORY;
import static com.blogger.wallpaper.utils.AppConfigExt.geturl;
import static com.blogger.wallpaper.utils.Tools.parseJsonResponse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.R;
import com.blogger.wallpaper.adapter.AdapterListener;
import com.blogger.wallpaper.adapter.AdapterListing;
import com.blogger.wallpaper.advertise.AdNetworkHelper;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.databinding.ActivityCategoryDetailBinding;
import com.blogger.wallpaper.model.Listing;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.utils.Tools;

import java.util.ArrayList;
import java.util.List;


public class ActivityCategoryDetail extends AppCompatActivity {

    private static final String EXTRA_OBJC_CATEGORY = "key.CATEGORY";
    private static final String EXTRA_OBJC_WALLPAPER = "key.WALLPAPER";

    public static void navigate(Activity activity, String data) {
        Intent i = new Intent(activity, ActivityCategoryDetail.class);
        i.putExtra(EXTRA_OBJC_CATEGORY, data);
        activity.startActivity(i);
    }

    public static void navigate(Activity activity, Wallpaper data) {
        Intent i = new Intent(activity, ActivityCategoryDetail.class);
        i.putExtra(EXTRA_OBJC_WALLPAPER, data);
        activity.startActivity(i);
    }

    private ActivityCategoryDetailBinding binding;
    private AdapterListing adapter;
    private boolean allLoaded = false;
    private String category;
    private Wallpaper wallpaper;
    private List<Wallpaper> wallpapers = new ArrayList<>();
    private boolean wallpaper_mode = false;
    public int page = 1;

    public static void navigate(ActivityMain activity, Wallpaper w) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = getIntent().getStringExtra(EXTRA_OBJC_CATEGORY);
        wallpaper = (Wallpaper) getIntent().getSerializableExtra(EXTRA_OBJC_WALLPAPER);
        wallpaper_mode = wallpaper != null;

        initWallpaperData();
        initToolbar();
        initComponent();
        requestAction(1);
        prepareAds();
    }

    private void initWallpaperData() {
        if (!wallpaper_mode) return;
        category = wallpaper.title;
        for (String s : wallpaper.images) {
            Wallpaper w = new Wallpaper(wallpaper);
            w.id = wallpaper.id + s;
            w.image = s;
            wallpapers.add(w);
        }

        ThisApp.itemsWallpaper = wallpapers;
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.toolbarIconText), PorterDuff.Mode.SRC_ATOP);
        binding.title.setText(category);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(null);
    }

    private void initComponent() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //set data and list adapter
        adapter = new AdapterListing(this, binding.recyclerView, AppConfig.general.listing_pagination_count);
        binding.recyclerView.setAdapter(adapter);


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    // Bottom of the page reached
                    if (allLoaded) {
                        adapter.setLoaded();
                    } else {
                        int next_page = page + 1;
                        Log.d("!!!!!!!!",next_page+"");
                        requestAction(next_page);
                    }

                    // int nextPage = page + 1;
                    //adapter.getNextPage(); // Implement a method to get the next page number
                    //requestAction(nextPage);
                }
            }
        });
        // detect when scroll reach bottom
        adapter.setListener(new AdapterListener<Object>() {
            @Override
            public void onClick(View view, String type, Object obj, int position) {
                super.onClick(view, type, obj, position);
                if (type.equals(AdapterListing.ActionType.ITEM.name())) {
                    //Log.d("!!",type+"===="+obj+"===="+position);
                    Wallpaper w = (Wallpaper) obj;
                    if (w.multiple) {
                        ActivityCategoryDetail.navigate(ActivityCategoryDetail.this, w);
                    } else if(w.child){
                        ActivityListingDetail.navigate(ActivityCategoryDetail.this, position);
                    } else {
                        ActivityListingDetail.navigate(ActivityCategoryDetail.this, w);
                    }
                }
            }

            @Override
            public void onLoadMore(int page) {
                super.onLoadMore(page);
                if (wallpaper_mode) return;
                if (allLoaded) {
                    adapter.setLoaded();
                } else {
                    int next_page = page + 1;
                    requestAction(next_page);
                }
            }
        });

        binding.swipeRefresh.setEnabled(!wallpaper_mode);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            allLoaded = false;
            adapter.resetListData();
            requestAction(1);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestAction(final int page_no) {
        if (page_no == 1) {
            showNoItemView(false);
            swipeProgress(true);
        } else {
            adapter.setLoadingOrFailed(null);
        }
        request(page_no);
    }

    private void request(Integer pageNo) {
        String tokenValue;
        if(pageNo!=1) {
            tokenValue = ThisApp.pref().getTokenValue();
        }else{
            tokenValue="";
        }
        Log.d("DTA",tokenValue);
        Log.d("URL",geturl(CATEGORY,adapter.selectedCategory,tokenValue));

        if (wallpaper_mode) {
            displayApiResultPlace(wallpapers);
            allLoaded = (adapter.getItemCount() == ThisApp.dao().getListingCount());
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,geturl(CATEGORY,category,tokenValue), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Handle response

                    List<Listing> sample = parseJsonResponse(response,getApplicationContext());
                    allLoaded = sample.size() < AppConfig.general.listing_pagination_count || sample.isEmpty();
                    displayApiResult(sample);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    if (error != null) Log.e("Error", error.getMessage());
                    onFailRequest();
                }
            });

            // Add the request to the RequestQueue.
            Volley.newRequestQueue(ThisApp.get()).add(stringRequest);
        }

    }

    private void onFailRequest() {
        swipeProgress(false);
        if (Tools.isConnect(this)) {
            adapter.setLoadingOrFailed(getString(R.string.failed_text));
        } else {
            adapter.setLoadingOrFailed(getString(R.string.no_internet_text));
        }
    }

    private void showNoItemView(boolean show) {
        ((TextView) binding.lytFailed.findViewById(R.id.failed_subtitle)).setText(getString(R.string.empty_state_no_data));
        if (show) {
            binding.lytFailed.setVisibility(View.VISIBLE);
        } else {
            binding.lytFailed.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        binding.swipeRefresh.post(() -> binding.swipeRefresh.setRefreshing(show));
        if (!show) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.shimmer.setVisibility(View.GONE);
            binding.shimmer.stopShimmer();
            return;
        }
        binding.recyclerView.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();
    }

    private void displayApiResult(final List<Listing> items) {

        List<Wallpaper> wallpapers = new ArrayList<>();
        for (Listing l : items) {
            wallpapers.add(Tools.parseListingToWallpaper(l));
        }
        Log.d("WWWW", String.valueOf(wallpapers));
        adapter.insertData(wallpapers);
        swipeProgress(false);
        showNoItemView(adapter.getItemCount() == 0);
    }

    private void displayApiResultPlace(final List<Wallpaper> items) {
        adapter.insertData(items);
        swipeProgress(false);
        showNoItemView(adapter.getItemCount() == 0);
    }

    private void prepareAds() {
        AdNetworkHelper adNetworkHelper = new AdNetworkHelper(this);
        adNetworkHelper.updateConsentStatus();
        adNetworkHelper.loadBannerAd(AppConfig.ads.ad_category_details_banner);
    }
}