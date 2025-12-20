package com.blogger.wallpaper.fragment;

import static com.blogger.wallpaper.utils.AppConfigExt.CATEGORY;
import static com.blogger.wallpaper.utils.AppConfigExt.geturl;
import static com.blogger.wallpaper.utils.Tools.parseJsonResponse;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.R;
import com.blogger.wallpaper.activity.ActivityCategoryDetail;
import com.blogger.wallpaper.activity.ActivityListingDetail;
import com.blogger.wallpaper.activity.ActivityMain;
import com.blogger.wallpaper.adapter.AdapterListener;
import com.blogger.wallpaper.adapter.AdapterListing;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.databinding.FragmentWallpaperBinding;
import com.blogger.wallpaper.model.Listing;
import com.blogger.wallpaper.model.SectionCategory;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class FragmentWallpaper extends Fragment {

    private FragmentWallpaperBinding binding;
    private AdapterListing adapter;
    private boolean allLoaded = false;

    public int page = 1;

    public static FragmentWallpaper instance() {
        return new FragmentWallpaper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWallpaperBinding.inflate(inflater, container, false);
        initComponent();
        requestAction(1);
        return binding.getRoot();
    }

    private void initComponent() {
        //binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Tools.getGridSpanCount(getActivity())));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set data and list adapter
        adapter = new AdapterListing(getActivity(), binding.recyclerView, AppConfig.general.listing_pagination_count);
        adapter.insertCategory(new SectionCategory(ThisApp.get().getCategories()));
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
                    Wallpaper w = (Wallpaper) obj;
                    if (w.multiple) {
                        ActivityCategoryDetail.navigate((ActivityMain) getActivity(), w);
                    } else {
                        ActivityListingDetail.navigate((ActivityMain) getActivity(), w);
                    }
                    try {
                        ((ActivityMain) getActivity()).showInterstitialAd();
                    } catch (Exception e) {
                    }
                } else if (type.equals(AdapterListing.ActionType.CATEGORY.name())) {
                    adapter.resetListData();
                    requestAction(1);
                }
            }

            @Override
            public void onLoadMore(int page) {
                Log.d("RRRRRRR","RRRRRRRRRRRRRR"+allLoaded);
                super.onLoadMore(page);
                if (allLoaded) {
                    adapter.setLoaded();
                } else {
                    int next_page = page + 1;
                    Log.d("!!!!!!!!",next_page+"");
                    requestAction(next_page);
                }
            }
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            allLoaded = false;
            adapter.resetListData();
            requestAction(1);
        });
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
    Log.d("Page number",""+pageNo+ ThisApp.pref().getTokenValue());
        String tokenValue;
        if(pageNo!=1) {
            tokenValue = ThisApp.pref().getTokenValue();
        }else{
            tokenValue="";
        }
        Log.d("DTA",tokenValue);
        Log.d("URL",geturl(CATEGORY,adapter.selectedCategory,tokenValue));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, geturl(CATEGORY,adapter.selectedCategory,tokenValue), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle response
                //Log.d("DTA",response);
                List<Listing> sample = parseJsonResponse(response,getContext());
                allLoaded = sample.size() < AppConfig.general.listing_pagination_count || sample.isEmpty();
                displayApiResult(sample);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                // if (error != null) Log.e("Error", Objects.requireNonNull(error.getMessage()));
                onFailRequest();
            }
        });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(ThisApp.get()).add(stringRequest);
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (Tools.isConnect(getActivity())) {
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
        adapter.insertData(wallpapers);
        swipeProgress(false);
        showNoItemView(adapter.getItemCount() == 0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}