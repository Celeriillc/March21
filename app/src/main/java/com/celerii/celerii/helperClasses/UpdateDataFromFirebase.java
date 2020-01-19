package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.view.View;

import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by DELL on 12/30/2019.
 */

public class UpdateDataFromFirebase {
    static SharedPreferencesManager sharedPreferencesManager;

    static FirebaseAuth auth = FirebaseAuth.getInstance();
    static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    static DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
    static FirebaseUser mFirebaseUser = auth.getCurrentUser();

    static int childrenCounter;
    static int classesCounter;

    static ArrayList<String> childrenFirebase = new ArrayList<>();
    static ArrayList<String> classesFirebase = new ArrayList<>();
    static ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
    static ArrayList<ClassesStudentsAndParentsModel> classesStudentsModelList = new ArrayList<>();
    static ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList = new ArrayList<>();

    private static HashMap<String, ClassesStudentsAndParentsModel> classesStudentsAndParentsModelMap;
    private static HashMap<String, ClassesStudentsAndParentsModel> classesStudentsModelMap;
    private static HashMap<String, StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelMap;

    public static void populateEssentials(Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);

        loadBasicInfo();
    }

    static void loadBasicInfo() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setMyUserID(mFirebaseUser.getUid());
                    sharedPreferencesManager.setMyFirstName(parent.getFirstName());
                    sharedPreferencesManager.setMyLastName(parent.getLastName());
                    sharedPreferencesManager.setMyPicURL(parent.getProfilePicURL());

                    getMyChildren(mFirebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getMyChildren(final String myID) {
        childrenCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                childrenCounter++;
                                if (dataSnapshot.exists()) {
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
                                }

                                if (childrenCounter == childrenCount) {
                                    getMyClasses(myID);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    getMyClasses(myID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getMyClasses(String myID) {
        classesCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(myID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classesCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String classKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                classesCounter++;
                                if (dataSnapshot.exists()) {
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());

                                    sharedPreferencesManager.deleteMyClasses();
                                    sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));
                                }

                                if (classesCount == classesCounter) {
                                    loadClassesStudentsandParentsInfo();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    loadClassesStudentsandParentsInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadClassesStudentsandParentsInfo() {
        classesStudentsAndParentsModelMap = new HashMap<>();
        classesStudentsModelMap = new HashMap<>();
        classesStudentsAndParentsModelList.clear();
        classesStudentsModelList.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String classKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Class School").child(classKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String schoolKey = postSnapshot.getKey();

                                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Class Students").child(classKey);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        final String studentKey = postSnapshot.getKey();

                                                        ClassesStudentsAndParentsModel classesStudentsAndParentsModel = new ClassesStudentsAndParentsModel(classKey, schoolKey, studentKey);
                                                        String existenceChecker = classKey + schoolKey + studentKey;
                                                        if (!classesStudentsModelMap.containsKey(existenceChecker)) {
                                                            classesStudentsModelList.add(classesStudentsAndParentsModel);
                                                            classesStudentsModelMap.put(existenceChecker, classesStudentsAndParentsModel);
                                                            Gson gson = new Gson();
                                                            String json = gson.toJson(classesStudentsModelList);
                                                            sharedPreferencesManager.setClassesStudent(json);
                                                        }

                                                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student Parent").child(studentKey);
                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                        final String parentKey = postSnapshot.getKey();

                                                                        ClassesStudentsAndParentsModel classesStudentsAndParentsModel = new ClassesStudentsAndParentsModel(classKey, schoolKey, studentKey, parentKey);

                                                                        String existenceChecker = classKey + schoolKey + studentKey + parentKey;
                                                                        if (!classesStudentsAndParentsModelMap.containsKey(existenceChecker)) {
                                                                            classesStudentsAndParentsModelList.add(classesStudentsAndParentsModel);
                                                                            classesStudentsAndParentsModelMap.put(existenceChecker, classesStudentsAndParentsModel);
                                                                            Gson gson = new Gson();
                                                                            String json = gson.toJson(classesStudentsAndParentsModelList);
                                                                            sharedPreferencesManager.setClassesStudentParent(json);
                                                                        }

                                                                        loadStudentsSchoolsandTeachersInfo();
                                                                    }
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

    static void loadStudentsSchoolsandTeachersInfo() {
        studentsSchoolsClassesandTeachersModelMap = new HashMap<>();
        classesStudentsAndParentsModelList.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String studentKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student School").child(studentKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String schoolKey = postSnapshot.getKey();

                                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student Class").child(studentKey);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        final String classKey = postSnapshot.getKey();

                                                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Class Teacher").child(classKey);
                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                        final String teacherKey = postSnapshot.getKey();

                                                                        StudentsSchoolsClassesandTeachersModel  studentsSchoolsClassesandTeachersModel = new StudentsSchoolsClassesandTeachersModel(studentKey, schoolKey, classKey, teacherKey);

                                                                        String existenceChecker = studentKey + schoolKey + classKey + teacherKey;
                                                                        if (!studentsSchoolsClassesandTeachersModelMap.containsKey(existenceChecker)) {
                                                                            studentsSchoolsClassesandTeachersModelList.add(studentsSchoolsClassesandTeachersModel);
                                                                            studentsSchoolsClassesandTeachersModelMap.put(existenceChecker, studentsSchoolsClassesandTeachersModel);
                                                                            Gson gson = new Gson();
                                                                            String json = gson.toJson(studentsSchoolsClassesandTeachersModelList);
                                                                            sharedPreferencesManager.setStudentsSchoolsClassesTeachers(json);
                                                                        }
                                                                    }
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
}
