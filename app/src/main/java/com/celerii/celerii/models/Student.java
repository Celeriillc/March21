package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class Student {
    String firstName, lastName, middleName, searchableFirstName, searchableLastName, searchableMiddleName,
            imageURL, gender, studentID, bio;

    public Student(){
        this.firstName = "";
        this.lastName = "";
        this.middleName = "";
        this.searchableFirstName = "";
        this.searchableLastName = "";
        this.searchableMiddleName = "";
        this.imageURL = "";
        this.gender = "";
        this.studentID = "";
        this.bio = "";
    }

    public Student(String firstName, String studentID){
        this.firstName = firstName;
        this.lastName = "";
        this.middleName = "";
        this.searchableFirstName = firstName.toLowerCase();
        this.searchableLastName = "";
        this.searchableMiddleName = "";
        this.imageURL = "";
        this.gender = "";
        this.studentID = studentID;
        this.bio = "";
    }

    public Student(String firstName, String studentID, String imageURL){
        this.firstName = firstName;
        this.lastName = "";
        this.middleName = "";
        this.searchableFirstName = firstName.toLowerCase();
        this.searchableLastName = "";
        this.searchableMiddleName = "";
        this.imageURL = imageURL;
        this.gender = "";
        this.studentID = studentID;
        this.bio = "";
    }

    public Student(String firstName, String lastName, String imageURL, String studentID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = "";
        this.searchableFirstName = firstName.toLowerCase();
        this.searchableLastName = lastName.toLowerCase();
        this.searchableMiddleName = "";
        this.imageURL = imageURL;
        this.gender = "";
        this.studentID = studentID;
        this.bio = "";
    }

    public Student(String firstName, String lastName, String middleName, String imageURL, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.searchableFirstName = firstName.toLowerCase();
        this.searchableLastName = lastName.toLowerCase();
        this.searchableMiddleName = middleName.toLowerCase();
        this.imageURL = imageURL;
        this.gender = gender;
        this.bio = "";
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

    public String getSearchableFirstName() {
        return searchableFirstName;
    }

    public void setSearchableFirstName(String searchableFirstName) {
        this.searchableFirstName = searchableFirstName;
    }

    public String getSearchableLastName() {
        return searchableLastName;
    }

    public void setSearchableLastName(String searchableLastName) {
        this.searchableLastName = searchableLastName;
    }

    public String getSearchableMiddleName() {
        return searchableMiddleName;
    }

    public void setSearchableMiddleName(String searchableMiddleName) {
        this.searchableMiddleName = searchableMiddleName;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
