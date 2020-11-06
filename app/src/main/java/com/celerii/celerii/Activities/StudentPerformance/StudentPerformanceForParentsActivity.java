package com.celerii.celerii.Activities.StudentPerformance;

import android.content.Context;
import android.content.Intent;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.Button;

import com.celerii.celerii.Activities.StudentPerformance.Current.CurrentFragment;
import com.celerii.celerii.Activities.StudentPerformance.History.HistoryFragment;
import com.celerii.celerii.R;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.materialdrawer.AccountHeader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentPerformanceForParentsActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Button button;
    AccountHeader headerResult;
    Toolbar toolbar;
    TabLayout tabLayout;
    String activeStudent = "";
    String activeStudentName = "";
    String parentActivity;
    Student activeStudentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_performance_for_parents);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        activeStudent = b.getString("Child ID");
        parentActivity = b.getString("parentActivity");

        if (activeStudent != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Student>() {}.getType();
            activeStudentModel = gson.fromJson(activeStudent, type);
            activeStudentName = activeStudentModel.getFirstName();
        } else {
            activeStudentName = "Child";
        }

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(activeStudentName.trim()  + "'s Performance History"); //Replace
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle(activeStudentName + "'s Performance History");
                        break;
                    case 1:
                        getSupportActionBar().setTitle(activeStudentName + "'s Current Performance");
                        break;
                    case 2:
                        getSupportActionBar().setTitle(activeStudentName + "'s Performance Prediction");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public String getData(){
        return activeStudent;
    }

    private void setupViewPager(ViewPager viewPager) {
        StudentPerformanceForParentsActivity.ViewPagerAdapter adapter = new StudentPerformanceForParentsActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HistoryFragment(), "History");
        adapter.addFrag(new CurrentFragment(), "Current");
        adapter.addFrag(new FutureFragment(), "Prediction");
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return null;
            return mFragmentTitleList.get(position);
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

    public void updateBadges() {
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentModel.getStudentID() + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentModel.getStudentID() + "/More/Status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentModel.getStudentID() + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentModel.getStudentID() + "/More/Status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
