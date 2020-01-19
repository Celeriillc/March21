package com.celerii.celerii.Activities.Inbox.Parent;

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

import com.celerii.celerii.R;

import java.util.ArrayList;
import java.util.List;

public class ParentMessageHome extends AppCompatActivity {

    //Drawer result = null;
    Button button;
    //AccountHeader headerResult;
    Toolbar toolbar;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_teacher_black_24dp,
            R.drawable.ic_school_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_message_home);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teachers"); //Replace

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);
//        setupTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle("Teachers");
                        break;
                    case 1:
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
        ParentMessageHome.ViewPagerAdapter adapter = new ParentMessageHome.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ParentTeacherMessageList(), "Teachers");
        adapter.addFrag(new ParentSchoolMessageList(), "Schools");
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
//            return null;
            return mFragmentTitleList.get(position);
        }

    }
}
