package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class Parent {
    String firstName;
    String lastName;
    String email;
    String middleName;
    String searchableFirstName;
    String searchableLastName;
    String searchableMiddleName;
    String gender;
    String occupation;
    String profilePicURL;
    String location;
    String maritalStatus;
    String phone;
    Boolean isDeleted;

    public Parent() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.middleName = "";
        this.searchableFirstName = "";
        this.searchableLastName = "";
        this.searchableMiddleName = "";
        this.gender = "";
        this.occupation = "";
        this.profilePicURL = "";
        this.location = "";
        this.maritalStatus = "";
        this.phone = "";
        this.isDeleted = false;
    }

    public Parent(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Parent(String firstName, String lastName, String searchableFirstName, String searchableLastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.searchableFirstName = searchableFirstName;
        this.searchableLastName = searchableLastName;
    }

    public Parent(String firstName, String lastName, String email, String middleName, String gender, String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.middleName = middleName;
        this.gender = gender;
        this.occupation = occupation;
    }

    public Parent(String firstName, String lastName, String email, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }

    public Parent(String firstName, String lastName, String email, String middleName, String gender, String occupation, String profilePicURL, String location, String maritalStatus, String phone, Boolean isDeleted) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.middleName = middleName;
        this.gender = gender;
        this.occupation = occupation;
        this.profilePicURL = profilePicURL;
        this.location = location;
        this.maritalStatus = maritalStatus;
        this.phone = phone;
        this.isDeleted = isDeleted;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
