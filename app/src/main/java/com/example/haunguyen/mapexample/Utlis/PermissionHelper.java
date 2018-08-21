package com.example.haunguyen.mapexample.Utlis;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public final class PermissionHelper {

    private static PermissionHelper permissionHelper;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private Activity activity;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private ArrayList<String> requiredPermissions;
    private ArrayList<String> ungrantedPermissions = new ArrayList<String>();

    private PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static synchronized PermissionHelper getInstance(Activity activity) {
        if (permissionHelper == null) {
            permissionHelper = new PermissionHelper(activity);
        }
        return permissionHelper;
    }


    private void initPermissions() {
        requiredPermissions = new ArrayList<String>();
        requiredPermissions.add(PERMISSION_ACCESS_FINE_LOCATION);
        //Add all the required permission in the list
    }

    public void requestPermissionsIfDenied() {
        ungrantedPermissions = getUnGrantedPermissionsList();
        if (canShowPermissionRationaleDialog()) {
            askPermissions();
            return;
        }
        askPermissions();
    }

    public void requestPermissionsIfDenied(final String permission) {
        if (canShowPermissionRationaleDialog(permission)) {
            askPermissions();
            return;
        }
        askPermission(permission);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean canShowPermissionRationaleDialog() {
        boolean shouldShowRationale = false;
        for (String permission : ungrantedPermissions) {
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            if (shouldShow) {
                shouldShowRationale = true;
            }
        }
        return shouldShowRationale;
    }

    public boolean canShowPermissionRationaleDialog(String permission) {
        boolean shouldShowRationale = false;
        boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        if (shouldShow) {
            shouldShowRationale = true;
        }
        return shouldShowRationale;
    }

    private void askPermissions() {
        if (ungrantedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity, ungrantedPermissions.toArray(new String[ungrantedPermissions.size()]), PERMISSION_REQUEST_CODE);
        }
    }

    private void askPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    public boolean isAllPermissionAvailable() {
        boolean isAllPermissionAvailable = true;
        initPermissions();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                isAllPermissionAvailable = false;
                break;
            }
        }
        return isAllPermissionAvailable;
    }

    public ArrayList<String> getUnGrantedPermissionsList() {
        ArrayList<String> list = new ArrayList<String>();
        for (String permission : requiredPermissions) {
            int result = ActivityCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }
        return list;
    }

    public boolean isPermissionAvailable(String permission) {
        boolean isPermissionAvailable = true;
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            isPermissionAvailable = false;
        }
        return isPermissionAvailable;
    }
}
