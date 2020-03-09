package com.waxsb.util.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    private static MyDataSource myDataSource = new MyDataSource("/jdbc.properties");

    public static Connection getConnection()  {

        Connection conn = null;
        try {
            conn = myDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static void closeResource(Connection conn, Statement ps){
      if(ps!=null){
          try {
              ps.close();
          } catch (SQLException e) {
              e.printStackTrace();
          }
      }
      if(conn!=null){
          try {
              conn.close();
          } catch (SQLException e) {
              e.printStackTrace();
          }
      }
    }

    public static void closeResource(Connection conn, Statement ps,ResultSet rs) throws SQLException {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            rs.close();
        }
    }
}
