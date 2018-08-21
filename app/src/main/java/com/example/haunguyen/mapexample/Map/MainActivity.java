package com.example.haunguyen.mapexample.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haunguyen.mapexample.Map.Network.Markers;
import com.example.haunguyen.mapexample.R;
import com.example.haunguyen.mapexample.Utlis.LocationHelper;
import com.example.haunguyen.mapexample.Utlis.PermissionHelper;
import com.example.haunguyen.mapexample.Utlis.SaveData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, IMapView,
        View.OnClickListener, LocationHelperListener {


    private static final int PLAY_SERVICE_ERROR = 10000;
    private GoogleMap mMaps;
    private PermissionHelper runtimePermissionHelper;
    private Location mLastLocation;
    private Polyline polyLine;
    private LatLng destination;
    private MapPresenter mapPresenter;
    private Button btnSetting;
    LocationHelper locationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSetting = findViewById(R.id.btnsetting);
        btnSetting.setBackgroundResource(R.drawable.bg_setting);
        btnSetting.setOnClickListener(this);

        //init map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Check permission
        if (Build.VERSION.SDK_INT >= 23) {
            runtimePermissionHelper = PermissionHelper.getInstance(this);
            if (runtimePermissionHelper.isAllPermissionAvailable()) {
                //TODO
                if (checkPlayServices()) {
                    locationHelper = new LocationHelper(getApplicationContext(), this);
                    mLastLocation = locationHelper.getLocation();
                }

            } else {
                runtimePermissionHelper.setActivity(this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
            // SDK below API 23. Do nothing just go with the flow.
            if (checkPlayServices()) {
                locationHelper = new LocationHelper(getApplicationContext(), this);
                mLastLocation = locationHelper.getLocation();
            }
        }
        mapPresenter = new MapPresenter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.stopUsingGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMaps = googleMap;
        if (mMaps != null) {
            mMaps.setOnMarkerClickListener(this);
            mMaps.setMyLocationEnabled(true);

        }
        if (mLastLocation != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15f);
            mMaps.animateCamera(cameraUpdate);
            mapPresenter.getMarkers();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //On draw direction from my loction to marker;
        if (mLastLocation != null) {
            destination = marker.getPosition();
            mapPresenter.getRoute(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), destination);
        }
        return false;
    }


    @Override
    public void showMarkers(List<Markers> markers) {
        if (mMaps != null) {
            for (Markers marker : markers) {
                mMaps.addMarker(new MarkerOptions().position(new LatLng(marker.getLatitude(), marker.getLongtitude()))
                        .title(marker.getDescription()));
            }
        }
    }

    @Override
    public void showRoute(List<LatLng> routes) {
        if (polyLine != null) {
            polyLine.remove();
        }
        if (routes != null && routes.size() > 0) {
            PolylineOptions polylineOptions = new PolylineOptions().width(10).color(
                    Color.BLUE);

            for (int i = 0; i < routes.size(); i++) {
                polylineOptions.add(routes.get(i));
            }
            // Adding route on the map
            polyLine = mMaps.addPolyline(polylineOptions);
        } else {
            Toast.makeText(getApplicationContext(), "No Route From your location to destination", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showSetting() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_setting);
        dialog.setTitle("Change Time Request Location");

        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.edTime);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);

        // if button is clicked, close the custom dialog
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf(text.getText().toString());
                mapPresenter.saveSetting(value, MainActivity.this, new IMapIteractor() {
                    @Override
                    public void onInputTimeError() {

                    }

                    @Override
                    public void onSaveSuccess() {
                        //call get location
                        if (locationHelper != null) {
                            locationHelper.stopUsingGPS();
                            locationHelper.setTimeRequest(SaveData.getInt(getApplicationContext()));
                            locationHelper.getLocation();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnsetting:
                mapPresenter.changeSetting();
                break;
        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICE_ERROR).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting DialogHelp Title
        alertDialog.setTitle("GPS is settings");

        // Setting DialogHelp Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_GRANTED) {
                //TODO
                mMaps.isMyLocationEnabled();
                locationHelper = new LocationHelper(getApplicationContext(), this);
            } else {
                runtimePermissionHelper.requestPermissionsIfDenied(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }


    @Override
    public void onLocationChange(Location location) {
        Log.d("locationMainActivity", System.currentTimeMillis() + "");
        mLastLocation = location;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15f);
        mMaps.animateCamera(cameraUpdate);
        if (destination != null) {
            mapPresenter.getRoute(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), destination);
        }

    }

    @Override
    public void onProviderDisable() {
        showSettingsAlert();
    }

    @Override
    public void onProviderEnable() {

    }

}
