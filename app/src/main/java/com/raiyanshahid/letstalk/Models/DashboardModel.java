package com.raiyanshahid.letstalk.Models;

public class DashboardModel {

    private String name;
    private String bio;
    private String time;
    private String image;
    private String uid;

    public DashboardModel(String name, String bio, String time,String image,String uid) {
        this.name = name;
        this.bio = bio;
        this.time = time;
        this.image = image;
        this.uid = uid;
    }

    public DashboardModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
