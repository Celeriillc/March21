package com.celerii.celerii.Activities.Delete;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.celerii.celerii.Activities.Search.Teacher.SearchResultsSchoolFragment;
import com.celerii.celerii.Activities.Search.Teacher.SearchResultsStudentFragment;
import com.celerii.celerii.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsTeacherActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results_teacher);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search term"); //Todo: Make dynamic
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
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
        SearchResultsTeacherActivity.ViewPagerAdapter adapter = new SearchResultsTeacherActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SearchResultsAllFragment(), "All");
        adapter.addFrag(new SearchResultsSchoolFragment(), "Schools");
        adapter.addFrag(new SearchResultsTeacherFragment(), "Teachers");
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
//            return null;
            return mFragmentTitleList.get(position);
        }

    }
}
