package com.waxsb.service.Impl;

import com.waxsb.dao.Impl.UserDaoImpl;
import com.waxsb.dao.UserDao;
import com.waxsb.model.User;
import com.waxsb.service.UserService;
import com.waxsb.util.Database.JDBCUtils;
import com.waxsb.util.Page.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao dao= new UserDaoImpl();
    @Override
    public boolean register(User user)  {
        //获取数据库连接
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("获取链接失败");
            e.printStackTrace();
        }
        try {
            //1.根据用户名查询用户对象
            if(dao.getUserByUsername(conn,user.getUsername())==null){
                //2.保存用户信息
                dao.insert(conn,user);
                JDBCUtils.closeResource(conn,null);
                conn.commit();
                return true;
            }
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
        return false;
    }

    @Override
    public User login(User user)  {
        //获取数据库连接
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            user=dao.getUserByUsernameAndPassword(conn,user.getUsername(),user.getPassword());
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
        return user;
    }

    @Override
    public PageBean<User> findUserByPage(String _currentPage, String _row) {
        int currentPage=Integer.parseInt(_currentPage);
        int rows=Integer.parseInt(_row);
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
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
        try {
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
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
         finally {
            JDBCUtils.closeResource(conn,null);
        }
        return pb;
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            user=dao.updatePassword(conn,user.getId(),newPassword);
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
        return user;
    }

    @Override
    public User updateMessage(User user) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //因为user是根据前端传回来的数据封装的，所以没有userId,只能根据username来判断
        try {
            user=dao.updateMessageByUsername(conn,user);
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

        return user;
    }

    @Override
    public void updateImg(int id, String image_src) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.updateImg(conn,id,image_src);
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
    public User findUserByUsername(String username) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        User user = null;
        try {
            user = dao.getUserByUsername(conn, username);
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
        return user;
    }

    @Override
    public PageBean<User> findUserBySearchName(String _currentPage, String _row, String username) {
        int currentPage=Integer.parseInt(_currentPage);
        int rows=Integer.parseInt(_row);
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
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

        try {
            //3.调用Dao查询总记录数
            int totalCount=dao.findTotalCountByUsername(conn,username);
            pb.setTotalCount(totalCount);

            //4.调用Dao查询List集合
            //计算开始的记录索引
            int start=(currentPage-1)*rows;
            List<User> list=dao.findBySearchName(conn,start,rows,username);
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
        }  finally {
            JDBCUtils.closeResource(conn,null);
        }
        return pb;
    }
}
