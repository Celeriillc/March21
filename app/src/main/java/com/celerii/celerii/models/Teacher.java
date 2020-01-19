package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class Teacher {
    String firstName;
    String lastName;
    String middleName;
    String email;
    String phone;
    String profilePicURL;
    String gender;
    String location;
    String maritalStatus;
    String availableForHomeLessons;
    String state;
    String bio;

    public Teacher() {
        firstName = "";
        lastName = "";
        middleName = "";
        email = "";
        phone = "";
        profilePicURL = "";
        gender = "";
        location = "";
        maritalStatus = "";
        availableForHomeLessons = "";
        state = "";
        bio = "";
    }

    public Teacher(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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
}
