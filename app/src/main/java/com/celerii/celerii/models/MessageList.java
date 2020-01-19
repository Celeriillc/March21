package com.celerii.celerii.models;

/**
 * Created by user on 7/5/2017.
 */
public class MessageList {
    String name, message, senderID, recieverID, time, profilepicUrl, otherParty;
    int noOfMessages;
    boolean seen, received;

    public MessageList() {
        this.time = "";
        this.recieverID = "";
    }

    public MessageList(String name, String message, String time, String profilepicUrl, int noOfMessages) {
        this.name = name;
        this.message = message;
        this.time = time;
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

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
