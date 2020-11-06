package com.celerii.celerii.Activities.Delete;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassListAdapterHorizontal;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassAssignment;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherCreateAssignmentPostActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private ArrayList<Class> classList;
    private ArrayList<String> parentList;
    public RecyclerView recyclerView;
    public ClassListAdapterHorizontal mAdapter;
    LinearLayoutManager mLayoutManager;
    String posterNameString;
    ClassAssignment classAssignment;
    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment();
    ProgressBar progressBar;

    ImageView posterPic;
    TextView posterName, assignmentpost, changeDueDate, displayDueDate, chooseClassToPostToDescriptor, noDataLayout;
    View assignmentPostSeparator;
    LinearLayout recyclerViewLayout, dateDueLayout;

    String assignment, time, dateDue, teacherID, imageURL, url;
    String isDue;
    List<Class> classRecipients = new ArrayList<Class>();

    Calendar calendar = Calendar.getInstance();
    int dueDate;
    int todaysDate;
    boolean isClassLoaded, isParentsLoaded, isTextNotEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_assignment_post);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Assignment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        posterPic = (ImageView) findViewById(R.id.posterpic);
        Glide.with(this)
                .load("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png")
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(posterPic);

        posterNameString = "Esther Oriabure O.";
        posterName = (TextView) findViewById(R.id.postername);
        posterName.setText(posterNameString);

        assignmentpost = (TextView) findViewById(R.id.assignmentpost);
        chooseClassToPostToDescriptor = (TextView) findViewById(R.id.chooseclasstoposttodescriptor);
        noDataLayout = (TextView) findViewById(R.id.nodatalayout);
        assignmentPostSeparator = findViewById(R.id.assignmentpostseparator);
        recyclerViewLayout = (LinearLayout) findViewById(R.id.recycler_view_layout);
        dateDueLayout = (LinearLayout) findViewById(R.id.dateduelayout);

        assignmentpost.setVisibility(View.GONE);
        chooseClassToPostToDescriptor.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.GONE);
        assignmentPostSeparator.setVisibility(View.GONE);
        recyclerViewLayout.setVisibility(View.GONE);
        dateDueLayout.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        classList = new ArrayList<>();
        parentList = new ArrayList<>();
        loadClasses();
        mAdapter = new ClassListAdapterHorizontal(classList, this);
        recyclerView.setAdapter(mAdapter);

        changeDueDate = (TextView) findViewById(R.id.changeduedate);
        displayDueDate = (TextView) findViewById(R.id.displayduedate);
        displayDueDate.setText(Month.Month(calendar.get(Calendar.MONTH)) + " " + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + ", " +
                String.valueOf(calendar.get(Calendar.YEAR)));
        dueDate = Integer.valueOf(String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                convertToTwoSignificantFigures(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));

        changeDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdp.show(getSupportFragmentManager(), "Select Due Date For Assignment");
            }
        });

        cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
            @Override
            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                displayDueDate.setText(Month.Month(monthOfYear) + " " + String.valueOf(dayOfMonth) + ", " + String.valueOf(year));
                CustomToast.whiteBackgroundBottomToast(TeacherCreateAssignmentPostActivity.this, String.valueOf(year) + " " + String.valueOf(monthOfYear));
                dueDate = Integer.valueOf(String.valueOf(year) + String.valueOf(monthOfYear + 1) +
                        convertToTwoSignificantFigures(String.valueOf(dayOfMonth)));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
//        if (!isClassLoaded || !isParentsLoaded || !isTextNotEmpty) {
        if (!isClassLoaded || !isParentsLoaded) {
            menu.findItem(R.id.action_send).setEnabled(false);
            menu.findItem(R.id.action_send).getIcon().setAlpha(70);
        }else{
            menu.findItem(R.id.action_send).setEnabled(true);
            menu.findItem(R.id.action_send).getIcon().setAlpha(255);
            makeViewVisible();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send){
            assignment = assignmentpost.getText().toString();
            time = String.valueOf(calendar.get(Calendar.YEAR)) + "/" + String.valueOf(calendar.get(Calendar.MONTH)) + "/" +
                    String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE))
                    + ":" + String.valueOf(calendar.get(Calendar.SECOND)) + ":" + String.valueOf(calendar.get(Calendar.MILLISECOND));
            dateDue = displayDueDate.getText().toString();
            teacherID = auth.getCurrentUser().getUid();
            imageURL = ""; //TODO: VERY IMPORTANT URL
            url = ""; //TODO: VERY IMPORTANT URL

            for (int i = 0; i < classList.size(); i++){
                if (classList.get(i).isTicked()){
                    classRecipients.add(classList.get(i));
                }
            }

            todaysDate = Integer.valueOf(String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.MONTH) + 1) +
                    convertToTwoSignificantFigures(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));

            if (todaysDate > dueDate){
                isDue = "true";
            } else {
                isDue = "false";
            }

            classAssignment = new ClassAssignment(assignment, time, dateDue, isDue, teacherID, imageURL, url);
            DatabaseReference newStoryR = mDatabaseReference.child("ClassAssignment").push();
            String pushID = newStoryR.getKey();
            DatabaseReference newStoryRef = mFirebaseDatabase.getReference();


            Map<String, Object> newStory = new HashMap<String, Object>();
            newStory.put("ClassAssignment/" + pushID, classAssignment);
            newStory.put("ClassAssignmentTeacherTimeline/" + teacherID + "/" + pushID, true);
            for (int i = 0; i < classRecipients.size(); i++){
                String classStoryReciepientsPush = "ClassAssignmentReciepients/" + pushID + "/";
                newStory.put(classStoryReciepientsPush + classRecipients.get(i).getID(), true);
                newStory.put("ClassAssignmentClass/" + classRecipients.get(i).getID() + "/" + pushID, true);
            }
            for (int i = 0; i < parentList.size(); i++){
                newStory.put("Parent Assignment Feed/" + parentList.get(i) + "/" + pushID, true);
            }

            newStoryRef.updateChildren(newStory, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        CustomToast.whiteBackgroundBottomToast(TeacherCreateAssignmentPostActivity.this, "Assignment Has been posted");
                        finish();
                    } else{
                        CustomToast.whiteBackgroundBottomToast(TeacherCreateAssignmentPostActivity.this, "Assignment could not be posted, try again");
                    }
                }
            });

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void loadClasses() {
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    classList.clear();
                    final String classKey = postSnapshot.getKey();
                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("Class/" + classKey);
                    newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            isClassLoaded = true;
                            invalidateOptionsMenu();
                            Class aClass = dataSnapshot.getValue(Class.class);
                            aClass.setTicked(false);
                            aClass.setID(classKey);
                            aClass.setClassPicURL("");
                            classList.add(aClass);
                            mAdapter.notifyDataSetChanged();
                            if (classList.size() > 0){
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                            }else{
                                isClassLoaded = false;
                                invalidateOptionsMenu();
                                recyclerViewLayout.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setText("You have no classes to post to");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            isClassLoaded = false;
                            invalidateOptionsMenu();
                            recyclerViewLayout.setVisibility(View.GONE);
                            noDataLayout.setVisibility(View.VISIBLE);
                            noDataLayout.setText("Your classes could not be loaded");
                        }
                    });

                    DatabaseReference newRefParents = FirebaseDatabase.getInstance().getReference("Class Parent/" + classKey);
                    newRefParents.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                isParentsLoaded = true;
                                invalidateOptionsMenu();
                                String parentKey = postSnapshot.getKey();
                                parentList.add(parentKey);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            isParentsLoaded = false;
                            invalidateOptionsMenu();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomToast.whiteBackgroundBottomToast(TeacherCreateAssignmentPostActivity.this, databaseError.toException().toString());
            }
        });
    }

    void makeViewVisible(){
        progressBar.setVisibility(View.GONE);
        assignmentpost.setVisibility(View.VISIBLE);
        chooseClassToPostToDescriptor.setVisibility(View.VISIBLE);
        assignmentPostSeparator.setVisibility(View.VISIBLE);
        if (classList.size() > 0){
            recyclerViewLayout.setVisibility(View.VISIBLE);
            noDataLayout.setVisibility(View.GONE);
        } else {
            recyclerViewLayout.setVisibility(View.GONE);
            noDataLayout.setVisibility(View.VISIBLE);
            noDataLayout.setText("You have no classes to post to");
        }
        dateDueLayout.setVisibility(View.VISIBLE);
    }

    String convertToTwoSignificantFigures(String number){
        if (number.length() == 1){
            return "0" + number;
        }else{
            return number;
        }
    }
}
