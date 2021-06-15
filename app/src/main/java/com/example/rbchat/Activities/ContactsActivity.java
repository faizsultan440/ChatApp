package com.example.rbchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.rbchat.Adapters.ContactsAdapters;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ContactsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ArrayList<User> users=new ArrayList<>();
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contacts);

        recyclerView=(RecyclerView) findViewById(R.id.contactrecyclerview);

        ContactsAdapters Adapters=new ContactsAdapters(getApplicationContext(), users);
        recyclerView.setAdapter(Adapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        database = FirebaseDatabase.getInstance();

        try{

            String name,number;
            Cursor cursor= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            cursor.moveToFirst();
            users.clear();
            do
            {
                name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //   Log.e("contactss",cursor.getCount()+"");
                //   Log.e("contactss",name+"  ,  "+number);
                //   Log.e("contactss",number.replaceAll(" ",""));

                Query q=database.getReference().child("users").orderByChild("uPhoneNumber").equalTo(number.replaceAll(" ",""));
                String finalName = name;
                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            User user = snapshot1.getValue(User.class);
                            user.setuName(finalName);
                            if(user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                                //flag=true;
                                continue;
                            }

                            users.add(user);
                        }
                        Adapters.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }while(cursor.moveToNext());
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"cant get contact "+e.getMessage(),Toast.LENGTH_SHORT).show();
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



}