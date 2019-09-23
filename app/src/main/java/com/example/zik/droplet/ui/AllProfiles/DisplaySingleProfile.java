package com.example.zik.droplet.ui.AllProfiles;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zik.droplet.FirebaseTasks;
import com.example.zik.droplet.R;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.zik.droplet.Utils.Constants.PICK_IMG_REQUEST;

//DISPLAY A SINGULAR PROFILES FULL DETAILS, OPTIONS TO EDIT AND REMOVE ON MENU

public class DisplaySingleProfile extends AppCompatActivity {
    public TextView nameView, locationView, bioView;
    public ImageButton profilePic, locateMeButton;
    public Button updateButton;
    Person profile;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        nameView = findViewById(R.id.profile_name);
        nameView.setFocusable(false);nameView.setClickable(false);
        locationView = findViewById(R.id.profile_location_text);
        locationView.setFocusable(false);locationView.setClickable(false);
        locateMeButton = findViewById(R.id.locate_me);
        locateMeButton.setVisibility(View.INVISIBLE);

        bioView = findViewById(R.id.profile_bio);
        bioView.setFocusable(false);bioView.setClickable(false);

        profilePic = findViewById(R.id.profile_image);
        updateButton = findViewById(R.id.button);
        updateButton.setText(R.string.update);
        updateButton.setVisibility(View.INVISIBLE);

        findViewById(R.id.cancel_textview).setVisibility(View.INVISIBLE);
        Bundle b = getIntent().getExtras();
        profile = b.getParcelable("profile");
        nameView.setText(profile.getName());
        locationView.setText(profile.getLocation());
        bioView.setText(profile.getBio());

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profile.getimageurl());
        final String[] downloadURL = new String[1];
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                downloadURL[0] = task.getResult().toString();
                Glide.with(DisplaySingleProfile.this.getApplicationContext())
                        .load(downloadURL[0])
                        .into(profilePic);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
 //               profile.setProfilePic(bitmap);
                profilePic.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
