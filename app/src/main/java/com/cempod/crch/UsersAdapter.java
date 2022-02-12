package com.cempod.crch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter {
    private List<User> users;
    public UsersAdapter(List<User> users){
        this.users = users;
    }
    class UserHolder extends RecyclerView.ViewHolder {
        TextView username;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_card, parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UserHolder)holder).username.setText(users.get(position).getUserName());
        Context context = holder.itemView.getContext();
        ((UserHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Chat.class);
                intent.putExtra("Id", users.get(position).getUserID());
                intent.putExtra("Name",users.get(position).getUserName());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return users.size();
    }
}
