package com.example.rbchat.Activities;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.rbchat.Adapters.ListMessageAdapter;
import com.example.rbchat.Adapters.MessageAdapter;
import com.example.rbchat.Model.Message;
import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.nio.file.Paths.get;
///1622978604000 14 june 2021
public class ChatActivity extends AppCompatActivity {

   public ActivityChatBinding binding;
    MessageAdapter adapter;
    ListMessageAdapter listadapter,list2adapter;
    ArrayList<Message> messages;
    String senderRoom , receiverRoom;
    Message message;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;

    ProgressDialog dialog;

    String senderUid;
    String receiverUid;
    Uri Image_uri;

    private static final int Permission_code=1001;
     private static final int  Request_code=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        User user=(User) getIntent().getSerializableExtra("user");
        String name =  user.getuName();
        receiverUid = user.getUid();

        senderUid = FirebaseAuth.getInstance().getUid();

//  ViewCompat.setNestedScrollingEnabled(binding.listview, false);





        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           if(snapshot.exists())
           {
               String status = snapshot.getValue(String.class);
               if(!status.isEmpty()) {
                   if (status.equals("Online"))
                   {

                       binding.chatdetailactivestatus.setText(status);
                   }
                   else{

                       String mytime;

                       try {
                           mytime = java.text.DateFormat.getTimeInstance().format(Long.parseLong(status));
                       }
                       catch (Exception E )
                       {
                           mytime = "";
                       }

                       binding.chatdetailactivestatus.setText("Last Seen : "+mytime);
//                       binding.chatdetailactivestatus.setVisibility(View.VISIBLE);
                   }
               }
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid ;

        messages = new ArrayList<>();

      ///// list view changed

/*
        adapter = new MessageAdapter(this, messages, senderRoom, receiverRoom,binding);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        */
////////////////////////////////////////////

        listadapter = new ListMessageAdapter(this, messages, senderRoom, receiverRoom,binding);
        binding.listview.setAdapter(listadapter);

       list2adapter = new ListMessageAdapter(this, messages, senderRoom, receiverRoom, binding);
//        binding.listview2.setAdapter(list2adapter);




        auth = FirebaseAuth.getInstance();

        Glide.with(getApplicationContext()).load(user.getuProfileImage())
                .placeholder(R.drawable.ic_avatar)
                .into(binding.profileimage);



        binding.chatdetailname.setText(name);

        binding.chatdetailback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        showmessagesinlist();



    binding.listview.post(new Runnable() {
        @Override
        public void run() {
      binding.listview.smoothScrollToPosition(listadapter.getCount()-1);
        }
    });


      binding.selectback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                binding.selecttoolbar.setVisibility(View.GONE);
                binding.constraintLayout2.setVisibility(View.VISIBLE);


              listadapter.selectedmessage.clear();
                listadapter.selectedpositions.clear();


                binding.listview.invalidateViews();
                binding.listview.invalidate();

            }




        });


binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {








    }
});

        binding.chatactivitydeleteicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listadapter.deletemessage();
            }
        });


//        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//
//                int reactions[] = new int[]{
//                        R.drawable.ic_like,
//                        R.drawable.ic_loved,
//                        R.drawable.ic_heart,
//                        R.drawable.ic_sad,
//                        R.drawable.ic_angry,
//                };
//
//
//           //     binding.listview.invalidateViews();
//                ImageView e1,e2,e3,e4,e5;
//                // binding.listview.invalidate();
//                RelativeLayout feelinglayout =(RelativeLayout) view.findViewById(R.id.feelinglayout);
//                ImageView feelingimage= (ImageView)view.findViewById(R.id.feeling);
//                e1=(ImageView) view.findViewById(R.id.emoji1);
//                e2=(ImageView) view.findViewById(R.id.emoji2);
//                e3=(ImageView) view.findViewById(R.id.emoji3);
//                e4=(ImageView) view.findViewById(R.id.emoji4);
//                e5=(ImageView) view.findViewById(R.id.emoji5);
//                final int[] pos = {0};
//
//                binding.dummyimage.bringToFront();
//
//             //   feelinglayout.bringToFront();
//
//
//
//
//
//
//                if (feelinglayout.getVisibility()!=View.VISIBLE){
//                    Log.e("from feeling","true");
//                    showfeelinglayout(feelinglayout);
//                   binding.dummyimage.setVisibility(View.VISIBLE);
//
//                }else{
//                    Log.e("from feeling","true");
//                    hidefeelinglayout(feelinglayout);
//                }
//
//               // feelinglayout.requestFocusFromTouch();
//
//   //            ((ConstraintLayout)view).bringToFront();
//              //  binding.listview.bringToFront();
//              feelinglayout.bringToFront();
//
//
////                ((ConstraintLayout)view).invalidate();
//
//                      // binding.listview.invalidate();
//
////                float temp = view.getZ();
////                view.setZ(binding.dummyimage.getZ());
////binding.dummyimage.setZ(temp);
//
//
//
//
//
//                e1.setOnClickListener(v->{
//                    pos[0] =0;
//                    setimage(view,feelingimage,reactions[pos[0]]);
//
//                });
//                e2.setOnClickListener(v->{
//                    pos[0] =1;
//                    Log.e("helloworld","ssssxxs");
//                    setimage(view,feelingimage,reactions[pos[0]]);
//
//                });
//                e3.setOnClickListener(v->{
//                    pos[0] =2;
//                    setimage(view,feelingimage,reactions[pos[0]]);
//
//                });
//                e4.setOnClickListener(v->{
//                    pos[0] =3;
//                    setimage(view,feelingimage,reactions[pos[0]]);
//
//                });
//                e5.setOnClickListener(v->{
//                    pos[0] =4;
//                    setimage(view,feelingimage,reactions[pos[0]]);
//
//                });
//
//
//
//                binding.dummyimage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(feelinglayout.getVisibility()==View.VISIBLE){
//                            hidefeelinglayout(feelinglayout);
//                            binding.dummyimage.setVisibility(View.GONE);
//                        }
//
//
//
//
//                    }
//                });
//
//
//
//
//
//
//            }
//
//            private void setimage(View v,ImageView feelingimage, int reaction) {
//                feelingimage.setImageResource(reaction);
//                feelingimage.setVisibility(View.VISIBLE);
//
//
//
//             //   binding.listview.invalidateViews();
//
//            }
//        });



        binding.listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        binding.constraintLayout2.setVisibility(View.GONE);
        binding.selecttoolbar.setVisibility(View.VISIBLE);

Message message= (Message) listadapter.getItem(position);

        Log.e("long pressed msg", message.getMessage());



        if(listadapter.selectedpositions.contains(position)){
            listadapter.selectedpositions.remove(Integer.valueOf(position));
            view.setBackground(getDrawable(R.drawable.defaultitem));

        }else{
            listadapter.selectedpositions.add(position);
            view.setBackground(getDrawable(R.drawable.select_drawable));
        }



        if (listadapter.selectedmessage.contains(message) ) {
            listadapter.selectedmessage.remove(message);

            if(listadapter.selectedmessage.isEmpty()){  binding.selecttoolbar.setVisibility(View.GONE);
                binding.constraintLayout2.setVisibility(View.VISIBLE);}


            //   sentlist.remove(viewHolder);
            Log.e("remove msg", message.getMessage());
        } else {
            listadapter.selectedmessage.add(message);
            // sentlist.add(viewHolder);
            Log.e("add msg", message.getMessage());
        }





        Log.e("from item long click","hello "+position+" view : "+view);


        return true;
    }
});




        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);



                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                message.setStatus("sent");
                binding.messageBox.setText("");


                String randomKey = database.getReference().push().getKey();

                message.setMessageId(randomKey);

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMessage", message.getMessage());
                lastMsgObj.put("lastMessageTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {



}
                        });
                    }
                });


                binding.listview.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.listview.smoothScrollToPosition(listadapter.getCount()-1);
                    }
                });

            }
        });



        binding.attachment.setOnClickListener(v -> {




            if (binding.relative.getVisibility()==View.INVISIBLE)
                showlayout();
            else
                hidelayout();



        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().hide();
            binding.Document.setOnClickListener(v -> {

                    getmediafrommobile();
});
            binding.gallery.setOnClickListener(v -> {

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] Permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestPermissions(Permission, Permission_code);
                        } else {
                            PickImeageFromGallary();
                        }
                    }
});
        binding.camera.setOnClickListener(v -> {

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    String[] Permission = {Manifest.permission.CAMERA};
                    requestPermissions(Permission, Permission_code);
                } else {
                    openCamera();
                }

            }
        });






        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(UserStoppedTyping,1000);
            }

            Runnable UserStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

  //      getSupportActionBar().setTitle(name);
  //      getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showmessagesinlist() {


//        HashMap<String, Object> statusObj = new HashMap<>();
//        statusObj.put("status", "seen");

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();


                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            message = snapshot1.getValue(Message.class);

                            try{

                                if(message.getStatus()!=null){

                                    Log.e("seen","not null");

                                    if(!message.getStatus().equals("seen") && !message.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
                                Log.e("seen",message.getStatus());
                                message.setStatus("seen");


                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(message.getMessageId()).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("seen",message.getStatus());
                                    }
                                });

                            }
                                }

                        }catch (Exception e){Log.e("seen",e.getMessage());}

                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }






                     //   adapter.notifyDataSetChanged();
                   //     binding.recyclerView.scrollToPosition(adapter.getItemCount()-1);


                        listadapter.notifyDataSetChanged();
                        list2adapter.notifyDataSetChanged();




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     switch (requestCode){
         case Permission_code:{
             if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 openCamera();
             }
             else {
                 Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show();
             }
         }
     }

    }

    private void openCamera() {
   //     ContentValues values=new ContentValues();
   //     values.put(MediaStore.Images.Media.TITLE,"New Picture");
   //     values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
   //     Image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // intent.setAction(Intent.ACTION_PICK);
   //     intent.putExtra(MediaStore.EXTRA_OUTPUT,Image_uri);
        startActivityForResult(intent, 2);

    }



    private void PickImeageFromGallary() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,Request_code);

    }

    public void getmediafrommobile() {
        Intent intent=new Intent();
        intent.setType("pdf/*");
        intent.setType("doc/*");
        intent.setType("excel/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Request_code);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Request_code) {
            if (data != null) {


          //binding.iamges.se(fianlphoto);
                if (data.getData() != null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();
                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        message.setMessage("photo");
                                        message.setStatus("sent");
                                        message.setImageUrl(filepath);
                                        binding.messageBox.setText("");

                                        String randomKey = database.getReference().push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMessage", message.getMessage());
                                        lastMsgObj.put("lastMessageTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .child(randomKey)
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }else{Log.e("chatactivity","data not found");}
            }
            else{Log.e("chatactivity","data not found");}
        }
        else if (requestCode==2){
            try {
                Bitmap finalphoto = (Bitmap) data.getExtras().get("data");
                Uri uri= getImageUri(getApplicationContext(),finalphoto);

            Log.e("abc",uri.toString());
            Uri selectedImage = uri;
            Calendar calendar = Calendar.getInstance();
            StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
            dialog.show();
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String filepath = uri.toString();

                                String messageTxt = binding.messageBox.getText().toString();
                                Date date = new Date();
                                Message message = new Message(messageTxt, senderUid, date.getTime());
                                message.setMessage("photo");
                                message.setStatus("sent");
                                message.setImageUrl(filepath);
                                binding.messageBox.setText("");

                                String randomKey = database.getReference().push().getKey();

                                HashMap<String, Object> lastMsgObj = new HashMap<>();
                                lastMsgObj.put("lastMessage", message.getMessage());
                                lastMsgObj.put("lastMessageTime", date.getTime());

                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        database.getReference().child("chats")
                                                .child(receiverRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });


            }
            catch(Exception e){
                Log.e("ChatActivity",e.getMessage());
            }

        }







    }





    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            database.getReference().child("presence").child(currentId).setValue("Online");
        }
        catch(Exception e) {
            Log.e(this.getClass().getSimpleName()+" OnResume: ",e.getMessage());
        }
    }






    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        try {
            Date date=new Date();
            database.getReference().child("presence").child(currentId).setValue(date.getTime()+"");
        }
        catch(Exception e) {
            Log.e(this.getClass().getSimpleName()+" OnPause: ",e.getMessage());
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void showlayout() {
        RelativeLayout view= binding.relative;
        float radius =Math.max(view.getWidth(),view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view,view.getLeft(),view.getTop(),0,radius*2);
        animator.setDuration(800);
        view.setVisibility(view.VISIBLE);
        animator.start();
    }
    private void hidelayout() {
        RelativeLayout view= binding.relative;
        float initialradius =Math.max(view.getWidth(),view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view,view.getLeft(),view.getTop(),initialradius * 2,0);
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(view.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void showfeelinglayout( RelativeLayout view) {

        float radius =Math.max(view.getWidth(),view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view,view.getLeft(),view.getTop(),0,radius*2);
        animator.setDuration(800);
        view.setVisibility(view.VISIBLE);
        animator.start();
    }
    private void hidefeelinglayout(RelativeLayout view) {

        try{
        float initialradius =Math.max(view.getWidth(),view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view,view.getLeft(),view.getTop(),initialradius * 2,0);
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(view.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }catch (Exception e){Log.e("Animation",e.getMessage());}


    }




    @Override
    public void onBackPressed() {

        if (binding.relative.getVisibility()==View.VISIBLE)
            hidelayout();
        else{
            finish();
            super.onBackPressed();}
    }

}