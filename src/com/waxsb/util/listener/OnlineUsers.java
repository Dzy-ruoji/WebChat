package com.waxsb.util.listener;

import com.waxsb.model.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

public class OnlineUsers implements HttpSessionBindingListener {
    //每次登录成功后的用户信息
    private User user;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //当前实例在session绑定，则触发此方法
    @Override
    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
        ServletContext application = httpSessionBindingEvent.getSession().getServletContext();
        List<User> userList = (List<User>) application.getAttribute("userList");
        userList.add(user);
        application.setAttribute("userList",userList);
        if(user!=null){
            System.out.println(user.getUsername()+"已经上线");
        }

    }

    //当前实例在session移除绑定，则触发此方法
    @Override
    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        ServletContext application = httpSessionBindingEvent.getSession().getServletContext();
        List<User> userList = (List<User>) application.getAttribute("userList");
        userList.remove(user);
        application.setAttribute("userList",userList);
        if(user!=null){
            System.out.println(user.getUsername()+"已经下线");
        }

    }


}
