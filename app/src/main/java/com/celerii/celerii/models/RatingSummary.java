package com.celerii.celerii.models;

/**
 * Created by user on 7/9/2017.
 */

public class RatingSummary {

    String urlPic;
    int numberOfVotes, noOfFive, noOfFour, noOfThree, noOfTwo, noOfOne;
    double rating;

    public RatingSummary() {
    }

    public RatingSummary(int numberOfVotes, double rating, int noOfFive, int noOfFour, int noOfThree, int noOfTwo, int noOfOne) {
        this.numberOfVotes = numberOfVotes;
        this.rating = rating;
        this.noOfFive = noOfFive;
        this.noOfFour = noOfFour;
        this.noOfThree = noOfThree;
        this.noOfTwo = noOfTwo;
        this.noOfOne = noOfOne;
    }

    public RatingSummary(int numberOfVotes, String urlPic, int noOfFive, int noOfFour, int noOfThree, int noOfTwo, int noOfOne, double rating) {
        this.numberOfVotes = numberOfVotes;
        this.urlPic = urlPic;
        this.noOfFive = noOfFive;
        this.noOfFour = noOfFour;
        this.noOfThree = noOfThree;
        this.noOfTwo = noOfTwo;
        this.noOfOne = noOfOne;
        this.rating = rating;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNoOfFive() {
        return noOfFive;
    }

    public void setNoOfFive(int noOfFive) {
        this.noOfFive = noOfFive;
    }

    public int getNoOfFour() {
        return noOfFour;
    }

    public void setNoOfFour(int noOfFour) {
        this.noOfFour = noOfFour;
    }

    public int getNoOfThree() {
        return noOfThree;
    }

    public void setNoOfThree(int noOfThree) {
        this.noOfThree = noOfThree;
    }

    public int getNoOfTwo() {
        return noOfTwo;
    }

    public void setNoOfTwo(int noOfTwo) {
        this.noOfTwo = noOfTwo;
    }

    public int getNoOfOne() {
        return noOfOne;
    }

    public void setNoOfOne(int noOfOne) {
        this.noOfOne = noOfOne;
    }

    public String getUrlPic() {
        return urlPic;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }
}
