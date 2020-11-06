package com.celerii.celerii.models;

/**
 * Created by DELL on 5/2/2018.
 */

public class SubscriptionModel {
    String subscriptionTier, subscriptionDate, expiryDate, studentAccount, parentAccount, amount;
    String sortableSubscriptionDate;

    public SubscriptionModel() {
        this.subscriptionTier = "";
        this.subscriptionDate = "0000/00/00 00:00:00:000";
        this.expiryDate = "0000/00/00 00:00:00:000";
        this.studentAccount = "";
        this.parentAccount = "";
        this.amount = "";
    }

    public SubscriptionModel(String subscriptionTier, String subscriptionDate, String expiryDate, String studentAccount, String parentAccount, String amount) {
        this.subscriptionTier = subscriptionTier;
        this.subscriptionDate = subscriptionDate;
        this.expiryDate = expiryDate;
        this.studentAccount = studentAccount;
        this.parentAccount = parentAccount;
        this.amount = amount;
    }

    public String getSubscriptionTier() {
        return subscriptionTier;
    }

    public void setSubscriptionTier(String subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }

    public String getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(String subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(String studentAccount) {
        this.studentAccount = studentAccount;
    }

    public String getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(String parentAccount) {
        this.parentAccount = parentAccount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSortableSubscriptionDate() {
        return sortableSubscriptionDate;
    }

    public void setSortableSubscriptionDate(String sortableSubscriptionDate) {
        this.sortableSubscriptionDate = sortableSubscriptionDate;
    }
}
