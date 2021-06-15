package com.example.rbchat.Fragements;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rbchat.Adapters.ContactsAdapters;
import com.example.rbchat.Adapters.UsersAdapters;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    public ContactsFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase database;
    ArrayList<User> users=new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView=view.findViewById(R.id.contactrecyclerview);

        ContactsAdapters Adapters=new ContactsAdapters(getContext(), users);
        recyclerView.setAdapter(Adapters);
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
            Toast.makeText(getContext(),"cant get contact "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}