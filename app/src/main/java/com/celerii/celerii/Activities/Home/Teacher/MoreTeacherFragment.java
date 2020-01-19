package com.celerii.celerii.Activities.Home.Teacher;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.MoreTeacherAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreTeacherHeaderModel;
import com.celerii.celerii.models.MoreTeachersModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreTeacherFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private ArrayList<MoreTeachersModel> moreTeachersModelList;
    private MoreTeacherHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreTeacherAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    SwipeRefreshLayout mySwipeRefreshLayout;

    ArrayList<String> classesFirebase = new ArrayList<>();
    ArrayList<String> childrenFirebase = new ArrayList<>();

    Boolean loadedFromFirebase;

    public MoreTeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_teacher, container, false);
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        moreTeachersModelList = new ArrayList<>();
        moreHeader = new MoreTeacherHeaderModel("", "");
        mAdapter = new MoreTeacherAdapter(moreTeachersModelList, moreHeader, getContext());
        loadDataFromFirebase();
        loadDataFromSharedPreferences();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDataFromSharedPreferences();
                        loadDataFromFirebase();
                    }
                }
        );

        return view;
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    sharedPreferencesManager.deleteMyClasses();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        classesFirebase.clear();
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
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    sharedPreferencesManager.deleteMyClasses();
                    sharedPreferencesManager.deleteActiveClass();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid());
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
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
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
        mySwipeRefreshLayout.setRefreshing(false);
        moreHeader.setTeacherID(mAuth.getCurrentUser().getUid());
        moreHeader.setTeacherName(sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName());
        moreHeader.setTeacherImageURL(sharedPreferencesManager.getMyPicURL());
        mAdapter.notifyDataSetChanged();
        Set<String> classSet = sharedPreferencesManager.getMyClasses();
        ArrayList<String> classes = new ArrayList<>();

        if (classSet != null){ classes = new ArrayList<>(classSet); }

        moreTeachersModelList.clear();
        moreTeachersModelList.add(new MoreTeachersModel());

        if (classes.size() > 0) {
            for (int i = 0; i < classes.size(); i++) {
                String[] classInfo = classes.get(i).split(" ");
                MoreTeachersModel moreTeachersModel = new MoreTeachersModel(classInfo[0], classInfo[1], classInfo[2]);
                moreTeachersModelList.add(moreTeachersModel);
            }
        }

        moreTeachersModelList.add(new MoreTeachersModel());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        loadDataFromSharedPreferences();
        loadDataFromFirebase();
        super.onResume();
    }
}
