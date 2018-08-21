package com.example.haunguyen.mapexample.Map;

import android.location.Location;

public interface LocationHelperListener {
    void onLocationChange(Location location);
    void onProviderDisable();
    void onProviderEnable();

}
