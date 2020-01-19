package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class Student {
    String firstName, lastName, middleName, imageURL, gender, studentID;

    public Student(){
        this.firstName = "";
        this.lastName = "";
        this.middleName = "";
        this.imageURL = "";
        this.gender = "";
    }

    public Student(String firstName, String lastName, String middleName, String imageURL, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.imageURL = imageURL;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
}
