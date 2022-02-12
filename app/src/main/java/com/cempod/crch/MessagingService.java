package com.cempod.crch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.prefs.Preferences;

public class MessagingService extends FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String s) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token");
            databaseReference.setValue(s);
        } else {


        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

            Map<String, String> data = remoteMessage.getData();
        Intent intent = new Intent(getApplicationContext(),Chat.class);
        intent.putExtra("Id", data.get("fromId"));
        intent.putExtra("Name",data.get("title"));

        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
        int lastNotificationId = sharedPreferences.getInt("lastNotificationId",0);
        int userNotificationId = sharedPreferences.getInt(data.get("fromId"),-1);
        if(userNotificationId == -1){
            userNotificationId = lastNotificationId+1;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("lastNotificationId",userNotificationId);
            editor.putInt(data.get("fromId"),userNotificationId);
            editor.commit();
        }

        String openedChatId = sharedPreferences.getString("openedChatId","");


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Chat.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "CRCh")
                            .setSmallIcon(R.drawable.outline_mail_24)
                            .setContentTitle("Новое сообщение от "+data.get("title"))
                            .setContentText("Не прочитано сообщений: "+data.get("body"))
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if(openedChatId.equals(data.get("fromId"))){}else{
            notificationManager.notify(userNotificationId, notification);
        }
        super.onMessageReceived(remoteMessage);
    }
}
