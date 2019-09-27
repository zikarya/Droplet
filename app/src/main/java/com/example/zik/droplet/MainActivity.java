package com.example.zik.droplet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.FirebaseTasks;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.zik.droplet.Utils.Constants.CREATE_LOGIN;

public class MainActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////
    //  SETS UP INITIAL VIEW, TWO OPTIONS
    //  1) LOGIN -- TRIGGERS FIREBASE AUTHENTICATION AND NEW INTENT
    //              TO LOAD THE PROFILE
    //  2) CREATE A NEW LOGIN  -- NEW INTENT TO SETUP NEW SCREEN TO
    //              ENTER PROFILE DETAILS
    ////////////////////////////////////////////////////////////////////


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
                Intent intent = new Intent(MainActivity.this, CreateLogin.class);
                startActivityForResult(intent, CREATE_LOGIN);

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
        if (requestCode == CREATE_LOGIN) {
            if (data != null) {
                Person signedIn = data.getParcelableExtra(Constants.NEW_PROFILE_REQUEST);
                FirebaseTasks.createAccount(signedIn, MainActivity.this);
                Toast.makeText(this,"Login created. Please sign in.", Toast.LENGTH_LONG).show();
            }
        }
    }
}