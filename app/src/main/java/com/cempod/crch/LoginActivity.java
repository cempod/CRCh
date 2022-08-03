package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    MotionLayout motionLayout;
    Button button;
    TextInputEditText emailText, passwordText, password2text, nicknameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tabLayout = findViewById(R.id.tabLayout);
        motionLayout = findViewById(R.id.motionLayout);
        button = findViewById(R.id.button);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        password2text = findViewById(R.id.password2Text);
        nicknameText = findViewById(R.id.nicknameText);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==1){
                    motionLayout.setTransition(R.id.start,R.id.end);
                    motionLayout.transitionToEnd();
                   button.setText("Регистрация");
                }else{
                    motionLayout.setTransition(R.id.end,R.id.start);
                    motionLayout.transitionToEnd();
                    button.setText("Войти");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabLayout.getSelectedTabPosition()!=1){
                    if(!emailText.getText().toString().isEmpty()&& !passwordText.getText().toString().isEmpty()){
FirebaseAuth mAuth = FirebaseAuth.getInstance();
mAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {

           // Toast.makeText(getApplicationContext(), "Успешно",
                   // Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("login-complete");
            // You can also include some extra data.
            //intent.putExtra("message", "This is my message!");
            Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(startIntent);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
finish();
        } else {
           

            Toast.makeText(getApplicationContext(), "Произошла ошибка",
                    Toast.LENGTH_SHORT).show();

        }
    }
});}else{
                        Toast.makeText(getApplicationContext(), "Заполните все поля",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{

                        if(!emailText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty()&& !password2text.getText().toString().isEmpty()&& !nicknameText.getText().toString().isEmpty()){
                            if(passwordText.getText().toString().equals(password2text.getText().toString())){
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.createUserWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                           // Toast.makeText(getApplicationContext(), "Успешно",
                                                   // Toast.LENGTH_SHORT).show();

                                            int intColor= ((int)(Math.random()*16777215)) | (0xFF << 24);
                                            String hexColor = String.format("#%06X", (0xFFFFFF & intColor));

                                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),nicknameText.getText().toString(),0, hexColor);
                                            DatabaseReference mDatabase;
// ...
                                            mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("users").child(user.getUserID()).setValue(user);
                                            Intent intent = new Intent("login-complete");
                                            // You can also include some extra data.
                                            //intent.putExtra("message", "This is my message!");
                                            Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                                            startActivity(startIntent);
                                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                           finish();
                                        } else {
                                           

                                            Toast.makeText(getApplicationContext(), "Произошла ошибка",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),"Пароли не совпадают!",Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Заполните все поля!",Toast.LENGTH_SHORT);
                            toast.show();
                        }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
       finishAffinity();
    }
}
