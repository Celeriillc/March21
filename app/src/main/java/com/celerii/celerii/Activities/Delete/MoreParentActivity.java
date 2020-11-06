package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.MoreParentsAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.MoreParentsHeaderModel;
import com.celerii.celerii.models.MoreParentsModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MoreParentActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<MoreParentsModel> moreParentsModelList;
    private MoreParentsHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreParentsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    ProgressBar progressBar;

    ArrayList<String> childIdsFirebase = new ArrayList<>();
    ArrayList<String> childNameFirebase = new ArrayList<>();
    ArrayList<String> childURLFirebase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_parent);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        if (mFirebaseUser == null){
            Intent I = new Intent(MoreParentActivity.this, IntroSlider.class);
            startActivity(I);
            finish();
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        moreParentsModelList = new ArrayList<>();
        moreParentsModelList.add(new MoreParentsModel());
        moreHeader = new MoreParentsHeaderModel("Clara Ikubese", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
//        loadDataFromFirebase();
//        loadDataFromSharedPreferences();
//        yeah();
//        mAdapter = new MoreParentsAdapter(moreParentsModelList, moreHeader, this, progressBar);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setMyFirstName(parent.getFirstName());
                    sharedPreferencesManager.setMyLastName(parent.getLastName());
                    sharedPreferencesManager.setMyPicURL(parent.getProfilePicURL());
                    loadDataFromSharedPreferences();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("ParentStudent").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childIdsFirebase.add(childKey);
                                    childNameFirebase.add(childInstance.getFirstName() + " " + childInstance.getLastName());
                                    childURLFirebase.add(childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
//                                    sharedPreferencesManager.deleteMyChildrensName();
//                                    sharedPreferencesManager.deleteMyChildrensURL();
//                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childIdsFirebase));
//                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childIdsFirebase));
//                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childIdsFirebase));
                                    loadDataFromSharedPreferences();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDataFromSharedPreferences() {
//        moreHeader.setParentID(mFirebaseUser.getUid());
//        moreHeader.setParentName(sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName());
//        moreHeader.setParentImageURL(sharedPreferencesManager.getMyPicURL());
//        mAdapter.notifyDataSetChanged();
//        Set<String> childIdSet = sharedPreferencesManager.getMyChildren();
//        Set<String> childNameSet = sharedPreferencesManager.getMyChildrensName();
//        Set<String> childURLSet = sharedPreferencesManager.getMyChildrensURL();
//        ArrayList<String> childIds = new ArrayList<>(childIdSet);
//        ArrayList<String> childName = new ArrayList<>(childNameSet);
//        ArrayList<String> childURL = new ArrayList<>(childURLSet);
//        for (int i = 0; i < childIds.size(); i++) {
//            MoreParentsModel moreParentsModel = new MoreParentsModel(childIds.get(i), childName.get(i), childURL.get(i));
//            moreParentsModelList.add(moreParentsModel);
//            mAdapter.notifyDataSetChanged();
//        }
    }

    void yeah(){
        MoreParentsModel moreParentsModel = new MoreParentsModel("0001", "J R Smith", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreParentsModelList.add(moreParentsModel);

        moreParentsModel = new MoreParentsModel("0002", "Kyrie Irving", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreParentsModelList.add(moreParentsModel);

        moreParentsModel = new MoreParentsModel("0003", "Lebron James", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreParentsModelList.add(moreParentsModel);

        moreParentsModelList.add(new MoreParentsModel());
    }
}