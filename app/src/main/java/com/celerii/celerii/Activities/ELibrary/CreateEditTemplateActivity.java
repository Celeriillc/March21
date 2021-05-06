package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.CreateEditTemplateAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.CreateEditTemplateHeaderModel;
import com.celerii.celerii.models.ELibraryMyTemplateModel;
import com.celerii.celerii.models.QuestionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateEditTemplateActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<QuestionModel> questionModelList;
    private CreateEditTemplateHeaderModel createEditTemplateHeaderModel;
    private ELibraryMyTemplateModel eLibraryMyTemplateModel;
    public RecyclerView recyclerView;
    public CreateEditTemplateAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Bundle bundle;
    String navType = "", templateID = "";

    String featureUseKey = "";
    String featureName = "E Library Create Edit Template";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_template);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        navType = bundle.getString("Nav Type");
        if (navType != null) {
            if (navType.equals("Edit")) {
                templateID = bundle.getString("templateID");
            }
        }

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(navType + " Template");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        questionModelList = new ArrayList<>();
        createEditTemplateHeaderModel = new CreateEditTemplateHeaderModel();
        eLibraryMyTemplateModel = new ELibraryMyTemplateModel();
        mAdapter = new CreateEditTemplateAdapter(questionModelList, createEditTemplateHeaderModel, this,this);
        recyclerView.setAdapter(mAdapter);

        if (navType.equals("Create")) {
            questionModelList.add(new QuestionModel());
            questionModelList.add(new QuestionModel());
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            loadFromFirebase();
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (navType.equals("Edit")) {
                            recyclerView.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.VISIBLE);
                            loadFromFirebase();
                        }
                    }
                }
        );
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        questionModelList.clear();
        mAdapter.notifyDataSetChanged();
        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Template").child(mFirebaseUser.getUid()).child(templateID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eLibraryMyTemplateModel = dataSnapshot.getValue(ELibraryMyTemplateModel.class);
                    createEditTemplateHeaderModel.setTemplateTitle(eLibraryMyTemplateModel.getTemplateTitle());
                    mAdapter.notifyDataSetChanged();
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Template Questions").child(mFirebaseUser.getUid()).child(templateID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                QuestionModel questionModel = postSnapshot.getValue(QuestionModel.class);
                                questionModelList.add(questionModel);
                            }
                        }

                        questionModelList.add(0, new QuestionModel());
                        questionModelList.add(new QuestionModel());
                        mAdapter.notifyDataSetChanged();
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void saveToCloud() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            showDialogWithMessage("Internet is down, check your connection and try again");
            return;
        }

        if (questionModelList.size() <= 2) {
            showDialogWithMessage("Templates cannot be saved to cloud because it doesn't contain any questions.");
            return;
        }

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(CreateEditTemplateActivity.this);
        progressDialog.show();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        String numberOfUses = "0";
        String key;
        Map<String, Object> newTemplate = new HashMap<String, Object>();
        final String templateTitle = createEditTemplateHeaderModel.getTemplateTitle();

        if (eLibraryMyTemplateModel.getTemplateID().equals("")) {
            key = mFirebaseDatabase.getReference().child("E Library Assignment Template").child(mFirebaseUser.getUid()).push().getKey();
            eLibraryMyTemplateModel = new ELibraryMyTemplateModel(key, templateTitle, numberOfUses, date, sortableDate);
        } else {
            key = eLibraryMyTemplateModel.getTemplateID();
            eLibraryMyTemplateModel.setTemplateTitle(templateTitle);
        }

        newTemplate.put("E Library Assignment Template/" + mFirebaseUser.getUid() + "/" + key, eLibraryMyTemplateModel);
        for (QuestionModel questionModel: questionModelList) {
            if (!questionModel.getQuestion().equals("")) {
                String questionKey = mFirebaseDatabase.getReference().child("E Library Assignment Template").child(mFirebaseUser.getUid()).child(key).push().getKey();
                newTemplate.put("E Library Assignment Template Questions/" + mFirebaseUser.getUid() + "/" + key + "/" + questionKey, questionModel);
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(newTemplate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    ShowDialogWithMessage.showDialogWithMessageAndClose(context, Html.fromHtml("<b>" + templateTitle + "</b>" +  " has been successfully created."));
                } else {
                    progressDialog.dismiss();
                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                    showDialogWithMessage(message);
                }
            }
        });
    }

//    private boolean validateOptions(EditText editText, String string, String optionType) {
//        if (string.isEmpty()) {
//            String messageString = "<b>" + optionType + "</b>" + " is empty, you need to enter " + "<b>" + optionType + "</b>" + " for this question to proceed";
//            showDialogWithMessage(messageString);
//            tit.requestFocus();
//            return false;
//        }
//        return true;
//    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String question = data.getStringExtra("Question");
                String answer = data.getStringExtra("Answer");
                String optionA = data.getStringExtra("OptionA");
                String optionB = data.getStringExtra("OptionB");
                String optionC = data.getStringExtra("OptionC");
                String optionD = data.getStringExtra("OptionD");
                QuestionModel questionModel = new QuestionModel(question, answer, optionA, optionB, optionC, optionD, Date.getDate());
                questionModelList.add(questionModelList.size() - 1, questionModel);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}