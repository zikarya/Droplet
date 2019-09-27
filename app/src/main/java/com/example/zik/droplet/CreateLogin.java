package com.example.zik.droplet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.FirebaseTasks;
import com.example.zik.droplet.Utils.MyLocationListener;
import com.example.zik.droplet.Utils.Person;

import java.io.IOException;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.TooltipCompat;
import static com.example.zik.droplet.Utils.Constants.PICK_IMG_REQUEST;

public class CreateLogin extends Activity {
    ///////////////////////////////////////////////////////////////////////////
    // SETS UP LOGIN CREATION FORM, VALIDATES FORM AND USES FIREBASETASKS TO
    // CREATE THE ACCOUNT IN THREE STEPS:
    // 1) UPLOAD THE PROFILE PICTURE
    // 2) CREATE A FIREBASE DATABASE ENTRY
    // 3) CREATE A FIREBASE LOGIN USING THE EMAIL AND PASSWORD
    ////////////////////////////////////////////////////////////////////////////

    private EditText pName, pEmail, pPassword;
    private TextView pLocation;
    private EditText pBio;
    private ImageButton pImage;
    private Person newProfile;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
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
        //USES MYLOCATIONLISTENER TO GET THE CURRENT LOCATION
                pLocation.setText(MyLocationListener.getMyLocation(CreateLogin.this, activity));
            }
        });
        //SETS THE EDIT BUTTON INVISIBLE ON THE CREATE ACCOUNT FORM (REQUIRED FOR PROFILE UPDATE)
        findViewById(R.id.edit_button).setVisibility(View.INVISIBLE);
        // ONCLICKLISTENER SET TO BUTTON SELECTION WILL TRIGGER THE ACCOUNT TO BE CREATED
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                newProfile.setName(pName.getText().toString());
                newProfile.setLocation(pLocation.getText().toString());
                newProfile.setBio(pBio.getText().toString());
                newProfile.setEmail(pEmail.getText().toString());
                newProfile.setPassword(pPassword.getText().toString());
                if (validateForm()) {
                    FirebaseTasks firebaseTasks = new FirebaseTasks();
                    newProfile.setimageurl(firebaseTasks.uploadImageToStorage(Uri.parse(newProfile.getimageurl()), CreateLogin.this).toString());
                    firebaseTasks.uploadProfileToDB(newProfile, CreateLogin.this);
                    data.putExtra(Constants.NEW_PROFILE_REQUEST, newProfile);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
        });
        findViewById(R.id.cancel_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMG_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null ) {
            //GETS RESULT OF IMAGE SELECTOR AND PLACES ON THE DISPLAY
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

    private boolean validateForm() {
        boolean valid = true;
        // ENSURE ALL FIELDS ARE POPULATED BEFORE ATTEMPTING TO CREATE ACCOUNT
        String email = pName.getText().toString();
        if (TextUtils.isEmpty(email)) {
            pName.setError("Required.");valid = false;
        } else {pName.setError(null);}

        String password = pPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pPassword.setError("Required.");valid = false;
        } else {pPassword.setError(null);}

        String location = pLocation.getText().toString();
        if (TextUtils.isEmpty(location)) {
            pLocation.setError("Required.");valid = false;
        } else {pLocation.setError(null);}

        String bio = pBio.getText().toString();
        if (TextUtils.isEmpty(bio)) {
            pBio.setError("Required.");valid = false;
        } else {pBio.setError(null);}

        return valid;
    }
}
