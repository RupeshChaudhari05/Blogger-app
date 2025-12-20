package com.blogger.wallpaper.adapter;

import static androidx.core.app.ActivityCompat.requestPermissions;

import static com.blogger.wallpaper.data.SharedPref.DEF;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogger.wallpaper.R;
import com.blogger.wallpaper.activity.ActivityListingDetail;
import com.blogger.wallpaper.activity.ActivityMain;
import com.blogger.wallpaper.activity.EditPost;
import com.blogger.wallpaper.data.ThisApp;
import com.blogger.wallpaper.model.SectionCategory;
import com.blogger.wallpaper.model.Wallpaper;
import com.blogger.wallpaper.room.table.EntityListing;
import com.blogger.wallpaper.utils.PermissionUtil;
import com.blogger.wallpaper.utils.PrefManager;
import com.blogger.wallpaper.utils.Tools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AdapterListing extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum ActionType {
        ITEM, CATEGORY
    }
    private int STORAGE_PERMISSION_CODE = 1;

    String[] images;
   // private int[] images;
    private int imagesIndex = 0;
    public String selectedImage ="";
    private final Context ctx;
    private List<Object> items = new ArrayList<>();

    private final int VIEW_ITEM = 200;
    private final int VIEW_TOP_TAB = 300;
    private final int VIEW_PROG = 0;
    private boolean loading = true;
    private String status;
    public String selectedCategory = null;
    private TextView previousCategory = null;
    private boolean categoryInitiated = false;
    private int page = 0;

    private AdapterListener<Object> listener;

    public void setListener(AdapterListener<Object> listener) {
        this.listener = listener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterListing(Context context, RecyclerView view, int page) {
        this.page = page;
        ctx = context;
        lastItemViewDetector(view);
    }

    public class SectionTabViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lytParent;

        public SectionTabViewHolder(View v) {
            super(v);
            lytParent = v.findViewById(R.id.lyt_parent);
        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progress_loading;
        public TextView status_loading;

        public ProgressViewHolder(View v) {
            super(v);
            progress_loading = v.findViewById(R.id.progress_loading);
            status_loading = v.findViewById(R.id.status_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
            vh = new WallpaperViewHolder(v);
        } else if (viewType == VIEW_TOP_TAB) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_tab, parent, false);
            vh = new SectionTabViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();

        if (holder instanceof WallpaperViewHolder) {
            final Wallpaper obj = (Wallpaper) items.get(position);
            WallpaperViewHolder v = (WallpaperViewHolder) holder;
            images = ThisApp.pref().getImageUrls();

            Document doc = Jsoup.parse(obj.content);

            // Select all list items within unordered list
            Elements listItems = doc.select("ul > li,blockquote,p");

            // Get the text from the list item
            String text1 = "";
            for (Element listItem : listItems) {
                text1 += listItem.text() + "\n";
            }

            // Display the text
            //Log.d("ParsedText", text1);

            String text = !text1.equals("") ?text1:obj.content;
            v.txtQuote.setText(text);

            //Tools.adjustFontSizeBasedOnLength(v.txtQuote,text);

            if(ThisApp.pref().getUserTagValue().equals("")){
                ((WallpaperViewHolder) holder).tag.setVisibility(View.GONE);
            }else {
                ((WallpaperViewHolder) holder).tag.setVisibility(View.VISIBLE);
                ((WallpaperViewHolder) holder).tag.setText(ThisApp.pref().getUserTagValue());
            }

            if(ThisApp.pref().getQuotesThemeValue().equals(DEF)){
                ((WallpaperViewHolder) holder).theam2_1.setVisibility(View.GONE);
                ((WallpaperViewHolder) holder).theam2_2.setVisibility(View.GONE);
                ((WallpaperViewHolder) holder).theam1_1.setVisibility(View.VISIBLE);
                ((WallpaperViewHolder) holder).theam1_2.setVisibility(View.VISIBLE);
            }else{
                ((WallpaperViewHolder) holder).theam2_1.setVisibility(View.VISIBLE);
                ((WallpaperViewHolder) holder).theam2_2.setVisibility(View.VISIBLE);
                ((WallpaperViewHolder) holder).theam1_1.setVisibility(View.GONE);
                ((WallpaperViewHolder) holder).theam1_2.setVisibility(View.GONE);
            }

            ((WallpaperViewHolder) holder).relativeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int arrayLength = images.length;


                    Random random = new Random();


                    int randomPosition = random.nextInt(arrayLength);
                    String s = images[randomPosition];
                    Glide.with(ctx)
                            .asBitmap()
                            .load(s)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Set the loaded bitmap as the background of the RelativeLayout
                                    ((WallpaperViewHolder) holder).relativeLayout.setBackground(new BitmapDrawable(ctx.getResources(), resource));
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Placeholder handling if needed
                                    ((WallpaperViewHolder) holder).relativeLayout.setBackground(placeholder);
                                }
                            });

                    selectedImage = s;
                    ++imagesIndex;  // update index, so that next time it points to next resource



                    if (imagesIndex == images.length - 1)
                        imagesIndex = 0; // if we have reached at last index of array, simply restart from beginning

                }
            });



            ((WallpaperViewHolder) holder).tv_quotes_watermark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ctx)
                            .setIcon(R.drawable.logo)
                            .setTitle("Remove Watermark")
                            .setMessage("Watch the video for Remove the Watermark")
                            .setPositiveButton("Watch", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ((WallpaperViewHolder) holder).tv_quotes_watermark.setVisibility(View.GONE);
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });


            ((WallpaperViewHolder) holder).ll_quote_save.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    Log.d("CCCC","AAA");
                    ((WallpaperViewHolder) holder).editImage.setVisibility(View.INVISIBLE);
                    ((ActivityMain) ctx).showInterstitialAd();
                    if (!PermissionUtil.isStorageGranted((Activity) ctx)) {
                        if (ThisApp.pref().getNeverAskAgain(PermissionUtil.STORAGE)) {
                            PermissionUtil.showDialog((AppCompatActivity) ctx);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    requestPermissions((Activity) ctx,PermissionUtil.PERMISSION_ALL, 500);
                                } else {
                                    requestPermissions((Activity) ctx,PermissionUtil.PERMISSION_ALL, 500);
                                }
                            }
                        }
                        return;
                    }

                        //((WallpaperViewHolder) holder).tv_quotes_watermark.setVisibility(View.VISIBLE);
                        Bitmap bitmap = Bitmap.createBitmap(((WallpaperViewHolder) holder).relativeLayout.getWidth(), ((WallpaperViewHolder) holder).relativeLayout.getHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        ((WallpaperViewHolder) holder).relativeLayout.draw(canvas);

                        OutputStream fos;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            Log.d("BBBB","AAA");
                            ContentResolver resolver = ctx.getContentResolver();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                            Toast.makeText(ctx, "File Saved", Toast.LENGTH_SHORT).show();
                            ((WallpaperViewHolder) holder).tv_save_quote.setText("Saved");
                            ((WallpaperViewHolder) holder).iv_save_quote.setImageResource(R.drawable.ic_menu_check);
                            try {
                                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                                fos.flush();
                                fos.close();


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //((WallpaperViewHolder) holder).tv_quotes_watermark.setVisibility(View.INVISIBLE);
                        } else {
                            ((WallpaperViewHolder) holder).editImage.setVisibility(View.INVISIBLE);
                            FileOutputStream outputStream = null;

                            File sdCard = Environment.getExternalStorageDirectory();

                            File directory = new File(sdCard.getAbsolutePath() + "/Latest Quotes");
                            directory.mkdir();

                            String filename = String.format("%d.jpg", System.currentTimeMillis());

                            File outFile = new File(directory, filename);

                            Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
                            ((WallpaperViewHolder) holder).tv_save_quote.setText("Saved");
                            ((WallpaperViewHolder) holder).iv_save_quote.setImageResource(R.drawable.ic_menu_check);


                            try {
                                outputStream = new FileOutputStream(outFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                                outputStream.flush();
                                outputStream.close();

                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(Uri.fromFile(outFile));
                                ctx.sendBroadcast(intent);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //((WallpaperViewHolder) holder).tv_quotes_watermark.setVisibility(View.INVISIBLE);



                        }


                        //show permission popup
                        //requestStoragePermission();


                }
            });

            //copy button
            String finalText = text;
            ((WallpaperViewHolder) holder).ll_copy_quote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Tosy", finalText);
                    // Check if context is not null
                    if (ctx != null) {
                        // Get the clipboard manager
                        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard != null) {
                            // Create a new ClipData object
                            ClipData clip = ClipData.newPlainText("label", finalText);
                            // Set the clip on the clipboard
                            clipboard.setPrimaryClip(clip);
                            // Show a toast indicating success
                            Toast.makeText(ctx, "Quotes Copied", Toast.LENGTH_SHORT).show();
                        } else {
                            // If clipboard is null, show an error message
                            Log.e("Clipboard", "Clipboard manager is null");
                            Toast.makeText(ctx, "Error copying quotes", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If context is null, show an error message
                        Log.e("Context", "Context is null");
                        Toast.makeText(ctx, "Error copying quotes", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            String finalText2 = text;
            ((WallpaperViewHolder) holder).editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("AAAAAAAAA", images[imagesIndex]);
                    EditPost.navigate((ActivityMain) ctx, finalText2,selectedImage);
                }
            });


            //When You Press Share Button
            String finalText1 = text;
            ((WallpaperViewHolder) holder).ll_quote_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup();
                }

                private void popup() {
                    PopupMenu popup = new PopupMenu(ctx, ((WallpaperViewHolder) holder).ll_quote_share);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.sub_text:
                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                    shareIntent.setType("text/plain");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, finalText1 + "\n https://play.google.com/store/apps/details?id=" + ctx.getPackageName());
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,ctx.getString(R.string.app_name));
                                    ctx.startActivity(Intent.createChooser(shareIntent, "Share Quote"));
                                    Toast.makeText(ctx, "Share as Text", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.sub_image:
                                    //((WallpaperViewHolder) holder).tv_quotes_watermark.setVisibility(View.VISIBLE);
                                    Bitmap bitmap = Bitmap.createBitmap(((WallpaperViewHolder) holder).relativeLayout.getWidth(), ((WallpaperViewHolder) holder).relativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
                                    Canvas canvas = new Canvas(bitmap);
                                    ((WallpaperViewHolder) holder).relativeLayout.draw(canvas);
                                    if (bitmap != null) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("image/png"); // Set the type to image/png
                                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                                        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + ctx.getPackageName());
                                        ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.app_name)));
                                        Toast.makeText(ctx, "Share as Image", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ctx, "Failed to share image", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(ctx, "Share as Image", Toast.LENGTH_SHORT).show();

                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.inflate(R.menu.menu_item);

                    popup.show();
                }
            });

           // ThisApp.dao().insertListing(EntityListing.entity(obj));
            if (ThisApp.dao().getListing(obj.id) != null)
                ((WallpaperViewHolder) holder).favBtn.setLiked(true);
            else
                ((WallpaperViewHolder) holder).favBtn.setLiked(false);

            ((WallpaperViewHolder) holder).favBtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if (ThisApp.dao().getListing(obj.id) == null){
                        ((WallpaperViewHolder) holder).favBtn.setLiked(true);
                        ThisApp.dao().insertListing(EntityListing.entity(obj));
                    } else {
                        ((WallpaperViewHolder) holder).favBtn.setLiked(false);
                        ThisApp.dao().deleteListing(obj.id);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    if (ThisApp.dao().getListing(obj.id) == null){
                        ((WallpaperViewHolder) holder).favBtn.setLiked(true);
                        ThisApp.dao().insertListing(EntityListing.entity(obj));
                    } else {
                        ((WallpaperViewHolder) holder).favBtn.setLiked(false);
                        ThisApp.dao().deleteListing(obj.id);
                    }
                }
            });








        } else if (holder instanceof SectionTabViewHolder) {
            if (categoryInitiated) return;
            final SectionCategory obj = (SectionCategory) items.get(position);
            SectionTabViewHolder v = (SectionTabViewHolder) holder;
            v.lytParent.removeAllViews();
            for (String cat : obj.categories) {
                TextView textView = new TextView(ctx);
                // set margin
                int marginHorizontal = ctx.getResources().getDimensionPixelOffset(R.dimen.spacing_2);
                int marginVertical = ctx.getResources().getDimensionPixelOffset(R.dimen.spacing_4);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(marginHorizontal, marginVertical * 4, marginHorizontal * 4, 0);
                textView.setLayoutParams(params);

                // set padding
                int paddingHorizontal = ctx.getResources().getDimensionPixelOffset(R.dimen.spacing_15);
                int padding1 = ctx.getResources().getDimensionPixelOffset(R.dimen.spacing_1);
                int paddingVertical = textView.getPaddingTop();
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setPadding(paddingHorizontal, paddingVertical + padding1, paddingHorizontal, paddingVertical);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(ctx.getResources().getColor(R.color.textIconPrimary));

                // set font and color
                textView.setBackgroundResource(R.drawable.button_tab_category);
                //textView.setBackgroundColor(ctx.getResources().getColor(R.color.accent));
                textView.setText(cat);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.txt_small));
                textView.setOnClickListener(view -> {
                    if (listener == null) return;
                    if (selectedCategory != null && selectedCategory.equals(cat)) {
                        selectedCategory = null;
                        previousCategory = null;
                        textView.setSelected(false);
                        textView.setTextColor(ctx.getResources().getColor(R.color.textIconPrimary));
                    } else {
                        selectedCategory = cat;
                        textView.setSelected(true);
                        textView.setTextColor(ctx.getResources().getColor(R.color.cardClearBg));
                        if (previousCategory != null) {
                            previousCategory.setSelected(false);
                            previousCategory.setTextColor(ctx.getResources().getColor(R.color.textIconPrimary));
                        }
                        previousCategory = textView;
                    }
                    listener.onClick(view, ActionType.CATEGORY.name(), cat, position);
                });

                v.lytParent.addView(textView);
            }
            categoryInitiated = true;
        }
        else {

            final ProgressViewHolder v = (ProgressViewHolder) holder;
            v.progress_loading.setVisibility(status == null ? View.VISIBLE : View.INVISIBLE);
            v.status_loading.setVisibility(status == null ? View.INVISIBLE : View.VISIBLE);

            if (status == null) return;
            v.status_loading.setText(status);
            v.status_loading.setOnClickListener(view -> {
                setLoaded();
                onLoadMore();
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = items.get(position);
        if (obj instanceof Wallpaper) {
            return VIEW_ITEM;
        } else if (obj instanceof SectionCategory) {
            return VIEW_TOP_TAB;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertCategory(SectionCategory category) {
        this.items.add(category);
        notifyItemInserted(0);
    }

    public void insertData(List<Wallpaper> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        status = null;
        loading = false;
        int last_index = getItemCount() - 1;
        if (last_index > -1 && items.get(last_index) == null) {
            items.remove(last_index);
            notifyItemRemoved(last_index);
        }
    }

    public void setLoadingOrFailed(String status) {
        setLoaded();
        this.status = status;
        this.items.add(null);
        notifyItemInserted(getItemCount() - 1);
        loading = true;
    }

    public void resetListData() {
        Object obj = this.items.size() == 0 ? null : this.items.get(0);
        int size = this.items.size();
        if (obj instanceof SectionCategory) {
            this.items = this.items.subList(0, 1);
            notifyItemRangeRemoved(1, size);
        } else {
            this.items = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    boolean scrollDown = false;

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE || !scrollDown) return;
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    boolean bottom = lastPos >= getItemCount() - page;
                    //Log.d("VVVVVVVVV", bottom+""dd);
                    if (!loading && bottom && listener != null) {
                        onLoadMore();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    scrollDown = dy > 0;
                }
            });

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    int spanCount = layoutManager.getSpanCount();
                    return type == VIEW_ITEM ? 1 : spanCount;
                }
            });
        }
    }

    private void onLoadMore() {
        int current_page = getItemCount() / page;
        listener.onLoadMore(current_page);
        loading = true;
        status = null;
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder {


        TextView  tv_save_quote;
        TextView txtQuote , likeText;
        TextView txtCategory;
        ImageView iv_save_quote, tv_quotes_watermark;
        RelativeLayout relativeLayout;
        //private Quote qte;
        LinearLayout ll_quote_save, ll_copy_quote, ll_quote_share;
        ImageView imgIcon,editImage;
        LikeButton favBtn;
        PrefManager prf;
        View darkView;
        ImageView theam1_1,theam1_2,theam2_1;
        View theam2_2;
        TextView tag;
        String lastSetText;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            txtQuote = itemView.findViewById(R.id.txtQuote);
            relativeLayout = itemView.findViewById(R.id.llBackground);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtQuote = itemView.findViewById(R.id.txtQuote);
            tv_quotes_watermark = itemView.findViewById(R.id.tv_quotes_watermark);
            likeText = itemView.findViewById(R.id.tv_like_quote_text);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            ll_copy_quote = itemView.findViewById(R.id.ll_copy_quote);
            ll_quote_save = itemView.findViewById(R.id.ll_quote_save);
            ll_quote_share = itemView.findViewById(R.id.ll_quote_share);
            tv_save_quote = itemView.findViewById(R.id.tv_save_quote);
            iv_save_quote = itemView.findViewById(R.id.iv_save_quote);
            favBtn = itemView.findViewById(R.id.favBtn);
            darkView = itemView.findViewById(R.id.darkView);
            editImage = itemView.findViewById(R.id.editImage);
            lastSetText = ""; // Initialize lastSetText

            theam1_1 = itemView.findViewById(R.id.theam1_1);
            theam1_2 = itemView.findViewById(R.id.theam1_2);
            theam2_1 = itemView.findViewById(R.id.theam2_1);
            theam2_2 = itemView.findViewById(R.id.theam2_2);
            tag  = itemView.findViewById(R.id.tag_view);



        }
    }

    private void requestStoragePermission(){
       if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)ctx,Manifest.permission.READ_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(ctx)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions((Activity)ctx,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }else {
            requestPermissions((Activity)ctx,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            // Store the bitmap in the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Image_" + System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Open an output stream to write the bitmap data
            OutputStream out = ctx.getContentResolver().openOutputStream(uri);
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






}
