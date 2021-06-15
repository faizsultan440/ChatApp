package com.example.rbchat.Fragements;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rbchat.Activities.ContactsActivity;
import com.example.rbchat.Activities.MainActivity;
import com.example.rbchat.Adapters.UsersAdapters;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    FirebaseDatabase database;
    ArrayList <User> users=new ArrayList<>();


    public ChatFragment() {
        // Required empty public constructor
    }

 //   ArrayList<User> list=new ArrayList<>();
RecyclerView recyclerView;
    FloatingActionButton contactfab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat, container, false);



        recyclerView=view.findViewById(R.id.chatrecyclerview);
        contactfab=view.findViewById(R.id.contactfab);


contactfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getContext(), ContactsActivity.class);
                startActivity(i);
            }
        });



        UsersAdapters usersAdapters=new UsersAdapters(getContext(), users);
        recyclerView.setAdapter(usersAdapters);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        database = FirebaseDatabase.getInstance();


        try{

            String name,number;
            Cursor cursor= getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
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
                        usersAdapters.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }while(cursor.moveToNext());
        }
        catch(Exception e)
        {
            Toast.makeText(getContext(),"cant get contact "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


//        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//                for(DataSnapshot snapshot1 : snapshot.getChildren())
//                {
//                    User user = snapshot1.getValue(User.class);
//                    users.add(user);
//                }
//                usersAdapters.notifyDataSetChanged();
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


       // users = new ArrayList<>();

        //user list (get from db)
     // User user=new User("123","Faiz","f@gmail.com","pass");
       // list.add(user);

     //   recyclerView.setHasFixedSize(true);











        // Inflate the layout for this fragment
        return view;
    }



}