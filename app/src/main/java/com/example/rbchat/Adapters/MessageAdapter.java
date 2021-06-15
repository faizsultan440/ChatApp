package com.example.rbchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.ChatActivity;
import com.example.rbchat.Activities.MainActivity;
import com.example.rbchat.Model.Message;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ActivityChatBinding;
import com.example.rbchat.databinding.ItemRecieveBinding;
import com.example.rbchat.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    ItemSendBinding binding;
    Context context;
    ArrayList<Message> messages;
    ArrayList<SentViewHolder> sentlist=new ArrayList<>();
    ArrayList<ReceiverViewHolder> recievelist=new ArrayList<>();
    ArrayList<Message> selectedmessage=new ArrayList<>();
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    ActivityChatBinding activityChatBinding;
    String senderRoom;
    String receiverRoom;
    //boolean selected=false;
    private static int currentPosition=0;


    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom, ActivityChatBinding binding)
    {
        setHasStableIds(true);

        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
        activityChatBinding=binding;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId()))
        {
            return ITEM_SENT;

        }else{
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        final boolean[] selected = {true};

//        holder.setIsRecyclable(false);





        int reactions[] = new int[]{
                R.drawable.ic_like,
                R.drawable.ic_loved,
                R.drawable.ic_heart,
                R.drawable.ic_sad,
                R.drawable.ic_angry,
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        try {
            ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

                if(pos != -1) {
                    if (holder.getClass() == SentViewHolder.class) {
                        SentViewHolder viewHolder = (SentViewHolder) holder;
                        viewHolder.binding.feeling.setImageResource(reactions[pos]);
                        viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    } else {

                        ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                        viewHolder.binding.feeling.setImageResource(reactions[pos]);
                        viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    }
                }
                Log.e("FEEL", message.getFeeling() + "");
                message.setFeeling(pos);
                Log.e("FEEL", message.getFeeling() + "");


                try {
                    Log.e("FEEL", senderRoom + "," + receiverRoom);
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(message.getMessageId()).child("feeling").setValue(message.getFeeling());



                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(message.getMessageId()).child("feeling").setValue(message.getFeeling());

                } catch (Exception e) {
                    Log.e("Cht,", e.getMessage());
                }


                return true; // true is closing popup, false is requesting a new selection
            });



        if(holder.getClass() == SentViewHolder.class)
        {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            try {

//                if(currentPosition == position){
//
//                    viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.select_drawable));
//                }else{viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));}




//                if(message.getMessage().equals("this message is deleted")) {
//                    viewHolder.binding.full.setClickable(false);
//                }

                if(message.getMessage().equals("photo"))
                {
                    viewHolder.binding.image.setVisibility(View.VISIBLE);
                    viewHolder.binding.message.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(message.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(viewHolder.binding.image);
                }
                viewHolder.binding.message.setText(message.getMessage());




                if(message.getFeeling() >= 0)
                {
//                    message.setFeeling(reactions[(int)message.getFeeling()]);
                    viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }else
                {
                    viewHolder.binding.feeling.setVisibility(View.GONE);
                }


            }
            catch (Exception e)
            {
                Log.e("Chat," , e.getMessage());
            }





            viewHolder.binding.full.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
    activityChatBinding.constraintLayout2.setVisibility(View.GONE);
    activityChatBinding.selecttoolbar.setVisibility(View.VISIBLE);




                    try {

//                        Log.e("get background",""+viewHolder.binding.full.getBackground());
//                        Log.e("get default",""+ContextCompat.getDrawable(context, R.drawable.defaultitem));
//                        Log.e("get select",""+  ContextCompat.getDrawable(context, R.drawable.select_drawable));

                        Log.e("selected before", selected +"");



                        if(!sentlist.contains(viewHolder) ){
                            Log.e("from long","true");
                            sentlist.add(viewHolder);
                            viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.select_drawable));

                        }else {
                            Log.e("from long","false");
                            sentlist.remove(viewHolder);
                            viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                        }

                        selected[0] =changevalue(selected[0]);
                        Log.e("selected after", selected +"");


                        if (selectedmessage.contains(message) ) {
        selectedmessage.remove(message);
     //   sentlist.remove(viewHolder);
        Log.e("remove msg", message.getMessage());
    } else {
        selectedmessage.add(message);
       // sentlist.add(viewHolder);
        Log.e("add msg", message.getMessage());
    }



}catch (Exception e){Log.e("in to long listenter",e.getMessage());}






                    activityChatBinding.selectback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            for (SentViewHolder sentViewHolder:sentlist) {
                                sentViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                            }
                                activityChatBinding.selecttoolbar.setVisibility(View.GONE);
                                activityChatBinding.constraintLayout2.setVisibility(View.VISIBLE);

                        selectedmessage.clear();
                            sentlist.clear();
                            recievelist.clear();
                        }




                    });




        activityChatBinding.chatactivitydeleteicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletemessage();
            }
        });


//                    currentPosition = position;
//                    notifyDataSetChanged();

                    return false;
                }

                private boolean changevalue(boolean selected) {
                    return !selected;
                }


            });



            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("MotionEventtrue",""+event);

                    popup.onTouch(v,event);
                    return true;
                }
            });

            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e("MotionEventfalse",""+event);

                    popup.onTouch(v,event);
                    return false;
                }
            });

        }else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            try {

//                if(message.getMessage().equals("this message is deleted")) {
//                    viewHolder.binding.full.setClickable(false);
//                }

//                if(currentPosition == position){
//
//                    viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.select_drawable));
//                }else{viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));}




                if(message.getMessage().equals("photo"))
                {
                    viewHolder.binding.image.setVisibility(View.VISIBLE);
                    viewHolder.binding.message.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(message.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .into(viewHolder.binding.image);
                }

                viewHolder.binding.message.setText(message.getMessage());




                if(message.getFeeling() >= 0)
                {
                   // message.setFeeling(reactions[(int)message.getFeeling()]);
                    viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }else
                {
                    viewHolder.binding.feeling.setVisibility(View.GONE);
                }

            }
            catch (Exception e)
            {
                Log.e("Chat," , e.getMessage());
            }



            viewHolder.binding.full.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activityChatBinding.constraintLayout2.setVisibility(View.GONE);
                    activityChatBinding.selecttoolbar.setVisibility(View.VISIBLE);


                    try {

//                        Log.e("get background",""+viewHolder.binding.full.getBackground());
//                        Log.e("get default",""+ContextCompat.getDrawable(context, R.drawable.defaultitem));
//                        Log.e("get select",""+  ContextCompat.getDrawable(context, R.drawable.select_drawable));

                        Log.e("selected before", selected[0] +"");



                        if(!recievelist.contains(viewHolder)){
                            Log.e("from long","true");
                            recievelist.add(viewHolder);
                            viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.select_drawable));

                        }else {
                            Log.e("from long","false");
                            recievelist.remove(viewHolder);
                            viewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));

                        }

                        selected[0] =changevalue(selected[0]);
                        Log.e("selected after", selected +"");


                        if (selectedmessage.contains(message)) {
                            selectedmessage.remove(message);

                            Log.e("remove msg", message.getMessage());
                        } else {
                            selectedmessage.add(message);
                             Log.e("add msg", message.getMessage());
                        }



                    }catch (Exception e){Log.e("in to long listenter",e.getMessage());}




                    activityChatBinding.selectback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            for (ReceiverViewHolder recieveViewHolder:recievelist ) {
                                recieveViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));

                            }

                            selectedmessage.clear();
                            sentlist.clear();
                            recievelist.clear();

                            activityChatBinding.selecttoolbar.setVisibility(View.GONE);
                            activityChatBinding.constraintLayout2.setVisibility(View.VISIBLE);

                        }
                    });
                    activityChatBinding.chatactivitydeleteicon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletemessage();
                        }
                    });

                    return false;
                }

                private boolean changevalue(boolean selected) {
                    return !selected;
                }
            });





            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v,event);
                    return false;
                }
            });

        }
        }catch (Exception e){Log.e("chatend :",e.getMessage());}
    }

    public void deletemessage() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder((ChatActivity) context);
            builder.setMessage("Delete selected message?");
            builder.setPositiveButton("delete for me", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Message m:selectedmessage) {
                        try{
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(m.getMessageId())
                                    .removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.e("from delete for me ","value removed");
                                        }
                                    });
                        }catch (Exception e){Log.e("from delete for me ",e.getMessage());}


                    }

                    for (SentViewHolder sentViewHolder:sentlist) {
                        sentViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                    }
                    for (ReceiverViewHolder recieveViewHolder:recievelist ) {
                        recieveViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));

                    }

                    ((ChatActivity) context).showmessagesinlist();
                    


                    selectedmessage.clear();
                    sentlist.clear();
                    recievelist.clear();
                    dialog.cancel();
                }
            });

            builder.setNegativeButton("delete for Everyone", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    for (Message m:selectedmessage) {
                        try{
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(m.getMessageId())
                                    .child("message")
                                    .setValue("this message is deleted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(m.getMessageId())
                                    .child("message")
                                    .setValue("this message is deleted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("from dialog box","delete for Everyone");

                                }
                            });

                        }catch (Exception e){Log.e("from delete for all ",e.getMessage());}


                    }

                    for (SentViewHolder sentViewHolder:sentlist) {
                        sentViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                    }
                    for (ReceiverViewHolder recieveViewHolder:recievelist ) {
                        recieveViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));

                    }


                    ((ChatActivity) context).showmessagesinlist();


                    selectedmessage.clear();
                    sentlist.clear();
                    recievelist.clear();
                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for (SentViewHolder sentViewHolder:sentlist) {
                                sentViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                            }
                            for (ReceiverViewHolder recieveViewHolder:recievelist ) {
                                recieveViewHolder.binding.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));

                            }



                            selectedmessage.clear();
                            sentlist.clear();
                            recievelist.clear();
                            dialog.cancel();

                        }
                    });


            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }catch (Exception e){Log.e("from delete",e.getMessage());}





        activityChatBinding.selecttoolbar.setVisibility(View.GONE);
        activityChatBinding.constraintLayout2.setVisibility(View.VISIBLE);
        Log.e("from delete","deleted");

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }



    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSendBinding binding;
    public SentViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemSendBinding.bind(itemView);
    }
}

public  class  ReceiverViewHolder extends RecyclerView.ViewHolder{

        ItemRecieveBinding binding;
    public ReceiverViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemRecieveBinding.bind(itemView);
    }
}
}