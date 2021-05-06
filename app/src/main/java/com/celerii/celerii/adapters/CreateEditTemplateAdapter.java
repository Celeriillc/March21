package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.ELibrary.CreateEditTemplateActivity;
import com.celerii.celerii.Activities.ELibrary.CreateQuestionActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.CreateEditTemplateHeaderModel;
import com.celerii.celerii.models.QuestionModel;

import java.util.List;

public class CreateEditTemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SharedPreferencesManager sharedPreferencesManager;
    private List<QuestionModel> questionModelList;
    private CreateEditTemplateHeaderModel createEditTemplateHeaderModel;
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
        TextView headerTitle, questionLabel;
        EditText title;

        public HeaderViewHolder(View view) {
            super(view);
            headerTitle = (TextView) view.findViewById(R.id.headertitle);
            title = (EditText) view.findViewById(R.id.title);
            questionLabel = (TextView) view.findViewById(R.id.questionlabel);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView addQuestion;
        Button createEdit;

        public FooterViewHolder(View view) {
            super(view);
            addQuestion = (TextView) view.findViewById(R.id.addquestion);
            createEdit = (Button) view.findViewById(R.id.createedit);
        }
    }

    public CreateEditTemplateAdapter(List<QuestionModel> questionModelList, CreateEditTemplateHeaderModel createEditTemplateHeaderModel, Activity activity, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.questionModelList = questionModelList;
        this.createEditTemplateHeaderModel = createEditTemplateHeaderModel;
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
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_header, parent, false);
                return new HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_footer, parent, false);
                return new FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_edit_template_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

            ((HeaderViewHolder) holder).title.setText(createEditTemplateHeaderModel.getTemplateTitle());
            ((HeaderViewHolder) holder).title.setImeOptions(EditorInfo.IME_ACTION_DONE);

            ((HeaderViewHolder) holder).title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    createEditTemplateHeaderModel.setTemplateTitle(editable.toString().trim());
                }
            });

        } else if (holder instanceof FooterViewHolder) {

            ((FooterViewHolder) holder).addQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CreateQuestionActivity.class);
                    activity.startActivityForResult(intent, 1);
                }
            });

            ((FooterViewHolder) holder).createEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof CreateEditTemplateActivity) {
                        ((CreateEditTemplateActivity)context).saveToCloud();
                    }
                }
            });

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
            } else {
//                ((MyViewHolder) holder).optionDBackground.setPadding(10, 10, 10, 10);
                ((MyViewHolder) holder).optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                ((MyViewHolder) holder).optionD.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                ((MyViewHolder) holder).optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
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
