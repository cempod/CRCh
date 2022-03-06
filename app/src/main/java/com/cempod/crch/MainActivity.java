package com.cempod.crch;

import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.RequestBuilder.post;
import static com.google.firebase.messaging.RemoteMessage.Builder.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {

RecyclerView recyclerView;
ArrayList<User> users = new ArrayList<>();
    ArrayList<RecyclerUser> recycleUsers = new ArrayList<>();
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
FloatingActionButton searchUserButton;
ImageButton mainMenuButton;
CircularProgressIndicator connectionIndicator;
TextView mainActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPreferences = getSharedPreferences("settings",MODE_PRIVATE);

        String nightMode = sPreferences.getString("night_mode","auto");
        if(nightMode.equals("true")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(nightMode.equals("false")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(nightMode.equals("auto")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        setContentView(R.layout.activity_main);
        mainActivityTitle = findViewById(R.id.mainActivityTitle);



        mainMenuButton = findViewById(R.id.mainMenuButton);
        connectionIndicator = findViewById(R.id.connectionIndicator);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        UsersAdapter adapter = new UsersAdapter(recycleUsers);
        recyclerView.setAdapter(adapter);
searchUserButton = findViewById(R.id.searchUserButton);
searchUserButton.setVisibility(View.GONE);
searchUserButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,FindUserActivity.class);
        startActivity(intent);
    }
});

mainMenuButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
        popupMenu.inflate(R.menu.main_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                 @Override
                                                 public boolean onMenuItemClick(MenuItem menuItem) {
                                                     switch (menuItem.getItemId()) {
                                                         case R.id.menu_edit:
                                                             Intent intent = new Intent(MainActivity.this,UserEditor.class);
                                                             startActivity(intent);
                                                             return true;
                                                         case R.id.menu_exit:
                                                            logout();
                                                             return true;

                                                         default:
                                                             return false;
                                                     }
                                                 }
                                             });
        popupMenu.show();
    }
});

        sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
        int lastNotificationId = sharedPreferences.getInt("lastNotificationId",-1);
        if (lastNotificationId == -1){
            editor = sharedPreferences.edit();
            editor.putInt("lastNotificationId",0);
            editor.putString("openedChatId","");
            editor.commit();
        }
        try {
           FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {}

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
            searchUserButton.setVisibility(View.VISIBLE);
            setOnline();
            setToken();
            getUsers();
            getAccount();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("login-complete"));


    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
            searchUserButton.setVisibility(View.VISIBLE);
            setOnline();
            setToken();
           getUsers();
getAccount();
        }
    };

    public void setOnline(){
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online");
// Write a string when this client loses connection
        presenceRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
        presenceRef.setValue("true");
    }
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

    public void getAccount(){
        UserIconsManager iconsManager = new UserIconsManager();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mainMenuButton.setImageResource(iconsManager.getIconIds()[user.getUserLogo()]);
                Drawable drawable = mainMenuButton.getBackground();
                if(drawable instanceof ShapeDrawable){
                    ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
                    shapeDrawable.getPaint().setColor(Color.parseColor(user.getUserColor()));
                   mainMenuButton.setBackground(drawable);

                }else if (drawable instanceof ColorDrawable) {
                    // alpha value may need to be set again after this call
                    ColorDrawable colorDrawable = (ColorDrawable) drawable;
                    colorDrawable.setColor(Color.parseColor(user.getUserColor()));
                    mainMenuButton.setBackground(drawable);

                }else if (drawable instanceof GradientDrawable) {
                    // alpha value may need to be set again after this call
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(Color.parseColor(user.getUserColor()));
                    mainMenuButton.setBackground(drawable);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    connectionIndicator.setIndeterminate(false);
                } else {
                    connectionIndicator.setIndeterminate(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getUsers(){
        DatabaseReference mDatabase, cDatabase, nDatabase;



// ...
        mDatabase = FirebaseDatabase.getInstance().getReference("chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        cDatabase = FirebaseDatabase.getInstance().getReference("users");
        nDatabase = FirebaseDatabase.getInstance().getReference("notifications/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

        ChildEventListener chatsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String id = snapshot.getKey();
                cDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        RecyclerUser recyclerUser = new RecyclerUser(user.getUserID(),user.getUserName(),user.getUserLogo(),user.getUserColor(),0);
                        recycleUsers.add(recyclerUser);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
        mDatabase.addChildEventListener(chatsListener);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String id = snapshot.getKey();
                cDatabase.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

boolean trigger = false;
                        for(RecyclerUser recyclerUser:recycleUsers) {
                            if (recyclerUser.getUserID().equals(user.getUserID())) {
                                recyclerUser.setUserColor(user.getUserColor());
                                recyclerUser.setUserLogo(user.getUserLogo());
                                recyclerUser.setUserName(user.getUserName());
                                if(snapshot.child("online").getValue() != null) {
                                    recyclerUser.setOnline(snapshot.child("online").getValue().toString());
                                }
                                recyclerView.getAdapter().notifyDataSetChanged();
                                trigger = true;
                                break;
                            }
                        }
                            if(!trigger){
                                RecyclerUser recyclerUser = new RecyclerUser(user.getUserID(),user.getUserName(),user.getUserLogo(),user.getUserColor(),0);
if(snapshot.child("online").getValue() != null) {
    recyclerUser.setOnline(snapshot.child("online").getValue().toString());
}
                                nDatabase.child(id).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       if(snapshot.exists()){
                                           int notify = Integer.parseInt(snapshot.getValue().toString());
                                           recyclerUser.setNotify(notify);
                                       }
                                        recycleUsers.add(recyclerUser);
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        recycleUsers.add(recyclerUser);
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }






                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
       // mDatabase.addChildEventListener(childEventListener);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
       // databaseReference.child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(notifyListener);



    }



    ChildEventListener notifyListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
try {
    int notify = Integer.parseInt(snapshot.child("messages").getValue().toString());
    for(int i = 0; i<recycleUsers.size();i++){
        if(recycleUsers.get(i).getUserID().equals(snapshot.getKey())){
            recycleUsers.get(i).setNotify(notify);
            break;
        }
    }
} catch (Exception e) {
    e.printStackTrace();
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
    };

    @Override
    protected void onPostResume() {
        editor = sharedPreferences.edit();
        editor.putString("openedChatId","");
        editor.commit();
if(FirebaseAuth.getInstance().getCurrentUser() != null){
    setOnline();
}
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {

        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}