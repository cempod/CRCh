package com.cempod.crch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfoManager {
    private User user;
    SimpleDateFormat onlineDateFormat = new SimpleDateFormat("HH:mm dd.MM.yy");

    public UserInfoManager(){

    }

    public void setUserListener(String userID, TextView chatUsernameText, TextView chatOnlineText,
                                  ImageView userChatAvatar, CircularProgressIndicator connectionChatIndicator){


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

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();
        userReference.child("users").child(userID).addValueEventListener(userListener);
    }


}
