package com.waxsb.dao;

import com.waxsb.util.Database.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDao {
        //考虑数据事物后的增删改操作
        public int update(Connection conn, String sql, Object...args) {//sql占位符的个数应该和可变形参的长度相同
            PreparedStatement ps = null;

            try {
                //1.预编译sql语句，返回PreparedStatement的实例
                try {
                    ps = conn.prepareStatement(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("编译sql语句时错误");
                }
                //2.填充占位符
                for(int i=0;i< args.length;i++){
                    try {
                        ps.setObject(i+1,args[i]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("填充sql语句时错误");
                    }
                }
                    try {
                        return ps.executeUpdate();//返回的是操作成功的行数
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("实现sql语句时错误");
                    }

            } finally {
                //4.资源的关闭
                JDBCUtils.closeResource(null ,ps);
            }
            return -1;
        }

        //通用的查询操作，用于返回表中的一条记录(Version-2.0：考虑事物)
        public <T> T getInstance( Connection conn,Class<T> clazz, String sql, Object... args){
            PreparedStatement ps = null;
            ResultSet rs = null;
                conn = JDBCUtils.getConnection();

            try {
                try {
                    ps = conn.prepareStatement(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("编译sql语句时错误");
                }
                for (int i = 0; i < args.length; i++) {
                    try {
                        ps.setObject(i + 1, args[i]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("填充sql语句时错误");
                    }
                }
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("实现sql语句时错误");
                }
                //获取结果集的元数据

                ResultSetMetaData rsmd = null;
                int columnCount=0;
                try {
                    rsmd = rs.getMetaData();
                    columnCount = rsmd.getColumnCount();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("sql语句与表中列名不符");
                }
                //通过ResultSetMetaData获取结果集的列数

                try {
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
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    JDBCUtils.closeResource(null, ps, rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        //查询多个对象,用于返回表中的多条记录(Version-2.0：考虑事物)
        public <T>List<T> getForList(Connection conn, Class<T> clazz, String sql, Object...args) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                try {
                    ps = conn.prepareStatement(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("编译sql语句时错误");
                }
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
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } finally {
                try {
                    JDBCUtils.closeResource(null,ps,rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        return null;
        }

        //用于查询特殊值的通用方法
        public <E> E getValue(Connection conn,String sql,Object...args) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                try {
                    ps = conn.prepareStatement(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("编译sql语句时错误");
                }
                for(int i=0;i< args.length;i++){
                    try {
                        ps.setObject(i+1,args[i]);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("填充sql语句时错误");
                    }
                }
                try {
                    rs = ps.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("实现sql语句时错误");
                }

                try {
                    if(rs.next()){
                        try {
                            rs.getObject(1);
                            return (E)rs.getObject(1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("sql语句与数据库表列名不符");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("数据库表无列名");
                }

            }finally {
                try {
                    JDBCUtils.closeResource(null,ps,rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

}
