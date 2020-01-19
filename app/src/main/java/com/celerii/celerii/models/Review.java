package com.celerii.celerii.models;

/**
 * Created by user on 7/9/2017.
 */

public class Review {

    String review, reviewerPicURL, reviewer, date, sortableDate, reviewerID, recieverID;
    String rating;

    public Review() {
    }

    public Review(String review, String reviewerPicURL, String reviewer) {
        this.review = review;
        this.reviewerPicURL = reviewerPicURL;
        this.reviewer = reviewer;
    }

    public Review(String review, String reviewerID, String recieverID, String rating, String date, String sortableDate) {
        this.review = review;
        this.reviewerID = reviewerID;
        this.recieverID = recieverID;
        this.rating = rating;
        this.date = date;
        this.sortableDate = sortableDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewerPicURL() {
        return reviewerPicURL;
    }

    public void setReviewerPicURL(String reviewerPicURL) {
        this.reviewerPicURL = reviewerPicURL;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
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

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
