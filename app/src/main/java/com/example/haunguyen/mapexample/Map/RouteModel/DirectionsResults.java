package com.example.haunguyen.mapexample.Map.RouteModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResults {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }
}
