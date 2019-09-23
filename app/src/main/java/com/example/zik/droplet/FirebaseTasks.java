package com.example.zik.droplet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.UUID;
import androidx.annotation.NonNull;

public class FirebaseTasks {
    public static void createAccount(final Person profile, FirebaseAuth firebaseAuth, final Context context){
        //TODO: VALIDATE CREATE LOGIN FORM
        Log.d("createAccount", profile.getName());
        firebaseAuth.createUserWithEmailAndPassword(profile.getEmail(),profile.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(context,"Oops, account not created", Toast.LENGTH_LONG);

                        }else{
                            Toast.makeText(context,"Welcome, account create.  Please log in.", Toast.LENGTH_LONG);
                        }
                    }
                });
    }

    public StorageReference uploadImageToStorage(Uri filePath, Context context) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            try {
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                try{progressDialog.dismiss();}catch (Exception e){}
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
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
        return ref;
    }

    public void uploadProfileToDB(final Context context, Person profile) {
        final ProgressDialog loadingBar = new ProgressDialog(context);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword("admin@admin.com","password");
        DatabaseReference profileDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(profile.getName());

        //CREATE HASHMAP TO UPLOAD TO FIREBASE
        HashMap userMap = new HashMap();
        userMap.put("name",profile.getName());
        userMap.put("email",profile.getEmail());
        userMap.put("password",profile.getPassword());
        userMap.put("imageurl",profile.getimageurl());
        userMap.put("location",profile.getLocation());
        userMap.put("bio",profile.getBio());
        profileDatabaseRef.updateChildren(userMap);

                /*.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(context ,"account Created", Toast.LENGTH_LONG);
                    try {
                        loadingBar.dismiss();
                    }catch (Exception e){}
                } else{
                    Toast.makeText(context, "account not Created", Toast.LENGTH_LONG);
                    try {
                        loadingBar.dismiss();
                    }catch(Exception e){}

                }
            }
        });*/
    }


}
