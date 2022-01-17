package com.cempod.crch;

public class User {
    private String userID;
    private String userName;
    private int userLogo;
    public User(String userID, String userName, int userLogo){
this.userID = userID;
this.userName = userName;
this.userLogo = userLogo;
    }
    public User(){}

    public int getUserLogo() {
        return userLogo;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserLogo(int userLogo) {
        this.userLogo = userLogo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
