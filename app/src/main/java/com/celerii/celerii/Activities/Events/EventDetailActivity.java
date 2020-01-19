package com.celerii.celerii.Activities.Events;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetailActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    TextView title, date, time, school, description;

    String accountType, eventAccount;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        Bundle b = getIntent().getExtras();
//        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitleEnabled(true);
//        collapsingToolbar.setTitle(b.getString("title"));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(b.getString("title"));
        getSupportActionBar().setHomeButtonEnabled(true);

        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        school = (TextView) findViewById(R.id.school);
        description = (TextView) findViewById(R.id.description);

        title.setText(b.getString("title"));
        date.setText(b.getString("date"));
        time.setText(b.getString("time"));
        school.setText(b.getString("school"));

        if (b.getString("description").equals("")) {
            description.setText("No remark has been provided");
        }
        else {
            description.setText(b.getString("description"));
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
