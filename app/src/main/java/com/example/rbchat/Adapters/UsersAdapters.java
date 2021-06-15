package com.example.rbchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.ChatActivity;
import com.example.rbchat.R;
import com.example.rbchat.Model.User;
import com.example.rbchat.databinding.SamplechatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.UserViewHolder> {

    Context context;
    ArrayList<User> users;

    public UsersAdapters (Context context, ArrayList<User> users)
    {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.samplechat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMessage").getValue(String.class);
                            Long msgTime = snapshot.child("lastMessageTime").getValue(Long.class);

                            String mytime;

                        holder.binding.lstMsg.setText(lastMsg);
                       try {
                            mytime = java.text.DateFormat.getTimeInstance().format(msgTime);
                       }
                       catch (Exception E )
                       {
                           mytime = "";
                       }
                            holder.binding.msgTime. setText(mytime + "");
                        }else
                        {
                            holder.binding.lstMsg.setText("Tap to chat...");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        holder.binding.nameofperson.setText(user.getuName());

        Glide.with(context).load(user.getuProfileImage())
                .placeholder(R.drawable.ic_avatar)
                .into(holder.binding.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("name", user.getuName());
//                intent.putExtra("UId", user.getUid());
//                intent.putExtra("uProfileimage", user.getuProfileImage());
                intent.putExtra("user",user);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        SamplechatBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        binding = SamplechatBinding.bind(itemView);

        }
    }

}
