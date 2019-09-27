package com.example.zik.droplet.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.UUID;
import androidx.annotation.NonNull;

import static com.example.zik.droplet.Utils.Constants.ADMIN_LOGIN_EMAIL;
import static com.example.zik.droplet.Utils.Constants.ADMIN_LOGIN_PASSWORD;
import static com.example.zik.droplet.Utils.Constants.CHILD_NAME;
import static com.example.zik.droplet.Utils.Constants.PROFILE_BIO;
import static com.example.zik.droplet.Utils.Constants.PROFILE_EMAIL;
import static com.example.zik.droplet.Utils.Constants.PROFILE_IMAGEURL;
import static com.example.zik.droplet.Utils.Constants.PROFILE_LOCATION;
import static com.example.zik.droplet.Utils.Constants.PROFILE_NAME;
import static com.example.zik.droplet.Utils.Constants.PROFILE_PASSWORD;

public class FirebaseTasks {

    public static void createAccount(final Person profile, final Context context){
        ////////////////////////////////////////////////////////////////////////////
        //    CREATES ACCOUNT FOR USER BY DOING THREE THINGS:
        //    1) CREATES "SignInWithEmailAndPassword" ON FIREBASE
        //    2) UPLOADS THE IMAGE ADDED TO FIREBASE STORAGE
        //    3) CREATES AND ENTRY ON THE FIREBASE DATABASE WITH ALL THE PROFILE
        //       DETAILS PROVIDED ON THE ON CREATE FORM
        //    ALSO CONTAINS ALTERNATIVE TASKS SUCH AS UPDATING THE PROFILE DETAILS
        /////////////////////////////////////////////////////////////////////////////
        final ProgressDialog progressDialog = new ProgressDialog(context);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(ADMIN_LOGIN_EMAIL, ADMIN_LOGIN_PASSWORD);
        firebaseAuth
            .createUserWithEmailAndPassword(profile.getEmail(),profile.getPassword())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        try{progressDialog.dismiss();}catch (Exception e){}
                        Toast.makeText(context,"Oops, account not created", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context,"Welcome, account created.  Please log in.", Toast.LENGTH_LONG).show();
                        try{progressDialog.dismiss();}catch (Exception e){}
                    } }
            });
    }

    public StorageReference uploadImageToStorage(Uri filePath, final Context context) {
        // UPLOADS THE PROFILE PICTURE PROVIDED AS "filepath" AND RETURNS THE FIREBASE
        // STORAGE URL FOR REFERENCE (FOR LOADING PROFILE ETC)
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.show();
            try {
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            } catch (Exception e) {
                return null;
            }
        }
        return ref;    }

    public void uploadProfileToDB(Person profile, final Context context) {
        // LOGS IN AS ADMINISTRATOR IN ORDER TO CREATE THE NEW USER
        // CREATES A HASHMAP WITH THE "profile" PROVIDED AND STORES TO FIREBASE DATABASE
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(ADMIN_LOGIN_EMAIL,ADMIN_LOGIN_PASSWORD);
        DatabaseReference profileDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(profile.getName());;
        //CREATE HASHMAP TO UPLOAD TO FIREBASE
        final HashMap userMap = new HashMap();
        userMap.put(PROFILE_NAME,profile.getName());
        userMap.put(PROFILE_EMAIL,profile.getEmail());
        userMap.put(PROFILE_PASSWORD,profile.getPassword());
        userMap.put(PROFILE_IMAGEURL,profile.getimageurl());
        userMap.put(PROFILE_LOCATION,profile.getLocation());
        userMap.put(PROFILE_BIO,profile.getBio());
        profileDatabaseRef.updateChildren(userMap);
    }

    public void updateProfileToDB(Person profile) {
        DatabaseReference profileDatabaseRef = FirebaseDatabase.getInstance().getReference();
        final Query query = profileDatabaseRef.child(CHILD_NAME).orderByChild(PROFILE_EMAIL).equalTo(profile.getEmail());
        //CREATE HASHMAP TO UPDATE FIREBASE
        final HashMap userMap = new HashMap();
        userMap.put(PROFILE_NAME,profile.getName());
        userMap.put(PROFILE_EMAIL,profile.getEmail());
        userMap.put(PROFILE_PASSWORD,profile.getPassword());
        userMap.put(PROFILE_IMAGEURL,profile.getimageurl());
        userMap.put(PROFILE_LOCATION,profile.getLocation());
        userMap.put(PROFILE_BIO,profile.getBio());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().setValue(userMap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}