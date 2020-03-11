package com.waxsb.dao;

import com.waxsb.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //将User对象添加到数据库中
    void insert(Connection conn, User user) throws SQLException;
    //针对指定的Id，删除表中的一条记录
    void deleteById(Connection conn, int id) throws SQLException;
    //针对对象中的Id修改表中指定的记录
    void updateId(Connection conn, User user) throws SQLException;
    //针对指定的Id查询得到对应的User对象
    User getUserById(Connection conn, int id) throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException;
    //查询表中所有记录构成的集合
    List<User> getAll(Connection conn) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;
    //返回数据表中的数据条目数
    long getCount(Connection conn) throws SQLException;
    //根据用户名查找对象
    User getUserByUsername(Connection conn, String username) throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException;
    //根据账号密码登录
    User getUserByUsernameAndPassword(Connection conn, String username, String password) throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException;

    int findTotalCount(Connection conn) throws SQLException;

    List<User> findByPage(Connection conn, int start, int rows) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    User updatePassword(Connection conn, int id, String newPassword) throws SQLException, IllegalAccessException, NoSuchFieldException, InstantiationException;

    User updateMessageByUsername(Connection conn,User user) throws SQLException, IllegalAccessException, NoSuchFieldException, InstantiationException;

    void updateImg(Connection conn, int id, String image_src) throws SQLException;

    List<User> findBySearchName(Connection conn, int start, int rows, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    int findTotalCountByUsername(Connection conn, String username) throws SQLException;
}
