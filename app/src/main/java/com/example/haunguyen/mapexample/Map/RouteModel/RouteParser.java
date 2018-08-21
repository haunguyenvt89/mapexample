package com.example.haunguyen.mapexample.Map.RouteModel;

import com.example.haunguyen.mapexample.Map.RouteModel.DirectionsResults;
import com.example.haunguyen.mapexample.Map.RouteModel.Location;
import com.example.haunguyen.mapexample.Map.RouteModel.Route;
import com.example.haunguyen.mapexample.Map.RouteModel.Steps;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RouteParser {

    public static List<LatLng> parse(DirectionsResults directionsResults) {

        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
        if(directionsResults.getRoutes().size()>0){
            List<LatLng> decodelist;
            Route routeA = directionsResults.getRoutes().get(0);
            if(routeA.getLegs().size()>0){
                List<Steps> steps= routeA.getLegs().get(0).getSteps();
                Steps step;
                Location location;
                String polyline;
                for(int i=0 ; i<steps.size();i++){
                    step = steps.get(i);
                    location =step.getStart_location();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location =step.getEnd_location();
                    routelist.add(new LatLng(location.getLat() ,location.getLng()));
                }
            }
        }

        return routelist;
    }


    private static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}