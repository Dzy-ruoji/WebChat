package com.waxsb.model;
import java.sql.Timestamp;

public class SocketMsg {//
    private int Mid;//消息id
    private int type;   //聊天类型：2:群聊，1：单聊.
    private String fromUser;//发送者.
    private String toUser;//接受者.
    private String msg;//内容
    private Timestamp createTime;//用户发送时间
    private int user_GroupID;//群id
    private String user_nickname;//群昵称
    private int msgType;//消息类型 0纯文本 1图片 2文件  3系统提示 4添加请求 5操作回应  6群公告

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMid() {
        return Mid;
    }

    public void setMid(int mid) {
        Mid = mid;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public int getUser_GroupID() {
        return user_GroupID;
    }

    public void setUser_GroupID(int user_GroupID) {
       this.user_GroupID = user_GroupID;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


}
