package com.vignesh.twitterclone;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TwitterUsersViewHolder extends RecyclerView.ViewHolder {

    private ImageView userImage;
    private TextView txtUserName;

    public TwitterUsersViewHolder(@NonNull View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.imgRecyclerView_UserImage);
        txtUserName = itemView.findViewById(R.id.txtRecyclerView_UserName_twitterUsers);

    }

    public ImageView getUserImage() {
        return userImage;
    }

    public TextView getTxtUserName() {
        return txtUserName;
    }
}
