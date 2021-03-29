package com.celerii.celerii.models;

import java.util.ArrayList;

public class SearchExistingIncomingAndOutgoingConnections {
    java.util.ArrayList<String> existingConnections, pendingIncomingRequests, pendingOutgoingRequests;

    public SearchExistingIncomingAndOutgoingConnections() {
        this.existingConnections = new ArrayList<>();
        this.pendingIncomingRequests = new ArrayList<>();
        this.pendingOutgoingRequests = new ArrayList<>();
    }

    public SearchExistingIncomingAndOutgoingConnections(ArrayList<String> existingConnections, ArrayList<String> pendingIncomingRequests, ArrayList<String> pendingOutgoingRequests) {
        this.existingConnections = existingConnections;
        this.pendingIncomingRequests = pendingIncomingRequests;
        this.pendingOutgoingRequests = pendingOutgoingRequests;
    }

    public ArrayList<String> getExistingConnections() {
        return existingConnections;
    }

    public void setExistingConnections(ArrayList<String> existingConnections) {
        this.existingConnections = existingConnections;
    }

    public ArrayList<String> getPendingIncomingRequests() {
        return pendingIncomingRequests;
    }

    public void setPendingIncomingRequests(ArrayList<String> pendingIncomingRequests) {
        this.pendingIncomingRequests = pendingIncomingRequests;
    }

    public ArrayList<String> getPendingOutgoingRequests() {
        return pendingOutgoingRequests;
    }

    public void setPendingOutgoingRequests(ArrayList<String> pendingOutgoingRequests) {
        this.pendingOutgoingRequests = pendingOutgoingRequests;
    }
}
