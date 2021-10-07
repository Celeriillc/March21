package com.celerii.celerii.Activities.Timetable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsClassesModel;
import com.celerii.celerii.models.TeacherTimetableModel;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TeacherTimetableActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    Toolbar toolbar;

    ScrollView superLayout;
    RelativeLayout superRelativeLayout;
    RelativeLayout progressBar;
    FloatingActionButton addNewActivity;
    CustomProgressDialogOne customProgressDialogOne;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ArrayList<TeacherTimetableModel> teacherTimetableModelList;
    ArrayList<StudentsClassesModel> studentsClassesModelList;
    int heightOfHour = 50;
    int counter = 0;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Timetable Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadLayout();
    }

    void loadLayout() {
        setContentView(R.layout.activity_teacher_timetable);

        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Timetable");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        superLayout = (ScrollView) findViewById(R.id.superlayout);
        superRelativeLayout = (RelativeLayout) findViewById(R.id.superrelativelayout);
        progressBar = (RelativeLayout) findViewById(R.id.progresslayout);
        addNewActivity = (FloatingActionButton) findViewById(R.id.addnewactivity);
        customProgressDialogOne = new CustomProgressDialogOne(context);
        teacherTimetableModelList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        superLayout.setVisibility(View.GONE);
        addNewActivity.setElevation(0);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            addNewActivity.hide();
            loadFromFirebaseParent();
        } else {
            addNewActivity.show();
            loadFromFirebaseTeacher();
        }

        addNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherTimetableActivity.this, AddNewTimetableActivity.class);
                startActivity(intent);
            }
        });
    }

    void loadFromFirebaseTeacher() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            String messageString = "Your device is not connected to the internet. Check your connection and try again.";
//            showDialogWithMessage(messageString);
//            progressBar.setVisibility(View.GONE);
//            superLayout.setVisibility(View.VISIBLE);
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    showDialogWithMessage(getString(R.string.no_internet_message_for_offline_download));
                    progressBar.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Timetable").child(mFirebaseUser.getUid());
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        TeacherTimetableModel teacherTimetableModel = postSnapshot.getValue(TeacherTimetableModel.class);
                        loadView(getTimeControl(getHour(teacherTimetableModel.getTimeOfTheDay(), teacherTimetableModel.getZone())), heightOfHour, teacherTimetableModel.getDayOfTheWeek(),
                                getMinute(teacherTimetableModel.getTimeOfTheDay()), Integer.parseInt(teacherTimetableModel.getDuration()), teacherTimetableModel.getSubject(),
                                teacherTimetableModel.getClassName(), teacherTimetableModel);
                        teacherTimetableModelList.add(teacherTimetableModel);
                    }
                }

                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                progressBar.setVisibility(View.GONE);
                superLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadFromFirebaseParent() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            String messageString = "Your device is not connected to the internet. Check your connection and try again.";
//            showDialogWithMessage(messageString);
//            progressBar.setVisibility(View.GONE);
//            superLayout.setVisibility(View.VISIBLE);
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    showDialogWithMessage(getString(R.string.no_internet_message_for_offline_download));
                    progressBar.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        String activeKid = sharedPreferencesManager.getActiveKid();

        if (activeKid == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeKid = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeKid);
                } else {
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    progressBar.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                progressBar.setVisibility(View.GONE);
                superLayout.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            Boolean activeKidExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Student>() {}.getType();
            Student activeKidModel = gson.fromJson(activeKid, type);

            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            type = new TypeToken<ArrayList<Student>>() {}.getType();
            ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

            for (Student student: myChildren) {
                if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                    activeKidExist = true;
                    activeKidModel = student;
                    activeKid = gson.toJson(activeKidModel);
                    sharedPreferencesManager.setActiveKid(activeKid);
                    break;
                }
            }

            if (!activeKidExist) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeKid = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeKid);
                } else {
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    progressBar.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeKidModel = gson.fromJson(activeKid, type);
        String activeKidID = activeKidModel.getStudentID();

        counter = 0;
        gson = new Gson();
        studentsClassesModelList = new ArrayList<>();
        String studentsClassesJSON = sharedPreferencesManager.getStudentsClasses();
        type = new TypeToken<ArrayList<StudentsClassesModel>>() {}.getType();
        studentsClassesModelList = gson.fromJson(studentsClassesJSON, type);

        if (studentsClassesModelList == null) {
            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
            studentsClassesModelList = new ArrayList<>();
        } else {
            for (int i = 0; i < studentsClassesModelList.size(); i++) {
                final StudentsClassesModel studentsClassesModel = studentsClassesModelList.get(i);

                if (studentsClassesModel.getStudentID().equals(activeKidID)) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("Class Timetable").child(studentsClassesModel.getClassID());
                    mDatabaseReference.keepSynced(true);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            counter++;
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    TeacherTimetableModel teacherTimetableModel = postSnapshot.getValue(TeacherTimetableModel.class);
                                    loadView(getTimeControl(getHour(teacherTimetableModel.getTimeOfTheDay(), teacherTimetableModel.getZone())), heightOfHour, teacherTimetableModel.getDayOfTheWeek(),
                                            getMinute(teacherTimetableModel.getTimeOfTheDay()), Integer.valueOf(teacherTimetableModel.getDuration()), teacherTimetableModel.getSubject(),
                                            teacherTimetableModel.getClassName(), teacherTimetableModel);
                                    teacherTimetableModelList.add(teacherTimetableModel);
                                }
                            }

                            if (counter == studentsClassesModelList.size()) {
                                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                progressBar.setVisibility(View.GONE);
                                superLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    counter++;

                    if (counter == studentsClassesModelList.size()) {
                        internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                        progressBar.setVisibility(View.GONE);
                        superLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    void loadView(final int hour, int height_dp, final String day, final int minute, final int duration, final String subjectString, final String classNameString, final TeacherTimetableModel teacherTimetableModel) {
        Resources r = this.getResources();
        int unit_height_dp = (int) ((duration / 60.0) * height_dp);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height_dp, r.getDisplayMetrics());
        int unit_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, unit_height_dp, r.getDisplayMetrics());
        int toolbarHeight = 0;
        TypedValue tv = new TypedValue();
//        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//        }
        int minuteInducedPadding = (int) (((minute) / 60.0) * height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int paddingStart = (width / 6) * getDayControl(day);
        int paddingTop = minuteInducedPadding + toolbarHeight + (hour * height);

        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        if (randomNum == 0) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_primary_purple)); }
        else if (randomNum == 1) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_accent)); }
        else if (randomNum == 2) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_primary_purple)); }
//        else if (randomNum == 3) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_teal_green)); }
//        else if (randomNum == 4) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_kilogarm_yellow)); }
//        else if (randomNum == 5) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_kilogarm_orange)); }
//        else if (randomNum == 6) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_dark_gray)); }
//        else if (randomNum == 7) { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_green)); }
        else { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_accent)); }
//        else { parent.setBackground(ContextCompat.getDrawable(this, R.drawable.timetable_card_kilogarm_yellow)); }

        LinearLayout subParent = new LinearLayout(this);
        subParent.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams subParentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.setGravity(Gravity.CENTER);
        subParent.setGravity(Gravity.CENTER);

        TextView subject = new TextView(this);
        LinearLayout.LayoutParams subjectLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectLayoutParams.setMargins(10, 15, 10, 0);
        subject.setTextColor(ContextCompat.getColor(this, R.color.white));
        subject.setGravity(Gravity.START);
        subject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        subject.setEllipsize(TextUtils.TruncateAt.END);
        subject.setSingleLine(true);
        subject.setText(subjectString);
        subject.setLayoutParams(subjectLayoutParams);
//        if (randomNum == 0) { subject.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryPurple)); }
//        else if (randomNum == 1) { subject.setTextColor(ContextCompat.getColor(this, R.color.accent)); }
//        else { subject.setTextColor(ContextCompat.getColor(this, R.color.colorKilogarmOrange)); }
        subParent.addView(subject);

        TextView classTV = new TextView(this);
        LinearLayout.LayoutParams classLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectLayoutParams.setMargins(10, 0, 10, 5);
        classTV.setTextColor(ContextCompat.getColor(this, R.color.white));
        classTV.setGravity(Gravity.START);
        classTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        classTV.setEllipsize(TextUtils.TruncateAt.END);
        classTV.setSingleLine(true);
        classTV.setText(classNameString);
        classTV.setLayoutParams(subjectLayoutParams);
//        if (randomNum == 0) { classTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryPurple)); }
//        else if (randomNum == 1) { classTV.setTextColor(ContextCompat.getColor(this, R.color.accent)); }
//        else { classTV.setTextColor(ContextCompat.getColor(this, R.color.colorKilogarmOrange)); }
        subParent.addView(classTV);
        parent.addView(subParent);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((width / 6), unit_height);
        layoutParams.setMargins(paddingStart, paddingTop, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
        parent.setLayoutParams(layoutParams);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = "";
                int hour = getHour(teacherTimetableModel.getTimeOfTheDay(), teacherTimetableModel.getZone());
                String zone = teacherTimetableModel.getZone();
                if (hour < 13) {
                    time = String.valueOf(Date.makeTwoDigits(hour)) + ":" +  String.valueOf(Date.makeTwoDigits(minute)) + " " + "AM";
                } else {
                    time = String.valueOf(Date.makeTwoDigits(hour - 12)) + ":" +  String.valueOf(Date.makeTwoDigits(minute)) + " " + "PM";
                }
//                time = String.valueOf(Date.makeTwoDigits(hour)) + ":" +  String.valueOf(Date.makeTwoDigits(minute)) + " " + zone;

                String durationString = String.valueOf(duration) + " Minutes";

                viewDetails(classNameString, subjectString, day, time, durationString);
            }
        });

        parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteActivity(teacherTimetableModel);
                return false;
            }
        });

        superRelativeLayout.addView(parent);
    }

    int getHour (String time, String zone) {
        if (time == null) {return 0;}
        if (time.equals("")) {return 0;}

        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);
//        if (zone.equals("AM")) {
//            hour = hour;
//        } else {
//            hour += 12;
//        }
        return hour;
    }

    int getMinute (String time) {
        if (time == null) {return 0;}
        if (time.equals("")) {return 0;}

        String[] timeArray = time.split(":");
        return Integer.valueOf(timeArray[1]);
    }

    int getTimeControl(int hour) {

        switch (hour) {
            case 6:
                return 1;
            case 7:
                return 2;
            case 8:
                return 3;
            case 9:
                return 4;
            case 10:
                return 5;
            case 11:
                return 6;
            case 12:
                return 7;
            case 13:
                return 8;
            case 14:
                return 9;
            case 15:
                return 10;
            case 16:
                return 11;
            case 17:
                return 12;
            case 18:
                return 13;
            case 19:
                return 14;
            case 20:
                return 15;
            case 21:
                return 16;
            case 22:
                return 17;
            case 23:
                return 18;
            case 0:
                return 19;
            case 1:
                return 20;
            case 2:
                return 21;
            case 3:
                return 22;
            case 4:
                return 23;
            case 5:
                return 24;
            default:
                return 24;
        }
    }

    int getDayControl(String day) {
        switch (day) {
            case "Monday":
                return 1;
            case "Tuesday":
                return 2;
            case "Wednesday":
                return 3;
            case "Thursday":
                return 4;
            case "Friday":
                return 5;
            default:
                return 1;
        }
    }

    void viewDetails(String classNameString, String subjectString, String dayString, String timeString, String durationString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_timetable_detail_dialog);
        TextView className = (TextView) dialog.findViewById(R.id.classname);
        TextView subject = (TextView) dialog.findViewById(R.id.subject);
        TextView day = (TextView) dialog.findViewById(R.id.day);
        TextView time = (TextView) dialog.findViewById(R.id.time);
        TextView duration = (TextView) dialog.findViewById(R.id.duration);
        Button close = (Button) dialog.findViewById(R.id.close);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        className.setText(classNameString);
        subject.setText(subjectString);
        day.setText(dayString);
        time.setText(timeString);
        duration.setText(durationString);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    void deleteActivity(final TeacherTimetableModel teacherTimetableModel) {

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            return;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button delete = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText("Do you want to delete this timetable activity. This action can not be undone");

        delete.setText("Delete");
        cancel.setText("Cancel");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                dialog.dismiss();
                customProgressDialogOne.show();
                Map<String, Object> timeTableUpdateMap = new HashMap<String, Object>();
                timeTableUpdateMap.put("Teacher Timetable/" + mFirebaseUser.getUid() + "/" + teacherTimetableModel.getPushKey(), null);
                timeTableUpdateMap.put("Class Timetable/" + teacherTimetableModel.getClassID() + "/" + teacherTimetableModel.getPushKey(), null);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(timeTableUpdateMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            customProgressDialogOne.dismiss();
                            String message = "This timetable activity was successfully deleted";
                            showDialogWithMessageForDelete(message);
                        } else {
                            customProgressDialogOne.dismiss();
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

    void showDialogWithMessageForDelete (String messageString) {
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
                progressBar.setVisibility(View.VISIBLE);
                superLayout.setVisibility(View.GONE);
                loadLayout();
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference loginUpdateRef = FirebaseDatabase.getInstance().getReference();
        loginUpdateRef.updateChildren(featureUseUpdateMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
