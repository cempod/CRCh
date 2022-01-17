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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {
    ImageButton sendButton;
    TextInputEditText messageTextEdit;
    RecyclerView messageRecycler;
ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Name");
        String userID = intent.getStringExtra("Id");
        getSupportActionBar().setTitle(name);
        messageRecycler = findViewById(R.id.messageRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        ChatAdapter adapter = new ChatAdapter(messages,FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(adapter);
        sendButton = findViewById(R.id.sendButton);
        messageTextEdit = findViewById(R.id.messageTextEdit);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(messageTextEdit.getText().toString(), dateFormat.format(new Date()).toString(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference mDatabase;
// ...
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(message);
           messageTextEdit.setText("");
            }

        });

// ...
        //mDatabase = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messages.add(message);
                messageRecycler.getAdapter().notifyDataSetChanged();
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
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).addChildEventListener(childEventListener);
    }
}