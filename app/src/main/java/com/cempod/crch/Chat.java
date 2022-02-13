package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
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

public class Chat extends AppCompatActivity {
    FloatingActionButton sendButton;
    int count;
    TextInputEditText messageTextEdit;
    RecyclerView messageRecycler;
ArrayList<Message> messages = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    String userID;
    ChatAdapter adapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NotificationManager notificationManager;
    MaterialToolbar chatAppBar;
    SimpleDateFormat onlineDateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        chatAppBar = findViewById(R.id.chatAppBar);
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
setOnline();


        String name = intent.getStringExtra("Name");
        userID = intent.getStringExtra("Id");
        editor = sharedPreferences.edit();
        editor.putString("openedChatId",userID);
        editor.commit();
       // getSupportActionBar().setTitle(name);
        chatAppBar.setTitle(name);
       // chatAppBar.setSubtitle("Онлайн");
        int notificationId = sharedPreferences.getInt(userID,-1);
        if(notificationId != -1){
            notificationManager.cancel(notificationId);
        }
        setChat();
        messageRecycler = findViewById(R.id.messageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatAdapter(messages,FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(adapter);
        linearLayoutManager.setStackFromEnd(true);
        sendButton = findViewById(R.id.sendButton);
        messageTextEdit = findViewById(R.id.messageTextEdit);
        count = 0;
        DatabaseReference outReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        outReference.addChildEventListener(outChildListener);

        DatabaseReference inReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID);

        inReference.addChildEventListener(inputChildListener);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    DatabaseReference mDatabase;
// ...
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom()).child("users");
                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                    mDatabase.child(userID).setValue(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(messageTextEdit.getText().toString().trim(), dateFormat.format(new Date()).toString(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference mDatabase;
// ...
                count = count+1;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").setValue(count);
                mDatabase.child("rooms").child(getRoom()).child("messages").push().setValue(message);

           messageTextEdit.setText("");
            }

        });






        Query mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom()).child("messages").limitToLast(100);
        mDatabase.addChildEventListener(childEventListener);
        DatabaseReference onlineReference = FirebaseDatabase.getInstance().getReference();
        onlineReference.child("users").child(userID).child("online").addValueEventListener(onlineValueListener);
    }

    private void setChat() {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    ChildEventListener outChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            count = Integer.parseInt(snapshot.getValue().toString());
            //   Toast.makeText(getApplicationContext(), "added"+count,
            //           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            count = Integer.parseInt(snapshot.getValue().toString());
            //  Toast.makeText(getApplicationContext(), "changed"+count,
            //          Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    };

    ChildEventListener inputChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).child("messages").setValue(0);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).child("messages").setValue(0);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Message message = snapshot.getValue(Message.class);
            messages.add(message);
            messageRecycler.getAdapter().notifyDataSetChanged();
            messageRecycler.smoothScrollToPosition(adapter.getItemCount());
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

ValueEventListener onlineValueListener = new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
       if(snapshot.getValue()!=null) {
           if (snapshot.getValue().toString().equals("true")) {
               chatAppBar.setSubtitle("Онлайн");
           } else {
               Long millis = Long.parseLong(snapshot.getValue().toString());
Date date = new Date(millis);

               chatAppBar.setSubtitle("Был(а) в сети "+onlineDateFormat.format(date) );
           }
       }else chatAppBar.setSubtitle("");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        chatAppBar.setSubtitle("");
    }
};



    public String getRoom() {
        String s1 = userID;
        String s2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int compare = s1.compareTo(s2);
    if(compare<0){
return s1+s2;
    }else{
       return s2+s1;
    }

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
       /* DatabaseReference inReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID);

        inReference.removeEventListener(inputChildListener);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("rooms").child(getRoom()).child("messages").removeEventListener(childEventListener);
        DatabaseReference outReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        outReference.removeEventListener(outChildListener);
       Toast.makeText(getApplicationContext(), "paused",
                       Toast.LENGTH_SHORT).show();*/

        super.onPause();
    }

    @Override
    protected void onPostResume() {
        editor.putString("openedChatId",userID);
        editor.commit();
        setOnline();
        int notificationId = sharedPreferences.getInt(userID,-1);
        if(notificationId != -1){
            notificationManager.cancel(notificationId);
        }
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        DatabaseReference inReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID);
        editor.putString("openedChatId","");
        editor.commit();
        inReference.removeEventListener(inputChildListener);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("rooms").child(getRoom()).child("messages").removeEventListener(childEventListener);
        DatabaseReference outReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        outReference.removeEventListener(outChildListener);
       // Toast.makeText(getApplicationContext(), "closed",
       //                 Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}