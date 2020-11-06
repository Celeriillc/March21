package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SetupSchoolListAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SetupSchoolListRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SetupSchoolListActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    Toolbar toolbar;
    private ArrayList<SetupSchoolListRow> setupSchoolListRowList;
    public RecyclerView recyclerView;
    public SetupSchoolListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_school_list);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        if (mFirebaseUser == null){
            sharedPreferencesManager.deleteActiveAccount();
            sharedPreferencesManager.deleteMyUserID();
            sharedPreferencesManager.deleteMyFirstName();
            sharedPreferencesManager.deleteMyLastName();
            sharedPreferencesManager.deleteMyPicURL();
            sharedPreferencesManager.deleteActiveKid();
            sharedPreferencesManager.deleteActiveClass();
            sharedPreferencesManager.deleteMyClasses();
            sharedPreferencesManager.deleteMyChildren();
            Intent I = new Intent(SetupSchoolListActivity.this, IntroSlider.class);
            startActivity(I);
            finish();
            return;
        }

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select a school");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        setupSchoolListRowList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new SetupSchoolListAdapter(setupSchoolListRowList, this);
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );
    }

    public void loadDetailsFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference().child("School");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    setupSchoolListRowList.clear();
                    setupSchoolListRowList.add(new SetupSchoolListRow());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        School school = postSnapshot.getValue(School.class);
                        SetupSchoolListRow setupSchoolListRow = new SetupSchoolListRow(postSnapshot.getKey(), school.getSchoolName(), school.getLocation(), school.getProfilePhotoUrl(), false);
                        setupSchoolListRowList.add(setupSchoolListRow);
                        mAdapter.notifyDataSetChanged();
                        mySwipeRefreshLayout.setRefreshing(false);
                        progressLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void yeah(){
        SetupSchoolListRow setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);

        setupSchoolListRow = new SetupSchoolListRow("", "Inglewood Academy", "172 Ijale Street, Owutu", "http://www.lagosschoolsonline.com/schools/Inglewood%20Academy%20Owotu%20IkoroduB.jpg", true);
        setupSchoolListRowList.add(setupSchoolListRow);
    }
}
