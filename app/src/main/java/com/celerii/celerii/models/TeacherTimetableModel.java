package com.celerii.celerii.models;

public class TeacherTimetableModel {
    String teacherID, classID, className, pushKey, subject, dayOfTheWeek, timeOfTheDay, duration;

    public TeacherTimetableModel() {
        this.teacherID = "";
        this.classID = "";
        this.className = "";
        this.pushKey = "";
        this.subject = "";
        this.dayOfTheWeek = "";
        this.timeOfTheDay = "";
        this.duration = "";
    }

    public TeacherTimetableModel(String teacherID, String classID, String className, String pushKey, String subject, String dayOfTheWeek, String timeOfTheDay, String duration) {
        this.teacherID = teacherID;
        this.classID = classID;
        this.className = className;
        this.pushKey = pushKey;
        this.subject = subject;
        this.dayOfTheWeek = dayOfTheWeek;
        this.timeOfTheDay = timeOfTheDay;
        this.duration = duration;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public String getTimeOfTheDay() {
        return timeOfTheDay;
    }

    public void setTimeOfTheDay(String timeOfTheDay) {
        this.timeOfTheDay = timeOfTheDay;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
