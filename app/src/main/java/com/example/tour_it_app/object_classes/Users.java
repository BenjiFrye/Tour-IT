package com.example.tour_it_app.object_classes;

public class Users {

    private String firstName;
    private String lastName;
    private String email;
    private String userID;

    //default constructor
    public Users(){}

    //constructor with full parameters
    public Users (String firstName, String lastName, String email, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
