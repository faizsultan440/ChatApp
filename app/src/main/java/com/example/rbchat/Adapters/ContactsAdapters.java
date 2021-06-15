package com.example.rbchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.CallActivity;
import com.example.rbchat.Activities.ChatActivity;
import com.example.rbchat.Activities.ContactsActivity;
import com.example.rbchat.Activities.ShowProfileActivity;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.example.rbchat.databinding.SamplecontactBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class ContactsAdapters extends RecyclerView.Adapter<ContactsAdapters.UserViewHolder> {


    FirebaseDatabase database;
    Context context;
    ArrayList<User> users;

    public ContactsAdapters (Context context, ArrayList<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ContactsAdapters.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.samplecontact, parent, false);
        return new ContactsAdapters.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        database=FirebaseDatabase.getInstance();

        holder.binding.contactnameofperson.setText(user.getuName());

        Glide.with(context).load(user.getuProfileImage())
                .placeholder(R.drawable.ic_avatar)
                .into(holder.binding.profileImage);

        //set online/offline status of contact.
        setstatus(holder,user.getUid());



        holder.binding.contactchaticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("name", user.getuName());
//                intent.putExtra("UId", user.getUid());
//                intent.putExtra("phonenumber",user.getuPhoneNumber());
//                intent.putExtra("profilepicture",user.getuProfileImage()+"");
                intent.putExtra("user",user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

             //   Log.e("contactss from adapter",user.getuProfileImage()+"");

          try {
              (context).startActivity(intent);
          }catch(Exception e){
              Log.e("contact activity",e.getMessage());
          }





            }
        });

        holder.binding.contactcallicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CallActivity.class);
//                intent.putExtra("name", user.getuName());
//                intent.putExtra("UId", user.getUid());
//                intent.putExtra("phonenumber",user.getuPhoneNumber());
//                intent.putExtra("profilepicture",user.getuProfileImage()+"");
intent.putExtra("user",user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                (context).startActivity(intent);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProfileActivity.class);
//                intent.putExtra("name", user.getuName());
//                intent.putExtra("UId", user.getUid());
//                intent.putExtra("phonenumber",user.getuPhoneNumber());
//                intent.putExtra("profilepicture",user.getuProfileImage()+"");
                intent.putExtra("user",user);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                (context).startActivity(intent);

            }
        });

    }



    private void setstatus(@NonNull UserViewHolder holder,String id) {
        database.getReference().child("presence").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String status = snapshot.getValue(String.class);
                    if(!status.isEmpty()) {
                        if (status.equals("Online"))
                        {
                            holder.binding.contactstatus.setText("Online");
                        }
                        else{
                            String mytime;

                            try {
                                mytime = java.text.DateFormat.getTimeInstance().format(Long.parseLong(status));
                            }
                            catch (Exception E )
                            {
                                Log.e("contact adp L132",E.getMessage());
                                mytime = "";
                            }
                            holder.binding.contactstatus.setText("Last Seen: "+mytime);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        SamplecontactBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SamplecontactBinding.bind(itemView);

        }
    }

}
