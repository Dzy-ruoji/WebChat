    package com.waxsb.util.Database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

    public abstract class MyAbstractDataSource implements MyDataSourceInterface {
        private String url="jdbc:mysql://localhost:3306/day17";
        private String driver="com.mysql.jdbc.Driver";
        private String user="root";
        private String password="root";
        //最大的正在使用的连接数
        private int poolMaxActiveConnections=10;

        //最大的空闲连接数
        private int poolMaxIdleConnections=5;

        //从连接池中获取连接的最大等待时间
        private int poolTimeToWait=30000;




        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPoolMaxActiveConnections() {
            return poolMaxActiveConnections;
        }

        public void setPoolMaxActiveConnections(int poolMaxActiveConnections) {
            this.poolMaxActiveConnections = poolMaxActiveConnections;
        }

        public int getPoolMaxIdleConnections() {
            return poolMaxIdleConnections;
        }

        public void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
            this.poolMaxIdleConnections = poolMaxIdleConnections;
        }

        public int getPoolTimeToWait() {
            return poolTimeToWait;
        }

        public void setPoolTimeToWait(int poolTimeToWait) {
            this.poolTimeToWait = poolTimeToWait;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return getConnection(user,password);
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return doGetConnection(username, password);
        }

        //获取数据库连接
        private Connection doGetConnection(String username, String password) throws SQLException {
            try {
                Class clazz = Class.forName(driver);
                Driver driver=(Driver)clazz.newInstance();
                DriverManager.registerDriver(driver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Connection conn = DriverManager.getConnection(url, username, password);
            return conn;
        }
    }
