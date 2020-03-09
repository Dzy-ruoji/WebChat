package com.waxsb.model;

import java.sql.Timestamp;


public class User_GroupsToUser {

    //List<User_GroupsToUser> ug  ug.user.name ug.UG_GroupNick
    private int UG_ID;//主键
    private int UG_UserID;//用户id
    private int UG_GroupID;//群id
    private Timestamp Datetime;//入群时间
    private String UG_GroupNick;//群内用户昵称
    private String level;//该用户在群内等级

    private User user;//用户
    private User_Groups user_Groups;//群
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User_Groups getUser_Groups() {
        return user_Groups;
    }

    public void setUser_Groups(User_Groups user_Groups) {
        this.user_Groups = user_Groups;
    }

    public int getUG_ID() {
        return UG_ID;
    }

    public void setUG_ID(int UG_ID) {
        this.UG_ID = UG_ID;
    }

    public int getUG_UserID() {
        return UG_UserID;
    }

    public void setUG_UserID(int UG_UserID) {
        this.UG_UserID = UG_UserID;
    }

    public int getUG_GroupID() {
        return UG_GroupID;
    }

    public void setUG_GroupID(int UG_GroupID) {
        this.UG_GroupID = UG_GroupID;
    }

    public Timestamp getDatetime() {
        return Datetime;
    }

    public void setDatetime(Timestamp datetime) {
        Datetime = datetime;
    }

    public String getUG_GroupNick() {
        return UG_GroupNick;
    }

    public void setUG_GroupNick(String UG_GroupNick) {
        this.UG_GroupNick = UG_GroupNick;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
