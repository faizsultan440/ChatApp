package com.example.rbchat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.rbchat.R;

public class Videoview extends AppCompatActivity {
    VideoView video;
    MediaController mc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        video=findViewById(R.id.vidview);
        mc= new MediaController(this);
        video.setMediaController(mc);
        getSupportActionBar().hide();

        String url=getIntent().getStringExtra("url");
        video.setVideoURI(Uri.parse(url));
                video.start();



    }
}