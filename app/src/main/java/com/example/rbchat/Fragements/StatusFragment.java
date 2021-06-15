package com.example.rbchat.Fragements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.MainActivity;
import com.example.rbchat.Adapters.FragmentsAdapter;
import com.example.rbchat.Adapters.StatusAdapter;
import com.example.rbchat.Model.Status;
import com.example.rbchat.Model.User;
import com.example.rbchat.Model.UserStatus;
import com.example.rbchat.R;
import com.example.rbchat.databinding.FragmentStatusBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusFragment extends Fragment {
    FragmentStatusBinding binding;
    FirebaseDatabase database;
    ProgressDialog dialog;
 User user;
    Query q;
    private String myphone;

    public StatusFragment() {
        // Required empty public constructor
    }

    StatusAdapter statusAdapter,mystatusAdapter;
public ArrayList<UserStatus> userstatuses,mystatusarraylist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


binding=FragmentStatusBinding.inflate(inflater,container,false);





database=FirebaseDatabase.getInstance();
        userstatuses=new ArrayList<>();
        mystatusarraylist=new ArrayList<>();

        Date date=new Date();
        user=new User();




       dialog=new ProgressDialog(getContext());
       dialog.setMessage("Uploading");
       dialog.setCancelable(false);




        statusAdapter=new StatusAdapter(getContext(),userstatuses,this);
        mystatusAdapter=new StatusAdapter(getContext(),mystatusarraylist,this);


        binding.statusrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.statusrecyclerview.setAdapter(statusAdapter);

        binding.mystatus.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mystatus.setAdapter(mystatusAdapter);




//       get value of user
getvalueofuser();

        //mystatus recycler

getmystories();

getstoriesofcontacts();

                                statusAdapter.notifyDataSetChanged();
                                mystatusAdapter.notifyDataSetChanged();


//upload my statuses
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        getmediafrommobile();
               }
        });



        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swiperefresh.setRefreshing(false);
                getmystories();
                getstoriesofcontacts();
//                                statusAdapter.notifyDataSetChanged();
//                                mystatusAdapter.notifyDataSetChanged();

            }
        });



        return binding.getRoot();
    }

    private void getmystories() {

      //  Log.e("getmystories",myphone+"abc");
        q=  database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    mystatusarraylist.clear();

                    HashMap<String,Object> obj=new HashMap<>();


obj=(HashMap<String,Object>)snapshot.getValue();
String name= (String) obj.get("name");

Long lu= (Long) obj.get("lastUpdated");
                String image=    (String) obj.get("profileImage");
                String number =   (String) obj.get("uPhoneNumber");
                //Log.e("getmystories",name+","+number);


                        UserStatus statusgroup = new UserStatus();

                        statusgroup.setName("My Stories");
                        statusgroup.setProfileImage(image);
                        statusgroup.setLastupdated(lu);
                    //    Log.e("mystatus",stories.child("name").getValue(String.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        if(!snapshot.child("statuses").hasChildren()){
                            statusgroup.setLastupdated(-1);

                        }


                        for (DataSnapshot singlestatus : snapshot.child("statuses").getChildren()) {

                            statuses.add(singlestatus.getValue(Status.class));

                        }

                        statusgroup.setStatuses(statuses);
                        mystatusarraylist.add(statusgroup);


                    statusAdapter.notifyDataSetChanged();
                    mystatusAdapter.notifyDataSetChanged();
                }
                else{Log.e("mystories","else case");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getvalueofuser() {
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);
                myphone=user.getuPhoneNumber();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getstoriesofcontacts() {


        try{

            String name,number;
            Cursor cursor= getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            cursor.moveToFirst();
            userstatuses.clear();

            do
            {
                name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                //get statuses list
                try {
                    String finalname=name;
                    q=  database.getReference().child("stories").orderByChild("uPhoneNumber").equalTo(number.replaceAll(" ",""));
                    ValueEventListener e=new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                for (DataSnapshot stories : snapshot.getChildren()) {
                                    boolean flag=false;
                                    Log.e("st frag L250",user.getuPhoneNumber()+","+stories.child("uPhoneNumber").getValue(String.class));


                                    UserStatus statusgroup = new UserStatus();

                                    statusgroup.setName(finalname);
                                    statusgroup.setProfileImage(stories.child("profileImage").getValue(String.class));
                                    statusgroup.setLastupdated(stories.child("lastUpdated").getValue(Long.class));

                                    ArrayList<Status> statuses = new ArrayList<>();

                                    for (DataSnapshot singlestatus : stories.child("statuses").getChildren()) {
                                        Status s= singlestatus.getValue(Status.class);
//remove status older than 24 hour
                                        Date date=new Date();
                                        Long now = date.getTime();
                                        Long cutoff = (now - 24 * 60 * 60 * 1000);

                                        if(s.getTimestamp()<cutoff){
                                            singlestatus.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                //    Log.e("value","removed ");
                                                }
                                            });
                                            continue;
                                        }
                                        else{
                                            //Log.e("st frag 276","else case");
                                        }

                                        statuses.add(s);

                                    }

                                    if(statuses.size()==0){
                                        //  stories.getRef().removeValue();
                                        //  Log.e("value","complete status group removed");
                                        continue;
                                    }



//                                    ArrayList<Status>  dummy=statuses;
//                                    dummy.remove(dummy.size()-1);
//
//                                    statusgroup.setStatuses(dummy);
//
//                                    if (userstatuses.contains(statusgroup)){
//                                        userstatuses.remove(statusgroup);
//                                        Log.e("st frag L304","removed");
//                                        statusAdapter.notifyDataSetChanged();
//                                    }
//                                    else{
//                                       // Log.e("st frag L304","else case");
//                                    }


                                    statusgroup.setStatuses(statuses);




                                    if(user.getuPhoneNumber().equals(stories.child("uPhoneNumber").getValue(String.class))){
                                        Log.e("st frag L313","continue "+stories.child("uPhoneNumber").getValue(String.class));
                                        continue;
                                    }



                                    userstatuses.add(statusgroup);


                                }

                                if(userstatuses.size()==0){binding.notext.setText("No Stories to Show");}
                                else{binding.notext.setText("Recent");}


//                                mystatusAdapter.notifyDataSetChanged();

                                //   userstatuses.clear();
                            }else{
                                Log.e("st frag L298","else case");
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                    q.addValueEventListener(e);

//                    q.removeEventListener(e);


                }catch(Exception e){
                    Log.e(" st frag Line 92",e.getMessage());
                }

            }while(cursor.moveToNext());
        }
        catch(Exception e)
        {
            Toast.makeText(getContext(),"error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        statusAdapter.notifyDataSetChanged();


    }

    public void getmediafrommobile() {
        Intent intent=new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,75);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null && data.getData()!=null && requestCode==75){
            dialog.show();
            FirebaseStorage storage=FirebaseStorage.getInstance();

            Date date=new Date();

            StorageReference ref=storage.getReference().child("status").child(date.getTime()+"");

            ref.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserStatus userStatus=new UserStatus();

                                userStatus.setName(user.getuName());
                                userStatus.setProfileImage(user.getuProfileImage());
                                userStatus.setLastupdated(date.getTime());
                                userStatus.setuPhoneNumber(user.getuPhoneNumber());

                                HashMap<String,Object> obj=new HashMap<>();
                                obj.put("name",userStatus.getName());
                                obj.put("profileImage",userStatus.getProfileImage());
                                obj.put("lastUpdated",userStatus.getLastupdated());
                                obj.put("uPhoneNumber",userStatus.getuPhoneNumber());

                                String Imageurl=uri.toString();

                                Status status=new Status(Imageurl,userStatus.getLastupdated());

                                database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);

                                database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").push().setValue(status);

                                dialog.dismiss();
                                Toast.makeText(getContext(),"Story Added Successfuly",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });

        }


    }



    }


