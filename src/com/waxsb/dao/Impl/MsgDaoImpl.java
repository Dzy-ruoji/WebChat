package com.waxsb.dao.Impl;
import com.waxsb.dao.BaseDao;
import com.waxsb.dao.MsgDao;
import com.waxsb.model.SocketMsg;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class MsgDaoImpl implements MsgDao {
    private BaseDao baseDao=new BaseDao();

    @Override
    public void insertContext(Connection conn, SocketMsg socketMsg) {
        String sql = "insert into socketmsg(Mid,type,fromUser,toUser,msg,createTime,user_GroupID,user_nickname,msgType) value(null,?,?,?,?,?,?,?,?)";
        baseDao.update(conn,sql,socketMsg.getType(),socketMsg.getFromUser(),socketMsg.getToUser(),socketMsg.getMsg(),socketMsg.getCreateTime(),socketMsg.getUser_GroupID(),socketMsg.getUser_nickname(),socketMsg.getMsgType());

    }

    @Override
    public int findPublicTotalCount(Connection conn, int user_groupID, Date date) {
        Number num;
        if(date==null){
            String sql="select count(*) from socketmsg where user_groupID=?";
            num = baseDao.getValue(conn, sql, user_groupID);
        }else {
            //查询某一天的记录
            String sql ="SELECT COUNT(*)  FROM socketmsg WHERE DATE_FORMAT(createTime,'%Y-%m-%d') = ? and user_groupID=? ";
            num = baseDao.getValue(conn,sql,date,user_groupID);
        }
        return num.intValue();
    }

    @Override
    public List<SocketMsg> findPublicMsg(Connection conn, int start, int rows, int user_groupID, Date date) {
        List<SocketMsg> msgList;
        if(date==null){
            String sql = "SELECT * FROM socketmsg WHERE user_groupID = ? ORDER BY createTime DESC LIMIT ? ,? ";
            msgList = baseDao.getForList(conn,SocketMsg.class,sql,user_groupID,start,rows);
        }else{
            String sql = "SELECT * FROM socketmsg s1, " +
                    "(SELECT DISTINCT DATE_FORMAT(createTime,'%Y-%m-%d')d FROM socketmsg WHERE createTime = ? AND user_GroupID=?) s2 " +
                    "HAVING DATE_FORMAT(s1.createTime,'%Y-%m-%d' ) = s2.d ORDER BY createTime DESC limit ?,?";
           msgList = baseDao.getForList(conn,SocketMsg.class,sql,date,user_groupID,start,rows);
        }
        return msgList;
    }

    @Override
    public int findPrivateTotalCount(Connection conn, String fromUser, String toUser, Date date) {
        Number num;
        if(date==null){
            String sql="select count(*) from socketmsg where ( fromUser =? and toUser = ? ) or ( fromUser =? and toUser = ? )";
            num = (Number) baseDao.getValue(conn, sql, fromUser,toUser,toUser,fromUser);
        }else {
            //查询某一天的记录
            String sql ="SELECT COUNT(*)  FROM socketmsg WHERE DATE_FORMAT(createTime,'%Y-%m-%d') = ? and (( fromUser =? and toUser = ? ) or ( fromUser =? and toUser = ? )) ";
             num = (Number) baseDao.getValue(conn, sql, date, fromUser, toUser, toUser, fromUser);
        }
        return num.intValue();
    }

    @Override
    public List<SocketMsg> findPrivateMsg(Connection conn, int start, int rows, String fromUser, String toUser, Date date) {
        List<SocketMsg> msgList;
        if(date==null){
            String sql = "SELECT * FROM socketmsg WHERE ( fromUser =? and toUser = ? ) or ( fromUser =? and toUser = ? ) ORDER BY createTime DESC LIMIT ? ,? ";
            msgList = baseDao.getForList(conn,SocketMsg.class,sql,fromUser,toUser,toUser,fromUser,start,rows);
        }else{
            String sql = "SELECT * FROM socketmsg s1, " +
                    "(SELECT DISTINCT DATE_FORMAT(createTime,'%Y-%m-%d')d FROM socketmsg WHERE createTime = ? AND (( fromUser =? and toUser = ? ) or ( fromUser =? and toUser = ? ))) s2 " +
                    "HAVING DATE_FORMAT(s1.createTime,'%Y-%m-%d' ) = s2.d ORDER BY createTime DESC limit ?,?";
            msgList = baseDao.getForList(conn,SocketMsg.class,sql,date,fromUser,toUser,toUser,fromUser,start,rows);
        }
        return msgList;
    }

    @Override
    public void deleteMsg(Connection conn) {
        String sql = "SELECT*FROM socketmsg WHERE DATE_FORMAT(createTime, '%Y %m') = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH),'%Y %m')";
    }
}
