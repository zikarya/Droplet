package com.example.zik.droplet.ui.NavMenuViewProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.zik.droplet.Utils.FirebaseTasks;
import com.example.zik.droplet.R;
import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.MyLocationListener;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import static android.app.Activity.RESULT_OK;
import static com.example.zik.droplet.Utils.Constants.PICK_IMG_REQUEST;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private View root;
    private static Person loggedIn;
    private ImageButton pImage;
    private Uri filepath;
    private TextView pName, pEmail, pBio, pLocation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loggedIn = getArguments().getParcelable(Constants.PROFILE_KEY);
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.profile, container, false);
        pName = root.findViewById(R.id.profile_name);
        pEmail = root.findViewById(R.id.profile_email);
        pImage = root.findViewById(R.id.profile_image);
        pBio = root.findViewById(R.id.profile_bio);
        pLocation = root.findViewById(R.id.profile_location_text);
        viewProfile();
        return root;
    }

    public void viewProfile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(loggedIn.getimageurl());
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        root.findViewById(R.id.button).setVisibility(View.INVISIBLE);
                        root.findViewById(R.id.cancel_textview).setVisibility(View.INVISIBLE);
                        root.findViewById(R.id.locate_me).setVisibility(View.INVISIBLE);
                        final FloatingActionButton editButton = root.findViewById(R.id.edit_button);
                        editButton.show();
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editButton.hide();
                                editProfile();
                            }
                        });
                        pName.setEnabled(false);
                        root.findViewById(R.id.profile_password).setVisibility(View.INVISIBLE);
                        pEmail.setEnabled(false);
                        pImage.setEnabled(false);
                        pBio.setEnabled(false);
                        String downloadurl = task.getResult().toString();
                        pName.setText(loggedIn.getName());
                        pEmail.setText(loggedIn.getEmail());
                        pLocation.setText(loggedIn.getLocation());
                        pBio.setText(loggedIn.getBio());
                        Glide.with(root)
                                .load(downloadurl)
                                .into(pImage);
                    return;
                    }
            });
    }

    public void editProfile(){

        TextView cancelTextview = root.findViewById(R.id.cancel_textview);
        cancelTextview.setVisibility(View.VISIBLE);
        cancelTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfile();
            }
        });

        pName.setEnabled(true); pBio.setEnabled(true);
        ImageButton locateMe = root.findViewById(R.id.locate_me);
        locateMe.setVisibility(View.VISIBLE);
        locateMe.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                pLocation.setText(MyLocationListener.getMyLocation(getContext(), getActivity()));
            }
        });

        pImage.setEnabled(true);
        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG_REQUEST);
            }
        });
        Button update = root.findViewById(R.id.button);
        update.setText("Update");
        update.setVisibility(View.VISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggedIn.setName(pName.getText().toString());
                loggedIn.setLocation(pLocation.getText().toString());
                loggedIn.setBio(pBio.getText().toString());
                if (validateForm()) {
                    FirebaseTasks firebaseTasks = new FirebaseTasks();
                    firebaseTasks.updateProfileToDB(loggedIn);
                    viewProfile();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = pName.getText().toString();
        if (TextUtils.isEmpty(email)) {
            pName.setError("Required.");valid = false;
        } else {pName.setError(null);}

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            loggedIn.setimageurl(data.getData().toString());
            try {
                filepath = Uri.parse(loggedIn.getimageurl());
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                loggedIn.setProfilePic(bitmap);
                pImage.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
