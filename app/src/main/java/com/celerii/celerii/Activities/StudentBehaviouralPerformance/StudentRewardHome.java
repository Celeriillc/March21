package com.celerii.celerii.Activities.StudentBehaviouralPerformance;

import android.content.Context;
import android.content.Intent;

import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.Student;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class StudentRewardHome extends AppCompatActivity {

    Context context;
    ImageView studentPic;
    LinearLayout studentProfilePictureClipper, studentLayout;
    TextView studentFullName;
    Toolbar toolbar;
    TabLayout tabLayout;
    Bundle bundle;
    String studentID;
    String studentName;
    String studentPicURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_reward_home);

        context = this;
        bundle = getIntent().getExtras();
        studentID = bundle.getString("studentID");
        studentName = bundle.getString("studentName");
        studentPicURL = bundle.getString("studentPicURL");

        studentPic = (ImageView) findViewById(R.id.studentpic);
        studentProfilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        studentLayout = (LinearLayout) findViewById(R.id.studentlayout);
        studentFullName = (TextView) findViewById(R.id.studentname);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(studentName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.home_tabs_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.home_tabs);
        tabLayout.setupWithViewPager(viewPager);

        studentFullName.setText(studentName);
        studentProfilePictureClipper.setClipToOutline(true);

        Drawable textDrawable;
        if (!studentName.isEmpty()) {
            String[] nameArray = studentName.replaceAll("\\s+", " ").split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1]);
            }
            studentPic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!studentPicURL.isEmpty()) {
            Glide.with(context)
                    .load(studentPicURL)
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(studentPic);
        }

        studentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentRewardHome.this, StudentProfileActivity.class);
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                Student student = new Student(studentName, studentID, studentPicURL);
                String studentCred = gson.toJson(student);
                bundle.putString("childID", studentCred);
                I.putExtras(bundle);
                startActivity(I);
            }
        });
    }

    public String getStudentID(){
        return studentID;
    }

    public String getStudentName(){
        return studentName;
    }

    public String getStudentPicURL(){
        return studentPicURL;
    }

    private void setupViewPager(ViewPager viewPager) {
        StudentRewardHome.ViewPagerAdapter adapter = new StudentRewardHome.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudentRewardFragment(), "Rewards");
        adapter.addFrag(new StudentPunishmentFragment(), "Punishments");
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
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
