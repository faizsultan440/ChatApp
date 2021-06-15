package com.example.rbchat.Fragements;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rbchat.Activities.ContactsActivity;
import com.example.rbchat.Adapters.CallsAdapters;
import com.example.rbchat.Adapters.ContactsAdapters;
import com.example.rbchat.Model.Calls;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class CallsFragment extends Fragment {



    public CallsFragment() {
        // Required empty public constructor
    }

    ArrayList<Calls> calls=new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton contactfab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_calls, container, false);

        Calls call=new Calls("dummy","1","2",54545,"m",54564);

        calls.add(call);

        recyclerView=view.findViewById(R.id.callfragmentrecyclerview);
        contactfab=view.findViewById(R.id.contactfab);


        contactfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getContext(), ContactsActivity.class);
                startActivity(i);
            }
        });





        CallsAdapters Adapters=new CallsAdapters(getContext(), calls);
        recyclerView.setAdapter(Adapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));









        // Inflate the layout for this fragment
        return view;
    }
}