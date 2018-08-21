package com.example.haunguyen.mapexample.Map.Network;

import java.util.ArrayList;
import java.util.List;

public class FakeLocation {

    IGetLocation getLocation;

    public FakeLocation(IGetLocation getLocation) {
        this.getLocation = getLocation;
    }

    public void getLocation(){
        List<Markers> markers = new ArrayList<>();
        Markers markers1 = new Markers(10.8329383,106.6714691, "Country House");
        Markers markers2 = new Markers(10.8360522,106.6693769, "Dien luc go vap");
        Markers markers3 = new Markers(10.8364131,106.671233, "Country House");
        Markers markers4 = new Markers(10.8372258,106.6709728, "Vuvuzela Go vap");
        Markers markers5 = new Markers(10.8375689,106.6707811, "KichiKichi");
        Markers markers6 = new Markers(10.832994,106.6720869, "Du mien coffe");
        markers.add(markers1);
        markers.add(markers2);
        markers.add(markers3);
        markers.add(markers4);
        markers.add(markers5);
        markers.add(markers6);
        getLocation.getLocationSucces(markers);
    }
}
