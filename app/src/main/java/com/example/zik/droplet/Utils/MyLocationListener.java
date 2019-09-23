package com.example.zik.droplet.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class MyLocationListener implements LocationListener {

    public static double latitude;
    private Context context;
    private Location location;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public static double longitude;


    public MyLocationListener(Context context) {
        this.context = context;
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission
                    ( context, android.Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {  }
            if (isGPSEnabled == true) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,0,0,this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (isNetworkEnabled==true) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,0,0,this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        catch(Exception ex) { Toast.makeText(context,"Exception "+ex, Toast.LENGTH_LONG).show(); }
    }

    @Nullable
    @Override
    public void onLocationChanged(Location loc){
        loc.getLatitude();
        loc.getLongitude();
        latitude=loc.getLatitude();
        longitude=loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {    }
    @Override
    public void onProviderEnabled(String provider) {    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {   }

}