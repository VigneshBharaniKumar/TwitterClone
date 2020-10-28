package com.vignesh.twitterclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TwitterUsersRecyclerAdapter extends RecyclerView.Adapter<TwitterUsersViewHolder> {

    private ArrayList<String> users;
    private ArrayList<String> usersFull;
    private onClickUserInterface onClickUserInterface;

    private SweetAlertDialog alertDialog;

    public interface onClickUserInterface {

        void onClickUser(String selectedUser);

    }

    public TwitterUsersRecyclerAdapter(ArrayList<String> users, TwitterUsersRecyclerAdapter.onClickUserInterface onClickUserInterface) {
        this.users = users;
        this.onClickUserInterface = onClickUserInterface;

        usersFull = new ArrayList<>(users);
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
    public void onBindViewHolder(@NonNull final TwitterUsersViewHolder holder, final int position) {
        holder.getTxtUserName().setText(users.get(position));

        for (String user : users) {
            if (ParseUser.getCurrentUser().getList("following") != null)
                if (ParseUser.getCurrentUser().getList("following").contains(users.get(position))) {
                    holder.getCbFollow().setChecked(true);
                }
        }

        holder.getCbFollow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickUserInterface.onClickUser(users.get(position));

                if (holder.getCbFollow().isChecked()) {
                    Toast.makeText(v.getContext(), "Started following " + users.get(position), Toast.LENGTH_SHORT).show();

                    ParseUser.getCurrentUser().add("following", users.get(position));
                } else if (!holder.getCbFollow().isChecked()) {
                    Toast.makeText(v.getContext(), "Un-followed " + users.get(position), Toast.LENGTH_SHORT).show();

                    ParseUser.getCurrentUser().getList("following").remove(users.get(position));
                    List currentFollowingList = ParseUser.getCurrentUser().getList("following");
                    ParseUser.getCurrentUser().remove("following");
                    ParseUser.getCurrentUser().put("following", currentFollowingList);
                }

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                        } else {
                            alertDialog = new SweetAlertDialog(v.getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Error : " + e);
                            alertDialog.show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public Filter getFilter() {
        return usersFilter;
    }

    private Filter usersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<String> filteredUsers = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {

                filteredUsers.addAll(usersFull);

            } else {

                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String user : usersFull) {

                    if (user.toLowerCase().contains(filterPattern)) {

                        filteredUsers.add(user);

                    }

                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredUsers;
            return  results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            users.clear();
            users.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };

}
