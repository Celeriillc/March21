package com.celerii.celerii.Activities.StudentAttendance;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.celerii.celerii.adapters.TeacherTakeAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AttendanceStatusModel;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.NotificationModel;
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
import java.util.Map;

public class TeacherTakeAttendanceActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private ArrayList<TeacherAttendanceRow> teacherAttendanceRowList;
    private HashMap<String, ArrayList<String>> studentParentList = new HashMap<String, ArrayList<String>>();
    private TeacherAttendanceHeader teacherAttendanceHeader;
    public RecyclerView recyclerView;
    public TeacherTakeAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    boolean connected = true;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    String activeClass = "";
    String myName, className, subject, term, date, year, month, day, month_year, term_year, subject_term_year, year_month_day, dateForAdapter, sortableDate,
            teacherName, teacherID, schoolID;

    String childName;
    String childImageURL;
    Integer maleCount = 0, femaleCount = 0, studentCount = 0;

    String featureUseKey = "";
    String featureName = "Teacher Take Attendance";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_take_attendance);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
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
                getSupportActionBar().setTitle("Take Class Attendance");
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
                getSupportActionBar().setTitle("Take Class Attendance");
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

        teacherName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        teacherID = sharedPreferencesManager.getMyUserID();

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

        Gson gson = new Gson();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
        String myClassesStudentsParentsJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsAndParentsModelList = gson.fromJson(myClassesStudentsParentsJSON, type);

        if (classesStudentsAndParentsModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayoutText.setText(Html.fromHtml("Your classes have no students or you're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
            errorLayoutButton.setText("Find my school");
            errorLayoutButton.setVisibility(View.VISIBLE);
        } else {
            studentParentList.clear();
            for (ClassesStudentsAndParentsModel classesStudentsAndParentsModel: classesStudentsAndParentsModelList) {
                String studentID = classesStudentsAndParentsModel.getStudentID();
                String parentID = classesStudentsAndParentsModel.getParentID();

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
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            showDialogWithMessage("Internet is down, check your connection and try again");
            return;
        }

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
        newAttendance.put("AttendanceClass/" + activeClass + "/" + pushID, teacherAttendanceHeader);
        for (int i = 0; i < teacherAttendanceRowList.size(); i++) {
            if (!teacherAttendanceRowList.get(i).getStudentID().equals("")) {
                newAttendance.put("AttendanceClass-Students/" + activeClass + "/" + pushID + "/Students/" + teacherAttendanceRowList.get(i).getStudentID(),
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
                newAttendance.put("AttandenceStudent/" + teacherAttendanceRowList.get(i).getStudentID() + "/" + pushID, studentAttendance);

                String studentID = teacherAttendanceRowList.get(i).getStudentID();
                ArrayList<String> parentIDList = studentParentList.get(studentID);
                if (parentIDList != null) {
                    for (int j = 0; j < parentIDList.size(); j++) {
                        String parentID = parentIDList.get(j);
                        NotificationModel notificationModel = new NotificationModel(auth.getCurrentUser().getUid(), parentID, "Parent", sharedPreferencesManager.getActiveAccount(), date, sortableDate, pushID, "NewAttendancePost", teacherAttendanceRowList.get(i).getImageURL(), teacherAttendanceRowList.get(i).getStudentID(), teacherAttendanceRowList.get(i).getName(), false);
                        newAttendance.put("AttendanceParentNotification/" + parentID + "/" + studentID + "/status", true);
                        newAttendance.put("AttendanceParentNotification/" + parentID + "/" + studentID + "/" + pushID + "/status", true);
                        newAttendance.put("AttendanceParentRecipients/" + pushID + "/" + parentID, true);
                        newAttendance.put("NotificationParent/" + parentID + "/" + pushID, notificationModel);
                        newAttendance.put("Notification Badges/Parents/" + parentID + "/Notifications/status", true);
                        newAttendance.put("Notification Badges/Parents/" + parentID + "/More/status", true);
                    }
                }
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
}

