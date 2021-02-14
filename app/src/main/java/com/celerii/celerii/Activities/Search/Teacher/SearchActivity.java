package com.celerii.celerii.Activities.Search.Teacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchHistoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SearchAnalyticsModel;
import com.celerii.celerii.models.SearchHistoryHeader;
import com.celerii.celerii.models.SearchHistoryRow;
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
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar mToolbar;
    private ArrayList<SearchHistoryRow> searchHistoryRowList;
    private ArrayList<String> connectedStudents;
    private SearchHistoryHeader searchHistoryHeader;
    public RecyclerView recyclerView;
    public SearchHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    boolean loading;

    String featureUseKey = "";
    String featureName = "Teacher Search Home (History)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        SearchView searchView;

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView = new SearchView(this);
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(15);
        searchView.setBackgroundColor(Color.TRANSPARENT);
        getSupportActionBar().setCustomView(searchView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Removes default cancel button (x)
        searchView.onActionViewExpanded();

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        //Removes margin between back action bar button and searchView
        LinearLayout searchEditFrame = (LinearLayout) searchView.findViewById(R.id.search_edit_frame);
        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 0;

        //Sets hint for search
        searchView.setQueryHint("Search for your school"); //Todo: Use @string resource

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        loading = false;
        searchHistoryHeader = new SearchHistoryHeader("Recent Search History", loading);
        searchHistoryRowList = new ArrayList<>();
        connectedStudents = new ArrayList<>();
        searchHistoryRowList.add(new SearchHistoryRow());
        loadDetailsFromFirebase();
        mAdapter = new SearchHistoryAdapter(searchHistoryRowList, connectedStudents, searchHistoryHeader, this);
        recyclerView.setAdapter(mAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {String mFirebaseUserID = mFirebaseUser.getUid();
                String date = Date.getDate();
                String sortableDate = Date.convertToSortableDate(date);
                String day = Date.getDay();
                String month = Date.getMonth();
                String year = Date.getYear();
                String day_month_year = day + "_" + month + "_" + year;
                String month_year = month + "_" + year;
                String platform = "Android";

                HashMap<String, Object> searchUpdateMap = new HashMap<>();
                String key = FirebaseDatabase.getInstance().getReference().child("Search Analytics").child("Feature Use Analytics").child(featureName).push().getKey();
                SearchAnalyticsModel searchAnalyticsModel = new SearchAnalyticsModel(mFirebaseUser.getUid(), "Teacher", date, sortableDate, day, month, year, key, platform, query);

                searchUpdateMap.put("Search Analytics/Search/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key, searchAnalyticsModel);

                searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key, searchAnalyticsModel);
                searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key, searchAnalyticsModel);

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(searchUpdateMap);

                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Query", query);
                bundle.putString("Search Key", key);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        loadDetailsFromFirebase();
        super.onResume();
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
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadDetailsFromFirebase(){
        Gson gson = new Gson();
        ArrayList<Student> moreParentsModelListLocal;
        String myChildrenJSON = sharedPreferencesManager.getMyChildren();
        Type type = new TypeToken<ArrayList<Student>>() {}.getType();
        moreParentsModelListLocal = gson.fromJson(myChildrenJSON, type);

        connectedStudents.clear();
        if (moreParentsModelListLocal != null) {
            if (moreParentsModelListLocal.size() > 0) {
                for (Student student: moreParentsModelListLocal) {
                    if (!connectedStudents.contains(student.getStudentID())) {
                        connectedStudents.add(student.getStudentID());
                    }
                }
            }
        }

        gson = new Gson();
        String classStudentsForTeacherJSON = sharedPreferencesManager.getClassStudentForTeacher();
        type = new TypeToken<HashMap<String, HashMap<String, Student>>>() {}.getType();
        HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = gson.fromJson(classStudentsForTeacherJSON, type);

        if (classStudentsForTeacherMap != null) {
            if (classStudentsForTeacherMap.size() > 0) {
                for (Map.Entry<String, HashMap<String, Student>> classMap: classStudentsForTeacherMap.entrySet()) {
                    String activeClassID = classMap.getKey();
                    HashMap<String, Student> classStudentMap = classStudentsForTeacherMap.get(activeClassID);
                    if (classStudentMap != null) {
                        if (classStudentMap.size() > 0) {
                            for (Map.Entry<String, Student> entry : classStudentMap.entrySet()) {
                                if (!connectedStudents.contains(entry.getKey())) {
                                    connectedStudents.add(entry.getKey());
                                }
                            }
                        }
                    }
                }
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference("MySearchHistory").child("Teachers").child(auth.getCurrentUser().getUid());
        mDatabaseReference.orderByChild("time").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    searchHistoryRowList.clear();
                    searchHistoryRowList.add(new SearchHistoryRow());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        SearchHistoryRow searchHistoryRow = postSnapshot.getValue(SearchHistoryRow.class);
                        searchHistoryRowList.add(1, searchHistoryRow);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
