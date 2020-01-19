package com.celerii.celerii.Activities.StudentPerformance;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.celerii.celerii.Activities.StudentPerformance.Current.CurrentFragment;
import com.celerii.celerii.Activities.StudentPerformance.History.HistoryFragment;
import com.celerii.celerii.R;
import com.mikepenz.materialdrawer.AccountHeader;

import java.util.ArrayList;
import java.util.List;

public class StudentPerformanceForParentsActivity extends AppCompatActivity {

    Button button;
    AccountHeader headerResult;
    Toolbar toolbar;
    TabLayout tabLayout;
    String activeStudent = "";
    String activeStudentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_performance_for_parents);

        Bundle b = getIntent().getExtras();
        activeStudent = b.getString("Child ID");
        if (activeStudent != null){
            activeStudentName = activeStudent.split(" ")[1];
        } else {
            activeStudentName = "Child";
        }

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(activeStudentName + "'s Performance History"); //Replace
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
