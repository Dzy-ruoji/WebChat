package com.waxsb.service.Impl;

import com.waxsb.dao.FriendDao;
import com.waxsb.dao.Impl.FriendDaoImpl;
import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;
import com.waxsb.service.FriendService;
import com.waxsb.util.Database.JDBCUtils;

import java.sql.Connection;
import java.util.List;

public class FriendServiceImpl implements FriendService {
    private FriendDao dao=new FriendDaoImpl();
    @Override
    public List<Friend> findFriendByName(String username) {
        Connection conn = JDBCUtils.getConnection();
        List<Friend> friend = dao.findFriendByMyName(conn,username);
        JDBCUtils.closeResource(conn,null);
        return friend;
    }

    @Override
    public void addFriend(AddFriend addFriend) {
        Connection conn = JDBCUtils.getConnection();
        dao.insert(conn,addFriend);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public List<AddFriend> findFriendRequest(String username) {
        Connection conn = JDBCUtils.getConnection();
        List<AddFriend> addFriends = dao.findFriendRequest(conn,username);
        JDBCUtils.closeResource(conn,null);
        return addFriends;
    }

    @Override
    public List<AddFriend> responseMessage(String username) {
        Connection conn = JDBCUtils.getConnection();
        List<AddFriend> friendRespond = dao.findFriendRespond(conn,username);
        JDBCUtils.closeResource(conn,null);
        return friendRespond;
    }

    @Override
    public void friendReqResp(AddFriend addFriend) {
        Connection conn = JDBCUtils.getConnection();
        dao.friendReqResp(conn,addFriend);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void allowFriendReq(Friend friend) {
        Connection conn=JDBCUtils.getConnection();
        dao.allowFriendReq(conn,friend);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void deleteMsg(String friend_1) {
        Connection conn=JDBCUtils.getConnection();
        dao.deleteMsg(conn,friend_1);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void deleteMyFriend(String myName, String friendName) {
        Connection conn=JDBCUtils.getConnection();
        dao.deleteMyFriend(conn,myName,friendName);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void updateNickname(String myName, String friendName, String nickname) {
        Connection conn=JDBCUtils.getConnection();
        dao.updateNickname(conn,myName,friendName,nickname);
        JDBCUtils.closeResource(conn,null);
    }


}
