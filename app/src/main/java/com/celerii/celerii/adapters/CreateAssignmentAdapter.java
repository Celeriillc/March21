package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.ELibrary.Teacher.CreateAssignmentActivity;
import com.celerii.celerii.Activities.ELibrary.Teacher.CreateQuestionActivity;
import com.celerii.celerii.Activities.ELibrary.Teacher.ELibraryLoadTemplateActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditClassActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.QuestionModel;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.gson.Gson;

import java.util.List;

public class CreateAssignmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<QuestionModel> questionModelList;
    private Context context;
    private ELibraryMyAssignmentModel eLibraryMyAssignmentModel;
    private Activity activity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView deleteQuestion;
        public LinearLayout optionABackground, optionBBackground, optionCBackground, optionDBackground;
        public TextView question, optionA, optionB, optionC, optionD, optionALabel, optionBLabel, optionCLabel, optionDLabel;

        public MyViewHolder(final View view) {
            super(view);

            deleteQuestion = (ImageView) view.findViewById(R.id.deletequestion);

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
        TextView className, date, time;
        LinearLayout classLayout, dateLayout, timeLayout;

        public HeaderViewHolder(View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            time = (TextView) view.findViewById(R.id.time);
            date = (TextView) view.findViewById(R.id.date);
            classLayout = (LinearLayout) view.findViewById(R.id.classlayout);
            dateLayout = (LinearLayout) view.findViewById(R.id.datelayout);
//            timeLayout = (LinearLayout) view.findViewById(R.id.timelayout);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView addQuestion, loadATemplate;
        Button createAssignment;

        public FooterViewHolder(View view) {
            super(view);
            addQuestion = (TextView) view.findViewById(R.id.addquestion);
            loadATemplate = (TextView) view.findViewById(R.id.loadatemplate);
            createAssignment = (Button) view.findViewById(R.id.createassignment);
        }
    }

    public CreateAssignmentAdapter(List<QuestionModel> questionModelList, ELibraryMyAssignmentModel eLibraryMyAssignmentModel, Activity activity, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.questionModelList = questionModelList;
        this.eLibraryMyAssignmentModel = eLibraryMyAssignmentModel;
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
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_assignment_header, parent, false);
                return new HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_assignment_footer, parent, false);
                return new FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

            ((HeaderViewHolder)holder).className.setText(eLibraryMyAssignmentModel.getClassName());
            ((HeaderViewHolder)holder).date.setText(Date.DateFormatMMDDYYYY(eLibraryMyAssignmentModel.getDueDate()));
//            ((HeaderViewHolder)holder).time.setText(Date.DateFormatHHMM(eLibraryMyAssignmentModel.getDueDate()));

            ((HeaderViewHolder) holder).className.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, EditClassActivity.class);
                    Bundle bundle = new Bundle();
                    Class classInstance = new Class(eLibraryMyAssignmentModel.getClassName(), eLibraryMyAssignmentModel.getClassID(), false);
                    Gson gson = new Gson();
                    String classJson = gson.toJson(classInstance);
                    bundle.putString("Class", classJson);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, 0);
                }
            });

            ((HeaderViewHolder) holder).date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyCustomBetterPickersDialogs);;
                    cdp.show(((CreateAssignmentActivity)context).getSupportFragmentManager(), "Material Calendar Example");

                    cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                        @Override
                        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                            Intent informationIntent = new Intent("Date Information");
                            informationIntent.putExtra("Year", String.valueOf(year));
                            informationIntent.putExtra("Month", String.valueOf(monthOfYear + 1));
                            informationIntent.putExtra("Day", String.valueOf(dayOfMonth));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(informationIntent);
                        }
                    });
                }
            });

//            ((HeaderViewHolder) holder).time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    TimePickerBuilder tpb = new TimePickerBuilder().setFragmentManager(((CreateAssignmentActivity)context).getSupportFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment);
//                    tpb.show();
//
//                    tpb.addTimePickerDialogHandler(new TimePickerDialogFragment.TimePickerDialogHandler() {
//                        @Override
//                        public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
//                            Intent informationIntent = new Intent("Time Information");
//                            informationIntent.putExtra("Hour", String.valueOf(hourOfDay));
//                            informationIntent.putExtra("Minute", String.valueOf(minute));
//                            LocalBroadcastManager.getInstance(context).sendBroadcast(informationIntent);
//                        }
//                    });
//                }
//            });

        } else if (holder instanceof FooterViewHolder) {

            ((FooterViewHolder) holder).addQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CreateQuestionActivity.class);
                    activity.startActivityForResult(intent, 10);
                }
            });

            ((FooterViewHolder) holder).loadATemplate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ELibraryLoadTemplateActivity.class);
                    activity.startActivityForResult(intent, 11);
                }
            });

            ((FooterViewHolder) holder).createAssignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof CreateAssignmentActivity) {
                        ((CreateAssignmentActivity)context).saveToCloud();
                    }
                }
            });

        } else if (holder instanceof MyViewHolder) {
            QuestionModel questionModel = questionModelList.get(position);

            ((MyViewHolder) holder).deleteQuestion.setVisibility(View.VISIBLE);

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
            } else {
//                ((MyViewHolder) holder).optionDBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionD.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }

            ((MyViewHolder) holder).deleteQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteQuestion(position);
                }
            });
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

    private void deleteQuestion(int position) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button endTest = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText(Html.fromHtml("Do you wish to delete this question? This process can not be undone."));

        endTest.setText("Delete");
        cancel.setText("Cancel");

        endTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                questionModelList.remove(position);
                notifyDataSetChanged();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
