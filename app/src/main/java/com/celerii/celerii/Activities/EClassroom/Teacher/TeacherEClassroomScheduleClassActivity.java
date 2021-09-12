package com.celerii.celerii.Activities.EClassroom.Teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditClassActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditDayActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherAttendanceActivity;
import com.celerii.celerii.Activities.Timetable.AddNewTimetableActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.TeacherTimetableModel;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder;
import com.codetroopers.betterpickers.timepicker.TimePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeacherEClassroomScheduleClassActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, TimePickerDialogFragment.TimePickerDialogHandler {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    CustomProgressDialogOne customProgressDialogOne;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private EditText className, subject, day, time, duration, description;
    Button save;

    ArrayList<String> subjectList = new ArrayList<>();
    HashMap<String, ArrayList<String>> classSchoolMap;
    HashMap<String, ArrayList<String>> studentParentList;
    ArrayList<Student> studentList;
    ArrayList<String> schoolList;

    CalendarDatePickerDialogFragment cdp;

    String activeClassID = "";
    String activeClassName = "";
    String activeClass;
    Class activeClassModel;
    String year, month, dayOfTheMonth;
    String hourOfTheDay;
    String minute;
    String zone;

    String featureUseKey = "";
    String featureName = "E Classroom Schedule Class";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_eclassroom_schedule_class);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        customProgressDialogOne = new CustomProgressDialogOne(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Schedule a Class");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        className = (EditText) findViewById(R.id.classname);
        subject = (EditText) findViewById(R.id.subject);
        day = (EditText) findViewById(R.id.dayoftheweek);
        time = (EditText) findViewById(R.id.timeoftheday);
//        duration = (EditText) findViewById(R.id.duration);
        description = (EditText) findViewById(R.id.description);
        save = (Button) findViewById(R.id.save);

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
                    className.setText(activeClassName);
                } else {
                    showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            } else {
                showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
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
                    showDialogWithMessageAndDisconnect("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }
            }

            type = new TypeToken<Class>() {}.getType();
            activeClassModel = gson.fromJson(activeClass, type);
            activeClassID = activeClassModel.getID();
            activeClassName = activeClassModel.getClassName();
            className.setText(activeClassName);
        }

        Gson gson = new Gson();
        subjectList = new ArrayList<>();
        String subjectJSON = sharedPreferencesManager.getSubjects();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        subjectList = gson.fromJson(subjectJSON, type);

        classSchoolMap = new HashMap<>();
        studentParentList = new HashMap<>();
        studentList = new ArrayList<>();
        schoolList = new ArrayList<>();

        if (subjectList == null) {
            subjectList = new ArrayList<>();
            showDialogWithMessageAndDisconnect("There are no subjects to create classes for. If you're not connected to a school, use the search feature to search for a school and send a request.");
            return;
        } else {
            subject.setText(subjectList.get(0));
        }

        cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyCustomBetterPickersDialogs);
        String date = Date.DateFormatMMDDYYYY(Date.getDate());
        year = Date.getYear();
        month = Date.getMonth();
        dayOfTheMonth = Date.getDay();
        day.setText(date);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hourOfTheDay = String.valueOf(Date.makeTwoDigits(calendar.get(Calendar.HOUR_OF_DAY)));
        minute = String.valueOf(Date.makeTwoDigits(calendar.get(Calendar.MINUTE)));
        if (hour < 13) {
            time.setText((hourOfTheDay) + ":" + (minute) + " AM");
            zone = "AM";
        } else {
            time.setText((hour - 12) + ":" + (minute) + " PM");
            zone = "PM";
        }

//        duration.setText("45 Minutes");

        loadSchool();
        loadParents();
        loadStudents();

        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int newYear, int monthOfYear, int dayOfMonth) {
                year = String.valueOf(year);
                month = String.valueOf(monthOfYear + 1);
                dayOfTheMonth = String.valueOf(dayOfMonth);
                String date = year + "/" + month + "/" + dayOfTheMonth + " 00:00:00:000";
                day.setText(Date.DateFormatMMDDYYYY(date));
            }
        });

        className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherEClassroomScheduleClassActivity.this, EditClassActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Class", activeClass);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherEClassroomScheduleClassActivity.this, EnterResultsEditSubjectsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Activity", "AddNewTimetable");
                bundle.putString("Subject", subject.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdp.show(((TeacherEClassroomScheduleClassActivity)context).getSupportFragmentManager(), "Material Calendar Example");
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerBuilder tpb = new TimePickerBuilder().setFragmentManager(getSupportFragmentManager()).setStyleResId(R.style.BetterPickersDialogFragment);
                tpb.show();
            }
        });

//        duration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    if (!duration.getText().toString().endsWith(" Minutes")) {
//                        String durationString = duration.getText().toString() + " Minutes";
//                        duration.setText(durationString);
//                    }
//                } else {
//                    duration.setText(duration.getText().toString().replace(" Minutes", ""));
//                }
//            }
//        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    showDialogWithMessage("Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class").child(activeClassID).push();
                String pushKey = mDatabaseReference.getKey();

                String dateCreated = Date.getDate();
                String sortableDateCreated = Date.convertToSortableDate(dateCreated);
                String dateScheduled = year + "/" + month + "/" + dayOfTheMonth + " " + hourOfTheDay + ":" + minute + ":00:000";
                String sortableDateScheduled = Date.convertToSortableDate(dateScheduled);
//                String classLink = activeClassID + subject.getText().toString() + mFirebaseUser.getUid() + sortableDateCreated;
                String classLink = pushKey;
                String schoolID = "";
                if (schoolList.size() > 0) {
                    schoolID = schoolList.get(0);
                }

                if (!Date.compareDates(dateScheduled, dateCreated)) {
                    showDialogWithMessage("The scheduled date and time needs to be set to a future date.");
                    return;
                }

                customProgressDialogOne.show();

                EClassroomScheduledClassesListModel eClassroomScheduledClassesListModel = new EClassroomScheduledClassesListModel(pushKey, activeClassID,
                        activeClassName, schoolID, mFirebaseUser.getUid(), dateCreated, sortableDateCreated, dateScheduled, sortableDateScheduled,
                        subject.getText().toString(), description.getText().toString(), classLink, true);

                HashMap<String, Object> scheduleClassMap = new HashMap<>();
                scheduleClassMap.put("E Classroom Scheduled Class/Teacher/" + mFirebaseUser.getUid() + "/" + pushKey, eClassroomScheduledClassesListModel);
                scheduleClassMap.put("E Classroom Scheduled Class Recipients/" + pushKey + "/Teacher/" + mFirebaseUser.getUid(), true);
                scheduleClassMap.put("E Classroom Scheduled Class Participants/" + pushKey + "/" + mFirebaseUser.getUid(), false);

                scheduleClassMap.put("E Classroom Scheduled Class/Class/" + activeClassID + "/" + pushKey, eClassroomScheduledClassesListModel);
                scheduleClassMap.put("E Classroom Scheduled Class Recipients/" + pushKey + "/Class/" + activeClassID, true);

                for (int i = 0; i < studentList.size(); i++) {
                    String studentID = studentList.get(i).getStudentID();
                    String studentName = studentList.get(i).getFirstName() + " " + studentList.get(i).getLastName();
                    String studentProfilePictureURL = studentList.get(i).getImageURL();

                    scheduleClassMap.put("E Classroom Scheduled Class/Student/" + studentID + "/" + pushKey, eClassroomScheduledClassesListModel);
                    scheduleClassMap.put("E Classroom Scheduled Class Recipients/" + pushKey + "/Student/" + studentID, true);
                    scheduleClassMap.put("E Classroom Scheduled Class Participants/" + pushKey + "/" + studentID, false);

                    ArrayList<String> parentIDList = studentParentList.get(studentID);
                    if (parentIDList != null) {
                        for (int j = 0; j < parentIDList.size(); j++) {
                            String parentID = parentIDList.get(j);
                            if (!parentID.isEmpty()) {
                                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), parentID, "Parent", "Teacher", dateCreated,
                                        sortableDateCreated, pushKey, "EClassroom", studentProfilePictureURL, studentID, studentName, false);
                                scheduleClassMap.put("NotificationParent/" + parentID + "/" + pushKey, notificationModel);
                                scheduleClassMap.put("Notification Badges/Parents/" + parentID + "/Notifications/status", true);
                                scheduleClassMap.put("Notification Badges/Parents/" + parentID + "/More/status", true);
                                scheduleClassMap.put("EClassroomParentNotification/" + parentID + "/" + studentID + "/status", true);
                                scheduleClassMap.put("EClassroomParentNotification/" + parentID + "/" + studentID + "/" + pushKey + "/status", true);
                                scheduleClassMap.put("EClassroomParentRecipients/" + pushKey + "/" + parentID, true);
                            }
                        }
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(scheduleClassMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            customProgressDialogOne.dismiss();
                            String message = "A new e-classroom schedule has been created.";
                            showDialogWithMessageAndDisconnect(message);
                        } else {
                            customProgressDialogOne.dismiss();
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
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

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onDialogTimeSet(int reference, int hourOfDay, int minute) {
        int hour = hourOfDay;
        hourOfTheDay = String.valueOf(hourOfDay);
        this.minute = String.valueOf(minute);
        if (hour < 13) {
            time.setText((hourOfTheDay) + ":" + Date.makeTwoDigits(minute) + " AM");
            zone = "AM";
        } else {
            time.setText((hour - 12) + ":" + (minute) + " PM");
            zone = "PM";
        }
    }

    String getDayOfTheWeek(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Sunday";
        }
    }

    void showDialogWithMessageAndDisconnect(String messageString) {
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
                finish();
                dialog.dismiss();
            }
        });
    }

    void showDialogWithMessage(String messageString) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                activeClass = data.getStringExtra("Selected Class");
                Gson gson = new Gson();
                Type type = new TypeToken<Class>() {}.getType();
                activeClassModel = gson.fromJson(activeClass, type);
                activeClassID = activeClassModel.getID();
                activeClassName = activeClassModel.getClassName();
                className.setText(activeClassName);
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                subject.setText(data.getStringExtra("Selected Subject"));
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                day.setText(data.getStringExtra("Selected Day"));
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}