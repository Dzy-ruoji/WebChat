package com.waxsb.model;

import java.util.Date;

public class User_GroupsMSGContent {
    private int GM_ID;// 消息ID
    private String GM_Content;//消息内容
    private int GM_ToID;//接收者ID
    private int GM_FromID;//发送者ID
    private String GM_FromUName;//发送者群昵称
    private Date GM_CreateTime;//发送时间
    private String type;//消息类型

    public int getGM_ID() {
        return GM_ID;
    }

    public void setGM_ID(int GM_ID) {
        this.GM_ID = GM_ID;
    }

    public String getGM_Content() {
        return GM_Content;
    }

    public void setGM_Content(String GM_Content) {
        this.GM_Content = GM_Content;
    }

    public int getGM_FromID() {
        return GM_FromID;
    }

    public void setGM_FromID(int GM_FromID) {
        this.GM_FromID = GM_FromID;
    }

    public String getGM_FromUName() {
        return GM_FromUName;
    }

    public void setGM_FromUName(String GM_FromUName) {
        this.GM_FromUName = GM_FromUName;
    }

    public Date getGM_CreateTime() {
        return GM_CreateTime;
    }

    public void setGM_CreateTime(Date GM_CreateTime) {
        this.GM_CreateTime = GM_CreateTime;
    }

    public int getGM_ToID() {
        return GM_ToID;
    }

    public void setGM_ToID(int GM_ToID) {
        this.GM_ToID = GM_ToID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
