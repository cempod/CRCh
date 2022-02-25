package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserEditor extends AppCompatActivity {
    RecyclerView iconRecyclerView;
    RecyclerIconAdapter iconAdapter;
    UserIconsManager iconsManager;
    ImageView userImageEdit;
    User user, defaultUser;
    EditText userNameEdit;
    Spinner nightSpinner;
    String nightMode;
    Button userEditButton;
    CardView userIconCard;
    ConstraintLayout settingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        nightMode = sharedPreferences.getString("night_mode","auto");
        if(nightMode.equals("true")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(nightMode.equals("false")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if(nightMode.equals("auto")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        setContentView(R.layout.activity_user_editor);
        settingsLayout = findViewById(R.id.settingsLayout);
        userIconCard = findViewById(R.id.userIconCard);
        iconRecyclerView = findViewById(R.id.iconRecyclerView);
        userImageEdit = findViewById(R.id.userImageEdit);
        userNameEdit = findViewById(R.id.userNameEdit);
        userEditButton = findViewById(R.id.userEditButton);
        nightSpinner = findViewById(R.id.nightSpinner);
        iconsManager = new UserIconsManager();
        iconAdapter = new RecyclerIconAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),5);
        iconRecyclerView.setLayoutManager(layoutManager);
        iconRecyclerView.setAdapter(iconAdapter);
        userIconCard.setVisibility(View.GONE);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.night_mode,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nightSpinner.setAdapter(adapter);

        if(nightMode.equals("true")){
            nightSpinner.setSelection(2);
        }
        if(nightMode.equals("false")){
            nightSpinner.setSelection(1);
        }
        if(nightMode.equals("auto")){
            nightSpinner.setSelection(0);
        }

        userEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(settingsLayout);
                if(userIconCard.getVisibility() == View.GONE){
                    userIconCard.setVisibility(View.VISIBLE);

                }else{
                    userIconCard.setVisibility(View.GONE);
                }
            }
        });

        nightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    editor.putString("night_mode", "auto");
                    if(!nightMode.equals("auto")){
                        nightMode = "auto";
                        recreate();
                    }
                }
                if(i == 1){
                    editor.putString("night_mode", "false");
                    if(!nightMode.equals("false")){
                        nightMode = "false";
                        recreate();
                    }
                }
                if(i == 2){
                    editor.putString("night_mode", "true");
                    if(!nightMode.equals("true")){
                        nightMode = "true";
                        recreate();
                    }
                }

                editor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                defaultUser = user;
                userNameEdit.setText(user.getUserName());
                userImageEdit.setImageResource(iconsManager.getIconIds()[user.getUserLogo()]);
                Drawable drawable = userImageEdit.getBackground();
                if(drawable instanceof ShapeDrawable){
                    ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
                    shapeDrawable.getPaint().setColor(Color.parseColor(user.getUserColor()));
                    userImageEdit.setBackground(drawable);

                }else if (drawable instanceof ColorDrawable) {
                    // alpha value may need to be set again after this call
                    ColorDrawable colorDrawable = (ColorDrawable) drawable;
                    colorDrawable.setColor(Color.parseColor(user.getUserColor()));
                    userImageEdit.setBackground(drawable);

                }else if (drawable instanceof GradientDrawable) {
                    // alpha value may need to be set again after this call
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(Color.parseColor(user.getUserColor()));
                    userImageEdit.setBackground(drawable);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}