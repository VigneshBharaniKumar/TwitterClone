package com.vignesh.twitterclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TweetsTab extends Fragment {

    private RecyclerView recyclerView_tweets;
    private SwipeRefreshLayout pullToRefreshLayout;
    private SweetAlertDialog alertDialog;
    private TextInputLayout edtSendTweetHome;
    private ImageButton btnSendTweetHome;
    private Tweet tweet;
    private ImageView imgShare;
    private Button btnSelectImage, btnCaptureImage;

    private Bitmap receivedBitmap = null;

    public final static String TWEET_MESSAGE_KEY = "tweetMessage";
    public final static String TWEET_USER_KEY = "user";
    public final static String TWEET_IMAGE_KEY = "tweetImage";

    public TweetsTab() {
        // Required empty public constructor
    }

    public static TweetsTab newInstance(String param1, String param2) {
        TweetsTab fragment = new TweetsTab();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tweets_tab, container, false);

        recyclerView_tweets = view.findViewById(R.id.recyclerView_tweets);
        pullToRefreshLayout = view.findViewById(R.id.pullToRefreshLayout);
        edtSendTweetHome = view.findViewById(R.id.edt_sendTweet_home);
        btnSendTweetHome = view.findViewById(R.id.btn_sendTweet_home);
        imgShare = view.findViewById(R.id.imgShare_home);
        btnSelectImage = view.findViewById(R.id.btnSelectImage_home);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage_home);

        refreshTweets();

        pullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTweets();
            }
        });

        btnSendTweetHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSendTweetHome.getEditText().getText().toString() != null && !edtSendTweetHome.getEditText().getText().toString().equals("")) {

                    ParseFile parseFile = null;

                    if (receivedBitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        receivedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();

                        parseFile = new ParseFile("image.png", bytes);
                    }
                    tweet = new Tweet(ParseUser.getCurrentUser().getUsername(),
                            edtSendTweetHome.getEditText().getText().toString(),
                            parseFile);
                    sendTweet(tweet);
                }
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);

                } else {
                    pickImage();
                }
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1001);

                } else {
                    captureImage();
                }
            }
        });

        return view;

    }

    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2000);

    }

    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 2001);
        }

    }

    private void refreshTweets() {

        pullToRefreshLayout.setRefreshing(true);

        final ArrayList<Tweet> tweets = new ArrayList<>();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Tweets");
        query1.whereContainedIn(TWEET_USER_KEY, ParseUser.getCurrentUser().getList("following"));

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Tweets");
        query2.whereEqualTo(TWEET_USER_KEY, ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> list = new ArrayList<ParseQuery<ParseObject>>();
        list.add(query1);
        list.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(list);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                pullToRefreshLayout.setRefreshing(false);

                if (e == null) {
                    for (ParseObject obj : objects) {

                        Tweet tweet = new Tweet(
                                obj.get(TWEET_USER_KEY).toString(),
                                obj.get(TWEET_MESSAGE_KEY).toString(),
                                (ParseFile) obj.get(TWEET_IMAGE_KEY));

                        tweets.add(tweet);

                    }

                    recyclerView_tweets.setAdapter(new TweetsRecyclerAdapter(tweets));
                    recyclerView_tweets.setLayoutManager(new LinearLayoutManager(getActivity()));

                } else {

                    alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                    alertDialog.setContentText("Error : " + e)
                            .setTitleText("Error");
                    alertDialog.show();

                }
            }
        });

    }

    public void sendTweet(final Tweet tweet) {

        alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setCancelable(false);
        alertDialog.show();

        final ParseObject object = new ParseObject("Tweets");
        object.put(TWEET_MESSAGE_KEY, tweet.getMessage());
        object.put(TWEET_USER_KEY, tweet.getUserName());
        if (tweet.getImage() != null) {
            object.put(TWEET_IMAGE_KEY, tweet.getImage());
        }

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                alertDialog.dismissWithAnimation();

                if (e == null) {
                    tweet.setObjectId(object.getObjectId());

                    alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitleText("Tweet Sent");
                    alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            alertDialog.dismissWithAnimation();
                        }
                    });
                    alertDialog.show();
                    edtSendTweetHome.getEditText().setText(null);
                    edtSendTweetHome.getEditText().clearFocus();
                } else {

                    alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitleText("Failed");
                    alertDialog.setContentText("Error : " + e);
                    alertDialog.show();

                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2001 && resultCode == getActivity().RESULT_OK) {

            /*assert data != null;
            Uri uri = data.getData();*/

            if (data != null) {

                Bundle extras = data.getExtras();
                receivedBitmap = (Bitmap) extras.get("data");
                ImageView imageView = getView().findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);

               /* receivedBitmap = MediaStore.Images.Media.getBitmap(SendTweet.this.getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);*/

            }

        } else if (requestCode == 2000 && resultCode == getActivity().RESULT_OK) {

            assert data != null;
            Uri uri = data.getData();

            if (data != null) {

                /*Bundle extras = data.getExtras();
                receivedBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);*/

                try {
                    receivedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView imageView = getView().findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                pickImage();

            }

        }

        if (requestCode == 1001) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                captureImage();

            }

        }

    }

}
