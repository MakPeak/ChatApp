package io.openim.android.sdk.models;

public class BusinessCard {
    String faceURL ;
    String nickname ;
    String userID ;

    public BusinessCard(String faceURL, String nickname, String userID) {
        this.faceURL = faceURL;
        this.nickname = nickname;
        this.userID = userID;
    }

    public String getFaceURL() {
        return faceURL;
    }

    public void setFaceURL(String faceURL) {
        this.faceURL = faceURL;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
