package com.example.zik.droplet.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;

public class MyLocationListener implements LocationListener {

    public static double latitude;
    private Context context;
    private Location location;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public static double longitude;


    public MyLocationListener(Context context) {
        // SETS UP LOCATION MANAGER WITH PERMISSIONS TO ALLOW USER TO GET CURRENT UPDATE

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getMyLocation(Context context, Activity activity) {
        // STATIC METHOD CALLED WHEN USER ATTEMPTS TO GET CURRENT LOCATION
        // LOCATIONMANAGER WILL RETURN THE LONGITUDE AND LATITUDE
        // GEOCODER WILL RETRIEVE THE LONGITUDE AND LATITUDE RETURNED BY LOCATION MANAGER

        Double latitude = 0.0, longitude;
        LocationManager mlocManager;
        LocationListener mlocListener;
        String location = null;
        mlocManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constants.MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        if (mlocManager.isProviderEnabled(GPS_PROVIDER)) {
            latitude = MyLocationListener.latitude;
            longitude = MyLocationListener.longitude;
            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    /*  TODO: CAN DISPLAY OPTIONS TO SELECT HOUSE NUMBER/ROAD USING  -->     result.append(address.getAddressLine(0));  */
                    result.append(address.getLocality()).append("\n");
                    result.append(address.getCountryName());
                }
            } catch (IOException e) {
            }
            location = result.toString();
        }
        return location;
    }

}