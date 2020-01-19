package com.celerii.celerii.Activities.StudentAttendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AttendanceStatusModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;


public class TeacherAttendanceActivity extends AppCompatActivity  {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    DatabaseReference headerDatabaseReference;

    Toolbar toolbar;
    private ArrayList<TeacherAttendanceRow> teacherAttendanceRowList;
    private TeacherAttendanceHeader teacherAttendanceHeader;
    public RecyclerView recyclerView;
    public TeacherAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    String activeClass = "fuk";
    String attendanceKey;

    String myName, className, subject, term, date, year, month, day, year_month_day, dateForAdapter, maleCount, femaleCount, studentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        activeClass = sharedPreferencesManager.getActiveClass();
        if (activeClass == null) {
            Set<String> classSet = sharedPreferencesManager.getMyClasses();
            ArrayList<String> classes = new ArrayList<>();
            if (classSet != null) {
                classes = new ArrayList<>(classSet);
                activeClass = classes.get(0);
                sharedPreferencesManager.setActiveClass(activeClass);
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayoutText.setText("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                return;
            }
        }

        activeClass = sharedPreferencesManager.getActiveClass().split(" ")[0];
        className = sharedPreferencesManager.getActiveClass().split(" ")[1];

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        headerDatabaseReference = mFirebaseDatabase.getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        date = Date.getDate();
        year = Date.getYear();
        month = Date.getMonth();
        day = Date.getDay();
        term = Term.getTermShort();
        year_month_day = year + "/" + month + "/" + day;
        subject = "General";

        teacherAttendanceHeader = new TeacherAttendanceHeader();
        teacherAttendanceRowList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new TeacherAttendanceRowAdapter(teacherAttendanceRowList, teacherAttendanceHeader, this);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadDetailsFromFirebase();
                }
            }
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Date Information"));
    }

    int counter;
    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;

        teacherAttendanceHeader.setDate(date);
        teacherAttendanceHeader.setSubject(subject);
        teacherAttendanceHeader.setClassID(activeClass);
        teacherAttendanceHeader.setClassName("");
        teacherAttendanceHeader.setTeacher("");
        teacherAttendanceHeader.setTerm("");
        teacherAttendanceHeader.setNoOfStudents("");
        teacherAttendanceHeader.setNoOfBoys("");
        teacherAttendanceHeader.setNoOfGirls("");
        teacherAttendanceRowList.clear();

        headerDatabaseReference = mFirebaseDatabase.getReference().child("AttendenceClass").child(activeClass);
        headerDatabaseReference.orderByChild("year_month_day").equalTo(year_month_day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        counter++;
                        TeacherAttendanceHeader header = postSnapshot.getValue(TeacherAttendanceHeader.class);
                        if (subject.equals(header.getSubject())) {
                            attendanceKey = postSnapshot.getKey();
                            teacherAttendanceHeader.setKey(attendanceKey);
                            teacherAttendanceHeader.setClassName(header.getClassName());
                            teacherAttendanceHeader.setTeacher(header.getTeacher());
                            teacherAttendanceHeader.setTeacherID(header.getTeacherID());
                            teacherAttendanceHeader.setTerm(header.getTerm());
                            teacherAttendanceHeader.setNoOfStudents(header.getNoOfStudents());
                            teacherAttendanceHeader.setNoOfBoys(header.getNoOfBoys());
                            teacherAttendanceHeader.setNoOfGirls(header.getNoOfGirls());
                            mAdapter.notifyDataSetChanged();
                            break;
                        } else {
                            if (dataSnapshot.getChildrenCount() == counter) {
                                teacherAttendanceRowList.clear();
                                teacherAttendanceRowList.add(0, new TeacherAttendanceRow());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                                return;
                            }
                        }
                    }

                    counter = 0;
                    DatabaseReference bodyDatabaseReference = mFirebaseDatabase.getReference("AttendenceClass-Students/" + activeClass + "/" + attendanceKey + "/Students");
                    bodyDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                teacherAttendanceRowList.clear();
                                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String childKey = postSnapshot.getKey();
                                    AttendanceStatusModel attendanceStatusModel = postSnapshot.getValue(AttendanceStatusModel.class);
                                    final TeacherAttendanceRow teacherAttendanceRow = new TeacherAttendanceRow();
                                    teacherAttendanceRow.setDate(teacherAttendanceHeader.getDate());
                                    teacherAttendanceRow.setTerm(teacherAttendanceHeader.getTerm());
                                    teacherAttendanceRow.setRemark(attendanceStatusModel.getRemark());
                                    teacherAttendanceRow.setAttendanceStatus(attendanceStatusModel.getAttendanceStatus());
                                    teacherAttendanceRow.setKey(attendanceKey);
                                    teacherAttendanceRow.setStudentID(childKey);

                                    DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("Student/" + childKey);
                                    childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            counter++;
                                            Student student = dataSnapshot.getValue(Student.class);
                                            teacherAttendanceRow.setName(student.getFirstName() + " " + student.getLastName());
                                            teacherAttendanceRow.setImageURL(student.getImageURL());
                                            teacherAttendanceRowList.add(teacherAttendanceRow);

                                            if (counter == childrenCount) {
                                                teacherAttendanceRowList.add(0, new TeacherAttendanceRow());
                                                mAdapter.notifyDataSetChanged();
                                                recyclerView.setVisibility(View.VISIBLE);
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            } else {
                                teacherAttendanceRowList.clear();
                                teacherAttendanceRowList.add(0, new TeacherAttendanceRow());
                                mAdapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    teacherAttendanceRowList.clear();
                    teacherAttendanceRowList.add(0, new TeacherAttendanceRow());
                    mAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                subject = data.getStringExtra("Selected Subject");

                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);

                teacherAttendanceHeader.setSubject(subject);
                mAdapter.notifyDataSetChanged();

                loadDetailsFromFirebase();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getStringExtra("Year");
            month = String.valueOf(intent.getStringExtra("Month"));
            day = String.valueOf(intent.getStringExtra("Day"));
            term = Term.getTermShort(month);
            date = year + "/" + month + "/" + day + " 12:00:00:00";
            year_month_day = year + "/" + month + "/" + day;

            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);

            teacherAttendanceHeader.setDate(date);
            mAdapter.notifyDataSetChanged();

            loadDetailsFromFirebase();
        }
    };
}
