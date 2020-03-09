package com.waxsb.model;

import java.util.List;

public class Friend {
    private String friend_1;//主动发起邀请人（当前用户）
    private String friend_2;//接受方（朋友）
    private String friendNickname_1;//主动发起邀请人的昵称（接受方给发起方起的）
    private String friendNickname_2;//接受方的昵称（发起方给接收方起的）

    public String getFriend_1() {
        return friend_1;
    }

    public void setFriend_1(String friend_1) {
        this.friend_1 = friend_1;
    }

    public String getFriend_2() {
        return friend_2;
    }

    public void setFriend_2(String friend_2) {
        this.friend_2 = friend_2;
    }

    public String getFriendNickname_1() {
        return friendNickname_1;
    }

    public void setFriendNickname_1(String friendNickname_1) {
        this.friendNickname_1 = friendNickname_1;
    }

    public String getFriendNickname_2() {
        return friendNickname_2;
    }

    public void setFriendNickname_2(String friendNickname_2) {
        this.friendNickname_2 = friendNickname_2;
    }
}
