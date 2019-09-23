package com.example.zik.droplet.ui.AllProfiles;

import android.content.Context;
import android.os.Bundle;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayProfilesList extends AppCompatActivity {

    final List<Person> personList = new ArrayList<>();
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profiles_main);
        final RecyclerView profilesRecyclerView = findViewById(R.id.list);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person;
                for (DataSnapshot profileSnapshot : dataSnapshot.getChildren()) {
                    person = profileSnapshot.getValue(Person.class);
                    personList.add(person);
                    ProfileAdapter profileAdapter = new ProfileAdapter(personList, DisplayProfilesList.this);
                    profilesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    profilesRecyclerView.setHasFixedSize(true);
                    profilesRecyclerView.setAdapter(profileAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
