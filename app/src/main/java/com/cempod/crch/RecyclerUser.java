package com.cempod.crch;

public class RecyclerUser extends User{

    private int notify = 0;
public RecyclerUser(String userId, String userName, int userLogo, int noyify){
this.setUserName(userName);
this.setUserLogo(userLogo);
this.setNotify(noyify);
this.setUserID(userId);
}

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public int getNotify() {
        return notify;
    }
}
