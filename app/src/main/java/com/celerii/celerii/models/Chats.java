package com.celerii.celerii.models;

/**
 * Created by user on 7/8/2017.
 */

public class Chats {
    String message, messageID, senderID, recieverID, datestamp, sortableDate, fileURL, otherProfilePicURL;
    boolean received, mine, isRow, seen;

    public Chats() {
        this.message = "";
        this.messageID = "";
        this.senderID = "";
        this.recieverID = "";
        this.datestamp = "";
        this.sortableDate = "";
        this.fileURL = "";
        this.otherProfilePicURL = "";
        this.received = false;
        this.mine = false;
        this.seen = false;
    }

    public Chats(String message, String senderID, String recieverID, String datestamp, String sortableDate, boolean seen, boolean mine, String fileURL, String otherProfilePicURL, boolean isRow) {
        this.message = message;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.datestamp = datestamp;
        this.sortableDate = sortableDate;
        this.fileURL = fileURL;
        this.otherProfilePicURL = otherProfilePicURL;
        this.seen = seen;
        this.mine = mine;
        this.isRow = isRow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getOtherProfilePicURL() {
        return otherProfilePicURL;
    }

    public void setOtherProfilePicURL(String otherProfilePicURL) {
        this.otherProfilePicURL = otherProfilePicURL;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isRow() {
        return isRow;
    }

    public void setRow(boolean row) {
        isRow = row;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getSortableDate() {
        return sortableDate;
    }

    public void setSortableDate(String sortableDate) {
        this.sortableDate = sortableDate;
    }
}
