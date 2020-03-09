package com.waxsb.util.listener;

import com.waxsb.model.User;
import com.waxsb.service.Impl.MsgContextServiceImpl;
import com.waxsb.service.MsgContextService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class InitOnlineUsersList implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //在线登录用户列表,有问题
        System.out.println("监听Servlet对象创建时触发");
        Map<User,HttpSession> userMap = new HashMap<User,HttpSession>();
        servletContextEvent.getServletContext().setAttribute("userMap",userMap);

        //服务器开启后进行定时任务


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("监听Servlet对象销毁时触发");
        Map<User,HttpSession> userMap = (Map<User, HttpSession>) servletContextEvent.getServletContext().getAttribute("userMap");
        for (Map.Entry<User, HttpSession> entry : userMap.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            entry.getValue().invalidate();
        }
    }

    public void deleteMsg(){
        MsgContextService msgContextService = new MsgContextServiceImpl();
        new Timer("testTimer").schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("删除两周前的聊天记录");
                msgContextService.deleteMsg();
            }
        }, 100000,1000*60*60*24);
    }
}
