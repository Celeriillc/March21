package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class Teacher {
    String firstName;
    String lastName;
    String middleName;
    String searchableFirstName;
    String searchableLastName;
    String searchableMiddleName;
    String email;
    String phone;
    String profilePicURL;
    String gender;
    String location;
    String maritalStatus;
    String availableForHomeLessons;
    String state;
    String bio;
    Boolean isDeleted = false;

    public Teacher() {
        firstName = "";
        lastName = "";
        middleName = "";
        searchableFirstName = "";
        searchableLastName = "";
        searchableMiddleName = "";
        email = "";
        phone = "";
        profilePicURL = "";
        gender = "";
        location = "";
        maritalStatus = "Nil";
        availableForHomeLessons = "";
        state = "";
        bio = "";
        isDeleted = false;
    }

    public Teacher(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Teacher(String firstName, String lastName, String searchableFirstName, String searchableLastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.searchableFirstName = searchableFirstName;
        this.searchableLastName = searchableLastName;
    }

    public Teacher(String firstName, String lastName, String email, String profilePicURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicURL = profilePicURL;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvailableForHomeLessons() {
        return availableForHomeLessons;
    }

    public void setAvailableForHomeLessons(String availableForHomeLessons) {
        this.availableForHomeLessons = availableForHomeLessons;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
