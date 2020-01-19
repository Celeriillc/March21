package com.celerii.celerii.Activities.EditTermAndYearInfo;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SelectYearAdapter;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.ParentCheckAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditYearActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;
    ParentCheckAttendanceSharedPreferences parentCheckAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    private ArrayList<String> selectYearModelList;
    public RecyclerView recyclerView;
    public SelectYearAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Toolbar toolbar;
    String selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_year);

        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        selectedYear = bundle.getString("Year");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Year");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        selectYearModelList = new ArrayList<>();
        mAdapter = new SelectYearAdapter(selectYearModelList, selectedYear, this);
        loadStaticData();
        recyclerView.setAdapter(mAdapter);

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
                        loadStaticData();
                    }
                }
        );


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Selected Year"));
    }

    private void loadStaticData() {
        int yearUpper = Integer.valueOf(Date.getYear());
        int yearLower = yearUpper - 100;
        for (int i = yearUpper; i >= yearLower; i--){
            selectYearModelList.add(Integer.toString(i));
        }
        selectYearModelList.add(0, Integer.toString(yearUpper));
        mySwipeRefreshLayout.setRefreshing(false);
        progressLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectedYear = intent.getStringExtra("SelectedYear");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            if (selectedYear == null) {return false;}
            Intent intent = new Intent();

            intent.putExtra("Selected Year", selectedYear);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (id == R.id.action_send){
            if (selectedYear == null) {return false;}
            Intent intent = new Intent();

            intent.putExtra("Selected Year", selectedYear);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
