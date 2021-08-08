package com.celerii.celerii.Activities.StudentAttendance;

import android.content.Context;
import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ParentAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.ParentCheckAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ParentAttendanceHeader;
import com.celerii.celerii.models.ParentAttendanceRow;
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
import java.util.Calendar;
import java.util.HashMap;

public class ParentAttendanceActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    ParentCheckAttendanceSharedPreferences parentCheckAttendanceSharedPreferences;

    Toolbar toolbar;
    private ArrayList<ParentAttendanceRow> parentAttendanceRowList;
    private ParentAttendanceHeader parentAttendanceHeader;
    public RecyclerView recyclerView;
    public ParentAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String mMonth;
    int thisMonth;
    int thisYear;
    String subject, term, month, year, month_year, term_year, subject_term_year;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    String childID, classID, parentActivity;
    String childsFirstName;
    String searchTerm;
    String className, school, teacher;
    int isNewcounter = 0;

    String featureUseKey = "";
    String featureName = "Parent Attendance Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_attendance);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        String activeKid = b.getString("Child ID");
        parentActivity = b.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }

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
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        if (activeKid == null) {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Gson gson = new Gson();
                ArrayList<Student> myChildren = new ArrayList<>();
                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                Type type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                myChildren = gson.fromJson(myChildrenJSON, type);

                if (myChildren != null) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeKid = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeKid);
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Attendance");
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return;
                    }
                } else {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Attendance");
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Attendance");
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("We couldn't find this student's account"));
                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeKid, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
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
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeKid = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeKid);
                        }
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Attendance");
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeKidModel = gson.fromJson(activeKid, type);
        childID = activeKidModel.getStudentID();
        childsFirstName = activeKidModel.getFirstName();

        Calendar calendar = Calendar.getInstance();
        thisYear = calendar.get(Calendar.YEAR);
        thisMonth = calendar.get(Calendar.MONTH);
        mMonth = Month.Month(thisMonth);
        subject = "General";

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(childsFirstName.trim()  + "'s Attendance");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        parentAttendanceHeader = new ParentAttendanceHeader("July", "2018", subject);
        parentAttendanceRowList = new ArrayList<>();
        mAdapter = new ParentAttendanceRowAdapter(parentAttendanceRowList, parentAttendanceHeader, childsFirstName.trim(), parentActivity, this, this);
        recyclerView.setAdapter(mAdapter);
        loadHeader();
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

    private void loadHeader(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        updateBadges();
        subject = parentCheckAttendanceSharedPreferences.getSubject();
        year = Date.getYear();
        month = Date.getMonth();
        term = Term.getTermShort();
        month_year = month + "_" + year;
        term_year = term + "_" + year;
        subject_term_year = subject + "_" + term + "_" + year;

        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Class").child(childID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        classID = postSnapshot.getKey();
                        parentAttendanceHeader.setClassID(classID);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        parentAttendanceHeader.setStudentID(childID);
        parentAttendanceHeader.setSubject(subject);
        parentAttendanceHeader.setYear(year);
        parentAttendanceHeader.setTerm(term);
    }

    private void loadFromFirebase() {
        isNewcounter = 0;

        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceStudent").child(childID);
        mDatabaseReference.orderByChild("subject_term_year").equalTo(subject_term_year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    parentAttendanceRowList.clear();
                    mAdapter.notifyDataSetChanged();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ParentAttendanceRow parentAttendanceRow = postSnapshot.getValue(ParentAttendanceRow.class);
                        parentAttendanceRow.setKey(postSnapshot.getKey());
                        parentAttendanceRow.setStudentID(childID);
                        parentAttendanceHeader.setClassID(parentAttendanceRow.getClassID());

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(parentAttendanceRow.getClassID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    parentAttendanceRow.setClassName(classInstance.getClassName());
                                    parentAttendanceRowList.add(parentAttendanceRow);
                                }

                                if (childrenCount == parentAttendanceRowList.size()){
                                    for (final ParentAttendanceRow parentAttendanceRow: parentAttendanceRowList) {
                                        String key = parentAttendanceRow.getKey();
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceParentNotification").child(mFirebaseUser.getUid()).child(childID).child(key).child("status");
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    boolean isNew = dataSnapshot.getValue(boolean.class);
                                                    if (isNew) {
                                                        parentAttendanceRow.setNew(true);
                                                    } else {
                                                        parentAttendanceRow.setNew(false);
                                                    }
                                                } else {
                                                    parentAttendanceRow.setNew(false);
                                                }

                                                isNewcounter++;

                                                if (isNewcounter == parentAttendanceRowList.size()) {
                                                    if (!parentAttendanceRowList.get(0).getClassID().equals("")) {
                                                        parentAttendanceRowList.add(0, new ParentAttendanceRow());
                                                    }
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    mAdapter.notifyDataSetChanged();
                                                    errorLayout.setVisibility(View.GONE);
                                                    progressLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    parentAttendanceRowList.clear();
                    parentAttendanceRowList.add(0, new ParentAttendanceRow());
                    mySwipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
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
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + childID + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + childID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("AttendanceParentNotification/" + mFirebaseUser.getUid() + "/" + childID + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + childID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_attendance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
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
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                subject = data.getStringExtra("Selected Subject");
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setSubject(subject);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                term = data.getStringExtra("Selected Term");
                term_year = term + "_" + year;
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setTerm(term);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                year = data.getStringExtra("Selected Year");
                term_year = term + "_" + year;
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setYear(year);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
    }

    @Override
    public void onResume() {
        UpdateDataFromFirebase.populateEssentials(this);
        super.onResume();
    }
}
