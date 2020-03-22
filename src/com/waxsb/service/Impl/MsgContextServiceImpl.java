package com.waxsb.service.Impl;

import com.waxsb.dao.Impl.MsgDaoImpl;
import com.waxsb.dao.MsgDao;
import com.waxsb.model.SocketMsg;
import com.waxsb.service.MsgContextService;
import com.waxsb.util.Database.JDBCUtils;
import com.waxsb.util.Page.PageBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;


public class MsgContextServiceImpl implements MsgContextService {
    private MsgDao dao = new MsgDaoImpl();

    @Override
    public void insertContext(SocketMsg socketMsg) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.insertContext(conn,socketMsg);
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
    public PageBean<SocketMsg> findPublicMsg(int currentPage, int rows, int user_groupID, Date date) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PageBean<SocketMsg> pb= null;
        try {
            //1.创建空的PageBean对象
            pb = new PageBean<SocketMsg>();
            //2.设置参数
            pb.setCurrentPage(currentPage);
            pb.setRows(rows);
            //3.群聊 调用Dao查询总记录数
            int totalCount=dao.findPublicTotalCount(conn,user_groupID,date);
            pb.setTotalCount(totalCount);

            //4.调用Dao查询List集合
            //计算开始的记录索引
            int start=(currentPage-1)*rows;
            List<SocketMsg> list=dao.findPublicMsg(conn,start,rows,user_groupID,date);
            pb.setList(list);
            //5.计算总页码
            int totalPage=(totalCount%rows)==0? (totalCount/rows):(totalCount/rows)+1;
            pb.setTotalPage(totalPage);
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
        return pb;
    }

    @Override
    public PageBean<SocketMsg> findPrivateMsg(int currentPage, int rows, String fromUser, String toUser, Date date) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PageBean<SocketMsg> pb= null;
        try {
            //1.创建空的PageBean对象
            pb = new PageBean<SocketMsg>();
            //2.设置参数
            pb.setCurrentPage(currentPage);
            pb.setRows(rows);
            //3.私聊 调用Dao查询总记录数
            int totalCount=dao.findPrivateTotalCount(conn,fromUser,toUser,date);
            pb.setTotalCount(totalCount);

            //4.调用Dao查询List集合
            //计算开始的记录索引
            int start=(currentPage-1)*rows;

            List<SocketMsg> list=dao.findPrivateMsg(conn,start,rows,fromUser,toUser,date);
            pb.setList(list);
            //5.计算总页码
            int totalPage=(totalCount%rows)==0? (totalCount/rows):(totalCount/rows)+1;
            pb.setTotalPage(totalPage);
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
        return pb;
    }

    @Override
    public void deleteMsg() {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.deleteMsg(conn);
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
    }

    @Override
    public List<SocketMsg> findContacts(String username) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            List<SocketMsg> soc = dao.findContacts(conn,username);
            conn.commit();
            return soc;
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
        return null;
    }
}
