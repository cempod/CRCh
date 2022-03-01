package com.cempod.crch;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatMessagesManager {
    private String userID;
    private ArrayList<Message> messages;
    private RecyclerView recyclerView;
    private int count;


    public ChatMessagesManager(RecyclerView recyclerView, String userID, ArrayList<Message> messages){
        this.userID = userID;
        this.messages = messages;
        this.recyclerView = recyclerView;
        setRoom();
        Query mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom()).child("messages").limitToLast(100);
        mDatabase.addChildEventListener(messagesListener);
        count = 0;
    }

    ChildEventListener messagesListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Message message = snapshot.getValue(Message.class);
            messages.add(message);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
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

    public void setRoom(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    DatabaseReference mDatabase;
// ...
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom()).child("users");
                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                    mDatabase.child(userID).setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);
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

    public void startInputListening(){
        DatabaseReference inReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID);
        inReference.addChildEventListener(inputChildListener);
    }
    public void stopInputListening(){
        DatabaseReference inReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID);
        inReference.removeEventListener(inputChildListener);
    }

    public  void startOutputListening(){
        DatabaseReference outReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        outReference.addChildEventListener(outChildListener);
    }


    public  void stopOutputListening(){
        DatabaseReference outReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        outReference.removeEventListener(outChildListener);
    }





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

    ChildEventListener outChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().toString().equals("messages")) {
                count = Integer.parseInt(snapshot.getValue().toString());
            }

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().toString().equals("messages")) {
                count = Integer.parseInt(snapshot.getValue().toString());
            }

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


}
