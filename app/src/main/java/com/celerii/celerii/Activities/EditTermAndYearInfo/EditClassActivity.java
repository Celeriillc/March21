package com.celerii.celerii.Activities.EditTermAndYearInfo;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SelectClassAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.ParentCheckAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class EditClassActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;
    ParentCheckAttendanceSharedPreferences parentCheckAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;

    private ArrayList<Class> selectClassModelList;
    public RecyclerView recyclerView;
    public SelectClassAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String featureUseKey = "";
    String featureName = "Edit Class";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    Toolbar toolbar;
    Class selectedClass;
    String selectedClassString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        selectedClassString = bundle.getString("Class");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Class");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        selectClassModelList = new ArrayList<>();
        mAdapter = new SelectClassAdapter(selectClassModelList, selectedClassString, this);
        loadStaticData();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadStaticData();
                    }
                }
        );


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Selected Class"));
    }

    private void loadStaticData() {
        Gson gson = new Gson();
        ArrayList<Class> moreTeachersModelListLocal = new ArrayList<>();
        String myClassesJSON = sharedPreferencesManager.getMyClasses();
        Type type = new TypeToken<ArrayList<Class>>() {}.getType();
        moreTeachersModelListLocal = gson.fromJson(myClassesJSON, type);

        if (moreTeachersModelListLocal == null) {
            String message = "You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.";
            showDialogWithMessageAndDisconnect(message);
        } else {
            selectClassModelList.clear();
            mAdapter.notifyDataSetChanged();
            selectClassModelList.addAll(moreTeachersModelListLocal);
            selectClassModelList.add(0, new Class());
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectedClassString = intent.getStringExtra("SelectedClass");
        }
    };

    void showDialogWithMessageAndDisconnect(String messageString) {
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
                finish();
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            if (selectedClassString == null) {return false;}
            Intent intent = new Intent();

            intent.putExtra("Selected Class", selectedClassString);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (id == R.id.action_send){
            if (selectedClassString == null) {return false;}
            Intent intent = new Intent();

            intent.putExtra("Selected Class", selectedClassString);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
}
