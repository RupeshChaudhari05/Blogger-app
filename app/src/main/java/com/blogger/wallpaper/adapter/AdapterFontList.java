package com.blogger.wallpaper.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.blogger.wallpaper.R;
import com.blogger.wallpaper.activity.EditPost;
import com.blogger.wallpaper.model.ModelFontDetail;

import java.util.ArrayList;

public class AdapterFontList extends RecyclerView.Adapter<AdapterFontList.ViewHolder> {

    Context context;
    ArrayList<ModelFontDetail> modelFontDetailArrayList;
    String checkname;

    public AdapterFontList(Context context, ArrayList<ModelFontDetail> modelFontDetailArrayList, String checkname) {
        this.context = context;
        this.modelFontDetailArrayList = modelFontDetailArrayList;
        this.checkname = checkname;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_rv_fontlist, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String[] fontlist1 = modelFontDetailArrayList.get(position).getFontName().split("\\.");
        holder.tv_font.setText(fontlist1[0].replace("-", " "));

        holder.tv_font.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + modelFontDetailArrayList.get(position).getFontName()));

        holder.tv_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkname.equals("fontforstickertext")) {
                    //EditPost.getInstance().SetFontToText(modelFontDetailArrayList.get(position).getFontName());

                }
                if (checkname.equals("fontstyle")) {
                   // EditPost.getInstance().setFontStyle(modelFontDetailArrayList.get(position).getFontName());

                }
                if (checkname.equals("greetingstyle")) {
                    EditPost editPost = EditPost.getInstance();
                    if (editPost != null) editPost.setFontStyle(modelFontDetailArrayList.get(position).getFontName());
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return modelFontDetailArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_font;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_font = itemView.findViewById(R.id.textview_fondemot);
        }
    }
}
