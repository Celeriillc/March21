package com.celerii.celerii.models;

public class ELibraryAssignmentStudentPerformanceModel {
    String studentID, totalQuestions, correctAnswers;

    public ELibraryAssignmentStudentPerformanceModel() {
        this.studentID = "";
        this.totalQuestions = "";
        this.correctAnswers = "";
    }

    public ELibraryAssignmentStudentPerformanceModel(String studentID, String totalQuestions, String correctAnswers) {
        this.studentID = studentID;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
