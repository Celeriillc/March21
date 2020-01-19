package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SetupSchoolListRow;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/31/2017.
 */

public class SetupSchoolListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private List<SetupSchoolListRow> setupSchoolListRowList;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView schoolName, schoolAddress;
        public ImageView schoolImage;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            schoolName = (TextView) view.findViewById(R.id.schoolname);
            schoolAddress = (TextView) view.findViewById(R.id.schooladdress);
            schoolImage = (ImageView) view.findViewById(R.id.schoolpic);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    public SetupSchoolListAdapter(List<SetupSchoolListRow> setupSchoolListRowList, Context context) {
        this.setupSchoolListRowList = setupSchoolListRowList;
        this.context = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        sharedPreferencesManager = new SharedPreferencesManager(context);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_school_list_row, parent, false);
                return new SetupSchoolListAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_school_list_header, parent, false);
                return new SetupSchoolListAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setup_school_list_row, parent, false);
                return new SetupSchoolListAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {

        }
        else {
            final SetupSchoolListRow setupSchoolListRow = setupSchoolListRowList.get(position);

            ((MyViewHolder)holder).schoolName.setText(setupSchoolListRow.getSchoolName());
            ((MyViewHolder)holder).schoolAddress.setText(setupSchoolListRow.getSchoolAddress());

            Glide.with(context)
                    .load(setupSchoolListRow.getSchoolImageURL())
                    .crossFade()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder)holder).schoolImage);

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance();
                    String time = String.valueOf(calendar.get(Calendar.YEAR)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
                            String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE))
                            + ":" + String.valueOf(calendar.get(Calendar.SECOND)) + ":" + String.valueOf(calendar.get(Calendar.MILLISECOND));
                    final TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = new TeacherSchoolConnectionRequest("Pending", time, "", sharedPreferencesManager.getMyUserID(), setupSchoolListRow.getSchoolId());
                    Map<String, Object> newRequestMap = new HashMap<String, Object>();

                    newRequestMap.put("Teacher To School Request School/" + setupSchoolListRow.getSchoolId() + "/" + sharedPreferencesManager.getMyUserID(), teacherSchoolConnectionRequest);
                    newRequestMap.put("Teacher To School Request Teacher/" + sharedPreferencesManager.getMyUserID() + "/" + setupSchoolListRow.getSchoolId(), teacherSchoolConnectionRequest);

                    mDatabaseReference = mFirebaseDatabase.getReference();
                    mDatabaseReference.updateChildren(newRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            applicationLauncherSharedPreferences.setLauncherActivity("Home");
                            Intent I = new Intent(context, TeacherMainActivityTwo.class);
                            context.startActivity(I);
                            ((Activity)context).finish();
                        }
                    });

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return setupSchoolListRowList.size();
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
        return position == setupSchoolListRowList.size () + 1;
    }
}
