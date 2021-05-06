package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryAssignmentStudentPerformanceModel;
import com.celerii.celerii.models.QuestionModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class ELibraryTakeTestActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    TextView errorLayoutButton;

    Toolbar toolbar;
    public LinearLayout optionABackground, optionBBackground, optionCBackground, optionDBackground;
    public TextView question, optionA, optionB, optionC, optionD, optionALabel, optionBLabel, optionCLabel, optionDLabel;

    Button previous, next, cancelTest;

    Bundle bundle;
    String materialTitle, assignmentID, activeStudent;
    private ArrayList<QuestionModel> questionModelList;
    int questionIndex = 0;

    String activeStudentID = "";
    String activeStudentName;

    String featureUseKey = "";
    String featureName = "E Library Parent Take Test";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_take_test);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assignment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) findViewById(R.id.errorlayoutbutton);

        bundle = getIntent().getExtras();
        materialTitle = bundle.getString("materialTitle");
        assignmentID = bundle.getString("assignmentID");
        activeStudent = bundle.getString("activeStudent");

        if (activeStudent == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeStudent = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeStudent);
                } else {
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                    } else {
                        errorLayoutText.setText("You do not have the permission to view this student's academic record");
                    }

                    return;
                }
            } else {
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                } else {
                    errorLayoutText.setText("You do not have the permission to view this student's academic record");
                }

                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {}.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student: myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                            errorLayoutButton.setText("Find my child");
                            errorLayoutButton.setVisibility(View.VISIBLE);
                        } else {
                            errorLayoutText.setText("You do not have the permission to view this student's academic record");
                        }

                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();
        questionModelList = new ArrayList<>();

        optionABackground = (LinearLayout) findViewById(R.id.optionabackground);
        optionBBackground = (LinearLayout) findViewById(R.id.optionbbackground);
        optionCBackground = (LinearLayout) findViewById(R.id.optioncbackground);
        optionDBackground = (LinearLayout) findViewById(R.id.optiondbackground);
        question = (TextView) findViewById(R.id.question);
        optionA = (TextView) findViewById(R.id.optiona);
        optionB = (TextView) findViewById(R.id.optionb);
        optionC = (TextView) findViewById(R.id.optionc);
        optionD = (TextView) findViewById(R.id.optiond);
        optionALabel = (TextView) findViewById(R.id.optionalabel);
        optionBLabel = (TextView) findViewById(R.id.optionblabel);
        optionCLabel = (TextView) findViewById(R.id.optionclabel);
        optionDLabel = (TextView) findViewById(R.id.optiondlabel);
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);
        cancelTest = (Button) findViewById(R.id.canceltest);

        superLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        loadQuestionsFromFirebase();

        optionABackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionModelList.get(questionIndex).setSelectedAnswer(questionModelList.get(questionIndex).getOptionA());
                resetLayout();
                optionABackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                optionA.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                optionALabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }
        });

        optionBBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionModelList.get(questionIndex).setSelectedAnswer(questionModelList.get(questionIndex).getOptionB());
                resetLayout();
                optionBBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                optionB.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }
        });

        optionCBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionModelList.get(questionIndex).setSelectedAnswer(questionModelList.get(questionIndex).getOptionC());
                resetLayout();
                optionCBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                optionC.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }
        });

        optionDBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionModelList.get(questionIndex).setSelectedAnswer(questionModelList.get(questionIndex).getOptionD());
                resetLayout();
                optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                optionD.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionIndex > 0) {
                    questionIndex--;
                    resetLayout();
                    highlightChosenAnswer(questionIndex);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionIndex < questionModelList.size() - 2) {
                    questionIndex++;
                    resetLayout();
                    highlightChosenAnswer(questionIndex);
                } else if (questionIndex < questionModelList.size() - 1) {
                    next.setText("Finish");
                    questionIndex++;
                    resetLayout();
                    highlightChosenAnswer(questionIndex);
                } else if (questionIndex == questionModelList.size() - 1) {
                    confirmSubmitTest();
                }
            }
        });

        cancelTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTest();
            }
        });
    }

    private void loadQuestionsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Questions").child("Student").child(activeStudentID).child(assignmentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        QuestionModel questionModel = postSnapshot.getValue(QuestionModel.class);
                        questionModel.setQuestionID(postSnapshot.getKey());
                        questionModelList.add(questionModel);
                    }

                    questionIndex = 0;
                    resetLayout();
                    highlightChosenAnswer(questionIndex);

                    superLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This assignment doesn't have any questions or has been deleted.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment").child("Student").child(activeStudentID).child(assignmentID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    ELibraryMyAssignmentModel eLibraryMyAssignmentModel = dataSnapshot.getValue(ELibraryMyAssignmentModel.class);
//                    if (eLibraryMyAssignmentModel.getSubmitted()) {
//                        String message = "You have taken this assignment's test already.";
//                        ShowDialogWithMessage.showDialogWithMessageAndClose(context, message);
//                    } else {
//                    }
//                } else {
//                    superLayout.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                    errorLayoutText.setText("This assignment doesn't have any questions or has been deleted.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void resetLayout() {
        optionABackground.setBackgroundResource(0);
        optionBBackground.setBackgroundResource(0);
        optionCBackground.setBackgroundResource(0);
        optionDBackground.setBackgroundResource(0);

        optionA.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionALabel.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionB.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionC.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionD.setTextColor(ContextCompat.getColor(context, R.color.black));
        optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
    }

    private void highlightChosenAnswer(int questionIndex) {
        question.setText(questionModelList.get(questionIndex).getQuestion());
        optionA.setText(questionModelList.get(questionIndex).getOptionA());
        optionB.setText(questionModelList.get(questionIndex).getOptionB());
        optionC.setText(questionModelList.get(questionIndex).getOptionC());
        optionD.setText(questionModelList.get(questionIndex).getOptionD());

        if (questionModelList.get(questionIndex).getSelectedAnswer().equals(questionModelList.get(questionIndex).getOptionA())) {
            optionABackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
            optionA.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            optionALabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        } else if (questionModelList.get(questionIndex).getSelectedAnswer().equals(questionModelList.get(questionIndex).getOptionB())) {
            optionBBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
            optionB.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            optionBLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        } else if (questionModelList.get(questionIndex).getSelectedAnswer().equals(questionModelList.get(questionIndex).getOptionC())) {
            optionCBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
            optionC.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            optionCLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        } else if (questionModelList.get(questionIndex).getSelectedAnswer().equals(questionModelList.get(questionIndex).getOptionD())) {
            optionDBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
            optionD.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
            optionDLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        }
    }

    private void cancelTest() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button endTest = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText(Html.fromHtml("You're about to end this assignment before completing it. Any progress made will be lost, do you wish to end the assignment?"));

        endTest.setText("End Assignment");
        cancel.setText("Cancel");

        endTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void confirmSubmitTest() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        final Button submit = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText(Html.fromHtml("You're about to submit this assignment. Please crosscheck your answers before clicking " + "<b>" + "Submit" + "</b>" + "."));

        submit.setText("Submit");
        cancel.setText("Cancel");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitTest();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void submitTest() {
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
        progressDialog.show();

        HashMap<String, Object> takeTestUpdateMap = new HashMap<>();

        int totalQuestions = questionModelList.size();
        int correctAnswers = 0;

        for (QuestionModel questionModel: questionModelList) {
            takeTestUpdateMap.put("E Library Assignment Questions/Student/" + activeStudentID + "/" +
                    assignmentID + "/" + questionModel.getQuestionID() + "/selectedAnswer", questionModel.getSelectedAnswer());

            if (questionModel.getSelectedAnswer().equals(questionModel.getAnswer())) {
                correctAnswers++;
            }
        }
        final ELibraryAssignmentStudentPerformanceModel eLibraryAssignmentStudentPerformanceModel = new ELibraryAssignmentStudentPerformanceModel(activeStudentID, String.valueOf(totalQuestions), String.valueOf(correctAnswers));
        takeTestUpdateMap.put("E Library Assignment Student Performance/" + assignmentID + "/" + activeStudentID, eLibraryAssignmentStudentPerformanceModel);
        takeTestUpdateMap.put("E Library Assignment/Student/" + activeStudentID + "/" + assignmentID + "/submitted", true);

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(takeTestUpdateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(ELibraryTakeTestActivity.this, ELibraryFinishTestActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialTitle", materialTitle);
                    bundle.putString("totalQuestions", eLibraryAssignmentStudentPerformanceModel.getTotalQuestions());
                    bundle.putString("correctAnswers", eLibraryAssignmentStudentPerformanceModel.getCorrectAnswers());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                    showDialogWithMessage(message);
                }
            }
        });
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void showDialogWithMessageAndClose (String messageString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            cancelTest();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelTest();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }
}