package com.example.rbchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rbchat.Model.Calls;
import com.example.rbchat.R;
import com.example.rbchat.databinding.SampleacallBinding;

import java.util.ArrayList;

public class CallsAdapters extends RecyclerView.Adapter<CallsAdapters.UserViewHolder> {

        Context context;
        ArrayList<Calls> calls;

public CallsAdapters (Context context, ArrayList<Calls> calls)
        {
        this.context = context;
        this.calls = calls;
        }

@NonNull
@Override
public CallsAdapters.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sampleacall, parent, false);
        return new CallsAdapters.UserViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//        Calls user = calls.get(position);
//
//        holder.binding.contactnameofperson.setText(user.getuName());
//
//        Glide.with(context).load(user.getuProfileImage())
//        .placeholder(R.drawable.ic_avatar)
//        .into(holder.binding.profileImage);
//
//
//
//        holder.binding.contactchaticon.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("name", user.getuName());
//        intent.putExtra("UId", user.getUid());
//        intent.putExtra("phonenumber",user.getuPhoneNumber());
//        intent.putExtra("profilepicture",user.getuProfileImage()+"");
//
//        //   Log.e("contactss from adapter",user.getuProfileImage()+"");
//
//        context.startActivity(intent);
//
//        }
//        });
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        Intent intent = new Intent(context, ShowProfileActivity.class);
//        intent.putExtra("name", user.getuName());
//        intent.putExtra("UId", user.getUid());
//        intent.putExtra("phonenumber",user.getuPhoneNumber());
//        intent.putExtra("profilepicture",user.getuProfileImage()+"");
//        context.startActivity(intent);
//
//        }
//        });

        }

@Override
public int getItemCount() {
        return calls.size();
        }

public class UserViewHolder extends RecyclerView.ViewHolder{

    SampleacallBinding binding;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = SampleacallBinding.bind(itemView);

    }
}

}
