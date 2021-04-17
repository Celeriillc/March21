package com.celerii.celerii.models;

public class ELibraryMyAssignmentModel {
    String assignmentID, teacherID, schoolID, classID, className, dateGiven, sortableDateGiven, dueDate, sortableDateDue, materialTitle, materialType,
            materialThumbnailURL, materialDescription, materialID, materialDomain, performance;

    public ELibraryMyAssignmentModel() {
        this.assignmentID = "";
        this.teacherID = "";
        this.schoolID = "";
        this.classID = "";
        this.dateGiven = "";
        this.sortableDateGiven = "";
        this.dueDate = "";
        this.sortableDateDue = "";
        this.materialTitle = "";
        this.materialType = "";
        this.materialThumbnailURL = "";
        this.materialDescription = "";
        this.materialID = "";
        this.materialDomain = "";
    }

    public ELibraryMyAssignmentModel(String assignmentID, String teacherID, String schoolID, String classID, String dateGiven, String sortableDateGiven, String dueDate, String sortableDateDue, String materialTitle, String materialType, String materialThumbnailURL, String materialDescription, String materialID, String materialDomain) {
        this.assignmentID = assignmentID;
        this.teacherID = teacherID;
        this.schoolID = schoolID;
        this.classID = classID;
        this.dateGiven = dateGiven;
        this.sortableDateGiven = sortableDateGiven;
        this.dueDate = dueDate;
        this.sortableDateDue = sortableDateDue;
        this.materialTitle = materialTitle;
        this.materialType = materialType;
        this.materialThumbnailURL = materialThumbnailURL;
        this.materialDescription = materialDescription;
        this.materialID = materialID;
        this.materialDomain = materialDomain;
    }

    public String getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.assignmentID = assignmentID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
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

    public String getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(String dateGiven) {
        this.dateGiven = dateGiven;
    }

    public String getSortableDateGiven() {
        return sortableDateGiven;
    }

    public void setSortableDateGiven(String sortableDateGiven) {
        this.sortableDateGiven = sortableDateGiven;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getSortableDateDue() {
        return sortableDateDue;
    }

    public void setSortableDateDue(String sortableDateDue) {
        this.sortableDateDue = sortableDateDue;
    }

    public String getMaterialTitle() {
        return materialTitle;
    }

    public void setMaterialTitle(String materialTitle) {
        this.materialTitle = materialTitle;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialThumbnailURL() {
        return materialThumbnailURL;
    }

    public void setMaterialThumbnailURL(String materialThumbnailURL) {
        this.materialThumbnailURL = materialThumbnailURL;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getMaterialDomain() {
        return materialDomain;
    }

    public void setMaterialDomain(String materialDomain) {
        this.materialDomain = materialDomain;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }
}
