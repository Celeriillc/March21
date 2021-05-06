package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryAssignmentDetailHeaderModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceDetailHeaderModel;
import com.celerii.celerii.models.QuestionModel;

import java.util.List;

public class ELibraryStudentPerformanceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SharedPreferencesManager sharedPreferencesManager;
    private List<QuestionModel> questionModelList;
    private ELibraryStudentPerformanceDetailHeaderModel eLibraryStudentPerformanceDetailHeaderModel;
    private Context context;
    private Activity activity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout optionABackground, optionBBackground, optionCBackground, optionDBackground;
        public TextView question, optionA, optionB, optionC, optionD, optionALabel, optionBLabel, optionCLabel, optionDLabel;

        public MyViewHolder(final View view) {
            super(view);
            optionABackground = (LinearLayout) view.findViewById(R.id.optionabackground);
            optionBBackground = (LinearLayout) view.findViewById(R.id.optionbbackground);
            optionCBackground = (LinearLayout) view.findViewById(R.id.optioncbackground);
            optionDBackground = (LinearLayout) view.findViewById(R.id.optiondbackground);

            question = (TextView) view.findViewById(R.id.question);
            optionA = (TextView) view.findViewById(R.id.optiona);
            optionB = (TextView) view.findViewById(R.id.optionb);
            optionC = (TextView) view.findViewById(R.id.optionc);
            optionD = (TextView) view.findViewById(R.id.optiond);
            optionALabel = (TextView) view.findViewById(R.id.optionalabel);
            optionBLabel = (TextView) view.findViewById(R.id.optionblabel);
            optionCLabel = (TextView) view.findViewById(R.id.optionclabel);
            optionDLabel = (TextView) view.findViewById(R.id.optiondlabel);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name, title, className, date, score;

        public HeaderViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            title = (TextView) view.findViewById(R.id.title);
            className = (TextView) view.findViewById(R.id.classname);
            date = (TextView) view.findViewById(R.id.date);
            score = (TextView) view.findViewById(R.id.score);
        }
    }

    public ELibraryStudentPerformanceDetailAdapter(List<QuestionModel> questionModelList, ELibraryStudentPerformanceDetailHeaderModel eLibraryStudentPerformanceDetailHeaderModel, Activity activity, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.questionModelList = questionModelList;
        this.eLibraryStudentPerformanceDetailHeaderModel = eLibraryStudentPerformanceDetailHeaderModel;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_row, parent, false);
                return new MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.e_library_student_performance_detail_header, parent, false);
                return new HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

            ((HeaderViewHolder) holder).name.setText(eLibraryStudentPerformanceDetailHeaderModel.getStudentName());
            ((HeaderViewHolder) holder).title.setText(eLibraryStudentPerformanceDetailHeaderModel.getTitle());
            ((HeaderViewHolder) holder).className.setText(eLibraryStudentPerformanceDetailHeaderModel.getClassName());
            ((HeaderViewHolder) holder).date.setText(Date.DateFormatMMDDYYYY(eLibraryStudentPerformanceDetailHeaderModel.getDate()));
            ((HeaderViewHolder) holder).score.setText(String.valueOf((int) Double.parseDouble(eLibraryStudentPerformanceDetailHeaderModel.getScore())));

        } else if (holder instanceof MyViewHolder) {
            QuestionModel questionModel = questionModelList.get(position);

            ((MyViewHolder) holder).question.setText(questionModel.getQuestion());
            ((MyViewHolder) holder).optionABackground.setBackgroundResource(0);
            ((MyViewHolder) holder).optionBBackground.setBackgroundResource(0);
            ((MyViewHolder) holder).optionCBackground.setBackgroundResource(0);
            ((MyViewHolder) holder).optionDBackground.setBackgroundResource(0);

//            ((MyViewHolder) holder).optionABackground.setPadding(0, 10, 0, 10);
//            ((MyViewHolder) holder).optionBBackground.setPadding(0, 10, 0, 10);
//            ((MyViewHolder) holder).optionCBackground.setPadding(0, 10, 0, 10);
//            ((MyViewHolder) holder).optionDBackground.setPadding(0, 10, 0, 10);

            ((MyViewHolder) holder).optionA.setText(questionModel.getOptionA());
            ((MyViewHolder) holder).optionB.setText(questionModel.getOptionB());
            ((MyViewHolder) holder).optionC.setText(questionModel.getOptionC());
            ((MyViewHolder) holder).optionD.setText(questionModel.getOptionD());

            ((MyViewHolder) holder).optionA.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionALabel.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionB.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionC.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionD.setTextColor(ContextCompat.getColor(context, R.color.black));
            ((MyViewHolder) holder).optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.black));

            if (questionModel.getAnswer().equals(questionModel.getOptionA())) {
//                ((MyViewHolder) holder).optionABackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionABackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionA.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionALabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            } else if (questionModel.getAnswer().equals(questionModel.getOptionB())) {
//                ((MyViewHolder) holder).optionBBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionBBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionB.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            } else if (questionModel.getAnswer().equals(questionModel.getOptionC())) {
//                ((MyViewHolder) holder).optionCBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionCBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionC.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            } else if (questionModel.getAnswer().equals(questionModel.getOptionD())) {
//                ((MyViewHolder) holder).optionDBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionD.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }

            if (questionModel.getSelectedAnswer().equals(questionModel.getOptionA())) {
//                ((MyViewHolder) holder).optionABackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionABackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                ((MyViewHolder) holder).optionA.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                ((MyViewHolder) holder).optionALabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else if (questionModel.getSelectedAnswer().equals(questionModel.getOptionB())) {
//                ((MyViewHolder) holder).optionBBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionBBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                ((MyViewHolder) holder).optionB.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                ((MyViewHolder) holder).optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else if (questionModel.getSelectedAnswer().equals(questionModel.getOptionC())) {
//                ((MyViewHolder) holder).optionCBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionCBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                ((MyViewHolder) holder).optionC.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                ((MyViewHolder) holder).optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else if (questionModel.getSelectedAnswer().equals(questionModel.getOptionD())) {
//                ((MyViewHolder) holder).optionDBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                ((MyViewHolder) holder).optionD.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                ((MyViewHolder) holder).optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }
    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
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
        return position == questionModelList.size () - 1;
    }
}
