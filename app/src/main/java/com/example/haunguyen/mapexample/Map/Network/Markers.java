package com.example.haunguyen.mapexample.Map.Network;

public class Markers {
    private double latitude;
    private double longtitude;
    private String description;

    public Markers(double latitude, double longtitude, String description) {
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public String getDescription() {
        return description;
    }
}
