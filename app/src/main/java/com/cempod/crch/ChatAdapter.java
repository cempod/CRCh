package com.cempod.crch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {
    List<Message> messages;
    String userID;
    public ChatAdapter(List<Message> messages, String userID){
        this.messages = messages;
        this.userID = userID;
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
messageTime = itemView.findViewById(R.id.messageTime);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
if(viewType == 0){
     view = LayoutInflater.from(parent.getContext()).inflate(R.layout.out_message_card, parent,false);
    }else    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_message_card, parent,false);
return new MessageHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSender()==userID){
            return 0;
        }else return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessageHolder)holder).messageText.setText(messages.get(position).getText());
        ((MessageHolder)holder).messageTime.setText(messages.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
