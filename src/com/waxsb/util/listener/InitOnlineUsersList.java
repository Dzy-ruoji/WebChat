package com.waxsb.util.listener;

import com.waxsb.model.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;

public class InitOnlineUsersList implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //在线登录用户列表,有问题
        System.out.println("监听Servlet对象创建时触发");
        List<User> userList = new ArrayList<User>();
        ServletContext application = servletContextEvent.getServletContext();
        application.setAttribute("userList",userList);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("监听Servlet对象销毁时触发");
    }
}
