package com.celerii.celerii.models;

/**
 * Created by DELL on 8/18/2018.
 */

public class TeacherSchoolConnectionRequest {
    String status, timeSent, sorttableTimeSent, teacher, school, schoolName, schoolProfilePictureURL, sender;

    public TeacherSchoolConnectionRequest() {
        this.status = "";
        this.timeSent = "";
        this.sorttableTimeSent = "";
        this.teacher = "";
        this.school = "";
        this.schoolName = "";
        this.schoolProfilePictureURL = "";
        this.sender = "";
    }

    public TeacherSchoolConnectionRequest(String status, String timeSent, String sorttableTimeSent, String teacher, String school) {
        this.status = status;
        this.timeSent = timeSent;
        this.sorttableTimeSent = sorttableTimeSent;
        this.teacher = teacher;
        this.school = school;
        this.schoolName = "";
        this.schoolProfilePictureURL = "";
        this.sender = "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getSorttableTimeSent() {
        return sorttableTimeSent;
    }

    public void setSorttableTimeSent(String sorttableTimeSent) {
        this.sorttableTimeSent = sorttableTimeSent;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolProfilePictureURL() {
        return schoolProfilePictureURL;
    }

    public void setSchoolProfilePictureURL(String schoolProfilePictureURL) {
        this.schoolProfilePictureURL = schoolProfilePictureURL;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
