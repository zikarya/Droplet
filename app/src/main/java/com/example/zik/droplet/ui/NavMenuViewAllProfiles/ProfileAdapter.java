package com.example.zik.droplet.ui.NavMenuViewAllProfiles;

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
/////////////////////////////////////////////////////////////////////////////////////
// ADAPTER CREATED TO SHOW EACH PROFILE IN ITS OWN VIEW, IT IS POSSIBLE TO ADD
// ONCLICKLISTENER TO EACH PROFILE VIEW TO PERFORM FURTHER TASKS IN THE FUTURE
// (EG SELECT USER FOR THE PURPOSE OF SENDING MESSAGE ETC
//////////////////////////////////////////////////////////////////////////////////////
        private List<Person> profileList;
        Context context;

        public ProfileAdapter(List<Person> pList, Context c){ profileList = pList;context = c; }

        @Override
        public ProfilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layoutIdForListItem = R.layout.all_profile_list_item;
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
                       // CAN SET FURTHER ACTIONS HERE TO ALLOW USER TO PERFORM TASK ON INDIVIDUAL
                       // VIEW (EG SELECT PROFILE TO SEND A MESSAGE TO)
                    }
                });
            }
        }
    }

