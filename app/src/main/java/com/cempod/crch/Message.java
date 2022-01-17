package com.cempod.crch;

public class Message {
    private  String text;
    private String publicKey;
    private  String senderID;
    private  String receiverID;

public Message(String text, String publicKey, String senderID, String receiverID){
this.text = text;
this.publicKey = publicKey;
this.receiverID = receiverID;
this.senderID = senderID;
}

    public String getPublicKey() {
        return publicKey;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getText() {
        return text;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setText(String text) {
        this.text = text;
    }
}
