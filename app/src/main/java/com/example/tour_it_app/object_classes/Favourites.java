package com.example.tour_it_app.object_classes;

public class Favourites {

    private String title;
    private double latitude;
    private double longitude;
    private String locationID;

    //Default constructor
    public Favourites(){}

    //constructor with full parameters
    public Favourites(String title, double latitude, double longitude, String id) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }



}
