package com.example.zik.droplet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth firebaseAuth;
    private EditText userEmail, userPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.create_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewProfile.class);
                startActivityForResult(intent, Constants.ADD_PROFILE);

            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailRequest = userEmail.getText().toString();
                String passwordRequest = userPassword.getText().toString();
                signIn(emailRequest, passwordRequest);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void signIn(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseTasks firebaseTasks = new FirebaseTasks();
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        return;
                    } else {
                        Toast.makeText(MainActivity.this, "failed to login", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = userEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Required.");
            valid = false;
        } else {
            userEmail.setError(null);
        }
        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Required.");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_PROFILE) {
            if (data != null) {
                Person signedIn = data.getParcelableExtra(Constants.NEW_PROFILE_REQUEST);
                FirebaseTasks.createAccount(signedIn, firebaseAuth, MainActivity.this);
            }
        }else if (requestCode == Constants.GET_PROFILE){
            if (data != null){
                Person signedIn = data.getParcelableExtra(Constants.LOGGED_IN_PROFILE_REQUEST);
                Intent intent = new Intent();
                intent.putExtra(Constants.LOGGED_IN_PROFILE_REQUEST, signedIn);
                startActivity(intent);
            }
        }
    }
}


