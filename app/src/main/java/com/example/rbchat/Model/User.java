package com.example.rbchat.Model;

import java.io.Serializable;

public class User implements Serializable {

   private String uid, uName, uPhoneNumber, uProfileImage;

   public User ()
   {

   }

    public User(String uid, String uName, String uPhoneNumber, String uProfileImage) {
        this.uid = uid;
        this.uName = uName;
        this.uPhoneNumber = uPhoneNumber;
        this.uProfileImage = uProfileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuPhoneNumber() {
        return uPhoneNumber;
    }

    public void setuPhoneNumber(String uPhoneNumber) {
        this.uPhoneNumber = uPhoneNumber;
    }

    public String getuProfileImage() {
        return uProfileImage;
    }

    public void setuProfileImage(String uProfileImage) {
        this.uProfileImage = uProfileImage;
    }
}
