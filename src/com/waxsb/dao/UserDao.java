package com.waxsb.dao;

import com.waxsb.model.User;

import java.sql.Connection;
import java.util.List;

public interface UserDao {
    //将User对象添加到数据库中
    void insert(Connection conn, User user);
    //针对指定的Id，删除表中的一条记录
    void deleteById(Connection conn, int id);
    //针对对象中的Id修改表中指定的记录
    void updateId(Connection conn, User user);
    //针对指定的Id查询得到对应的User对象
    User getUserById(Connection conn, int id);
    //查询表中所有记录构成的集合
    List<User> getAll(Connection conn);
    //返回数据表中的数据条目数
    long getCount(Connection conn);
    //根据用户名查找对象
    User getUserByUsername(Connection conn, String username);
    //根据账号密码登录
    User getUserByUsernameAndPassword(Connection conn, String username, String password);

    int findTotalCount(Connection conn);

    List<User> findByPage(Connection conn, int start, int rows);
}
