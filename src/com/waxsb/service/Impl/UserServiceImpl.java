package com.waxsb.service.Impl;

import com.waxsb.dao.Impl.UserDaoImpl;
import com.waxsb.dao.UserDao;
import com.waxsb.model.User;
import com.waxsb.service.UserService;
import com.waxsb.util.Database.JDBCUtils;
import com.waxsb.util.Page.PageBean;

import java.sql.Connection;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao dao= new UserDaoImpl();
    @Override
    public boolean register(User user)  {
        //获取数据库连接
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1.根据用户名查询用户对象
        if(dao.getUserByUsername(conn,user.getUsername())==null){
            //2.保存用户信息
            dao.insert(conn,user);
            JDBCUtils.closeResource(conn,null);
            return true;
        }
        JDBCUtils.closeResource(conn,null);
        return false;
    }

    @Override
    public User login(User user)  {
        //获取数据库连接
        Connection conn=null;
        try {
            conn = JDBCUtils.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
       user=dao.getUserByUsernameAndPassword(conn,user.getUsername(),user.getPassword());
            JDBCUtils.closeResource(conn,null);

        return user;
    }

    @Override
    public PageBean<User> findUserByPage(String _currentPage, String _row) {
        int currentPage=Integer.parseInt(_currentPage);
        int rows=Integer.parseInt(_row);
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(currentPage<=0){
            currentPage=1;
        }
        //1.创建空的PageBean对象
        PageBean<User> pb=new PageBean<User>();
        //2.设置参数
        pb.setCurrentPage(currentPage);
        pb.setRows(rows);
        //3.调用Dao查询总记录数
        int totalCount=dao.findTotalCount(conn);
        pb.setTotalCount(totalCount);
        //4.调用Dao查询List集合
        //计算开始的记录索引
        int start=(currentPage-1)*rows;
        List<User> list=dao.findByPage(conn,start,rows);
        pb.setList(list);
        //5.计算总页码
        int totalPage=(totalCount%rows)==0? (totalCount/rows):(totalCount/rows)+1;
        pb.setTotalPage(totalPage);
        System.out.println(pb);
        JDBCUtils.closeResource(conn,null);
        return pb;
    }

}
