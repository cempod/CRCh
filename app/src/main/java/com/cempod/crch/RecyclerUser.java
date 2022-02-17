package com.cempod.crch;

public class RecyclerUser extends User{

    private int notify = 0;
    private  String online = "";
public RecyclerUser(String userId, String userName, int userLogo, int notify){
this.setUserName(userName);
this.setUserLogo(userLogo);
this.setNotify(notify);
this.setUserID(userId);
}
    public RecyclerUser(String userId, String userName, int userLogo, String userColor, int notify){
        this.setUserName(userName);
        this.setUserLogo(userLogo);
        this.setNotify(notify);
        this.setUserID(userId);
        this.setUserColor(userColor);
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getOnline() {
        return online;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public int getNotify() {
        return notify;
    }
}
