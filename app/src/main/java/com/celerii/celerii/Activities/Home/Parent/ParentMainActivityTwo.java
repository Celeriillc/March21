package com.celerii.celerii.Activities.Home.Parent;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.celerii.celerii.Activities.Utility.ApplicationLaunchActivity;
import com.celerii.celerii.BuildConfig;
import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Home.RemoteCampaignActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Inbox.InboxFragment;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.Activities.Settings.TutorialsActivity;
import com.celerii.celerii.Activities.Utility.HTMLViewerActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CheckDeletedState;
import com.celerii.celerii.helperClasses.CheckLoggedInState;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.LogoutProtocol;
import com.celerii.celerii.helperClasses.MyFirebaseInstanceIdService;
import com.celerii.celerii.helperClasses.NotificationReceiver;
import com.celerii.celerii.helperClasses.ServerDeviceTimeDifference;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.celerii.celerii.models.RemoteCampaign;
import com.celerii.celerii.models.Student;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ParentMainActivityTwo extends AppCompatActivity {
    Context context = this;

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseAuth.IdTokenListener authIDTokenListener;
    AHBottomNavigation bottomNavigation;
    FragmentTransaction mFragmentTransaction;
    Fragment frag1, frag2, frag3, frag4, active;
    AHBottomNavigationItem item1, item2, item3, item4;
    Toolbar toolbar;
    ImageView requestBadge;
    TextView toolbarTitle;
    String activeStudentID = "";
    Boolean isPendingRequest = false;

    //Onboard variables
    Dialog dialog;
    int[] layouts;
    ViewPager viewPager;
    LinearLayout dotsLayout;
    Button next;
    private TextView[] dots;
    MyViewPagerAdapter myViewPagerAdapter;
    LinearLayout onBoardingSearchBalloon;
    LinearLayout tutorialBalloon;
    Button dismissTutorialBalloon;

    AppUpdateManager appUpdateManager;
    public static final int REQUEST_CODE = 1234;

    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main_two);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbartitle);
        onBoardingSearchBalloon = (LinearLayout) findViewById(R.id.onboardingsearchballon);
        tutorialBalloon = (LinearLayout) findViewById(R.id.tutorialballoon);
        dismissTutorialBalloon = (Button) findViewById(R.id.dismisstutorialballoon);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Celerii Parent");
//        getSupportActionBar().setLogo(R.drawable.ic_celerii_logo_colored);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("Celerii Parent");
        String activeStudent = sharedPreferencesManager.getActiveKid();

        if (activeStudent == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeStudent = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeStudent);
                    gson = new Gson();
                    type = new TypeToken<Student>() {
                    }.getType();
                    Student activeStudentModel = gson.fromJson(activeStudent, type);
                    activeStudentID = activeStudentModel.getStudentID();
                }
            } else {
                activeStudentID = "";
            }
        }

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser == null){
            sharedPreferencesManager.clear();
            Intent I = new Intent(ParentMainActivityTwo.this, IntroSlider.class);
            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
            startActivity(I);
            finishAffinity();
            return;
        }

        // Create items
        item1 = new AHBottomNavigationItem("Class Feed", R.drawable.ic_file_text, R.color.colorPrimary);
        item2 = new AHBottomNavigationItem("Inbox", R.drawable.ic_message_circle, R.color.colorPrimary);
        item3 = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications, R.color.colorPrimary);
        item4 = new AHBottomNavigationItem("More", R.drawable.ic_more_horizontal, R.color.colorPrimary);

        // Set icons for items
        item1.setDrawable(R.drawable.ic_file_text_filled);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryPurple));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.black));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        loadBadgesFromFirebase();
        initFCM();
        onBoardFirebaseCheck();
        onBoardingSearchBalloonCheck();
        tutorialFirebaseCheck();
//        checkServerForApplicationUpdates();
        remoteCampaign();
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setUseElevation(false);

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        frag1 = new ParentHomeClassFeed();
        frag2 = new InboxFragment();
        frag3 = new ParentHomeNotification();
        frag4 = new MoreParentFragment();

//        mFragmentTransaction.replace(R.id.frame_fragmentholder, frag1);
//        mFragmentTransaction.commit();

//        mFragmentTransaction.add(R.id.frame_fragmentholder, frag4).hide(frag4);
//        mFragmentTransaction.add(R.id.frame_fragmentholder, frag3).hide(frag3);
//        mFragmentTransaction.add(R.id.frame_fragmentholder, frag2).hide(frag2);
//        mFragmentTransaction.add(R.id.frame_fragmentholder, frag1);
//        mFragmentTransaction.commit();
//        active = frag1;

        Bundle fromNotificationBundle = getIntent().getExtras();
        if (fromNotificationBundle != null) {
            String fragNumber = fromNotificationBundle.getString("Fragment Int");
            if (fragNumber.equals("1")) {
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag4).hide(frag4);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag3).hide(frag3);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag1).hide(frag1);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag2);
                mFragmentTransaction.commit();
                bottomNavigation.setCurrentItem(1);
                bottomNavigation.setNotification("", 1);
                active = frag2;
                setIconDefaults();
                item2.setDrawable(R.drawable.ic_message_circle_filled);
            } else if (fragNumber.equals("2")) {
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag4).hide(frag4);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag2).hide(frag2);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag1).hide(frag1);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag3);
                mFragmentTransaction.commit();
                bottomNavigation.setCurrentItem(2);
                bottomNavigation.setNotification("", 2);
                active = frag3;
                setIconDefaults();
                item3.setDrawable(R.drawable.ic_notifications_filled);
            } else if (fragNumber.equals("3")) {
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag3).hide(frag3);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag2).hide(frag2);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag1).hide(frag1);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag4);
                mFragmentTransaction.commit();
                bottomNavigation.setCurrentItem(3);
                bottomNavigation.setNotification("", 3);
                active = frag4;
                setIconDefaults();
            } else {
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag4).hide(frag4);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag3).hide(frag3);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag2).hide(frag2);
                mFragmentTransaction.add(R.id.frame_fragmentholder, frag1);
                mFragmentTransaction.commit();
                bottomNavigation.setCurrentItem(0);
                bottomNavigation.setNotification("", 0);
                active = frag1;
                setIconDefaults();
                item1.setDrawable(R.drawable.ic_file_text_filled);
            }
            sharedPreferencesManager.setActiveAccount("Parent");
            mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
            mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue("Parent");
        } else {
            mFragmentTransaction.add(R.id.frame_fragmentholder, frag4).hide(frag4);
            mFragmentTransaction.add(R.id.frame_fragmentholder, frag3).hide(frag3);
            mFragmentTransaction.add(R.id.frame_fragmentholder, frag2).hide(frag2);
            mFragmentTransaction.add(R.id.frame_fragmentholder, frag1);
            mFragmentTransaction.commit();
            bottomNavigation.setCurrentItem(0);
            bottomNavigation.setNotification("", 0);
            active = frag1;
            setIconDefaults();
            item1.setDrawable(R.drawable.ic_file_text_filled);
        }

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        Map<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
                        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/ClassStory", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 0);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag1).commit();
                        getSupportFragmentManager().beginTransaction().hide(active).show(frag1).commit();
                        active = frag1;
                        setIconDefaults();
                        item1.setDrawable(R.drawable.ic_file_text_filled);
                        return true;
                    case 1:
                        bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        bottomNavBadgeMap = new HashMap<String, Object>();
                        notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/General/" + mFirebaseUser.getUid() + "/Inbox", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 1);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag2).commit();
                        getSupportFragmentManager().beginTransaction().hide(active).show(frag2).commit();
                        active = frag2;
                        setIconDefaults();
                        item2.setDrawable(R.drawable.ic_message_circle_filled);
                        return true;
                    case 2:
                        bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        bottomNavBadgeMap = new HashMap<String, Object>();
                        notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 2);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag3).commit();
                        getSupportFragmentManager().beginTransaction().hide(active).show(frag3).commit();
                        active = frag3;
                        setIconDefaults();
                        item3.setDrawable(R.drawable.ic_notifications_filled);
                        return true;
                    case 3:
                        bottomNavBadgeRef = mFirebaseDatabase.getReference();
                        bottomNavBadgeMap = new HashMap<String, Object>();
                        notificationBadgeModel = new NotificationBadgeModel(false, 0);
                        bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/More", notificationBadgeModel);
                        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
                        bottomNavigation.setNotification("", 3);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag4).commit();
                        getSupportFragmentManager().beginTransaction().hide(active).show(frag4).commit();
                        active = frag4;
                        setIconDefaults();
                        return true;
                }
                return false;
            }
        });

        onBoardingSearchBalloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatePressed(onBoardingSearchBalloon);
                startActivity(new Intent(ParentMainActivityTwo.this, ParentSearchActivity.class));
            }
        });

        tutorialBalloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatePressed(tutorialBalloon);
                startActivity(new Intent(ParentMainActivityTwo.this, TutorialsActivity.class));
            }
        });

        dismissTutorialBalloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateGone(tutorialBalloon);
                tutorialBalloon.setVisibility(View.GONE);
            }
        });

        authIDTokenListener = new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser() == null) {
                    LogoutProtocol.goToIntro(context);
                }
            }
        };

        Thread t =  new Thread(new Runnable(){
            public void run() {
                MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
                myFirebaseInstanceIdService.onTokenRefresh();
            }});
        t.start();
    }

//    public void createNotification () {
//        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
//        Intent intent = new Intent(this, myBroadcastReceiver.getClass());
//        Intent intent = new Intent(this, MyBroadcastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                this.getApplicationContext(), 234324243, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
//        long currentTimeInt = System.currentTimeMillis();
//        alarmManager.set(AlarmManager.RTC_WAKEUP , System.currentTimeMillis() + (10000), pendingIntent) ;
//    }

    private void setIconDefaults() {
        item1.setDrawable(R.drawable.ic_file_text);
        item2.setDrawable(R.drawable.ic_message_circle);
        item3.setDrawable(R.drawable.ic_notifications);
        item4.setDrawable(R.drawable.ic_more_horizontal);
    }

    public AHBottomNavigation getData(){
        return bottomNavigation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_request);

        View actionView = menuItem.getActionView();
        requestBadge = (ImageView) actionView.findViewById(R.id.requestbadge);

        if (isPendingRequest) {
            requestBadge.setVisibility(View.VISIBLE);
        } else {
            requestBadge.setVisibility(View.GONE);
        }

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_request) {
//            new MyTask().execute();
            startActivity(new Intent(ParentMainActivityTwo.this, ParentsRequestActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            if (onBoardingSearchBalloon.getVisibility() == View.VISIBLE) {
                animateGone(onBoardingSearchBalloon);
            }

            startActivity(new Intent(ParentMainActivityTwo.this, ParentSearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Rect srcRect, dstRect;
        int r;
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        if (w > h){
            output = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);
            int left = (w - h) / 2;
            int right = left + h;
            srcRect = new Rect(left, 0, right, h);
            dstRect = new Rect(0, 0, h, h);
            r = h / 2;
        }else{
            output = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
            int top = (h - w)/2;
            int bottom = top + w;
            srcRect = new Rect(0, top, w, bottom);
            dstRect = new Rect(0, 0, w, w);
            r = w / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, "Channel_Test");

        @Override
        protected Void doInBackground(Void... voids) {
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent((Activity)context, CommentStoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("postKey", "opp");
            resultIntent.putExtras(bundle);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//            Intent acceptIntent = new Intent(context, NotificationReceiver.class);
//            acceptIntent.putExtra("message", "Accept");
//            acceptIntent.putExtra("notificationID", 0);
//            PendingIntent actionIntentAccept = PendingIntent.getBroadcast(context,
//                    0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//            Intent declineIntent = new Intent(context, NotificationReceiver.class);
//            declineIntent.putExtra("message", "Decline");
//            declineIntent.putExtra("notificationID", 0);
//            PendingIntent actionIntentDecline = PendingIntent.getBroadcast(context,
//                    0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                URL myURL = new URL("https://businessday.ng/wp-content/uploads/2019/09/Untitled-design-2019-09-26T163510.353.png");
                Bitmap bitmap = getCircleBitmap(BitmapFactory.decodeStream(myURL.openConnection().getInputStream()));

                builder.setSmallIcon(R.drawable.ic_celerii_logo_colored)
                        .setLargeIcon(bitmap)
                        .setColor(ContextCompat.getColor(context, R.color.colorSecondaryPurple))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSubText("Clara Ikubese")
                        .setContentTitle("Esther Oriabure")
                        .setContentText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3"))
                        .setOnlyAlertOnce(true)
//                        .addAction(R.mipmap.ic_launcher, "Accept", actionIntentAccept)
//                        .addAction(R.mipmap.ic_launcher, "Decline", actionIntentDecline)
                        .setContentIntent(resultPendingIntent);
            } catch (Exception e) {
                builder.setSmallIcon(R.drawable.ic_celerii_logo_colored)
                        .setColor(ContextCompat.getColor(context, R.color.colorSecondaryPurple))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSubText("Clara Ikubese")
                        .setContentTitle("Esther Oriabure")
                        .setContentText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3"))
                        .setOnlyAlertOnce(true)
//                        .addAction(R.mipmap.ic_launcher, "Accept", actionIntentAccept)
//                        .addAction(R.mipmap.ic_launcher, "Decline", actionIntentDecline)
                        .setContentIntent(resultPendingIntent);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("com.celerii.celerii");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "com.celerii.celerii",
                        "Celerii",
                        NotificationManager.IMPORTANCE_DEFAULT);

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel(channel);
                }
            }

            mNotificationManager.notify(0, builder.build());
            super.onPostExecute(aVoid);
        }
    }

    private void loadBadgesFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("ClassStory");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (notificationBadgeModel.getStatus()){
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            CustomToast.blueBackgroundToast(getBaseContext(), "New class story.");
                        }
                        AHNotification notification = new AHNotification.Builder()
                                .setText(" ")
                                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                                .setTextColor(ContextCompat.getColor(context, R.color.white))
                                .build();
                        bottomNavigation.setNotification(notification, 0);
                    } else {
                        bottomNavigation.setNotification("", 0);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("General").child(mFirebaseUser.getUid()).child("Inbox");
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
                        bottomNavigation.setNotification(notification, 1);
                    } else {
                        bottomNavigation.setNotification("", 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("Notifications");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (notificationBadgeModel.getStatus()){
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            CustomToast.blueBackgroundToast(getBaseContext(), "New notification.");
                        }
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

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child(activeStudentID).child("More");
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

        mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Recipients").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isPendingRequest = true;
                    if (requestBadge != null) {
                        requestBadge.setVisibility(View.VISIBLE);
                    }
                } else {
                    isPendingRequest = false;
                    if (requestBadge != null) {
                        requestBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addIdTokenListener(authIDTokenListener);
//        Analytics.loginAnalytics(context, mFirebaseUser.getUid(), "Parent");
//        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop () {
        super.onStop();
        try {
            CustomToast.cancelToast();
            auth.removeIdTokenListener(authIDTokenListener);
        } catch (Exception e) {

        }

        tutorialBalloon.setVisibility(View.GONE);
        onBoardingSearchBalloon.setVisibility(View.GONE);

//        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
//        String currentSessionLoginKey = sharedPreferencesManager.getCurrentLoginSessionKey();
//        String day_month_year = sharedPreferencesManager.getCurrentLoginSessionDayMonthYear();
//        String month_year = sharedPreferencesManager.getCurrentLoginSessionMonthYear();
//        String year = sharedPreferencesManager.getCurrentLoginSessionYear();
//        HashMap<String, Object> loginUpdateMap = new HashMap<>();
//        String mFirebaseUserID = mFirebaseUser.getUid();
//
//        loginUpdateMap.put("Analytics/User Login History/" + mFirebaseUserID + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/User Daily Login History/" + mFirebaseUserID + "/" + day_month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/User Monthly Login History/" + mFirebaseUserID + "/" + month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/User Yearly Login History/" + mFirebaseUserID + "/" + year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//
//        loginUpdateMap.put("Analytics/Login History/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/Daily Login History/" + day_month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/Monthly Login History/" + month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//        loginUpdateMap.put("Analytics/Yearly Login History/" + year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
//
//        DatabaseReference loginUpdateRef = FirebaseDatabase.getInstance().getReference();
//        loginUpdateRef.updateChildren(loginUpdateMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServerForApplicationUpdates();
        int currentItem = bottomNavigation.getCurrentItem();
        if (currentItem == 0){
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag1).commit();
            getSupportFragmentManager().beginTransaction().hide(active).show(frag1).commit();
            active = frag1;
            setIconDefaults();
            item1.setDrawable(R.drawable.ic_file_text_filled);
        } else if (currentItem == 1){
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag2).commit();
            getSupportFragmentManager().beginTransaction().hide(active).show(frag2).commit();
            active = frag2;
            setIconDefaults();
            item2.setDrawable(R.drawable.ic_message_circle_filled);
        } else if (currentItem == 2){
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag3).commit();
            getSupportFragmentManager().beginTransaction().hide(active).show(frag3).commit();
            active = frag3;
            setIconDefaults();
            item3.setDrawable(R.drawable.ic_notifications_filled);
        } else if (currentItem == 3){
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, frag4).commit();
            getSupportFragmentManager().beginTransaction().hide(active).show(frag4).commit();
            active = frag4;
            setIconDefaults();
        }
        UpdateDataFromFirebase.populateEssentials(this);
        ServerDeviceTimeDifference.getDeviceServerTimeDifference(this);
        CheckDeletedState.isDeleted(this);
    }

    //region Generate and send token to FireBase
    private void initFCM () {
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer (String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String deviceID = FirebaseInstanceId.getInstance().getId();
        reference.child("UserRoles").child(mFirebaseUser.getUid()).child("token").setValue(token);
    }
    //endregion

    //region onBoarding flow
    private void onBoardFirebaseCheck() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Analytics").child("User Login History").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("accountType_platform").equalTo("Parent_Android").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int numberOfAndroidLogins = (int) dataSnapshot.getChildrenCount();
                    if (numberOfAndroidLogins == 1)
                        onBoardDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onBoardDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_onboarding_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        viewPager = (ViewPager) dialog.findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) dialog.findViewById(R.id.dots);
        next = (Button) dialog.findViewById(R.id.next);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        try {
            dialog.show();
        } catch (Exception e) {
            return;
        }

        layouts = new int[]{
                R.layout.fragment_parent_on_boarding_one,
                R.layout.fragment_parent_on_boarding_two};

//      adding bottom dots
        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        next.setText("Find your child to get started");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParentMainActivityTwo.this, ParentSearchActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(60);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.colorLightGray));
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(5, 0, 5, 0); //(left, top, right, bottom);
            dots[i].setLayoutParams(llp);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextSize(60);
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryPurple));
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            addBottomDots(position);
            if (position == 0) {
                next.setText("Find your child to get started");
                next.setVisibility(View.VISIBLE);
            } else if (position == layouts.length - 1) {
                next.setText("See tutorials");
                next.setVisibility(View.VISIBLE);
            }

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(ParentMainActivityTwo.this, ParentSearchActivity.class);
                    } else {
                        intent = new Intent(ParentMainActivityTwo.this, TutorialsActivity.class);
                    }
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //View pager adapter
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    //endregion

    //region Show search balloon
    private void onBoardingSearchBalloonCheck() {
        Gson gson = new Gson();
        ArrayList<Student> childList = new ArrayList<>();
        String myChildrenJSON = sharedPreferencesManager.getMyChildren();
        Type type = new TypeToken<ArrayList<Student>>() {}.getType();
        childList = gson.fromJson(myChildrenJSON, type);

        if (childList == null && tutorialBalloon.getVisibility() == View.GONE) {
            onBoardingSearchBalloon.setVisibility(View.VISIBLE);
            animateVisible(onBoardingSearchBalloon);
        } else {
            onBoardingSearchBalloon.setVisibility(View.GONE);
        }
    }
    //endregion

    //region Show tutorial balloon
    private void tutorialFirebaseCheck() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Analytics").child("User Login History").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("accountType_platform").equalTo("Parent_Android").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int numberOfAndroidLogins = (int) dataSnapshot.getChildrenCount();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Analytics").child("Feature Use Analytics User").child(mFirebaseUser.getUid()).child("Tutorials");
                    mDatabaseReference.orderByChild("accountType_platform").equalTo("Parent_Android").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                if (numberOfAndroidLogins != 0)  {
                                    if ((numberOfAndroidLogins % 3) == 0) {
                                        tutorialBalloon.setVisibility(View.VISIBLE);
                                        animateVisible(tutorialBalloon);
                                        onBoardingSearchBalloon.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //endregion

    //region Remote Campaign
    private void remoteCampaign() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Campaigns").child("User Campaigns").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String campaignKey = "";

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        Boolean campaignStatus = postSnapshot.getValue(Boolean.class);

                        if (!campaignStatus) {
                            campaignKey = key;
                        }
                    }

                    if (!campaignKey.equals("")) {
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Campaigns").child("Campaigns").child(campaignKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    RemoteCampaign remoteCampaign = dataSnapshot.getValue(RemoteCampaign.class);
                                    remoteCampaign.setCampaignID(dataSnapshot.getKey());
                                    if (remoteCampaign.getCampaignTarget().equals("Parent")
                                            || remoteCampaign.getCampaignTarget().equals("All")
                                            || remoteCampaign.getCampaignTarget().equals("Parent&Teacher")
                                            || remoteCampaign.getCampaignTarget().equals("Parent&School")) {

                                        Intent I = new Intent(ParentMainActivityTwo.this, RemoteCampaignActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ID", remoteCampaign.getCampaignID());
                                        bundle.putString("URL", remoteCampaign.getCampaignURL());
                                        bundle.putString("BackgroundURL", remoteCampaign.getCampaignBackgroundURL());
                                        bundle.putString("IconURL", remoteCampaign.getCampaignIconURL());
                                        bundle.putString("Title", remoteCampaign.getCampaignTitle());
                                        bundle.putString("Text", remoteCampaign.getCampaignText());
                                        I.putExtras(bundle);
                                        startActivity(I);
                                    }
//                                    else {
//                                        DatabaseReference remoteCampaignUpdate = mFirebaseDatabase.getReference();
//                                        Map<String, Object> remoteCampaignMap = new HashMap<String, Object>();
//                                        remoteCampaignMap.put("Campaigns/User Campaigns/" + mFirebaseUser.getUid() + "/" + remoteCampaign.getCampaignID(), true);
//                                        remoteCampaignMap.put("Campaigns/Campaign Recipients/" + remoteCampaign.getCampaignID() + "/" + mFirebaseUser.getUid(), true);
//                                        remoteCampaignUpdate.updateChildren(remoteCampaignMap);
//                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //endregion

    //region Handle Automatic updates
    private void checkServerForApplicationUpdates() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(context);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks whether the platform allows the specified type of update
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    // If the update is downloaded but not installed, notify the user to complete the update.
                    popupSnackBarForCompleteUpdate();
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if ((appUpdateInfo.availableVersionCode() % 1000) == 0 && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        showDialogForImmediateUpdateWithMessage(appUpdateInfo);
//                        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
//                        CustomToast.primaryBackgroundToast(context, "Version Code: " + appUpdateInfo.availableVersionCode() + "\n" +
//                                "UpdatePriority: " + appUpdateInfo.updatePriority() + "\n");
                    } else if ((appUpdateInfo.availableVersionCode() % 50) == 0 && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        showDialogForFlexibleUpdateWithMessage(appUpdateInfo);
//                        startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE);
//                        CustomToast.primaryBackgroundToast(context, "Version Code: " + appUpdateInfo.availableVersionCode() + "\n" +
//                                "UpdatePriority: " + appUpdateInfo.updatePriority() + "\n");
                    } else {
                        int versionCode = BuildConfig.VERSION_CODE;
                        int currentVersionByTen = versionCode / 1000;
                        int updateByTen = appUpdateInfo.availableVersionCode() / 1000;

//                        String info = "VersionCode: " + String.valueOf(versionCode) + " CurrentVersionByTen: " + String.valueOf(currentVersionByTen) + " UpdateByTen: " + String.valueOf(updateByTen);
//                        CustomToast.primaryBackgroundToast(context, info);

                        if (updateByTen > currentVersionByTen) {
//                            CustomToast.primaryBackgroundToast(context, "UpdateByTen > CurrentVersionByTen");
//                            if ((appUpdateInfo.availableVersionCode() - versionCode) > 10) {
//                                CustomToast.primaryBackgroundToast(context, "appUpdateInfo.availableVersionCode - versionCode");
                                showDialogForImmediateUpdateWithMessage(appUpdateInfo);
//                            }
                        }
                    }
                }
            }
        });

        InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(@NonNull InstallState state) {
                int installStatus = state.installStatus();
                switch (installStatus) {
                    case InstallStatus.DOWNLOADING:
                        break;
                    case InstallStatus.DOWNLOADED:
                        popupSnackBarForCompleteUpdate();
                        break;
                    case InstallStatus.FAILED:
                        String message = "Remote update failed to complete";
                        showDialogWithMessage(message);
                        break;
                    case InstallStatus.CANCELED:
                        message = "Remote update has been cancelled";
                        showDialogWithMessage(message);
                        Log.e("Remote Update", "Update flow cancelled! Result code: ");
                        break;
                }
            }
        };

        appUpdateManager.registerListener(listener);
    }

    private void startUpdate(final AppUpdateInfo appUpdateInfo, final int appUpdateType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateType, (Activity) context, REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void popupSnackBarForCompleteUpdate() {
        String snackBarMessage = "Restart Celerii to install updates";
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), snackBarMessage, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Restart", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i("Remote Update", "Update flow completed! Result code: " + resultCode);
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Remote Update", "Update flow cancelled! Result code: " + resultCode);
            } else {
                String message = "Remote update failed to complete";
                showDialogWithMessage(message);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void showDialogWithMessage (String messageString) {
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
                dialog.dismiss();
            }
        });
    }

    void showDialogForFlexibleUpdateWithMessage (final AppUpdateInfo appUpdateInfo) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_binary_selection_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        String messageString = "Your version of Celerii is outdated. An update is available. " +
                "You can continue to use Celerii while your update is being downloaded, else, dismiss this dialog.";
        message.setText(messageString);

        OK.setText("Update Celerii");
        cancel.setText("Cancel");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void showDialogForImmediateUpdateWithMessage (final AppUpdateInfo appUpdateInfo) {
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

        String messageString = "Your version of Celerii is outdated. An important update is available. You need to update your app to the latest version to continue.";
        message.setText(messageString);

        OK.setText("Update Celerii");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
                dialog.dismiss();
            }
        });
    }
    //endregion

    //region Animations
    public void animateVisible(final LinearLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(500);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    public void animateGone(final LinearLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(300);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    public void animatePressed(final LinearLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(300);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimation(Animation animation){
        animation.setRepeatCount(0);
        return animation;
    }
    //endregion

//    public static class MyBroadcastReceiver extends BroadcastReceiver {
//
//        public MyBroadcastReceiver() {
//
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            new MyTask().execute();
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Reminder");
//            builder.setSmallIcon(R.drawable.ic_celerii_logo_outline_bordered)
//                    .setColor(ContextCompat.getColor(context, R.color.colorSecondaryPurple))
//                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setSubText("Clara Ikubese")
//                    .setContentTitle("Esther Oriabure")
//                    .setContentText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3")
//                    .setAutoCancel(true)
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Esther Oriabure commented on a post you made for SS3. Esther Oriabure commented on a post you made for SS3"))
//                    .setOnlyAlertOnce(true);
////                        .addAction(R.mipmap.ic_launcher, "Accept", actionIntentAccept)
////                        .addAction(R.mipmap.ic_launcher, "Decline", actionIntentDecline)
////                    .setContentIntent(resultPendingIntent);
//
//            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                builder.setChannelId("com.celerii.celerii");
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel channel = new NotificationChannel(
//                        "com.celerii.celerii",
//                        "Celerii",
//                        NotificationManager.IMPORTANCE_DEFAULT);
//
//                if (mNotificationManager != null) {
//                    mNotificationManager.createNotificationChannel(channel);
//                }
//            }
//            mNotificationManager.notify(200, builder.build());
//
//            Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
//        }
//    }
}
