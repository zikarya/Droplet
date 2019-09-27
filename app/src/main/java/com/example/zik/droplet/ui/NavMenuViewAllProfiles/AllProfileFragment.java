package com.example.zik.droplet.ui.NavMenuViewAllProfiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zik.droplet.R;
import com.example.zik.droplet.Utils.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllProfileFragment extends Fragment {
// SET UP ALLPROFILE FRAGMENT, WHICH CONTAINS A LIST OF THE PROFILES
// CURRENTLY REGISTERED IN THE DATABASE (VIEWING PURPOSES ONLY)

    private AllViewModel profileViewModel;
    private View root;
    private Person loggedIn;
    final List<Person> personList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(AllViewModel.class);
        root = inflater.inflate(R.layout.all_profiles_main, container, false);
            final RecyclerView profilesRecyclerView = root.findViewById(R.id.list);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
            dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person;
                for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                    person = profileSnapshot.getValue(Person.class);
                    personList.add(person);
                    // SETUP PROFILEADAPTER TO SHOW EACH PROFILE IN A SEPERATE VIEW WITHIN
                    // A SCROLLABLE VIEW (RECYCLERVIEW)
                    ProfileAdapter profileAdapter = new ProfileAdapter(personList, getContext());
                    profilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    profilesRecyclerView.setHasFixedSize(true);
                    profilesRecyclerView.setAdapter(profileAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
            return root;
    }


}