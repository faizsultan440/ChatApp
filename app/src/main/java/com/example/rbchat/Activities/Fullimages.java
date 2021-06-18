package com.example.rbchat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.rbchat.R;

public class Fullimages extends AppCompatActivity {

    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimages);


        img=(ImageView) findViewById(R.id.fulimg);

        String url=getIntent().getStringExtra("url");
        Glide.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .into(img);



    }
}