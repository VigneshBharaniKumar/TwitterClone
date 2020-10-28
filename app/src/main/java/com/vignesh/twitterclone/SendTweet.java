package com.vignesh.twitterclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SendTweet extends AppCompatActivity {

    public final static String TWEET_MESSAGE_KEY = "tweetMessage";
    public final static String TWEET_USER_KEY = "user";
    public final static String TWEET_IMAGE_KEY = "tweetImage";

    public SweetAlertDialog alertDialog;

    private Tweet tweet;

    private Bitmap receivedBitmap = null;

    private TextInputLayout edtSendTweetHome;
    private ImageButton btnSendTweetHome;
    private ImageView imgShare;
    private Button btnSelectImage, btnCaptureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        edtSendTweetHome = findViewById(R.id.edt_sendTweet_home);
        btnSendTweetHome = findViewById(R.id.btn_sendTweet_home);
        imgShare = findViewById(R.id.imgShare_home);
        btnSelectImage = findViewById(R.id.btnSelectImage_home);
        btnCaptureImage = findViewById(R.id.btnCaptureImage_home);

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
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(SendTweet.this,
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
                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(SendTweet.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1001);

                } else {
                    captureImage();
                }
            }
        });

    }

    private void pickImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2000);

    }

    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2001);
        }

    }

    public void sendTweet(final Tweet tweet) {

        alertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
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

                    alertDialog = new SweetAlertDialog(SendTweet.this, SweetAlertDialog.SUCCESS_TYPE);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitleText("Tweet Sent");
                    alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
                    alertDialog.show();
                    imgShare.setImageBitmap(null);
                    imgShare.setVisibility(View.GONE);
                    edtSendTweetHome.getEditText().setText(null);
                    edtSendTweetHome.getEditText().clearFocus();
                } else {

                    alertDialog = new SweetAlertDialog(SendTweet.this, SweetAlertDialog.ERROR_TYPE);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitleText("Failed");
                    alertDialog.setContentText("Error : " + e);
                    alertDialog.show();

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2001 && resultCode == RESULT_OK) {

            /*assert data != null;
            Uri uri = data.getData();*/

            if (data != null) {

                Bundle extras = data.getExtras();
                receivedBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);

               /* receivedBitmap = MediaStore.Images.Media.getBitmap(SendTweet.this.getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);*/

            }

        } else if (requestCode == 2000 && resultCode == RESULT_OK) {

            assert data != null;
            Uri uri = data.getData();

            if (data != null) {

                /*Bundle extras = data.getExtras();
                receivedBitmap = (Bitmap) extras.get("data");
                ImageView imageView = findViewById(R.id.imgShare_home);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(receivedBitmap);*/

                try {
                    receivedBitmap = MediaStore.Images.Media.getBitmap(SendTweet.this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView imageView = findViewById(R.id.imgShare_home);
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
