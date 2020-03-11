package com.waxsb.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

//数据源的连接池
public class MyDataSource extends MyAbstractDataSource {

    //空闲连接池
    private final List<ConnectionProxy> idleConnections = new ArrayList<ConnectionProxy>();

    //激活的连接池
    private final List<ConnectionProxy> activeConnections = new ArrayList<ConnectionProxy>();

    //监视器对象
    private final Object monitor = new Object();

    public MyDataSource() {

    }

    public MyDataSource(String url, String driver, String user, String password, int poolMaxActiveConnections, int poolMaxIdleConnections, int poolTimeToWait) {
        super(url, driver, user, password, poolMaxActiveConnections, poolMaxIdleConnections, poolTimeToWait);
    }

    public MyDataSource(String url, String driver, String user, String password) {
        super(url, driver, user, password);
    }

    public MyDataSource(String path) {
        super(path);
    }

    //覆盖父类方法，返回一个动态代理链接
    @Override
    public Connection getConnection() throws SQLException {
        ConnectionProxy connectionProxy=getConnectionProxy(super.getUser(),super.getPassword());
        Connection proxyConnection = connectionProxy.getProxyConnection();
        return  proxyConnection;
    }

    //获取连接
    public ConnectionProxy getConnectionProxy(String username,String password) throws SQLException {
        boolean wait=false;
        ConnectionProxy connectionProxy=null;
        //刚开始没有连接
        while (connectionProxy==null){
            //做一个同步线程
            synchronized (monitor){
               //如果空闲连接不为空，那么可以直接获取连接
                if(!idleConnections.isEmpty()){
                    //返回被移除的元素
                    connectionProxy = idleConnections.remove(0);
                }else {
                    //没有空闲连接可以使用，那么我们需要获取连接新的连接（创建新连接）
                    if(activeConnections.size()<getPoolMaxActiveConnections()){
                        //如果当前激活的连接数小于我们允许的最大连接数，那么此时可以创建一个新的连接
                        connectionProxy=new ConnectionProxy(super.getConnection(),this);
                    }
                        //否则不能创建新连接，需要等待 poolTimeOut
                }
                if(connectionProxy==null){
                    try {
                        //连接对象是空，需要等待
                        monitor.wait(super.getPoolTimeToWait());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //万一等待被线程打断，退出循环
                        break;
                    }
                }

            }

        }
        if(connectionProxy!=null){
            //连接对象不为空，已经拿到连接
            activeConnections.add(connectionProxy);
        }
        //返回连接对象
        return connectionProxy;
    }

    //关闭连接，不是把连接关闭，而是把连接放入连接池
    public void closeConnection(ConnectionProxy connectionProxy){
        synchronized (monitor){
            //关闭连接，把激活状态的连接变成空闲连接
            activeConnections.remove(connectionProxy);
            if(idleConnections.size()<super.getPoolMaxIdleConnections()){
                idleConnections.add(connectionProxy);
            }
            //通知一下，唤醒上面那个等待获取连接的线程
            monitor.notify();
        }
    }
}
