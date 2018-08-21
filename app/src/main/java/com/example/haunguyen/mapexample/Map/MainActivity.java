package com.example.haunguyen.mapexample.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.haunguyen.mapexample.Map.Network.Markers;
import com.example.haunguyen.mapexample.R;
import com.example.haunguyen.mapexample.Utlis.PermissionHelper;
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

import fr.quentinklein.slt.LocationTracker;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, IMapView,
        View.OnClickListener{


    private static final int PLAY_SERVICE_ERROR = 10000;
    private GoogleMap mMaps;
    private PermissionHelper runtimePermissionHelper;
    private LocationTracker locationTracker;
    private Location mLastLocation;
    private Polyline polyLine;
    private Boolean isPermissionGranted;
    private LatLng destination;
    private MapPresenter mapPresenter;
    private Button btnSetting;
    private LocationTracker tracker;


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
                // All permissions available. Go with the flow
                //TODO
                if (checkPlayServices()) {
//                    buildGoogleApiClient();
//                    requestLocationService();
                    startLocationTracking();
                }

            } else {
                // Few permissions not granted. Ask for ungranted permissions
                runtimePermissionHelper.setActivity(this);
                runtimePermissionHelper.requestPermissionsIfDenied();
            }
        } else {
            // SDK below API 23. Do nothing just go with the flow.
            if (checkPlayServices()) {
                startLocationTracking();
            }
        }


        mapPresenter = new MapPresenter(this);


    }

    private void startLocationTracking(){
        tracker = new LocationTracker(getApplicationContext()) {
            @Override
            public void onLocationFound(Location location) {
                // Do some stuff
                mLastLocation = location;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15f);
                mMaps.animateCamera(cameraUpdate);
                if(destination != null){
                    mapPresenter.getRoute(new LatLng(location.getLatitude(), location.getLongitude()), destination);
                }

            }

            @Override
            public void onTimeout() {

            }
        };
        tracker.startListening();
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
        }
        mMaps.setMyLocationEnabled(true);
        mapPresenter.getMarkers();

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
        if(polyLine!=null){
            polyLine.remove();
        }
        if (routes != null && routes.size() > 0) {
            PolylineOptions polylineOptions = new PolylineOptions().width(10).color(
                    Color.BLUE);

            for (int i = 0; i < routes.size(); i++) {
                polylineOptions.add(routes.get(i));
            }
            // Adding route on the map
            polyLine =  mMaps.addPolyline(polylineOptions);
        }else {
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_GRANTED) {
                //TODO
                isPermissionGranted = true;
                mMaps.isMyLocationEnabled();
            } else {
                isPermissionGranted = false;
                runtimePermissionHelper.requestPermissionsIfDenied(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }


}
