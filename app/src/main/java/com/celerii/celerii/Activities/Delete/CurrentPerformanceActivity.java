package com.celerii.celerii.Activities.Delete;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordClass;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentPerformanceActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;
    ScrollView scrollView;

    Toolbar toolbar;
    View current, classAverage;
    String activeSubject = "", activeStudent = "", activeClass = "";

    TextView currentLabel, currentClass, currentMaxObtainable, currentTerm, currentScore, currentUpFrom, currentDownFrom, currentYear;
    TextView classAverageLabel, classAverageClass, classAverageMaxObtainable, classAverageTerm, classAverageScore, classAverageUpFrom,
            classAverageDownFrom, classAverageYear;

    String year, term, currentClassString, currentDateString, currentTimeString, currentTermString, currentScoreString, currentUpFromString,
            currentDownFromString, currentYearString, recordKey;
    String classAverageClassString, classAverageDateString, classAverageTimeString, classAverageTermString, classAverageScoreString, classAverageUpFromString,
            classAverageDownFromString, classAverageYearString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_performance);

        Bundle bundle = getIntent().getExtras();
        activeStudent = bundle.getString("Active Student").split(" ")[0];
        activeClass = bundle.getString("Class");
        activeSubject = bundle.getString("Subject");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);
        scrollView = (ScrollView) findViewById(R.id.scrollview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(activeSubject);

        current = findViewById(R.id.current);
        classAverage = findViewById(R.id.classaverage);

        scrollView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        currentLabel = (TextView) current.findViewById(R.id.label);
        currentClass = (TextView) current.findViewById(R.id.classname);
        currentMaxObtainable = (TextView) current.findViewById(R.id.maxobtainable);
        currentTerm = (TextView) current.findViewById(R.id.term);
        currentYear = (TextView) current.findViewById(R.id.year);
        currentScore = (TextView) current.findViewById(R.id.score);

        classAverageLabel = (TextView) classAverage.findViewById(R.id.label);
        classAverageClass = (TextView) classAverage.findViewById(R.id.classname);
        classAverageMaxObtainable = (TextView) classAverage.findViewById(R.id.maxobtainable);
        classAverageTerm = (TextView) classAverage.findViewById(R.id.term);
        classAverageYear = (TextView) classAverage.findViewById(R.id.year);
        classAverageScore = (TextView) classAverage.findViewById(R.id.score);

        currentLabel.setText("Current Score");
        classAverageLabel.setText("Class Average");

        loadFromFirebase();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );
    }

    void loadFromFirebase() {
        year = Date.getYear();
        term = Term.getTermShort();
        currentYear.setText(year);
        currentTerm.setText(Term.Term(term));
        classAverageYear.setText(year);
        classAverageTerm.setText(Term.Term(term));
        String subject_year_term = activeSubject + "_" + year + "_" + term;
        String class_subject_year_term = activeClass + "_" + activeSubject + "_" + year + "_" + term;

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(activeStudent).child(class_subject_year_term);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent();
                if (dataSnapshot.exists()){
                    academicRecordStudent = dataSnapshot.getValue(AcademicRecordStudent.class);
                    currentMaxObtainable.setText(academicRecordStudent.getMaxObtainable() + "%");
                    currentScore.setText(String.valueOf(Double.valueOf(academicRecordStudent.getScore()).intValue()) + "%");
                    recordKey = dataSnapshot.getKey();

                    mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordClass").child(academicRecordStudent.getClassID()).child(recordKey);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                AcademicRecordClass academicRecordClass = dataSnapshot.getValue(AcademicRecordClass.class);
                                classAverageMaxObtainable.setText(academicRecordClass.getMaxObtainable() + "%");
                                classAverageScore.setText(String.valueOf(Double.valueOf(academicRecordClass.getClassAverage()).intValue()) + "%");

                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(academicRecordClass.getClassID());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                            String className = classInstance.getClassName();
                                            currentClass.setText(className);
                                            classAverageClass.setText(className);

                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            scrollView.setVisibility(View.VISIBLE);
                                            errorLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    currentMaxObtainable.setText("0");
                    currentClass.setText("Not Available");
                    currentScore.setText("0%");
                    classAverageMaxObtainable.setText("0");
                    classAverageClass.setText("Not Available");
                    classAverageScore.setText("0%");

                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
