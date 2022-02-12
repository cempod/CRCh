package com.cempod.crch;

import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.RequestBuilder.post;
import static com.google.firebase.messaging.RemoteMessage.Builder.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.BasicResponseHandler;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {

RecyclerView recyclerView;
ArrayList<User> users = new ArrayList<>();
    ArrayList<RecyclerUser> recycleUsers = new ArrayList<>();
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        UsersAdapter adapter = new UsersAdapter(recycleUsers);
        recyclerView.setAdapter(adapter);


        sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
        int lastNotificationId = sharedPreferences.getInt("lastNotificationId",-1);
        if (lastNotificationId == -1){
            editor = sharedPreferences.edit();
            editor.putInt("lastNotificationId",0);
            editor.putString("openedChatId","");
            editor.commit();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CRCh", "Messages",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for messages");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);

            NotificationChannel channel1 = new NotificationChannel("CRChService", "Messages service",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("You can hide this notification");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel1);

        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
          //  if (isServiceRunning(NotificationService.class)){}else{
//startService(new Intent(this,NotificationService.class));}
            setToken();
            getUsers();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("login-complete"));


    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            setToken();
           getUsers();

        }
    };

    public void setToken(){
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(token);


                    }
                });

    }

    public void getUsers(){
        DatabaseReference mDatabase;



// ...
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
User user = snapshot.getValue(User.class);
users.add(user);
RecyclerUser recyclerUser = new RecyclerUser(user.getUserID(),user.getUserName(),user.getUserLogo(),0);
recycleUsers.add(recyclerUser);
recyclerView.getAdapter().notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                for(int i = 0; i<users.size();i++){
                    if(users.get(i).getUserID().equals(user.getUserID())){
                        users.get(i).setUserName(user.getUserName());
                        users.get(i).setUserLogo(user.getUserLogo());
                        break;
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();

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
        mDatabase.addChildEventListener(childEventListener);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int notify = Integer.parseInt(snapshot.child("messages").getValue().toString());
                for(int i = 0; i<recycleUsers.size();i++){
                    if(recycleUsers.get(i).getUserID().equals(snapshot.getKey())){
                        recycleUsers.get(i).setNotify(notify);
                        break;
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int notify = Integer.parseInt(snapshot.child("messages").getValue().toString());
                for(int i = 0; i<recycleUsers.size();i++){
                    if(recycleUsers.get(i).getUserID().equals(snapshot.getKey())){
                        recycleUsers.get(i).setNotify(notify);
                        break;
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
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

    @Override
    protected void onPostResume() {
        editor = sharedPreferences.edit();
        editor.putString("openedChatId","");
        editor.commit();
        
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}