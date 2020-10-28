package com.vignesh.twitterclone;

import android.graphics.Bitmap;

import com.parse.ParseFile;
import com.parse.ParseObject;

public class Tweet {

    private String userName, message, objectId;
    private ParseFile image;

    public Tweet(String userName, String message, ParseFile image) {
        this.userName = userName;
        this.message = message;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getObjectId() {
        return objectId;
    }

    public ParseFile getImage() {
        return image;
    }

    public void setImage(ParseFile image) {
        this.image = image;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
