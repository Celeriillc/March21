package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailForMultipleImagesActivity;
import com.celerii.celerii.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by user on 7/11/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<String> galleryModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.item_img);
        }
    }

    public GalleryAdapter(List<String> galleryModelList, Context context) {
        this.galleryModelList = galleryModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String model = galleryModelList.get(position);

        Glide.with(context)
                .load(model)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, GalleryDetailForMultipleImagesActivity.class);
                Bundle b = new Bundle();
                b.putInt("currentImage", position);
                I.putExtras(b);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.image.setTransitionName("imageTransition");
                    Pair<View, String> pair1 = Pair.create((View) holder.image, holder.image.getTransitionName());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.image, holder.image.getTransitionName());
                    context.startActivity(I, optionsCompat.toBundle());
                }
                else {
                    context.startActivity(I);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        if (galleryModelList.size() <= 7) {
            return galleryModelList.size();
//        } else {
//            return 7;
//        }
    }
}
