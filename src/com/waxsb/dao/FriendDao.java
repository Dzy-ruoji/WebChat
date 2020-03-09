package com.waxsb.dao;

import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;

import java.sql.Connection;
import java.util.List;

public interface FriendDao {
    List<Friend> findFriendByMyName(Connection conn, String username);

    void insert(Connection conn, AddFriend addFriend);

    List<AddFriend> findFriendRequest(Connection conn, String username);

    List<AddFriend> findFriendRespond(Connection conn, String username);

    void friendReqResp(Connection conn, AddFriend addFriend);

    void allowFriendReq(Connection conn, Friend friend);

    void deleteMsg(Connection conn, String friend_1);

    void deleteMyFriend(Connection conn,String myName , String friendName);

    void updateNickname(Connection conn,String myName, String friendName, String nickname);
}
