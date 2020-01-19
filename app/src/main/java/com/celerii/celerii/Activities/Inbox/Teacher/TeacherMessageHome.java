package com.celerii.celerii.Activities.Inbox.Teacher;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.celerii.celerii.R;

import java.util.ArrayList;
import java.util.List;

public class TeacherMessageHome extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_parents_white_24dp,
            R.drawable.ic_colleagues_white_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_message_home);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Parents");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);
        //setupTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle("Parents");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Colleagues");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Schools");
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

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        TeacherMessageHome.ViewPagerAdapter adapter = new TeacherMessageHome.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TeacherParentMessageList(), "Parents");
        adapter.addFrag(new TeacherTeacherMessageList(), "Colleagues");
        adapter.addFrag(new TeacherSchoolMessageList(), "Schools");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
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
}
