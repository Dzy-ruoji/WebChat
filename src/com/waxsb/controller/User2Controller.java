package com.waxsb.controller;

import com.waxsb.model.User;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/User2Controller/*")
public class User2Controller extends BaseServlet {
    private UserService userService =  new UserServiceImpl();

    /** 分页获取数据  */
    public void findByPage(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ResultInfo resultInfo;
        try {
            //获取当前页码
            String pageNum = request.getParameter("pageNum");
            //获取显示条数
            String pageSize = request.getParameter("pageSize");
            //获取分页对象
            PageBean<User> pb = userService.findUserByPage(pageNum, pageSize);
            //请求成功
            resultInfo = ResultInfo.ResponseSuccess(pb);
        } catch (Exception e) {
            resultInfo = ResultInfo.ResponseFail();
        }
        //转换成json返回客户端
        MyJson.returnJson(resultInfo,response);
    }

}
