package com.celerii.celerii.models;

public class EClassroomScheduledClassesListModel {
    String scheduledClassID, classID, className, schoolID, teacherID, dateCreated, sortableDateCreated, dateScheduled, sortableDateScheduled, subject, description, classLink;
    Boolean open;

    public EClassroomScheduledClassesListModel() {
        this.scheduledClassID = "";
        this.classID = "";
        this.className = "";
        this.schoolID = "";
        this.teacherID = "";
        this.dateCreated = "";
        this.sortableDateCreated = "";
        this.dateScheduled = "";
        this.sortableDateScheduled = "";
        this.subject = "";
        this.description = "";
        this.classLink = "";
        this.open = true;
    }

    public EClassroomScheduledClassesListModel(String scheduledClassID, String classID, String className, String schoolID, String teacherID, String dateCreated, String sortableDateCreated, String dateScheduled, String sortableDateScheduled, String subject, String description, String classLink) {
        this.scheduledClassID = scheduledClassID;
        this.classID = classID;
        this.className = className;
        this.schoolID = schoolID;
        this.teacherID = teacherID;
        this.dateCreated = dateCreated;
        this.sortableDateCreated = sortableDateCreated;
        this.dateScheduled = dateScheduled;
        this.sortableDateScheduled = sortableDateScheduled;
        this.subject = subject;
        this.description = "";
        this.classLink = classLink;
        this.open = true;
    }

    public EClassroomScheduledClassesListModel(String scheduledClassID, String classID, String className, String schoolID, String teacherID, String dateCreated, String sortableDateCreated, String dateScheduled, String sortableDateScheduled, String subject, String description, String classLink, Boolean open) {
        this.scheduledClassID = scheduledClassID;
        this.classID = classID;
        this.className = className;
        this.schoolID = schoolID;
        this.teacherID = teacherID;
        this.dateCreated = dateCreated;
        this.sortableDateCreated = sortableDateCreated;
        this.dateScheduled = dateScheduled;
        this.sortableDateScheduled = sortableDateScheduled;
        this.subject = subject;
        this.description = "";
        this.classLink = classLink;
        this.open = open;
    }

    public String getScheduledClassID() {
        return scheduledClassID;
    }

    public void setScheduledClassID(String scheduledClassID) {
        this.scheduledClassID = scheduledClassID;
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

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getSortableDateCreated() {
        return sortableDateCreated;
    }

    public void setSortableDateCreated(String sortableDateCreated) {
        this.sortableDateCreated = sortableDateCreated;
    }

    public String getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(String dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public String getSortableDateScheduled() {
        return sortableDateScheduled;
    }

    public void setSortableDateScheduled(String sortableDateScheduled) {
        this.sortableDateScheduled = sortableDateScheduled;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassLink() {
        return classLink;
    }

    public void setClassLink(String classLink) {
        this.classLink = classLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
