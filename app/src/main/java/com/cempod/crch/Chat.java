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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class Chat extends AppCompatActivity {
    ImageButton sendButton;
    TextInputEditText messageTextEdit;
    RecyclerView messageRecycler;
ArrayList<Message> messages = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        userID = intent.getStringExtra("Id");
        getSupportActionBar().setTitle(name);
        messageRecycler = findViewById(R.id.messageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        ChatAdapter adapter = new ChatAdapter(messages,FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(adapter);
        linearLayoutManager.setStackFromEnd(true);
        sendButton = findViewById(R.id.sendButton);
        messageTextEdit = findViewById(R.id.messageTextEdit);

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
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("rooms").child(getRoom()).child("messages").push().setValue(message);
           messageTextEdit.setText("");
            }

        });



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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("rooms").child(getRoom()).child("messages").addChildEventListener(childEventListener);
    }

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

}