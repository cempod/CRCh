package com.cempod.crch;

import java.util.Date;

public class Message {
    private String time;
    private String text;
    private  String sender;
    public Message(String text, String time, String sender){
        this.text = text;
        this.time = time;
        this.sender = sender;
    }
    public Message(){}

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
