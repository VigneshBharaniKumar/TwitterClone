package com.vignesh.twitterclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

public class TweetsRecyclerAdapter extends RecyclerView.Adapter<TweetsViewHolder> {

    private ArrayList<Tweet> tweets;

    public TweetsRecyclerAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public TweetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tweets_view_holder, parent,false);
        TweetsViewHolder tweetsViewHolder = new TweetsViewHolder(view);
        return tweetsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TweetsViewHolder holder, int position) {

        holder.getTxtUserName().setText(tweets.get(position).getUserName());
        holder.getTxtMessage().setText(tweets.get(position).getMessage());

        if (tweets.get(position).getImage() != null) {

            ParseFile postPicture = tweets.get(position).getImage();
            postPicture.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {

                        Bitmap imgBitMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        holder.getImgPost().setImageBitmap(imgBitMap);
                        holder.getImgPost().setVisibility(View.VISIBLE);

                    }
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
