package com.example.zik.droplet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.MyLocationListener;
import com.example.zik.droplet.Utils.Person;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.zik.droplet.Utils.Constants.PICK_IMG_REQUEST;

public class AddNewProfile extends Activity {

    private EditText pName, pEmail, pPassword;
    private TextView pLocation;
    private EditText pBio;
    private ImageButton pImage;
    private Person newProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        pImage = findViewById(R.id.profile_image);
        pName = findViewById(R.id.profile_name);
        pEmail = findViewById(R.id.profile_email);
        pPassword = findViewById(R.id.profile_password);
        pLocation = findViewById(R.id.profile_location_text);
        pBio = findViewById(R.id.profile_bio);
        newProfile = new Person();
        TooltipCompat.setTooltipText(pImage, "Add Pic");
        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQUEST);
            }
        });
        findViewById(R.id.locate_me).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                getLocation();
                pLocation.setText(newProfile.getLocation());
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                newProfile.setName(pName.getText().toString());
                newProfile.setLocation(pLocation.getText().toString());
                newProfile.setBio(pBio.getText().toString());
                newProfile.setEmail(pEmail.getText().toString());
                newProfile.setPassword(pPassword.getText().toString());
                FirebaseTasks firebaseTasks = new FirebaseTasks();
                newProfile.setimageurl(firebaseTasks.uploadImageToStorage(Uri.parse(newProfile.getimageurl()), AddNewProfile.this).toString());
                firebaseTasks.uploadProfileToDB(AddNewProfile.this, newProfile);
                data.putExtra(Constants.NEW_PROFILE_REQUEST, newProfile);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        findViewById(R.id.cancel_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        Double latitude = 0.0, longitude;
        LocationManager mlocManager;
        LocationListener mlocListener;
        String location = null;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            if (mlocManager.isProviderEnabled(GPS_PROVIDER)) {
                latitude = MyLocationListener.latitude;
                longitude = MyLocationListener.longitude;
                location = getAddressFromLongLat(latitude,longitude);
                if (latitude == 0.0) {
                    Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "GPS is currently off...", Toast.LENGTH_LONG).show();
            }
            newProfile.setLocation(location);
        }
    private String getAddressFromLongLat(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                /*  TODO: CAN DISPLAY OPTIONS TO SELECT HOUSE NUMBER/ROAD USING  -->     result.append(address.getAddressLine(0));  */
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return result.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            newProfile.setimageurl((data.getData()).toString());
            try {
                Uri filepath = Uri.parse(newProfile.getimageurl());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                newProfile.setProfilePic(bitmap);
                pImage.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

 //TODO: CREATE OnSavedInstanceState
}
