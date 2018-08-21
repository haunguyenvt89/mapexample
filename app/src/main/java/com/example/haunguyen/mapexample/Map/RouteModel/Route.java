package com.example.haunguyen.mapexample.Map.RouteModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Route {
    @SerializedName("overview_polyline")
    private Polyline overviewPolyLine;

    private List<Legs> legs;

    public Polyline getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public List<Legs> getLegs() {
        return legs;
    }
}