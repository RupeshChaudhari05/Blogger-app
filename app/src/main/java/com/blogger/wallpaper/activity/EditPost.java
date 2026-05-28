package com.blogger.wallpaper.activity;

import static com.blogger.wallpaper.data.SharedPref.DEF;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
// BuildConfig not used in this file — removed
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogger.wallpaper.AppConfig;
import com.blogger.wallpaper.ImageBlurHelper;
import com.blogger.wallpaper.R;
import com.blogger.wallpaper.adapter.AdapterFontList;
import com.blogger.wallpaper.adapter.AdapterListing;
import com.blogger.wallpaper.adapter.AdapterTextColourPicker;
import com.blogger.wallpaper.config.EditPostHelper;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.databinding.ActivityEditPostBinding;
import com.blogger.wallpaper.model.ModelColorList;
import com.blogger.wallpaper.model.ModelFontDetail;
import com.blogger.wallpaper.utils.PermissionUtil;
import com.blogger.wallpaper.utils.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yalantis.ucrop.UCrop;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;



public class EditPost extends AppCompatActivity {
    private ActivityEditPostBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    public static final String EXTRA_IMAGE  = "image";
    AppCompatTextView txtQuote;
    String QuotesText;
    View darkView;
    PopupWindow mPopupWindow;
    private EditPostHelper editPostHelper;  // ADDED: Helper for user-friendly operations

    LinearLayout topLayout,fontpopup,colorPiker,imageUpload,ll_quote_save,blurIffect,ll_quote_share,backgroundChange,imageSizeChange;
    Context context;
    TextView tag;

    static RelativeLayout llBackground;

    ImageView iv_save_quote,tv_quotes_watermark;
    TextView tv_save_quote;

    ImageView theam1_1,theam1_2,theam2_1;
    View theam2_2;
    CardView quotes_card_view;

    /** WeakReference prevents Activity memory leaks. Dialogs launched from this Activity use it. */
    private static WeakReference<EditPost> instanceRef;

    public static EditPost getInstance() {
        return instanceRef != null ? instanceRef.get() : null;
    }

    private static final int GALLERY_REQUEST = 1;
    private static final int STORAGE_PERMISSION_REQUEST = 3;

    public static void navigate(Activity activity, String quote, String image) {
        ThisApp.itemsWallpaper = new ArrayList<>();
        Intent i = new Intent(activity, EditPost.class);
        i.putExtra(EXTRA_OBJECT, quote);
        i.putExtra(EXTRA_IMAGE, image);
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instanceRef = new WeakReference<>(this);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        context = EditPost.this;
        setContentView(binding.getRoot());
        QuotesText = getIntent().getStringExtra(EXTRA_OBJECT);
        
        // ADDED: Initialize EditPostHelper for better UX
        editPostHelper = new EditPostHelper(this, binding.llBackground);
        
        initComponent();

        fontpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFontList();
            }
        });

        backgroundChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });

        imageSizeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup_postSize();
            }
        });

        colorPiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        openDailogForBackgroundcolour();
            }
        });

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        ll_quote_save.setOnClickListener(new View.OnClickListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                Log.d("CCCC","AAA");
                
                // IMPROVED: Show user-friendly message
                editPostHelper.notifyOperationStart("Saving Quote");
                
                if (!PermissionUtil.isStorageGranted((Activity) context)) {
                    if (ThisApp.pref().getNeverAskAgain(PermissionUtil.STORAGE)) {
                        PermissionUtil.showDialog((AppCompatActivity) context);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                requestPermissions(PermissionUtil.PERMISSION_ALL, 500);
                            } else {
                                requestPermissions(PermissionUtil.PERMISSION_ALL, 500);
                            }
                        }
                    }
                    editPostHelper.notifyError("Storage permission required");
                    return;
                }

                // IMPROVED: Use helper to create bitmap
                Bitmap bitmap = editPostHelper.createBitmapFromLayout();
                if (bitmap == null) {
                    editPostHelper.notifyError("Failed to create image");
                    return;
                }

                OutputStream fos;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    saveBitmapModernWay(bitmap);
                } else {
                    saveBitmapLegacyWay(bitmap);
                }
            }
        });


        tv_quotes_watermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_quotes_watermark.setVisibility(View.GONE);
                editPostHelper.showToast("Watermark hidden");
            }
        });

        blurIffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyBlurEffect();
            }
        });


        ll_quote_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareOptions();
            }
        });
    }

    private void initComponent() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        Tools.changeOverflowMenuIconColor(binding.toolbar, Color.WHITE);
        txtQuote = binding.txtQuote;
        txtQuote.setText(QuotesText);
        Tools.adjustFontSizeBasedOnLength(txtQuote,QuotesText);

        Log.d("QQQQ",QuotesText);
        Log.d("WWW",""+getIntent().getStringExtra(EXTRA_IMAGE));

        topLayout = binding.layoutQuotesParentView;
        fontpopup = binding.fontpopup;
        colorPiker = binding.colorPiker;
        llBackground = binding.llBackground;
        imageSizeChange = binding.imageSizeChange;
        getimage(getIntent().getStringExtra(EXTRA_IMAGE));
        quotes_card_view = binding.quotesCardView;

       // llBackground.setBackgroundResource(getIntent().getIntExtra(EXTRA_IMAGE,0));

        iv_save_quote = binding.ivSaveQuote;
        tv_quotes_watermark = binding.tvQuotesWatermark;
        imageUpload = binding.imageUpload;
        tv_save_quote = binding.tvSaveQuote;
        ll_quote_save = binding.llQuoteSave;
        darkView = binding.darkView;
        blurIffect = binding.blurIffect;
        ll_quote_share = binding.llQuoteShare;
        backgroundChange = binding.backgroundChange;

        theam1_1 = binding.theam11;
        theam1_2 = binding.theam12;
        theam2_1 = binding.theam21;
        theam2_2 = binding.theam22;
        tag = binding.tagView;
        Log.d("VVVV",ThisApp.pref().getUserTagValue());
        if(ThisApp.pref().getUserTagValue().equals("")){
            tag.setVisibility(View.GONE);
        }else {
            tag.setVisibility(View.VISIBLE);
            tag.setText(ThisApp.pref().getUserTagValue());
        }

        if(ThisApp.pref().getQuotesThemeValue().equals(DEF)){
            theam2_1.setVisibility(View.GONE);
            theam2_2.setVisibility(View.GONE);
            theam1_1.setVisibility(View.VISIBLE);
            theam1_2.setVisibility(View.VISIBLE);
        }else{
            theam2_1.setVisibility(View.VISIBLE);
            theam2_2.setVisibility(View.VISIBLE);
            theam1_1.setVisibility(View.GONE);
            theam1_2.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instanceRef = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            ActivitySearch.navigate(this);
        } else if (item.getItemId() == R.id.action_setting) {
            ActivitySetting.navigate(this);
        } else if (item.getItemId() == R.id.action_more_app) {
            Tools.directLinkCustomTab(this, AppConfig.general.more_apps_url);
        } else if (item.getItemId() == R.id.action_rate_app) {
            Tools.rateAction(this);
        } else if (item.getItemId() == R.id.action_about) {
            Tools.aboutAction(this);
        }else if (item.getItemId()==R.id.theam_select){
            Tools.themeAction(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopup_postSize() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dilog_fram_sizes);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);

        // Initialize the horizontal scroll view in the popup
        initHorizontalScrollViewResizeImage(dialog);

        // Show the popup
        //mPopupWindow.showAtLocation(topLayout, Gravity.CENTER, 0, 0);
        //mPopupWindow.showAsDropDown(fontpopup, 0, 20);
        dialog.show();
    }

    private void showPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dilog_baground_image_selector);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);

        // Initialize the horizontal scroll view in the popup
        initHorizontalScrollView(dialog);

        // Show the popup
        //mPopupWindow.showAtLocation(topLayout, Gravity.CENTER, 0, 0);
        //mPopupWindow.showAsDropDown(fontpopup, 0, 20);
        dialog.show();
    }

    private void initHorizontalScrollViewResizeImage(Dialog popupView) {
        HorizontalScrollView horizontalScrollView = popupView.findViewById(R.id.horizontalScrollView);
        LinearLayout imageContainer = popupView.findViewById(R.id.imageContainer);

        String[] imageUrls = {"Instagram Post", "Facebook Post", "WhatsApp Story", "Default Image"};

        for (int i = 0; i < imageUrls.length; i++) {
            // Create a TextView instead of ImageView
            TextView textView = new TextView(this);

            // Set text
            textView.setText(imageUrls[i]);

            // Set background color
            textView.setBackgroundColor(Color.parseColor("#FF0000")); // Example color

            // Set text color
            textView.setTextColor(Color.WHITE); // Example color

            // Set padding
            textView.setPadding(20, 20, 20, 20); // Example padding

            // Set gravity to center text horizontally and vertically
            textView.setGravity(Gravity.CENTER);

            // Set LayoutParams for the TextView
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            textView.setLayoutParams(layoutParams);
            textView.setTag(i);
            // Set OnClickListener for the text
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int clickedIndex = (int) v.getTag();
                    String size = imageUrls[clickedIndex];
                    if (size.equals("Instagram Post")) {
                        llBackground.setLayoutParams(new LinearLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_INSTAGRAM_W, com.blogger.wallpaper.Constants.SIZE_INSTAGRAM_H));
                        quotes_card_view.setLayoutParams(new RelativeLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_INSTAGRAM_W, com.blogger.wallpaper.Constants.SIZE_INSTAGRAM_H));
                    } else if (size.equals("Facebook Post")) {
                        llBackground.setLayoutParams(new LinearLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_FACEBOOK_W, com.blogger.wallpaper.Constants.SIZE_FACEBOOK_H));
                        quotes_card_view.setLayoutParams(new RelativeLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_FACEBOOK_W, com.blogger.wallpaper.Constants.SIZE_FACEBOOK_H));
                    } else if (size.equals("WhatsApp Story")) {
                        quotes_card_view.setLayoutParams(new RelativeLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_WHATSAPP_W, com.blogger.wallpaper.Constants.SIZE_WHATSAPP_H));
                        llBackground.setLayoutParams(new LinearLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_WHATSAPP_W, com.blogger.wallpaper.Constants.SIZE_WHATSAPP_H));
                    } else if (size.equals("Generic Image")) {
                        llBackground.setLayoutParams(new LinearLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_GENERIC_W, com.blogger.wallpaper.Constants.SIZE_GENERIC_H));
                        quotes_card_view.setLayoutParams(new RelativeLayout.LayoutParams(com.blogger.wallpaper.Constants.SIZE_GENERIC_W, com.blogger.wallpaper.Constants.SIZE_GENERIC_H));
                    } else {
                        // Default size
                        llBackground.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        quotes_card_view.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                }

            });


            // Add the TextView to the image container
            imageContainer.addView(textView);
        }
    }

    private void initHorizontalScrollView(Dialog popupView) {
        HorizontalScrollView horizontalScrollView = popupView.findViewById(R.id.horizontalScrollView);
        LinearLayout imageContainer = popupView.findViewById(R.id.imageContainer);

        String[] imageUrls = ThisApp.pref().getImageUrls();

        for (int i = 0; i < imageUrls.length; i++) {
            ImageView imageView = new ImageView(this);
            Glide.with(this)
                    .load(imageUrls[i])
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.logo) // Placeholder image while loading
                            .error(R.drawable.ic_close) // Error image if loading fails
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache strategy
                            .fitCenter()) // Scale type
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(layoutParams);

            // Set tag to identify the image
            imageView.setTag(i);

            // Set OnClickListener for the image
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the tag to identify which image was clicked
                    int clickedIndex = (int) v.getTag();
                    // Do something with the clicked image, for example:
                    setRelativeLayoutBackground(imageUrls[clickedIndex]);
                    //Toast.makeText(EditPost.this, "Image " + clickedIndex + " clicked!", Toast.LENGTH_SHORT).show();
                }
            });

            imageContainer.addView(imageView);
        }






    }


    public void openFontList() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_fontstyle);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);



        RecyclerView rvList = dialog.findViewById(R.id.rv_font_style);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvList.setLayoutManager(linearLayoutManager);

        AdapterFontList adapterFontList = new AdapterFontList(getApplicationContext(), getfontList(), "greetingstyle");
        rvList.setAdapter(adapterFontList);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private void setRelativeLayoutBackground(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.logo) // Placeholder image while loading
                .error(R.drawable.ic_close) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache strategy

        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        llBackground.setBackground(resource);
                    }
                });
    }
    public void setFontStyle(String fontName) {
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + fontName);
        txtQuote.setTypeface(font);
    }

    public void setTextbackgroundcolor(int colour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtQuote.setTextColor(context.getColor(colour));
        }
    }

    /**
     * IMPROVED: Helper method to save bitmap in modern way (Android Q+)
     */
    private void saveBitmapModernWay(Bitmap bitmap) {
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try (OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri))) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                tv_save_quote.setText("Saved");
                iv_save_quote.setImageResource(R.drawable.ic_menu_check);
                editPostHelper.notifySuccess(editPostHelper.getStatusMessage("save"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            editPostHelper.notifyError("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            editPostHelper.notifyError("Failed to save");
        }
    }

    /**
     * IMPROVED: Helper method to save bitmap in legacy way (Before Android Q)
     */
    private void saveBitmapLegacyWay(Bitmap bitmap) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/" + AppConfig.general.download_directory);
            directory.mkdirs();

            String filename = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(directory, filename);

            try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                context.sendBroadcast(intent);

                tv_save_quote.setText("Saved");
                iv_save_quote.setImageResource(R.drawable.ic_menu_check);
                editPostHelper.notifySuccess(editPostHelper.getStatusMessage("save"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            editPostHelper.notifyError("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            editPostHelper.notifyError("Failed to save");
        }
    }

    /**
     * IMPROVED: Helper method to apply blur effect
     */
    private void applyBlurEffect() {
        try {
            Drawable backgroundDrawable = llBackground.getBackground();
            if (backgroundDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;
                Bitmap blurredBitmap = ImageBlurHelper.blurBitmap(context, bitmapDrawable.getBitmap(),
                        (int) com.blogger.wallpaper.Constants.DEFAULT_BLUR_RADIUS);
                if (blurredBitmap != null) {
                    llBackground.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
                    editPostHelper.notifySuccess(editPostHelper.getStatusMessage("blur_applied"));
                } else {
                    editPostHelper.notifyError("Failed to apply blur effect");
                }
            } else {
                editPostHelper.showToast("Set a background image first");
            }
        } catch (Exception e) {
            e.printStackTrace();
            editPostHelper.notifyError("Blur error");
        }
    }

    /**
     * IMPROVED: Helper method to show share options
     */
    private void showShareOptions() {
        PopupMenu popup = new PopupMenu(context, ll_quote_share);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sub_text:
                        shareAsText();
                        return true;
                    case R.id.sub_image:
                        shareAsImage();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.menu_item);
        popup.show();
    }

    /**
     * IMPROVED: Helper method to share quote as text
     */
    private void shareAsText() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, QuotesText + "\n https://play.google.com/store/apps/details?id=" + context.getPackageName());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            context.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
            editPostHelper.notifySuccess(editPostHelper.getStatusMessage("share_text"));
        } catch (Exception e) {
            e.printStackTrace();
            editPostHelper.notifyError("Share error");
        }
    }

    /**
     * IMPROVED: Helper method to share quote as image
     */
    private void shareAsImage() {
        try {
            Bitmap bitmap = editPostHelper.createBitmapFromLayout();
            if (bitmap != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.app_name)));
                editPostHelper.notifySuccess(editPostHelper.getStatusMessage("share_image"));
            } else {
                editPostHelper.notifyError("Failed to create image for sharing");
            }
        } catch (Exception e) {
            e.printStackTrace();
            editPostHelper.notifyError("Share error");
        }
    }

    public static ArrayList<ModelFontDetail> getfontList() {
        ArrayList<ModelFontDetail> data = new ArrayList<>();
        String[] fontnamelist = new String[]
                {"abhayalibre_bold.ttf", "abhayalibre_extrabold.ttf", "abhayalibre_medium.ttf", "artifika_regular.ttf", "archivo_black.ttf",
                        "ArchivoNarrow.otf", "ABeeZee.otf", "After_Shok.ttf", "AbrilFatface.otf", "Acknowledgement.otf",
                        "Acme.ttf", "AlfaSlabOne.ttf", "AlmendraDisplay.otf", "Almendra.otf", "alpha_echo.ttf",
                        "Amadeus.ttf", "AMERSN.ttf", "ANUDI.ttf", "AquilineTwo.ttf", "Arbutus.ttf", "AlexBrush.ttf",
                        "Alisandra.ttf", "Allura.ttf", "Amarillo.ttf", "BEARPAW.ttf", "bigelowrules.ttf", "BLACKR.ttf",
                        "BOYCOTT.ttf", "BebasNeue.ttf", "BLKCHCRY.TTF", "Carousel.ttf", "Caslon_Calligraphic.ttf",
                        "CroissantOne.ttf", "Carnevalee-Freakshow.ttf", "CAROBTN.TTF", "CaviarDreams.ttf",
                        "Cocogoose.ttf", "diplomata.ttf", "deftone stylus.ttf", "Dosis.ttf", "FONTL.TTF",
                        "Hugtophia.ttf", "ICE_AGE.ttf", "Kingthings_Calligraphica.ttf", "Love Like This.ttf",
                        "MADE Canvas.otf", "Merci-Heart-Brush.ttf", "Metropolis.otf", "Montserrat.otf",
                        "MontserratAlternates.otf",
                        "norwester.otf", "ostrich.ttf", "squealer.ttf", "Titillium.otf", "Ubuntu.ttf"};

        for (int i = 0; i < fontnamelist.length; i++) {

            data.add(new ModelFontDetail(fontnamelist[i], fontnamelist[i]));
        }
        return data;

    }

    public static ArrayList<ModelColorList> getColorList() {
        ArrayList<ModelColorList> data = new ArrayList<>();
        /* data.add(new ModelColorList(R.color.colorWhite));*/
        data.add(new ModelColorList(R.color.color1));
        data.add(new ModelColorList(R.color.white));
        data.add(new ModelColorList(R.color.color6));
        data.add(new ModelColorList(R.color.color7));
        data.add(new ModelColorList(R.color.color3));
        data.add(new ModelColorList(R.color.color12));
        data.add(new ModelColorList(R.color.black));
        data.add(new ModelColorList(R.color.color2));
        data.add(new ModelColorList(R.color.color4));
        data.add(new ModelColorList(R.color.color5));
        data.add(new ModelColorList(R.color.color9));
        data.add(new ModelColorList(R.color.color8));
        data.add(new ModelColorList(R.color.color10));
        data.add(new ModelColorList(R.color.color11));
        return data;
    }

    public void openDailogForBackgroundcolour() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dailog_bg_color);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        final TextView tvOpacity = dialog.findViewById(R.id.tvOpacity);
        final SeekBar sbOpacity = dialog.findViewById(R.id.sbOpacity);
        RecyclerView rv_bg_color = dialog.findViewById(R.id.rv_bg_color);
        TextView tv_dailog_tittle = dialog.findViewById(R.id.tv_dailog_tittle);

        tv_dailog_tittle.setText("Change Text Color");
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 7);
        rv_bg_color.setLayoutManager(linearLayoutManager);


        AdapterTextColourPicker adapterTextColourPicker = new AdapterTextColourPicker(getApplicationContext(), getColorList(), "bgTextcolor");
        rv_bg_color.setAdapter(adapterTextColourPicker);


        sbOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / 5)) * 5;
                //iv_customimage.setAlpha((Float.valueOf(progress) / Float.valueOf(100)));
                txtQuote.setTextSize(progress);
                tvOpacity.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getimage(String index){
        Glide.with(this)
                .asBitmap()
                .load(index)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap as the background of the RelativeLayout
                        llBackground.setBackground(new BitmapDrawable(getResources(), resource));
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Placeholder handling if needed
                        llBackground.setBackground(placeholder);
                    }
                });

    }


    private void pickFromGallery() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");  // 1
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), GALLERY_REQUEST);
    }
    String currentPhotoPath;
    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri uri = UCrop.getOutput(data);
                showImage(uri);
            }else if (requestCode == GALLERY_REQUEST && data != null) {
                Uri sourceUri = data.getData(); // 1
                File file = null; // 2
                try {
                    file = getImageFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4
            }
        }
    }
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(500, 500)
                .withAspectRatio(5f, 5f)
                .start(EditPost.this);
    }
    private void showImage(Uri imageUri) {
        try {
            // Convert the Uri to a File object
            File file = new File(imageUri.getPath());

            // Decode the image file into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


            // Set the bitmap as the background of the LinearLayout
            llBackground.setBackground(new BitmapDrawable(getResources(), bitmap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            // Store the bitmap in the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image_" + System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Open an output stream to write the bitmap data
            OutputStream out = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            if (out != null) {
                out.close();
                bmpUri = uri; // Set the URI
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
