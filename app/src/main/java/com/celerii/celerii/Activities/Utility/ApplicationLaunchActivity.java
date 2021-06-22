package com.celerii.celerii.Activities.Utility;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import com.celerii.celerii.Activities.EClassroom.Parent.ParentEClassroomMessageBoardActivity;
import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.LoginAndSignup.FederatedSignInAccountTypeActivity;
import com.celerii.celerii.Activities.LoginAndSignup.WelcomeToBetaActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.LoginAndSignup.SignUpActivityFive;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.models.ReminderModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ApplicationLaunchActivity extends AppCompatActivity {

    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_launch);

        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        if (mFirebaseUser == null){
            Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
            startActivity(I);
            finish();
        }
        else {
            String launchActivity = applicationLauncherSharedPreferences.getLauncherActivity();
            if (launchActivity.equals("SignupFive")) {
                Intent I = new Intent(ApplicationLaunchActivity.this, SignUpActivityFive.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("WelcomeToBeta")) {
                Intent I = new Intent(ApplicationLaunchActivity.this, WelcomeToBetaActivity.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("IntroSlider")) {
                auth.signOut();
                Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("FederatedSignInAccountType")) {
                Intent I = new Intent(ApplicationLaunchActivity.this, FederatedSignInAccountTypeActivity.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("Home")) {
                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    Intent I = new Intent(ApplicationLaunchActivity.this, TeacherMainActivityTwo.class);
                    startActivity(I);
                    finish();
                } else {
                    Intent I = new Intent(ApplicationLaunchActivity.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    finish();
                }
            } else {
                sharedPreferencesManager.clear();
                auth.signOut();
                Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
                startActivity(I);
                finish();
            }
        }

        prepareTeacherEventReminders();
    }

    int idCounter;
    int teacherEventCounter;
    int parentEventCounter;
    int studentCounter;
    ArrayList<Integer> reminderIDs;
    HashMap<Integer, ReminderModel> reminderModels;
    ArrayList<Student> children;
    public void prepareTeacherEventReminders() {
        idCounter = 0;
        teacherEventCounter = 0;
        parentEventCounter = 0;
        studentCounter = 0;

        Gson gson = new Gson();
        String reminderIDsJSON = sharedPreferencesManager.getReminderIDs();
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        reminderModels = new HashMap<>();
        reminderIDs = gson.fromJson(reminderIDsJSON, type);

        if (reminderIDs != null) {
            for (int reminderID : reminderIDs) {
                deleteAlarm(reminderID);
            }
            reminderIDs.clear();
        } else {
            reminderIDs = new ArrayList<>();
        }

        sharedPreferencesManager.deleteReminderIDs();
        sharedPreferencesManager.deleteReminderDetails();

        gson = new Gson();
        String myChildrenJSON = sharedPreferencesManager.getMyChildren();
        type = new TypeToken<ArrayList<Student>>() {}.getType();
        children = gson.fromJson(myChildrenJSON, type);

        try {
            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Events").child(mFirebaseUser.getUid());
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            int teacherEventChildrenCount = (int) dataSnapshot.getChildrenCount();
                            String eventKey = postSnapshot.getKey();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    teacherEventCounter++;
                                    if (dataSnapshot.exists()) {
                                        EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);

                                        String todaysDate = (Date.getDate());
                                        if (Date.compareDates(eventRow.getEventDate(), todaysDate)) {
                                            ReminderModel reminderModel = new ReminderModel();
                                            reminderModel.setActivityID(eventKey);
                                            reminderModel.setAccountType("Teacher");
                                            reminderModel.setReminderType("Event");
                                            reminderModel.setEventTitle(eventRow.getEventTitle());
                                            reminderModel.setEventSender(eventRow.getSchoolID());
                                            reminderModel.setOriginalScheduledDate(eventRow.getEventDate());

                                            ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, zeroMinutes);
                                            idCounter++;

                                            ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, tenMinutes);
                                            idCounter++;

                                            ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneHour);
                                            idCounter++;

                                            ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneDay);
                                            idCounter++;
                                        }
                                    }

                                    if (teacherEventCounter == teacherEventChildrenCount) {
                                        prepareParentEventReminders();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        prepareParentEventReminders();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            return;
        }
    }

    private void prepareParentEventReminders() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent Events").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        int parentEventChildrenCount = (int) dataSnapshot.getChildrenCount();
                        String eventKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                parentEventCounter++;
                                if (dataSnapshot.exists()) {
                                    EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);

                                    String todaysDate = (Date.getDate());
                                    if (Date.compareDates(eventRow.getEventDate(), todaysDate)) {
                                        ReminderModel reminderModel = new ReminderModel();
                                        reminderModel.setActivityID(eventKey);
                                        reminderModel.setAccountType("Parent");
                                        reminderModel.setReminderType("Event");
                                        reminderModel.setEventTitle(eventRow.getEventTitle());
                                        reminderModel.setEventSender(eventRow.getSchoolID());
                                        reminderModel.setOriginalScheduledDate(eventRow.getEventDate());

                                        ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, zeroMinutes);
                                        idCounter++;

                                        ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, tenMinutes);
                                        idCounter++;

                                        ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, oneHour);
                                        idCounter++;

                                        ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                        createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                        reminderIDs.add(idCounter);
                                        reminderModels.put(idCounter, oneDay);
                                        idCounter++;
                                    }
                                }

                                if (parentEventCounter == parentEventChildrenCount) {
                                    prepareStudentEClassroomReminders();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    prepareStudentEClassroomReminders();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void prepareStudentEClassroomReminders() {
        if (children != null) {
            if (children.size() > 0) {
                for (Student child : children) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class").child("Student").child(child.getStudentID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            studentCounter++;
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    EClassroomScheduledClassesListModel eClassroomScheduledClassesListModel = postSnapshot.getValue(EClassroomScheduledClassesListModel.class);

                                    if (eClassroomScheduledClassesListModel.getOpen() == null) {
                                        eClassroomScheduledClassesListModel.setOpen(true);
                                    }

                                    String todaysDate = (Date.getDate());
                                    if (Date.compareDates(eClassroomScheduledClassesListModel.getDateScheduled(), todaysDate)) {
                                        if (eClassroomScheduledClassesListModel.getOpen()) {
                                            ReminderModel reminderModel = new ReminderModel();
                                            reminderModel.setActivityID(postSnapshot.getKey());
                                            reminderModel.setAccountType("Parent");
                                            reminderModel.setReminderType("EClassroom");
                                            reminderModel.seteClassroomChildName(child.getFirstName() + " " + child.getLastName());
                                            reminderModel.seteClassroomChildID(child.getStudentID());
                                            reminderModel.seteClassroomChildProfilePictureURL(child.getImageURL());
                                            reminderModel.seteClassroomLink(eClassroomScheduledClassesListModel.getClassLink());
                                            reminderModel.seteClassroomState("Scheduled");
                                            reminderModel.setOriginalScheduledDate(eClassroomScheduledClassesListModel.getDateScheduled());

                                            ReminderModel zeroMinutes = getTimeInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, zeroMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, zeroMinutes);
                                            idCounter++;

                                            ReminderModel tenMinutes = getMinusTenMinutesInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, tenMinutes.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, tenMinutes);
                                            idCounter++;

                                            ReminderModel oneHour = getMinusOneHourInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneHour.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneHour);
                                            idCounter++;

                                            ReminderModel oneDay = getMinusOneDayInMilliSeconds(reminderModel);
                                            createAlarm(idCounter, oneDay.getTimeInMilliseconds());
                                            reminderIDs.add(idCounter);
                                            reminderModels.put(idCounter, oneDay);
                                            idCounter++;
                                        }
                                    }
                                }

                                if (studentCounter == children.size()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(reminderIDs);
                                    sharedPreferencesManager.setReminderIDs(json);

                                    gson = new Gson();
                                    json = gson.toJson(reminderModels);
                                    sharedPreferencesManager.setReminderDetails(json);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                Gson gson = new Gson();
                String json = gson.toJson(reminderIDs);
                sharedPreferencesManager.setReminderIDs(json);

                gson = new Gson();
                json = gson.toJson(reminderModels);
                sharedPreferencesManager.setReminderDetails(json);
            }
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(reminderIDs);
            sharedPreferencesManager.setReminderIDs(json);

            gson = new Gson();
            json = gson.toJson(reminderModels);
            sharedPreferencesManager.setReminderDetails(json);
        }
    }

    public void createAlarm(int reminderID, long timeInMilliseconds) {
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("reminderID", reminderID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), reminderID, intent, 0);

        long currentTime = System.currentTimeMillis();
        long timeDifference = timeInMilliseconds - currentTime;

        if (timeDifference > 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + timeDifference, pendingIntent);
        }
    }

    public void deleteAlarm(int reminderID) {
        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
        Intent intent = new Intent(this, ApplicationLaunchActivity.MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), reminderID, intent, 0);
        alarmManager.cancel(pendingIntent) ;
    }

    private ReminderModel getMinusOneDayInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("1 day");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getMinusOneHourInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.HOUR, -1);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("1 hour");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getMinusTenMinutesInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        calendar.add(Calendar.MINUTE, -10);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("10 minutes");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    private ReminderModel getTimeInMilliSeconds(ReminderModel reminderModel) {
        ReminderModel newReminderModel = new ReminderModel(reminderModel);

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[0]);
        int month = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[1]) - 1;
        int day = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[0].split("/")[2]);
        int hourOfDay = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[0]);
        int minute = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[1]);
        int seconds = Integer.parseInt(reminderModel.getOriginalScheduledDate().split(" ")[1].split(":")[2]);
        calendar.set(year, month, day, hourOfDay, minute, seconds);
        long milliseconds = calendar.getTimeInMillis();
        newReminderModel.setTimeInMilliseconds(milliseconds);
        newReminderModel.setTimeToEvent("less than a minute");
        newReminderModel.setScheduledDate(String.format("%s/%s/%s %s:%s:%s:000", String.valueOf(calendar.get(Calendar.YEAR)),
                Date.makeTwoDigits(String.valueOf(Month.MonthBase1(calendar.get(Calendar.MONTH)))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))), Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.MINUTE))),
                Date.makeTwoDigits(String.valueOf(calendar.get(Calendar.SECOND)))));

        return newReminderModel;
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int reminderID = intent.getIntExtra("reminderID", -1);
            Gson gson = new Gson();
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
            String reminderDetailsJSON = sharedPreferencesManager.getReminderDetails();
            Type type = new TypeToken<HashMap<Integer, ReminderModel>>() {}.getType();
            HashMap<Integer, ReminderModel> reminderModels = gson.fromJson(reminderDetailsJSON, type);
            ReminderModel reminderModel = new ReminderModel();

            String reminderIDsJSON = sharedPreferencesManager.getReminderIDs();
            type = new TypeToken<ArrayList<Integer>>() {}.getType();
            ArrayList<Integer> reminderIDs = gson.fromJson(reminderIDsJSON, type);

            if (reminderModels != null) {
                if (reminderModels.size() > 0) {
                    if (reminderID >= 0) {
                        reminderModel = reminderModels.get(reminderID);
                    }
                }
            }

            Intent resultIntent;
            PendingIntent resultPendingIntent;
            Bundle bundle = new Bundle();
            Spanned message;
            String subText;
            String contentTitle;
            int notificationID;

            if (reminderModel != null) {
                if (!reminderModel.getActivityID().trim().equals("")) {
                    if (reminderModel.getReminderType().equals("Event")) {
                        resultIntent = new Intent(context, EventDetailActivity.class);
                        bundle.putString("Event ID", reminderModel.getActivityID());
                        bundle.putString("Color Number", String.valueOf(0));
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "Your event <strong>%s</strong> is scheduled " +
                                "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.getEventTitle(),
                                reminderModel.getTimeToEvent()));
                        subText = "Event Reminder";
                        contentTitle = "You have an upcoming event";
                        notificationID = 100;
                    } else {
                        Student student = new Student(reminderModel.geteClassroomChildName(), reminderModel.geteClassroomChildID(),
                                reminderModel.geteClassroomChildProfilePictureURL());
                        gson = new Gson();
                        String activeKid = gson.toJson(student);

                        resultIntent = new Intent(context, ParentEClassroomMessageBoardActivity.class);
                        bundle.putString("Child ID", activeKid);
                        bundle.putString("Scheduled Class ID", reminderModel.getActivityID());
                        bundle.putString("Scheduled Class Link", reminderModel.geteClassroomLink());
                        bundle.putString("Scheduled Class State", reminderModel.geteClassroomState());
                        bundle.putString("Scheduled Class Scheduled Date", reminderModel.getOriginalScheduledDate());
                        bundle.putString("parentActivity", reminderModel.getAccountType());
                        resultIntent.putExtras(bundle);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        message = Html.fromHtml(String.format(Locale.getDefault(), "<strong>%s</strong> has a remote class scheduled " +
                                        "to hold in <strong>%s</strong>. Tap this notification to view.", reminderModel.geteClassroomChildName(),
                                reminderModel.getTimeToEvent()));
                        subText = "Scheduled E-Classroom Reminder";
                        contentTitle = reminderModel.geteClassroomChildName() + " has a remote class coming up soon";
                        notificationID = 101;
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Reminder");
                    builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
                            .setColor(ContextCompat.getColor(context, R.color.colorSecondaryPurple))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setSubText(subText)
                            .setContentTitle(contentTitle)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setOnlyAlertOnce(true)
                            .setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder.setChannelId("com.celerii.celerii");
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(
                                "com.celerii.celerii",
                                "Celerii",
                                NotificationManager.IMPORTANCE_DEFAULT);

                        if (mNotificationManager != null) {
                            mNotificationManager.createNotificationChannel(channel);
                        }
                    }

                    try {
                        mNotificationManager.notify(notificationID, builder.build());
                    } catch (NullPointerException e) {
                        return;
                    }

                    try {
                        reminderIDs.remove(reminderID);
                        reminderModels.remove(reminderID);

                        gson = new Gson();
                        String json = gson.toJson(reminderIDs);
                        sharedPreferencesManager.setReminderIDs(json);

                        gson = new Gson();
                        json = gson.toJson(reminderModels);
                        sharedPreferencesManager.setReminderDetails(json);
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }
        }
    }
}
