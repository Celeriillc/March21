package com.celerii.celerii.models;

/**
 * Created by DELL on 10/15/2017.
 */

public class TeacherActivityStatistics {
    int totalPointsAwarded, totalClassPosts, totalPostLikes, totalAssignmentPosts, totalAssignmentViews;

    public TeacherActivityStatistics() {
    }

    public TeacherActivityStatistics(int totalPointsAwarded, int totalClassPosts, int totalPostLikes, int totalAssignmentPosts, int totalAssignmentViews) {
        this.totalPointsAwarded = totalPointsAwarded;
        this.totalClassPosts = totalClassPosts;
        this.totalPostLikes = totalPostLikes;
        this.totalAssignmentPosts = totalAssignmentPosts;
        this.totalAssignmentViews = totalAssignmentViews;
    }

    public int getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(int totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
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

    public int getTotalAssignmentPosts() {
        return totalAssignmentPosts;
    }

    public void setTotalAssignmentPosts(int totalAssignmentPosts) {
        this.totalAssignmentPosts = totalAssignmentPosts;
    }

    public int getTotalAssignmentViews() {
        return totalAssignmentViews;
    }

    public void setTotalAssignmentViews(int totalAssignmentViews) {
        this.totalAssignmentViews = totalAssignmentViews;
    }
}
