package com.blogger.wallpaper.activity;

import static com.blogger.wallpaper.utils.AppConfigExt.SEARCH;
import static com.blogger.wallpaper.utils.AppConfigExt.geturl;
import static com.blogger.wallpaper.utils.Tools.parseJsonResponse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

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
import com.blogger.wallpaper.databinding.ActivitySearchBinding;
import com.blogger.wallpaper.model.Listing;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearch extends AppCompatActivity {

    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivitySearch.class);
        activity.startActivity(i);
    }

    private ActivitySearchBinding binding;

    private AdapterListing adapter;
    private boolean allLoaded = false;
    private String query = "";
    public int page = 1;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable workRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initComponent();
        showNoItemView(true);
        prepareAds();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.toolbarIconText), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initComponent() {
        initToolbar();

        binding.toolbarMenuClear.setVisibility(View.GONE);
        binding.toolbarMenuClear.setOnClickListener(view -> {
            binding.searchInput.setText("");
            binding.toolbarMenuClear.setVisibility(View.GONE);
        });


        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence chars, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence chars, int i, int i1, int i2) {
                if (!chars.toString().trim().equals("") && binding.toolbarMenuClear.getVisibility() == View.GONE) {
                    binding.toolbarMenuClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> {
                    if (binding.searchInput.getText().toString().trim().equals("")) return;
                    searchAction();
                };
                handler.postDelayed(workRunnable, 1200);
            }
        });

        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handler.removeCallbacks(workRunnable);
                allLoaded = false;
                adapter.resetListData();
                searchAction();
                return true;
            }
            return false;
        });

       // binding.recyclerView.setLayoutManager(new GridLayoutManager(this, Tools.getGridSpanCount(this)));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                Wallpaper w = (Wallpaper) obj;
                ActivityListingDetail.navigate(ActivitySearch.this, w);
            }

            @Override
            public void onLoadMore(int page) {
                super.onLoadMore(page);
                if (allLoaded) {
                    adapter.setLoaded();
                } else {
                    int next_page = page + 1;
                    requestAction(next_page);
                }
            }
        });
    }

    private void searchAction() {
        Tools.hideKeyboard(this);
        query = binding.searchInput.getText().toString().trim();
        if (!query.equals("")) {
            adapter.resetListData();
            // request action will be here
            requestAction(1);
        } else {
            Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestAction(final int page_no) {
        if (page_no == 1) {
            showNoItemView(false);
            swipeProgress(true);
        } else {
            adapter.setLoadingOrFailed(null);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> requestPost(page_no), 200);
    }

    private void requestPost(Integer pageNo) {

        String tokenValue;
        if(pageNo!=1) {
            tokenValue = ThisApp.pref().getTokenValue();
        }else{
            tokenValue="";
        }
        Log.d("DTA",tokenValue);
        Log.d("URL",geturl(SEARCH,query,tokenValue));


//        ParamList param = new ParamList();
//        param.keyword = query;
//        param.url = AppConfig.general.blogger_url;
//        param.count = AppConfig.general.listing_pagination_count.toString();
//        param.page = pageNo.toString();
//
//        ThisApp.get().bloggerAPI.getPosts(param, new RequestListener<RespPosts>() {
//            @Override
//            public void onSuccess(RespPosts resp) {
//                super.onSuccess(resp);
//                allLoaded = resp.list.size() < AppConfig.general.listing_pagination_count || resp.list.size() == 0;
//                displayApiResult(resp.list);
//            }
//
//            @Override
//            public void onFailed(String messages) {
//                super.onFailed(messages);
//                Log.e("Error", messages);
//                onFailRequest();
//            }
//        });
        Log.d("SEarch",query);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, geturl(SEARCH,query,tokenValue), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle response
                Log.d("SEarch",response);
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
        adapter.insertData(wallpapers);
        swipeProgress(false);
        showNoItemView(adapter.getItemCount() == 0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareAds() {
        AdNetworkHelper adNetworkHelper = new AdNetworkHelper(this);
        adNetworkHelper.updateConsentStatus();
        adNetworkHelper.loadBannerAd(AppConfig.ads.ad_search_banner);
    }
}