package com.celerii.celerii.models;

import com.celerii.celerii.helperClasses.Date;

/**
 * Created by user on 7/4/2017.
 */

public class Comment {
    String posterPic, posterName, posterID, time, sortableDate, comment, commentID, accountType;

    public Comment() {
        this.posterPic = "";
        this.posterName = "";
        this.posterID = "";
        this.time = "";
        this.sortableDate = "";
        this.comment = "";
        this.commentID = "";
        this.accountType = "";
    }

    public Comment(String posterPic, String posterName, String posterID, String time, String comment, String commentID, String accountType) {
        this.posterPic = posterPic;
        this.posterName = posterName;
        this.posterID = posterID;
        this.time = time;
        this.sortableDate = Date.convertToSortableDate(time);
        this.comment = comment;
        this.commentID = commentID;
        this.accountType = accountType;
    }

    public String getPosterPic() {
        return posterPic;
    }

    public void setPosterPic(String posterPic) {
        this.posterPic = posterPic;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterID() {
        return posterID;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
