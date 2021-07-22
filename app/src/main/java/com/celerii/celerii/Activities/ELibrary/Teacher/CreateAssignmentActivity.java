package com.celerii.celerii.Activities.ELibrary.Teacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.CreateAssignmentAdapter;
import com.celerii.celerii.adapters.CreateEditTemplateAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.ELibraryMyTemplateModel;
import com.celerii.celerii.models.ManageKidsModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.QuestionModel;
import com.celerii.celerii.models.School;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CreateAssignmentActivity extends AppCompatActivity {

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
    private ELibraryMyAssignmentModel eLibraryMyAssignmentModel;
    public RecyclerView recyclerView;
    public CreateAssignmentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Bundle bundle;
    String materialTitle = "", materialType = "", materialID = "", materialDescription = "";
    String year, month, day, hour, minute;

    HashMap<String, ArrayList<String>> classSchoolMap;
    HashMap<String, ArrayList<String>> studentParentList;
    ArrayList<Student> studentList;
    ArrayList<String> schoolList;

    String activeClassID = "";
    String activeClassName = "";
    String activeClass;
    Class activeClassModel;

    String featureUseKey = "";
    String featureName = "E Library Create Assignment";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        materialID = bundle.getString("id");
        materialTitle = bundle.getString("title");
        materialType = bundle.getString("type");
        materialDescription = bundle.getString("description");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Assignment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        activeClass = sharedPreferencesManager.getActiveClass();

        if (activeClass == null) {
            Gson gson = new Gson();
            ArrayList<Class> myClasses = new ArrayList<>();
            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            Type type = new TypeToken<ArrayList<Class>>() {}.getType();
            myClasses = gson.fromJson(myClassesJSON, type);

            if (myClasses != null) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                    gson = new Gson();
                    type = new TypeToken<Class>() {}.getType();
                    activeClassModel = gson.fromJson(activeClass, type);
                    activeClassID = activeClassModel.getID();
                    activeClassName = activeClassModel.getClassName();
                } else {
                    showDialogWithMessageAndClose("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            } else {
                showDialogWithMessageAndClose("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                return;
            }
        } else {
            Boolean activeClassExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Class>() {}.getType();
            activeClassModel = gson.fromJson(activeClass, type);

            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);

            for (Class classInstance: myClasses) {
                if (activeClassModel.getID().equals(classInstance.getID())) {
                    activeClassExist = true;
                    activeClassModel = classInstance;
                    activeClass = gson.toJson(activeClassModel);
                    sharedPreferencesManager.setActiveClass(activeClass);
                    break;
                }
            }

            if (!activeClassExist) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                } else {
                    showDialogWithMessageAndClose("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            }

            type = new TypeToken<Class>() {}.getType();
            activeClassModel = gson.fromJson(activeClass, type);
            activeClassID = activeClassModel.getID();
            activeClassName = activeClassModel.getClassName();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        questionModelList = new ArrayList<>();
        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        year = Date.getYear();
        month = Date.getMonth();
        day = Date.getDay();
        hour = Date.getHour();
        minute = Date.getMinute();

        questionModelList.add(new QuestionModel());
        questionModelList.add(new QuestionModel());
        eLibraryMyAssignmentModel = new ELibraryMyAssignmentModel(mFirebaseUser.getUid(), activeClassID, activeClassName, date, sortableDate, materialTitle, materialType, "", materialDescription, materialID);
        mAdapter = new CreateAssignmentAdapter(questionModelList, eLibraryMyAssignmentModel, this, context);
        recyclerView.setAdapter(mAdapter);

        classSchoolMap = new HashMap<>();
        studentParentList = new HashMap<>();
        studentList = new ArrayList<>();
        schoolList = new ArrayList<>();

        recyclerView.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);

        loadSchool();
        loadParents();
        loadStudents();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverDate,
                new IntentFilter("Date Information"));

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverTime,
//                new IntentFilter("Time Information"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveToCloud() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            showDialogWithMessage("Internet is down, check your connection and try again");
            return;
        }

        if (questionModelList.size() <= 2) {
            showDialogWithMessage("Assignments cannot be created because it doesn't contain any questions.");
            return;
        }

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
        progressDialog.show();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);

        HashMap<String, Object> newAssignmentMap = new HashMap<>();
        String key = mFirebaseDatabase.getReference().child("E Library Assignment").child("Teacher").child(mFirebaseUser.getUid()).push().getKey();
        eLibraryMyAssignmentModel.setAssignmentID(key);
        eLibraryMyAssignmentModel.setDateGiven(date);
        eLibraryMyAssignmentModel.setSortableDateGiven(sortableDate);
        if (schoolList.size() != 0) {
            eLibraryMyAssignmentModel.setSchoolID(schoolList.get(0));
        }

        newAssignmentMap.put("E Library Assignment/Teacher/" + mFirebaseUser.getUid() + "/" + key, eLibraryMyAssignmentModel);
        newAssignmentMap.put("E Library Assignment/Class/" + activeClassID + "/" + key, eLibraryMyAssignmentModel);

        for (String school: schoolList) {
            newAssignmentMap.put("E Library Assignment/School/" + school + "/" + key, eLibraryMyAssignmentModel);
        }

        for (int i = 0; i < studentList.size(); i++) {
            String studentID = studentList.get(i).getStudentID();
            String studentName = studentList.get(i).getFirstName() + " " + studentList.get(i).getLastName();
            String studentProfilePictureURL = studentList.get(i).getImageURL();

            newAssignmentMap.put("E Library Assignment/Student/" + studentID + "/" + key, eLibraryMyAssignmentModel);

            ArrayList<String> parentIDList = studentParentList.get(studentID);
            if (parentIDList != null) {
                for (int j = 0; j < parentIDList.size(); j++) {
                    String parentID = parentIDList.get(j);
                    if (!parentID.isEmpty()) {
                        NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), parentID, "Parent", "Teacher", date,
                                sortableDate, key, "ELibraryAssignment", studentProfilePictureURL, studentID, studentName, false);
                        newAssignmentMap.put("NotificationParent/" + parentID + "/" + key, notificationModel);
                        newAssignmentMap.put("Notification Badges/Parents/" + parentID + "/Notifications/status", true);
                        newAssignmentMap.put("Notification Badges/Parents/" + parentID + "/More/status", true);
                        newAssignmentMap.put("ELibraryAssignmentParentNotification/" + parentID + "/" + studentID + "/status", true);
                        newAssignmentMap.put("ELibraryAssignmentParentNotification/" + parentID + "/" + studentID + "/" + key + "/status", true);
                        newAssignmentMap.put("ELibraryAssignmentParentRecipients/" + key + "/" + parentID, true);
                    }
                }
            }
        }

        for (QuestionModel questionModel: questionModelList) {
            if (!questionModel.getQuestion().equals("")) {
                String questionKey = mFirebaseDatabase.getReference().child("E Library Assignment Questions").child("Teacher").child(mFirebaseUser.getUid()).child(key).push().getKey();
                newAssignmentMap.put("E Library Assignment Questions/Teacher/" + mFirebaseUser.getUid() + "/" + key + "/" + questionKey, questionModel);
                newAssignmentMap.put("E Library Assignment Questions/Class/" + activeClassID + "/" + key + "/" + questionKey, questionModel);

                for (String school : schoolList) {
                    newAssignmentMap.put("E Library Assignment Questions/School/" + school + "/" + key + "/" + questionKey, questionModel);
                }

                for (int i = 0; i < studentList.size(); i++) {
                    String studentID = studentList.get(i).getStudentID();
                    newAssignmentMap.put("E Library Assignment Questions/Student/" + studentID + "/" + key + "/" + questionKey, questionModel);
                }
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(newAssignmentMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    ShowDialogWithMessage.showDialogWithMessageAndClose(context, Html.fromHtml("Your assignment has been successfully created."));
                } else {
                    progressDialog.dismiss();
                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                    showDialogWithMessage(message);
                }
            }
        });
    }

    void loadSchool() {
        Gson gson = new Gson();
        String classStudentParentJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = gson.fromJson(classStudentParentJSON, type);

        if (classesStudentsAndParentsModelList == null) {
            classesStudentsAndParentsModelList = new ArrayList<>();
        } else {
            for (int i = 0; i < classesStudentsAndParentsModelList.size(); i++) {

                if (classSchoolMap.containsKey(classesStudentsAndParentsModelList.get(i).getClassID())) {
                    if (!classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).contains(classesStudentsAndParentsModelList.get(i).getSchoolID())) {
                        classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getSchoolID());
                    }
                } else {
                    classSchoolMap.put(classesStudentsAndParentsModelList.get(i).getClassID(), new ArrayList<String>());
                    classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getSchoolID());
                }
            }

            if (classSchoolMap.containsKey(activeClassID)) {
                ArrayList<String> schools = classSchoolMap.get(activeClassID);
                if (schools != null) {
                    for (String school : schools) {
                        if (!schoolList.contains(school)) {
                            schoolList.add(school);
                        }
                    }
                }
            }
        }
    }

    void loadParents () {
        Gson gson = new Gson();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
        String myClassesStudentsParentsJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsAndParentsModelList = gson.fromJson(myClassesStudentsParentsJSON, type);

        if (classesStudentsAndParentsModelList == null) {

        } else {
            for (ClassesStudentsAndParentsModel classesStudentsAndParentsModel: classesStudentsAndParentsModelList) {
                String studentID = classesStudentsAndParentsModel.getStudentID();
                String parentID = classesStudentsAndParentsModel.getParentID();

                if (!parentID.isEmpty()) {
                    try {
                        if (!studentParentList.get(studentID).contains(parentID)) {
                            studentParentList.get(studentID).add(parentID);
                        }
                    } catch (Exception e) {
                        studentParentList.put(studentID, new ArrayList<String>());
                        studentParentList.get(studentID).add(parentID);
                    }
                }
            }
        }
    }

    void loadStudents() {
        Gson gson = new Gson();
        HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = new HashMap<String, HashMap<String, Student>>();
        String classStudentsForTeacherJSON = sharedPreferencesManager.getClassStudentForTeacher();
        Type type = new TypeToken<HashMap<String, HashMap<String, Student>>>() {}.getType();
        classStudentsForTeacherMap = gson.fromJson(classStudentsForTeacherJSON, type);

        if (classStudentsForTeacherMap == null) {
            studentList = new ArrayList<>();
        } else if (classStudentsForTeacherMap.size() == 0) {
            studentList = new ArrayList<>();
        } else {
            studentList.clear();
            HashMap<String, Student> classMap = classStudentsForTeacherMap.get(activeClassID);
            if (classMap != null) {
                for (Map.Entry<String, Student> entry : classMap.entrySet()) {
                    String studentID = entry.getKey();
                    Student studentModel = entry.getValue();
                    studentModel.setStudentID(studentID);
                    studentList.add(studentModel);
                }
            }
        }
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
            if (resultCode == RESULT_OK) {
                String activeClass = data.getStringExtra("Selected Class");
                Gson gson = new Gson();
                Type type = new TypeToken<Class>() {}.getType();
                activeClassModel = gson.fromJson(activeClass, type);
                activeClassID = activeClassModel.getID();
                activeClassName = activeClassModel.getClassName();
                eLibraryMyAssignmentModel.setClassID(activeClassID);
                eLibraryMyAssignmentModel.setClassName(activeClassName);
                loadStudents();
                mAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
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
        } else if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String selectedTemplate = data.getStringExtra("Selected Template");
                Gson gson = new Gson();
                Type type = new TypeToken<ELibraryMyTemplateModel>() {}.getType();
                ELibraryMyTemplateModel template = gson.fromJson(selectedTemplate, type);

                final CustomProgressDialogOne progressDialogOne = new CustomProgressDialogOne(context);
                progressDialogOne.show();

                mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Template Questions").child(mFirebaseUser.getUid()).child(template.getTemplateID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                QuestionModel questionModel = postSnapshot.getValue(QuestionModel.class);
                                questionModelList.add(questionModelList.size() - 1, questionModel);
                            }

                            mAdapter.notifyDataSetChanged();
                        } else {
                            showDialogWithMessage("The selected template doesn't contain any questions");
                        }
                        progressDialogOne.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    public BroadcastReceiver mMessageReceiverDate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getStringExtra("Year");
            month = String.valueOf(intent.getStringExtra("Month"));
            day = String.valueOf(intent.getStringExtra("Day"));

//            String date = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":00:000";
            String date = year + "/" + month + "/" + day + " 00:00:00:000";
            String sortableDate = Date.convertToSortableDate(date);

            eLibraryMyAssignmentModel.setDueDate(date);
            eLibraryMyAssignmentModel.setSortableDateDue(sortableDate);
            mAdapter.notifyDataSetChanged();
        }
    };

//    public BroadcastReceiver mMessageReceiverTime = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            hour = intent.getStringExtra("Hour");
//            minute = String.valueOf(intent.getStringExtra("Minute"));
//
//            String date = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":00:000";
//            String sortableDate = Date.convertToSortableDate(date);
//
//            eLibraryMyAssignmentModel.setDueDate(date);
//            eLibraryMyAssignmentModel.setSortableDateDue(sortableDate);
//            mAdapter.notifyDataSetChanged();
//        }
//    };
}