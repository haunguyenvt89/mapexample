package com.example.haunguyen.mapexample.Map.RouteModel;

import com.google.gson.annotations.SerializedName;

public class Polyline {
    @SerializedName("points")
    public String points;

    public String getPoints() {
        return points;
    }
}
