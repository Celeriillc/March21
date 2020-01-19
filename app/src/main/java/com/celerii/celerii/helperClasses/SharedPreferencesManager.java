package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by DELL on 8/17/2017.
 */

public class SharedPreferencesManager {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String active_Account = "Parent";
    static final String my_User_ID = "myUserID";
    static final String my_First_Name = "my_FirstName";
    static final String my_Last_Name = "my_LastName";
    static final String my_pic_URL = "my_picURL";
    static final String active_Class = "activeClass";
    static final String active_Kid = "activeKid";
    static final String my_Classes = "myClasses";
    static final String my_Children = "myChildren";
    static final String classes_students_parents = "classesStudentsParents";
    static final String classes_students = "classesStudents";
    static final String students_schools_classes_teachers = "studentsSchoolsClassesTeachers";

    public SharedPreferencesManager(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("AltariiPreferences", 0);
        editor = prefs.edit();
    }

    //region ActiveAccount
    public String getActiveAccount() {
        return prefs.getString(active_Account, null);
    }

    public void setActiveAccount(String activeAccount) {
        editor.putString(active_Account, activeAccount);
        editor.commit();
    }

    public void deleteActiveAccount() {
        editor.remove(active_Account);
        editor.commit();
    }
    //endregion

    //region MyUserID
    public String getMyUserID() {
        return prefs.getString(my_User_ID, null);
    }

    public void setMyUserID(String myUserID) {
        editor.putString(my_User_ID, myUserID);
        editor.commit();
    }

    public void deleteMyUserID() {
        editor.remove(my_User_ID);
        editor.commit();
    }
    //endregion

    //region MyFirstName
    public String getMyFirstName() {
        return prefs.getString(my_First_Name, "");
    }

    public void setMyFirstName(String myFirstName) {
        editor.putString(my_First_Name, myFirstName);
        editor.commit();
    }

    public void deleteMyFirstName() {
        editor.remove(my_First_Name);
        editor.commit();
    }
    //endregion

    //region MyLastName
    public String getMyLastName() {
        return prefs.getString(my_Last_Name, "");
    }

    public void setMyLastName(String myLastName) {
        editor.putString(my_Last_Name, myLastName);
        editor.commit();
    }

    public void deleteMyLastName() {
        editor.remove(my_Last_Name);
        editor.commit();
    }
    //endregion

    //region MyPicURL
    public String getMyPicURL() {
        return prefs.getString(my_pic_URL, "");
    }

    public void setMyPicURL(String myPicURL) {
        editor.putString(my_pic_URL, myPicURL);
        editor.commit();
    }

    public void deleteMyPicURL() {
        editor.remove(my_pic_URL);
        editor.commit();
    }
    //endregion

    //region ActiveClass
    public String getActiveClass() {
        return prefs.getString(active_Class, null);
    }

    public void setActiveClass(String activeClass) {
        editor.putString(active_Class, activeClass);
        editor.commit();
    }

    public void deleteActiveClass() {
        editor.remove(active_Class);
        editor.commit();
    }
    //endregion

    //region ActiveKid
    public String getActiveKid() {
        return prefs.getString(active_Kid, null);
    }

    public void setActiveKid(String activeKid) {
        editor.putString(active_Kid, activeKid);
        editor.commit();
    }

    public void deleteActiveKid() {
        editor.remove(active_Kid);
        editor.commit();
    }
    //endregion

    //region myClasses
    public Set<String> getMyClasses() {
        return prefs.getStringSet(my_Classes, null);
    }

    public void setMyClasses(Set<String> myClasses) {
        editor.putStringSet(my_Classes, myClasses);
        editor.commit();
    }

    public void deleteMyClasses() {
        editor.remove(my_Classes);
        editor.commit();
    }
    //endregion

    //region myChildren
    public Set<String> getMyChildren() {
        return prefs.getStringSet(my_Children, null);
    }

    public void setMyChildren(Set<String> myChildren) {
        editor.putStringSet(my_Children, myChildren);
        editor.commit();
    }

    public void deleteMyChildren() {
        editor.remove(my_Children);
        editor.commit();
    }
    //endregion

    //region ClassesStudentParent
    public String getClassesStudentParent() {
        return prefs.getString(classes_students_parents, null);
    }

    public void setClassesStudentParent(String myClassesStudentParent) {
        editor.putString(classes_students_parents, myClassesStudentParent);
        editor.commit();
    }

    public void deleteClassesStudentParent() {
        editor.remove(classes_students_parents);
        editor.commit();
    }
    //endregion

    //region ClassesStudent
    public String getClassesStudent() {
        return prefs.getString(classes_students, null);
    }

    public void setClassesStudent(String myClassesStudent) {
        editor.putString(classes_students, myClassesStudent);
        editor.commit();
    }

    public void deleteClassesStudent() {
        editor.remove(classes_students);
        editor.commit();
    }
    //endregion

    //region StudentsSchoolsClassesTeachers
    public String getStudentsSchoolsClassesTeachers() {
        return prefs.getString(students_schools_classes_teachers, null);
    }

    public void setStudentsSchoolsClassesTeachers(String myStudentsSchoolsClassesTeachers) {
        editor.putString(students_schools_classes_teachers, myStudentsSchoolsClassesTeachers);
        editor.commit();
    }

    public void deleteStudentsSchoolsClassesTeachers() {
        editor.remove(students_schools_classes_teachers);
        editor.commit();
    }
    //endregion

    public void clear() {
        deleteActiveAccount();
        deleteMyUserID();
        deleteMyFirstName();
        deleteMyLastName();
        deleteMyPicURL();
        deleteActiveKid();
        deleteActiveClass();
        deleteMyClasses();
        deleteMyChildren();
        deleteClassesStudentParent();
        deleteClassesStudent();
        deleteStudentsSchoolsClassesTeachers();
    }
}
