package com.example.haunguyen.mapexample.Map;

import android.content.Context;

import com.example.haunguyen.mapexample.Map.RouteModel.DirectionsResults;
import com.example.haunguyen.mapexample.Map.Network.DirectionServices;
import com.example.haunguyen.mapexample.Map.Network.FakeLocation;
import com.example.haunguyen.mapexample.Map.Network.IGetLocation;
import com.example.haunguyen.mapexample.Map.Network.Markers;
import com.example.haunguyen.mapexample.Map.RouteModel.RouteParser;
import com.example.haunguyen.mapexample.Utlis.SaveData;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPresenter implements IGetLocation {
    private IMapView iMapView;

    private DirectionServices directionServices;
    private FakeLocation fakeLocation;

    public MapPresenter(IMapView mapView){
        iMapView = mapView;
    }

    public void getMarkers(){
        if (fakeLocation == null){
            fakeLocation = new FakeLocation(this);
        }
        fakeLocation.getLocation();
    }

    public void  getRoute(LatLng origin, LatLng destination){
        if (directionServices == null){
            directionServices = new DirectionServices();
        }
        String str_origin = origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = destination.latitude + "," + destination.longitude;
        directionServices.getDirection()
                .getRoute(str_origin, str_dest).enqueue(new Callback<DirectionsResults>() {
            @Override
            public void onResponse(Call<DirectionsResults> call, Response<DirectionsResults> response) {
                if (response != null){
                    List route = RouteParser.parse(response.body());
                    iMapView.showRoute(route);
                }else {
                    iMapView.showRoute(null);
                }
            }

            @Override
            public void onFailure(Call<DirectionsResults> call, Throwable t) {

            }
        });
    }

    public void changeSetting(){
        //openDialog
        iMapView.showSetting();
    }

    public void saveSetting(int value, Context c,  IMapIteractor iMapIteractor){
        SaveData.saveSetting(value, c);
        //fake save success
        iMapIteractor.onSaveSuccess();
    }

    @Override
    public void getLocationSucces(List<Markers> markers) {
        if (markers != null && markers.size() > 0){
            iMapView.showMarkers(markers);
        }
    }
}
