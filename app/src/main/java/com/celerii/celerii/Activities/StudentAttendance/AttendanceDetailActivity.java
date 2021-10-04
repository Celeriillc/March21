package com.celerii.celerii.Activities.StudentAttendance;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ParentAttendanceRow;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
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
import java.util.Calendar;
import java.util.HashMap;

public class AttendanceDetailActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    TextView date, status, term, subject, teacher, className, school;
    EditText remark;
    Bundle b;

    String activeKid, activeKidName, activeKidID, activeAccount;
    String dateString, statusString, termString, subjectString, teacherString, classID, schoolString, remarkString, key;
    String parentActivity;
    Boolean isSubscribed;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Attendance Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

//        Gson gson = new Gson();
//        Type type = new TypeToken<Student>() {}.getType();
//        Student activeStudentModel = gson.fromJson(sharedPreferencesManager.getActiveKid(), type);

        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        activeKidID = b.getString("ID");
        activeKidName = b.getString("name");
        activeAccount = b.getString("accountType");
//        activeKidName = activeStudentModel.getFirstName();
        parentActivity = b.getString("parentActivity");
        isSubscribed = b.getBoolean("isSubscribed");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance Detail");
        getSupportActionBar().setHomeButtonEnabled(true);

        date = (TextView) findViewById(R.id.date);
        status = (TextView) findViewById(R.id.status);
        term = (TextView) findViewById(R.id.term);
        subject = (TextView) findViewById(R.id.subject);
        teacher = (TextView) findViewById(R.id.teacher);
        className = (TextView) findViewById(R.id.classname);
        school = (TextView) findViewById(R.id.school);
        remark = (EditText) findViewById(R.id.remark);

        if (!isSubscribed) {
            String message;
            if (activeAccount.equals("Parent")) {
                message = activeKidName + "'s attendance information is not currently available, please subscribe " + activeKidName + " to a Celerii plan to get the latest information from " + activeKidName + "'s school";
            } else {
                message = activeKidName + "'s attendance information is not currently available, please check back when " + activeKidName + " has an active Celerii subscription";
            }
            showDialogWithMessageAndClose(message);
            return;
        }

        updateBadges();
        loadFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

    }

    private void loadFromFirebase() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            superLayout.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(getString(R.string.no_internet_message_for_offline_download));
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceStudent").child(activeKidID).child(key);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final ParentAttendanceRow parentAttendanceRow = dataSnapshot.getValue(ParentAttendanceRow.class);

                    dateString = parentAttendanceRow.getDate();
                    statusString = parentAttendanceRow.getAttendanceStatus();
                    termString = Term.Term(parentAttendanceRow.getTerm());
                    subjectString = parentAttendanceRow.getSubject();
                    classID = parentAttendanceRow.getClassID();
                    schoolString = parentAttendanceRow.getSchoolID();
                    teacherString = parentAttendanceRow.getTeacherID();
                    remarkString = parentAttendanceRow.getRemark().trim();

                    String[] datearray = dateString.split(" ")[0].split("/");
                    Calendar c = Calendar.getInstance();
                    c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
                    final int day = c.get(Calendar.DAY_OF_WEEK);

                    date.setText(Day.Day(day) + ", " + Date.DateFormatMMDDYYYY(dateString));
                    status.setText(statusString);
                    term.setText(termString);
                    subject.setText(subjectString);
                    remark.setText(remarkString);

                    if (TextUtils.isEmpty(remarkString)) {
                        if (statusString.equals("Present")) {
                            remark.setHint("Enter your remarks here");
                        } else if (statusString.equals("Absent")) {
                            remark.setHint("Please tell us why they were absent today");
                        } else {
                            remark.setHint("Please tell us why they were late to class today");
                        }
                    } else {
                        remark.setText(remarkString);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                    mDatabaseReference.keepSynced(true);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Class classInstance = dataSnapshot.getValue(Class.class);
                                className.setText(classInstance.getClassName());
                            } else {
                                className.setText("This class has been deleted");
                            }

                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolString);
                            mDatabaseReference.keepSynced(true);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        School schoolInstance = dataSnapshot.getValue(School.class);
                                        school.setText(schoolInstance.getSchoolName());
                                    } else {
                                        school.setText("This school account has been deleted");
                                    }

                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherString);
                                    mDatabaseReference.keepSynced(true);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                Teacher teacherInstance = dataSnapshot.getValue(Teacher.class);
                                                teacher.setText(teacherInstance.getFirstName() + " " + teacherInstance.getLastName());
                                            } else {
                                                teacher.setText("This teacher account has been deleted");
                                            }

                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            superLayout.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    mySwipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("Sorry, we couldn't find this attendance record");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void updateBadges() {
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + activeKidID + "/status", false);
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + activeKidID + "/" + key + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeKidID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + activeKidID + "/status", false);
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + activeKidID + "/" + key + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeKidID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }
    }

    public void updateRemarks() {
        try {
            String remarkString = remark.getText().toString().trim();
            HashMap<String, Object> remarkUpdateMap = new HashMap<>();
            remarkUpdateMap.put("AttendanceStudent/" + activeKidID + "/" + key + "/remark", remarkString);
            remarkUpdateMap.put("AttendanceClass-Students/" + classID + "/" + key + "/Students/" + activeKidID + "/remark", remarkString);
            mDatabaseReference = mFirebaseDatabase.getReference();
            mDatabaseReference.updateChildren(remarkUpdateMap);
        } catch (Exception e) {}
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

        if (id == android.R.id.home) {
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    updateRemarks();
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "3");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            } else {
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    updateRemarks();
                }
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                updateRemarks();
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "2");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "3");
                i.putExtras(bundle);
                startActivity(i);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                updateRemarks();
            }
        }
    }
}
