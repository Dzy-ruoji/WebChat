package com.waxsb.model;

import java.sql.Timestamp;
import java.util.List;

public class User_Groups {
    private int UG_ID;//群id
    private String UG_Number;//群号码
    private String UG_Name;//群名称
    private Timestamp UG_CreateTime;//创建时间
    private int UG_AdminID;//群主id
    private String announcement;//群公告
    private List<User_GroupsToUser> user_groupsList;//表示该群所有用户及对应昵称

    public List<User_GroupsToUser> getUser_groupsList() {
        return user_groupsList;
    }

    public void setUser_groupsList(List<User_GroupsToUser> user_groupsList) {
        this.user_groupsList = user_groupsList;
    }

    public int getUG_ID() {
        return UG_ID;
    }

    public void setUG_ID(int UG_ID) {
        this.UG_ID = UG_ID;
    }

    public String getUG_Name() {
        return UG_Name;
    }

    public void setUG_Name(String UG_Name) {
        this.UG_Name = UG_Name;
    }

    public Timestamp getUG_CreateTime() {
        return UG_CreateTime;
    }

    public void setUG_CreateTime(Timestamp UG_CreateTime) {
        this.UG_CreateTime = UG_CreateTime;
    }

    public int getUG_AdminID() {
        return UG_AdminID;
    }

    public void setUG_AdminID(int UG_AdminID) {
        this.UG_AdminID = UG_AdminID;
    }

    public String getUG_Number() {
        return UG_Number;
    }

    public void setUG_Number(String UG_Number) {
        this.UG_Number = UG_Number;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
