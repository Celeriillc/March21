package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.celerii.celerii.Activities.Home.Parent.ParentHomeClassFeed;
import com.celerii.celerii.Activities.Home.Parent.ParentHomeNotification;
import com.celerii.celerii.Activities.Home.Teacher.TeacherCreateClassPostActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherHomeClass;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Inbox.Teacher.TeacherMessageHome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

public class TeacherMainActivity extends AppCompatActivity {

    Drawer result = null;
    Button button;
    AccountHeader headerResult;
    Toolbar toolbar;
    TabLayout tabLayout;
    FloatingActionButton fab;
    private int[] tabIcons = {
            R.drawable.ic_home_white_24dp,
            R.drawable.ic_newspaper,
            R.drawable.ic_assignment_black_24dp,
            R.drawable.ic_notifications_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

//        Intent I = new Intent(TeacherMainActivity.this, TeacherSetupActivityOne.class);
//        startActivity(I);
//        finish();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class Home"); //TODO; Make dynamic

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.materialdesignwallpaperbeach)
                .addProfiles(
                        new ProfileDrawerItem().withName("Esther Oriabure").withEmail("Toulouse").withIcon(getResources().getDrawable(R.drawable.hueywhite))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        //return false;
                        if (profile.getIdentifier() == 2000)
                        {
//                            Toast.makeText(TeacherMainActivity.this, "Okay Manage", Toast.LENGTH_SHORT).show();
                            result.closeDrawer();
                        }
                        return true;
                    }
                })
                .build();

        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(R.drawable.ic_home_black_24dp);
        PrimaryDrawerItem messages = new PrimaryDrawerItem().withIdentifier(2).withName("Messages").withIcon(R.drawable.ic_chat_black_24dp);
        PrimaryDrawerItem attendance = new PrimaryDrawerItem().withIdentifier(3).withName("Attendance").withIcon(R.drawable.ic_people_black_24dp);
        PrimaryDrawerItem timetable = new PrimaryDrawerItem().withIdentifier(4).withName("Timetable").withIcon(R.drawable.ic_timetable);
        PrimaryDrawerItem enterResult = new PrimaryDrawerItem().withIdentifier(5).withName("Enter Student Results").withIcon(R.drawable.ic_timetable);
        PrimaryDrawerItem personalPerformance = new PrimaryDrawerItem().withIdentifier(6).withName("Personal Performance").withIcon(R.drawable.ic_insert_chart_black_24dp);
        PrimaryDrawerItem studentPerformance = new PrimaryDrawerItem().withIdentifier(7).withName("Student Performance").withIcon(R.drawable.ic_insert_chart_black_24dp);
        PrimaryDrawerItem events = new PrimaryDrawerItem().withIdentifier(8).withName("Events").withIcon(R.drawable.ic_event_black_24dp);
        PrimaryDrawerItem newsletter = new PrimaryDrawerItem().withIdentifier(9).withName("Newsletter").withIcon(R.drawable.ic_newsletter);
        DividerDrawerItem divider = new DividerDrawerItem();
        PrimaryDrawerItem profile = new PrimaryDrawerItem().withIdentifier(10).withName("Profile").withIcon(R.drawable.ic_account_circle);
        PrimaryDrawerItem classProfile = new PrimaryDrawerItem().withIdentifier(11).withName("Class Profile").withIcon(R.drawable.ic_account_circle);
        PrimaryDrawerItem options = new PrimaryDrawerItem().withIdentifier(12).withName("Options").withIcon(R.drawable.ic_settings_black_24dp);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar).withAccountHeader(headerResult)
                .addDrawerItems(
                        home,
                        messages,
                        attendance,
                        timetable,
                        enterResult,
                        personalPerformance,
                        studentPerformance,
                        events,
                        newsletter,
                        divider,
                        profile,
                        classProfile,
                        options
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.getIdentifier() == 1){}
                        else if (drawerItem.getIdentifier() == 2)
                        {
                            result.closeDrawer();
                            Intent I = new Intent(TeacherMainActivity.this, TeacherMessageHome.class);
                            startActivity(I);
                            overridePendingTransition(0, 0);
                        }
                        return  true;
                    }
                })
                .build();



        messages.withBadge("2");
        result.updateItem(messages);
        events.withBadge("3");
        result.updateItem(events);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(TeacherMainActivity.this, "Shit bro", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle("Class Home");
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add_white_24dp));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Toast.makeText(TeacherMainActivity.this, "Shit bro", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Class Feed");
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_home_white_24dp));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent I = new Intent(TeacherMainActivity.this, TeacherCreateClassPostActivity.class);
                                startActivity(I);

                            }
                        });
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Assignments");
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add_white_24dp));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent I = new Intent(TeacherMainActivity.this, TeacherCreateAssignmentPostActivity.class);
                                startActivity(I);
                            }
                        });
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Notifications");
                        fab.setVisibility(View.INVISIBLE);
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
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search){
            startActivity(new Intent(TeacherMainActivity.this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        TeacherMainActivity.ViewPagerAdapter adapter = new TeacherMainActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TeacherHomeClass(), "Class Home");
        adapter.addFrag(new ParentHomeClassFeed(), "Class Feed");
        adapter.addFrag(new ParentHomeAssignment(), "Assignments");
        adapter.addFrag(new ParentHomeNotification(), "Notification");
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
            return null;
//            return mFragmentTitleList.get(position);
        }

    }
}
