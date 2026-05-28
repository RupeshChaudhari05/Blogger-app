package com.blogger.wallpaper.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.blogger.wallpaper.R;
import com.blogger.wallpaper.activity.EditPost;
import com.blogger.wallpaper.model.ModelColorList;

import java.util.ArrayList;


public class AdapterTextColourPicker extends RecyclerView.Adapter<AdapterTextColourPicker.ViewHolder> {

    Context context;
    private ArrayList<ModelColorList> modelColorListArrayList;
    String nameofmodule;

    public AdapterTextColourPicker(Context context, ArrayList<ModelColorList> modelColorListArrayList, String nameofmodule) {
        this.context = context;
        this.modelColorListArrayList = modelColorListArrayList;
        this.nameofmodule = nameofmodule;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_item_colour, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.ivclr.setImageResource(modelColorListArrayList.get(position).getColour());

       // holder.ivclr.setBackgroundColor(modelColorListArrayList.get(position).getColour());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(nameofmodule.equals("bgTextcolor"))
                {
                    EditPost editPost = EditPost.getInstance();
                    if (editPost != null) editPost.setTextbackgroundcolor(modelColorListArrayList.get(position).getColour());
                }

//                if(nameofmodule.equals("overlay"))
//                {
//                    ActivityCreatePost.getInstance().setOverlayBackground(modelColorListArrayList.get(position).getColour());
//
//                }else if(nameofmodule.equals("overlay_custom"))
//                {
//                    CreateCustomImageFragment.getInstance().setOverlayBackground(modelColorListArrayList.get(position).getColour());
//
//                }else if(nameofmodule.equals("fontcolor"))
//                {
//                    CreateCustomImageFragment.getInstance().setFontColor(modelColorListArrayList.get(position).getColour());
//
//                }else if(nameofmodule.equals("bgTextcolor"))
//                {
//                    ActivityCreatePost.getInstance().setTextbackgroundcolor(modelColorListArrayList.get(position).getColour());
//                }else
//                {
//                    EditPost.getInstance().setbackgroundcolor(modelColorListArrayList.get(position).getColour());
//
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelColorListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivclr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivclr=itemView.findViewById(R.id.ivColor);
        }
    }
}
