package com.celerii.celerii.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 6/27/2017.
 */

public class ClassStory {
    private String story, date, sortableDate, posterID, posterAccountType, imageURL, profilePicURL, postID, posterName;
    private int numberOfComments, noOfLikes;
    private String classReciepient, url;
    private boolean isLiked = false;
    private List<String> classRecipients = new ArrayList<String>();
    private Comment comment;

    public ClassStory() {
        this.story = "";
        this.date = "";
        this.sortableDate = "";
        this.posterID = "";
        this.posterAccountType = "";
        this.imageURL = "";
        this.profilePicURL = "";
        this.postID = "";
        this.posterName = "";
        this.numberOfComments = 0;
        this.noOfLikes = 0;
        this.classReciepient = "";
        this.url = "";
        this.isLiked = false;
        this.imageURL = "";
        this.classRecipients = new ArrayList<String>();
    }

    public ClassStory(String story, String date, String sortableDate, String posterAccountType, String posterID, String posterName, String profilePicURL, String classReciepient, String imageURL, String url) {
        this.story = story;
        this.date = date;
        this.sortableDate = sortableDate;
        this.posterAccountType = posterAccountType;
        this.posterID = posterID;
        this.posterName = posterName;
        this.profilePicURL = profilePicURL;
        this.classReciepient = classReciepient;
        this.imageURL = imageURL;
        this.url = url;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public String getPosterAccountType() {
        return posterAccountType;
    }

    public void setPosterAccountType(String posterAccountType) {
        this.posterAccountType = posterAccountType;
    }

    public String getPosterID() {
        return posterID;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
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

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
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

    public List<String> getClassRecipients() {
        return classRecipients;
    }

    public void setClassRecipients(List<String> classRecipients) {
        this.classRecipients = classRecipients;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
