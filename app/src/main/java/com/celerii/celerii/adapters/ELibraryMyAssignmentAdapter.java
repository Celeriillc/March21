package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.ELibraryStudentPerformanceDetailActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryParentAssignmentActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryAssignmentDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ELibraryMyAssignmentAdapter extends RecyclerView.Adapter<ELibraryMyAssignmentAdapter.MyViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList;
    String activeStudent;
    private String type;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, className, dueDate, averagePerformance;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            className = (TextView) view.findViewById(R.id.classname);
            dueDate = (TextView) view.findViewById(R.id.duedate);
            averagePerformance = (TextView) view.findViewById(R.id.averageperformance);
            clickableView = view;
        }
    }

    public ELibraryMyAssignmentAdapter(List<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList, String activeStudent, String type, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.eLibraryMyAssignmentModelList = eLibraryMyAssignmentModelList;
        this.activeStudent = activeStudent;
        this.type = type;
        this.context = context;
    }

    public ELibraryMyAssignmentAdapter(List<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList, String type, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.eLibraryMyAssignmentModelList = eLibraryMyAssignmentModelList;
        this.type = type;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_library_my_assignment_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ELibraryMyAssignmentModel eLibraryMyAssignmentModel = eLibraryMyAssignmentModelList.get(position);

        String className = "Class: " + eLibraryMyAssignmentModel.getClassName();
        String dueDate = "Due Date: " + Date.DateFormatMMDDYYYY(eLibraryMyAssignmentModel.getDueDate()) + " by " + Date.DateFormatHHMM(eLibraryMyAssignmentModel.getDueDate());
        String averagePerformance = "";

        if (type.equals("TeacherMyAssignment")) {
            holder.dueDate.setVisibility(View.GONE);
            holder.averagePerformance.setVisibility(View.VISIBLE);
            averagePerformance = "Average Performance: " + String.valueOf((int) Double.parseDouble(eLibraryMyAssignmentModel.getPerformance())) + "%";
        } else if (type.equals("ParentMyAssignment")) {
            holder.dueDate.setVisibility(View.VISIBLE);
            holder.averagePerformance.setVisibility(View.GONE);
        } else if (type.equals("ParentMyPerformance")) {
            holder.dueDate.setVisibility(View.GONE);
            holder.averagePerformance.setVisibility(View.VISIBLE);
            averagePerformance = "Performance: " + String.valueOf((int) Double.parseDouble(eLibraryMyAssignmentModel.getPerformance())) + "%";
        }

        holder.title.setText(eLibraryMyAssignmentModel.getMaterialTitle());
        holder.className.setText(className);
        holder.dueDate.setText(dueDate);
        holder.averagePerformance.setText(averagePerformance);

        Glide.with(context)
                .load(eLibraryMyAssignmentModel.getMaterialThumbnailURL())
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(holder.image);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent I;
                Bundle bundle;

                if (type.equals("TeacherMyAssignment")) {
                    I = new Intent(context, ELibraryAssignmentDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("AssignmentID", eLibraryMyAssignmentModel.getAssignmentID());
                    bundle.putString("materialID", eLibraryMyAssignmentModel.getMaterialID());
                    bundle.putString("Title", eLibraryMyAssignmentModel.getMaterialTitle());
                    bundle.putString("ClassID", eLibraryMyAssignmentModel.getClassID());
                    bundle.putString("ClassName", eLibraryMyAssignmentModel.getClassName());
                    bundle.putString("Date", eLibraryMyAssignmentModel.getDateGiven());
                    bundle.putString("Sortable Date", eLibraryMyAssignmentModel.getSortableDateGiven());
                } else if (type.equals("ParentMyAssignment")) {
                    I = new Intent(context, ELibraryParentAssignmentActivity.class);
                    bundle = new Bundle();
                    bundle.putString("materialId", eLibraryMyAssignmentModel.getMaterialID());
                    bundle.putString("assignmentID", eLibraryMyAssignmentModel.getAssignmentID());
                    bundle.putString("activeStudent", activeStudent);
                } else { /*if (type.equals("ParentMyPerformance"))*/
                    Gson gson = new Gson();
                    Type type = new TypeToken<Student>() {}.getType();

                    Student activeStudentModel = gson.fromJson(activeStudent, type);
                    String activeStudentID = activeStudentModel.getStudentID();
                    String activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

                    I = new Intent(context, ELibraryStudentPerformanceDetailActivity.class);
                    bundle = new Bundle();
                    bundle.putString("assignmentID", eLibraryMyAssignmentModel.getAssignmentID());
                    bundle.putString("studentID", activeStudentID);
                    bundle.putString("studentName", activeStudentName);
                    bundle.putString("score", eLibraryMyAssignmentModel.getPerformance());
                }

                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eLibraryMyAssignmentModelList.size();
    }
}
