package com.celerii.celerii.Activities.Search.Teacher;

import android.support.design.widget.TabLayout;
//import com.google.android.material.tabs.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.celerii.celerii.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    TabLayout tabLayout;
    static String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        Bundle bundle = getIntent().getExtras();
        query = bundle.getString("Query");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(query);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SearchResultsActivity.ViewPagerAdapter adapter = new SearchResultsActivity.ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFrag(new SearchResultsAllFragment(), "All");
        adapter.addFrag(new SearchResultsSchoolFragment(), "Schools");
        adapter.addFrag(new SearchResultsStudentFragment(), "Students");
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
            Fragment fragment = mFragmentList.get(position);
            Bundle args = new Bundle();
            args.putString("Query", query);
            fragment.setArguments(args);
            return fragment;
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
//            return null;
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
}
