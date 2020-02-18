package com.waxsb.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String requestName = uri.substring(uri.lastIndexOf("/") + 1);

        try {

            // 获取指定类的字节码对象
            Class<? extends BaseServlet> clazz = this.getClass();//这里的this指的是继承BaseServlet对象
            // 通过类的字节码对象获取方法的字节码对象
            Method method = clazz.getMethod(requestName, HttpServletRequest.class, HttpServletResponse.class);
            // 让方法执行

            method.invoke(this, request, response);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
