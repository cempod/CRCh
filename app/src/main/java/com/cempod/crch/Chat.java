package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class Chat extends AppCompatActivity {
    FloatingActionButton sendButton;
    int count;
    Intent intent;
    MotionLayout chatLayout;
    TextView typingText;
    TextInputEditText messageTextEdit;
    RecyclerView messageRecycler;
ArrayList<Message> messages = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    String userID;
    ChatAdapter adapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NotificationManager notificationManager ;
    TextView chatUsernameText, chatOnlineText;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    User user;
    String name;
    ImageView avatar;
    CircularProgressIndicator connectionChatIndicator;
    UserIconsManager iconsManager = new UserIconsManager();
    ChatMessagesManager messagesManager;
    UserInfoManager infoManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        name = intent.getStringExtra("Name");
        userID = intent.getStringExtra("Id");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
        int notificationId = sharedPreferences.getInt(userID,-1);
        if(notificationId != -1){

            try{
                notificationManager.cancel(notificationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        editor = sharedPreferences.edit();
        editor.putString("openedChatId",userID);
        editor.commit();
        postponeEnterTransition();
        SharedPreferences sPreferences = getSharedPreferences("settings",MODE_PRIVATE);

        String nightMode = sPreferences.getString("night_mode","auto");
        if(nightMode.equals("true")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(nightMode.equals("false")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(nightMode.equals("auto")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_chat);
        findIDS();
        typingText.setText(name+" набирает сообщение");
        chatUsernameText.setTransitionName("username"+intent.getStringExtra("Position"));
        adapter = new ChatAdapter(messages,FirebaseAuth.getInstance().getCurrentUser().getUid());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setAdapter(adapter);
        messageRecycler.setLayoutManager(linearLayoutManager);
        chatUsernameText.setText(name);
        avatar.setTransitionName("avatar"+intent.getStringExtra("Position"));
        avatar.setTag(intent.getStringExtra("Position"));
        Bitmap bitmap = (Bitmap) (intent.getParcelableExtra("avatar"));
        avatar.setImageBitmap(bitmap);
        if(intent.getStringExtra("from")!=null){
        getWindow().getSharedElementEnterTransition().addListener(openChatAnimation);
        }else{
            mainFunction();
        }

        startPostponedEnterTransition();
  }

 Transition.TransitionListener openChatAnimation = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {




        }

        @Override
        public void onTransitionEnd(Transition transition) {
            if(messages.size()==0){
          mainFunction();
            }else {

            }
        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    };

    private void mainFunction() {
        setChat();
        setOnline();
        messagesManager = new ChatMessagesManager(messageRecycler,userID,messages);
        messagesManager.startInputListening();
        messagesManager.startInputListening();
        infoManager = new UserInfoManager();
        infoManager.setUserListener(userID,chatUsernameText,chatOnlineText,avatar,connectionChatIndicator);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message = new Message(messageTextEdit.getText().toString().trim(), dateFormat.format(new Date()).toString(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference mDatabase;
// ...
                count = count+1;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").setValue(count);
                mDatabase.child("rooms").child(messagesManager.getRoom()).child("messages").push().setValue(message);
                mDatabase.child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing").setValue("false");
                messageTextEdit.setText("");
            }

        });
        avatar.setTransitionName("avatar");
        chatUsernameText.setTransitionName("username");

        messageTextEdit.addTextChangedListener(new TextWatcher() {
            DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            boolean isTyping = false;
            class TypeTimer extends AsyncTask<Void, Void, Void> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    isTyping = true;
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        TimeUnit.SECONDS.sleep(5);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    databaseReference.child("typing").setValue("false");

                    isTyping = false;
                }
            }

            TypeTimer typeTimer;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isTyping){
                    databaseReference.child("typing").setValue(ServerValue.TIMESTAMP);
                    typeTimer = new TypeTimer();
                    typeTimer.execute();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isTyping) {
                    databaseReference.child("typing").setValue("false");
                }
            }
        });

        DatabaseReference typingReference = FirebaseDatabase.getInstance().getReference();
        typingReference.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).child("typing").addValueEventListener(typingValueListener);



    }

    ValueEventListener typingValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                if(snapshot.getValue().toString().equals("false")){
                    chatLayout.transitionToStart();
                }else{
                    chatLayout.transitionToEnd();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    public void findIDS(){
        typingText=findViewById(R.id.typingText);
        chatUsernameText = findViewById(R.id.chatUsernameText);
        messageRecycler = findViewById(R.id.messageRecycler);
        chatLayout = findViewById(R.id.chatLayout);
        avatar = findViewById(R.id.userChatAvatar);
        messageTextEdit = findViewById(R.id.messageTextEdit);
        sendButton = findViewById(R.id.sendButton);
        connectionChatIndicator = findViewById(R.id.connectionChatIndicator);
        chatOnlineText = findViewById(R.id.chatOnlineText);

    }


    private void setChat() {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
    databaseReference.child(userID).child("id").setValue(userID);
    }

    public void setOnline(){
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online");
// Write a string when this client loses connection
        presenceRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
        presenceRef.setValue("true");
    }





    @Override
    protected void onPause() {
        editor.putString("openedChatId","");
        editor.commit();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child("typing").setValue("false");
        if(messagesManager!=null) {
            messagesManager.stopInputListening();
            messagesManager.stopInputListening();
        }
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        editor.putString("openedChatId",userID);
        editor.commit();
        setOnline();
        int notificationId = sharedPreferences.getInt(userID,-1);
        if(notificationId != -1){

            try{
                notificationManager.cancel(notificationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(messagesManager!=null) {
            messagesManager.startInputListening();
            messagesManager.startInputListening();
        }
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        editor.putString("openedChatId","");
        editor.commit();
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.child("typing").setValue("false");
        super.onDestroy();
    }
}