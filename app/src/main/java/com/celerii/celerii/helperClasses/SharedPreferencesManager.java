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
    static final String my_middle_name = "myMiddleName";
    static final String my_Last_Name = "my_LastName";
    static final String my_pic_URL = "my_picURL";
    static final String my_phone_number = "myPhoneNumber";
    static final String my_gender = "myGender";
    static final String my_relationship_status = "myRelationshipStatus";
    static final String my_bio = "myBio";
    static final String my_occupation = "myOccupation";
    static final String active_Class = "activeClass";
    static final String active_Kid = "activeKid";
    static final String my_Classes = "myClasses";
    static final String my_Children = "myChildren";
    static final String classes_students_parents = "classesStudentsParents";
    static final String classes_students = "classesStudents";
    static final String students_schools_classes_teachers = "studentsSchoolsClassesTeachers";
    static final String students_classes = "studentsClasses";
    static final String subjects = "subjects";
    static final String class_student_for_teacher = "classStudentForTeacher";
    static final String parent_feed = "parentFeed";
    static final String teacher_feed = "teacherFeed";
    static final String messages = "messages";
    static final String parent_notification = "parentNotification";
    static final String teacher_notification = "teacherNotification";
    static final String subscription_information_parents = "subscriptionInformationParents";
    static final String subscription_information_teachers = "subscriptionInformationTeachers";
    static final String is_open_to_all = "isOpenToAll";
    static final String current_login_session_key = "currentLoginSessionKey";
    static final String current_login_session_day_month_year = "currentLoginSessionDayMonthYear";
    static final String current_login_session_month_year = "currentLoginSessionMonthYear";
    static final String current_login_session_year = "currentLoginSessionYear";
    static final String my_referral_link = "myReferralLink";
    static final String my_referral_text = "myReferralText";
    static final String my_referral_subject = "myReferralSubject";
    static final String my_secondary_referral_subject = "mySecondaryReferralSubject";

    public SharedPreferencesManager(Context context) {
        this.context = context;
        if (this.context == null) {
            return;
        }
        prefs = this.context.getSharedPreferences("AltariiPreferences", 0);
        editor = prefs.edit();
    }

    //region ActiveAccount
    public String getActiveAccount() {
        return prefs.getString(active_Account, "");
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
        return prefs.getString(my_User_ID, "");
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

    //region MyMiddleName
    public String getMyMiddleName() {
        return prefs.getString(my_middle_name, "");
    }

    public void setMyMiddleName(String myMiddleName) {
        editor.putString(my_middle_name, myMiddleName);
        editor.commit();
    }

    public void deleteMyMiddleName() {
        editor.remove(my_middle_name);
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

    //region MyPhoneNumber
    public String getMyPhoneNumber() {
        return prefs.getString(my_phone_number, "");
    }

    public void setMyPhoneNumber(String myPhoneNumber) {
        editor.putString(my_phone_number, myPhoneNumber);
        editor.commit();
    }

    public void deleteMyPhoneNumber() {
        editor.remove(my_phone_number);
        editor.commit();
    }
    //endregion

    //region MyGender
    public String getMyGender() {
        return prefs.getString(my_gender, "");
    }

    public void setMyGender(String myGender) {
        editor.putString(my_gender, myGender);
        editor.commit();
    }

    public void deleteMyGender() {
        editor.remove(my_gender);
        editor.commit();
    }
    //endregion

    //region MyRelationshipStatus
    public String getMyRelationshipStatus() {
        return prefs.getString(my_relationship_status, "");
    }

    public void setMyRelationshipStatus(String myRelationshipStatus) {
        editor.putString(my_relationship_status, myRelationshipStatus);
        editor.commit();
    }

    public void deleteMyRelationshipStatus() {
        editor.remove(my_relationship_status);
        editor.commit();
    }
    //endregion

    //region MyBio
    public String getMyBio() {
        return prefs.getString(my_bio, "");
    }

    public void setMyBio(String myBio) {
        editor.putString(my_bio, myBio);
        editor.commit();
    }

    public void deleteMyBio() {
        editor.remove(my_bio);
        editor.commit();
    }
    //endregion

    //region MyOccupation
    public String getMyOccupation() {
        return prefs.getString(my_occupation, "");
    }

    public void setMyOccupation(String myOccupation) {
        editor.putString(my_occupation, myOccupation);
        editor.commit();
    }

    public void deleteMyOccupation() {
        editor.remove(my_occupation);
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
//    public Set<String> getMyClasses() {
//        return prefs.getStringSet(my_Classes, null);
//    }
//
//    public void setMyClasses(Set<String> myClasses) {
//        editor.putStringSet(my_Classes, myClasses);
//        editor.commit();
//    }
//
//    public void deleteMyClasses() {
//        editor.remove(my_Classes);
//        editor.commit();
//    }
//    //endregion
//
//    //region myChildren
//    public Set<String> getMyChildren() {
//        return prefs.getStringSet(my_Children, null);
//    }
//
//    public void setMyChildren(Set<String> myChildren) {
//        editor.putStringSet(my_Children, myChildren);
//        editor.commit();
//    }
//
//    public void deleteMyChildren() {
//        editor.remove(my_Children);
//        editor.commit();
//    }
    //endregion

    //region myClasses
    public String getMyClasses() {
        return prefs.getString(my_Classes, null);
    }

    public void setMyClasses(String myClasses) {
        editor.putString(my_Classes, myClasses);
        editor.commit();
    }

    public void deleteMyClasses() {
        editor.remove(my_Classes);
        editor.commit();
    }
    //endregion

    //region myChildren
    public String getMyChildren() {
        return prefs.getString(my_Children, null);
    }

    public void setMyChildren(String myChildren) {
        editor.putString(my_Children, myChildren);
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

    //region StudentsClasses
    public String getStudentsClasses() {
        return prefs.getString(students_classes, null);
    }

    public void setStudentsClasses(String myStudentsClasses) {
        editor.putString(students_classes, myStudentsClasses);
        editor.commit();
    }

    public void deleteStudentsClasses() {
        editor.remove(students_classes);
        editor.commit();
    }
    //endregion

    //region Subjects
    public String getSubjects() {
        return prefs.getString(subjects, null);
    }

    public void setSubjects(String subjects) {
        editor.putString(this.subjects, subjects);
        editor.commit();
    }

    public void deleteSubjects() {
        editor.remove(subjects);
        editor.commit();
    }
    //endregion

    //region ParentFeed
    public String getParentFeed() {
        return prefs.getString(parent_feed, null);
    }

    public void setParentFeed(String parentFeed) {
        editor.putString(parent_feed, parentFeed);
        editor.commit();
    }

    public void deleteParentFeed() {
        editor.remove(parent_feed);
        editor.commit();
    }
    //endregion

    //region TeacherFeed
    public String getTeacherFeed() {
        return prefs.getString(teacher_feed, null);
    }

    public void setTeacherFeed(String teacherFeed) {
        editor.putString(teacher_feed, teacherFeed);
        editor.commit();
    }

    public void deleteTeacherFeed() {
        editor.remove(teacher_feed);
        editor.commit();
    }
    //endregion

    //region ClassStudentForTeacher
    public String getClassStudentForTeacher() {
        return prefs.getString(class_student_for_teacher, null);
    }

    public void setClassStudentForTeacher(String classStudentForTeacher) {
        editor.putString(class_student_for_teacher, classStudentForTeacher);
        editor.commit();
    }

    public void deleteClassStudentForTeacher() {
        editor.remove(class_student_for_teacher);
        editor.commit();
    }
    //endregion

    //region Messages
    public String getMessages() {
        return prefs.getString(messages, null);
    }

    public void setMessages(String messagesA) {
        editor.putString(messages, messagesA);
        editor.commit();
    }

    public void deleteMessages() {
        editor.remove(messages);
        editor.commit();
    }
    //endregion

    //region ParentNotification
    public String getParentNotification() {
        return prefs.getString(parent_notification, null);
    }

    public void setParentNotification(String parentNotification) {
        editor.putString(parent_notification, parentNotification);
        editor.commit();
    }

    public void deleteParentNotification() {
        editor.remove(parent_notification);
        editor.commit();
    }
    //endregion

    //region TeacherNotification
    public String getTeacherNotification() {
        return prefs.getString(teacher_notification, null);
    }

    public void setTeacherNotification(String teacherNotification) {
        editor.putString(teacher_notification, teacherNotification);
        editor.commit();
    }

    public void deleteTeacherNotification() {
        editor.remove(teacher_notification);
        editor.commit();
    }
    //endregion

    //region SubscriptionInformationParents
    public String getSubscriptionInformationParents() {
        return prefs.getString(subscription_information_parents, null);
    }

    public void setSubscriptionInformationParents(String subscriptionInformationParents) {
        editor.putString(subscription_information_parents, subscriptionInformationParents);
        editor.commit();
    }

    public void deleteSubscriptionInformationParents() {
        editor.remove(subscription_information_parents);
        editor.commit();
    }
    //endregion

    //region SubscriptionInformationTeachers
    public String getSubscriptionInformationTeachers() {
        return prefs.getString(subscription_information_teachers, null);
    }

    public void setSubscriptionInformationTeachers(String subscriptionInformationTeachers) {
        editor.putString(subscription_information_teachers, subscriptionInformationTeachers);
        editor.commit();
    }

    public void deleteSubscriptionInformationTeachers() {
        editor.remove(subscription_information_teachers);
        editor.commit();
    }
    //endregion

    //region IsOpenToAll
    public Boolean getIsOpenToAll() {
        return prefs.getBoolean(is_open_to_all, false);
    }

    public void setIsOpenToAll(Boolean isOpenToAll) {
        editor.putBoolean(is_open_to_all, isOpenToAll);
        editor.commit();
    }

    public void deleteIsOpenToAll() {
        editor.remove(is_open_to_all);
        editor.commit();
    }
    //endregion

    //region CurrentLoginSessionKey
    public String getCurrentLoginSessionKey() {
        return prefs.getString(current_login_session_key, null);
    }

    public void setCurrentLoginSessionKey(String currentLoginSessionKey) {
        editor.putString(current_login_session_key, currentLoginSessionKey);
        editor.commit();
    }

    public void deleteCurrentLoginSessionKey() {
        editor.remove(current_login_session_key);
        editor.commit();
    }
    //endregion

    //region CurrentLoginSessionDayMonthYear
    public String getCurrentLoginSessionDayMonthYear() {
        return prefs.getString(current_login_session_day_month_year, null);
    }

    public void setCurrentLoginSessionDayMonthYear(String currentLoginSessionDayMonthYear) {
        editor.putString(current_login_session_day_month_year, currentLoginSessionDayMonthYear);
        editor.commit();
    }

    public void deleteCurrentLoginSessionDayMonthYear() {
        editor.remove(current_login_session_day_month_year);
        editor.commit();
    }
    //endregion

    //region CurrentLoginSessionMonthYear
    public String getCurrentLoginSessionMonthYear() {
        return prefs.getString(current_login_session_month_year, null);
    }

    public void setCurrentLoginSessionMonthYear(String currentLoginSessionMonthYear) {
        editor.putString(current_login_session_month_year, currentLoginSessionMonthYear);
        editor.commit();
    }

    public void deleteCurrentLoginSessionMonthYear() {
        editor.remove(current_login_session_month_year);
        editor.commit();
    }
    //endregion

    //region CurrentLoginSessionYear
    public String getCurrentLoginSessionYear() {
        return prefs.getString(current_login_session_year, null);
    }

    public void setCurrentLoginSessionYear(String currentLoginSessionYear) {
        editor.putString(current_login_session_year, currentLoginSessionYear);
        editor.commit();
    }

    public void deleteCurrentLoginSessionYear() {
        editor.remove(current_login_session_year);
        editor.commit();
    }
    //endregion

    //region MyReferalLink
    public String getMyReferralLink() {
        return prefs.getString(my_referral_link, "");
    }

    public void setMyReferralLink(String myReferralLink) {
        editor.putString(my_referral_link, myReferralLink);
        editor.commit();
    }

    public void deleteMyReferralLink() {
        editor.remove(my_referral_link);
        editor.commit();
    }
    //endregion

    //region MyReferalText
    public String getMyReferralText() {
        return prefs.getString(my_referral_text, "");
    }

    public void setMyReferralText(String myReferralText) {
        editor.putString(my_referral_text, myReferralText);
        editor.commit();
    }

    public void deleteMyReferralText() {
        editor.remove(my_referral_text);
        editor.commit();
    }
    //endregion

    //region MyReferralSubject
    public String getMyReferralSubject() {
        return prefs.getString(my_referral_subject, "");
    }

    public void setMyReferralSubject(String myReferralSubject) {
        editor.putString(my_referral_subject, myReferralSubject);
        editor.commit();
    }

    public void deleteMyReferralSubject() {
        editor.remove(my_referral_subject);
        editor.commit();
    }
    //endregion

    //region MySecondaryReferralSubject
    public String getMySecondaryReferralSubject() {
        return prefs.getString(my_secondary_referral_subject, "");
    }

    public void setMySecondaryReferralSubject(String myReferralSubject) {
        editor.putString(my_secondary_referral_subject, myReferralSubject);
        editor.commit();
    }

    public void deleteMySecondaryReferralSubject() {
        editor.remove(my_secondary_referral_subject);
        editor.commit();
    }
    //endregion

    public void clear() {
        deleteActiveAccount();
        deleteMyUserID();
        deleteMyFirstName();
        deleteMyMiddleName();
        deleteMyLastName();
        deleteMyPicURL();
        deleteMyPhoneNumber();
        deleteMyGender();
        deleteMyRelationshipStatus();
        deleteMyBio();
        deleteMyOccupation();
        deleteActiveKid();
        deleteActiveClass();
        deleteMyClasses();
        deleteMyChildren();
        deleteClassesStudentParent();
        deleteClassesStudent();
        deleteStudentsSchoolsClassesTeachers();
        deleteStudentsClasses();
        deleteSubjects();
        deleteClassStudentForTeacher();
        deleteParentFeed();
        deleteTeacherFeed();
        deleteMessages();
        deleteParentNotification();
        deleteTeacherNotification();
        deleteSubscriptionInformationParents();
        deleteCurrentLoginSessionKey();
        deleteCurrentLoginSessionDayMonthYear();
        deleteCurrentLoginSessionMonthYear();
        deleteCurrentLoginSessionYear();
        deleteMyReferralLink();
        deleteMyReferralText();
        deleteMyReferralSubject();
        deleteMySecondaryReferralSubject();
    }
}
