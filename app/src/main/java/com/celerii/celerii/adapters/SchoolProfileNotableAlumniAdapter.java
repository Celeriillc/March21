package com.celerii.celerii.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.NotableAlumni;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SchoolProfileNotableAlumniAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotableAlumni> notableAlumniList;
    private String schoolName;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notableAlumni, notableAlumniSet;
        public ImageView notableAlumniPic;
        public LinearLayout notableAlumniPicClipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            notableAlumni = (TextView) view.findViewById(R.id.notablealumni);
            notableAlumniSet = (TextView) view.findViewById(R.id.notablealumniset);
            notableAlumniPic = (ImageView) view.findViewById(R.id.notablealumnipic);
            notableAlumniPicClipper = (LinearLayout) view.findViewById(R.id.notablealumnipicclipper);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SchoolProfileNotableAlumniAdapter(List<NotableAlumni> notableAlumniList, Context context) {
        this.notableAlumniList = notableAlumniList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_notable_alumni_row, parent, false);
                return new SchoolProfileNotableAlumniAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_notable_alumni_header, parent, false);
                return new SchoolProfileNotableAlumniAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_notable_alumni_row, parent, false);
                return new SchoolProfileNotableAlumniAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            if (notableAlumniList.size() > 0) {
                ((HeaderViewHolder) holder).header.setText(notableAlumniList.get(0).getName());
            }
        } else if (holder instanceof MyViewHolder) {
            final NotableAlumni notableAlumni = notableAlumniList.get(position);
            ((MyViewHolder) holder).notableAlumni.setText(notableAlumni.getName());
            String set_note = notableAlumni.getSet() + " - " + notableAlumni.getNote();
            ((MyViewHolder) holder).notableAlumniSet.setText(set_note);
            ((MyViewHolder) holder).notableAlumniPicClipper.setClipToOutline(true);

            Drawable textDrawable;
            if (!notableAlumni.getName().trim().isEmpty()) {
                String[] nameArray = notableAlumni.getName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 45);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 45);
                }
                ((MyViewHolder) holder).notableAlumniPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 45);
            }

            if (!notableAlumni.getProfilePictureURL().isEmpty()) {
                Glide.with(context)
                        .load(notableAlumni.getProfilePictureURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).notableAlumniPic);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notableAlumniList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader (position)) {
            return Header;
        } else if(isPositionFooter (position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == notableAlumniList.size () + 1;
    }
}