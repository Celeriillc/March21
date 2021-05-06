package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.Settings.FAQActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.FAQModel;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.MyViewHolder> {

    private List<String> tagsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tag;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            tag = (TextView) view.findViewById(R.id.tag);
            clickableView = view;
        }
    }

    public TagsAdapter(List<String> tagsList, Context context) {
        this.tagsList = tagsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tags_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String tag = tagsList.get(position);

        holder.tag.setText("#"+tag);
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }
}
