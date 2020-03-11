package com.waxsb.dao.Impl;

import com.waxsb.dao.BaseDao;
import com.waxsb.dao.FriendDao;
import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FriendDaoImpl implements FriendDao {
    private BaseDao baseDao=new BaseDao();

    @Override
    public List<Friend> findFriendByMyName(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String sql="select * from t_friend where friend_1 = ? or friend_2 = ?";
        List<Friend> friends = baseDao.getForList(conn, Friend.class, sql, username, username);
        if(friends==null||friends.size()==0){
            return null;
        }else{
            return friends;
        }
    }

    @Override
    public void insert(Connection conn, AddFriend addFriend) throws SQLException {
        String sql = "insert into t_addfriend(friend_1,friend_2,f1_allow,f2_allow) value(?,?,?,?)";
        baseDao.update(conn,sql,addFriend.getFriend_1(),addFriend.getFriend_2(),addFriend.getF1_allow(),addFriend.getF2_allow());

    }

    @Override
    public List<AddFriend> findFriendRequest(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
       String sql = "select * from t_addfriend where friend_2=? and f2_allow is null";
        List<AddFriend> addFriends = baseDao.getForList(conn, AddFriend.class, sql, username);
        if(addFriends==null||addFriends.size()==0){
            return null;
        }else{
            return addFriends;
        }
    }

    @Override
    public List<AddFriend> findFriendRespond(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String sql=" select * from t_addfriend t where t.friend_1=? and t.f2_allow is not null";
        List<AddFriend> addFriendsResp=baseDao.getForList(conn,AddFriend.class,sql,username);
        if(addFriendsResp==null||addFriendsResp.size()==0){
            return null;
        }else{
            return addFriendsResp;
        }
    }

    @Override
    public void friendReqResp(Connection conn, AddFriend addFriend) throws SQLException {
        String sql="update t_addfriend set f2_allow=? where friend_1=? and friend_2=?";
        baseDao.update(conn,sql,addFriend.getF2_allow(),addFriend.getFriend_1(),addFriend.getFriend_2());
    }

    @Override
    public void allowFriendReq(Connection conn, Friend friend) throws SQLException {
        String sql = "insert into t_friend(friend_1,friend_2) value(?,?)";
        baseDao.update(conn,sql,friend.getFriend_1(),friend.getFriend_2());
    }

    @Override
    public void deleteMsg(Connection conn, String friend_1) throws SQLException {
        String sql = " delete from t_addfriend where friend_1=? and f2_allow is not null";
        baseDao.update(conn,sql,friend_1);
    }

    @Override
    public void deleteMyFriend(Connection conn,String myName ,String friendName) throws SQLException {
        String sql = "SELECT * FROM t_friend WHERE (friend_1 = ? AND friend_2 = ? ) OR ( friend_1 = ? AND friend_2 = ? )";
        baseDao.update(conn,sql,myName,friendName,friendName,myName);
    }

    @Override
    public void updateNickname(Connection conn,String myName, String friendName, String nickname) throws SQLException {
        String sql = " UPDATE t_friend t1,\n" +
                "  (SELECT COUNT(1) c,friendNickname_1 f1,friendNickname_2 f2,friend_1 n,friend_2 n2 FROM t_friend WHERE friend_1 = ? AND friend_2 = ?) t2, \n" +
                "  (SELECT COUNT(1) c,friendNickname_1 f1,friendNickname_2 f2,friend_2 n,friend_1 n2 FROM t_friend WHERE friend_2 = ? AND friend_1 = ?) t3 \n" +
                "  SET t1.friendNickname_2 = IF(t2.c>0,?,t3.f2), t1.friendNickname_1 = IF(t3.c>0,?,t2.f1) \n" +
                "  WHERE t1.`friend_1`=IF(t2.c>0,t2.n,IF(t3.c>0,t3.n2,-1)) AND t1.`friend_2`=IF(t2.c>0,t2.n2,IF(t3.c>0,t3.n,-1)) ";
        baseDao.update(conn,sql,myName,friendName,myName,friendName,nickname,nickname);
    }


}


