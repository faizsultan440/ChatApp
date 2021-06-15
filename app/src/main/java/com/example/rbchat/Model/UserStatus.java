package com.example.rbchat.Model;

import java.util.ArrayList;

public class UserStatus {
    public String getuPhoneNumber() {
        return uPhoneNumber;
    }

    public void setuPhoneNumber(String uPhoneNumber) {
        this.uPhoneNumber = uPhoneNumber;
    }

    private String uPhoneNumber;
    private String name,profileImage;
private long lastupdated;
private ArrayList<Status> statuses;

    public UserStatus(String name, String profileImage, long lastupdated, String uPhoneNumber,ArrayList<Status> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastupdated = lastupdated;
        this.uPhoneNumber=uPhoneNumber;
        this.statuses = statuses;
    }

    public UserStatus() {
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

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
