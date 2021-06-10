package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.ELibraryBooksDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryMaterialsModel;

import java.util.List;

public class ELibraryBooksListAdapter extends RecyclerView.Adapter<ELibraryBooksListAdapter.MyViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<ELibraryMaterialsModel> eLibraryMaterialsModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image, icon;
        public TextView title, author;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            author = (TextView) view.findViewById(R.id.author);
            clickableView = view;
        }
    }

    public ELibraryBooksListAdapter(List<ELibraryMaterialsModel> eLibraryMaterialsModelList, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.eLibraryMaterialsModelList = eLibraryMaterialsModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_library_books_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ELibraryMaterialsModel eLibraryMaterialsModel = eLibraryMaterialsModelList.get(position);

        holder.title.setText(eLibraryMaterialsModel.getTitle());
        holder.author.setText(eLibraryMaterialsModel.getAuthor());

        if (eLibraryMaterialsModel.getMaterialThumbnailURL().trim().isEmpty()) {
            if (eLibraryMaterialsModel.getType().equals("pdf")) {
                if (position % 2 == 0) {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_accent_24));
                }
            } else if (eLibraryMaterialsModel.getType().equals("video")) {
                if (position % 2 == 0) {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_purple_24));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_accent_24));
                }
            } else if (eLibraryMaterialsModel.getType().equals("audio")) {
                if (position % 2 == 0) {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_purple_24));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_accent_24));
                }
            } else {
                if (position % 2 == 0) {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_purple_24));
                } else {
                    holder.image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_accent_24));
                }
            }
        } else {
            if (eLibraryMaterialsModel.getType().equals("pdf")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_white_24));
            } else if (eLibraryMaterialsModel.getType().equals("video")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_white_24));
            } else if (eLibraryMaterialsModel.getType().equals("audio")) {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_white_24));
            } else {
                holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_white_24));
            }
            Glide.with(context)
                    .load(eLibraryMaterialsModel.getMaterialThumbnailURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(holder.image);
        }

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ELibraryBooksDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", eLibraryMaterialsModel.getMaterialID());
                bundle.putString("titleString", eLibraryMaterialsModel.getTitle());
                bundle.putString("authorString", eLibraryMaterialsModel.getAuthor());
                bundle.putString("typeString", eLibraryMaterialsModel.getType());
                bundle.putString("descriptionString", eLibraryMaterialsModel.getDescription());
                bundle.putString("thumbnailURL", eLibraryMaterialsModel.getMaterialThumbnailURL());
                bundle.putString("materialURL", eLibraryMaterialsModel.getMaterialURL());
                bundle.putString("materialUploader", eLibraryMaterialsModel.getUploader());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eLibraryMaterialsModelList.size();
    }
}
