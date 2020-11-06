package com.celerii.celerii.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.ManageClassesModel;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 12/17/2017.
 */

public class ManageClassesAdapter extends RecyclerView.Adapter<ManageClassesAdapter.MyViewHolder> {
    private List<ManageClassesModel> manageClassesModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className;
        public ImageView classPic;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.kidname);
            classPic = (ImageView) view.findViewById(R.id.kidpic);
            clickableView = view;
        }
    }

    public ManageClassesAdapter(List<ManageClassesModel> manageClassesModelList, Context context) {
        this.manageClassesModelList = manageClassesModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_kids_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ManageClassesModel manageClassesModel = manageClassesModelList.get(position);

        holder.className.setText(manageClassesModel.getName());
        if (!manageClassesModel.getPicURL().isEmpty()) {
            Glide.with(context)
                    .load(manageClassesModel.getPicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.classPic);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.classPic);
        }
        final String classId = manageClassesModel.getClassId();


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ClassID", classId);
//                Intent I = new Intent(context, FAQActivity.class);
//                I.putExtras(bundle);
//                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return manageClassesModelList.size();
    }
}
