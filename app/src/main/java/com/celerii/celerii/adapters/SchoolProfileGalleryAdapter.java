package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.GalleryModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by user on 7/11/2017.
 */

public class SchoolProfileGalleryAdapter extends RecyclerView.Adapter<SchoolProfileGalleryAdapter.MyViewHolder> {

    private List<GalleryModel> galleryModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.item_img);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public SchoolProfileGalleryAdapter(List<GalleryModel> galleryModelList, Context context) {
        this.galleryModelList = galleryModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row_school_profile, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GalleryModel model = galleryModelList.get(position);

        if (!model.getURL().isEmpty()) {
            Glide.with(context)
                    .load(model.getURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .fitCenter()
                    .into(holder.image);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.profileimageplaceholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(holder.image);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("URL", model.getURL());
                Intent I = new Intent(context, GalleryDetailActivity.class);
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
        return galleryModelList.size();
    }
}
