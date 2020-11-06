package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class School {
    String schoolName, state, location, country, city, aboutUs, yearOfEstablishment, motto, numberOfEmployees, size, schoolFeesRange;
    String profilePhotoUrl, backgroundPhotoUrl, email, website;
    String searchableSchoolName, searchableLocation;
    Boolean isDeleted;

    public School() {
        schoolName = "";
        state = "";
        location = "";
        country = "";
        city = "";
        aboutUs = "";
        yearOfEstablishment = "";
        motto = "";
        numberOfEmployees = "";
        size = "";
        schoolFeesRange = "";
        profilePhotoUrl = "";
        backgroundPhotoUrl = "";
        email = "";
        website = "";
        searchableSchoolName = "";
        searchableLocation = "";
        isDeleted = false;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getYearOfEstablishment() {
        return yearOfEstablishment;
    }

    public void setYearOfEstablishment(String yearOfEstablishment) {
        this.yearOfEstablishment = yearOfEstablishment;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(String numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSchoolFeesRange() {
        return schoolFeesRange;
    }

    public void setSchoolFeesRange(String schoolFeesRange) {
        this.schoolFeesRange = schoolFeesRange;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getBackgroundPhotoUrl() {
        return backgroundPhotoUrl;
    }

    public void setBackgroundPhotoUrl(String backgroundPhotoUrl) {
        this.backgroundPhotoUrl = backgroundPhotoUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSearchableSchoolName() {
        return searchableSchoolName;
    }

    public void setSearchableSchoolName(String searchableSchoolName) {
        this.searchableSchoolName = searchableSchoolName;
    }

    public String getSearchableLocation() {
        return searchableLocation;
    }

    public void setSearchableLocation(String searchableLocation) {
        this.searchableLocation = searchableLocation;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
