package com.waxsb.dao;

import com.waxsb.util.Database.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public  abstract class BaseDao {
        //考虑数据事物后的增删改操作
        public int update(Connection conn, String sql, Object...args){//sql占位符的个数应该和可变形参的长度相同
            PreparedStatement ps = null;
            try {

                //1.预编译sql语句，返回PreparedStatement的实例
                ps = conn.prepareStatement(sql);
                //2.填充占位符
                for(int i=0;i< args.length;i++){
                    ps.setObject(i+1,args[i]);
                }
                //3.执行
                /*
                 * ps.execute();
                 *如果执行的是查询操作，有返回结果，则此方法返回true
                 *如果执行的是增、删、改操作，没有返回结果，则此方法返回false
                 * */
                //ps.execute();

                return ps.executeUpdate();//返回的是操作成功的行数
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //4.资源的关闭
                JDBCUtils.closeResource(null ,ps);
            }
            return 0;
        }

        //通用的查询操作，用于返回表中的一条记录(Version-2.0：考虑事物)
        public <T> T getInstance( Connection conn,Class<T> clazz, String sql, Object... args) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = JDBCUtils.getConnection();
                ps = conn.prepareStatement(sql);
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                rs = ps.executeQuery();
                //获取结果集的元数据
                ResultSetMetaData rsmd = rs.getMetaData();
                //通过ResultSetMetaData获取结果集的列数
                int columnCount = rsmd.getColumnCount();
                if (rs.next()) {
                    T t = clazz.newInstance();
                    //处理结果集一行数据中的每一列
                    for (int i = 0; i < columnCount; i++) {
                        Object columnValue = rs.getObject(i + 1);
                        //获取每个列的列名
                        String columnName = rsmd.getColumnLabel(i + 1);
                        //给t对象指定的columnLabel属性，赋值为columnValue：通过反射
                        Field field = clazz.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(t, columnValue);
                    }

                    return t;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                JDBCUtils.closeResource(null, ps, rs);
            }
            return null;
        }

        //查询多个对象,用于返回表中的多条记录(Version-2.0：考虑事物)
        public <T>List<T> getForList(Connection conn, Class<T> clazz, String sql, Object...args){
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                for(int i=0;i<args.length;i++){
                    ps.setObject(i+1,args[i]);
                }
                rs = ps.executeQuery();
                //获取结果集的元数据
                ResultSetMetaData rsmd = rs.getMetaData();
                //通过ResultSetMetaData获取结果集的列数
                int columnCount=rsmd.getColumnCount();
                //创建集合对象
                ArrayList<T> list = new ArrayList<>();

                while(rs.next()){
                    T t = clazz.newInstance();
                    //处理结果集一行数据中的每一列:给t对象指定的属性赋值
                    for(int i=0;i<columnCount;i++){
                        Object columnValue = rs.getObject(i + 1);
                        //获取每个列的列名
                        String columnName = rsmd.getColumnLabel(i + 1);
                        //给t对象指定的columnLabel属性，赋值为columnValue：通过反射
                        Field field = clazz.getDeclaredField(columnName);
                        field.setAccessible(true);
                        field.set(t,columnValue);
                    }
                    list.add(t);
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                JDBCUtils.closeResource(null,ps,rs);
            }
            return null;
        }

        //用于查询特殊值的通用方法
        public <E> E getValue(Connection conn,String sql,Object...args) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement(sql);
                for(int i=0;i< args.length;i++){
                    ps.setObject(i+1,args[i]);
                }
                rs = ps.executeQuery();
                if(rs.next()){
                    rs.getObject(1);
                    return (E)rs.getObject(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                JDBCUtils.closeResource(null,ps,rs);
            }

            return null;
        }

}
