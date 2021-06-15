package com.example.rbchat.Activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TaskStackBuilder;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.rbchat.Adapters.FragmentsAdapter;
import com.example.rbchat.Adapters.UsersAdapters;
import com.example.rbchat.R;
import com.example.rbchat.Model.User;
import com.example.rbchat.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    private DatabaseReference reference;
    ArrayList <User> users;
    UsersAdapters usersAdapter;

    TabLayout tabLayout;
    ViewPager viewPager;

    String currentId = FirebaseAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database=FirebaseDatabase.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("OfflineCapabilities");
        reference.keepSynced(true);
//
//        try {
//            database.getReference().child("presence").child(currentId).setValue("Online");
//        } catch (NullPointerException e) {
//            System.out.println("NullPointerException thrown!");
//        }
//        users = new ArrayList<>();
//        usersAdapter = new UsersAdapters(this, users);
//        binding.recyclerview.setAdapter(usersAdapter);
//
//        database = FirebaseDatabase.getInstance();
//        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//           users.clear();
//           for(DataSnapshot snapshot1 : snapshot.getChildren())
//           {
//               User user = snapshot1.getValue(User.class);
//               users.add(user);
//           }
//           usersAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},1 );

//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.RECORD_AUDIO) !=
//                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
//                (this, android.Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED ) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
//                    2);
//        }
//

        viewPager=(ViewPager) findViewById(R.id.viewpager);
        tabLayout=(TabLayout) findViewById(R.id.tablayout);

viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group:
                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
                break;
            case R.id.search:
                Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED && grantResults[3]==PackageManager.PERMISSION_GRANTED){
            Log.e("RB-CHAT","Permissions granted");
        }
        else{
            Log.e("RB-CHAT","Permissions  denied");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},1 );

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            database.getReference().child("presence").child(currentId).setValue("Online");
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
            database.getReference().child("presence").child(currentId).setValue(date.getTime()+"");
        }
        catch(Exception e) {
            Log.e(this.getClass().getSimpleName()+" OnPause: ",e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {

        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            Date date=new Date();
           database.getReference().child("presence").child(currentId).setValue(date.getTime()+"");
        }
        catch(Exception e) {
           Log.e(this.getClass().getSimpleName()+" Ondestroy: ",e.getMessage());
        }

        super.onDestroy();

    }

}
