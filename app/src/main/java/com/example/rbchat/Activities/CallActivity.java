package com.example.rbchat.Activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rbchat.Model.User;
import com.example.rbchat.R;
import com.example.rbchat.databinding.ActivityCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.Date;
import java.util.List;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding;
    User recipient;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private String callerid;
    private Call call;
    SinchClient sinchClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding=ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  recipientid=getIntent().getStringExtra("UId");
        recipient=(User)getIntent().getSerializableExtra("user");

      try {
          callerid = FirebaseAuth.getInstance().getUid();

          Log.e("texts",callerid);

          binding.name.setText(recipient.getuName());


         sinchClient = Sinch.getSinchClientBuilder()
                  .context(getApplicationContext())
                  .userId(callerid)
                  .applicationKey(R.string.APP_KEY+"")
                  .applicationSecret(R.string.APP_SECRET + "")
                  .environmentHost(R.string.ENVIRONMENT + "")
                  .build();


          sinchClient.setSupportCalling(true);
          sinchClient.startListeningOnActiveConnection();

          sinchClient.setSupportActiveConnectionInBackground(true);


          sinchClient.addSinchClientListener(new SinchClientListener() {
              @Override
              public void onClientStarted(SinchClient sinchClient) {
                  Log.e("chaat stoped",sinchClient.getLocalUserId());
              }

              @Override
              public void onClientStopped(SinchClient sinchClient) {
                  Log.e("chaat stoped",sinchClient.getLocalUserId());
              }

              @Override
              public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
Log.e("chaat",sinchError.getMessage());
              }

              @Override
              public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

              }

              @Override
              public void onLogMessage(int i, String s, String s1) {

              }
          });

          sinchClient.start();


        //  sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


          if (call == null) {
              call = sinchClient.getCallClient().callUser(recipient.getUid());
              call.addCallListener(new SinchCallListener());
              // button.setText("Hang Up");
          } else {
              call.hangup();
          }
      }catch (Exception e){
          Log.e("chaaat",e.getMessage());
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


    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();

            binding.status.setText("Call Ended");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            binding.status.setText("Connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
          binding.status.setText("Ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Log.e("callll","incoming call");
            Toast.makeText(getApplicationContext(), "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new SinchCallListener());
           // button.setText("Hang Up");
        }


    }
}
