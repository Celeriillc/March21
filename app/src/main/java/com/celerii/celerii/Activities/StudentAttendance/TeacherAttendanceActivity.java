package com.celerii.celerii.Activities.StudentAttendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AttendanceStatusModel;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherAttendanceRow;
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


public class TeacherAttendanceActivity extends AppCompatActivity  {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    DatabaseReference headerDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private ArrayList<TeacherAttendanceRow> teacherAttendanceRowList;
    private TeacherAttendanceHeader teacherAttendanceHeader;
    public RecyclerView recyclerView;
    public TeacherAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    String activeClass = "fuk";
    String attendanceKey;

    String myName, className, subject, term, date, year, month, day, year_month_day, dateForAdapter, maleCount, femaleCount, studentCount;

    String featureUseKey = "";
    String featureName = "Teacher Attendance Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        headerDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        activeClass = sharedPreferencesManager.getActiveClass();

        if (activeClass == null) {
            Gson gson = new Gson();
            ArrayList<Class> myClasses = new ArrayList<>();
            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            Type type = new TypeToken<ArrayList<Class>>() {}.getType();
            myClasses = gson.fromJson(myClassesJSON, type);

            if (myClasses != null) {
                gson = new Gson();
                activeClass = gson.toJson(myClasses.get(0));
                sharedPreferencesManager.setActiveClass(activeClass);
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("View Attendance Records");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("You're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                errorLayoutButton.setText("Find my school");
                errorLayoutButton.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            Boolean activeClassExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Class>() {}.getType();
            Class activeClassModel = gson.fromJson(activeClass, type);

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
                    if (myClasses.size() > 1) {
                        gson = new Gson();
                        activeClass = gson.toJson(myClasses.get(0));
                        sharedPreferencesManager.setActiveClass(activeClass);
                    }
                }
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("View Attendance Records");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("You're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                errorLayoutButton.setText("Find my school");
                errorLayoutButton.setVisibility(View.VISIBLE);
                return;
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Class>() {}.getType();
        Class activeClassModel = gson.fromJson(activeClass, type);
        activeClass = activeClassModel.getID();
        className = activeClassModel.getClassName();

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

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
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
