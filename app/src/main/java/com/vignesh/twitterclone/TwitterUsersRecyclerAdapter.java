package com.vignesh.twitterclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TwitterUsersRecyclerAdapter extends RecyclerView.Adapter<TwitterUsersViewHolder> {

    private ArrayList<String> users;
    private onClickUserInterface onClickUserInterface;

    public interface onClickUserInterface {

        void onClickUser(String selectedUser);

    }

    public TwitterUsersRecyclerAdapter(ArrayList<String> users, TwitterUsersRecyclerAdapter.onClickUserInterface onClickUserInterface) {
        this.users = users;
        this.onClickUserInterface = onClickUserInterface;
    }

    @NonNull
    @Override
    public TwitterUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.twitter_users_view_holder, parent, false);
        TwitterUsersViewHolder twitterUsersViewHolder = new TwitterUsersViewHolder(view);
        return twitterUsersViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull TwitterUsersViewHolder holder, final int position) {
        holder.getTxtUserName().setText(users.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUserInterface.onClickUser(users.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
