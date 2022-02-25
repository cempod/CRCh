package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    NotificationManager notificationManager;
    TextView chatUsernameText, chatOnlineText;
    SimpleDateFormat onlineDateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");
    User user;
    ImageView avatar;
    CircularProgressIndicator connectionChatIndicator;
    UserIconsManager iconsManager = new UserIconsManager();
    ConstraintLayout chatAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Intent intent = getIntent();
        sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("openedChatId",userID);
        editor.commit();
        String name = intent.getStringExtra("Name");
        userID = intent.getStringExtra("Id");
        setContentView(R.layout.activity_chat);
        chatUsernameText = findViewById(R.id.chatUsernameText);
        messageRecycler = findViewById(R.id.messageRecycler);

        chatLayout = findViewById(R.id.chatLayout);
        chatUsernameText.setText(name);
        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {

                avatar = findViewById(R.id.userChatAvatar);
                Bitmap bitmap = (Bitmap) (intent.getParcelableExtra("avatar"));
                avatar.setImageBitmap(bitmap);

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                if(messages.size()==0){


                    chatOnlineText = findViewById(R.id.chatOnlineText);

                    typingText = findViewById(R.id.typingText);
                    connectionChatIndicator = findViewById(R.id.connectionChatIndicator);
                    chatAppBar = findViewById(R.id.chatAppBar);
                    notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    setOnline();






                    typingText.setText(name+" набирает сообщение");
                    // getSupportActionBar().setTitle(name);

                    // chatAppBar.setSubtitle("Онлайн");
                    int notificationId = sharedPreferences.getInt(userID,-1);
                    if(notificationId != -1){
                        notificationManager.cancel(notificationId);
                    }
                    setChat();

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
                            mDatabase.child("notifications").child(userID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing").setValue("false");
                            messageTextEdit.setText("");
                        }

                    });






                     Query mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(getRoom()).child("messages").limitToLast(100);
                     mDatabase.addChildEventListener(childEventListener);
                    DatabaseReference onlineReference = FirebaseDatabase.getInstance().getReference();
                    onlineReference.child("users").child(userID).addValueEventListener(userListener);
                    DatabaseReference typingReference = FirebaseDatabase.getInstance().getReference();
                    typingReference.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).child("typing").addValueEventListener(typingValueListener);


                }else {messageRecycler.setVisibility(View.INVISIBLE);}


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
        });

  }

    private void setChat() {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
    databaseReference.child(userID).child("id").setValue(userID);
    }

    ChildEventListener outChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().toString().equals("messages")) {
                count = Integer.parseInt(snapshot.getValue().toString());
            }
              // Toast.makeText(getApplicationContext(), "added"+snapshot.getValue().toString()+snapshot.getKey().toString(),
               //        Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if(snapshot.getKey().toString().equals("messages")) {
                count = Integer.parseInt(snapshot.getValue().toString());
            }
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

   ValueEventListener userListener = new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot snapshot) {
           if(snapshot.getValue()!=null){

               user = snapshot.getValue(User.class);
               chatUsernameText.setText(user.getUserName());
               if(snapshot.child("online").getValue()!=null){
               if (snapshot.child("online").getValue().toString().equals("true")) {
                   chatOnlineText.setText("Онлайн");
                   chatOnlineText.setVisibility(View.VISIBLE);
                   connectionChatIndicator.setProgress(100);
               } else {
                   Long millis = Long.parseLong(snapshot.child("online").getValue().toString());
                   Date date = new Date(millis);
                   connectionChatIndicator.setProgress(0);
                   chatOnlineText.setText("Был(а) в сети "+onlineDateFormat.format(date) );
                   chatOnlineText.setVisibility(View.VISIBLE);
               }
           }else{
                   connectionChatIndicator.setProgress(0);
                   chatOnlineText.setText("");
                   chatOnlineText.setVisibility(View.GONE);
               }
           }
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
               chatOnlineText.setText("Онлайн");
           } else {
               Long millis = Long.parseLong(snapshot.getValue().toString());
Date date = new Date(millis);

               chatOnlineText.setText("Был(а) в сети "+onlineDateFormat.format(date) );
           }
       }else chatOnlineText.setText("");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        chatOnlineText.setText("");
    }
};

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

            try{
                notificationManager.cancel(notificationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
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