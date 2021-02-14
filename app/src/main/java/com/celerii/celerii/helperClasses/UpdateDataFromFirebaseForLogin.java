package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.models.Admin;
import com.celerii.celerii.models.Chats;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.MessageList;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsClassesModel;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
import com.celerii.celerii.models.SubscriptionModel;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UpdateDataFromFirebaseForLogin {
    static SharedPreferencesManager sharedPreferencesManager;
    static ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    static FirebaseAuth auth;
    static FirebaseDatabase mFirebaseDatabase;
    static DatabaseReference mDatabaseReference;
    static FirebaseUser mFirebaseUser;

    private static int numberOfPostsPerLoad = 20;

    private static int childrenCounter;
    private static int classesCounter;
    private static int subscriptionCounter = 0;
    private static int subscriptionCounterForTeachers = 0;
    private static int classStudentsForTeacherCounter1 = 0;
    private static int classStudentsForTeacherCounter2 = 0;
    private static int parentFeedCounter = 0;
    private static int teacherFeedCounter = 0;
    private static int parentNotificationCounter = 0;
    private static int teacherNotificationCounter = 0;

    private static ArrayList<Student> myChildren = new ArrayList<>();
    private static ArrayList<Class> myClasses = new ArrayList<>();
    private static ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
    private static ArrayList<ClassesStudentsAndParentsModel> classesStudentsModelList = new ArrayList<>();
    private static ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
    private static ArrayList<StudentsClassesModel> studentsClassesModelList = new ArrayList<>();
    private static ArrayList<String> subjectList = new ArrayList<>();
    private static ArrayList<ClassStory> parentClassStoryList = new ArrayList<>();
    private static ArrayList<String> parentStoryKeyList = new ArrayList<>();
    private static ArrayList<ClassStory> teacherClassStoryList = new ArrayList<>();
    private static ArrayList<String> teacherStoryKeyList = new ArrayList<>();
    private static ArrayList<MessageList> inboxList = new ArrayList<>();
    private static ArrayList<NotificationModel> parentNotificationModelList = new ArrayList<>();
    private static ArrayList<NotificationModel> teacherNotificationModelList = new ArrayList<>();

    private static HashMap<String, ClassesStudentsAndParentsModel> classesStudentsAndParentsModelMap;
    private static HashMap<String, ClassesStudentsAndParentsModel> classesStudentsModelMap;
    private static HashMap<String, StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelMap;
    private static HashMap<String, StudentsClassesModel> studentsClassesModelMap;
    private static HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = new HashMap<>();
    private static HashMap<String, ArrayList<SubscriptionModel>> subscriptionMap = new HashMap<>();
    private static HashMap<String, SubscriptionModel> subscriptionMapForTeachers = new HashMap<>();

    static Context context;
    static String activeAccount;
    static CustomProgressDialogOne progressDialog;

    public static void populateEssentials(Context contextp, String activeAccountp, CustomProgressDialogOne progressDialogp) {

        context = contextp;
        activeAccount = activeAccountp;
        progressDialog = progressDialogp;

        sharedPreferencesManager = new SharedPreferencesManager(context);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(context);
        sharedPreferencesManager.setActiveAccount(activeAccount);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            LogoutProtocol.logout(context, "No Internet");
//            CustomToast.blueBackgroundToast(context, "No Internet");
//            sharedPreferencesManager.clear();
//            auth.signOut();
//            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
//            ((Activity)context).finishAffinity();
//            Intent intent = new Intent(((Activity)context), IntroSlider.class);
//            ((Activity)context).startActivity(intent);
            return;
        }

        if (mFirebaseUser == null) {
            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
            ((Activity)context).finishAffinity();
            Intent intent = new Intent(((Activity)context), IntroSlider.class);
            ((Activity)context).startActivity(intent);
        }


        loadBasicInfo();
    }

    static void loadBasicInfo() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    sharedPreferencesManager.setMyUserID(mFirebaseUser.getUid());
                    sharedPreferencesManager.setMyFirstName(teacher.getFirstName());
                    sharedPreferencesManager.setMyMiddleName(teacher.getMiddleName());
                    sharedPreferencesManager.setMyLastName(teacher.getLastName());
                    sharedPreferencesManager.setMyPicURL(teacher.getProfilePicURL());
                    sharedPreferencesManager.setMyPhoneNumber(teacher.getPhone());
                    sharedPreferencesManager.setMyGender(teacher.getGender());
                    sharedPreferencesManager.setMyRelationshipStatus(teacher.getMaritalStatus());
                    sharedPreferencesManager.setMyBio(teacher.getBio());

                    getMyParentInfo(mFirebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getMyParentInfo(final String myID) {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setMyOccupation(parent.getOccupation());
                }

                getMyChildren();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static ArrayList<String> childrenKeyHolder = new ArrayList<>();
    private static void getMyChildren() {
        if (mFirebaseUser == null) {
            return;
        }

        childrenCounter = 0;
        myChildren.clear();
        childrenKeyHolder.clear();
        final ArrayList<String> childrenKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myChildren.clear();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String childKey = postSnapShot.getKey();
                        childrenKeyList.add(childKey);
                    }

                    getMyChildrenRecursive(0, childrenKeyList);
                } else {
                    sharedPreferencesManager.deleteMyChildren();
                    getMyClasses();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getMyChildrenRecursive(final Integer index, final ArrayList<String> childrenKeyList) {
        String childKey = childrenKeyList.get(index);
        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrenCounter++;
                if (dataSnapshot.exists()) {
                    Student childInstance = dataSnapshot.getValue(Student.class);
                    childInstance.setStudentID(dataSnapshot.getKey());
                    if (!childrenKeyHolder.contains(dataSnapshot.getKey())) {
                        childrenKeyHolder.add(dataSnapshot.getKey());
                        myChildren.add(childInstance);
                    }
                }

                if (index == childrenKeyList.size() - 1) {
                    sharedPreferencesManager.deleteMyChildren();
                    Gson gson = new Gson();
                    String json = gson.toJson(myChildren);
                    sharedPreferencesManager.setMyChildren(json);
                    getMyClasses();
                } else {
                    getMyChildrenRecursive(index + 1, childrenKeyList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static ArrayList<String> classesKeyHolder = new ArrayList<>();
    private static void getMyClasses() {
        if (mFirebaseUser == null) {
            return;
        }

        classesCounter = 0;
        myClasses.clear();
        classesKeyHolder.clear();
        final ArrayList<String> classesKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classesCount = (int) dataSnapshot.getChildrenCount();
                    myClasses.clear();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String classKey = postSnapShot.getKey();
                        classesKeyList.add(classKey);
                    }

                    getMyClassesRecursive(0, classesKeyList);
                } else {
                    sharedPreferencesManager.deleteMyClasses();
                    loadClassesStudentsandParentsInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void getMyClassesRecursive(final Integer index, final ArrayList<String> classesKeyList) {
        String classKey = classesKeyList.get(index);
        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classesCounter++;
                if (dataSnapshot.exists()) {
                    Class classInstance = dataSnapshot.getValue(Class.class);
                    classInstance.setID(dataSnapshot.getKey());
                    if (!classesKeyHolder.contains(dataSnapshot.getKey())) {
                        classesKeyHolder.add(dataSnapshot.getKey());
                        myClasses.add(classInstance);
                    }
                }

                if (index == classesKeyList.size() - 1) {
                    sharedPreferencesManager.deleteMyClasses();
                    Gson gson = new Gson();
                    String json = gson.toJson(myClasses);
                    sharedPreferencesManager.setMyClasses(json);
                    loadClassesStudentsandParentsInfo();
                } else {
                    getMyClassesRecursive(index + 1, classesKeyList);
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

                loadStudentsSchoolsandTeachersInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadStudentsSchoolsandTeachersInfo() {
        studentsSchoolsClassesandTeachersModelMap = new HashMap<>();
        studentsSchoolsClassesandTeachersModelList.clear();
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

                loadStudentsClassesInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadStudentsClassesInfo() {
        studentsClassesModelMap = new HashMap<>();
        studentsClassesModelList.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String studentKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student Class").child(studentKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String classKey = postSnapshot.getKey();

                                        StudentsClassesModel  studentsClassesModel = new StudentsClassesModel(studentKey, classKey);

                                        String existenceChecker = studentKey + classKey;
                                        if (!studentsClassesModelMap.containsKey(existenceChecker)) {
                                            studentsClassesModelList.add(studentsClassesModel);
                                            studentsClassesModelMap.put(existenceChecker, studentsClassesModel);
                                            Gson gson = new Gson();
                                            String json = gson.toJson(studentsClassesModelList);
                                            sharedPreferencesManager.setStudentsClasses(json);
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

                loadSubjectsInfo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadSubjectsInfo() {
        subjectList.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher School/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String schoolKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("School Subjects").child(schoolKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String subjectKey = postSnapshot.getKey();

                                        if (!subjectList.contains(subjectKey)) {
                                            subjectList.add(subjectKey);
                                            Gson gson = new Gson();
                                            String json = gson.toJson(subjectList);
                                            sharedPreferencesManager.setSubjects(json);
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

                loadClassStudentsForTeacher();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadClassStudentsForTeacher() {
        classStudentsForTeacherMap.clear();
        classStudentsForTeacherCounter1 = 0;
        classStudentsForTeacherCounter2 = 0;

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classStudentsForTeacherChildrenCount1 = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String classKey = postSnapshot.getKey();
                        classStudentsForTeacherMap.put(classKey, new HashMap<String, Student>());

                        mDatabaseReference = mFirebaseDatabase.getReference("Class Students").child(classKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final int classStudentsForTeacherChildrenCount2 = (int) dataSnapshot.getChildrenCount();

                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        final String studentKey = postSnapshot.getKey();

                                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(studentKey);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                classStudentsForTeacherCounter2++;
                                                if (dataSnapshot.exists()) {
                                                    Student student = dataSnapshot.getValue(Student.class);
                                                    try {
                                                        classStudentsForTeacherMap.get(classKey).put(studentKey, student);
                                                    } catch (Exception e) {

                                                    }
                                                }

                                                if (classStudentsForTeacherCounter2 == classStudentsForTeacherChildrenCount2) {
                                                    classStudentsForTeacherCounter2 = 0;
                                                    classStudentsForTeacherCounter1++;
                                                    if (classStudentsForTeacherCounter1 == classStudentsForTeacherChildrenCount1) {
                                                        Gson gson = new Gson();
                                                        String json = gson.toJson(classStudentsForTeacherMap);
                                                        sharedPreferencesManager.setClassStudentForTeacher(json);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else {
                                    classStudentsForTeacherCounter1++;
                                    if (classStudentsForTeacherCounter1 == classStudentsForTeacherChildrenCount1) {
                                        Gson gson = new Gson();
                                        String json = gson.toJson(classStudentsForTeacherMap);
                                        sharedPreferencesManager.setClassStudentForTeacher(json);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                loadParentFeed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadParentFeed() {
        parentClassStoryList.clear();
        parentStoryKeyList.clear();

        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryParentFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    parentFeedCounter = 0;
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String classStoryKeys = postSnapshot.getKey();
                        if (parentStoryKeyList.contains(classStoryKeys)) { continue; }
                        parentStoryKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String posterAccountType = classStoryServer.getPosterAccountType();

                                    if (posterAccountType.equals("School")) {
                                        String schoolID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                parentFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    String posterName = school.getSchoolName();
                                                    String posterProfilePicURL = school.getProfilePhotoUrl();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    parentClassStoryList.add(classStoryServer);
                                                }

                                                if (parentFeedCounter == childrenCount) {
                                                    if (parentClassStoryList.size() > 1) {
                                                        Collections.sort(parentClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(parentClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(parentClassStoryList);
                                                    sharedPreferencesManager.setParentFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (posterAccountType.equals("Teacher") || posterAccountType.equals("Parent")) {
                                        String teacherID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                parentFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    String posterName = teacher.getFirstName() + " " + teacher.getLastName();
                                                    String posterProfilePicURL = teacher.getProfilePicURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    parentClassStoryList.add(classStoryServer);
                                                }

                                                if (parentFeedCounter == childrenCount) {
                                                    if (parentClassStoryList.size() > 1) {
                                                        Collections.sort(parentClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(parentClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(parentClassStoryList);
                                                    sharedPreferencesManager.setParentFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                parentFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    Admin admin = dataSnapshot.getValue(Admin.class);
                                                    String posterName = admin.getDisplayName();
                                                    String posterProfilePicURL = admin.getProfilePictureURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    parentClassStoryList.add(classStoryServer);
                                                }

                                                if (parentFeedCounter == childrenCount) {
                                                    if (parentClassStoryList.size() > 1) {
                                                        Collections.sort(parentClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(parentClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(parentClassStoryList);
                                                    sharedPreferencesManager.setParentFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else {
                                    parentFeedCounter++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                loadTeacherFeed();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadTeacherFeed() {
        teacherClassStoryList.clear();
        teacherStoryKeyList.clear();

        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryTeacherFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherFeedCounter = 0;
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String classStoryKeys = postSnapshot.getKey();
                        if (teacherStoryKeyList.contains(classStoryKeys)) { continue; }
                        teacherStoryKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String posterAccountType = classStoryServer.getPosterAccountType();

                                    if (posterAccountType.equals("School")) {
                                        String schoolID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                teacherFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    String posterName = school.getSchoolName();
                                                    String posterProfilePicURL = school.getProfilePhotoUrl();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    teacherClassStoryList.add(classStoryServer);
                                                }

                                                if (teacherFeedCounter == childrenCount) {
                                                    if (teacherClassStoryList.size() > 1) {
                                                        Collections.sort(teacherClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(teacherClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(teacherClassStoryList);
                                                    sharedPreferencesManager.setTeacherFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (posterAccountType.equals("Teacher") || posterAccountType.equals("Parent")) {
                                        String teacherID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                teacherFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    String posterName = teacher.getFirstName() + " " + teacher.getLastName();
                                                    String posterProfilePicURL = teacher.getProfilePicURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    teacherClassStoryList.add(classStoryServer);
                                                }

                                                if (teacherFeedCounter == childrenCount) {
                                                    if (teacherClassStoryList.size() > 1) {
                                                        Collections.sort(teacherClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(teacherClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(teacherClassStoryList);
                                                    sharedPreferencesManager.setTeacherFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                teacherFeedCounter++;
                                                if (dataSnapshot.exists()) {
                                                    Admin admin = dataSnapshot.getValue(Admin.class);
                                                    String posterName = admin.getDisplayName();
                                                    String posterProfilePicURL = admin.getProfilePictureURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    teacherClassStoryList.add(classStoryServer);
                                                }

                                                if (teacherFeedCounter == childrenCount) {
                                                    if (teacherClassStoryList.size() > 1) {
                                                        Collections.sort(teacherClassStoryList, new Comparator<ClassStory>() {
                                                            @Override
                                                            public int compare(ClassStory o1, ClassStory o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(teacherClassStoryList);
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(teacherClassStoryList);
                                                    sharedPreferencesManager.setTeacherFeed(json);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else {
                                    teacherFeedCounter++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                loadMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadMessages() {
        inboxList = new ArrayList<>();
        final ArrayList<MessageList> subList = new ArrayList<>();

        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages Recent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inboxList.clear();
                    subList.clear();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Chats chat = postSnapshot.getValue(Chats.class);
                        final MessageList message = new MessageList();
                        message.setMessage(chat.getMessage());
                        message.setTime(chat.getDatestamp());
                        message.setReceived(chat.isReceived());
                        message.setSeen(chat.isSeen());
                        message.setReceiverID(chat.getReceiverID());
                        message.setSenderID(chat.getSenderID());
                        message.setSortableTime(chat.getSortableDate());

                        String otherPartyID;
                        if (chat.getSenderID().equals(mFirebaseUser.getUid())) {
                            otherPartyID = chat.getReceiverID();
                        } else {
                            otherPartyID = chat.getSenderID();
                        }

                        message.setOtherParty(otherPartyID);
                        subList.add(message);
                    }

                    if (childrenCount == subList.size()) {
                        for (MessageList lcvMessage : subList) {
                            final MessageList newMessage = lcvMessage;
                            String otherPartyID = newMessage.getOtherParty();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(newMessage.getOtherParty());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Parent parent = dataSnapshot.getValue(Parent.class);
                                        newMessage.setName(parent.getFirstName() + " " + parent.getLastName());
                                        newMessage.setProfilepicUrl(parent.getProfilePicURL());
                                        inboxList.add(newMessage);
                                    }

                                    if (subList.size() == inboxList.size()) {
                                        if (subList.size() > 1) {
                                            Collections.sort(subList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(subList);
//                                            subList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(subList);
                                        sharedPreferencesManager.setMessages(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(newMessage.getOtherParty());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        newMessage.setName(school.getSchoolName());
                                        newMessage.setProfilepicUrl(school.getProfilePhotoUrl());
                                        inboxList.add(newMessage);
                                    }

                                    if (subList.size() == inboxList.size()) {
                                        if (subList.size() > 1) {
                                            Collections.sort(subList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(subList);
//                                            subList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(subList);
                                        sharedPreferencesManager.setMessages(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(newMessage.getOtherParty());
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Admin admin = dataSnapshot.getValue(Admin.class);
                                        newMessage.setName(admin.getDisplayName());
                                        newMessage.setProfilepicUrl(admin.getProfilePictureURL());
                                        inboxList.add(newMessage);
                                    }

                                    if (subList.size() == inboxList.size()) {
                                        if (subList.size() > 1) {
                                            Collections.sort(subList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(subList);
//                                            subList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(subList);
                                        sharedPreferencesManager.setMessages(json);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                loadParentNotification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadParentNotification() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("NotificationParent").child(mFirebaseUser.getUid());
        mDatabaseReference./*orderByChild("time").*/limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    parentNotificationModelList.clear();
                    parentNotificationCounter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final NotificationModel notificationModel = postSnapshot.getValue(NotificationModel.class);
                        notificationModel.setSortableTime(Date.convertToSortableDate(notificationModel.getTime()));

                        if (notificationModel.getFromAccountType().equals("School")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    parentNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        notificationModel.setFromName(school.getSchoolName());
                                        notificationModel.setFromProfilePicture(school.getProfilePhotoUrl());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        parentNotificationModelList.add(notificationModel);
//                                    }

                                    if (parentNotificationCounter == childrenCount) {
                                        if (parentNotificationModelList.size() > 1) {
                                            Collections.sort(parentNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(parentNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(parentNotificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else if (notificationModel.getFromAccountType().equals("Teacher") || notificationModel.getFromAccountType().equals("Parent")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    parentNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        notificationModel.setFromName(teacher.getFirstName() + " " + teacher.getLastName());
                                        notificationModel.setFromProfilePicture(teacher.getProfilePicURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        parentNotificationModelList.add(notificationModel);
//                                    }

                                    if (parentNotificationCounter == childrenCount) {
                                        if (parentNotificationModelList.size() > 1) {
                                            Collections.sort(parentNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(parentNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(parentNotificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    parentNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        Admin admin = dataSnapshot.getValue(Admin.class);
                                        notificationModel.setFromName(admin.getDisplayName());
                                        notificationModel.setFromProfilePicture(admin.getProfilePictureURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        parentNotificationModelList.add(notificationModel);
//                                    }

                                    if (parentNotificationCounter == childrenCount) {
                                        if (parentNotificationModelList.size() > 1) {
                                            Collections.sort(parentNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(parentNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(parentNotificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                loadTeacherNotification();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadTeacherNotification() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("NotificationTeacher").child(mFirebaseUser.getUid());
        mDatabaseReference./*orderByChild("time").*/limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherNotificationModelList.clear();
                    teacherNotificationCounter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final NotificationModel notificationModel = postSnapshot.getValue(NotificationModel.class);
                        notificationModel.setSortableTime(Date.convertToSortableDate(notificationModel.getTime()));

                        if (notificationModel.getFromAccountType().equals("School")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    teacherNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        notificationModel.setFromName(school.getSchoolName());
                                        notificationModel.setFromProfilePicture(school.getProfilePhotoUrl());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        teacherNotificationModelList.add(notificationModel);
//                                    }

                                    if (teacherNotificationCounter == childrenCount) {
                                        if (teacherNotificationModelList.size() > 1) {
                                            Collections.sort(teacherNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(teacherNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(teacherNotificationModelList);
                                        sharedPreferencesManager.setTeacherNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else if (notificationModel.getFromAccountType().equals("Teacher") || notificationModel.getFromAccountType().equals("Parent")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    teacherNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        notificationModel.setFromName(teacher.getFirstName() + " " + teacher.getLastName());
                                        notificationModel.setFromProfilePicture(teacher.getProfilePicURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        teacherNotificationModelList.add(notificationModel);
//                                    }

                                    if (teacherNotificationCounter == childrenCount) {
                                        if (teacherNotificationModelList.size() > 1) {
                                            Collections.sort(teacherNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(teacherNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(teacherNotificationModelList);
                                        sharedPreferencesManager.setTeacherNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    teacherNotificationCounter++;
                                    if (dataSnapshot.exists()) {
                                        Admin admin = dataSnapshot.getValue(Admin.class);
                                        notificationModel.setFromName(admin.getDisplayName());
                                        notificationModel.setFromProfilePicture(admin.getProfilePictureURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        teacherNotificationModelList.add(notificationModel);
//                                    }

                                    if (teacherNotificationCounter == childrenCount) {
                                        if (teacherNotificationModelList.size() > 1) {
                                            Collections.sort(teacherNotificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(teacherNotificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(teacherNotificationModelList);
                                        sharedPreferencesManager.setTeacherNotification(json);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                loadStudentSubscriptionInformationParent();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadStudentSubscriptionInformationParent() {
        subscriptionMap = new HashMap<>();
        subscriptionCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String studentID = postSnapshot.getKey();
                        subscriptionMap.put(studentID, new ArrayList<SubscriptionModel>());
                    }

                    for (final String studentID : subscriptionMap.keySet()) {
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Subscription").child(studentID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                subscriptionCounter++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        SubscriptionModel subscriptionModel = postSnapshot.getValue(SubscriptionModel.class);
                                        try {
                                            subscriptionMap.get(studentID).add(subscriptionModel);
                                        } catch (Exception e) {

                                        }

                                    }
                                }

                                if (subscriptionCounter == subscriptionMap.size()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(subscriptionMap);
                                    sharedPreferencesManager.setSubscriptionInformationParents(json);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                loadStudentSubscriptionInformationTeacher();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void loadStudentSubscriptionInformationTeacher() {
        subscriptionMapForTeachers = new HashMap<>();
        subscriptionCounterForTeachers = 0;

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class/" + mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final String classKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class Students").child(classKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                subscriptionCounterForTeachers++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        final String studentKey = postSnapshot.getKey();
                                        SubscriptionModel subscriptionModel = new SubscriptionModel();
                                        subscriptionModel.setStudentAccount(studentKey);
                                        subscriptionMapForTeachers.put(studentKey, subscriptionModel);
                                    }
                                }

                                if (childrenCount == subscriptionCounterForTeachers) {
                                    subscriptionCounterForTeachers = 0;
                                    for (final Map.Entry<String, SubscriptionModel> entry : subscriptionMapForTeachers.entrySet()) {
                                        String studentID = entry.getKey();
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Subscription").child(studentID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                subscriptionCounterForTeachers++;
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        SubscriptionModel subscriptionModel = postSnapshot.getValue(SubscriptionModel.class);
                                                        if (Date.compareDates(subscriptionModel.getExpiryDate(), entry.getValue().getExpiryDate())) {
                                                            subscriptionMapForTeachers.put(entry.getKey(), subscriptionModel);
                                                        }
                                                    }
                                                }

                                                if (subscriptionCounterForTeachers == subscriptionMapForTeachers.size()) {
                                                    Gson gson = new Gson();
                                                    String json = gson.toJson(subscriptionMapForTeachers);
                                                    sharedPreferencesManager.setSubscriptionInformationTeachers(json);
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
                }

                loadIsOpenToAll();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void loadIsOpenToAll() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("SystemValues").child("IsOpenToAll");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean isOpenToAll = dataSnapshot.getValue(Boolean.class);
                    sharedPreferencesManager.setIsOpenToAll(isOpenToAll);
                }

                applicationLauncherSharedPreferences.setLauncherActivity("Home");
                if (activeAccount.equals("Parent")) {
                    progressDialog.dismiss();
                    ((Activity)context).finishAffinity();
                    Intent intent = new Intent(((Activity)context), ParentMainActivityTwo.class);
                    ((Activity)context).startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    ((Activity)context).finishAffinity();
                    Intent intent = new Intent(((Activity)context), TeacherMainActivityTwo.class);
                    ((Activity)context).startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
