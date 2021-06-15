package com.example.rbchat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.MainActivity;
import com.example.rbchat.Fragements.StatusFragment;
import com.example.rbchat.Model.Status;
import com.example.rbchat.Model.UserStatus;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ItemSendBinding;
import com.example.rbchat.databinding.SamplestatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private final StatusFragment fragment;
    Context context;
    ArrayList<UserStatus> userstatuses;

    public StatusAdapter(Context context, ArrayList<UserStatus> userstatuses,StatusFragment fragment) {
        this.context = context;
        this.userstatuses = userstatuses;
        this.fragment=fragment;
    }


    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.samplestatus,parent,false);



        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

        UserStatus userStatusgroup = userstatuses.get(position);

                Log.e("status adapter line 53",userStatusgroup.getStatuses().size()+"");



if(userStatusgroup.getLastupdated()==-1){

    try {
        holder.binding.name.setText(userStatusgroup.getName());
         holder.binding.lastupdated.setText("Tap to add Status Update");

        holder.binding.circularstatusview.setPortionsCount(1);

        Glide.with(context).load(userStatusgroup.getProfileImage()).into(holder.binding.image);

    }
    catch (Exception e){
        Log.e("Exception : ",e.getMessage());
    }

    holder.binding.singlestatus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

fragment.getmediafrommobile();

        }
    });

}else{


    try {
        holder.binding.name.setText(userStatusgroup.getName());
        String mytime;

        try {
            mytime = java.text.DateFormat.getTimeInstance().format(userStatusgroup.getLastupdated());
        }
        catch (Exception E )
        {
            mytime = "";
        }
        holder.binding.lastupdated.setText(mytime + "");


        //  holder.binding.lastupdated.setText(userStatusgroup.getLastupdated()+"");

        holder.binding.circularstatusview.setPortionsCount(userStatusgroup.getStatuses().size());

        Status laststatus = userStatusgroup.getStatuses().get(userStatusgroup.getStatuses().size()-1);
        Glide.with(context).load(laststatus.getImageUrl()).into(holder.binding.image);

    }
    catch (Exception e){
        Log.e("Exception : ",e.getMessage());
    }


    holder.binding.singlestatus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                ArrayList<MyStory> myStories = new ArrayList<>();

                for (Status status : userStatusgroup.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));
                }


                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatusgroup.getName()) // Default is Hidden
                        .setTitleLogoUrl(userStatusgroup.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }catch (Exception e){Log.e("status adap L115",e.getMessage());}
        }
    });




}


    }

    @Override
    public int getItemCount() {
        return userstatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{
        SamplestatusBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SamplestatusBinding.bind(itemView);
        }
    }


}
