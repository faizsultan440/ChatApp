package com.example.rbchat;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class App extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!

    FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!


        Log.e("from new","hello");

        database=FirebaseDatabase.getInstance();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            database.getReference().child("presence").child(currentId).setValue("Online");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }

    }



    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("from new","Terminate");
              String currentId = FirebaseAuth.getInstance().getUid();
        try {
 Date date=new Date();
            database.getReference().child("presence").child(currentId).setValue(date.getTime()+"");
        }
        catch(NullPointerException e) {
            System.out.println("NullPointerException thrown!");
        }



    }
}