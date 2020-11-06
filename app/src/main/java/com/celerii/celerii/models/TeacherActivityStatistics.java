package com.celerii.celerii.models;

/**
 * Created by DELL on 10/15/2017.
 */

public class TeacherActivityStatistics {
    int totalPointsAwarded, totalPointsFined, totalClassPosts, totalPostLikes;

    public TeacherActivityStatistics() {
        this.totalPointsAwarded = 0;
        this.totalPointsFined = 0;
        this.totalClassPosts = 0;
        this.totalPostLikes = 0;
    }

    public TeacherActivityStatistics(int totalPointsAwarded, int totalPointsFined, int totalClassPosts, int totalPostLikes) {
        this.totalPointsAwarded = totalPointsAwarded;
        this.totalPointsFined = totalPointsFined;
        this.totalClassPosts = totalClassPosts;
        this.totalPostLikes = totalPostLikes;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    public int getTotalPointsFined() {
        return totalPointsFined;
    }

    public void setTotalPointsFined(int totalPointsFined) {
        this.totalPointsFined = totalPointsFined;
    }

    public int getTotalClassPosts() {
        return totalClassPosts;
    }

    public void setTotalClassPosts(int totalClassPosts) {
        this.totalClassPosts = totalClassPosts;
    }

    public int getTotalPostLikes() {
        return totalPostLikes;
    }

    public void setTotalPostLikes(int totalPostLikes) {
        this.totalPostLikes = totalPostLikes;
    }
}
