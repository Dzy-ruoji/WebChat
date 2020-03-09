    package com.waxsb.util.Database;

import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

    public abstract class MyAbstractDataSource implements MyDataSourceInterface {
        //默认属性
        private String url;
        private String driver;
        private String user;
        private String password;

        //最大的正在使用的连接数
        private int poolMaxActiveConnections=100;

        //最大的空闲连接数
        private int poolMaxIdleConnections=50;

        //从连接池中获取连接的最大等待时间
        private int poolTimeToWait=30000;

        public MyAbstractDataSource() {

        }

        public MyAbstractDataSource(String url, String driver, String user, String password, int poolMaxActiveConnections, int poolMaxIdleConnections, int poolTimeToWait) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
            this.poolMaxActiveConnections = poolMaxActiveConnections;
            this.poolMaxIdleConnections = poolMaxIdleConnections;
            this.poolTimeToWait = poolTimeToWait;
        }

        public MyAbstractDataSource(String url, String driver, String user, String password) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
        }

        public MyAbstractDataSource(String path){
            //path为配置文件的路径
            Properties pros=new Properties();
            try {
                InputStream in= JDBCUtils.class.getResourceAsStream(path);
                pros.load(in);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //user会变成什么，等等再测试
            user=pros.getProperty("user");
            password=pros.getProperty("password");
            url=pros.getProperty("url");
            driver=pros.getProperty("driver");

            try {
                poolMaxActiveConnections= Integer.parseInt(pros.getProperty("poolMaxActiveConnections"));
            } catch (NumberFormatException e) {
                System.out.println("最大的正在使用的连接数默认为100");
                poolMaxActiveConnections=100;
            }

            try {
                poolMaxIdleConnections= Integer.parseInt(pros.getProperty("poolMaxIdleConnections"));
            } catch (NumberFormatException e) {
               System.out.println("最大的空闲连接数默认为50");
                poolMaxIdleConnections=50;
            }

            try {
                poolTimeToWait= Integer.parseInt(pros.getProperty("poolTimeToWait"));
            } catch (NumberFormatException e) {
               System.out.println("从连接池中获取连接的最大等待时间默认为30s");
                poolTimeToWait=3000;
            }
        }

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
