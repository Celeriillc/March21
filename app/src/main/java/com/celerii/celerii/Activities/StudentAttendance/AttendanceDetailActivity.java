package com.celerii.celerii.Activities.StudentAttendance;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ParentAttendanceRow;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AttendanceDetailActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    TextView date, status, term, subject, teacher, className, school;
    EditText remark;
    Bundle b;

    String activeKid, activeKidName, activeKidID;
    String dateString, statusString, termString, subjectString, teacherString, classNameString, schoolString, remarkString, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        activeKidID = b.getString("ID");
        activeKidName = sharedPreferencesManager.getActiveKid().split(" ")[1];

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
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendenceStudent").child(activeKidID).child(key);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final ParentAttendanceRow parentAttendanceRow = dataSnapshot.getValue(ParentAttendanceRow.class);

                    dateString = parentAttendanceRow.getDate();
                    statusString = parentAttendanceRow.getAttendanceStatus();
                    termString = Term.Term(parentAttendanceRow.getTerm());
                    subjectString = parentAttendanceRow.getSubject();
                    classNameString = parentAttendanceRow.getClassID();
                    schoolString = parentAttendanceRow.getSchoolID();
                    teacherString = parentAttendanceRow.getTeacherID();
                    remarkString = parentAttendanceRow.getRemark();

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
                            remark.setHint("Please tell us why " + activeKidName + " was absent");
                        } else {
                            remark.setHint("Please tell us why " + activeKidName + " was late to class");
                        }
                    } else {
                        remark.setText(remarkString);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classNameString);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        mDatabaseReference = mFirebaseDatabase.getReference();

        Map<String, Object> remarkMap = new HashMap<String, Object>();
        remarkMap.put("AttendenceStudent/" + activeKidID + "/" + key + "/remark", remark.getText().toString());
        mDatabaseReference.updateChildren(remarkMap);

        super.onStop();
    }
}
