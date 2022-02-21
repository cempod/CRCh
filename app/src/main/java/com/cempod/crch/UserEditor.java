package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_editor);
        iconRecyclerView = findViewById(R.id.iconRecyclerView);
        userImageEdit = findViewById(R.id.userImageEdit);
        userNameEdit = findViewById(R.id.userNameEdit);
        iconsManager = new UserIconsManager();
        iconAdapter = new RecyclerIconAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),5);
        iconRecyclerView.setLayoutManager(layoutManager);
        iconRecyclerView.setAdapter(iconAdapter);
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