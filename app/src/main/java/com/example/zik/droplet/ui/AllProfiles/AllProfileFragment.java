package com.example.zik.droplet.ui.AllProfiles;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zik.droplet.R;
import com.example.zik.droplet.Utils.Constants;
import com.example.zik.droplet.Utils.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllProfileFragment extends Fragment {

    private AllViewModel profileViewModel;
    private View root;
    private Person loggedIn;
    final List<Person> personList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(AllViewModel.class);
        root = inflater.inflate(R.layout.profiles_main, container, false);
            final RecyclerView profilesRecyclerView = root.findViewById(R.id.list);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
            dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person;
                for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                    person = profileSnapshot.getValue(Person.class);
                    personList.add(person);
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