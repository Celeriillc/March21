package com.celerii.celerii.models;

/**
 * Created by user on 7/2/2017.
 */

public class ClassAssignment {

    String story, time, teacherID, imageURL, profilePicURL, timeDue, dueStatus;
    int numberOfComments, noOfViews;
    String classReciepient, url;

    public ClassAssignment(String story, String time, String timeDue, String dueStatus, String teacherID, String imageURL, int numberOfComments,
                      int noOfViews, String classReciepient, String url, String profilePicURL) {
        this.story = story;
        this.time = time;
        this.timeDue = timeDue;
        this.dueStatus = dueStatus;
        this.teacherID = teacherID;
        this.imageURL = imageURL;
        this.numberOfComments = numberOfComments;
        this.noOfViews = noOfViews;
        this.classReciepient = classReciepient;
        this.url = url;
        this.profilePicURL = profilePicURL;
    }

    public ClassAssignment(String story, String time, String timeDue, String dueStatus, String teacherID, String imageURL, String classReciepient, String url, String profilePicURL) {
        this.story = story;
        this.time = time;
        this.timeDue = timeDue;
        this.dueStatus = dueStatus;
        this.teacherID = teacherID;
        this.imageURL = imageURL;
        this.classReciepient = classReciepient;
        this.url = url;
        this.profilePicURL = profilePicURL;
    }

    public ClassAssignment(String story, String time, String timeDue, String dueStatus, String teacherID, String imageURL, String url) {
        this.story = story;
        this.time = time;
        this.timeDue = timeDue;
        this.dueStatus = dueStatus;
        this.imageURL = imageURL;
        this.teacherID = teacherID;
        this.url = url;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeDue() {
        return timeDue;
    }

    public void setTimeDue(String timeDue) {
        this.timeDue = timeDue;
    }

    public String getDueStatus() {
        return dueStatus;
    }

    public void setDueStatus(String dueStatus) {
        this.dueStatus = dueStatus;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getNoOfViews() {
        return noOfViews;
    }

    public void setNoOfViews(int noOfViews) {
        this.noOfViews = noOfViews;
    }

    public String getClassReciepient() {
        return classReciepient;
    }

    public void setClassReciepient(String classReciepient) {
        this.classReciepient = classReciepient;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
