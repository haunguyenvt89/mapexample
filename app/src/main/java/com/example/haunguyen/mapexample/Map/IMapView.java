package com.example.haunguyen.mapexample.Map;

import com.example.haunguyen.mapexample.Map.Network.Markers;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface IMapView {
    void showMarkers(List<Markers> markers);
    void showRoute(List<LatLng> routes);
    void showSetting();
}
