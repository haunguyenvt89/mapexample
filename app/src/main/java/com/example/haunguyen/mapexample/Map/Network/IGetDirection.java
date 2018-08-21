package com.example.haunguyen.mapexample.Map.Network;

import com.example.haunguyen.mapexample.Map.RouteModel.DirectionsResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGetDirection {
    @GET("/maps/api/directions/json")
    Call <DirectionsResults> getRoute(@Query("origin") String origin, @Query("destination") String destination);
}
