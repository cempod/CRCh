package com.cempod.crch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.cli.Options;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter {
    private List<RecyclerUser> users;
    UserIconsManager iconsManager = new UserIconsManager();
    public UsersAdapter(List<RecyclerUser> users){
        this.users = users;
    }
    class UserHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView countOfMessage;
        ImageView iconOfMessages;
        ImageView userAvatar;
        CircularProgressIndicator userOnlineCircle;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameText);
            iconOfMessages = itemView.findViewById(R.id.iconOfMessages);
            countOfMessage = itemView.findViewById(R.id.countOfMessages);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userOnlineCircle = itemView.findViewById(R.id.userOnlineCircle);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_card, parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ((UserHolder)holder).username.setText(users.get(position).getUserName());
        ((UserHolder)holder).userAvatar.setTransitionName("avatar"+(users.get(position).getUserID()));
        ((UserHolder)holder).userAvatar.setTag(position);
        ((UserHolder)holder).username.setTag(position);
        ((UserHolder)holder).username.setTransitionName("username"+users.get(position).getUserID());
        if(users.get(position).getOnline().equals("true")){
            ((UserHolder)holder).userOnlineCircle.setProgress(100);
        }else{
            ((UserHolder)holder).userOnlineCircle.setProgress(0);
        }
        Drawable drawable = ((UserHolder)holder).userAvatar.getBackground();
        if(drawable instanceof ShapeDrawable){
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(Color.parseColor(users.get(position).getUserColor()));
            ((UserHolder)holder).userAvatar.setBackground(shapeDrawable);
        }else if (drawable instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(Color.parseColor(users.get(position).getUserColor()));
            ((UserHolder)holder).userAvatar.setBackground(colorDrawable);
        }else if (drawable instanceof GradientDrawable) {
            // alpha value may need to be set again after this call
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(Color.parseColor(users.get(position).getUserColor()));
            ((UserHolder)holder).userAvatar.setBackground(gradientDrawable);
        }

        if(users.get(position).getUserLogo()>=0){
            ((UserHolder)holder).userAvatar.setImageResource(iconsManager.getIconIds()[users.get(position).getUserLogo()]);
        }else{
            ((UserHolder)holder).userAvatar.setImageResource(iconsManager.getIconIds()[0]);
        }

        if(users.get(position).getNotify()!=0){
            ((UserHolder)holder).iconOfMessages.setVisibility(View.VISIBLE);
            ((UserHolder)holder).countOfMessage.setText(Integer.toString(users.get(position).getNotify()));
        }else{
            ((UserHolder)holder).iconOfMessages.setVisibility(View.INVISIBLE);
            ((UserHolder)holder).countOfMessage.setText("");
        }

        Context context = holder.itemView.getContext();
        ((UserHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((UserHolder)holder).userAvatar.buildDrawingCache();
                Intent intent = new Intent(context,Chat.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                        new Pair<>(((UserHolder)holder).userAvatar,"avatar"+users.get(position).getUserID()),
                        new Pair<>(((UserHolder)holder).username,"username"+users.get(position).getUserID())

                );
                intent.putExtra("Id", users.get(position).getUserID());
                intent.putExtra("Name",users.get(position).getUserName());
                intent.putExtra("Position",users.get(position).getUserID());
                intent.putExtra("from","main");

                Bitmap bitmap = ((UserHolder)holder).userAvatar.getDrawingCache();
                intent.putExtra("avatar",bitmap);
                context.startActivity(intent, options.toBundle());
            }
        });
    }



    @Override
    public int getItemCount() {
        return users.size();
    }
}
