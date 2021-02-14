package com.celerii.celerii.models;

/**
 * Created by user on 7/5/2017.
 */
public class MessageList {
    String name, message, senderID, receiverID, time, sortableTime, profilepicUrl, otherParty;
    int noOfMessages;
    boolean seen, received;

    public MessageList() {
        this.name = "";
        this.message = "";
        this.senderID = "";
        this.receiverID = "";
        this.time = "0000/00/00 00:00:00:000";
        this.sortableTime = "00000000000000000";
        this.profilepicUrl = "";
        this.otherParty = "";
        this.noOfMessages = 0;
        this.seen = false;
        this.received = false;
    }

    public MessageList(String name, String message, String time, String sortableTime, String profilepicUrl, int noOfMessages) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.sortableTime = sortableTime;
        this.profilepicUrl = profilepicUrl;
        this.noOfMessages = noOfMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSortableTime() {
        return sortableTime;
    }

    public void setSortableTime(String sortableTime) {
        this.sortableTime = sortableTime;
    }

    public String getProfilepicUrl() {
        return profilepicUrl;
    }

    public void setProfilepicUrl(String profilepicUrl) {
        this.profilepicUrl = profilepicUrl;
    }

    public String getOtherParty() {
        return otherParty;
    }

    public void setOtherParty(String otherParty) {
        this.otherParty = otherParty;
    }

    public int getNoOfMessages() {
        return noOfMessages;
    }

    public void setNoOfMessages(int noOfMessages) {
        this.noOfMessages = noOfMessages;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
