package com.celerii.celerii.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Teacher.MoreTeacherFragment;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.models.Class;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/8/2017.
 */

public class MoreTeacherAdapter extends RecyclerView.Adapter<MoreTeacherAdapter.MyViewHolder> {
    private List<Class> moreTeachersModelList;
    private Context context;
    private MoreTeacherFragment mFragment;
    private int lastSelectedPosition;
    private SharedPreferencesManager sharedPreferencesManager;
    private TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;
    private TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  TextView className;
        private  ImageView classPic;
        private LinearLayout parentView, classPicClipper;
//        private  RadioButton selectedClass;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            classPic = (ImageView) view.findViewById(R.id.classpic);
            parentView = (LinearLayout) view.findViewById(R.id.parentview);
            classPicClipper = (LinearLayout) view.findViewById(R.id.classpicclipper);
//            selectedClass = (RadioButton) view.findViewById(R.id.selectedclass);
            clickableView = view;
        }
    }

    public MoreTeacherAdapter(List<Class> moreTeachersModelList, Context context, MoreTeacherFragment mFragment) {
        this.moreTeachersModelList = moreTeachersModelList;
        this.context = context;
        this.mFragment = mFragment;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(context);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(context);
        if (moreTeachersModelList.size() == 0){
            lastSelectedPosition = -1;
        }
    }

    @Override
    public MoreTeacherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;

        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_body_teacher, parent, false);
        return new MoreTeacherAdapter.MyViewHolder(rowView);
    }

    public void onBindViewHolder(final MoreTeacherAdapter.MyViewHolder holder, final int position) {
        final Class moreTeachersModel = moreTeachersModelList.get(position);

        Class activeClass = null;
        Gson gson = new Gson();
        String activeClassJSON = sharedPreferencesManager.getActiveClass();
        Type type = new TypeToken<Class>() {}.getType();
        activeClass = gson.fromJson(activeClassJSON, type);

            if (activeClass.getID().equals(moreTeachersModel.getID())) {
                holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple));
                holder.className.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_white_with_purple_border));
                holder.className.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }

        holder.classPicClipper.setClipToOutline(true);
        holder.className.setText(moreTeachersModel.getClassName());

        Glide.with(context)
                .load(moreTeachersModel.getClassPicURL())
                .centerCrop()
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(((MyViewHolder) holder).classPic);

        ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(moreTeachersModel);
                sharedPreferencesManager.setActiveClass(json);

                teacherTakeAttendanceSharedPreferences.deleteSubject();
                teacherEnterResultsSharedPreferences.deleteSubject();

                notifyDataSetChanged();
                mFragment.loadFooter();
            }
        });
    }

    @Override
    public int getItemCount() {
        return moreTeachersModelList.size();
    }
}
