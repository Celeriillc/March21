package com.celerii.celerii.Activities.Home.Teacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.celerii.celerii.Activities.Inbox.InboxFragment;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class TeacherMainActivityTwo extends AppCompatActivity {
    Context context = this;

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    AHBottomNavigation bottomNavigation;
    FragmentTransaction mFragmentTransaction;
    Fragment frag1, frag2, frag3, frag4, frag5;
    Toolbar toolbar;
//    CustomToast customToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_two);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser == null){
            sharedPreferencesManager.clear();
            Intent I = new Intent(TeacherMainActivityTwo.this, IntroSlider.class);
            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
            startActivity(I);
            finish();
            return;
        }

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Class Home", R.drawable.ic_home_white_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Class Feed", R.drawable.ic_newspaper, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Inbox", R.drawable.ic_chat_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications_black_24dp, R.color.colorPrimary);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("More", R.drawable.ic_more_black_24dp, R.color.colorPrimary);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryPurple));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.colorDeepGray));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setCurrentItem(0);
        loadBadgesFromFirebase();
        initFCM();
        bottomNavigation.setUseElevation(false);

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        frag1 = new TeacherHomeClass();
        frag2 = new TeacherHomeClassFeed();
        frag3 = new InboxFragment();
        frag4 = new TeacherHomeNotification();
        frag5 = new MoreTeacherFragment();

        bottomNavigation.setNotification("", 0);
        mFragmentTransaction.replace(R.id.frame_fragmentholder, frag1);
        mFragmentTransaction.commit();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        bottomNavigation.setNotification("", 0);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag1).commit();
                        return true;
                    case 1:
                        bottomNavigation.setNotification("", 1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag2).commit();
                        return true;
                    case 2:
                        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        Map<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
                        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/General/" + mFirebaseUser.getUid() + "/Inbox", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 2);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag3).commit();
                        return true;
                    case 3:
                        bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        bottomNavBadgeMap = new HashMap<String, Object>();
                        notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Notifications", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 3);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag4).commit();
                        return true;
                    case 4:
                        bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        bottomNavBadgeMap = new HashMap<String, Object>();
                        notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/More", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 4);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag5).commit();
                        return true;
                }
                return false;
            }
        });
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
            startActivity(new Intent(TeacherMainActivityTwo.this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBadgesFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("General").child(mFirebaseUser.getUid()).child("Inbox");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (notificationBadgeModel.getStatus()){
                        CustomToast.blueBackgroundToast(getBaseContext(), "New message in your inbox.");
                        AHNotification notification = new AHNotification.Builder()
                                .setText(" ")
                                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .build();
                        bottomNavigation.setNotification(notification, 2);
                    } else {
                        bottomNavigation.setNotification("", 2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Teachers").child(mFirebaseUser.getUid()).child("Notifications");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (notificationBadgeModel.getStatus()){
                        CustomToast.blueBackgroundToast(getBaseContext(), "New notification.");
                        AHNotification notification = new AHNotification.Builder()
                                .setText(" ")
                                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .build();
                        bottomNavigation.setNotification(notification, 3);
                    } else {
                        bottomNavigation.setNotification("", 3);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Teachers").child(mFirebaseUser.getUid()).child("More");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (notificationBadgeModel.getStatus()){
                        AHNotification notification = new AHNotification.Builder()
                                .setText(" ")
                                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .build();
                        bottomNavigation.setNotification(notification, 4);
                    } else {
                        bottomNavigation.setNotification("", 4);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void toastReserve(){
//                        LayoutInflater inflater = getLayoutInflater();
//                        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
//
//                        TextView text = (TextView) layout.findViewById(R.id.text);
//                        text.setText("New Class Story");
//                        double actionBarHeight;
//                        TypedValue tv = new TypedValue();
//                        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
//                        {
//                            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//                        } else {
//                            actionBarHeight = 50;
//                        }
//                        Toast toast = new Toast(getApplicationContext());
//                        toast.setGravity(Gravity.TOP, 0, (int)actionBarHeight + 20);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(layout);
//                        toast.show();
    }

    @Override
    protected void onStop () {
        super.onStop();
        try {
            CustomToast.cancelToast();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentItem = bottomNavigation.getCurrentItem();
        if (currentItem == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag1).commit();
        } else if (currentItem == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag2).commit();
        } else if (currentItem == 2){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag3).commit();
        } else if (currentItem == 3){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag4).commit();
        } else if (currentItem == 4){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag5).commit();
        }
        UpdateDataFromFirebase.populateEssentials(this);
    }

    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String deviceID = FirebaseInstanceId.getInstance().getId();
        reference.child("UserRoles").child(mFirebaseUser.getUid()).child("Tokens").child(deviceID).setValue(token);
    }
}
