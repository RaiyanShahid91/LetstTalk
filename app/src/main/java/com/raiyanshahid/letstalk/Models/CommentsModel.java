package com.raiyanshahid.letstalk.Models;

public class CommentsModel {

    String comment;
    String timestamp;
    String name;
    String profileImage;
    String uid;

    public CommentsModel(String comment, String timestamp, String name,String profileImage, String uid) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.name = name;
        this.profileImage = profileImage;
        this.uid = uid;
    }

    public CommentsModel() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
