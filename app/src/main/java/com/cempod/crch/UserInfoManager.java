package com.cempod.crch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
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
    UserIconsManager iconsManager = new UserIconsManager();

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
                    getAvatar(user.getUserLogo(),user.getUserColor(),userChatAvatar);
                    if(snapshot.child("online").getValue()!=null){
                        if (snapshot.child("online").getValue().toString().equals("true")) {
                            if(chatOnlineText!=null)  chatOnlineText.setText("Онлайн");
                           if(chatOnlineText!=null) chatOnlineText.setVisibility(View.VISIBLE);
                            connectionChatIndicator.setProgress(100);
                        } else {
                            Long millis = Long.parseLong(snapshot.child("online").getValue().toString());
                            Date date = new Date(millis);
                            connectionChatIndicator.setProgress(0);
                            if(chatOnlineText!=null) chatOnlineText.setText("Был(а) в сети "+onlineDateFormat.format(date) );
                            if(chatOnlineText!=null) chatOnlineText.setVisibility(View.VISIBLE);
                        }
                    }else{
                        connectionChatIndicator.setProgress(0);
                        if(chatOnlineText!=null) chatOnlineText.setText("");
                        if(chatOnlineText!=null) chatOnlineText.setVisibility(View.GONE);
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

    public void getAvatar(int userLogo,String userColor,ImageView userChatAvatar ) {
        userChatAvatar.setImageResource (iconsManager.getIconIds()[userLogo]);
        userChatAvatar.setBackgroundResource(R.drawable.user_circle_background);
        Drawable drawable = userChatAvatar.getBackground();
        if(drawable instanceof ShapeDrawable){
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(Color.parseColor(userColor));
            userChatAvatar.setBackground(shapeDrawable);
        }else if (drawable instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(Color.parseColor(userColor));
            userChatAvatar.setBackground(colorDrawable);
        }else if (drawable instanceof GradientDrawable) {
            // alpha value may need to be set again after this call
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(Color.parseColor(userColor));
            userChatAvatar.setBackground(gradientDrawable);
        }
        userChatAvatar.buildDrawingCache();
    }



    public  void setNotificationListener(ImageView icon,TextView count, String userID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userID).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (Integer.parseInt(snapshot.getValue().toString()) == 0) {
                        count.setText("");
                        icon.setVisibility(View.INVISIBLE);
                    } else {
                        count.setText(snapshot.getValue().toString());
                        icon.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
