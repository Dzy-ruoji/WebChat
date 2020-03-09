package com.waxsb.util.Database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

//使用动态代理实现对数据库连接的代理
public class ConnectionProxy implements InvocationHandler {
    //真实连接
    private Connection realConnection;
    //代理连接
    private Connection proxyConnection;
    //持有数据源对象
    private MyDataSource myDataSource;

    public Connection getRealConnection() {
        return realConnection;
    }

    public void setRealConnection(Connection realConnection) {
        this.realConnection = realConnection;
    }

    public Connection getProxyConnection() {
        return this.proxyConnection;
    }

    public void setProxyConnection(Connection proxyConnection) {
        this.proxyConnection = proxyConnection;
    }

    public MyDataSource getMyDataSource() {
        return myDataSource;
    }

    public void setMyDataSource(MyDataSource myDataSource) {
        this.myDataSource = myDataSource;
    }

    public ConnectionProxy(Connection realConnection, MyDataSource myDataSource) {
        //初始化真实连接
        this.realConnection = realConnection;
        //初始化数据源
        this.myDataSource = myDataSource;
        //初始化代理连接
        this.proxyConnection= (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
                                                     new Class<?>[] {Connection.class},
                                                     this);
    }

    //当调用Connection对象里面的方法时，首先会被invoke拦截
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取到当前对象调用的方法名称
        String methodName=method.getName();

        if(methodName.equalsIgnoreCase("close")){
            // 把连接归还到连接池
            myDataSource.closeConnection(this);
            return null;
        }else{
            //可能有问题，执行的是真实链接
            return  method.invoke(realConnection,args);
        }

    }
}
