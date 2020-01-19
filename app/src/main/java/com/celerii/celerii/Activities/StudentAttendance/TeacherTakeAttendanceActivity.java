package com.celerii.celerii.Activities.StudentAttendance;

import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherTakeAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TeacherTakeAttendanceActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    private ArrayList<TeacherAttendanceRow> teacherAttendanceRowList;
    private TeacherAttendanceHeader teacherAttendanceHeader;
    public RecyclerView recyclerView;
    public TeacherTakeAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    boolean connected = true;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    String activeClass = "";
    String myName, className, subject, term, date, year, month, day, month_year, term_year, subject_term_year, year_month_day, dateForAdapter, sortableDate,
            teacherName, teacherID, schoolID;

    String childName;
    String childImageURL;
    Integer maleCount = 0, femaleCount = 0, studentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_take_attendance);

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
        teacherName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        teacherID = sharedPreferencesManager.getMyUserID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New " + className + " Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        teacherAttendanceHeader = new TeacherAttendanceHeader();
        teacherAttendanceRowList = new ArrayList<>();
        teacherAttendanceRowList.add(new TeacherAttendanceRow());
        mAdapter = new TeacherTakeAttendanceRowAdapter(teacherAttendanceRowList, teacherAttendanceHeader, this, this);

        date = Date.getDate();
        year = Date.getYear();
        month = Date.getMonth();
        day = Date.getDay();
        term = Term.getTermShort();

        loadHeaderFromFirebase();
        loadDetailsFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadHeaderFromFirebase();
                        loadDetailsFromFirebase();
                    }
                }
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Date Information"));
    }

    void loadHeaderFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subject = teacherTakeAttendanceSharedPreferences.getSubject();
        month_year = month + "_" + year;
        term_year = term + "_" + year;
        subject_term_year = subject + "_" + term + "_" + year;
        year_month_day = year + "/" + month + "/" + day;
        sortableDate = Date.convertToSortableDate(date);
        dateForAdapter = Date.DateFormatMMDDYYYY(date);

        teacherAttendanceHeader.setSubject(subject);
        teacherAttendanceHeader.setDate(date);
        teacherAttendanceHeader.setSortableDate(sortableDate);
        teacherAttendanceHeader.setDay(day);
        teacherAttendanceHeader.setMonth(month);
        teacherAttendanceHeader.setYear(year);
        teacherAttendanceHeader.setTerm(term);
        teacherAttendanceHeader.setTerm_year(term_year);
        teacherAttendanceHeader.setMonth_year(month_year);
        teacherAttendanceHeader.setSubject_term_year(subject_term_year);
        teacherAttendanceHeader.setYear_month_day(year_month_day);
        teacherAttendanceHeader.setClassID(activeClass);
        teacherAttendanceHeader.setClassName(className);
        teacherAttendanceHeader.setTeacher(teacherName);
        teacherAttendanceHeader.setTeacherID(teacherID);
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("Class School/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        schoolID = postSnapshot.getKey();
                        teacherAttendanceHeader.setSchoolID(schoolID);
                        break;
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            maleCount = 0;
                            femaleCount = 0;
                            studentCount = 0;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                final String studentKey = postSnapshot.getKey();

                                DatabaseReference childReference = mFirebaseDatabase.getReference("Student/" + studentKey);
                                childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Student student = dataSnapshot.getValue(Student.class);
                                        String gender = student.getGender();
                                        if (gender.equals("Male")) {
                                            maleCount++;
                                        } else if (gender.equals("Female")) {
                                            femaleCount++;
                                        }
                                        studentCount++;

                                        teacherAttendanceHeader.setNoOfBoys(String.valueOf(maleCount));
                                        teacherAttendanceHeader.setNoOfGirls(String.valueOf(femaleCount));
                                        teacherAttendanceHeader.setNoOfStudents(String.valueOf(studentCount));
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            teacherAttendanceHeader.setNoOfBoys("Not Available");
                            teacherAttendanceHeader.setNoOfGirls("Not Available");
                            teacherAttendanceHeader.setNoOfStudents("Not Available");
                        }
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

    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherAttendanceRowList.clear();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String childKey = postSnapshot.getKey();

                        DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("Student/" + childKey);
                        childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Student child = dataSnapshot.getValue(Student.class);
                                    childName = child.getFirstName() + " " + child.getLastName();
                                    childImageURL = child.getImageURL();

                                    TeacherAttendanceRow teacherAttendanceRow = new TeacherAttendanceRow(childName, "Present", childImageURL, date, term, day, month, year, "", month_year, term_year);
                                    teacherAttendanceRow.setStudentID(dataSnapshot.getKey());
                                    teacherAttendanceRowList.add(teacherAttendanceRow);

                                    if (childrenCount == teacherAttendanceRowList.size()) {
                                        teacherAttendanceRowList.add(0, new TeacherAttendanceRow());
                                        teacherAttendanceRowList.add(new TeacherAttendanceRow());
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This class doesn't contain any students");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                teacherAttendanceHeader.setSubject(data.getStringExtra("Selected Subject"));
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                teacherAttendanceHeader.setTerm(data.getStringExtra("Selected Term"));
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void saveToCloud() {
        if (CheckNetworkConnectivity.isNetworkAvailable(this)) {
            if (teacherAttendanceRowList.size() <= 2) {
                showDialogWithMessage("Attendance cannot be saved to cloud because the class contains no students.");
                return;
            }

            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(TeacherTakeAttendanceActivity.this);
            progressDialog.show();

            mDatabaseReference = mFirebaseDatabase.getReference("AttendanceClass").child(activeClass).push();
            String pushID = mDatabaseReference.getKey();
            mDatabaseReference = mFirebaseDatabase.getReference();
            teacherAttendanceHeader.setDate(date);
            teacherAttendanceHeader.setKey(pushID);

            Map<String, Object> newAttendance = new HashMap<String, Object>();
            newAttendance.put("AttendenceClass/" + activeClass + "/" + pushID, teacherAttendanceHeader);
            for (int i = 0; i < teacherAttendanceRowList.size(); i++) {
                if (teacherAttendanceRowList.get(i).getStudentID() != null) {
                    newAttendance.put("AttendenceClass-Students/" + activeClass + "/" + pushID + "/Students/" + teacherAttendanceRowList.get(i).getStudentID(),
                            new AttendanceStatusModel(teacherAttendanceRowList.get(i).getAttendanceStatus(), ""));

                    TeacherAttendanceRow studentAttendance = teacherAttendanceRowList.get(i);
                    studentAttendance.setTeacherID(auth.getCurrentUser().getUid());
                    studentAttendance.setSubject(subject);
                    studentAttendance.setClassID(activeClass);
                    studentAttendance.setSchoolID(schoolID);
                    studentAttendance.setSortableDate(sortableDate);
                    studentAttendance.setSubject_term_year(subject_term_year);
                    studentAttendance.setYear_month_day(year_month_day);
                    studentAttendance.setKey(pushID);
                    newAttendance.put("AttendenceStudent/" + teacherAttendanceRowList.get(i).getStudentID() + "/" + pushID, studentAttendance);
                }
            }

            mDatabaseReference.updateChildren(newAttendance, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    //ProgressBar Hold
                    progressDialog.dismiss();
                    showDialogWithMessage("Attendance has been posted");
                }
            });
        } else {
            showDialogWithMessage("Your device is not connected to the internet. Check your connection and try again.");
        }
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

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

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getStringExtra("Year");
            month = String.valueOf(intent.getStringExtra("Month"));
            day = String.valueOf(intent.getStringExtra("Day"));
            term = Term.getTermShort(month);
            date = year + "/" + month + "/" + day + " 12:00:00:00";

            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);

            loadHeaderFromFirebase();
            loadDetailsFromFirebase();
        }
    };
}

