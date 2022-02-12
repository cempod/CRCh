package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class Chat extends AppCompatActivity {
    ImageButton sendButton;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);

        String name = intent.getStringExtra("Name");
        userID = intent.getStringExtra("Id");
        getSupportActionBar().setTitle(name);
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
                Message message = new Message(messageTextEdit.getText().toString(), dateFormat.format(new Date()).toString(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference mDatabase;
// ...
                count = count+1;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").setValue(count);
                mDatabase.child("rooms").child(getRoom()).child("messages").push().setValue(message);

           messageTextEdit.setText("");
            }

        });






        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("rooms").child(getRoom()).child("messages").addChildEventListener(childEventListener);
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