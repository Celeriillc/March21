package com.celerii.celerii.models;

/**
 * Created by DELL on 9/2/2017.
 */

public class SearchResultsRow {
    String entityId, entityName, entityAddress, entityAddressID, entityPic, entityType;

    public SearchResultsRow() {
        this.entityId = "";
        this.entityName = "";
        this.entityAddress = "";
        this.entityAddressID = "";
        this.entityPic = "";
        this.entityType = "";
    }

    public SearchResultsRow(String entityId, String entityName, String entityAddress, String entityPic, String entityType) {
        this.entityId = entityId;
        this.entityName = entityName;
        this.entityAddress = entityAddress;
        this.entityAddressID = "";
        this.entityPic = entityPic;
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityAddress() {
        return entityAddress;
    }

    public void setEntityAddress(String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public String getEntityAddressID() {
        return entityAddressID;
    }

    public void setEntityAddressID(String entityAddressID) {
        this.entityAddressID = entityAddressID;
    }

    public String getEntityPic() {
        return entityPic;
    }

    public void setEntityPic(String entityPic) {
        this.entityPic = entityPic;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
