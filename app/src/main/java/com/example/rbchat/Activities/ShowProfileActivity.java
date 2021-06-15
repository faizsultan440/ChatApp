package com.example.rbchat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ActivityMainBinding;
import com.example.rbchat.databinding.ActivityShowProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ShowProfileActivity extends AppCompatActivity {

    ActivityShowProfileBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user=(User)getIntent().getSerializableExtra("user");


        binding.phonenumber.setText(user.getuPhoneNumber());

        getSupportActionBar().setTitle(user.getuName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(getApplicationContext()).load(user.getuProfileImage())
                .placeholder(R.drawable.ic_avatar)
                .into(binding.profile);

        binding.contactchaticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//                intent.putExtra("name", getIntent().getStringExtra("name"));
//                intent.putExtra("UId", getIntent().getStringExtra("UId"));
//                intent.putExtra("phonenumber",getIntent().getStringExtra("phonenumber"));
//                intent.putExtra("profilepicture",getIntent().getStringExtra("profilepicture")+"");

                //   Log.e("contactss from adapter",user.getuProfileImage()+"");

                intent.putExtra("user",user);

                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("Online");
        }
        catch(Exception e) {
            Log.e(this.getClass().getSimpleName()+" OnResume: ",e.getMessage());
        }
    }






    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            Date date=new Date();
            FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue(date.getTime()+"");
        }
        catch(Exception e) {
            Log.e(this.getClass().getSimpleName()+" OnPause: ",e.getMessage());
        }

    }



}