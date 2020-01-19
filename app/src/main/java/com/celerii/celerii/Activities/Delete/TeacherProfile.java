package com.celerii.celerii.Activities.Delete;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.celerii.celerii.Activities.Home.Parent.ParentHomeNotification;
import com.celerii.celerii.R;

public class TeacherProfile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        mToolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Esther Oriabure");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragmentholder, new TeacherProfileFragment())
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottombaritem_profile:
                        // TODO
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_fragmentholder, new TeacherProfileFragment())
                                .commit();
                        return true;
                    case R.id.bottombaritem_Reviews:
                        // TODO
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_fragmentholder, new ReviewFragment())
                                .commit();
                        return true;
                    case R.id.bottombaritem_Other:
                        // TODO
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_fragmentholder, new ParentHomeNotification())
                                .commit();
                        return true;
                }
                return false;
            }
        });
    }
}
