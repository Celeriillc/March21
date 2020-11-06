package com.celerii.celerii.models;

/**
 * Created by user on 6/24/2017.
 */

public class User {
    String email;
    String role;
    String mode;

    public User(){
        this.email = "";
        this.role = "";
        this.mode = "";
    }

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public User(String email, String role, String mode) {
        this.email = email;
        this.role = role;
        this.mode = mode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
