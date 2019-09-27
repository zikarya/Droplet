package com.example.zik.droplet;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ///////////////////////////////////////////////////////////////////////////////////
    //  CREATES THE VIEW FOR ONCE USER HAS BEEN AUTHENTICATED, GETS THE PROFILE DETAILS
    //  STORED FROM ON FIREBASE DATABASE, LOADS THE PROFILE PICTURE BY GETTING FROM
    //  FIREBASE STORAGE USING "profile.getimageurl()".
    //  IMPLEMENTS NAVIGATION VIEW FOR THE SIDE MENU
    ///////////////////////////////////////////////////////////////////////////////////

    private AppBarConfiguration mAppBarConfiguration;
    private Person loggedIn;
    private View navHeader;
    private NavController navController;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_user_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile,R.id.nav_sign_out)
                .setDrawerLayout(drawerLayout)
                .build();
        String email = getIntent().getStringExtra("email");
        loadPersonProfile(email);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PROFILE_KEY, loggedIn);
        item.setChecked(true);
        drawerLayout.closeDrawers();
        switch (id) {
            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_profile:
                bundle.putParcelable(Constants.PROFILE_KEY,loggedIn);
                navController.navigate(R.id.nav_profile,bundle);
                break;
            case R.id.nav_profile_all:
                navController.navigate(R.id.nav_profile_all);
                break;
            case R.id.nav_sign_out:
                signOut();
                break;
        }
        return true;
    }

    private void signOut() {
        finish();
    }

    public void loadPersonProfile(String email) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = ref.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    loggedIn = dataSnapshot1.getValue(Person.class);
                }
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(loggedIn.getimageurl());
                final String[] downloadURL = new String[1];
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        TextView headerName = navHeader.findViewById(R.id.nav_header_name);
                        TextView headerEmail = navHeader.findViewById(R.id.nav_header_email);
                        ImageView profilePic = navHeader.findViewById(R.id.profile_image);
                        downloadURL[0] = task.getResult().toString();
                        headerName.setText(loggedIn.getName());
                        headerEmail.setText(loggedIn.getEmail());
                        Glide.with(Login.this.getApplicationContext())
                                .load(downloadURL[0])
                                .into(profilePic);
                    }
                });
                return;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
    }
}
