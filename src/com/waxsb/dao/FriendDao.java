package com.waxsb.dao;

import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface FriendDao {
    List<Friend> findFriendByMyName(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    void insert(Connection conn, AddFriend addFriend) throws SQLException;

    List<AddFriend> findFriendRequest(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    List<AddFriend> findFriendRespond(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    void friendReqResp(Connection conn, AddFriend addFriend) throws SQLException;

    void allowFriendReq(Connection conn, Friend friend) throws SQLException;

    void deleteMsg(Connection conn, String friend_1) throws SQLException;

    void deleteMyFriend(Connection conn,String myName , String friendName) throws SQLException;

    void updateNickname(Connection conn,String myName, String friendName, String nickname) throws SQLException;
}
