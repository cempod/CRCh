package com.cempod.crch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {
    ArrayList<RecyclerUser> list = new ArrayList<>();
    RecyclerView findRecycler;

    TextInputEditText findUserTextEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        findUserTextEdit = findViewById(R.id.findUserTextEdit);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
findRecycler = findViewById(R.id.findRecycler);
RecyclerView.Adapter adapter = new UsersAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        findRecycler.setLayoutManager(linearLayoutManager);
        findRecycler.setAdapter(adapter);

        findUserTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(findUserTextEdit.getText().toString().isEmpty()){ list.clear();
                    findRecycler.getAdapter().notifyDataSetChanged();}else {
                    list.clear();
                   // findRecycler.getAdapter().notifyDataSetChanged();
                    Query query = databaseReference.child("users").orderByChild("userName").startAt(findUserTextEdit.getText().toString()).endAt(findUserTextEdit.getText().toString() + "\uf8ff");
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            User user = snapshot.getValue(User.class);

                            list.add(new RecyclerUser(user.getUserID(),user.getUserName(),user.getUserLogo(),user.getUserColor(),0));
                            findRecycler.getAdapter().notifyDataSetChanged();
                            Toast toast = Toast.makeText(getApplicationContext(), user.getUserID(), Toast.LENGTH_SHORT);
                           // toast.show();
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}