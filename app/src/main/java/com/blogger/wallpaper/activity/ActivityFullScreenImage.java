package com.blogger.wallpaper.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blogger.wallpaper.R;
import com.blogger.wallpaper.databinding.ActivityFullScreenImageBinding;
import com.blogger.wallpaper.utils.Tools;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class ActivityFullScreenImage extends AppCompatActivity {

    public static final String EXTRA_IMG = "EXTRA_IMG";
    public static final String EXTRA_POS = "EXTRA_POS";

    public static void navigate(Activity activity, ArrayList<String> images, int position, View sharedView) {
        Intent intent = new Intent(activity, ActivityFullScreenImage.class);
        intent.putExtra(EXTRA_IMG, images);
        intent.putExtra(EXTRA_POS, position);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, EXTRA_IMG);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void navigate(Activity activity, String image) {
        Intent intent = new Intent(activity, ActivityFullScreenImage.class);
        ArrayList<String> images = new ArrayList<>();
        images.add(image);
        intent.putExtra(EXTRA_IMG, images);
        intent.putExtra(EXTRA_POS, 0);
        activity.startActivity(intent);
    }

    private AdapterFullScreenImage adapter;
    private ActivityFullScreenImageBinding binding;

    private ArrayList<String> items = new ArrayList<>();
    private int position = 0;

    private boolean textOverlayVisible = false;
    private Typeface selectedFont = Typeface.DEFAULT;
    private int selectedColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullScreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // animation transition
        ViewCompat.setTransitionName(binding.pager, EXTRA_IMG);

        items = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_IMG);
        position = getIntent().getIntExtra(EXTRA_POS, 0);

        initComponent();

        Tools.RTLMode(getWindow());
        Tools.darkNavigation(this);
    }

    private void initComponent() {
        adapter = new AdapterFullScreenImage(this, items);
        final int total = adapter.getCount();
        binding.pager.setAdapter(adapter);
        binding.textPage.setText(String.format(getString(R.string.image_of), 1, total));
        if (items.size() == 1) binding.textPage.setVisibility(View.GONE);

        // displaying selected image first
        binding.pager.setCurrentItem(position);
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                binding.textPage.setText(String.format(getString(R.string.image_of), (pos + 1), total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        findViewById(R.id.btn_close).setOnClickListener(view -> finish());
        findViewById(R.id.btn_close).setContentDescription("Close full screen view");

        findViewById(R.id.btn_options).setOnClickListener(view -> showOptionsDialog());
        findViewById(R.id.btn_options).setContentDescription("Open text customization options");

        findViewById(R.id.btn_toggle_text).setOnClickListener(view -> {
            textOverlayVisible = !textOverlayVisible;
            adapter.notifyDataSetChanged();
            Toast.makeText(this, textOverlayVisible ? "Text overlay shown" : "Text overlay hidden", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_toggle_text).setContentDescription("Toggle text overlay on image");
    }

    private void showOptionsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_font_color_options);
        dialog.setTitle("Customize Text");

        Spinner spinnerFont = dialog.findViewById(R.id.spinner_font);
        Spinner spinnerColor = dialog.findViewById(R.id.spinner_color);
        Button btnApply = dialog.findViewById(R.id.btn_apply);

        // Font options
        String[] fonts = {"Default", "Serif", "Sans Serif", "Monospace"};
        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fonts);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFont.setAdapter(fontAdapter);

        // Color options
        String[] colors = {"White", "Black", "Red", "Blue", "Green"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);

        spinnerFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedFont = Typeface.DEFAULT;
                        break;
                    case 1:
                        selectedFont = Typeface.SERIF;
                        break;
                    case 2:
                        selectedFont = Typeface.SANS_SERIF;
                        break;
                    case 3:
                        selectedFont = Typeface.MONOSPACE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedColor = Color.WHITE;
                        break;
                    case 1:
                        selectedColor = Color.BLACK;
                        break;
                    case 2:
                        selectedColor = Color.RED;
                        break;
                    case 3:
                        selectedColor = Color.BLUE;
                        break;
                    case 4:
                        selectedColor = Color.GREEN;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnApply.setOnClickListener(v -> {
            adapter.notifyDataSetChanged(); // Refresh to apply changes
            Toast.makeText(ActivityFullScreenImage.this, "Changes applied", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    public class AdapterFullScreenImage extends PagerAdapter {

        private Activity act;
        private List<String> image_paths;
        private LayoutInflater inflater;

        // constructor
        public AdapterFullScreenImage(Activity activity, List<String> imagePaths) {
            this.act = activity;
            this.image_paths = imagePaths;
        }

        @Override
        public int getCount() {
            return this.image_paths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView image;
            TextView textOverlay;
            inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.item_slider_image, container, false);

            image = viewLayout.findViewById(R.id.image);
            textOverlay = viewLayout.findViewById(R.id.text_overlay);
            if (textOverlay != null) {
                textOverlay.setTypeface(selectedFont);
                textOverlay.setTextColor(selectedColor);
                textOverlay.setText("Sample Text"); // Or get from data
                textOverlay.setVisibility(textOverlayVisible ? View.VISIBLE : View.GONE);
            }
            Tools.displayImage(act, image, image_paths.get(position));
            image.setContentDescription("Image " + (position + 1) + " of " + image_paths.size());
            (container).addView(viewLayout);

            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((RelativeLayout) object);

        }

    }
}