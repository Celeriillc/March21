package com.celerii.celerii.Activities.Delete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.MoreTeacherAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreTeacherHeaderModel;
import com.celerii.celerii.models.MoreTeachersModel;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class MoreTeacherActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private ArrayList<MoreTeachersModel> moreTeachersModelList;
    private MoreTeacherHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreTeacherAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;

    ArrayList<String> classesFirebase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_teacher);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        moreTeachersModelList = new ArrayList<>();
        moreTeachersModelList.add(new MoreTeachersModel());
        moreHeader = new MoreTeacherHeaderModel("Esther Oriabure", "http://beautifulng.com/wp-content/uploads/2016/04/Natural-hair.jpg");
//        loadDataFromFirebase();
//        loadDataFromSharedPreferences();
//        yeah();
        mAdapter = new MoreTeacherAdapter(moreTeachersModelList, moreHeader, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    sharedPreferencesManager.setMyFirstName(teacher.getFirstName());
                    sharedPreferencesManager.setMyLastName(teacher.getLastName());
                    sharedPreferencesManager.setMyPicURL(teacher.getProfilePicURL());
                    loadDataFromSharedPreferences();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("TeacherClasses").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String classKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());

                                    sharedPreferencesManager.deleteMyClasses();
                                    sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));
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
//
    }

    void yeah(){
        MoreTeachersModel moreTeachersModel = new MoreTeachersModel("tou", "Toulouse", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreTeachersModelList.add(moreTeachersModel);

        moreTeachersModel = new MoreTeachersModel("foo", "Graphene", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreTeachersModelList.add(moreTeachersModel);

        moreTeachersModel = new MoreTeachersModel("pyo", "Diastole", "http://www.gossipmill.com/wp-content/uploads/2015/11/12188915_877024615750272_8641503643949066633_n.jpg");
        moreTeachersModelList.add(moreTeachersModel);

        moreTeachersModelList.add(new MoreTeachersModel());
    }
}
