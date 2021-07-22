package com.celerii.celerii.Activities.ELibrary.Teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateQuestionActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    LinearLayout optionALayout, optionBLayout, optionCLayout, optionDLayout;
    EditText question, optionA, optionB, optionC, optionD;
    ImageView optionAImage, optionBImage, optionCImage, optionDImage;
    Button createQuestion;

    String correctOption, answer;

    String featureUseKey = "";
    String featureName = "E Library Create Question";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        optionALayout = (LinearLayout) findViewById(R.id.optionalayout);
        optionBLayout = (LinearLayout) findViewById(R.id.optionblayout);
        optionCLayout = (LinearLayout) findViewById(R.id.optionclayout);
        optionDLayout = (LinearLayout) findViewById(R.id.optiondlayout);
        question = (EditText) findViewById(R.id.question);
        optionA = (EditText) findViewById(R.id.optiona);
        optionB = (EditText) findViewById(R.id.optionb);
        optionC = (EditText) findViewById(R.id.optionc);
        optionD = (EditText) findViewById(R.id.optiond);
        optionAImage = (ImageView) findViewById(R.id.optionaimage);
        optionBImage = (ImageView) findViewById(R.id.optionbimage);
        optionCImage = (ImageView) findViewById(R.id.optioncimage);
        optionDImage = (ImageView) findViewById(R.id.optiondimage);
        createQuestion = (Button) findViewById(R.id.createquestion);

        correctOption = "A";
        optionBImage.setVisibility(View.INVISIBLE);
        optionCImage.setVisibility(View.INVISIBLE);
        optionDImage.setVisibility(View.INVISIBLE);

        optionALayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOptionImages();
                optionAImage.setVisibility(View.VISIBLE);
                correctOption = "A";
            }
        });

        optionBLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOptionImages();
                optionBImage.setVisibility(View.VISIBLE);
                correctOption = "B";
            }
        });

        optionCLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOptionImages();
                optionCImage.setVisibility(View.VISIBLE);
                correctOption = "C";
            }
        });

        optionDLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeOptionImages();
                optionDImage.setVisibility(View.VISIBLE);
                correctOption = "D";
            }
        });

        createQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionString = question.getText().toString().trim();
                String optionAString = optionA.getText().toString().trim();
                String optionBString = optionB.getText().toString().trim();
                String optionCString = optionC.getText().toString().trim();
                String optionDString = optionD.getText().toString().trim();

                if (!validateQuestion(questionString))
                    return;

                if (!validateOptions(optionA, optionAString, "Option A"))
                    return;

                if (!validateOptions(optionB, optionBString, "Option B"))
                    return;

                if (!validateOptions(optionC, optionCString, "Option C"))
                    return;

                if (!validateOptions(optionD, optionDString, "Option D"))
                    return;

                if (correctOption.equals("A"))
                    answer = optionAString;
                else if (correctOption.equals("B"))
                    answer = optionBString;
                else if (correctOption.equals("C"))
                    answer = optionCString;
                else
                    answer = optionDString;

                Intent intent = new Intent();
                intent.putExtra("Question", questionString);
                intent.putExtra("Answer", answer);
                intent.putExtra("OptionA", optionAString);
                intent.putExtra("OptionB", optionBString);
                intent.putExtra("OptionC", optionCString);
                intent.putExtra("OptionD", optionDString);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeOptionImages() {
        optionAImage.setVisibility(View.INVISIBLE);
        optionBImage.setVisibility(View.INVISIBLE);
        optionCImage.setVisibility(View.INVISIBLE);
        optionDImage.setVisibility(View.INVISIBLE);
    }

    private boolean validateOptions(EditText editText, String string, String optionType) {
        if (string.isEmpty()) {
            String messageString = "<b>" + optionType + "</b>" + " is empty, you need to enter " + "<b>" + optionType + "</b>" + " for this question to proceed";
            showDialogWithMessage(Html.fromHtml(messageString));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateQuestion(String string) {
        if (string.isEmpty()) {
            String messageString = "The " + "<b>" + "Question" + "</b>" + " field is empty";
            showDialogWithMessage(Html.fromHtml(messageString));
            question.requestFocus();
            return false;
        }
        return true;
    }

    void showDialogWithMessage (Spanned messageString) {
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
}