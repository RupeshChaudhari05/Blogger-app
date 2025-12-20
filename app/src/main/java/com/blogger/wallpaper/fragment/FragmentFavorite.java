package com.blogger.wallpaper.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.R;
import com.blogger.wallpaper.activity.ActivityListingDetail;
import com.blogger.wallpaper.activity.ActivityMain;
import com.blogger.wallpaper.adapter.AdapterListener;
import com.blogger.wallpaper.adapter.AdapterListing;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.databinding.FragmentFavoriteBinding;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.room.table.EntityListing;
import com.blogger.wallpaper.utils.Tools;

import java.util.ArrayList;
import java.util.List;


public class FragmentFavorite extends Fragment {

    private FragmentFavoriteBinding binding;
    private AdapterListing adapter;
    private boolean allLoaded = false;
    private static int last_count = -1;
    public int page = 1;

    public static FragmentFavorite instance() {
        return new FragmentFavorite();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        initComponent();
        return binding.getRoot();
    }

    private void initComponent() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set data and list adapter
        adapter = new AdapterListing(getActivity(), binding.recyclerView, AppConfig.general.listing_pagination_count);
        binding.recyclerView.setAdapter(adapter);

        // detect when scroll reach bottom
        adapter.setListener(new AdapterListener<Object>() {
            @Override
            public void onClick(View view, String type, Object obj, int position) {
                super.onClick(view, type, obj, position);
                Wallpaper w = (Wallpaper) obj;
                ActivityListingDetail.navigate((ActivityMain) getActivity(), w);
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

        binding.swipeRefresh.setOnRefreshListener(() -> {
            allLoaded = false;
            adapter.resetListData();
            requestAction(1);
        });
    }

    private void requestAction(final int page_no) {
        if (page_no == 1) {
            adapter.resetListData();
            showNoItemView(false);
            swipeProgress(true);
        } else {
            adapter.setLoadingOrFailed(null);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> request(page_no), 200);
    }

    private void request(Integer pageNo) {
        String page = pageNo.toString();
        String count1 = AppConfig.general.listing_pagination_count.toString();

        Integer count = Integer.parseInt(count1);
        int offset = (pageNo - 1) * count;
        List<EntityListing> entityListings = ThisApp.dao().getAllListingByPage(count, offset);
        List<Wallpaper> items = new ArrayList<>();
        for (EntityListing e : entityListings) {
            items.add(e.original());
        }
        displayApiResultPlace(items);
        allLoaded = (adapter.getItemCount() >= ThisApp.dao().getListingCount());
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

    private void displayApiResultPlace(final List<Wallpaper> items) {
        adapter.insertData(items);
        swipeProgress(false);
        showNoItemView(adapter.getItemCount() == 0);
    }

    public void updateData(){
        int listing_count = ThisApp.dao().getListingCount();
        if(listing_count != last_count){
            last_count = listing_count;
            requestAction(1);
        }
    }
}