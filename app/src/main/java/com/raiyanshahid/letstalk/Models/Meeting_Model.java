package com.raiyanshahid.letstalk.Models;

public class Meeting_Model {
    String secretCode;
    String time;
    String name;
    String uid;

    public Meeting_Model(String secretCode, String time, String name, String uid) {
        this.secretCode = secretCode;
        this.time = time;
        this.name = name;
        this.uid = uid;
    }

    public Meeting_Model() {
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
