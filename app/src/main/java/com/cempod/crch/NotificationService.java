package com.cempod.crch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    ArrayList<String> ids = new ArrayList<String>();
    String result;
    ArrayList<String[]> usernames = new ArrayList<String[]>();
    public NotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(getApplicationContext(), "start service",
           //     Toast.LENGTH_SHORT).show();
        getUserNames();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "CRChService")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification service")
                        .setContentText("is working");
        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       // notificationManager.notify(1, notification);
        startForeground(-1,notification);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
           mDatabase.addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                   //Toast.makeText(getApplicationContext(), "added",
                          // Toast.LENGTH_SHORT).show();
                   if(!snapshot.child("messages").getValue().toString().equals("0")) {
                       NotificationCompat.Builder builder =
                               new NotificationCompat.Builder(getApplicationContext(), "CRCh")
                                       .setSmallIcon(R.mipmap.ic_launcher)
                                       .setContentTitle("Новое сообщение")
                                       .setContentText("Вам сообщение от "+getName(snapshot.getKey()));
                       Notification notification = builder.build();

                       NotificationManager notificationManager =
                               (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                       notificationManager.notify(getID(snapshot.getKey()), notification);
                   }
               }

               @Override
               public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                  if(!snapshot.child("messages").getValue().toString().equals("0")) {
                       NotificationCompat.Builder builder =
                               new NotificationCompat.Builder(getApplicationContext(), "CRCh")
                                       .setSmallIcon(R.mipmap.ic_launcher)
                                       .setContentTitle("Новое сообщение" )
                                       .setContentText("Вам сообщение от "+getName(snapshot.getKey()));
                       Notification notification = builder.build();

                       NotificationManager notificationManager =
                               (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                       notificationManager.notify(getID(snapshot.getKey()), notification);
                   }else{
                      notificationManager.cancel(getID(snapshot.getKey()));
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
           });

        } else {

        }

        return super.onStartCommand(intent, flags, startId);


    }

    private void getUserNames() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                usernames.add(new String[]{snapshot.getKey(),snapshot.child("userName").getValue().toString()});
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
        });
    }

    public int getID(String user){
       if(ids.contains(user)){
           return ids.indexOf(user);
       }else{
           ids.add(user);
           return ids.indexOf(user);
       }
    }

 public String getName(String id){
String username = "";
     for(int i = 0; i<usernames.size();i++){
         if(usernames.get(i)[0].equals(id)){
             username = usernames.get(i)[1];
             break;
         }
     }
        return username;
 }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}