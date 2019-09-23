package com.example.zik.droplet.ui.AllProfiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zik.droplet.R;
import com.example.zik.droplet.Utils.Person;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfilesViewHolder> {

        private List<Person> profileList;
        Context context;

        public ProfileAdapter(List<Person> pList, Context c){ profileList = pList;context = c; }

        @Override
        public ProfilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutIdForListItem = R.layout.profile_list_item;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            boolean attach = false;
            View view = inflater.inflate(layoutIdForListItem, parent, attach);
            ProfilesViewHolder viewHolder = new ProfilesViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ProfilesViewHolder holder, int position) {
            holder.nameView.setText(profileList.get(position).getName());
            holder.locationView.setText(profileList.get(position).getLocation());
        }

        @Override
        public int getItemCount() {
            return profileList.size();
        }

        class ProfilesViewHolder extends RecyclerView.ViewHolder {
            final TextView nameView, locationView;
            public ProfilesViewHolder(View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.name);
                locationView = itemView.findViewById(R.id.location);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DisplaySingleProfile.class);
                        intent.putExtra( "profile", profileList.get(getAdapterPosition()));
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

