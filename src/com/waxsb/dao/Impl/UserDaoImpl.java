package com.waxsb.dao.Impl;

import com.waxsb.dao.BaseDao;
import com.waxsb.dao.UserDao;
import com.waxsb.model.User;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    private BaseDao baseDao=new BaseDao();
    @Override
    public void insert(Connection conn, User user) {
        String sql = "insert into User(id,username,password,name,birthday,gender,telephone,email) value(null,?,?,?,?,?,?,?)";
            baseDao.update(conn,sql,user.getUsername(),user.getPassword(),user.getName(),user.getBirthday(),user.getGender(),user.getTelephone(),user.getEmail());
    }

    @Override
    public void deleteById(Connection conn, int id){
        String sql = "delete from User where id=?";
        baseDao.update(conn,sql,id);

    }

    @Override
    public void updateId(Connection conn, User user){
        String sql = "update user set name=?,birthday=?,gender=?,telephone=?,email=? where id=?";
        baseDao.update(conn,sql,user.getName(),user.getBirthday(),user.getGender(),user.getTelephone(),user.getEmail(),user.getId());
    }

    @Override
    public User getUserById(Connection conn, int id) {
        String sql = "select name,birthday,gender,telephone,email from User where id=?";
        User user = baseDao.getInstance(conn, User.class, sql, id);
        return user;
    }

    @Override
    public List<User> getAll(Connection conn)  {
        String sql="select name,birthday,gender,telephone,email from User";
        List<User> list = baseDao.getForList(conn,User.class,sql);
        return list;
    }

    @Override
    public long getCount(Connection conn)  {
        String sql = "select count(*) from User";
        return baseDao.getValue(conn,sql);
    }

    @Override
    public User getUserByUsername(Connection conn, String username) {
        String sql = "select * from User where username=?";
        User user = baseDao.getInstance(conn, User.class, sql, username);
        return user;
    }

    @Override
    public User getUserByUsernameAndPassword(Connection conn, String username, String password) {
        String sql = "select * from user where username= ? and password =?";
        User user = baseDao.getInstance(conn, User.class, sql, username, password);
        return user;
    }

    @Override
    public int findTotalCount(Connection conn) {
        String sql = "select count(*) from user";
        int count = Integer.parseInt(baseDao.getValue(conn,sql).toString());
        return count;
    }

    @Override
    public List<User> findByPage(Connection conn, int start, int rows) {
        String sql="select * from user limit ?,?";
        List<User> forList = baseDao.getForList(conn, User.class,sql,start,rows);
        return forList;
    }

    @Override
    public User updatePassword(Connection conn, int id, String newPassword) {
        String sql = "update user set password=? where id=?";
        baseDao.update(conn,sql,newPassword,id);

         sql="select *from user where id=?";
        return baseDao.getInstance(conn, User.class, sql, id);
    }

    @Override
    public User updateMessageByUsername(Connection conn, User user) {
        String sql = "update user set gender=?,name=?,birthday=?,telephone=? where username=?";
        baseDao.update(conn, sql, user.getGender(), user.getName(), user.getBirthday(), user.getTelephone(), user.getUsername());
        sql="select *from user where username=?";
        user =baseDao.getInstance(conn, User.class, sql, user.getUsername());
        return user;
    }

    @Override
    public void updateImg(Connection conn, int id, String image_src) {
        String sql = "update user set src=? where id=?";
        baseDao.update(conn,sql,image_src,id);
    }

    @Override
    public List<User> findBySearchName(Connection conn, int start, int rows, String username) {
        //1.定义sql模板
        String sql="select * from user where 1 = 1 ";
        StringBuilder sb = new StringBuilder(sql);
        List params = new ArrayList();//条件
        //判断参数是否有值
        if(username!=null && username.length()>0){
            sb.append(" and username like ?");
            params.add("%"+username+"%");
        }

        sb.append(" limit ?,?");//分页条件
        sql = sb.toString();
        params.add(start);
        params.add(rows);


        return  baseDao.getForList(conn, User.class,sql,params.toArray());
    }


    @Override
    public int findTotalCountByUsername(Connection conn, String username) {
        //1.定义sql模板
        String sql="select count(*) from user where 1=1 ";
        StringBuilder sb = new StringBuilder(sql);
        List params = new ArrayList();//条件
        //判断参数是否有值
        if(username!=null && username.length()>0){
            sb.append(" and username like ?");
            params.add("%"+username+"%");
        }
        sql = sb.toString();

        return Integer.parseInt(baseDao.getValue(conn,sql,params.toArray()).toString());
    }

}
