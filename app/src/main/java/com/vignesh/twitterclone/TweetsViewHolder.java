package com.vignesh.twitterclone;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TweetsViewHolder extends RecyclerView.ViewHolder {

    private TextView txtUserName, txtMessage;
    private ImageView imgPost;

    public TweetsViewHolder(@NonNull View itemView) {
        super(itemView);
        txtUserName = itemView.findViewById(R.id.txtUserName_tweets);
        txtMessage = itemView.findViewById(R.id.txtTweetMessage_Tweet);
        imgPost = itemView.findViewById(R.id.imgPost_Tweet);
    }

    public TextView getTxtUserName() {
        return txtUserName;
    }

    public void setTxtUserName(TextView txtUserName) {
        this.txtUserName = txtUserName;
    }

    public TextView getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(TextView txtMessage) {
        this.txtMessage = txtMessage;
    }

    public ImageView getImgPost() {
        return imgPost;
    }
}
