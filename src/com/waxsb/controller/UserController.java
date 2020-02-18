package com.waxsb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waxsb.model.User;
import com.waxsb.service.Impl.UserServiceImpl;
import com.waxsb.service.UserService;
import com.waxsb.util.CheckCode.MyCheckCode;
import com.waxsb.util.Json.GetJson;
import com.waxsb.util.LockHelper;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import com.waxsb.util.Page.PageMsg;
import com.waxsb.util.listener.OnlineUsers;
import net.sf.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@WebServlet("/user/*")
public class UserController extends BaseServlet {
    ResultInfo resultInfo = new ResultInfo();
    OnlineUsers onlineUsers = new OnlineUsers();

    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //登录逻辑
        System.out.println("login");
        req.setCharacterEncoding("utf-8");
        JSONObject jsonObject = MyCheckCode.JudCheckCode(req, resp);
        if(jsonObject==null){
            return;
        }

        //验证码正确、封装对象
        User user=new User();
        user.setUsername((String) jsonObject.get("username"));
        user.setPassword((String) jsonObject.get("password"));
        //调用service完成登录
        UserService userService = new UserServiceImpl();
        User u=userService.login(user);
        if(u==null){
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误");
        }

        if(u!=null){
            List<User> userList = (List<User>) req.getSession().getServletContext().getAttribute("userList");//获取已登录的用户
            for(User userLogin : userList) {
                if(u.getUsername().equals(userLogin.getUsername())){
                    LockHelper.destroyedSession(userLogin);//销毁前一个用户
                    break;
                }
            }
            //登录成功
            req.getSession().setAttribute("user",u);
            onlineUsers.setUser(u);
            req.getSession().setAttribute("onlineUsers",onlineUsers);//感知到自己被绑定，触发绑定事件
            LockHelper.putSession(req.getSession());
             userList = (List<User>) req.getSession().getServletContext().getAttribute("userList");
            int size = userList.size();
            req.getServletContext().setAttribute("size",size);
            resultInfo.setFlag(true);
        }
        //将resultInfo对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(resultInfo);
        //将json数据写回客户端
        //设置content-type
        resp.setContentType("application/json;character=utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(json);
        System.out.println("login success");
    }

    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException, ParseException {

        req.setCharacterEncoding("utf-8");
        //注册逻辑
        System.out.println("register...");
            //将json转化成字符串json
        JSONObject jsonObject = GetJson.getJson(req);
        //2.封装对象
        User user = new User();
        user.setUsername((String) jsonObject.get("username"));
        user.setName((String) jsonObject.get("name"));
        user.setPassword((String) jsonObject.get("password"));
        user.setEmail((String) jsonObject.get("email"));
        user.setTelephone((String) jsonObject.get("telephone"));
        user.setGender((String)jsonObject.get("gender"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = sdf.parse((String) jsonObject.get("birthday"));
        user.setBirthday(birthday);

        //3.调用service完成注册
        UserService userService = new UserServiceImpl();
        boolean flag =userService.register(user);
        //4.响应结果
        if(flag){
            //登录成功
            resultInfo.setFlag(true);
        }else{
            //注册失败
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("注册失败,用户名相同");
        }
        //将resultInfo对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(resultInfo);
        //将json数据写回客户端
        //设置content-type
        resp.setContentType("application/json;character=utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(json);
        System.out.println("注册流程结束");
    }

   public void checkCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       MyCheckCode.RespondCheckCode(req, resp);
   }

    public void getUserList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PageBean<User> userList = PageMsg.GetUserList(req, resp);
        resultInfo.setData(userList);
        //将resultInfo对象序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(resultInfo);
        //将json数据写回客户端
        //设置content-type
        resp.setContentType("application/json;character=utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(json);
        System.out.println("查询流程结束");

    }
}

