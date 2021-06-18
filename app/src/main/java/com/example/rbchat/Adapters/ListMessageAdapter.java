package com.example.rbchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.rbchat.Activities.ChatActivity;
import com.example.rbchat.Model.Message;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ActivityChatBinding;
import com.example.rbchat.databinding.ItemRecieveBinding;
import com.example.rbchat.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListMessageAdapter extends BaseAdapter {


    Context context;
    ArrayList<Message> messages;
    ArrayList<ViewHolder> sentlist=new ArrayList<>();

    public ArrayList<Message> selectedmessage=new ArrayList<>();
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    final int ITEM_DETAIL= 3;
    ActivityChatBinding activityChatBinding;
    String senderRoom;
    String receiverRoom;

    ViewHolder viewHolder;
    private int currentPosition;
    public ArrayList<View> views;
    public ArrayList<Integer> selectedpositions;
    public ReactionPopup popup;
    public boolean felingflag=false;


    public ListMessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom, ActivityChatBinding binding)
    {
       // setHasStableIds(true);

        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
        activityChatBinding=binding;
        views=new ArrayList<>();
        selectedpositions=new ArrayList<>();
    }



    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        if(type == ITEM_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return view;
        }else if(type == ITEM_RECEIVE){
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent, false);
            return view;
        }else if(type==ITEM_DETAIL){
            View view = LayoutInflater.from(context).inflate(R.layout.item_detail,parent, false);
            return view;

        }
        else
            return null;
    }



    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
       return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }







    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if("dummy".equals(message.getSenderId())){
            return ITEM_DETAIL;
        }
       else if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId()))
        {
            return ITEM_SENT;

        }else{
            return ITEM_RECEIVE;
        }

    }


    public class ViewHolder{


        boolean selected=false;
        ConstraintLayout full;
        ImageView image,feeling,msgstatus;
        TextView msgTime,message;
        RelativeLayout feelinglayout;
        ConstraintLayout linearlayout3;



        public ViewHolder(View view) {
full=(ConstraintLayout) view.findViewById(R.id.full);
image=(ImageView) view.findViewById(R.id.image);
feeling=(ImageView) view.findViewById(R.id.feeling);
msgTime=(TextView) view.findViewById(R.id.msgTime);
message=(TextView)view.findViewById(R.id.message);
linearlayout3=(ConstraintLayout)view.findViewById(R.id.linearLayout3);
//feelinglayout=( RelativeLayout ) view.findViewById(R.id.feelinglayout);
msgstatus=(ImageView) view.findViewById(R.id.msgStatus);

        }
    }








    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);
        final boolean[] selected = {true};

        View row=convertView;
        viewHolder=null;




//if(row==null) {

    row = getInflatedLayoutForType(getItemViewType(position), parent);




        viewHolder = new ViewHolder(row);

    row.setTag(viewHolder);

//}else{viewHolder=(ViewHolder)row.getTag();}

//viewHolder.feelinglayout.setVisibility(View.INVISIBLE);

        if (selectedpositions.contains(position)){
            Log.e("from views if","true");
            row.setBackground(context.getDrawable(R.drawable.select_drawable));}else{
            Log.e("from views if",row.toString());
            row.setBackground(context.getDrawable(R.drawable.defaultitem));

        }





                try {


                    String mytime;

                  //  holder.binding.lstMsg.setText(lastMsg);
                    try {
                        mytime = java.text.DateFormat.getTimeInstance().format(message.getTimestamp());
                    }
                    catch (Exception E )
                    {
                        mytime = "";
                    }
                  viewHolder.msgTime.setText(mytime);

                    int reactions[] = new int[]{
                            R.drawable.ic_like,
                            R.drawable.ic_loved,
                            R.drawable.ic_heart,
                            R.drawable.ic_sad,
                            R.drawable.ic_angry,
                    };



                    if(message.getMessage().equals("photo"))
                    {
                        viewHolder.image.setVisibility(View.VISIBLE);
                        viewHolder.message.setVisibility(View.GONE);
                        Glide.with(context)
                                .load(message.getImageUrl())
                                .placeholder(R.drawable.placeholder)
                                .into(viewHolder.image);
                    }else
                    viewHolder.message.setText(message.getMessage());

try {
    if (message.getStatus().equals("seen")) {
        viewHolder.msgstatus.setImageResource(R.drawable.ic_double_check);

    }
}catch (Exception e){e.printStackTrace();}



                    if(message.getFeeling() >= 0)
                    {
//         message.setFeeling(reactions[(int)message.getFeeling()]);
                        viewHolder.feeling.setImageResource(reactions[message.getFeeling()]);
                        viewHolder.feeling.setVisibility(View.VISIBLE);
                    }else
                    {
                        viewHolder.feeling.setVisibility(View.GONE);
                    }






                    View rowfortouchevent=row;



                viewHolder.linearlayout3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {


                        Message m = (Message) getItem(position);


                        ReactionsConfig config = new ReactionsConfigBuilder(context)
                                .withReactions(reactions)
                                .build();
                        try {
                            popup = new ReactionPopup(context, config, (pos) -> {

                                if(pos != -1) {

                                    viewHolder.feeling.setImageResource(reactions[pos]);
                                    viewHolder.feeling.setVisibility(View.VISIBLE);

                                }
                                Log.e("FEEL", m.getFeeling() + "");
                                m.setFeeling(pos);
                                Log.e("FEEL", m.getFeeling() + "");


                                try {
                                    Log.e("FEEL", senderRoom + "," + receiverRoom);
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(senderRoom)
                                            .child("messages")
                                            .child(m.getMessageId()).child("feeling").setValue(m.getFeeling());



                                    FirebaseDatabase.getInstance().getReference()
                                            .child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .child(m.getMessageId()).child("feeling").setValue(m.getFeeling());

                                } catch (Exception e) {
                                    Log.e("Cht,", e.getMessage());
                                }


                                return true; // true is closing popup, false is requesting a new selection
                            });







                        }
                        catch (Exception e)
                        {
                            Log.e("Chat," , e.getLocalizedMessage()+","+e.getMessage());
                        }







                        Log.e("MotionEventtrue",""+position);


                        popup.onTouch(v,event);
                        return false;
                    }
                });

//
//                viewHolder.message.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        Log.e("MotionEventtrue",""+event);
//
//                        popup.onTouch(v,event);
//                        return false;
//                    }
//                });
//
//                viewHolder.image.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        Log.e("MotionEventfalse",""+event);
//
//                        popup.onTouch(v,event);
//                        return false;
//                    }
//                });
//

        }catch (Exception e){Log.e("chatend :",e.getMessage());}


        return row;
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

                    for (ViewHolder sentViewHolder:sentlist) {
                        sentViewHolder.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                    }



                    ((ChatActivity) context).showmessagesinlist();

                    selectedpositions.clear();
                    selectedmessage.clear();
                  //  sentlist.clear();

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
                                    .removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.e("from delete for me ","value removed");
                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(m.getMessageId())
                                    .removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.e("from delete for me ","value removed");
                                        }
                                    });

                        }catch (Exception e){Log.e("from delete for all ",e.getMessage());}


                    }

                    for (ViewHolder sentViewHolder:sentlist) {
                        sentViewHolder.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                    }


                    ((ChatActivity) context).showmessagesinlist();

                    selectedpositions.clear();

                    selectedmessage.clear();
//                    sentlist.clear();

                    dialog.cancel();
                }
            });
            builder.setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for (ViewHolder sentViewHolder:sentlist) {
                                sentViewHolder.full.setBackground(ContextCompat.getDrawable(context, R.drawable.defaultitem));
                            }

try {




    selectedpositions.clear();

    selectedmessage.clear();

    activityChatBinding.listview.invalidateViews();


    sentlist.clear();
}catch(Exception e){Log.e("from cancel : ",e.getMessage());}
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



   public RelativeLayout getfeelinglayout(){


        return viewHolder.feelinglayout;
        }






}
