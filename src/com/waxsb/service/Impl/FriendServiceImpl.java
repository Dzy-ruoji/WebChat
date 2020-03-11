package com.waxsb.service.Impl;

import com.waxsb.dao.FriendDao;
import com.waxsb.dao.Impl.FriendDaoImpl;
import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;
import com.waxsb.service.FriendService;
import com.waxsb.util.Database.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FriendServiceImpl implements FriendService {
    private FriendDao dao=new FriendDaoImpl();
    @Override
    public List<Friend> findFriendByName(String username) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Friend> friend = null;
        try {
            friend = dao.findFriendByMyName(conn,username);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return friend;
    }

    @Override
    public void addFriend(AddFriend addFriend) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.insert(conn,addFriend);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public List<AddFriend> findFriendRequest(String username) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<AddFriend> addFriends = null;
        try {
            addFriends = dao.findFriendRequest(conn,username);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
        return addFriends;
    }

    @Override
    public List<AddFriend> responseMessage(String username) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<AddFriend> friendRespond = null;
        try {
            friendRespond = dao.findFriendRespond(conn,username);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        JDBCUtils.closeResource(conn,null);
        return friendRespond;
    }

    @Override
    public void friendReqResp(AddFriend addFriend) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.friendReqResp(conn,addFriend);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }

    }

    @Override
    public void allowFriendReq(Friend friend) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.allowFriendReq(conn,friend);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void deleteMsg(String friend_1) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.deleteMsg(conn,friend_1);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void deleteMyFriend(String myName, String friendName) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.deleteMyFriend(conn,myName,friendName);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void updateNickname(String myName, String friendName, String nickname) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.updateNickname(conn,myName,friendName,nickname);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }

    }

}
