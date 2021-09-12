package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditExamTypeActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditMaxObtainableActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditPercentageOfTotalScoreActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.StudentPerformance.EnterResultsActivity;
import com.celerii.celerii.R;
//import com.celerii.celerii.databinding.EnterResultRowBinding;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.EnterResultHeader;
import com.celerii.celerii.models.EnterResultRow;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by DELL on 8/18/2017.
 */

public class EnterResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EnterResultRow> enterResultRowList;
    private Context context;
    private EnterResultHeader enterResultHeader;
    private Activity myActivity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyCustomEditTextListener myCustomEditTextListener;
        public TextView studentName;
        public EditText score;
        public ImageView studentPic;
        public View clickableView;

        public MyViewHolder(final View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            studentName = (TextView) view.findViewById(R.id.kidname);
            score = (EditText) view.findViewById(R.id.kidscore);
            this.myCustomEditTextListener = myCustomEditTextListener;
            score.addTextChangedListener(myCustomEditTextListener);
            studentPic = (ImageView) view.findViewById(R.id.kidPicture);
            clickableView = view;
        }

        void enableTextWatcher() {
            score.addTextChangedListener(myCustomEditTextListener);
        }

        void disableTextWatcher() {
            score.removeTextChangedListener(myCustomEditTextListener);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView teacher, className, subject, term, date, maximumScore, percentageOfTotal, testType;
        LinearLayout teacherLayout, classNameLayout, subjectLayout, termLayout, dateLayout, maximumScoreLayout, percentageOfTotalLayout,
                testTypeLayout;

        public HeaderViewHolder(View view) {
            super(view);
            teacher = (TextView) view.findViewById(R.id.teacher);
            className = (TextView) view.findViewById(R.id.classname);
            subject = (TextView) view.findViewById(R.id.subject);
            term = (TextView) view.findViewById(R.id.term);
            date = (TextView) view.findViewById(R.id.date);
            maximumScore = (TextView) view.findViewById(R.id.maximumscore);
            percentageOfTotal = (TextView) view.findViewById(R.id.percentageoftotal);
            testType = (TextView) view.findViewById(R.id.testtype);

            teacherLayout = (LinearLayout) view.findViewById(R.id.teacherlayout);
            classNameLayout = (LinearLayout) view.findViewById(R.id.classlayout);
            subjectLayout = (LinearLayout) view.findViewById(R.id.subjectlayout);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            dateLayout = (LinearLayout) view.findViewById(R.id.datelayout);
            maximumScoreLayout = (LinearLayout) view.findViewById(R.id.maximumscorelayout);
            percentageOfTotalLayout = (LinearLayout) view.findViewById(R.id.percentageoftotallayout);
            testTypeLayout = (LinearLayout) view.findViewById(R.id.testtypelayout);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        Button saveToCloud;

        public FooterViewHolder(View view) {
            super(view);
            saveToCloud = (Button) view.findViewById(R.id.savetocloud);
        }
    }

    public EnterResultAdapter(List<EnterResultRow> enterResultRowList, EnterResultHeader enterResultHeader, Activity myActivity,
                                           Context context) {
        this.enterResultRowList = enterResultRowList;
        this.enterResultHeader = enterResultHeader;
        this.myActivity = myActivity;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_result_row, parent, false);
                MyViewHolder vh = new MyViewHolder(rowView, new MyCustomEditTextListener());
                return vh;
//                return new EnterResultAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_result_header, parent, false);
                return new EnterResultAdapter.HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_result_footer, parent, false);
                return new EnterResultAdapter.FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_result_row, parent, false);
                vh = new MyViewHolder(rowView, new MyCustomEditTextListener());
                return vh;
//                return new EnterResultAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).teacher.setText(enterResultHeader.getTeacher());
            ((HeaderViewHolder) holder).className.setText(enterResultHeader.getClassName());
            ((HeaderViewHolder) holder).subject.setText(enterResultHeader.getSubject());
            ((HeaderViewHolder) holder).testType.setText(enterResultHeader.getTestType());
            ((HeaderViewHolder) holder).maximumScore.setText(enterResultHeader.getMaxScore());
            ((HeaderViewHolder) holder).percentageOfTotal.setText(enterResultHeader.getPercentageOfTotal() + "%");
            ((HeaderViewHolder) holder).date.setText(Date.DateFormatMMDDYYYY(enterResultHeader.getDate()));
            ((HeaderViewHolder) holder).term.setText(Term.Term(enterResultHeader.getTerm()));

            ((HeaderViewHolder) holder).subjectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditSubjectsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Active Class", enterResultHeader.getClassID());
                    bundle.putString("Subject", enterResultHeader.getSubject());
                    bundle.putString("Activity", "WriteResult");
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 1);
                }
            });

            ((HeaderViewHolder) holder).testTypeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditExamTypeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Test Type", enterResultHeader.getTestType());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 2);
                }
            });

            ((HeaderViewHolder) holder).maximumScoreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditMaxObtainableActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Maximum Obtainable", enterResultHeader.getMaxScore());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 3);
                }
            });

            ((HeaderViewHolder) holder).percentageOfTotalLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditPercentageOfTotalScoreActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("PreviousPercentageOfTotal", enterResultHeader.getPreviousPercentageOfTotal());
                    bundle.putString("PercentageOfTotal", enterResultHeader.getPercentageOfTotal());
                    String subject_year_term = enterResultHeader.getSubject() + "_" + enterResultHeader.getYear() + "_" + enterResultHeader.getTerm();
                    String class_subject_year_term = enterResultHeader.getClassID() + "_" + enterResultHeader.getSubject() + "_" + enterResultHeader.getYear() + "_" + enterResultHeader.getTerm();
                    bundle.putString("SubjectYearTerm", subject_year_term);
                    bundle.putString("ClassSubjectYearTerm", class_subject_year_term);
                    bundle.putString("ClassID", enterResultHeader.getClassID());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 4);
                }
            });

            ((HeaderViewHolder) holder).dateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyCustomBetterPickersDialogs);
                    cdp.show(((EnterResultsActivity)context).getSupportFragmentManager(), "Material Calendar Example");

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

            ((HeaderViewHolder) holder).termLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", enterResultHeader.getTerm());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 5);
                }
            });
        } else if(holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).saveToCloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof EnterResultsActivity) {
                        ((EnterResultsActivity)context).confirmSaveToCloud();
                    }
                }
            });
        } else {
            final EnterResultRow enterResultRow = enterResultRowList.get(position);

            ((MyViewHolder)holder).myCustomEditTextListener.updatePosition(((MyViewHolder)holder).getAdapterPosition());
            ((MyViewHolder)holder).studentName.setText(enterResultRow.getName());

            Drawable textDrawable;
            if (!enterResultRow.getName().isEmpty()) {
                String[] nameArray = enterResultRow.getName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 50);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 50);
                }
                ((MyViewHolder) holder).studentPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 50);
            }

            if (!enterResultRow.getImageURL().isEmpty()) {
                Glide.with(context)
                        .load(enterResultRow.getImageURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .crossFade()
                        .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).studentPic);
            }

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((MyViewHolder) holder).score.setEnabled(true);
                        ((MyViewHolder) holder).score.requestFocus();

                        //Show keyboard by default
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return enterResultRowList.size();
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == enterResultRowList.size () - 1;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).enableTextWatcher();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).disableTextWatcher();
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enterResultRowList.get(position).setScore(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
