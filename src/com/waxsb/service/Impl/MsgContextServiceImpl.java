package com.waxsb.service.Impl;

import com.waxsb.dao.Impl.MsgDaoImpl;
import com.waxsb.dao.MsgDao;
import com.waxsb.model.SocketMsg;
import com.waxsb.service.MsgContextService;
import com.waxsb.util.Database.JDBCUtils;
import com.waxsb.util.Page.PageBean;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;


public class MsgContextServiceImpl implements MsgContextService {
    private MsgDao dao = new MsgDaoImpl();

    @Override
    public void insertContext(SocketMsg socketMsg) {
        Connection conn = JDBCUtils.getConnection();
        dao.insertContext(conn,socketMsg);
        JDBCUtils.closeResource(conn,null);

    }

    @Override
    public PageBean<SocketMsg> findPublicMsg(int currentPage, int rows, int user_groupID, Date date) {
        Connection conn = JDBCUtils.getConnection();
        //1.创建空的PageBean对象
        PageBean<SocketMsg> pb=new PageBean<SocketMsg>();
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
        JDBCUtils.closeResource(conn,null);
        return pb;
    }

    @Override
    public PageBean<SocketMsg> findPrivateMsg(int currentPage, int rows, String fromUser, String toUser, Date date) {
        Connection conn = JDBCUtils.getConnection();
        //1.创建空的PageBean对象
        PageBean<SocketMsg> pb=new PageBean<SocketMsg>();
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
        JDBCUtils.closeResource(conn,null);
        return pb;
    }

    @Override
    public void deleteMsg() {
        Connection conn = JDBCUtils.getConnection();
        dao.deleteMsg(conn);
        JDBCUtils.closeResource(conn,null);
    }
}
